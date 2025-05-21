package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Model.MODEL_USER;
import Model.Session;
import Controller.Connection.DatabaseConnection;

public class DAO_User {
    // Lấy thông tin người dùng theo tên đăng nhập
    public MODEL_USER getUserByUsername(String username) {
        MODEL_USER user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnectionn();

            if (conn == null) {
                System.err.println("Không thể lấy kết nối Database");
                return null;
            }

            String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new MODEL_USER();
                user.setUserName(rs.getString("TenDangNhap"));
                user.setPassWord(rs.getString("MatKhau"));
                user.setVaiTro(rs.getString("VaiTro"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn SQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                // KHÔNG đóng conn ở đây
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public MODEL_USER getCurrentUser() {
        Session session = Session.getInstance();
        String currentUsername = session.getUsername();
        System.out.println("Lấy thông tin cho người dùng: " + currentUsername);
        return getUserByUsername(currentUsername);
    }

}