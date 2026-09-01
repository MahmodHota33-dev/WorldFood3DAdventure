package com.mahmodhota.worldfood3dadventure.ui.world3d.filament

import com.google.android.filament.Engine
import com.google.android.filament.IndexBuffer
import com.google.android.filament.VertexBuffer
import com.google.android.filament.VertexBuffer.VertexAttribute
import com.google.android.filament.VertexBuffer.AttributeType
import com.google.android.filament.IndexBuffer.Builder.IndexType
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

/**
 * Procedural sphere generator for Filament.
 * Creates vertex and index buffers for a UV-mapped unit sphere with proper TBN quaternions.
 */
object FilamentSphereGenerator {

    data class SphereData(
        val vertexBuffer: VertexBuffer,
        val indexBuffer: IndexBuffer,
        val indexCount: Int
    )

    fun createSphere(engine: Engine, stacks: Int = 48, slices: Int = 48): SphereData {
        val vertexCount = (stacks + 1) * (slices + 1)
        val indexCount = stacks * slices * 6
        
        // 9 floats: px, py, pz, qx, qy, qz, qw, u, v
        val vertexBufferData = ByteBuffer.allocateDirect(vertexCount * 9 * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        for (stack in 0..stacks) {
            val phi = (stack.toFloat() * PI.toFloat()) / stacks.toFloat()
            val sinPhi = sin(phi)
            val cosPhi = cos(phi)

            for (slice in 0..slices) {
                val theta = (slice.toFloat() * 2f * PI.toFloat()) / slices.toFloat()
                val sinTheta = sin(theta - PI.toFloat()) // Align lon=-180 to slice=0
                val cosTheta = cos(theta - PI.toFloat())

                // Position: matching GlobeMath consistent mapping
                val x = sinPhi * sinTheta
                val y = cosPhi
                val z = sinPhi * cosTheta

                // Normal is same as position for unit sphere
                val nx = x
                val ny = y
                val nz = z

                // Tangent (perpendicular to Normal)
                val tx = cosTheta
                val ty = 0f
                val tz = -sinTheta
                
                // Bitangent
                val bx = ny * tz - nz * ty
                val by = nz * tx - nx * tz
                val bz = nx * ty - ny * tx

                // Convert TBN matrix to Quaternion
                val q = matrixToQuaternion(tx, ty, tz, bx, by, bz, nx, ny, nz)

                // P10-GEO: Standard OpenGL/Filament texture convention:
                // v=0 is bottom (South Pole), v=1 is top (North Pole).
                // Our stack loop goes from 0 (North) to stacks (South).
                // So we must flip v to ensure North maps to the top of the texture.
                val u = slice.toFloat() / slices.toFloat()
                val v = 1.0f - (stack.toFloat() / stacks.toFloat())

                vertexBufferData.put(x); vertexBufferData.put(y); vertexBufferData.put(z)
                vertexBufferData.put(q[0]); vertexBufferData.put(q[1]); vertexBufferData.put(q[2]); vertexBufferData.put(q[3])
                vertexBufferData.put(u); vertexBufferData.put(v)
            }
        }
        vertexBufferData.flip()

        val vBuffer = VertexBuffer.Builder()
            .vertexCount(vertexCount)
            .bufferCount(1)
            .attribute(VertexAttribute.POSITION, 0, AttributeType.FLOAT3, 0, 36)
            .attribute(VertexAttribute.TANGENTS, 0, AttributeType.FLOAT4, 12, 36)
            .attribute(VertexAttribute.UV0, 0, AttributeType.FLOAT2, 28, 36)
            .build(engine)
        vBuffer.setBufferAt(engine, 0, vertexBufferData)

        val indexBufferData = ByteBuffer.allocateDirect(indexCount * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()

        for (stack in 0 until stacks) {
            for (slice in 0 until slices) {
                val first = (stack * (slices + 1) + slice).toShort()
                val second = (first + slices + 1).toShort()

                indexBufferData.put(first)
                indexBufferData.put(second)
                indexBufferData.put((first + 1).toShort())

                indexBufferData.put(second)
                indexBufferData.put((second + 1).toShort())
                indexBufferData.put((first + 1).toShort())
            }
        }
        indexBufferData.flip()

        val iBuffer = IndexBuffer.Builder()
            .indexCount(indexCount)
            .bufferType(IndexType.USHORT)
            .build(engine)
        iBuffer.setBuffer(engine, indexBufferData)

        return SphereData(vBuffer, iBuffer, indexCount)
    }

    private fun matrixToQuaternion(
        m00: Float, m10: Float, m20: Float,
        m01: Float, m11: Float, m21: Float,
        m02: Float, m12: Float, m22: Float
    ): FloatArray {
        val tr = m00 + m11 + m22
        var qx: Float
        val qy: Float
        val qz: Float
        val qw: Float
        if (tr > 0) {
            val s = sqrt(tr + 1.0f) * 2f
            qw = 0.25f * s
            qx = (m21 - m12) / s
            qy = (m02 - m20) / s
            qz = (m10 - m01) / s
        } else if (m00 > m11 && m00 > m22) {
            val s = sqrt(1.0f + m00 - m11 - m22) * 2f
            qw = (m21 - m12) / s
            qx = 0.25f * s
            qy = (m01 + m10) / s
            qz = (m02 + m20) / s
        } else if (m11 > m22) {
            val s = sqrt(1.0f + m11 - m00 - m22) * 2f
            qw = (m02 - m20) / s
            qx = (m01 + m10) / s
            qy = 0.25f * s
            qz = (m12 + m21) / s
        } else {
            val s = sqrt(1.0f + m22 - m00 - m11) * 2f
            qw = (m10 - m01) / s
            qx = (m02 + m20) / s
            qy = (m12 + m21) / s
            qz = 0.25f * s
        }
        return floatArrayOf(qx, qy, qz, qw)
    }
}
