package city.shadow.org.livemap.vr.primitives;

import android.content.Context;

import org.shadow.livecity.files.fileformat.BuildingFileV1;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

class ConvFileLoader {

    private static final String TAG = "ConvFileLoader";
    private final BuildingFileV1 buildingFileV1;

    public ConvFileLoader(Context context, String fileName) throws IOException, ClassNotFoundException {
            InputStream is = context.getAssets().open(fileName);

            ObjectInputStream ois = new ObjectInputStream(is);

            buildingFileV1 = (BuildingFileV1) ois.readObject();

            is.close();
    }


    public int getVertexSize() {
        return buildingFileV1.getVertextCoordBuffer().length;
    }

    public void fillVertexBuffer(FloatBuffer drawVertexBuffer) {
        drawVertexBuffer.put(buildingFileV1.getVertextCoordBuffer());
    }

    public int getNormalSize() {
        return buildingFileV1.getNormalsCoordBuffer().length;
    }

    public void fillNormalsBuffer(FloatBuffer drawNormalsBuffer) {
        drawNormalsBuffer.put(buildingFileV1.getNormalsCoordBuffer());
    }


    public int getTextureSize() {
        return buildingFileV1.getTextureCoordBuffer().length;
    }

    public void fillTextureCorrdsBuffer(FloatBuffer textureCoordsBuffer) {
        textureCoordsBuffer.put(buildingFileV1.getTextureCoordBuffer());
    }

    public int getIndicesSize() {
        return buildingFileV1.getIndexBuffer().length;
    }

    public void fillIndicesBuffer(ShortBuffer drawIndicesBuffer) {
        drawIndicesBuffer.put(buildingFileV1.getIndexBuffer());
    }
}
