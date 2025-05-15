package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_MUAGIAI;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DAO_MUAGIAI implements DAOInterface<MODEL_MUAGIAI>{

    @Override
    public void insertDB(MODEL_MUAGIAI modelMuagiai) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "INSERT INTO MuaGiai (MaMG, TenMG, NgayKhaiMac, NgayBeMac, LogoMG) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelMuagiai.getMaMG());
            ps.setString(2, modelMuagiai.getTenMG());
            ps.setDate(3, Date.valueOf(modelMuagiai.getNgayBD()));
            ps.setDate(4, Date.valueOf(modelMuagiai.getNgayKT()));
            ps.setString(5, modelMuagiai.getLogoFileName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDB(MODEL_MUAGIAI modelMuagiai) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "UPDATE MuaGiai SET TenMG = ?, NgayKhaiMac = ?, NgayBeMac = ?, LogoMG = ? " +
                     "WHERE MaMG = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(5, modelMuagiai.getMaMG());
            ps.setString(1, modelMuagiai.getTenMG());
            ps.setDate(2, Date.valueOf(modelMuagiai.getNgayBD()));
            ps.setDate(3, Date.valueOf(modelMuagiai.getNgayKT()));
            ps.setString(4, modelMuagiai.getLogoFileName());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int deleteDB(MODEL_MUAGIAI modelMuagiai) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "DELETE FROM MuaGiai WHERE MaMG = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelMuagiai.getMaMG());
            return ps.executeUpdate();
        }
    }

    @Override
    public MODEL_MUAGIAI getFromRs(ResultSet rs) throws Exception {

            int id= Integer.parseInt(rs.getString("MaMG"));
            String clbName= (rs.getString("TenMG"));
            LocalDate startDate= rs.getDate("NgayKhaiMac").toLocalDate();
            LocalDate endDate=rs.getDate("NgayBeMac").toLocalDate();
            String clbLogo= rs.getString("LogoMG");
        return new MODEL_MUAGIAI(id, clbName, startDate, endDate,clbLogo);
    }

    @Override
    public ArrayList<MODEL_MUAGIAI> selectAllDB() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        DatabaseConnection db = null;
        ArrayList<MODEL_MUAGIAI> ds = new ArrayList<>();

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();

            String sql = "SELECT * FROM MUAGIAI";

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
//            if(conn!=null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return ds;
    }

    @Override
    public MODEL_MUAGIAI selectByID(int id) {
        return null;
    }

    @Override
    public ArrayList<MODEL_MUAGIAI> selectByCondition(String Condition) {
        return null;
    }
}
