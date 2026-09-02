package com.vitalsense.app.core.state

import com.vitalsense.app.core.data.local.seed.SeedDataProvider
import com.vitalsense.app.core.data.model.*
import com.vitalsense.app.core.ui.theme.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStateHolder @Inject constructor() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.PATIENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _isPresentationLightMode = MutableStateFlow(true)
    val isPresentationLightMode: StateFlow<Boolean> = _isPresentationLightMode.asStateFlow()

    private val _activePatient = MutableStateFlow(SeedDataProvider.initialPatients.first())
    val activePatient: StateFlow<Patient> = _activePatient.asStateFlow()

    private val _activeAsha = MutableStateFlow(SeedDataProvider.initialAshaWorkers.first())
    val activeAsha: StateFlow<AshaWorker> = _activeAsha.asStateFlow()

    private val _activeDoctor = MutableStateFlow(SeedDataProvider.initialDoctors.first())
    val activeDoctor: StateFlow<Doctor> = _activeDoctor.asStateFlow()

    private val _activeProxyPatient = MutableStateFlow<Patient?>(null)
    val activeProxyPatient: StateFlow<Patient?> = _activeProxyPatient.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    fun login(role: UserRole) {
        _currentRole.value = role
        _isLoggedIn.value = true
    }

    fun loginAsPatient(patient: Patient) {
        _activePatient.value = patient
        _currentRole.value = UserRole.PATIENT
        _isLoggedIn.value = true
    }

    fun loginAsAsha(asha: AshaWorker) {
        _activeAsha.value = asha
        _currentRole.value = UserRole.ASHA
        _isLoggedIn.value = true
    }

    fun loginAsDoctor(doctor: Doctor) {
        _activeDoctor.value = doctor
        _currentRole.value = UserRole.DOCTOR
        _isLoggedIn.value = true
    }

    fun loginAsAdmin() {
        _currentRole.value = UserRole.ADMIN
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
        _activeProxyPatient.value = null
    }

    fun switchRole(newRole: UserRole) {
        _currentRole.value = newRole
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    fun togglePresentationTheme() {
        _isPresentationLightMode.value = !_isPresentationLightMode.value
    }

    fun selectPatient(patient: Patient) {
        _activePatient.value = patient
    }

    fun selectAsha(asha: AshaWorker) {
        _activeAsha.value = asha
    }

    fun selectDoctor(doctor: Doctor) {
        _activeDoctor.value = doctor
    }

    fun setProxyPatient(patient: Patient?) {
        _activeProxyPatient.value = patient
    }

    fun clearProxy() {
        _activeProxyPatient.value = null
    }

    fun toggleOffline() {
        _isOffline.value = !_isOffline.value
    }
}
