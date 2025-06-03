package Controller;

import Model.*;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import Service.Service;

public class PlayerController implements Initializable {
    @FXML
    private VBox Player_table;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> compeFilter;
    @FXML private ComboBox<String> ClubFilter;
    @FXML private Button addBtn;


    private String selectedClub = null;
    private String selectedCompetition = null;
    private Service service;

    

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        service = new Service();
        // Create the header row
        configureUIBasedOnRole();
        try {
            setFilter();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Đặt giá trị mặc định cho ComboBox
        compeFilter.getSelectionModel().selectFirst();
        ClubFilter.getSelectionModel().selectFirst();

        // Thiết lập sự kiện tìm kiếm
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                filterPlayers();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        ClubFilter.setOnAction(event -> {
            selectedClub = ClubFilter.getValue();
            // Chỉ lọc theo CLB nếu không phải là giá trị mặc định "AllClub"
            if (!"AllClubs".equals(selectedClub)) {
                try {
                    filterPlayers();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                selectedClub = null;
                try {
                    filterPlayers();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Thêm listener cho ComboBox Mùa giải
        compeFilter.setOnAction(event -> {
            selectedCompetition = compeFilter.getValue();
            // Chỉ lọc theo mùa giải nếu không phải là giá trị mặc định "Choose your League"
            if (!"Choose your League".equals(selectedCompetition)) {
                try {
                    filterPlayers();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                selectedCompetition = null;
                try {
                    filterPlayers();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        loadPlayers();
    }


    private void configureUIBasedOnRole() {
        Session session = Session.getInstance();
        int userRole = session.getRole();

        // Nếu role là "A", ẩn Registry và Rules buttons
        if (userRole == 5 || userRole == 4 || userRole == 3 || userRole == 1) {
            if (addBtn != null) {
                addBtn.setVisible(false);
                addBtn.setManaged(false); // Không chiếm không gian trong layout
            }

        }
    }

    public void setFilter() throws SQLException {
        List<MODEL_MUAGIAI> ds1 = service.getAllTournament();
        ArrayList<String> dsMG = new ArrayList<>();
        dsMG.add("Choose your League");
        for (MODEL_MUAGIAI mg : ds1) {
            dsMG.add(mg.getTenMG());
        }
        compeFilter.getItems().addAll(dsMG);

        List<MODEL_CLB> ds2 = new ArrayList<>();
        ArrayList<String> dsCLB = new ArrayList<>();
        dsCLB.add("AllClubs");
        ds2 = service.getAllClubs();
        for (MODEL_CLB clb : ds2) {
            dsCLB.add(clb.getTenCLB());
        }
        ClubFilter.getItems().addAll(dsCLB);
    }

    private void filterPlayers() throws SQLException {
        String searchText = searchField.getText().toLowerCase().trim();

        // Xóa tất cả các hàng hiện tại, chỉ giữ lại header
        Player_table.getChildren().clear();
        Player_table.getChildren().add(createHeaderRow());

        // Đọc tất cả cầu thủ từ database
        List<MODEL_CAUTHU> allPlayers = service.getAllPlayers();
        ArrayList<MODEL_CAUTHU> filteredPlayers = new ArrayList<>();

        // Lọc danh sách cầu thủ dựa trên các tiêu chí
        for (MODEL_CAUTHU player : allPlayers) {
            boolean matchesSearch = true;
            boolean matchesClub = true;
            boolean matchesCompetition = true;

            // Lọc theo từ khóa tìm kiếm
            if (!searchText.isEmpty()) {
                // Tìm theo tên cầu thủ
                boolean matchesName = player.getTenCT().toLowerCase().contains(searchText);

                // Tìm theo vị trí
                String position = getPositionText(player.getMaVT() + "");
                boolean matchesPosition = position.toLowerCase().contains(searchText);

                // Tìm theo quốc tịch
                boolean matchesNationality = player.getQuocTich().toLowerCase().contains(searchText);

                // Kết hợp các điều kiện tìm kiếm
                matchesSearch = matchesName || matchesPosition || matchesNationality;
            }

            // Lọc theo CLB
            if (selectedClub != null && !selectedClub.isEmpty()) {
                MODEL_CLB player_clb = service.getCLBByID(player.getMaCLB());
                matchesClub = player_clb.getTenCLB().equals(selectedClub);
            }

            // Lọc theo mùa giải
            // Lọc theo mùa giải
            if (selectedCompetition != null && !selectedCompetition.isEmpty()) {
                String sql = "MaCT = " + player.getMaCT();
                List<MODEL_CAUTHUTHAMGIACLB> registedPlayers = service.getRegistedPlayersByCondition(sql);
                if (!registedPlayers.isEmpty()) {
                    MODEL_CAUTHUTHAMGIACLB ct_clb = registedPlayers.getFirst();
                    MODEL_MUAGIAI mgi = service.getTournamentByID(ct_clb.getMaMG());
                    matchesCompetition = mgi.getTenMG().equals(selectedCompetition);
                } else {
                    matchesCompetition = false;
                }
            }

            // Thêm cầu thủ vào danh sách đã lọc nếu thỏa mãn tất cả điều kiện
            if (matchesSearch && matchesClub && matchesCompetition) {
                filteredPlayers.add(player);
            }
        }

        // Hiển thị các cầu thủ đã lọc
        if (filteredPlayers.isEmpty()) {
            displayNoResults();
        } else {
            for (MODEL_CAUTHU player : filteredPlayers) {
                Player_table.getChildren().add(createPlayerRow(player));
            }
        }
    }

    private String getPositionText(String positionCode) {
        if (positionCode.equalsIgnoreCase("4")) {
            return "Goalkeeper";
        } else if (positionCode.equalsIgnoreCase("2")) {
            return "Midfielder";
        } else if (positionCode.equalsIgnoreCase("1")) {
            return "Forward";
        } else {
            return "Defender";
        }
    }

    /**
     * Hiển thị thông báo khi không có kết quả tìm kiếm
     */
    private void displayNoResults() {
        HBox noResultsRow = new HBox();
        noResultsRow.setPadding(new Insets(20));
        noResultsRow.setAlignment(Pos.CENTER);

        Label noResultsLabel = new Label("Không tìm thấy cầu thủ phù hợp");
        noResultsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        noResultsLabel.setStyle("-fx-text-fill: #991f18;");

        noResultsRow.getChildren().add(noResultsLabel);
        Player_table.getChildren().add(noResultsRow);
    }

    @FXML
    private void resetFilter() {
        searchField.clear();
        // Đặt ComboBox trở lại giá trị mặc định thay vì xóa hoàn toàn
        compeFilter.getSelectionModel().selectFirst();
        ClubFilter.getSelectionModel().selectFirst();

        // Reset các biến lọc
        selectedClub = null;
        selectedCompetition = null;

        try {
            // Tải lại toàn bộ cầu thủ
            Player_table.getChildren().clear();
            loadPlayers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlayers() {
        Player_table.getChildren().add(createHeaderRow());

        List<MODEL_CAUTHU> playersList = new ArrayList<>();
        playersList = service.getAllPlayers();
        // Có thể duyệt qua danh sách nếu cần
        for (MODEL_CAUTHU player : playersList) {
            Player_table.getChildren().add(createPlayerRow(player));
        }
    }

    private HBox createHeaderRow() {
        HBox headerRow = new HBox();
        headerRow.setPadding(new Insets(5, 10, 10, 10));
        headerRow.setStyle(
                "-fx-border-color: transparent transparent #b8b8ff transparent;" +
                        "-fx-border-width: 0 0 1px 0;" +
                        "-fx-border-style: solid;");

        Label playerLabel = new Label("Player");
        Label positionLabel = new Label("Position");
        Label nationalityLabel = new Label("Nationality");

        // Set style for header labels
        String headerStyle = "-fx-text-fill: #8E1616;";
        playerLabel.setStyle(headerStyle);
        positionLabel.setStyle(headerStyle);
        nationalityLabel.setStyle(headerStyle);

        playerLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        positionLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nationalityLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Add labels to header row with proper spacing and alignment
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        headerRow.getChildren().addAll(
                playerLabel,
                spacer1,
                positionLabel,
                spacer2,
                nationalityLabel);

        // Set appropriate widths
        playerLabel.setPrefWidth(250);
        positionLabel.setPrefWidth(200);
        nationalityLabel.setPrefWidth(190);

        return headerRow;
    }

    private HBox createPlayerRow(MODEL_CAUTHU player) {
        HBox playerRow = new HBox();
        playerRow.getStyleClass().add("player_row");

        // Player image
        ImageView playerImage = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/Image/PlayerAva/" + player.getAvatar()));
            playerImage.setImage(image);
        } catch (Exception e) {
            // Use a placeholder if image not found
            Image image = new Image(getClass().getResourceAsStream("/Image/PlayerAva/default_ava.png"));
            playerImage.setImage(image);
        }
        playerImage.getStyleClass().add("player_image");

        // Player name with image
        HBox playerNameBox = new HBox(10);
        Label nameLabel = new Label(player.getTenCT());
        nameLabel.getStyleClass().add("player_name");
        nameLabel.setOnMouseClicked(event -> openPlayerDetails(player));
        playerNameBox.getChildren().addAll(playerImage, nameLabel);
        playerNameBox.setAlignment(Pos.CENTER_LEFT);

        // Position label
        String position = "";
        if (player.getMaVT() == 4) {
            position = "GoalKeeper";
        } else if (player.getMaVT() == 2) {
            position = "Midfielder";
        } else if (player.getMaVT() == 1) {
            position = "Forward";
        } else {
            position = "Defender";
        }
        Label positionLabel = new Label(position);
        positionLabel.getStyleClass().add("player_info");

        // Nationality with flag
        // NationalityLogo từ trang https://www.countryflags.com/image-overview/
        ImageView flagImage = new ImageView();
        try {
            Image flag = new Image(
                    getClass().getResourceAsStream("/Image/NationLogo/" + player.getQuocTich() + ".png"));
            flagImage.setImage(flag);
        } catch (Exception e) {
            // Create a placeholder for missing flag
            Image flag = new Image(getClass().getResourceAsStream("/Image/NationLogo/Vietnam.png"));
            flagImage.setImage(flag);
        }
        flagImage.getStyleClass().add("flag_image");

        HBox nationalityBox = new HBox(10);
        Label nationalityLabel = new Label(player.getQuocTich());
        nationalityLabel.getStyleClass().add("player_info");
        nationalityBox.getChildren().addAll(flagImage, nationalityLabel);
        nationalityBox.setAlignment(Pos.CENTER_LEFT);

        // Add items to player row with proper spacing and alignment
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        playerRow.getChildren().addAll(
                playerNameBox,
                spacer1,
                positionLabel,
                spacer2,
                nationalityBox);

        // Set appropriate widths
        playerNameBox.setPrefWidth(250);
        positionLabel.setPrefWidth(200);
        nationalityBox.setPrefWidth(190);

        return playerRow;
    }

    private void openPlayerDetails(MODEL_CAUTHU player) {
        // Create a new stage for the popup with blurred background effect
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block input to other windows
        popupStage.initStyle(StageStyle.TRANSPARENT); // Transparent stage for custom shape

        try {
            // Take a snapshot of the current scene to use as background
            Scene currentScene = Player_table.getScene();

            // Create the root container for our popup
            StackPane overlayRoot = new StackPane();

            // Create a semi-transparent rectangle to darken the background
            Rectangle overlay = new Rectangle(currentScene.getWidth(), currentScene.getHeight() + 40);
            overlay.setFill(Color.rgb(50, 50, 50, 0.7));
            // Create the popup content
            VBox popupContent = createPlayerPopupContent(player);
            popupContent.setMaxWidth(700);
            popupContent.setMaxHeight(500);
            popupContent.setStyle(
                    "-fx-background-color: white; -fx-border-color: #991f18; -fx-border-width: 1px; -fx-border-radius: 5px;");

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
            popupScene.getStylesheets().add(getClass().getResource("/css/Player.css").toExternalForm());

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
            openRegularPopup(player);
        }
    }

    // Helper method to check if a mouse event is inside a node
    private boolean isClickInsideNode(MouseEvent event, Node node) {
        Point2D point = new Point2D(event.getX(), event.getY());
        Point2D nodePoint = node.sceneToLocal(point);
        return node.contains(nodePoint);
    }

    private void openRegularPopup(MODEL_CAUTHU player) {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block input to other windows
        popupStage.initStyle(StageStyle.UNDECORATED); // Remove window decorations for a cleaner look

        // Create the popup content
        VBox popupRoot = createPlayerPopupContent(player);

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

    private VBox createPlayerPopupContent(MODEL_CAUTHU player) {
        // Fetch related data
        PlayerClubData clubData = fetchPlayerClubData(player.getMaCT());

        // Main container setup
        VBox mainContainer = createMainContainer();

        // Build sections
        HBox headerSection = createHeaderSection(player);
        VBox clubDetailsSection = createClubDetailsSection(clubData.clb, player);
        VBox personalDetailsSection = createPersonalDetailsSection(player);

        // Assemble final layout
        mainContainer.getChildren().addAll(
                headerSection,
                clubDetailsSection,
                personalDetailsSection);

        return mainContainer;
    }

    private PlayerClubData fetchPlayerClubData(int playerId) {
        PlayerClubData data = new PlayerClubData();
        try {
            String sql = "MaCT= " + playerId;
            MODEL_CAUTHUTHAMGIACLB ct_clb = service.getRegistedPlayersByCondition(sql).get(0);
            data.ctclb = ct_clb;

            data.clb = service.getCLBByID(data.ctclb.getMaCLB());
        } catch (Exception e) {
            e.printStackTrace();
            // Consider logging the error properly instead of printStackTrace
        }
        return data;
    }

    private VBox createMainContainer() {
        VBox container = new VBox();
        container.getStyleClass().add("player_detail_container");
        container.setPadding(new Insets(20));
        container.setSpacing(20);
        return container;
    }

    private HBox createHeaderSection(MODEL_CAUTHU player) {
        HBox headerSection = new HBox();
        headerSection.setSpacing(20);
        headerSection.setAlignment(Pos.CENTER_LEFT);

        ImageView playerImage = createPlayerImage(player);
        Label playerName = createPlayerNameLabel(player);
        Label jerseyNumber = createJerseyNumberLabel(player);
        Region spacer = createSpacer();

        headerSection.getChildren().addAll(playerImage, playerName, spacer, jerseyNumber);
        return headerSection;
    }

    private ImageView createPlayerImage(MODEL_CAUTHU player) {
        ImageView playerImage = new ImageView();
        Image image = loadImageWithFallback(
                "/Image/PlayerAva/" + player.getAvatar(),
                "/Image/PlayerAva/default_ava.png");

        playerImage.setImage(image);
        playerImage.setFitWidth(160);
        playerImage.setFitHeight(160);
        playerImage.setPreserveRatio(true);
        playerImage.getStyleClass().add("detail_player_image");

        return playerImage;
    }

    private Label createPlayerNameLabel(MODEL_CAUTHU player) {
        Label playerName = new Label(player.getTenCT());
        playerName.getStyleClass().add("detail_player_name");
        return playerName;
    }

    private Label createJerseyNumberLabel(MODEL_CAUTHU player) {
        Label jerseyNumber = new Label(String.valueOf(player.getSoAo()));
        jerseyNumber.getStyleClass().add("detail_jersey_number");
        return jerseyNumber;
    }

    private Region createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private VBox createClubDetailsSection(MODEL_CLB clb, MODEL_CAUTHU player) {
        VBox section = new VBox();
        section.setSpacing(10);

        Label title = new Label("Club Details");
        title.getStyleClass().add("section_title");

        GridPane grid = createDetailsGrid();
        populateClubDetailsGrid(grid, clb, player);

        section.getChildren().addAll(title, grid);
        return section;
    }

    private VBox createPersonalDetailsSection(MODEL_CAUTHU player) {
        VBox section = new VBox();
        section.setSpacing(10);

        Label title = new Label("Personal Details");
        title.getStyleClass().add("section_title");

        GridPane grid = createDetailsGrid();
        populatePersonalDetailsGrid(grid, player);

        section.getChildren().addAll(title, grid);
        return section;
    }

    private GridPane createDetailsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));

        // Configure column constraints
        ColumnConstraints[] columns = {
                createColumnConstraints(100, 80, 120),
                createColumnConstraints(150, 120, 200),
                createColumnConstraints(100, 80, 120),
                createColumnConstraints(120, 100, 150)
        };

        grid.getColumnConstraints().addAll(columns);
        return grid;
    }

    private ColumnConstraints createColumnConstraints(int prefWidth, int minWidth, int maxWidth) {
        ColumnConstraints col = new ColumnConstraints();
        col.setPrefWidth(prefWidth);
        col.setMinWidth(minWidth);
        col.setMaxWidth(maxWidth);
        return col;
    }

    private void populateClubDetailsGrid(GridPane grid, MODEL_CLB clb, MODEL_CAUTHU player) {
        // Club information
        Label clubLabel = createDetailLabel("Club");
        HBox clubBox = createClubInfoBox(clb);

        // Position information
        Label posLabel = createDetailLabel("Position");
        Label posValue = createDetailValue(getPositionDisplayName(player.getMaVT()));

        // Add to grid
        grid.add(clubLabel, 0, 0);
        grid.add(clubBox, 1, 0);
        grid.add(posLabel, 2, 0);
        grid.add(posValue, 3, 0);
    }

    private void populatePersonalDetailsGrid(GridPane grid, MODEL_CAUTHU player) {
        // Nationality information
        Label nationalityLabel = createDetailLabel("Nationality");
        HBox nationalityBox = createNationalityBox(player);

        // Date of birth information
        Label dobLabel = createDetailLabel("Date of Birth");
        Label dobValue = createDetailValue(formatDate(player.getNgaysinh()));

        // Add to grid
        grid.add(nationalityLabel, 0, 0);
        grid.add(nationalityBox, 1, 0);
        grid.add(dobLabel, 2, 0);
        grid.add(dobValue, 3, 0);
    }

    private HBox createClubInfoBox(MODEL_CLB clb) {
        HBox clubBox = new HBox();
        clubBox.setSpacing(8);
        clubBox.setAlignment(Pos.CENTER_LEFT);

        ImageView clubImage = createClubImage(clb);
        Label clubValue = createDetailValue(clb.getTenCLB());

        clubBox.getChildren().addAll(clubImage, clubValue);
        return clubBox;
    }

    private ImageView createClubImage(MODEL_CLB clb) {
        ImageView clubImage = new ImageView();
        Image image = loadImageWithFallback(
                "/Image/ClubLogo/" + clb.getLogoCLB(),
                "/Image/ClubLogo/default_logo.png");

        clubImage.setImage(image);
        clubImage.getStyleClass().add("detail_image");
        return clubImage;
    }

    private HBox createNationalityBox(MODEL_CAUTHU player) {
        HBox nationalityBox = new HBox();
        nationalityBox.setSpacing(8);
        nationalityBox.setAlignment(Pos.CENTER_LEFT);

        ImageView flagImage = createFlagImage(player);
        Label nationalityValue = createDetailValue(player.getQuocTich());

        nationalityBox.getChildren().addAll(flagImage, nationalityValue);
        return nationalityBox;
    }

    private ImageView createFlagImage(MODEL_CAUTHU player) {
        ImageView flagImage = new ImageView();
        Image image = loadImageWithFallback(
                "/Image/NationLogo/" + player.getQuocTich() + ".png",
                "/Image/NationLogo/England.png");

        flagImage.setImage(image);
        flagImage.getStyleClass().add("detail_image");
        return flagImage;
    }

    private Label createDetailLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("detail_label");
        return label;
    }

    private Label createDetailValue(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("detail_value");
        return label;
    }

    private Image loadImageWithFallback(String primaryPath, String fallbackPath) {
        try {
            return new Image(getClass().getResourceAsStream(primaryPath));
        } catch (Exception e) {
            return new Image(getClass().getResourceAsStream(fallbackPath));
        }
    }

    private String getPositionDisplayName(int positionCode) {
        switch (positionCode) {
            case 4:
                return "GoalKeeper";
            case 2:
                return "Midfielder";
            case 1:
                return "Forward";
            default:
                return "Defender";
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    // Helper class to hold related data
    private static class PlayerClubData {
        MODEL_CAUTHUTHAMGIACLB ctclb = new MODEL_CAUTHUTHAMGIACLB();
        MODEL_CLB clb = new MODEL_CLB();
    }

    @FXML
    public void openPlayerManagement() {
        String sql = "TenCLB= '" + ClubFilter.getValue() + "'";
        MODEL_CLB clb = service.getClbByCondition(sql);
        if (clb != null) {
            openPlayerRegistrationWindow(clb);
        } else {
            AlertUtils.showWarning("Cảnh báo", "Chưa chọn CLB",
                    "Vui lòng chọn một CLB từ CLB filter để quản lý danh sách cầu thủ.");
        }
    }

    private void openPlayerRegistrationWindow(MODEL_CLB club) {
        try {
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
            stage.setTitle("Quản lý cầu thủ - " + club.getTenCLB());
            stage.setScene(scene);
            stage.setResizable(false);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Không thể mở cửa sổ đăng ký cầu thủ", e.getMessage());
        }
    }

    @FXML
    private ImageView userIcon;

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
