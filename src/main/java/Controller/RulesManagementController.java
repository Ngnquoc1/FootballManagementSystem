package Controller;

import Model.MODEL_GIAIDAU;
import Model.MODEL_QUYDINH;

import Model.MODEL_THUTU_UUTIEN;
import Service.*;
import Util.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.io.File;
import java.net.URL;

import java.sql.SQLException;

import java.util.List;

import java.util.ResourceBundle;

public class RulesManagementController implements Initializable {
    @FXML
    private ComboBox<MODEL_GIAIDAU> cboMuaGiai;

    @FXML
    private Label lblTrangThaiMuaGiai;

    @FXML
    private Label lblTrangThaiQuyDinh;

    @FXML
    private Spinner<Integer> spnTuoiToiThieu,spnTuoiToiDa;

    @FXML
    private Spinner<Integer> spnSoCTToiThieu,spnSoCTToiDa,spnSoCTNuocNgoaiToiDa;

    @FXML
    private Spinner<Integer> spnPhutGhiBanToiDa,spnSoDiemThang, spnSoDiemHoa, spnSoDiemThua;

    @FXML
    private TableView<MODEL_THUTU_UUTIEN> priorityOrderTable;
    @FXML
    private TableColumn<MODEL_THUTU_UUTIEN, String> colTenTTUT;
    @FXML
    private Button btnMoveUp, btnMoveDown;

    private final ObservableList<MODEL_THUTU_UUTIEN> priorityList = FXCollections.observableArrayList();
    private Service service;
    private ExportService exportService;
    private EmailService emailService;

    private ObservableList<MODEL_GIAIDAU> danhSachMuaGiai;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Khởi tạo services
        service = new Service();
        exportService = new ExportService();
        emailService = new EmailService();
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
        vohieuhoaForm(true,true);
    }

    private void setupComboBox() {
        cboMuaGiai.setConverter(new StringConverter<MODEL_GIAIDAU>() {
            @Override
            public String toString(MODEL_GIAIDAU muaGiai) {
                return muaGiai != null ? muaGiai.getTenGD() : "";
            }

            @Override
            public MODEL_GIAIDAU fromString(String string) {
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
        spnSoDiemThang.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 3));
        spnSoDiemHoa.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 19, 1));
        spnSoDiemThua.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 18, 0));

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
        spnSoDiemThang.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal <= spnSoDiemHoa.getValue()) {
                    spnSoDiemHoa.getValueFactory().setValue(newVal - 1);
                }
                if (spnSoDiemHoa.getValue() <= spnSoDiemThua.getValue()) {
                    spnSoDiemThua.getValueFactory().setValue(spnSoDiemHoa.getValue() - 1);
                }
            }
        });
        spnSoDiemHoa.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal >= spnSoDiemThang.getValue()) {
                    spnSoDiemThang.getValueFactory().setValue(newVal + 1);
                }
                if (newVal <= spnSoDiemThua.getValue()) {
                    spnSoDiemThua.getValueFactory().setValue(newVal - 1);
                }
            }
        });

        spnSoDiemThua.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal >= spnSoDiemHoa.getValue()) {
                    spnSoDiemHoa.getValueFactory().setValue(newVal + 1);
                }
                if (spnSoDiemHoa.getValue() >= spnSoDiemThang.getValue()) {
                    spnSoDiemThang.getValueFactory().setValue(spnSoDiemHoa.getValue() + 1);
                }
            }
        });
    }

    private void taiDanhSachMuaGiai() throws SQLException {
        List<MODEL_GIAIDAU> list = service.getAllTournament();
        danhSachMuaGiai = FXCollections.observableArrayList(list);
        cboMuaGiai.setItems(danhSachMuaGiai);
    }

    @FXML
    private void chonMuaGiai(ActionEvent event) {
        MODEL_GIAIDAU selectedMuaGiai = cboMuaGiai.getValue();
        if (selectedMuaGiai != null) {
            // Hiển thị trạng thái mùa giải
            lblTrangThaiMuaGiai.setText("(" + selectedMuaGiai.getStatus() + ")");
            boolean ok=false,ok1=false;
            // Đặt màu cho trạng thái
            switch (selectedMuaGiai.getStatus()) {
                case "Đang diễn ra":
                    lblTrangThaiMuaGiai.setTextFill(javafx.scene.paint.Color.GREEN);
                    ok=true;
                    break;
                case "Sắp diễn ra":
                    lblTrangThaiMuaGiai.setTextFill(javafx.scene.paint.Color.BLUE);
                    break;
                case "Đã kết thúc":
                    lblTrangThaiMuaGiai.setTextFill(javafx.scene.paint.Color.RED);
                    ok=true;
                    break;
            }

            // Tải quy định của mùa giải
            taiQuyDinhMuaGiai(selectedMuaGiai.getMaGD());

            // Kích hoạt form
            vohieuhoaForm(ok,ok1);
        } else {
            lblTrangThaiMuaGiai.setText("");
            lblTrangThaiQuyDinh.setText("");
            vohieuhoaForm(true,true);
        }
    }

    private void taiQuyDinhMuaGiai(int maMG) {
        MODEL_QUYDINH quyDinhHienTai = service.getQDByMaGD(maMG);

        if (quyDinhHienTai != null) {
            // Hiển thị quy định hiện có
            spnTuoiToiThieu.getValueFactory().setValue(quyDinhHienTai.getTuoiToiThieu());
            spnTuoiToiDa.getValueFactory().setValue(quyDinhHienTai.getTuoiToiDa());
            spnSoCTToiThieu.getValueFactory().setValue(quyDinhHienTai.getSoCTToiThieu());
            spnSoCTToiDa.getValueFactory().setValue(quyDinhHienTai.getSoCTToiDa());
            spnSoCTNuocNgoaiToiDa.getValueFactory().setValue(quyDinhHienTai.getSoCTNuocNgoaiToiDa());
            spnPhutGhiBanToiDa.getValueFactory().setValue(quyDinhHienTai.getPhutGhiBanToiDa());
            spnSoDiemThang.getValueFactory().setValue(quyDinhHienTai.getDiemThang());
            spnSoDiemHoa.getValueFactory().setValue(quyDinhHienTai.getDiemHoa());
            spnSoDiemThua.getValueFactory().setValue(quyDinhHienTai.getDiemThua());
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
        MODEL_GIAIDAU selectedMuaGiai = cboMuaGiai.getValue();
        if (selectedMuaGiai == null) {
            AlertUtils.showError("Lỗi", "Chưa chọn mùa giải", "Vui lòng chọn mùa giải trước khi áp dụng quy định mặc định.");
            return;
        }

        boolean choosen= AlertUtils.showConfirmation("Xác nhận áp dụng mặc định","Ap dụng quy định mặc định",
                "Bạn có chắc chắn muốn áp dụng quy định mặc định cho mùa giải này?");

        if (choosen) {
            apDungGiaTriMacDinh();
            AlertUtils.showInformation("Thông báo", "Áp dụng quy định mặc định",
                    "Đã áp dụng quy định mặc định cho mùa giải: " + selectedMuaGiai.getTenGD());
        }
    }

    @FXML
    private void capNhatQuyDinh(ActionEvent event) {
        MODEL_GIAIDAU selectedMuaGiai = cboMuaGiai.getValue();
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
            quyDinh.setMaGD(selectedMuaGiai.getMaGD());

            boolean ketQua;
            ketQua = service.updateQD(quyDinh);
            // Save to DB if a tournament is selected
            service.savePriorityOrder(selectedMuaGiai.getMaGD(), priorityList);
            service.recalculateRanking(selectedMuaGiai.getMaGD());
            if (ketQua) {
                AlertUtils.showInformation("Thông báo", "Cập nhật quy định thành công",
                        "Quy định đã được cập nhật thành công cho mùa giải: " + selectedMuaGiai.getTenGD());
                // Tải lại quy định
                taiQuyDinhMuaGiai(selectedMuaGiai.getMaGD());

                sendRulesUpdateToClub();
            } else {
                AlertUtils.showError("Lỗi", "Cập nhật quy định thất bại",
                        "Không thể cập nhật quy định cho mùa giải: " + selectedMuaGiai.getTenGD());
            }
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
        spnSoDiemThang.getValueFactory().setValue(macDinh.getDiemThang());
        spnSoDiemHoa.getValueFactory().setValue(macDinh.getDiemHoa());
        spnSoDiemThua.getValueFactory().setValue(macDinh.getDiemThua());

        List<MODEL_THUTU_UUTIEN> defaultPriorityList = MODEL_THUTU_UUTIEN.getDefaultList(macDinh.getMaGD());
        priorityList.setAll(defaultPriorityList);
        updatePriorityOrder();
    }

    private void vohieuhoaForm(boolean disable, boolean disable1) {
        spnTuoiToiThieu.setDisable(disable);
        spnTuoiToiDa.setDisable(disable);
        spnSoCTToiThieu.setDisable(disable);
        spnSoCTToiDa.setDisable(disable);
        spnSoCTNuocNgoaiToiDa.setDisable(disable);
        spnPhutGhiBanToiDa.setDisable(disable1);
        spnSoDiemThang.setDisable(disable1);
        spnSoDiemHoa.setDisable(disable1);
        spnSoDiemThua.setDisable(disable1);
    }


    private MODEL_QUYDINH layThongTinTuForm() {
        MODEL_QUYDINH quyDinh = new MODEL_QUYDINH();

        quyDinh.setTuoiToiThieu(spnTuoiToiThieu.getValue());
        quyDinh.setTuoiToiDa(spnTuoiToiDa.getValue());
        quyDinh.setSoCTToiThieu(spnSoCTToiThieu.getValue());
        quyDinh.setSoCTToiDa(spnSoCTToiDa.getValue());
        quyDinh.setSoCTNuocNgoaiToiDa(spnSoCTNuocNgoaiToiDa.getValue());
        quyDinh.setPhutGhiBanToiDa(spnPhutGhiBanToiDa.getValue());
        quyDinh.setDiemThang(spnSoDiemThang.getValue());
        quyDinh.setDiemHoa(spnSoDiemHoa.getValue());
        quyDinh.setDiemThua(spnSoDiemThua.getValue());
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

        if (spnPhutGhiBanToiDa.getValue() <= 0) {
            sb.append("Phút ghi bàn tối đa phải lớn hơn 0!\n");
        }

        if (spnSoDiemThang.getValue() <= 0 || spnSoDiemHoa.getValue() < 0 || spnSoDiemThua.getValue() < 0) {
            sb.append("Điểm thắng, hòa và thua phải lớn hơn hoặc bằng 0!\n");
        }
        if (spnSoDiemThang.getValue() <= spnSoDiemHoa.getValue()) {
            sb.append("Điểm thắng phải lớn hơn điểm hòa!\n");
        }
        if (spnSoDiemHoa.getValue() <= spnSoDiemThua.getValue()) {
            sb.append("Điểm hòa phải lớn hơn điểm thua!\n");
        }
        if (priorityList.isEmpty()) {
            sb.append("Danh sách thứ tự ưu tiên không được để trống!\n");
        }

        if (!sb.isEmpty()) {
            AlertUtils.showError("Lỗi", "Kiểm tra dữ liệu không hợp lệ", sb.toString());
            return false;
        }

        return true;
    }


    @FXML
    private ImageView userIcon;

    @FXML
    private void showUserPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/UserPopup.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.NONE);
            popupStage.initStyle(StageStyle.UNDECORATED);

            Scene scene = new Scene(root);
            popupStage.setScene(scene);

            popupStage.setX(userIcon.localToScreen(0, 0).getX() - 100);
            popupStage.setY(userIcon.localToScreen(0, 0).getY() + 40);

            popupStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    popupStage.close();
                }
            });

            popupStage.initOwner(userIcon.getScene().getWindow());

            popupStage.show();
        } catch (Exception e) {
            System.err.println("Lỗi hiển thị UserPopup: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private int okk=0;
    private void sendRulesUpdateToClub() {
        // Create a non-blocking alert window
        Stage sendingStage = new Stage();
        sendingStage.initStyle(StageStyle.UTILITY);
        sendingStage.setAlwaysOnTop(true);
        sendingStage.setResizable(false);
        sendingStage.setTitle("Sending...");
        Label label = new Label("Đang gửi thông báo đến các Câu lạc bộ...");
        label.setStyle("-fx-padding: 20px; -fx-font-size: 14px;");
        Scene scene = new Scene(label);
        sendingStage.setScene(scene);
        sendingStage.show();

        // Run email sending in a background thread to avoid UI freeze
        new Thread(() -> {
            List<Integer> clubIDs = service.getRegistedClubIdsByTournament(cboMuaGiai.getValue().getMaGD());
            okk = 0;
            for (Integer clubID : clubIDs) {
                String email = service.getCLBByID(clubID).getEmail();
                String subject = "Thông báo cập nhật quy định mùa giải";
                String content = "Chào quý Câu lạc bộ,\n\n" +
                        "Chúng tôi xin thông báo rằng quy định của mùa giải " + cboMuaGiai.getValue().getTenGD() + " đã được cập nhật.\n" +
                        "Vui lòng kiểm tra lại các quy định mới để đảm bảo tuân thủ trong quá trình tham gia giải đấu.\n\n" +
                        "Trân trọng,\n" +
                        "Ban tổ chức giải đấu";
                File file = exportService.exportTournamentInfo(cboMuaGiai.getValue().getMaGD());
                try {
                    emailService.sendEmail(email, subject, content, file);
                } catch (Exception e) {
                    okk=1;
                    javafx.application.Platform.runLater(() -> {
                        AlertUtils.showError("Lỗi gửi email", "Không thể gửi email đến Câu lạc bộ",
                                "Đã xảy ra lỗi khi gửi email đến Câu lạc bộ: " + clubID + ". Vui lòng kiểm tra lại thông tin email hoặc kết nối mạng.");
                    });
                    e.printStackTrace();
                }
            }
            // Close the sending alert and show result
            javafx.application.Platform.runLater(() -> {
                sendingStage.close();
                if (okk == 0) {
                    AlertUtils.showInformation("Thông báo", "Gửi thông báo thành công",
                            "Đã gửi thông báo cập nhật quy định đến tất cả các Câu lạc bộ đã đăng ký tham gia mùa giải: " + cboMuaGiai.getValue().getTenGD());
                }
            });
        }).start();
    }
}
