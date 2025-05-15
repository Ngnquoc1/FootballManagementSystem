package Controller;

import DAO.DAO_CLB;
import DAO.DAO_MUAGIAI;
import DAO.DAO_SAN;
import Model.MODEL_CLB;
import Model.MODEL_MUAGIAI;
import Model.MODEL_SAN;
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
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClbController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> compeFilter;
    @FXML private Button resetBtn;
    @FXML private GridPane team_container;

    private ObservableList<MODEL_CLB> allClubs = FXCollections.observableArrayList();
    private ObservableList<MODEL_CLB> filteredClubs = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setFilter();
        try {
            List<MODEL_CLB> clubs = new DAO_CLB().selectAllDB();
            allClubs.addAll(clubs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
            loadTeams( allClubs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void setFilter() {
        DAO_MUAGIAI daoMG= new DAO_MUAGIAI();
        ArrayList<MODEL_MUAGIAI> ds1 = daoMG.selectAllDB();
        ArrayList<String> dsMG = new ArrayList<>();
        for (MODEL_MUAGIAI mg : ds1) {
            dsMG.add(mg.getTenMG());
        }
        compeFilter.getItems().addAll(dsMG);
        compeFilter.getSelectionModel().selectFirst();
    }
//    private void loadSampleData() {
//        // Tạo dữ liệu mẫu cho các câu lạc bộ
//        String[] clubNames = {
//                "Arsenal", "Aston Villa", "Bournemouth", "Brentford",
//                "Brighton & Hove Albion", "Chelsea", "Crystal Palace", "Everton",
//                "Fulham", "Liverpool", "Manchester City", "Manchester United",
//                "Newcastle United", "Nottingham Forest", "Southampton", "Tottenham Hotspur",
//                "West Ham United", "Wolverhampton"
//        };
//
//        for (int i = 0; i < clubNames.length; i++) {
//            MODEL_CLB club = new MODEL_CLB();
//            club.setMaCLB(i + 1);
//            club.setMaSan(i + 100);
//            club.setTenCLB(clubNames[i]);
//            club.setLogoCLB("/Image/ClubLogo/" + clubNames[i].toLowerCase().replace(" ", "_") + ".png");
//            allClubs.add(club);
//        }
//
//        filteredClubs.addAll(allClubs);
//    }

    private void filterClubs() throws SQLException {
        String searchText = searchField.getText().toLowerCase();
        System.out.println(searchText);
        String season = compeFilter.getValue();

        // Lọc câu lạc bộ theo tên
        Predicate<MODEL_CLB> searchFilter = club ->
                searchText.isEmpty() || club.getTenCLB().toLowerCase().contains(searchText);

        // Trong thực tế, bạn có thể thêm lọc theo mùa giải ở đây

        filteredClubs.clear();
        filteredClubs.addAll(allClubs.stream()
                .filter(searchFilter)
                .collect(Collectors.toList()));
        loadTeams( filteredClubs);
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
            row.setMinHeight(150);  // Chiều cao tối thiểu cho mỗi hàng
            row.setPrefHeight(150); // Chiều cao ưu tiên
            row.setVgrow(Priority.SOMETIMES);
            team_container.getRowConstraints().add(row);
        }

        // Thêm card cho từng đội bóng vào GridPane
        for (int i = 0; i < teams.size(); i++) {
            MODEL_CLB team = teams.get(i);
            VBox teamCard =  teamCardView(team);

            // Tính toán vị trí hàng và cột dựa trên index
            int row = i / numCols;
            int col = i % numCols;

            // Thêm vào GridPane tại vị trí (col, row)
            team_container.add(teamCard, col, row);
        }

    }
    private VBox teamCardView(MODEL_CLB team){
        VBox teamCard = new VBox(5);
        teamCard.setAlignment(Pos.CENTER);
        teamCard.setPadding(new Insets(5,10,0, 10));
        
        ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/Image/ClubLogo/"+ team.getLogoCLB())));
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
        content.getChildren().addAll( nameLabel,spacer, arrowLabel);

        HBox colorBar = new HBox();
        colorBar.setPrefHeight(3);
        colorBar.getStyleClass().addAll("color-bar");

        teamCard.getChildren().addAll(logoView, content,colorBar);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ClubsDetail1.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            ClubsDetailController controller=loader.getController();
            MODEL_SAN stadium=new DAO_SAN().selectByID(club.getMaSan());
            controller.setData(club,stadium);
            Stage stage = new Stage();
            stage.setTitle("Club Details - " + club.getTenCLB());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
            stage.showAndWait();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to load club details");
            alert.setContentText("An error occurred while trying to display the club details.");
            alert.showAndWait();
        }
        // Ở đây bạn có thể mở một cửa sổ mới để hiển thị chi tiết câu lạc bộ
        // hoặc chuyển đến một màn hình khác


    }
    @FXML
    public void controlClub(){

    }

}