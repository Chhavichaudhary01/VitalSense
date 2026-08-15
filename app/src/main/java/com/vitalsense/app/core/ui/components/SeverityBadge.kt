package com.vitalsense.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.data.model.SeverityLevel
import com.vitalsense.app.core.ui.theme.*

@Composable
fun SeverityBadge(
    severity: SeverityLevel,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (severity) {
        SeverityLevel.LOW -> GlumeSuccessContainer to GlumeSuccessText
        SeverityLevel.MODERATE -> GlumeWarningContainer to GlumeWarningAmber
        SeverityLevel.HIGH -> GlumeAlertContainer to GlumeAlertCoral
        SeverityLevel.SEVERE -> GlumeAlertCoral.copy(alpha = 0.35f) to GlumeAlertText
    }

    Surface(
        shape = PillShape,
        color = backgroundColor,
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Text(
            text = severity.displayName,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor,
            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 2.dp)
        )
    }
}
