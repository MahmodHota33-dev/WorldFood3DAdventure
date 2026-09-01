package com.mahmodhota.worldfood3dadventure.ui.world3d.filament

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.Matrix
import android.view.Surface
import androidx.compose.ui.geometry.Offset
import com.google.android.filament.*
import com.google.android.filament.View.*
import com.google.android.filament.android.TextureHelper
import java.nio.ByteBuffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Manages the Filament Engine, Scene, and Renderer lifecycle.
 */
class FilamentGlobeEngine(private val context: Context) {

    private var engine: Engine? = null
    private var renderer: Renderer? = null
    private var scene: Scene? = null
    private var view: View? = null
    private var camera: Camera? = null
    private var swapChain: SwapChain? = null
    private var sphereEntity: Int = Entity.NULL
    private val lightEntities = mutableListOf<Int>()
    
    private var vertexBuffer: VertexBuffer? = null
    private var indexBuffer: IndexBuffer? = null
    private var material: Material? = null
    private var materialInstance: MaterialInstance? = null
    private var texture: Texture? = null

    private val modelMatrix = FloatArray(16).apply { Matrix.setIdentityM(this, 0) }
    private val mvpMatrix = FloatArray(16)
    
    // Optimized projection state
    private val viewMatrix = FloatArray(16)
    private val projectionF = FloatArray(16)
    private val modelView = FloatArray(16)
    
    // Scratch arrays for project()
    private val scratchClipPos = FloatArray(4)
    private val scratchViewNormal = FloatArray(4)
    private val scratchWorldPos = FloatArray(4)
    private val scratchNormal = FloatArray(4)
    
    private val tempDouble16 = DoubleArray(16)
    
    private var lastWidth = 0
    private var lastHeight = 0
    private var frameCount = 0L

    // Cache to avoid redundant matrix math
    private var lastRotX = -999f
    private var lastRotY = -999f
    private val matrixLock = Any()

    init {
        try {
            android.util.Log.d("FilamentGlobe", "Initializing Filament...")
            Filament.init()
            
            engine = Engine.create()
            renderer = engine!!.createRenderer()
            scene = engine!!.createScene()
            view = engine!!.createView()
            camera = engine!!.createCamera(EntityManager.get().create())
            
            view!!.scene = scene
            view!!.camera = camera
            
            // Enable high-quality post-processing for "Premium" feel
            view!!.bloomOptions = BloomOptions().apply {
                enabled = true
                strength = 0.45f
            }
            // Use high quality tonemapping
            view!!.colorGrading = ColorGrading.Builder()
                .toneMapping(ColorGrading.ToneMapping.ACES)
                .build(engine!!)
            
            // Move camera back to see the whole globe (radius ~1)
            val tm = engine!!.transformManager
            val camInstance = tm.getInstance(camera!!.entity)
            val camTransform = FloatArray(16)
            Matrix.setIdentityM(camTransform, 0)
            Matrix.translateM(camTransform, 0, 0f, 0f, 4.2f)
            tm.setTransform(camInstance, camTransform)
            
            // Set Camera Exposure - Sunny Day settings (F/16, 1/125s, ISO 100)
            // This helps with high lux directional lights.
            camera!!.setExposure(16.0f, 1.0f / 125.0f, 100.0f)
            
            setupScene()
            loadMaterial()
            loadTexture()
            android.util.Log.d("FilamentGlobe", "Filament initialized successfully")
        } catch (e: Throwable) {
            android.util.Log.e("FilamentGlobe", "Failed to initialize Filament native layer", e)
            release()
            throw e
        }
    }

    private fun setupScene() {
        val engine = engine!!
        val sphereData = FilamentSphereGenerator.createSphere(engine)
        vertexBuffer = sphereData.vertexBuffer
        indexBuffer = sphereData.indexBuffer

        // 2. Lighting - Significant increase for "Heller" (brighter) feel
        val light = EntityManager.get().create()
        lightEntities.add(light)
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1.0f, 1.0f, 0.98f) // Brighter white
            .intensity(500_000.0f)     // Increased from 350k
            .direction(0.5f, -0.6f, -1.0f) 
            .castShadows(true)
            .build(engine, light)
        scene!!.addEntity(light)
        
        val fillLight = EntityManager.get().create()
        lightEntities.add(fillLight)
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(0.8f, 0.9f, 1.0f) // Stronger blue fill
            .intensity(180_000.0f)    // Increased from 120k
            .direction(-0.6f, 0.4f, 1.0f)
            .build(engine, fillLight)
        scene!!.addEntity(fillLight)
        
        // Add a third light for Rim/Atmosphere effect
        val rimLight = EntityManager.get().create()
        lightEntities.add(rimLight)
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(0.5f, 0.7f, 1.0f) 
            .intensity(100_000.0f)
            .direction(0.0f, 0.0f, 1.0f) // Directly towards camera for bright edges
            .build(engine, rimLight)
        scene!!.addEntity(rimLight)
        
        // Add a very weak ambient light from the back-bottom-left to avoid pitch-black areas
        val ambientFill = EntityManager.get().create()
        lightEntities.add(ambientFill)
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(0.3f, 0.4f, 0.6f)
            .intensity(30_000.0f)
            .direction(0.7f, -0.7f, 1.0f)
            .build(engine, ambientFill)
        scene!!.addEntity(ambientFill)

        // 3. Earth Entity
        sphereEntity = EntityManager.get().create()
        RenderableManager.Builder(1)
            .boundingBox(Box(0.0f, 0.0f, 0.0f, 1.1f, 1.1f, 1.1f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vertexBuffer!!, indexBuffer!!, 0, sphereData.indexCount)
            .build(engine, sphereEntity)
        scene!!.addEntity(sphereEntity)
    }

    private fun loadMaterial() {
        val engine = engine ?: return
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open("materials/earth.filamat")
            val bytes = inputStream.readBytes()
            inputStream.close()
            
            val buffer = ByteBuffer.allocateDirect(bytes.size)
            buffer.put(bytes)
            buffer.flip()
            
            material = Material.Builder()
                .payload(buffer, buffer.remaining())
                .build(engine)
            
            materialInstance = material!!.createInstance()
            
            engine.renderableManager.setMaterialInstanceAt(
                engine.renderableManager.getInstance(sphereEntity),
                0, materialInstance!!
            )
            android.util.Log.d("FilamentMaterial", "EARTH MATERIAL: LOADED")
        } catch (e: Exception) {
            android.util.Log.e("FilamentGlobe", "FAILED to load material: $e")
        }
    }

    private fun loadTexture() {
        val engine = engine ?: return
        try {
            val assetManager = context.assets
            val texturePath = "textures/earth_2k.png"
            
            val bitmap = try {
                val inputStream = assetManager.open(texturePath)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                generateProceduralEarthBitmap()
            } ?: return
            
            texture = Texture.Builder()
                .width(bitmap.width)
                .height(bitmap.height)
                .sampler(Texture.Sampler.SAMPLER_2D)
                .format(Texture.InternalFormat.SRGB8_A8)
                .levels(1)
                .build(engine)
            
            TextureHelper.setBitmap(engine, texture!!, 0, bitmap)
            
            val sampler = TextureSampler(
                TextureSampler.MinFilter.LINEAR,
                TextureSampler.MagFilter.LINEAR,
                TextureSampler.WrapMode.REPEAT
            )
            
            materialInstance?.setParameter("earthTexture", texture!!, sampler)
            android.util.Log.d("FilamentTexture", "EARTH TEXTURE: READY")
        } catch (e: Exception) {
            android.util.Log.e("FilamentGlobe", "Texture pipeline failed: $e")
        }
    }

    private fun generateProceduralEarthBitmap(): android.graphics.Bitmap {
        val width = 2048 // Higher resolution
        val height = 1024
        val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.parseColor("#0D47A1")) // Deep Ocean Blue
        
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        
        // 1. Draw subtle grid
        paint.color = android.graphics.Color.WHITE
        paint.alpha = 30
        for (i in 0..12) {
            val y = (i * height / 12).toFloat()
            canvas.drawLine(0f, y, width.toFloat(), y, paint)
        }
        for (i in 0..24) {
            val x = (i * width / 24).toFloat()
            canvas.drawLine(x, 0f, x, height.toFloat(), paint)
        }

        // 2. Draw Continents
        val landPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#2E7D32") // Lush Green
            style = android.graphics.Paint.Style.FILL
        }
        val coastPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#81C784") // Lighter Coast
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 2.0f
        }
        val desertPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#FBC02D") // Golden dry areas
            style = android.graphics.Paint.Style.FILL
            alpha = 150
        }

        com.mahmodhota.worldfood3dadventure.ui.world3d.GlobeContinentData.allPolygons.forEach { poly ->
            val path = android.graphics.Path()
            for (i in poly.indices step 2) {
                val lat = poly[i]
                val lon = poly[i+1]
                // u = (lon + 180) / 360 -> x = u * width
                val x = (lon + 180f) * (width / 360f)
                val y = (90f - lat) * (height / 180f)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            canvas.drawPath(path, landPaint)
            
            // Desert overlay
            canvas.save()
            canvas.clipPath(path)
            canvas.drawRect(width * 0.45f, height * 0.30f, width * 0.65f, height * 0.50f, desertPaint) // Sahara
            canvas.restore()
            
            canvas.drawPath(path, coastPaint)
        }

        // 3. Add City Lights (white/yellow dots) - Bright for Bloom
        paint.color = android.graphics.Color.WHITE
        paint.alpha = 255
        com.mahmodhota.worldfood3dadventure.ui.world3d.CITY_LIGHTS.forEach { light ->
            val x = (light.lon + 180f) * (width / 360f)
            val y = (90f - light.lat) * (height / 180f)
            
            // Bright core
            paint.color = android.graphics.Color.WHITE
            canvas.drawCircle(x, y, 2.5f * light.brightness, paint)
            
            // Warm glow
            paint.color = android.graphics.Color.parseColor("#FFF176")
            paint.alpha = 180
            canvas.drawCircle(x, y, 5f * light.brightness, paint)
        }
        
        return bitmap
    }

    fun onSurfaceAvailable(surface: Surface) {
        engine?.let {
            swapChain = it.createSwapChain(surface)
        }
    }

    fun onSurfaceDestroyed() {
        engine?.let { eng ->
            swapChain?.let { sc ->
                eng.destroySwapChain(sc)
            }
            swapChain = null
        }
    }

    fun onResized(width: Int, height: Int) {
        lastWidth = width
        lastHeight = height
        view?.let { v ->
            v.viewport = Viewport(0, 0, width, height)
            val aspect = width.toDouble() / height.toDouble()
            camera?.setProjection(45.0, aspect, 0.1, 20.0, Camera.Fov.VERTICAL)
        }
    }

    fun setZoom(zoom: Float) {
        val engine = engine ?: return
        val camera = camera ?: return
        val tm = engine.transformManager
        val camInstance = tm.getInstance(camera.entity)
        val camTransform = FloatArray(16)
        Matrix.setIdentityM(camTransform, 0)
        // Zooming in moves the camera closer. Default zoom 1.0 -> Z=4.2
        val z = (4.2f / zoom).coerceIn(1.5f, 10f)
        Matrix.translateM(camTransform, 0, 0f, 0f, z)
        tm.setTransform(camInstance, camTransform)
    }

    /**
     * Updates projection matrices for the current rotation.
     * Call this once per frame before multiple project() calls.
     */
    fun prepareProjection(rotX: Float, rotY: Float) {
        val camera = camera ?: return
        
        synchronized(matrixLock) {
            if (rotX == lastRotX && rotY == lastRotY) return
            lastRotX = rotX
            lastRotY = rotY

            // Update model matrix
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.rotateM(modelMatrix, 0, rotX, 1f, 0f, 0f)
            Matrix.rotateM(modelMatrix, 0, rotY, 0f, 1f, 0f)

            // Update globe entity transform immediately (used in render)
            engine?.transformManager?.let { tm ->
                tm.setTransform(tm.getInstance(sphereEntity), modelMatrix)
            }

            // Cache camera matrices (converting Double -> Float)
            camera.getProjectionMatrix(tempDouble16)
            for (i in 0..15) projectionF[i] = tempDouble16[i].toFloat()

            camera.getModelMatrix(tempDouble16)
            for (i in 0..15) viewMatrix[i] = tempDouble16[i].toFloat()
            Matrix.invertM(viewMatrix, 0, viewMatrix, 0)

            // MVP = P * V * M
            Matrix.multiplyMM(modelView, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionF, 0, modelView, 0)
        }
    }

    /**
     * Holds the result of a point projection including screen coordinates and visibility info.
     */
    data class ProjectionResult(
        val offset: Offset,
        val visibility: Float, // 1.0 = fully facing camera, 0.0 = on horizon or behind
        val isVisible: Boolean
    )

    /**
     * Projects geographic XYZ coordinates to screen coordinates.
     * Uses cached matrices from prepareProjection().
     */
    fun project(xyz: FloatArray): ProjectionResult? {
        if (lastWidth <= 0 || lastHeight <= 0) return null
        
        synchronized(matrixLock) {
            // worldPos = [x, y, z, 1]
            scratchWorldPos[0] = xyz[0]
            scratchWorldPos[1] = xyz[1]
            scratchWorldPos[2] = xyz[2]
            scratchWorldPos[3] = 1f

            // normal = [x, y, z, 0]
            scratchNormal[0] = xyz[0]
            scratchNormal[1] = xyz[1]
            scratchNormal[2] = xyz[2]
            scratchNormal[3] = 0f

            return projectInternal()
        }
    }

    fun project(latDeg: Float, lonDeg: Float): ProjectionResult? {
        if (lastWidth <= 0 || lastHeight <= 0) return null
        
        synchronized(matrixLock) {
            val phi = (90f - latDeg) * PI.toFloat() / 180f
            val theta = lonDeg * PI.toFloat() / 180f

            val sPhi = sin(phi)
            scratchWorldPos[0] = sPhi * sin(theta)
            scratchWorldPos[1] = cos(phi)
            scratchWorldPos[2] = sPhi * cos(theta)
            scratchWorldPos[3] = 1f

            scratchNormal[0] = scratchWorldPos[0]
            scratchNormal[1] = scratchWorldPos[1]
            scratchNormal[2] = scratchWorldPos[2]
            scratchNormal[3] = 0f

            return projectInternal()
        }
    }

    private fun projectInternal(): ProjectionResult? {
        // Normal culling in view space
        Matrix.multiplyMV(scratchViewNormal, 0, modelView, 0, scratchNormal, 0)
        
        // Visibility factor based on the Z component of the normal in view space.
        // Z+ is towards the camera. 
        val visibility = scratchViewNormal[2].coerceIn(0f, 1f)
        
        if (visibility <= 0.02f) return null // Hide completely if behind or very close to horizon
        
        // Clip space
        Matrix.multiplyMV(scratchClipPos, 0, mvpMatrix, 0, scratchWorldPos, 0)
        if (scratchClipPos[3] <= 0) return null
        
        val ndcX = scratchClipPos[0] / scratchClipPos[3]
        val ndcY = scratchClipPos[1] / scratchClipPos[3]
        
        val screenX = (ndcX + 1f) * 0.5f * lastWidth
        val screenY = (1f - ndcY) * 0.5f * lastHeight
        
        return ProjectionResult(
            offset = Offset(screenX, screenY),
            visibility = visibility,
            isVisible = true
        )
    }

    fun render(frameTimeNanos: Long) {
        val renderer = renderer ?: return
        val swapChain = swapChain ?: return
        
        frameCount++
        if (frameCount % 180 == 0L) {
            android.util.Log.d("FilamentEngine", "Rendering frame $frameCount")
        }

        // Note: modelMatrix and globe entity transform are updated in prepareProjection() 
        // called from the UI layer recomposition loop or choreographer callback.
        
        if (renderer.beginFrame(swapChain, frameTimeNanos)) {
            renderer.render(view!!)
            renderer.endFrame()
        }
    }

    fun release() {
        val engine = engine ?: return
        
        android.util.Log.d("FilamentEngine", "Releasing engine resources...")
        
        // 0. Destroy SwapChain first to stop presenting
        swapChain?.let { engine.destroySwapChain(it) }
        swapChain = null

        // 1. Unbind materials from entities by destroying entities first
        // This MUST happen before destroying materialInstance to avoid "still in use" crash.
        if (sphereEntity != Entity.NULL) {
            scene?.removeEntity(sphereEntity)
            engine.destroyEntity(sphereEntity)
            sphereEntity = Entity.NULL
        }
        
        lightEntities.forEach {
            scene?.removeEntity(it)
            engine.destroyEntity(it)
        }
        lightEntities.clear()

        if (camera != null) {
            engine.destroyEntity(camera!!.entity)
            camera = null
        }

        // 2. Destroy structural objects
        view?.let { engine.destroyView(it) }
        view = null
        scene?.let { engine.destroyScene(it) }
        scene = null
        renderer?.let { engine.destroyRenderer(it) }
        renderer = null
        
        // 3. Destroy resources
        vertexBuffer?.let { engine.destroyVertexBuffer(it) }
        vertexBuffer = null
        indexBuffer?.let { engine.destroyIndexBuffer(it) }
        indexBuffer = null
        materialInstance?.let { engine.destroyMaterialInstance(it) }
        materialInstance = null
        material?.let { engine.destroyMaterial(it) }
        material = null
        texture?.let { engine.destroyTexture(it) }
        texture = null
        
        // 4. Finally destroy the engine
        engine.destroy()
        this.engine = null
        android.util.Log.d("FilamentEngine", "Engine released successfully")
    }
}
