package Controller;

import Model.*;
import Service.Service;
import Util.AlertUtils;
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
            List<Match> matches2 = service.getPendingMatchList();
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

        List<MODEL_MUAGIAI> ds1 = service.getAllTournament();
        ArrayList<String> dsMG = new ArrayList<>();
        for (MODEL_MUAGIAI mg : ds1) {
            dsMG.add(mg.getTenMG());
        }
        compeFilter.getItems().addAll(dsMG);
        compeFilter.getSelectionModel().selectFirst();

        if(compeFilter.getValue()==null){
            List<MODEL_CLB> ds2 = service.getAllClubs();
            ArrayList<String> dsCLB = new ArrayList<>();
            for (MODEL_CLB clb : ds2) {
                dsCLB.add(clb.getTenCLB());
            }
            clubFilter.getItems().addAll(dsCLB);
        }
        else {
            MODEL_MUAGIAI mg=service.getTournamentByName(compeFilter.getValue());
            List<Integer> ds2 = service.getRegistedClubIdsByTournament(mg.getMaMG());
            ArrayList<String> dsCLB = new ArrayList<>();
            for(Integer id : ds2) {
                MODEL_CLB clb = service.getCLBByID(id);
                if (clb != null) {
                    dsCLB.add(clb.getTenCLB());
                }
            }
            clubFilter.getItems().addAll(dsCLB);
        }

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
            List<Match> matches2 = service.getPendingMatchList();
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
                AlertUtils.showError("Error","", "Please just select ONE match to remove.");
            }
        } else {
            Match selectedMatch = selectedMatch1 != null ? selectedMatch1 : selectedMatch2;
            if (selectedMatch != null) {
                boolean response=AlertUtils.showConfirmation("Xác nhận xóa","Bạn có chắc chắn muốn xóa kết quả trận đấu này không?",
                                        "Bạn có chắc chắn muốn xóa trận đấu này?");
                if (response) {
                    try {
                        // Xóa trận đấu
                        MODEL_KETQUATD model = new MODEL_KETQUATD(selectedMatch.getId(), selectedMatch.getScoreCLB1(), selectedMatch.getScoreCLB2());
                        int result = service.deleteResult(model);
                        if (result == 1) {
                            // Tải lại dữ liệu với bộ lọc hiện tại
                            find();
                            // Thông báo thành công
                            AlertUtils.showInformation("Success","", "Xóa trận đấu thành công!");
                        }
                    } catch (SQLException e) {
                        AlertUtils.showError("Error","", "Vui lòng chọn 1 tận đu để xóa");
                    }
                }
            } else {
                AlertUtils.showError("Error","", "Vui lòng chọn 1 trận đấu để xóa");
            }
        }
    }

    @FXML
    public void update() {
        Match selectedMatch1 = resultTable.getSelectionModel().getSelectedItem();
        Match selectedMatch2 = fixtureTable.getSelectionModel().getSelectedItem();
        if (selectedMatch1 != null && selectedMatch2 != null) {
            if (selectedMatch1.getId() != selectedMatch2.getId()) {
                AlertUtils.showError("Error","", "Please just select ONE match to update.");
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
                    AlertUtils.showError("Error","", "Error loading GoalFrame: ");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                AlertUtils.showError("Error","", "Please select a match to update.");
            }
        }
    }

    public void save(int id,int score1,int score2) throws SQLException {
        MODEL_KETQUATD model = new MODEL_KETQUATD(id,score1,score2);
        System.out.println(model.getMaTD());
        if (service.getResultByID(model.getMaTD()) == null) {
            try {
                service.insertResult(model);
                AlertUtils.showInformation("Success", "", "Thêm kết quả thành công!");
            }
            catch (SQLException e) {
                AlertUtils.showError("Error","", "Lỗi khi thêm kết quả: " + e.getMessage());
            }
        } else {
            try {
                service.updateResult(model);
            } catch (SQLException e) {
                AlertUtils.showError("Error","", "Lỗi khi cập nhật kết quả: " + e.getMessage());
            }
        }
        find();
    }

}
