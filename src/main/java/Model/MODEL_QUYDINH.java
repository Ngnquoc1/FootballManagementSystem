package Model;

public class MODEL_QUYDINH {

    private int maQD,
            maMG,
            tuoiToiThieu,
            tuoiToiDa,
            soCTToiThieu,
            soCTToiDa,
            soCTNuocNgoaiToiDa,
            phutGhiBanToiDa;

    MODEL_QUYDINH() {
        this.tuoiToiThieu = 16;
        this.tuoiToiDa = 40;
        this.soCTNuocNgoaiToiDa = 3;
        this.soCTToiDa = 22;
        this.soCTToiThieu = 15;
        this.phutGhiBanToiDa=90;
    }

    public int getMaQD() {
        return maQD;
    }

    public void setMaQD(int maQD) {
        this.maQD = maQD;
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
    
}
