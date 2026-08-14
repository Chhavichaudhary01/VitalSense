package com.vitalsense.app.core.data.local.seed

import com.google.gson.Gson
import com.vitalsense.app.core.data.local.entity.*
import com.vitalsense.app.core.data.model.*

object SeedDataProvider {
    private val gson = Gson()

    val initialVillages = listOf(
        Village(
            id = "v_sundarpura",
            name = "Sundarpura",
            district = "Rampur",
            state = "Uttar Pradesh",
            population = 1450,
            latitude = 26.8467,
            longitude = 80.9462,
            activeCases = 14,
            highRiskCount = 3
        ),
        Village(
            id = "v_kalyanpur",
            name = "Kalyanpur",
            district = "Rampur",
            state = "Uttar Pradesh",
            population = 2100,
            latitude = 26.8821,
            longitude = 80.9812,
            activeCases = 22,
            highRiskCount = 6
        ),
        Village(
            id = "v_bhimnagar",
            name = "Bhimnagar",
            district = "Rampur",
            state = "Uttar Pradesh",
            population = 980,
            latitude = 26.8150,
            longitude = 80.9120,
            activeCases = 8,
            highRiskCount = 1
        )
    )

    val initialAshaWorkers = listOf(
        AshaWorker(
            id = "asha_priya",
            name = "Priya Devi",
            ashaUniqueId = "ASHA-7701",
            phone = "+91 98765 43210",
            assignedVillages = listOf("Sundarpura", "Bhimnagar"),
            activePatientCount = 18,
            alertCount = 3
        ),
        AshaWorker(
            id = "asha_sunita",
            name = "Sunita Sharma",
            ashaUniqueId = "ASHA-8842",
            phone = "+91 98765 12345",
            assignedVillages = listOf("Kalyanpur"),
            activePatientCount = 24,
            alertCount = 5
        )
    )

    val initialDoctors = listOf(
        Doctor(
            id = "doc_rajesh",
            name = "Dr. Rajesh Varma",
            specialty = DoctorSpecialty.GENERAL_PHYSICIAN,
            qualification = "MBBS, MD (Medicine)",
            hospitalName = "Rampur Civil Hospital",
            distanceKm = 4.2,
            phone = "+91 94150 11223",
            availableDays = "Mon - Sat (9:00 AM - 4:00 PM)"
        ),
        Doctor(
            id = "doc_ananya",
            name = "Dr. Ananya Sen",
            specialty = DoctorSpecialty.PSYCHOLOGIST,
            qualification = "Ph.D. Clinical Psychology",
            hospitalName = "District Community Wellness Center",
            distanceKm = 6.5,
            phone = "+91 94150 99887",
            availableDays = "Mon - Fri (10:00 AM - 3:00 PM)"
        )
    )

    val initialPatients = listOf(
        Patient(
            id = "pat_ramesh",
            name = "Ramesh Kumar",
            age = 42,
            gender = "Male",
            phone = "+91 98111 22334",
            villageId = "v_sundarpura",
            villageName = "Sundarpura",
            ashaWorkerId = "asha_priya",
            ashaWorkerName = "Priya Devi",
            currentRiskLevel = SeverityLevel.SEVERE,
            lastCondition = "Severe Chest Congestion & High Spiking Fever (103°F)",
            lastVisitDate = "2026-08-10",
            nextAppointmentDate = "2026-08-18 (10:30 AM)",
            emergencyContact = "+91 98111 99999 (Brother - Suresh)"
        ),
        Patient(
            id = "pat_anita",
            name = "Anita Sharma",
            age = 28,
            gender = "Female",
            phone = "+91 98222 33445",
            villageId = "v_sundarpura",
            villageName = "Sundarpura",
            ashaWorkerId = "asha_priya",
            ashaWorkerName = "Priya Devi",
            currentRiskLevel = SeverityLevel.MODERATE,
            lastCondition = "2nd Trimester Routine Prenatal Care & Mild Anemia",
            lastVisitDate = "2026-08-05",
            nextAppointmentDate = "2026-08-20 (11:00 AM)",
            emergencyContact = "+91 98222 88888 (Husband - Manoj)"
        ),
        Patient(
            id = "pat_vikram",
            name = "Vikram Singh",
            age = 65,
            gender = "Male",
            phone = "+91 98333 44556",
            villageId = "v_kalyanpur",
            villageName = "Kalyanpur",
            ashaWorkerId = "asha_sunita",
            ashaWorkerName = "Sunita Sharma",
            currentRiskLevel = SeverityLevel.HIGH,
            lastCondition = "Hypertension (160/95) & Chronic Dizziness with Fatigue",
            lastVisitDate = "2026-08-12",
            nextAppointmentDate = "2026-08-16 (02:00 PM)",
            emergencyContact = "+91 98333 77777 (Son - Rahul)"
        ),
        Patient(
            id = "pat_meena",
            name = "Meena Patel",
            age = 19,
            gender = "Female",
            phone = "+91 98444 55667",
            villageId = "v_bhimnagar",
            villageName = "Bhimnagar",
            ashaWorkerId = "asha_priya",
            ashaWorkerName = "Priya Devi",
            currentRiskLevel = SeverityLevel.LOW,
            lastCondition = "Dietary Guidance & Iron Supplements Check",
            lastVisitDate = "2026-07-28",
            nextAppointmentDate = null,
            emergencyContact = "+91 98444 66666 (Mother - Shakuntala)"
        ),
        Patient(
            id = "pat_suresh",
            name = "Suresh Yadav",
            age = 35,
            gender = "Male",
            phone = "+91 98555 66778",
            villageId = "v_kalyanpur",
            villageName = "Kalyanpur",
            ashaWorkerId = "asha_sunita",
            ashaWorkerName = "Sunita Sharma",
            currentRiskLevel = SeverityLevel.MODERATE,
            lastCondition = "Chronic Agricultural Stress & Severe Sleep Disruption",
            lastVisitDate = "2026-08-08",
            nextAppointmentDate = "2026-08-17 (03:30 PM)",
            emergencyContact = "+91 98555 55555 (Wife - Geeta)"
        )
    )

    val initialConditionRecords = listOf(
        ConditionRecord(
            id = "cond_1",
            patientId = "pat_ramesh",
            patientName = "Ramesh Kumar",
            villageId = "v_sundarpura",
            villageName = "Sundarpura",
            category = ConditionCategory.GENERAL_MEDICINE,
            severity = SeverityLevel.SEVERE,
            requestedDoctorType = DoctorSpecialty.GENERAL_PHYSICIAN,
            notes = "Patient experiencing severe coughing with yellowish phlegm, 103°F fever for 3 days, and shortness of breath.",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            ashaProxyLogged = false
        ),
        ConditionRecord(
            id = "cond_2",
            patientId = "pat_anita",
            patientName = "Anita Sharma",
            villageId = "v_sundarpura",
            villageName = "Sundarpura",
            category = ConditionCategory.MATERNAL_HEALTH,
            severity = SeverityLevel.MODERATE,
            requestedDoctorType = DoctorSpecialty.GYNECOLOGIST,
            notes = "Week 22 pregnancy checkup. Mild fatigue and leg cramps reported. Hb level 10.2.",
            timestamp = System.currentTimeMillis() - 86400000L * 4,
            ashaProxyLogged = true
        ),
        ConditionRecord(
            id = "cond_3",
            patientId = "pat_vikram",
            patientName = "Vikram Singh",
            villageId = "v_kalyanpur",
            villageName = "Kalyanpur",
            category = ConditionCategory.GENERAL_MEDICINE,
            severity = SeverityLevel.HIGH,
            requestedDoctorType = DoctorSpecialty.GENERAL_PHYSICIAN,
            notes = "Blood pressure spiked to 160/95. Persistent morning headache and blurry vision on standing.",
            timestamp = System.currentTimeMillis() - 86400000L * 1,
            ashaProxyLogged = true
        ),
        ConditionRecord(
            id = "cond_4",
            patientId = "pat_suresh",
            patientName = "Suresh Yadav",
            villageId = "v_kalyanpur",
            villageName = "Kalyanpur",
            category = ConditionCategory.MENTAL_HEALTH,
            severity = SeverityLevel.MODERATE,
            requestedDoctorType = DoctorSpecialty.PSYCHOLOGIST,
            notes = "Crop failure stress resulting in insomnia, anxiety attacks, and loss of appetite.",
            timestamp = System.currentTimeMillis() - 86400000L * 3,
            ashaProxyLogged = false
        )
    )

    val initialPrescriptions = listOf(
        Prescription(
            id = "rx_1",
            patientId = "pat_ramesh",
            patientName = "Ramesh Kumar",
            doctorId = "doc_rajesh",
            doctorName = "Dr. Rajesh Varma",
            doctorSpecialty = "General Physician",
            timestamp = System.currentTimeMillis() - 86400000L,
            dateFormatted = "13 Aug 2026",
            medicines = listOf(
                PrescribedMedicine("Amoxicillin 500mg", "1 capsule", "3 times daily after meals", "5 days", 15),
                PrescribedMedicine("Paracetamol 650mg", "1 tablet", "SOS (if fever > 100°F)", "3 days", 6),
                PrescribedMedicine("Ambroxol Cough Syrup", "10 ml", "Twice daily after food", "5 days", 1)
            ),
            instructions = "Drink lukewarm water, avoid heavy physical labor, and review at PHC if breathing difficulty worsens.",
            isOcrExtracted = false
        ),
        Prescription(
            id = "rx_2",
            patientId = "pat_anita",
            patientName = "Anita Sharma",
            doctorId = "doc_rajesh",
            doctorName = "Dr. Rajesh Varma",
            doctorSpecialty = "General Physician",
            timestamp = System.currentTimeMillis() - 86400000L * 5,
            dateFormatted = "09 Aug 2026",
            medicines = listOf(
                PrescribedMedicine("Iron Folic Acid (IFA) Tablets", "1 tablet", "Once daily after lunch", "30 days", 30),
                PrescribedMedicine("Calcium 500mg + Vit D3", "1 tablet", "Once daily after dinner", "30 days", 30)
            ),
            instructions = "Do not take Iron and Calcium tablets together. Maintain high green leafy vegetable diet.",
            isOcrExtracted = true
        )
    )

    val initialAppointments = listOf(
        Appointment(
            id = "apt_1",
            patientId = "pat_ramesh",
            patientName = "Ramesh Kumar",
            doctorId = "doc_rajesh",
            doctorName = "Dr. Rajesh Varma",
            doctorSpecialty = "General Physician",
            dateFormatted = "18 Aug 2026",
            timeSlot = "10:30 AM",
            status = "Confirmed",
            proposedBy = UserRole.DOCTOR
        ),
        Appointment(
            id = "apt_2",
            patientId = "pat_vikram",
            patientName = "Vikram Singh",
            doctorId = "doc_rajesh",
            doctorName = "Dr. Rajesh Varma",
            doctorSpecialty = "General Physician",
            dateFormatted = "16 Aug 2026",
            timeSlot = "02:00 PM",
            status = "Confirmed",
            proposedBy = UserRole.PATIENT
        ),
        Appointment(
            id = "apt_3",
            patientId = "pat_suresh",
            patientName = "Suresh Yadav",
            doctorId = "doc_ananya",
            doctorName = "Dr. Ananya Sen",
            doctorSpecialty = "Psychologist & Mental Health",
            dateFormatted = "17 Aug 2026",
            timeSlot = "03:30 PM",
            status = "Pending Confirmation",
            proposedBy = UserRole.PATIENT
        )
    )

    val initialDispensaryItems = listOf(
        DispensaryItem("disp_1", "Paracetamol 650mg", "Analgesic / Antipyretic", 450, "tablets", 100),
        DispensaryItem("disp_2", "Amoxicillin 500mg", "Antibiotic", 180, "capsules", 50),
        DispensaryItem("disp_3", "Oral Rehydration Salts (ORS)", "Hydration", 320, "packets", 80),
        DispensaryItem("disp_4", "Iron & Folic Acid (IFA)", "Maternal / Anemia", 500, "tablets", 150),
        DispensaryItem("disp_5", "Cetirizine 10mg", "Antihistamine", 220, "tablets", 60),
        DispensaryItem("disp_6", "Amlodipine 5mg", "Hypertension", 35, "tablets", 50), // Low stock
        DispensaryItem("disp_7", "Metformin 500mg", "Diabetes", 240, "tablets", 70),
        DispensaryItem("disp_8", "Ambroxol Syrup (100ml)", "Respiratory", 12, "bottles", 20) // Low stock
    )

    val initialNotices = listOf(
        BroadcastNotice(
            id = "not_1",
            senderRole = UserRole.ADMIN,
            senderName = "District Chief Medical Officer",
            targetRole = "ALL",
            targetVillage = "Sundarpura",
            title = "⚠️ Seasonal Viral & Fever Outbreak Advisory",
            message = "High incidence of respiratory fever detected in Sundarpura. All ASHA workers are requested to conduct door-to-door temperature monitoring and distribute ORS packets.",
            timestamp = System.currentTimeMillis() - 3600000L * 4,
            isUrgent = true
        ),
        BroadcastNotice(
            id = "not_2",
            senderRole = UserRole.ASHA,
            senderName = "Priya Devi (ASHA-7701)",
            targetRole = "PATIENT",
            targetVillage = "Sundarpura",
            title = "👶 Weekly Village Maternal Immunization Camp",
            message = "Immunization and nutrition checkup camp this Friday at Sundarpura Primary School from 9:00 AM to 1:00 PM. Please bring your Health Card.",
            timestamp = System.currentTimeMillis() - 3600000L * 18,
            isUrgent = false
        )
    )

    val initialSchemes = listOf(
        GovernmentScheme(
            id = "sch_1",
            title = "Ayushman Bharat — PM-JAY",
            category = "Universal Health Coverage",
            targetBeneficiary = "All Rural Families / BPL Card Holders",
            benefitsSummary = "Cashless health cover up to ₹5 Lakh per family per year for secondary and tertiary hospitalization care.",
            eligibility = "Identified via SECC 2011 database or verified ration card holder."
        ),
        GovernmentScheme(
            id = "sch_2",
            title = "Pradhan Mantri Matru Vandana Yojana (PMMVY)",
            category = "Maternal & Child Health",
            targetBeneficiary = "Pregnant Women & Lactating Mothers",
            benefitsSummary = "Direct cash incentive of ₹5,000 in three installments upon early pregnancy registration and institutional delivery.",
            eligibility = "First live birth, registered at Anganwadi/PHC center."
        ),
        GovernmentScheme(
            id = "sch_3",
            title = "Rashtriya Kishor Swasthya Karyakram (RKSK)",
            category = "Adolescent & Mental Health",
            targetBeneficiary = "Adolescents (Age 10–19)",
            benefitsSummary = "Free peer counseling, nutrition advice, IFA supplements, and mental wellness support at Adolescent Friendly Health Clinics (AFHC).",
            eligibility = "All rural adolescents residing in the district."
        ),
        GovernmentScheme(
            id = "sch_4",
            title = "National TB Elimination Programme (Nikshay Poshan)",
            category = "Communicable Diseases",
            targetBeneficiary = "Notified TB Patients",
            benefitsSummary = "Financial incentive of ₹500/month directly into bank account for nutritional support throughout treatment.",
            eligibility = "All active TB patients registered on the Nikshay portal."
        )
    )

    // Entity conversions for Room seeding
    fun getVillageEntities(): List<VillageEntity> = initialVillages.map {
        VillageEntity(it.id, it.name, it.district, it.state, it.population, it.latitude, it.longitude, it.activeCases, it.highRiskCount)
    }

    fun getAshaEntities(): List<AshaWorkerEntity> = initialAshaWorkers.map {
        AshaWorkerEntity(it.id, it.name, it.ashaUniqueId, it.phone, gson.toJson(it.assignedVillages), it.activePatientCount, it.alertCount)
    }

    fun getDoctorEntities(): List<DoctorEntity> = initialDoctors.map {
        DoctorEntity(it.id, it.name, it.specialty, it.qualification, it.hospitalName, it.distanceKm, it.phone, it.availableDays)
    }

    fun getPatientEntities(): List<PatientEntity> = initialPatients.map {
        PatientEntity(it.id, it.name, it.age, it.gender, it.phone, it.villageId, it.villageName, it.ashaWorkerId, it.ashaWorkerName, it.currentRiskLevel, it.lastCondition, it.lastVisitDate, it.nextAppointmentDate, it.emergencyContact, it.profilePhotoUrl)
    }

    fun getConditionEntities(): List<ConditionRecordEntity> = initialConditionRecords.map {
        ConditionRecordEntity(it.id, it.patientId, it.patientName, it.villageId, it.villageName, it.category, it.severity, it.requestedDoctorType, it.notes, it.timestamp, it.ashaProxyLogged, it.isPendingSync)
    }

    fun getPrescriptionEntities(): List<PrescriptionEntity> = initialPrescriptions.map {
        PrescriptionEntity(it.id, it.patientId, it.patientName, it.doctorId, it.doctorName, it.doctorSpecialty, it.timestamp, it.dateFormatted, gson.toJson(it.medicines), it.instructions, it.isOcrExtracted)
    }

    fun getAppointmentEntities(): List<AppointmentEntity> = initialAppointments.map {
        AppointmentEntity(it.id, it.patientId, it.patientName, it.doctorId, it.doctorName, it.doctorSpecialty, it.dateFormatted, it.timeSlot, it.status, it.proposedBy)
    }

    fun getDispensaryEntities(): List<DispensaryEntity> = initialDispensaryItems.map {
        DispensaryEntity(it.id, it.medicineName, it.category, it.availableQuantity, it.unit, it.reorderThreshold)
    }

    fun getNoticeEntities(): List<BroadcastNoticeEntity> = initialNotices.map {
        BroadcastNoticeEntity(it.id, it.senderRole, it.senderName, it.targetRole, it.targetVillage, it.title, it.message, it.timestamp, it.isUrgent)
    }

    fun getSchemeEntities(): List<GovernmentSchemeEntity> = initialSchemes.map {
        GovernmentSchemeEntity(it.id, it.title, it.category, it.targetBeneficiary, it.benefitsSummary, it.eligibility, it.applicationUrl)
    }
}
