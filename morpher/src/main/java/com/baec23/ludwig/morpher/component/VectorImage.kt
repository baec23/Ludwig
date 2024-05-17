package com.baec23.ludwig.morpher.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import com.baec23.ludwig.morpher.model.morpher.VectorSource

@Composable
fun VectorImage(
    modifier: Modifier = Modifier,
    source: VectorSource,
    color: Color = Color.Black,
    alpha: Float = 1.0f,
    style: DrawStyle = Stroke(
        width = 10f,
        join = StrokeJoin.Round,
        cap = StrokeCap.Round
    ),
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) {
    val path = remember(key1 = source) { source.ludwigPath.toComposePath() }
    val vectorWidth = source.vectorSize.width
    val vectorHeight = source.vectorSize.height
    val vectorOffsetX = source.vectorOffset.x
    val vectorOffsetY = source.vectorOffset.y

    BoxWithConstraints(modifier = modifier) {
        val canvasWidth: Float
        val canvasHeight: Float
        val scaleFactor: Float

        if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
            val minWidth = constraints.minWidth.toFloat()
            val minHeight = constraints.minHeight.toFloat()
            val maxWidth = constraints.maxWidth.toFloat()
            val maxHeight = constraints.maxHeight.toFloat()
            canvasWidth = maxOf(minOf(vectorWidth, maxWidth), minWidth)
            canvasHeight = maxOf(minOf(vectorHeight, maxHeight), minHeight)

            val scaleFactorX = canvasWidth / vectorWidth
            val scaleFactorY = canvasHeight / vectorHeight
            scaleFactor = minOf(scaleFactorX, scaleFactorY)
        } else if (constraints.hasBoundedWidth) {
            scaleFactor = constraints.maxWidth.toFloat() / vectorWidth
            canvasWidth = constraints.maxWidth.toFloat()
            canvasHeight = vectorHeight * scaleFactor
        } else if (constraints.hasBoundedHeight) {
            scaleFactor = constraints.maxHeight.toFloat() / vectorHeight
            canvasWidth = vectorWidth * scaleFactor
            canvasHeight = constraints.maxHeight.toFloat()
        } else {
            canvasWidth = vectorWidth
            canvasHeight = vectorHeight
            scaleFactor = 1f
        }

        val scaledWidth = vectorWidth * scaleFactor
        val scaledHeight = vectorHeight * scaleFactor
        val translateX = (canvasWidth - scaledWidth) / 2f
        val translateY = (canvasHeight - scaledHeight) / 2f

        // Stroke width needs to be scaled because of canvas scaling
        val mStyle = if (style is Stroke) {
            val scaledStrokeWidth = style.width / scaleFactor
            Stroke(
                width = scaledStrokeWidth,
                miter = style.miter,
                cap = style.cap,
                join = style.join,
                pathEffect = style.pathEffect
            )
        } else {
            style
        }

        Layout(
            content = {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    withTransform({
                        //Move vector to start at 0,0 so scaling works properly
                        translate(
                            left = -vectorOffsetX * scaleFactor,
                            top = -vectorOffsetY * scaleFactor
                        )
                        scale(scaleFactor, Offset(0f, 0f))
                        //Translate to center if due to maintaining aspect ratio, there's empty space
                        translate(left = translateX / scaleFactor, top = translateY / scaleFactor)
                    }) {
                        drawPath(
                            path,
                            color = color,
                            style = mStyle,
                            alpha = alpha,
                            colorFilter = colorFilter,
                            blendMode = blendMode

                        )
                    }
                }
            }
        ) { measurables, _ ->
            val placeable = measurables.first()
                .measure(constraints = Constraints.fixed(canvasWidth.toInt(), canvasHeight.toInt()))

            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }
    }
}

@Composable
fun DebugSubpathVectorImage(
    modifier: Modifier = Modifier,
    source: VectorSource,
    colors: List<Color>,
    alpha: Float = 1.0f,
    style: DrawStyle = Stroke(
        width = 10f,
        join = StrokeJoin.Round,
        cap = StrokeCap.Round
    ),
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) {
//    val path = remember(key1 = source) { source.ludwigPath.toComposePath() }
    val vectorWidth = source.vectorSize.width
    val vectorHeight = source.vectorSize.height
    val vectorOffsetX = source.vectorOffset.x
    val vectorOffsetY = source.vectorOffset.y

    BoxWithConstraints(modifier = modifier) {
        val canvasWidth: Float
        val canvasHeight: Float
        val scaleFactor: Float

        if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
            val minWidth = constraints.minWidth.toFloat()
            val minHeight = constraints.minHeight.toFloat()
            val maxWidth = constraints.maxWidth.toFloat()
            val maxHeight = constraints.maxHeight.toFloat()
            canvasWidth = maxOf(minOf(vectorWidth, maxWidth), minWidth)
            canvasHeight = maxOf(minOf(vectorHeight, maxHeight), minHeight)

            val scaleFactorX = canvasWidth / vectorWidth
            val scaleFactorY = canvasHeight / vectorHeight
            scaleFactor = minOf(scaleFactorX, scaleFactorY)
        } else if (constraints.hasBoundedWidth) {
            scaleFactor = constraints.maxWidth.toFloat() / vectorWidth
            canvasWidth = constraints.maxWidth.toFloat()
            canvasHeight = vectorHeight * scaleFactor
        } else if (constraints.hasBoundedHeight) {
            scaleFactor = constraints.maxHeight.toFloat() / vectorHeight
            canvasWidth = vectorWidth * scaleFactor
            canvasHeight = constraints.maxHeight.toFloat()
        } else {
            canvasWidth = vectorWidth
            canvasHeight = vectorHeight
            scaleFactor = 1f
        }

        val scaledWidth = vectorWidth * scaleFactor
        val scaledHeight = vectorHeight * scaleFactor
        val translateX = (canvasWidth - scaledWidth) / 2f
        val translateY = (canvasHeight - scaledHeight) / 2f

        // Stroke width needs to be scaled because of canvas scaling
        val mStyle = if (style is Stroke) {
            val scaledStrokeWidth = style.width / scaleFactor
            Stroke(
                width = scaledStrokeWidth,
                miter = style.miter,
                cap = style.cap,
                join = style.join,
                pathEffect = style.pathEffect
            )
        } else {
            style
        }

        Layout(
            content = {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    withTransform({
                        //Move vector to start at 0,0 so scaling works properly
                        translate(
                            left = -vectorOffsetX * scaleFactor,
                            top = -vectorOffsetY * scaleFactor
                        )
                        scale(scaleFactor, Offset(0f, 0f))
                        //Translate to center if due to maintaining aspect ratio, there's empty space
                        translate(left = translateX / scaleFactor, top = translateY / scaleFactor)
                    }) {
                        var colorIndex = 0
                        source.ludwigPath.subpaths.forEach { subpath ->
                            subpath.pathSegments.forEach { segment ->
                                drawPath(
                                    segment.path,
                                    color = colors[colorIndex],
                                    style = mStyle,
                                    alpha = alpha,
                                    colorFilter = colorFilter,
                                    blendMode = blendMode

                                )
                                colorIndex++
                            }
//                            drawPath(
//                                subpath.toComposePath(),
//                                color = colors[index],
//                                style = mStyle,
//                                alpha = alpha,
//                                colorFilter = colorFilter,
//                                blendMode = blendMode
//
//                            )
                        }
                    }
                }
            }
        ) { measurables, _ ->
            val placeable = measurables.first()
                .measure(constraints = Constraints.fixed(canvasWidth.toInt(), canvasHeight.toInt()))

            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }
    }
}


@Composable
fun DebugVectorImage(
    modifier: Modifier = Modifier,
    source: VectorSource,
    colors: List<Color>,
    alpha: Float = 1.0f,
    style: DrawStyle = Stroke(
        width = 10f,
        join = StrokeJoin.Round,
        cap = StrokeCap.Round
    ),
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) {
//    val path = remember(key1 = source) { source.ludwigPath.toComposePath() }
    val vectorWidth = source.vectorSize.width
    val vectorHeight = source.vectorSize.height
    val vectorOffsetX = source.vectorOffset.x
    val vectorOffsetY = source.vectorOffset.y

    BoxWithConstraints(modifier = modifier) {
        val canvasWidth: Float
        val canvasHeight: Float
        val scaleFactor: Float

        if (constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
            val minWidth = constraints.minWidth.toFloat()
            val minHeight = constraints.minHeight.toFloat()
            val maxWidth = constraints.maxWidth.toFloat()
            val maxHeight = constraints.maxHeight.toFloat()
            canvasWidth = maxOf(minOf(vectorWidth, maxWidth), minWidth)
            canvasHeight = maxOf(minOf(vectorHeight, maxHeight), minHeight)

            val scaleFactorX = canvasWidth / vectorWidth
            val scaleFactorY = canvasHeight / vectorHeight
            scaleFactor = minOf(scaleFactorX, scaleFactorY)
        } else if (constraints.hasBoundedWidth) {
            scaleFactor = constraints.maxWidth.toFloat() / vectorWidth
            canvasWidth = constraints.maxWidth.toFloat()
            canvasHeight = vectorHeight * scaleFactor
        } else if (constraints.hasBoundedHeight) {
            scaleFactor = constraints.maxHeight.toFloat() / vectorHeight
            canvasWidth = vectorWidth * scaleFactor
            canvasHeight = constraints.maxHeight.toFloat()
        } else {
            canvasWidth = vectorWidth
            canvasHeight = vectorHeight
            scaleFactor = 1f
        }

        val scaledWidth = vectorWidth * scaleFactor
        val scaledHeight = vectorHeight * scaleFactor
        val translateX = (canvasWidth - scaledWidth) / 2f
        val translateY = (canvasHeight - scaledHeight) / 2f

        // Stroke width needs to be scaled because of canvas scaling
        val mStyle = if (style is Stroke) {
            val scaledStrokeWidth = style.width / scaleFactor
            Stroke(
                width = scaledStrokeWidth,
                miter = style.miter,
                cap = style.cap,
                join = style.join,
                pathEffect = style.pathEffect
            )
        } else {
            style
        }

        Layout(
            content = {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    withTransform({
                        //Move vector to start at 0,0 so scaling works properly
                        translate(
                            left = -vectorOffsetX * scaleFactor,
                            top = -vectorOffsetY * scaleFactor
                        )
                        scale(scaleFactor, Offset(0f, 0f))
                        //Translate to center if due to maintaining aspect ratio, there's empty space
                        translate(left = translateX / scaleFactor, top = translateY / scaleFactor)
                    }) {
                        source.ludwigPath.subpaths.forEachIndexed { index, subpath ->
                            drawPath(
                                subpath.toComposePath(),
                                color = colors[index],
                                style = mStyle,
                                alpha = alpha,
                                colorFilter = colorFilter,
                                blendMode = blendMode

                            )
                        }
                    }
                }
            }
        ) { measurables, _ ->
            val placeable = measurables.first()
                .measure(constraints = Constraints.fixed(canvasWidth.toInt(), canvasHeight.toInt()))

            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }
    }
}

