package Controller;

import Controller.DAO.DAO_CLB;
import Controller.DAO.DAO_MUAGIAI;
import Controller.DAO.DAO_Match;
import Controller.DAO.DAO_SAN;
import Model.MODEL_CLB;
import Model.MODEL_MUAGIAI;
import Model.MODEL_SAN;
import Model.Match;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ResultManagementController implements Initializable {
    @FXML
    private TableView<Match> fixtureTable,resultTable;
    @FXML
    private TableColumn<Match, String> idCol, leagueCol,roundCol, homeTeamCol, awayTeamCol, idCol1, leagueCol1,roundCol1, homeTeamCol1, awayTeamCol1,dateCol,dateCol1, timeCol,stadiumCol,scoreCol1, scoreCol2;
    @FXML
    private Button findBtn, removeBtn, updateBtn, saveBtn, cancelBtn, resetBtn;
    @FXML
    private ComboBox<String> compeFilter, clubFilter;
    @FXML
    private Label idLabel;
    @FXML
    private TextField score1Form,score2Form;


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
            List<Match> matches1 = DAO_Match.getResultedMatchList();
            List<Match> matches2 = DAO_Match.getPendingMatchList();
            loadCompletedMatchData(matches1);
            loadFixtureData(matches2);
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
    private void setCombobox() {

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

        DAO_SAN daoSan = new DAO_SAN();
        ArrayList<MODEL_SAN> ds3 = daoSan.selectAllDB();
        ArrayList<String> dsSan = new ArrayList<>();
        for (MODEL_SAN san : ds3) {
            dsSan.add(san.getTenSan());
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
    private void showInfoAlert(String title,String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private Match collectFormData() {
        // Validate required fields
        if (score1Form.getText() == null || score1Form.getText().isEmpty()) {
            showErrorAlert("Score1(Home team) field is required.");
            return null;
        }
        if (score2Form.getText() == null || score2Form.getText().isEmpty()) {
            showErrorAlert("Score2(Away team) field is required.");
            return null;
        }
        return null;
    }
    @FXML
    private void resetFilter() throws SQLException {
        Map<LocalDate, List<Match>> matchesByDate = new DAO_Match().getUpcomingMatchs();
        clubFilter.getSelectionModel().clearSelection();
        compeFilter.getSelectionModel().selectFirst();
        find();
    }
    @FXML
    public void resetForm() {
        idLabel.setText("");
        score1Form.setText("");
        score2Form.setText("");
    }

    @FXML
    public void find() {
        DAO_Match daoMatch = new DAO_Match();
        String compe = compeFilter.getValue();
        String clb = clubFilter.getValue();

        String sql = "m.TenMG= '" + compe + "'";
        if (clb!=null) {
            sql += " and (c1.TenCLB='" + clb + "' or c2.TenCLB= '" + clb + "')";
        }
        try {
            List<Match> matches1 = DAO_Match.getResultedMatchList();
            loadCompletedMatchData(matches1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    public void remove() {}

    @FXML
    public void update() {}

    @FXML
    public void save() {}

    @FXML
    public void cancel() {
        resetForm();
    }

}
