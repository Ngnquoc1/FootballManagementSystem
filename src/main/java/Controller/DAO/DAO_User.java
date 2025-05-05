package Controller.DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_USER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DAO_User {

    public static MODEL_USER Login(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        DatabaseConnection db = null;
        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();
            String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Bạn có thể mã hóa nếu cần
            rs = pstmt.executeQuery();
            MODEL_USER user = null;
            if (rs.next()) {
                user = new MODEL_USER();
                user.setUserName(rs.getString("TenDangNhap"));
                user.setPassWord(rs.getString("MatKhau"));
                user.setVaiTro(rs.getString("VaiTro"));
                return user;
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
//                if (conn != null) conn.close();
//                if (db != null) db.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
