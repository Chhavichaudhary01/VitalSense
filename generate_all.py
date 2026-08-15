import os

def write_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content.strip())

patient_dir = "app/src/main/java/com/vitalsense/app/feature/patient"
asha_dir = "app/src/main/java/com/vitalsense/app/feature/asha"
doctor_dir = "app/src/main/java/com/vitalsense/app/feature/doctor"
admin_dir = "app/src/main/java/com/vitalsense/app/feature/admin"

# PATIENT
write_file(f"{patient_dir}/AppointmentsScreen.kt", """
package com.vitalsense.app.feature.patient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Appointment
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.components.VitalSenseButton
@Composable
fun AppointmentsScreen(appointments: List<Appointment>, onRequestNew: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("My Appointments", style = MaterialTheme.typography.headlineMedium)
        VitalSenseButton("Propose New Appointment", onClick = onRequestNew)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(appointments) { appt ->
                VitalSenseCard {
                    Column {
                        Text("${appt.dateFormatted} at ${appt.timeSlot}")
                        Text("Doctor: ${appt.doctorName}")
                        Text("Status: ${appt.status}")
                    }
                }
            }
        }
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
                        Text(doc.specialty)
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
                        Text(scheme.name, style = MaterialTheme.typography.titleMedium)
                        Text(scheme.description)
                        Text("Category: ${scheme.targetCategory}")
                    }
                }
            }
        }
    }
}
""")

write_file(f"{patient_dir}/PrescriptionOcrScreen.kt", """
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
        VitalSenseButton("Simulate OCR Scanner", onClick = { ocrText = "Amoxicillin 500mg 1x daily\\nParacetamol 250mg as needed" })
        OutlinedTextField(
            value = ocrText,
            onValueChange = { ocrText = it },
            label = { Text("Extracted Text") },
            modifier = Modifier.fillMaxWidth().height(150.dp)
        )
        VitalSenseButton("Confirm & Save", onClick = { /* Save logic */ })
    }
}
""")

write_file(f"{patient_dir}/FullManualScreen.kt", """
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
        Text("1. Health Card: View your details offline.\\n2. SOS: Send emergency alerts.\\n3. OCR: Scan physical prescriptions.")
    }
}
""")

# ASHA
write_file(f"{asha_dir}/PatientRegistrationScreen.kt", """
package com.vitalsense.app.feature.asha
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Patient
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
            onSave(Patient(id = UUID.randomUUID().toString(), name = name, age = age.toIntOrNull() ?: 30, gender = "Unknown", phone = "000", villageId = "v1", villageName = "Sundarpura", ashaWorkerId = ashaId, ashaWorkerName = ashaName))
        })
    }
}
""")

write_file(f"{asha_dir}/AshaPatientChatScreen.kt", """
package com.vitalsense.app.feature.asha
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun AshaPatientChatScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Chat with Patient", style = MaterialTheme.typography.headlineMedium)
        Text("Messages persist locally (mocked thread)")
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
            onSend(BroadcastNotice(id = UUID.randomUUID().toString(), senderRole = "ASHA", senderName = ashaName, targetRole = "PATIENT", targetVillage = "All", title = title, message = msg, timestamp = System.currentTimeMillis(), isUrgent = false))
        })
    }
}
""")

# DOCTOR
write_file(f"{doctor_dir}/PendingCasesScreen.kt", """
package com.vitalsense.app.feature.doctor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.ConditionRecord
import com.vitalsense.app.core.ui.components.VitalSenseCard
@Composable
fun PendingCasesScreen(conditions: List<ConditionRecord>, onSelect: (ConditionRecord) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Pending Cases", style = MaterialTheme.typography.headlineMedium)
        LazyColumn {
            items(conditions) { cond ->
                VitalSenseCard(onClick = { onSelect(cond) }) {
                    Column {
                        Text("${cond.patientName} (${cond.villageName})")
                        Text("Category: ${cond.category.displayName} | Risk: ${cond.severity.name}")
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
            onSave(Prescription(id = UUID.randomUUID().toString(), patientId = condition.patientId, patientName = condition.patientName, doctorId = doctor.id, doctorName = doctor.name, doctorSpecialty = doctor.specialty, timestamp = System.currentTimeMillis(), dateFormatted = "Today", medicines = emptyList(), instructions = "$medName - $dosage", isOcrExtracted = false))
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
                        Text("Available: ${item.currentStock}")
                        if (item.isLowStockAlert) Text("LOW STOCK ALERT", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
""")

write_file(f"{doctor_dir}/AppointmentConfirmationScreen.kt", """
package com.vitalsense.app.feature.doctor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Appointment
import com.vitalsense.app.core.ui.components.VitalSenseCard
@Composable
fun AppointmentConfirmationScreen(appointments: List<Appointment>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Pending Appointments", style = MaterialTheme.typography.headlineMedium)
        LazyColumn {
            items(appointments) { appt ->
                VitalSenseCard {
                    Column {
                        Text("${appt.patientName} on ${appt.dateFormatted}")
                        Text("Status: ${appt.status}")
                        // Add buttons for Accept/Reject in a real impl
                    }
                }
            }
        }
    }
}
""")

# ADMIN
write_file(f"{admin_dir}/VillageListScreen.kt", """
package com.vitalsense.app.feature.admin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Village
import com.vitalsense.app.core.ui.components.VitalSenseCard
@Composable
fun VillageListScreen(villages: List<Village>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Villages", style = MaterialTheme.typography.headlineMedium)
        LazyColumn {
            items(villages) { v ->
                VitalSenseCard {
                    Text(v.name)
                    Text("Population: ${v.population}")
                }
            }
        }
    }
}
""")

write_file(f"{admin_dir}/VillageOutbreakGridScreen.kt", """
package com.vitalsense.app.feature.admin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Village
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.CoralAlert
import com.vitalsense.app.core.ui.theme.LimePrimary
@Composable
fun VillageOutbreakGridScreen(villages: List<Village>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Village Outbreak Heatmap", style = MaterialTheme.typography.headlineMedium)
        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(villages) { v ->
                val bg = if (v.highRiskCount > 0) CoralAlert else LimePrimary
                VitalSenseCard(backgroundColor = bg) {
                    Column {
                        Text(v.name, style = MaterialTheme.typography.titleMedium)
                        Text("Active Cases: ${v.activeCases}")
                        Text("High Risk: ${v.highRiskCount}")
                    }
                }
            }
        }
    }
}
""")

write_file(f"{admin_dir}/AdminBroadcastScreen.kt", """
package com.vitalsense.app.feature.admin
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.ui.components.VitalSenseButton
@Composable
fun AdminBroadcastScreen(onSend: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("System Broadcast", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = msg, onValueChange = { msg = it }, label = { Text("Message") })
        VitalSenseButton("Send Broadcast", onClick = { onSend(title, msg) })
    }
}
""")

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
                VitalSenseCard { Text("${d.name} (${d.specialty})") }
            }
            item { Text("ASHAs") }
            items(ashas) { a ->
                VitalSenseCard { Text("${a.name} (${a.villageName})") }
            }
        }
    }
}
""")

print("Generated ALL screens")
