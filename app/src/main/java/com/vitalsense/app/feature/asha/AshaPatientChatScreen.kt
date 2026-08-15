package com.vitalsense.app.feature.asha
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun AshaPatientChatScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Chat with Patient", style = MaterialTheme.typography.headlineMedium)
        Text("Messages persist locally (mocked thread)")
    }
}