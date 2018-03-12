package city.shadow.org.livemap.vr.primitives.impl;

import android.content.Context;
import android.opengl.GLES20;

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

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glUseProgram;

public class TexturedSquare implements IOpenGLPrimitive {

    private final float[] vertices;
    private final short[] indices;
    private final float [] uvs;
    private final int textureProgramId;
    private final int textureHandler;

    public FloatBuffer vertexBuffer;
    public ShortBuffer drawListBuffer;
    public FloatBuffer uvBuffer;

    private float radius = 1.0f;

    public TexturedSquare(Context context, float sz) {

        radius = sz;

        String shader = FileUtils.readFile(context, "shaders/texture_vertex_shader.glsl");
        int textureVertexShader = ShaderUtils.createShader(GL_VERTEX_SHADER, shader);

        shader = FileUtils.readFile(context, "shaders/texture_fragment_shader.glsl");
        int textureFragmentShader = ShaderUtils.createShader(GL_FRAGMENT_SHADER, shader);

        textureProgramId = OpenGLUtils.createProgram(textureVertexShader, textureFragmentShader);


        // We have to create the vertices of our triangle.
        vertices = new float[]
                {-sz, 0.0f, -sz,
                        -sz, 0.0f, sz,
                        sz, 0.0f, sz,
                        sz, 0.0f, -sz,
                };

        indices = new short[]{0, 1, 2, 0, 2, 3}; // The order of vertex rendering.

        // Create our UV coordinates.
        uvs = new float[] {
                0.0f, 0.0f,
                0.0f, sz,
                sz, sz,
                sz, 0.0f
        };

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

        // The texture buffer
        ByteBuffer uvbb = ByteBuffer.allocateDirect(uvs.length * 4);
        uvbb.order(ByteOrder.nativeOrder());
        uvBuffer = uvbb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        textureHandler = loadTexture(context);
    }

    private int loadTexture(Context context) {
        try {
            //int textureHandler = TextureUtils.loadTexture(context, "textures/TexturesCom_FloorsMixedSize0030_1_seamless_S.jpg");
            //int textureHandler = TextureUtils.loadTexture(context, "textures/TexturesCom_FloorStreets0100_1_seamless_S.jpg");
            int textureHandler = TextureUtils.loadTexture(context, "textures/TexturesCom_MarbleTiles0163_1_M.jpg");
            //int textureHandler = TextureUtils.loadTexture(context, "textures/TexturesCom_FloorStreets0098_1_seamless_S.jpg");
            //int textureHandler = TextureUtils.loadTexture(context, "textures/TexturesCom_ConcreteBare0433_11_seamless_S.jpg");


            OpenGLUtils.checkGLError("Load textureHandler");

            return textureHandler;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }


    @Override
    public void init() {

    }

    @Override
    public void draw(int programId, float[] mMatrix) {

        glUseProgram(this.textureProgramId);
        glActiveTexture(GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_2D, textureHandler);

        int mRadiusHandle =
                GLES20.glGetAttribLocation(textureProgramId, "vRadius");

        GLES20.glUniform1f(mRadiusHandle, radius);

        // get handle to vertex shader'sz vPosition member
        int mPositionHandle =
                GLES20.glGetAttribLocation(textureProgramId, "vPosition");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        // Get handle to texture coordinates location
        int mTexCoordLoc = GLES20.glGetAttribLocation(textureProgramId,
                "a_texCoord" );

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
                false,
                0, uvBuffer);

        // Get handle to shape'sz transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(textureProgramId,
                "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, mMatrix, 0);

        // Get handle to textures locations
        int mSamplerLoc = GLES20.glGetUniformLocation (textureProgramId,
                "s_texture" );

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i ( mSamplerLoc, 0);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
    }
}
