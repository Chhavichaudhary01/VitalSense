import os

def write_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content.strip())

patient_dir = "app/src/main/java/com/vitalsense/app/feature/patient"

health_card_code = """
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
"""

condition_entry_code = """
package com.vitalsense.app.feature.patient

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.ConditionCategory
import com.vitalsense.app.core.data.model.ConditionRecord
import com.vitalsense.app.core.data.model.SeverityLevel
import com.vitalsense.app.core.ui.components.VitalSenseButton
import java.util.UUID

@Composable
fun ConditionEntryScreen(
    patientId: String,
    patientName: String,
    villageId: String,
    villageName: String,
    category: ConditionCategory,
    onLogCondition: (ConditionRecord) -> Unit
) {
    var severity by remember { mutableStateOf(SeverityLevel.LOW) }
    var notes by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Log ${category.displayName} Symptom", style = MaterialTheme.typography.headlineMedium)
        
        Text("Severity:")
        Row {
            SeverityLevel.values().forEach { level ->
                VitalSenseButton(
                    text = level.name,
                    onClick = { severity = level },
                    backgroundColor = if (severity == level) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        VitalSenseButton(
            text = "Submit to Doctor",
            onClick = {
                onLogCondition(
                    ConditionRecord(
                        id = UUID.randomUUID().toString(),
                        patientId = patientId,
                        patientName = patientName,
                        villageId = villageId,
                        villageName = villageName,
                        category = category,
                        severity = severity,
                        requestedDoctorType = "General",
                        notes = notes,
                        timestamp = System.currentTimeMillis()
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
"""

prescriptions_code = """
package com.vitalsense.app.feature.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Prescription
import com.vitalsense.app.core.ui.components.VitalSenseCard

@Composable
fun PrescriptionsListScreen(prescriptions: List<Prescription>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("My Prescriptions", style = MaterialTheme.typography.headlineMedium) }
        items(prescriptions) { rx ->
            VitalSenseCard {
                Column {
                    Text("Date: ${rx.dateFormatted}")
                    Text("Doctor: ${rx.doctorName} (${rx.doctorSpecialty})")
                    Text("Instructions: ${rx.instructions}")
                }
            }
        }
    }
}
"""

write_file(f"{patient_dir}/HealthCardViewerScreen.kt", health_card_code)
write_file(f"{patient_dir}/ConditionEntryScreen.kt", condition_entry_code)
write_file(f"{patient_dir}/PrescriptionsListScreen.kt", prescriptions_code)

print("Generated Patient screens")
