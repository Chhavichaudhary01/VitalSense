package com.vitalsense.app.feature.prescriptions.ocr

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.vitalsense.app.core.data.model.PrescribedMedicine
import kotlinx.coroutines.tasks.await
import java.io.File

object PrescriptionOcrHelper {

    private val textRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    /**
     * Expanded rural clinic and dispensary medicine keywords for heuristic matching
     */
    private val commonMedicines = listOf(
        "Paracetamol", "PCM", "Crocin", "Dolo", "Calpol", "Amoxicillin", "Amox", "Augmentin",
        "Cetirizine", "Cpm", "Levocetirizine", "ORS", "Zinc", "Iron Folic Acid", "IFA",
        "Metformin", "Amlodipine", "Telmisartan", "Losartan", "Atorvastatin", "Albendazole",
        "Azithromycin", "Ibuprofen", "Combiflam", "Pantoprazole", "Pan-D", "Omeprazole", "Omee",
        "Ranitidine", "Domperidone", "Ondansetron", "Cough Syrup", "Vitamin C", "Calcium",
        "Multivitamin", "B-Complex", "Ciprofloxacin", "Ofloxacin", "Metronidazole"
    )

    /**
     * Performs on-device text recognition on a captured photo file
     */
    suspend fun recognizeTextFromFile(context: Context, file: File): String {
        return try {
            val uri = Uri.fromFile(file)
            val image = InputImage.fromFilePath(context, uri)
            val visionText = textRecognizer.process(image).await()
            visionText.text.trim()
        } catch (e: Exception) {
            "OCR Processing Error: ${e.localizedMessage ?: e.message}"
        }
    }

    /**
     * Performs on-device text recognition using ML Kit from an in-memory Bitmap
     */
    suspend fun recognizeTextFromBitmap(bitmap: Bitmap, rotationDegrees: Int = 0): String {
        return try {
            val image = InputImage.fromBitmap(bitmap, rotationDegrees)
            val visionText = textRecognizer.process(image).await()
            visionText.text.trim()
        } catch (e: Exception) {
            "OCR Processing Error: ${e.localizedMessage ?: e.message}"
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
                    trimmed.contains("1-0-0", ignoreCase = true) || trimmed.contains("OD", ignoreCase = true) || trimmed.contains("morning", ignoreCase = true) -> "Once daily (morning)"
                    else -> "Once daily"
                }

                val dosage = when {
                    trimmed.contains("650mg", ignoreCase = true) || trimmed.contains("650 mg", ignoreCase = true) -> "650 mg"
                    trimmed.contains("500mg", ignoreCase = true) || trimmed.contains("500 mg", ignoreCase = true) -> "500 mg"
                    trimmed.contains("250mg", ignoreCase = true) || trimmed.contains("250 mg", ignoreCase = true) -> "250 mg"
                    trimmed.contains("100mg", ignoreCase = true) || trimmed.contains("100 mg", ignoreCase = true) -> "100 mg"
                    trimmed.contains("10mg", ignoreCase = true) || trimmed.contains("10 mg", ignoreCase = true) -> "10 mg"
                    trimmed.contains("5mg", ignoreCase = true) || trimmed.contains("5 mg", ignoreCase = true) -> "5 mg"
                    trimmed.contains("10ml", ignoreCase = true) || trimmed.contains("10 ml", ignoreCase = true) -> "10 ml"
                    trimmed.contains("5ml", ignoreCase = true) || trimmed.contains("5 ml", ignoreCase = true) -> "5 ml"
                    else -> "Standard dose"
                }

                val duration = when {
                    trimmed.contains("3 days", ignoreCase = true) || trimmed.contains("3 d", ignoreCase = true) || trimmed.contains("3/7", ignoreCase = true) -> "3 Days"
                    trimmed.contains("5 days", ignoreCase = true) || trimmed.contains("5 d", ignoreCase = true) || trimmed.contains("5/7", ignoreCase = true) -> "5 Days"
                    trimmed.contains("7 days", ignoreCase = true) || trimmed.contains("7 d", ignoreCase = true) || trimmed.contains("7/7", ignoreCase = true) || trimmed.contains("1 week", ignoreCase = true) -> "7 Days"
                    trimmed.contains("10 days", ignoreCase = true) || trimmed.contains("10 d", ignoreCase = true) -> "10 Days"
                    trimmed.contains("14 days", ignoreCase = true) || trimmed.contains("2 weeks", ignoreCase = true) -> "14 Days"
                    trimmed.contains("30 days", ignoreCase = true) || trimmed.contains("1 month", ignoreCase = true) -> "30 Days"
                    else -> "5 Days"
                }

                medicines.add(
                    PrescribedMedicine(
                        name = matchedMed,
                        dosage = dosage,
                        frequency = frequency,
                        duration = duration,
                        quantity = 10
                    )
                )
            }
        }

        return medicines
    }
}
