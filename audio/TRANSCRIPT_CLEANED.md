# VitalSense (SehatSetu) — Polished & Context-Corrected Script

This document contains the verified, corrected text transcribed from your 10 voiceover recordings (`1.ogg` through `10.ogg`), resolving speech-to-text phonetic mishearings with the exact medical, technical, and team terms (e.g. *AarogyaX*, *VitalSense*, *ASHA*, *Room DB*, *GPS*, *queued sync*, *OPD queue*).

---

### Summary Table

| # | Audio File | Duration | Feature / Topic | Polished Narration Script |
|---|---|---|---|---|
| **01** | `1.ogg` | 15.63s | Problem Landscape | In rural India, a routine health checkup can mean a 30 to 100 kilometer trek. Over 70% of patients referred to specialists never reach completion. And the moment mobile signal drops, most healthcare apps simply stop working. |
| **02** | `2.ogg` | 20.25s | Team & Overview | We are Team AarogyaX and this is VitalSense: an offline-first healthcare platform built for Smart India Hackathon problem statement SIH26133, connecting patients, ASHA workers, doctors, and district health officials in one unified system that works with or without the internet. |
| **03** | `3.ogg` | 28.49s | Offline-First Engine | This is VitalSense working with zero internet. Every patient record, vital sign, and prescription is written instantly to the local database on the device—no waiting, no server dependency. When the connection drops completely, nothing is lost. The moment signal returns, VitalSense automatically syncs every queued record to the cloud in the background, with no manual action needed from the health worker. |
| **04** | `4.ogg` | 25.79s | ASHA Proxy & Triage | Many rural patients can't read, write, or navigate an app themselves. An ASHA worker can register symptoms and vitals directly on the patient's behalf. The moment vitals are entered, VitalSense's built-in triage engine checks them against clinical risk thresholds, automatically flagging high-risk maternal or emergency cases in real time so the most urgent patients are never missed. |
| **05** | `5.ogg` | 22.53s | Doctor OPD Queue | On the doctor's side, VitalSense shows a live OPD queue prioritized automatically by triage severity, not just who arrived first. During a video consultation, the doctor sees the patient's live vitals overlay directly on the screen—SpO2, heart rate, and blood pressure—without needing a second device or phone call to check numbers. |
| **06** | `6.ogg` | 27.93s | Specialist Referrals | When a case needs a specialist, the doctor raises a referral in seconds, choosing the department and the urgency level. But unlike a paper slip that often gets lost, VitalSense tracks the entire closed loop: the specialist receives it in their own queue, can accept it, request more history, or respond with findings—and those findings flow straight back into the patient's record. Nothing falls through the cracks. |
| **07** | `7.ogg` | 27.27s | AI Prescription OCR | Paper prescriptions get damaged, lost, or misread. VitalSense scans prescriptions directly with the phone camera, and on-device AI extracts the text—no internet, no cloud upload, so patient data never leaves the device. It even understands medical shorthand automatically, expanding abbreviations so a handwritten slip becomes a clean, structured digital record instantly. |
| **08** | `8.ogg` | 27.25s | Emergency SOS & SMS | In a real emergency, every second counts, and rural areas often have no data at all. One tap on VitalSense's SOS button sends the patient's GPS location to emergency contacts and dispatches a 108 ambulance request. If there's no internet, the app automatically falls back to direct cellular SMS carrying the exact GPS coordinates, so help is never blocked by a dead network. |
| **09** | `9.ogg` | 25.37s | Disease Surveillance | Zoom out to the district level, and VitalSense becomes an early warning system. As ASHA workers and doctors log symptoms across villages, the system aggregates this data into real-time outbreak heatmaps—spotting clusters of Dengue, Malaria, and diarrheal disease before they become district-wide emergencies, giving health officials days or weeks of early warning instead of reacting after the fact. |
| **10** | `10.ogg` | 31.25s | Impact Metrics & Outro | The result: OPD waiting times cut from 75 minutes to under 5; patients saving 30 to 70 kilometers and up to 500 rupees per visit; specialist referrals that used to take 7 to 14 days now closing in under 24 hours. This is VitalSense, built by Team AarogyaX for Smart India Hackathon problem statement SIH26133: offline-first, built for the last mile, built for Bharat. Thank you. |

---

## Key App & Video Alignments Identified

1. **Clip 1 / Audio 01 & 02:** Problem hook (travel barrier, broken referral loop, no signal) $\rightarrow$ Team AarogyaX intro.
2. **Audio 03 (Offline Sync Engine):** Highlight Room DB + WorkManager atomic outbox pattern.
3. **Audio 04 (ASHA Proxy & Triage):** Highlight ASHA role, proxy registration for non-literate patients, maternal high-risk flagging.
4. **Audio 05 (Doctor Live HUD & Video Consult):** Highlight OPD queue sorted by clinical severity + on-screen vitals HUD overlay.
5. **Audio 06 (Closed-Loop Referral):** Highlight Department selection, urgency level, specialist accept/review flow.
6. **Audio 07 (On-Device OCR):** Highlight ML Kit on-device scan, zero cloud upload, medical abbreviation expansion.
7. **Audio 08 (Emergency SOS):** Highlight 108 ambulance dispatch, GPS capture, offline SMS fallback.
8. **Audio 09 (District Surveillance Heatmap):** Highlight village symptom aggregation, Malaria/Dengue cluster alerts.
9. **Audio 10 (Quantified Impact Metrics):** 
   - OPD wait: `75 min → < 5 min`
   - Patient travel saved: `30–70 km` & `₹500 / visit`
   - Referral turnaround: `7–14 days → < 24 hours`
