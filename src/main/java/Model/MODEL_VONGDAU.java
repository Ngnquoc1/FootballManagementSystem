package Model;

import java.sql.Date;

public class MODEL_VONGDAU {
    private int maVD, maMG;
    private String tenVD;  // Đã thay đổi từ int sang String
    private Date ngayBD, ngayKT;

    public MODEL_VONGDAU() {}

    public MODEL_VONGDAU(int maVD, String tenVD, int maMG, Date ngayBD, Date ngayKT) {
        this.maVD = maVD;
        this.tenVD = tenVD;
        this.maMG = maMG;
        this.ngayBD = ngayBD;
        this.ngayKT = ngayKT;
    }

    public int getMaVD() {
        return maVD;
    }

    public void setMaVD(int maVD) {
        this.maVD = maVD;
    }

    public String getTenVD() {  // Đã thay đổi kiểu trả về
        return tenVD;
    }

    public void setTenVD(String tenVD) {  // Đã thay đổi kiểu tham số
        this.tenVD = tenVD;
    }

    public int getMaMG() {
        return maMG;
    }

    public void setMaMG(int maMG) {
        this.maMG = maMG;
    }

    public Date getNgayBD() {
        return ngayBD;
    }

    public void setNgayBD(Date ngayBD) {
        this.ngayBD = ngayBD;
    }

    public Date getNgayKT() {
        return ngayKT;
    }

    public void setNgayKT(Date ngayKT) {
        this.ngayKT = ngayKT;
    }

    // Phương thức để lấy trạng thái vòng đấu dựa trên ngày hiện tại
    public String getStatus() {
        Date today = new Date(System.currentTimeMillis());

        if (today.before(ngayBD)) {
            return "Sắp diễn ra";
        } else if (today.after(ngayKT)) {
            return "Đã kết thúc";
        } else {
            return "Đang diễn ra";
        }
    }
}