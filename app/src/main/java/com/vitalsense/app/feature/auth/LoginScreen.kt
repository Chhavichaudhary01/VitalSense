package com.vitalsense.app.feature.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.local.seed.SeedDataProvider
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.components.*
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.util.AudioGuidanceHelper

@Composable
fun LoginScreen(
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onPatientLogin: (Patient) -> Unit,
    onAshaLogin: (AshaWorker) -> Unit,
    onDoctorLogin: (Doctor) -> Unit,
    onAdminLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var selectedRole by remember { mutableStateOf(UserRole.DOCTOR) } // Default to Doctor to showcase Glume UI

    // Form inputs
    var phoneInput by remember { mutableStateOf("") }
    var ashaIdInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var doctorEmailInput by remember { mutableStateOf("") }
    var doctorPasswordInput by remember { mutableStateOf("") }
    var adminPasscodeInput by remember { mutableStateOf("") }
    var showAshaQrClaimDialog by remember { mutableStateOf(false) }
    var pendingSelectedRole by remember { mutableStateOf<UserRole?>(null) }
    var showRoleConfirmDialog by remember { mutableStateOf(false) }

    val samplePatients = remember { SeedDataProvider.initialPatients }
    val sampleAshas = remember { SeedDataProvider.initialAshaWorkers }
    val sampleDoctors = remember { SeedDataProvider.initialDoctors }

    val context = androidx.compose.ui.platform.LocalContext.current

    // Double-Confirmation Dialog for Role Selection as per UX Architecture §1.3
    if (showRoleConfirmDialog && pendingSelectedRole != null) {
        val role = pendingSelectedRole!!
        val roleName = when (role) {
            UserRole.PATIENT -> if (currentLanguage == AppLanguage.HINDI) "मरीज़ (Patient)" else "Patient"
            UserRole.ASHA -> if (currentLanguage == AppLanguage.HINDI) "आशा स्वास्थ्य कार्यकर्ता (Health Worker)" else "ASHA Health Worker"
            UserRole.DOCTOR -> if (currentLanguage == AppLanguage.HINDI) "डॉक्टर (Doctor)" else "Doctor"
            UserRole.ADMIN -> if (currentLanguage == AppLanguage.HINDI) "ज़िला प्रशासक (Admin)" else "District Admin"
        }

        VitalSenseDialog(
            onDismissRequest = { showRoleConfirmDialog = false },
            title = if (currentLanguage == AppLanguage.HINDI) "भूमिका चयन पुष्टि" else "Confirm Role Selection",
            icon = { Text("✅", fontSize = 22.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        selectedRole = role
                        showRoleConfirmDialog = false
                        AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(containerColor = GlumeSuccessMint),
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "✅ हाँ, सही है (Yes, Correct)" else "✅ Yes, Correct",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = GlumeBackground
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRoleConfirmDialog = false },
                    shape = PillShape,
                    modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "🔁 फिर से चुनें (Choose Again)" else "🔁 Choose Again",
                        style = MaterialTheme.typography.labelLarge,
                        color = GlumeTextSecondary
                    )
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "आपने चुना है: $roleName" else "You have chosen: $roleName",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "क्या आप इस भूमिका के साथ आगे बढ़ना चाहते हैं?" else "Do you want to proceed with this role experience?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GlumeTextSecondary
                )
            }
        }
    }

    if (showAshaQrClaimDialog) {
        com.vitalsense.app.feature.auth.components.AshaQrClaimDialog(
            language = currentLanguage,
            onDismiss = { showAshaQrClaimDialog = false },
            onPatientClaimed = { claimedPatient ->
                onPatientLogin(claimedPatient)
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.md, bottom = Spacing.xxl)
    ) {
        // 1. Minimal Header & Quick Nav Links (3 Items Max: 🩺 For Patients, 🧑‍⚕️ For ASHA, 📞 108 Help)
        item {
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
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(VitalSenseTealPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🫀", fontSize = 20.sp)
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "VitalSense",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = PillShape,
                                    color = VitalSenseTealContainer
                                ) {
                                    Text(
                                        text = "सेहतसेतु",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = VitalSenseTealPrimary
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Reactive Language Switcher
                    Surface(
                        onClick = onToggleLanguage,
                        shape = PillShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "🌐", fontSize = 14.sp)
                            Text(
                                text = currentLanguage.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // 3 Nav Shortcut Chips (Glanceable Navigation)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Surface(
                        onClick = {
                            selectedRole = UserRole.PATIENT
                            AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
                        },
                        shape = PillShape,
                        color = if (selectedRole == UserRole.PATIENT) VitalSenseTealContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (selectedRole == UserRole.PATIENT) VitalSenseTealPrimary else MaterialTheme.colorScheme.outline),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🩺 ", fontSize = 12.sp)
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "मरीज़" else "Patients",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedRole == UserRole.PATIENT) VitalSenseTealPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    Surface(
                        onClick = {
                            selectedRole = UserRole.ASHA
                            AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
                        },
                        shape = PillShape,
                        color = if (selectedRole == UserRole.ASHA) VitalSenseTealContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (selectedRole == UserRole.ASHA) VitalSenseTealPrimary else MaterialTheme.colorScheme.outline),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🧑‍⚕️ ", fontSize = 12.sp)
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "आशा" else "ASHA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedRole == UserRole.ASHA) VitalSenseTealPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    Surface(
                        onClick = {
                            com.vitalsense.app.core.util.EmergencySosHelper.dialEmergencyCall(context, "108")
                        },
                        shape = PillShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, GlumeAlertCoral.copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📞 ", fontSize = 12.sp)
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "108 मदद" else "108 Help",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GlumeAlertCoral
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. THE LOW-LITERACY HERO CARD (Clean Presentation Design)
        item {
            VitalSenseCard(
                backgroundColor = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    // Offline Badge
                    Surface(
                        shape = PillShape,
                        color = VitalSenseTealContainer,
                        border = BorderStroke(1.dp, VitalSenseTealPrimary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🛡️", fontSize = 12.sp)
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "100% ऑफ़लाइन सक्षम · Works Zero-Internet" else "100% Offline Ready · Works Zero-Internet",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = VitalSenseTealPrimary
                                )
                            )
                        }
                    }

                    // Headline (<=6 words, emoji-anchored, large type)
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "🫀 आपकी सेहत, एक नज़र में" else "🫀 Your health, at a glance",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            lineHeight = 30.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    // Spoken-language Subhead
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "ग्रामीण और दूरदराज क्षेत्रों के लिए ऑफ़लाइन टेलीमेडिसिन और स्वास्थ्य निगरानी।"
                        else
                            "Zero-internet telemedicine & vital signs tracking designed for rural communities.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    // Action Buttons (56dp height touch targets)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        // 1. Primary CTA: Quick Start / Demo Login
                        Button(
                            onClick = {
                                when (selectedRole) {
                                    UserRole.PATIENT -> onPatientLogin(samplePatients.first())
                                    UserRole.ASHA -> onAshaLogin(sampleAshas.first())
                                    UserRole.DOCTOR -> onDoctorLogin(sampleDoctors.first())
                                    UserRole.ADMIN -> onAdminLogin()
                                }
                            },
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(containerColor = VitalSenseTealPrimary),
                            modifier = Modifier.weight(1.2f).height(52.dp)
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "📲 शुरू करें" else "📲 Get Started",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        // 2. Secondary Ghost Button: 'Listen to this'
                        OutlinedButton(
                            onClick = {
                                AudioGuidanceHelper.provideHapticFeedback(context, isSuccess = true)
                                val speech = if (currentLanguage == AppLanguage.HINDI)
                                    "नमस्ते। वाइटलसेंस में आपका स्वागत है। आपकी सेहत, एक नज़र में। यह ऐप बिना इंटरनेट के भी आपकी धड़कन, ऑक्सीजन और स्वास्थ्य की पूरी देखभाल करता है।"
                                else
                                    "Welcome to VitalSense. Your health, at a glance. Offline health monitoring for rural families."
                                AudioGuidanceHelper.speak(context, speech, currentLanguage)
                            },
                            shape = PillShape,
                            border = BorderStroke(1.dp, VitalSenseTealPrimary.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.weight(1f).height(52.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🔊", fontSize = 16.sp)
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "इसे सुनें" else "Listen",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = VitalSenseTealPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. 4-Role Selector Cards (2x2 Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    RoleCard(
                        role = UserRole.PATIENT,
                        title = strings.rolePatient,
                        desc = strings.rolePatientDesc,
                        icon = "🧑",
                        isSelected = selectedRole == UserRole.PATIENT,
                        onClick = {
                            pendingSelectedRole = UserRole.PATIENT
                            showRoleConfirmDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                    RoleCard(
                        role = UserRole.ASHA,
                        title = strings.roleAsha,
                        desc = strings.roleAshaDesc,
                        icon = "🩺",
                        isSelected = selectedRole == UserRole.ASHA,
                        onClick = {
                            pendingSelectedRole = UserRole.ASHA
                            showRoleConfirmDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    RoleCard(
                        role = UserRole.DOCTOR,
                        title = strings.roleDoctor,
                        desc = strings.roleDoctorDesc,
                        icon = "👨‍⚕️",
                        isSelected = selectedRole == UserRole.DOCTOR,
                        onClick = {
                            pendingSelectedRole = UserRole.DOCTOR
                            showRoleConfirmDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                    RoleCard(
                        role = UserRole.ADMIN,
                        title = strings.roleAdmin,
                        desc = strings.roleAdminDesc,
                        icon = "🛡️",
                        isSelected = selectedRole == UserRole.ADMIN,
                        onClick = {
                            pendingSelectedRole = UserRole.ADMIN
                            showRoleConfirmDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Role Credentials Form & 1-Tap Demo Login
        item {
            VitalSenseCard(
                backgroundColor = GlumeSurfaceCard
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Text(
                        text = when (selectedRole) {
                            UserRole.PATIENT -> strings.patientSignIn
                            UserRole.ASHA -> strings.ashaSignIn
                            UserRole.DOCTOR -> strings.doctorSignIn
                            UserRole.ADMIN -> strings.adminSignIn
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = GlumeTextPrimary
                    )

                    when (selectedRole) {
                        UserRole.PATIENT -> {
                            VitalSenseTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = strings.mobileNumber,
                                placeholder = "+91 98111 22334"
                            )
                            VitalSenseTextField(
                                value = ashaIdInput,
                                onValueChange = { ashaIdInput = it },
                                label = strings.ashaHelperIdOptional,
                                placeholder = "e.g. ASHA-7701"
                            )
                            VitalSenseButton(
                                text = strings.logInAsPatient,
                                onClick = { onPatientLogin(samplePatients.first()) },
                                style = ButtonStyle.PRIMARY
                            )

                            OutlinedButton(
                                onClick = { showAshaQrClaimDialog = true },
                                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp),
                                shape = PillShape,
                                border = BorderStroke(1.dp, GlumePrimaryPurple)
                            ) {
                                Text(
                                    text = "🪪 " + (if (currentLanguage == AppLanguage.HINDI) "आशा स्वास्थ्य कार्ड स्कैन करें (QR Claim)" else "Scan ASHA Health Card (QR Claim)"),
                                    color = GlumePrimaryPurpleLight,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            // 1-Tap Demo Logins for Evaluators
                            Text(
                                text = strings.quickDemoLogin,
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumeTextSecondary
                            )
                            samplePatients.forEach { patient ->
                                Surface(
                                    onClick = { onPatientLogin(patient) },
                                    shape = PillShape,
                                    color = GlumeSurfaceElevated,
                                    border = BorderStroke(1.dp, GlumeBorder),
                                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${patient.name} (${patient.villageName})",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = GlumeTextPrimary
                                        )
                                        SeverityBadge(severity = patient.currentRiskLevel)
                                    }
                                }
                            }
                        }

                        UserRole.ASHA -> {
                            VitalSenseTextField(
                                value = ashaIdInput,
                                onValueChange = { ashaIdInput = it },
                                label = strings.uniqueAshaId,
                                placeholder = "e.g. ASHA-7701"
                            )
                            VitalSenseTextField(
                                value = pinInput,
                                onValueChange = { pinInput = it },
                                label = strings.securityPin,
                                placeholder = "••••",
                                visualTransformation = PasswordVisualTransformation()
                            )
                            VitalSenseButton(
                                text = strings.logInAsAsha,
                                onClick = { onAshaLogin(sampleAshas.first()) },
                                style = ButtonStyle.PRIMARY
                            )

                            Text(
                                text = strings.quickDemoLogin,
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumeTextSecondary
                            )
                            sampleAshas.forEach { asha ->
                                Surface(
                                    onClick = { onAshaLogin(asha) },
                                    shape = PillShape,
                                    color = GlumeSurfaceElevated,
                                    border = BorderStroke(1.dp, GlumeBorder),
                                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${asha.name} (${asha.ashaUniqueId})",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = GlumeTextPrimary
                                        )
                                        Text(
                                            text = "${asha.activePatientCount} patients",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GlumeTextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        UserRole.DOCTOR -> {
                            VitalSenseTextField(
                                value = doctorEmailInput,
                                onValueChange = { doctorEmailInput = it },
                                label = strings.doctorEmail,
                                placeholder = "dr.rajesh@vitalsense.org"
                            )
                            VitalSenseTextField(
                                value = doctorPasswordInput,
                                onValueChange = { doctorPasswordInput = it },
                                label = strings.password,
                                placeholder = "••••••••",
                                visualTransformation = PasswordVisualTransformation()
                            )
                            VitalSenseButton(
                                text = strings.logInAsDoctor,
                                onClick = { onDoctorLogin(sampleDoctors.first()) },
                                style = ButtonStyle.PRIMARY
                            )

                            Text(
                                text = strings.quickDemoLogin,
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumeTextSecondary
                            )
                            sampleDoctors.forEach { doc ->
                                Surface(
                                    onClick = { onDoctorLogin(doc) },
                                    shape = PillShape,
                                    color = GlumeSurfaceElevated,
                                    border = BorderStroke(1.dp, GlumeBorder),
                                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = doc.name,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = GlumeTextPrimary
                                        )
                                        Text(
                                            text = doc.specialty.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GlumePrimaryPurpleLight
                                        )
                                    }
                                }
                            }
                        }

                        UserRole.ADMIN -> {
                            VitalSenseTextField(
                                value = adminPasscodeInput,
                                onValueChange = { adminPasscodeInput = it },
                                label = strings.adminPasscode,
                                placeholder = "ADMIN-RAMPUR-2026",
                                visualTransformation = PasswordVisualTransformation()
                            )
                            VitalSenseButton(
                                text = strings.logInAsAdmin,
                                onClick = onAdminLogin,
                                style = ButtonStyle.PRIMARY
                            )

                            Text(
                                text = strings.quickDemoLogin,
                                style = MaterialTheme.typography.labelSmall,
                                color = GlumeTextSecondary
                            )
                            Surface(
                                onClick = onAdminLogin,
                                shape = PillShape,
                                color = GlumeSurfaceElevated,
                                border = BorderStroke(1.dp, GlumeBorder),
                                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "District Chief Medical Officer (Rampur)",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = GlumeTextPrimary
                                    )
                                    Text(
                                        text = "Full Access",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GlumeSuccessText
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Offline resilience badge
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.offlineBanner,
                    style = MaterialTheme.typography.labelSmall,
                    color = GlumeTextSecondary
                )
            }
        }
    }
}

@Composable
private fun RoleCard(
    role: UserRole,
    title: String,
    desc: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 90.dp),
        shape = CardShape,
        color = if (isSelected) GlumePrimaryPurpleContainer else GlumeSurfaceCard,
        shadowElevation = 0.dp,
        border = if (isSelected) BorderStroke(1.5.dp, GlumePrimaryPurple) else BorderStroke(1.dp, GlumeBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, style = MaterialTheme.typography.titleLarge)
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(GlumePrimaryPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) GlumePrimaryPurpleLight else GlumeTextPrimary
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.labelSmall,
                    color = GlumeTextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
