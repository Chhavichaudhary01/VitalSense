package com.vitalsense.app.feature.patient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.ConditionCategory
import com.vitalsense.app.core.data.model.ConditionRecord
import com.vitalsense.app.core.data.model.SeverityLevel
import com.vitalsense.app.core.data.model.DoctorSpecialty
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
                    // Simplified color
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
                        requestedDoctorType = DoctorSpecialty.GENERAL_PHYSICIAN,
                        notes = notes,
                        timestamp = System.currentTimeMillis()
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}