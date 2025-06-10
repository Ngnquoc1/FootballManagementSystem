package Controller;

import Model.*;
import Service.Service;

import Util.AlertUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FixtureController implements Initializable {
    @FXML
    private Button resetBtn, addBtn;
    @FXML
    private ScrollPane Calendar;
    @FXML
    private ComboBox<String> compeFilter, clubFilter;
    private Service service = new Service();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureUIBasedOnRole();
        try {
            setFilter();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Map<LocalDate, List<Match>> matchesByDate = null;
        try {
            matchesByDate = service.getUpcomingMatchs();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        createFullMatch(matchesByDate);
    }

    private void configureUIBasedOnRole() {
        Session session = Session.getInstance();
        int userRole = session.getRole();


        if (userRole == 5 || userRole == 4 || userRole == 2 || userRole == 1) {
            if (addBtn != null) {
                addBtn.setVisible(false);
                addBtn.setManaged(false); // Không chiếm không gian trong layout
            }

        }
    }

    private void setFilter() throws SQLException {

        List<MODEL_MUAGIAI> ds1 = service.getAllTournament();
        ArrayList<String> dsMG = new ArrayList<>();
        for (MODEL_MUAGIAI mg : ds1) {
            dsMG.add(mg.getTenMG());
        }
        compeFilter.getItems().addAll(dsMG);
        compeFilter.getSelectionModel().selectFirst();

        List<MODEL_CLB> ds2 = service.getAllClubs();
        ArrayList<String> dsCLB = new ArrayList<>();
        for (MODEL_CLB clb : ds2) {
            dsCLB.add(clb.getTenCLB());
        }
        clubFilter.getItems().addAll(dsCLB);
    }

    private void createFullMatch(Map<LocalDate, List<Match>> matchesByDate) {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(10));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy");

        // Sort dates to ensure chronological order
        Map<LocalDate, List<Match>> finalMatchesByDate = matchesByDate;
        matchesByDate.keySet().stream().sorted().forEach(date -> {
            List<Match> matches = finalMatchesByDate.get(date);

            // Date header with Premier League logo

            HBox dateHeader = createDateHeader(date, dateFormatter);
            mainContent.getChildren().add(dateHeader);

            // Add match rows for this date
            for (Match match : matches) {
                HBox matchRow = createMatchRow(match);
                mainContent.getChildren().add(matchRow);
            }
        });
        Calendar.setContent(mainContent);
        Calendar.setFitToWidth(true);
    }

    private HBox createDateHeader(LocalDate date, DateTimeFormatter formatter) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label dateLabel = new Label(date.format(formatter));
        dateLabel.getStyleClass().add("date-header");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label leagueLabel = new Label("V-League 2025 ");
        leagueLabel.getStyleClass().add("league-label");

        ImageView leagueLogo = new ImageView(new Image(getClass().getResourceAsStream("/icons/logo.png")));
        leagueLogo.setFitHeight(30);
        leagueLogo.setFitWidth(30);

        header.getChildren().addAll(dateLabel, spacer, leagueLabel, leagueLogo);
        return header;
    }

    private HBox createMatchRow(Match match) {
        HBox row = new HBox(0);
        row.getStyleClass().add("match-row");
 
        // Home team with logo
        Image logo1 = new Image(String.valueOf(getClass().getResource("/Image/ClubLogo/" + match.getLogoCLB1())));
        ImageView homeTeamLogo = new ImageView(logo1);
        homeTeamLogo.getStyleClass().add("team-logo");

        // Home team label
        Label homeTeamLabel = new Label(match.getTenCLB1());
        homeTeamLabel.setAlignment(Pos.CENTER_RIGHT);
        homeTeamLabel.getStyleClass().add("team-label");

        HBox homeGroup = new HBox(5, homeTeamLabel, homeTeamLogo);
        homeGroup.getStyleClass().add("team-group");

        // Kick-off time
        Label timeLabel = new Label(match.getFormattedGioThiDau());
        timeLabel.getStyleClass().add("time-label");

        Label roundLabel = new Label(match.getTenVongDau());
        roundLabel.getStyleClass().add("round-label");

        // Away team with logo
        Image logo2 = new Image(String.valueOf(getClass().getResource("/Image/ClubLogo/" + match.getLogoCLB2())));
        ImageView awayTeamLogo = new ImageView(logo2);
        awayTeamLogo.getStyleClass().add("team-logo");

        // Away team label
        Label awayTeamLabel = new Label(match.getTenCLB2());
        awayTeamLabel.setAlignment(Pos.CENTER_LEFT);
        awayTeamLabel.getStyleClass().add("team-label");

        HBox awayGroup = new HBox(5, awayTeamLogo, awayTeamLabel);
        awayGroup.getStyleClass().add("team-group");

        // Stadium info
        ImageView stadiumIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/stadium.png")));
        stadiumIcon.getStyleClass().add("stadium-icon");

        Label venueLabel = new Label(match.getSanThiDau());
        venueLabel.setStyle("-fx-text-fill: #991f18;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Quick view button
        Label quickViewLabel = new Label("Quick View");
        quickViewLabel.getStyleClass().add("quick-view-label");

        ImageView arrowIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/right-arrow-line.png")));
        arrowIcon.getStyleClass().add("arrow-icon");

        HBox info = new HBox(5);
        info.setAlignment(Pos.CENTER_LEFT);
        info.getChildren().addAll(homeGroup, timeLabel, awayGroup);

        HBox venueBox = new HBox(5);
        venueBox.setAlignment(Pos.CENTER_LEFT);
        venueBox.getChildren().addAll(stadiumIcon, venueLabel);

        HBox quickViewBox = new HBox(5);
        quickViewBox.setAlignment(Pos.CENTER_RIGHT);
        quickViewBox.getChildren().addAll(quickViewLabel, arrowIcon);

        row.getChildren().addAll(
                info, venueBox, spacer, quickViewBox);

        row.setOnMouseClicked(event -> openMatchResult(match));

        return row;
    }

    @FXML
    public void filterByCLB() throws SQLException {
        String selectedCLB = clubFilter.getSelectionModel().getSelectedItem();
        Map<LocalDate, List<Match>> matchesByDate = service.getUpcomingMatchs();

        Map<LocalDate, List<Match>> filteredMatches = new HashMap<>();

        for (Map.Entry<LocalDate, List<Match>> entry : matchesByDate.entrySet()) {
            List<Match> filteredList = entry.getValue().stream()
                    .filter(match -> match.getTenCLB1().equals(selectedCLB) || match.getTenCLB2().equals(selectedCLB))
                    .toList();
            if (!filteredList.isEmpty()) {
                filteredMatches.put(entry.getKey(), filteredList);
            }
        }
        createFullMatch(filteredMatches);
    }

    @FXML
    public void resetFilter() throws SQLException {
        Map<LocalDate, List<Match>> matchesByDate = service.getUpcomingMatchs();
        clubFilter.getSelectionModel().clearSelection();
        compeFilter.getSelectionModel().selectFirst();
        createFullMatch(matchesByDate);
    }

    @FXML
    private void controlFixture() throws SQLException {
        try {
            // Tải file FXML của cửa sổ mới
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FixtureManagementFrame.fxml"));
            Parent root = loader.load();
            FixtureManagementController fixtureManagementController = loader.getController();
            fixtureManagementController.setFixtureController(this);
            // Tạo một Stage mới
            Stage stage = new Stage();
            stage.setTitle("Add Match");
            stage.setScene(new Scene(root));
            stage.initOwner(addBtn.getScene().getWindow()); // Đặt chủ sở hữu là cửa sổ hiện tại
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(event -> {
                // Khi cửa sổ đóng, gọi lại phương thức resetFilter() để làm mới dữ liệu
                try {
                    this.resetFilter();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMatchResult(Match match) {
        // Create a new stage for the popup with blurred background effect
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block input to other windows
        popupStage.initStyle(StageStyle.TRANSPARENT); // Transparent stage for custom shape

        try {
            // Take a snapshot of the current scene to use as background
            Scene currentScene = Calendar.getScene();

            // Create the root container for our popup
            StackPane overlayRoot = new StackPane();

            // Create a semi-transparent rectangle to darken the background
            Rectangle overlay = new Rectangle(currentScene.getWidth(), currentScene.getHeight() + 40);
            overlay.setFill(Color.rgb(50, 50, 50, 0.7));
            // Create the popup content
            BorderPane popupContent = createMatchPopupContent(match);
            popupContent.setMaxWidth(700);
            popupContent.setMaxHeight(500);
            popupContent.setStyle(
                    "-fx-background-color: white; -fx-border-color: #3a0ca3; -fx-border-width: 1px; -fx-border-radius: 5px;");

            // Position the popup in the center
            StackPane.setAlignment(popupContent, Pos.CENTER);

            // Create the X button for closing
            Button closeButton = new Button("X");
            closeButton.getStyleClass().add("close-button");
            closeButton.setOnAction(e -> popupStage.close());

            // Create a container for the X button positioned at top-right
            AnchorPane closeButtonContainer = new AnchorPane();
            closeButtonContainer.getChildren().add(closeButton);
            AnchorPane.setTopAnchor(closeButton, 10.0);
            AnchorPane.setRightAnchor(closeButton, 10.0);

            // Add all layers to the overlay
            overlayRoot.getChildren().addAll(overlay, closeButtonContainer, popupContent);
            overlayRoot.setStyle("-fx-background-color: transparent;");

            // Create scene with our overlay root
            Scene popupScene = new Scene(overlayRoot);
            popupScene.setFill(Color.TRANSPARENT); // Make scene background transparent
            popupScene.getStylesheets().add(getClass().getResource("/css/popup.css").toExternalForm());

            // Allow clicking outside the popup content to close the popup
            overlayRoot.setOnMouseClicked(event -> {
                // Check if click is outside the popup content
                if (!isClickInsideNode(event, popupContent)) {
                    popupStage.close();
                }
            });

            // Prevent clicks on popup content from closing the popup
            popupContent.setOnMouseClicked(Event::consume);

            // Set scene to stage and show
            popupStage.setScene(popupScene);
            popupStage.setWidth(currentScene.getWidth());
            popupStage.setHeight(currentScene.getHeight());
            // Lấy Stage gốc (giả sử currentScene là scene hiện tại)
            Window parentWindow = currentScene.getWindow();

            // Tính vị trí giữa dựa trên kích thước và vị trí Stage gốc
            double centerX = parentWindow.getX() + (parentWindow.getWidth() - popupStage.getWidth()) / 2;
            double centerY = parentWindow.getY() + (parentWindow.getHeight() - popupStage.getHeight()) / 2 + 12;

            // Đặt vị trí popupStage
            popupStage.setX(centerX);
            popupStage.setY(centerY);

            popupStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to regular popup if there's an error
            openRegularPopup(match);
        }
    }

    // Helper method to check if a mouse event is inside a node
    private boolean isClickInsideNode(MouseEvent event, Node node) {
        Point2D point = new Point2D(event.getX(), event.getY());
        Point2D nodePoint = node.sceneToLocal(point);
        return node.contains(nodePoint);
    }

    // The fallback to regular popup if something goes wrong
    private void openRegularPopup(Match match) {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block input to other windows
        popupStage.initStyle(StageStyle.UNDECORATED); // Remove window decorations for a cleaner look

        // Create the popup content
        BorderPane popupRoot = createMatchPopupContent(match);

        // Create and set scene
        Scene popupScene = new Scene(popupRoot);
        popupScene.getStylesheets().add(getClass().getResource("/css/popup.css").toExternalForm());

        // Add custom styles for the popup
        popupRoot.setStyle("-fx-background-color: white; -fx-border-color: #3a0ca3; -fx-border-width: 1px;");

        popupStage.setScene(popupScene);
        popupStage.setWidth(700);
        popupStage.setHeight(500);

        // Center the popup on the parent window
        popupStage.centerOnScreen();

        // Show the popup
        popupStage.show();
    }

    // private final TeamPlayerService service = new TeamPlayerService();
    private static final String BOLD_BLUE_STYLE = "-fx-font-weight: bold; -fx-text-fill: #3a0ca3;";
    private static final String WHITE_BOLD_STYLE = "-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;";
    private static final String TEAM_BOX_STYLE = "-fx-background-color: #DD3333;";
    private static final String HEADER_STYLE = "-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: none;";

    private BorderPane createMatchPopupContent(Match match) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // TOP - Header with team logos and score
        VBox header = createMatchHeader(match);
        root.setTop(header);

        // CENTER - Match details
        VBox centerContent = createMatchDetails(match);
        centerContent.setPrefWidth(150);
        root.setCenter(centerContent);

        // BOTTOM - Action buttons
        HBox buttons = createActionButtons(match);
        root.setBottom(buttons);

        return root;
    }

    private VBox createMatchDetails(Match match) {
        VBox detailsBox = new VBox(20);
        detailsBox.setPadding(new Insets(20, 10, 20, 10));
        detailsBox.setAlignment(Pos.TOP_CENTER);

        // Competition info
        HBox competitionInfo = new HBox(10);
        competitionInfo.setAlignment(Pos.CENTER);
        Label competitionLabel = new Label("V-League");
        competitionLabel.getStyleClass().add("team-label");
        ImageView leagueLogo = new ImageView(new Image(getClass().getResourceAsStream("/Image/logo.png")));
        leagueLogo.setFitHeight(40);
        leagueLogo.setFitWidth(40);
        competitionInfo.getChildren().addAll(competitionLabel, leagueLogo);

        // Date and venue info
        VBox dateVenueBox = new VBox(10);
        dateVenueBox.setAlignment(Pos.CENTER);

        HBox dateBox = new HBox(10);
        dateBox.setAlignment(Pos.CENTER);
        ImageView calendarIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/calendar.png")));
        calendarIcon.getStyleClass().add("stadium-icon");

        // Format the date as shown in the image
        String dateStr = match.getNgayThiDau().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy"));
        Label dateLabel = new Label(dateStr);
        dateLabel.setStyle("-fx-text-fill: #991f18; -fx-font-size: 16px;");
        dateBox.getChildren().addAll(calendarIcon, dateLabel);

        HBox venueBox = new HBox(10);
        venueBox.setAlignment(Pos.CENTER);
        ImageView stadiumIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/stadium.png")));
        stadiumIcon.getStyleClass().add("stadium-icon");
        Label venueLabel = new Label(match.getSanThiDau());
        venueLabel.setStyle("-fx-text-fill: #991f18; -fx-font-size: 16px;");
        venueBox.getChildren().addAll(stadiumIcon, venueLabel);

        dateVenueBox.getChildren().addAll(dateBox, venueBox);

        // Team standings info (using data from your standings layout)
        GridPane standingsGrid = new GridPane();
        standingsGrid.setHgap(15);
        standingsGrid.setVgap(5);
        standingsGrid.setAlignment(Pos.CENTER);
        standingsGrid.setPadding(new Insets(15));
        standingsGrid.setStyle("-fx-background-color: #F7CFD8; -fx-background-radius: 5px;");

        // Headers
        Label teamHeader = new Label("Team");
        teamHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #991f18;");
        Label posHeader = new Label("Pos");
        posHeader.setStyle("-fx-font-weight: bold;-fx-font-size: 16px; -fx-text-fill: #991f18;");
        Label pointsHeader = new Label("Pts");
        pointsHeader.setStyle("-fx-font-weight: bold;-fx-font-size: 16px; -fx-text-fill: #991f18;");

        standingsGrid.add(teamHeader, 0, 0);
        standingsGrid.add(posHeader, 1, 0);
        standingsGrid.add(pointsHeader, 2, 0);

        // Home team standing (example data)
        ImageView homeTeamSmallLogo = new ImageView(
                new Image(getClass().getResourceAsStream("/Image/CLubLogo/" + match.getLogoCLB1())));
        homeTeamSmallLogo.setFitHeight(30);
        homeTeamSmallLogo.setFitWidth(30);
        Label homeTeamStanding = new Label(match.getTenCLB1());
        homeTeamStanding.setStyle("-fx-font-size: 15px;-fx-text-fill: #991f18;");
        HBox homeTeamStandingBox = new HBox(5, homeTeamSmallLogo, homeTeamStanding);
        homeTeamStandingBox.setAlignment(Pos.CENTER_LEFT);

        MODEL_BXH_CLB homeTeam = service.getBxhCLBByCLBName(match.getTenCLB1());
        Label homeTeamPos = new Label(homeTeam.getHang() + ""); // Get from standings data
        homeTeamPos.setStyle("-fx-font-size: 15px;-fx-text-fill: #991f18;");
        Label homeTeamPts = new Label(homeTeam.getDiem() + ""); // Get from standings data
        homeTeamPts.setStyle("-fx-font-size: 15px;-fx-text-fill: #991f18;");

        standingsGrid.add(homeTeamStandingBox, 0, 1);
        standingsGrid.add(homeTeamPos, 1, 1);
        standingsGrid.add(homeTeamPts, 2, 1);

        // Away team standing (example data)
        ImageView awayTeamSmallLogo = new ImageView(
                new Image(getClass().getResourceAsStream("/Image/CLubLogo/" + match.getLogoCLB2())));
        awayTeamSmallLogo.setFitHeight(30);
        awayTeamSmallLogo.setFitWidth(30);
        Label awayTeamStanding = new Label(match.getTenCLB2());
        awayTeamStanding.setStyle("-fx-font-size: 15px;-fx-text-fill: #991f18;");
        HBox awayTeamStandingBox = new HBox(5, awayTeamSmallLogo, awayTeamStanding);
        awayTeamStandingBox.setAlignment(Pos.CENTER_LEFT);

        MODEL_BXH_CLB awayTeam = service.getBxhCLBByCLBName(match.getTenCLB2());
        Label awayTeamPos = new Label(awayTeam.getHang() + ""); // Get from standings data
        awayTeamPos.setStyle("-fx-font-size: 15px;-fx-text-fill: #991f18;");
        Label awayTeamPts = new Label(awayTeam.getDiem() + ""); // Get from standings data
        awayTeamPts.setStyle("-fx-font-size: 15px;-fx-text-fill: #991f18;");

        standingsGrid.add(awayTeamStandingBox, 0, 2);
        standingsGrid.add(awayTeamPos, 1, 2);
        standingsGrid.add(awayTeamPts, 2, 2);

        // Add to details box
        detailsBox.getChildren().addAll(competitionInfo, dateVenueBox, standingsGrid);

        return detailsBox;
    }

    private HBox createActionButtons(Match match) {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5, 0, 5, 0));
        buttonBox.getStyleClass().add("button_popup");

        // Stadium Info button
        HBox containerButton = new HBox(15);
        Button stadiumButton = new Button("Stadium Information");
        stadiumButton.setStyle(
                "-fx-border-width: none;-fx-background-color: white;-fx-text-fill: #991f18; -fx-font-size: 16px;");
        stadiumButton.setPrefWidth(180);
        stadiumButton.setOnAction(e -> openStadiumInfo(match));

        ImageView arrowLogo = createImageView("/icons/right-arrow-line.png", 20, 20);
        arrowLogo.setStyle(" -fx-effect: innershadow(gaussian, #FF9D23, 100, 0.8, 0, 0);");
        containerButton.getChildren().addAll(stadiumButton, arrowLogo);
        containerButton.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        containerButton.setAlignment(Pos.CENTER);
        containerButton.setPadding(new Insets(2, 2, 2, 2));

        buttonBox.getChildren().addAll(containerButton);
        return buttonBox;
    }

    private VBox createMatchHeader(Match match) {

        // Teams and score display
        HBox teamsDisplay = createTeamsDisplay(match);

        // Create the header container
        VBox heading = new VBox(10);
        heading.setAlignment(Pos.CENTER);
        heading.setPadding(new Insets(15, 20, 30, 20));
        heading.setStyle(HEADER_STYLE);
        heading.getChildren().addAll(teamsDisplay);

        return heading;
    }

    private HBox createTeamsDisplay(Match match) {
        // Home team box
        VBox homeDisplay = new VBox();
        HBox homeTeamBox = new HBox(5);
        homeTeamBox.getStyleClass().addAll("hometeam-box",
                match.getLogoCLB1().toLowerCase().replace(".png", "-") + "homebox");

        ImageView homeLogo = createImageView("/Image/ClubLogo/" + match.getLogoCLB1(), 60, 60);

        Label homeNameLabel = new Label(match.getTenCLB1());
        homeNameLabel.setAlignment(Pos.CENTER_LEFT);
        homeNameLabel.setPrefWidth(200);
        homeNameLabel.setStyle(WHITE_BOLD_STYLE);

        homeTeamBox.getChildren().addAll(homeLogo, homeNameLabel);

        Region bottomspacer = new Region();
        VBox.setVgrow(bottomspacer, Priority.ALWAYS);

        homeDisplay.getChildren().addAll(homeTeamBox, bottomspacer);

        // Result display
        VBox timeDisplay = new VBox();
        Label timeLabel = new Label(match.getFormattedGioThiDau());
        timeLabel.setAlignment(Pos.CENTER);
        timeLabel.setMinHeight(80);
        timeLabel.setMinWidth(120);
        timeLabel.setStyle(
                "-fx-font-size: 45px; -fx-font-weight: bold; -fx-text-fill: white;-fx-background-color: #3a0ca3; -fx-border-color:none;-fx-background-radius: 0 0 10px 10px;");
        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);
        timeDisplay.getChildren().addAll(timeLabel, bottomSpacer);
        // Away team box
        VBox awayDisplay = new VBox();
        HBox awayTeamBox = new HBox(5);
        awayTeamBox.getStyleClass().addAll("awayteam-box",
                match.getLogoCLB2().toLowerCase().replace(".png", "-") + "awaybox");

        ImageView awayLogo = createImageView("/Image/ClubLogo/" + match.getLogoCLB2(), 60, 60);

        Label awayNameLabel = new Label(match.getTenCLB2());
        awayNameLabel.setAlignment(Pos.CENTER_RIGHT);
        awayNameLabel.setPrefWidth(200);
        awayNameLabel.setStyle(WHITE_BOLD_STYLE);

        awayTeamBox.getChildren().addAll(awayNameLabel, awayLogo);

        awayDisplay.getChildren().addAll(awayTeamBox, bottomspacer);

        // Container for all elements
        HBox container = new HBox(5);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(homeDisplay, timeDisplay, awayDisplay);

        return container;
    }


    private ImageView createImageView(String resourcePath, double height, double width) {
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(resourcePath)));
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        return imageView;
    }

    // Helper methods for button actions
    private void openStadiumInfo(Match match) {
        // Create another popup or navigate to stadium info page
        MODEL_CLB club = new MODEL_CLB();
        try {
            MODEL_SAN san = service.getStadiumByName(match.getSanThiDau());
            club = service.getCLBByMaSan(san.getMaSan());
        } catch (Exception e) {
            e.printStackTrace();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ClubsDetail.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            ClubsDetailController controller = loader.getController();
            MODEL_SAN stadium = service.getStadiumById(club.getMaSan());
            controller.setData(club, stadium);
            Stage stage = new Stage();
            stage.setTitle("Club Details - " + club.getTenCLB());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Error", "Unable to load club details",
                    "An error occurred while trying to display the club details.");

        }
    }
    @FXML ImageView userIcon;
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

}
