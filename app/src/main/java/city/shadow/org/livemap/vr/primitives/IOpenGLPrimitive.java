package city.shadow.org.livemap.vr.primitives;

public interface IOpenGLPrimitive {

    public void init();

    public void draw(int programId, float[] mMatrix);

}
