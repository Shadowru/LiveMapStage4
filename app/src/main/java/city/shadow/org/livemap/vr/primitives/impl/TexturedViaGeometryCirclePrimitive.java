package city.shadow.org.livemap.vr.primitives.impl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import city.shadow.org.livemap.vr.primitives.IOpenGLPrimitive;

public class TexturedViaGeometryCirclePrimitive implements IOpenGLPrimitive {

    private static final int POSITION_COUNT = 3;
    private final int outerVertexCount;
    private final int vertexCount;

    private final FloatBuffer vertexBuffer;

    private final float center_x = 0.0f;
    private final float center_z = 0.0f;
    private int textureHandler;

    public TexturedViaGeometryCirclePrimitive(float radius) {
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

            float fi = (i / (float) (outerVertexCount-1));
            double rad = fi * 2*Math.PI;

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

    }

    public void setTextureHandler(int textureHandler) {
        this.textureHandler = textureHandler;
    }
}
