# 🏥 VitalSense (SehatSetu) — System Architecture & Progress Guide

**Last Updated:** Phase 1 (Person 1 Foundation Complete)  
**Target Repository:** `https://github.com/alexansh/VitalSense`  
**Platform:** Native Android (Kotlin + Jetpack Compose)

---

## 1. How the Entire Project Works

VitalSense is a **single-codebase, role-aware, offline-first Android application** built to bridge rural healthcare infrastructure (Admins, ASHA Workers, Doctors) and rural Patients.

### 🔄 End-to-End System Architecture

```
                               ┌─────────────────────────────────────────┐
                               │       TopRoleSwitcherBar (Header)       │
                               │  [👤 Patient | 🤝 ASHA | 🩺 Doc | 🛡️ Admin]│
                               └────────────────────┬────────────────────┘
                                                    │
                      ┌─────────────────────────────┼─────────────────────────────┐
                      │                             │                             │
                      ▼                             ▼                             ▼
           ┌──────────────────────┐      ┌──────────────────────┐      ┌──────────────────────┐
           │     Patient Home     │      │   ASHA Worker Home   │      │     Doctor Home      │
           │ • Health Card        │      │ • Caseload List      │      │ • Pending Cases      │
           │ • Category Grid      │      │ • "Act as Proxy"     │      │ • Prescriptions      │
           │ • Emergency SOS      │      │ • Broadcast Notices  │      │ • Dispensary Check   │
           └──────────┬───────────┘      └──────────┬───────────┘      └──────────┬───────────┘
                      │                             │                             │
                      └──────────────────────┐      │      ┌──────────────────────┘
                                             ▼      ▼      ▼
                               ┌─────────────────────────────────────────┐
                               │           VitalSenseNavGraph            │
                               └────────────────────┬────────────────────┘
                                                    │
                               ┌────────────────────▼────────────────────┐
                               │             AppStateHolder              │
                               │  • Active Role: StateFlow<UserRole>     │
                               │  • Active Proxy: StateFlow<Patient?>    │
                               │  • Connectivity: StateFlow<Boolean>     │
                               └────────────────────┬────────────────────┘
                                                    │
                               ┌────────────────────▼────────────────────┐
                               │          VitalSenseRepository           │
                               │   (Dual In-Memory & Room Persistence)   │
                               └────────────────────┬────────────────────┘
                                                    │
                               ┌────────────────────▼────────────────────┐
                               │         VitalSenseDatabase (Room)       │
                               │  • Villages • Patients • ASHA • Doctors │
                               │  • Prescriptions • Appointments • Stock │
                               └─────────────────────────────────────────┘
```

---

## 2. What Is Implemented Right Now (Person 1 Scope)

| Component | Status | Description & Location |
|---|:---:|---|
| **Build & Toolchain** | ✅ Complete | Modern Gradle build with Jetpack Compose (BOM 2024.02.01), Hilt DI (`2.51.1`), Room (`2.6.1`), ML Kit OCR, and Coil. |
| **Design System & Tokens** | ✅ Complete | Colors (`#E8EB7D` Lime, `#A3AEFE` Lavender, `#FFF8ED` Cream, `#FF6B6B` Coral, etc.), Poppins typography, 16dp card shapes, and pill buttons in [`core/ui/theme`](file:///c:/Users/saras/OneDrive/Documents/sehatSetu/app/src/main/java/com/vitalsense/app/core/ui/theme). |
| **Reusable UI Components** | ✅ Complete | [`TopRoleSwitcherBar`](file:///c:/Users/saras/OneDrive/Documents/sehatSetu/app/src/main/java/com/vitalsense/app/core/ui/components/TopRoleSwitcherBar.kt), [`VitalSenseButton`](file:///c:/Users/saras/OneDrive/Documents/sehatSetu/app/src/main/java/com/vitalsense/app/core/ui/components/VitalSenseButton.kt), [`VitalSenseCard`](file:///c:/Users/saras/OneDrive/Documents/sehatSetu/app/src/main/java/com/vitalsense/app/core/ui/components/VitalSenseCard.kt), [`SeverityBadge`](file:///c:/Users/saras/OneDrive/Documents/sehatSetu/app/src/main/java/com/vitalsense/app/core/ui/components/SeverityBadge.kt), [`CategoryChip`](file:///c:/Users/saras/OneDrive/Documents/sehatSetu/app/src/main/java/com/vitalsense/app/core/ui/components/CategoryChip.kt), and [`InlineHelpBanner`](file:///c:/Users/saras/OneDrive/Documents/sehatSetu/app/src/main/java/com/vitalsense/app/core/ui/components/InlineHelpBanner.kt). |
| **Local Database & DAOs** | ✅ Complete | Room SQLite Database with 10 Tables (`villages`, `patients`, `asha_workers`, `doctors`, `condition_records`, `prescriptions`, `appointments`, `broadcast_notices`, `dispensary_stock`, `government_schemes`). |
| **Pre-Seeded Rich Demo Data** | ✅ Complete | Pre-loaded in [`SeedDataProvider.kt`](file:///c:/Users/saras/OneDrive/Documents/sehatSetu/app/src/main/java/com/vitalsense/app/core/data/local/seed/SeedDataProvider.kt):<br>• 3 Villages (`Sundarpura`, `Kalyanpur`, `Bhimnagar`)<br>• 2 ASHA Workers (`Priya Devi`, `Sunita Sharma`)<br>• 2 Doctors (`Dr. Rajesh Varma`, `Dr. Ananya Sen`)<br>• 5 Patients (`Ramesh Kumar`, `Anita Sharma`, `Vikram Singh`, `Meena Patel`, `Suresh Yadav`)<br>• Dispensary stock, government schemes, and alerts. |
| **Global State & Role Router** | ✅ Complete | [`AppStateHolder.kt`](file:///c:/Users/saras/OneDrive/Documents/sehatSetu/app/src/main/java/com/vitalsense/app/core/state/AppStateHolder.kt) manages active role, online/offline state, and the ASHA Proxy context. |
| **Role Dashboard Shells** | ✅ Complete | Interactive scaffolds for Patient, ASHA, Doctor, and Admin ready for Persons 2–5 to build inside. |

---

## 3. Why It Was Implemented This Way (Design & Architecture Rationale)

1. **Decoupled Architecture for Future UI Redesigns:**
   * *Why:* Since the final UI design will evolve, all UI composables are stateless and consume `StateFlow` from repositories. Changing any UI layout, colors, or icons requires zero changes to database queries or business logic.
2. **Dual In-Memory + Room Database Strategy:**
   * *Why:* In a live 2-day prototype presentation, network latency or database IO race conditions can cause demo glitches. The dual-layer repository updates memory `MutableStateFlow` instantly for snappy UI updates while asynchronously persisting all transactions to SQLite via Room.
3. **TopRoleSwitcherBar with ASHA Proxy Banner:**
   * *Why:* During a presentation or jury evaluation, switching between Admin, ASHA, Doctor, and Patient on a physical phone must be 1-tap seamless. The proxy mechanism lets the evaluator immediately experience how an ASHA worker acts on behalf of a rural patient without logging in and out.
4. **Pre-Seeded Data with Realistic Rural Context:**
   * *Why:* Instead of empty blank screens, the app immediately showcases real-world clinical scenarios: high fever outbreak in Sundarpura, maternal trimester care, hypertension logs, and low-stock alerts on essential medicines.

---

## 4. Team Integration Guide (Persons 2, 3, 4, 5)

Each team member has an isolated module folder so work proceeds in parallel without git merge conflicts:

### 👤 Person 2 (Patient Journey & SOS)
* **Working Directory:** `app/src/main/java/com/vitalsense/app/feature/patient/`
* **Tasks:**
  * Build the full **Offline Health Card** viewer.
  * Build the **Symptom Logging** form calling `repository.logCondition(...)`.
  * Enhance the **Emergency SOS** trigger dialog.

### 👤 Person 3 (ASHA Worker & Caseload)
* **Working Directory:** `app/src/main/java/com/vitalsense/app/feature/asha/`
* **Tasks:**
  * Build the **New Patient Registration** form calling `repository.savePatient(...)`.
  * Expand the **Caseload Filter** (filter by high risk / village).
  * Build the **ASHA ↔ Patient Direct Messaging** screen.

### 👤 Person 4 (Doctor & Admin Outbreak Surveillance)
* **Working Directories:** `app/src/main/java/com/vitalsense/app/feature/doctor/` & `feature/admin/`
* **Tasks:**
  * Build the **Prescription Creator** form calling `repository.savePrescription(...)`.
  * Build the **Admin Village Outbreak Heat Map** visualization.
  * Implement the **Doctor Appointment Confirmation** workflow.

### 👤 Person 5 (AI OCR, Map & Schemes)
* **Working Directory:** `app/src/main/java/com/vitalsense/app/feature/prescriptions/` & `feature/patient/`
* **Tasks:**
  * Implement **ML Kit Text Recognition** for photographed physical prescriptions.
  * Build the **Mental Wellness** screen (mood check-in & guided breathing).
  * Build the **Nearby Healthcare Facilities Map** and **Government Schemes Browser**.

---

## 5. Verification
* Verified with clean build: `./gradlew assembleDebug` (`BUILD SUCCESSFUL`).
