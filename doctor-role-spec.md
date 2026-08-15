# VitalSense — Doctor Role Specification

**Purpose:** This document details every responsibility, permission, screen, and data interaction the **Doctor** role requires in the VitalSense Android app. Use this as the implementation reference for building (or auditing) the Doctor-facing features.

**Scope note:** Doctor account approval/onboarding workflow is intentionally excluded from this document — treat doctor accounts as already provisioned and active for implementation purposes.

---

## 1. Role Summary

The Doctor is a specialist-categorized medical professional who reviews patient-submitted cases, responds with medical guidance, issues prescriptions, and manages appointments. Doctors operate on a **restricted patient view** — they only see patients who were referred to them or who specifically requested their specialty.

---

## 2. Core Duties (from PRD)

### 2.1 Case Review
- View a queue/list of patient case submissions assigned or routed to them.
- Each case includes: **condition description, category type, severity (low/mid/high/severe)**, and the requested doctor type/specialty.
- Must be able to open a case and see full context: patient's health record, prior prescriptions, prior doctor responses, and any uploaded reports/photos relevant to the case.

### 2.2 Medical Response
- Compose and submit a **free-text medical response** to a patient case.
- Response must be timestamped (date + time) and permanently attached to that case in the patient's history.
- Doctor should be able to view their own past responses for a given patient (response history, not just latest).

### 2.3 Prescription Management
- Issue a **structured prescription** tied to a case, containing:
  - Medicine name
  - Dosage
  - Quantity
- Every prescription is timestamped (date + time saved).
- Doctor can **view and update** prescriptions they've issued (e.g., correcting dosage, adding a follow-up prescription).
- Prescription quantity/availability checks against the **dispensary dataset** (prototype: hardcoded/mock; architecture should allow swapping in a live inventory API later without restructuring the prescription data model).

### 2.4 Appointment Scheduling
- Doctor can **propose an appointment time** to a patient (bidirectional scheduling — not just accepting patient-proposed times).
- Doctor can **view, accept, decline, or reschedule** appointment requests initiated by a patient or ASHA proxy.
- Needs a calendar/list view of upcoming confirmed appointments.
- Mutual confirmation required — an appointment isn't "booked" until both sides have confirmed.

### 2.5 Specialty Categorization
- Each doctor account has a **doctor type/specialty** field (e.g., physician, psychologist, neurosurgeon — should be an extensible enum/list, not hardcoded to just these three).
- This specialty value determines which incoming patient cases are routed to this doctor (patients/ASHA workers select a "desired doctor type" when submitting a case).
- A doctor should only see cases matching their specialty, or cases explicitly assigned/referred to them by another doctor or admin (see §4 below for a suggested addition).

### 2.6 Mental Health Referral Handling
- Doctors categorized as **psychologist**-type specifically receive referrals originating from the Patient app's **Mental Stress Relief** section.
- These referrals should arrive through the **same case/category/doctor-type pipeline** as physical health cases — no separate parallel system — but should be visually/contextually flagged as a mental-health-origin case so the doctor has appropriate context before opening it.

---

## 3. Permissions & Access Scope (Enforce at Data Layer, Not Just UI)

| Rule | Detail |
|---|---|
| Patient visibility | Doctor sees ONLY patients who (a) submitted a case requesting their specialty, or (b) were referred/assigned to them |
| Health Card access | **View-only** — doctors cannot edit or generate a patient's Health Card |
| No proxy access | Doctors cannot act on behalf of a patient (unlike ASHA workers) — no editing patient-submitted data, only responding to it |
| No admin functions | No village management, no heat map, no broadcast instructions, no doctor/ASHA account review |
| No map access | Per current feature matrix, doctors do not get the doctor/hospital map view (patient/ASHA-only feature) |
| No government scheme access | Not currently in doctor's feature set |
| No SOS handling | Doctors are not part of the SOS alert chain (ASHA worker + secondary contact only) |
| No offline core access | Doctor role is not listed as requiring offline-first functionality — assume online-required for doctor dashboard (reasonable, since doctors are presumed to have facility-based connectivity, unlike rural patients) |

**RBAC implementation note:** All of the above must be enforced server-side / at the data query layer, not just hidden in the UI. A doctor's API/database queries should be scoped by their doctor ID and specialty at all times — never trust client-side role checks alone.

---

## 4. Suggested Additions (Not in Original PRD — Recommended for Implementation)

These are gaps or natural extensions I'd recommend adding to the Doctor role, based on what similar clinical workflows typically need. Flag these to the team as proposed scope — they're not confirmed requirements, but implementing the architecture to support them now will save rework later.

### 4.1 Case Triage / Priority Sorting
The PRD doesn't specify how a doctor's case queue is ordered. Recommend:
- Default sort by **severity** (severe/high cases surfaced first), then by submission time (oldest first within same severity).
- A visual severity indicator (color-coded badge) on each case in the queue.

### 4.2 Case Status / Workflow States
Currently the PRD implies a case just gets "a response," but doesn't define a lifecycle. Recommend adding explicit case states so both doctor and patient know where things stand:
- `Pending Review` → `In Progress` (doctor opened it) → `Responded` → `Closed` (or `Follow-up Requested`)
- This also gives the Admin's future analytics something structured to report on (e.g., average time-to-response per doctor).

### 4.3 Doctor-to-Doctor Referral / Escalation
Not in the PRD, but clinically realistic: a physician reviewing a case may need to **refer it onward** to a specialist (e.g., a general physician suspects a neurological issue and wants to route to a neurosurgeon).
- Add a "Refer to specialist" action on a case, which re-routes it into another specialty's queue while preserving full case history and the referring doctor's notes.

### 4.4 Doctor Notes (Private, Not Patient-Facing)
Recommend a private notes field per patient/case, visible only to doctors (and possibly other doctors treating the same patient), separate from the patient-facing "response." This is standard in clinical software — doctors often need to record observations that aren't meant for direct patient communication.

### 4.5 Prescription History / Drug Interaction Awareness (Prototype-Light Version)
Even at prototype stage, consider surfacing a patient's **prior prescriptions list** prominently when a doctor is about to issue a new one — reduces risk of duplicate/conflicting prescriptions. Full interaction-checking is out of scope, but the visibility alone is low-cost and high-value.

### 4.6 Doctor Dashboard Summary View
A landing dashboard for doctors showing at a glance:
- Count of pending cases
- Today's confirmed appointments
- Any recently updated cases they're following

### 4.7 Response Templates / Quick Replies (Optional, Lower Priority)
For common guidance (e.g., "rest and hydration, follow up if symptoms persist 3+ days"), allow doctors to save and reuse response templates — reduces friction for high-volume rural case review. Lower priority than the above, but worth noting as a future efficiency feature.

### 4.8 Appointment Notes / Outcome Logging
After a scheduled appointment occurs, allow the doctor to log a brief outcome/follow-up note tied to that appointment — currently the PRD only covers scheduling, not post-appointment documentation.

---

## 5. Data Model Touchpoints (For Implementation Reference)

The Doctor role interacts with (at minimum) these data entities — ensure schema/API design accounts for all of them:

- **Doctor profile**: ID, name, specialty/type, credentials (future), active status
- **Case**: patient ID, condition, category, severity, requested doctor type, assigned doctor ID, status (see §4.2), timestamps
- **Response**: case ID, doctor ID, text content, timestamp
- **Prescription**: case ID, doctor ID, patient ID, medicine name, dosage, quantity, timestamp, dispensary-check reference
- **Appointment**: patient ID, doctor ID, proposed time(s), confirmation status (pending/confirmed/declined), initiator (patient/ASHA/doctor)
- **Referral (if §4.3 implemented)**: originating case ID, referring doctor ID, target specialty/doctor ID, notes

---

## 6. Screens Required (Doctor-Facing)

1. **Login** (shared single login screen, routes to Doctor dashboard on role match)
2. **Dashboard / Summary** (see §4.6)
3. **Case Queue** (list, filterable/sortable by severity and status)
4. **Case Detail View** (patient context, health history, prior prescriptions, response composer, prescription composer)
5. **Prescription Issue/Edit Form**
6. **Appointment Calendar / List** (view, propose, accept, decline, reschedule)
7. **Patient Health Card** (view-only)
8. **Doctor Notes panel** (if §4.4 implemented — likely embedded in Case Detail View rather than standalone)

---

## 7. Explicitly Out of Scope for Doctor Role (Do Not Implement)

- Admin functions (villages, heat map, broadcasts, account approval/review)
- ASHA-style proxy actions on behalf of a patient
- Editing/generating a patient's Health Card
- SOS alert handling
- Government scheme content management
- Doctor/hospital map view
- Payments/billing (out of scope for entire app per PRD §7)

---

*End of specification. Cross-reference against `prd.md` for full multi-role context before implementation.*
