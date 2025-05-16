package Controller;

import Service.Service;
import DAO.DAO_CLB;
import DAO.DAO_MUAGIAI;
import Model.MODEL_CLB;
import Model.MODEL_MUAGIAI;
import Model.Match;
import Model.Session;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FixtureController implements Initializable {
    @FXML
    private Button resetBtn,addBtn;
    @FXML
    private ScrollPane Calendar;
    @FXML
    private ComboBox<String> compeFilter, clubFilter;
    private Service service = new Service();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Session session = Session.getInstance();
        String role = session.getRole();
//        addBtn.setVisible(Objects.equals(role, "A"));
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
    private void setFilter() throws SQLException {

        DAO_MUAGIAI daoMG= new DAO_MUAGIAI();
        ArrayList<MODEL_MUAGIAI> ds1 = daoMG.selectAllDB();
        ArrayList<String> dsMG = new ArrayList<>();
        for (MODEL_MUAGIAI mg : ds1) {
            dsMG.add(mg.getTenMG());
        }
        compeFilter.getItems().addAll(dsMG);
        compeFilter.getSelectionModel().selectFirst();


        DAO_CLB daoClb= new DAO_CLB();
        ArrayList<MODEL_CLB> ds2 = daoClb.selectAllDB();
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


        // Kick-off time
        Label timeLabel = new Label(match.getFormattedGioThiDau());
        timeLabel.getStyleClass().add("time-label");

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
        info.getChildren().addAll(homeGroup, timeLabel, awayGroup);

        HBox venueBox = new HBox(5);
        venueBox.setAlignment(Pos.CENTER_LEFT);
        venueBox.getChildren().addAll(stadiumIcon, venueLabel);

        HBox quickViewBox = new HBox(5);
        quickViewBox.setAlignment(Pos.CENTER_RIGHT);
        quickViewBox.getChildren().addAll(quickViewLabel, arrowIcon);

        row.getChildren().addAll(
                info, venueBox, spacer, quickViewBox
        );
        return row;
    }
    @FXML
    public void filterByCLB() throws SQLException {
        String selectedCLB = clubFilter.getSelectionModel().getSelectedItem();
        Map<LocalDate,List<Match>> matchesByDate = service.getUpcomingMatchs();

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/MatchManagementFrame.fxml"));
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

}
