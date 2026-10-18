# 🛠️ VitalSense (SehatSetu) — Technology Stack Specification

**Problem Statement:** Smart India Hackathon (SIH) 26133  
**Platform:** Native Android (Min SDK: 26, Target SDK: 34/35)  
**Architecture:** Clean Architecture + MVVM + Offline-First Repository Pattern  

---

## 1. Core Platform & Build Infrastructure

| Layer / Tool | Technology / Specification | Purpose & Rationale |
| :--- | :--- | :--- |
| **Language** | **Kotlin 1.9.22** | 100% idiomatic Kotlin with Coroutines, Flow, Sealed Interfaces, and Pattern Matching. |
| **JDK Runtime** | **Java 21 LTS (OpenJDK / Eclipse Temurin)** | Modern JVM toolchain with virtual execution, pattern matching, and bytecode optimization. |
| **Build System** | **Gradle 8.7 + Android Gradle Plugin 8.7.0** | Incremental builds, dependency constraint caching, and resource shrink optimization. |
| **Annotation Processing** | **KAPT (Kotlin Annotation Processing Tool)** | Generates compile-time Room DAOs, Dagger-Hilt dependency injectors, and AndroidX metadata. |
| **Base Activity** | **`ComponentActivity` (Jetpack Compose)** | Pure declarative Compose entrypoint, preventing AppCompat decor overhead and lifecycle collisions. |

---

## 2. Presentation & User Interface (UI/UX)

```
┌──────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI                     │
├────────────────────────────┬─────────────────────────────┤
│   Design Systems           │   Multilingual Engine       │
│   • Glume Design (Cards)   │   • Dynamic Compose Tier    │
│   • NagarSeva Theme (Gov)  │   • Android Per-App Locale  │
│   • Sunlight Contrast Mode │   • Native Script Pickers   │
└────────────────────────────┴─────────────────────────────┘
```

| Component | Technology | Implementation Details |
| :--- | :--- | :--- |
| **UI Toolkit** | **Jetpack Compose (BOM 2024.02.00)** | Fully reactive, declarative UI replacing legacy XML views for faster runtime rendering. |
| **Design System** | **Material 3 (1.2.0) + Custom Design Tokens** | Custom Glume color palettes (`GlumePrimaryPurple`, `GlumeSuccessMint`, `GlumeAlertCoral`) with rounded pill shapes. |
| **Daylight Visibility** | **Sunlight High-Contrast Mode** | Dynamic palette toggle optimizing screen contrast for outdoor ASHA field visits under direct sunlight. |
| **Typography & A11y** | **Custom Scaled Typography + Touch Targets** | Strict adherence to minimum 48dp touch targets, high contrast ratios, and iconography cues for low-literacy users. |
| **Localization Engine** | **Two-Tier 4-Language System** | Supports **English**, **हिन्दी (Hindi)**, **தமிழ் (Tamil)**, and **मराठी (Marathi)**: <br>• *Tier 1 (Compose Dynamic):* `interface AppStrings` via `LocalAppStrings` CompositionLocalProvider.<br>• *Tier 2 (OS Resources):* `res/values-*/strings.xml` + `locales_config.xml` for system notifications and workers. |

---

## 3. Architecture, Dependency Injection & State Management

| Concern | Technology | Architectural Role |
| :--- | :--- | :--- |
| **Architectural Pattern** | **MVVM + Clean Architecture** | Strict separation into Presentation (Compose), Domain (Models & Use Cases), and Data (Local & Remote Repositories). |
| **Dependency Injection** | **Google Dagger Hilt 2.51** | Compile-time dependency injection across ViewModels, Repositories, Database DAOs, and Android Services. |
| **Asynchronous Streaming** | **Kotlin Coroutines & StateFlow** | Cold Flows for Room database reactive observations; hot StateFlows for UI state updates. |
| **Navigation** | **Single-Activity Compose Navigation** | Role-segmented navigation graph (`VitalSenseNavGraph`) switching between Patient, ASHA, Doctor, and Admin routes. |

---

## 4. Local Data & Offline-First Resilience

VitalSense treats the **local database as the primary source of truth** for all reads. Network availability is treated as an opportunistic synchronization layer.

| Component | Library / Pattern | Technical Specification |
| :--- | :--- | :--- |
| **Local Database** | **Android Room 2.6.1 (SQLite)** | High-performance relational storage for patients, vitals, appointments, prescriptions, IPD beds, OT surgeries, and equipment. |
| **Sync Strategy** | **Outbox Pattern + WorkManager 2.9.0** | Offline writes are recorded with `syncState = PENDING_SYNC` and batched to Cloud Firestore when network connectivity is re-established. |
| **Offline Health Card** | **Locally Cached ABHA Profile** | Complete demographic, QR code, and emergency contact data readable and renderable without internet access. |
| **Secure Key Store** | **EncryptedSharedPreferences** | Hardware-backed AES-256-GCM encryption for active authentication tokens, user PINs, and session credentials. |

---

## 5. Cloud Backend & Remote Services

| Capability | Cloud Service | Purpose & Usage |
| :--- | :--- | :--- |
| **Remote Database** | **Google Cloud Firestore** | NoSQL distributed database providing real-time multi-role updates across Doctors, Admins, and ASHAs. |
| **Push Alerts** | **Firebase Cloud Messaging (FCM)** | High-priority broadcasts for disease outbreak notices, restock reminders, and appointment call invitations. |
| **Blob Storage** | **Firebase Storage** | Encrypted bucket storage for prescription camera scans, digitized lab reports, and doctor reference documents. |
| **Map Services** | **Google Maps Platform SDK** | Geolocation tracking of rural PHC clinics, village boundaries, and district disease epidemic clusters. |

---

## 6. ABDM & Digital Health Ecosystem (SIH 26133)

| Component | Engine / Architecture | Capability |
| :--- | :--- | :--- |
| **ABHA ID Creation** | **`AbdmManager.kt`** | Simulates 14-digit ABHA ID (`14-XXXX-XXXX-XXXX`) and `@abdm` address creation via Aadhaar/biometric verification. |
| **Consent Management** | **`ConsentManager.kt`** | Electronic Consent Artifacts conforming to ABDM M2/M3: date-bounded, revocable by patient, with emergency break-glass overrides. |
| **FHIR Data Modeling** | **JSON Resource Schemas** | Standardized medical history, diagnostic encounters, and medication requests for longitudinal health records. |
| **Audit Logging** | **`AuditLogEntity` + Room DAO** | Tamper-evident local logging of every health record access, consent grant, and proxy action. |

---

## 7. AI, Computer Vision & Telemedicine Hardware Interop

| Capability | Framework / SDK | Implementation |
| :--- | :--- | :--- |
| **Prescription OCR** | **Google ML Kit Text Recognition** | On-device computer vision extracting medication names, dosages, and durations from physical handwritten paper slips. |
| **Rule Triage Engine** | **`TriageEngine.kt`** | Clinical scoring algorithms categorizing patient vital signs (SpO2, Heart Rate, BP) into Low, Moderate, High, or Critical urgency tiers. |
| **Tele-Consultation** | **WebRTC + Agora RTC SDK** | Adaptive low-bandwidth audio/video streaming with integrated live in-call Tele-Vitals HUD (`TeleConsultationModal`). |
| **Audio Voice Guidance** | **Android Text-To-Speech (TTS)** | Multilingual spoken audio health verdicts for non-literate patients in all 4 supported regional languages. |
| **Emergency Fallback** | **Android Telephony / SmsManager** | Cellular SMS dispatch with GPS latitude/longitude coordinates when cellular data/internet is completely dead. |\n