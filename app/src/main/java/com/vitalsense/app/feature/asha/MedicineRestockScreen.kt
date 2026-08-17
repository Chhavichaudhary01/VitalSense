package com.vitalsense.app.feature.asha

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.AshaMedicine
import com.vitalsense.app.core.ui.components.VitalSenseCard
import com.vitalsense.app.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineRestockScreen(
    medicines: List<AshaMedicine>,
    onBackClick: () -> Unit,
    onRequestRestock: (AshaMedicine) -> Unit = {}
) {
    var restockSuccessMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Restock Tracker") },
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
                    text = "ASHA Field Kit Stock & Indent",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = GlumeTextPrimary
                )
            }

            if (restockSuccessMsg != null) {
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
                                text = restockSuccessMsg ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = GlumeSuccessMint
                            )
                            IconButton(onClick = { restockSuccessMsg = null }, modifier = Modifier.size(24.dp)) {
                                Text("✕", color = GlumeSuccessMint, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            if (medicines.isEmpty()) {
                item {
                    Text(
                        text = "No medicines found in kit.",
                        color = GlumeTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(medicines) { medicine ->
                    val isOutOfStock = medicine.availableQuantity <= 0
                    val isLowStock = medicine.availableQuantity <= medicine.minStockQuantity && !isOutOfStock

                    val statusColor = when {
                        isOutOfStock -> GlumeAlertCoral
                        isLowStock -> GlumeWarningAmber
                        else -> GlumeSuccessMint
                    }
                    val statusText = when {
                        isOutOfStock -> "Out of Stock"
                        isLowStock -> "Low Stock"
                        else -> "In Stock"
                    }

                    VitalSenseCard(
                        backgroundColor = if (isOutOfStock) GlumeAlertContainer else GlumeSurfaceCard,
                        border = BorderStroke(1.dp, if (isOutOfStock) GlumeAlertCoral.copy(alpha = 0.4f) else GlumeBorder)
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
                                    text = medicine.medicineName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GlumeTextPrimary
                                )
                                Surface(shape = CircleShape, color = statusColor.copy(alpha = 0.2f)) {
                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            
                            Text(
                                text = "Quantity: ${medicine.availableQuantity} ${medicine.unit} (Min: ${medicine.minStockQuantity})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GlumeTextPrimary
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Expiry: ${medicine.expiryDateFormatted}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlumeTextSecondary
                                )
                                Text(
                                    text = "Last Restock: ${medicine.lastRestockDateFormatted}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlumeTextSecondary
                                )
                            }

                            if (isLowStock || isOutOfStock) {
                                HorizontalDivider(color = GlumeBorder, modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Kit refill needed from PHC dispensary",
                                        style = MaterialTheme.typography.bodySmall.copy(color = GlumeAlertCoral, fontSize = 11.sp)
                                    )
                                    Button(
                                        onClick = {
                                            val updated = medicine.copy(
                                                availableQuantity = medicine.availableQuantity + 50,
                                                lastRestockDateFormatted = "Today"
                                            )
                                            onRequestRestock(updated)
                                            restockSuccessMsg = "✓ Indent submitted for 50 ${medicine.unit} of ${medicine.medicineName}!"
                                        },
                                        shape = PillShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Request Refill (+50)", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
