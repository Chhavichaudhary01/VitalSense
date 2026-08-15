package com.vitalsense.app.feature.patient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun FullManualScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Help Manual", style = MaterialTheme.typography.headlineMedium)
        Text("1. Health Card: View your details offline.\n2. SOS: Send emergency alerts.\n3. OCR: Scan physical prescriptions.")
    }
}