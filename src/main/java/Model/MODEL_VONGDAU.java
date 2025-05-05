package Model;


import java.sql.Date;

public class MODEL_VONGDAU {
    private int maVD,tenVD,maMG;
    private Date ngayBD,ngayKT;
    public MODEL_VONGDAU() {}

    public int getMaVD() {
        return maVD;
    }

    public void setMaVD(int maVD) {
        this.maVD = maVD;
    }

    public int getTenVD() {
        return tenVD;
    }

    public void setTenVD(int tenVD) {
        this.tenVD = tenVD;
    }

    public int getMaMG() {
        return maMG;
    }

    public void setMaMG(int maMG) {
        this.maMG = maMG;
    }

    public Date getNgayBD() {
        return ngayBD;
    }

    public void setNgayBD(Date ngayBD) {
        this.ngayBD = ngayBD;
    }

    public Date getNgayKT() {
        return ngayKT;
    }

    public void setNgayKT(Date ngayKT) {
        this.ngayKT = ngayKT;
    }
}
