package Controller;

import Model.MODEL_MUAGIAI;
import Model.MODEL_QUYDINH;

import Model.MODEL_THUTU_UUTIEN;
import Service.Service;
import Util.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import java.util.ResourceBundle;
public class RulesManagementController implements Initializable{
    @FXML
    private ComboBox<MODEL_MUAGIAI> cboMuaGiai;

    @FXML
    private Label lblTrangThaiMuaGiai;

    @FXML
    private Label lblTrangThaiQuyDinh;

    @FXML
    private Spinner<Integer> spnTuoiToiThieu;

    @FXML
    private Spinner<Integer> spnTuoiToiDa;

    @FXML
    private Spinner<Integer> spnSoCTToiThieu;

    @FXML
    private Spinner<Integer> spnSoCTToiDa;

    @FXML
    private Spinner<Integer> spnSoCTNuocNgoaiToiDa;

    @FXML
    private Spinner<Integer> spnPhutGhiBanToiDa;

    @FXML
    private TableView<MODEL_THUTU_UUTIEN> priorityOrderTable;
    @FXML
    private TableColumn<MODEL_THUTU_UUTIEN, String> colTenTTUT;
    @FXML
    private Button btnMoveUp, btnMoveDown;

    private final ObservableList<MODEL_THUTU_UUTIEN> priorityList = FXCollections.observableArrayList();
    private Service service;

    private ObservableList<MODEL_MUAGIAI> danhSachMuaGiai;
    private MODEL_QUYDINH quyDinhHienTai;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Khởi tạo services
        service = new Service();

        // Thiết lập ComboBox
        setupComboBox();

        // Thiết lập Spinners
        setupSpinners();

        // Tải dữ liệu
        try {
            taiDanhSachMuaGiai();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        colTenTTUT.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTenTTUT()));
        priorityOrderTable.setItems(priorityList);
        // Vô hiệu hóa form ban đầu
        vohieuhoaForm(true);
    }

    private void setupComboBox() {
        cboMuaGiai.setConverter(new StringConverter<MODEL_MUAGIAI>() {
            @Override
            public String toString(MODEL_MUAGIAI muaGiai) {
                return muaGiai != null ? muaGiai.getTenMG() : "";
            }

            @Override
            public MODEL_MUAGIAI fromString(String string) {
                return null;
            }
        });
    }

    private void setupSpinners() {
        // Thiết lập các spinner với giá trị mặc định
        spnTuoiToiThieu.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 50, 16));
        spnTuoiToiDa.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(20, 60, 40));
        spnSoCTToiThieu.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 30, 15));
        spnSoCTToiDa.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 50, 22));
        spnSoCTNuocNgoaiToiDa.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 3));
        spnPhutGhiBanToiDa.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(60, 120, 90));

        // Thêm listener để kiểm tra logic
        spnTuoiToiThieu.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal >= spnTuoiToiDa.getValue()) {
                spnTuoiToiDa.getValueFactory().setValue(newVal + 1);
            }
        });

        spnTuoiToiDa.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal <= spnTuoiToiThieu.getValue()) {
                spnTuoiToiThieu.getValueFactory().setValue(newVal - 1);
            }
        });

        spnSoCTToiThieu.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal >= spnSoCTToiDa.getValue()) {
                spnSoCTToiDa.getValueFactory().setValue(newVal + 1);
            }
        });

        spnSoCTToiDa.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal <= spnSoCTToiThieu.getValue()) {
                spnSoCTToiThieu.getValueFactory().setValue(newVal - 1);
            }
        });
    }

    private void taiDanhSachMuaGiai() throws SQLException {
        List<MODEL_MUAGIAI> list = service.getAllTournament();
        danhSachMuaGiai = FXCollections.observableArrayList(list);
        cboMuaGiai.setItems(danhSachMuaGiai);
    }

    @FXML
    private void chonMuaGiai(ActionEvent event) {
        MODEL_MUAGIAI selectedMuaGiai = cboMuaGiai.getValue();
        if (selectedMuaGiai != null) {
            // Hiển thị trạng thái mùa giải
            lblTrangThaiMuaGiai.setText("(" + selectedMuaGiai.getStatus() + ")");

            // Đặt màu cho trạng thái
            switch (selectedMuaGiai.getStatus()) {
                case "Đang diễn ra":
                    lblTrangThaiMuaGiai.setTextFill(javafx.scene.paint.Color.GREEN);
                    break;
                case "Sắp diễn ra":
                    lblTrangThaiMuaGiai.setTextFill(javafx.scene.paint.Color.BLUE);
                    break;
                case "Đã kết thúc":
                    lblTrangThaiMuaGiai.setTextFill(javafx.scene.paint.Color.RED);
                    break;
            }

            // Tải quy định của mùa giải
            taiQuyDinhMuaGiai(selectedMuaGiai.getMaMG());

            // Kích hoạt form
            vohieuhoaForm(false);
        } else {
            lblTrangThaiMuaGiai.setText("");
            lblTrangThaiQuyDinh.setText("");
            vohieuhoaForm(true);
        }
    }

    private void taiQuyDinhMuaGiai(int maMG) {
        quyDinhHienTai = service.getQDByMaMG(maMG);

        if (quyDinhHienTai != null) {
            // Hiển thị quy định hiện có
            spnTuoiToiThieu.getValueFactory().setValue(quyDinhHienTai.getTuoiToiThieu());
            spnTuoiToiDa.getValueFactory().setValue(quyDinhHienTai.getTuoiToiDa());
            spnSoCTToiThieu.getValueFactory().setValue(quyDinhHienTai.getSoCTToiThieu());
            spnSoCTToiDa.getValueFactory().setValue(quyDinhHienTai.getSoCTToiDa());
            spnSoCTNuocNgoaiToiDa.getValueFactory().setValue(quyDinhHienTai.getSoCTNuocNgoaiToiDa());
            spnPhutGhiBanToiDa.getValueFactory().setValue(quyDinhHienTai.getPhutGhiBanToiDa());

            lblTrangThaiQuyDinh.setText("Đã có quy định");
            lblTrangThaiQuyDinh.setTextFill(javafx.scene.paint.Color.GREEN);

            List<MODEL_THUTU_UUTIEN> list = service.getPriorityOrderByTournament(maMG);
            priorityList.setAll(list);
            updatePriorityOrder();
        } else {
            // Áp dụng quy định mặc định
            apDungGiaTriMacDinh();

            lblTrangThaiQuyDinh.setText("Chưa có quy định");
            lblTrangThaiQuyDinh.setTextFill(javafx.scene.paint.Color.RED);
        }
    }

    @FXML
    private void handleMoveUp() {
        int selectedIndex = priorityOrderTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            MODEL_THUTU_UUTIEN selected = priorityList.remove(selectedIndex);
            priorityList.add(selectedIndex - 1, selected);
            priorityOrderTable.getSelectionModel().select(selectedIndex - 1);
            updatePriorityOrder();
        }
    }

    @FXML
    private void handleMoveDown() {
        int selectedIndex = priorityOrderTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < priorityList.size() - 1) {
            MODEL_THUTU_UUTIEN selected = priorityList.remove(selectedIndex);
            priorityList.add(selectedIndex + 1, selected);
            priorityOrderTable.getSelectionModel().select(selectedIndex + 1);
            updatePriorityOrder();
        }
    }

    private void updatePriorityOrder() {
        for (int i = 0; i < priorityList.size(); i++) {
            priorityList.get(i).setThuTu(i + 1);
        }
        priorityOrderTable.refresh();
    }

    @FXML
    private void apDungMacDinh(ActionEvent event) {
        MODEL_MUAGIAI selectedMuaGiai = cboMuaGiai.getValue();
        if (selectedMuaGiai == null) {
            AlertUtils.showError("Lỗi", "Chưa chọn mùa giải", "Vui lòng chọn mùa giải trước khi áp dụng quy định mặc định.");
            return;
        }

        boolean choosen= AlertUtils.showConfirmation("Xác nhận áp dụng mặc định","Ap dụng quy định mặc định",
                "Bạn có chắc chắn muốn áp dụng quy định mặc định cho mùa giải này?");

        if (choosen) {
            apDungGiaTriMacDinh();
            AlertUtils.showInformation("Thông báo", "Áp dụng quy định mặc định",
                    "Đã áp dụng quy định mặc định cho mùa giải: " + selectedMuaGiai.getTenMG());
        }
    }

    @FXML
    private void capNhatQuyDinh(ActionEvent event) {
        MODEL_MUAGIAI selectedMuaGiai = cboMuaGiai.getValue();
        if (selectedMuaGiai == null) {
            AlertUtils.showError("Lỗi", "Chưa chọn mùa giải", "Vui lòng chọn mùa giải trước khi cập nhật quy định.");
            return;
        }

        if (!kiemTraDuLieu()) {
            return;
        }

        boolean result = AlertUtils.showConfirmation("Xác nhận cập nhật quy định", "Cập nhật quy định",
                "Bạn có chắc chắn muốn cập nhật quy định cho mùa giải này?");
        if (result) {
            MODEL_QUYDINH quyDinh = layThongTinTuForm();
            quyDinh.setMaMG(selectedMuaGiai.getMaMG());

            boolean ketQua;
            ketQua = service.updateQD(quyDinh);
            // Save to DB if a tournament is selected
            service.savePriorityOrder(selectedMuaGiai.getMaMG(), priorityList);

            if (ketQua) {
                AlertUtils.showInformation("Thông báo", "Cập nhật quy định thành công",
                        "Quy định đã được cập nhật thành công cho mùa giải: " + selectedMuaGiai.getTenMG());
                // Tải lại quy định
                taiQuyDinhMuaGiai(selectedMuaGiai.getMaMG());
            } else {
                AlertUtils.showError("Lỗi", "Cập nhật quy định thất bại",
                        "Không thể cập nhật quy định cho mùa giải: " + selectedMuaGiai.getTenMG());
            }
        }
    }

    @FXML
    private void lamMoi(ActionEvent event) {
        MODEL_MUAGIAI selectedMuaGiai = cboMuaGiai.getValue();
        if (selectedMuaGiai != null) {
            // Tải lại quy định từ database
            taiQuyDinhMuaGiai(selectedMuaGiai.getMaMG());
        } else {
            // Reset về mặc định
            apDungGiaTriMacDinh();
        }
    }

    private void apDungGiaTriMacDinh() {
        MODEL_QUYDINH macDinh = new MODEL_QUYDINH(); // Constructor đã có giá trị mặc định

        spnTuoiToiThieu.getValueFactory().setValue(macDinh.getTuoiToiThieu());
        spnTuoiToiDa.getValueFactory().setValue(macDinh.getTuoiToiDa());
        spnSoCTToiThieu.getValueFactory().setValue(macDinh.getSoCTToiThieu());
        spnSoCTToiDa.getValueFactory().setValue(macDinh.getSoCTToiDa());
        spnSoCTNuocNgoaiToiDa.getValueFactory().setValue(macDinh.getSoCTNuocNgoaiToiDa());
        spnPhutGhiBanToiDa.getValueFactory().setValue(macDinh.getPhutGhiBanToiDa());

        List<MODEL_THUTU_UUTIEN> defaultPriorityList = MODEL_THUTU_UUTIEN.getDefaultList(macDinh.getMaMG());
        priorityList.setAll(defaultPriorityList);
        updatePriorityOrder();
    }

    private void vohieuhoaForm(boolean disable) {
        spnTuoiToiThieu.setDisable(disable);
        spnTuoiToiDa.setDisable(disable);
        spnSoCTToiThieu.setDisable(disable);
        spnSoCTToiDa.setDisable(disable);
        spnSoCTNuocNgoaiToiDa.setDisable(disable);
        spnPhutGhiBanToiDa.setDisable(disable);
    }

    private MODEL_QUYDINH layThongTinTuForm() {
        MODEL_QUYDINH quyDinh = new MODEL_QUYDINH();

        quyDinh.setTuoiToiThieu(spnTuoiToiThieu.getValue());
        quyDinh.setTuoiToiDa(spnTuoiToiDa.getValue());
        quyDinh.setSoCTToiThieu(spnSoCTToiThieu.getValue());
        quyDinh.setSoCTToiDa(spnSoCTToiDa.getValue());
        quyDinh.setSoCTNuocNgoaiToiDa(spnSoCTNuocNgoaiToiDa.getValue());
        quyDinh.setPhutGhiBanToiDa(spnPhutGhiBanToiDa.getValue());

        return quyDinh;
    }

    private boolean kiemTraDuLieu() {
        StringBuilder sb = new StringBuilder();

        if (spnTuoiToiThieu.getValue() >= spnTuoiToiDa.getValue()) {
            sb.append("Tuổi tối thiểu phải nhỏ hơn tuổi tối đa!\n");
        }

        if (spnSoCTToiThieu.getValue() >= spnSoCTToiDa.getValue()) {
            sb.append("Số cầu thủ tối thiểu phải nhỏ hơn số cầu thủ tối đa!\n");
        }

        if (spnSoCTNuocNgoaiToiDa.getValue() > spnSoCTToiDa.getValue()) {
            sb.append("Số cầu thủ nước ngoài không được vượt quá tổng số cầu thủ tối đa!\n");
        }

        if (sb.length() > 0) {
            AlertUtils.showError("Lỗi", "Kiểm tra dữ liệu không hợp lệ", sb.toString());
            return false;
        }

        return true;
    }
}
