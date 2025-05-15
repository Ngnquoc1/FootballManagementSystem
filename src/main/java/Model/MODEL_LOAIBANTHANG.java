package Model;

public class MODEL_LOAIBANTHANG {
    private int maLoaiBT;
    private String tenLoaiBT;
    public MODEL_LOAIBANTHANG() {}
    public MODEL_LOAIBANTHANG(String tenLoaiBT, int maLoaiBT) {
        this.tenLoaiBT = tenLoaiBT;
        this.maLoaiBT = maLoaiBT;
    }
    public int getMaLoaiBT() {
        return maLoaiBT;
    }
    public void setMaLoaiBT(int maLoaiBT) {
        this.maLoaiBT = maLoaiBT;
    }
    public String getTenLoaiBT() {
        return tenLoaiBT;
    }
    public void setTenLoaiBT(String tenLoaiBT) {
        this.tenLoaiBT = tenLoaiBT;
    }
}
