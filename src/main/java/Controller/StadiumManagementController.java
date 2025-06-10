package Controller;

import Model.MODEL_SAN;
import Service.Service;
import Util.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class StadiumManagementController {
    @FXML private TextField txtTenSan;
    @FXML private TextField txtDiaChi;
    @FXML private TextField txtSucChua;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;
    @FXML private TableView<MODEL_SAN> stadiumTable;
    @FXML private TableColumn<MODEL_SAN, Integer> colMaSan;
    @FXML private TableColumn<MODEL_SAN, String> colTenSan;
    @FXML private TableColumn<MODEL_SAN, String> colDiaChi;
    @FXML private TableColumn<MODEL_SAN, Long> colSucChua;

    private final Service service = new Service();
    private ObservableList<MODEL_SAN> stadiumList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMaSan.setCellValueFactory(cellData -> cellData.getValue().maSanProperty().asObject());
        colTenSan.setCellValueFactory(cellData -> cellData.getValue().tenSanProperty());
        colDiaChi.setCellValueFactory(cellData -> cellData.getValue().diaChiProperty());
        colSucChua.setCellValueFactory(cellData -> cellData.getValue().sucChuaProperty().asObject());

        stadiumTable.setItems(stadiumList);
        loadStadiums();

        stadiumTable.setOnMouseClicked(this::onTableClicked);

        btnAdd.setOnAction(e -> addStadium());
        btnEdit.setOnAction(e -> editStadium());
        btnDelete.setOnAction(e -> deleteStadium());
        btnRefresh.setOnAction(e -> loadStadiums());
    }

    private void loadStadiums() {
        stadiumList.clear();
        stadiumList.addAll(service.getAllStadiums());
        clearFields();
    }

    private void onTableClicked(MouseEvent event) {
        MODEL_SAN selected = stadiumTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtTenSan.setText(selected.getTenSan());
            txtDiaChi.setText(selected.getDiaChi());
            txtSucChua.setText(String.valueOf(selected.getSucChua()));
        }
    }

    private void addStadium() {
        String tenSan = txtTenSan.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String sucChuaStr = txtSucChua.getText().trim();
        if (tenSan.isEmpty() || diaChi.isEmpty() || sucChuaStr.isEmpty()) {
            AlertUtils.showError("Thông báo", "Thông tin không đầy đủ", "Vui lòng nhập đầy đủ thông tin sân.");
            return;
        }
        try {
            int sucChua = Integer.parseInt(sucChuaStr);
            MODEL_SAN san = new MODEL_SAN();
            san.setTenSan(tenSan);
            san.setDiaChi(diaChi);
            san.setSucChua(sucChua);
            service.addStadium(san);
            loadStadiums();
            AlertUtils.showInformation("Thông báo", "Thêm sân thành công", "Sân đã được thêm vào danh sách.");
        } catch (Exception ex) {
            AlertUtils.showError("Lỗi", "Không thể thêm sân", "Vui lòng kiểm tra lại thông tin nhập vào.");
        }
    }

    private void editStadium() {
        MODEL_SAN selected = stadiumTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showError("Thông báo", "Chưa chọn sân", "Vui lòng chọn sân để sửa.");
            return;
        }
        String tenSan = txtTenSan.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String sucChuaStr = txtSucChua.getText().trim();
        if (tenSan.isEmpty() || diaChi.isEmpty() || sucChuaStr.isEmpty()) {
            AlertUtils.showError("Thông báo", "Thông tin không đầy đủ", "Vui lòng nhập đầy đủ thông tin sân.");
            return;
        }
        try {
            int sucChua = Integer.parseInt(sucChuaStr);
            selected.setTenSan(tenSan);
            selected.setDiaChi(diaChi);
            selected.setSucChua(sucChua);
            service.updateStadium(selected);
            AlertUtils.showInformation("Thông báo", "Sửa sân thành công", "Thông tin sân đã được cập nhật.");
            loadStadiums();
        } catch (Exception ex) {
            AlertUtils.showError("Lỗi", "Không thể sửa sân", "Vui lòng kiểm tra lại thông tin nhập vào.");
        }
    }

    private void deleteStadium() {
        MODEL_SAN selected = stadiumTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showError("Thông báo", "Chưa chọn sân", "Vui lòng chọn sân để xóa.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa sân này?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            try {
                service.deleteStadium(selected.getMaSan());
                AlertUtils.showInformation("Thông báo", "Xóa sân thành công", "Sân đã được xóa khỏi danh sách.");
                loadStadiums();
            } catch (Exception ex) {
                AlertUtils.showError("Lỗi", "Không thể xóa sân: " ,"Sân này đang được sử dụng bởi các CLB.");
            }
        }
    }

    private void clearFields() {
        txtTenSan.clear();
        txtDiaChi.clear();
        txtSucChua.clear();
        stadiumTable.getSelectionModel().clearSelection();
    }


}
