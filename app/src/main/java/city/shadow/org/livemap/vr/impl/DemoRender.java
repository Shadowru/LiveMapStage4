package city.shadow.org.livemap.vr.impl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

import city.shadow.org.livemap.utils.FileUtils;
import city.shadow.org.livemap.vr.intf.CityRender;
import city.shadow.org.livemap.vr.primitives.ObjectModel;
import city.shadow.org.livemap.vr.primitives.impl.CirclePrimitive;
import city.shadow.org.livemap.vr.primitives.impl.LinePrimitive;
import city.shadow.org.livemap.vr.primitives.impl.SkyBoxPrimitive;
import city.shadow.org.livemap.vr.primitives.impl.TexturedSquare;
import city.shadow.org.livemap.vr.utils.OpenGLUtils;
import city.shadow.org.livemap.vr.utils.ShaderUtils;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;

public class DemoRender implements CityRender {

    private final static int POSITION_COUNT = 3;

    private final GvrActivity gvrActivity;

    // cam position
    private float eyeX = 0.0f;
    private float eyeY = 2.8f;
    private float eyeZ = 0.0f;

    // cam target position
    private float centerX = 0.0f;
    private float centerY = 20.0f;
    private float centerZ = 1000.0f;

    // up-vector
    private final float upX = 0;
    private final float upY = 1;
    private final float upZ = 0;

    //Frustrum settings
    private final float near = 0.1f;
    private final float far = (float)Math.sqrt(Math.pow(1000d, 2) + Math.pow(1000d, 2));
    float sz = far;//(float)(31^2);

    int colorsProgramId;

    private float[] camera = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMatrix = new float[16];

    private float[] rotateMatrix = new float[16];

    private LinePrimitive xAxis;
    private LinePrimitive zAxis;

    private TexturedSquare texturedSquare;
    private SkyBoxPrimitive skyBox;
    private ObjectModel objectModel;

    public DemoRender(GvrActivity gvrActivity) {
        this.gvrActivity = gvrActivity;
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f);

        String shader = FileUtils.readFile(getContext(), "shaders/solidcolor_fragment_shader.glsl");
        int solidcolorFragmentShader = ShaderUtils.createShader(GL_FRAGMENT_SHADER, shader);

        shader = FileUtils.readFile(getContext(), "shaders/solidcolor_vertex_shader.glsl");
        int solidcolorVertexShader = ShaderUtils.createShader(GL_VERTEX_SHADER, shader);

        colorsProgramId = OpenGLUtils.createProgram(solidcolorVertexShader, solidcolorFragmentShader);

        Matrix.setIdentityM(rotateMatrix, 0);

        //POT

        //X axis
        xAxis = new LinePrimitive(-sz, 0.1f, 0, sz, 0.1f, 0);
        xAxis.setColor(1.0f, 0.0f, 0.0f, 1.0f);

        // Z axis
        zAxis = new LinePrimitive(0, 0.1f, -sz, 0, 0.1f, sz);
        zAxis.setColor(0.0f, 0.0f, 1.0f, 1.0f);

        texturedSquare = new TexturedSquare(getContext(), sz);

        skyBox = new SkyBoxPrimitive(getContext());
        skyBox.init();

        objectModel = new ObjectModel(getContext(), "objects/hangar (1).obj.conv", "objects/TexturesCom_MetalGalvanized0049_3_M.jpg");
        objectModel.init();

        float[] modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, 270.0f, 1, 0, 0);
        Matrix.translateM(modelMatrix, 0, 0, -550.0f, 0.0f);//-65f);

        objectModel.setModelMatrix(modelMatrix);


    }



    private Context getContext() {
        return gvrActivity.getApplicationContext();
    }

    @Override
    public void onSurfaceChanged(int i, int i1) {

    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        createViewMatrix();
    }

    @Override
    public void onDrawEye(Eye eye) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        proceedMatrix(mMatrix, eye);

        renderSkyBox(mMatrix);

        renderAxis(mMatrix);

        renderEarth(mMatrix);

        renderCity(mMatrix);

    }

    private void renderCity(float[] mMatrix) {

        eyeZ += 0.03f;
        centerZ += 0.03f;

        objectModel.draw(colorsProgramId, mMatrix);
    }

    private void renderSkyBox(float[] mMatrix) {
        skyBox.draw(colorsProgramId, mMatrix);
    }

    private void renderEarth(float[] mMatrix) {
        texturedSquare.draw(-1, mMatrix);
    }

    private void renderAxis(float[] mMatrix) {

        xAxis.draw(colorsProgramId, mMatrix);

        zAxis.draw(colorsProgramId, mMatrix);

    }

    private void createViewMatrix() {
        Matrix.setLookAtM(camera, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    private void proceedMatrix(float[] mMatrix, Eye eyeView) {

        mProjectionMatrix = eyeView.getPerspective(near, far);

        Matrix.multiplyMM(mViewMatrix, 0, eyeView.getEyeView(), 0, camera, 0);
        Matrix.multiplyMM(mViewMatrix, 0, mViewMatrix, 0, rotateMatrix, 0);
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onRendererShutdown() {

    }


}
