package Controller;

import Model.MODEL_CAUTHU;
import Model.MODEL_CLB;
import Model.MODEL_VITRITD;
import Service.Service;
import Util.AlertUtils;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
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
    private Label idLabel,clubNameLabel;
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
    private Button chooseAvaButton,closeBtn;
    @FXML
    private ImageView avaImageView,clubImgaeView;

    private MODEL_CLB currentClub;
    private Service service;
    private MODEL_CAUTHU selectedPlayer;

    private ObservableList<MODEL_CAUTHU> playersList = FXCollections.observableArrayList();
    private FilteredList<MODEL_CAUTHU> filteredPlayersList;
    private final String AVA_DIRECTORY = "C:\\\\STUDY\\\\JAVA\\\\DEMO1\\\\src\\\\main\\\\resources\\\\Image\\\\PlayerAva";
    private File selectedAvaFile;
    private Runnable refreshCallback;

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
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
                    if (player.getSoAo() == Integer.parseInt(selectedNumber)) {
                        return true;
                    }
                    return false;
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
            Image clubLogo = new Image(getClass().getResourceAsStream("/Image/ClubLogo/" + currentClub.getLogoCLB()));
            clubImgaeView.setImage(clubLogo);
        }
        catch (Exception e) {
            Image defaultLogo = new Image(getClass().getResourceAsStream("/Image/ClubLogo/default_logo.png"));
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
            if(idLabel.getText()==null || idLabel.getText().isEmpty()){
                player.setMaCT(0);
            }
            else
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
    private void resetFilter()  {
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
    private void update(){
        selectedPlayer = playersTableView.getSelectionModel().getSelectedItem();
        if (selectedPlayer != null) {
            idLabel.setText(String.valueOf(selectedPlayer.getMaCT()));
            playerNameField.setText(selectedPlayer.getTenCT());
            playerDobPicker.setValue(selectedPlayer.getNgaysinh().toLocalDate());
            playerNationalityCombo.setValue(selectedPlayer.getQuocTich());
            playerNumberField.setText(String.valueOf(selectedPlayer.getSoAo()));
            playerPositionCombo.setValue(service.getPositionById(selectedPlayer.getMaVT()));
            Image avaImg;
            try{
                avaImg = new Image(getClass().getResourceAsStream("/Image/PlayerAva/" + selectedPlayer.getAvatar()));
            } catch (Exception e) {
                avaImg = new Image(getClass().getResourceAsStream("/Image/PlayerAva/default_ava.png"));
                AlertUtils.showError("Error", "Image Error", "An error occurred while loading the image.");
            }
            avaImageView.setImage(avaImg);
        } else {
            AlertUtils.showWarning("Warning", "No Player Selected", "Please select a player to update.");
        }
    }
    @FXML
    private void save() {
        try {
            MODEL_CAUTHU player = collectFormData();
            if(selectedPlayer != null){
                selectedPlayer.setMaVT(player.getMaVT());
                selectedPlayer.setTenCT(player.getTenCT());
                selectedPlayer.setNgaysinh(player.getNgaysinh());
                selectedPlayer.setQuocTich(player.getQuocTich());
                selectedPlayer.setSoAo(player.getSoAo());
                selectedPlayer.setLoaiCT(player.getLoaiCT());
                selectedPlayer.setMaCLB(player.getMaCLB());
                if(selectedAvaFile != null) {
                    String oldLogoFileName = player.getAvatar();
                    if (oldLogoFileName!= null && !oldLogoFileName.isEmpty()) {
                        try {
                            Files.deleteIfExists(Paths.get(AVA_DIRECTORY, oldLogoFileName));
                        } catch (IOException e) {
                            System.err.println("Không thể xóa file logo cũ: " + e.getMessage());
                            return;
                        }
                    }
                    String newLogoFileName = saveAvaFile(selectedAvaFile, player.getTenCT());
                    if (newLogoFileName != null) {
                        selectedPlayer.setAvatar(newLogoFileName);
                    }
                }
                service.updatePlayer(selectedPlayer);
                loadPlayersData();
                AlertUtils.showInformation("Success", "Player Updated", "Player has been updated successfully.");
            }
            else  {
                String logoFileName = null;

                if (selectedAvaFile != null) {
                    logoFileName = saveAvaFile(selectedAvaFile, player.getTenCT());
                    if (logoFileName != null) {
                        player.setAvatar(logoFileName);
                    }
                }
                else{
                    player.setAvatar("default_ava.png");
                    AlertUtils.showError("Error", "Avatar Error", "Please choose an avatar.");
                    return;
                }
                int id=service.addPlayer(player);
                player.setMaCT(id);
                playersList.add(player);
                idLabel.setText(String.valueOf(id));
                loadPlayersData();
                AlertUtils.showInformation("Success", "Player Added", "Player has been added successfully.");
                playersTableView.refresh();
            }
        } catch (SQLException e) {
                AlertUtils.showError("Error", "Database Error", "An error occurred while accessing the database."+ e.getMessage());
        } catch (Exception e) {
            AlertUtils.showError("Error", "Database Error 1", "An error occurred while accessing the database."+e.getMessage());

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
                service.removePlayer(selectedPlayer.getMaCT());
                playersList.remove(selectedPlayer);
                loadPlayersData();
                resetForm();
                AlertUtils.showInformation("Success", "Player Deleted", "Player has been deleted successfully.");
            }
        } else {
            AlertUtils.showWarning("Warning", "No Player Selected", "Please select a player to delete.");
        }
    }
    @FXML
    public void cancel(){
        resetForm();
    }
    @FXML
    public void handleChooseAva(){
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
    
    public String saveAvaFile( File avaFile, String playerName) {
        if (avaFile != null) {
            try {
                String fileExtension = getFileExtension(avaFile.getName());
                String uniqueFileName = generateUniqueFileName(playerName) + fileExtension;
                Path targetPath = Paths.get(AVA_DIRECTORY, uniqueFileName);

                Files.copy(avaFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                return uniqueFileName;
            } catch (IOException e) {
                AlertUtils.showError("Error", "File Error", "An error occurred while saving the file.");
                return null;
            }
        }
        return null;
    }
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }
    private String generateUniqueFileName(String playerName) {
        // Remove spaces and special characters from the player's name
        String sanitizedPlayerName = playerName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        // Append a unique identifier (timestamp) to the sanitized name
        return sanitizedPlayerName + "_" + System.currentTimeMillis();
    }
    @FXML
    public void closeBtn(){
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        try {
            if (refreshCallback != null) {
                refreshCallback.run(); // Call the refresh callback
            }
            stage.close();
        } catch (Exception e) {
            AlertUtils.showError("Error", "Close Error", "An error occurred while closing the window.");
        }
    }
}

