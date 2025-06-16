package Model;

public class MODEL_BXH_CLB {
    private int MaGD,MaCLB;
    private int SoTran,Thang,Hoa,Thua,HieuSo,Hang,Diem;
    public MODEL_BXH_CLB() {
    }

    public MODEL_BXH_CLB(int maGD, int maCLB, int SoTran, int Thang, int Hoa, int Thua, int hieuSo, int Hang, int diem) {
        this.MaGD = maGD;
        this.MaCLB = maCLB;
        this.SoTran = SoTran;
        this.Thang = Thang;
        this.Hoa = Hoa;
        this.Thua = Thua;
        this.HieuSo = hieuSo;
        this.Hang = Hang;
        this.Diem = diem;
    }

    public int getMaGD() {
        return MaGD;
    }

    public void setMaGD(int MaGD) {
        MaGD = MaGD;
    }

    public int getMaCLB() {
        return MaCLB;
    }

    public void setMaCLB(int maCLB) {
        MaCLB = maCLB;
    }

    public int getSoTran() {
        return SoTran;
    }

    public void setSoTran(int SoTran) {
        this.SoTran = SoTran;
    }

    public int getThang() {
        return Thang;
    }

    public void setThang(int Thang) {
        this.Thang = Thang;
    }

    public int getHoa() {
        return Hoa;
    }

    public void setHoa(int Hoa) {
        this.Hoa = Hoa;
    }

    public int getThua() {
        return Thua;
    }

    public void setThua(int Thua) {
        this.Thua = Thua;
    }

    public int getHieuSo() {
        return HieuSo;
    }

    public void setHieuSo(int hieuSo) {
        HieuSo = hieuSo;
    }

    public int getHang() {
        return Hang;
    }

    public void setHang(int Hang) {
        this.Hang = Hang;
    }

    public int getDiem() {
        return this.Diem;
    }

    public void setDiem(int diem) {
        Diem = diem;
    }

}
