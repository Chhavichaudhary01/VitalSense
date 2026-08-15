package com.vitalsense.app.feature.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.data.repository.VitalSenseRepository
import com.vitalsense.app.core.state.AppStateHolder
import com.vitalsense.app.core.ui.components.TopRoleSwitcherBar
import com.vitalsense.app.feature.admin.AdminHomeScreen
import com.vitalsense.app.feature.admin.AdminViewModel
import com.vitalsense.app.feature.asha.AshaHomeScreen
import com.vitalsense.app.feature.auth.LoginScreen
import com.vitalsense.app.feature.doctor.DoctorHomeScreen
import com.vitalsense.app.feature.patient.PatientHomeScreen
import com.vitalsense.app.feature.patient.PatientViewModel
import kotlinx.coroutines.launch

@Composable
fun VitalSenseNavGraph(
    appStateHolder: AppStateHolder,
    repository: VitalSenseRepository,
    modifier: Modifier = Modifier,
    adminViewModel: AdminViewModel = hiltViewModel(),
    patientViewModel: PatientViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val isLoggedIn by appStateHolder.isLoggedIn.collectAsStateWithLifecycle()
    val currentRole by appStateHolder.currentRole.collectAsStateWithLifecycle()
    val activePatient by appStateHolder.activePatient.collectAsStateWithLifecycle()
    val activeAsha by appStateHolder.activeAsha.collectAsStateWithLifecycle()
    val activeDoctor by appStateHolder.activeDoctor.collectAsStateWithLifecycle()
    val activeProxyPatient by appStateHolder.activeProxyPatient.collectAsStateWithLifecycle()
    val isOffline by appStateHolder.isOffline.collectAsStateWithLifecycle()

    // Data streams from repository
    val villages by repository.getVillages().collectAsStateWithLifecycle(initialValue = emptyList())
    val patients by repository.getPatients().collectAsStateWithLifecycle(initialValue = emptyList())
    val conditions by repository.getConditionRecords().collectAsStateWithLifecycle(initialValue = emptyList())
    val appointments by repository.getAppointments().collectAsStateWithLifecycle(initialValue = emptyList())
    val notices by repository.getNotices().collectAsStateWithLifecycle(initialValue = emptyList())
    val dispensaryStock by repository.getDispensaryStock().collectAsStateWithLifecycle(initialValue = emptyList())

    // The effective patient (either the direct patient or the proxy patient being managed by ASHA)
    val effectivePatient = activeProxyPatient ?: activePatient

    AnimatedContent(
        targetState = isLoggedIn,
        label = "AuthTransition"
    ) { loggedIn ->
        if (!loggedIn) {
            LoginScreen(
                onPatientLogin = { selectedPatient ->
                    appStateHolder.loginAsPatient(selectedPatient)
                },
                onAshaLogin = { selectedAsha ->
                    appStateHolder.loginAsAsha(selectedAsha)
                },
                onDoctorLogin = { selectedDoctor ->
                    appStateHolder.loginAsDoctor(selectedDoctor)
                },
                onAdminLogin = {
                    appStateHolder.loginAsAdmin()
                },
                modifier = modifier
            )
        } else {
            Scaffold(
                topBar = {
                    TopRoleSwitcherBar(
                        currentRole = currentRole,
                        onRoleSelected = { newRole ->
                            appStateHolder.switchRole(newRole)
                        },
                        activeProxyPatient = activeProxyPatient,
                        onExitProxy = {
                            appStateHolder.clearProxy()
                            appStateHolder.switchRole(UserRole.ASHA)
                        },
                        isOffline = isOffline,
                        onToggleOffline = {
                            appStateHolder.toggleOffline()
                        },
                        onLogout = {
                            appStateHolder.logout()
                        }
                    )
                },
                containerColor = MaterialTheme.colorScheme.background,
                modifier = modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentRole) {
                        UserRole.PATIENT -> {
                            var showMentalWellness by remember { mutableStateOf(false) }

                            if (showMentalWellness) {
                                com.vitalsense.app.feature.patient.mentalhealth.MentalWellnessScreen(
                                    patient = effectivePatient,
                                    onLogMood = { notes, severity ->
                                        patientViewModel.logMentalWellness(
                                            patient = effectivePatient,
                                            moodNotes = notes,
                                            severityLevel = severity,
                                            isProxy = activeProxyPatient != null
                                        )
                                    },
                                    onBack = { showMentalWellness = false }
                                )
                            } else {
                                PatientHomeScreen(
                                    patient = effectivePatient,
                                    onCategoryClick = { category ->
                                        if (category == ConditionCategory.MENTAL_HEALTH) {
                                            showMentalWellness = true
                                        }
                                    },
                                    onViewHealthCard = {
                                        // Hook for Person 2 to navigate to full Health Card screen
                                    },
                                    onTriggerSos = {
                                        coroutineScope.launch {
                                            repository.triggerEmergencySos(effectivePatient, null, null)
                                        }
                                    }
                                )
                            }
                        }

                        UserRole.ASHA -> {
                            AshaHomeScreen(
                                asha = activeAsha,
                                patients = patients.filter { it.ashaWorkerId == activeAsha.id },
                                notices = notices,
                                onSelectProxyPatient = { selectedPatient ->
                                    // Activate Proxy Mode: switch to Patient dashboard on behalf of this patient!
                                    appStateHolder.setProxyPatient(selectedPatient)
                                    appStateHolder.switchRole(UserRole.PATIENT)
                                },
                                onRegisterPatientClick = {
                                    // Hook for Person 3 to open patient registration
                                },
                                onSendNoticeClick = {
                                    // Hook for Person 3 to broadcast notice
                                }
                            )
                        }

                        UserRole.DOCTOR -> {
                            DoctorHomeScreen(
                                doctor = activeDoctor,
                                pendingConditions = conditions,
                                appointments = appointments.filter { it.doctorId == activeDoctor.id },
                                dispensaryStock = dispensaryStock,
                                onRespondClick = { _ ->
                                    // Hook for Person 4 to open prescription writer
                                }
                            )
                        }

                        UserRole.ADMIN -> {
                            AdminHomeScreen(
                                villages = villages,
                                notices = notices,
                                onSendBroadcast = { title, message, village ->
                                    adminViewModel.sendBroadcast(
                                        title = title,
                                        message = message,
                                        targetVillage = village
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
