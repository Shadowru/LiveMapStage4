package city.shadow.org.livemap.vr.files;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class ObjFileLoader {

    private static final String TAG = "ObjFileLoader";

    private static final java.lang.String COMMENT_PREFIX = "#";

    private ArrayList<Float> vertexCoords = new ArrayList<>();
    private ArrayList<Float> vertexNormals = new ArrayList<>();
    private ArrayList<Float> textureCoord = new ArrayList<>();

    private ArrayList<Short> vertexDrawOrderBuffer = new ArrayList<>();
    private ArrayList<Float> vertexNormalsBuffer = new ArrayList<>();

    private int vertexCount;
    private int textureCoordsCount;
    private int normalsCount;
    private int faceCount;
    private int drawOrderBufferSize;

    public ObjFileLoader(Context context, String fileName) {

        vertexCount = 0;
        textureCoordsCount = 0;
        normalsCount = 0;
        faceCount = 0;

        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(
                    fileName,
                    AssetManager.ACCESS_STREAMING
            );

            loadOBJModel(inputStream);

            inputStream.close();

            printInfo();

        } catch (IOException e) {
            Log.e(TAG, "Load OBJ model", e);
        }

    }


    private void loadOBJModel(InputStream inputStream) {

        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line = null;

        try {

            final ObjLineParser lineParser = new ObjLineParser();

            while ((line = bufferedReader.readLine()) != null) {


                line = line.trim();

                if (line.startsWith(COMMENT_PREFIX)) {
                    continue;
                }

                if (lineParser.parse(line)) {

                    if (lineParser.isVertex()) {
                        addVertex(lineParser);
                    } else if (lineParser.isNormals()) {
                        addNormal(lineParser);
                    } else if (lineParser.isTextureCoordinate()) {
                        addTextureCoordinate(lineParser);
                    } else if (lineParser.isFace()) {
                        addFace(lineParser);
                    }
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "Load OBJ Model error. Line : " + line, e);
        }

        drawOrderBufferSize = vertexDrawOrderBuffer.size();

    }

    public int getTextureCoordsCount() {
        return textureCoordsCount;
    }

    private void addTextureCoordinate(ObjLineParser lineParser) {
        textureCoordsCount++;
        //X
        textureCoord.add(
                lineParser.getNextFloat()
        );
        //Y
        textureCoord.add(
                lineParser.getNextFloat()
        );
    }

    public int getNormalsCount() {
        return normalsCount;
    }

    private void addNormal(ObjLineParser lineParser) {
        normalsCount++;
        //X
        vertexNormals.add(
                lineParser.getNextFloat()
        );
        //Y
        vertexNormals.add(
                lineParser.getNextFloat()
        );
        //Z
        vertexNormals.add(
                lineParser.getNextFloat()
        );
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private void addVertex(ObjLineParser lineParser) {
        vertexCount++;
        //X
        vertexCoords.add(
                lineParser.getNextFloat()
        );
        //Y
        vertexCoords.add(
                lineParser.getNextFloat()
        );
        //Z
        vertexCoords.add(
                lineParser.getNextFloat()
        );
    }

    private void printInfo() {

        Log.i(TAG, "Vertices count : " + getVertexCount());
        Log.i(TAG, "Normals count : " + getNormalsCount());
        Log.i(TAG, "Texture coords count : " + getTextureCoordsCount());
        Log.i(TAG, "Faces count : " + getFaceCount());
        Log.i(TAG, "Draw order buffer size : " + getDrawOrderBufferSize());

    }

    public int getFaceCount() {
        return faceCount;
    }

    public int getDrawOrderBufferSize() {
        return drawOrderBufferSize;
    }

    private void addFace(ObjLineParser lineParser) {

        faceCount++;

        //Zero point
        ObjTriple objTripleZero = lineParser.getNextTriple();
        addIndexes(objTripleZero);

        ObjTriple objTriplePoint1 = lineParser.getNextTriple();
        addIndexes(objTriplePoint1);

        ObjTriple objTriplePoint2 = lineParser.getNextTriple();
        addIndexes(objTriplePoint2);

        objTriplePoint1 = objTriplePoint2;

        while((objTriplePoint2 = lineParser.getNextTriple()) != null){

            addIndexes(objTripleZero);
            addIndexes(objTriplePoint1);
            addIndexes(objTriplePoint2);
            objTriplePoint1 = objTriplePoint2;
        }

    }

    private void addIndexes(ObjTriple objTriple) {
        vertexDrawOrderBuffer.add(
                translateIndex(objTriple.getV()).shortValue()
        );
    }

    private Integer translateIndex(Integer v) {
        if(v < 0){
            return vertexCount - v;
        }
        return v-1;
    }


/*
    private void parceFace(String line) {
        String[] splittedFace = line.split(" ");

        //TODO:rewrite piece of junk
        for (int i = 0; i < 3; i++) {
            int vertexId = getVertexIndex(splittedFace[i]);
            vertexIndices.add(
                    translateIndex(vertexId).shortValue()
            );
            int vertexNormalsId = getNormalsIndex(splittedFace[i]);
            vertexNormalIndices.add(
                    vertexNormals.get(translateIndex(vertexNormalsId))
            );
        }

        for (int i = 3; i < splittedFace.length; i++) {
            int vertexId1 = getVertexIndex(splittedFace[i - 3]);
            int vertexNormalsId1 = getNormalsIndex(splittedFace[i - 3]);

            int vertexId2 = getVertexIndex(splittedFace[i - 1]);
            int vertexNormalsId2 = getNormalsIndex(splittedFace[i - 1]);

            int vertexId3 = getVertexIndex(splittedFace[i]);
            int vertexNormalsId3 = getNormalsIndex(splittedFace[i]);

            vertexIndices.add(
                    translateIndex(vertexId1).shortValue()
            );
            vertexIndices.add(
                    translateIndex(vertexId2).shortValue()
            );
            vertexIndices.add(
                    translateIndex(vertexId3).shortValue()
            );

            vertexNormalIndices.add(
                    vertexNormals.get(translateIndex(vertexNormalsId1))
            );
            vertexNormalIndices.add(
                    vertexNormals.get(translateIndex(vertexNormalsId2))
            );
            vertexNormalIndices.add(
                    vertexNormals.get(translateIndex(vertexNormalsId3))
            );
        }

    }

    private int getVertexIndex(String textIndexes) {
        int coordsIdx = textIndexes.indexOf('/');
        if (coordsIdx != -1) {
            return Integer.parseInt(textIndexes.substring(0, coordsIdx));
        }
        return Integer.parseInt(textIndexes);
    }

    private int getNormalsIndex(String textIndexes) {
        int coordsIdx = textIndexes.lastIndexOf('/');
        if (coordsIdx != -1) {
            return Integer.parseInt(textIndexes.substring(coordsIdx + 1));
        }
        return Integer.parseInt(textIndexes);
    }
*/

    public void fillVertexBuffer(FloatBuffer drawVertexBuffer) {
        for(Float f : vertexCoords)
        {
            drawVertexBuffer.put( f );
        }
    }

    public void fillNormalsBuffer(FloatBuffer drawNormalsBuffer) {
        for(Float f : vertexNormals)
        {
            drawNormalsBuffer.put( f );
        }
    }

    public void fillDrawOrderBuffer(ShortBuffer drawIndicesBuffer) {
        for (Short vertexIndex : vertexDrawOrderBuffer) {
            drawIndicesBuffer.put(vertexIndex);
        }
    }

    public void fillTextureCorrdsBuffer(FloatBuffer textureCoordsBuffer) {
        for(Float f : textureCoord)
        {
            textureCoordsBuffer.put( f );
        }
    }
}
