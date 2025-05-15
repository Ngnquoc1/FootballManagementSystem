package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_SAN;

import java.sql.*;
import java.util.ArrayList;

public class DAO_SAN implements DAOInterface<MODEL_SAN> {
    @Override
    public void insertDB(MODEL_SAN model) {

    }

    @Override
    public void updateDB(MODEL_SAN model) {

    }

    @Override
    public int deleteDB(MODEL_SAN model) {
        return 0;
    }

    @Override
    public MODEL_SAN getFromRs(java.sql.ResultSet rs) throws Exception {
            MODEL_SAN SAN = new MODEL_SAN();
            SAN.setMaSan(rs.getInt("MaSan"));
            SAN.setTenSan(rs.getString("TenSAN"));
            SAN.setDiaChi(rs.getString("DiaChi"));
            SAN.setSucChua(rs.getInt("SucChua"));

        return SAN;
    }

    @Override
    public ArrayList<MODEL_SAN> selectAllDB() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        DatabaseConnection db = null;
        ArrayList<MODEL_SAN> ds = new ArrayList<>();

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();

            String sql = "SELECT * FROM \"SAN\"";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next())
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

    @Override
    public MODEL_SAN selectByID(int id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM SAN WHERE MaSan = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return getFromRs(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public java.util.ArrayList<MODEL_SAN> selectByCondition(String Condition) {
        return null;
    }
}
