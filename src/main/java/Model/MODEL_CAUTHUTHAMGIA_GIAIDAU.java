package Model;

public class MODEL_CAUTHUTHAMGIA_GIAIDAU {
    private int maMG,maCLB,maCT;
    private boolean selected;
    public MODEL_CAUTHUTHAMGIA_GIAIDAU() {}

    public MODEL_CAUTHUTHAMGIA_GIAIDAU(int maCT, int maMG, int maCLB) {
        this.maCT = maCT;
        this.maMG = maCLB;
        this.maCLB = maCLB;
    }
    public int getMaMG() {
        return maMG;
    }

    public void setMaMG(int maMG) {
        this.maMG = maMG;
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

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
