package Model;

import javafx.beans.property.*;

public class MODEL_SAN {
    private IntegerProperty maSan = new SimpleIntegerProperty();
    private StringProperty tenSan = new SimpleStringProperty();
    private StringProperty diaChi = new SimpleStringProperty();
    private LongProperty sucChua = new SimpleLongProperty();

    public MODEL_SAN() {}

    // Property methods for JavaFX binding
    public IntegerProperty maSanProperty() {
        return maSan;
    }
    public StringProperty tenSanProperty() {
        return tenSan;
    }
    public StringProperty diaChiProperty() {
        return diaChi;
    }
    public LongProperty sucChuaProperty() {
        return sucChua;
    }

    // Standard getters and setters
    public int getMaSan() {
        return maSan.get();
    }
    public void setMaSan(int maSan) {
        this.maSan.set(maSan);
    }

    public String getTenSan() {
        return tenSan.get();
    }
    public void setTenSan(String tenSan) {
        this.tenSan.set(tenSan);
    }

    public String getDiaChi() {
        return diaChi.get();
    }
    public void setDiaChi(String diaChi) {
        this.diaChi.set(diaChi);
    }

    public long getSucChua() {
        return sucChua.get();
    }
    public void setSucChua(long sucChua) {
        this.sucChua.set(sucChua);
    }
}
