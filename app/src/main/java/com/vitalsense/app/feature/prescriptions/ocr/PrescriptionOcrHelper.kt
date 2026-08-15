package com.vitalsense.app.feature.prescriptions.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.vitalsense.app.core.data.model.PrescribedMedicine
import kotlinx.coroutines.tasks.await

object PrescriptionOcrHelper {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Common rural dispensary medicine keywords for heuristic extraction matching
     */
    private val commonMedicines = listOf(
        "Paracetamol", "PCM", "Crocin", "Amoxicillin", "Amox", "Cetirizine", "Cpm",
        "ORS", "Zinc", "Iron Folic Acid", "IFA", "Metformin", "Amlodipine", "Albendazole",
        "Azithromycin", "Ibuprofen", "Pantoprazole", "Omeprazole", "Cough Syrup", "Vitamin C",
        "Calcium", "Multivitamin", "Ranitidine", "Domperidone"
    )

    /**
     * Performs on-device text recognition using ML Kit
     */
    suspend fun recognizeTextFromBitmap(bitmap: Bitmap): String {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val visionText = textRecognizer.process(image).await()
            visionText.text
        } catch (e: Exception) {
            "OCR Processing Error: ${e.message}"
        }
    }

    /**
     * Parses raw OCR text into structured PrescribedMedicine objects for human review
     */
    fun parseMedicinesFromText(rawText: String): List<PrescribedMedicine> {
        val medicines = mutableListOf<PrescribedMedicine>()
        val lines = rawText.split("\n")

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isBlank()) continue

            // Check if line matches known medicines
            val matchedMed = commonMedicines.firstOrNull { trimmed.contains(it, ignoreCase = true) }
            if (matchedMed != null) {
                // Extract dosage and frequency if present
                val frequency = when {
                    trimmed.contains("1-0-1", ignoreCase = true) || trimmed.contains("BD", ignoreCase = true) || trimmed.contains("twice", ignoreCase = true) -> "Twice daily (after meals)"
                    trimmed.contains("1-1-1", ignoreCase = true) || trimmed.contains("TDS", ignoreCase = true) || trimmed.contains("thrice", ignoreCase = true) -> "3 times daily"
                    trimmed.contains("0-0-1", ignoreCase = true) || trimmed.contains("HS", ignoreCase = true) || trimmed.contains("night", ignoreCase = true) -> "Once at bedtime"
                    else -> "Once daily"
                }

                val dosage = when {
                    trimmed.contains("650mg", ignoreCase = true) -> "650 mg"
                    trimmed.contains("500mg", ignoreCase = true) -> "500 mg"
                    trimmed.contains("250mg", ignoreCase = true) -> "250 mg"
                    trimmed.contains("10mg", ignoreCase = true) -> "10 mg"
                    trimmed.contains("5mg", ignoreCase = true) -> "5 mg"
                    else -> "Standard dose"
                }

                medicines.add(
                    PrescribedMedicine(
                        name = matchedMed,
                        dosage = dosage,
                        frequency = frequency,
                        duration = "5 Days",
                        quantity = 10
                    )
                )
            }
        }

        // Fallback default if no known medicine name matched the raw handwritten text
        if (medicines.isEmpty() && rawText.isNotBlank()) {
            medicines.add(
                PrescribedMedicine(
                    name = "Digitized Prescription Note",
                    dosage = "As directed",
                    frequency = "See instructions",
                    duration = "7 Days",
                    quantity = 1
                )
            )
        }

        return medicines
    }
}
