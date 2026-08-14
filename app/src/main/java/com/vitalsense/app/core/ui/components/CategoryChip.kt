package com.vitalsense.app.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.ConditionCategory
import com.vitalsense.app.core.ui.theme.DarkCharcoal
import com.vitalsense.app.core.ui.theme.PillShape
import com.vitalsense.app.core.ui.theme.TextPrimaryNearBlack

@Composable
fun CategoryChip(
    category: ConditionCategory,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val chipBaseColor = Color(category.colorHex)
    val backgroundColor = if (isSelected) DarkCharcoal else chipBaseColor.copy(alpha = 0.5f)
    val contentColor = if (isSelected) Color.White else TextPrimaryNearBlack

    Surface(
        shape = PillShape,
        color = backgroundColor,
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = category.emoji, fontSize = 14.sp)
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = contentColor
            )
        }
    }
}
