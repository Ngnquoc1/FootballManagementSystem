package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_KETQUATD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_KQUATRANDAU implements DAOInterface<MODEL_KETQUATD>{
    @Override
    public void insertDB(MODEL_KETQUATD model) throws SQLException {
        Connection conn = null;
        CallableStatement cstmt = null;
        DatabaseConnection db = null;

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();

            String sql = "{call InsertMatchResult(?, ?,?)}";
            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, model.getMaTD());
            cstmt.setInt(2, model.getDiemCLB1());
            cstmt.setInt(3, model.getDiemCLB2());
            cstmt.execute();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (cstmt != null) try { cstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
//            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void updateDB(MODEL_KETQUATD model) throws SQLException {
        Connection conn = null;
        CallableStatement cstmt = null;
        DatabaseConnection db = null;

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();

            String sql = "{call UpdateMatchResult(?, ?,?)}";
            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, model.getMaTD());
            cstmt.setInt(2, model.getDiemCLB1());
            cstmt.setInt(3, model.getDiemCLB2());
            cstmt.execute();

        } catch (SQLException e) {
            throw e;
        } finally {
            if (cstmt != null) try { cstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
//            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public int deleteDB(MODEL_KETQUATD model) throws SQLException {
        if (model == null || model.getMaTD() <= 0) {
            throw new SQLException("Dữ liệu không hợp lệ: Match hoặc ID không hợp lệ.");
        }

        int maTD = model.getMaTD();
        Connection conn = null;
        CallableStatement cstmt = null;
        DatabaseConnection db = null;

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();

            String sql = "{call DeleteMatchResultAndGoals(?)}";
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
    public MODEL_KETQUATD getFromRs(java.sql.ResultSet rs) throws Exception {
        int maTD = rs.getInt("MaTD");
        int score1 = rs.getInt("DiemCLB1");
        int score2 = rs.getInt("DiemCLB2");
        return new MODEL_KETQUATD(maTD, score1, score2);
    }

    @Override
    public java.util.ArrayList<MODEL_KETQUATD> selectAllDB() {
        return null;
    }

    @Override
    public MODEL_KETQUATD selectByID(int id) {
        List<MODEL_KETQUATD> res = selectByCondition("MaTD = " + id);
        return res.isEmpty() ? null : res.getFirst();
    }

    @Override
    public java.util.ArrayList<MODEL_KETQUATD> selectByCondition(String Condition) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        DatabaseConnection db = null;
        ArrayList<MODEL_KETQUATD> ds = new ArrayList<>();

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();
            String sql = "SELECT * FROM KetQuaTD WHERE " + Condition;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
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
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return ds;
    }
}
