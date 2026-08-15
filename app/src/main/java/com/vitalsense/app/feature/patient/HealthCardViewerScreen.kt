package com.vitalsense.app.feature.patient

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.components.SeverityBadge
import com.vitalsense.app.core.ui.theme.*

@Composable
fun HealthCardViewerScreen(patient: Patient) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Offline Health Card", style = MaterialTheme.typography.headlineMedium)
        VitalSenseCard(backgroundColor = LimePrimary) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Name: ${patient.name}", style = MaterialTheme.typography.titleLarge)
                Text("Age: ${patient.age} | Gender: ${patient.gender}")
                Text("Blood Group: O+ (Mock)")
                Text("Allergies: None (Mock)")
                Text("Emergency Contact: ${patient.emergencyContact}")
                Text("QR: [ ${patient.id} ]", style = MaterialTheme.typography.labelSmall)
                Row {
                    Text("Current Risk: ")
                    SeverityBadge(severity = patient.currentRiskLevel)
                }
            }
        }
    }
}