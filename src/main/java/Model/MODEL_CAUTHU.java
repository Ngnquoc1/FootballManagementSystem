package Model;

import java.sql.Date;

public class MODEL_CAUTHU {
    private int maCT,loaiCT,maCLB,soAo,maVT;
    private String  tenCT,QuocTich;
    private Date ngaysinh;
    private String avatar;

    public MODEL_CAUTHU() {
    }

    public MODEL_CAUTHU(int maCT,int maCLB,int maVT, int loaiCT, String tenCT, String quocTich, Date ngaysinh, int soAo, String avatar) {
        this.maCT = maCT;
        this.maCLB = maCLB;
        this.maVT = maVT;
        this.loaiCT = loaiCT;
        this.tenCT = tenCT;
        QuocTich = quocTich;
        this.ngaysinh = ngaysinh;
        this.soAo = soAo;
        this.avatar = avatar;
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

    public String getQuocTich() {
        return QuocTich;
    }
    public void setQuocTich(String quocTich) {
        QuocTich = quocTich;
    }
    public int getSoAo() {
        return soAo;
    }
    public void setSoAo(int soAo) {
        this.soAo = soAo;
    }
    public int getMaVT() {
        return maVT;
    }
    public void setMaVT(int maVT) {
        this.maVT = maVT;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getAvatar() {
        return avatar;
    }
    public int getMaCLB() {
        return maCLB;
    }
    public void setMaCLB(int maCLB) {
        this.maCLB = maCLB;
    }

    @Override
    public String toString() {
        return "MODEL_CAUTHU{" +
                "maCT=" + maCT +
                ", loaiCT=" + loaiCT +
                ", maCLB=" + maCLB +
                ", soAo=" + soAo +
                ", maVT=" + maVT +
                ", tenCT='" + tenCT + '\'' +
                ", QuocTich='" + QuocTich + '\'' +
                ", ngaysinh=" + ngaysinh +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
