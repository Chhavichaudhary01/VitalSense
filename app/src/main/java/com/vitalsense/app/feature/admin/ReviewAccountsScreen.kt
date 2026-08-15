package com.vitalsense.app.feature.admin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.Doctor
import com.vitalsense.app.core.data.model.AshaWorker
import com.vitalsense.app.core.ui.components.VitalSenseCard
@Composable
fun ReviewAccountsScreen(doctors: List<Doctor>, ashas: List<AshaWorker>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Review Accounts", style = MaterialTheme.typography.headlineMedium)
        LazyColumn {
            item { Text("Doctors") }
            items(doctors) { d ->
                VitalSenseCard { Text("${d.name} (${d.specialty.name})") }
            }
            item { Text("ASHAs") }
            items(ashas) { a ->
                VitalSenseCard { Text("${a.name} (${a.assignedVillages.joinToString()})") }
            }
        }
    }
}