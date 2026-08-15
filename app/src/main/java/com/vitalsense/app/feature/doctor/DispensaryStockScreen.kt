package com.vitalsense.app.feature.doctor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.DispensaryItem
import com.vitalsense.app.core.ui.components.VitalSenseCard
@Composable
fun DispensaryStockScreen(stock: List<DispensaryItem>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Dispensary Stock", style = MaterialTheme.typography.headlineMedium)
        LazyColumn {
            items(stock) { item ->
                VitalSenseCard {
                    Column {
                        Text(item.medicineName, style = MaterialTheme.typography.titleMedium)
                        Text("Available: ${item.availableQuantity}")
                        if (item.isLowStock) Text("LOW STOCK ALERT", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}