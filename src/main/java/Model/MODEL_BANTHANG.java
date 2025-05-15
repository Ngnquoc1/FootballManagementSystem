package Model;

public class MODEL_BANTHANG {
    private int maBT,maCT,maTD,phutGhiBan;
    private int maLoaiBT;
    public MODEL_BANTHANG() {}
    public MODEL_BANTHANG(int maBT, int maCT, int maTD, int phutGhiBan, int loaiBT) {
        this.maBT = maBT;
        this.maCT = maCT;
        this.maTD = maTD;
        this.phutGhiBan = phutGhiBan;
        this.maLoaiBT = loaiBT;
    }
    public int getMaBT() {
        return maBT;
    }
    public void setMaBT(int maBT) {
        this.maBT = maBT;
    }
    public int getMaCT() {
        return maCT;
    }

    public void setMaCT(int maCT) {
        this.maCT = maCT;
    }

    public int getMaTD() {
        return maTD;
    }

    public void setMaTD(int maTD) {
        this.maTD = maTD;
    }

    public int getPhutGhiBan() {
        return phutGhiBan;
    }

    public void setPhutGhiBan(int phutGhiBan) {
        this.phutGhiBan = phutGhiBan;
    }

    public int getmaLoaiBT() {
        return maLoaiBT;
    }

    public void setmaLoaiBT(int loaiBT) {
        this.maLoaiBT = loaiBT;
    }
}
