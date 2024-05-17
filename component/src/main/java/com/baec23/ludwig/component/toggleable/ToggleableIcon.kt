package com.baec23.ludwig.component.toggleable

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.baec23.ludwig.core.autoResizingText.AutoResizingText

@Composable
private fun Toggleable(
    modifier: Modifier = Modifier,
    isToggled: Boolean,
    onToggle: () -> Unit,
    toggledOnColor: Color = MaterialTheme.colorScheme.primary,
    toggledOffColor: Color = Color.LightGray,
    blendMode: BlendMode = BlendMode.Screen,
    toggleOnScale: Float = 1f,
    toggledOffScale: Float = 0.75f,
    content: @Composable BoxScope.() -> Unit
) {
    val isToggledTransition =
        updateTransition(targetState = isToggled, label = "isToggledTransition")
    val animatedColor by isToggledTransition.animateColor(label = "animatedColor") { if (it) toggledOnColor else toggledOffColor }
    val animatedScale by isToggledTransition.animateFloat(
        label = "animatedScale",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        }) { if (it) toggleOnScale else toggledOffScale }

    Box(modifier = modifier
        .scale(animatedScale)
        .drawWithContent {
            drawContent()
            drawRect(size = size, color = animatedColor, blendMode = blendMode)
        }
        .clickable(interactionSource = MutableInteractionSource(), indication = null) {
            onToggle()
        }
    ) {
        content()
    }
}

@Composable
fun ToggleableIcon(
    modifier: Modifier = Modifier,
    isToggled: Boolean,
    imageVector: ImageVector,
    iconSize: DpSize = DpSize(50.dp, 50.dp),
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    contentDescription: String? = null,
    label: String? = null,
    toggledOnColor: Color = MaterialTheme.colorScheme.primary,
    toggledOffColor: Color = Color.LightGray,
    toggledOnScale: Float = 1f,
    toggledOffScale: Float = 0.75f,
    onToggle: () -> Unit,
) {
    val isToggledTransition =
        updateTransition(targetState = isToggled, label = "isToggledTransition")
    val animatedColor by isToggledTransition.animateColor(label = "colorAnimation") { if (it) toggledOnColor else toggledOffColor }
    val animatedScale by isToggledTransition.animateFloat(
        label = "scaleAnimation",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        }) { if (it) toggledOnScale else toggledOffScale }

    Box(
        modifier = modifier
            .clickable(interactionSource = interactionSource, indication = null) { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .weight(2f, fill = false)
                    .size(iconSize)
                    .scale(animatedScale),
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = animatedColor
            )
            label?.let {
                AutoResizingText(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .scale(animatedScale),
                    text = label,
                    color = animatedColor,
                    overflow = TextOverflow.Clip,
                    maxLines = 1,
                    minLines = 1,
                )
            }
        }
    }
}
