package com.vitalsense.app.feature.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.data.repository.VitalSenseRepository
import com.vitalsense.app.core.state.AppStateHolder
import com.vitalsense.app.core.ui.components.TopRoleSwitcherBar
import com.vitalsense.app.feature.admin.AdminHomeScreen
import com.vitalsense.app.feature.asha.AshaHomeScreen
import com.vitalsense.app.feature.auth.LoginScreen
import com.vitalsense.app.feature.doctor.DoctorHomeScreen
import com.vitalsense.app.feature.patient.PatientHomeScreen
import kotlinx.coroutines.launch

@Composable
fun VitalSenseNavGraph(
    appStateHolder: AppStateHolder,
    repository: VitalSenseRepository,
    modifier: Modifier = Modifier
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

    val activeUserName = when (currentRole) {
        UserRole.PATIENT -> effectivePatient.name
        UserRole.ASHA -> activeAsha.name
        UserRole.DOCTOR -> activeDoctor.name
        UserRole.ADMIN -> "District CMO (Rampur)"
    }

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
                        activeUserName = activeUserName,
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
                            PatientHomeScreen(
                                patient = effectivePatient,
                                onCategoryClick = { _ ->
                                    // Hook for Person 2/5 to navigate to category detail or symptom entry
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
                                    coroutineScope.launch {
                                        repository.sendNotice(
                                            BroadcastNotice(
                                                id = "notice_${System.currentTimeMillis()}",
                                                senderRole = UserRole.ADMIN,
                                                senderName = "District Chief Medical Officer",
                                                targetRole = "ALL",
                                                targetVillage = village,
                                                title = title,
                                                message = message,
                                                timestamp = System.currentTimeMillis(),
                                                isUrgent = true
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
