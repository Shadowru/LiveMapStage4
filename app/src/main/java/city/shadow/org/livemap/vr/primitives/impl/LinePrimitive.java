package city.shadow.org.livemap.vr.primitives.impl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import city.shadow.org.livemap.vr.primitives.IOpenGLPrimitive;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform4f;

public class LinePrimitive implements IOpenGLPrimitive {

    private static final int POSITION_COUNT = 3;
    private FloatBuffer vertexBuffer;
    private float[] rgba = {0.1f, 0.1f, 0.1f, 0.3f};

    public LinePrimitive(float x0, float y0, float z0, float x1, float y1, float z1) {

        float[] vertices = {
                x0, y0, z0,
                x1, y1, z1,
        };

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
        glDrawArrays(GL_LINES, 0, 2);

        GLES20.glDisableVertexAttribArray(aPositionLocation);

    }

    public void setColor(float r, float g, float b, float a) {
        rgba[0] = r;
        rgba[1] = g;
        rgba[2] = b;
        rgba[3] = a;
    }
}
