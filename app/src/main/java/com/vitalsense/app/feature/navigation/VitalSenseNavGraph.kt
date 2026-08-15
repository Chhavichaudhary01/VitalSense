package com.vitalsense.app.feature.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import com.vitalsense.app.feature.asha.AshaHomeScreen
import com.vitalsense.app.feature.auth.LoginScreen
import com.vitalsense.app.feature.doctor.CaseDetailScreen
import com.vitalsense.app.feature.doctor.DoctorHomeScreen
import com.vitalsense.app.feature.doctor.DoctorViewModel
import com.vitalsense.app.feature.patient.PatientHomeScreen
import com.vitalsense.app.feature.patient.PatientViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun VitalSenseNavGraph(
    appStateHolder: AppStateHolder,
    repository: VitalSenseRepository,
    patientViewModel: PatientViewModel = hiltViewModel(),
    doctorViewModel: DoctorViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // Core global state
    val isLoggedIn by appStateHolder.isLoggedIn.collectAsStateWithLifecycle()
    val currentRole by appStateHolder.currentRole.collectAsStateWithLifecycle()
    val activePatient by appStateHolder.activePatient.collectAsStateWithLifecycle()
    val activeAsha by appStateHolder.activeAsha.collectAsStateWithLifecycle()
    val activeDoctor by appStateHolder.activeDoctor.collectAsStateWithLifecycle()
    val activeProxyPatient by appStateHolder.activeProxyPatient.collectAsStateWithLifecycle()
    val isOffline by appStateHolder.isOffline.collectAsStateWithLifecycle()

    // Doctor specific scoped streams
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

    val currentLanguage by appStateHolder.currentLanguage.collectAsStateWithLifecycle()

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
        transitionSpec = {
            fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
        },
        label = "AuthTransition"
    ) { loggedIn ->
        if (!loggedIn) {
            LoginScreen(
                currentLanguage = currentLanguage,
                onToggleLanguage = { appStateHolder.toggleLanguage() },
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
                        currentLanguage = currentLanguage,
                        onToggleLanguage = {
                            appStateHolder.toggleLanguage()
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
                    AnimatedContent(
                        targetState = currentRole,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                        },
                        label = "RoleTransition"
                    ) { role ->
                        when (role) {
                            UserRole.PATIENT -> {
                                var showMentalWellness by remember { mutableStateOf(false) }

                                AnimatedContent(
                                    targetState = showMentalWellness,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                                    },
                                    label = "PatientScreenTransition"
                                ) { inMentalWellness ->
                                    if (inMentalWellness) {
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
                                        if (activeProxyPatient != null) {
                                            BackHandler {
                                                appStateHolder.clearProxy()
                                                appStateHolder.switchRole(UserRole.ASHA)
                                            }
                                        } else {
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
                                                // Health card view hook
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
                            }

                            UserRole.ASHA -> {
                                BackHandler {
                                    appStateHolder.logout()
                                }

                                AshaHomeScreen(
                                    asha = activeAsha,
                                    patients = patients.filter { it.ashaWorkerId == activeAsha.id },
                                    notices = notices,
                                    onSelectProxyPatient = { selectedPatient ->
                                        appStateHolder.setProxyPatient(selectedPatient)
                                        appStateHolder.switchRole(UserRole.PATIENT)
                                    },
                                    onRegisterPatientClick = {},
                                    onSendNoticeClick = {},
                                    onSavePrescription = { rx ->
                                        coroutineScope.launch {
                                            repository.savePrescription(rx)
                                        }
                                    }
                                )
                            }

                            UserRole.DOCTOR -> {
                                val activeCase = selectedDoctorCase

                                AnimatedContent(
                                    targetState = activeCase,
                                    transitionSpec = {
                                        if (targetState != null) {
                                            slideInHorizontally(tween(240)) { it / 4 } + fadeIn(tween(220)) togetherWith
                                                    slideOutHorizontally(tween(200)) { -it / 4 } + fadeOut(tween(180))
                                        } else {
                                            slideInHorizontally(tween(240)) { -it / 4 } + fadeIn(tween(220)) togetherWith
                                                    slideOutHorizontally(tween(200)) { it / 4 } + fadeOut(tween(180))
                                        }
                                    },
                                    label = "DoctorDetailTransition"
                                ) { currentDoctorCase ->
                                    if (currentDoctorCase != null) {
                                        BackHandler {
                                            doctorViewModel.clearSelectedCase()
                                        }

                                        CaseDetailScreen(
                                            record = currentDoctorCase,
                                            patient = patientProfile,
                                            priorPrescriptions = patientPrescriptions,
                                            dispensaryStock = doctorDispensaryStock,
                                            currentDoctor = activeDoctor,
                                            allConditions = allConditions.filter { it.patientId == currentDoctorCase.patientId },
                                            allAppointments = allAppointments.filter { it.patientId == currentDoctorCase.patientId },
                                            onBack = { doctorViewModel.clearSelectedCase() },
                                            onSubmitResponse = { responseText, privateNotes ->
                                                doctorViewModel.submitMedicalResponse(
                                                    caseId = currentDoctorCase.id,
                                                    responseText = responseText,
                                                    privateNotes = privateNotes
                                                )
                                            },
                                            onIssuePrescription = { medicines, instructions ->
                                                doctorViewModel.issuePrescription(
                                                    caseId = currentDoctorCase.id,
                                                    patientId = currentDoctorCase.patientId,
                                                    patientName = currentDoctorCase.patientName,
                                                    medicines = medicines,
                                                    instructions = instructions
                                                )
                                            },
                                            onProposeAppointment = { date, timeSlot ->
                                                doctorViewModel.proposeAppointment(
                                                    patientId = currentDoctorCase.patientId,
                                                    patientName = currentDoctorCase.patientName,
                                                    dateFormatted = date,
                                                    timeSlot = timeSlot
                                                )
                                            },
                                            onReferCase = { targetSpecialty, referralNotes ->
                                                doctorViewModel.referCase(
                                                    caseId = currentDoctorCase.id,
                                                    targetSpecialty = targetSpecialty,
                                                    referralNotes = referralNotes
                                                )
                                            }
                                        )
                                    } else {
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
                            }

                            UserRole.ADMIN -> {
                                BackHandler {
                                    appStateHolder.logout()
                                }

                                AdminHomeScreen(
                                    villages = villages,
                                    notices = notices,
                                    dispensaryStock = doctorDispensaryStock,
                                    onSendBroadcast = { title, message, village ->
                                        coroutineScope.launch {
                                            val broadcast = BroadcastNotice(
                                                id = "notice_${System.currentTimeMillis()}",
                                                senderRole = UserRole.ADMIN,
                                                senderName = "Chief Medical Officer",
                                                targetRole = "ALL",
                                                targetVillage = village,
                                                title = title,
                                                message = message,
                                                timestamp = System.currentTimeMillis(),
                                                isUrgent = false
                                            )
                                            repository.sendNotice(broadcast)
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
}
