package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_TRANDAU;

import java.sql.*;
import java.util.ArrayList;

public class DAO_TRANDAU implements DAOInterface<MODEL_TRANDAU> {

    @Override
    public void insertDB(MODEL_TRANDAU modelTrandau) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "INSERT INTO TranDau (maTD, maCLB1, maCLB2, maVD, maSan, thoiGian) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelTrandau.getMaTD());
            ps.setInt(2, modelTrandau.getMaCLB1());
            ps.setInt(3, modelTrandau.getMaCLB2());
            ps.setInt(4, modelTrandau.getMaVD());
            ps.setInt(5, modelTrandau.getMaSan());
            ps.setDate(6, modelTrandau.getThoiGian());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateDB(MODEL_TRANDAU modelTrandau) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "UPDATE TranDau SET maCLB1 = ?, maCLB2 = ?, maVD = ?, maSan = ?, thoiGian = ? WHERE maTD = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelTrandau.getMaCLB1());
            ps.setInt(2, modelTrandau.getMaCLB2());
            ps.setInt(3, modelTrandau.getMaVD());
            ps.setInt(4, modelTrandau.getMaSan());
            ps.setDate(5, modelTrandau.getThoiGian());
            ps.setInt(6, modelTrandau.getMaTD());
            ps.executeUpdate();
        }
    }

    @Override
    public int deleteDB(MODEL_TRANDAU modelTrandau) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "DELETE FROM TranDau WHERE maTD = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelTrandau.getMaTD());
            return ps.executeUpdate();
        }
    }

    @Override
    public MODEL_TRANDAU getFromRs(ResultSet rs) throws Exception {
        MODEL_TRANDAU td = new MODEL_TRANDAU();
        td.setMaTD(rs.getInt("maTD"));
        td.setMaCLB1(rs.getInt("maCLB1"));
        td.setMaCLB2(rs.getInt("maCLB2"));
        td.setMaVD(rs.getInt("maVD"));
        td.setMaSan(rs.getInt("maSan"));
        td.setThoiGian(rs.getDate("thoiGian"));
        return td;
    }

    @Override
    public ArrayList<MODEL_TRANDAU> selectAllDB() throws SQLException {
        ArrayList<MODEL_TRANDAU> ds = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM TranDau";
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
    public MODEL_TRANDAU selectByID(int id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM TranDau WHERE maTD = ?";
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
    public ArrayList<MODEL_TRANDAU> selectByCondition(String condition) throws SQLException {
        ArrayList<MODEL_TRANDAU> ds = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM TranDau WHERE " + condition;
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