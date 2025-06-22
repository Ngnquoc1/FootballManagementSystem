package Controller;

import Model.*;
import Util.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;
import Service.*;
public class WeeklyStatisticsController implements Initializable {

    // Comboboxes
    @FXML private ComboBox<String> tournamentComboBox;
    @FXML private ComboBox<String> periodFilterComboBox;
    @FXML private ComboBox<String> period1ComboBox;
    @FXML private ComboBox<String> period2ComboBox;

    // Toggle groups and radio buttons
    @FXML private ToggleGroup viewModeToggleGroup;
    @FXML private RadioButton weeklyRadio;
    @FXML private RadioButton roundRadio;

    @FXML private ToggleGroup trendToggleGroup;
    @FXML private RadioButton matchesTrendRadio;
    @FXML private RadioButton goalsTrendRadio;

    // Labels for overview
    @FXML private Label totalMatchesLabel;
    @FXML private Label totalGoalsLabel;
    @FXML private Label avgGoalsPerMatchLabel;

    // Charts
    @FXML private PieChart resultDistributionPieChart;
    @FXML private LineChart<String, Number> trendLineChart;
    @FXML private CategoryAxis timeAxis;
    @FXML private NumberAxis trendValueAxis;

    @FXML private BarChart<String, Number> comparisonBarChart;
    @FXML private CategoryAxis comparisonTimeAxis;
    @FXML private NumberAxis comparisonValueAxis;

    @FXML private AreaChart<String, Number> goalEfficiencyAreaChart;
    @FXML private CategoryAxis efficiencyTimeAxis;
    @FXML private NumberAxis efficiencyGoalAxis;


    // Tables
    @FXML private TableView<WeeklyStatistics> detailedStatsTable;
    @FXML private TableColumn<WeeklyStatistics, String> periodColumn;
    @FXML private TableColumn<WeeklyStatistics, Integer> matchCountColumn;
    @FXML private TableColumn<WeeklyStatistics, Integer> goalCountColumn;
    @FXML private TableColumn<WeeklyStatistics, Double> avgGoalsColumn;

    @FXML private TableView<PerformanceRanking> performanceRankingTable;
    @FXML private TableColumn<PerformanceRanking, String> rankPeriodColumn;
    @FXML private TableColumn<PerformanceRanking, Integer> rankPositionColumn;
    @FXML private TableColumn<PerformanceRanking, Double> rankScoreColumn;
    @FXML private TableColumn<PerformanceRanking, Double> rankGoalRatioColumn;
    @FXML private TableColumn<PerformanceRanking, String> rankNotesColumn;

    @FXML private TableView<ComparisonData> comparisonTable;
    @FXML private TableColumn<ComparisonData, String> metricColumn;
    @FXML private TableColumn<ComparisonData, String> period1ValueColumn;
    @FXML private TableColumn<ComparisonData, String> period2ValueColumn;
    @FXML private TableColumn<ComparisonData, String> differenceColumn;
    @FXML private TableColumn<ComparisonData, String> percentageColumn;

    // Other controls
    @FXML private Label radarChartPlaceholder;
    @FXML private TextArea analysisTextArea;

    // Buttons
    @FXML private Button refreshButton;
    @FXML private Button exportDetailedButton;
    @FXML private Button compareButton;
    @FXML private Button exportComparisonButton;
    @FXML private Button exportSummaryButton;
    @FXML private Button closeButton;

    // DAOs
    private Service service;
    private ExportService ExportUtils;
    // Data
    private int selectedTournamentId = -1;
    private boolean isWeeklyMode = true;
    private List<WeeklyStatistics> currentStatistics = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo DAOs
        service = new Service();

        // Thiết lập các combobox
        setupComboBoxes();

        // Thiết lập các bảng
        setupTables();

        // Thiết lập các sự kiện
        setupEventHandlers();

        // Thiết lập các biểu đồ mặc định
        setupDefaultCharts();
    }

    private void setupComboBoxes() {
        try {
            // Lấy danh sách giải đấu
            List<MODEL_GIAIDAU> tournaments = service.getAllTournament();
            ObservableList<String> tournamentNames = FXCollections.observableArrayList();
            for (MODEL_GIAIDAU tournament : tournaments) {
                tournamentNames.add(tournament.getTenGD());
            }
            tournamentComboBox.setItems(tournamentNames);
            tournamentComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    selectedTournamentId = tournaments.stream()
                            .filter(t -> t.getTenGD().equals(newValue))
                            .findFirst()
                            .map(MODEL_GIAIDAU::getMaGD)
                            .orElse(-1);
                    refreshAllData();
                }
            });

        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể tải dữ liệu combobox", e.getMessage());
        }
    }

    private void setupTables() {
        // Thiết lập bảng thống kê chi tiết
        periodColumn.setCellValueFactory(new PropertyValueFactory<>("period"));
        matchCountColumn.setCellValueFactory(new PropertyValueFactory<>("matchCount"));
        goalCountColumn.setCellValueFactory(new PropertyValueFactory<>("goalCount"));
        avgGoalsColumn.setCellValueFactory(new PropertyValueFactory<>("avgGoals"));

        // Thiết lập bảng xếp hạng hiệu suất
        rankPeriodColumn.setCellValueFactory(new PropertyValueFactory<>("period"));
        rankPositionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        rankScoreColumn.setCellValueFactory(new PropertyValueFactory<>("performanceScore"));
        rankGoalRatioColumn.setCellValueFactory(new PropertyValueFactory<>("goalRatio"));
        rankNotesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        // Thiết lập bảng so sánh
        metricColumn.setCellValueFactory(new PropertyValueFactory<>("metric"));
        period1ValueColumn.setCellValueFactory(new PropertyValueFactory<>("period1Value"));
        period2ValueColumn.setCellValueFactory(new PropertyValueFactory<>("period2Value"));
        differenceColumn.setCellValueFactory(new PropertyValueFactory<>("difference"));
        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
    }

    private void setupEventHandlers() {
        // Sự kiện cho radio button chế độ xem
        weeklyRadio.setOnAction(event -> {
            isWeeklyMode = true;
            refreshAllData();
        });

        roundRadio.setOnAction(event -> {
            isWeeklyMode = false;
            refreshAllData();
        });

        // Sự kiện cho radio button xu hướng
        matchesTrendRadio.setOnAction(event -> updateTrendChart("matches"));
        goalsTrendRadio.setOnAction(event -> updateTrendChart("goals"));

        // Sự kiện cho các nút
        refreshButton.setOnAction(event -> refreshAllData());
        compareButton.setOnAction(event -> performComparison());
        exportDetailedButton.setOnAction(event -> exportDetailedReport());
        exportComparisonButton.setOnAction(event -> exportComparisonReport());
        exportSummaryButton.setOnAction(event -> exportSummaryReport());

        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        // Sự kiện cho combobox lọc thời gian
        periodFilterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filterDataByPeriod(newValue);
            }
        });
    }

    private void setupDefaultCharts() {
        // Thiết lập các nhãn trục mặc định
        timeAxis.setLabel(isWeeklyMode ? "Tuần" : "Vòng đấu");
        trendValueAxis.setLabel("Số trận đấu");

        comparisonTimeAxis.setLabel(isWeeklyMode ? "Tuần" : "Vòng đấu");
        comparisonValueAxis.setLabel("Giá trị");

        efficiencyTimeAxis.setLabel(isWeeklyMode ? "Tuần" : "Vòng đấu");
        efficiencyGoalAxis.setLabel("Số bàn thắng");

    }

    private void refreshAllData() {
        if (selectedTournamentId != -1) {
            try {
                // Lấy dữ liệu thống kê
                currentStatistics = service.getWeeklyStatistics(selectedTournamentId, isWeeklyMode);

                // Cập nhật tổng quan
                updateOverviewData();

                // Cập nhật các biểu đồ
                updateResultDistributionChart();
                updateTrendChart("matches");
                updateComparisonChart();
                updateGoalEfficiencyChart();
                updateCardChart();

                // Cập nhật bảng
                updateDetailedTable();
                updatePerformanceRankingTable();

                // Cập nhật combobox lọc
                updatePeriodFilterComboBox();
                updateComparisonComboBoxes();

            } catch (Exception e) {
                AlertUtils.showError("Lỗi", "Không thể tải dữ liệu thống kê", e.getMessage());
            }
        }
    }

    private void updateOverviewData() {
        if (currentStatistics.isEmpty()) {
            totalMatchesLabel.setText("0");
            totalGoalsLabel.setText("0");
            avgGoalsPerMatchLabel.setText("0.0");
            return;
        }

        int totalMatches = currentStatistics.stream().mapToInt(WeeklyStatistics::getMatchCount).sum();
        int totalGoals = currentStatistics.stream().mapToInt(WeeklyStatistics::getGoalCount).sum();

        double avgGoalsPerMatch = totalMatches > 0 ? (double) totalGoals / totalMatches : 0.0;

        totalMatchesLabel.setText(String.valueOf(totalMatches));
        totalGoalsLabel.setText(String.valueOf(totalGoals));
        avgGoalsPerMatchLabel.setText(String.format("%.2f", avgGoalsPerMatch));

    }

    private void updateResultDistributionChart() {
        try {
            resultDistributionPieChart.getData().clear();

            if (currentStatistics.isEmpty()) {
                return;
            }

            // Tính toán phân bố kết quả
            Map<String, Integer> resultCounts = service.getResultDistribution(selectedTournamentId);

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            for (Map.Entry<String, Integer> entry : resultCounts.entrySet()) {
                if (entry.getValue() > 0) {
                    pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }
            }

            resultDistributionPieChart.setData(pieChartData);

        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể cập nhật biểu đồ phân bố kết quả", e.getMessage());
        }
    }

    private void updateTrendChart(String dataType) {
        try {
            trendLineChart.getData().clear();

            if (currentStatistics.isEmpty()) {
                return;
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();

            switch (dataType) {
                case "matches":
                    series.setName("Số trận đấu");
                    trendValueAxis.setLabel("Số trận đấu");
                    for (WeeklyStatistics stat : currentStatistics) {
                        series.getData().add(new XYChart.Data<>(stat.getPeriod(), stat.getMatchCount()));
                    }
                    break;
                case "goals":
                    series.setName("Số bàn thắng");
                    trendValueAxis.setLabel("Số bàn thắng");
                    for (WeeklyStatistics stat : currentStatistics) {
                        series.getData().add(new XYChart.Data<>(stat.getPeriod(), stat.getGoalCount()));
                    }
                    break;
                case "attendance":
                    series.setName("Số khán giả");
                    trendValueAxis.setLabel("Số khán giả");
                    break;
            }

            trendLineChart.getData().add(series);

        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể cập nhật biểu đồ xu hướng", e.getMessage());
        }
    }

    private void updateComparisonChart() {
        try {
            comparisonBarChart.getData().clear();

            if (currentStatistics.isEmpty()) {
                return;
            }

            XYChart.Series<String, Number> matchSeries = new XYChart.Series<>();
            matchSeries.setName("Số trận đấu");

            XYChart.Series<String, Number> goalSeries = new XYChart.Series<>();
            goalSeries.setName("Số bàn thắng");

            for (WeeklyStatistics stat : currentStatistics) {
                matchSeries.getData().add(new XYChart.Data<>(stat.getPeriod(), stat.getMatchCount()));
                goalSeries.getData().add(new XYChart.Data<>(stat.getPeriod(), stat.getGoalCount()));
            }

            comparisonBarChart.getData().addAll(matchSeries, goalSeries);

        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể cập nhật biểu đồ so sánh", e.getMessage());
        }
    }

    private void updateGoalEfficiencyChart() {
        try {
            goalEfficiencyAreaChart.getData().clear();

            if (currentStatistics.isEmpty()) {
                return;
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Hiệu suất ghi bàn");

            for (WeeklyStatistics stat : currentStatistics) {
                series.getData().add(new XYChart.Data<>(stat.getPeriod(), stat.getGoalCount()));
            }

            goalEfficiencyAreaChart.getData().add(series);

        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể cập nhật biểu đồ hiệu suất ghi bàn", e.getMessage());
        }
    }

    private void updateCardChart() {
        try {

            if (currentStatistics.isEmpty()) {
                return;
            }

            XYChart.Series<String, Number> yellowCardSeries = new XYChart.Series<>();
            yellowCardSeries.setName("Thẻ vàng");

            XYChart.Series<String, Number> redCardSeries = new XYChart.Series<>();
            redCardSeries.setName("Thẻ đỏ");


        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể cập nhật biểu đồ thẻ phạt", e.getMessage());
        }
    }

    private void updateDetailedTable() {
        detailedStatsTable.setItems(FXCollections.observableArrayList(currentStatistics));
    }

    private void updatePerformanceRankingTable() {
        try {
            List<PerformanceRanking> rankings = service.getPerformanceRanking(selectedTournamentId,  isWeeklyMode);
            performanceRankingTable.setItems(FXCollections.observableArrayList(rankings));
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể cập nhật bảng xếp hạng hiệu suất", e.getMessage());
        }
    }

    private void updatePeriodFilterComboBox() {
        ObservableList<String> periods = FXCollections.observableArrayList();
        periods.add("Tất cả");

        for (WeeklyStatistics stat : currentStatistics) {
            periods.add(stat.getPeriod());
        }

        periodFilterComboBox.setItems(periods);
        periodFilterComboBox.getSelectionModel().selectFirst();
    }

    private void updateComparisonComboBoxes() {
        ObservableList<String> periods = FXCollections.observableArrayList();

        for (WeeklyStatistics stat : currentStatistics) {
            periods.add(stat.getPeriod());
        }

        period1ComboBox.setItems(periods);
        period2ComboBox.setItems(periods);
    }

    private void filterDataByPeriod(String selectedPeriod) {
        if ("Tất cả".equals(selectedPeriod)) {
            updateDetailedTable();
        } else {
            List<WeeklyStatistics> filteredStats = currentStatistics.stream()
                    .filter(stat -> stat.getPeriod().equals(selectedPeriod))
                    .toList();
            detailedStatsTable.setItems(FXCollections.observableArrayList(filteredStats));
        }
    }

    private void performComparison() {
        String period1 = period1ComboBox.getSelectionModel().getSelectedItem();
        String period2 = period2ComboBox.getSelectionModel().getSelectedItem();

        if (period1 == null || period2 == null) {
            AlertUtils.showWarning("Cảnh báo","", "Vui lòng chọn đầy đủ hai thời gian để so sánh");
            return;
        }

        if (period1.equals(period2)) {
            AlertUtils.showWarning("Cảnh báo","", "Vui lòng chọn hai thời gian khác nhau để so sánh");
            return;
        }

        try {
            List<ComparisonData> comparisonData = service.getComparisonData(
                    selectedTournamentId,  period1, period2, isWeeklyMode);

            comparisonTable.setItems(FXCollections.observableArrayList(comparisonData));

            // Tạo phân tích tự động
            generateAnalysis(comparisonData, period1, period2);

        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể thực hiện so sánh", e.getMessage());
        }
    }

    private void generateAnalysis(List<ComparisonData> comparisonData, String period1, String period2) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("PHÂN TÍCH SO SÁNH GIỮA ").append(period1).append(" VÀ ").append(period2).append(":\n\n");

        for (ComparisonData data : comparisonData) {
            String trend = data.getPercentage().startsWith("+") ? "tăng" : "giảm";
            analysis.append("- ").append(data.getMetric()).append(": ")
                    .append(trend).append(" ").append(data.getPercentage()).append("\n");
        }

        analysis.append("\nKẾT LUẬN:\n");
        analysis.append("Dựa trên các chỉ số trên, có thể thấy xu hướng phát triển của giải đấu qua các thời kỳ.");

        analysisTextArea.setText(analysis.toString());
    }

    private void exportDetailedReport() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Xuất báo cáo thống kê chi tiết");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"),
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(exportDetailedButton.getScene().getWindow());

            if (file != null) {
                String filePath = file.getAbsolutePath();

                if (filePath.endsWith(".xlsx")) {
                    ExportUtils.exportWeeklyStatsToExcel(currentStatistics, filePath, isWeeklyMode);
                } else if (filePath.endsWith(".pdf")) {
                    MODEL_GIAIDAU selectedTournament = service.getTournamentByID(selectedTournamentId);
                    ExportUtils.exportWeeklyStatsToPDF(currentStatistics, filePath, isWeeklyMode,selectedTournament.getTenGD());
                }

                AlertUtils.showInformation("Thành công", "Xuất báo cáo thành công",
                        "Báo cáo thống kê chi tiết đã được xuất thành công.");
            }
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể xuất báo cáo", e.getMessage());
        }
    }

    private void exportComparisonReport() {
        ObservableList<ComparisonData> comparisonData = comparisonTable.getItems();

        if (comparisonData.isEmpty()) {
            AlertUtils.showWarning("Cảnh báo","", "Vui lòng thực hiện so sánh trước khi xuất báo cáo");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Xuất báo cáo so sánh");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"),
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(exportComparisonButton.getScene().getWindow());

            if (file != null) {
                String filePath = file.getAbsolutePath();
                String period1 = period1ComboBox.getSelectionModel().getSelectedItem();
                String period2 = period2ComboBox.getSelectionModel().getSelectedItem();
                String analysis = analysisTextArea.getText();

                if (filePath.endsWith(".xlsx")) {
                    ExportUtils.exportComparisonToExcel(comparisonData, filePath, period1, period2, analysis);
                } else if (filePath.endsWith(".pdf")) {
                    ExportUtils.exportComparisonToPDF(comparisonData, filePath, period1, period2, analysis);
                }

                AlertUtils.showInformation("Thành công", "Xuất báo cáo thành công",
                        "Báo cáo so sánh đã được xuất thành công.");
            }
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể xuất báo cáo so sánh", e.getMessage());
        }
    }

    private void exportSummaryReport() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Xuất báo cáo tổng hợp");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"),
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(exportSummaryButton.getScene().getWindow());

            if (file != null) {
                String filePath = file.getAbsolutePath();

                // Tạo dữ liệu tổng hợp
                Map<String, Object> summaryData = createSummaryData();
                MODEL_GIAIDAU selectedTournament = service.getTournamentByID(selectedTournamentId);
                if (filePath.endsWith(".xlsx")) {

                    ExportUtils.exportSummaryToExcel(summaryData, filePath, selectedTournament.getTenGD(), isWeeklyMode);
                } else if (filePath.endsWith(".pdf")) {
                    ExportUtils.exportSummaryToPDF(summaryData, filePath,  selectedTournament.getTenGD(), isWeeklyMode);
                }

                AlertUtils.showInformation("Thành công", "Xuất báo cáo thành công",
                        "Báo cáo tổng hợp đã được xuất thành công.");
            }
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Không thể xuất báo cáo tổng hợp", e.getMessage());
        }
    }

    private Map<String, Object> createSummaryData() {
        Map<String, Object> summaryData = new HashMap<>();

        summaryData.put("tournament", tournamentComboBox.getSelectionModel().getSelectedItem());
        summaryData.put("mode", isWeeklyMode ? "Theo tuần" : "Theo vòng đấu");
        summaryData.put("totalMatches", totalMatchesLabel.getText());
        summaryData.put("totalGoals", totalGoalsLabel.getText());
        summaryData.put("avgGoalsPerMatch", avgGoalsPerMatchLabel.getText());

        summaryData.put("weeklyStatistics", currentStatistics);
        summaryData.put("performanceRanking", performanceRankingTable.getItems());

        return summaryData;
    }
}