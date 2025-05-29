package Controller;

import Model.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MenuController {
    @FXML
    private Button resultMenuBtn,fixtureMenuBtn,tableMenuBtn,clubMenuBtn,playerMenuBtn,leagueMenuBtn, rulesMenuBtn, registryMenuBtn,homeMenuBtn;
    @FXML
    private BorderPane mainContent;

    @FXML
    public void initialize() {
        mainContent.setUserData(this);

        configureUIBasedOnRole();

        clickOnHomeBtn();
    }

    private void configureUIBasedOnRole() {
        Session session = Session.getInstance();
        String userRole = session.getRole();

        // Nếu role là "A", ẩn Registry và Rules buttons
        if ("A".equals(userRole)) {
            if (registryMenuBtn != null) {
                registryMenuBtn.setVisible(false);
                registryMenuBtn.setManaged(false); // Không chiếm không gian trong layout
            }
            if (rulesMenuBtn != null) {
                rulesMenuBtn.setVisible(false);
                rulesMenuBtn.setManaged(false); // Không chiếm không gian trong layout
            }
            if(leagueMenuBtn != null) {
                leagueMenuBtn.setVisible(false);
                leagueMenuBtn.setManaged(false);
            }
        }
    }

    @FXML
    public void clickOnHomeBtn() {
        homeMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("HomeFrame");
        mainContent.setCenter(view);
    }

    @FXML
    public void clickOnResultBtn() {
        resultMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("ResultFrame");
        mainContent.setCenter(view);
    }

    @FXML
    public void clickOnFixtureBtn() {
        fixtureMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("FixtureFrame");
        mainContent.setCenter(view);
    }

    @FXML
    public void clickOnTableBtn() {
        tableMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("TableFrame");
        mainContent.setCenter(view);
    }

    @FXML
    public void clickOnClubBtn(){
        clubMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("ClubFrame");
        mainContent.setCenter(view);
    }
    @FXML void clickOnPlayerBtn(){
        playerMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("PlayerFrame");
        mainContent.setCenter(view);
    }
    @FXML
    public void clickOnLeagueBtn(){
        leagueMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("TournamentFrame");
        mainContent.setCenter(view);
    }
    @FXML
    public void clickOnRulesBtn(){
        leagueMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("RulesManagementFrame");
        mainContent.setCenter(view);
    }
    @FXML
    public void clickOnRegistryBtn(){
        leagueMenuBtn.getStyleClass().add("selectedBtn");
        FxmlLoader fxmlLoader = new FxmlLoader();
        VBox view = fxmlLoader.getPane("RegistrationFrame");
        mainContent.setCenter(view);
    }

}