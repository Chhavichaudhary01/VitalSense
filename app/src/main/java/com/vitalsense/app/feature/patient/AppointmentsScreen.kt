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