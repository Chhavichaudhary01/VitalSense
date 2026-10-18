package com.vitalsense.app.feature.asha

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.R
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.data.model.SeverityLevel
import com.vitalsense.app.core.ui.components.VitalSenseButton
import com.vitalsense.app.core.ui.components.VitalSenseTextField
import com.vitalsense.app.core.ui.theme.*
import java.util.UUID

@Composable
fun PatientRegistrationScreen(
    ashaId: String,
    ashaName: String,
    onSave: (Patient) -> Unit,
    onBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var village by remember { mutableStateOf("Sundarpura") }
    var phone by remember { mutableStateOf("") }
    var initialCondition by remember { mutableStateOf("Routine Checkup / General Health") }

    // Real-Time Clinical Triage Vitals Inputs (Audio 4)
    var systolicBp by remember { mutableStateOf("") }
    var diastolicBp by remember { mutableStateOf("") }
    var spO2Text by remember { mutableStateOf("") }
    var isMaternalHighRisk by remember { mutableStateOf(false) }

    val sysBp = systolicBp.toIntOrNull() ?: 120
    val diaBp = diastolicBp.toIntOrNull() ?: 80
    val spo2 = spO2Text.toIntOrNull() ?: 98

    val isClinicalEmergency = (spO2Text.isNotBlank() && spo2 < 90) || sysBp > 165 || diaBp > 110
    val isMaternalAlert = isMaternalHighRisk || (gender == "Female" && sysBp >= 140 && diaBp >= 90)
    val autoTriageSeverity = when {
        isClinicalEmergency -> SeverityLevel.SEVERE
        isMaternalAlert || (spO2Text.isNotBlank() && spo2 < 94) || sysBp >= 140 -> SeverityLevel.HIGH
        sysBp in 130..139 || (spO2Text.isNotBlank() && spo2 in 94..95) -> SeverityLevel.MODERATE
        else -> SeverityLevel.LOW
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.md)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack) {
                Text("←", fontSize = 22.sp, color = VS_OnBackground)
            }
            Text(
                text = stringResource(R.string.registerNewPatientTitle),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = VS_OnBackground
            )
        }

        // Proxy Registration Banner (Audio 4)
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = VS_PrimaryContainer.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, VS_Primary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🤝", fontSize = 16.sp)
                Column {
                    Text(
                        text = "ASHA Proxy Mode: Registering on behalf of patient",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = VS_Primary
                        )
                    )
                    Text(
                        text = "ASHA: $ashaName · Real-time clinical threshold checking active",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = VS_OnSurfaceVariant
                    )
                }
            }
        }

        VitalSenseTextField(
            value = name,
            onValueChange = { name = it },
            label = "Patient Full Name",
            placeholder = "e.g. Meera Devi"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                VitalSenseTextField(
                    value = ageText,
                    onValueChange = { ageText = it.filter { ch -> ch.isDigit() } },
                    label = "Age (Years)",
                    placeholder = "29"
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                VitalSenseTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone",
                    placeholder = "98765 00000"
                )
            }
        }

        VitalSenseTextField(
            value = initialCondition,
            onValueChange = { initialCondition = it },
            label = "Symptoms / Health Reason",
            placeholder = "e.g. Prenatal ANC checkup, Fever"
        )

        // Vitals Check
        Text(
            text = "Clinical Vitals Check (Triage Input)",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = VS_OnSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                VitalSenseTextField(
                    value = systolicBp,
                    onValueChange = { systolicBp = it.filter { ch -> ch.isDigit() } },
                    label = "Sys BP",
                    placeholder = "120"
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                VitalSenseTextField(
                    value = diastolicBp,
                    onValueChange = { diastolicBp = it.filter { ch -> ch.isDigit() } },
                    label = "Dia BP",
                    placeholder = "80"
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                VitalSenseTextField(
                    value = spO2Text,
                    onValueChange = { spO2Text = it.filter { ch -> ch.isDigit() } },
                    label = "SpO2 %",
                    placeholder = "98"
                )
            }
        }

        // Maternal Health Toggle
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isMaternalHighRisk) VS_ErrorContainer.copy(alpha = 0.6f) else VS_SurfaceVariant,
            border = BorderStroke(1.dp, if (isMaternalHighRisk) VS_Error else VS_Outline),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isMaternalHighRisk = !isMaternalHighRisk }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🤰", fontSize = 16.sp)
                    Column {
                        Text(
                            text = "High-Risk Maternal / Antenatal Case",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isMaternalHighRisk) VS_OnErrorContainer else VS_OnBackground
                        )
                        Text(
                            text = "Auto-flags pregnant mother for urgent doctor queue priority",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = VS_OnSurfaceVariant
                        )
                    }
                }
                Switch(checked = isMaternalHighRisk, onCheckedChange = { isMaternalHighRisk = it })
            }
        }

        // Real-Time Clinical Triage Engine Card
        val isElevated = autoTriageSeverity != SeverityLevel.LOW
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isElevated) VS_ErrorContainer else VS_SuccessContainer,
            border = BorderStroke(1.dp, if (isElevated) VS_Error else VS_Success.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(if (isElevated) "🚨" else "✅", fontSize = 14.sp)
                        Text(
                            text = "Built-in Triage Engine",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isElevated) VS_OnErrorContainer else VS_OnSuccessContainer
                        )
                    }
                    Surface(
                        shape = PillShape,
                        color = if (isElevated) VS_Error else VS_Success
                    ) {
                        Text(
                            text = autoTriageSeverity.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = when {
                        isClinicalEmergency -> "Emergency vitals threshold crossed! Immediate priority assigned in doctor OPD queue."
                        isMaternalAlert -> "Maternal High-Risk Flagged (BP: $sysBp/$diaBp mmHg). Flagged for urgent doctor attention."
                        autoTriageSeverity == SeverityLevel.HIGH -> "Vitals outside safe clinical thresholds. Priority queue placement."
                        else -> "Vitals stable and within safe clinical thresholds. Standard OPD queue."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = if (isElevated) VS_OnErrorContainer else VS_OnSuccessContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        VitalSenseButton(
            text = "Register with Triage Priority",
            onClick = {
                val ageInt = ageText.toIntOrNull() ?: 30
                onSave(
                    Patient(
                        id = "pat_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                        name = name.ifBlank { "Registered Villager" },
                        age = ageInt,
                        gender = gender,
                        phone = phone.ifBlank { "+91 98765 43210" },
                        villageId = "v1",
                        villageName = village,
                        ashaWorkerId = ashaId,
                        ashaWorkerName = ashaName,
                        currentRiskLevel = autoTriageSeverity,
                        lastCondition = if (isMaternalAlert) "Maternal ANC Checkup (High Risk)" else initialCondition.trim(),
                        lastVisitDate = "Today",
                        nextAppointmentDate = "Scheduled via ASHA",
                        emergencyContact = "112"
                    )
                )
            }
        )
    }
}