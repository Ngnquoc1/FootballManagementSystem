package Controller;

import Model.*;
import Service.*;
import Util.AlertUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class TableController {
    @FXML
    private VBox mainContainer;
    // Bộ lọc
    @FXML
    private ComboBox<String> compeFilter;
    @FXML
    private ComboBox<String> rankingTypeFilter;
    @FXML
    private Button refreshBtn;

    // TabPane và các Tab
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab clubTab;
    @FXML
    private Tab scorerTab;
    @FXML
    private Tab statisticsTab;


    // Thông tin giải đấu
    @FXML
    private Label competitionInfoLabel;
    @FXML
    private Label scorerCompetitionInfoLabel;

    @FXML private BarChart<String, Number> teamBarChart;
    @FXML private CategoryAxis teamAxis;
    @FXML private NumberAxis teamValueAxis;
    @FXML private RadioButton pointsRadio, goalsRadio, concededRadio, goalDiffRadio;
    @FXML private ToggleGroup teamStatsToggleGroup;
    // Bảng xếp hạng CLB
    @FXML
    private TableView<MODEL_BXH_CLB> clubTableView;
    @FXML
    private TableColumn<MODEL_BXH_CLB, Integer> clubRankColumn;
    @FXML
    private TableColumn<MODEL_BXH_CLB, String> clubNameColumn;
    @FXML
    private TableColumn<MODEL_BXH_CLB, Integer> clubPlayedColumn;
    @FXML
    private TableColumn<MODEL_BXH_CLB, Integer> clubWonColumn;
    @FXML
    private TableColumn<MODEL_BXH_CLB, Integer> clubDrawnColumn;
    @FXML
    private TableColumn<MODEL_BXH_CLB, Integer> clubLostColumn;
    @FXML
    private TableColumn<MODEL_BXH_CLB, Integer> clubGDColumn;
    @FXML
    private TableColumn<MODEL_BXH_CLB, Integer> clubPointsColumn;
    @FXML
    private HBox clubLegendBox;

    // Bảng xếp hạng Vua phá lưới
    @FXML
    private TableView<MODEL_BXH_BANTHANG> scorerTableView;
    @FXML
    private TableColumn<MODEL_BXH_BANTHANG, Integer> scorerRankColumn;
    @FXML
    private TableColumn<MODEL_BXH_BANTHANG, String> scorerNameColumn;
    @FXML
    private TableColumn<MODEL_BXH_BANTHANG, String> scorerClubColumn;
    @FXML
    private TableColumn<MODEL_BXH_BANTHANG, Integer> scorerGoalsColumn;
    @FXML
    private TableColumn<MODEL_BXH_BANTHANG, Integer> scorerPenaltyColumn;


    // Nút xuất báo cáo và đóng
    @FXML
    private Button exportButton;
    @FXML
    private Button closeButton;

    // Dữ liệu
    private final ObservableList<MODEL_BXH_CLB> vleagueClubRankings = FXCollections.observableArrayList();
    private final ObservableList<MODEL_BXH_BANTHANG> vleagueScorerRankings = FXCollections.observableArrayList();


    private Service service;
    private ExportService exportService;
    @FXML
    private ImageView userIcon;

    @FXML
    private void initialize() throws SQLException {
        service = new Service();
        exportService = new ExportService();
        configureUIBasedOnRole();
        // Thiết lập các ComboBox
        compeFilter.setItems(FXCollections.observableArrayList(
                service.getAllTournament().stream().map(MODEL_MUAGIAI::getTenMG).toList()));
        rankingTypeFilter.setItems(FXCollections.observableArrayList("BXH CLB", "Vua phá lưới","Thống kê"));

        // Thiết lập giá trị mặc định
        compeFilter.getSelectionModel().selectFirst();
        rankingTypeFilter.setValue("BXH CLB");

        // Thiết lập các cột cho bảng xếp hạng CLB
        setupClubTableColumns();

        // Thiết lập các cột cho bảng xếp hạng Vua phá lưới
        setupScorerTableColumns();

        initializeStatisticsTab();
        // Hiển thị dữ liệu ban đầu
        updateTableView();

        // Xử lý sự kiện khi thay đổi giải đấu hoặc loại BXH
        compeFilter.setOnAction(e -> handleFilterChange());
        rankingTypeFilter.setOnAction(e -> handleFilterChange());

        // Xử lý sự kiện khi chuyển tab
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == clubTab) {
                rankingTypeFilter.setValue("BXH CLB");
            } else if (newTab == scorerTab) {
                rankingTypeFilter.setValue("Vua phá lưới");
            } else if (newTab == statisticsTab) {
                rankingTypeFilter.setValue("Thống kê");
            }
            updateTableView();
        });

    }
    private void initializeStatisticsTab() {
        pointsRadio.setOnAction(event -> updateTeamBarChart("points"));
        goalsRadio.setOnAction(event -> updateTeamBarChart("goals"));
        concededRadio.setOnAction(event -> updateTeamBarChart("conceded"));
        goalDiffRadio.setOnAction(event -> updateTeamBarChart("goalDiff"));
        // Thiết lập RadioButton mặc định
        pointsRadio.setSelected(true);
        //
        teamAxis.setLabel("Đội bóng");
        teamValueAxis.setLabel("Điểm số");

    }
    private void updateTeamBarChart(String dataType) {
        try {
            // Xóa dữ liệu cũ
            teamBarChart.getData().clear();
            // Lấy dữ liệu từ bảng
            ObservableList<MODEL_BXH_CLB> teamStats = clubTableView.getItems();

            if (teamStats == null || teamStats.isEmpty()) {
                return;
            }

            // Tạo series mới
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            int tournamentId = service.getTournamentByName(compeFilter.getValue()).getMaMG();
            // Thiết lập tên series và nhãn trục y dựa trên loại dữ liệu
            switch (dataType) {
                case "points":
                    series.setName("Điểm số");
                    teamValueAxis.setLabel("Điểm số");
                    for (MODEL_BXH_CLB stat : teamStats) {
                        String teamName = service.getCLBByID(stat.getMaCLB()).getTenCLB();
                        series.getData().add(new XYChart.Data<>(teamName, stat.getDiem()));
                    }
                    break;
                case "goals":
                    series.setName("Bàn thắng");
                    teamValueAxis.setLabel("Số bàn thắng");
                    for (MODEL_BXH_CLB stat : teamStats) {
                        String teamName = service.getCLBByID(stat.getMaCLB()).getTenCLB();
                        int totalGoals=service.getTotalGoalsByCLB(stat.getMaCLB(), tournamentId);
                        series.getData().add(new XYChart.Data<>(teamName, totalGoals));
                    }
                    break;
                case "conceded":
                    series.setName("Bàn thua");
                    teamValueAxis.setLabel("Số bàn thua");
                    for (MODEL_BXH_CLB stat : teamStats) {
                        String teamName = service.getCLBByID(stat.getMaCLB()).getTenCLB();
                        int totalConceded=service.getTotalConcededByCLB(stat.getMaCLB(), tournamentId);
                        series.getData().add(new XYChart.Data<>(teamName, totalConceded));                    }
                    break;
                case "goalDiff":
                    series.setName("Hiệu số");
                    teamValueAxis.setLabel("Hiệu số bàn thắng/thua");
                    for (MODEL_BXH_CLB stat : teamStats) {
                        String teamName = service.getCLBByID(stat.getMaCLB()).getTenCLB();
                        series.getData().add(new XYChart.Data<>(teamName, stat.getHieuSo()));
                    }
                    break;
            }

            // Thêm series vào biểu đồ
            teamBarChart.getData().add(series);

        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể cập nhật biểu đồ đội bóng", e.getMessage());
        }
    }

    private void setupClubTableColumns() throws SQLException {
        String condition = "TenMG = '" + compeFilter.getValue() + "'";
        List<MODEL_MUAGIAI> musimList = service.getAllTournament();

        if (musimList == null || musimList.isEmpty()) {
            throw new RuntimeException("No matching season found for condition: " + condition);
        }

        int maMG = musimList.get(0).getMaMG();
        clubRankColumn.setCellValueFactory(new PropertyValueFactory<>("Hang"));
        clubNameColumn.setCellValueFactory(cellData -> {
            try {
                int maCLB = cellData.getValue().getMaCLB();
                String clbName = service.getCLBByID(maCLB).getTenCLB();
                return new SimpleStringProperty(clbName != null ? clbName : "Unknown");
            } catch (Exception e) {
                return new SimpleStringProperty("Error");
            }
        });
        clubPlayedColumn.setCellValueFactory(new PropertyValueFactory<>("SoTran"));
        clubWonColumn.setCellValueFactory(new PropertyValueFactory<>("Thang"));
        clubDrawnColumn.setCellValueFactory(new PropertyValueFactory<>("Hoa"));
        clubLostColumn.setCellValueFactory(new PropertyValueFactory<>("Thua"));
        clubGDColumn.setCellValueFactory(new PropertyValueFactory<>("HieuSo"));
        clubPointsColumn.setCellValueFactory(new PropertyValueFactory<>("Diem"));

        clubTableView.setItems(vleagueClubRankings);
        List<MODEL_BXH_CLB> modelList = service.getBxhCLBByTournamentId(maMG);
        vleagueClubRankings.addAll(modelList);
        // Thiết lập màu nền cho các hàng dựa trên thứ hạng
        clubTableView.setRowFactory(tv -> new TableRow<MODEL_BXH_CLB>() {
            @Override
            protected void updateItem(MODEL_BXH_CLB item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (item.getHang() <= 3) {
                        setStyle("-fx-background-color: rgba(76, 175, 80, 0.2);"); // AFC Champions League
                    } else if (item.getHang() <= 5) {
                        setStyle("-fx-background-color: rgba(33, 150, 243, 0.2);"); // AFC Cup
                    } else if (item.getHang() >= 13) {
                        setStyle("-fx-background-color: rgba(244, 67, 54, 0.2);"); // Xuống hạng
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void setupScorerTableColumns() throws SQLException {
        String condition = "TenMG = '" + compeFilter.getValue() + "'";
        int maMG = service.getTournamentByName(compeFilter.getValue()).getMaMG();

        scorerRankColumn.setCellValueFactory(new PropertyValueFactory<>("Hang"));
        scorerNameColumn.setCellValueFactory(cellData -> {
            try {
                int maCauThu = cellData.getValue().getMaCT();
                String playerName = service.getPlayerById(maCauThu).getTenCT();
                return new SimpleStringProperty(playerName != null ? playerName : "Unknown");
            } catch (Exception e) {
                return new SimpleStringProperty("Error");
            }
        });
        scorerClubColumn.setCellValueFactory(cellData -> {
            try {
                int maCT = cellData.getValue().getMaCT();
                String condition1 = "MaCT = " + maCT + " AND MaMG = " + maMG;
                int maCLB = service.getRegistedPlayersByCondition(condition1).get(0).getMaCLB();
                String clubName = service.getCLBByID(maCLB).getTenCLB();
                return new SimpleStringProperty(clubName != null ? clubName : "Unknown");
            } catch (Exception e) {
                return new SimpleStringProperty("Error");
            }
        });
        scorerGoalsColumn.setCellValueFactory(new PropertyValueFactory<>("SoBanThang"));
        scorerPenaltyColumn.setCellValueFactory(new PropertyValueFactory<>("Penalty"));

        scorerTableView.setItems(vleagueScorerRankings);
        List<MODEL_BXH_BANTHANG> modelLists = service.getBxhBanThangByTournamentId(maMG);
        vleagueScorerRankings.addAll(modelLists);
        // Thiết lập màu nền cho top 3 cầu thủ ghi bàn nhiều nhất
        scorerTableView.setRowFactory(tv -> new TableRow<MODEL_BXH_BANTHANG>() {
            @Override
            protected void updateItem(MODEL_BXH_BANTHANG item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if (item.getHang() == 1) {
                        setStyle("-fx-background-color: rgba(255, 215, 0, 0.2);"); // Gold
                    } else if (item.getHang() == 2) {
                        setStyle("-fx-background-color: rgba(192, 192, 192, 0.2);"); // Silver
                    } else if (item.getHang() == 3) {
                        setStyle("-fx-background-color: rgba(205, 127, 50, 0.2);"); // Bronze
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void handleFilterChange() {
        updateTableView();

    }

    private void updateTableView() {
        String competition = compeFilter.getValue();
        String rankingType = rankingTypeFilter.getValue();
        MODEL_MUAGIAI selectedTournament = service.getTournamentByName(competition);
        competitionInfoLabel.setText(competition);
        scorerCompetitionInfoLabel.setText(competition + " - Vua phá lưới");
        List<MODEL_BXH_CLB> clubParticipation = service.getBxhCLBByTournamentId(selectedTournament.getMaMG());
        vleagueClubRankings.clear();
        vleagueClubRankings.addAll(clubParticipation);
        List<MODEL_BXH_BANTHANG> scorerParticipation = service.getBxhBanThangByTournamentId(selectedTournament.getMaMG());
        vleagueScorerRankings.clear();
        vleagueScorerRankings.addAll(scorerParticipation);
        if ("BXH CLB".equals(rankingType)) {
            tabPane.getSelectionModel().select(clubTab);
            clubTableView.setItems(vleagueClubRankings);
            clubLegendBox.setVisible(true);
            exportButton.setVisible(true);
        } else if("Vua phá lưới".equals(rankingType)) {
            tabPane.getSelectionModel().select(scorerTab);
            scorerTableView.setItems(vleagueScorerRankings);
            exportButton.setVisible(true);
        } else if ("Thống kê".equals(rankingType)) {
            tabPane.getSelectionModel().select(statisticsTab);
            clubTableView.setItems(vleagueClubRankings);
            pointsRadio.setSelected(true);
            updateTeamBarChart("points");
            exportButton.setVisible(false);
        } else {
            AlertUtils.showError("Lỗi", "Chưa chọn loại bảng xếp hạng", "Vui lòng chọn loại bảng xếp hạng để hiển thị.");
        }
    }

    @FXML
    private void handleRefresh() {
        updateTableView();
        AlertUtils.showInformation("Làm mới dữ liệu", "", "Dữ liệu đã được làm mới!");
    }

    @FXML
    private void handleExport() {
        String rankingType = rankingTypeFilter.getValue();
        if ("BXH CLB".equals(rankingType)) {
            exportClubRankingsToExcel();
        } else if ("Vua phá lưới".equals(rankingType)) {
            exportScorerRankingsToExcel();
        } else {
            AlertUtils.showError("Lỗi xuất báo cáo", "", "Chưa chọn loại bảng xếp hạng để xuất!");
        }
    }

    private void exportScorerRankingsToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        File file = fileChooser.showSaveDialog(mainContainer.getScene().getWindow());

        if (file != null) {
            try {
                if (file.getName().endsWith(".xlsx")) {
                    exportService.exportScorerRankingsToExcel(vleagueScorerRankings, file,
                            compeFilter.getValue(), rankingTypeFilter.getValue());
                } else {
                    AlertUtils.showError("Lỗi xuất báo cáo", "", "Định dạng file không hợp lệ!");
                    return;
                }
            } catch (Exception e) {
                AlertUtils.showError("Lỗi xuất báo cáo", "", "Không thể xuất báo cáo: " + e.getMessage());
                return;
            }
            AlertUtils.showInformation("Xuất báo cáo", "",
                    "Đã xuất báo cáo thành công!\nFile: " + file.getAbsolutePath());
        }
    }

    private void exportClubRankingsToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        File file = fileChooser.showSaveDialog(mainContainer.getScene().getWindow());

        if (file != null) {
            try {
                if (file.getName().endsWith(".xlsx")) {
                    exportService.exportClubRankingsToExcel(vleagueClubRankings, file,
                            compeFilter.getValue(), rankingTypeFilter.getValue());
                }else {
                    AlertUtils.showError("Lỗi xuất báo cáo", "", "Định dạng file không hợp lệ!");
                    return;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                AlertUtils.showError("Lỗi xuất báo cáo", "", "Không thể xuất báo cáo: " + e.getMessage());
                return;
            }
            AlertUtils.showInformation("Xuất báo cáo", "",
                    "Đã xuất báo cáo thành công!\nFile: " + file.getAbsolutePath());
        }
    }


    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private void configureUIBasedOnRole() {
        Session session = Session.getInstance();
        int userRole = session.getRole();

        // Nếu role là "A", ẩn Registry và Rules buttons
        if (userRole == 5 || userRole == 3 || userRole == 2 || userRole == 1) {
            if (exportButton != null) {
                exportButton.setVisible(false);
                exportButton.setManaged(false); // Không chiếm không gian trong layout
            }
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
    
}