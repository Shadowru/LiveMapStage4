package city.shadow.org.livemap.vr.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import java.io.IOException;
import java.io.InputStream;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

public class TextureUtils {

    private static String[] postfix = {"Left2048.png", "Right2048.png", "Down2048.png", "Up2048.png", "Front2048.png", "Back2048.png"};

    public static int loadCubeTexture(Context context, String cubeTexturePrefix) throws IOException {

        glActiveTexture(GL_TEXTURE0);


        final int[] textureIds = new int[1];
        glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return 0;
        }

        GLES20.glBindTexture( GLES20.GL_TEXTURE_CUBE_MAP, textureIds[ 0 ]);

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);


        final Bitmap[] cubeBitmaps = new Bitmap[6];


        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        for(int i=0;i <cubeBitmaps.length;i++){
            cubeBitmaps[i] = loadBitmap(context, cubeTexturePrefix + postfix[i]);
        }


        //LEFT
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
        //RIGHT
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);

        //BOTTOM
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
        //TOP
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);

        //FRONT
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
        //BACK
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);

        glBindTexture(GL_TEXTURE_2D, 0);

        // format cube map texture
        glTexParameteri( GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S,     GLES20.GL_CLAMP_TO_EDGE);
        glTexParameteri( GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T,     GLES20.GL_CLAMP_TO_EDGE);

        for (Bitmap bitmap : cubeBitmaps) {
            bitmap.recycle();
        }

        return textureIds[0];
    }

    public static boolean loadCubeMapSide(
            Context context, int sideTarget, String fileName) throws IOException {

        Bitmap bitmap = loadBitmap(context, fileName);

        if (bitmap == null) {
            return false;
        }

        // copy image data into 'target' side of cube map
        texImage2D(sideTarget, 0, bitmap, 0);

        bitmap.recycle();
        return true;
    }


    public static int loadTexture(Context context, String fileName) throws IOException {

        final int[] textureIds = new int[1];
        glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return 0;
        }

        Bitmap bitmap = loadBitmap(context, fileName);

        if (bitmap == null) {
            glDeleteTextures(1, textureIds, 0);
            return 0;
        }

        // настройка объекта текстуры
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureIds[0]);

        glTexParameteri(GLES20.GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GLES20.GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();

        glBindTexture(GL_TEXTURE_2D, 0);

        return textureIds[0];
    }

    private static Bitmap loadBitmap(Context context, String fileName) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        InputStream inputStream = context.getAssets().open(
                fileName,
                AssetManager.ACCESS_STREAMING
        );

        final Bitmap bitmap = BitmapFactory.decodeStream(
                inputStream,
                null,
                options
        );

        inputStream.close();

        return bitmap;
    }
}