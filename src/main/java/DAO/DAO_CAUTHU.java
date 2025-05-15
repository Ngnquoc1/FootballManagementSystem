package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_CAUTHU;

import java.sql.*;
import java.util.ArrayList;

public class DAO_CAUTHU implements DAOInterface<MODEL_CAUTHU> {

    @Override
    public void insertDB(MODEL_CAUTHU modelCauthu) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "INSERT INTO CauThu (MaCT, TenCT, NgaySinh, LoaiCT) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelCauthu.getMaCT());
            ps.setString(2, modelCauthu.getTenCT());
            ps.setDate(3, modelCauthu.getNgaysinh());
            ps.setInt(4, modelCauthu.getLoaiCT());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateDB(MODEL_CAUTHU modelCauthu) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "UPDATE CauThu SET TenCT = ?, NgaySinh = ?, LoaiCT = ? WHERE MaCT = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, modelCauthu.getTenCT());
            ps.setDate(2, modelCauthu.getNgaysinh());
            ps.setInt(3, modelCauthu.getLoaiCT());
            ps.setInt(4, modelCauthu.getMaCT());
            ps.executeUpdate();
        }
    }

    @Override
    public int deleteDB(MODEL_CAUTHU modelCauthu) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "DELETE FROM CauThu WHERE MaCT = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, modelCauthu.getMaCT());
            return ps.executeUpdate();
        }
    }

    @Override
    public MODEL_CAUTHU getFromRs(ResultSet rs) throws Exception {
        MODEL_CAUTHU cauthu = new MODEL_CAUTHU();
        cauthu.setMaCT(rs.getInt("MaCT"));
        cauthu.setTenCT(rs.getString("TenCT"));
        cauthu.setNgaysinh(rs.getDate("NgaySinh"));
        cauthu.setLoaiCT(rs.getInt("LoaiCT"));
        return cauthu;
    }

    @Override
    public ArrayList<MODEL_CAUTHU> selectAllDB() throws SQLException {
        ArrayList<MODEL_CAUTHU> ds = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM CauThu";
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
    public MODEL_CAUTHU selectByID(int id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM CauThu WHERE MaCT = ?";
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
    public ArrayList<MODEL_CAUTHU> selectByCondition(String condition) throws SQLException {
        ArrayList<MODEL_CAUTHU> ds = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM CauThu WHERE " + condition;
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