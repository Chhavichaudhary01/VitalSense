package com.vitalsense.app.core.ui.util

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSizeCategory {
    COMPACT, // < 600dp (standard portrait phones)
    MEDIUM,  // 600dp .. 840dp (foldables, small tablets, landscape phones)
    EXPANDED // > 840dp (large tablets, desktop, chromebooks)
}

/**
 * Adaptive container that provides:
 * 1. Resolution awareness (Compact, Medium, Expanded).
 * 2. Optimal max-width bounds on wide screens (centered with max 960dp) so UI never over-stretches.
 * 3. Consistent horizontal and vertical gutters.
 */
@Composable
fun AdaptiveScreenContainer(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 960.dp,
    contentAlignment: Alignment = Alignment.TopCenter,
    content: @Composable (windowCategory: WindowSizeCategory) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = contentAlignment
    ) {
        val windowCategory = when {
            this.maxWidth < 600.dp -> WindowSizeCategory.COMPACT
            this.maxWidth < 840.dp -> WindowSizeCategory.MEDIUM
            else -> WindowSizeCategory.EXPANDED
        }

        Box(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxSize()
        ) {
            content(windowCategory)
        }
    }
}

/**
 * Calculates adaptive column counts for Bento grids and metric cards.
 */
fun adaptiveGridColumns(category: WindowSizeCategory): Int {
    return when (category) {
        WindowSizeCategory.COMPACT -> 2
        WindowSizeCategory.MEDIUM -> 3
        WindowSizeCategory.EXPANDED -> 4
    }
}
