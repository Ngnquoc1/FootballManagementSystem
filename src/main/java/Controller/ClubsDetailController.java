package Controller;

import Model.MODEL_CLB;
import Model.MODEL_SAN;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;
public class ClubsDetailController {

    @FXML private BorderPane mainContainer;

    // Club information
    @FXML private ImageView clubLogoImageView;
    @FXML private Label clubNameLabel;
    @FXML private Label clubEstablishedLabel;
    @FXML private Label clubNicknameLabel;
    @FXML private Label clubIdLabel;
    @FXML private Label countryLabel;
    @FXML private Label websiteLabel;
    @FXML private Label colorLabel;
    @FXML private Label coachLabel;
    @FXML private Label leagueLabel;


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

    @FXML
    private void initialize() {
    }


    private void displayClubData() {
        // Set additional club info (this would come from an extended model or database)
        System.out.println("Displaying club data for: " + club.getTenCLB());

        // Kiểm tra và thiết lập thông tin cơ bản của CLB
        if (clubIdLabel != null) clubIdLabel.setText(String.valueOf(club.getMaCLB()));
        else System.out.println("clubIdLabel is null");

        if (clubNameLabel != null) clubNameLabel.setText(club.getTenCLB().toUpperCase());
        else System.out.println("clubNameLabel is null");

        if (countryLabel != null) countryLabel.setText("Việt Nam");
        else System.out.println("countryLabel is null");

        if (websiteLabel != null) websiteLabel.setText(club.getEmail());
        else System.out.println("websiteLabel is null");

        if (colorLabel != null) colorLabel.setText("Đỏ");
        else System.out.println("colorLabel is null");

        if (coachLabel != null) coachLabel.setText(club.getTenHLV());
        else System.out.println("coachLabel is null");

        if (leagueLabel != null) leagueLabel.setText("V-League");
        else System.out.println("leagueLabel is null");

        // Load club logo
        loadClubLogo();
    }

    private void displayStadiumData() {
        System.out.println("Displaying stadium data for: " + stadium.getTenSan());

        // Kiểm tra và thiết lập thông tin sân vận động
        if (stadiumIdLabel != null) stadiumIdLabel.setText(String.valueOf(stadium.getMaSan()));
        else System.out.println("stadiumIdLabel is null");

        if (stadiumNameLabel != null) stadiumNameLabel.setText(stadium.getTenSan());
        else System.out.println("stadiumNameLabel is null");

        if (addressLabel != null) addressLabel.setText(stadium.getDiaChi());
        else System.out.println("addressLabel is null");

        if (capacityLabel != null) capacityLabel.setText(String.format("%,d người", stadium.getSucChua()));
        else System.out.println("capacityLabel is null");

        // Thiết lập thanh sức chứa dựa trên phần trăm (giả sử 100,000 là sức chứa tối đa)
        double maxCapacity = 100000.0;
        double percentage = stadium.getSucChua() / maxCapacity;

        if (capacityBar != null) capacityBar.setWidth(400.0 * percentage);
        else System.out.println("capacityBar is null");

        if (capacityPercentLabel != null) capacityPercentLabel.setText(String.format("%.0f%%", percentage * 100));
        else System.out.println("capacityPercentLabel is null");
    }

    private void loadClubLogo() {
        try {
            // Try to load from resources
            String logoPath = "/Image/ClubLogo/" + club.getLogoCLB();
            Image logoImage = null;

            try {
                logoImage = new Image(getClass().getResourceAsStream(logoPath));
            } catch (Exception e) {
                // If not found in resources, try to load from file system
                File logoFile = new File("Image/ClubLogo/" + club.getLogoCLB());
                if (logoFile.exists()) {
                    logoImage = new Image(logoFile.toURI().toString());
                } else {
                    // If still not found, use a placeholder
                    logoImage = new Image(getClass().getResourceAsStream("/Image/placeholder_logo.png"));
                }
            }

            if (logoImage != null && !logoImage.isError()) {
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
            String encodedAddress = URLEncoder.encode(address, "UTF-8");

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
            if (results.length() > 0) {
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

    // Method to set club and stadium data from outside
    public void setData(MODEL_CLB club, MODEL_SAN stadium) {
        this.club = club;
        this.stadium = stadium;
        initialize();
        displayClubData();
        displayStadiumData();
        displayStadiumLocation();
    }

}