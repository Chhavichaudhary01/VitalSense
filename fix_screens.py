import os

def write_file(path, content):
    with open(path, "w", encoding="utf-8") as f:
        f.write(content.strip())

patient_dir = "app/src/main/java/com/vitalsense/app/feature/patient"
asha_dir = "app/src/main/java/com/vitalsense/app/feature/asha"
doctor_dir = "app/src/main/java/com/vitalsense/app/feature/doctor"
admin_dir = "app/src/main/java/com/vitalsense/app/feature/admin"

write_file(f"{admin_dir}/ReviewAccountsScreen.kt", """
package com.vitalsense.app.feature.admin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Doctor
import com.vitalsense.app.core.data.model.AshaWorker
import com.vitalsense.app.core.ui.components.VitalSenseCard
@Composable
fun ReviewAccountsScreen(doctors: List<Doctor>, ashas: List<AshaWorker>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Review Accounts", style = MaterialTheme.typography.headlineMedium)
        LazyColumn {
            item { Text("Doctors") }
            items(doctors) { d ->
                VitalSenseCard { Text("${d.name} (${d.specialty.name})") }
            }
            item { Text("ASHAs") }
            items(ashas) { a ->
                VitalSenseCard { Text("${a.name} (${a.assignedVillages.joinToString()})") }
            }
        }
    }
}
""")

write_file(f"{asha_dir}/BroadcastNoticesScreen.kt", """
package com.vitalsense.app.feature.asha
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.BroadcastNotice
import com.vitalsense.app.core.data.model.UserRole
import com.vitalsense.app.core.ui.components.VitalSenseButton
import java.util.UUID
@Composable
fun BroadcastNoticesScreen(ashaName: String, onSend: (BroadcastNotice) -> Unit) {
    var title by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Send Notice to Caseload", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = msg, onValueChange = { msg = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth())
        VitalSenseButton("Send Broadcast", onClick = {
            onSend(BroadcastNotice(id = UUID.randomUUID().toString(), senderRole = UserRole.ASHA, senderName = ashaName, targetRole = "PATIENT", targetVillage = "All", title = title, message = msg, timestamp = System.currentTimeMillis(), isUrgent = false))
        })
    }
}
""")

write_file(f"{asha_dir}/PatientRegistrationScreen.kt", """
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
""")

write_file(f"{doctor_dir}/DispensaryStockScreen.kt", """
package com.vitalsense.app.feature.doctor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.DispensaryItem
import com.vitalsense.app.core.ui.components.VitalSenseCard
@Composable
fun DispensaryStockScreen(stock: List<DispensaryItem>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Dispensary Stock", style = MaterialTheme.typography.headlineMedium)
        LazyColumn {
            items(stock) { item ->
                VitalSenseCard {
                    Column {
                        Text(item.medicineName, style = MaterialTheme.typography.titleMedium)
                        Text("Available: ${item.availableQuantity}")
                        if (item.isLowStock) Text("LOW STOCK ALERT", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
""")

write_file(f"{doctor_dir}/PrescriptionCreatorScreen.kt", """
package com.vitalsense.app.feature.doctor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Prescription
import com.vitalsense.app.core.data.model.Doctor
import com.vitalsense.app.core.data.model.ConditionRecord
import com.vitalsense.app.core.ui.components.VitalSenseButton
import java.util.UUID
@Composable
fun PrescriptionCreatorScreen(doctor: Doctor, condition: ConditionRecord, onSave: (Prescription) -> Unit) {
    var medName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Write Prescription for ${condition.patientName}", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = medName, onValueChange = { medName = it }, label = { Text("Medicine Name") })
        OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage") })
        VitalSenseButton("Issue Prescription", onClick = {
            onSave(Prescription(id = UUID.randomUUID().toString(), patientId = condition.patientId, patientName = condition.patientName, doctorId = doctor.id, doctorName = doctor.name, doctorSpecialty = doctor.specialty.name, timestamp = System.currentTimeMillis(), dateFormatted = "Today", medicines = emptyList(), instructions = "$medName - $dosage", isOcrExtracted = false))
        })
    }
}
""")

write_file(f"{patient_dir}/ConditionEntryScreen.kt", """
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
""")

write_file(f"{patient_dir}/DoctorMapListScreen.kt", """
package com.vitalsense.app.feature.patient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Doctor
import com.vitalsense.app.core.ui.components.VitalSenseCard
@Composable
fun DoctorMapListScreen(doctors: List<Doctor>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Nearest Doctors (List View)", style = MaterialTheme.typography.headlineMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(doctors) { doc ->
                VitalSenseCard {
                    Column {
                        Text(doc.name, style = MaterialTheme.typography.titleMedium)
                        Text(doc.specialty.name)
                        Text("Distance: 2.5 km (mocked)")
                    }
                }
            }
        }
    }
}
""")

write_file(f"{patient_dir}/SchemesBrowserScreen.kt", """
package com.vitalsense.app.feature.patient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.GovernmentScheme
import com.vitalsense.app.core.ui.components.VitalSenseCard
@Composable
fun SchemesBrowserScreen(schemes: List<GovernmentScheme>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Govt Schemes", style = MaterialTheme.typography.headlineMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(schemes) { scheme ->
                VitalSenseCard {
                    Column {
                        Text(scheme.title, style = MaterialTheme.typography.titleMedium)
                        Text(scheme.benefitsSummary)
                        Text("Category: ${scheme.category}")
                    }
                }
            }
        }
    }
}
""")

print("Fixes applied.")
