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
    var selectedRole by remember { mutableStateOf(UserRole.PATIENT) }

    // Form inputs
    var phoneInput by remember { mutableStateOf("") }
    var ashaIdInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var doctorEmailInput by remember { mutableStateOf("") }
    var doctorPasswordInput by remember { mutableStateOf("") }
    var adminPasscodeInput by remember { mutableStateOf("") }

    val samplePatients = remember { SeedDataProvider.initialPatients }
    val sampleAshas = remember { SeedDataProvider.initialAshaWorkers }
    val sampleDoctors = remember { SeedDataProvider.initialDoctors }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .padding(horizontal = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(top = Spacing.lg, bottom = Spacing.xxl)
    ) {
        // 1. App Header & Reactive Language Switcher
        item {
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(LimePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "V",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryNearBlack
                        )
                    }
                    Column {
                        Text(
                            text = strings.appName,
                            style = MaterialTheme.typography.displayMedium,
                            color = TextPrimaryNearBlack
                        )
                        Text(
                            text = strings.tagline,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }

                // Working Language Toggle Pill
                Surface(
                    onClick = onToggleLanguage,
                    shape = PillShape,
                    color = SurfaceWhite,
                    border = BorderStroke(1.5.dp, DarkCharcoal),
                    shadowElevation = 2.dp,
                    modifier = Modifier.defaultMinSize(minHeight = 40.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
                    ) {
                        Text(text = "🌐", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = currentLanguage.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimaryNearBlack
                        )
                    }
                }
            }
        }

        // 2. Welcome Title
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = strings.whoIsUsing,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = strings.selectRoleDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryMuted
                )
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
                        icon = "👤",
                        color = LimePrimary,
                        isSelected = selectedRole == UserRole.PATIENT,
                        onClick = { selectedRole = UserRole.PATIENT },
                        modifier = Modifier.weight(1f)
                    )
                    RoleCard(
                        role = UserRole.ASHA,
                        title = strings.roleAsha,
                        desc = strings.roleAshaDesc,
                        icon = "🤝",
                        color = LavenderSecondary,
                        isSelected = selectedRole == UserRole.ASHA,
                        onClick = { selectedRole = UserRole.ASHA },
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
                        icon = "🩺",
                        color = BlushPinkTertiary,
                        isSelected = selectedRole == UserRole.DOCTOR,
                        onClick = { selectedRole = UserRole.DOCTOR },
                        modifier = Modifier.weight(1f)
                    )
                    RoleCard(
                        role = UserRole.ADMIN,
                        title = strings.roleAdmin,
                        desc = strings.roleAdminDesc,
                        icon = "🛡️",
                        color = AmberWarning,
                        isSelected = selectedRole == UserRole.ADMIN,
                        onClick = { selectedRole = UserRole.ADMIN },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Role Credentials Form & 1-Tap Demo Login
        item {
            VitalSenseCard(
                elevation = 2.dp,
                backgroundColor = SurfaceWhite
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
                        color = TextPrimaryNearBlack
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

                            // 1-Tap Demo Logins for Evaluators
                            Text(
                                text = strings.quickDemoLogin,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                            samplePatients.forEach { patient ->
                                Surface(
                                    onClick = { onPatientLogin(patient) },
                                    shape = PillShape,
                                    color = LimePrimary.copy(alpha = 0.35f),
                                    border = BorderStroke(1.dp, CardBorderSubtle),
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
                                            color = TextPrimaryNearBlack
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
                                style = ButtonStyle.DARK
                            )

                            Text(
                                text = strings.quickDemoLogin,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                            sampleAshas.forEach { asha ->
                                Surface(
                                    onClick = { onAshaLogin(asha) },
                                    shape = PillShape,
                                    color = LavenderSecondary.copy(alpha = 0.35f),
                                    border = BorderStroke(1.dp, CardBorderSubtle),
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
                                            color = TextPrimaryNearBlack
                                        )
                                        Text(
                                            text = "${asha.activePatientCount} patients",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondaryMuted
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
                                color = TextSecondaryMuted
                            )
                            sampleDoctors.forEach { doc ->
                                Surface(
                                    onClick = { onDoctorLogin(doc) },
                                    shape = PillShape,
                                    color = BlushPinkTertiary.copy(alpha = 0.35f),
                                    border = BorderStroke(1.dp, CardBorderSubtle),
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
                                            color = TextPrimaryNearBlack
                                        )
                                        Text(
                                            text = doc.specialty.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondaryMuted
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
                                style = ButtonStyle.DARK
                            )

                            Text(
                                text = strings.quickDemoLogin,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                            Surface(
                                onClick = onAdminLogin,
                                shape = PillShape,
                                color = AmberWarning.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, CardBorderSubtle),
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
                                        color = TextPrimaryNearBlack
                                    )
                                    Text(
                                        text = "Full Access",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AmberWarningDark
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
                    color = TextSecondaryMuted
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
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 90.dp),
        shape = CardShape,
        color = if (isSelected) color.copy(alpha = 0.45f) else SurfaceWhite,
        shadowElevation = if (isSelected) 2.dp else 1.dp,
        border = if (isSelected) BorderStroke(2.dp, DarkCharcoal) else BorderStroke(1.dp, CardBorderColor)
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
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(DarkCharcoal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✓", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryNearBlack
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted,
                    maxLines = 1
                )
            }
        }
    }
}
