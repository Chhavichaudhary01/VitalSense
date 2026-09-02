package com.vitalsense.app.feature.doctor.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.QueueEntry
import com.vitalsense.app.core.data.model.QueueEntrySource
import com.vitalsense.app.core.data.model.QueueEntryStatus
import com.vitalsense.app.core.ui.components.TabularStatusChip
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.ui.util.touchSpring
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun QueueEntryListItem(
    entry: QueueEntry,
    modifier: Modifier = Modifier,
    isDoctorMode: Boolean = true,
    onPrioritize: (() -> Unit)? = null,
    onSkip: (() -> Unit)? = null,
    onMarkNoShow: (() -> Unit)? = null,
    onStartConsultation: (() -> Unit)? = null
) {
    val timeFormatted = rememberTime(entry.checkedInAt)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .touchSpring(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (entry.priorityFlag) NagarSevaElevatedLight else NagarSevaSurfaceLight
        ),
        border = BorderStroke(
            1.dp,
            if (entry.priorityFlag) GlumeAlertAmber.copy(alpha = 0.5f) else NagarSevaBorderLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: Token, Name, Source Badge, Priority Star
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Token Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (entry.provisionalToken) GlumeAlertAmberContainer else NagarSevaPrimary.copy(alpha = 0.12f),
                        border = BorderStroke(
                            1.dp,
                            if (entry.provisionalToken) GlumeAlertAmber else NagarSevaPrimary.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = if (entry.provisionalToken) "PENDING" else "#${entry.tokenNumber}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            ),
                            color = if (entry.provisionalToken) GlumeAlertAmber else NagarSevaPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = entry.patientName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (entry.priorityFlag) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Prioritized",
                                    tint = GlumeAlertAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "Checked in: $timeFormatted",
                            style = MaterialTheme.typography.labelSmall,
                            color = GlumeTextSecondary
                        )
                    }
                }

                // Status & Source Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (entry.source == QueueEntrySource.SCHEDULED) Color(0xFFEFF6FF) else Color(0xFFF3E8FF)
                    ) {
                        Text(
                            text = if (entry.source == QueueEntrySource.SCHEDULED) "Scheduled" else "Walk-in",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = if (entry.source == QueueEntrySource.SCHEDULED) Color(0xFF1D4ED8) else Color(0xFF7E22CE),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    val (chipBg, chipFg) = when (entry.status) {
                        QueueEntryStatus.WAITING -> Pair(NagarSevaStatusProgressBg, NagarSevaStatusProgress)
                        QueueEntryStatus.CALLED -> Pair(Color(0xFFEDE9FE), Color(0xFF7C5CFF))
                        QueueEntryStatus.IN_CONSULTATION -> Pair(NagarSevaStatusNormalBg, NagarSevaStatusNormal)
                        QueueEntryStatus.COMPLETED -> Pair(Color(0xFFF1F5F9), GlumeTextSecondary)
                        QueueEntryStatus.NO_SHOW -> Pair(NagarSevaStatusUrgentBg, NagarSevaStatusUrgent)
                        QueueEntryStatus.SKIPPED -> Pair(Color(0xFFFFEDD5), Color(0xFFC2410C))
                        QueueEntryStatus.CANCELLED -> Pair(Color(0xFFF1F5F9), GlumeTextSecondary)
                    }

                    TabularStatusChip(
                        statusText = entry.status.name,
                        containerColor = chipBg,
                        textColor = chipFg
                    )
                }
            }

            // Row 2: Doctor Action Controls (only in Doctor Mode and when WAITING or CALLED)
            if (isDoctorMode && (entry.status == QueueEntryStatus.WAITING || entry.status == QueueEntryStatus.CALLED)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (entry.status == QueueEntryStatus.CALLED && onStartConsultation != null) {
                        Button(
                            onClick = onStartConsultation,
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NagarSevaPrimary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("Start Consult", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    if (onPrioritize != null) {
                        OutlinedButton(
                            onClick = onPrioritize,
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (entry.priorityFlag) GlumeAlertAmber else NagarSevaBorderLight),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = if (entry.priorityFlag) "Deprioritize" else "Prioritize",
                                fontSize = 11.sp,
                                color = if (entry.priorityFlag) GlumeAlertAmber else GlumeTextPrimary
                            )
                        }
                    }

                    if (onSkip != null) {
                        OutlinedButton(
                            onClick = onSkip,
                            modifier = Modifier.weight(0.8f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, NagarSevaBorderLight),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text("Skip", fontSize = 11.sp, color = GlumeTextPrimary)
                        }
                    }

                    if (onMarkNoShow != null) {
                        OutlinedButton(
                            onClick = onMarkNoShow,
                            modifier = Modifier.weight(0.9f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, NagarSevaStatusUrgent.copy(alpha = 0.4f)),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text("No-Show", fontSize = 11.sp, color = NagarSevaStatusUrgent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberTime(timestamp: Long): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
}
