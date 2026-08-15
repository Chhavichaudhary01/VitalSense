package com.vitalsense.app.feature.admin
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.ui.components.VitalSenseButton
@Composable
fun AdminBroadcastScreen(onSend: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("System Broadcast", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = msg, onValueChange = { msg = it }, label = { Text("Message") })
        VitalSenseButton("Send Broadcast", onClick = { onSend(title, msg) })
    }
}