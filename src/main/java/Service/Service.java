package Service;

import Controller.Connection.DatabaseConnection;
import DAO.DAO_MUAGIAI;
import DAO.DAO_Match;
import Model.MODEL_MUAGIAI;
import Model.MODEL_USER;
import Model.Match;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service {
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

    public int selectCLBIDFromGoal(int maCT,int maTD) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT cclb.MaCLB " +
                "FROM BANTHANG bt " +
                "JOIN TranDau td ON bt.MaTD = td.MaTD " +
                "JOIN VongDau vd ON td.MaVD = vd.MaVD " +
                "JOIN CAUTHU_CLB cclb ON bt.MaCT = cclb.MaCT AND cclb.MaMG = vd.MaMG " +
                "WHERE bt.MaCT = ? AND bt.MaTD = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, maCT); // Thay bằng giá trị thực tế (ví dụ: từ thuộc tính)
            stmt.setLong(2, maTD); // Thay bằng giá trị thực tế (ví dụ: từ thuộc tính)

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MaCLB");
            } else {
                throw new SQLException("Không tìm thấy MaCLB cho MaCT và MaTD đã cho.");
            }
        }
    }

    public  Map<LocalDate, List<Match>> getUpcomingMatchs() throws SQLException {
        Map<LocalDate, List<Match>> matchesByDate = new HashMap<>();
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        DAO_Match daoMatch = new DAO_Match();
        try {
            // Kết nối tới cơ sở dữ liệu
            DatabaseConnection db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();
            cstmt = conn.prepareCall("{call GetUpComingMatches(?)}");
            cstmt.registerOutParameter(1, OracleTypes.CURSOR); // Đăng ký tham số đầu ra là cursor
            cstmt.execute();

            // Lấy ResultSet từ cursor
            rs = (ResultSet) cstmt.getObject(1);

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("ThoiGian");
                LocalDate ngayThiDau = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Match match = daoMatch.getFromRs(rs);
                matchesByDate.computeIfAbsent(ngayThiDau, k -> new ArrayList<>()).add(match);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (cstmt != null) try { cstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return matchesByDate;
    }
    public List<Match> getResultedMatchList() throws SQLException {
        List<Match> matchList = new ArrayList<>();
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        DAO_Match daoMatch = new DAO_Match();
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();
            cstmt = conn.prepareCall("{call GetResultedMatches(?)}");
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();

            rs = (ResultSet) cstmt.getObject(1);
            while (rs.next()) {
                Match match = daoMatch.getFromRs(rs);
                matchList.add(match);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (cstmt != null) try { cstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return matchList;
    }
    public Map<LocalDate, List<Match>> getResultedMatchs() throws SQLException {
        Map<LocalDate, List<Match>> matchesByDate = new HashMap<>();
        List<Match> matchList = this.getResultedMatchList(); // Gọi lại hàm đầu tiên
        for (Match match : matchList) {
            LocalDate ngayThiDau = match.getNgayThiDau();
            matchesByDate.computeIfAbsent(ngayThiDau, k -> new ArrayList<>()).add(match);
        }
        return matchesByDate;
    }

    public void InsertInitialRanking(int MaMG,int MaCLB) throws SQLException{
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "{call InsertInitialRanking(?, ?)}";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, MaMG);
            ps.setInt(2, MaCLB);
            ps.executeUpdate();
        }
    }
    public void updateRanking(int MaTD) throws SQLException{
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "{call UpdateRanking(?)}";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, MaTD);
            ps.executeUpdate();
        }
    }

    public static List<Match> getPendingMatchList() throws SQLException {
        List<Match> matchList = new ArrayList<>();
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        DAO_Match daoMatch = new DAO_Match();
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();
            cstmt = conn.prepareCall("{call GetPendingMatches(?)}");
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();

            rs = (ResultSet) cstmt.getObject(1);
            while (rs.next()) {
                Match match = daoMatch.getFromRs(rs);
                matchList.add(match);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (cstmt != null) try { cstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return matchList;
    }
    public List<MODEL_MUAGIAI> selectAllTournament() throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        return new DAO_MUAGIAI().selectAllDB();
    }
}
