package Model;

import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.io.File;
import java.time.LocalDate;

public class MODEL_MUAGIAI {
    private final IntegerProperty maMG;
    private final StringProperty tenMG;
    private final ObjectProperty<LocalDate> ngayBD;
    private final ObjectProperty<LocalDate> ngayKT;
    private final StringProperty logoFileName; // Chỉ lưu tên file, không lưu đường dẫn đầy đủ

    public MODEL_MUAGIAI(int maMG, String tenMG, LocalDate ngayBD, LocalDate ngayKT) {
        this(maMG, tenMG, ngayBD, ngayKT, null);
    }

    public MODEL_MUAGIAI(int maMG, String tenMG, LocalDate ngayBD, LocalDate ngayKT, String logoFileName) {
        this.maMG = new SimpleIntegerProperty(maMG);
        this.tenMG = new SimpleStringProperty(tenMG);
        this.ngayBD = new SimpleObjectProperty<>(ngayBD);
        this.ngayKT = new SimpleObjectProperty<>(ngayKT);
        this.logoFileName = new SimpleStringProperty(logoFileName);
    }

    public MODEL_MUAGIAI(IntegerProperty maMG, StringProperty tenMG, ObjectProperty<LocalDate> ngayBD, ObjectProperty<LocalDate> ngayKT, StringProperty logoFileName) {
        this.maMG = maMG;
        this.tenMG = tenMG;
        this.ngayBD = ngayBD;
        this.ngayKT = ngayKT;
        this.logoFileName = logoFileName;
    }

    // Getters
    public int getMaMG() { return maMG.get(); }
    public String getTenMG() { return tenMG.get(); }
    public LocalDate getNgayBD() { return ngayBD.get(); }
    public LocalDate getNgayKT() { return ngayKT.get(); }
    public String getLogoFileName() { return logoFileName.get(); }

    // Phương thức để lấy Image từ logoFileName khi cần
    public Image getLogo() {
        if (getLogoFileName() == null || getLogoFileName().isEmpty()) {
            return null;
        }

        try {
            File file = new File("src/main/resources/Image/LeagueLogo/" + getLogoFileName());
            if (file.exists()) {
                return new Image(file.toURI().toString());
            }
        } catch (Exception e) {
            System.err.println("Không thể tải hình ảnh logo: " + e.getMessage());
        }
        return null;
    }

    // Setters
    public void setMaMG(int id) { this.maMG.set(id); }
    public void setTenMG(String name) { this.tenMG.set(name); }
    public void setNgayBD(LocalDate ngayBD) { this.ngayBD.set(ngayBD); }
    public void setNgayKT(LocalDate ngayKT) { this.ngayKT.set(ngayKT); }
    public void setLogoFileName(String logoFileName) { this.logoFileName.set(logoFileName); }

    // Property getters
    public IntegerProperty idProperty() { return maMG; }
    public StringProperty nameProperty() { return tenMG; }
    public ObjectProperty<LocalDate> ngayBDProperty() { return ngayBD; }
    public ObjectProperty<LocalDate> ngayKTProperty() { return ngayKT; }
    public StringProperty logoFileNameProperty() { return logoFileName; }

    // Trạng thái giải đấu
    public String getStatus() {
        LocalDate today = LocalDate.now();

        if (ngayBD.get().isAfter(today)) {
            return "Sắp diễn ra";
        } else if (ngayKT.get().isBefore(today)) {
            return "Đã kết thúc";
        } else {
            return "Đang diễn ra";
        }
    }
}