# Implementation Prompt for Google Antigravity — VitalSense Queue & Appointment System

Paste everything below into Antigravity as the task brief. It is written to be self-contained: it references the real files, classes, and conventions already in the `alexansh/VitalSense` repository so the agent extends the codebase instead of reinventing it.

---

## 0. Context you must load first

This is an existing Android app, not a greenfield project. Before writing any code:

1. Read `tech-stack.md`, `doctor-role-spec.md`, and `prd.md` at the repo root.
2. Read these existing files in full — the new feature must match their patterns exactly:
   - `app/src/main/java/com/vitalsense/app/core/data/model/Models.kt` and `Enums.kt`
   - `app/src/main/java/com/vitalsense/app/core/data/local/entity/Entities.kt`
   - `app/src/main/java/com/vitalsense/app/core/data/local/dao/VitalSenseDao.kt`
   - `app/src/main/java/com/vitalsense/app/core/data/local/VitalSenseDatabase.kt`
   - `app/src/main/java/com/vitalsense/app/core/data/repository/VitalSenseRepository.kt` and `VitalSenseRepositoryImpl.kt`
   - `app/src/main/java/com/vitalsense/app/core/data/remote/FirestoreDataSource.kt`
   - `app/src/main/java/com/vitalsense/app/core/sync/SyncManager.kt` and `SyncWorker.kt`
   - `app/src/main/java/com/vitalsense/app/core/state/AppStateHolder.kt`
   - `app/src/main/java/com/vitalsense/app/feature/doctor/DoctorViewModel.kt`, `DoctorHomeScreen.kt`, `AppointmentConfirmationScreen.kt`, `components/ScheduleAppointmentDialog.kt`, `components/ProposeAppointmentDialog.kt`
   - `app/src/main/java/com/vitalsense/app/feature/patient/AppointmentsScreen.kt`, `PatientHomeScreen.kt`
   - `app/src/main/java/com/vitalsense/app/feature/navigation/VitalSenseNavGraph.kt`
   - `app/src/main/java/com/vitalsense/app/core/di/DatabaseModule.kt` and `FirebaseModule.kt`
   - `app/src/main/java/com/vitalsense/app/core/ui/components/*` (reuse `VitalSenseCard`, `VitalSenseButton`, existing theme — do not invent new base components)

**Ground truth about current state (do not re-derive, just confirm):** `Appointment` already exists as a booking record (`propose → accept/decline/reschedule`) with Room persistence, an outbox-pattern write path, and optional Firestore upload via `FirestoreDataSource.uploadAppointment`. `AppointmentsScreen.kt` (patient) and `AppointmentConfirmationScreen.kt` (doctor) are minimal stubs that only render a status string. **There is no live queue concept anywhere in the codebase** — no token numbers, no check-in, no position/ETA, no "call next" doctor action. That is the gap this task fills.

---

## 1. Product requirements (confirmed scope — do not deviate without flagging)

1. **Queue ordering model — Hybrid.** Scheduled appointments and walk-ins are merged into a single, per-doctor, per-day live queue, ordered primarily by **check-in time** (not booking time). A scheduled appointment does not occupy a queue position until the patient (or the appointment holder) actually checks in on the day of the visit. Walk-ins may join the same day's queue directly if the doctor has queue check-in open for that date.
2. **Live updates — real-time.** Queue state must update live via **Firestore snapshot listeners** while online. This is the one place in the app where near-real-time correctness matters more than offline-first purity (this mirrors `doctor-role-spec.md`'s existing statement that the doctor dashboard is online-required). Offline behavior is still required and specified in §5 — it must degrade gracefully, not crash or silently desync.
3. **Who can operate the queue:**
   - **Doctor**: check a patient in, call next, start consultation, complete consultation, mark no-show, skip, manually prioritize an entry, add a walk-in, open/close today's queue, define daily slot capacity.
   - **Admin**: **read-only oversight** — can view live queue length, current token, and average wait time across all doctors/villages, drilling into any single doctor's queue. Admin cannot call, skip, or modify any entry.
   - **ASHA workers do NOT get queue-operation permissions in this feature.** They keep their existing appointment-proposal capability (already implemented) but do not check patients in or manage the live queue. Do not add ASHA-facing queue-management UI.
   - **Patients** can: check themselves in for a scheduled appointment, join a walk-in queue if open, view their own live position/ETA, and cancel their own queue entry.

---

## 2. Data model

### 2.1 New domain models (add to `Models.kt`)

```kotlin
enum class QueueEntrySource { SCHEDULED, WALK_IN }

enum class QueueEntryStatus {
    WAITING, CALLED, IN_CONSULTATION, COMPLETED, NO_SHOW, SKIPPED, CANCELLED
}

data class DoctorDaySlotConfig(
    val id: String,
    val doctorId: String,
    val dateFormatted: String,       // "yyyy-MM-dd"
    val startTime: String,           // "HH:mm"
    val endTime: String,             // "HH:mm"
    val capacity: Int,               // max scheduled bookings in this block
    val isWalkInOpen: Boolean = true // whether walk-ins can join today's queue
)

data class QueueEntry(
    val id: String,
    val doctorId: String,
    val doctorName: String,
    val dateFormatted: String,
    val tokenNumber: Int,
    val provisionalToken: Boolean = false, // true until an offline check-in is reconciled with the server
    val appointmentId: String?,            // null for walk-ins
    val patientId: String,
    val patientName: String,
    val source: QueueEntrySource,
    val status: QueueEntryStatus,
    val priorityFlag: Boolean = false,     // doctor-set manual priority bump, see §3.2
    val checkedInAt: Long,
    val calledAt: Long? = null,
    val consultationStartedAt: Long? = null,
    val completedAt: Long? = null,
    val outcomeNotes: String? = null,
    val isPendingSync: Boolean = false
)
```

Do not add automatic severity-based reordering. Ordering is check-in time plus an explicit, doctor-triggered `priorityFlag` (see §3.2) — this avoids surprising patients with an opaque algorithm and avoids race conditions from automatic re-sorting mid-queue.

### 2.2 New Room entities (add to `entity/Entities.kt`, alongside the existing `AppointmentEntity`)

```kotlin
@Entity(tableName = "doctor_day_slots")
data class DoctorDaySlotEntity(
    @PrimaryKey val id: String,
    val doctorId: String,
    val dateFormatted: String,
    val startTime: String,
    val endTime: String,
    val capacity: Int,
    val isWalkInOpen: Boolean
)

@Entity(tableName = "queue_entries")
data class QueueEntryEntity(
    @PrimaryKey val id: String,
    val doctorId: String,
    val doctorName: String,
    val dateFormatted: String,
    val tokenNumber: Int,
    val provisionalToken: Boolean,
    val appointmentId: String?,
    val patientId: String,
    val patientName: String,
    val source: QueueEntrySource,
    val status: QueueEntryStatus,
    val priorityFlag: Boolean,
    val checkedInAt: Long,
    val calledAt: Long?,
    val consultationStartedAt: Long?,
    val completedAt: Long?,
    val outcomeNotes: String?,
    val isPendingSync: Boolean
)
```

Register `QueueEntrySource` and `QueueEntryStatus` in `core/data/local/typeconverters/Converters.kt` following the existing enum `TypeConverter` pattern already used for `UserRole` etc.

Add both new entities to the `@Database(entities = [...])` list in `VitalSenseDatabase.kt` and **bump `version` from 3 to 4**. Unlike the rest of the app, **do not rely on `fallbackToDestructiveMigration()` for this change** — write an explicit `Migration(3, 4)` that creates the two new tables, and add it via `.addMigrations(MIGRATION_3_4)` on the database builder (keep `fallbackToDestructiveMigration()` as a safety net after the explicit migration, so existing installs are not wiped). This is a hard requirement — the task is "production grade," and destructive migration on real patient data is not acceptable.

### 2.3 DAO (extend `VitalSenseDao.kt`)

Add, following the existing `Flow`-returning query style:

```kotlin
// --- Queue ---
@Query("SELECT * FROM queue_entries WHERE doctorId = :doctorId AND dateFormatted = :date ORDER BY checkedInAt ASC")
fun observeDoctorQueue(doctorId: String, date: String): Flow<List<QueueEntryEntity>>

@Query("SELECT * FROM queue_entries WHERE patientId = :patientId AND dateFormatted = :date LIMIT 1")
fun observePatientQueueEntry(patientId: String, date: String): Flow<QueueEntryEntity?>

@Query("SELECT * FROM queue_entries WHERE doctorId = :doctorId AND dateFormatted = :date AND status = 'COMPLETED' ORDER BY completedAt DESC LIMIT :limit")
suspend fun getRecentCompletedEntries(doctorId: String, date: String, limit: Int): List<QueueEntryEntity>

@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun upsertQueueEntry(entry: QueueEntryEntity)

@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun upsertQueueEntries(entries: List<QueueEntryEntity>)

@Query("SELECT * FROM doctor_day_slots WHERE doctorId = :doctorId AND dateFormatted = :date")
fun observeDoctorSlots(doctorId: String, date: String): Flow<List<DoctorDaySlotEntity>>

@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun upsertDoctorSlot(slot: DoctorDaySlotEntity)
```

### 2.4 Firestore layout

Do **not** nest queue entries under `doctors/{id}/queue/...` — deep nesting makes the admin oversight query (across all doctors) awkward. Instead, use a flat top-level collection with fields that support the two access patterns needed:

```
queue_entries/{entryId}
  doctorId, doctorName, dateFormatted, tokenNumber, provisionalToken,
  appointmentId, patientId, patientName, source, status, priorityFlag,
  checkedInAt, calledAt, consultationStartedAt, completedAt, outcomeNotes

doctor_day_slots/{slotId}
  doctorId, dateFormatted, startTime, endTime, capacity, isWalkInOpen

queue_counters/{doctorId}_{dateFormatted}
  nextToken: Int   // used only inside the token-assignment transaction, §3.1
```

Composite index required: `queue_entries` on `(doctorId ASC, dateFormatted ASC, checkedInAt ASC)` for the doctor/admin live queue query, and `(patientId ASC, dateFormatted ASC)` for the patient's own-entry listener.

---

## 3. Core queue logic (this is the part that must be correct, not just wired up)

### 3.1 Token assignment must be atomic

Two patients (or a patient and a walk-in add by the doctor) can check in at nearly the same instant. Token numbers must never collide or skip unpredictably. Implement token assignment as a **Firestore transaction**:

1. Read `queue_counters/{doctorId}_{date}`. If missing, treat `nextToken` as 1.
2. Within the same transaction, write the new `queue_entries` document with `tokenNumber = nextToken` and increment/write `nextToken + 1` back to the counter doc.
3. Wrap this in a repository method `suspend fun checkIn(...): QueueEntry` that runs the transaction via `firestore.runTransaction { ... }`.

**Offline check-in path:** if the device is offline when check-in is attempted, do not block the user. Generate a **local-only entry** with `provisionalToken = true` and a locally-generated negative or `"local_"`-prefixed token placeholder, insert it into Room immediately (so the UI shows *something*), and enqueue an `OutboxEntity` (same pattern as `scheduleAppointment` in `VitalSenseRepositoryImpl` — reuse the existing outbox/`SyncWorker` mechanism, do not build a second sync system). When `SyncWorker` later drains the outbox and connectivity is available, run the real transaction to get the authoritative token number, then update the local Room row and clear `provisionalToken`. The patient-facing queue status UI (§4.3) must visibly show "Confirming your position…" whenever `provisionalToken == true`.

### 3.2 Ordering and manual priority

Default order for a given doctor/day is `checkedInAt ASC` among entries with `status == WAITING`. A doctor can tap "Prioritize" on any waiting entry, which sets `priorityFlag = true`; entries with `priorityFlag = true` sort before entries without it (still ordered by `checkedInAt` within each group). This is a manual, visible, doctor-initiated action — never auto-apply it from `SeverityLevel` or any other heuristic.

`IN_CONSULTATION` at most one entry per doctor at a time — enforce this in the repository (`startConsultation` should refuse, or auto-complete-the-previous, if another entry for the same doctor is already `IN_CONSULTATION`; pick "refuse with a clear error" rather than silently completing someone else's consultation).

### 3.3 Position and ETA calculation

Computed **client-side** from the live snapshot (no Cloud Function needed for MVP scale):

- `position` for a given `WAITING` entry = count of other `WAITING` entries for the same doctor/day that sort ahead of it under the §3.2 ordering.
- `avgConsultationSeconds` for a doctor = mean of `(completedAt - consultationStartedAt)` over the doctor's last N (default 15) `COMPLETED` entries **today**, falling back to the doctor's last N completed entries across the most recent 7 days if today has fewer than 3 samples, falling back to a hardcoded default of 600 seconds if there is no history at all. Implement this as a pure function (e.g. `QueueEtaCalculator.averageConsultationSeconds(entries: List<QueueEntry>): Long`) that is unit-testable in isolation — do not inline this math into a ViewModel.
- `estimatedWaitSeconds` for a waiting entry = `position * avgConsultationSeconds`.

### 3.4 State transition rules (enforce these, do not just expose raw setters)

Valid transitions only: `WAITING → CALLED → IN_CONSULTATION → COMPLETED`, with `WAITING → SKIPPED` (doctor calls but patient not present — re-enters queue once, at the back, on next skip becomes `NO_SHOW` and does not re-enter), `WAITING/CALLED → NO_SHOW`, and `WAITING → CANCELLED` (patient-initiated only). Reject and log any other transition attempt rather than silently applying it — this matters because both the doctor app and the sync worker can attempt writes, and stale/out-of-order writes must not corrupt state.

---

## 4. Repository, ViewModel, and screens

### 4.1 Repository interface additions (`VitalSenseRepository.kt`)

```kotlin
// --- Queue ---
fun observeDoctorQueue(doctorId: String, date: String): Flow<List<QueueEntry>>
fun observePatientQueueEntry(patientId: String, date: String): Flow<QueueEntry?>
fun observeDoctorSlots(doctorId: String, date: String): Flow<List<DoctorDaySlotConfig>>
fun observeAllDoctorQueueSummaries(date: String): Flow<List<DoctorQueueSummary>> // for admin oversight

suspend fun defineDoctorSlot(slot: DoctorDaySlotConfig)
suspend fun checkInAppointment(appointmentId: String): QueueEntry
suspend fun joinWalkInQueue(doctorId: String, patientId: String, patientName: String): QueueEntry
suspend fun callNext(doctorId: String, date: String)
suspend fun startConsultation(entryId: String)
suspend fun completeConsultation(entryId: String, outcomeNotes: String?)
suspend fun markNoShow(entryId: String)
suspend fun skipEntry(entryId: String)
suspend fun prioritizeEntry(entryId: String)
suspend fun cancelQueueEntry(entryId: String)
```

Add a small `DoctorQueueSummary(doctorId, doctorName, dateFormatted, waitingCount, currentToken, avgWaitSeconds, isQueueOpen)` model for the admin view — do not make Admin subscribe to every doctor's full entry list just to show a summary card.

### 4.2 Implementation notes for `VitalSenseRepositoryImpl.kt`

- Add a `QueueRemoteDataSource` (new class, same package as `FirestoreDataSource`, or extend `FirestoreDataSource` with a `queue_entries`/`doctor_day_slots`/`queue_counters` section following its existing structure) that exposes the Firestore transaction from §3.1 and a `callbackFlow`-based listener exactly like the existing `awaitClose`/`callbackFlow` pattern already used elsewhere in `FirestoreDataSource`.
- `observeDoctorQueue` should merge the Firestore listener (when online) with the Room cache: emit Room's cached list immediately (so the screen isn't blank on open), then switch to/reconcile with the live Firestore stream, writing every update back into Room so the last-known state survives the doctor or patient going offline mid-session. Use the same `MutableStateFlow` + `.update {}` idiom already used for `_appointments` in this file, not a new architecture.
- All the mutation methods (`callNext`, `startConsultation`, etc.) should: (1) apply optimistic local update to Room + in-memory flow, (2) attempt the Firestore write, (3) on failure, write an `OutboxEntity` with `actionType = "QUEUE_ENTRY"` for `SyncWorker` to retry later — mirror exactly how `scheduleAppointment` currently handles the outbox in this file.

### 4.3 New/changed screens (Compose, reuse `VitalSenseCard` / `VitalSenseButton` / existing theme — no new design language)

1. **`feature/doctor/DoctorQueueScreen.kt`** (new) — replaces `AppointmentConfirmationScreen.kt` as the doctor's day-of view (keep `AppointmentConfirmationScreen` if you want a separate "booking requests" tab, but the live queue is the new primary screen). Shows: current token being served, big "Call Next" primary action, ordered list of `WAITING` entries with per-row actions (Prioritize, Skip, No-show), "Add Walk-in" button opening a patient-picker dialog, and an "Open/Close Queue for Today" toggle that writes `isWalkInOpen` / creates today's `DoctorDaySlotConfig` if missing.
2. **`feature/doctor/components/DoctorSlotConfigDialog.kt`** (new) — simple form: date (defaults today), start/end time, capacity, walk-in toggle. Calls `defineDoctorSlot`.
3. **`feature/patient/QueueStatusScreen.kt`** (new) — shown after check-in. Displays token number (or "Confirming your position…" if `provisionalToken`), live position, ETA (formatted as "~12 min"), doctor name, and a Cancel button. Update `AppointmentsScreen.kt` so any appointment card whose `dateFormatted` is today shows a "Check In" button that calls `checkInAppointment` and navigates to this screen; also add a "Join Walk-in Queue" entry point from wherever the patient currently views a doctor's profile, gated on `isWalkInOpen`.
4. **`feature/admin/QueueOversightScreen.kt`** (new, under the existing admin feature package) — read-only list of `DoctorQueueSummary` cards (waiting count, current token, avg wait, open/closed badge), tap-through to a read-only version of the same queue list UI used in `DoctorQueueScreen` (extract the row/list composable into `feature/doctor/components/QueueEntryListItem.kt` so both screens share it instead of duplicating).
5. Wire all three new screens into `VitalSenseNavGraph.kt` following the existing role-graph pattern (look at how `AppointmentConfirmationScreen` and `AppointmentsScreen` are currently routed and mirror it).

### 4.4 ViewModels

- Extend `DoctorViewModel.kt` with the queue state/actions (`todaysQueue: StateFlow<List<QueueEntry>>`, `callNext()`, `startConsultation(id)`, etc.), following its existing `flatMapLatest` + `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ...)` idiom — do not introduce a different reactive pattern.
- Add `PatientQueueViewModel.kt` (new, `feature/patient` package) for the patient-facing check-in/status screen.
- Add `AdminQueueOversightViewModel.kt` (new, `feature/admin` package) for the summary view.

---

## 5. Offline behavior (must not regress the app's core offline-first promise)

- A patient who is offline can still tap "Check In" — see §3.1 provisional-token path. They can also view their **last-synced** queue position/ETA from Room if they lose connectivity after checking in; clearly label this as "Last updated Xm ago" rather than presenting stale data as live.
- If the doctor's device goes offline mid-session, queue-management actions (`callNext`, `completeConsultation`, etc.) should still apply optimistically to the local Room-backed state and queue into the outbox — the doctor should not be blocked from running their clinic because of a network blip. Reconciliation on reconnect follows the same outbox/`SyncWorker` drain already in place.
- Do not attempt to build a custom local conflict-resolution engine for the queue beyond what's specified above — last-write-wins on reconnect (matching the project's existing documented conflict policy in `tech-stack.md` §3) is acceptable for this feature.

---

## 6. Notifications

Add a Cloud Function (`functions/index.js` or `.ts`, following whatever convention the repo's `Cloud Functions for Firebase` setup already uses — if none exists yet, create a minimal `functions/` directory) triggered `onWrite` for `queue_entries/{entryId}`:

- If `status` changed to `CALLED`, send an FCM push to the patient: "It's your turn — please proceed to [doctor name]."
- If the entry's computed position (recompute server-side using the same ordering rule as §3.2/3.3) becomes `<= 2` and a "near-turn" notification hasn't already been sent for this entry, send: "You're next in line — about N people ahead of you."

Add `com.google.firebase:firebase-messaging-ktx` to `app/build.gradle` (not currently present) and a minimal `FirebaseMessagingService` subclass under `core/` to receive and display these as local notifications, following Android's standard FCM setup. Do not build a client-side polling fallback for this — real-time was the explicit requirement.

---

## 7. Security rules

Add/extend `firestore.rules` (if the repo doesn't have one yet, create it at the repo root and note that it needs to be deployed via `firebase deploy --only firestore:rules`):

```
match /queue_entries/{entryId} {
  allow read: if request.auth != null; // tighten to matching doctorId/patientId/admin claim once custom claims exist
  allow create: if request.auth != null;
  allow update: if request.auth != null
    && (resource.data.doctorId == request.resource.data.doctorId); // doctor-owned entries only mutate within same doctor
  allow delete: if false;
}
match /doctor_day_slots/{slotId} {
  allow read: if request.auth != null;
  allow write: if request.auth != null;
}
match /queue_counters/{counterId} {
  allow read, write: if request.auth != null;
}
```

**Flag this explicitly in your output:** the current app (see `FirestoreDataSource.kt`'s `signInAnonymously()` call) has no real per-role auth or custom claims yet — everything runs under anonymous Firebase Auth. The rules above are therefore intentionally permissive (any authenticated, i.e. any anonymous, client). Add a `TODO` comment block in `firestore.rules` describing the tightened version (`request.auth.token.role == 'doctor' && request.auth.token.doctorId == resource.data.doctorId`, etc.) that should replace this once real role-based auth with custom claims is implemented — do not silently ship a false sense of security by pretending these rules are production-final.

---

## 8. Testing (required, not optional)

- **Unit tests** (`app/src/test/...`, JUnit + coroutines-test, matching whatever test setup already exists in the repo — check `app/src/test` first):
  - `QueueEtaCalculatorTest` — covers the fallback chain in §3.3 (today's data / 7-day fallback / hardcoded default).
  - `QueueOrderingTest` — covers §3.2 (priority flag sorting, stability of ordering, `IN_CONSULTATION` single-doctor invariant).
  - A repository test that simulates two near-simultaneous `checkIn` calls against a fake/in-memory transaction implementation and asserts no duplicate token numbers.
- **Instrumented/Compose UI test** for `DoctorQueueScreen`: verify tapping "Call Next" transitions the top `WAITING` entry to `CALLED` and updates the displayed current-token text.

---

## 9. Definition of done

- [ ] Room migration 3→4 applied without data loss (explicit `Migration`, not destructive fallback)
- [ ] Doctor can open/close today's queue, define slot capacity, add walk-ins, call next, start/complete consultations, skip, mark no-show, and manually prioritize
- [ ] Patient can check in to a same-day appointment or join an open walk-in queue, see live token/position/ETA, and cancel
- [ ] Admin sees a read-only live summary across all doctors and can drill into any one doctor's queue, with no mutation controls exposed
- [ ] Offline check-in produces a provisional token that reconciles automatically once connectivity returns, via the existing outbox/`SyncWorker` mechanism — no second sync system introduced
- [ ] Token numbers are assigned via an atomic Firestore transaction; a concurrency test proves no collisions
- [ ] FCM notifications fire on "called" and "near-turn" (position ≤ 2)
- [ ] `firestore.rules` updated and the auth gap explicitly flagged with a TODO for future custom-claims tightening
- [ ] All new UI strings added as string resources (no hardcoded patient-facing text), consistent with the app's existing localization approach
- [ ] Unit tests for ETA calculation, ordering, and token-collision safety all pass
- [ ] No new UI components invented where an existing shared component (`VitalSenseCard`, `VitalSenseButton`, theme) already covers the need
