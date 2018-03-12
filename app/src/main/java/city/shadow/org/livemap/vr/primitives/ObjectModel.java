package city.shadow.org.livemap.vr.primitives;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import city.shadow.org.livemap.utils.FileUtils;
import city.shadow.org.livemap.vr.utils.OpenGLUtils;
import city.shadow.org.livemap.vr.utils.ShaderUtils;
import city.shadow.org.livemap.vr.utils.TextureUtils;

import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform4f;

public class ObjectModel implements IOpenGLPrimitive {

    public static final String TAG = "ObjectModel";

    private static final int POSITION_COUNT = 3;
    private String textureName;


    private FloatBuffer drawVertexBuffer;
    private FloatBuffer drawNormalsBuffer;
    private ShortBuffer drawIndicesBuffer;


    private final Context context;
    private final String fileName;

    private float[] rgba = {
            253f/256f,
            204f/256f,
            170f/256f,
            1.0f
    };

    private float[] modelMatrix;

    private int drawProgramId;
    private int textureHandler;
    private int drawOrderSize;
    private FloatBuffer textureCoordsBuffer;

    public ObjectModel(Context context, String fileName) {
        this(context, fileName, null);
    }

    public ObjectModel(Context context, String fileName, String textureName) {
        this.context = context;
        this.fileName = fileName;
        this.textureName = textureName;
    }

    @Override
    public void init() {

        ConvFileLoader objFileLoader = null;
        try {

            objFileLoader = new ConvFileLoader(context, fileName);
            allocateDrawBuffers(objFileLoader);

        } catch (IOException e) {
            Log.e(TAG, "IO error", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "ClassNotFoundException", e);
        }


        drawProgramId = loadProgram();

        textureHandler = loadTexture(context);

    }

    public void setModelMatrix(float[] modelMatrix){
        this.modelMatrix = modelMatrix;
    };

    private int loadTexture(Context context) {
        try {

            if(textureName == null){
                textureName = "textures/TexturesCom_ConcreteBare0433_11_seamless_S.jpg";
            }

            int textureHandler = TextureUtils.loadTexture(context, textureName);

            OpenGLUtils.checkGLError("Load textureHandler");

            return textureHandler;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private int loadProgram() {

        String shader = FileUtils.readFile(context, "shaders/solidcolor_point_light_vertex_shader.glsl");
        int textureVertexShader = ShaderUtils.createShader(GL_VERTEX_SHADER, shader);

        shader = FileUtils.readFile(context, "shaders/solidcolor_point_light_fragment_shader.glsl");
        int textureFragmentShader = ShaderUtils.createShader(GL_FRAGMENT_SHADER, shader);

        return OpenGLUtils.createProgram(textureVertexShader, textureFragmentShader);

    }



    private void allocateDrawBuffers(ConvFileLoader objFileLoader) {

        ByteBuffer bb = ByteBuffer.allocateDirect( objFileLoader.getVertexSize() * 4);
        bb.order( ByteOrder.nativeOrder() );
        drawVertexBuffer = bb.asFloatBuffer();
        objFileLoader.fillVertexBuffer(drawVertexBuffer);
        drawVertexBuffer.position(0);

        ByteBuffer nbb = ByteBuffer.allocateDirect( objFileLoader.getNormalSize() *4);
        nbb.order( ByteOrder.nativeOrder() );
        drawNormalsBuffer = nbb.asFloatBuffer();
        objFileLoader.fillNormalsBuffer(drawNormalsBuffer);
        drawNormalsBuffer.position(0);

        ByteBuffer tbb = ByteBuffer.allocateDirect( objFileLoader.getTextureSize() * 4);
        tbb.order( ByteOrder.nativeOrder() );
        textureCoordsBuffer = tbb.asFloatBuffer();
        objFileLoader.fillTextureCorrdsBuffer(textureCoordsBuffer);
        textureCoordsBuffer.position(0);

        drawOrderSize = objFileLoader.getIndicesSize();

        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrderSize * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawIndicesBuffer = dlb.asShortBuffer();
        objFileLoader.fillIndicesBuffer(drawIndicesBuffer);
        drawIndicesBuffer.position(0);

    }


    @Override
    public void draw(int nullId, float[] mMatrix) {

        int programId = drawProgramId;

        GLES20.glDisable(GL_CULL_FACE);

        GLES20.glUseProgram(programId);

        int uColorLocation = glGetUniformLocation(programId, "u_Color");
        OpenGLUtils.checkGLError("glGetUniformLocation");

        int uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
        OpenGLUtils.checkGLError("glGetUniformLocation");

        int uModelMatrixLocation = glGetUniformLocation(programId, "m_Matrix");
        OpenGLUtils.checkGLError("glGetUniformLocation");

        int aPositionLocation = glGetAttribLocation(programId, "a_Position");

        GLES20.glEnableVertexAttribArray(aPositionLocation);

        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GLES20.GL_FLOAT, false, 0, drawVertexBuffer);

        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(uModelMatrixLocation, 1, false, modelMatrix, 0);

        glUniform4f(
                uColorLocation,
                rgba[0],
                rgba[1],
                rgba[2],
                rgba[3]
        );

        int uLightPos = glGetUniformLocation(programId, "u_LightPos");
        OpenGLUtils.checkGLError("glGetUniformLocation");

        glUniform3f(
                uLightPos,
                -140.0f,
                10.0f,
                -400.0f
        );

        int mNormalHandle = GLES20.glGetAttribLocation(programId, "a_Normal");
        OpenGLUtils.checkGLError("glGetAttribLocation");

        GLES20.glEnableVertexAttribArray(mNormalHandle);
        OpenGLUtils.checkGLError("glEnableVertexAttribArray");

        // Pass in the normal information
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 0, drawNormalsBuffer);

        int s_texture = glGetUniformLocation(programId, "s_texture");
        glUniform1i(s_texture, 0);

        glActiveTexture(GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_2D, textureHandler);

        int mTexCoordLoc = GLES20.glGetAttribLocation(programId,
                "a_texCoord" );

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
                false,
                0, textureCoordsBuffer);

        GLES20.glLineWidth(1.0f);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrderSize,
                GLES20.GL_UNSIGNED_SHORT, drawIndicesBuffer);

        GLES20.glDisableVertexAttribArray(mTexCoordLoc);
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(mNormalHandle);

    }


    public void incYAxis() {
        Matrix.translateM(modelMatrix, 0, 0, 0.1f, 0.0f);//-65f);
    }
}
