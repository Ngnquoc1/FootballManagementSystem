package Controller;

import Model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Optional;


import Util.ValidationUtils;
import Util.AlertUtils;
import Util.FileUtils;
import Service.Service;
public class ClubManagementController implements Initializable {

    // Các trường thông tin cơ bản CLB
    @FXML private TextField searchClubField;
    @FXML private Button searchButton;
    @FXML private HBox searchResultBox;
    @FXML private ImageView resultClubLogo;
    @FXML private Label resultClubName;
    @FXML private Label resultClubInfo;
    @FXML private Button selectClubButton;

    @FXML private TextField clubNameField;
    @FXML private TextField emailField;
    @FXML private ImageView clubLogoPreview;
    @FXML private Button uploadLogoButton;
    @FXML private Label logoPathLabel;
    @FXML private TextField coachField;
    @FXML private Label clubNameError;
    @FXML private Label emailError;
    @FXML private Label coachError;

    // Các trường thông tin sân vận động
    @FXML private ComboBox<MODEL_SAN> stadiumComboBox;
    @FXML private TextField stadiumIdField;
    @FXML private Button viewStadiumButton;
    @FXML private Button addStadiumButton;
    @FXML private Label stadiumError;

    // Các trường thông tin giải đấu

    // Các trường cam kết và điều khoản
    @FXML private CheckBox termsCheckbox;
    @FXML private Label termsError;

    // Các nút thao tác
    @FXML private Button cancelButton;
    @FXML private Button removeRegistrationBtn;
    @FXML private Button submitButton;

    // Logo giải đấu
    @FXML private ImageView tournamentLogo;

    // Danh sách cầu thủ
    private ObservableList<MODEL_CAUTHUTHAMGIACLB> playersList = FXCollections.observableArrayList();
    private ObservableList<MODEL_CAUTHUTHAMGIACLB> selectedPlayersList = FXCollections.observableArrayList();

    // File logo đã chọn
    private File selectedLogoFile;
    private String logoPath = "";


    // Dữ liệu CLB hiện tại
    private MODEL_CLB currentClub = null;
    private boolean isExistingClub = false;
    private Service service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        service=new Service();
        // Khởi tạo logo giải đấu
        try {
            Image logo = new Image(getClass().getResourceAsStream("/Image/logo.png"));
            tournamentLogo.setImage(logo);
        } catch (Exception e) {
            System.err.println("Không thể tải logo giải đấu: " + e.getMessage());
        }

        // Khởi tạo danh sách sân vận động
        loadStadiums();

        // Thiết lập sự kiện cho combobox và button
        setupListener();
        // Ẩn kết quả tìm kiếm ban đầu
        searchResultBox.setVisible(false);

    }

    private void loadStadiums() {
        try {
            List<MODEL_SAN> stadiums = service.getAllStadiums();
            stadiumComboBox.setItems(FXCollections.observableArrayList(stadiums));

            // Thiết lập cách hiển thị tên sân trong combobox
            stadiumComboBox.setConverter(new StringConverter<MODEL_SAN>() {
                @Override
                public String toString(MODEL_SAN stadium) {
                    return stadium != null ? stadium.getTenSan() : "";
                }

                @Override
                public MODEL_SAN fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể tải danh sách sân vận động", e.getMessage());
        }
    }

    public void setupListener(){
        // Nút chọn CLB từ kết quả tìm kiếm
        selectClubButton.setOnAction(event -> {
            selectFoundClub();
        });
        // Nút hủy
        cancelButton.setOnAction(event -> {
            if (AlertUtils.showConfirmation("Xác nhận", "Bạn có chắc muốn hủy?",
                    "Tất cả thông tin sẽ bị mất nếu bạn hủy.")) {
                closeForm();
            }
        });

        // Listener cho combobox sân vận động
        stadiumComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                stadiumIdField.setText(String.valueOf(newValue.getMaSan()));
            } else {
                stadiumIdField.clear();
            }
        });
    }

    private void selectFoundClub() {
        if (currentClub != null) {
            // Điền thông tin CLB vào form
            clubNameField.setText(currentClub.getTenCLB());
            emailField.setText(currentClub.getEmail());
            coachField.setText(currentClub.getTenHLV());

            // Tải logo CLB
            try {
                Image logo = new Image(getClass().getResourceAsStream("/Image/ClubLogo/"+ currentClub.getLogoCLB()));
                clubLogoPreview.setImage(logo);
                logoPath = currentClub.getLogoCLB();
                logoPathLabel.setText(logoPath);
            } catch (Exception e) {
                System.err.println("Không thể tải logo CLB: " + e.getMessage());
            }

            // Chọn sân vận động
            for (MODEL_SAN stadium : stadiumComboBox.getItems()) {
                if (stadium.getMaSan() == currentClub.getMaSan()) {
                    stadiumComboBox.getSelectionModel().select(stadium);
                    break;
                }
            }

            // Đánh dấu là CLB đã tồn tại
            isExistingClub = true;

            // Ẩn kết quả tìm kiếm
            searchResultBox.setVisible(false);

            AlertUtils.showInformation("Thông báo", "Đã chọn CLB",
                    "Đã tải thông tin CLB " + currentClub.getTenCLB() + ". Vui lòng kiểm tra và cập nhật thông tin nếu cần.");
        }
    }




    @FXML
    private void searchClub() {
        String searchTerm = searchClubField.getText().trim();
        if (searchTerm.isEmpty()) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng nhập tên CLB", "Hãy nhập tên CLB để tìm kiếm.");
            return;
        }

        try {
            MODEL_CLB club = service.findClubByName(searchTerm);
            if (club != null) {
                // Hiển thị kết quả tìm kiếm
                resultClubName.setText(club.getTenCLB());
                resultClubInfo.setText("HLV: " + club.getTenHLV() + " | Email: " + club.getEmail());

                // Tải logo CLB
                try {
                    Image logo = new Image(getClass().getResourceAsStream("/Image/ClubLogo/"+ club.getLogoCLB()));
                    resultClubLogo.setImage(logo);
                } catch (Exception e) {
                    resultClubLogo.setImage(null);
                }

                searchResultBox.setVisible(true);
                currentClub = club;
            } else {
                searchResultBox.setVisible(false);
                AlertUtils.showInformation("Thông báo", "Không tìm thấy CLB",
                        "Không tìm thấy CLB với tên \"" + searchTerm + "\". Vui lòng nhập thông tin CLB mới.");
            }
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể tìm kiếm CLB", e.getMessage());
        }
    }

    @FXML
    private void uploadClubLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn logo CLB");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg")
        );

        selectedLogoFile = fileChooser.showOpenDialog(uploadLogoButton.getScene().getWindow());
        if (selectedLogoFile != null) {
            try {
                Image image = new Image(selectedLogoFile.toURI().toString());
                clubLogoPreview.setImage(image);
                logoPath = selectedLogoFile.getAbsolutePath();
                logoPathLabel.setText(logoPath);
            } catch (Exception e) {
                AlertUtils.showError("Lỗi", "Không thể tải hình ảnh", e.getMessage());
            }
        }
    }

    @FXML
    private void viewStadiumDetails() {
        MODEL_SAN selectedStadium = stadiumComboBox.getSelectionModel().getSelectedItem();
        if (selectedStadium != null) {
            // Hiển thị thông tin chi tiết sân vận động
            // Đây là phần mã giả, bạn cần tạo một dialog riêng để hiển thị thông tin sân
            AlertUtils.showInformation("Thông tin sân vận động", selectedStadium.getTenSan(),
                    "Địa chỉ: " + selectedStadium.getDiaChi() + "\nSức chứa: " + selectedStadium.getSucChua());
        } else {
            AlertUtils.showWarning("Cảnh báo", "Chưa chọn sân vận động", "Vui lòng chọn một sân vận động để xem thông tin chi tiết.");
        }
    }

    @FXML
    private void addNewStadium() {
        // Create a dialog for adding a new stadium
        Dialog<MODEL_SAN> dialog = new Dialog<>();
        dialog.setTitle("Thêm sân vận động mới");
        dialog.setHeaderText("Nhập thông tin sân vận động");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create input fields
        TextField nameField = new TextField();
        nameField.setPromptText("Tên sân vận động");

        TextField addressField = new TextField();
        addressField.setPromptText("Địa chỉ");

        TextField capacityField = new TextField();
        capacityField.setPromptText("Sức chứa");

        // Layout for the dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Tên sân:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Địa chỉ:"), 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(new Label("Sức chứa:"), 0, 2);
        grid.add(capacityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a MODEL_SAN when the Add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    // Validate inputs
                    if (ValidationUtils.isNullOrEmpty(nameField.getText()) ||
                            ValidationUtils.isNullOrEmpty(addressField.getText()) ||
                            !ValidationUtils.isNumeric(capacityField.getText())) {
                        AlertUtils.showWarning("Cảnh báo", "Thông tin không hợp lệ", "Vui lòng nhập đầy đủ và chính xác thông tin.");
                        return null;
                    }

                    // Create a new stadium model
                    MODEL_SAN newStadium = new MODEL_SAN();
                    newStadium.setTenSan(nameField.getText().trim());
                    newStadium.setDiaChi(addressField.getText().trim());
                    newStadium.setSucChua(Integer.parseInt(capacityField.getText().trim()));

                    return newStadium;
                } catch (Exception e) {
                    AlertUtils.showError("Lỗi", "Không thể thêm sân vận động", e.getMessage());
                }
            }
            return null;
        });

        // Show the dialog and handle the result
        Optional<MODEL_SAN> result = dialog.showAndWait();
        result.ifPresent(newStadium -> {
            try {
                // Save the new stadium to the database
                int newStadiumId = service.addStadium(newStadium);
                newStadium.setMaSan(newStadiumId);

                // Refresh the stadium combo box
                loadStadiums();

                AlertUtils.showInformation("Thành công", "Thêm sân vận động thành công",
                        "Sân vận động \"" + newStadium.getTenSan() + "\" đã được thêm.");
            } catch (Exception e) {
                AlertUtils.showError("Lỗi", "Không thể lưu sân vận động", e.getMessage());
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Xóa tất cả thông báo lỗi
        clubNameError.setVisible(false);
        emailError.setVisible(false);
        coachError.setVisible(false);
        stadiumError.setVisible(false);
        termsError.setVisible(false);

        // Kiểm tra tên CLB
        if (ValidationUtils.isNullOrEmpty(clubNameField.getText())) {
            clubNameError.setText("Vui lòng nhập tên CLB");
            clubNameError.setVisible(true);
            isValid = false;
        }

        // Kiểm tra email
        if (ValidationUtils.isNullOrEmpty(emailField.getText())) {
            emailError.setText("Vui lòng nhập địa chỉ email");
            emailError.setVisible(true);
            isValid = false;
        } else if (!ValidationUtils.isValidEmail(emailField.getText())) {
            emailError.setText("Email không hợp lệ");
            emailError.setVisible(true);
            isValid = false;
        }

        // Kiểm tra huấn luyện viên trưởng
        if (ValidationUtils.isNullOrEmpty(coachField.getText())) {
            coachError.setText("Vui lòng nhập tên huấn luyện viên trưởng");
            coachError.setVisible(true);
            isValid = false;
        }

        // Kiểm tra sân vận động
        if (stadiumComboBox.getSelectionModel().getSelectedItem() == null) {
            stadiumError.setText("Vui lòng chọn sân vận động");
            stadiumError.setVisible(true);
            isValid = false;
        }

        // Kiểm tra điều khoản
        if (!termsCheckbox.isSelected()) {
            termsError.setText("Bạn phải đồng ý với điều lệ giải đấu");
            termsError.setVisible(true);
            isValid = false;
        }

        // Kiểm tra logo
        if (ValidationUtils.isNullOrEmpty(logoPath)) {
            AlertUtils.showWarning("Cảnh báo", "Chưa có logo CLB", "Vui lòng tải lên logo CLB.");
            isValid = false;
        }

        return isValid;
    }

    @FXML
    private void saveBtn() {
        if (!validateForm()) {
            AlertUtils.showWarning("Cảnh báo", "Thông tin không hợp lệ", "Vui lòng kiểm tra lại thông tin.");
            return;
        }
        try {
            // Lấy thông tin từ form
            String clubName = clubNameField.getText().trim();
            String email = emailField.getText().trim();
            String coach = coachField.getText().trim();
            MODEL_SAN stadium = stadiumComboBox.getSelectionModel().getSelectedItem();

            // Đường dẫn thư mục lưu logo
            String logoDirectory = System.getProperty("user.dir") + "/src/main/resources/Image/ClubLogo";

            // Tạo hoặc cập nhật thông tin CLB
            MODEL_CLB club;
            if (isExistingClub && currentClub != null) {
                // Cập nhật thông tin CLB hiện có
                club = currentClub;
                club.setTenCLB(clubName);
                club.setEmail(email);
                club.setTenHLV(coach);
                club.setMaSan(stadium.getMaSan());

                // Xóa logo cũ nếu có và lưu logo mới
                if (selectedLogoFile != null) {
                    if (club.getLogoCLB() != null && !club.getLogoCLB().isEmpty()) {
                        File oldLogoFile = new File(logoDirectory, club.getLogoCLB());
                        if (oldLogoFile.exists()) {
                            oldLogoFile.delete();
                        }
                    }
                    String newLogoFileName = FileUtils.copyLogoToDirectory(selectedLogoFile, logoDirectory, clubName);
                    club.setLogoCLB(newLogoFileName);
                }

                // Cập nhật CLB trong CSDL
                service.updateClub(club);
                AlertUtils.showInformation("Thành công", "Cập nhật CLB thành công",
                        "CLB " + clubName + " đã được cập nhật thành công.");
            } else {
                // Tạo CLB mới
                club = new MODEL_CLB();
                club.setTenCLB(clubName);
                club.setEmail(email);
                club.setTenHLV(coach);
                club.setMaSan(stadium.getMaSan());

                // Lưu logo mới
                if (selectedLogoFile != null) {
                    String newLogoFileName = FileUtils.copyLogoToDirectory(selectedLogoFile, logoDirectory, clubName);
                    club.setLogoCLB(newLogoFileName);
                }

                // Thêm CLB mới vào CSDL
                int newClubId = service.addClub(club);
                club.setMaCLB(newClubId);
                AlertUtils.showInformation("Thành công", "Thêm CLB thành công",
                        "CLB " + clubName + " đã được thêm thành công.");


            }
            // Mở cửa sổ đăng ký cầu thủ
            //Option Pane
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Đăng ký cầu thủ");
            alert.setContentText("Bạn có muốn đăng ký cầu thủ cho CLB này không?");
            ButtonType yesButton = new ButtonType("Có");
            ButtonType noButton = new ButtonType("Không");
            alert.getButtonTypes().setAll(yesButton, noButton);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == yesButton) {
                // Mở cửa sổ đăng ký cầu thủ
                openPlayerRegistrationWindow(club);
            } else {
                AlertUtils.showInformation("Thông báo", "Đã thêm CLB",
                        "CLB " + clubName + " đã được thêm thành công.");
            }

            closeForm();
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể Thêm / Cập nhật CLB", e.getMessage());
        }
    }

    @FXML
    private void removeBtn() {
        if (currentClub != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận");
            alert.setHeaderText("Bạn có chắc chắn muốn xóa CLB này không?");
            alert.setContentText("Tất cả thông tin sẽ bị mất nếu bạn xóa.");


            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    service.removeClub(currentClub.getMaCLB());
                    AlertUtils.showInformation("Thông báo", "Đã xóa CLB",
                            "CLB " + currentClub.getTenCLB() + " đã được xóa thành công.");
                } catch (Exception e) {
                    AlertUtils.showError("Lỗi", "Không thể xóa đăng ký CLB", e.getMessage());
                }
            }
        } else {
            AlertUtils.showWarning("Cảnh báo", "Chưa chọn CLB", "Vui lòng chọn một CLB để xóa.");
        }
    }

    private void closeForm() {
        // Đóng form đăng ký
        cancelButton.getScene().getWindow().hide();
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

            // Tạo scene mới
            Scene scene = new Scene(root);

            // Tạo stage mới
            Stage stage = new Stage();
            stage.setTitle("Đăng ký cầu thủ - " + club.getTenCLB() );
            stage.setScene(scene);
            stage.setResizable(false);

            // Hiển thị cửa sổ đăng ký cầu thủ
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Không thể mở cửa sổ đăng ký cầu thủ", e.getMessage());
        }
    }
}
