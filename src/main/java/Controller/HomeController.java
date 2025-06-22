package Controller;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import Model.MODEL_BXH_CLB;
import Model.MODEL_CLB;
import Model.Match;
import Model.Session;
import Service.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HomeController implements Initializable {

    @FXML
    private Label latestHomeTeam;
    @FXML
    private Label latestAwayTeam;
    @FXML
    private Label latestScore;
    @FXML
    private Label latestDate;
    @FXML
    private ImageView latestHomeImage;
    @FXML
    private ImageView latestAwayImage;

    @FXML
    private Label nextHomeTeam;
    @FXML
    private Label nextAwayTeam;
    @FXML
    private Label nextTime;
    @FXML
    private Label nextDate;
    @FXML
    private ImageView nextHomeImage;
    @FXML
    private ImageView nextAwayImage;

    @FXML
    private Label team1Name;
    @FXML
    private Label team1Points;
    @FXML
    private ImageView team1Image;
    @FXML
    private VBox team1Card;

    @FXML
    private Label team2Name;
    @FXML
    private Label team2Points;
    @FXML
    private ImageView team2Image;
    @FXML
    private VBox team2Card;

    @FXML
    private Label team3Name;
    @FXML
    private Label team3Points;
    @FXML
    private ImageView team3Image;
    @FXML
    private VBox team3Card;

    @FXML
    private Button viewResultBtn;
    @FXML
    private Button viewFixtureBtn;
    @FXML
    private Button viewTableBtn;
    @FXML
    private ImageView userIcon;

    private Service service;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        service = new Service();

        try {
            loadLatestMatch();
            loadNextMatch();
            loadTopTeams();
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();

            // loadDefaultData();
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

    private MenuController getMenuController() {
        try {
            BorderPane mainContent = (BorderPane) viewResultBtn.getScene().getRoot();
            return (MenuController) mainContent.getUserData();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Chuyển đến màn hình kết quả
    @FXML
    private void navigateToResultFrame() {
        MenuController menuController = getMenuController();
        if (menuController != null) {
            menuController.clickOnResultBtn();
        }
    }

    // Chuyển đến màn hình lịch thi đấu
    @FXML
    private void navigateToFixtureFrame() {
        MenuController menuController = getMenuController();
        if (menuController != null) {
            menuController.clickOnFixtureBtn();
        }
    }

    // Chuyển đến màn hình bảng xếp hạng
    @FXML
    private void navigateToTableFrame() {
        MenuController menuController = getMenuController();
        if (menuController != null) {
            menuController.clickOnTableBtn();
        }
    }

    private void loadLatestMatch() {
        try {
            // Lấy danh sách các trận đấu đã diễn ra
            List<Match> matches = service.getResultedMatchList();

            if (!matches.isEmpty()) {
                // Lấy trận đấu gần nhất
                Match latestMatch = matches.get(0);

                latestHomeTeam.setText(latestMatch.getTenCLB1());
                latestAwayTeam.setText(latestMatch.getTenCLB2());
                latestScore.setText(latestMatch.getScoreCLB1() + " - " + latestMatch.getScoreCLB2());
                latestDate.setText(latestMatch.getNgayThiDau().format(dateFormatter));

                loadTeamLogo(latestHomeImage, latestMatch.getLogoCLB1());
                loadTeamLogo(latestAwayImage, latestMatch.getLogoCLB2());

                return;
            }
        } catch (Exception e) {
            System.err.println("Error loading latest match: " + e.getMessage());
        }

        // loadDefaultLatestMatch();
    }

    private void loadNextMatch() {
        try {
            // Lấy trận gần nhất
            List<Match> upcomingMatches = new ArrayList<>();
            service.getUpcomingMatchs().forEach((date, matches) -> upcomingMatches.addAll(matches));

            if (!upcomingMatches.isEmpty()) {
                // Mình viết thuật toán ngu dữ
                upcomingMatches.sort((m1, m2) -> m1.getNgayThiDau().compareTo(m2.getNgayThiDau()));
                Match nextMatch = upcomingMatches.get(0);

                nextHomeTeam.setText(nextMatch.getTenCLB1());
                nextAwayTeam.setText(nextMatch.getTenCLB2());
                nextTime.setText(nextMatch.getGioThiDau().format(DateTimeFormatter.ofPattern("HH:mm")));
                nextDate.setText(nextMatch.getNgayThiDau().format(dateFormatter));

                loadTeamLogo(nextHomeImage, nextMatch.getLogoCLB1());
                loadTeamLogo(nextAwayImage, nextMatch.getLogoCLB2());

                return;
            }
        } catch (Exception e) {
            System.err.println("Error loading upcoming match: " + e.getMessage());
        }

        // loadDefaultNextMatch();
    }

    private void loadTopTeams() {
        try {
            // Clear all previous data first
            team1Name.setText("");
            team1Points.setText("");
            team1Image.setImage(null);
            team1Card.setStyle(
                    "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

            team2Name.setText("");
            team2Points.setText("");
            team2Image.setImage(null);
            team2Card.setStyle(
                    "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

            team3Name.setText("");
            team3Points.setText("");
            team3Image.setImage(null);
            team3Card.setStyle(
                    "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

            ArrayList<MODEL_BXH_CLB> rankings = service.getCurrentSeasonBxhCLB();

            if (rankings == null || rankings.isEmpty()) {
                System.err.println("Không có dữ liệu bảng xếp hạng cho mùa giải hiện tại");
                return;
            }

            rankings.sort((a, b) -> Integer.compare(a.getHang(), b.getHang()));

            System.out.println("Số lượng đội trong bảng xếp hạng: " + rankings.size());

            if (rankings.size() >= 1) {
                MODEL_BXH_CLB team1Ranking = rankings.get(0);
                MODEL_CLB club1 = service.getCLBByID(team1Ranking.getMaCLB());
                team1Name.setText(club1.getTenCLB());
                team1Points.setText(String.valueOf(team1Ranking.getDiem()));
                loadTeamLogo(team1Image, club1.getLogoCLB());
                team1Card.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #f9f3ee, #e6f7ff); -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
            } else {
                team1Name.setText("");
                team1Points.setText("");
                team1Image.setImage(null);
            }

            if (rankings.size() >= 2) {
                MODEL_BXH_CLB team2Ranking = rankings.get(1);
                MODEL_CLB club2 = service.getCLBByID(team2Ranking.getMaCLB());
                team2Name.setText(club2.getTenCLB());
                team2Points.setText(String.valueOf(team2Ranking.getDiem()));
                loadTeamLogo(team2Image, club2.getLogoCLB());
                team2Card.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #f9f3ee, #e6f7ff); -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
            } else {
                team2Name.setText("");
                team2Points.setText("");
                team2Image.setImage(null);
            }
            if (rankings.size() >= 3) {
                MODEL_BXH_CLB team3Ranking = rankings.get(2);
                MODEL_CLB club3 = service.getCLBByID(team3Ranking.getMaCLB());
                team3Name.setText(club3.getTenCLB());
                team3Points.setText(String.valueOf(team3Ranking.getDiem()));
                loadTeamLogo(team3Image, club3.getLogoCLB());
                team3Card.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #f9f3ee, #e6f7ff); -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
            } else {
                // Xóa nội dung nếu không có đội hạng 3
                team3Name.setText("");
                team3Points.setText("");
                team3Image.setImage(null);
            }

        } catch (Exception e) {
            System.err.println("Error loading top teams: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTeamLogo(ImageView imageView, String logoPath) {
        try {
            String imagePath = "src/main/resources/image/ClubLogo/" + logoPath;
            File logoFile = new File(imagePath);
            Image image = new Image(logoFile.toURI().toString());
            imageView.setImage(image);
        } catch (Exception e) {
            imageView.setImage(new Image(new File("src/main/resources/icons/club.png").toURI().toString()));
        }
    }
}