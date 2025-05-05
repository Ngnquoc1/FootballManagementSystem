package Controller.DAO;

import Controller.Connection.DatabaseConnection;

import Model.Match;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DAO_Match implements DAOInterface<Match> {

    @Override
    public void insertDB(Match model) throws SQLException {
        int maTD = model.getId();
        String tenMuaGiai = model.getTenMuaGiai();
        String tenVD = model.getTenVongDau();
        String tenCLB1 = model.getTenCLB1();
        String tenCLB2 = model.getTenCLB2();
        String tenSan = model.getSanThiDau();
        LocalDateTime newDateTime = LocalDateTime.of(model.getNgayThiDau(), model.getGioThiDau());

        Connection conn = null;
        CallableStatement cstmt = null;
        DatabaseConnection db = null;

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();

            String sql = "{call InsertMatch(?, ?, ?, ?, ?, ?, ?)}";
            cstmt = conn.prepareCall(sql);
            cstmt.setString(1, tenMuaGiai);
            cstmt.setString(2, tenVD);
            cstmt.setString(3, tenCLB1);
            cstmt.setString(4, tenCLB2);
            cstmt.setTimestamp(5, Timestamp.valueOf(newDateTime));
            cstmt.setString(6, tenSan);
            cstmt.registerOutParameter(7, Types.INTEGER); // Đăng ký để nhận MaTD

            cstmt.execute();
            maTD = cstmt.getInt(7); // Lấy MaTD trả về
            model.setId(maTD); // Cập nhật MaTD vào model

        } catch (SQLException e) {
            throw e;
        } finally {
            if (cstmt != null) try { cstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
//            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void updateDB(Match model) throws SQLException {

        int maTD = model.getId();
        String tenVD = model.getTenVongDau();
        String tenMuaGiai = model.getTenMuaGiai();
        String tenCLB1 = model.getTenCLB1();
        String tenCLB2 = model.getTenCLB2();
        String tenSan = model.getSanThiDau();
        LocalDateTime newDateTime = LocalDateTime.of(model.getNgayThiDau(), model.getGioThiDau());

        Connection conn = null;
        CallableStatement cstmt = null;
        DatabaseConnection db = null;

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();

            String sql = "{call UpdateMatch(?, ?,?, ?, ?, ?, ?)}";
            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, maTD);
            cstmt.setString(2, tenMuaGiai);
            cstmt.setString(3, tenVD);
            cstmt.setString(4, tenCLB1);
            cstmt.setString(5, tenCLB2);
            cstmt.setTimestamp(6, Timestamp.valueOf(newDateTime));
            cstmt.setString(7, tenSan);

            cstmt.execute();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (cstmt != null) try { cstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public int deleteDB(Match model) throws SQLException {
        if (model == null || model.getId() <= 0) {
            throw new SQLException("Dữ liệu không hợp lệ: Match hoặc ID không hợp lệ.");
        }

        int maTD = model.getId();
        Connection conn = null;
        CallableStatement cstmt = null;
        DatabaseConnection db = null;

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();

            String sql = "{call DeleteMatch(?)}";
            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, maTD);

            cstmt.execute();
            return 1; // Trả về 1 nếu xóa thành công
        } catch (SQLException e) {
            throw e; // Ném lại ngoại lệ để giao diện xử lý
        } finally {
            if (cstmt != null) try { cstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public ArrayList<Match> getFromRs(java.sql.ResultSet rs) throws Exception {
        ArrayList<Match> ds = new ArrayList<>();
        while (rs.next()) {
            int maTD = rs.getInt("ID");
            String tenCLB1 = rs.getString("TenCLB1");
            String logoCLB1 = rs.getString("LogoCLB1");
            String tenCLB2 = rs.getString("TenCLB2");
            String logoCLB2 = rs.getString("LogoCLB2");
            String sanThiDau = rs.getString("SanThiDau");
            String tenMuaGiai = rs.getString("TenMuaGiai");
            String tenVD = rs.getString("TenVD");
            // Lấy ThoiGian và chuyển thành LocalDate, LocalTime
            Timestamp timestamp = rs.getTimestamp("ThoiGian");
            LocalDate ngayThiDau = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime gioThiDau = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

            Match match = new Match(maTD,tenMuaGiai,tenVD, tenCLB1, tenCLB2, gioThiDau, ngayThiDau, sanThiDau, logoCLB1, logoCLB2, null, null);
            ds.add(match);
        }
        return ds;
    }

    @Override
    public java.util.ArrayList<Match> selectAllDB(){
        return null;
    }

    @Override
    public Match selectByID(int id) {
        List<Match> matches = selectByCondition("td.MaTD = " + id);
        return matches.isEmpty() ? null : matches.getFirst();
    }

    @Override
    public ArrayList<Match> selectByCondition(String condition) {
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        DatabaseConnection db = null;
        ArrayList<Match> ds = new ArrayList<>();

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();

            // Gọi procedure
            String sql = "{call GetMatchesByCondition(?, ?)}";
            cstmt = conn.prepareCall(sql);
            // Nếu không có điều kiện, truyền NULL
            if (condition == null || condition.trim().isEmpty()) {
                cstmt.setNull(1, Types.VARCHAR);
            } else {
                cstmt.setString(1, condition);
            }
            cstmt.registerOutParameter(2, Types.REF_CURSOR); // Đăng ký cursor
            cstmt.execute();

            // Lấy ResultSet từ cursor
            rs = (ResultSet) cstmt.getObject(2);
            ds = getFromRs(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (cstmt != null) try { cstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return ds;
    }

    private static final String SQL_GET_MATCHES_WITHOUT_RESULTS =
            "SELECT " +
                    "    td.MaTD AS ID, " +
                    "    v.TenVD AS TenVD, " +
                    "    c1.TenCLB AS TenCLB1, " +
                    "    c1.LogoCLB AS LogoCLB1, " +
                    "    c2.TenCLB AS TenCLB2, " +
                    "    c2.LogoCLB AS LogoCLB2, " +
                    "    td.ThoiGian AS ThoiGian, " + // Lấy trực tiếp ThoiGian
                    "    s.TenSan AS SanThiDau, " +
                    "    m.TenMG AS TenMuaGiai " +
                    "FROM TranDau td " +
                    "JOIN VongDau v ON td.MaVD = v.MaVD " +
                    "JOIN CLB c1 ON td.MaCLB1 = c1.MaCLB " +
                    "JOIN CLB c2 ON td.MaCLB2 = c2.MaCLB " +
                    "JOIN SAN s ON td.MaSan = s.MaSan " +
                    "JOIN MuaGiai m ON v.MaMG = m.MaMG " +
                    "LEFT JOIN KetQuaTD kq ON td.MaTD = kq.MaTD " +
                    "WHERE kq.MaTD IS NULL " +
                    "ORDER BY td.ThoiGian";

    public static Map<LocalDate, List<Match>> getUpcomingMatchs() throws SQLException {
        Map<LocalDate, List<Match>> matchesByDate = new HashMap<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Kết nối tới cơ sở dữ liệu
            DatabaseConnection db= DatabaseConnection.getInstance();
            conn = db.getConnectionn();
            stmt = conn.prepareStatement(SQL_GET_MATCHES_WITHOUT_RESULTS);
            rs = stmt.executeQuery();
            // Duyệt qua kết quả và nhóm theo ngày
            while (rs.next()) {

                int maTD = rs.getInt("ID");
                String tenCLB1 = rs.getString("TenCLB1");
                String logoCLB1 = rs.getString("LogoCLB1");
                String tenCLB2 = rs.getString("TenCLB2");
                String logoCLB2 = rs.getString("LogoCLB2");
                String sanThiDau = rs.getString("SanThiDau");
                String tenMuaGiai = rs.getString("TenMuaGiai");
                String tenVD = rs.getString("TenVD");
                Timestamp timestamp = rs.getTimestamp("ThoiGian");
                LocalDate ngayThiDau = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalTime gioThiDau = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

                Match match = new Match(maTD,tenMuaGiai,tenVD,tenCLB1, tenCLB2, gioThiDau, ngayThiDau, sanThiDau, logoCLB1, logoCLB2, null, null);
                // Nhóm theo ngày thi đấu
                matchesByDate.computeIfAbsent(ngayThiDau, k -> new ArrayList<>()).add(match);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
            throw e;
        } finally {
            // Đóng các tài nguyên
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }

        return matchesByDate;
    }
}
