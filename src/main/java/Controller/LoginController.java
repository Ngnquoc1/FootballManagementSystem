package Controller;

import Controller.DAO.DAO_User;
import Model.MODEL_USER;
import Model.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private FXMLLoader loader;
    @FXML
    private TextField userText;
    @FXML
    private PasswordField passText;

    @FXML
    public void switchScene(ActionEvent event, String view) throws IOException {
        // Logic to switch to MyTest.fxml
        root = FXMLLoader.load(getClass().getResource("/View/"+view+".fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void login(ActionEvent event) throws IOException {
        MODEL_USER user = new MODEL_USER();
        String username = userText.getText();
        String password = passText.getText();
        user = DAO_User.Login(username, password);
        if (user != null) {
            Session session = Session.getInstance();
            session.setUsername(user.getUserName());
            session.setRole(user.getVaiTro());
            if ("A".equals(user.getVaiTro())) {
                switchScene(event,"FixtureFrame");
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid username or password!");
            alert.showAndWait();
        }
    }
    void loginAsGuest(ActionEvent event) throws IOException {
        switchScene(event,"GuestFrame");
    }
}
