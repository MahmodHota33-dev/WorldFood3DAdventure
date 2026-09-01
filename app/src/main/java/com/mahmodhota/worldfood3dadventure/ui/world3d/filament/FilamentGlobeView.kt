package com.mahmodhota.worldfood3dadventure.ui.world3d.filament

import android.view.Choreographer
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * Compose wrapper for the Filament SurfaceView.
 * Integrates Filament with the Android/Compose lifecycle and touch events.
 */
@Composable
fun FilamentGlobeView(
    onFailure: () -> Unit,
    modifier: Modifier = Modifier,
    rotationX: Float = 0f,
    rotationY: Float = 0f,
    onEngineReady: (FilamentGlobeEngine) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Ensure the rendering loop uses latest rotation values
    val currentRotationX = rememberUpdatedState(rotationX)
    val currentRotationY = rememberUpdatedState(rotationY)
    
    // 1. Engine Persistence
    val engine = remember { 
        try {
            FilamentGlobeEngine(context)
        } catch (e: Throwable) {
            android.util.Log.e("FilamentGlobeView", "Engine creation failed", e)
            null
        }
    }

    LaunchedEffect(engine) {
        if (engine == null) {
            onFailure()
        } else {
            onEngineReady(engine)
        }
    }

    if (engine == null) {
        return
    }

    // 2. Rendering Loop (Choreographer)
    val choreographer = remember { Choreographer.getInstance() }
    val frameCallback = remember {
        object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                // Ensure matrices are updated for the current rotation before rendering
                engine.prepareProjection(currentRotationX.value, currentRotationY.value)
                engine.render(frameTimeNanos)
                choreographer.postFrameCallback(this)
            }
        }
    }

    // 3. Lifecycle Management
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> choreographer.postFrameCallback(frameCallback)
                Lifecycle.Event.ON_PAUSE -> choreographer.removeFrameCallback(frameCallback)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            choreographer.removeFrameCallback(frameCallback)
            engine.release()
        }
    }

    // 4. SurfaceView and Gestures
    AndroidView(
        factory = { ctx ->
            SurfaceView(ctx).apply {
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        engine.onSurfaceAvailable(holder.surface)
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                        engine.onResized(width, height)
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        engine.onSurfaceDestroyed()
                    }
                })
            }
        },
        modifier = modifier.fillMaxSize(),
        update = { /* Configuration updates */ }
    )
}
