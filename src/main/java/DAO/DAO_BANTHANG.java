package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_BANTHANG;

import java.sql.*;
import java.util.ArrayList;

public class DAO_BANTHANG implements DAOInterface<MODEL_BANTHANG> {

    @Override
    public void insertDB(MODEL_BANTHANG modelBanthang) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "{call InsertGoal (?, ?, ?, ?)}";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelBanthang.getMaCT());
            ps.setInt(2, modelBanthang.getMaTD());
            ps.setInt(3, modelBanthang.getPhutGhiBan());
            ps.setInt(4, modelBanthang.getmaLoaiBT());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateDB(MODEL_BANTHANG modelBanthang) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "{call UpdateGoal(?,?,?,?,?)}";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelBanthang.getMaCT());
            ps.setInt(2, modelBanthang.getMaTD());
            ps.setInt(3, modelBanthang.getPhutGhiBan());
            ps.setInt(4, modelBanthang.getmaLoaiBT());
            ps.setInt(5, modelBanthang.getMaBT());
            ps.executeUpdate();
        }
    }

    @Override
    public int deleteDB(MODEL_BANTHANG modelBanthang) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "{call DeleteGoal(?)}";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelBanthang.getMaBT());
            return ps.executeUpdate();
        }
    }

    @Override
    public MODEL_BANTHANG getFromRs(ResultSet rs) throws Exception {
        MODEL_BANTHANG modelBanthang = new MODEL_BANTHANG();
        modelBanthang.setMaBT(rs.getInt("MaBT"));
        modelBanthang.setMaCT(rs.getInt("MaCT"));
        modelBanthang.setMaTD(rs.getInt("MaTD"));
        modelBanthang.setPhutGhiBan(rs.getInt("PhutGhiBan"));
        modelBanthang.setmaLoaiBT(rs.getInt("MaLoaiBT"));
        return modelBanthang;
    }

    @Override
    public ArrayList<MODEL_BANTHANG> selectAllDB() throws SQLException {
        ArrayList<MODEL_BANTHANG> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM BanThang";
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
    public MODEL_BANTHANG selectByID(int id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM BanThang WHERE MaBT = ?";
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
    public ArrayList<MODEL_BANTHANG> selectByCondition(String condition) throws SQLException {
        ArrayList<MODEL_BANTHANG> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM BanThang WHERE " + condition;
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