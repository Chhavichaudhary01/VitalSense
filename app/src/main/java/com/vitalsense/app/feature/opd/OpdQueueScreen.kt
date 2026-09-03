package com.vitalsense.app.feature.opd

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.OpdToken
import com.vitalsense.app.core.data.model.Patient
import com.vitalsense.app.core.ui.components.ButtonStyle
import com.vitalsense.app.core.ui.components.VitalSenseButton
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.core.ui.theme.LocalAppStrings
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpdQueueScreen(
    patient: Patient,
    opdTokens: List<OpdToken>,
    onBackClick: () -> Unit,
    onBookToken: (OpdToken) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var showBookTokenDialog by remember { mutableStateOf(false) }

    val activeToken = opdTokens.firstOrNull { it.patientId == patient.id && it.status != "Completed" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = strings.opdLiveQueueAndTokens,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = strings.opdSubtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = GlumeTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.exit,
                            tint = GlumeTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showBookTokenDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = strings.bookOpdToken,
                            tint = GlumePrimaryPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GlumeBackground)
            )
        },
        containerColor = GlumeBackground,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // 1. Active Token Hero Card
            item {
                if (activeToken != null) {
                    ActiveTokenCard(token = activeToken)
                } else {
                    NoActiveTokenCard(onBookClick = { showBookTokenDialog = true })
                }
            }

            // 2. Hospital Department Live Queue Display
            item {
                Text(
                    text = "Hospital Departments Live Board",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = GlumePrimaryPurpleLight
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    DepartmentQueueRow(
                        department = "General Medicine (OPD-A)",
                        doctor = "Dr. Rajesh Kumar",
                        room = "Room 4",
                        currentServing = "A-21",
                        totalQueue = 42
                    )
                    DepartmentQueueRow(
                        department = "Maternal & Antenatal (OPD-B)",
                        doctor = "Dr. Priya (MO)",
                        room = "Room 2",
                        currentServing = "B-12",
                        totalQueue = 18
                    )
                    DepartmentQueueRow(
                        department = "Orthopedics & Trauma (OPD-C)",
                        doctor = "Dr. Ayushman Dev Singh",
                        room = "Trauma Bay 1",
                        currentServing = "C-03",
                        totalQueue = 11
                    )
                    DepartmentQueueRow(
                        department = "Pediatrics & Child Care (OPD-D)",
                        doctor = "Dr. S. K. Verma",
                        room = "Room 7",
                        currentServing = "D-09",
                        totalQueue = 24
                    )
                }
            }

            // 3. Queue History
            item {
                Text(
                    text = strings.yourActiveTokens,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = GlumePrimaryPurpleLight
                )
            }

            if (opdTokens.isEmpty()) {
                item {
                    Text(
                        text = strings.noActiveTokens,
                        style = MaterialTheme.typography.bodySmall,
                        color = GlumeTextSecondary
                    )
                }
            } else {
                items(opdTokens) { token ->
                    PastTokenCard(token = token)
                }
            }

            item { Spacer(Modifier.height(Spacing.xl)) }
        }
    }

    if (showBookTokenDialog) {
        BookOpdTokenDialog(
            patient = patient,
            onDismiss = { showBookTokenDialog = false },
            onConfirmBook = { newToken ->
                onBookToken(newToken)
                showBookTokenDialog = false
            }
        )
    }
}

@Composable
fun ActiveTokenCard(
    token: OpdToken,
    modifier: Modifier = Modifier
) {
    VitalSenseCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = GlumeSurfaceElevated,
        border = BorderStroke(2.dp, GlumePrimaryPurple)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = PillShape,
                    color = GlumePrimaryPurpleContainer
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(GlumeSuccessText)
                        )
                        Text(
                            text = "LIVE OPD QUEUE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = GlumePrimaryPurple
                            )
                        )
                    }
                }

                Surface(
                    shape = PillShape,
                    color = if (token.status == "Serving") GlumeSuccessContainer else GlumeWarningContainer
                ) {
                    Text(
                        text = token.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (token.status == "Serving") GlumeSuccessText else GlumeWarningText
                        ),
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Token Number",
                        style = MaterialTheme.typography.labelSmall,
                        color = GlumeTextSecondary
                    )
                    Text(
                        text = token.tokenNumber,
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                        color = GlumePrimaryPurpleLight
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Currently Serving",
                        style = MaterialTheme.typography.labelSmall,
                        color = GlumeTextSecondary
                    )
                    Text(
                        text = token.currentServingToken,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = GlumeSuccessText
                    )
                }
            }

            HorizontalDivider(color = GlumeBorder.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Department", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                    Text(token.department, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GlumeTextPrimary)
                }
                Column {
                    Text("Room / Cabin", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                    Text(token.cabinNumber, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GlumeTextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Est. Wait Time", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                    Text("~${token.estimatedWaitMinutes} mins", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GlumeWarningText)
                }
            }
        }
    }
}

@Composable
fun NoActiveTokenCard(
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    VitalSenseCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = GlumeSurfaceElevated,
        border = BorderStroke(1.dp, GlumeBorder)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Icon(
                imageVector = Icons.Default.ConfirmationNumber,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = GlumePrimaryPurple
            )
            Text(
                text = "No Active OPD Token",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = GlumeTextPrimary
            )
            Text(
                text = "Self check-in or generate a digital queue slip to visit PHC / District Hospital doctors without physical lines.",
                style = MaterialTheme.typography.bodySmall,
                color = GlumeTextSecondary
            )
            VitalSenseButton(
                text = "🎟️ Book OPD Token Now",
                onClick = onBookClick,
                style = ButtonStyle.PRIMARY
            )
        }
    }
}

@Composable
fun DepartmentQueueRow(
    department: String,
    doctor: String,
    room: String,
    currentServing: String,
    totalQueue: Int
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = GlumeSurfaceElevated,
        border = BorderStroke(1.dp, GlumeBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(department, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = GlumeTextPrimary)
                Text("$doctor · $room", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Serving:", style = MaterialTheme.typography.labelSmall, color = GlumeTextTertiary)
                    Text(currentServing, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GlumeSuccessText)
                }
                Text("Queue: $totalQueue", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
            }
        }
    }
}

@Composable
fun PastTokenCard(token: OpdToken) {
    VitalSenseCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = GlumeSurfaceElevated,
        border = BorderStroke(1.dp, GlumeBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${token.tokenNumber} · ${token.department}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )
                Text(
                    text = "${token.doctorName} · ${token.dateFormatted}",
                    style = MaterialTheme.typography.labelSmall,
                    color = GlumeTextSecondary
                )
            }
            Surface(shape = PillShape, color = GlumeSuccessContainer) {
                Text(
                    text = token.status,
                    style = MaterialTheme.typography.labelSmall.copy(color = GlumeSuccessText, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun BookOpdTokenDialog(
    patient: Patient,
    onDismiss: () -> Unit,
    onConfirmBook: (OpdToken) -> Unit
) {
    val strings = LocalAppStrings.current
    var selectedDept by remember { mutableStateOf("General Medicine") }
    var selectedDoctor by remember { mutableStateOf("Dr. Rajesh Varma") }

    val departments = listOf(
        "General Medicine" to "Dr. Rajesh Varma",
        "Maternal & Antenatal Care" to "Dr. Priya (MO)",
        "Orthopedics & Trauma Surgery" to "Dr. Ayushman Dev Singh",
        "Pediatrics & Child Care" to "Dr. S. K. Verma"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.bookHospitalOpdToken,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = GlumeTextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text(
                    text = strings.selectDepartment,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextSecondary
                )

                departments.forEach { (dept, doc) ->
                    val isSelected = selectedDept == dept
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) GlumePrimaryPurpleContainer else GlumeSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) GlumePrimaryPurple else GlumeBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedDept = dept
                                selectedDoctor = doc
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedDept = dept
                                    selectedDoctor = doc
                                }
                            )
                            Column {
                                Text(
                                    text = dept,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = GlumeTextPrimary
                                )
                                Text(
                                    text = "Consultant: $doc",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GlumeTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            VitalSenseButton(
                text = strings.confirmBooking,
                onClick = {
                    val tokenPrefix = when (selectedDept) {
                        "General Medicine" -> "OPD-A"
                        "Maternal & Antenatal Care" -> "OPD-B"
                        "Orthopedics & Trauma Surgery" -> "OPD-C"
                        else -> "OPD-D"
                    }
                    val tokenNum = (10..50).random()
                    val newToken = OpdToken(
                        id = "tok_${System.currentTimeMillis()}",
                        tokenNumber = "$tokenPrefix$tokenNum",
                        patientId = patient.id,
                        patientName = patient.name,
                        doctorName = selectedDoctor,
                        department = selectedDept,
                        cabinNumber = if (selectedDept.contains("Trauma")) "Trauma Bay 1" else "Room ${(1..6).random()}",
                        currentServingToken = "$tokenPrefix${maxOf(1, tokenNum - 3)}",
                        estimatedWaitMinutes = 15,
                        status = "In Queue",
                        dateFormatted = "Today"
                    )
                    onConfirmBook(newToken)
                },
                style = ButtonStyle.PRIMARY
            )
        },
        dismissButton = {
            VitalSenseButton(
                text = strings.cancel,
                onClick = onDismiss,
                style = ButtonStyle.SECONDARY
            )
        },
        containerColor = GlumeBackground
    )
}
