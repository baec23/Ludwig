package com.baec23.ludwig.ui

import android.animation.ValueAnimator
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import android.view.animation.AnticipateOvershootInterpolator
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baec23.ludwig.R
import com.baec23.ludwig.morpher.model.morpher.VectorSource

//private const val SHADER_SRC = """
//    uniform shader composable;
//    uniform float time;
//    uniform float elapsedTime;
//    uniform float2 size;
//
//    half4 main(in float2 fragCoord) {
//      float scaled = (1.0-time/5000.0);
////      return half4(0.5, 0.5, 0.5, 1.0);
//      return half4(scaled,scaled, 0, 1.0);
//   }
//"""

//private const val SHADER_SRC = """
//    uniform shader composable;
//    uniform float time;
//    uniform float2 size;
//
//    float colormap_grayscale(float x) {
//        float gray;
//
//        if (x < 0.0) {
//            gray = 104.0 / 255.0;
//        } else if (x < 20049.0 / 82979.0) {
//            gray = (829.79 * x + 1.51) / 255.0;
//        } else if (x < 327013.0 / 810990.0) {
//            gray = (82679670.0 / 10875673217.0 * x - 2190770.0 / 10875673217.0) / 255.0;
//        } else if (x < 1.0) {
//            gray = (103806720.0 / 483977.0 * x + 2607415.0 / 483977.0) / 255.0;
//        } else {
//            gray = 0.8;
//        }
//
//        return min(gray, 0.8);
//    }
//
//
//    float4 colormap(float x) {
//        float color = colormap_grayscale(x);
//        return float4(color, color, color, 1.0);
//    }
//
//
//    float rand(float2 n) {
//        return fract(sin(dot(n, float2(12.9898, 4.1414))) * 43758.5453);
//    }
//
//    float noise(float2 p){
//        float2 ip = floor(p);
//        float2 u = fract(p);
//        u = u*u*(3.0-2.0*u);
//
//        float res = mix(
//            mix(rand(ip),rand(ip+float2(1.0,0.0)),u.x),
//            mix(rand(ip+float2(0.0,1.0)),rand(ip+float2(1.0,1.0)),u.x),u.y);
//        return res*res;
//    }
//
//    const mat2 mtx = mat2( 0.80,  0.60, -0.60,  0.80 );
//
//    float fbm( float2 p )
//    {
//        float f = 0.0;
//
//        f += 0.500000*noise( p + time/2  ); p = mtx*p*2.02;
//        f += 0.031250*noise( p ); p = mtx*p*2.01;
//        f += 0.250000*noise( p ); p = mtx*p*2.03;
//        f += 0.125000*noise( p ); p = mtx*p*2.01;
//        f += 0.062500*noise( p ); p = mtx*p*2.04;
//        f += 0.015625*noise( p + sin(time/2) );
//
//        return f/0.96875;
//    }
//
//    float pattern( in float2 p )
//    {
//        return fbm( p + fbm( p + fbm( p ) ) );
//    }
//
//    half4 main(float2 fragCoord)
//    {
//        float2 uv = fragCoord/size.y;
//        float shade = pattern(uv);
//        return half4(colormap(shade).rgb, shade);
//    }
//"""
private const val SHADER_SRC = """
    uniform shader composable;
    uniform float time;
    uniform float2 size;
    float3 base_color = float3(0.1, 0.1, 0.1);
    float3 bright_color = float3(0.8, 0.8, 0.8);

    float2 hash22(float2 p)
    {
        p = float2( dot(p,float2(127.1,311.7)),
                  dot(p,float2(269.5,183.3)));
      
        return -1.0 + 2.0 * fract(sin(p)*43758.5453123);
    }


    float perlin_noise(float2 p)
    {
        float2 pi = floor(p);
        float2 pf = p-pi;
        
        float2 w = pf*pf*(3.-2.*pf);
        
        float f00 = dot(hash22(pi+float2(.0,.0)),pf-float2(.0,.0));
        float f01 = dot(hash22(pi+float2(.0,1.)),pf-float2(.0,1.));
        float f10 = dot(hash22(pi+float2(1.0,0.)),pf-float2(1.0,0.));
        float f11 = dot(hash22(pi+float2(1.0,1.)),pf-float2(1.0,1.));
        
        float xm1 = mix(f00,f10,w.x);
        float xm2 = mix(f01,f11,w.x);
        
        return mix(xm1,xm2,w.y);
    }

    // Official HSV to RGB conversion 
    float3 hsv2rgb( in float3 c )
    {
        float3 rgb = clamp( abs(mod(c.x*6.0+float3(0.0,4.0,2.0),6.0)-3.0)-1.0, 0.0, 1.0 );

        return c.z * mix( float3(1.0), rgb, c.y);
    }

    half4 main(float2 fragCoord )
    {
        // Normalized pixel coordinates (from 0 to 1)
        float2 uv = fragCoord/size.xy;
        float2 uv2 = uv + perlin_noise(4.8 * float2(uv.x + 38.913 + time * 0.01, uv.y + 81.975 + time * 0.01));
        // Squash it vertically, so it looks OK in isometric
        uv2 *= float2(1.0, 2.0);
        uv2 = uv2 + float2(time * 0.05, 0.0);

        // Time varying pixel color
        float f = perlin_noise(0.3 * uv2);

        // Output to screen
        f = (f + time * .01) * 8.0;
        f = f - floor(f);
        
        float mix_amount = smoothstep(0.25, 0.27, f);
        mix_amount = min(mix_amount, 1.0 - smoothstep(0.45, 0.60, f));
        
        float3 col2 = mix(base_color, bright_color, mix_amount);
        return half4(col2, 1.0);
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
//        return half4(finalColor, 1.0);
//    }
//"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShaderTestScreen() {
    val shader = RuntimeShader(SHADER_SRC)
    val shaderBrush = ShaderBrush(shader)
    val appleVectorSource =
        VectorSource.fromImageVector(ImageVector.vectorResource(R.drawable.applelogo))
    val firefoxVectorSource =
        VectorSource.fromImageVector(ImageVector.vectorResource(R.drawable.flower))
    var currSelectedSource by remember { mutableStateOf(appleVectorSource) }

    val coroutineScope = rememberCoroutineScope()
    var timeMs by remember { mutableFloatStateOf(0f) }
    var elapsedMs by remember { mutableFloatStateOf(0f) }
    var startTime by remember { mutableFloatStateOf(System.currentTimeMillis().toFloat()) }
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            while (true) {
//                timeMs = (System.currentTimeMillis()).toFloat()
//                elapsedMs = (timeMs - startTime).toFloat()
//                if (elapsedMs > 10000) {
//                    startTime = timeMs
//                    elapsedMs = 0f
//                }
//                delay(10)
//            }
//        }
//    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        CrazyButton(text = "Hello")
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CrazyButton(modifier: Modifier = Modifier, text: String) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        CrazyButtonBackground(
            modifier = Modifier.shadow(4.dp)
        )
        Text(color = Color.White, text = text, fontWeight = FontWeight.Black, fontSize = 24.sp)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CrazyButtonBackground(modifier: Modifier = Modifier) {
    val shader = RuntimeShader(SHADER_SRC)
    val shaderAnimator = ValueAnimator.ofFloat(0f, 40000f)
    var animationValue by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        shaderAnimator.duration = 10000L
        shaderAnimator.repeatCount = ValueAnimator.INFINITE
        shaderAnimator.repeatMode = ValueAnimator.REVERSE
        shaderAnimator.interpolator = AnticipateOvershootInterpolator()
        shaderAnimator.addUpdateListener { animation ->
            animationValue = animation.animatedValue as Float
        }
        shaderAnimator.start()
    }
    Surface(modifier = modifier
        .fillMaxSize()
        .onSizeChanged {
            shader.setFloatUniform("size", it.width.toFloat(), it.height.toFloat())
        }
        .graphicsLayer {
            shader.setFloatUniform("time", animationValue / 1000f)
            renderEffect = RenderEffect
                .createChainEffect(
                    RenderEffect.createBlurEffect(5f, 5f, Shader.TileMode.CLAMP),
                    RenderEffect.createRuntimeShaderEffect(shader, "composable"),
                )
                .asComposeRenderEffect()
        }
    ) {
    }
}