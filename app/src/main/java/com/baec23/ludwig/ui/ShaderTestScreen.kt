package com.baec23.ludwig.ui

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baec23.ludwig.morpher.component.VectorImage
import com.baec23.ludwig.morpher.model.morpher.VectorSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SHADER_SRC = """
    uniform shader composable;
    uniform float time;
    uniform float2 size;
    
    float colormap_grayscale(float x) {
        float gray;
        
        if (x < 0.0) {
            gray = 104.0 / 255.0;
        } else if (x < 20049.0 / 82979.0) {
            gray = (829.79 * x + 1.51) / 255.0;
        } else if (x < 327013.0 / 810990.0) {
            gray = (82679670.0 / 10875673217.0 * x - 2190770.0 / 10875673217.0) / 255.0;
        } else if (x < 1.0) {
            gray = (103806720.0 / 483977.0 * x + 2607415.0 / 483977.0) / 255.0;
        } else {
            gray = 1.0;
        }
        
        return gray;
    }


    float4 colormap(float x) {
        return float4(colormap_grayscale(x), colormap_grayscale(x), colormap_grayscale(x), 1.0);
    }


    float rand(float2 n) { 
        return fract(sin(dot(n, float2(12.9898, 4.1414))) * 43758.5453);
    }

    float noise(float2 p){
        float2 ip = floor(p);
        float2 u = fract(p);
        u = u*u*(3.0-2.0*u);

        float res = mix(
            mix(rand(ip),rand(ip+float2(1.0,0.0)),u.x),
            mix(rand(ip+float2(0.0,1.0)),rand(ip+float2(1.0,1.0)),u.x),u.y);
        return res*res;
    }

    const mat2 mtx = mat2( 0.80,  0.60, -0.60,  0.80 );

    float fbm( float2 p )
    {
        float f = 0.0;

        f += 0.500000*noise( p + time/2  ); p = mtx*p*2.02;
        f += 0.031250*noise( p ); p = mtx*p*2.01;
        f += 0.250000*noise( p ); p = mtx*p*2.03;
        f += 0.125000*noise( p ); p = mtx*p*2.01;
        f += 0.062500*noise( p ); p = mtx*p*2.04;
        f += 0.015625*noise( p + sin(time/2) );

        return f/0.96875;
    }

    float pattern( in float2 p )
    {
        return fbm( p + fbm( p + fbm( p ) ) );
    }

    half4 main(float2 fragCoord)
    {
        float2 uv = fragCoord/size.x;
        float shade = pattern(uv);
        if(composable.eval(fragCoord).a == 0){
            shade = 0.0;
        }
        return half4(colormap(shade).rgb, shade);
    }
"""
//private const val SHADER_SRC = """
//    uniform shader composable;
//    uniform float time;
//    uniform float2 size;
//    float ltime;
//
//    float noise(float2 p)
//    {
//      return sin(p.x*10.) * sin(p.y*(3. + sin(ltime/11.))) + .2;
//    }
//
//    mat2 rotate(float angle)
//    {
//      return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
//    }
//
//
//    float fbm(float2 p)
//    {
//      p *= 1.1;
//      float f = 0.;
//      float amp = .5;
//      for( int i = 0; i < 3; i++) {
//        mat2 modify = rotate(ltime/50. * float(i*i));
//        f += amp*noise(p);
//        p = modify * p;
//        p *= 2.;
//        amp /= 2.2;
//      }
//      return f;
//    }
//
//    float pattern(float2 p, out float2 q, out float2 r) {
//      q = float2( fbm(p + float2(1.)),
//            fbm(rotate(.1*ltime)*p + float2(3.)));
//      r = float2( fbm(rotate(.2)*q + float2(0.)),
//            fbm(q + float2(0.)));
//      return fbm(p + 1.*r);
//
//    }
//
//    float3 hsv2rgb(float3 c)
//    {
//        float4 K = float4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
//        float3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
//        return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
//    }
//
//    half4 main(float2 fragCoord) {
//      float2 p = fragCoord.xy / size.xy;
//      ltime = time;
//      float ctime = time + fbm(p/8.)*40.;
//      float ftime = fract(ctime/6.);
//      ltime = floor(ctime/6.) + (1.-cos(ftime*3.1415)/2.);
//      ltime = ltime*6.;
//      float2 q;
//      float2 r;
//      float f = pattern(p, q, r);
//
//      // Sample color from composable
//      float4 composableColor = composable.eval(fragCoord);
//
//      // Calculate pattern color
//      float3 patternColor = hsv2rgb(float3(q.x/10. + ltime/100. + .4, abs(r.y)*3. + .1, r.x + f));
//
//      // Blend the composable color and pattern color
//      float blendFactor = 0.5;  // Adjust this factor to balance the influence of composable and pattern colors
//      float3 col = mix(composableColor.rgb, patternColor, blendFactor);
//
//      // Apply vignette effect
//      float vig = 1. - pow(4.*(p.x - .5)*(p.x - .5), 10.);
//      vig *= 1. - pow(4.*(p.y - .5)*(p.y - .5), 10.);
//
//      return half4(col * vig, composableColor.a);
//    }
//"""
//private const val SHADER_SRC = """
//    uniform shader composable;
//    uniform float time;
//    uniform float2 size;
//
//    float3 palette( float t ) {
//        float3 a = float3(0.5, 0.5, 0.5);
//        float3 b = float3(0.5, 0.5, 0.5);
//        float3 c = float3(1.0, 1.0, 1.0);
//        float3 d = float3(0.263,0.416,0.557);
//
//        return a + b*cos( 6.28318*(c*t+d) );
//    }
//
//    half4 main(float2 fragCoord) {
//        float2 uv = (fragCoord * 2.0 - size.xy) / size.y;
//        float2 uv0 = uv;
//        float3 finalColor = float3(0.0, 0.0, 0.0);
//
//        for (float i = 0.0; i < 4.0; i++) {
//            uv = fract(uv * 1.5) - 0.5;
//
//            float d = length(uv) * exp(-length(uv0));
//
//            float3 col = palette(length(uv0) + i*.4 + time*.4);
//
//            d = sin(d*8. + time)/8.;
//            d = abs(d);
//
//            d = pow(0.01 / d, 1.2);
//
//            finalColor += col * d;
//        }
//        return half4(finalColor, composable.eval(fragCoord).a);
//    }
//"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShaderTestScreen() {
    val shader = RuntimeShader(SHADER_SRC)

    val coroutineScope = rememberCoroutineScope()
    var timeMs by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (true) {
                timeMs = (System.currentTimeMillis() % 20000L) / 1000f
                delay(20)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            VectorImage(
                modifier = Modifier
                    .padding(50.dp),
                source = VectorSource.fromImageVector(Icons.Default.Star),
                style = Stroke(width = 160f),
                color = Color.Black
            )
            VectorImage(
                modifier = Modifier
                    .onSizeChanged { size ->
                        shader.setFloatUniform(
                            "size",
                            size.width.toFloat(),
                            size.height.toFloat()
                        )
                    }
                    .alpha(0.5f)
                    .graphicsLayer {
                        shader.setFloatUniform("time", timeMs)
                        clip = true
                        renderEffect =
                            RenderEffect
                                .createChainEffect(
                                    RenderEffect.createBlurEffect(
                                        10.0f,
                                        10.0f,
                                        Shader.TileMode.CLAMP
                                    ),
                                    RenderEffect
                                        .createRuntimeShaderEffect(shader, "composable"),
                                )
                                .asComposeRenderEffect()
                    }
                    .padding(50.dp),
                source = VectorSource.fromImageVector(Icons.Default.Star),
                style = Stroke(width = 160f),
                color = Color.Black
            )
            Text(
                text = "Hello World",
                fontSize = 60.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}
