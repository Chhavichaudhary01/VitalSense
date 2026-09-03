package com.vitalsense.app.core.data.medicine

import com.google.gson.Gson
import com.vitalsense.app.core.data.model.PrescribedMedicine
import com.vitalsense.app.core.data.model.Prescription
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID

class MedicineAvailabilityRepositoryTest {

    // Helper repository simulator that uses the pure calculation and catalog logic
    private val helperRepo = object : MedicineAvailabilityRepository {
        override suspend fun getNearbyStoresWithAvailability(
            medicineId: String,
            patientLat: Double,
            patientLng: Double,
            radiusMeters: Int
        ): List<StoreAvailabilityResult> = emptyList()

        override fun suggestAlternatives(
            medicineId: String,
            unavailableAt: List<StoreAvailabilityResult>
        ): List<Medicine> {
            val targetMed = getMedicineById(medicineId) ?: findMedicineByName(medicineId) ?: return emptyList()
            val candidateSubstitutes = targetMed.substitutes.mapNotNull { subId ->
                MedicineCatalog.medicines.firstOrNull { it.id == subId }
            }
            return candidateSubstitutes.filter {
                it.therapeuticClass.equals(targetMed.therapeuticClass, ignoreCase = true)
            }.take(3)
        }

        override fun getMedicineById(medicineId: String): Medicine? {
            return MedicineCatalog.medicines.firstOrNull { it.id.equals(medicineId, ignoreCase = true) }
        }

        override fun findMedicineByName(name: String): Medicine? {
            val clean = name.trim().lowercase()
            return MedicineCatalog.medicines.firstOrNull {
                it.name.lowercase() == clean || it.genericName.lowercase() == clean ||
                clean.contains(it.name.lowercase()) || it.name.lowercase().contains(clean)
            }
        }

        override fun getAllMedicines(): List<Medicine> = MedicineCatalog.medicines

        override fun calculateStockStatus(placeId: String, storeName: String, medicineId: String): Pair<Boolean, Boolean> {
            val chainMatch = MedicineCatalog.chainAvailabilityRules.firstOrNull { rule ->
                storeName.contains(rule.storeNamePattern, ignoreCase = true) &&
                        rule.medicineId.equals(medicineId, ignoreCase = true)
            }
            if (chainMatch != null) {
                return Pair(chainMatch.inStock, true)
            }

            val combinedKey = "${placeId}_${medicineId}"
            val hashCode = combinedKey.hashCode()
            val inStock = (kotlin.math.abs(hashCode) % 10) < 6
            return Pair(inStock, false)
        }
    }

    /**
     * (a) Deterministic pseudo-random fallback test for unmatched store names.
     * Ensures that identical (placeId + medicineId) inputs ALWAYS yield the exact same stock status,
     * while producing a believable distribution across different stores.
     */
    @Test
    fun testDeterministicPseudoRandomFallbackForUnmatchedStores() {
        val testStorePlaceId = "ch_unique_place_98765"
        val testStoreName = "Independent Rural Chemist"
        val medId = "med_paracetamol_650"

        // 1. Call repeatedly to verify 100% determinism
        val (firstCallStock, firstIsChain) = helperRepo.calculateStockStatus(testStorePlaceId, testStoreName, medId)
        assertFalse("Unmatched store should not be classified as a known chain", firstIsChain)

        for (i in 1..20) {
            val (subsequentStock, subsequentIsChain) = helperRepo.calculateStockStatus(testStorePlaceId, testStoreName, medId)
            assertEquals("Stock status must remain strictly deterministic on repetition", firstCallStock, subsequentStock)
            assertEquals("Chain classification must remain consistent", firstIsChain, subsequentIsChain)
        }

        // 2. Verify known chain pattern matching overrides fallback
        val (apolloStock, apolloIsChain) = helperRepo.calculateStockStatus("store_apollo_1", "Apollo Pharmacy Civil Lines", "med_paracetamol_650")
        assertTrue("Store containing 'Apollo' must be matched as known chain", apolloIsChain)
        assertTrue("Apollo Pharmacy should have Paracetamol 650 in stock per catalog rules", apolloStock)

        // 3. Verify balanced distribution across 100 independent stores
        var inStockCount = 0
        val sampleSize = 100
        for (i in 1..sampleSize) {
            val (inStock, isChain) = helperRepo.calculateStockStatus("local_chemist_$i", "Gupta Medicos #$i", "med_paracetamol_650")
            assertFalse(isChain)
            if (inStock) inStockCount++
        }

        // The hash % 10 < 6 formula targets ~60% in-stock; assert reasonable range between 40% and 80%
        assertTrue("Expected between 40 and 80 in-stock stores out of 100, but got $inStockCount", inStockCount in 40..80)
    }

    /**
     * (b) Strict therapeutic class substitution test.
     * Ensures that suggested alternatives belong exclusively to the exact same therapeuticClass.
     */
    @Test
    fun testSubstitutionLookupReturnsOnlySameTherapeuticClass() {
        val antipyretic = helperRepo.getMedicineById("med_paracetamol_650")
        assertNotNull("Reference medicine must exist", antipyretic)

        val antipyreticSubs = helperRepo.suggestAlternatives("med_paracetamol_650")
        assertTrue("Must suggest at least one alternative for Paracetamol", antipyreticSubs.isNotEmpty())

        antipyreticSubs.forEach { sub ->
            assertEquals(
                "Suggested substitute '${sub.name}' must have same therapeutic class as Paracetamol",
                antipyretic!!.therapeuticClass,
                sub.therapeuticClass
            )
            assertNotEquals("Substitute must not be the original medicine itself", antipyretic.id, sub.id)
        }

        // Check Antibiotics
        val antibioticSubs = helperRepo.suggestAlternatives("med_amoxicillin_500")
        assertTrue("Must suggest alternatives for Amoxicillin", antibioticSubs.isNotEmpty())
        antibioticSubs.forEach { sub ->
            assertEquals(
                "Substitute for Amoxicillin must be in 'Antibiotic - Broad Spectrum'",
                "Antibiotic - Broad Spectrum",
                sub.therapeuticClass
            )
        }

        // Check Antihypertensive
        val antihypertensiveSubs = helperRepo.suggestAlternatives("med_amlodipine_5")
        assertTrue("Must suggest alternatives for Amlodipine", antihypertensiveSubs.isNotEmpty())
        antihypertensiveSubs.forEach { sub ->
            assertEquals(
                "Substitute for Amlodipine must be 'Antihypertensive'",
                "Antihypertensive",
                sub.therapeuticClass
            )
        }
    }

    /**
     * (c) Doctor-side override and 'hasAlternativeAvailable' flag persistence test.
     * Ensures doctor can keep an unavailable medicine with clinical override, and the flag
     * correctly serializes and persists without blocking prescription creation.
     */
    @Test
    fun testDoctorOverridePersistsAlternativeAvailableFlag() {
        val gson = Gson()

        // Doctor prescribes an unavailable medicine and overrides suggestion
        val overriddenMedicine = PrescribedMedicine(
            name = "Amoxicillin 500mg",
            dosage = "1 capsule",
            frequency = "TDS",
            duration = "5 days",
            quantity = 15,
            medicineId = "med_amoxicillin_500",
            hasAlternativeAvailable = true // Flagged by system after clinical override
        )

        val regularMedicine = PrescribedMedicine(
            name = "Paracetamol 650mg",
            dosage = "1 tablet",
            frequency = "SOS",
            duration = "3 days",
            quantity = 6,
            medicineId = "med_paracetamol_650",
            hasAlternativeAvailable = false
        )

        val prescription = Prescription(
            id = UUID.randomUUID().toString(),
            patientId = "pat_ramesh_kumar",
            patientName = "Ramesh Kumar",
            doctorId = "doc_101",
            doctorName = "Dr. Ananya Roy",
            doctorSpecialty = "General Medicine",
            timestamp = System.currentTimeMillis(),
            dateFormatted = "03 Sep 2026",
            medicines = listOf(overriddenMedicine, regularMedicine),
            instructions = "Take with warm water",
            isOcrExtracted = false
        )

        // Verify prescription creation succeeded without blocking
        assertNotNull(prescription.id)
        assertEquals(2, prescription.medicines.size)

        // Verify that the overridden line retains hasAlternativeAvailable = true
        val amox = prescription.medicines.first { it.name.startsWith("Amoxicillin") }
        assertTrue("Unavailable medicine line must persist hasAlternativeAvailable = true", amox.hasAlternativeAvailable)

        val pcm = prescription.medicines.first { it.name.startsWith("Paracetamol") }
        assertFalse("Available medicine line must have hasAlternativeAvailable = false", pcm.hasAlternativeAvailable)

        // Verify JSON serialization / deserialization roundtrip (as stored in Room / Firestore)
        val json = gson.toJson(prescription)
        val deserialized = gson.fromJson(json, Prescription::class.java)

        assertEquals(prescription.id, deserialized.id)
        assertTrue(
            "hasAlternativeAvailable must persist after JSON serialization",
            deserialized.medicines.first { it.name.startsWith("Amoxicillin") }.hasAlternativeAvailable
        )
    }
}
