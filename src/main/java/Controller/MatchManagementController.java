package Controller;

import Model.*;
import Service.Service;
import DAO.DAO_CLB;
import DAO.DAO_MUAGIAI;
import DAO.DAO_Match;
import DAO.DAO_SAN;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchManagementController implements Initializable {
    @FXML
    private TableView<Match> fixtureTable;
    @FXML
    private TableColumn<Match, String> idCol, leagueCol,roundCol, homeTeamCol, awayTeamCol, dateCol, timeCol, stadiumCol;
    @FXML
    private Spinner<Integer> hourSpinner, minSpinner;
    @FXML
    private Button addBtn, findBtn, removeBtn, updateBtn, saveBtn, cancelBtn, resetBtn,closeBtn;
    @FXML
    private ComboBox<String> compeFilter, clubFilter, compeForm, clbForm1, clbForm2, staForm,roundForm;
    @FXML
    private Label idLabel;
    @FXML
    private DatePicker dateForm;
    private Service service = new Service();
    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        // Ánh xạ các cột với thuộc tính của lớp Match
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        leagueCol.setCellValueFactory(new PropertyValueFactory<>("tenMuaGiai"));
        roundCol.setCellValueFactory(new PropertyValueFactory<>("tenVongDau"));
        homeTeamCol.setCellValueFactory(new PropertyValueFactory<>("tenCLB1"));
        awayTeamCol.setCellValueFactory(new PropertyValueFactory<>("tenCLB2"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("ngayThiDau"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("gioThiDau"));
        stadiumCol.setCellValueFactory(new PropertyValueFactory<>("sanThiDau"));

        // Tải dữ liệu vào bảng
        List<Match> matches = new DAO_Match().selectByCondition("");
        loadFixtureData(matches);
        try {
            setCombobox();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        minSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
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

        DAO_SAN daoSan = new DAO_SAN();
        ArrayList<MODEL_SAN> ds3 = daoSan.selectAllDB();
        ArrayList<String> dsSan = new ArrayList<>();
        for (MODEL_SAN san : ds3) {
            dsSan.add(san.getTenSan());
        }
        roundForm.getItems().addAll("Lượt đi", "Lượt về");
        compeForm.getItems().addAll(dsMG);
        clbForm1.getItems().addAll(dsCLB);
        clbForm2.getItems().addAll(dsCLB);
        clubFilter.getItems().addAll(dsCLB);
        staForm.getItems().addAll(dsSan);
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
        if (compeForm.getValue() == null || compeForm.getValue().isEmpty()) {
            showErrorAlert("Competition field is required.");
            return null;
        }
        if (clbForm1.getValue() == null || clbForm1.getValue().isEmpty()) {
            showErrorAlert("Home team field is required.");
            return null;
        }
        if (clbForm2.getValue() == null || clbForm2.getValue().isEmpty()) {
            showErrorAlert("Away team field is required.");
            return null;
        }
        if (staForm.getValue() == null || staForm.getValue().isEmpty()) {
            showErrorAlert("Stadium field is required.");
            return null;
        }
        if (dateForm.getValue() == null) {
            showErrorAlert("Date field is required.");
            return null;
        }
        if (roundForm.getValue() == null || roundForm.getValue().isEmpty()) {
            showErrorAlert("Round field is required.");
            return null;
        }
        String tenMuaGiai = compeForm.getValue();
        String tenVongDau = roundForm.getValue();
        String tenCLB1 = clbForm1.getValue();
        String tenCLB2 = clbForm2.getValue();
        String tenSan = staForm.getValue();
        LocalDate date = dateForm.getValue();
        int hour = hourSpinner.getValue();
        int minute = minSpinner.getValue();
        LocalTime time = LocalTime.of(hour, minute);
        int id = idLabel.getText().isEmpty() ? 0 : Integer.parseInt(idLabel.getText());
        return new Match(id, tenMuaGiai,tenVongDau, tenCLB1, tenCLB2, time, date, tenSan, null, null, null, null);
    }

    @FXML
    private void resetFilter() throws SQLException {
        Map<LocalDate, List<Match>> matchesByDate = service.getUpcomingMatchs();
        clubFilter.getSelectionModel().clearSelection();
        compeFilter.getSelectionModel().selectFirst();
        find();
    }
    @FXML
    public void resetForm() {
        idLabel.setText("");
        compeForm.getSelectionModel().clearSelection();
        roundForm.getSelectionModel().clearSelection();
        clbForm1.getSelectionModel().clearSelection();
        clbForm2.getSelectionModel().clearSelection();
        staForm.getSelectionModel().clearSelection();
        dateForm.setValue(null);
        hourSpinner.getValueFactory().setValue(0);
        minSpinner.getValueFactory().setValue(0);
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
        List<Match> matches = daoMatch.selectByCondition(sql);
        loadFixtureData(matches);
    }

    @FXML
    public void add() {
        resetForm();
    }

    @FXML
    public void remove() {
        Match selectedMatch = fixtureTable.getSelectionModel().getSelectedItem();
        if (selectedMatch != null) {
            //Xác nhận xóa
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText("Bạn có chắc chắn muốn xóa trận đấu này không?");
            alert.setContentText("Trận đấu sẽ bị xóa vĩnh viễn.");
            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(okButton, cancelButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == okButton) {
                    try {
                        // Xóa trận đấu
                        DAO_Match daoMatch = new DAO_Match();
                        int result = daoMatch.deleteDB(selectedMatch);
                        if (result == 1) {
                            // Tải lại dữ liệu với bộ lọc hiện tại
                            find();
                            // Reset form
                            resetForm();
                            // Thông báo thành công
                            showInfoAlert("Success", "Xóa trận đấu thành công!");
                        }
                    } catch (SQLException e) {
                        showErrorAlert("Vui lòng chọn 1 tận đu để xóa"+ e.getMessage());
                    }
                }
            });
        } else {
            showErrorAlert("Vui lòng chọn 1 tận đu để xóa");
        }
    }

    @FXML
    public void update() {
        Match selectedMatch = fixtureTable.getSelectionModel().getSelectedItem();
        if (selectedMatch != null) {
            String id = String.valueOf(selectedMatch.getId());
            String tenMG = selectedMatch.getTenMuaGiai();
            String tenCLB1 = selectedMatch.getTenCLB1();
            String tenCLB2 = selectedMatch.getTenCLB2();
            String sanThiDau = selectedMatch.getSanThiDau();
            String tenVD = selectedMatch.getTenVongDau();

            LocalDate date = selectedMatch.getNgayThiDau();
            LocalTime time = selectedMatch.getGioThiDau();

            System.out.println(tenVD+" "+tenMG);
            idLabel.setText(id);
            compeForm.getSelectionModel().select(tenMG);
            clbForm1.getSelectionModel().select(tenCLB1);
            clbForm2.getSelectionModel().select(tenCLB2);
            roundForm.getSelectionModel().select(tenVD);
            staForm.getSelectionModel().select(sanThiDau);
            dateForm.setValue(date);
            hourSpinner.getValueFactory().setValue(time.getHour());
            minSpinner.getValueFactory().setValue(time.getMinute());

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No row is selected ");
            alert.showAndWait();
        }
    }

    @FXML
    public void save() {
        try {
            // Lấy dữ liệu từ các trường nhập
            Match match = collectFormData();

            // Thuc hien update hoac insert
            DAO_Match daoMatch = new DAO_Match();
            if (daoMatch.selectByID(match.getId()) != null) {
                daoMatch.updateDB(match);
                // Thông báo thành công
                showInfoAlert("Success", "Cập nhật trận đấu thành công!");
            }
            // Nếu không có id thì thực hiện insert
            else {
                daoMatch.insertDB(match);
                idLabel.setText(String.valueOf(match.getId()));

                showInfoAlert("Success", "Thêm trận đấu thành công!");
            }

            // Cập nhật lại danh sách trận đấu
            List<Match> matches = daoMatch.selectByCondition("");
            loadFixtureData(matches);

        } catch (SQLException e) {
            showErrorAlert("Lỗi khi thực hiện thao tác với cơ sở dữ liệu: " + e.getMessage());
        } catch (Exception e) {
            showErrorAlert("Dữ liệu nhập không hợp lệ: " + e.getMessage());
        }

    }

    @FXML
    public void cancel() {
        resetForm();
    }

    @FXML
    public void closeBtn(){
        // Đóng cửa sổ hiện tại
        if (closeBtn.getScene().getWindow() != null) {
            closeBtn.getScene().getWindow().hide();
        }
    }
}
