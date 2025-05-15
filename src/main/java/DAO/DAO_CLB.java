package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_CLB;

import java.sql.*;
import java.util.ArrayList;

public class DAO_CLB implements DAOInterface<MODEL_CLB> {

    @Override
    public void insertDB(MODEL_CLB model) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "INSERT INTO CLB (MaCLB, TenCLB, SanNha, LogoCLB) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, model.getMaCLB());
            ps.setString(2, model.getTenCLB());
            ps.setInt(3, model.getMaSan());
            ps.setString(4, model.getLogoCLB());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateDB(MODEL_CLB model) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "UPDATE CLB SET TenCLB = ?, SanNha = ?, LogoCLB = ? WHERE MaCLB = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, model.getTenCLB());
            ps.setInt(2, model.getMaSan());
            ps.setString(3, model.getLogoCLB());
            ps.setInt(4, model.getMaCLB());
            ps.executeUpdate();
        }
    }

    @Override
    public int deleteDB(MODEL_CLB model) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "DELETE FROM CLB WHERE MaCLB = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, model.getMaCLB());
            return ps.executeUpdate();
        }
    }

    @Override
    public MODEL_CLB getFromRs(ResultSet rs) throws Exception {
        MODEL_CLB clb = new MODEL_CLB();
        clb.setMaCLB(rs.getInt("MaCLB"));
        clb.setTenCLB(rs.getString("TenCLB"));
        clb.setMaSan(rs.getInt("SanNha"));
        clb.setLogoCLB(rs.getString("LogoCLB"));
        clb.setEmail(rs.getString("Email"));
        clb.setTenHLV(rs.getString("TenHLV"));
        return clb;
    }

    @Override
    public ArrayList<MODEL_CLB> selectAllDB() throws SQLException {
        ArrayList<MODEL_CLB> ds = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM CLB";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(getFromRs(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Override
    public MODEL_CLB selectByID(int id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM CLB WHERE MaCLB = ?";
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
    public ArrayList<MODEL_CLB> selectByCondition(String condition) throws SQLException {
        ArrayList<MODEL_CLB> ds = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM CLB WHERE " + condition;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(getFromRs(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
}