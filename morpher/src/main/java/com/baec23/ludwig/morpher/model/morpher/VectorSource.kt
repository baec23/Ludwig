package com.baec23.ludwig.morpher.model.morpher

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathOperation.Companion.Union
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.graphics.vector.toPath
import com.baec23.ludwig.morpher.model.path.LudwigPath


data class VectorSource(
    val ludwigPath: LudwigPath,
    val viewportSize: Size,
    val viewportOffset: Offset,
    val vectorSize: Size,
    val vectorOffset: Offset
) {
    companion object {
        /**
         * The 'd' attribute within a <path> element
         * If viewportSize and/or viewportOffset are null, will use bounds
         */
        fun fromPathString(
            pathString: String,
            viewportSize: Size? = null,
            viewportOffset: Offset? = null,
        ): VectorSource {
            val pathParser = PathParser().parsePathString(pathString)
            val bounds = pathParser.toPath().getBounds()
            val pathData = pathParser.toNodes()
            val viewportWidth = viewportSize?.width ?: bounds.width
            val viewportHeight = viewportSize?.height ?: bounds.height
            val viewportOffsetX = viewportOffset?.x ?: bounds.left
            val viewportOffsetY = viewportOffset?.y ?: bounds.top
            return VectorSource(
                LudwigPath.fromPath(pathData.toPath(), 1000f, 1000f),
                Size(viewportWidth, viewportHeight),
                Offset(viewportOffsetX, viewportOffsetY),
                Size(1000f, 1000f),
                Offset(0f, 0f)
            )
        }

        fun fromImageVector(imageVector: ImageVector): VectorSource {
            val pathData = imageVector.root.filterIsInstance<VectorPath>().flatMap { it.pathData }
            return VectorSource(
                LudwigPath.fromPath(pathData.toPath(), 1000f, 1000f),
                Size(imageVector.viewportWidth, imageVector.viewportHeight),
                Offset(0f, 0f),
                Size(1000f, 1000f),
                Offset(0f, 0f)
            )
        }

        fun fromText(text: String): VectorSource {
            val path = Path()
            val paint = Paint()
            paint.setTypeface(Typeface.DEFAULT_BOLD)

            val bounds = RectF()

            paint.getTextPath(text, 0, text.length, 0f, 0f, path)

            val unscaledLudwigPath = LudwigPath.fromPath(path)
            val resultPath = androidx.compose.ui.graphics.Path()
            unscaledLudwigPath.subpaths.forEach { subpath ->
                resultPath.op(resultPath, subpath.toComposePath(), Union)
            }

//            val resultBounds = resultPath.getBounds()
//            unscaledLudwigPath.subpaths.forEach { subpath ->
//                val currBounds = subpath.toComposePath().getBounds()
//                if (currBounds.left > resultBounds.left &&
//                    currBounds.right < resultBounds.right &&
//                    currBounds.top > resultBounds.top &&
//                    currBounds.bottom < resultBounds.bottom
//                ) {
//                    resultPath.op(resultPath, subpath.toComposePath(), Difference)
//                }
//            }


            return VectorSource(
                LudwigPath.fromPath(
                    path,
                    1000f,
                    1000f
                ),
                Size(bounds.width(), bounds.height()),
                Offset(bounds.left, bounds.right),
                Size(1000f, 1000f),
                Offset(0f, 0f)
            )
        }
    }
}