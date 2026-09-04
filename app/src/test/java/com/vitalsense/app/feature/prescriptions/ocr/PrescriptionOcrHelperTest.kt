package com.vitalsense.app.feature.prescriptions.ocr

import org.junit.Assert.*
import org.junit.Test

class PrescriptionOcrHelperTest {

    @Test
    fun testParseMedicinesFromFeverPrescription() {
        val sampleText = """
            Rx:
            Tab Paracetamol 650mg 1-0-1 (BD) for 3 days
            Tab Cetirizine 10mg 0-0-1 (HS) x 5 days
            Syp Cough Syrup 10ml TDS 5/7
        """.trimIndent()

        val medicines = PrescriptionOcrHelper.parseMedicinesFromText(sampleText)

        assertEquals(3, medicines.size)

        // Paracetamol
        val pcm = medicines.find { it.name == "Paracetamol" }
        assertNotNull(pcm)
        assertEquals("650 mg", pcm?.dosage)
        assertEquals("Twice daily (after meals)", pcm?.frequency)
        assertEquals("3 Days", pcm?.duration)

        // Cetirizine
        val ctz = medicines.find { it.name == "Cetirizine" }
        assertNotNull(ctz)
        assertEquals("10 mg", ctz?.dosage)
        assertEquals("Once at bedtime", ctz?.frequency)
        assertEquals("5 Days", ctz?.duration)

        // Cough Syrup
        val syp = medicines.find { it.name == "Cough Syrup" }
        assertNotNull(syp)
        assertEquals("10 ml", syp?.dosage)
        assertEquals("3 times daily", syp?.frequency)
    }

    @Test
    fun testParseMedicinesFromMaternalPrescription() {
        val sampleText = """
            Rx:
            Tab Iron Folic Acid 100mg 1-0-0 OD 30 days
            Tab Calcium 500mg 0-0-1 HS 30 days
        """.trimIndent()

        val medicines = PrescriptionOcrHelper.parseMedicinesFromText(sampleText)

        assertEquals(2, medicines.size)
        val ifa = medicines.find { it.name == "Iron Folic Acid" }
        assertNotNull(ifa)
        assertEquals("100 mg", ifa?.dosage)
        assertEquals("Once daily (morning)", ifa?.frequency)
        assertEquals("30 Days", ifa?.duration)
    }

    @Test
    fun testParseMedicinesWithNoRecognizedMedicines() {
        val gibberish = "Notes: Rest well and hydrate with coconut water."
        val medicines = PrescriptionOcrHelper.parseMedicinesFromText(gibberish)
        assertTrue(medicines.isEmpty())
    }
}
