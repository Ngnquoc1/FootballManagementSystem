package Controller.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Singleton Pattern: đảm bảo chỉ có một kết nối duy nhất
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private DatabaseConnection() throws SQLException { // Đổi thành private để đảm bảo singleton
        connectToDatabase();
    }

    // Kết nối tới CSDL Oracle
    private void connectToDatabase() throws SQLException {
        final String url = "jdbc:oracle:thin:@localhost:1521:orcl"; // Địa chỉ CSDL
        final String username = "c##QLDB"; // Thay bằng username thật
        final String password = "1"; // Thay bằng mật khẩu thật

        try {
            // Load Oracle JDBC Driver (không bắt buộc với ojdbc 6 trở lên)
            Class.forName("oracle.jdbc.OracleDriver");

            // Kết nối đến CSDL
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Kết nối thành công!");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy driver JDBC Oracle!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Connection getConnectionn() {
        return connection;
    }
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đã ngắt kết nối CSDL!");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi ngắt kết nối: " + e.getMessage());
        }
    }
}