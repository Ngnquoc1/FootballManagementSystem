package Model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Match {
    private int id;
    private String tenMuaGiai;
    private String tenVongDau;
    private String tenCLB1;
    private String tenCLB2;
    private LocalTime gioThiDau;
    private LocalDate ngayThiDau;
    private String sanThiDau;
    private String logoCLB1;
    private String logoCLB2;
    private Integer scoreCLB1;
    private Integer scoreCLB2;


    // Định dạng cho LocalDate và LocalTime khi hiển thị
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Constructor
    public Match(int id,String tenMG,String tenVD, String tenCLB1, String tenCLB2, LocalTime gioThiDau, LocalDate ngayThiDau, String sanThiDau, String logoCLB1, String logoCLB2, Integer scoreCLB1, Integer scoreCLB2) {
        this.id = id;
        this.tenMuaGiai = tenMG;
        this.tenVongDau = tenVD;
        this.tenCLB1 = tenCLB1;
        this.tenCLB2 = tenCLB2;
        this.gioThiDau = gioThiDau;
        this.ngayThiDau = ngayThiDau;
        this.sanThiDau = sanThiDau;
        this.logoCLB1 = logoCLB1;
        this.logoCLB2 = logoCLB2;
        this.scoreCLB1 = scoreCLB1;
        this.scoreCLB2 = scoreCLB2;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTenCLB1() { return tenCLB1; }
    public void setTenCLB1(String tenCLB1) { this.tenCLB1 = tenCLB1; }
    public String getTenCLB2() { return tenCLB2; }
    public void setTenCLB2(String tenCLB2) { this.tenCLB2 = tenCLB2; }
    public LocalTime getGioThiDau() { return gioThiDau; }
    public void setGioThiDau(LocalTime gioThiDau) { this.gioThiDau = gioThiDau; }
    public LocalDate getNgayThiDau() { return ngayThiDau; }
    public void setNgayThiDau(LocalDate ngayThiDau) { this.ngayThiDau = ngayThiDau; }
    public String getSanThiDau() { return sanThiDau; }
    public void setSanThiDau(String sanThiDau) { this.sanThiDau = sanThiDau; }
    public String getLogoCLB1() { return logoCLB1; }
    public void setLogoCLB1(String logoCLB1) { this.logoCLB1 = logoCLB1; }
    public String getLogoCLB2() { return logoCLB2; }
    public void setLogoCLB2(String logoCLB2) { this.logoCLB2 = logoCLB2; }
    public Integer getScoreCLB1() { return scoreCLB1; }
    public void setScoreCLB1(Integer scoreCLB1) { this.scoreCLB1 = scoreCLB1; }
    public Integer getScoreCLB2() { return scoreCLB2; }
    public void setScoreCLB2(Integer scoreCLB2) { this.scoreCLB2 = scoreCLB2; }
    public String getTenMuaGiai() {
        return tenMuaGiai;
    }
    public void setTenMuaGiai(String tenMuaGiai) {
        this.tenMuaGiai = tenMuaGiai;
    }
    public String getTenVongDau() {
        return tenVongDau;
    }
    public void setTenVongDau(String tenVongDau) {
        this.tenVongDau = tenVongDau;
    }

    public String getFormattedDate() {
        return ngayThiDau.format(DATE_FORMATTER);
    }
    public String getFormattedGioThiDau() {
        return String.format("%02d:%02d", gioThiDau.getHour(), gioThiDau.getMinute());
    }

    public static Timestamp convertToSqlTimestamp(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            throw new IllegalArgumentException("LocalDate and LocalTime cannot be null");
        }
        // Kết hợp LocalDate và LocalTime thành LocalDateTime
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        // Chuyển đổi LocalDateTime sang java.sql.Timestamp
        return Timestamp.valueOf(dateTime);
    }
    @Override
    public String toString() {
        String score = (scoreCLB1 != null && scoreCLB2 != null)
                ? scoreCLB1 + " - " + scoreCLB2
                : "Chưa có kết quả";

        return  "Mùa giải: " + tenMuaGiai + "\n" +
                "Vòng đấu: " + tenVongDau + "\n" +
                "Trận đấu: " + tenCLB1 + " vs " + tenCLB2 + "\n" +
                "Tỷ số: " + score + "\n" +
                "Logo CLB1: " + (logoCLB1 != null ? logoCLB1 : "Chưa có logo") + "\n" +
                "Logo CLB2: " + (logoCLB2 != null ? logoCLB2 : "Chưa có logo") + "\n" +
                "Giờ thi đấu: " + getFormattedGioThiDau() + "\n" +
                "Ngày thi đấu: " + getFormattedDate() + "\n" +
                "Sân thi đấu: " + sanThiDau + "\n" +
                "-------------------------";
    }
}
