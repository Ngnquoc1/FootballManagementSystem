package Controller;

import Model.MODEL_BXH_CLB;
import Model.MODEL_MUAGIAI;
import Model.MODEL_BXH_BANTHANG;
import Model.Session;
import Service.Service;
import Util.AlertUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TableController {
    @FXML private VBox mainContainer;

    // Bộ lọc
    @FXML private ComboBox<String> compeFilter;
    @FXML private ComboBox<String> rankingTypeFilter;
    @FXML private Button refreshBtn;

    // TabPane và các Tab
    @FXML private TabPane tabPane;
    @FXML private Tab clubTab;
    @FXML private Tab scorerTab;
    @FXML private Tab bracketTab;

    // Thông tin giải đấu
    @FXML private Label competitionInfoLabel;
    @FXML private Label scorerCompetitionInfoLabel;
    @FXML private Label bracketCompetitionInfoLabel;

    // Bảng xếp hạng CLB
    @FXML private TableView<MODEL_BXH_CLB> clubTableView;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> clubRankColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, String> clubNameColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> clubPlayedColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> clubWonColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> clubDrawnColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> clubLostColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> clubGDColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> clubPointsColumn;
    @FXML private HBox clubLegendBox;

    // Bảng xếp hạng Vua phá lưới
    @FXML private TableView<MODEL_BXH_BANTHANG> scorerTableView;
    @FXML private TableColumn<MODEL_BXH_BANTHANG, Integer> scorerRankColumn;
    @FXML private TableColumn<MODEL_BXH_BANTHANG, String> scorerNameColumn;
    @FXML private TableColumn<MODEL_BXH_BANTHANG, String> scorerClubColumn;
    @FXML private TableColumn<MODEL_BXH_BANTHANG, Integer> scorerGoalsColumn;
    @FXML private TableColumn<MODEL_BXH_BANTHANG, Integer> scorerPenaltyColumn;
    @FXML private TableColumn<MODEL_BXH_BANTHANG, Integer> scorerMatchesColumn;
    @FXML private TableColumn<MODEL_BXH_BANTHANG, Integer> scorerMinutesColumn;

    // Bảng xếp hạng theo nhánh
    @FXML private ComboBox<String> bracketComboBox;
    @FXML private TableView<MODEL_BXH_CLB> bracketTableView;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> bracketRankColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, String> bracketClubColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> bracketPlayedColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> bracketWonColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> bracketDrawnColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> bracketLostColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> bracketGFColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> bracketGAColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> bracketGDColumn;
    @FXML private TableColumn<MODEL_BXH_CLB, Integer> bracketPointsColumn;

    // Nút xuất báo cáo và đóng
    @FXML private Button exportButton;
    @FXML private Button closeButton;

    // Dữ liệu
    private ObservableList<MODEL_BXH_CLB> vleagueClubRankings = FXCollections.observableArrayList();
    private ObservableList<MODEL_BXH_CLB> nationalCupClubRankings = FXCollections.observableArrayList();
    private ObservableList<MODEL_BXH_BANTHANG> vleagueScorerRankings = FXCollections.observableArrayList();
    private ObservableList<MODEL_BXH_BANTHANG> nationalCupScorerRankings = FXCollections.observableArrayList();
    private ObservableList<MODEL_BXH_CLB> bracketARankings = FXCollections.observableArrayList();
    private ObservableList<MODEL_BXH_CLB> bracketBRankings = FXCollections.observableArrayList();

    private Service service;
    @FXML
    private void initialize() throws SQLException {
        service = new Service();
        // Thiết lập các ComboBox
        compeFilter.setItems(FXCollections.observableArrayList(
                service.getAllTournament().stream().map(MODEL_MUAGIAI::getTenMG).toList()));
        rankingTypeFilter.setItems(FXCollections.observableArrayList("BXH CLB", "Vua phá lưới"));
        bracketComboBox.setItems(FXCollections.observableArrayList("Nhánh A", "Nhánh B"));

        // Thiết lập giá trị mặc định
        compeFilter.getSelectionModel().selectFirst();
        rankingTypeFilter.setValue("BXH CLB");
        bracketComboBox.setValue("Nhánh A");

        // Thiết lập các cột cho bảng xếp hạng CLB
        setupClubTableColumns();

        // Thiết lập các cột cho bảng xếp hạng Vua phá lưới
        setupScorerTableColumns();
//
        // Thiết lập các cột cho bảng xếp hạng theo nhánh
//        setupBracketTableColumns();

        // Tạo dữ liệu mẫu
        // Hiển thị dữ liệu ban đầu
        updateTableView();

        // Xử lý sự kiện khi thay đổi giải đấu hoặc loại BXH
        compeFilter.setOnAction(e -> handleFilterChange());
        rankingTypeFilter.setOnAction(e -> handleFilterChange());
//        bracketComboBox.setOnAction(e -> updateBracketTableView());

        // Xử lý sự kiện khi chuyển tab
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == clubTab) {
                rankingTypeFilter.setValue("BXH CLB");
            } else if (newTab == scorerTab) {
                rankingTypeFilter.setValue("Vua phá lưới");
            } else if (newTab == bracketTab) {
                compeFilter.setValue("Cúp Quốc Gia");
                rankingTypeFilter.setValue("BXH CLB");
            }
            updateTableView();
        });
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
            ;
            try {
                int maCauThu = cellData.getValue().getMaCT();
                String playerName = service.getPlayerById(maCauThu).getTenCT();
                return new SimpleStringProperty(playerName != null ? playerName : "Unknown");
            } catch (Exception e) {
                return new SimpleStringProperty("Error");
            }
        });
        scorerClubColumn.setCellValueFactory(cellData -> {
            ;
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

//    private void setupBracketTableColumns() {
//        bracketRankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
//        bracketClubColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        bracketPlayedColumn.setCellValueFactory(new PropertyValueFactory<>("played"));
//        bracketWonColumn.setCellValueFactory(new PropertyValueFactory<>("won"));
//        bracketDrawnColumn.setCellValueFactory(new PropertyValueFactory<>("drawn"));
//        bracketLostColumn.setCellValueFactory(new PropertyValueFactory<>("lost"));
//        bracketGFColumn.setCellValueFactory(new PropertyValueFactory<>("goalsFor"));
//        bracketGAColumn.setCellValueFactory(new PropertyValueFactory<>("goalsAgainst"));
//        bracketGDColumn.setCellValueFactory(new PropertyValueFactory<>("goalDifference"));
//        bracketPointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
//
//        // Thiết lập màu nền cho các hàng dựa trên thứ hạng
//        bracketTableView.setRowFactory(tv -> new TableRow<MODEL_BXH_CLB>() {
//            @Override
//            protected void updateItem(MODEL_BXH_CLB item, boolean empty) {
//                super.updateItem(item, empty);
//                if (item == null || empty) {
//                    setStyle("");
//                } else {
//                    if (item.getHang() <= 2) {
//                        setStyle("-fx-background-color: rgba(76, 175, 80, 0.2);"); // Đi tiếp
//                    } else {
//                        setStyle("");
//                    }
//                }
//            }
//        });
//    }
    private void handleFilterChange() {
        updateTableView();
    }

    private void updateTableView() {
        String competition = compeFilter.getValue();
        String rankingType = rankingTypeFilter.getValue();

        if ("V-League 2025".equals(competition)) {
            competitionInfoLabel.setText("V-League 2025");
            scorerCompetitionInfoLabel.setText("V-League 2025 - Vua phá lưới");

            if ("BXH CLB".equals(rankingType)) {
                tabPane.getSelectionModel().select(clubTab);
                clubTableView.setItems(vleagueClubRankings);
                clubLegendBox.setVisible(true);
            } else {
                tabPane.getSelectionModel().select(scorerTab);
                scorerTableView.setItems(vleagueScorerRankings);
            }
        } else if ("Cúp Quốc Gia".equals(competition)) {
            competitionInfoLabel.setText("Cúp Quốc Gia 2023");
            scorerCompetitionInfoLabel.setText("Cúp Quốc Gia 2023 - Vua phá lưới");
            bracketCompetitionInfoLabel.setText("Cúp Quốc Gia 2023");

            if ("BXH CLB".equals(rankingType)) {
                tabPane.getSelectionModel().select(bracketTab);
//                updateBracketTableView();
                clubLegendBox.setVisible(false);
            } else {
                tabPane.getSelectionModel().select(scorerTab);
                scorerTableView.setItems(nationalCupScorerRankings);
            }
        }
    }

//    private void updateBracketTableView() {
//        String bracket = bracketComboBox.getValue();
//
//        if ("Nhánh A".equals(bracket)) {
//            bracketTableView.setItems(bracketARankings);
//        } else if ("Nhánh B".equals(bracket)) {
//            bracketTableView.setItems(bracketBRankings);
//        }
//    }

    @FXML
    private void handleRefresh() {
        updateTableView();
        AlertUtils.showInformation("Làm mới dữ liệu","", "Dữ liệu đã được làm mới!");
    }

    @FXML
    private void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showSaveDialog(mainContainer.getScene().getWindow());

        if (file != null) {
            AlertUtils.showInformation("Xuất báo cáo", "", "Đã xuất báo cáo thành công!\nFile: " + file.getAbsolutePath());
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
    private ImageView userIcon;

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