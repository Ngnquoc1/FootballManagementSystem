package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import DAO.DAO_User;
import Model.MODEL_USER;
import Model.Session;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class UserPopupController {

    @FXML
    private Button logoutButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label roleLabel;
    @FXML
    private Label usernameLabel;

    private DAO_User daoUser = new DAO_User();

    @FXML
    private void initialize() {
        Session session = Session.getInstance();
        String currentUsername = session.getUsername();

        if (currentUsername != null && !currentUsername.isEmpty()) {
            usernameLabel.setText(currentUsername);

            MODEL_USER currentUser = daoUser.getUserByUsername(currentUsername);
            if (currentUser != null) {
                roleLabel.setText(currentUser.getVaiTroText());
            } else {
                roleLabel.setText("Không xác định");
            }
        } else {
            roleLabel.setText("Không xác định");
            usernameLabel.setText("Khách");
        }
    }

    @FXML
    private void handleLogout() {
        System.out.println("Đăng xuất thành công");

        // Xóa thông tin phiên đăng nhập
        Session.getInstance().clear();

        // Đóng popup
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();

        // Chuyển hướng về màn hình đăng nhập
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/loginFrame1.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setScene(scene);

            // Đóng tất cả các cửa sổ khác
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            Stage mainStage = (Stage) currentStage.getOwner();

            if (mainStage != null) {
                mainStage.close();
            }

            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}