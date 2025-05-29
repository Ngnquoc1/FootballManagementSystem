package Controller;



import Controller.Connection.DatabaseConnection;
import Model.*;
import Service.Service;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import oracle.jdbc.OracleTypes;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ResultsController implements Initializable {
    @FXML
    private Button resetBtn,addBtn;
    @FXML
    private ScrollPane Calendar;
    @FXML
    private ComboBox<String> compeFilter, clubFilter;
    @FXML
    private ImageView userIcon;
    private Service service = new Service();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureUIBasedOnRole();
        Map<LocalDate, List<Match>> matchesByDate = null;
        try {
            matchesByDate = service.getResultedMatchs();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        createFullMatch(matchesByDate);
        try {
            setFilter();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void configureUIBasedOnRole() {
        Session session = Session.getInstance();
        int userRole = session.getRole();

        // Nếu role là "A", ẩn Registry và Rules buttons
        if (userRole == 5 || userRole == 3 || userRole == 2 || userRole == 1) {
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

            //Add match rows for this date
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

        //Home team label
        Label homeTeamLabel = new Label(match.getTenCLB1());
        homeTeamLabel.setAlignment(Pos.CENTER_RIGHT);
        homeTeamLabel.getStyleClass().add("team-label");

        HBox homeGroup = new HBox(5, homeTeamLabel, homeTeamLogo);
        homeGroup.getStyleClass().add("team-group");


        // SCore
        String score1 = String.valueOf(match.getScoreCLB1());
        String score2 = String.valueOf(match.getScoreCLB2());
        Label scoreLabel =new Label(score1 + " - " + score2);
        scoreLabel.getStyleClass().add("time-label");

        Label roundLabel=new Label(match.getTenVongDau());
        roundLabel.getStyleClass().add("round-label");

        // Away team with logo
        Image logo2 = new Image(String.valueOf(getClass().getResource("/Image/ClubLogo/" + match.getLogoCLB2())));
        ImageView awayTeamLogo = new ImageView(logo2);
        awayTeamLogo.getStyleClass().add("team-logo");

        //Away team label
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
        info.getChildren().addAll(homeGroup, scoreLabel, awayGroup);

        HBox venueBox = new HBox(5);
        venueBox.setAlignment(Pos.CENTER_LEFT);
        venueBox.getChildren().addAll(stadiumIcon, venueLabel);

        HBox quickViewBox = new HBox(5);
        quickViewBox.setAlignment(Pos.CENTER_RIGHT);
        quickViewBox.getChildren().addAll(quickViewLabel, arrowIcon);

        row.getChildren().addAll(
                info, venueBox, spacer, quickViewBox
        );

        row.setOnMouseClicked(event -> openMatchResult(match));
        return row;
    }
    @FXML
    public void filterByCLB() throws SQLException {
        String selectedCLB = clubFilter.getSelectionModel().getSelectedItem();
        Map<LocalDate,List<Match>> matchesByDate = service.getResultedMatchs();

        Map<LocalDate,List<Match>> filteredMatches = new HashMap<>();

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
    private void resetFilter() throws SQLException {
        Map<LocalDate, List<Match>> matchesByDate = service.getResultedMatchs();
        clubFilter.getSelectionModel().clearSelection();
        compeFilter.getSelectionModel().selectFirst();
        createFullMatch(matchesByDate);

    }
    @FXML
    public void controlResult(){
        try {
            // Tải file FXML của cửa sổ mới
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ResultManagementFrame.fxml"));
            Parent root = loader.load();

            // Tạo một Stage mới
            Stage stage = new Stage();
            stage.setTitle("Result Management");
            stage.setScene(new Scene(root));
            stage.initOwner(addBtn.getScene().getWindow()); // Đặt chủ sở hữu là cửa sổ hiện tại
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(event -> {
                // Khi cửa sổ đóng, gọi lại phương thức resetFilter() để làm mới dữ liệu
                try {
                    resetFilter();
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
            Rectangle overlay = new Rectangle(currentScene.getWidth(), currentScene.getHeight()+40);
            overlay.setFill(Color.rgb(50, 50, 50, 0.7));
            // Create the popup content
            BorderPane popupContent = createMatchPopupContent(match);
            popupContent.setMaxWidth(700);
            popupContent.setMaxHeight(500);
            popupContent.setStyle("-fx-background-color: white; -fx-border-color: #991f18; -fx-border-width: 1px; -fx-border-radius: 5px;");

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
//       Lấy Stage gốc (giả sử currentScene là scene hiện tại)
            Window parentWindow = currentScene.getWindow();

            // Tính vị trí giữa dựa trên kích thước và vị trí Stage gốc
            double centerX = parentWindow.getX() + (parentWindow.getWidth() - popupStage.getWidth()) / 2;
            double centerY = parentWindow.getY() + (parentWindow.getHeight() - popupStage.getHeight()) / 2+ 12;

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
        popupRoot.setStyle("-fx-background-color: white; -fx-border-color: #991f18; -fx-border-width: 1px;");

        popupStage.setScene(popupScene);
        popupStage.setWidth(700);
        popupStage.setHeight(500);

        // Center the popup on the parent window
        popupStage.centerOnScreen();

        // Show the popup
        popupStage.show();
    }
    //    private final TeamPlayerService service = new TeamPlayerService();
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


        // BOTTOM - Action buttons
        HBox buttons = createActionButtons(match);
        root.setBottom(buttons);

        return root;
    }


    private HBox createActionButtons(Match match) {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5, 0, 5, 0));
        buttonBox.getStyleClass().add("button_popup");

        // Stadium Info button
        HBox containerButton = new HBox(15);
        Button stadiumButton = new Button("Stadium Information");
        stadiumButton.setStyle("-fx-border-width: none;-fx-background-color: white;-fx-text-fill: #991f18; -fx-font-size: 16px;");
        stadiumButton.setPrefWidth(180);
        stadiumButton.setOnAction(e -> openStadiumInfo(match));

        ImageView arrowLogo = createImageView("/icons/right-arrow-line.png", 20, 20);
        arrowLogo.setStyle(" -fx-effect: innershadow(gaussian, #FF9D23, 100, 0.8, 0, 0);");
        containerButton.getChildren().addAll(stadiumButton,arrowLogo);
        containerButton.setStyle("-fx-background-color: white; -fx-background-radius: 10px;");
        containerButton.setAlignment(Pos.CENTER);
        containerButton.setPadding(new Insets(2, 2, 2, 2));


        buttonBox.getChildren().addAll(containerButton);
        return buttonBox;
    }

    private VBox createMatchHeader(Match match) {

        HBox matchInfo = createMatchInfoRow(match);

        // Teams and score display
        HBox teamsDisplay = createTeamsDisplay(match);

        // Create timeline components
        HBox timelineDisplay = goalsLineDisplay(match);

        // Create the header container
        VBox heading = new VBox(10);
        heading.setAlignment(Pos.CENTER);
        heading.setPadding(new Insets(15, 20, 30, 20));
        heading.setStyle(HEADER_STYLE);
        heading.getChildren().addAll(matchInfo, teamsDisplay, timelineDisplay);

        return heading;
    }
    private HBox goalsLineDisplay(Match match){
        HBox container = new HBox(10);

        HBox homeDisplay = new HBox(5);
        ImageView homeLogo = createImageView("/Image/ClubLogo/" + match.getLogoCLB1(), 30, 30);
        Label homeTeamLabel = new Label(match.getTenCLB1());
        homeTeamLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #991f18; -fx-font-weight: bold;");
        homeDisplay.getChildren().addAll(homeLogo, homeTeamLabel);
        homeDisplay.setAlignment(Pos.CENTER_LEFT);

        Region centerSpacer = new Region();
        VBox.setVgrow(centerSpacer, Priority.ALWAYS);

        HBox awayDisplay = new HBox(5);
        ImageView awayLogo = createImageView("/Image/ClubLogo/" + match.getLogoCLB2(), 30, 30);
        Label awayTeamLabel = new Label(match.getTenCLB2());
        awayTeamLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #991f18; -fx-font-weight: bold;");
        awayDisplay.getChildren().addAll(awayLogo, awayTeamLabel);
        awayDisplay.setAlignment(Pos.CENTER_LEFT);

        VBox display = new VBox(15);
        display.getChildren().addAll(homeDisplay,centerSpacer, awayDisplay);

        HBox timelineComponents = createTimeline(match);
        container.getChildren().addAll(display, timelineComponents);
        return container;
    }

    private HBox createTimeline(Match match) {
        HBox container = new HBox();

        // Create the main timeline
        Pane timelinePane = new Pane();
        timelinePane.setPrefHeight(80);
        timelinePane.setPrefWidth(430);

        // Draw the horizontal timeline
        Line timelineLine = new Line(0, 40, 430, 40);
        timelineLine.setStrokeWidth(2);
        timelineLine.setStroke(Color.rgb(153, 31, 24)); // Changed to match the #991f18 color

        timelinePane.getChildren().add(timelineLine);

        List<GoalPopUpScene> goalsCLB1 = getGoalsOfMatchList(match.getId(), match.getTenCLB1());
        List<GoalPopUpScene> goalsCLB2 = getGoalsOfMatchList(match.getId(), match.getTenCLB2());
        List<GoalPopUpScene> allGoals = new ArrayList<>(goalsCLB1);
        allGoals.addAll(goalsCLB2);

        int max =90;
        for(GoalPopUpScene goal : allGoals ) {
            if (goal.getPhutGhiBan() > 90) max = goal.getPhutGhiBan();
        }
        max+=1;

        for(GoalPopUpScene goal : allGoals ) {
            int minute = goal.getPhutGhiBan();
            String label = (minute <= 45) ? String.valueOf(minute) : String.valueOf(minute + 1);
            double x = calculateXPosition(label, max, 400);
            addTimeMarker(timelinePane, label, x, false);
        }

        // Add time markers
        // Timeline có độ dài là 530, để KO và FT ở vị trí 0 và 530, giá trị tối thiểu (1) đến giá trị tối đa (101) nằm từ 15 đến 515 nên kích thuoc là 500
        // giá trị ối đa bằng ((45+5) -1 + 1) + ((90+4)-45+1) + 1(HT)
        addTimeMarker(timelinePane, "KO", 0, true);
        addTimeMarker(timelinePane, "HT", calculateXPosition("HT", max, 400), true);
        addTimeMarker(timelinePane, "FT", 430, true);

        // Goals
        for(GoalPopUpScene goal : goalsCLB1 ) {
            int minute = goal.getPhutGhiBan();
            String label = (minute <= 45) ? String.valueOf(minute) : String.valueOf(minute + 1);
            double x = calculateXPosition(label, max, 400);
            addMatchEvent(timelinePane, "goal", x, -20);
        }

        for(GoalPopUpScene goal : goalsCLB2 ) {
            int minute = goal.getPhutGhiBan();
            String label = (minute <= 45) ? String.valueOf(minute) : String.valueOf(minute + 1);
            double x = calculateXPosition(label, max, 400);
            addMatchEvent(timelinePane, "goal", x, 40);
        }

        container.getChildren().add(timelinePane);
        return container;
    }
    private int parseTimeToMinute(String timeText) {
        if(timeText.equalsIgnoreCase("HT")) return 45+1;
        if (timeText.contains("+")) {
            String[] parts = timeText.split("\\+");
            try {
                int base = Integer.parseInt(parts[0].trim());
                int added = Integer.parseInt(parts[1].trim());
                return base + added;
            } catch (NumberFormatException e) {
                return 0;
            }
        } else {
            try {
                return Integer.parseInt(timeText.trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    private int calculateXPosition(String timeText, int totalMinutes, int totalPixels) {
        int minute = parseTimeToMinute(timeText);
        return (int) Math.ceil((double) totalPixels * minute / totalMinutes) + 15;
    }


    private void addTimeMarker(Pane timelinePane, String timeText, double xPosition, boolean isMajorEvent) {
        // Create vertical tick on timeline
        Line tick = new Line(xPosition, 35, xPosition, 45);
        tick.setStrokeWidth(1.5);
        tick.setStroke(Color.rgb(153, 31, 24)); // Changed to match the #3a0ca3 color

        // Create time label
        Label timeLabel = new Label(timeText);
        timeLabel.setFont(Font.font("Arial", isMajorEvent ? FontWeight.BOLD : FontWeight.NORMAL, 12));
        timeLabel.setStyle("-fx-text-fill: #991f18;"); // Match the style from createInfoBox
        timeLabel.setLayoutX(xPosition - 5);
        timeLabel.setLayoutY(50);

        timelinePane.getChildren().addAll(tick, timeLabel);
    }

    private void addMatchEvent(Pane timelinePane, String eventType, double xPosition, double yOffset) {
        ImageView eventIcon = new ImageView();
        try {
            String imagePath = "";
            switch(eventType) {
                case "goal":
                    imagePath = "/icons/football.png";
                    break;
                case "yellow_card":
                    imagePath = "/icons/cards.png";
                    break;
                case "red_card":
                    imagePath = "/icons/cards.png";
                    break;
                case "substitution":
                    imagePath = "/icons/substitution.png";
                    break;
            }

            eventIcon.setImage(new Image(getClass().getResourceAsStream(imagePath)));
            eventIcon.setFitHeight(20);
            eventIcon.setFitWidth(20);

            // Apply the styling from createInfoBox
            if(eventType.equalsIgnoreCase("yellow_card")) {
                eventIcon.setStyle("-fx-effect: innershadow(gaussian, #F3C623, 100, 0.8, 0, 0);");
            }


            // Create an info box with the styled icon
            HBox infoBox = new HBox(eventIcon);
            infoBox.setLayoutX(xPosition - 10);
            infoBox.setLayoutY(40 + yOffset - 10);

            timelinePane.getChildren().add(infoBox);
        } catch (Exception e) {
            // If image can't be loaded, use a colored rectangle with styling
            StackPane placeholder = new StackPane();
            placeholder.setPrefSize(15, 15);

            switch(eventType) {
                case "goal":
                    placeholder.setStyle("-fx-background-color: #000000; -fx-effect: innershadow(gaussian, #3a0ca3, 100, 0.8, 0, 0);");
                    break;
                case "yellow_card":
                    placeholder.setStyle("-fx-background-color: #FFD700; -fx-effect: innershadow(gaussian, #3a0ca3, 100, 0.8, 0, 0);");
                    break;
                case "red_card":
                    placeholder.setStyle("-fx-background-color: #FF0000; -fx-effect: innershadow(gaussian, #3a0ca3, 100, 0.8, 0, 0);");
                    break;
                case "substitution":
                    placeholder.setStyle("-fx-background-color: #00FF00; -fx-effect: innershadow(gaussian, #3a0ca3, 100, 0.8, 0, 0);");
                    break;
            }

            placeholder.setLayoutX(xPosition - 7.5);
            placeholder.setLayoutY(50 + yOffset - 7.5);

            timelinePane.getChildren().add(placeholder);
        }
    }

    private HBox createMatchInfoRow(Match match) {
        HBox matchInfo = new HBox(10);
        matchInfo.setAlignment(Pos.CENTER);

        // Date display
        HBox dateBox = createInfoBox(
                createImageView("/icons/calendar.png", 15, 15),
                new Label(match.getNgayThiDau().toString())
        );

        // Kickoff time display
        HBox kickOffBox = createInfoBox(
                createImageView("/icons/clock.png", 14, 14),
                new Label(match.getGioThiDau().toString())
        );

        // Venue display
        HBox venueBox = createInfoBox(
                createImageView("/icons/stadium.png", 14, 14),
                new Label(match.getSanThiDau())
        );

        // Add spacers for centering
        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        matchInfo.getChildren().addAll(leftSpacer, dateBox, kickOffBox, venueBox, rightSpacer);
        return matchInfo;
    }
    private HBox createInfoBox(ImageView icon, Label label) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER);
//        box.setPrefWidth(width);

        icon.setStyle("-fx-effect: innershadow(gaussian, #991f18, 100, 0.8, 0, 0);");
        label.setAlignment(Pos.CENTER_LEFT);
        label.setStyle("-fx-text-fill: #991f18; -fx-font-size: 14px;");

        box.getChildren().addAll(icon, label);
        return box;
    }
    private HBox createTeamsDisplay(Match match) {
        // Home team box
        VBox homeDisplay = new VBox();
        HBox homeTeamBox = new HBox(5);
        homeTeamBox.getStyleClass().addAll("hometeam-box", match.getLogoCLB1().toLowerCase().replace(".png", "-") + "homebox");

        ImageView homeLogo = createImageView("/Image/ClubLogo/" + match.getLogoCLB1(), 60, 60);

        Label homeNameLabel = new Label(match.getTenCLB1());
        homeNameLabel.setAlignment(Pos.CENTER_LEFT);
        homeNameLabel.setPrefWidth(200);
        homeNameLabel.setStyle(WHITE_BOLD_STYLE);

        homeTeamBox.getChildren().addAll(homeLogo, homeNameLabel);

        VBox homeGoals = GoalsListDisplay(match,true);
        homeDisplay.getChildren().addAll( homeTeamBox, homeGoals);

        // Result display
        VBox timeDisplay = new VBox();
        Label timeLabel = new Label(match.getScoreCLB1() + " - "+ match.getScoreCLB2());
        timeLabel.setAlignment(Pos.CENTER);
        timeLabel.setMinHeight(80);
        timeLabel.setMinWidth(120);
        timeLabel.setStyle("-fx-font-size: 45px; -fx-font-weight: bold; -fx-text-fill: white;-fx-background-color: #3a0ca3; -fx-border-color:none;-fx-background-radius: 0 0 10px 10px;");
        Region bottomSpacer = new Region();
//         bottomSpacer.setPrefHeight(25); // chiều cao khoảng trống
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);
        timeDisplay.getChildren().addAll(timeLabel, bottomSpacer);
        // Away team box
        VBox awayDisplay = new VBox();
        HBox awayTeamBox = new HBox(5);
        awayTeamBox.getStyleClass().addAll("awayteam-box", match.getLogoCLB2().toLowerCase().replace(".png", "-") + "awaybox");


        ImageView awayLogo = createImageView("/Image/ClubLogo/" + match.getLogoCLB2(), 60, 60);

        Label awayNameLabel = new Label(match.getTenCLB2());
        awayNameLabel.setAlignment(Pos.CENTER_RIGHT);
        awayNameLabel.setPrefWidth(200);
        awayNameLabel.setStyle(WHITE_BOLD_STYLE);

        awayTeamBox.getChildren().addAll(awayNameLabel, awayLogo);
        VBox awayGoals = GoalsListDisplay(match,false);
        awayDisplay.getChildren().addAll(awayTeamBox, awayGoals);

        // Container for all elements
        HBox container = new HBox(5);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(homeDisplay, timeDisplay, awayDisplay);

        return container;
    }

    private VBox GoalsListDisplay(Match match ,Boolean isHome) {
        VBox List = new VBox(10);
        String nameClub = "";
        if(isHome == true){
            List.setPadding(new Insets(10,10,5,0));
            nameClub = match.getTenCLB1();
        }else{
            List.setPadding(new Insets(10,0,5,10));
            nameClub = match.getTenCLB2();
        }

        List<GoalPopUpScene> goalsList = getGoalsOfMatchList(match.getId(), nameClub);
        for(GoalPopUpScene goal :goalsList ){
            VBox goalBox = createGoalsPlayer(goal.getTenCauThu(), goal.getPhutGhiBan(), isHome);
            List.getChildren().add(goalBox);
        }
        return List;

    }

    private VBox createGoalsPlayer(String name, int minute, Boolean isHome){
        VBox container = new VBox(3);
        HBox timeBox = new HBox(5);
        Label timeLabel = new Label(minute+"'");
        timeLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #991f18;");
        ImageView ballLogo = createImageView("/icons/football.png", 15, 15);
        ballLogo.setStyle("-fx-effect: innershadow(gaussian, #991f18, 100, 0.8, 0, 0);");
        if(isHome){
            timeBox.getChildren().addAll(timeLabel, ballLogo);
            timeBox.setAlignment(Pos.CENTER_RIGHT);
        }else{
            timeBox.getChildren().addAll(ballLogo, timeLabel);
            timeBox.setAlignment(Pos.CENTER_LEFT);
        }

        Label player = new Label(name);
        player.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #991f18;");

        container.getChildren().addAll(timeBox, player);
        if(isHome){
            container.setAlignment(Pos.CENTER_RIGHT);
        }else{
            container.setAlignment(Pos.CENTER_LEFT);
        }
        return container;
    }


    private ImageView createImageView(String resourcePath, double height, double width) {
        Image img=null;
        try {
             img = new Image(getClass().getResourceAsStream(resourcePath));
        }
        catch (Exception e) {
            e.printStackTrace();
            // Fallback to a default image if the resource is not found
        }
        ImageView imageView = new ImageView(img);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        return imageView;
    }

    // Helper methods for button actions
    private void openStadiumInfo(Match match) {
        // Create another popup or navigate to stadium info page
        MODEL_CLB club = new MODEL_CLB();
        try{
            MODEL_SAN san = service.getStadiumByName(match.getSanThiDau());
            club = service.getCLBByMaSan(san.getMaSan());
        }catch(Exception e){
            e.printStackTrace();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ClubsDetail.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            ClubsDetailController controller=loader.getController();
            MODEL_SAN stadium=service.getStadiumById(club.getMaSan());
            controller.setData(club,stadium);
            Stage stage = new Stage();
            stage.setTitle("Club Details - " + club.getTenCLB());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to load club details");
            alert.setContentText("An error occurred while trying to display the club details.");
            alert.showAndWait();
        }
    }

    // Inner class
    public class GoalPopUpScene {
        private int phutGhiBan;
        private String tenCauThu;

        public GoalPopUpScene() {}

        public GoalPopUpScene(int phutGhiBan, String tenCauThu) {
            this.phutGhiBan = phutGhiBan;
            this.tenCauThu = tenCauThu;
        }

        public int getPhutGhiBan() {
            return phutGhiBan;
        }

        public void setPhutGhiBan(int phutGhiBan) {
            this.phutGhiBan = phutGhiBan;
        }

        public String getTenCauThu() {
            return tenCauThu;
        }

        public void setTenCauThu(String tenCauThu) {
            this.tenCauThu = tenCauThu;
        }
    }

    public List<GoalPopUpScene> getGoalsOfMatchList(int id, String nameClub)  {
        List<GoalPopUpScene> goalsList = new ArrayList<>();
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();
            cstmt = conn.prepareCall("{call GetGoalsOfMatch(?,?,?)}");
            cstmt.setInt(1, id);
            cstmt.setString(2, nameClub);
            cstmt.registerOutParameter(3, OracleTypes.CURSOR);
            cstmt.execute();

            rs = (ResultSet) cstmt.getObject(3);
            while (rs.next()) {
                GoalPopUpScene banthang = new GoalPopUpScene();
                banthang.setPhutGhiBan(rs.getInt("PhutGhiBan"));
                banthang.setTenCauThu(rs.getString("TenCauThu"));
                goalsList.add(banthang);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (cstmt != null) try { cstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return goalsList;
    }

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
