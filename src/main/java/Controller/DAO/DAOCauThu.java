package Controller.DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_CAUTHU;
import Model.Match;

import java.sql.*;
import java.util.ArrayList;

public class DAOCauThu implements DAOInterface<MODEL_CAUTHU>{

    @Override
    public void insertDB(MODEL_CAUTHU modelCauthu) {

    }

    @Override
    public void updateDB(MODEL_CAUTHU modelCauthu) {

    }

    @Override
    public int deleteDB(MODEL_CAUTHU modelCauthu) {
        return 0;
    }

    @Override
    public ArrayList<MODEL_CAUTHU> getFromRs(ResultSet rs) throws Exception {
        ArrayList<MODEL_CAUTHU> dsCauThu = new ArrayList<>();
        while (rs.next()) {
            MODEL_CAUTHU ct = new MODEL_CAUTHU();
            ct.setMaCT(Integer.parseInt(rs.getString("MaCT")));
            ct.setTenCT(rs.getString("TenCT"));
            ct.setNgaysinh(rs.getDate("NgaySinh"));
            ct.setLoaiCT(Integer.parseInt(rs.getString("LoaiCT")));
            dsCauThu.add(ct);
        }
        return dsCauThu;
    }

    @Override
    public ArrayList<MODEL_CAUTHU> selectAllDB() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        DatabaseConnection db = null;
        ArrayList<MODEL_CAUTHU> dsCauThu = new ArrayList<>();

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();

            String sql = "SELECT * FROM \"CauThu\"";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            dsCauThu =getFromRs(rs);
            System.out.println(dsCauThu);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return dsCauThu;
    }

    @Override
    public Match selectByID(int id) {
        return null;
    }

    @Override
    public ArrayList<MODEL_CAUTHU> selectByCondition(String Condition) {
        return null;
    }
}
