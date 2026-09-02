package com.vitalsense.app.feature.bloodbank

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.BloodStockItem
import com.vitalsense.app.core.ui.components.ButtonStyle
import com.vitalsense.app.core.ui.components.VitalSenseButton
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodBankScreen(
    bloodStock: List<BloodStockItem>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedBloodGroup by remember { mutableStateOf("All") }

    val bloodGroups = listOf("All", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

    val filteredStock = remember(selectedBloodGroup, bloodStock) {
        if (selectedBloodGroup == "All") bloodStock
        else bloodStock.filter { it.bloodGroup.equals(selectedBloodGroup, ignoreCase = true) }
    }

    val totalUnits = remember(bloodStock) { bloodStock.sumOf { it.unitsAvailable } }
    val criticalCount = remember(bloodStock) { bloodStock.count { it.isCritical } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "District Blood Bank Registry",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GlumeTextPrimary
                        )
                        Text(
                            text = "Emergency Transfusion Network",
                            style = MaterialTheme.typography.labelSmall,
                            color = GlumeTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GlumeTextPrimary
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
            // 1. Hero Inventory Metrics
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    VitalSenseCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = GlumeSurfaceElevated,
                        border = BorderStroke(1.dp, GlumeBorder)
                    ) {
                        Column {
                            Text("Total Units in Network", style = MaterialTheme.typography.labelSmall, color = GlumeTextSecondary)
                            Text(
                                text = "$totalUnits Units",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = GlumePrimaryPurpleLight
                            )
                        }
                    }

                    VitalSenseCard(
                        modifier = Modifier.weight(1f),
                        backgroundColor = if (criticalCount > 0) GlumeErrorContainer.copy(alpha = 0.4f) else GlumeSurfaceElevated,
                        border = BorderStroke(1.dp, if (criticalCount > 0) GlumeError.copy(alpha = 0.5f) else GlumeBorder)
                    ) {
                        Column {
                            Text(
                                text = "Critical Shortages",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (criticalCount > 0) GlumeError else GlumeTextSecondary
                            )
                            Text(
                                text = "$criticalCount Groups Low",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (criticalCount > 0) GlumeError else GlumeSuccessText
                            )
                        }
                    }
                }
            }

            // 2. Filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(bloodGroups) { group ->
                        val isSelected = selectedBloodGroup == group
                        Surface(
                            shape = PillShape,
                            color = if (isSelected) GlumePrimaryPurple else GlumeSurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) GlumePrimaryPurple else GlumeBorder),
                            modifier = Modifier.clickable { selectedBloodGroup = group }
                        ) {
                            Text(
                                text = group,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                                color = if (isSelected) Color.White else GlumeTextPrimary,
                                modifier = Modifier.padding(horizontal = Spacing.md, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // 3. Blood Stock Cards
            items(filteredStock, key = { it.id }) { item ->
                BloodStockCard(
                    item = item,
                    onCallHospital = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.contactPhone}"))
                        context.startActivity(intent)
                    }
                )
            }

            // 4. Emergency Transfusion Protocol Note
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GlumePrimaryPurpleContainer.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, GlumePrimaryPurple.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(Spacing.sm), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            Icon(imageVector = Icons.Default.LocalHospital, contentDescription = null, tint = GlumePrimaryPurple, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Emergency Transfusion Protocol",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GlumePrimaryPurpleLight
                            )
                        }
                        Text(
                            text = "Universal Donor: O Negative (O-) · Universal Recipient: AB Positive (AB+). For maternal hemorrhages or road trauma, cross-matching is fast-tracked at District Hospital Rampur.",
                            style = MaterialTheme.typography.bodySmall,
                            color = GlumeTextPrimary
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(Spacing.xl)) }
        }
    }
}

@Composable
fun BloodStockCard(
    item: BloodStockItem,
    onCallHospital: () -> Unit,
    modifier: Modifier = Modifier
) {
    VitalSenseCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = GlumeSurfaceElevated,
        border = BorderStroke(1.dp, if (item.isCritical) GlumeError.copy(alpha = 0.6f) else GlumeBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (item.isCritical) GlumeErrorContainer else GlumePrimaryPurpleContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.bloodGroup,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (item.isCritical) GlumeError else GlumePrimaryPurple
                        )
                    )
                }

                Column {
                    Text(
                        text = "${item.unitsAvailable} Units Available",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = GlumeTextPrimary
                    )
                    Text(
                        text = item.hospitalName,
                        style = MaterialTheme.typography.labelSmall,
                        color = GlumeTextSecondary
                    )
                    Text(
                        text = "Phone: ${item.contactPhone}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GlumePrimaryPurpleLight
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Surface(
                    shape = PillShape,
                    color = when (item.status) {
                        "Critical" -> GlumeErrorContainer
                        "Low Stock" -> GlumeWarningContainer
                        else -> GlumeSuccessContainer
                    }
                ) {
                    Text(
                        text = item.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (item.status) {
                                "Critical" -> GlumeError
                                "Low Stock" -> GlumeWarningText
                                else -> GlumeSuccessText
                            }
                        ),
                        modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
                    )
                }

                IconButton(onClick = onCallHospital) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call Blood Bank",
                        tint = GlumeSuccessText
                    )
                }
            }
        }
    }
}
