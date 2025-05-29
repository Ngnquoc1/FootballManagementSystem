package Controller;

import Model.MODEL_CLB;
import Model.MODEL_MUAGIAI;
import Model.MODEL_SAN;
import Model.Session;
import Service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClubController implements Initializable {
    @FXML
    private ImageView userIcon;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> compeFilter;
    @FXML
    private Button resetBtn, addBtn;
    @FXML
    private GridPane team_container;

    private ObservableList<MODEL_CLB> allClubs = FXCollections.observableArrayList();
    private ObservableList<MODEL_CLB> filteredClubs = FXCollections.observableArrayList();

    private Service service = new Service();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureUIBasedOnRole();
        try {
            setFilter();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<MODEL_CLB> clubs = service.getAllClubs();
        allClubs.addAll(clubs);

        // Thiết lập sự kiện tìm kiếm
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                filterClubs();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        // Thiết lập sự kiện lọc theo mùa giải
        compeFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                filterClubs();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        // Hiển thị danh sách câu lạc bộ ban đầu
        try {
            loadTeams(allClubs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void configureUIBasedOnRole() {
        Session session = Session.getInstance();
        int userRole = session.getRole();


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
        dsMG.add("All CLubs");
        for (MODEL_MUAGIAI mg : ds1) {
            dsMG.add(mg.getTenMG());
        }
        compeFilter.getItems().addAll(dsMG);
        compeFilter.getSelectionModel().selectFirst();
    }

    private void filterClubs() throws SQLException {
        String searchText = searchField.getText().toLowerCase();
        System.out.println(searchText);
        String season = compeFilter.getValue();

        // Lọc câu lạc bộ theo tên
        Predicate<MODEL_CLB> searchFilter = club -> searchText.isEmpty()
                || club.getTenCLB().toLowerCase().contains(searchText);

        filteredClubs.clear();
        filteredClubs.addAll(allClubs.stream()
                .filter(searchFilter)
                .collect(Collectors.toList()));
        loadTeams(filteredClubs);
    }

    @FXML
    private void resetFilter() {
        searchField.clear();
        compeFilter.getSelectionModel().selectFirst();
        filteredClubs.clear();
        filteredClubs.addAll(allClubs);
        try {
            loadTeams(filteredClubs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    private void loadTeams(List<MODEL_CLB> teams) throws SQLException {
        team_container.getChildren().clear();
        // Số cột cố định trong GridPane
        int numCols = 3;

        // Tính toán số hàng cần thiết dựa trên số lượng đội bóng
        int numRows = (int) Math.ceil((double) teams.size() / numCols);

        // Xóa tất cả các ràng buộc hàng hiện có
        team_container.getRowConstraints().clear();

        // Tạo động các RowConstraints dựa trên số hàng cần thiết
        for (int i = 0; i < numRows; i++) {
            RowConstraints row = new RowConstraints();
            row.setMinHeight(150); // Chiều cao tối thiểu cho mỗi hàng
            row.setPrefHeight(150); // Chiều cao ưu tiên
            row.setVgrow(Priority.SOMETIMES);
            team_container.getRowConstraints().add(row);
        }

        // Thêm card cho từng đội bóng vào GridPane
        for (int i = 0; i < teams.size(); i++) {
            MODEL_CLB team = teams.get(i);
            VBox teamCard = teamCardView(team);

            // Tính toán vị trí hàng và cột dựa trên index
            int row = i / numCols;
            int col = i % numCols;

            // Thêm vào GridPane tại vị trí (col, row)
            team_container.add(teamCard, col, row);
        }

    }

    private VBox teamCardView(MODEL_CLB team) {
        VBox teamCard = new VBox(5);
        teamCard.setAlignment(Pos.CENTER);
        teamCard.setPadding(new Insets(5, 10, 0, 10));

        Image logo = null;
        try {
            logo = new Image(getClass().getResourceAsStream("/Image/ClubLogo/" + team.getLogoCLB()));
        } catch (Exception e) {
            logo = new Image(getClass().getResourceAsStream("/Image/ClubLogo/default_logo.png"));
        }
        ImageView logoView = new ImageView(logo);
        logoView.setPreserveRatio(true);
        logoView.getStyleClass().add("logo-view");

        // Create team name label
        Label nameLabel = new Label(team.getTenCLB());
        nameLabel.getStyleClass().add("team-name");
        nameLabel.setWrapText(true);

        // Create arrow icon for navigation
        Label arrowLabel = new Label("→");
        arrowLabel.getStyleClass().add("arrow-club-icon");

        Region spacer = new Region();
        spacer.setPrefWidth(20);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox content = new HBox(5);
        content.getChildren().addAll(nameLabel, spacer, arrowLabel);

        HBox colorBar = new HBox();
        colorBar.setPrefHeight(3);
        colorBar.getStyleClass().addAll("color-bar");

        teamCard.getChildren().addAll(logoView, content, colorBar);
        teamCard.getStyleClass().add("team-card");

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        HBox hBox = new HBox(leftSpacer, teamCard, rightSpacer);

        Region topSpacer = new Region();
        Region bottomSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        VBox wrapper = new VBox(topSpacer, hBox, bottomSpacer);
        wrapper.setOnMouseClicked(event -> {
            handleClubClick(team);
        });
        return wrapper;
    }

    private void handleClubClick(MODEL_CLB club) {
        // Xử lý khi người dùng nhấp vào câu lạc bộ
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to load club details");
            alert.setContentText("An error occurred while trying to display the club details.");
            alert.showAndWait();
        }
    }

    @FXML
    public void controlClub() {
        try {
            // Tải file FXML của cửa sổ mới
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ClubManagementFrame.fxml"));
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
                resetFilter();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}