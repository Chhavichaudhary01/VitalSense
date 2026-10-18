# VitalSense (SehatSetu) Prototype Showcase — Storyboard

- **Project:** VitalSense (Android Native Compose Kotlin)
- **Team Name:** AarogyaX
- **SIH Problem Statement:** SIH26133 — *"Accessibility and quality of public healthcare services, particularly in rural and underserved areas"*
- **Target Duration:** ~5:10 mins (310 seconds) | **Hard Maximum:** 5:45 mins (345 seconds)
- **Voiceover Persona:** AI Male Voice (Young Adult, 20s, Indian English: `en-IN-PrabhatNeural`, unhurried -2% rate)
- **Video Format:** 1920x1080 Landscape, 30 FPS Constant Frame Rate, H.264, AAC Stereo

---

## Storyboard Overview & Scene Timing

| Scene | Scene Title | Visual Source & Timestamps | Estimated Duration | Voiceover Word Count | Callout Elements |
| :---: | :--- | :--- | :---: | :---: | :--- |
| **S01** | Title Card & Intro | Clean motion graphic card | **15s** (450 frames) | 26 words | Team AarogyaX, SIH26133, VitalSense Badge |
| **S02** | The Rural Healthcare Problem | Qualitative typographic canvas | **16s** (480 frames) | 27 words | Low Connectivity & Low Digital Literacy |
| **S03** | The Solution (4-in-1 Ecosystem) | 4-Role Architecture Diagram | **18s** (540 frames) | 28 words | Patient, ASHA, Doctor, Admin Cards |
| **S04** | Role Selection & Multilingual | Clip 1 (0:03–0:12) & Clip 5 (0:04–0:14) | **22s** (660 frames) | 33 words | 4-Language Runtime Switch, Role Gate |
| **S05** | Patient Experience & IoT Vitals | Clip 2 (0:04–0:34) | **32s** (960 frames) | 42 words | Live Vitals HUD, Bluetooth Pulse Oximeter |
| **S06** | ASHA Proxy Care & Care Journey | Clip 2 (0:36–1:00) | **26s** (780 frames) | 36 words | Offline ABHA QR Card, Care Journey |
| **S07** | AI Prescription OCR & Pharmacy | Clip 2 (3:00–3:22) | **28s** (840 frames) | 38 words | ML Kit Scanner, Jan Aushadhi Stock |
| **S08** | Doctor Workflow & Referrals | Clip 3 (0:05–0:30, 1:46–2:15, 3:22–3:38) | **38s** (1140 frames) | 52 words | Severity Triage Queue, Refer to Specialist |
| **S09** | District Admin & Surveillance | Clip 4 (0:23–0:42, 2:30–3:00) | **32s** (960 frames) | 44 words | Outbreak Heat Map, Broadcast Directives |
| **S10** | Emergency SOS with SMS Fallback | Clip 2 (3:37–3:48) & Clip 3 (2:22–2:30) | **26s** (780 frames) | 36 words | 3-Second SOS Trigger, Cellular SMS Fallback |
| **S11** | Mental Wellness & Breathing | Clip 2 (1:37–1:46) | **24s** (720 frames) | 34 words | Icon-Driven Mood Check-in, Guided Breathing |
| **S12** | Offline-First Architecture | `vitalsense_tech_stack.svg` & Clip 5 (0:15–0:20) | **24s** (720 frames) | 34 words | Room SQLite Outbox, WorkManager Sync |
| **S13** | Outro & Download QR | Clean outro card + `vitalsense_download_qr.png` | **18s** (540 frames) | 26 words | GitHub Link, Verified APK Download QR |
| **TOTAL** | **Full Prototype Showcase** | **13 Scenes** | **319s (~5:19)** | **~436 words** | **Target: 10-15% under 5:45 (Safe)** |

---

## Scene-by-Scene Breakdown

### Scene 01: Title Card & SIH Problem Statement
- **Duration:** 15.0s (450 frames @ 30fps)
- **Visuals:** Elegant medical teal and slate background (#0F172A to #006A60 gradient). Animated VitalSense emblem. Clean typography displaying Team AarogyaX and SIH Problem Statement SIH26133.
- **Voiceover Line:**
  > *"This is VitalSense, an offline-first Android application that connects rural patients, ASHA workers, doctors, and health administrators in one unified ecosystem."*
- **Callouts & Overlays:**
  - `Team AarogyaX` (Top badge)
  - `SIH26133: Healthcare Accessibility & Quality in Rural Areas` (Subtitle banner)
  - Tagline: *"Connecting Rural Health Infrastructure with Low-Connectivity Communities"*

---

### Scene 02: The Problem Context
- **Duration:** 16.0s (480 frames @ 30fps)
- **Visuals:** Calm typographic narrative over soft, dark teal canvas. No fabricated statistics; strictly qualitative and grounded in rural realities.
- **Voiceover Line:**
  > *"In many rural areas, weak connectivity and low digital literacy make timely healthcare hard to reach. VitalSense is built to work exactly where the internet doesn't."*
- **Callouts & Overlays:**
  - Key focus card 1: `Intermittent & Zero Connectivity (2G / Dead-Zones)`
  - Key focus card 2: `Vernacular & Non-Literate Patient Access Barriers`

---

### Scene 03: The 4-in-1 Solution Architecture
- **Duration:** 18.0s (540 frames @ 30fps)
- **Visuals:** Clean, animated four-card role grid matching the app's real architecture:
  1. 🧑‍🌾 **Patient Portal** (Prescriptions, OPD tokens & consults)
  2. 👩‍⚕️ **ASHA Worker** (Door-to-door vitals, proxy care & offline sync)
  3. 👨‍⚕️ **Doctor Portal** (Triage queues, specialist referrals & bed roster)
  4. 🏛️ **District Admin** (Outbreak surveillance, OT scheduling & directives)
- **Voiceover Line:**
  > *"One app, four roles. Patients, ASHA workers, doctors, and health administrators each get their own permission-aware workflow, inside a single native Android codebase."*
- **Callouts & Overlays:**
  - Highlight badge: `Single Unified APK • Role-Gated Navigation`

---

### Scene 04: Login & Multilingual Accessibility
- **Duration:** 22.0s (660 frames @ 30fps)
- **Footage Source:**
  - `Screen_Recording_20260919_120510_VitalSense.mp4` (`00:03 – 00:10`)
  - `Screen_Recording_20260919_122813_VitalSense.mp4` (`00:03 – 00:15`)
- **Visuals:** Centered phone frame showing the role selection screen, followed by live runtime switching across Hindi, Tamil, and Marathi.
- **Voiceover Line:**
  > *"At launch, users select their role with a single tap. The interface instantly adapts to English, Hindi, Tamil, or Marathi, making navigation natural for vernacular communities."*
- **Callouts & Overlays:**
  - Arrow 1 -> points to Top-Right Language Picker: `Instant 4-Language Switch (English, हिंदी, தமிழ், मराठी)`
  - Arrow 2 -> points to Role Cards: `One-Tap Role Selection`
  - Arrow 3 -> points to Bottom-Left Pill: `Live Offline/Online Network Indicator`

---

### Scene 05: Patient Experience & IoT Vitals
- **Duration:** 32.0s (960 frames @ 30fps)
- **Footage Source:** `Screen_Recording_20260919_121836_VitalSense.mp4` (`00:04 – 00:36`)
- **Visuals:** Patient dashboard ("Namaste, Ramesh!"). Tap on "Take Reading" opens the Bluetooth Health Sensor dialog ("Put the clip on your finger"), simulated live reading records 78 BPM & 99% SpO2, and saves to the local record with a green confirmation toast.
- **Voiceover Line:**
  > *"For patients like Ramesh, the dashboard presents a simple, icon-driven interface. Vitals like pulse rate and blood oxygen can be captured seamlessly from connected health sensors and saved directly to the offline health record."*
- **Callouts & Overlays:**
  - Arrow 1 -> points to SpO2 & Heart Rate Cards (`00:08`): `Real-Time Vitals HUD (78 BPM • 99% SpO2)`
  - Arrow 2 -> points to Finger Sensor Dialog (`00:24`): `Bluetooth Pulse Oximeter Integration`
  - Privacy Overlay: Subtle blur option over demo emergency contact number `+91 98111 99000`.

---

### Scene 06: ASHA Proxy Care & Longitudinal Care Journey
- **Duration:** 26.0s (780 frames @ 30fps)
- **Footage Source:** `Screen_Recording_20260919_121836_VitalSense.mp4` (`00:36 – 01:02`)
- **Visuals:** Offline ABHA digital health card with permanent offline QR code and assigned ASHA worker Priya Devi. Transition into the "Care Journey" longitudinal timeline showing ASHA home visits, cardiologist consultations, lipid profile lab results, and initial PHC checkups.
- **Voiceover Line:**
  > *"Frontline ASHA workers act as digital proxies for non-literate citizens—conducting home visits, updating ABHA digital health cards, and tracking a unified longitudinal care journey across clinic visits and lab investigations."*
- **Callouts & Overlays:**
  - Arrow 1 -> points to ABHA QR Code: `Offline ABHA Health Card (Zero-Data Readable)`
  - Arrow 2 -> points to Care Journey Timeline: `Unified Longitudinal Medical Record`

---

### Scene 07: AI Prescription Digitization & Jan Aushadhi Locator
- **Duration:** 28.0s (840 frames @ 30fps)
- **Footage Source:** `Screen_Recording_20260919_121836_VitalSense.mp4` (`03:00 – 03:22`)
- **Visuals:** Camera document scanner frame ("Google Auto Crop & Clean Scanner"), followed by the "Find Medicine Nearby" pharmacy locator displaying real-time stock availability for Amoxicillin 500mg at nearby Jan Aushadhi Medical Stores.
- **Voiceover Line:**
  > *"A photo of a paper prescription is processed on-device with ML Kit OCR, auto-cropped, and digitized into structured dosages. Patients can then locate prescribed medicines at nearby Jan Aushadhi stores with real-time stock indicators."*
- **Callouts & Overlays:**
  - Arrow 1 -> points to Scanner Frame (`03:02`): `On-Device ML Kit Prescription OCR`
  - Arrow 2 -> points to Jan Aushadhi List (`03:16`): `Nearby Jan Aushadhi Stock Availability`

---

### Scene 08: Doctor Workflow & Closed-Loop Specialist Referrals
- **Duration:** 38.0s (1140 frames @ 30fps)
- **Footage Source:** `Screen_Recording_20260919_122252_VitalSense.mp4` (`00:05 – 00:28`, `01:48 – 02:14`, `03:22 – 03:40`)
- **Visuals:** Doctor dashboard (Dr. Rajesh Varma, Rampur Civil Hospital) displaying the triage severity breakdown. Active OPD consultation queue ("Now Serving Token #1"), entering clinical notes "ordered CBC test", IPD bed tracker, and the Patient Record History accordion featuring the `🩺 Refer to Specialist` button.
- **Voiceover Line:**
  > *"Doctors receive an organized OPD queue sorted by clinical severity. From their consultation desk, doctors review longitudinal history, issue digital prescriptions, manage IPD bed admissions, and directly refer high-risk cases to tertiary specialists."*
- **Callouts & Overlays:**
  - Arrow 1 -> points to Triage Severity Bars: `Automated Clinical Severity Triage`
  - Arrow 2 -> points to Serving Token #1: `Real-Time OPD Consultation Desk`
  - Arrow 3 -> points to Refer to Specialist Button in Patient History: `Direct Specialist Referral Dispatch`

---

### Scene 09: District Admin & Epidemic Disease Surveillance
- **Duration:** 32.0s (960 frames @ 30fps)
- **Footage Source:** `Screen_Recording_20260919_122712_VitalSense.mp4` (`00:23 – 00:42`, `02:30 – 03:05`)
- **Visuals:** District Health Command dashboard (District CMO Rampur). Interactive Disease Outbreak Heatmap with cluster markers (Sundarpur, Kalyanpur, Bhimnagar). Admin initiates a "Broadcast Health Directive", drafting and dispatching a "malaria widespread" advisory to all ASHA workers and residents in Bhimnagar.
- **Voiceover Line:**
  > *"District health officers monitor real-time disease outbreak heat maps and longitudinal symptom curves. When an outbreak like malaria is detected, administrators broadcast instant health directives to frontline workers across affected villages."*
- **Callouts & Overlays:**
  - Arrow 1 -> points to Heatmap Cluster: `Spatial Village Outbreak Heatmap`
  - Arrow 2 -> points to Broadcast Directive Form: `Instant District-Wide Advisory Push`

---

### Scene 10: Emergency SOS & Cellular SMS Fallback
- **Duration:** 26.0s (780 frames @ 30fps)
- **Footage Source:**
  - `Screen_Recording_20260919_121836_VitalSense.mp4` (`03:37 – 03:48`)
  - `Screen_Recording_20260919_122252_VitalSense.mp4` (`02:22 – 02:30`)
- **Visuals:** 3-second SOS visual countdown on patient device. Alert dispatches simultaneously to ASHA worker Priya Devi and 108 Ambulance, with 1-tap call and cellular SMS fallback. Doctor dashboard displays persistent red emergency banner with exact GPS coordinates (Lat 26.8467, Lng 80.9462).
- **Voiceover Line:**
  > *"In a critical emergency, one tap triggers the SOS system. It transmits GPS coordinates to nearby doctors and ASHA workers, and automatically falls back to cellular SMS when mobile data is unavailable."*
- **Callouts & Overlays:**
  - Arrow 1 -> points to SOS Countdown Card: `3-Second Emergency SOS Trigger`
  - Arrow 2 -> points to SMS Fallback Button: `Zero-Internet Cellular SMS Fallback`
  - Arrow 3 -> points to Doctor GPS Alert: `Instant GPS Coordinate Broadcast`

---

### Scene 11: Mental Wellness & Guided Box Breathing
- **Duration:** 24.0s (720 frames @ 30fps)
- **Footage Source:** `Screen_Recording_20260919_121836_VitalSense.mp4` (`01:37 – 01:46`)
- **Visuals:** "Mental Health Referral" screen ("Take a moment for yourself, Ramesh"). Non-stigmatized icon check-ins (mood emojis), followed by the interactive animated 4-4-4 guided breathing circle (Breathe in 4s, hold 4s, exhale 4s).
- **Voiceover Line:**
  > *"To address mental health in rural areas without stigma, VitalSense offers simple, icon-based emotional check-ins and guided box breathing exercises that guide users with visual cues."*
- **Callouts & Overlays:**
  - Arrow 1 -> points to Mood Emojis: `Non-Stigmatized Icon Check-In`
  - Arrow 2 -> points to Breathing Circle: `Guided 4-4-4 Box Breathing Session`

---

### Scene 12: Offline-First Engine (Room Outbox + WorkManager)
- **Duration:** 24.0s (720 frames @ 30fps)
- **Footage Source:**
  - `Screen_Recording_20260919_122813_VitalSense.mp4` (`00:15 – 00:20`, Airplane Mode toggle)
  - `vitalsense_tech_stack.svg` (Clean vector architecture diagram)
- **Visuals:** Android notification shade pulled down with Airplane mode activated, proving full offline functionality. Cut to clean vector diagram highlighting Room SQLite Outbox and Android WorkManager background sync engine.
- **Voiceover Line:**
  > *"Under the hood, an atomic Room database outbox and WorkManager background synchronization keep every action functional in zero-connectivity dead-zones, syncing seamlessly once network connectivity resumes."*
- **Callouts & Overlays:**
  - Architecture Callout 1: `Atomic SQLite Room Outbox (Zero-Latency Local Reads/Writes)`
  - Architecture Callout 2: `WorkManager Background Sync with Exponential Backoff`

---

### Scene 13: Outro & Scan to Download Prototype
- **Duration:** 18.0s (540 frames @ 30fps)
- **Visuals:** Final showcase card with VitalSense brand identity, Team AarogyaX, GitHub repository link, and `vitalsense_download_qr.png` with text "Scan to Download Live Prototype".
- **Voiceover Line:**
  > *"VitalSense: healthcare that keeps working, even when the network doesn't. Scan the QR code to test the live prototype on GitHub."*
- **Callouts & Overlays:**
  - `GitHub: github.com/alexansh/VitalSense`
  - `QR Code: Resolves to Live Release APK (https://github.com/alexansh/VitalSense/releases/download/version/app-debug.apk)`
  - `Team AarogyaX • Smart India Hackathon 2024`

---

## Privacy & Redaction Review (Rule 3)
1. **Identified PII Items in Footage:**
   - Patient Name: "Ramesh Kumar" (Sundarpur village) — Mock persona.
   - Doctor Name: "Dr. Rajesh Varma" (DOC-101) — Mock persona.
   - Phone Numbers visible:
     - `+91 98111 99000` (Patient Emergency Contact)
     - `+91 9876543001` (Oncology Specialist Contact)
     - `+91 98111 22334` (SOS Alert Contact)
   - Coordinates: `Lat 26.8467, Lng 80.9462` (Lucknow/Rampur sample coordinates).
2. **Mitigation:**
   - Remotion `Blur.tsx` component will provide configurable gaussian blur/redaction pills over phone numbers for total privacy compliance.
