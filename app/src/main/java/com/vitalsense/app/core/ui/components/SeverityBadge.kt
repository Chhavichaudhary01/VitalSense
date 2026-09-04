package com.vitalsense.app.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vitalsense.app.core.data.model.SeverityLevel

@Composable
fun SeverityBadge(
    severity: SeverityLevel,
    modifier: Modifier = Modifier
) {
    val type = when (severity) {
        SeverityLevel.LOW -> VSPillType.Success
        SeverityLevel.MODERATE -> VSPillType.Warning
        SeverityLevel.HIGH -> VSPillType.Error
        SeverityLevel.SEVERE -> VSPillType.Error
    }
    VSStatusPill(
        text = severity.displayName,
        type = type,
        modifier = modifier
    )
}
