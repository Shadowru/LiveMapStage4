package city.shadow.org.livemap.vr.utils;

import android.opengl.GLES20;
import android.util.Log;

import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glLinkProgram;

public class OpenGLUtils {

    private static final String TAG = "OpenGLUtils";

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     *
     * @param label Label to report in case of error.
     */
    public static void checkGLError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }

    public static int createProgram(int vertexShader, int fragmentShader) {
        int programId = GLES20.glCreateProgram();
        glAttachShader(programId, vertexShader);
        checkGLError("Vertex Shader");
        glAttachShader(programId, fragmentShader);
        checkGLError("Fragment Shader");
        glLinkProgram(programId);

        checkGLError("Link program");
        return programId;
    }
}
