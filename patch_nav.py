import re

file_path = "app/src/main/java/com/vitalsense/app/feature/patient/PatientHomeScreen.kt"

with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Add states at the beginning
states = """
    var showSosConfirmation by remember { mutableStateOf(false) }
    var sosSentSuccess by remember { mutableStateOf(false) }
    var currentSubScreen by remember { mutableStateOf("home") }

    if (currentSubScreen == "prescriptions") {
        PrescriptionsListScreen(emptyList()) // mock
        return
    }
    if (currentSubScreen == "appointments") {
        AppointmentsScreen(emptyList()) {} // mock
        return
    }
    if (currentSubScreen == "doctors") {
        DoctorMapListScreen(emptyList()) // mock
        return
    }
    if (currentSubScreen == "schemes") {
        SchemesBrowserScreen(emptyList()) // mock
        return
    }
    if (currentSubScreen == "ocr") {
        PrescriptionOcrScreen() // mock
        return
    }
    if (currentSubScreen == "manual") {
        FullManualScreen() // mock
        return
    }
"""

content = content.replace("    var showSosConfirmation by remember { mutableStateOf(false) }\\n    var sosSentSuccess by remember { mutableStateOf(false) }", states)

# Replace TODO navs
content = content.replace('onClick = { /* TODO nav */ }', 'onClick = { }')
content = content.replace('VitalSenseButton("My Prescriptions", onClick = { })', 'VitalSenseButton("My Prescriptions", onClick = { currentSubScreen = "prescriptions" })')
content = content.replace('VitalSenseButton("My Appointments", onClick = { })', 'VitalSenseButton("My Appointments", onClick = { currentSubScreen = "appointments" })')
content = content.replace('VitalSenseButton("Find Doctors (Map)", onClick = { })', 'VitalSenseButton("Find Doctors (Map)", onClick = { currentSubScreen = "doctors" })')
content = content.replace('VitalSenseButton("Government Schemes", onClick = { })', 'VitalSenseButton("Government Schemes", onClick = { currentSubScreen = "schemes" })')
content = content.replace('VitalSenseButton("Upload Prescription (OCR)", onClick = { })', 'VitalSenseButton("Upload Prescription (OCR)", onClick = { currentSubScreen = "ocr" })')
content = content.replace('VitalSenseButton("Help / Manual", onClick = { })', 'VitalSenseButton("Help / Manual", onClick = { currentSubScreen = "manual" })')

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Navigation patched in PatientHomeScreen.kt")
