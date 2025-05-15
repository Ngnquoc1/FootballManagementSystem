package Model;

public class MODEL_KETQUATD {
    private int maTD,diemCLB1,diemCLB2;
    public MODEL_KETQUATD(int maTD,int diem1,int diem2) {
        this.maTD=maTD;
        this.diemCLB1=diem1;
        this.diemCLB2=diem2;
    }

    public int getMaTD() {
        return maTD;
    }

    public void setMaTD(int maTD) {
        this.maTD = maTD;
    }

    public int getDiemCLB1() {
        return diemCLB1;
    }

    public void setDiemCLB1(int diemCLB1) {
        this.diemCLB1 = diemCLB1;
    }

    public int getDiemCLB2() {
        return diemCLB2;
    }

    public void setDiemCLB2(int diemCLB2) {
        this.diemCLB2 = diemCLB2;
    }
}
