package com.vitalsense.app.feature.asha

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.DailyRound
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*
import com.vitalsense.app.feature.asha.components.LogDailyRoundDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyRoundsScreen(
    rounds: List<DailyRound>,
    onBackClick: () -> Unit,
    onSaveRound: (DailyRound) -> Unit = {}
) {
    var showLogRoundDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Village Rounds") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GlumeSurfaceElevated,
                    titleContentColor = GlumeTextPrimary,
                    navigationIconContentColor = GlumeTextPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showLogRoundDialog = true },
                containerColor = GlumePrimaryPurple,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Log Round")
                    Text("Log Visit", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = GlumeBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xxl)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Village Rounds & Door-to-Door Visits",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = GlumeTextPrimary
                    )
                }
            }

            if (successMessage != null) {
                item {
                    Surface(
                        shape = PillShape,
                        color = GlumeSuccessContainer,
                        border = BorderStroke(1.dp, GlumeSuccessMint),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = successMessage ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = GlumeSuccessMint
                            )
                            IconButton(onClick = { successMessage = null }, modifier = Modifier.size(24.dp)) {
                                Text("✕", color = GlumeSuccessMint, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            if (rounds.isEmpty()) {
                item {
                    Text(
                        text = "No village rounds logged yet. Tap '+ Log Visit' to record door-to-door checkups.",
                        color = GlumeTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(rounds) { round ->
                    VitalSenseCard(
                        backgroundColor = GlumeSurfaceCard,
                        border = BorderStroke(1.dp, GlumeBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.sm),
                            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = round.householdName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GlumeTextPrimary
                                )
                                Text(
                                    text = round.dateFormatted,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlumeTextSecondary
                                )
                            }
                            Text(
                                text = "Person: ${round.personName} | Village: ${round.villageName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GlumeTextSecondary
                            )
                            
                            HorizontalDivider(color = GlumeBorder, modifier = Modifier.padding(vertical = 4.dp))
                            
                            Text(
                                text = "Purpose: ${round.purpose}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = GlumePrimaryPurpleLight
                            )
                            Text(
                                text = "Notes: ${round.notes}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GlumeTextPrimary
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (round.isPregnancyChecked) {
                                    Surface(shape = PillShape, color = GlumePrimaryPurpleContainer) {
                                        Text("🤰 Maternal", fontSize = 10.sp, color = GlumePrimaryPurpleLight, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                if (round.isChildHealthChecked) {
                                    Surface(shape = PillShape, color = GlumeSuccessContainer) {
                                        Text("👶 Child", fontSize = 10.sp, color = GlumeSuccessMint, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                if (round.isImmunizationChecked) {
                                    Surface(shape = PillShape, color = GlumeWarningContainer) {
                                        Text("💉 Vaccine", fontSize = 10.sp, color = GlumeWarningAmber, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLogRoundDialog) {
        LogDailyRoundDialog(
            ashaWorkerId = rounds.firstOrNull()?.ashaWorkerId ?: "asha_priya",
            onDismiss = { showLogRoundDialog = false },
            onSaveRound = { newRound ->
                onSaveRound(newRound)
                showLogRoundDialog = false
                successMessage = "✓ Visit for ${newRound.householdName} saved!"
            }
        )
    }
}
