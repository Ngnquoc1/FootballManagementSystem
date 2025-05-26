package Model;

public class MODEL_VITRITD {
    private int maViTri;
    private String tenViTri;

    public MODEL_VITRITD() {
    }

    public MODEL_VITRITD(int maViTri, String tenViTri) {
        this.maViTri = maViTri;
        this.tenViTri = tenViTri;
    }

    public int getMaViTri() {
        return maViTri;
    }

    public void setMaViTri(int maViTri) {
        this.maViTri = maViTri;
    }

    public String getTenViTri() {
        return tenViTri;
    }

    public void setTenViTri(String tenViTri) {
        this.tenViTri = tenViTri;
    }
}
