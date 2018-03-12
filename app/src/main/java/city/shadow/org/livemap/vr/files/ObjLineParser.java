package city.shadow.org.livemap.vr.files;

class ObjLineParser {

    private static final String TAG = "OBJLINEPARSER";
    private static final int PREFIX_INDEX = 0;

    private static final java.lang.String PREFIX_VERTEX = "v";
    private static final java.lang.String PREFIX_NORMALS = "vn";
    private static final java.lang.String PREFIX_TEXTURES = "vt";
    private static final java.lang.String PREFIX_FACES = "f";

    private String[] lineSections;
    private int idx;

    public boolean parse(String line) {

        lineSections = line.split("\\s+");
        if(lineSections.length < 4){
            //Log.d(TAG, "Bad line : " + line);
            return false;
        }
        idx = 1;
        return true;
    }

    public boolean isVertex(){
        return lineSections[PREFIX_INDEX].equals(PREFIX_VERTEX);
    }

    public boolean isFace(){
        return lineSections[PREFIX_INDEX].equals(PREFIX_FACES);
    }

    public boolean isTextureCoordinate(){
        return lineSections[PREFIX_INDEX].equals(PREFIX_TEXTURES);
    }

    public boolean isNormals(){
        return lineSections[PREFIX_INDEX].equals(PREFIX_NORMALS);
    }

    public Float getNextFloat() {
        if(idx >= lineSections.length){
            return null;
        }

        return Float.parseFloat(
                lineSections[idx++]
        );
    }

    public Integer getNextInt() {
        if(idx >= lineSections.length){
            return null;
        }

        return Integer.parseInt(
                lineSections[idx++]
        );
    }

    public ObjTriple getNextTriple() {
        if(idx >= lineSections.length){
            return null;
        }
        return ObjTriple.parseTriple(
                lineSections[idx++]
        );
    }
}
