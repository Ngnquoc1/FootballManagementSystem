package Controller.DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_MUAGIAI;
import Model.Match;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DAO_MUAGIAI implements DAOInterface<MODEL_MUAGIAI>{

    @Override
    public void insertDB(MODEL_MUAGIAI modelMuagiai) {
    }

    @Override
    public void updateDB(MODEL_MUAGIAI modelMuagiai) {
        return ;
    }

    @Override
    public int deleteDB(MODEL_MUAGIAI modelMuagiai) {
        return 0;
    }

    @Override
    public MODEL_MUAGIAI getFromRs(ResultSet rs) throws Exception {
            MODEL_MUAGIAI clb = new MODEL_MUAGIAI();
            clb.setMaMG(Integer.parseInt(rs.getString("MaMG")));
            clb.setTenMG(rs.getString("TenMG"));
            clb.setNgayBD(rs.getDate("NgayKhaiMac"));
            clb.setNgayKT(rs.getDate("NgayBeMac"));
        return clb;
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
    public Match selectByID(int id) {
        return null;
    }

    @Override
    public ArrayList<MODEL_MUAGIAI> selectByCondition(String Condition) {
        return null;
    }
}
