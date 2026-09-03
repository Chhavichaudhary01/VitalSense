package com.vitalsense.app.core.ui.theme

/**
 * Supported application languages.
 * Display name and native script name are provided for high-accessibility selection.
 */
enum class AppLanguage(val code: String, val displayName: String, val nativeName: String, val buttonLabel: String) {
    ENGLISH("en", "English", "English", "EN"),
    HINDI("hi", "Hindi", "हिन्दी", "हिन्दी"),
    TAMIL("ta", "Tamil", "தமிழ்", "தமிழ்"),
    MARATHI("mr", "Marathi", "मराठी", "मराठी")
}
