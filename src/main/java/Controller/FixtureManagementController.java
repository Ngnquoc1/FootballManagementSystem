package Controller;

import Model.*;
import Service.Service;
import Util.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FixtureManagementController implements Initializable {
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
    private FixtureController fixtureController = new FixtureController();
    public void setFixtureController(FixtureController controller) {
        this.fixtureController = controller;
    }
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
        List<Match> matches = service.getMatchViewByCondition("");
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

        List<MODEL_MUAGIAI> ds1 = service.getAllTournament();
        ArrayList<String> dsMG = new ArrayList<>();
        for (MODEL_MUAGIAI mg : ds1) {
            dsMG.add(mg.getTenMG());
        }
        compeFilter.getItems().addAll(dsMG);
        compeForm.getItems().addAll(dsMG);
        compeFilter.getSelectionModel().selectFirst();

        clubFilter.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            clubFilter.getItems().clear();
            if (compeFilter.getValue() != null) {
                MODEL_MUAGIAI mg = service.getTournamentByName(compeFilter.getValue());
                List<Integer> dsClbIds = service.getRegistedClubIdsByTournament(mg.getMaMG());
                for (Integer clbId : dsClbIds) {
                    MODEL_CLB clb = service.getCLBByID(clbId);
                    if (clb != null) {
                        clubFilter.getItems().add(clb.getTenCLB());
                    }
                }
            }
        });

        roundForm.addEventFilter( MouseEvent.MOUSE_CLICKED, event -> {;
            if (compeForm.getValue() != null) {
                MODEL_MUAGIAI mg= service.getTournamentByName(compeForm.getValue());
                List<MODEL_VONGDAU> ds2 = service.getAllRoundByTournament(mg.getMaMG());
                ArrayList<String> dsVD = new ArrayList<>();
                for (MODEL_VONGDAU vd : ds2) {
                    dsVD.add(vd.getTenVD());
                }
                roundForm.getItems().setAll(dsVD);
            }
        });
        clbForm1.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (compeForm.getValue() != null) {
                MODEL_MUAGIAI mg = service.getTournamentByName(compeForm.getValue());
                List<Integer> dsClbIds = service.getRegistedClubIdsByTournament(mg.getMaMG());
                for( Integer clbId : dsClbIds) {
                    MODEL_CLB clb = service.getCLBByID(clbId);
                    if (clb != null) {
                        clbForm1.getItems().add(clb.getTenCLB());
                    }
                }
            }
        });
        clbForm2.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (compeForm.getValue() != null) {
                MODEL_MUAGIAI mg = service.getTournamentByName(compeForm.getValue());
                List<Integer> dsClbIds = service.getRegistedClubIdsByTournament(mg.getMaMG());
                for( Integer clbId : dsClbIds) {
                    MODEL_CLB clb = service.getCLBByID(clbId);
                    if (clb != null && (clbForm1.getValue()!=null && !clb.getTenCLB().equals(clbForm1.getValue()))) {
                        clbForm2.getItems().add(clb.getTenCLB());
                    }
                }
            }
        });
        dateForm.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if(roundForm.getValue()!=null)
                {
                    MODEL_VONGDAU vd = service.getRoundByName(roundForm.getValue());
                    LocalDate startDate = vd.getNgayBD().toLocalDate();
                    LocalDate endDate = vd.getNgayKT().toLocalDate();
                    setDisable(empty || date.isBefore(startDate) || date.isAfter(endDate));
                }
            }
        } );
        staForm.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if(compeForm.getValue() != null) {
                MODEL_MUAGIAI mg = service.getTournamentByName(compeForm.getValue());
                List<Integer> dsClbIds = service.getRegistedClubIdsByTournament(mg.getMaMG());
                List<String> dsSanNames = new ArrayList<>();
                for (Integer clbId : dsClbIds) {
                    MODEL_CLB clb = service.getCLBByID(clbId);
                    if (clb != null) {

                        String stadiumName = service.getStadiumById(clb.getMaSan()).getTenSan();
                        if (stadiumName != null  && !dsSanNames.contains(stadiumName)) {
                            dsSanNames.add(stadiumName);
                        }
                    }
                }
                staForm.getItems().setAll(dsSanNames);
            }
        });
    }


    private Match collectFormData() {
        // Validate required fields
        if (compeForm.getValue() == null || compeForm.getValue().isEmpty()) {
            AlertUtils.showError("Error"," ", "Competition field is required.");
            return null;
        }
        if (clbForm1.getValue() == null || clbForm1.getValue().isEmpty()) {
            AlertUtils.showError("Error"," ", "Home team field is required.");
            return null;
        }
        if (clbForm2.getValue() == null || clbForm2.getValue().isEmpty()) {
            AlertUtils.showError("Error"," ", "Away team is required.");
            return null;
        }
        if (staForm.getValue() == null || staForm.getValue().isEmpty()) {
            AlertUtils.showError("Error"," ", "Stadium field is required.");
            return null;
        }
        if (dateForm.getValue() == null) {
            AlertUtils.showError("Error"," ", "Date field is required.");
            return null;
        }
        if (roundForm.getValue() == null || roundForm.getValue().isEmpty()) {
            AlertUtils.showError("Error"," ", "Round field is required.");
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
        String compe = compeFilter.getValue();
        String clb = clubFilter.getValue();

        String sql = "m.TenMG= '" + compe + "'";
        if (clb!=null) {
            sql += " and (c1.TenCLB='" + clb + "' or c2.TenCLB= '" + clb + "')";
        }
        List<Match> matches = service.getMatchViewByCondition(sql);
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
                        int result = service.deleteMatch(selectedMatch);
                        if (result == 1) {
                            // Tải lại dữ liệu với bộ lọc hiện tại
                            find();
                            // Reset form
                            resetForm();
                            // Thông báo thành công
                            AlertUtils.showInformation("Success", "Xóa trận đấu thành công", "Trận đấu đã được xóa thành công.");
                        }
                    } catch (SQLException e) {
                        AlertUtils.showError("Error", "Lỗi khi xóa trận đấu", "Không thể xóa trận đấu: " );
                    }
                }
            });
        } else {
            AlertUtils.showError("Error", "Chưa chọn trận đấu", "Vui lòng chọn một trận đấu để xóa.");
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
            AlertUtils.showError("Error", "Chưa chọn trận đấu", "Vui lòng chọn một trận đấu để cập nhật.");
        }
    }

    @FXML
    public void save() {
        try {
            // Lấy dữ liệu từ các trường nhập
            Match match = collectFormData();
            System.out.println(match);
            // Thuc hien update hoac insert
            if (service.selectByID(match.getId()) != null) {
                service.updateMatch(match);
                // Thông báo thành công
                AlertUtils.showInformation("Success","", "Cập nhật trận đấu thành công!");
            }
            // Nếu không có id thì thực hiện insert
            else {
                service.insertMatch(match);
                idLabel.setText(String.valueOf(match.getId()));

                AlertUtils.showInformation("Success","", "Thêm trận đấu thành công!");
            }

            // Cập nhật lại danh sách trận đấu
            List<Match> matches = service.getMatchViewByCondition("");
            loadFixtureData(matches);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertUtils.showError("Error", "Lỗi cơ sở dữ liệu", "Không thể thực hiện thao tác với cơ sở dữ liệu: ");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Error", "Lỗi dữ liệu", "Dữ liệu nhập không hợp lệ: " );
        }

    }

    @FXML
    public void cancel() {
        resetForm();
    }

    @FXML
    public void closeBtn(){
        Stage currentStage = (Stage) closeBtn.getScene().getWindow();

        try {
            fixtureController.resetFilter();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Đóng cửa sổ hiện tại
        currentStage.close();

    }
}
