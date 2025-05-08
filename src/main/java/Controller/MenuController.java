package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MenuController {
    @FXML
    private Button resultMenuBtn,fixtureMenuBtn;
    @FXML
    private BorderPane mainContent;


    @FXML
    public void clickOnResultBtn() {
        resetButtonStyle();
        resultMenuBtn.getStyleClass().add("selected-button");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("ResultFrame");
        mainContent.setCenter(view);

    }
    @FXML
    public void clickOnFixtureBtn() {
        resetButtonStyle();
        fixtureMenuBtn.getStyleClass().add("selected-button");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("FixtureFrame");
        mainContent.setCenter(view);
    }

    public void resetButtonStyle() {
        resultMenuBtn.getStyleClass().remove("selected-button");
        fixtureMenuBtn.getStyleClass().remove("selected-button");
    }
}
