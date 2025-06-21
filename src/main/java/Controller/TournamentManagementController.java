package Controller;

import Model.MODEL_GIAIDAU;
import Model.MODEL_VONGDAU;
import Service.Service;
import Util.AlertUtils;
import Util.FileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.nio.file.Paths;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Service.EmailService;
import Service.ExportService;
public class TournamentManagementController {
    @FXML private Label totalTournamentsLabel;
    @FXML private Label activeTournamentsLabel;
    @FXML private Label upcomingTournamentsLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> compeFilter;

    @FXML private TableView<MODEL_GIAIDAU> tournamentsTableView;
    @FXML private TableColumn<MODEL_GIAIDAU, Integer> idColumn;
    @FXML private TableColumn<MODEL_GIAIDAU, String> nameColumn;
    @FXML private TableColumn<MODEL_GIAIDAU, LocalDate> startDateColumn;
    @FXML private TableColumn<MODEL_GIAIDAU, LocalDate> endDateColumn;
    @FXML private TableColumn<MODEL_GIAIDAU, String> statusColumn;
    @FXML private TableColumn<MODEL_GIAIDAU, Void> logoColumn;

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ImageView logoImageView;
    @FXML private Button chooseLogoButton;

    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button viewDetailsButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button sendInfoButton; // Thêm khai báo nút

    private final ObservableList<MODEL_GIAIDAU> tournamentsList = FXCollections.observableArrayList();
    private FilteredList<MODEL_GIAIDAU> filteredTournaments;
    private MODEL_GIAIDAU currentModel;
    private boolean isEditing = false;

    private final Service service = new Service();

    private File selectedLogoFile;
    private final String LOGO_DIRECTORY = "src/main/resources/Image/LeagueLogo/";

    @FXML
    private void initialize() throws SQLException {
        // Thiết lập các cột cho TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("maGD"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("tenGD"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("ngayBD"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("ngayKT"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        logoColumn.setCellValueFactory(new PropertyValueFactory<>("logoFileName"));

        // Định dạng hiển thị ngày tháng
        startDateColumn.setCellFactory(column -> new TableCell<MODEL_GIAIDAU, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });

        endDateColumn.setCellFactory(column -> new TableCell<MODEL_GIAIDAU, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });

        // Thiết lập màu cho trạng thái
        statusColumn.setCellFactory(column -> new TableCell<MODEL_GIAIDAU, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Đang diễn ra":
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case "Sắp diễn ra":
                            setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                            break;
                        case "Đã kết thúc":
                            setStyle("-fx-text-fill: grey;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
        setFilter();

        // Thiết lập filtered list cho tìm kiếm
        filteredTournaments = new FilteredList<>(tournamentsList, p -> true);
        tournamentsTableView.setItems(filteredTournaments);

        List<MODEL_GIAIDAU> allList = service.getAllTournament();
        tournamentsList.addAll(allList);

        updateStatistics();

        resetForm();
        enableForm(false);

        // Xử lý sự kiện khi chọn một giải đấu trong bảng
        tournamentsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteButton.setDisable(false);
                viewDetailsButton.setDisable(false);
            } else {
                editButton.setDisable(true);
                deleteButton.setDisable(true);
                viewDetailsButton.setDisable(false);
            }
        });
        createLogoDirectory();
    }
    private void setFilter() throws SQLException {

        List<MODEL_GIAIDAU> ds1 = service.getAllTournament();
        ArrayList<String> dsMG = new ArrayList<>();
        for (MODEL_GIAIDAU mg : ds1) {
            dsMG.add(mg.getTenGD());
        }
        compeFilter.getItems().addAll(dsMG);
        compeFilter.getSelectionModel().selectFirst();
    }

    private void createLogoDirectory() {
        File directory = new File(LOGO_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void updateStatistics() {
        int total = tournamentsList.size();
        int active = 0;
        int upcoming = 0;

        LocalDate today = LocalDate.now();

        for (MODEL_GIAIDAU tournament : tournamentsList) {
            if (tournament.getNgayBD().isBefore(today) && tournament.getNgayKT().isAfter(today)) {
                active++;
            } else if (tournament.getNgayBD().isAfter(today)) {
                upcoming++;
            }
        }

        totalTournamentsLabel.setText(String.valueOf(total));
        activeTournamentsLabel.setText(String.valueOf(active));
        upcomingTournamentsLabel.setText(String.valueOf(upcoming));
    }

    @FXML
    private void filterByLeague() {
        String selectedLeague = compeFilter.getSelectionModel().getSelectedItem();
        if (selectedLeague != null) {
            filteredTournaments.setPredicate(tournament -> tournament.getTenGD().equals(selectedLeague));
        } else {
            filteredTournaments.setPredicate(tournament -> true);
        }
    }

    @FXML
    private void resetFilter() {
        filteredTournaments.setPredicate(tournament -> true);
        compeFilter.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        filteredTournaments.setPredicate(MODEL_MUAGIAI -> true);
    }

    @FXML
    private void handleAddTournament() {
        resetForm();
        isEditing = false;
        enableForm(true);
    }

    @FXML
    private void handleEditTournament() {
        MODEL_GIAIDAU selectedTournament = tournamentsTableView.getSelectionModel().getSelectedItem();
        if (selectedTournament != null) {
            currentModel = selectedTournament;
            isEditing = true;

            // Điền thông tin vào form
            idField.setText(String.valueOf(selectedTournament.getMaGD()));
            nameField.setText(selectedTournament.getTenGD());
            startDatePicker.setValue(selectedTournament.getNgayBD());
            endDatePicker.setValue(selectedTournament.getNgayKT());
            logoImageView.setImage(selectedTournament.getLogo());

            // Reset selectedLogoFile nếu đang sử dụng logo đã lưu
            selectedLogoFile = null;

            enableForm(true);
        }
    }

    @FXML
    private void handleDeleteTournament()  {
        MODEL_GIAIDAU selectedTournament = tournamentsTableView.getSelectionModel().getSelectedItem();
        if (selectedTournament != null) {
            boolean choose= AlertUtils.showConfirmation("Xác nhận xóa","", "Bạn có chắc chắn muốn xóa giải đấu này?");
            if (choose) {
                // Xóa file logo nếu có
                String logoFileName = selectedTournament.getLogoFileName();
                if (logoFileName != null && !logoFileName.isEmpty()) {
                    try {
                        Files.deleteIfExists(Paths.get(LOGO_DIRECTORY, logoFileName));
                    } catch (IOException e) {
                        System.err.println("Không thể xóa file logo: " + e.getMessage());
                    }
                }
                try {
                    service.deleteTournament(selectedTournament);}
                catch (Exception e) {
                    AlertUtils.showError("Lỗi", "Không thể xóa giải đấu", e.getMessage());
                    return;
                }
                    // Xóa giải đấu khỏi danh sách
                    tournamentsList.remove(selectedTournament);
                    updateStatistics();
            }
        }
    }

    @FXML
    private void handleChooseLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn Logo Giải Đấu");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) chooseLogoButton.getScene().getWindow();
        selectedLogoFile = fileChooser.showOpenDialog(stage);

        if (selectedLogoFile != null) {
            try {
                Image image = new Image(selectedLogoFile.toURI().toString());
                logoImageView.setImage(image);
            } catch (Exception e) {
                AlertUtils.showError("Lỗi", "Không thể tải hình ảnh", e.getMessage());
            }
        }
    }

    @FXML
    private void handleViewDetails() {
        MODEL_GIAIDAU selectedTournament = tournamentsTableView.getSelectionModel().getSelectedItem();
        if (selectedTournament != null) {
            List<MODEL_VONGDAU> listVD = service.getAllRoundByTournament(selectedTournament.getMaGD());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Chi tiết giải đấu");
            alert.setHeaderText(selectedTournament.getTenGD());

            // Tạo dialog với logo
            DialogPane dialogPane = alert.getDialogPane();

            // Thêm logo vào dialog nếu có
            Image logo = selectedTournament.getLogo();
            if (logo != null) {
                ImageView logoView = new ImageView(logo);
                logoView.setFitHeight(50);
                logoView.setFitWidth(50);
                logoView.setPreserveRatio(true);
                dialogPane.setGraphic(logoView);
            }

            StringBuilder content = new StringBuilder("ID: " + selectedTournament.getMaGD() + "\n" +
                    "Ngày bắt đầu: " + selectedTournament.getNgayBD().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + "\n" +
                    "Ngày kết thúc: " + selectedTournament.getNgayKT().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + "\n" +
                    "Trạng thái: " + selectedTournament.getStatus());
            if (listVD != null && !listVD.isEmpty()) {
                content.append("\n\nDanh sách vòng đấu:\n");
                for (MODEL_VONGDAU vongDau : listVD) {
                    content.append("- ").append(vongDau.getTenVD()).append(" (ID: ").append(vongDau.getMaVD())
                            .append(") - Từ: ").append(vongDau.getNgayBD()).append(" Đến: ").append(vongDau.getNgayKT())
                            .append("\n");
                }
            } else {
                content.append("\n\nKhông có vòng đấu nào trong giải đấu này.");
            }

            alert.setContentText(content.toString());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSave() throws SQLException, IOException {
        if (!validateForm()) {
            AlertUtils.showError("Lỗi", "Thông tin không hợp lệ",
                    "Vui lòng điền đầy đủ thông tin giải đấu và đảm bảo ngày kết thúc sau ngày bắt đầu!");
            return;
        }

        String name = nameField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        MODEL_GIAIDAU savedTournament = null;

        if (isEditing && currentModel != null) {
            // Cập nhật gig đấu hiện tại
            currentModel.setTenGD(name);
            currentModel.setNgayBD(startDate);
            currentModel.setNgayKT(endDate);

            // Cập nhật logo nếu có chọn logo mới
            if (selectedLogoFile != null) {
                // Xóa logo cũ nếu có
                String oldLogoFileName = currentModel.getLogoFileName();
                if (oldLogoFileName != null && !oldLogoFileName.isEmpty()) {
                    try {
                        Files.deleteIfExists(Paths.get(LOGO_DIRECTORY, oldLogoFileName));
                    } catch (IOException e) {
                        System.err.println("Không thể xóa file logo cũ: " + e.getMessage());
                    }
                }
                // Lưu logo mới
                String newLogoFileName = FileUtils.copyLogoToDirectory(selectedLogoFile, LOGO_DIRECTORY, name);
                if (newLogoFileName != null) {
                    currentModel.setLogoFileName(newLogoFileName);
                }
            }
            service.updateTournament(currentModel);
            tournamentsTableView.refresh();
            savedTournament = currentModel;
        } else {
            String logoFileName = null;

            if (selectedLogoFile != null) {
                logoFileName = FileUtils.copyLogoToDirectory(selectedLogoFile, LOGO_DIRECTORY, name);
            }
            MODEL_GIAIDAU newTournament = new MODEL_GIAIDAU(0, name, startDate, endDate, logoFileName);
            int newID=service.insertTournament(newTournament);
            newTournament.setMaGD(newID);
            service.insertDefaultQD(newID);
            tournamentsList.add(newTournament);
            savedTournament = newTournament;
        }

        updateStatistics();
        resetForm();
        enableForm(false);

        // Mở màn hình quản lý vòng đấu sau khi lưu giải đấu
        if (savedTournament != null) {
            openRoundManagement(savedTournament);
        }
    }

    // Phương thức mới để mở màn hình quản lý vòng đấu
    private void openRoundManagement(MODEL_GIAIDAU tournament) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/RoundFrame.fxml"));
            Parent root = loader.load();

            // Lấy controller và thiết lập thông tin giải đấu
            RoundController controller = loader.getController();
            controller.setTournament(tournament);

            // Tạo cửa sổ mới
            Stage stage = new Stage();
            stage.setTitle("Quản lý vòng đấu - " + tournament.getTenGD());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi","", "Không thể mở màn hình quản lý vòng đấu ");
        }
    }

    @FXML
    private void handleCancel() {
        resetForm();
        enableForm(false);
    }

    private boolean validateForm() {
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
        logoImageView.setImage(null);
        selectedLogoFile = null;

        currentModel = null;
    }

    private void enableForm(boolean enable) {
        nameField.setDisable(!enable);
        startDatePicker.setDisable(!enable);
        endDatePicker.setDisable(!enable);
        chooseLogoButton.setDisable(!enable);
        saveButton.setDisable(!enable);
        cancelButton.setDisable(!enable);
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

    @FXML
    private void handleSendInfoToClubs() {
        MODEL_GIAIDAU selectedTournament = tournamentsTableView.getSelectionModel().getSelectedItem();
        if (selectedTournament == null) {
            AlertUtils.showWarning("Chú ý", "Chưa chọn giải đấu", "Vui lòng chọn một giải đấu để gửi thông tin.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nhập email CLB");
        dialog.setHeaderText("Gửi thông tin & quy định cho CLB");
        dialog.setContentText("Nhập email của CLB muốn gửi:");

        String email = dialog.showAndWait().orElse(null);
        if (email == null || email.trim().isEmpty()) {
            AlertUtils.showWarning("Chú ý", "Chưa nhập email", "Vui lòng nhập email của CLB.");
            return;
        }

        if (!isValidEmail(email.trim())) {
            AlertUtils.showError("Lỗi", "Email không hợp lệ", "Vui lòng nhập đúng định dạng email.");
            return;
        }

        try {
            String subject = "Thông tin & Quy định giải đấu: " + selectedTournament.getTenGD();
            String content = service.getTournamentInfoAndRules(selectedTournament.getMaGD());
            EmailService emailService = new EmailService();
            ExportService exportService = new ExportService();
            File file =exportService.exportTournamentInfo(selectedTournament.getMaGD());
            emailService.sendEmail(email.trim(), subject, content,file);
            AlertUtils.showInformation("Thành công", "Đã gửi email", "Thông tin và quy định giải đấu đã được gửi đến CLB.");
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể gửi email", e.getMessage());
        }
    }

    // Hàm kiểm tra định dạng email hợp lệ
    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
