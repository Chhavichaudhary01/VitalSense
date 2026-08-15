package com.vitalsense.app.feature.doctor.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.theme.*

@Composable
fun ReferCaseDialog(
    patientName: String,
    currentSpecialty: DoctorSpecialty,
    onDismiss: () -> Unit,
    onRefer: (targetSpecialty: DoctorSpecialty, referralNotes: String) -> Unit
) {
    var selectedSpecialty by remember {
        mutableStateOf(
            if (currentSpecialty == DoctorSpecialty.GENERAL_PHYSICIAN) DoctorSpecialty.PSYCHOLOGIST
            else DoctorSpecialty.GENERAL_PHYSICIAN
        )
    }
    var referralNotes by remember { mutableStateOf("Patient requires specialized clinical consultation. Case history and initial symptoms attached.") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = DialogShape,
            color = GlumeSurfaceCard,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, GlumeBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🔄 Refer Case to Specialist",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "Patient: $patientName (§4.3 Escalation)",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Text(text = "✕", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GlumeTextSecondary)
                    }
                }

                HorizontalDivider(color = GlumeBorder)

                Text(
                    text = "Select Target Medical Specialty:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    DoctorSpecialty.values().filter { it != currentSpecialty }.forEach { specialty ->
                        val isSelected = selectedSpecialty == specialty
                        Surface(
                            shape = CardShape,
                            color = if (isSelected) GlumePrimaryPurpleContainer else GlumeSurfaceElevated,
                            border = if (isSelected) BorderStroke(1.5.dp, GlumePrimaryPurple) else BorderStroke(1.dp, GlumeBorder),
                            onClick = { selectedSpecialty = specialty },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = specialty.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) GlumePrimaryPurpleLight else GlumeTextPrimary
                                )
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedSpecialty = specialty },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = GlumePrimaryPurple,
                                        unselectedColor = GlumeTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Clinical Referral Notes:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )

                OutlinedTextField(
                    value = referralNotes,
                    onValueChange = { referralNotes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
                    shape = InputShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = GlumeSurfaceElevated,
                        unfocusedContainerColor = GlumeSurfaceCard,
                        focusedBorderColor = GlumePrimaryPurple,
                        unfocusedBorderColor = GlumeBorder,
                        focusedTextColor = GlumeTextPrimary,
                        unfocusedTextColor = GlumeTextPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).defaultMinSize(minHeight = 44.dp),
                        shape = PillShape,
                        border = BorderStroke(1.dp, GlumeBorder)
                    ) {
                        Text("Cancel", color = GlumeTextSecondary)
                    }

                    Button(
                        onClick = {
                            onRefer(selectedSpecialty, referralNotes)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.3f).defaultMinSize(minHeight = 44.dp),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple, contentColor = GlumeTextPrimary)
                    ) {
                        Text("Transfer Case →", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}
