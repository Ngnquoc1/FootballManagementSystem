package Model;

public class MODEL_BXH_BANTHANG {
    private int MaGD, MaCT,SoBanThang, Penalty,Hang;

    public MODEL_BXH_BANTHANG(int maGD, int maCT, int soBanThang, int penalty, int hang) {
        MaGD = maGD;
        MaCT = maCT;
        SoBanThang = soBanThang;
        Penalty = penalty;
        Hang = hang;
    }

    public MODEL_BXH_BANTHANG() {

    }

    public int getMaGD() {
        return this.MaGD;
    }

    public void setMaGD(int MaGD) {
        this.MaGD = MaGD;
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
