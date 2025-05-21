package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MenuController {
    @FXML
    private Button homeMenuBtn, resultMenuBtn, fixtureMenuBtn, tableMenuBtn, clubMenuBtn, playerMenuBtn, leagueMenuBtn;
    @FXML
    private BorderPane mainContent;

    @FXML
    public void initialize() {
        mainContent.setUserData(this);
        clickOnHomeBtn();
    }

    @FXML
    public void clickOnHomeBtn() {
        resetButtonStyle();
        homeMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("HomeFrame");
        mainContent.setCenter(view);
    }

    @FXML
    public void clickOnResultBtn() {
        resetButtonStyle();
        resultMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("ResultFrame");
        mainContent.setCenter(view);
    }

    @FXML
    public void clickOnFixtureBtn() {
        resetButtonStyle();
        fixtureMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("FixtureFrame");
        mainContent.setCenter(view);
    }

    @FXML
    public void clickOnTableBtn() {
        resetButtonStyle();
        tableMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("TableFrame");
        mainContent.setCenter(view);
    }

    @FXML
    public void clickOnClubBtn() {
        resetButtonStyle();
        clubMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("CLBFrame");
        mainContent.setCenter(view);
    }

    @FXML
    public void clickOnPlayerBtn() {
        resetButtonStyle();
        playerMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("PlayerFrame");
        mainContent.setCenter(view);
    }

    @FXML
    public void clickOnLeagueBtn() {
        resetButtonStyle();
        leagueMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("TournamentFrame");
        mainContent.setCenter(view);
    }

    public void resetButtonStyle() {
        homeMenuBtn.getStyleClass().remove("selectedBtn");
        resultMenuBtn.getStyleClass().remove("selectedBtn");
        fixtureMenuBtn.getStyleClass().remove("selectedBtn");
        tableMenuBtn.getStyleClass().remove("selectedBtn");
        clubMenuBtn.getStyleClass().remove("selectedBtn");
        playerMenuBtn.getStyleClass().remove("selectedBtn");
        leagueMenuBtn.getStyleClass().remove("selectedBtn");
    }
}