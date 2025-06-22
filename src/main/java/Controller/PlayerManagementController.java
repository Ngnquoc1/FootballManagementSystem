package Controller;

import Model.MODEL_CAUTHU;
import Model.MODEL_CLB;
import Model.MODEL_VITRITD;
import Service.Service;
import Util.AlertUtils;
import Util.FileUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


public class PlayerManagementController implements Initializable {
    @FXML
    private TableView<MODEL_CAUTHU> playersTableView;
    @FXML
    private TableColumn<MODEL_CAUTHU, Integer> idColumn;
    @FXML
    private TableColumn<MODEL_CAUTHU, String> playerNumberColumn;
    @FXML
    private TableColumn<MODEL_CAUTHU, String> playerNameColumn;
    @FXML
    private TableColumn<MODEL_CAUTHU, String> playerDobColumn;
    @FXML
    private TableColumn<MODEL_CAUTHU, String> playerPositionColumn;
    @FXML
    private TableColumn<MODEL_CAUTHU, String> playerNationalityColumn;
    @FXML
    private TableColumn<MODEL_CAUTHU, String> playerAvaColumn;

    @FXML
    private Label idLabel, clubNameLabel;
    @FXML
    private TextField playerNameField;
    @FXML
    private DatePicker playerDobPicker;
    @FXML
    private ComboBox<String> playerNationalityCombo;
    @FXML
    private TextField playerNumberField;
    @FXML
    private ComboBox<String> playerPositionCombo;

    @FXML
    private ComboBox<String> posFilter, playerNoFilter;
    @FXML
    private Button chooseAvaButton, closeBtn, btnAddFromCSV;
    @FXML
    private ImageView avaImageView, clubImgaeView;

    private MODEL_CLB currentClub;
    private Service service;
    private MODEL_CAUTHU selectedPlayer;

    private final ObservableList<MODEL_CAUTHU> playersList = FXCollections.observableArrayList();
    private FilteredList<MODEL_CAUTHU> filteredPlayersList;
    private final String AVA_DIRECTORY = "C:\\\\STUDY\\\\JAVA\\\\DEMO1\\\\src\\\\main\\\\resources\\\\Image\\\\PlayerAva";
    private File selectedAvaFile;
    private PlayerController preController;

    public void setPreController(PlayerController playerController) {
        this.preController = playerController;
    }

    public void setPlayersClub(MODEL_CLB club) {
        this.currentClub = club;
        initializeData();
        loadPlayersData();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void initializeData() {
        service = new Service();

        setupPlayersTable();

        try {
            setCombobox();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        playerNoFilter.setOnAction(event -> {
            String selectedNumber = playerNoFilter.getValue();
            if (selectedNumber != null) {
                filteredPlayersList.setPredicate(player -> {
                    return player.getSoAo() == Integer.parseInt(selectedNumber);
                });
            } else {
                filteredPlayersList.setPredicate(null);
            }
        });
        posFilter.setOnAction(event -> {
            String selectedPosition = posFilter.getValue();
            if (selectedPosition != null) {
                filteredPlayersList.setPredicate(player -> {
                    try {
                        String position = service.getPositionById(player.getMaVT());
                        return position.equals(selectedPosition);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                filteredPlayersList.setPredicate(null);
            }
        });
        initializeCountryComboBox();
        createAvaDirectory();
        clubNameLabel.setText(currentClub.getTenCLB());
        try {
            Image clubLogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/ClubLogo/" + currentClub.getLogoCLB())));
            clubImgaeView.setImage(clubLogo);
        } catch (Exception e) {
            Image defaultLogo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/ClubLogo/default_logo.png")));
            clubImgaeView.setImage(defaultLogo);
        }

    }

    private void createAvaDirectory() {
        File directory = new File(AVA_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void setupPlayersTable() {
        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("maCT")
        );
        playerNumberColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSoAo() + ""));
        playerNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTenCT()));
        playerDobColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNgaysinh().toString()));

        playerPositionColumn.setCellValueFactory(cellData -> {
            int maVT = cellData.getValue().getMaVT();
            String position = service.getPositionById(maVT);
            return new SimpleStringProperty(position != null ? position : "Unknown");
        });
        playerNationalityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getQuocTich()));
        playerAvaColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAvatar()));

    }

    private void loadPlayersData() {
        playersList.clear();
        try {
            playersList.addAll(service.getPlayersByClubId(currentClub.getMaCLB()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        filteredPlayersList = new FilteredList<>(playersList, p -> true);
        playersTableView.setItems(filteredPlayersList);
    }

    private void setCombobox() throws SQLException {
        List<MODEL_CAUTHU> players = service.getPlayersByClubId(currentClub.getMaCLB());
        ObservableList<String> playerNumbers = FXCollections.observableArrayList();
        for (MODEL_CAUTHU player : players) {
            playerNumbers.add(player.getSoAo() + "");
        }
        playerNoFilter.setItems(playerNumbers);
        List<MODEL_VITRITD> positionsList = service.getAllPositions();
        ObservableList<String> positions = FXCollections.observableArrayList();
        for (MODEL_VITRITD pos : positionsList) {
            positions.add(pos.getTenViTri());
        }
        posFilter.setItems(positions);
        playerPositionCombo.setItems(positions);
    }

    private void initializeCountryComboBox() {
        // List of countries
        ObservableList<String> countries = FXCollections.observableArrayList(
                "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Argentina", "Armenia", "Australia",
                "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
                "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei",
                "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Chad",
                "Chile", "China", "Colombia", "Comoros", "Congo", "Costa Rica", "Croatia", "Cuba", "Cyprus",
                "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt",
                "El Salvador", "Estonia", "Eswatini", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia",
                "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guyana", "Haiti",
                "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel",
                "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan",
                "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania",
                "Luxembourg", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
                "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro",
                "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand",
                "Nicaragua", "Niger", "Nigeria", "North Korea", "North Macedonia", "Norway", "Oman", "Pakistan",
                "Palau", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland",
                "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia",
                "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia",
                "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands",
                "Somalia", "South Africa", "South Korea", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname",
                "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste",
                "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda",
                "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "Uzbekistan",
                "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe"
        );

        // Set the items in the ComboBox
        playerNationalityCombo.setItems(countries);
    }

    private MODEL_CAUTHU collectFormData() {
        if (ValidateForm()) {
            MODEL_CAUTHU player = new MODEL_CAUTHU();
            player.setTenCT(playerNameField.getText());
            player.setNgaysinh(java.sql.Date.valueOf(playerDobPicker.getValue()));
            player.setQuocTich(playerNationalityCombo.getValue());
            if (!Objects.equals(playerNationalityCombo.getValue(), "VietNam")) {
                player.setLoaiCT(1);
            } else {
                player.setLoaiCT(0);
            }
            player.setSoAo(Integer.parseInt(playerNumberField.getText()));
            player.setMaCLB(currentClub.getMaCLB());
            player.setMaVT(service.getPositionIdByName(playerPositionCombo.getValue()));
            if (idLabel.getText() == null || idLabel.getText().isEmpty()) {
                player.setMaCT(0);
            } else
                player.setMaCT(Integer.parseInt(idLabel.getText()));
            return player;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Please fill in all fields correctly.");
            alert.showAndWait();
            return null;
        }
    }

    private boolean ValidateForm() {
        if (playerNameField.getText().isEmpty() || playerDobPicker.getValue() == null ||
                playerNationalityCombo.getSelectionModel().getSelectedItem().isEmpty() || playerNumberField.getText().isEmpty() ||
                playerPositionCombo.getValue() == null) {
            return false;
        }
        try {
            Integer.parseInt(playerNumberField.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    @FXML
    private void resetFilter() {
        playerNoFilter.setValue(null);
        posFilter.setValue(null);
        filteredPlayersList.setPredicate(null);
    }

    @FXML
    private void resetForm() {
        playerNameField.clear();
        playerDobPicker.setValue(null);
        playerNationalityCombo.getSelectionModel().clearSelection();
        playerNumberField.clear();
        playerPositionCombo.getSelectionModel().clearSelection();
        avaImageView.setImage(null);
        selectedPlayer = null;
    }

    @FXML
    private void add() {
        resetForm();
    }

    @FXML
    private void update() {
        selectedPlayer = playersTableView.getSelectionModel().getSelectedItem();
        if (selectedPlayer != null) {
            idLabel.setText(String.valueOf(selectedPlayer.getMaCT()));
            playerNameField.setText(selectedPlayer.getTenCT());
            playerDobPicker.setValue(selectedPlayer.getNgaysinh().toLocalDate());
            playerNationalityCombo.setValue(selectedPlayer.getQuocTich());
            playerNumberField.setText(String.valueOf(selectedPlayer.getSoAo()));
            playerPositionCombo.setValue(service.getPositionById(selectedPlayer.getMaVT()));
            Image avaImg;
            try {
                String path = "src/main/resources/Image/PlayerAva/" + selectedPlayer.getAvatar();
                File avaFile = new File(path);
                if (avaFile.exists()) {
                    avaImg = new Image(avaFile.toURI().toString());
                } else {
                    avaImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/PlayerAva/default_ava.png")));
                }
            } catch (Exception e) {
                avaImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Image/PlayerAva/default_ava.png")));
            }
            avaImageView.setImage(avaImg);
        } else {
            AlertUtils.showWarning("Lỗi", "Chưa chọn cầu thủ", "Hãy chọn cầu thủ để cập nhật.");
        }
    }

    @FXML
    private void save() {
        try {
            MODEL_CAUTHU player = collectFormData();
            if (selectedPlayer != null) {
                selectedPlayer.setMaVT(player.getMaVT());
                selectedPlayer.setTenCT(player.getTenCT());
                selectedPlayer.setNgaysinh(player.getNgaysinh());
                selectedPlayer.setQuocTich(player.getQuocTich());
                selectedPlayer.setSoAo(player.getSoAo());
                selectedPlayer.setLoaiCT(player.getLoaiCT());
                selectedPlayer.setMaCLB(player.getMaCLB());
                if (selectedAvaFile != null) {
                    String oldLogoFileName = selectedPlayer.getAvatar();
                    if (oldLogoFileName != null && !oldLogoFileName.isEmpty()) {
                        try {
                            Files.deleteIfExists(Paths.get(AVA_DIRECTORY, oldLogoFileName));
                            System.out.println("Đã xóa file logo cũ: " + oldLogoFileName);
                        } catch (IOException e) {
                            System.err.println("Không thể xóa file logo cũ: " + e.getMessage());
                            return;
                        }
                    }
                    String newLogoFileName = FileUtils.copyLogoToDirectory(selectedAvaFile, AVA_DIRECTORY, player.getTenCT());
                    selectedPlayer.setAvatar(newLogoFileName);
                    selectedAvaFile = null;
                }
                service.updatePlayer(selectedPlayer);
                loadPlayersData();
                AlertUtils.showInformation("Thành công", "Cập nhật cầu thủ", "Cầu thủ đã được cập nhật thành công.");
            } else {
                String logoFileName = null;

                if (selectedAvaFile != null) {
                    assert player != null;
                    logoFileName = FileUtils.copyLogoToDirectory(selectedAvaFile, AVA_DIRECTORY, player.getTenCT());
                    player.setAvatar(logoFileName);
                } else {
                    assert player != null;
                    player.setAvatar("default_ava.png");
                    AlertUtils.showError("Lỗi", "Lỗi avatar", "Vui lòng chọn ảnh đại diện.");
                    return;
                }
                int id = service.addPlayer(player);
                player.setMaCT(id);
                playersList.add(player);
                idLabel.setText(String.valueOf(id));
                loadPlayersData();
                AlertUtils.showInformation("Thành công", "Thêm cầu thủ", "Cầu thủ đã được thêm thành công.");
                playersTableView.refresh();
            }
        } catch (SQLException e) {
            AlertUtils.showError("Lỗi", "Lỗi cơ sở dữ liệu", "Có lỗi xảy ra khi truy cập cơ sở dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Lỗi cơ sở dữ liệu", "Có lỗi xảy ra khi truy cập cơ sở dữ liệu: " + e.getMessage());

        }
    }

    @FXML
    private void remove() {
        MODEL_CAUTHU selectedPlayer = playersTableView.getSelectionModel().getSelectedItem();
        if (selectedPlayer != null) {
            if (AlertUtils.showConfirmation("Confirmation", "Delete Player", "Are you sure you want to delete this player?")) {
                // Xóa file logo nếu có
                String logoFileName = selectedPlayer.getAvatar();
                if (logoFileName != null && !logoFileName.isEmpty()) {
                    try {
                        Files.deleteIfExists(Paths.get(AVA_DIRECTORY, logoFileName));
                    } catch (IOException e) {
                        System.err.println("Không thể xóa file logo: " + e.getMessage());
                    }
                }
                try {
                    service.removePlayer(selectedPlayer.getMaCT());
                    AlertUtils.showInformation("Thành công", "Xóa cầu thủ", "Cầu thủ đã được xóa thành công.");
                    playersList.remove(selectedPlayer);
                    loadPlayersData();
                    resetForm();
                } catch (Exception e) {
                    AlertUtils.showError("Lỗi", "Không thể xóa cầu thủ này", "Cầu thủ này đang được đăng ký. Nếu muốn xóa, hãy hủy đăng ký trước.");
                }
            }
        } else {
            AlertUtils.showWarning("Lỗi", "Chưa chọn cầu thủ", "Vui lòng chọn một cầu thủ để xóa.");
        }
    }

    @FXML
    public void cancel() {
        resetForm();
    }

    @FXML
    public void handleChooseAva() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Avatar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedAvaFile = fileChooser.showOpenDialog(chooseAvaButton.getScene().getWindow());
        if (selectedAvaFile != null) {
            try {
                Image image = new Image(selectedAvaFile.toURI().toString());
                avaImageView.setImage(image);
            } catch (Exception e) {
                AlertUtils.showError("Error", "Image Error", "An error occurred while loading the image.");
            }
        }
    }

    @FXML
    private void handleAddFromCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(btnAddFromCSV.getScene().getWindow());
        if (file != null) {
            StringBuilder errorMsg = new StringBuilder();
            int addedCount = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String line;
                boolean isFirstLine = true;
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        if (line.toLowerCase().contains("tên") || line.toLowerCase().contains("name")) continue;
                    }
                    String[] cols = line.split(",");
                    if (cols.length < 5) {
                        errorMsg.append("Dòng dữ liệu không đủ cột: ").append(line).append("\n");
                        continue;
                    }
                    try {
                        int soAo = Integer.parseInt(cols[0].trim());
                        if (playersList.stream().anyMatch(p -> p.getSoAo() == soAo && p.getMaCLB() == currentClub.getMaCLB())) {
                            errorMsg.append("Số áo đã tồn tại trong CLB: ").append(soAo).append("\n");
                            continue;
                        }
                        String tenCT = cols[1].trim();
                        java.sql.Date ngaysinh;
                        try {
                            // Try parsing dd/MM/yyyy
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            java.util.Date utilDate = sdf.parse(cols[2].trim());
                            ngaysinh = new java.sql.Date(utilDate.getTime());
                        } catch (Exception ex) {
                            errorMsg.append("Ngày sinh không hợp lệ: ").append(cols[2].trim()).append("\n");
                            continue;
                        }
                        int maVT = service.getPositionIdByName(cols[3].trim());
                        if (maVT == -1) {
                            errorMsg.append("Vị trí không hợp lệ: ").append(cols[3].trim()).append("\n");
                            continue;
                        }
                        String quocTich = cols[4].trim();
                        if (quocTich.isEmpty()) {
                            errorMsg.append("Quốc tịch không hợp lệ\n");
                            continue;
                        }
                        MODEL_CAUTHU player = new MODEL_CAUTHU();
                        player.setSoAo(soAo);
                        player.setTenCT(tenCT);
                        player.setNgaysinh(ngaysinh);
                        player.setMaVT(maVT);
                        player.setQuocTich(quocTich);
                        player.setMaCLB(currentClub.getMaCLB());
                        player.setLoaiCT("Vietnam".equalsIgnoreCase(quocTich) ? 0 : 1);
                        player.setAvatar("default_ava.png");
                        int id = service.addPlayer(player);
                        player.setMaCT(id);
                        playersList.add(player);
                        addedCount++;
                    } catch (Exception ex) {
                        errorMsg.append("Lỗi không xác định ở dòng: ").append(line).append("\n");
                    }
                }
                playersTableView.refresh();
                String resultMsg = "Đã thêm " + addedCount + " cầu thủ từ file CSV!";
                if (errorMsg.length() > 0) {
                    resultMsg += "\nMột số dòng bị bỏ qua:\n" + errorMsg;
                    AlertUtils.showWarning("Kết quả nhập CSV", "Hoàn thành với cảnh báo", resultMsg);
                } else {
                    AlertUtils.showInformation("Thành công", "Đã thêm danh sách cầu thủ từ file CSV!", "");
                }
            } catch (Exception e) {
                AlertUtils.showError("Lỗi", "Không thể đọc file CSV", e.getMessage());
            }
        }
    }

    @FXML
    public void closeBtn() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        try {
            if (preController != null) {
                preController.filterPlayers();
            }
            stage.close();
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Lỗi đóng cửa sổ", "Có lỗi xảy ra khi đóng cửa sổ: " + e.getMessage());
        }
    }
}
