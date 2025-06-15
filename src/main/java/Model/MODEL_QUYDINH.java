package Model;

public class MODEL_QUYDINH {

    private int maMG, tuoiToiThieu, tuoiToiDa, soCTToiThieu, soCTToiDa,
            soCTNuocNgoaiToiDa, phutGhiBanToiDa,diemThang,diemThua,diemHoa;

    public MODEL_QUYDINH() {
        this.tuoiToiThieu = 16;
        this.tuoiToiDa = 40;
        this.soCTNuocNgoaiToiDa = 3;
        this.soCTToiDa = 22;
        this.soCTToiThieu = 15;
        this.phutGhiBanToiDa=90;
        this.diemThang=3;
        this.diemThua=0;
        this.diemHoa=1;
    }


    public int getSoCTNuocNgoaiToiDa() {
        return soCTNuocNgoaiToiDa;
    }

    public void setSoCTNuocNgoaiToiDa(int soCTNuocNgoaiToiDa) {
        this.soCTNuocNgoaiToiDa = soCTNuocNgoaiToiDa;
    }

    public int getSoCTToiDa() {
        return soCTToiDa;
    }

    public void setSoCTToiDa(int soCTToiDa) {
        this.soCTToiDa = soCTToiDa;
    }

    public int getSoCTToiThieu() {
        return soCTToiThieu;
    }

    public void setSoCTToiThieu(int soCTToiThieu) {
        this.soCTToiThieu = soCTToiThieu;
    }

    public int getTuoiToiDa() {
        return tuoiToiDa;
    }

    public void setTuoiToiDa(int tuoiToiDa) {
        this.tuoiToiDa = tuoiToiDa;
    }

    public int getTuoiToiThieu() {
        return tuoiToiThieu;
    }

    public void setTuoiToiThieu(int tuoiToiThieu) {
        this.tuoiToiThieu = tuoiToiThieu;
    }

    public int getMaMG() {
        return maMG;
    }

    public void setMaMG(int maMG) {
        this.maMG = maMG;
    }

    public int getPhutGhiBanToiDa() {
        return phutGhiBanToiDa;
    }

    public void setPhutGhiBanToiDa(int phutGhiBanToiDa) {
        this.phutGhiBanToiDa = phutGhiBanToiDa;
    }

    public int getDiemThang() {
        return diemThang;
    }
    public void setDiemThang(int diemThang) {
        this.diemThang = diemThang;
    }
    public int getDiemThua() {
        return diemThua;
    }
    public void setDiemThua(int diemThua) {
        this.diemThua = diemThua;
    }
    public int getDiemHoa() {
        return diemHoa;
    }
    public void setDiemHoa(int diemHoa) {
        this.diemHoa = diemHoa;
    }
}
