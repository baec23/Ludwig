package com.baec23.ludwig.ui

import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInOutElastic
import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.baec23.ludwig.component.section.DisplaySection
import com.baec23.ludwig.component.section.ExpandableDisplaySection
import com.baec23.ludwig.morpher.component.AnimatedFillVector
import com.baec23.ludwig.morpher.component.AnimatedVector
import com.baec23.ludwig.morpher.component.DebugSubpathVectorImage
import com.baec23.ludwig.morpher.component.VectorImage
import com.baec23.ludwig.morpher.model.morpher.VectorSource
import com.baec23.ludwig.morpher.model.path.LudwigSubpath
import com.baec23.ludwig.morpher.model.path.PathSegment
import kotlin.random.Random

@Composable
fun PathExplorerScreen() {
    val testStringVectorSource = VectorSource.fromText("a")
    val testStringVectorSource2 = VectorSource.fromText("b")

    val targetVectors: List<VectorSource> = listOf(
        testStringVectorSource,
        testStringVectorSource2
    )

    var currSelectedSource by remember { mutableStateOf(testStringVectorSource) }
    var isControlsExpanded by remember { mutableStateOf(false) }
    var isDetailsExpanded by remember { mutableStateOf(false) }
//    val numColors = currSelectedSource.ludwigPath.subpaths.flatMap { it.pathSegments }.size
    val colors =
        remember(currSelectedSource) { generateRandomColors(currSelectedSource.ludwigPath.subpaths.size) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedVector(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(36.dp),
                vectorSource = currSelectedSource,
                strokeWidth = 30f,
                animationSpec = tween(durationMillis = 1000, easing = EaseInOutExpo)
//                animationSpec = tween(durationMillis = 800, easing = EaseInOutExpo)
            )
//            DebugSubpathVectorImage(
//                modifier = Modifier
//                    .weight(1f)
//                    .aspectRatio(1f)
//                    .padding(36.dp),
//                source = currSelectedSource,
//                colors = colors
//            )
//            DebugVectorImage(
//                modifier = Modifier.fillMaxWidth(),
//                source = currSelectedSource,
//                colors = colors
//            )
        }
        ExpandableDisplaySection(
            modifier = Modifier.padding(horizontal = 8.dp),
            isExpanded = isDetailsExpanded,
            onExpand = { isDetailsExpanded = !isDetailsExpanded },
            headerText = "Details",
            contentPadding = PaddingValues(16.dp),
            headerIcon = Icons.Default.Star
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
//                    .verticalScroll(rememberScrollState())
            ) {
                currSelectedSource.ludwigPath.subpaths.forEachIndexed { index, subpath ->
                    SubpathItem(subpath = subpath, subpathIndex = index, color = colors[index])
                }
            }
        }

        ExpandableDisplaySection(
            modifier = Modifier.padding(horizontal = 8.dp),
            isExpanded = isControlsExpanded,
            onExpand = { isControlsExpanded = !isControlsExpanded },
            headerText = "Controls",
            contentPadding = PaddingValues(16.dp),
            headerIcon = Icons.Default.Star
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                targetVectors.forEach {
                    Box(modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .alpha(if (currSelectedSource == it) 1f else 0.5f)
                        .border(
                            width = if (currSelectedSource == it) 6.dp else 2.dp,
                            color = Color.Black,
                            shape = CircleShape
                        )
                        .clickable() {
                            currSelectedSource = it
                        }
                        .padding(24.dp)) {
                        VectorImage(
                            modifier = Modifier.aspectRatio(1f),
                            source = it,
                            style = Stroke(width = 5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubpathItem(subpath: LudwigSubpath, subpathIndex: Int, color: Color) {
    var isExpanded by remember { mutableStateOf(false) }
    ExpandableDisplaySection(
        isExpanded = isExpanded,
        onExpand = { isExpanded = !isExpanded },
        headerText = "Subpath ${subpathIndex} | isClosed = ${subpath.isClosed} | area = ${subpath.area} | length = ${subpath.length}",
        headerIcon = Icons.Filled.Star,
        headerIconColor = color
    ) {
        subpath.pathSegments.forEach {
            PathSegmentItem(pathSegment = it)
        }
    }
}

@Composable
fun PathSegmentItem(pathSegment: PathSegment) {
    DisplaySection(headerText = pathSegment.pathNode.javaClass.name.split("$").last()) {
        Text(text = "start = (${pathSegment.startPosition.x}, ${pathSegment.startPosition.y})")
        Text(text = "end = (${pathSegment.endPosition.x}, ${pathSegment.endPosition.y})")
    }
}

private fun generateRandomColors(count: Int): List<Color> {
    return List(count) {
        Color(
            red = Random.nextFloat(),
            green = Random.nextFloat(),
            blue = Random.nextFloat(),
            alpha = 1f // You can adjust alpha if needed
        )
    }
}