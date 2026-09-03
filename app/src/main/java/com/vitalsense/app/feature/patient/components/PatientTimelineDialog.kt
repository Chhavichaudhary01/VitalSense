package com.vitalsense.app.feature.patient.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.ui.theme.*

data class TimelineEvent(
    val date: String,
    val title: String,
    val description: String,
    val eventType: EventType,
    val doctorName: String? = null
)

enum class EventType {
    VISIT, REFERRAL, MEDICATION, LAB_TEST
}

fun getMockTimelineEvents(): List<TimelineEvent> {
    return listOf(
        TimelineEvent("Today, 10:30 AM", "ASHA Home Visit", "Blood pressure monitored: 140/90. Patient reported mild dizziness.", EventType.VISIT),
        TimelineEvent("Aug 12, 2026", "Specialist Consultation", "Cardiologist prescribed Ramipril 5mg.", EventType.REFERRAL, "Dr. Sharma (Cardio)"),
        TimelineEvent("Aug 10, 2026", "Lab Test: Lipid Profile", "Cholesterol elevated (240 mg/dL).", EventType.LAB_TEST),
        TimelineEvent("Aug 05, 2026", "PHC Checkup", "Initial hypertension diagnosis.", EventType.VISIT, "Dr. Gupta")
    )
}

@Composable
fun PatientTimelineDialog(
    patient: Patient,
    onDismiss: () -> Unit
) {
    val events = getMockTimelineEvents()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = GlumeBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Care Journey",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "Longitudinal Health Record for ${patient.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(GlumeSurfaceElevated, CircleShape)
                    ) {
                        Text("✕", color = GlumeTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(events) { event ->
                        TimelineEventCard(event)
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineEventCard(event: TimelineEvent) {
    val icon = when (event.eventType) {
        EventType.VISIT -> "🩺"
        EventType.REFERRAL -> "🔄"
        EventType.MEDICATION -> "💊"
        EventType.LAB_TEST -> "🧪"
    }

    val iconColor = when (event.eventType) {
        EventType.VISIT -> GlumePrimaryPurpleLight
        EventType.REFERRAL -> GlumeWarningAmber
        EventType.MEDICATION -> GlumeSuccessMint
        EventType.LAB_TEST -> GlumeAlertCoral
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GlumeSurfaceCard),
        border = BorderStroke(1.dp, GlumeBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(icon, fontSize = 24.sp)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = GlumeTextPrimary
                    )
                    Text(
                        text = event.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = GlumeTextTertiary
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = GlumeTextSecondary
                )
                
                if (event.doctorName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Attending: ${event.doctorName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GlumePrimaryPurple
                    )
                }
            }
        }
    }
}
