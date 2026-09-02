package com.vitalsense.app.feature.referrals

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.ExternalReferral
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.ui.components.VitalSenseButton
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.components.VitalSenseTextField
import com.vitalsense.app.core.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExternalReferralScreen(
    referrals: List<ExternalReferral>,
    patients: List<Patient> = emptyList(),
    onBackClick: () -> Unit,
    onIssueReferral: (ExternalReferral) -> Unit,
    modifier: Modifier = Modifier
) {
    var showIssueDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GlumeBackground)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.md, bottom = Spacing.xxl)
    ) {
        // 1. Header with Back Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBackClick,
                    shape = PillShape,
                    color = GlumeSurfaceCard,
                    border = BorderStroke(1.dp, GlumeBorder),
                    modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Text("←", color = GlumeTextPrimary, fontWeight = FontWeight.Bold)
                        Text("Hospital Desk", style = MaterialTheme.typography.labelMedium, color = GlumeTextPrimary)
                    }
                }

                Surface(
                    shape = PillShape,
                    color = GlumePrimaryPurpleContainer,
                    border = BorderStroke(1.dp, GlumePrimaryPurple.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "Hospital Network · External Referrals",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GlumePrimaryPurpleLight,
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 4.dp)
                    )
                }
            }
        }

        // 2. Hero HUD
        item {
            VitalSenseCard(
                backgroundColor = GlumeSurfaceCard,
                border = BorderStroke(1.dp, GlumeBorder)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🏛️ Super-Specialty External Referrals",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary
                            )
                            Text(
                                text = "Empanelled Apex Hospitals & Cashless Requisition Desk",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlumeTextSecondary
                            )
                        }

                        Button(
                            onClick = { showIssueDialog = true },
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GlumePrimaryPurple,
                                contentColor = GlumeTextPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = Spacing.sm, vertical = Spacing.xxs),
                            modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                        ) {
                            Text("+ Issue Voucher", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    HorizontalDivider(color = GlumeBorderSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Active Referral Passes", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text("${referrals.size} Active", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = GlumeTextPrimary)
                        }
                        Column {
                            Text("Tie-up Network", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text("AIIMS, Central Rly, KGMU", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GlumePrimaryPurpleLight)
                        }
                    }
                }
            }
        }

        // 3. Referral Passes List
        items(referrals, key = { it.id }) { ref ->
            VitalSenseCard(
                backgroundColor = GlumeSurfaceCard,
                border = BorderStroke(1.dp, GlumeBorder)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Surface(
                                shape = PillShape,
                                color = GlumePrimaryPurpleContainer
                            ) {
                                Text(
                                    text = ref.referralLetterId,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GlumePrimaryPurpleLight,
                                    modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "· Issued ${ref.issuedDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumeTextSecondary
                            )
                        }

                        if (ref.isCashlessApproved) {
                            Surface(
                                shape = PillShape,
                                color = GlumeSuccessContainer
                            ) {
                                Text(
                                    text = "✓ CASHLESS APPROVED",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    color = GlumeSuccessMint,
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "🏥 ${ref.empanelledHospitalName}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = GlumeTextPrimary
                    )

                    Text(
                        text = "Specialty: ${ref.specialtyRequired}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = GlumePrimaryPurpleLight
                    )

                    Text(
                        text = "Clinical Summary: ${ref.clinicalSummary}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlumeTextSecondary
                    )

                    HorizontalDivider(color = GlumeBorderSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Beneficiary Patient", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                            Text(ref.patientName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GlumeTextPrimary)
                        }

                        if (ref.ambulanceRequisitioned) {
                            Surface(
                                shape = PillShape,
                                color = GlumeAlertContainer
                            ) {
                                Text(
                                    text = "🚑 Ambulance Requisitioned",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = GlumeAlertCoral,
                                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Issue Referral Modal Dialog
    if (showIssueDialog) {
        var patientName by remember { mutableStateOf("") }
        var hospitalName by remember { mutableStateOf("Railway Central Hospital, New Delhi") }
        var specialty by remember { mutableStateOf("Cardiothoracic Surgery") }
        var summary by remember { mutableStateOf("") }
        var ambulanceNeeded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showIssueDialog = false },
            title = {
                Text(
                    text = "Issue Super-Specialty Referral Voucher",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    VitalSenseTextField(
                        value = patientName,
                        onValueChange = { patientName = it },
                        label = "Patient Full Name",
                        placeholder = "e.g. Ramesh Kumar"
                    )

                    VitalSenseTextField(
                        value = hospitalName,
                        onValueChange = { hospitalName = it },
                        label = "Empanelled Hospital / Medical College",
                        placeholder = "e.g. AIIMS New Delhi"
                    )

                    VitalSenseTextField(
                        value = specialty,
                        onValueChange = { specialty = it },
                        label = "Specialty / Department Required",
                        placeholder = "e.g. Neurosurgery, Oncology"
                    )

                    VitalSenseTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = "Clinical Justification & Case Summary",
                        placeholder = "Describe indications requiring tertiary care...",
                        singleLine = false,
                        maxLines = 3
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Requisition Emergency Transport / Ambulance",
                            style = MaterialTheme.typography.labelSmall,
                            color = GlumeTextPrimary
                        )
                        Switch(
                            checked = ambulanceNeeded,
                            onCheckedChange = { ambulanceNeeded = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GlumeTextPrimary,
                                checkedTrackColor = GlumePrimaryPurple
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                        val randNum = (1000..9999).random()
                        val newRef = ExternalReferral(
                            id = "ref_${System.currentTimeMillis()}",
                            referralLetterId = "REF-2026-$randNum",
                            patientId = "pat_ref",
                            patientName = patientName.ifBlank { "Beneficiary Inmate" },
                            referringDoctorName = "Dr. Rajesh Kumar",
                            empanelledHospitalName = hospitalName.ifBlank { "Railway Central Hospital" },
                            specialtyRequired = specialty.ifBlank { "Tertiary Super-Specialty" },
                            clinicalSummary = summary.ifBlank { "Tertiary evaluation and management." },
                            isCashlessApproved = true,
                            ambulanceRequisitioned = ambulanceNeeded,
                            issuedDate = currentDate,
                            status = "Active"
                        )
                        onIssueReferral(newRef)
                        showIssueDialog = false
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple),
                    enabled = patientName.isNotBlank()
                ) {
                    Text("Issue & Sign Voucher", style = MaterialTheme.typography.labelSmall)
                }
            },
            dismissButton = {
                TextButton(onClick = { showIssueDialog = false }) {
                    Text("Cancel", color = GlumeTextSecondary)
                }
            },
            containerColor = GlumeSurfaceCard,
            tonalElevation = 6.dp
        )
    }
}
