package com.vitalsense.app.feature.asha
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.data.model.SeverityLevel
import com.vitalsense.app.core.ui.components.VitalSenseButton
import java.util.UUID
@Composable
fun PatientRegistrationScreen(ashaId: String, ashaName: String, onSave: (Patient) -> Unit) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Register New Patient", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
        VitalSenseButton("Register", onClick = {
            onSave(Patient(id = UUID.randomUUID().toString(), name = name, age = age.toIntOrNull() ?: 30, gender = "Unknown", phone = "000", villageId = "v1", villageName = "Sundarpura", ashaWorkerId = ashaId, ashaWorkerName = ashaName, currentRiskLevel = SeverityLevel.LOW, lastCondition = "Healthy", lastVisitDate = "Today", nextAppointmentDate = null, emergencyContact = "112"))
        })
    }
}