package Model;

import java.util.Date;

public class MODEL_MUAGIAI {
    private int maMG;
    private String tenMG;
    private Date ngayBD,ngayKT;

    public Date getNgayKT() {
        return ngayKT;
    }

    public void setNgayKT(Date ngayKT) {
        this.ngayKT = ngayKT;
    }

    public int getMaMG() {
        return maMG;
    }

    public void setMaMG(int maMG) {
        this.maMG = maMG;
    }

    public String getTenMG() {
        return tenMG;
    }

    public void setTenMG(String tenMG) {
        this.tenMG = tenMG;
    }
    public Date getNgayBD() {
        return ngayBD;
    }
    public void setNgayBD(Date ngayBD) {
        this.ngayBD = ngayBD;
    }
}
