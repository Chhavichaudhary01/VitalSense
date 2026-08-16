package com.vitalsense.app.feature.admin.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalsense.app.core.data.model.SeverityLevel
import com.vitalsense.app.core.data.model.Village
import com.vitalsense.app.core.ui.components.SeverityBadge
import com.vitalsense.app.core.ui.theme.*
import kotlin.math.max

enum class MapLayerType {
    STANDARD,
    SATELLITE,
    DARK
}

@Composable
fun DistrictOutbreakMapView(
    villages: List<Village>,
    selectedVillage: Village?,
    onSelectVillage: (Village) -> Unit,
    onBroadcastToVillage: (Village) -> Unit,
    modifier: Modifier = Modifier
) {
    var mapLayer by remember { mutableStateOf(MapLayerType.STANDARD) }
    var zoomLevel by remember { mutableFloatStateOf(1f) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(BorderStroke(1.dp, GlumeBorder), CardShape),
        color = when (mapLayer) {
            MapLayerType.STANDARD -> Color(0xFFF4F3F0)
            MapLayerType.SATELLITE -> Color(0xFF1E281E)
            MapLayerType.DARK -> Color(0xFF12141C)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // 1. Google Map Realistic Terrain Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            val width = size.width.toFloat()
                            val height = size.height.toFloat()

                            // Find tapped village node
                            val tappedVillage = villages.minByOrNull { village ->
                                val (vx, vy) = getVillageScreenCoordinates(village, width, height, zoomLevel)
                                val distSq = (tapOffset.x - vx) * (tapOffset.x - vx) + (tapOffset.y - vy) * (tapOffset.y - vy)
                                distSq
                            }

                            if (tappedVillage != null) {
                                val (vx, vy) = getVillageScreenCoordinates(tappedVillage, width, height, zoomLevel)
                                val dist = kotlin.math.sqrt((tapOffset.x - vx) * (tapOffset.x - vx) + (tapOffset.y - vy) * (tapOffset.y - vy))
                                if (dist < 40f * zoomLevel) {
                                    onSelectVillage(tappedVillage)
                                }
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // A. Base Ground & Terrain Areas
                when (mapLayer) {
                    MapLayerType.STANDARD -> {
                        // Standard Google Maps Land Color
                        drawRect(Color(0xFFF3F1EC))

                        // Green Parks & Forest Land (Google Maps Green #D2EBD2)
                        val greenPath1 = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(w * 0.4f, 0f)
                            cubicTo(w * 0.35f, h * 0.25f, w * 0.15f, h * 0.35f, 0f, h * 0.3f)
                            close()
                        }
                        drawPath(greenPath1, Color(0xFFD6EAD8))

                        val greenPath2 = Path().apply {
                            moveTo(w * 0.7f, h)
                            lineTo(w, h * 0.65f)
                            lineTo(w, h)
                            close()
                        }
                        drawPath(greenPath2, Color(0xFFCCE6CE))

                        // Ramganga River Stream (Google Maps Blue #AAD3DF)
                        val riverPath = Path().apply {
                            moveTo(0f, h * 0.78f)
                            cubicTo(w * 0.28f, h * 0.65f, w * 0.45f, h * 0.85f, w * 0.72f, h * 0.45f)
                            cubicTo(w * 0.85f, h * 0.25f, w * 0.95f, h * 0.30f, w, h * 0.15f)
                        }
                        drawPath(riverPath, Color(0xFFAAD3DF), style = Stroke(width = 24f * zoomLevel))

                        // Secondary Road Network (Google Maps White/Gray Streets)
                        for (i in 1..4) {
                            drawLine(
                                color = Color(0xFFFFFFFF),
                                start = Offset(0f, h * (i / 5f)),
                                end = Offset(w, h * (i / 5f) + 15f),
                                strokeWidth = 3f
                            )
                            drawLine(
                                color = Color(0xFFFFFFFF),
                                start = Offset(w * (i / 5f), 0f),
                                end = Offset(w * (i / 5f) - 20f, h),
                                strokeWidth = 3f
                            )
                        }

                        // Primary National Highway NH-24 (Google Maps Orange-Yellow Highway #FCD475)
                        val highwayPath = Path().apply {
                            moveTo(0f, h * 0.35f)
                            lineTo(w * 0.48f, h * 0.45f)
                            lineTo(w, h * 0.55f)
                        }
                        // Highway Casing (Orange Border)
                        drawPath(highwayPath, Color(0xFFF9B858), style = Stroke(width = 10f * zoomLevel))
                        // Highway Fill (Yellow Highway)
                        drawPath(highwayPath, Color(0xFFFFDF88), style = Stroke(width = 7f * zoomLevel))

                        // State Highway SH-43
                        val stateHwyPath = Path().apply {
                            moveTo(w * 0.35f, 0f)
                            lineTo(w * 0.48f, h * 0.45f)
                            lineTo(w * 0.62f, h)
                        }
                        drawPath(stateHwyPath, Color(0xFFFBD78D), style = Stroke(width = 6f * zoomLevel))
                    }

                    MapLayerType.SATELLITE -> {
                        // Satellite Earth Surface
                        drawRect(Color(0xFF1F2B1D))

                        // Agricultural crop patch variations
                        drawRect(Color(0xFF283626), topLeft = Offset(0f, 0f), size = Size(w * 0.45f, h * 0.4f))
                        drawRect(Color(0xFF334330), topLeft = Offset(w * 0.55f, h * 0.55f), size = Size(w * 0.45f, h * 0.45f))

                        // River Stream in Satellite (Dark Navy Water #15222E)
                        val riverPath = Path().apply {
                            moveTo(0f, h * 0.78f)
                            cubicTo(w * 0.28f, h * 0.65f, w * 0.45f, h * 0.85f, w * 0.72f, h * 0.45f)
                            cubicTo(w * 0.85f, h * 0.25f, w * 0.95f, h * 0.30f, w, h * 0.15f)
                        }
                        drawPath(riverPath, Color(0xFF1B3245), style = Stroke(width = 22f * zoomLevel))

                        // Highways in Satellite (Clean White Lines with Glow)
                        val highwayPath = Path().apply {
                            moveTo(0f, h * 0.35f)
                            lineTo(w * 0.48f, h * 0.45f)
                            lineTo(w, h * 0.55f)
                        }
                        drawPath(highwayPath, Color(0xCCFFFFFF), style = Stroke(width = 4f * zoomLevel))
                    }

                    MapLayerType.DARK -> {
                        // Google Maps Dark Night Mode (#1B1D28)
                        drawRect(Color(0xFF141620))

                        // Dark River (#0F2133)
                        val riverPath = Path().apply {
                            moveTo(0f, h * 0.78f)
                            cubicTo(w * 0.28f, h * 0.65f, w * 0.45f, h * 0.85f, w * 0.72f, h * 0.45f)
                            cubicTo(w * 0.85f, h * 0.25f, w * 0.95f, h * 0.30f, w, h * 0.15f)
                        }
                        drawPath(riverPath, Color(0xFF0F263B), style = Stroke(width = 22f * zoomLevel))

                        // Roads in Dark Mode
                        val highwayPath = Path().apply {
                            moveTo(0f, h * 0.35f)
                            lineTo(w * 0.48f, h * 0.45f)
                            lineTo(w, h * 0.55f)
                        }
                        drawPath(highwayPath, Color(0xFF2C2F42), style = Stroke(width = 6f * zoomLevel))
                    }
                }

                // B. Village Outbreak Heat Radius & Google Maps Location Markers
                villages.forEach { village ->
                    val (vx, vy) = getVillageScreenCoordinates(village, w, h, zoomLevel)
                    val isSelected = selectedVillage?.id == village.id

                    val pinColor = when {
                        village.highRiskCount > 0 -> Color(0xFFEA4335) // Google Maps Red
                        village.activeCases > 5 -> Color(0xFFFBBC04) // Google Maps Yellow/Amber
                        else -> Color(0xFF34A853) // Google Maps Green
                    }

                    val heatRadius = (max(village.activeCases, 3) * 5.5f * zoomLevel).coerceIn(24f, 75f)

                    // 1. Heat Radius Circle Overlay
                    drawCircle(
                        color = pinColor.copy(alpha = if (isSelected) 0.35f else 0.18f),
                        radius = heatRadius,
                        center = Offset(vx, vy)
                    )
                    drawCircle(
                        color = pinColor.copy(alpha = if (isSelected) 0.8f else 0.4f),
                        radius = heatRadius,
                        center = Offset(vx, vy),
                        style = Stroke(width = if (isSelected) 2.5f else 1.2f)
                    )

                    // 2. Google Maps Teardrop Pin Marker
                    val pinSize = if (isSelected) 30f else 22f

                    // Shadow underneath pin
                    drawCircle(
                        color = Color(0x44000000),
                        radius = pinSize * 0.45f,
                        center = Offset(vx, vy + pinSize * 0.2f)
                    )

                    // Pin Head
                    drawCircle(
                        color = pinColor,
                        radius = pinSize * 0.7f,
                        center = Offset(vx, vy - pinSize * 0.6f)
                    )
                    // Pin Inner White Eye
                    drawCircle(
                        color = Color.White,
                        radius = pinSize * 0.28f,
                        center = Offset(vx, vy - pinSize * 0.6f)
                    )
                    // Pin Tip Arrow
                    val tipPath = Path().apply {
                        moveTo(vx - pinSize * 0.4f, vy - pinSize * 0.45f)
                        lineTo(vx + pinSize * 0.4f, vy - pinSize * 0.45f)
                        lineTo(vx, vy)
                        close()
                    }
                    drawPath(tipPath, pinColor)
                }
            }

            // 2. Floating Marker Label Overlay Chips (HTML/Compose layer for crisp text)
            villages.forEach { village ->
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val (vx, vy) = getVillageScreenCoordinates(village, maxWidth.value, maxHeight.value, zoomLevel)
                    val isSelected = selectedVillage?.id == village.id

                    val pinColor = when {
                        village.highRiskCount > 0 -> GlumeAlertCoral
                        village.activeCases > 5 -> GlumeWarningAmber
                        else -> GlumeSuccessMint
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = (vx - 45).dp, y = (vy - 48).dp)
                            .clickable { onSelectVillage(village) }
                    ) {
                        Surface(
                            shape = PillShape,
                            color = if (isSelected) GlumePrimaryPurple else Color.White,
                            shadowElevation = 4.dp,
                            border = BorderStroke(1.dp, if (isSelected) Color.White else pinColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(pinColor)
                                )
                                Text(
                                    text = "${village.name} (${village.activeCases})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF1E293B)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 3. Top-Right Map Controls (Layer Switcher & Zoom)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
            ) {
                // Layer Selector Pill
                Surface(
                    shape = PillShape,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        listOf(
                            MapLayerType.STANDARD to "🗺️ Map",
                            MapLayerType.SATELLITE to "🛰️ Satellite",
                            MapLayerType.DARK to "🌙 Night"
                        ).forEach { (type, title) ->
                            val active = mapLayer == type
                            Box(
                                modifier = Modifier
                                    .clip(PillShape)
                                    .background(if (active) GlumePrimaryPurple else Color.Transparent)
                                    .clickable { mapLayer = type }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                        color = if (active) Color.White else Color(0xFF475569)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 4. Floating Zoom Controls on Middle-Right
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { zoomLevel = (zoomLevel + 0.25f).coerceAtMost(1.8f) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF334155))
                    }
                }
                Surface(
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { zoomLevel = (zoomLevel - 0.25f).coerceAtLeast(0.8f) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("−", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF334155))
                    }
                }
            }

            // 5. Google Maps Bottom Watermark & Scale Bar
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White.copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "G",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF4285F4)
                        )
                        Text(
                            text = "o",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFFEA4335)
                        )
                        Text(
                            text = "o",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFFFBBC04)
                        )
                        Text(
                            text = "g",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF4285F4)
                        )
                        Text(
                            text = "l",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF34A853)
                        )
                        Text(
                            text = "e",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFFEA4335)
                        )
                        Text(
                            text = " Maps",
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Text(
                    text = "2 km ───┤",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (mapLayer == MapLayerType.STANDARD) Color(0xFF475569) else Color(0xFF94A3B8)
                )
            }
        }

        // 6. Interactive Google Map Info Card (When a Village Marker is Selected)
        AnimatedVisibility(
            visible = selectedVillage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (selectedVillage != null) {
                Surface(
                    color = GlumeSurfaceElevated,
                    border = BorderStroke(1.dp, GlumePrimaryPurple.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "📍 ${selectedVillage.name} (${selectedVillage.district})",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GlumeTextPrimary
                                )
                                Text(
                                    text = "Population: ${selectedVillage.population} · Active Cases: ${selectedVillage.activeCases} · Critical: ${selectedVillage.highRiskCount}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GlumeTextSecondary
                                )
                            }
                            SeverityBadge(
                                severity = if (selectedVillage.highRiskCount > 2) SeverityLevel.SEVERE else if (selectedVillage.highRiskCount > 0) SeverityLevel.HIGH else if (selectedVillage.activeCases > 5) SeverityLevel.MODERATE else SeverityLevel.LOW
                            )
                        }

                        Button(
                            onClick = { onBroadcastToVillage(selectedVillage) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp),
                            shape = PillShape,
                            colors = ButtonDefaults.buttonColors(containerColor = GlumePrimaryPurple)
                        ) {
                            Text(
                                text = "📢 Send Advisory to ${selectedVillage.name} Residents",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getVillageScreenCoordinates(village: Village, width: Float, height: Float, zoom: Float): Pair<Float, Float> {
    val centerX = width / 2f
    val centerY = height / 2f

    val (baseXFraction, baseYFraction) = when (village.name.lowercase()) {
        "sundarpura" -> 0.28f to 0.42f
        "kalyanpur" -> 0.72f to 0.38f
        "bhimnagar" -> 0.48f to 0.75f
        else -> 0.50f to 0.50f
    }

    val unzoomedX = width * baseXFraction
    val unzoomedY = height * baseYFraction

    val zoomedX = centerX + (unzoomedX - centerX) * zoom
    val zoomedY = centerY + (unzoomedY - centerY) * zoom

    return zoomedX to zoomedY
}
