package com.baec23.ludwig.morpher.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import com.baec23.ludwig.morpher.MorphAnimator
import com.baec23.ludwig.morpher.model.morpher.VectorSource
import kotlinx.coroutines.delay

@Composable
fun AnimatedVector(
    modifier: Modifier = Modifier,
    vectorSource: VectorSource,
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 1000, easing = EaseInOutExpo),
    strokeWidth: Float = 10f,
    strokeColor: Color = Color.Black,
) {
    var startVectorSource by remember { mutableStateOf(vectorSource) }
    var endVectorSource by remember { mutableStateOf(vectorSource) }
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(key1 = vectorSource) {
        if (animationProgress.value != animationProgress.targetValue) {
            animationProgress.animateTo(
                targetValue = animationProgress.targetValue,
                animationSpec = tween(180)
            )
            delay(200)
        }

        when (animationProgress.targetValue) {
            1f -> {
                startVectorSource = vectorSource
                animationProgress.animateTo(
                    0f,
                    animationSpec = animationSpec,
                )
            }

            0f -> {
                endVectorSource = vectorSource
                animationProgress.animateTo(
                    1f,
                    animationSpec = animationSpec,
                )
            }
        }
    }
    AnimatedVector(
        modifier = modifier,
        startSource = startVectorSource,
        endSource = endVectorSource,
        progress = animationProgress.value,
        strokeWidth = strokeWidth,
        strokeColor = strokeColor,
    )
}

@Composable
fun AnimatedFillVector(
    modifier: Modifier = Modifier,
    vectorSource: VectorSource,
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 1000, easing = EaseInOutExpo),
    fillColor: Color = Color.Black,
) {
    var startVectorSource by remember { mutableStateOf(vectorSource) }
    var endVectorSource by remember { mutableStateOf(vectorSource) }
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(key1 = vectorSource) {
        if (animationProgress.value != animationProgress.targetValue) {
            animationProgress.animateTo(
                targetValue = animationProgress.targetValue,
                animationSpec = tween(180)
            )
            delay(200)
        }

        when (animationProgress.targetValue) {
            1f -> {
                startVectorSource = vectorSource
                animationProgress.animateTo(
                    0f,
                    animationSpec = animationSpec,
                )
            }

            0f -> {
                endVectorSource = vectorSource
                animationProgress.animateTo(
                    1f,
                    animationSpec = animationSpec,
                )
            }
        }
    }
    AnimatedFillVector(
        modifier = modifier,
        startSource = startVectorSource,
        endSource = endVectorSource,
        progress = animationProgress.value,
        fillColor = fillColor
    )
}

@Composable
fun AnimatedFillVector(
    modifier: Modifier = Modifier,
    startSource: VectorSource,
    endSource: VectorSource,
    progress: Float,
    fillColor: Color = Color.Black,
    extraPathsBreakpoint: Float = 0.2f,
    animationSmoothness: Int = 200,
) {
    var morphAnimator by remember { mutableStateOf<MorphAnimator?>(null) }
    val vectorWidth = 1000f
    val vectorHeight = 1000f
    val vectorOffsetX = 0f
    val vectorOffsetY = 0f

    LaunchedEffect(startSource, endSource) {
        morphAnimator = MorphAnimator(
            start = startSource,
            end = endSource,
            smoothness = animationSmoothness
        )
    }

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
                        morphAnimator?.let { animator ->
                            val pairedPaths =
                                animator.getInterpolatedPairedPath(progress)
                            drawPath(
                                pairedPaths,
                                color = fillColor,
                                style = Fill
                            )

                            val extraStartPathsAnimationProgress =
                                if (progress <= extraPathsBreakpoint) {
                                    progress / extraPathsBreakpoint
                                } else {
                                    1f
                                }
                            val extraStartPaths =
                                animator.getInterpolatedUnpairedStartPath(
                                    extraStartPathsAnimationProgress
                                )
                            if (extraStartPathsAnimationProgress < 1f) {
                                drawPath(
                                    extraStartPaths,
                                    color = fillColor,
                                    style = Fill,
                                    alpha = minOf(
                                        maxOf(1f - extraStartPathsAnimationProgress, 0f),
                                        1f
                                    )
                                )
                            }


                            val extraEndPathsBreakpoint = 1f - extraPathsBreakpoint
                            val extraEndPathsAnimationProgress =
                                if (progress < extraEndPathsBreakpoint) {
                                    0f
                                } else {
                                    (progress - extraEndPathsBreakpoint) / (1f - extraEndPathsBreakpoint)
                                }
                            val extraEndPaths =
                                animator.getInterpolatedUnpairedEndPath(1f - extraEndPathsAnimationProgress)
                            if (extraEndPathsAnimationProgress > 0f) {
                                drawPath(
                                    extraEndPaths,
                                    color = fillColor,
                                    style = Fill,
                                    alpha = minOf(maxOf(extraEndPathsAnimationProgress, 0f), 1f)
                                )
                            }
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
fun AnimatedVector(
    modifier: Modifier = Modifier,
    startSource: VectorSource,
    endSource: VectorSource,
    progress: Float,
    strokeWidth: Float = 20f,
    strokeColor: Color = Color.Black,
    extraPathsBreakpoint: Float = 0.2f,
    animationSmoothness: Int = 200,
) {
    var morphAnimator by remember { mutableStateOf<MorphAnimator?>(null) }
    val vectorWidth = 1000f
    val vectorHeight = 1000f
    val vectorOffsetX = 0f
    val vectorOffsetY = 0f

    LaunchedEffect(startSource, endSource) {
        morphAnimator = MorphAnimator(
            start = startSource,
            end = endSource,
            smoothness = animationSmoothness
        )
    }

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
                        morphAnimator?.let { animator ->
                            val pairedPaths =
                                animator.getInterpolatedPairedPath(progress)
                            drawPath(
                                pairedPaths,
                                color = strokeColor,
                                style = Stroke(
                                    width = strokeWidth,
                                    join = StrokeJoin.Round,
                                    cap = StrokeCap.Round
                                )
                            )

                            val extraStartPathsAnimationProgress =
                                if (progress <= extraPathsBreakpoint) {
                                    progress / extraPathsBreakpoint
                                } else {
                                    1f
                                }
                            val extraStartPaths =
                                animator.getInterpolatedUnpairedStartPath(
                                    extraStartPathsAnimationProgress
                                )
                            if (extraStartPathsAnimationProgress < 1f) {
                                drawPath(
                                    extraStartPaths,
                                    color = strokeColor,
                                    style = Stroke(
                                        width = strokeWidth,
                                        join = StrokeJoin.Round,
                                        cap = StrokeCap.Round
                                    ),
                                    alpha = minOf(
                                        maxOf(1f - extraStartPathsAnimationProgress, 0f),
                                        1f
                                    )
                                )
                            }


                            val extraEndPathsBreakpoint = 1f - extraPathsBreakpoint
                            val extraEndPathsAnimationProgress =
                                if (progress < extraEndPathsBreakpoint) {
                                    0f
                                } else {
                                    (progress - extraEndPathsBreakpoint) / (1f - extraEndPathsBreakpoint)
                                }
                            val extraEndPaths =
                                animator.getInterpolatedUnpairedEndPath(1f - extraEndPathsAnimationProgress)
                            if (extraEndPathsAnimationProgress > 0f) {
                                drawPath(
                                    extraEndPaths,
                                    color = strokeColor,
                                    style = Stroke(
                                        width = strokeWidth,
                                        join = StrokeJoin.Round,
                                        cap = StrokeCap.Round
                                    ),
                                    alpha = minOf(maxOf(extraEndPathsAnimationProgress, 0f), 1f)
                                )
                            }
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
