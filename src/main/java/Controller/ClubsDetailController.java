package Controller;

import Model.MODEL_CLB;
import Model.MODEL_GIAIDAU;
import Model.MODEL_SAN;
import Service.Service;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
public class ClubsDetailController {

    // Club information
    @FXML private ImageView clubLogoImageView;
    @FXML private Label clubNameLabel;
    @FXML private Label clubIdLabel;
    @FXML private Label countryLabel;
    @FXML private Label websiteLabel;
    @FXML private Label colorLabel;
    @FXML private Label coachLabel;
    @FXML private Label leagueLabel;
    @FXML private VBox achievementsContainer;

    // Stadium information
    @FXML private Label stadiumNameLabel;
    @FXML private Label stadiumIdLabel;
    @FXML private Label addressLabel;
    @FXML private Label capacityLabel;
    @FXML private Rectangle capacityBar;
    @FXML private Label capacityPercentLabel;
    @FXML private WebView mapWebView;
    // Models
    private MODEL_CLB club;
    private MODEL_SAN stadium;
    private String logoDirectory = "src/main/resources/image/ClubLogo/";
    private Service service;
    @FXML
    private void initialize() {
    }

    private void displayClubData() {
        // Set additional club info (this would come from an extended model or database)
        System.out.println("Displaying club data for: " + club.getTenCLB());

        // Kiểm tra và thiết lập thông tin cơ bản của CLB
        if (clubIdLabel != null) clubIdLabel.setText(String.valueOf(club.getMaCLB()));

        if (clubNameLabel != null) clubNameLabel.setText(club.getTenCLB().toUpperCase());

        if (countryLabel != null) countryLabel.setText("Việt Nam");

        if (websiteLabel != null) websiteLabel.setText(club.getEmail());

        if (colorLabel != null) colorLabel.setText("Đỏ");

        if (coachLabel != null) coachLabel.setText(club.getTenHLV());

        if (leagueLabel != null) leagueLabel.setText("V-League");

        // Load club logo
        loadClubLogo();
    }

    private void displayStadiumData() {

        // Kiểm tra và thiết lập thông tin sân vận động
        if (stadiumIdLabel != null) stadiumIdLabel.setText(String.valueOf(stadium.getMaSan()));

        if (stadiumNameLabel != null) stadiumNameLabel.setText(stadium.getTenSan());

        if (addressLabel != null) addressLabel.setText(stadium.getDiaChi());

        if (capacityLabel != null) capacityLabel.setText(String.format("%,d người", stadium.getSucChua()));

        // Thiết lập thanh sức chứa dựa trên phần trăm (giả sử 100,000 là sức chứa tối đa)
        double maxCapacity = 100000.0;
        double percentage = stadium.getSucChua() / maxCapacity;

        if (capacityBar != null) capacityBar.setWidth(400.0 * percentage);

        if (capacityPercentLabel != null) capacityPercentLabel.setText(String.format("%.0f%%", percentage * 100));
    }

    private void loadClubLogo() {
        try {
            // Try to load from resources
            String logoPath = logoDirectory + club.getLogoCLB();
            Image logoImage;

            try {
                File logoFile = new File(logoPath);
                logoImage = new Image(logoFile.toURI().toString());
            } catch (Exception e) {
                // If not found in resources, try to load from file system
                File logoFile = new File(logoDirectory + "default_logo.png");
                logoImage = new Image(logoFile.toURI().toString());
            }

            if (!logoImage.isError()) {
                clubLogoImageView.setImage(logoImage);
            }
        } catch (Exception e) {
            System.out.println("Error loading club logo: " + e.getMessage());
        }
    }

    private void displayStadiumLocation() {
        if (stadium == null || mapWebView == null) return;

        try {
            WebEngine webEngine = mapWebView.getEngine();

            // Lấy địa chỉ sân vận động
            String address = stadium.getDiaChi();
            String stadiumName = stadium.getTenSan();

            // Lấy tọa độ từ địa chỉ
            double[] coordinates = getCoordinatesFromAddress(address);
            double latitude = coordinates[0];
            double longitude = coordinates[1];

            // HTML với OpenStreetMap và Leaflet (giống như ví dụ 1)
            String mapContent =
                    "<html>" +
                            "<head>" +
                            "   <link rel='stylesheet' href='https://unpkg.com/leaflet@1.7.1/dist/leaflet.css' />" +
                            "   <script src='https://unpkg.com/leaflet@1.7.1/dist/leaflet.js'></script>" +
                            "   <style>body, html, #map { height: 100%; margin: 0; padding: 0; }</style>" +
                            "</head>" +
                            "<body>" +
                            "   <div id='map'></div>" +
                            "   <script>" +
                            "       var map = L.map('map').setView([" + latitude + ", " + longitude + "], 15);" +
                            "       L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
                            "           attribution: '&copy; OpenStreetMap contributors'" +
                            "       }).addTo(map);" +
                            "       L.marker([" + latitude + ", " + longitude + "]).addTo(map)" +
                            "           .bindPopup('" + stadiumName + "<br>" + address + "').openPopup();" +
                            "   </script>" +
                            "</body>" +
                            "</html>";

            webEngine.loadContent(mapContent);

            System.out.println("OpenStreetMap loaded for stadium: " + stadium.getTenSan());
        } catch (Exception e) {
            System.out.println("Error loading map: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double[] getCoordinatesFromAddress(String address) {
        try {
            // Encode địa chỉ
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);

            // Tạo URL cho Nominatim API
            URL url = new URL("https://nominatim.openstreetmap.org/search?format=json&q=" + encodedAddress);

            // Mở kết nối
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Thêm User-Agent (bắt buộc cho Nominatim API)
            connection.setRequestProperty("User-Agent", "JavaFX Football Manager App");

            // Đọc phản hồi
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON
            JSONArray results = new JSONArray(response.toString());
            if (!results.isEmpty()) {
                JSONObject result = results.getJSONObject(0);
                double lat = result.getDouble("lat");
                double lon = result.getDouble("lon");
                return new double[] {lat, lon};
            }
        } catch (Exception e) {
            System.out.println("Error geocoding address: " + e.getMessage());
            e.printStackTrace();
        }

        // Trả về tọa độ mặc định nếu không tìm thấy
        return new double[] {21.0285, 105.8542}; // Tọa độ mặc định (Hà Nội)
    }

    private void displayArchivedData() {
        Map<Integer, MODEL_GIAIDAU> achievements = service.getArchivedData(this.club.getMaCLB());
        achievementsContainer.getChildren().clear();
        achievementsContainer.setSpacing(10);
        achievementsContainer.setStyle("-fx-padding: 10;");

        for (Map.Entry<Integer, MODEL_GIAIDAU> entry : achievements.entrySet()) {
            HBox achievementRow = new HBox(15); // Increased spacing between elements
            achievementRow.setAlignment(Pos.CENTER_LEFT);
            achievementRow.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 5; "
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

            // Rank label with special styling for champion
            Label rankLabel = new Label();
            if (entry.getKey() == 1) {
                rankLabel.setText("Vô địch");
                rankLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 80px; -fx-text-fill: gold; "
                        + "-fx-background-color: linear-gradient(to right, #FFD700, #FFA500); "
                        + "-fx-background-radius: 15; -fx-padding: 5 10; -fx-text-fill: white;");
            } else {
                rankLabel.setText("Hạng " + entry.getKey());
                rankLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 80px; -fx-text-fill: #2c3e50; "
                        + "-fx-background-color: #ecf0f1; -fx-background-radius: 15; -fx-padding: 5 10;");
            }

            // League name label
            Label leagueLabel = new Label(entry.getValue().getTenGD());
            leagueLabel.setStyle("-fx-min-width: 200px; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

            // Year label with custom styling
            Label yearLabel = new Label(entry.getValue().getNgayKT().getYear() + "");
            yearLabel.setStyle("-fx-min-width: 60px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");

            achievementRow.getChildren().addAll(rankLabel, leagueLabel, yearLabel);

            // Add hover effect
            achievementRow.setOnMouseEntered(e ->
                    achievementRow.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 5; "
                            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"));
            achievementRow.setOnMouseExited(e ->
                    achievementRow.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 5; "
                            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);"));

            achievementsContainer.getChildren().add(achievementRow);
        }

        if (achievements.isEmpty()) {
            Label noDataLabel = new Label("Chưa có thành tích");
            noDataLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-font-size: 14px; "
                    + "-fx-padding: 20; -fx-alignment: center;");
            achievementsContainer.getChildren().add(noDataLabel);
        }
    }

    // Method to set club and stadium data from outside
    public void setData(MODEL_CLB club, MODEL_SAN stadium) {
        service= new Service();
        this.club = club;
        this.stadium = stadium;
        initialize();
        displayClubData();
        displayStadiumData();
        displayStadiumLocation();
        displayArchivedData();
    }

}