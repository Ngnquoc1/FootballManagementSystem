package Model;


import java.util.ArrayList;
import java.util.List;

public class MODEL_THUTU_UUTIEN {
    private int maMG;
    private String tenTTUT;
    private int thuTu;

    public static List<MODEL_THUTU_UUTIEN> getDefaultList(int maMG) {
        List<MODEL_THUTU_UUTIEN> list = new ArrayList<>();
        list.add(new MODEL_THUTU_UUTIEN(maMG, "Diem", 1));
        list.add(new MODEL_THUTU_UUTIEN(maMG, "HieuSo", 2));
        list.add(new MODEL_THUTU_UUTIEN(maMG, "Thang", 3));
        list.add(new MODEL_THUTU_UUTIEN(maMG, "Hoa", 4));
        list.add(new MODEL_THUTU_UUTIEN(maMG, "Thua", 5));
        list.add(new MODEL_THUTU_UUTIEN(maMG, "SoTran", 6));
        return list;
    }

    public MODEL_THUTU_UUTIEN(int maMG, String tenTTUT, int thuTu) {
        this.maMG = maMG;
        this.tenTTUT = tenTTUT;
        this.thuTu = thuTu;
    }

    public int getMaMG() {
        return maMG;
    }

    public void setMaMG(int maTTUT) {
        this.maMG = maTTUT;
    }

    public String getTenTTUT() {
        return tenTTUT;
    }

    public void setTenTTUT(String tenTTUT) {
        this.tenTTUT = tenTTUT;
    }

    public int getThuTu() {
        return thuTu;
    }

    public void setThuTu(int thuTu) {
        this.thuTu = thuTu;
    }
}