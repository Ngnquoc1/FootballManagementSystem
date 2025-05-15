package Model;

public class MODEL_CLB {
    private int maCLB,maSan;
    private String tenCLB,logoCLB,tenHLV,Email;
    public MODEL_CLB() {
    }

    public MODEL_CLB(String email, String tenHLV, String logoCLB, String tenCLB, int maSan, int maCLB) {
        Email = email;
        this.tenHLV = tenHLV;
        this.logoCLB = logoCLB;
        this.tenCLB = tenCLB;
        this.maSan = maSan;
        this.maCLB = maCLB;
    }
    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        Email = email;
    }
    public String getTenHLV() {
        return tenHLV;
    }
    public void setTenHLV(String tenHLV) {
        this.tenHLV = tenHLV;
    }

    public int getMaCLB() {
        return maCLB;
    }

    public void setMaCLB(int maCLB) {
        this.maCLB = maCLB;
    }

    public int getMaSan() {
        return maSan;
    }

    public void setMaSan(int maSan) {
        this.maSan = maSan;
    }

    public String getTenCLB() {
        return tenCLB;
    }

    public void setTenCLB(String tenCLB) {
        this.tenCLB = tenCLB;
    }
    public String getLogoCLB() {
        return logoCLB;
    }
    public void setLogoCLB(String logoCLB) {
        this.logoCLB = logoCLB;
    }
}
