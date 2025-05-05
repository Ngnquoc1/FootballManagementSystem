package Controller.DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_CLB;
import Model.Match;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAO_CLB implements DAOInterface<MODEL_CLB> {
    @Override
    public void insertDB(MODEL_CLB model) {

    }

    @Override
    public void updateDB(MODEL_CLB model) {

    }

    @Override
    public int deleteDB(MODEL_CLB model) {
        return 0;
    }

    @Override
    public java.util.ArrayList<MODEL_CLB> getFromRs(java.sql.ResultSet rs) throws Exception {
        ArrayList<MODEL_CLB> ds = new ArrayList<>();
        while (rs.next()) {
            MODEL_CLB clb = new MODEL_CLB();
            clb.setMaCLB(Integer.parseInt(rs.getString("MaCLB")));
            clb.setTenCLB(rs.getString("TenCLB"));
            clb.setMaSan(Integer.parseInt(rs.getString("SanNha")));
            clb.setLogoCLB(rs.getString("LogoCLB"));
            ds.add(clb);
        }
        return ds;
    }

    @Override
    public ArrayList<MODEL_CLB> selectAllDB() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        DatabaseConnection db = null;
        ArrayList<MODEL_CLB> ds = new ArrayList<>();

        try {
            db = DatabaseConnection.getInstance();
            conn = db.getConnectionn();

            String sql = "SELECT * FROM \"CLB\"";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ds =getFromRs(rs);
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
    public Match selectByID(int id) {
        return null;
    }

    @Override
    public java.util.ArrayList<MODEL_CLB> selectByCondition(String Condition) {
        return null;
    }
}
