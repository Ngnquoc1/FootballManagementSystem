package Model;

public class MODEL_BXH_BANTHANG {
    private int MaMG, MaCT,SoBanThang, Penalty,Hang;

    public MODEL_BXH_BANTHANG(int maMG, int maCT, int soBanThang, int penalty, int hang) {
        MaMG = maMG;
        MaCT = maCT;
        SoBanThang = soBanThang;
        Penalty = penalty;
        Hang = hang;
    }

    public MODEL_BXH_BANTHANG() {

    }

    public int getMaMG() {
        return MaMG;
    }

    public void setMaMG(int maMG) {
        MaMG = maMG;
    }

    public int getMaCT() {
        return MaCT;
    }

    public void setMaCT(int maCT) {
        MaCT = maCT;
    }

    public int getSoBanThang() {
        return SoBanThang;
    }

    public void setSoBanThang(int soBanThang) {
        SoBanThang = soBanThang;
    }

    public int getPenalty() {
        return Penalty;
    }

    public void setPenalty(int penalty) {
        Penalty = penalty;
    }

    public int getHang() {
        return Hang;
    }

    public void setHang(int hang) {
        Hang = hang;
    }
}
