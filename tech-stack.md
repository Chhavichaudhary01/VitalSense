# VitalSense — Tech Stack Document

**Platform:** Native Android, built in Android Studio
**Document status:** Draft v1.0 — prototype/MVP scoping

---

## 1. Guiding Principles

- **Offline-first**: local database is the source of truth for the UI; network is a sync layer, not a dependency for core reads.
- **Single app, role-aware**: one codebase, one auth flow, role-driven navigation graph — not four separate apps glued together.
- **Low-resource friendly**: rural users may have older devices, low RAM, and 2G/3G connectivity — keep payloads, animations, and background work light.
- **Prototype-pragmatic**: use hardcoded/mocked data (dispensary stock, government schemes) where a real backend integration is out of scope for now, but structure code so mocks are swappable for real services later (repository pattern).

---

## 2. Application Architecture

- **Language**: Kotlin (100%), using modern Android conventions.
- **Architecture pattern**: **MVVM** (Model-View-ViewModel) with a **Repository layer**, following official Android App Architecture guidance.
- **UI toolkit**: **Jetpack Compose** (recommended) for faster iteration on an icon-heavy, highly visual, multi-role UI; XML/View system is a fallback if the team has more XML experience — either is viable, but Compose is preferred for the theming and localization flexibility needed here.
- **Navigation**: Jetpack **Navigation Component** (Compose Navigation if using Compose), with a role-based navigation graph decided at login (Admin graph / ASHA graph / Doctor graph / Patient graph), sharing common screens (e.g., chat, health card) where logic overlaps.
- **Dependency Injection**: **Hilt** (built on Dagger) for clean, testable module wiring across the many role-specific ViewModels/Repositories.
- **Asynchronous work**: Kotlin **Coroutines + Flow** for all async operations (DB reads, network calls, sync jobs).

---

## 3. Local Data & Offline Support

- **Local database**: **Room** (SQLite abstraction) — stores patients, ASHA caseloads, condition entries, prescriptions, chats, cached map/doctor data, and queued outbound actions.
- **Offline-first sync strategy**:
  - Repository pattern: ViewModels only ever talk to Repositories, which decide whether to serve from Room (cache) or trigger a network fetch.
  - **WorkManager** for background sync jobs — retries automatically under Android's constraints (network available, battery not low), ideal for intermittent rural connectivity.
  - An **outbox pattern**: writes made offline (new symptom entry, chat message, uploaded prescription) are stored locally with a `pending_sync` flag and pushed when connectivity returns.
  - Conflict resolution: last-write-wins for prototype, with a manual-review flag for high-sensitivity fields (e.g., severity level) if a conflict is detected.
- **File/media caching**: uploaded images (prescriptions, reports) stored locally first (app-private storage), uploaded opportunistically; thumbnails cached for instant offline viewing.
- **Cached "previously viewed" reports**: any report/prescription/chat the user has opened is guaranteed to be cached in Room + local file storage for offline re-access (per PRD §4.7).

---

## 4. Backend & Cloud Services

For a prototype/MVP with a small team, **Firebase** is recommended as the fastest path to a working multi-role, real-time backend without standing up custom infrastructure:

| Need | Recommended Service |
|---|---|
| Authentication (role-based login) | **Firebase Authentication** (email/phone + custom claims for role: admin/asha/doctor/patient) |
| Primary structured data (patients, cases, prescriptions, appointments, chats) | **Cloud Firestore** (NoSQL, real-time sync, offline persistence built in — pairs naturally with the offline-first requirement) |
| File storage (prescription images, reports, chat attachments) | **Firebase Storage** |
| Push notifications (instructions, notices, appointment updates) | **Firebase Cloud Messaging (FCM)** |
| Background/serverless logic (e.g., aggregating heat-map data, triggering SOS SMS relay) | **Cloud Functions for Firebase** |
| Analytics (usage patterns, drop-off points in onboarding) | **Firebase Analytics** |

> **Note:** Firestore's native offline persistence complements, but does not replace, the local Room database — Room remains useful for app-specific derived/cached data (e.g., pre-rendered Health Card state, instruction "seen" flags) that shouldn't live in the remote schema.

**Alternative** (if the team prefers full control / avoiding vendor lock-in): a custom REST/GraphQL backend (e.g., Node.js/Express or Django) with PostgreSQL + PostGIS (for map/geo queries) and a self-hosted object store (S3-compatible) — more setup effort, better long-term flexibility for a real dispensary/government-system integration. Recommended only if the team already has backend engineering capacity; otherwise Firebase is the faster prototype path.

---

## 5. Maps & Location

- **Google Maps SDK for Android** + **Places API** for the "nearest doctor/hospital" map view.
- **Fused Location Provider API** (Google Play Services) for the patient's current location (used for map centering and SOS location sharing).
- Offline consideration: cache last-fetched nearby facility list + basic map tile region if feasible; Maps SDK has limited offline support, so the fallback UX should clearly show "showing last known nearby facilities" when offline.

---

## 6. AI / OCR for Prescription Digitization

Goal: a patient uploads a photo of a physical prescription → app extracts the text → patient/ASHA reviews and edits before saving as structured data.

Recommended approach for prototype:

- **On-device text recognition**: **ML Kit Text Recognition (Google)** — free, works offline, good for extracting raw text from a photographed prescription without needing network access (fits the offline-first requirement).
- **Structuring the extracted text** (turning raw OCR text into medicine name / dosage / quantity fields): a lightweight rules/heuristic parser for the prototype, with an option to call a **cloud LLM API (e.g., Anthropic Claude API)** when online for higher-accuracy structuring/translation of messy handwriting-derived text — falling back to manual entry/edit when offline or when confidence is low.
- Always require a **human-in-the-loop confirmation step**: OCR output is pre-filled into an editable form, never auto-saved without review, since prescription accuracy is safety-critical.

---

## 7. Emergency SOS & Messaging

- **SMS**: Android `SmsManager` API (or a share/`Intent`-based fallback to the default SMS app) to guarantee SOS alerts work with **no data connectivity**, only cellular signal.
- **In-app/push notification**: Firebase Cloud Messaging, used when data connectivity is available, as a faster/richer complement to SMS.
- SOS payload includes patient identity, last known location (if available), and timestamp.
- Required permissions: `SEND_SMS`, `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` — must be requested with clear, icon-based rationale screens given the target user's low digital literacy.

---

## 8. Localization

- Android's built-in **resource-based localization** (`strings.xml` per locale / `res/values-<lang>/`).
- In-app **language switcher** (not just device-locale-based), since users may want to pick a regional language different from their device default.
- All patient-facing instructional/help content authored in a translatable format (string resources or a CMS-style Firestore collection keyed by language code) so new languages can be added without a code release.

---

## 9. Notifications & Chat

- **Firebase Cloud Messaging** for admin broadcasts, ASHA notices, appointment reminders.
- **Chat**: Firestore real-time listeners for ASHA↔Patient and relevant doctor threads, with local Room caching so chat history is viewable offline; new offline messages queue via the outbox pattern (§3) and send on reconnect.

---

## 10. Data Visualization (Admin Heat Map)

- A Compose-based custom heat map / choropleth view over villages/regions (color-coded by case density/severity), or **Google Maps heatmap layer** (`android-maps-utils` HeatmapTileProvider) if plotting over an actual geographic map is preferred over an abstract village-list view.
- Aggregation of anonymized case data (condition category, severity, village) can be computed via a **Cloud Function** on write, keeping the heavy aggregation off-device.

---

## 11. Security & Privacy

- **Firebase Authentication** with custom claims for RBAC (`role: admin | asha | doctor | patient`), enforced via **Firestore Security Rules** so role permissions are checked server-side, not just hidden in the UI.
- Sensitive health data encrypted at rest (Firestore/Storage default encryption) and in transit (TLS by default).
- ASHA "proxy access" to a patient's data should be modeled explicitly in the data schema (e.g., an `authorizedHelpers` list on each patient record) and checked in security rules — not assumed from role alone.
- Local Room database: consider **SQLCipher for Android** (encrypted SQLite) for at-rest protection of cached health data on-device, especially given shared/family device usage common in rural settings.

---

## 12. Suggested Module/Package Structure

```
com.vitalsense.app
├── core/
│   ├── di/                 (Hilt modules)
│   ├── data/
│   │   ├── local/          (Room DB, DAOs, entities)
│   │   ├── remote/         (Firestore/Firebase or REST services)
│   │   └── repository/     (Repository implementations — single source of truth per feature)
│   ├── sync/                (WorkManager sync jobs, outbox pattern)
│   ├── location/
│   └── ui/theme/            (Compose theme, icon library, shared components)
├── feature/
│   ├── auth/                (login, role routing)
│   ├── admin/                (villages, heatmap, broadcasts, review)
│   ├── asha/                 (caseload, proxy actions, notices)
│   ├── doctor/                (case review, prescriptions, appointments)
│   ├── patient/
│   │   ├── healthcard/
│   │   ├── conditionentry/
│   │   ├── prescriptions/      (upload, OCR, manual entry)
│   │   ├── appointments/
│   │   ├── map/
│   │   ├── schemes/
│   │   ├── mentalhealth/
│   │   ├── sos/
│   │   └── help/                (inline instructions + full manual)
│   └── chat/
└── MainActivity / NavGraph
```

---

## 13. Summary Stack Table

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM + Repository pattern |
| DI | Hilt |
| Async | Coroutines + Flow |
| Local DB | Room (+ SQLCipher for encryption) |
| Background sync | WorkManager (outbox pattern) |
| Backend (recommended) | Firebase (Auth, Firestore, Storage, Cloud Functions, FCM) |
| Maps | Google Maps SDK + Places API + Fused Location Provider |
| OCR | ML Kit Text Recognition (on-device) + optional cloud LLM structuring when online |
| SOS/SMS | Android `SmsManager` / SMS intent fallback |
| Notifications | Firebase Cloud Messaging |
| Localization | Android string resources + in-app language switcher |
| Heat map | Google Maps heatmap layer (android-maps-utils) or custom Compose choropleth |
| Security | Firebase Auth custom claims + Firestore Security Rules + local encryption |

---

## 14. Notes on Prototype vs. Production

| Item | Prototype approach | Future production path |
|---|---|---|
| Dispensary/medicine stock | Hardcoded mock dataset in-app | Real-time API integration with dispensary inventory system |
| Government schemes | Static curated content | Live feed from government open-data API |
| SMS for SOS | Native Android SMS intent/`SmsManager` | Dedicated SMS gateway provider (e.g., Twilio, or telecom partnership) for reliability guarantees |
| OCR structuring | Heuristic parser + optional cloud LLM call | Fine-tuned/purpose-built medical OCR model |
| Backend | Firebase (fast to build, real-time, offline-friendly) | Evaluate custom backend if scale/compliance needs exceed Firebase's fit |
