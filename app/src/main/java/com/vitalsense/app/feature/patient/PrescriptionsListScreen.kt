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