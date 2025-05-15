package Controller;

import DAO.*;
import Service.Service;
import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GoalManagementController {

    @FXML private Label matchInfoLabel;
    @FXML private Label scoreLabel;

    @FXML private TableView<MODEL_BANTHANG> goalsTableView;
    @FXML private TableColumn<MODEL_BANTHANG, Integer> timeColumn;
    @FXML private TableColumn<MODEL_BANTHANG, String> playerColumn;
    @FXML private TableColumn<MODEL_BANTHANG, String> teamColumn;
    @FXML private TableColumn<MODEL_BANTHANG, String> typeColumn;

    @FXML private TextField timeField;
    @FXML private ComboBox<String> goalTypeComboBox;
    @FXML private ComboBox<String> teamComboBox;
    @FXML private ComboBox<String> playerComboBox;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button finishButton;

    private ObservableList<MODEL_BANTHANG> goalsList = FXCollections.observableArrayList();
    private MODEL_BANTHANG currentGoal;
    private boolean isEditing = false;

    private Service service = new Service();
    private DAO_BANTHANG daoBanthang = new DAO_BANTHANG();
    private DAO_TRANDAU daoTrandau = new DAO_TRANDAU();
    private DAO_CAUTHU daoCauthu = new DAO_CAUTHU();
    private DAO_CLB daoClb = new DAO_CLB();
    private DAO_CAUTHU_CLB daoCauthuClb = new DAO_CAUTHU_CLB();
    private DAO_LOAIBT daoLoaiBT = new DAO_LOAIBT();
    private MODEL_TRANDAU currentMatch;
    private Map<String, Integer> teamToClbIdMap = new HashMap<>();
    private Map<String, Integer> playerToCauthuIdMap = new HashMap<>();
    private Map<Integer, String> cauthuIdToNameMap = new HashMap<>();
    private int maTD; // Mã trận đấu được truyền vào

    private ResultManagementController resultManagementController;

    public void setResultManagementController(ResultManagementController controller) {
        this.resultManagementController = controller;
    }

    public void setMatchId(int matchId) throws SQLException {
        this.maTD = matchId;
        // Tải thông tin trận đấu
        loadMatchInfo();

        // Tải danh sách bàn thắng
        loadGoals();
    }


    @FXML
    private void initialize() throws SQLException {
        // Thiết lập các cột cho TableView
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("phutGhiBan"));
        playerColumn.setCellValueFactory(cellData -> {
            try {
                int maCT = cellData.getValue().getMaCT();
                String playerName = cauthuIdToNameMap.get(maCT);
                return new javafx.beans.property.SimpleStringProperty(playerName != null ? playerName : "Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
        teamColumn.setCellValueFactory(cellData -> {
            try {
                int maCT = cellData.getValue().getMaCT();
                int maTD = cellData.getValue().getMaTD();
                int maCLB = service.selectCLBIDFromGoal(maCT, maTD);
                MODEL_CLB clb = daoClb.selectByID(maCLB);
                return new javafx.beans.property.SimpleStringProperty(clb != null ? clb.getTenCLB() : "Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
        typeColumn.setCellValueFactory(cellData -> {
            try {
                int maLoaiBT = cellData.getValue().getmaLoaiBT();
                MODEL_LOAIBANTHANG loaiBT=daoLoaiBT.selectByID(maLoaiBT);
                return new javafx.beans.property.SimpleStringProperty(loaiBT != null ? loaiBT.getTenLoaiBT() : "Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });

        // Gán danh sách bàn thắng cho TableView
        goalsTableView.setItems(goalsList);

        // Thiết lập dữ liệu cho các ComboBox
        goalTypeComboBox.setItems(FXCollections.observableArrayList(
                "Bàn thắng thường", "Phạt đền", "Đá phạt", "Phản lưới nhà"
        ));



        // Xử lý sự kiện khi chọn đội
        teamComboBox.setOnAction(e -> {
            try {
                updatePlayersList();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Thiết lập trạng thái ban đầu của form
        resetForm();

        // Xử lý sự kiện khi chọn một bàn thắng trong bảng
        goalsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                editButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                editButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
    }

    private void loadMatchInfo() throws SQLException {
        currentMatch = daoTrandau.selectByID(maTD);
        if (currentMatch != null) {
            MODEL_CLB clb1 = daoClb.selectByID(currentMatch.getMaCLB1());
            MODEL_CLB clb2 = daoClb.selectByID(currentMatch.getMaCLB2());
            if (clb1 != null && clb2 != null) {
                matchInfoLabel.setText(clb1.getTenCLB() + " vs " + clb2.getTenCLB());
                teamComboBox.setItems(FXCollections.observableArrayList(clb1.getTenCLB(), clb2.getTenCLB()));
                teamToClbIdMap.put(clb1.getTenCLB(), clb1.getMaCLB());
                teamToClbIdMap.put(clb2.getTenCLB(), clb2.getMaCLB());
            }
        }
    }

    private void loadGoals() throws SQLException {
        List<MODEL_BANTHANG> goals = daoBanthang.selectByCondition("MaTD = " + maTD);
        goalsList.clear();
        goalsList.addAll(goals);

        // Tải danh sách cầu thủ để hiển thị
        for (MODEL_BANTHANG goal : goals) {
            MODEL_CAUTHU cauthu = daoCauthu.selectByID(goal.getMaCT());
            if (cauthu != null) {
                cauthuIdToNameMap.put(cauthu.getMaCT(), cauthu.getTenCT());
            }
        }
        updateScore();
    }

    private void updatePlayersList() throws SQLException {
        String selectedTeam = teamComboBox.getValue();
        if (selectedTeam == null) return;

        Integer maCLB = teamToClbIdMap.get(selectedTeam);
        if (maCLB != null) {
            List<MODEL_CAUTHUTHAMGIACLB> playersInTeam = daoCauthuClb.selectByCondition("MaCLB = " + maCLB);
            List<MODEL_CAUTHU> players=new ArrayList<>();
            if (playersInTeam.isEmpty()) {
                playerComboBox.setItems(FXCollections.observableArrayList());
                return;
            }
            else {
                // Lấy danh sách cầu thủ từ đội bóng
                playerComboBox.setItems(FXCollections.observableArrayList(
                        playersInTeam.stream().map(player -> {
                            try {
                                players.add(daoCauthu.selectByID(player.getMaCT()));
                                return daoCauthu.selectByID(player.getMaCT()).getTenCT();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }).collect(Collectors.toList())
                ));
            }
            // Cập nhật map để ánh xạ tên cầu thủ với MaCT
            playerToCauthuIdMap.clear();
            for (MODEL_CAUTHU player : players) {
                playerToCauthuIdMap.put(player.getTenCT(), player.getMaCT());
                cauthuIdToNameMap.put(player.getMaCT(), player.getTenCT());
            }
        }
    }

    private void updateScore() throws SQLException {
        int homeGoals = 0;
        int awayGoals = 0;

        MODEL_CLB clb1 = daoClb.selectByID(currentMatch.getMaCLB1());
        MODEL_CLB clb2 = daoClb.selectByID(currentMatch.getMaCLB2());
        String homeTeam = clb1 != null ? clb1.getTenCLB() : "";
        String awayTeam = clb2 != null ? clb2.getTenCLB() : "";

        for (MODEL_BANTHANG goal : goalsList) {
            MODEL_CAUTHU cauthu = daoCauthu.selectByID(goal.getMaCT());
            MODEL_LOAIBANTHANG loaiBT = daoLoaiBT.selectByID(goal.getmaLoaiBT());
            if (cauthu != null) {
                int maCLB = service.selectCLBIDFromGoal(cauthu.getMaCT(), goal.getMaTD());
                MODEL_CLB clb = daoClb.selectByID(maCLB);
                String teamName = clb != null ? clb.getTenCLB() : "";
                boolean isOwnGoal = "Phản lưới nhà".equals(loaiBT.getTenLoaiBT());

                if (teamName.equals(homeTeam)) {
                    if (!isOwnGoal) homeGoals++; else awayGoals++;
                } else if (teamName.equals(awayTeam)) {
                    if (!isOwnGoal) awayGoals++; else homeGoals++;
                }
            }
        }
        scoreLabel.setText(homeGoals + " - " + awayGoals);
    }

    @FXML
    private void handleAddGoal() {
        resetForm();
        isEditing = false;
        enableForm(true);
    }

    @FXML
    private void handleEditGoal() throws SQLException {
        MODEL_BANTHANG selectedGoal = goalsTableView.getSelectionModel().getSelectedItem();
        if (selectedGoal != null) {
            currentGoal = selectedGoal;
            isEditing = true;

            // Điền thông tin vào form
            timeField.setText(String.valueOf(selectedGoal.getPhutGhiBan()));
            String tenLBT = daoLoaiBT.selectByID(selectedGoal.getmaLoaiBT()).getTenLoaiBT();
            goalTypeComboBox.setValue(tenLBT);

            MODEL_CAUTHU cauthu = daoCauthu.selectByID(selectedGoal.getMaCT());
            if (cauthu != null) {
                int maCLB = service.selectCLBIDFromGoal(cauthu.getMaCT(), selectedGoal.getMaTD());
                MODEL_CLB clb = daoClb.selectByID(maCLB);
                if (clb != null) {
                    teamComboBox.setValue(clb.getTenCLB());
                    updatePlayersList();
                    playerComboBox.setValue(cauthu.getTenCT());
                }
            }

            enableForm(true);
        }
    }

    @FXML
    private void handleDeleteGoal() {
        MODEL_BANTHANG selectedGoal = goalsTableView.getSelectionModel().getSelectedItem();
        if (selectedGoal != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText(null);
            alert.setContentText("Bạn có chắc chắn muốn xóa bàn thắng này?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        daoBanthang.deleteDB(selectedGoal);
                        goalsList.remove(selectedGoal);
                        updateScore();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showErrorAlert("Lỗi khi xóa bàn thắng: " + e.getMessage());
                    }
                }
            });
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            showErrorAlert("Vui lòng điền đầy đủ thông tin bàn thắng!");
            return;
        }

        try {
            int phutGhiBan = Integer.parseInt(timeField.getText());
            String loaiBT = goalTypeComboBox.getValue();
            String team = teamComboBox.getValue();
            String player = playerComboBox.getValue();
            int maCT = playerToCauthuIdMap.get(player);

            MODEL_BANTHANG goal = new MODEL_BANTHANG();
            ArrayList<MODEL_LOAIBANTHANG> loaiBTModel = daoLoaiBT.selectByCondition("TenLoaiBT='" + loaiBT + "'");
            int maLoaiBT = loaiBTModel.getFirst().getMaLoaiBT();
            goal.setMaCT(maCT);
            goal.setMaTD(maTD);
            goal.setPhutGhiBan(phutGhiBan);
            goal.setmaLoaiBT(maLoaiBT);

            if (isEditing && currentGoal != null) {
                // Cập nhật bàn thắng
                goal.setMaBT(currentGoal.getMaBT());
                daoBanthang.updateDB(goal);
                goalsList.set(goalsList.indexOf(currentGoal), goal);
            } else {
                // Thêm bàn thắng mới
                daoBanthang.insertDB(goal);
                goalsList.add(goal);

                // Cập nhật MaBT sau khi thêm (cần truy vấn lại vì MaBT tự tăng)
                List<MODEL_BANTHANG> updatedGoals = daoBanthang.selectByCondition(
                        "MaTD = " + maTD + " AND MaCT = " + maCT + " AND phutGhiBan = " + phutGhiBan
                );
                if (!updatedGoals.isEmpty()) {
                    goal.setMaBT(updatedGoals.getFirst().getMaBT());
                }
            }

            // Cập nhật tỉ số
            updateScore();

            // Reset form
            resetForm();
            enableForm(false);
        } catch (NumberFormatException e) {
            showErrorAlert("Phút ghi bàn phải là một số nguyên!");
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Lỗi khi lưu bàn thắng: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        resetForm();
        enableForm(false);
    }

    @FXML
    private void handleFinish() {

        try {
            // Parse the score from the scoreLabel
            String[] scores = scoreLabel.getText().split(" - ");
            int score1 = Integer.parseInt(scores[0].trim());
            int score2 = Integer.parseInt(scores[1].trim());

            // Call the save method in ResultManagementController
            if (resultManagementController != null) {
                resultManagementController.save(maTD, score1, score2);
            }

            // Close the current stage
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hoàn thành");
            alert.setHeaderText(null);
            alert.setContentText("Đã lưu thông tin bàn thắng thành công!");
            alert.showAndWait();

            Stage stage = (Stage) finishButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showErrorAlert("Lỗi khi lưu thông tin: " + e.getMessage());
        }

    }

    private boolean validateForm() {
        String timeText = timeField.getText();
        if (timeText == null || timeText.isEmpty()) return false;

        try {
            Integer.parseInt(timeText);
        } catch (NumberFormatException e) {
            return false;
        }

        return goalTypeComboBox.getValue() != null &&
                teamComboBox.getValue() != null &&
                playerComboBox.getValue() != null;
    }

    private void resetForm() {
        timeField.clear();
        goalTypeComboBox.setValue(null);
        teamComboBox.setValue(null);
        playerComboBox.setValue(null);
        currentGoal = null;
    }

    private void enableForm(boolean enable) {
        timeField.setDisable(!enable);
        goalTypeComboBox.setDisable(!enable);
        teamComboBox.setDisable(!enable);
        playerComboBox.setDisable(!enable);
        saveButton.setDisable(!enable);
        cancelButton.setDisable(!enable);
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}