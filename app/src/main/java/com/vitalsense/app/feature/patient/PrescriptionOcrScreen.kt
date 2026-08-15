package com.vitalsense.app.feature.patient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.ui.components.VitalSenseButton
@Composable
fun PrescriptionOcrScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Upload Prescription (OCR)", style = MaterialTheme.typography.headlineMedium)
        var ocrText by remember { mutableStateOf("") }
        VitalSenseButton("Simulate OCR Scanner", onClick = { ocrText = "Amoxicillin 500mg 1x daily\nParacetamol 250mg as needed" })
        OutlinedTextField(
            value = ocrText,
            onValueChange = { ocrText = it },
            label = { Text("Extracted Text") },
            modifier = Modifier.fillMaxWidth().height(150.dp)
        )
        VitalSenseButton("Confirm & Save", onClick = { /* Save logic */ })
    }
}