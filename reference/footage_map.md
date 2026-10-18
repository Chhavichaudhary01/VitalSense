# VitalSense Footage Map (SIH 26133 Prototype Showcase)

This map inventories all screen recordings provided in `FOOTAGE_DIR` (`C:\Users\saras\OneDrive\Documents\sehatSetu\video`), verified via `ffprobe` and frame-by-frame contact sheet inspection.

---

## Footage Inventory Summary

| Clip Filename | Duration | Resolution | Orientation | Frame Rate (FPS) | Audio | Primary Role / Feature Focus |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `Screen_Recording_20260919_120510_VitalSense.mp4` | 42.71s | 1080x1810 | Portrait | VFR (~12.0 avg) | AAC (Mic/System) | **Splash & Role Selection (4 Roles)** |
| `Screen_Recording_20260919_121836_VitalSense.mp4` | 228.96s (3m 49s) | 1080x1810 | Portrait | VFR (~14.0 avg) | AAC (Mic/System) | **Patient Experience & Emergency SOS** |
| `Screen_Recording_20260919_122252_VitalSense.mp4` | 225.09s (3m 45s) | 1080x1810 | Portrait | VFR (~14.5 avg) | AAC (Mic/System) | **Doctor Portal, OPD, Referrals & Beds** |
| `Screen_Recording_20260919_122712_VitalSense.mp4` | 219.78s (3m 40s) | 1080x1810 | Portrait | VFR (~15.7 avg) | AAC (Mic/System) | **District Admin, Outbreak Map & Directives** |
| `Screen_Recording_20260919_122813_VitalSense.mp4` | 37.61s | 1080x1810 | Portrait | VFR (~11.2 avg) | AAC (Mic/System) | **Multilingual Localization & Offline Toggle** |

**Total Raw Footage:** 754.15 seconds (~12 minutes 34 seconds).  
All clips are native **1080x1810 portrait** recordings from a real Android device.

---

## Detailed Clip Breakdown & Key Moments

### Clip 1: `Screen_Recording_20260919_120510_VitalSense.mp4`
- **Role/Module:** System Entry, Splash Screen & Unified Role Gate
- **Key Timestamps:**
  - `00:00 – 00:03`: Clean app launch, splash logo ("VitalSense — Bridging Rural Healthcare — Zero-Internet Ready").
  - `00:04 – 00:20`: Main Role Gate ("Who is using the app? Select your role to access your dedicated healthcare portal"). Shows all 4 role cards:
    1. **Patient** (Prescriptions, OPD tokens & consults)
    2. **Doctor** (Patient queues, e-prescriptions & review)
    3. **ASHA Worker** (Door-to-door vitals, offline sync & SOS)
    4. **Admin** (Surveillance, bed/OT roster & outreach)
  - `00:06 – 00:20`: Live network status indicator ("Offline" / "Online" status pill at bottom-left), "108 Ambulance" emergency speed dial at bottom-right, and language picker at top-right.
- **Privacy / PII Check:** Clean. No personal names or credentials shown.

---

### Clip 2: `Screen_Recording_20260919_121836_VitalSense.mp4`
- **Role/Module:** Patient Experience (Low-Literacy Friendly, Offline ABHA Card, Vitals, Care Journey, Mental Health, Referrals & Emergency SOS)
- **Key Timestamps:**
  - `00:00 – 00:03`: Patient login via Instant Demo Sign-in.
  - `00:04 – 00:18`: Patient Dashboard ("Namaste, Ramesh!") with live Vitals HUD (Heart rate 76 bpm, Oxygen SpO2 98%, Blood Pressure 120/80, Temp 98.4°F) and "Get Help Now (Urgent)" central CTA.
  - `00:19 – 00:35`: Bluetooth Health Sensor pairing dialog ("Put the clip on your finger" -> live pulse oximeter simulation recording 78 BPM, 99% SpO2 -> saved to health record).
  - `00:36 – 00:50`: Offline Digital Health Card (ABHA QR code readable offline, assigned ASHA Priya Devi, active clinical condition).
  - `00:50 – 00:55`: Longitudinal Care Journey timeline (ASHA home visits, specialist consultations, lipid profile lab test, PHC checkup).
  - `01:15 – 01:36`: Symptom logger with clinical triage severity selectors (Mild, Moderate, High, Severe Alert).
  - `01:37 – 01:46`: Mental Health check-in (mood emoji icons) and animated 4-4-4 guided breathing exercise ("Take a moment for yourself, Ramesh").
  - `01:49 – 01:59`: Tele-consultation booking (Video / Voice call options with Dr. Rajesh Varma).
  - `02:00 – 02:04`: Live visit queue token display ("Now Serving #1", next in line < 2 min).
  - `02:22 – 02:38`: OPD Live Queue & Token generation (generates token `OPD-A39` for General Medicine).
  - `02:40 – 02:45`: District Blood Bank Registry (live stock across blood groups with low stock flags).
  - `02:54 – 03:00`: Doctor-to-Doctor Specialist Referrals accordion (active referral to Cardiologist Dr. Rajesh Varma).
  - `03:00 – 03:06`: Prescription Scanner camera interface ("Google Auto Crop & Clean Scanner").
  - `03:13 – 03:22`: "Find Medicine Nearby" (pharmacy & Jan Aushadhi Kendra locator with real-time stock availability for Amoxicillin 500mg).
  - `03:32 – 03:36`: Government Health Schemes (PM-JAY, PMMVY, RKSK, Nikshay Poshan).
  - `03:37 – 03:48`: **Emergency SOS workflow** (3-second visual countdown, automated dispatch to ASHA Priya Devi and 108 Ambulance, with 1-tap call and direct cellular SMS fallback).
- **Privacy / PII Check:** Contains mock patient identity "Ramesh Kumar" (Sundarpur village), emergency contact `+91 98111 99000 (Brother - Suresh)`, and condition diagnosis "Severe Chest Congestion & High Spiking Fever (103°F)". Blur/redaction overlay component will be available.

---

### Clip 3: `Screen_Recording_20260919_122252_VitalSense.mp4`
- **Role/Module:** Doctor Portal (OPD Queue, Triage Desk, OT Scheduler, IPD Bed Management, Specialist Referrals, SOS Alerts & Patient History)
- **Key Timestamps:**
  - `00:00 – 00:04`: Doctor consultation desk login (DOC-101) for Dr. Rajesh Varma (General Physician, Rampur Civil Hospital).
  - `00:05 – 00:15`: Clinical Triage Desk with severity breakdown (Low Risk: 2, Moderate: 1, High: 0, Urgent: 1), pending cases, today's appointments.
  - `00:16 – 00:38`: Operation Theatre (OT) Scheduler (Surgical roster: Trauma & Ortho OT, Major OT-1, Emergency Cervical Cerclage; "Book OT Slot" form).
  - `00:40 – 01:10`: IPD Bed Occupancy Tracker (11 total beds across Male, Female, Trauma, and ICU wards; live admission of Ramesh Kumar to BED-02).
  - `01:18 – 01:40`: Super-Specialty External Referrals (tertiary referral vouchers to Railway Central Hospital and AIIMS New Delhi with Emergency Ambulance requisition).
  - `01:46 – 02:18`: Live Clinic Visit Queue (Serving Token #1, In Consultation with Ramesh Kumar, adding clinical notes "ordered CBC test", consultation completion).
  - `02:22 – 02:30`: Emergency Patients SOS Alerts (Live red alert cards with encoded GPS coordinates Lat 26.8467, Lng 80.9462, "Mark Emergency Over").
  - `02:44 – 03:04`: Specialist Triage Case Review & Clinical Advice response, and Clinical Actions (Start Tele-Consultation Call, Issue Prescription, Propose Appointment, Refer to Specialist, Order Lab Test, Scan External Rx OCR).
  - `03:15 – 03:24`: Dispensary Stock inventory tracking (Paracetamol, Amoxicillin, ORS, IFA tablets with "Admin Restocked" badges).
  - `03:22 – 03:45`: Patient Record History accordion (Ramesh Kumar, Anita Sharma, Vikram Singh; `Call`, `Clinical History`, and `Refer to Specialist` buttons; Medical History & Records modal).
- **Privacy / PII Check:** Mock patient names (Ramesh Kumar, Anita Sharma, Vikram Singh, Meena Patel, Suresh Yadav), mock GPS coordinates (Lat 26.8467, Lng 80.9462).

---

### Clip 4: `Screen_Recording_20260919_122712_VitalSense.mp4`
- **Role/Module:** District Admin Portal (Disease Surveillance, Outbreak Heatmap, Hospital Operations, Bio-Medical Equipment & Directives)
- **Key Timestamps:**
  - `00:00 – 00:04`: Admin login (`admin@vitalsense.gov.in`) and District Health Command dashboard (District CMO Rampur, Population: 4500).
  - `00:05 – 00:22`: Doctor Restock Reminders and Dispensary Restock form (restocking Amlodipine 5mg).
  - `00:23 – 00:42`: Disease Outbreak Heatmap (interactive village cluster map: Sundarpur, Kalyanpur, Bhimnagar with "Send Advisory to Sundarpur Residents").
  - `00:43 – 01:00`: Disease Trends & Outbreak Analytics graphs (longitudinal trends for Malaria, Dengue, Cholera, Typhoid; Record New Data).
  - `01:26 – 01:42`: Bio-Medical Equipment Tracker (Oxygen Generator, 12-lead ECG, Defibrillator, Ultrasound scanner, Autoclave, ICU Ventilator; Calibration and maintenance due tracking).
  - `01:46 – 01:52`: Live Clinic Queue Oversight across 3 active doctors (Dr. Rajesh Varma, Dr. Ananya Sen, Dr. Ayushman Dev Singh).
  - `02:00 – 02:05`: Facility Quality Metrics (Cleanliness, Staff Availability, Equipment Readiness for PHC-A, CHC-B, DH-C).
  - `02:10 – 02:22`: Hospital Departments (8 clinical departments and specialist doctor details).
  - `02:30 – 03:40`: Broadcast Health Directive (District CMO broadcasts real-time alert "malaria widespread — All ASHA workers are advised to perform precautionary measures" to Bhimnagar village).
- **Privacy / PII Check:** Mock admin credential `admin@vitalsense.gov.in`, sample doctor contact number `+91 9876543001`.

---

### Clip 5: `Screen_Recording_20260919_122813_VitalSense.mp4`
- **Role/Module:** Multilingual Localization (English, Hindi, Tamil, Marathi) & Offline Connectivity Resilience
- **Key Timestamps:**
  - `00:00 – 00:04`: Role selection screen in English.
  - `00:04 – 00:07`: Instant runtime switch to **हिन्दी (Hindi)** -> "ऐप का उपयोग कौन कर रहा है?".
  - `00:07 – 00:10`: Instant runtime switch to **தமிழ் (Tamil)** -> "பயன்படுத்துபவர் யார்?".
  - `00:10 – 00:14`: Instant runtime switch to **मराठी (Marathi)** -> "अ‍ॅप कोण वापरत आहे?".
  - `00:15 – 00:20`: Android quick settings shade pull-down toggling Airplane Mode / WiFi, demonstrating zero-internet offline readiness.
- **Privacy / PII Check:** Clean. No personal data shown.

---

## Verification Against Script & Missing Items Report

1. **ASHA Dedicated Screen Recording:**
   - The footage shows ASHA proxy care features extensively through the Patient Care Journey (ASHA home visits), assigned ASHA in the ABHA card, Emergency SOS alert to ASHA, and District Admin directives sent to ASHA workers.
   - However, there is no clip showing the direct login into the ASHA Worker portal (e.g. ASHA household survey list).
   - **Resolution:** In Scene 6 (ASHA Proxy Care), we will use the footage of the Patient Care Journey, assigned ASHA card, and SOS alert dispatch to ASHA, clearly highlighting how ASHA workers record symptoms and manage records on behalf of rural citizens.
2. **AI Prescription OCR Camera:**
   - Shown in Clip 2 (`03:00 - 03:06`, Google Auto Crop & Clean Scanner) and Doctor portal (`02:56`, "Scan External Rx (OCR)").
3. **QR Code Resolution:**
   - Verified via `cv2.QRCodeDetector`: `vitalsense_download_qr.png` resolves to `https://github.com/alexansh/VitalSense/releases/download/version/app-debug.apk`.
