package org.shadow.livecity.files.fileformat;

public class BuildingFileV1 implements BuildingFile {

    private static final long serialVersionUID = 5955148066138169859L;

    private final int minorVersion = 1;
    private final int majorVersion = 0;

    private float[] vertextCoordBuffer;
    private float[] normalsCoordBuffer;
    private float[] textureCoordBuffer;

    private short[] indexBuffer;

    public float[] getVertextCoordBuffer() {
        return vertextCoordBuffer;
    }

    public void setVertextCoordBuffer(float[] vertextCoordBuffer) {
        this.vertextCoordBuffer = vertextCoordBuffer;
    }

    public float[] getNormalsCoordBuffer() {
        return normalsCoordBuffer;
    }

    public void setNormalsCoordBuffer(float[] normalsCoordBuffer) {
        this.normalsCoordBuffer = normalsCoordBuffer;
    }

    public float[] getTextureCoordBuffer() {
        return textureCoordBuffer;
    }

    public void setTextureCoordBuffer(float[] textureCoordBuffer) {
        this.textureCoordBuffer = textureCoordBuffer;
    }

    public short[] getIndexBuffer() {
        return indexBuffer;
    }

    public void setIndexBuffer(short[] indexBuffer) {
        this.indexBuffer = indexBuffer;
    }
}
