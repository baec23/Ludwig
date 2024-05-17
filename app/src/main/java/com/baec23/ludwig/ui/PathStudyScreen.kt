package com.baec23.ludwig.ui

import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun PathStudyScreen() {
    val paint = Paint()
    paint.setTypeface(Typeface.DEFAULT)
    val path1 = Path().asAndroidPath()
    paint.getTextPath("A", 0, 1, 0f, 10f, path1)
    val scalingMatrix = Matrix()
    scalingMatrix.postScale(50f, 50f)
    path1.transform(scalingMatrix)
//    path1.addOval(Rect(Offset(300f, 300f), 200f))
//    val path2 = Path()
//    path2.addOval(Rect(Offset(300f, 300f), 100f))
//    val path3 = android.graphics.Path(path1.asAndroidPath()).asComposePath()
//    path3.op(path1, path2, Intersect)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.LightGray)
        ) {
            val pi = androidx.graphics.path.PathIterator(path1)
            drawPath(
                path1.asComposePath(),
                color = Color.Green,
                style = Stroke(15f)
            )
        }
//        Box(
//            modifier = Modifier
//                .padding(2.dp)
//                .background(Color.LightGray)
//                .drawWithContent {
//                    Log.d("DEBUG", "${drawContext.size}")
//                    drawContent()
//                }
////                .drawBehind {
//////                    drawPath(
//////                        path2,
//////                        color = Color.Blue,
//////                        style=Stroke(5f)
//////                    )
//////                    drawPath(
//////                        path3,
//////                        color = Color.Red,
//////                    )
////                }
//        ) {
//            Text("WHAt")
//
//        }
    }
}