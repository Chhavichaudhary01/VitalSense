package com.vitalsense.app.core.data.medicine

/**
 * Reference medicine definition with therapeutic class and substitute mappings.
 *
 * NOTE: Substitution pairs and clinical indications provided here are for
 * PROTOTYPE / DEMO PURPOSES ONLY and are not a substitute for actual clinical
 * or pharmacological validation. A real deployment requires this catalog to be
 * reviewed by a licensed physician or pharmacist.
 */
data class Medicine(
    val id: String,
    val name: String,
    val genericName: String,
    val therapeuticClass: String, // e.g. "Antipyretic & Analgesic", "Antibiotic - Broad Spectrum", "Antihypertensive"
    val substitutes: List<String>, // list of other Medicine ids considered clinically interchangeable for common cases
    val commonDosage: String = "",
    val commonUseDescription: String = ""
)

/**
 * Hardcoded mock availability rule matching against store name patterns.
 */
data class StoreAvailability(
    val storeNamePattern: String, // e.g. "Apollo", "Tata 1mg", "Jan Aushadhi", "PHC"
    val medicineId: String,
    val inStock: Boolean
)

/**
 * Result representing a real pharmacy store's availability status for a specific medicine.
 */
data class StoreAvailabilityResult(
    val placeId: String,
    val storeName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Double,
    val phoneNumber: String?,
    val medicineId: String,
    val medicineName: String,
    val inStock: Boolean,
    val isKnownChain: Boolean = false
)

/**
 * Geographic pharmacy store location data model.
 */
data class NearbyPharmacy(
    val placeId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String? = null
)

object MedicineCatalog {

    /**
     * Curated catalog of ~22 essential medicines across 8 common therapeutic classes.
     * Each medicine links to clinically relevant substitute options within the same therapeutic class.
     */
    val medicines: List<Medicine> = listOf(
        // 1. Antipyretic & Analgesic (Fever & Pain)
        Medicine(
            id = "med_paracetamol_650",
            name = "Paracetamol 650mg",
            genericName = "Paracetamol (Acetaminophen)",
            therapeuticClass = "Antipyretic & Analgesic",
            substitutes = listOf("med_ibuprofen_400", "med_paracetamol_500", "med_mefenamic_500"),
            commonDosage = "1 tablet SOS after food",
            commonUseDescription = "Reduces acute fever, headache, body ache, and mild-to-moderate pain"
        ),
        Medicine(
            id = "med_ibuprofen_400",
            name = "Ibuprofen 400mg",
            genericName = "Ibuprofen (NSAID)",
            therapeuticClass = "Antipyretic & Analgesic",
            substitutes = listOf("med_paracetamol_650", "med_paracetamol_500"),
            commonDosage = "1 tablet twice daily after meals",
            commonUseDescription = "Anti-inflammatory pain reliever for muscular pain, joint inflammation, and fever"
        ),
        Medicine(
            id = "med_paracetamol_500",
            name = "Paracetamol 500mg",
            genericName = "Paracetamol (Generic)",
            therapeuticClass = "Antipyretic & Analgesic",
            substitutes = listOf("med_paracetamol_650", "med_ibuprofen_400"),
            commonDosage = "1 tablet 3 times daily",
            commonUseDescription = "Standard antipyretic for mild fever and common cold aches"
        ),
        Medicine(
            id = "med_mefenamic_500",
            name = "Mefenamic Acid 500mg",
            genericName = "Mefenamic Acid",
            therapeuticClass = "Antipyretic & Analgesic",
            substitutes = listOf("med_paracetamol_650", "med_ibuprofen_400"),
            commonDosage = "1 tablet after meals",
            commonUseDescription = "Relieves severe spasmodic pain, dental pain, and acute inflammatory pain"
        ),

        // 2. Antibiotics (Broad-Spectrum & Respiratory)
        Medicine(
            id = "med_amoxicillin_500",
            name = "Amoxicillin 500mg",
            genericName = "Amoxicillin Trihydrate",
            therapeuticClass = "Antibiotic - Broad Spectrum",
            substitutes = listOf("med_azithromycin_500", "med_amox_clav_625", "med_cefixime_200"),
            commonDosage = "1 capsule three times daily for 5 days",
            commonUseDescription = "First-line antibiotic for ear, nose, throat, respiratory, and urinary tract infections"
        ),
        Medicine(
            id = "med_azithromycin_500",
            name = "Azithromycin 500mg",
            genericName = "Azithromycin (Macrolide)",
            therapeuticClass = "Antibiotic - Broad Spectrum",
            substitutes = listOf("med_amoxicillin_500", "med_cefixime_200"),
            commonDosage = "1 tablet once daily 1 hour before meals for 3-5 days",
            commonUseDescription = "Once-daily antibiotic for chest infections, tonsillitis, sinusitis, and bronchitis"
        ),
        Medicine(
            id = "med_amox_clav_625",
            name = "Amoxicillin + Clavulanate 625mg",
            genericName = "Co-Amoxiclav",
            therapeuticClass = "Antibiotic - Broad Spectrum",
            substitutes = listOf("med_amoxicillin_500", "med_cefixime_200"),
            commonDosage = "1 tablet twice daily with food for 5-7 days",
            commonUseDescription = "Beta-lactamase resistant antibiotic for resistant bacterial and dental infections"
        ),
        Medicine(
            id = "med_cefixime_200",
            name = "Cefixime 200mg",
            genericName = "Cefixime (3rd Gen Cephalosporin)",
            therapeuticClass = "Antibiotic - Broad Spectrum",
            substitutes = listOf("med_amoxicillin_500", "med_azithromycin_500"),
            commonDosage = "1 tablet twice daily after meals for 5 days",
            commonUseDescription = "Broad-spectrum coverage for typhoid, complicated respiratory and urinary infections"
        ),

        // 3. Antihypertensive
        Medicine(
            id = "med_amlodipine_5",
            name = "Amlodipine 5mg",
            genericName = "Amlodipine Besylate",
            therapeuticClass = "Antihypertensive",
            substitutes = listOf("med_losartan_50", "med_telmisartan_40"),
            commonDosage = "1 tablet once daily morning",
            commonUseDescription = "Calcium channel blocker for essential hypertension and chronic angina"
        ),
        Medicine(
            id = "med_losartan_50",
            name = "Losartan 50mg",
            genericName = "Losartan Potassium",
            therapeuticClass = "Antihypertensive",
            substitutes = listOf("med_amlodipine_5", "med_telmisartan_40"),
            commonDosage = "1 tablet once daily morning",
            commonUseDescription = "Angiotensin II receptor blocker (ARB) lowering blood pressure and protecting kidneys"
        ),
        Medicine(
            id = "med_telmisartan_40",
            name = "Telmisartan 40mg",
            genericName = "Telmisartan",
            therapeuticClass = "Antihypertensive",
            substitutes = listOf("med_amlodipine_5", "med_losartan_50"),
            commonDosage = "1 tablet once daily morning",
            commonUseDescription = "Long-acting ARB for 24-hour continuous arterial blood pressure control"
        ),

        // 4. Antidiabetic
        Medicine(
            id = "med_metformin_500",
            name = "Metformin 500mg",
            genericName = "Metformin Hydrochloride",
            therapeuticClass = "Antidiabetic",
            substitutes = listOf("med_glimepiride_2", "med_metformin_1000", "med_gliclazide_80"),
            commonDosage = "1 tablet twice daily with meals",
            commonUseDescription = "Primary biguanide improving insulin sensitivity for Type-2 Diabetes Mellitus"
        ),
        Medicine(
            id = "med_glimepiride_2",
            name = "Glimepiride 2mg",
            genericName = "Glimepiride (Sulfonylurea)",
            therapeuticClass = "Antidiabetic",
            substitutes = listOf("med_metformin_500", "med_gliclazide_80"),
            commonDosage = "1 tablet once daily before breakfast",
            commonUseDescription = "Stimulates pancreatic beta cells to release insulin"
        ),
        Medicine(
            id = "med_metformin_1000",
            name = "Metformin 1000mg ER",
            genericName = "Metformin Hydrochloride Extended-Release",
            therapeuticClass = "Antidiabetic",
            substitutes = listOf("med_metformin_500", "med_glimepiride_2"),
            commonDosage = "1 tablet once daily with evening dinner",
            commonUseDescription = "Extended release formulation for glycemic control with minimal gastrointestinal discomfort"
        ),
        Medicine(
            id = "med_gliclazide_80",
            name = "Gliclazide 80mg",
            genericName = "Gliclazide",
            therapeuticClass = "Antidiabetic",
            substitutes = listOf("med_glimepiride_2", "med_metformin_500"),
            commonDosage = "1 tablet before morning meal",
            commonUseDescription = "Second-generation sulfonylurea for glycemic management"
        ),

        // 5. Antihistamine & Antiallergic
        Medicine(
            id = "med_cetirizine_10",
            name = "Cetirizine 10mg",
            genericName = "Cetirizine Hydrochloride",
            therapeuticClass = "Antihistamine",
            substitutes = listOf("med_levocetirizine_5", "med_fexofenadine_120"),
            commonDosage = "1 tablet at bedtime",
            commonUseDescription = "Relieves sneezing, runny nose, allergic conjunctivitis, itching, and hives"
        ),
        Medicine(
            id = "med_levocetirizine_5",
            name = "Levocetirizine 5mg",
            genericName = "Levocetirizine Dihydrochloride",
            therapeuticClass = "Antihistamine",
            substitutes = listOf("med_cetirizine_10", "med_fexofenadine_120"),
            commonDosage = "1 tablet at bedtime",
            commonUseDescription = "Active enantiomer providing rapid anti-allergic relief with minimal sedation"
        ),
        Medicine(
            id = "med_fexofenadine_120",
            name = "Fexofenadine 120mg",
            genericName = "Fexofenadine Hydrochloride",
            therapeuticClass = "Antihistamine",
            substitutes = listOf("med_cetirizine_10", "med_levocetirizine_5"),
            commonDosage = "1 tablet once daily morning",
            commonUseDescription = "Non-sedating antihistamine for seasonal allergic rhinitis and skin allergies"
        ),

        // 6. Gastrointestinal (Proton Pump Inhibitors)
        Medicine(
            id = "med_pantoprazole_40",
            name = "Pantoprazole 40mg",
            genericName = "Pantoprazole Sodium",
            therapeuticClass = "Gastrointestinal - PPI",
            substitutes = listOf("med_omeprazole_20", "med_rabeprazole_20"),
            commonDosage = "1 tablet once daily morning empty stomach",
            commonUseDescription = "Suppresses gastric acid for acid reflux, gastritis, GERD, and gastric ulcers"
        ),
        Medicine(
            id = "med_omeprazole_20",
            name = "Omeprazole 20mg",
            genericName = "Omeprazole",
            therapeuticClass = "Gastrointestinal - PPI",
            substitutes = listOf("med_pantoprazole_40", "med_rabeprazole_20"),
            commonDosage = "1 capsule 30 mins before breakfast",
            commonUseDescription = "Inhibits gastric acid secretion to heal peptic ulcers and heartburn"
        ),
        Medicine(
            id = "med_rabeprazole_20",
            name = "Rabeprazole 20mg",
            genericName = "Rabeprazole Sodium",
            therapeuticClass = "Gastrointestinal - PPI",
            substitutes = listOf("med_pantoprazole_40", "med_omeprazole_20"),
            commonDosage = "1 tablet once daily before meal",
            commonUseDescription = "Rapid-onset acid suppression for hyperacidity and dyspepsia"
        ),

        // 7. Respiratory / Bronchodilator
        Medicine(
            id = "med_salbutamol_inhaler",
            name = "Salbutamol Inhaler 100mcg",
            genericName = "Salbutamol (Albuterol)",
            therapeuticClass = "Respiratory - Bronchodilator",
            substitutes = listOf("med_formoterol_inhaler"),
            commonDosage = "1-2 puffs SOS for shortness of breath or wheezing",
            commonUseDescription = "Fast-acting reliever inhaler for acute asthma and bronchospasm"
        ),
        Medicine(
            id = "med_formoterol_inhaler",
            name = "Formoterol Inhaler 12mcg",
            genericName = "Formoterol Fumarate",
            therapeuticClass = "Respiratory - Bronchodilator",
            substitutes = listOf("med_salbutamol_inhaler"),
            commonDosage = "1 puff twice daily",
            commonUseDescription = "Long-acting bronchodilator for maintenance control of chronic asthma/COPD"
        ),

        // 8. Oral Rehydration & Electrolyte
        Medicine(
            id = "med_ors_sachet",
            name = "Oral Rehydration Salts (ORS) Sachet 21.8g",
            genericName = "WHO Recommended Electrolyte Formula",
            therapeuticClass = "Electrolyte Replenisher",
            substitutes = listOf("med_electral_powder"),
            commonDosage = "Dissolve 1 sachet in 1 liter clean drinking water, drink frequently",
            commonUseDescription = "Prevents and treats severe dehydration from acute diarrhea, vomiting, and heat exhaustion"
        ),
        Medicine(
            id = "med_electral_powder",
            name = "Electral Powder 21.8g",
            genericName = "ORS Equivalent",
            therapeuticClass = "Electrolyte Replenisher",
            substitutes = listOf("med_ors_sachet"),
            commonDosage = "Mix in 1L potable water",
            commonUseDescription = "Replenishes essential electrolytes lost during gastroenteritis and dehydration"
        )
    )

    /**
     * Pattern-based chain availability rules for simulated pharmacy networks.
     */
    val chainAvailabilityRules: List<StoreAvailability> = listOf(
        // Apollo Pharmacy: strong stocking of branded acute & chronic lines
        StoreAvailability("Apollo", "med_paracetamol_650", true),
        StoreAvailability("Apollo", "med_amoxicillin_500", true),
        StoreAvailability("Apollo", "med_amlodipine_5", true),
        StoreAvailability("Apollo", "med_pantoprazole_40", true),
        StoreAvailability("Apollo", "med_cetirizine_10", true),
        StoreAvailability("Apollo", "med_salbutamol_inhaler", true),
        StoreAvailability("Apollo", "med_metformin_500", true),
        StoreAvailability("Apollo", "med_azithromycin_500", true),
        StoreAvailability("Apollo", "med_ors_sachet", true),
        StoreAvailability("Apollo", "med_losartan_50", false), // Simulated temporary stockout

        // Tata 1mg: comprehensive catalog, occasional antibiotic stockouts
        StoreAvailability("1mg", "med_ibuprofen_400", true),
        StoreAvailability("1mg", "med_azithromycin_500", true),
        StoreAvailability("1mg", "med_losartan_50", true),
        StoreAvailability("1mg", "med_omeprazole_20", true),
        StoreAvailability("1mg", "med_levocetirizine_5", true),
        StoreAvailability("1mg", "med_formoterol_inhaler", true),
        StoreAvailability("1mg", "med_metformin_500", true),
        StoreAvailability("1mg", "med_amoxicillin_500", false), // Simulated stockout
        StoreAvailability("1mg", "med_paracetamol_650", false), // Simulated stockout

        // Jan Aushadhi Kendra: specialized in affordable generic essentials
        StoreAvailability("Jan Aushadhi", "med_paracetamol_500", true),
        StoreAvailability("Jan Aushadhi", "med_amoxicillin_500", true),
        StoreAvailability("Jan Aushadhi", "med_metformin_500", true),
        StoreAvailability("Jan Aushadhi", "med_ors_sachet", true),
        StoreAvailability("Jan Aushadhi", "med_cetirizine_10", true),
        StoreAvailability("Jan Aushadhi", "med_amlodipine_5", true),
        StoreAvailability("Jan Aushadhi", "med_paracetamol_650", false), // Stocks generic 500mg instead
        StoreAvailability("Jan Aushadhi", "med_azithromycin_500", false),

        // Primary Health Centre (PHC) Dispensary: state-supplied essential kit
        StoreAvailability("PHC", "med_paracetamol_500", true),
        StoreAvailability("PHC", "med_amoxicillin_500", true),
        StoreAvailability("PHC", "med_ors_sachet", true),
        StoreAvailability("PHC", "med_metformin_500", true),
        StoreAvailability("PHC", "med_cetirizine_10", true),
        StoreAvailability("PHC", "med_paracetamol_650", false),
        StoreAvailability("PHC", "med_azithromycin_500", false)
    )
}
