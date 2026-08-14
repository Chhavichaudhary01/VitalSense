# VitalSense — Product Requirements Document (PRD)

**Platform:** Android (built with Android Studio)
**Document status:** Draft v1.0 — prototype/MVP scoping
**Audience:** Team engineers, designers, and stakeholders

---

## 1. Overview

VitalSense is a single Android application that serves four distinct user roles — **Admin, ASHA Worker, Doctor, and Patient** — behind separate, role-based logins. Rather than building four apps, VitalSense is one codebase with role-aware navigation, permissions, and dashboards, so rural health infrastructure (ASHA workers, doctors, and administrators) and rural patients can operate in one connected ecosystem.

### 1.1 Problem Statement

VitalSense exists to solve three interconnected problems:

1. **Early detection of disease and viral trends** — Aggregating patient-reported symptoms, severity, and category data (village/region level) so outbreaks and trends can be spotted early via an admin-facing heat map.
2. **Bridging rural populations and medical facilities** — Connecting patients in villages with the nearest available doctors/hospitals, ASHA workers as human intermediaries, and a structured referral/appointment/prescription flow.
3. **Mental stress relief support** — Providing patients (and the ASHA workers helping them) with accessible, low-friction ways to seek mental health support, alongside physical health tracking.

### 1.2 Target Users

| Role | Description |
|---|---|
| **Admin** | Oversees the whole system — regions/villages, disease trend monitoring, staff (doctor & ASHA) performance, and system-wide broadcasts. |
| **ASHA Worker** | Community health worker who manages a caseload of patients, acts as a digital proxy/helper for patients who can't use the app themselves, and is the first point of contact for many patients. |
| **Doctor** | Reviews patient cases, responds with medical guidance, prescribes medicine, and manages appointments — categorized by specialty (physician, psychologist, neurosurgeon, etc.). |
| **Patient** | Rural end-user, often with low digital literacy and low/no connectivity, who needs simple, icon-driven access to health tracking, doctor connection, medicine info, government schemes, and emergency help. |

---

## 2. Goals & Success Metrics (Prototype Phase)

| Goal | Metric |
|---|---|
| Enable early trend detection | Admin heat map reflects village-level case/severity data with < 5 min refresh in online mode |
| Reduce friction for rural patients | Patient can complete self-registration and first symptom entry in under 5 taps/screens |
| ASHA worker as force-multiplier | One ASHA worker can manage and act on behalf of many patients without duplicate data entry |
| Works without reliable connectivity | Core patient flows (view health card, log symptoms, view cached reports) function fully offline |
| Reduce patient overwhelm | Every patient screen has an inline "what is this page for" instruction, dismissible, with a full manual accessible separately |

---

## 3. Roles, Permissions & Core Functionality

### 3.1 Admin

- **Add villages/regions** to the system (used for grouping patients, ASHA workers, and heat-map geography).
- **View heat map** of registered users and disease/symptom trends by village/region, filterable by condition category, severity, and time range.
- **Send instructions/broadcasts** to all users or targeted role groups (e.g., all ASHA workers in a region, all patients in a village).
- **Review doctors and ASHA workers** — view performance/activity, approve new doctor/ASHA accounts, flag/deactivate accounts.
- (Prototype-only) View a hardcoded dispensary/medicine stock view, standing in for a future real dispensary integration.

### 3.2 ASHA Worker

- Has a **unique ASHA ID**, shareable with patients so patients can add them as a "helper."
- Manages a **caseload of patients** ("their patients") — add new patients, view all patients & their statuses, see who is currently active/at-risk.
- Can **send instructions/notices and chat** with their patients.
- **Full proxy access**: everything a patient can do in the app, an ASHA worker can do on that patient's behalf (entering symptoms, uploading prescriptions/reports, scheduling appointments, etc.) — for patients who struggle with the app directly.
- Receives/relays **government scheme information** relevant to their patients.

### 3.3 Doctor

- Categorized by **doctor type/specialty** (e.g., physician, psychologist, neurosurgeon).
- Reviews patient case submissions (condition, severity, category).
- Provides a **response** and, where applicable, a **medicine prescription**.
- Can **schedule appointments** with patients (bidirectional — patient or doctor can propose a time).
- Views/updates prescriptions, each saved with **date and time**, tied to checkpoint/quantity data from the (prototype: hardcoded) dispensary.

### 3.4 Patient

- Can **self-register**, or be **registered by an ASHA worker**.
- Can **add an ASHA worker as a helper** by entering the worker's unique ID.
- Patient (or their ASHA helper) can:
  - Enter/update a health record: **condition severity** (low / mid / high / severe), **category type**, and desired **doctor type**.
  - View **doctor responses** and **prescriptions**.
  - **Request/accept appointments** with a doctor.
  - View/generate a **Health Card**: current risk level + patient details, always accessible, works offline (cached).
  - **Upload old prescriptions, old chats, and old reports** (as photos or files).
  - **Upload a photo of a prescription** — if no physical prescription exists, **manually fill in** a digital prescription form instead.
  - Use an **AI OCR feature** to auto-read an uploaded prescription image and convert it to structured digital text for review/edit before saving.
  - View a **map** of nearest doctors/hospitals.
  - View **government scheme** information relevant to them.
  - Use a prominent **Emergency SOS button** that triggers SMS/notification alerts to ASHA worker/emergency contacts.
  - Access a **mental stress relief** section (see §4.3).
  - Use the app in their **regional/local language**.

---

## 4. Detailed Functional Requirements

### 4.1 Authentication & Role Routing

- Single app, single login screen; login determines role and routes to the correct dashboard.
- Role-based access control (RBAC) enforced both in UI (what's shown) and at the data layer (what can be read/written).
- ASHA-as-proxy access must be scoped: an ASHA worker only gets full access to patients who are (a) in their caseload, or (b) have explicitly added that ASHA worker's ID as their helper.
- Doctors only see patients assigned/referred to them or who requested their specialty.

### 4.2 Disease Trend Detection & Heat Map (Admin)

- Every patient case entry (condition, category, severity, village) feeds an aggregated, anonymized trend dataset.
- Admin heat map: village/region-level visualization, color-coded by case volume/severity, filterable by disease category and date range.
- Designed to surface emerging clusters (e.g., spike of similar symptoms in one village) for early outbreak/trend detection.

### 4.3 Mental Stress Relief

- A dedicated, low-friction, icon-based section within the Patient dashboard (and visible/usable by ASHA proxy) offering:
  - Simple mood/stress check-ins.
  - Guided breathing/relaxation content.
  - A path to flag stress/mental health concerns to their ASHA worker or a psychologist-type doctor directly (feeds into the same condition/category/doctor-type flow as physical health).
- Designed to reduce stigma and reduce app complexity — no long text, primarily icon/visual driven.

### 4.4 Rural-to-Medical-Facility Bridge

- **Map view** for patients showing nearest doctors and hospitals (distance, type, availability where known).
- **Appointment scheduling**, initiated by either patient/ASHA or doctor, with mutual confirmation.
- **Government scheme information** surfaced to patients and ASHA workers (informational, filterable by category — e.g., maternal health, disability, elderly care).
- ASHA worker acts as the human bridge — chat, notices, and full proxy actions for patients who can't self-navigate the app or reach a facility directly.

### 4.5 Health Records, Prescriptions & Health Card

- **Condition entry**: severity (low/mid/high/severe), category type, requested doctor type.
- **Doctor response & prescription**: text response + structured prescription (medicine name, dosage, quantity), all timestamped (date & time saved).
- **Dispensary linkage (prototype)**: prescription medicine availability/quantity is shown via a **hardcoded mock dataset** representing a future real-time dispensary system connected to Admin.
- **Health Card**: auto-generated, always-visible summary card with patient details + current risk level (derived from latest severity/condition data), viewable and shareable, cached for offline access.
- **Uploads**: old prescriptions, old chat logs, and old reports can be uploaded (image/file) by patient or ASHA proxy.
- **Prescription capture**:
  - Option A: Upload photo of physical prescription → **AI OCR** extracts and digitizes the text → user reviews/edits/confirms before saving.
  - Option B: No physical prescription available → **manual digital entry** form.

### 4.6 Localization

- Full UI localization into regional/local languages (language selectable at first launch and changeable in settings).
- All patient-facing instructional content, labels, and AI OCR output should support the selected regional language (OCR output may need translation step where source prescription is in a different language than the patient's selected UI language).

### 4.7 Offline-First & Low Connectivity

- Core patient functionality must work fully offline:
  - Viewing Health Card, previously viewed/cached reports, prescriptions, and instructions.
  - Drafting/queuing new entries (symptoms, prescription uploads, chat messages) for sync once connectivity returns.
- Data syncs automatically in the background when connectivity is available; conflict resolution favors most-recent-edit-wins for prototype, with manual review flag for conflicting critical fields (e.g., severity).
- Map and doctor/hospital data cached from last successful fetch for offline browsing (read-only when offline).

### 4.8 Emergency SOS

- Persistent, icon-heavy **SOS button** on the patient dashboard.
- On trigger: sends SMS + in-app notification to the patient's linked ASHA worker (and optionally a secondary emergency contact), including patient location if available.
- Must function via SMS even with no data connectivity (SMS fallback path required — see Tech Stack).

### 4.9 Instructions & Onboarding UX

- Every patient-facing page includes a **short, inline, dismissible instruction** (icon + minimal text) explaining that page's purpose the first time it's opened (and re-accessible via a help icon).
- A separate **full manual/help section** exists where a patient can browse instructions for every feature at their own pace.
- Design priority throughout the patient experience: **icon-heavy, minimal text**, large touch targets, suited for low digital literacy.

### 4.10 Chat & Notices

- ASHA ↔ Patient chat (basic text; should degrade gracefully to SMS-style queued messages under poor connectivity).
- Admin → all-users or targeted broadcast notices/instructions.
- ASHA → their patients notices.

---

## 5. Non-Functional Requirements

- **Offline resilience**: Core flows must not hard-fail without internet; app should clearly indicate offline/sync status.
- **Low-network tolerance**: All network calls should be resilient to slow/intermittent connections with retry/queue logic; avoid large payloads on constrained connections.
- **Accessibility & literacy**: Icon-first design, minimal reading burden, large fonts/touch targets, regional language support.
- **Privacy & data sensitivity**: Health data (conditions, prescriptions, mental health entries) is sensitive; role-based access control and secure storage are required even at prototype stage.
- **Performance**: Health Card and cached reports should render instantly (local-first read).
- **Scalability (future)**: Architecture should not preclude connecting to a real dispensary/pharmacy system, real SMS gateway, and multi-district admin hierarchies later.

---

## 6. Prototype Scope & Assumptions

The following are explicitly simplified/mocked for the prototype and called out as future real integrations:

- **Dispensary/medicine stock**: hardcoded mock data, not a live inventory system.
- **AI OCR for prescriptions**: on-device or API-based OCR (see tech-stack.md) — prototype-grade accuracy is acceptable; human review/edit step is mandatory before saving.
- **Government schemes data**: static/curated content set for prototype, not a live government API feed.
- **SMS gateway for SOS**: prototype may use Android's native SMS intent/API rather than a full telecom-grade gateway.

---

## 7. Out of Scope (for this phase)

- Payments/billing.
- Real-time video consultation.
- Full EHR (Electronic Health Record) interoperability with hospital systems.
- Multi-district/state-level admin hierarchy (single admin tier assumed for prototype).
- Wearable device integration.

---

## 8. Open Questions

- What regional languages must be supported at launch, and who provides/reviews the translations?
- What's the legal/compliance requirement around storing patient health data (state health data regulations)?
- Who is the "secondary emergency contact" for SOS if no ASHA worker is reachable?
- Should doctors be admin-approved before activation, or self-registered with admin review after the fact?
- What's the expected patient volume per ASHA worker (affects caseload UI/pagination design)?

---

## 9. Appendix — Feature Matrix by Role

| Feature | Admin | ASHA Worker | Doctor | Patient |
|---|:---:|:---:|:---:|:---:|
| Add villages | ✅ | ❌ | ❌ | ❌ |
| Heat map / trend view | ✅ | ❌ | ❌ | ❌ |
| Broadcast instructions | ✅ (all) | ✅ (own patients) | ❌ | ❌ |
| Review doctors/ASHA workers | ✅ | ❌ | ❌ | ❌ |
| Manage patient caseload | ❌ | ✅ | ❌ | ❌ |
| Register new patient | ❌ | ✅ | ❌ | ✅ (self) |
| Act as proxy for a patient | ❌ | ✅ | ❌ | — |
| Add helper via ASHA ID | ❌ | — | ❌ | ✅ |
| Enter condition/severity | ❌ | ✅ (proxy) | ❌ | ✅ |
| Respond & prescribe | ❌ | ❌ | ✅ | ❌ |
| Schedule appointment | ❌ | ✅ (proxy) | ✅ | ✅ |
| View/generate Health Card | ❌ | ✅ (proxy) | view only | ✅ |
| Upload prescriptions/reports/chats | ❌ | ✅ (proxy) | ❌ | ✅ |
| AI OCR prescription read | ❌ | ✅ (proxy) | ❌ | ✅ |
| View map (doctors/hospitals) | ❌ | ✅ | ❌ | ✅ |
| Government scheme info | ❌ | ✅ | ❌ | ✅ |
| SOS button | ❌ | receives alert | ❌ | ✅ |
| Mental stress relief section | ❌ | ✅ (proxy) | receives referral | ✅ |
| Offline core access | ❌ | ✅ | ❌ | ✅ |
