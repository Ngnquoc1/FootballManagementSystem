package Controller;

import Model.*;
import Service.*;
import Util.AlertUtils;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {

    @FXML
    private ComboBox<MODEL_CLB> cboCLB;

    @FXML
    private ComboBox<MODEL_MUAGIAI> cboMuaGiai;

    @FXML
    private Label lblTrangThaiDangKy,lblTrangThaiMuaGiai,lblTuoiToiThieu,
                  lblTuoiToiDa,lblSoCTNuocNgoaiToiDa,lblSoCTToiThieu, lblSoCTToiDa,lblThongKeCauThu;

    @FXML
    private TableView<CauThuViewModel> tableCauThu;

    @FXML
    private TableColumn<CauThuViewModel, Boolean> colChon;

    @FXML
    private TableColumn<CauThuViewModel, Integer> colMaCT;

    @FXML
    private TableColumn<CauThuViewModel, String> colTenCT;

    @FXML
    private TableColumn<CauThuViewModel, Integer> colTuoi;

    @FXML
    private TableColumn<CauThuViewModel, String> colQuocTich;

    @FXML
    private TableColumn<CauThuViewModel, Integer> colSoAo;

    @FXML
    private TableColumn<CauThuViewModel, String> colViTri;

    @FXML
    private TableColumn<CauThuViewModel, String> colTrangThai;

    private Service service;

    private ObservableList<MODEL_CLB> danhSachCLB;
    private ObservableList<MODEL_MUAGIAI> danhSachMuaGiai;
    private ObservableList<CauThuViewModel> danhSachCauThu;

    private MODEL_QUYDINH quyDinhHienTai;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        service = new Service();

        // Thiết lập ComboBox
        setupComboBoxes();

        // Thiết lập TableView
        setupTableView();

        // Tải dữ liệu
        loadPlayerList();
        loadTournamentList();
    }

    private void setupComboBoxes() {
        // ComboBox CLB
        cboCLB.setConverter(new StringConverter<MODEL_CLB>() {
            @Override
            public String toString(MODEL_CLB clb) {
                return clb != null ? clb.getTenCLB() : "";
            }

            @Override
            public MODEL_CLB fromString(String string) {
                return null;
            }
        });

        // ComboBox Mùa giải
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

    private void setupTableView() {
        // Thiết lập các cột
        colChon.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colChon.setCellFactory(CheckBoxTableCell.forTableColumn(colChon));
        colChon.setEditable(true);

        colMaCT.setCellValueFactory(cellData -> cellData.getValue().maCTProperty().asObject());
        colTenCT.setCellValueFactory(cellData -> cellData.getValue().tenCTProperty());
        colTuoi.setCellValueFactory(cellData -> cellData.getValue().tuoiProperty().asObject());
        colQuocTich.setCellValueFactory(cellData -> cellData.getValue().quocTichProperty());
        colSoAo.setCellValueFactory(cellData -> cellData.getValue().soAoProperty().asObject());
        colViTri.setCellValueFactory(cellData -> cellData.getValue().viTriProperty());
        colTrangThai.setCellValueFactory(cellData -> cellData.getValue().trangThaiProperty());

        tableCauThu.setEditable(true);

        // Lắng nghe thay đổi selection để cập nhật thống kê
        tableCauThu.getItems().addListener((javafx.collections.ListChangeListener<CauThuViewModel>) change -> {
            updateStatistics();
        });
    }

    private void loadPlayerList() {
        List<MODEL_CLB> list = service.getAllClubs();
        danhSachCLB = FXCollections.observableArrayList(list);
        cboCLB.setItems(danhSachCLB);
    }

    private void loadTournamentList() {
        List<MODEL_MUAGIAI> list = service.getAllActiveTournaments();
        danhSachMuaGiai = FXCollections.observableArrayList(list);
        cboMuaGiai.setItems(danhSachMuaGiai);
    }

    @FXML
    private void selectClub(ActionEvent event) {
        MODEL_CLB selectedCLB = cboCLB.getValue();
        if (selectedCLB != null) {
            loadPlayersList(selectedCLB.getMaCLB());
            checkRegistrationStatus();
        }
    }

    @FXML
    private void selectTournament(ActionEvent event) {
        MODEL_MUAGIAI selectedMuaGiai = cboMuaGiai.getValue();
        if (selectedMuaGiai != null) {
            // Hiển thị trạng thái mùa giải
            lblTrangThaiMuaGiai.setText(selectedMuaGiai.getStatus());

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

            loadTournamentRules(selectedMuaGiai.getMaMG());
            checkRegistrationStatus();
        }
    }

    private void loadPlayersList(int maCLB) {
        List<MODEL_CAUTHU> list = service.getPlayersByClubId(maCLB);
        danhSachCauThu = FXCollections.observableArrayList();

        for (MODEL_CAUTHU cauThu : list) {
            CauThuViewModel viewModel = new CauThuViewModel(cauThu);
            // Thêm listener cho checkbox
            viewModel.selectedProperty().addListener((obs, oldVal, newVal) -> {
                updateStatistics();
            });
            danhSachCauThu.add(viewModel);
        }

        tableCauThu.setItems(danhSachCauThu);
        updateStatistics();
    }

    private void loadTournamentRules(int maMG) {
        quyDinhHienTai = service.getQDByMaMG(maMG);
        if (quyDinhHienTai != null) {
            lblTuoiToiThieu.setText(String.valueOf(quyDinhHienTai.getTuoiToiThieu()));
            lblTuoiToiDa.setText(String.valueOf(quyDinhHienTai.getTuoiToiDa()));
            lblSoCTNuocNgoaiToiDa.setText(String.valueOf(quyDinhHienTai.getSoCTNuocNgoaiToiDa()));
            lblSoCTToiThieu.setText(String.valueOf(quyDinhHienTai.getSoCTToiThieu()));
            lblSoCTToiDa.setText(String.valueOf(quyDinhHienTai.getSoCTToiDa()));
        }
    }

    private void checkRegistrationStatus() {
        MODEL_CLB selectedCLB = cboCLB.getValue();
        MODEL_MUAGIAI selectedMuaGiai = cboMuaGiai.getValue();

        if (selectedCLB != null && selectedMuaGiai != null) {
            boolean daDangKy = service.checkRegistration(selectedCLB.getMaCLB(), selectedMuaGiai.getMaMG());
            if (daDangKy) {
                lblTrangThaiDangKy.setText("Đã đăng ký");
                lblTrangThaiDangKy.setTextFill(javafx.scene.paint.Color.GREEN);
                // Load danh sách cầu thủ đã đăng ký
                loadRegistedPlayers(selectedCLB.getMaCLB(), selectedMuaGiai.getMaMG());
            } else {
                lblTrangThaiDangKy.setText("Chưa đăng ký");
                lblTrangThaiDangKy.setTextFill(javafx.scene.paint.Color.RED);
            }
        }
    }

    private void loadRegistedPlayers(int maCLB, int maMG) {
        List<MODEL_CAUTHUTHAMGIACLB> danhSachDaDangKy = service.getRegistedPlayers(maCLB, maMG);

        for (CauThuViewModel viewModel : danhSachCauThu) {
            boolean daDangKy = danhSachDaDangKy.stream()
                    .anyMatch(ct -> ct.getMaCT() == viewModel.getMaCT());
            viewModel.setSelected(daDangKy);
        }

        updateStatistics();
    }

    private void updateStatistics() {
        if (danhSachCauThu == null) return;

        int soDaChon = 0;
        int soNuocNgoai = 0;
        int tongSo = danhSachCauThu.size();

        for (CauThuViewModel viewModel : danhSachCauThu) {
            if (viewModel.isSelected()) {
                soDaChon++;
                if (!"Viet Nam".equals(viewModel.getQuocTich())) {
                    soNuocNgoai++;
                }
            }
        }

        int maxNuocNgoai = quyDinhHienTai != null ? quyDinhHienTai.getSoCTNuocNgoaiToiDa() : 3;

        lblThongKeCauThu.setText(String.format("Đã chọn: %d/%d | Nước ngoài: %d/%d",
                soDaChon, tongSo, soNuocNgoai, maxNuocNgoai));
    }

    @FXML
    private void selectAll(ActionEvent event) {
        if (danhSachCauThu != null) {
            for (CauThuViewModel viewModel : danhSachCauThu) {
                if ("Đủ điều kiện".equals(viewModel.getTrangThai())) {
                    viewModel.setSelected(true);
                }
            }
        }
    }

    @FXML
    private void unselectAll(ActionEvent event) {
        if (danhSachCauThu != null) {
            for (CauThuViewModel viewModel : danhSachCauThu) {
                viewModel.setSelected(false);
            }
        }
    }

    @FXML
    private void addRegistration(ActionEvent event) {
        MODEL_CLB selectedCLB = cboCLB.getValue();
        MODEL_MUAGIAI selectedMuaGiai = cboMuaGiai.getValue();

        if (selectedCLB == null || selectedMuaGiai == null) {
            AlertUtils.showError("Lỗi","Chưa chọn CLB hoặc mùa giải!",
                    "Vui lòng chọn CLB và mùa giải trước khi đăng ký!");
            return;
        }

        // Kiểm tra trạng thái mùa giải
        if ("Đã kết thúc".equals(selectedMuaGiai.getStatus())) {
            AlertUtils.showError("Lỗi","Mùa giải đã kết thúc!",
                    "Không thể đăng ký cho mùa giải đã kết thúc!");
            return;
        }

        if (!checkRules()) {
            return;
        }

        boolean ok=AlertUtils.showConfirmation("Xác nhận đăng ký",
                "Đăng ký tham gia mùa giải",
                "Bạn có chắc chắn muốn đăng ký tham gia mùa giải này?");
        if (ok) {
            boolean ketQua = service.addRegistration(selectedCLB.getMaCLB(), selectedMuaGiai.getMaMG(),
                    getPlayerList());

            if (ketQua) {
                AlertUtils.showInformation("Thông báo","Đăng ký tham gia thành công!",
                        "Bạn đã đăng ký tham gia mùa giải thành công!");
                checkRegistrationStatus();
            } else {
                AlertUtils.showError("Lỗi","Đăng ký tham gia thất bại!",
                        "Có lỗi xảy ra trong quá trình đăng ký tham gia!");

            }
        }
    }

    @FXML
    private void removeRegistration(ActionEvent event) {
        MODEL_CLB selectedCLB = cboCLB.getValue();
        MODEL_MUAGIAI selectedMuaGiai = cboMuaGiai.getValue();

        if (selectedCLB == null || selectedMuaGiai == null) {
            AlertUtils.showError("Lỗi","Chưa chọn CLB hoặc mùa giải!",
                    "Vui lòng chọn CLB và mùa giải trước khi hủy đăng ký!");
            return;
        }

        // Kiểm tra trạng thái mùa giải
        if ("Đang diễn ra".equals(selectedMuaGiai.getStatus())) {
            AlertUtils.showError("Lỗi","Không thể hủy đăng ký!",
                    "Mùa giải đang diễn ra, không thể hủy đăng ký!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận hủy đăng ký");
        alert.setHeaderText("Hủy đăng ký tham gia mùa giải");
        alert.setContentText("Bạn có chắc chắn muốn hủy đăng ký tham gia mùa giải này?");

        Optional<ButtonType> result = alert.showAndWait();
        boolean ok=AlertUtils.showConfirmation("Xác nhận hủy đăng ký",
                "Hủy đăng ký tham gia mùa giải",
                "Bạn có chắc chắn muốn hủy đăng ký tham gia mùa giải này?");
        if (ok) {
            boolean ketQua = service.removeRegistration(selectedCLB.getMaCLB(), selectedMuaGiai.getMaMG());

            if (ketQua) {
                AlertUtils.showInformation("Thông báo","Hủy đăng ký thành công!",
                        "Bạn đã hủy đăng ký tham gia mùa giải thành công!");
                checkRegistrationStatus();
                unselectAll(null);
            } else {
                AlertUtils.showError("Lỗi","Hủy đăng ký thất bại!",
                        "Có lỗi xảy ra trong quá trình hủy đăng ký!");
            }
        }
    }

    @FXML
    private void quanLyCauThu(ActionEvent event) {
        openPlayerRegistrationWindow(cboCLB.getValue());

    }

    private boolean checkRules() {
        if (quyDinhHienTai == null) {
            AlertUtils.showError("Lỗi","Chưa có quy định nào được chọn!",
                    "Vui lòng chọn mùa giải trước khi kiểm tra quy định!");
            return false;
        }

        List<CauThuViewModel> danhSachDaChon = getPlayerList();

        // Kiểm tra số lượng cầu thủ
        if (danhSachDaChon.size() < quyDinhHienTai.getSoCTToiThieu()) {
            AlertUtils.showError("Lỗi","Số lượng cầu thủ không đủ!",
                    String.format("Số lượng cầu thủ phải ít nhất %d người!", quyDinhHienTai.getSoCTToiThieu()));

            return false;
        }

        if (danhSachDaChon.size() > quyDinhHienTai.getSoCTToiDa()) {
            AlertUtils.showError("Lỗi","Số lượng cầu thủ vượt quá quy định!",
                    String.format("Số lượng cầu thủ không được vượt quá %d người!", quyDinhHienTai.getSoCTToiDa()));

            return false;
        }

        // Kiểm tra tuổi và số cầu thủ nước ngoài
        int soCauThuNuocNgoai = 0;
        for (CauThuViewModel cauThu : danhSachDaChon) {
            if (cauThu.getTuoi() < quyDinhHienTai.getTuoiToiThieu() ||
                    cauThu.getTuoi() > quyDinhHienTai.getTuoiToiDa()) {
                AlertUtils.showError("Lỗi","Tuổi cầu thủ không hợp lệ!",
                        String.format("Cầu thủ %s không đủ điều kiện về tuổi!", cauThu.getTenCT()));
                return false;
            }

            if (!"Việt Nam".equals(cauThu.getQuocTich())) {
                soCauThuNuocNgoai++;
            }
        }

        if (soCauThuNuocNgoai > quyDinhHienTai.getSoCTNuocNgoaiToiDa()) {
            AlertUtils.showError("Lỗi","Số cầu thủ nước ngoài vượt quá quy định!",
                    String.format("Số cầu thủ nước ngoài không được vượt quá %d người!", quyDinhHienTai.getSoCTNuocNgoaiToiDa()));
            return false;
        }

        return true;
    }

    private List<CauThuViewModel> getPlayerList() {
        return danhSachCauThu.stream()
                .filter(CauThuViewModel::isSelected)
                .collect(java.util.stream.Collectors.toList());
    }

    private void openPlayerRegistrationWindow(MODEL_CLB club) {
        try {
            // Kiểm tra xem CLB có hợp lệ không
            if (club == null) {
                AlertUtils.showWarning("Cảnh báo", "Chưa chọn CLB", "Vui lòng chọn một CLB để đăng ký cầu thủ.");
                return;
            }
            // Tải FXML cho cửa sổ đăng ký cầu thủ
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/PLayerManagementFrame.fxml"));
            Parent root = loader.load();

            // Lấy controller
            PlayerManagementController controller = loader.getController();

            // Thiết lập thông tin CLB và giải đấu
            controller.setPlayersClub(club);
            controller.setRefreshCallback(() -> loadPlayersList(club.getMaCLB()));
            // Tạo scene mới
            Scene scene = new Scene(root);

            // Tạo stage mới
            Stage stage = new Stage();
            stage.setTitle("Đăng ký cầu thủ - " + club.getTenCLB() );
            stage.setScene(scene);
            stage.setResizable(false);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Không thể mở cửa sổ đăng ký cầu thủ", e.getMessage());
        }
    }

    // ViewModel class cho cầu thủ
    public static class CauThuViewModel {
        private final SimpleBooleanProperty selected;
        private final SimpleIntegerProperty maCT;
        private final SimpleStringProperty tenCT;
        private final SimpleIntegerProperty tuoi;
        private final SimpleStringProperty quocTich;
        private final SimpleIntegerProperty soAo;
        private final SimpleStringProperty viTri;
        private final SimpleStringProperty trangThai;
        private Service service;
        public CauThuViewModel(MODEL_CAUTHU cauThu) {
            service = new Service();
            this.selected = new SimpleBooleanProperty(false);
            this.maCT = new SimpleIntegerProperty(cauThu.getMaCT());
            this.tenCT = new SimpleStringProperty(cauThu.getTenCT());
            this.tuoi = new SimpleIntegerProperty(calculateAge(cauThu.getNgaysinh()));
            this.quocTich = new SimpleStringProperty(cauThu.getQuocTich());
            this.soAo = new SimpleIntegerProperty(cauThu.getSoAo());
            this.viTri = new SimpleStringProperty(getTenVT(cauThu.getMaVT()));
            this.trangThai = new SimpleStringProperty(checkStatus(cauThu));
        }

        private int calculateAge(java.sql.Date ngaySinh) {
            if (ngaySinh == null) return 0;
            LocalDate birthDate = ngaySinh.toLocalDate();
            LocalDate currentDate = LocalDate.now();
            return Period.between(birthDate, currentDate).getYears();
        }

        private String getTenVT(int maVT) {
            return service.getPositionById(maVT);        }

        private String checkStatus(MODEL_CAUTHU cauThu) {
            int tuoi = calculateAge(cauThu.getNgaysinh());
            if (tuoi < 16 || tuoi > 40) {
                return "Không đủ điều kiện";
            }
            return "Đủ điều kiện";
        }

        // Getters and Property methods
        public SimpleBooleanProperty selectedProperty() { return selected; }
        public boolean isSelected() { return selected.get(); }
        public void setSelected(boolean selected) { this.selected.set(selected); }

        public SimpleIntegerProperty maCTProperty() { return maCT; }
        public int getMaCT() { return maCT.get(); }

        public SimpleStringProperty tenCTProperty() { return tenCT; }
        public String getTenCT() { return tenCT.get(); }

        public SimpleIntegerProperty tuoiProperty() { return tuoi; }
        public int getTuoi() { return tuoi.get(); }

        public SimpleStringProperty quocTichProperty() { return quocTich; }
        public String getQuocTich() { return quocTich.get(); }

        public SimpleIntegerProperty soAoProperty() { return soAo; }
        public int getSoAo() { return soAo.get(); }

        public SimpleStringProperty viTriProperty() { return viTri; }
        public String getViTri() { return viTri.get(); }

        public SimpleStringProperty trangThaiProperty() { return trangThai; }
        public String getTrangThai() { return trangThai.get(); }
    }
}