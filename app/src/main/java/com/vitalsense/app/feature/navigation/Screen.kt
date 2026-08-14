package com.vitalsense.app.feature.navigation

sealed class Screen(val route: String) {
    object PatientHome : Screen("patient_home")
    object AshaHome : Screen("asha_home")
    object DoctorHome : Screen("doctor_home")
    object AdminHome : Screen("admin_home")

    // Sub-screens for Persons 2, 3, 4, 5 to plug into
    object HealthCard : Screen("patient_health_card")
    object LogCondition : Screen("patient_log_condition")
    object Prescriptions : Screen("patient_prescriptions")
    object MentalWellness : Screen("patient_mental_wellness")
    object NearbyMap : Screen("patient_nearby_map")
    object GovernmentSchemes : Screen("patient_government_schemes")
    object AshaCaseload : Screen("asha_caseload")
    object DoctorCaseReview : Screen("doctor_case_review")
    object AdminHeatMap : Screen("admin_heat_map")
}
