package Model;

import java.sql.Date;

public class MODEL_TRANDAU {
    private int maTD,maCLB1,maCLB2,maVD,maSan;
    private Date thoiGian;

    public MODEL_TRANDAU() {}

    public int getMaTD() {
        return maTD;
    }

    public void setMaTD(int maTD) {
        this.maTD = maTD;
    }

    public int getMaCLB1() {
        return maCLB1;
    }

    public void setMaCLB1(int maCLB1) {
        this.maCLB1 = maCLB1;
    }

    public int getMaCLB2() {
        return maCLB2;
    }

    public void setMaCLB2(int maCLB2) {
        this.maCLB2 = maCLB2;
    }

    public int getMaVD() {
        return maVD;
    }

    public void setMaVD(int maVD) {
        this.maVD = maVD;
    }

    public int getMaSan() {
        return maSan;
    }

    public void setMaSan(int maSan) {
        this.maSan = maSan;
    }

    public Date getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(Date thoiGian) {
        this.thoiGian = thoiGian;
    }
}
