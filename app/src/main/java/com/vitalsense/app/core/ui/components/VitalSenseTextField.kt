package com.vitalsense.app.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.vitalsense.app.core.ui.theme.*

@Composable
fun VitalSenseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 4,
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label, style = MaterialTheme.typography.bodyMedium, color = GlumeTextSecondary) },
            placeholder = if (placeholder != null) {
                { Text(text = placeholder, style = MaterialTheme.typography.bodyMedium, color = GlumeTextTertiary) }
            } else null,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = enabled,
            shape = InputShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GlumeSurfaceElevated,
                unfocusedContainerColor = GlumeSurfaceCard,
                disabledContainerColor = GlumeSurfaceElevated.copy(alpha = 0.4f),
                errorContainerColor = GlumeSurfaceCard,
                focusedBorderColor = GlumePrimaryPurple,
                unfocusedBorderColor = GlumeBorder,
                errorBorderColor = GlumeAlertCoral,
                focusedLabelColor = GlumePrimaryPurpleLight,
                unfocusedLabelColor = GlumeTextSecondary,
                cursorColor = GlumePrimaryPurple,
                focusedTextColor = GlumeTextPrimary,
                unfocusedTextColor = GlumeTextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = GlumeAlertCoral,
                modifier = Modifier.padding(start = Spacing.xs, top = Spacing.xxs)
            )
        }
    }
}
