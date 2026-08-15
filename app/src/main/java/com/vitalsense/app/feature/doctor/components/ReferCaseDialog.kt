package com.vitalsense.app.feature.doctor.components

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
            shape = CardShape,
            color = WarmCreamBackground,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = "Patient: $patientName (§4.3 Escalation)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Text(text = "✕", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "Select Target Medical Specialty:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryNearBlack
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DoctorSpecialty.values().filter { it != currentSpecialty }.forEach { specialty ->
                        val isSelected = selectedSpecialty == specialty
                        Surface(
                            shape = CardShape,
                            color = if (isSelected) LavenderSecondary.copy(alpha = 0.5f) else SurfaceWhite,
                            onClick = { selectedSpecialty = specialty },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = specialty.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = TextPrimaryNearBlack
                                )
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedSpecialty = specialty }
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Clinical Referral Notes:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryNearBlack
                )

                OutlinedTextField(
                    value = referralNotes,
                    onValueChange = { referralNotes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = PillShape
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onRefer(selectedSpecialty, referralNotes)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = BlushPinkTertiary, contentColor = TextPrimaryNearBlack)
                    ) {
                        Text("Transfer Case →", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
