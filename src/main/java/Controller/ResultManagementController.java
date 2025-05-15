package Controller;

import DAO.*;
import Model.*;
import Service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class ResultManagementController implements Initializable {
    @FXML
    private TableView<Match> fixtureTable, resultTable;
    @FXML
    private TableColumn<Match, String> idCol, leagueCol, roundCol, homeTeamCol, awayTeamCol, idCol1, leagueCol1, roundCol1, homeTeamCol1, awayTeamCol1, dateCol, dateCol1, timeCol, stadiumCol, scoreCol1, scoreCol2;
    @FXML
    private Button findBtn, removeBtn, updateBtn, resetBtn;
    @FXML
    private ComboBox<String> compeFilter, clubFilter;
    @FXML
    private Label idLabel;
    @FXML
    private Service service= new Service();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        leagueCol.setCellValueFactory(new PropertyValueFactory<>("tenMuaGiai"));
        roundCol.setCellValueFactory(new PropertyValueFactory<>("tenVongDau"));
        homeTeamCol.setCellValueFactory(new PropertyValueFactory<>("tenCLB1"));
        awayTeamCol.setCellValueFactory(new PropertyValueFactory<>("tenCLB2"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("ngayThiDau"));
        scoreCol1.setCellValueFactory(new PropertyValueFactory<>("scoreCLB1"));
        scoreCol2.setCellValueFactory(new PropertyValueFactory<>("scoreCLB2"));

        idCol1.setCellValueFactory(new PropertyValueFactory<>("id"));
        leagueCol1.setCellValueFactory(new PropertyValueFactory<>("tenMuaGiai"));
        roundCol1.setCellValueFactory(new PropertyValueFactory<>("tenVongDau"));
        homeTeamCol1.setCellValueFactory(new PropertyValueFactory<>("tenCLB1"));
        awayTeamCol1.setCellValueFactory(new PropertyValueFactory<>("tenCLB2"));
        dateCol1.setCellValueFactory(new PropertyValueFactory<>("ngayThiDau"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("gioThiDau"));
        stadiumCol.setCellValueFactory(new PropertyValueFactory<>("sanThiDau"));
        // Tải dữ liệu vào bảng
        try {
            List<Match> matches1 = service.getResultedMatchList();
            List<Match> matches2 = DAO_Match.getPendingMatchList();
            loadCompletedMatchData(matches1);
            loadFixtureData(matches2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        fixtureTable.setOnMouseClicked(event -> {
            if (!fixtureTable.getSelectionModel().isEmpty()) {
                resultTable.getSelectionModel().clearSelection();
            }
        });
        // Đảm bảo chỉ chọn một dòng trong hai bảng
        resultTable.setOnMouseClicked(event -> {
            if (!resultTable.getSelectionModel().isEmpty()) {
                fixtureTable.getSelectionModel().clearSelection();
            }
        });
        try {
            setCombobox();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCompletedMatchData(List<Match> matches) {
        // Chuyển đổi sang ObservableList và gán vào TableView
        ObservableList<Match> matchList = FXCollections.observableArrayList(matches);
        resultTable.setItems(matchList);
    }

    private void loadFixtureData(List<Match> matches) {
        // Chuyển đổi sang ObservableList và gán vào TableView
        ObservableList<Match> matchList = FXCollections.observableArrayList(matches);
        fixtureTable.setItems(matchList);
    }

    private void setCombobox() throws SQLException {

        DAO_MUAGIAI daoMG = new DAO_MUAGIAI();
        ArrayList<MODEL_MUAGIAI> ds1 = daoMG.selectAllDB();
        ArrayList<String> dsMG = new ArrayList<>();
        for (MODEL_MUAGIAI mg : ds1) {
            dsMG.add(mg.getTenMG());
        }
        compeFilter.getItems().addAll(dsMG);
        compeFilter.getSelectionModel().selectFirst();


        DAO_CLB daoClb = new DAO_CLB();
        ArrayList<MODEL_CLB> ds2 = daoClb.selectAllDB();
        ArrayList<String> dsCLB = new ArrayList<>();
        for (MODEL_CLB clb : ds2) {
            dsCLB.add(clb.getTenCLB());
        }
        clubFilter.getItems().addAll(dsCLB);
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void resetFilter() throws SQLException {
        Map<LocalDate, List<Match>> matchesByDate = service.getUpcomingMatchs();
        clubFilter.getSelectionModel().clearSelection();
        compeFilter.getSelectionModel().selectFirst();
        find();
    }


    @FXML
    public void find() {
        DAO_Match daoMatch = new DAO_Match();
        String compe = compeFilter.getValue();
        String clb = clubFilter.getValue();

        try {
            List<Match> matches1 = service.getResultedMatchList();
            List<Match> matches1F = new ArrayList<>();
            for (Match match : matches1) {
                if (Objects.equals(match.getTenMuaGiai(), compe)) {
                    if (clb != null) {
                        if (Objects.equals(match.getTenCLB1(), clb) ||
                                Objects.equals(match.getTenCLB2(), clb)) {
                            matches1F.add(match);
                        }
                    } else {
                        matches1F.add(match);
                    }
                }
            }
            List<Match> matches2 = DAO_Match.getPendingMatchList();
            List<Match> matches2F = new ArrayList<>();
            for (Match match : matches2) {
                if (Objects.equals(match.getTenMuaGiai(), compe)) {
                    if (clb != null) {
                        if (Objects.equals(match.getTenCLB1(), clb) ||
                                Objects.equals(match.getTenCLB2(), clb)) {
                            matches2F.add(match);
                        }
                    } else matches2F.add(match);
                }
            }
            loadCompletedMatchData(matches1F);
            loadFixtureData(matches2F);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    public void remove() {
        Match selectedMatch1 = resultTable.getSelectionModel().getSelectedItem();
        Match selectedMatch2 = fixtureTable.getSelectionModel().getSelectedItem();
        if (selectedMatch1 != null && selectedMatch2 != null) {
            if (selectedMatch1.getId() != selectedMatch2.getId()) {
                showErrorAlert("Please just select ONE match to remove.");
            }
        } else {
            Match selectedMatch = selectedMatch1 != null ? selectedMatch1 : selectedMatch2;
            if (selectedMatch != null) {
                //Xác nhận xóa
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận xóa");
                alert.setHeaderText("Bạn có chắc chắn muốn xóa kết quả trận đấu này không?");
                alert.setContentText("Kết quả trận đấu sẽ bị xóa vĩnh viễn.");
                ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(okButton, cancelButton);

                alert.showAndWait().ifPresent(response -> {
                    if (response == okButton) {
                        try {
                            // Xóa trận đấu
                            DAO_KQUATRANDAU daoKq = new DAO_KQUATRANDAU();
                            MODEL_KETQUATD model = new MODEL_KETQUATD(selectedMatch.getId(), selectedMatch.getScoreCLB1(), selectedMatch.getScoreCLB2());
                            int result = daoKq.deleteDB(model);
                            if (result == 1) {
                                // Tải lại dữ liệu với bộ lọc hiện tại
                                find();
                                // Thông báo thành công
                                showInfoAlert("Success", "Xóa trận đấu thành công!");
                            }
                        } catch (SQLException e) {
                            showErrorAlert("Vui lòng chọn 1 tận đu để xóa" + e.getMessage());
                        }
                    }
                });
            } else {
                showErrorAlert("Vui lòng chọn 1 tận đu để xóa");
            }
        }
    }

    @FXML
    public void update() {
        Match selectedMatch1 = resultTable.getSelectionModel().getSelectedItem();
        Match selectedMatch2 = fixtureTable.getSelectionModel().getSelectedItem();
        if (selectedMatch1 != null && selectedMatch2 != null) {
            if (selectedMatch1.getId() != selectedMatch2.getId()) {
                showErrorAlert("Please just select ONE match to update.");
            }
        } else {
            Match selectedMatch = selectedMatch1 != null ? selectedMatch1 : selectedMatch2;
            if (selectedMatch != null) {
                try {
                    // Load GoalFrame
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/GoalFrame.fxml"));
                    Parent root = loader.load();

                    // Get GoalManagementController1 and pass the idLabel value
                    GoalManagementController controller = loader.getController();
                    controller.setMatchId(selectedMatch.getId()); // Pass the match ID
                    controller.setResultManagementController(this); // Pass the current controller

                    // Open the GoalFrame
                    Stage stage = new Stage();
                    stage.setTitle("Goal Management");
                    stage.setScene(new Scene(root));
                    stage.initOwner(updateBtn.getScene().getWindow());
                    stage.show();
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                    showErrorAlert("Error loading GoalFrame: " + e.getMessage());
                }
            } else {
                showErrorAlert("Please select a match to update.");
            }
        }
    }

    public void save(int id,int score1,int score2) throws SQLException {
        MODEL_KETQUATD model = new MODEL_KETQUATD(id,score1,score2);

        DAO_KQUATRANDAU daoKq = new DAO_KQUATRANDAU();
        DAO_BXH_CLB daoBxhClb = new DAO_BXH_CLB();
        if (daoKq.selectByID(model.getMaTD()) == null) {
            daoKq.insertDB(model);

            showInfoAlert("Success", "Thêm kết quả thành công!");

        } else {
            daoKq.updateDB(model);
            showInfoAlert("Success", "Cập nhật kết quả thành công!");
        }
        // Cập nhật BXH
        service.updateRanking(model.getMaTD());
        find();
    }

}
