package com.vitalsense.app.feature.asha

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.DailyRound
import com.vitalsense.app.core.ui.components.VitalSenseCard

import com.vitalsense.app.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyRoundsScreen(
    rounds: List<DailyRound>,
    onBackClick: () -> Unit
) {
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
                Text(
                    text = "Village Rounds & Door-to-Door Visits",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )
            }

            if (rounds.isEmpty()) {
                item {
                    Text(
                        text = "No rounds found.",
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
                            Text(
                                text = "Purpose: ${round.purpose}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = GlumePrimaryPurpleLight
                            )

                            HorizontalDivider(color = GlumeBorder, thickness = 1.dp)

                            Text(
                                text = "Checklist",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumeTextPrimary
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (round.isPregnancyChecked) ChecklistItem("Pregnancy Checked")
                                if (round.isChildHealthChecked) ChecklistItem("Child Health Checked")
                                if (round.isImmunizationChecked) ChecklistItem("Immunization Checked")
                                if (round.isMedicineGiven) ChecklistItem("Medicine Given")
                                if (round.isCounsellingDone) ChecklistItem("Counselling Done")
                            }

                            if (round.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notes: ${round.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlumeTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            
                            val statusColor = when (round.status) {
                                "Completed" -> GlumeSuccessMint
                                "Pending" -> GlumeAlertCoral
                                else -> GlumeTextSecondary
                            }
                            Text(
                                text = "Status: ${round.status}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = statusColor,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChecklistItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Text(text = "✓", color = GlumeSuccessMint, style = MaterialTheme.typography.bodyMedium)
        Text(text = text, color = GlumeTextPrimary, style = MaterialTheme.typography.bodyMedium)
    }
}
