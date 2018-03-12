package city.shadow.org.livemap.vr.primitives.impl;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import city.shadow.org.livemap.utils.FileUtils;
import city.shadow.org.livemap.vr.primitives.IOpenGLPrimitive;
import city.shadow.org.livemap.vr.utils.OpenGLUtils;
import city.shadow.org.livemap.vr.utils.ShaderUtils;
import city.shadow.org.livemap.vr.utils.TextureUtils;

import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUseProgram;

public class SkyBoxPrimitive implements IOpenGLPrimitive {

    private static final String TAG = "SkyBoxPrimitive";

    private static final int POSITION_COUNT = 3;

    static final float sz = 1000f;

    // the idea is that all vectors have a length of 1 so that position can also be used as cubemap texture coords
    float[] vertices = new float[]{
            -sz,  sz,  sz,     // (0) Top-left near
            sz,  sz,  sz,     // (sz) Top-right near
            -sz, -sz,  sz,     // (2) Bottom-left near
            sz, -sz,  sz,     // (3) Bottom-right near
            -sz,  sz, -sz,     // (4) Top-left far
            sz,  sz, -sz,     // (5) Top-right far
            -sz, -sz, -sz,     // (6) Bottom-left far
            sz, -sz, -sz      // (7) Bottom-right far 
    };

    static final float TextureArray[] = {
            0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,    // front
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, // back
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // top
            0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, // bottom
            0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // left
            1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f  // right
    };

    static final short indices[] = {// Front
            1, 3, 0,
            0, 3, 2,

            // Back
            4, 6, 5,
            5, 6, 7,

            // Left
            0, 2, 4,
            4, 2, 6,

            // Right
            5, 7, 1,
            1, 7, 3,

            // Top
            5, 1, 4,
            4, 1, 0,

            // Bottom
            6, 2, 7,
            7, 2, 3
    };

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    private Context context;
    private int cubeTexture;
    private int skyboxProgramId;

    public SkyBoxPrimitive(Context context) {
        this.context = context;
    }

    @Override
    public void init() {

        String shader = FileUtils.readFile(context, "shaders/cube_vertex_shader.glsl");
        int skyBoxVertexShader = ShaderUtils.createShader(GL_VERTEX_SHADER, shader);

        shader = FileUtils.readFile(context, "shaders/cube_fragment_shader.glsl");
        int skyBoxFragmentShader = ShaderUtils.createShader(GL_FRAGMENT_SHADER, shader);

        skyboxProgramId = OpenGLUtils.createProgram(skyBoxVertexShader, skyBoxFragmentShader);

        OpenGLUtils.checkGLError("Skybox program");

        // The vertex buffer.
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(indices);
        drawListBuffer.position(0);

        //Load cube

        try {
            loadCube();
            OpenGLUtils.checkGLError("Load cube texture");
        } catch (IOException e) {
            Log.e(TAG, "Load Cube", e);
        }

    }

    private void loadCube() throws IOException {

        cubeTexture = TextureUtils.loadCubeTexture(
                context,
                "skybox/SunSet"
        );

    }

    @Override
    public void draw(int programId, float[] mMatrix) {

        glUseProgram(skyboxProgramId);

        glDisable(GL_DEPTH_TEST);   // skybox should be drawn behind anything else

        int uMatrixLocation = glGetUniformLocation(skyboxProgramId, "u_Matrix");
        int aPositionLocation = glGetAttribLocation(skyboxProgramId, "a_Position");

        int u_TextureUnit = glGetAttribLocation(skyboxProgramId, "u_TextureUnit");

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubeTexture);

        GLES20.glUniform1i(u_TextureUnit, 0);

        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(aPositionLocation);

        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);


        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(aPositionLocation);

        glEnable(GL_DEPTH_TEST);
    }
}
