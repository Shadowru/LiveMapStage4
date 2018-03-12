package city.shadow.org.livemap.vr.primitives.impl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import city.shadow.org.livemap.vr.primitives.IOpenGLPrimitive;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform4f;

public class CirclePrimitive implements IOpenGLPrimitive {

    private static final int POSITION_COUNT = 3;
    private final int outerVertexCount;
    private final int vertexCount;

    private final FloatBuffer vertexBuffer;

    private final float center_x = 0.0f;
    private final float center_z = 0.0f;

    private float[] rgba = {0.5f, 0.5f, 0.5f, 1f};

    public CirclePrimitive(float radius) {

        vertexCount = 21;

// Create a buffer for vertex data
        float vertices[] = new float[vertexCount*POSITION_COUNT]; // (x,y,z) for each vertex
        int idx = 0;

// Center vertex for triangle fan
        vertices[idx++] = center_x;
        vertices[idx++] = 0;
        vertices[idx++] = center_z;

// Outer vertices of the circle
        outerVertexCount = vertexCount-1;

        for (int i = 0; i < outerVertexCount; ++i){
            float percent = (i / (float) (outerVertexCount-1));
            double rad = percent * 2*Math.PI;

            //Vertex position
            double outer_x = center_x + radius * Math.cos(rad);
            double outer_z = center_z + radius * Math.sin(rad);

            vertices[idx++] = (float)outer_x;
            vertices[idx++] = 0.0f;
            vertices[idx++] = (float)outer_z;
        }


        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

    }

    @Override
    public void init() {

    }

    @Override
    public void draw(int programId, float[] mMatrix) {
        GLES20.glUseProgram(programId);

        int uColorLocation = glGetUniformLocation(programId, "u_Color");
        int aPositionLocation = glGetAttribLocation(programId, "a_Position");
        int uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");

        GLES20.glEnableVertexAttribArray(aPositionLocation);

        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);

        // Axis width
        glLineWidth(5);
        //X axis
        glUniform4f(
                uColorLocation,
                rgba[0],
                rgba[1],
                rgba[2],
                rgba[3]
        );
        glDrawArrays(GL_TRIANGLE_FAN, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(aPositionLocation);
    }
}
