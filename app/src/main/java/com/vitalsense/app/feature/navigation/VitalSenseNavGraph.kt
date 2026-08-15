package com.vitalsense.app.feature.navigation

import androidx.activity.compose.BackHandler
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
import com.vitalsense.app.feature.doctor.CaseDetailScreen
import com.vitalsense.app.feature.doctor.DoctorHomeScreen
import com.vitalsense.app.feature.doctor.DoctorViewModel
import com.vitalsense.app.feature.patient.PatientHomeScreen
import com.vitalsense.app.feature.patient.PatientViewModel
import kotlinx.coroutines.launch

@Composable
fun VitalSenseNavGraph(
    appStateHolder: AppStateHolder,
    repository: VitalSenseRepository,
    modifier: Modifier = Modifier,
    adminViewModel: AdminViewModel = hiltViewModel(),
    patientViewModel: PatientViewModel = hiltViewModel(),
    doctorViewModel: DoctorViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val isLoggedIn by appStateHolder.isLoggedIn.collectAsStateWithLifecycle()
    val currentRole by appStateHolder.currentRole.collectAsStateWithLifecycle()
    val activePatient by appStateHolder.activePatient.collectAsStateWithLifecycle()
    val activeAsha by appStateHolder.activeAsha.collectAsStateWithLifecycle()
    val activeDoctor by appStateHolder.activeDoctor.collectAsStateWithLifecycle()
    val activeProxyPatient by appStateHolder.activeProxyPatient.collectAsStateWithLifecycle()
    val isOffline by appStateHolder.isOffline.collectAsStateWithLifecycle()

    // Doctor specific scoped streams (§2 & §3)
    val doctorCases by doctorViewModel.scopedCases.collectAsStateWithLifecycle()
    val doctorAppointments by doctorViewModel.appointments.collectAsStateWithLifecycle()
    val doctorDispensaryStock by doctorViewModel.dispensaryStock.collectAsStateWithLifecycle()
    val selectedDoctorCase by doctorViewModel.selectedCase.collectAsStateWithLifecycle()
    val patientPrescriptions by doctorViewModel.patientPrescriptions.collectAsStateWithLifecycle()
    val patientProfile by doctorViewModel.patientProfile.collectAsStateWithLifecycle()

    // Data streams from repository for general components
    val villages by repository.getVillages().collectAsStateWithLifecycle(initialValue = emptyList())
    val patients by repository.getPatients().collectAsStateWithLifecycle(initialValue = emptyList())
    val notices by repository.getNotices().collectAsStateWithLifecycle(initialValue = emptyList())
    val allPrescriptions by repository.getPrescriptions().collectAsStateWithLifecycle(initialValue = emptyList())
    val allConditions by repository.getConditionRecords().collectAsStateWithLifecycle(initialValue = emptyList())
    val allAppointments by repository.getAppointments().collectAsStateWithLifecycle(initialValue = emptyList())

    // The effective patient (either direct or proxy managed by ASHA)
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
                            doctorViewModel.clearSelectedCase()
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
                                // Intercept system back button to return to Patient Home
                                BackHandler {
                                    showMentalWellness = false
                                }

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
                                // If at Patient root and in Proxy mode, back button returns to ASHA Caseload
                                if (activeProxyPatient != null) {
                                    BackHandler {
                                        appStateHolder.clearProxy()
                                        appStateHolder.switchRole(UserRole.ASHA)
                                    }
                                } else {
                                    // If at Patient root, back button returns to Login Screen
                                    BackHandler {
                                        appStateHolder.logout()
                                    }
                                }

                                PatientHomeScreen(
                                    patient = effectivePatient,
                                    notices = notices,
                                    prescriptions = allPrescriptions.filter { it.patientId == effectivePatient.id },
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
                                    },
                                    onSavePrescription = { rx ->
                                        coroutineScope.launch {
                                            repository.savePrescription(rx)
                                        }
                                    }
                                )
                            }
                        }

                        UserRole.ASHA -> {
                            // If at ASHA root, back button returns to Login Screen
                            BackHandler {
                                appStateHolder.logout()
                            }

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
                                },
                                onSavePrescription = { rx ->
                                    coroutineScope.launch {
                                        repository.savePrescription(rx)
                                    }
                                }
                            )
                        }

                        UserRole.DOCTOR -> {
                            val activeCase = selectedDoctorCase
                            if (activeCase != null) {
                                // Intercept system back button to return to Doctor Home Case Queue
                                BackHandler {
                                    doctorViewModel.clearSelectedCase()
                                }

                                CaseDetailScreen(
                                    record = activeCase,
                                    patient = patientProfile,
                                    priorPrescriptions = patientPrescriptions,
                                    dispensaryStock = doctorDispensaryStock,
                                    currentDoctor = activeDoctor,
                                    allConditions = allConditions.filter { it.patientId == activeCase.patientId },
                                    allAppointments = allAppointments.filter { it.patientId == activeCase.patientId },
                                    onBack = { doctorViewModel.clearSelectedCase() },
                                    onSubmitResponse = { responseText, privateNotes ->
                                        doctorViewModel.submitMedicalResponse(
                                            caseId = activeCase.id,
                                            responseText = responseText,
                                            privateNotes = privateNotes
                                        )
                                    },
                                    onIssuePrescription = { medicines, instructions ->
                                        doctorViewModel.issuePrescription(
                                            caseId = activeCase.id,
                                            patientId = activeCase.patientId,
                                            patientName = activeCase.patientName,
                                            medicines = medicines,
                                            instructions = instructions
                                        )
                                    },
                                    onProposeAppointment = { date, timeSlot ->
                                        doctorViewModel.proposeAppointment(
                                            patientId = activeCase.patientId,
                                            patientName = activeCase.patientName,
                                            dateFormatted = date,
                                            timeSlot = timeSlot
                                        )
                                    },
                                    onReferCase = { targetSpecialty, referralNotes ->
                                        doctorViewModel.referCase(
                                            caseId = activeCase.id,
                                            targetSpecialty = targetSpecialty,
                                            referralNotes = referralNotes
                                        )
                                    }
                                )
                            } else {
                                // If at Doctor root, back button returns to Login Screen
                                BackHandler {
                                    appStateHolder.logout()
                                }

                                DoctorHomeScreen(
                                    doctor = activeDoctor,
                                    cases = doctorCases,
                                    appointments = doctorAppointments,
                                    dispensaryStock = doctorDispensaryStock,
                                    patients = patients,
                                    notices = notices,
                                    allConditions = allConditions,
                                    allPrescriptions = allPrescriptions,
                                    onSelectCase = { record ->
                                        doctorViewModel.selectCase(record)
                                    },
                                    onAcceptAppointment = { apptId ->
                                        doctorViewModel.acceptAppointment(apptId)
                                    },
                                    onDeclineAppointment = { apptId ->
                                        doctorViewModel.declineAppointment(apptId)
                                    },
                                    onProposeAppointment = { patId, patName, date, slot ->
                                        doctorViewModel.proposeAppointment(
                                            patientId = patId,
                                            patientName = patName,
                                            dateFormatted = date,
                                            timeSlot = slot
                                        )
                                    }
                                )
                            }
                        }

                        UserRole.ADMIN -> {
                            // If at Admin root, back button returns to Login Screen
                            BackHandler {
                                appStateHolder.logout()
                            }

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
