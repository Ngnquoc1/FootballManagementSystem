package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_CAUTHUTHAMGIACLB;

import java.sql.*;
import java.util.ArrayList;

public class DAO_CAUTHU_CLB implements DAOInterface<MODEL_CAUTHUTHAMGIACLB> {

    @Override
    public void insertDB(MODEL_CAUTHUTHAMGIACLB modelCauthuthamgiaclb) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "INSERT INTO CAUTHU_CLB (MaMG, MaCLB, MaCT) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelCauthuthamgiaclb.getMaMG());
            ps.setInt(2, modelCauthuthamgiaclb.getMaCLB());
            ps.setInt(3, modelCauthuthamgiaclb.getMaCT());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateDB(MODEL_CAUTHUTHAMGIACLB modelCauthuthamgiaclb) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "UPDATE CAUTHU_CLB SET MaCLB = ?, MaCT = ? WHERE MaMG = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelCauthuthamgiaclb.getMaCLB());
            ps.setInt(2, modelCauthuthamgiaclb.getMaCT());
            ps.setInt(3, modelCauthuthamgiaclb.getMaMG());
            ps.executeUpdate();
        }
    }

    @Override
    public int deleteDB(MODEL_CAUTHUTHAMGIACLB modelCauthuthamgiaclb) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "DELETE FROM CAUTHU_CLB WHERE MaMG = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelCauthuthamgiaclb.getMaMG());
            return ps.executeUpdate();
        }
    }

    @Override
    public MODEL_CAUTHUTHAMGIACLB getFromRs(ResultSet rs) throws Exception {
        MODEL_CAUTHUTHAMGIACLB model = new MODEL_CAUTHUTHAMGIACLB();
        model.setMaMG(rs.getInt("MaMG"));
        model.setMaCLB(rs.getInt("MaCLB"));
        model.setMaCT(rs.getInt("MaCT"));
        return model;
    }

    @Override
    public ArrayList<MODEL_CAUTHUTHAMGIACLB> selectAllDB() throws SQLException {
        ArrayList<MODEL_CAUTHUTHAMGIACLB> ds = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM CAUTHU_CLB";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(getFromRs(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Override
    public MODEL_CAUTHUTHAMGIACLB selectByID(int idMG) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM CAUTHU_CLB WHERE MaMG = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMG);
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
    public ArrayList<MODEL_CAUTHUTHAMGIACLB> selectByCondition(String condition) throws SQLException {
        ArrayList<MODEL_CAUTHUTHAMGIACLB> ds = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM CAUTHU_CLB WHERE " + condition;
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(getFromRs(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
}