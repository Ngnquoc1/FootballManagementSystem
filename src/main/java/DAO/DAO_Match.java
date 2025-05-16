package DAO;

import Controller.Connection.DatabaseConnection;

import Model.Match;
import Service.Service;
import oracle.jdbc.OracleTypes;

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
    public Match getFromRs(ResultSet rs) throws Exception {
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
            int scoreCLB1 = rs.getInt("Score1");
            int scoreCLB2 = rs.getInt("Score2");

        return new Match(maTD,tenMuaGiai,tenVD, tenCLB1, tenCLB2, gioThiDau, ngayThiDau, sanThiDau, logoCLB1, logoCLB2, scoreCLB1, scoreCLB2);
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
            String sql = "{call GetUpComingMatchesByCondition(?, ?)}";
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
            while(rs.next())
            {
                ds.add(getFromRs(rs));
            }

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

}
