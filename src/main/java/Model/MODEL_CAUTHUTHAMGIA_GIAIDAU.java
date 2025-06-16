package Model;

public class MODEL_CAUTHUTHAMGIA_GIAIDAU {
    private int maGD,maCLB,maCT;
    private boolean selected;
    public MODEL_CAUTHUTHAMGIA_GIAIDAU() {}

    public MODEL_CAUTHUTHAMGIA_GIAIDAU(int maCT, int maMG, int maCLB) {
        this.maCT = maCT;
        this.maGD = maCLB;
        this.maCLB = maCLB;
    }
    public int getMaGD() {
        return maGD;
    }

    public void setMaGD(int maGD) {
        this.maGD = maGD;
    }

    public int getMaCLB() {
        return maCLB;
    }

    public void setMaCLB(int maCLB) {
        this.maCLB = maCLB;
    }

    public int getMaCT() {
        return maCT;
    }

    public void setMaCT(int maCT) {
        this.maCT = maCT;
    }

}
