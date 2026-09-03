package com.vitalsense.app.feature.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*

data class DiagnosticService(
    val name: String,
    val status: String, // "Available", "Maintenance", "Limited"
    val waitingTime: String
)

val mockDiagnostics = listOf(
    DiagnosticService("Digital X-Ray", "Available", "~15 mins"),
    DiagnosticService("MRI Scanner", "Maintenance", "N/A"),
    DiagnosticService("Pathology Lab (Blood)", "Available", "~30 mins"),
    DiagnosticService("Ultrasound", "Limited", "~2 hours (Staff Shortage)"),
    DiagnosticService("ECG", "Available", "~10 mins")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDiagnosticsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnostics Availability", color = GlumeTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = GlumeTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlumeBackground
                )
            )
        },
        containerColor = GlumeBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Spacing.md)
        ) {
            Text(
                text = "Live Machine & Lab Status",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = GlumeTextPrimary,
                modifier = Modifier.padding(bottom = Spacing.sm, top = Spacing.sm)
            )
            
            Text(
                text = "Monitor the real-time operational status of all facility diagnostic machines and laboratories.",
                style = MaterialTheme.typography.bodySmall,
                color = GlumeTextSecondary,
                modifier = Modifier.padding(bottom = Spacing.md)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                contentPadding = PaddingValues(bottom = Spacing.xxl)
            ) {
                items(mockDiagnostics) { service ->
                    DiagnosticServiceCard(service)
                }
            }
        }
    }
}

@Composable
fun DiagnosticServiceCard(service: DiagnosticService) {
    val statusColor = when (service.status) {
        "Available" -> GlumeSuccessMint
        "Maintenance" -> GlumeAlertCoral
        else -> GlumeWarningAmber
    }

    VitalSenseCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )
                Text(
                    text = "Wait Time: ${service.waitingTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GlumeTextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Surface(
                shape = PillShape,
                color = statusColor.copy(alpha = 0.15f),
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = service.status.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(color = statusColor, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = Spacing.sm, vertical = 4.dp)
                )
            }
        }
    }
}
