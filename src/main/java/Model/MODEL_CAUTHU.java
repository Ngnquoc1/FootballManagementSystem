package Model;

import java.sql.Date;

public class MODEL_CAUTHU {
    private int maCT,loaiCT;
    private String  tenCT;
    private Date ngaysinh;

    public MODEL_CAUTHU() {
    }

    public int getMaCT() {
        return maCT;
    }

    public void setMaCT(int maCT) {
        this.maCT = maCT;
    }

    public int getLoaiCT() {
        return loaiCT;
    }

    public void setLoaiCT(int loaiCT) {
        this.loaiCT = loaiCT;
    }

    public String getTenCT() {
        return tenCT;
    }

    public void setTenCT(String tenCT) {
        this.tenCT = tenCT;
    }

    public Date getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(Date ngaysinh) {
        this.ngaysinh = ngaysinh;
    }
}
