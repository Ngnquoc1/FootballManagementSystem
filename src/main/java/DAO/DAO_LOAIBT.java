package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_LOAIBANTHANG;

import java.sql.*;
import java.util.ArrayList;

public class DAO_LOAIBT implements DAOInterface<MODEL_LOAIBANTHANG> {

    @Override
    public void insertDB(MODEL_LOAIBANTHANG modelLoaibanthang) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "INSERT INTO LoaiBanThang (MaLoaiBT, TenLoaiBT) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelLoaibanthang.getMaLoaiBT());
            ps.setString(2, modelLoaibanthang.getTenLoaiBT());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateDB(MODEL_LOAIBANTHANG modelLoaibanthang) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "UPDATE LoaiBanThang SET TenLoaiBT = ? WHERE MaLoaiBT = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, modelLoaibanthang.getTenLoaiBT());
            ps.setInt(2, modelLoaibanthang.getMaLoaiBT());
            ps.executeUpdate();
        }
    }

    @Override
    public int deleteDB(MODEL_LOAIBANTHANG modelLoaibanthang) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "DELETE FROM LoaiBanThang WHERE MaLoaiBT = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelLoaibanthang.getMaLoaiBT());
            return ps.executeUpdate();
        }
    }

    @Override
    public MODEL_LOAIBANTHANG getFromRs(ResultSet rs) throws Exception {
        MODEL_LOAIBANTHANG model = new MODEL_LOAIBANTHANG();
        model.setMaLoaiBT(rs.getInt("MaLoaiBT"));
        model.setTenLoaiBT(rs.getString("TenLoaiBT"));
        return model;
    }

    @Override
    public ArrayList<MODEL_LOAIBANTHANG> selectAllDB() throws SQLException {
        ArrayList<MODEL_LOAIBANTHANG> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM LoaiBanThang";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(getFromRs(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public MODEL_LOAIBANTHANG selectByID(int id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM LoaiBanThang WHERE MaLoaiBT = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getFromRs(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<MODEL_LOAIBANTHANG> selectByCondition(String condition) throws SQLException {
        ArrayList<MODEL_LOAIBANTHANG> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM LoaiBanThang WHERE " + condition;
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(getFromRs(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}