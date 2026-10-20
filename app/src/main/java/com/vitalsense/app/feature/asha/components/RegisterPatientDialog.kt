package com.vitalsense.app.feature.asha.components
import androidx.compose.ui.res.stringResource
import com.vitalsense.app.R

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.*
import com.vitalsense.app.core.ui.theme.*
import java.util.UUID

@Composable
fun RegisterPatientDialog(
    asha: AshaWorker,
    isOffline: Boolean = false,
    isSunlightMode: Boolean = false,
    onDismiss: () -> Unit,
    onRegister: (Patient) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var village by remember { mutableStateOf(asha.assignedVillages.firstOrNull() ?: "Sundarpura") }
    var phone by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var initialCondition by remember { mutableStateOf("Routine Checkup / General Health") }
    var selectedRiskLevel by remember { mutableStateOf(SeverityLevel.LOW) }

    // Real-Time Clinical Triage Vitals Inputs (Audio 4)
    var systolicBp by remember { mutableStateOf("") }
    var diastolicBp by remember { mutableStateOf("") }
    var spO2Text by remember { mutableStateOf("") }
    var heartRateText by remember { mutableStateOf("") }
    var isMaternalHighRisk by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var showEmergencySosDialog by remember { mutableStateOf(false) }
    var emergencySosTriggered by remember { mutableStateOf(false) }
    var userBypassedEmergencyPrompt by remember { mutableStateOf(false) }

    // Dynamic Clinical Triage Evaluation
    val sysBp = systolicBp.toIntOrNull() ?: 120
    val diaBp = diastolicBp.toIntOrNull() ?: 80
    val spo2 = spO2Text.toIntOrNull() ?: 98
    val hr = heartRateText.toIntOrNull() ?: 76

    val isClinicalEmergency = (spO2Text.isNotBlank() && spo2 < 90) || sysBp > 165 || diaBp > 110
    val isMaternalAlert = isMaternalHighRisk || (gender == "Female" && sysBp >= 140 && diaBp >= 90)
    val autoTriageSeverity = when {
        isClinicalEmergency -> SeverityLevel.SEVERE
        isMaternalAlert || (spO2Text.isNotBlank() && spo2 < 94) || sysBp >= 140 -> SeverityLevel.HIGH
        sysBp in 130..139 || (spO2Text.isNotBlank() && spo2 in 94..95) -> SeverityLevel.MODERATE
        else -> selectedRiskLevel
    }

    val regBgColor = if (isSunlightMode) Color.White else VS_Surface
    val regBorder = if (isSunlightMode) BorderStroke(2.dp, Color.Black) else BorderStroke(1.dp, VS_Primary.copy(alpha = 0.6f))
    val regTextPrimary = if (isSunlightMode) Color.Black else VS_OnBackground
    val regTextSecondary = if (isSunlightMode) Color(0xFF1E293B) else VS_OnSurfaceVariant

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = regBgColor,
            border = regBorder,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(vertical = Spacing.sm)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.md)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                // Header
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
                            shape = CircleShape,
                            color = VS_PrimaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("➕", fontSize = 16.sp)
                            }
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.registerNewVillagerTitle),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = VS_OnBackground
                            )
                            Text(
                                text = "ASHA: ${asha.name} (${asha.ashaUniqueId})",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = VS_OnSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Text("✕", color = VS_OnSurfaceVariant, fontWeight = FontWeight.Bold)
                    }
                }

                if (isOffline) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VS_WarningContainer,
                        border = BorderStroke(1.dp, VS_Warning),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Text("⚡", fontSize = 14.sp)
                            Column {
                                Text(
                                    text = "Offline Mode Active (Zero Internet)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = VS_OnWarningContainer
                                )
                                Text(
                                    text = "Patient will be stored in Room SQLite & queued for auto-sync when online.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = VS_OnWarningContainer
                                )
                            }
                        }
                    }
                }

                // Proxy Registration Badge (Audio 4)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = VS_PrimaryContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, VS_Primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🤝", fontSize = 13.sp)
                        Text(
                            text = "ASHA Proxy Mode: Registering on behalf of patient",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = VS_Primary
                            )
                        )
                    }
                }

                HorizontalDivider(color = VS_Outline)

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall.copy(color = VS_Error, fontWeight = FontWeight.Bold)
                    )
                }

                // Full Name
                VitalSenseTextField(
                    value = name,
                    onValueChange = { name = it ; errorMessage = "" },
                    label = "Villager Full Name",
                    placeholder = "e.g. Meera Devi"
                )

                // Age & Gender Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        VitalSenseTextField(
                            value = ageText,
                            onValueChange = { ageText = it.filter { ch -> ch.isDigit() } },
                            label = "Age (Years)",
                            placeholder = "e.g. 29"
                        )
                    }

                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(
                            text = stringResource(R.string.genderLabel),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = VS_OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Female", "Male").forEach { g ->
                                val isSelected = gender == g
                                Surface(
                                    shape = PillShape,
                                    color = if (isSelected) VS_Primary else VS_SurfaceVariant,
                                    border = BorderStroke(1.dp, if (isSelected) VS_PrimaryContainer else VS_Outline),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { gender = g }
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                        Text(
                                            text = g,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.White else VS_OnBackground,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Phone & Emergency Contact
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        VitalSenseTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Mobile Phone",
                            placeholder = "98765 00000"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        VitalSenseTextField(
                            value = emergencyContact,
                            onValueChange = { emergencyContact = it },
                            label = "Emergency Phone",
                            placeholder = "Family / ASHA"
                        )
                    }
                }

                // Village Selector
                Text(
                    text = stringResource(R.string.assignedVillageLabel),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = VS_OnSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    asha.assignedVillages.forEach { v ->
                        val isSelected = village == v
                        Surface(
                            shape = PillShape,
                            color = if (isSelected) VS_Primary else VS_SurfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) VS_PrimaryContainer else VS_Outline),
                            modifier = Modifier.clickable { village = v }
                        ) {
                            Text(
                                text = "🏡 $v",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White else VS_OnBackground,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Initial Condition
                VitalSenseTextField(
                    value = initialCondition,
                    onValueChange = { initialCondition = it },
                    label = "Initial Health Condition / Symptoms",
                    placeholder = "e.g. Prenatal ANC checkup, Fever, Hypertension"
                )

                // Clinical Vitals Entry (Audio 4: "checks them against clinical risk thresholds")
                Text(
                    text = "Clinical Vitals Check (Triage Input)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
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

                // Maternal Care / High-Risk Pregnancy Toggle
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
                        Switch(
                            checked = isMaternalHighRisk,
                            onCheckedChange = { isMaternalHighRisk = it }
                        )
                    }
                }

                // Built-in Clinical Triage Engine Assessment Card (Audio 4)
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

                if (isClinicalEmergency) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.5.dp, Color(0xFFEF4444)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🚨", fontSize = 20.sp)
                                Column {
                                    Text(
                                        text = "CRITICAL VITALS DETECTED",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFDC2626)
                                    )
                                    Text(
                                        text = when {
                                            spO2Text.isNotBlank() && spo2 < 90 -> "Severe Hypoxia (SpO₂: $spo2%). High mortality risk."
                                            sysBp > 165 || diaBp > 110 -> "Hypertensive Emergency ($sysBp/$diaBp mmHg). Imminent stroke risk."
                                            else -> "Critical vital signs threshold exceeded."
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Color(0xFF7F1D1D)
                                    )
                                }
                            }

                            Button(
                                onClick = { showEmergencySosDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🚨 Trigger 1-Tap Emergency Referral / 108 Ambulance", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xxs))

                // Submit Button
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessage = "Please enter patient name."
                            return@Button
                        }
                        if (isClinicalEmergency && !userBypassedEmergencyPrompt && !emergencySosTriggered) {
                            showEmergencySosDialog = true
                            return@Button
                        }
                        val ageInt = ageText.toIntOrNull() ?: 25
                        val finalPhone = if (phone.isNotBlank()) phone else "+91 98765 ${UUID.randomUUID().toString().take(5)}"
                        val finalEmergency = if (emergencyContact.isNotBlank()) emergencyContact else asha.phone

                        val newPatient = Patient(
                            id = "pat_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                            name = name.trim(),
                            age = ageInt,
                            gender = gender,
                            phone = finalPhone,
                            villageId = "vil_${village.lowercase().replace(" ", "_")}",
                            villageName = village,
                            ashaWorkerId = asha.id,
                            ashaWorkerName = asha.name,
                            currentRiskLevel = autoTriageSeverity,
                            lastCondition = if (isMaternalAlert) "Maternal ANC Checkup (High Risk)" else initialCondition.trim().ifEmpty { "Routine Health Checkup" },
                            lastVisitDate = "Today",
                            nextAppointmentDate = "Scheduled via ASHA",
                            emergencyContact = finalEmergency
                        )

                        onRegister(newPatient)
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VS_Primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = if (isOffline) "⚡ Queue Patient in Room DB" else stringResource(R.string.registerVillagerCaseload),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }

    if (showEmergencySosDialog) {
        val context = androidx.compose.ui.platform.LocalContext.current
        AlertDialog(
            onDismissRequest = { showEmergencySosDialog = false },
            icon = { Text("🚨", fontSize = 32.sp) },
            title = {
                Text(
                    text = "Critical Vitals Detected!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFDC2626)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Patient displays life-threatening vitals:\n• SpO₂: $spo2%\n• Blood Pressure: $sysBp/$diaBp mmHg\n• Heart Rate: $hr bpm",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = VS_OnBackground
                    )
                    Text(
                        text = "Trigger 1-Tap Emergency Referral / 108 Ambulance now to automatically alert the PHC Medical Officer and dispatch the nearest emergency medical ambulance?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = VS_OnSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEmergencySosDialog = false
                        emergencySosTriggered = true
                        val patName = name.trim().ifEmpty { "Critical Emergency Patient" }
                        val ageInt = ageText.toIntOrNull() ?: 25
                        val finalPhone = if (phone.isNotBlank()) phone else "+91 98765 ${UUID.randomUUID().toString().take(5)}"
                        val finalEmergency = if (emergencyContact.isNotBlank()) emergencyContact else asha.phone

                        val newPatient = Patient(
                            id = "pat_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                            name = patName,
                            age = ageInt,
                            gender = gender,
                            phone = finalPhone,
                            villageId = "vil_${village.lowercase().replace(" ", "_")}",
                            villageName = village,
                            ashaWorkerId = asha.id,
                            ashaWorkerName = asha.name,
                            currentRiskLevel = SeverityLevel.SEVERE,
                            lastCondition = "EMERGENCY: SpO2 $spo2%, BP $sysBp/$diaBp mmHg (108 SOS Dispatched)",
                            lastVisitDate = "Today",
                            nextAppointmentDate = "Immediate Emergency Referral",
                            emergencyContact = finalEmergency
                        )
                        onRegister(newPatient)
                        com.vitalsense.app.core.util.AudioGuidanceHelper.provideEmergencySosHaptic(context)
                        com.vitalsense.app.core.util.AudioGuidanceHelper.speakEmergencyAlert(context)
                        com.vitalsense.app.core.util.EmergencySosHelper.dialEmergencyCall(context, "108")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("🚑 Trigger 1-Tap 108 SOS & Referral", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEmergencySosDialog = false
                        userBypassedEmergencyPrompt = true
                        val patName = name.trim().ifEmpty { "Registered Villager" }
                        val ageInt = ageText.toIntOrNull() ?: 25
                        val finalPhone = if (phone.isNotBlank()) phone else "+91 98765 ${UUID.randomUUID().toString().take(5)}"
                        val finalEmergency = if (emergencyContact.isNotBlank()) emergencyContact else asha.phone

                        val newPatient = Patient(
                            id = "pat_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                            name = patName,
                            age = ageInt,
                            gender = gender,
                            phone = finalPhone,
                            villageId = "vil_${village.lowercase().replace(" ", "_")}",
                            villageName = village,
                            ashaWorkerId = asha.id,
                            ashaWorkerName = asha.name,
                            currentRiskLevel = autoTriageSeverity,
                            lastCondition = if (isMaternalAlert) "Maternal ANC Checkup (High Risk)" else initialCondition.trim().ifEmpty { "Routine Health Checkup" },
                            lastVisitDate = "Today",
                            nextAppointmentDate = "Scheduled via ASHA",
                            emergencyContact = finalEmergency
                        )
                        onRegister(newPatient)
                    }
                ) {
                    Text("Continue Standard Registration", color = VS_OnSurfaceVariant)
                }
            }
        )
    }
}
