import os

os.makedirs('app/src/main/java/com/vitalsense/app/core/ui/util', exist_ok=True)

touch_spring_code = '''package com.vitalsense.app.core.ui.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale

/**
 * NagarSeva Touch Physics & Micro-Interactions:
 * Tactile scale-down spring feedback on all interactive cards, buttons, and chips.
 */
fun Modifier.touchSpring(
    pressedScale: Float = 0.96f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = 0.65f,
            stiffness = 500f
        ),
        label = "TouchSpringScale"
    )

    this
        .scale(scale)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )
}
'''

with open('app/src/main/java/com/vitalsense/app/core/ui/util/TouchSpringUtils.kt', 'w', encoding='utf-8') as f:
    f.write(touch_spring_code)

print('Wrote TouchSpringUtils.kt')
