package city.shadow.org.livemap.vr.files;

import java.util.StringTokenizer;

class ObjTriple {


    private final Integer v;
    private final Integer vt;
    private final Integer vn;

    public ObjTriple(Integer v, Integer vt, Integer vn) {
        this.v = v;
        this.vt = vt;
        this.vn = vn;
    }

    public Integer getV() {
        return v;
    }

    public Integer getVt() {
        return vt;
    }

    public Integer getVn() {
        return vn;
    }

    public static ObjTriple parseTriple(String lineSection) {

        final StringTokenizer stringTokenizer = new StringTokenizer(lineSection, "/", true);
        String sV = stringTokenizer.nextToken();
        stringTokenizer.nextToken();
        String sVt = stringTokenizer.nextToken();
        stringTokenizer.nextToken();
        String sVn = stringTokenizer.nextToken();

        Integer vt = null;
        if(sVt.length() > 0){
           vt = Integer.parseInt(sVt);
        }

        Integer vn = null;
        if(sVn.length() > 0){
            vn = Integer.parseInt(sVn);
        }

        return new ObjTriple(
                Integer.parseInt(sV),
                vt,
                vn
        );
    }
}
