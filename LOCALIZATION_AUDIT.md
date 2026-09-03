# VitalSense — Comprehensive Multilingual Localization Audit & Architecture Report

## 1. Executive Summary & Compliance
VitalSense (\sehatSetu\) has undergone a complete, exhaustive app-wide localization audit and implementation pass ensuring **zero English string leakage** across the entire application runtime. When a user selects any supported language, all visible texts, labels, HUD badges, card headers, dialogs, notifications, and screen-reader accessibility descriptions dynamically update and persist.

### Supported Application Languages
1. **English (Default Baseline)** — \en2. **हिन्दी (Hindi)** — \hi3. **தமிழ் (Tamil)** — \	a4. **मराठी (Marathi)** — \mr
> **Note on Native Script Mandate:**
> The language selection dialog and header pills always display language names in their authentic native scripts (\English\, \हिन्दी\, \தமிழ்\, \मराठी\) regardless of which language is currently active.

---

## 2. Architecture & Localization Engine

### Two-Tier Integrated Localization Pattern
1. **Compose Dynamic Tier (\LocalAppStrings\):**
   - Centralized \AppStrings\ data class in \com.vitalsense.app.core.ui.theme.Localization.kt\.
   - Complete localized implementations: \EnglishStrings\, \HindiStrings\, \TamilStrings\, and \MarathiStrings\.
   - Provided across the Compose widget hierarchy via \CompositionLocalProvider(LocalAppStrings provides appStrings)\.
   - Reactively re-renders all active screens immediately when the language is switched.

2. **Android OS Resource Tier (\strings.xml\):**
   - Synchronized XML resource bundles in:
     - \pp/src/main/res/values/strings.xml\ (English baseline)
     - \pp/src/main/res/values-hi/strings.xml\ (Hindi)
     - \pp/src/main/res/values-ta/strings.xml\ (Tamil)
     - \pp/src/main/res/values-mr/strings.xml\ (Marathi)
   - Utilized by system components, background WorkManager jobs (\AppointmentReminderWorker\), and Android per-app language management (\AppCompatDelegate.setApplicationLocales\).

3. **Cold-Start Persistence:**
   - Saved in \SharedPreferences\ as \pref_app_language\.
   - Initialized at Application startup in \VitalSenseApplication\ and \MainActivity\.

---

## 3. Audited & Localized Components by Role

### A. Patient Experience
- **Patient Home Dashboard (\PatientHomeScreen.kt\):**
  - Live Queue & Appointments HUD card (\strings.liveQueueAndAppointments\, \strings.hud\, \strings.book\).
  - Hospital & Clinical Services Desk Hub (\strings.hospitalClinicalServices\).
  - Diagnostic Lab Reports card (\strings.labReports\).
  - OPD Live Queue Hub card (\strings.opdQueue\).
  - District Blood Bank Hub card (\strings.bloodBank\).
  - Tele-Consultation Specialist Card (\strings.consultationCardTitle\, \strings.consultationCardDesc\, \strings.routineBadge\).
- **Appointments Screen (\AppointmentsScreen.kt\):**
  - Screen Header (\strings.scheduledAppointments\).
  - Action buttons (\strings.bookACall\, \strings.liveQueueHud\).
  - Empty state view (\strings.noUpcomingAppointments\, \strings.bookConsultationSubtitle\).
  - Booking modal dialog (\strings.bookTeleConsultation\).
- **Prescription Upload & Digitization (\PrescriptionUploadDialog.kt\):**
  - Modal title (\strings.addPrescription\).
  - Segmented AI Scan & Manual Entry tabs (\strings.scanPrescription\, \strings.writeDownPrescription\).
- **Patient Health Halo (\StatusHaloCard.kt\):**
  - Triage health status verdicts, greetings, subtitles, and biometric summaries across all 4 languages.
- **Referral Status View (\ReferralStatusCard.kt\):**
  - Specialist referral tier badges, status chips, and hospital designations.

### B. Hospital & Clinical Hub Modules (OPD, Blood Bank, Lab, IPD, OT, BME)
- **OPD Queue & Token Slip Desk (\OpdQueueScreen.kt\):**
  - Header & Subtitle (\strings.opdLiveQueueAndTokens\, \strings.opdSubtitle\).
  - Navigation actions & content descriptions (\strings.exit\, \strings.bookOpdToken\).
  - History & Token status cards (\strings.yourActiveTokens\, \strings.noActiveTokens\).
  - Token generation dialog (\strings.bookHospitalOpdToken\, \strings.selectDepartment\, \strings.confirmBooking\, \strings.cancel\).
- **District Blood Bank Registry (\BloodBankScreen.kt\):**
  - Header & Subtitle (\strings.bloodBankRegistry\, \strings.bloodBankSubtitle\).
  - Hero Inventory Metrics (\strings.bloodUnitsAvailable\).
  - Stock cards & action buttons (\strings.callBloodBank\).
- **Diagnostic Lab Reports Hub (\LabReportsScreen.kt\):**
  - Header & Subtitle (\strings.diagnosticLabReports\).
  - Metrics & Normal range indicators (\strings.statTotal\, \strings.normalRange\).
  - Detail dialogs & PDF download (\strings.downloadReport\, \strings.cancel\).
- **In-Patient Ward & Bed Matrix (\IpdBedTrackerScreen.kt\):**
  - Header & HUD title (\strings.ipdBedTracker\, \strings.ipdSubtitle\, \strings.hospitalClinicalServices\).
  - Bed occupancy indicators (\strings.occupied\, \strings.totalBeds\, \strings.available\).
  - Patient admission modal (\strings.admitPatient\, \strings.cancel\).
- **Operation Theatre Scheduler (\OtSchedulerScreen.kt\):**
  - Header & HUD title (\strings.otScheduler\, \strings.otSubtitle\, \strings.roleDoctor\).
  - Booking action & modal dialog (\strings.bookOtSlot\, \strings.confirmBooking\, \strings.cancel\).
- **Bio-Medical Equipment Registry (\BioMedicalScreen.kt\):**
  - Header & HUD title (\strings.bioMedicalTracker\, \strings.bioMedicalSubtitle\, \strings.hospitalClinicalServices\).
  - Operational metrics & service logging modal (\strings.operational\, \strings.reportFault\, \strings.cancel\).

### C. Doctor Clinical Workstation
- **Doctor Home Dashboard (\DoctorHomeScreen.kt\):**
  - Live OPD & Walk-in Queue card (\strings.liveQueueTitle\, \strings.liveQueueDesc\).
  - Live Queue HUD launch action (\strings.openHud\).
  - Triage breakdown and analytics.
- **Doctor Live Queue Desk (\DoctorQueueScreen.kt\):**
  - Header title (\strings.liveQueueTitle\).
  - Navigation actions (\strings.exit\).
- **Doctor Specialist Referrals (\SpecialistReferralsScreen.kt\):**
  - Referrals queue header (\strings.specialistReferrals\).
  - Urgency tiers and referral status chips.
- **Tele-Consultation Modal & Call Room (\TeleConsultationModal.kt\):**
  - In-call Live Tele-Vitals HUD (\strings.liveTeleVitals\).
  - End call control action & description (\strings.endCall\).
  - Prescription during call action (\strings.prescribeDuringCall\).
- **Case Analytics & Medical History (\CaseAnalyticsCard.kt\, \CaseDetailScreen.kt\, \PatientHistoryDialog.kt\):**
  - Longitudinal health history (\strings.medicalHistoryTitle\, \strings.noMedicalHistory\).
  - Triage priority breakdowns (\strings.triageBreakdownTitle\).
- **Prescription Composer (\PrescriptionComposerDialog.kt\):**
  - Dialog title (\strings.newPrescription\).
  - Issue & dispensary sync button (\strings.savePrescriptionRecord\).

### D. ASHA Field Operations
- **ASHA Dashboard (\AshaHomeScreen.kt\):**
  - Caseload monitoring progress (\strings.caseload\).
  - Stat cards: Caseload (\strings.caseload\), High-Risk Triage (\strings.highRiskTriage\), Assigned Villages (\strings.assignedVillages\).
  - Unique ASHA ID badge & QR claim system.

### E. District Administration & Command
- **Admin Dashboard (\AdminHomeScreen.kt\):**
  - Header (\strings.districtCommand\).
  - Stock restock actions (\strings.requestRestock\).
  - Stat cards: Active Cases (\strings.totalActiveCases\), Monitored Villages (\strings.assignedVillages\), Outbreaks (\strings.outbreaks\).
  - Outbreak surveillance map section (\strings.diseaseHotspots\).
- **District Queue Oversight (\QueueOversightScreen.kt\):**
  - Header title (\strings.queueOversight\).
  - Navigation actions (\strings.exit\).

### F. Authentication & System Infrastructure
- **Role Scoped Login Portal (\LoginScreen.kt\):**
  - App name & Tagline (\strings.appName\, \strings.tagline\).
  - Role selection headers (\strings.chooseRole\, \strings.chooseRoleSubtitle\).
  - Role cards (\strings.rolePatient\, \strings.roleDoctor\, \strings.roleAsha\, \strings.roleAdmin\).
  - Portal designations (\strings.patientPortal\, \strings.doctorPortal\, \strings.ashaPortal\, \strings.adminPortal\).
  - Offline capability indicator (\strings.offline\).
- **Top Role & Status Navigation Island (\TopRoleSwitcherBar.kt\):**
  - Portal role subtitle across all 4 roles.
  - Quick language trigger button displaying active language in native script (\currentLanguage.nativeName\).
  - Online/Offline sync status (\strings.online\, \strings.offline\).
- **Language Switcher Modal (\ChangeLanguageDialog.kt\):**
  - Interactive selection of all 4 languages with native script rendering.
- **Background Notification Workers (\AppointmentReminderWorker.kt\):**
  - WorkManager scheduled notification title and body rendered from localized XML resources (\R.string.appointment_reminder_title\, \R.string.appointment_reminder_body\).

---

## 4. Compliance & Policy Verification
- [x] **Zero HMIS References:** Clean scan verified (0 occurrences of 'HMIS' across code, resources, and documentation).
- [x] **Native Script Preservation:** Languages rendered as \English\, \हिन्दी\, \தமிழ்\, \मराठी\.
- [x] **Deprecation Free:** \HorizontalDivider\ utilized in place of obsolete \Divider\.
- [x] **Unit Testing:** \LocalizationTest.kt\ passes with complete 4-language coverage verification.
- [x] **Build Verification:** Tested via \./gradlew testDebugUnitTest\ and \./gradlew assembleDebug assembleRelease\.
