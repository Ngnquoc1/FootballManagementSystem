package Controller;

import Service.Service;
import Model.MODEL_USER;
import Model.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;



public class LoginFormController {

    @FXML
    private StackPane mainContainer;
    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signInButton;
    @FXML
    private Hyperlink guestLink;
    @FXML
    private ImageView logoImageView;
    @FXML
    private HBox userNameBox;
    @FXML
    private HBox passwordBox;
    private Service service= new Service();
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void switchScene(ActionEvent event, String view) throws IOException {
        // Logic to switch to MyTest.fxml
        root = FXMLLoader.load(getClass().getResource("/View/"+view+".fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void initialize() {
        // Load background image
        try {
            InputStream bgStream = getClass().getResourceAsStream("/Image/loginBG.jpg");
            BackgroundImage backgroundImage = new BackgroundImage(
                    new Image(bgStream),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );
            mainContainer.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            // Fallback to a gradient background if image not found
            mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #1e5799, #207cca 51%, #2989d8 100%);");
            System.out.println("Background image not found. Using fallback color.");
        }

        // Try to add icon to username field
        try {
            InputStream userIconStream = getClass().getResourceAsStream("/icons/person.png");
            assert userIconStream != null;
            ImageView userIcon = new ImageView(new Image(userIconStream));
            userIcon.setFitHeight(20);
            userIcon.setFitWidth(20);
            userNameBox.getChildren().add(0, userIcon); // Add at the beginning
        } catch (Exception e) {
            System.out.println("User icon not found.");
        }

        // Try to add icon to password field
        try {
            InputStream lockIconStream = getClass().getResourceAsStream("/icons/lock.png");
            assert lockIconStream != null;
            ImageView lockIcon = new ImageView(new Image(lockIconStream));
            lockIcon.setFitHeight(20);
            lockIcon.setFitWidth(20);
            passwordBox.getChildren().add(0, lockIcon); // Add at the beginning
        } catch (Exception e) {
            System.out.println("Lock icon not found.");
        }
    }

    @FXML
    private void handleSignIn(ActionEvent event) throws IOException {
        String username = userNameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both username and password");
            alert.showAndWait();
        } else {
            MODEL_USER user = new MODEL_USER();
            user = service.Login(username, password);
            if (user != null) {
                Session session = Session.getInstance();
                session.setUsername(user.getUserName());
                session.setRole(user.getVaiTro());
                if ("A".equals(user.getVaiTro())) {
                    switchScene(event, "MenuFrame");
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Invalid username or password!");
                alert.showAndWait();
            }
        }
        // Here you would add your authentication logic
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Information");
        alert.setHeaderText(null);
        alert.setContentText("Login attempt with username: " + username);
        alert.showAndWait();
    }
    @FXML
    private void handleGuestLogin() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guest Login");
        alert.setHeaderText(null);
        alert.setContentText("Logging in as guest...");
        alert.showAndWait();
    }
}


