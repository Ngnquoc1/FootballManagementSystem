package Controller;

import Model.MODEL_MUAGIAI;
import Model.MODEL_VONGDAU;
import Service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class RoundController {

    @FXML private Label tournamentNameLabel;
    @FXML private Label tournamentDatesLabel;
    @FXML private Label tournamentStatusLabel;
    @FXML private Label totalRoundsLabel;

    @FXML private TableView<MODEL_VONGDAU> roundsTableView;
    @FXML private TableColumn<MODEL_VONGDAU, Integer> idColumn;
    @FXML private TableColumn<MODEL_VONGDAU, String> nameColumn;
    @FXML private TableColumn<MODEL_VONGDAU, Date> startDateColumn;
    @FXML private TableColumn<MODEL_VONGDAU, Date> endDateColumn;
    @FXML private TableColumn<MODEL_VONGDAU, String> statusColumn;

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button finishButton;

    private MODEL_MUAGIAI tournament;
    private ObservableList<MODEL_VONGDAU> roundsList = FXCollections.observableArrayList();
    private MODEL_VONGDAU currentRound;
    private boolean isEditing = false;
    private int nextId = 1;
    private Service service = new Service();
    @FXML
    private void initialize() {
        // Thiết lập các cột cho TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("maVD"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("tenVD"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("ngayBD"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("ngayKT"));

        // Định dạng hiển thị ngày tháng
        startDateColumn.setCellFactory(column -> new TableCell<MODEL_VONGDAU, Date>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });

        endDateColumn.setCellFactory(column -> new TableCell<MODEL_VONGDAU, Date>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });

        // Thêm cột trạng thái (tính toán dựa trên ngày hiện tại)
        statusColumn.setCellFactory(column -> new TableCell<MODEL_VONGDAU, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    MODEL_VONGDAU round = getTableView().getItems().get(getIndex());
                    LocalDate today = LocalDate.now();
                    LocalDate startDate = round.getNgayBD().toLocalDate();
                    LocalDate endDate = round.getNgayKT().toLocalDate();

                    String statusText;
                    if (today.isBefore(startDate)) {
                        statusText = "Sắp diễn ra";
                        setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                    } else if (today.isAfter(endDate)) {
                        statusText = "Đã kết thúc";
                        setStyle("-fx-text-fill: grey;");
                    } else {
                        statusText = "Đang diễn ra";
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    }
                    setText(statusText);
                }
            }
        });

        // Thiết lập danh sách vòng đấu cho TableView
        roundsTableView.setItems(roundsList);

        // Xử lý sự kiện khi chọn một vòng đấu trong bảng
        roundsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                editButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        // Thiết lập giới hạn ngày cho DatePicker
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (tournament != null) {
                    LocalDate tournamentStart = tournament.getNgayBD();
                    LocalDate tournamentEnd = tournament.getNgayKT();
                    setDisable(empty || date.isBefore(tournamentStart) || date.isAfter(tournamentEnd));
                }
            }
        });

        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (tournament != null) {
                    LocalDate tournamentStart = tournament.getNgayBD();
                    LocalDate tournamentEnd = tournament.getNgayKT();
                    setDisable(empty || date.isBefore(tournamentStart) || date.isAfter(tournamentEnd));
                }
            }
        });
    }

    // Phương thức để thiết lập thông tin giải đấu
    public void setTournament(MODEL_MUAGIAI tournament) {
        this.tournament = tournament;

        // Cập nhật thông tin giải đấu trên giao diện
        tournamentNameLabel.setText("QUẢN LÝ VÒNG ĐẤU - " + tournament.getTenMG());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateRange = tournament.getNgayBD().format(formatter) + " - " + tournament.getNgayKT().format(formatter);
        tournamentDatesLabel.setText("Thời gian: " + dateRange);

        // Cập nhật trạng thái giải đấu
        String status = tournament.getStatus();
        tournamentStatusLabel.setText(status);

        switch (status) {
            case "Đang diễn ra":
                tournamentStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                break;
            case "Sắp diễn ra":
                tournamentStatusLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                break;
            case "Đã kết thúc":
                tournamentStatusLabel.setStyle("-fx-text-fill: grey;");
                break;
        }

        // Tải danh sách vòng đấu của giải đấu này
        loadRounds();
    }

    private void loadRounds() {
        try {
            // Tải danh sách vòng đấu từ cơ sở dữ liệu
            List<MODEL_VONGDAU> rounds = service.getAllRoundByTournament(tournament.getMaMG());
            roundsList.clear();
            roundsList.addAll(rounds);

            // Cập nhật nextId
            if (!rounds.isEmpty()) {
                nextId = service.getNextIdVD();
            }

            // Cập nhật tổng số vòng đấu
            updateTotalRounds();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách vòng đấu", e.getMessage());
        }
    }

    private void updateTotalRounds() {
        totalRoundsLabel.setText(String.valueOf(roundsList.size()));
    }

    @FXML
    private void handleAddRound() {
        resetForm();
        isEditing = false;
        enableForm(true);
    }

    @FXML
    private void handleEditRound() {
        MODEL_VONGDAU selectedRound = roundsTableView.getSelectionModel().getSelectedItem();
        if (selectedRound != null) {
            currentRound = selectedRound;
            isEditing = true;

            // Điền thông tin vào form
            idField.setText(String.valueOf(selectedRound.getMaVD()));
            nameField.setText(selectedRound.getTenVD());  // Không cần ép kiểu nữa
            startDatePicker.setValue(selectedRound.getNgayBD().toLocalDate());
            endDatePicker.setValue(selectedRound.getNgayKT().toLocalDate());

            enableForm(true);
        }
    }

    @FXML
    private void handleDeleteRound() {
        MODEL_VONGDAU selectedRound = roundsTableView.getSelectionModel().getSelectedItem();
        if (selectedRound != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText(null);
            alert.setContentText("Bạn có chắc chắn muốn xóa vòng đấu này?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Xóa vòng đấu khỏi cơ sở dữ liệu
                    boolean success = service.deleteVD(selectedRound.getMaVD());
                    if (!success) {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa vòng đấu", "Đã xảy ra lỗi khi xóa vòng đấu khỏi cơ sở dữ liệu.");
                        return;
                    }

                    // Xóa khỏi danh sách hiển thị
                    roundsList.remove(selectedRound);
                    updateTotalRounds();

                    showAlert(Alert.AlertType.INFORMATION, "Thành công", null, "Đã xóa vòng đấu thành công!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa vòng đấu", e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Thông tin không hợp lệ",
                    "Vui lòng điền đầy đủ thông tin vòng đấu và đảm bảo ngày kết thúc sau ngày bắt đầu!");
            return;
        }

        try {
            String name = nameField.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (isEditing && currentRound != null) {
                // Cập nhật vòng đấu hiện tại
                currentRound.setTenVD(name);  // Không cần ép kiểu nữa
                currentRound.setNgayBD(Date.valueOf(startDate));
                currentRound.setNgayKT(Date.valueOf(endDate));

                // Cập nhật trong cơ sở dữ liệu
                boolean success = service.updateVD(currentRound);
                if (!success) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật vòng đấu", "Đã xảy ra lỗi khi cập nhật vòng đấu trong cơ sở dữ liệu.");
                    return;
                }

                roundsTableView.refresh();
            } else {
                // Tạo vòng đấu mới
                MODEL_VONGDAU newRound = new MODEL_VONGDAU();
                newRound.setMaVD(nextId);
                newRound.setTenVD(name);  // Không cần ép kiểu nữa
                newRound.setMaMG(tournament.getMaMG());
                newRound.setNgayBD(Date.valueOf(startDate));
                newRound.setNgayKT(Date.valueOf(endDate));

                // Lưu vào cơ sở dữ liệu
                boolean success = service.insertVD(newRound);
                if (!success) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm vòng đấu", "Đã xảy ra lỗi khi thêm vòng đấu vào cơ sở dữ liệu.");
                    return;
                }

                roundsList.add(newRound);
                nextId = service.getNextIdVD();
            }

            updateTotalRounds();
            resetForm();
            enableForm(false);

            showAlert(Alert.AlertType.INFORMATION, "Thành công", null, "Đã lưu thông tin vòng đấu thành công!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu vòng đấu", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        resetForm();
        enableForm(false);
    }

    @FXML
    private void handleFinish() {
        // Đóng cửa sổ hiện tại
        Stage stage = (Stage) finishButton.getScene().getWindow();
        stage.close();
    }

    private boolean validateForm() {
        // Kiểm tra tên vòng đấu không được để trống
        return nameField.getText() != null && !nameField.getText().isEmpty() &&
                startDatePicker.getValue() != null &&
                endDatePicker.getValue() != null &&
                !endDatePicker.getValue().isBefore(startDatePicker.getValue());
    }

    private void resetForm() {
        idField.clear();
        nameField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);

        currentRound = null;
    }

    private void enableForm(boolean enable) {
        nameField.setDisable(!enable);
        startDatePicker.setDisable(!enable);
        endDatePicker.setDisable(!enable);
        saveButton.setDisable(!enable);
        cancelButton.setDisable(!enable);
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}