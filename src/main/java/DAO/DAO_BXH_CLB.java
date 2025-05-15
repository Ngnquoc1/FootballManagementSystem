package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_BXH_CLB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAO_BXH_CLB implements DAOInterface<MODEL_BXH_CLB>{

    @Override
    public void insertDB(MODEL_BXH_CLB modelBxhClb) throws SQLException {

    }
    @Override
    public void updateDB(MODEL_BXH_CLB modelBxhClb) throws SQLException {

    }

    @Override
    public int deleteDB(MODEL_BXH_CLB modelBxhClb) throws SQLException {
        return 0;
    }

    @Override
    public MODEL_BXH_CLB getFromRs(ResultSet rs) throws Exception {
        MODEL_BXH_CLB modelBxhClb = new MODEL_BXH_CLB();
        modelBxhClb.setMaMG(rs.getInt("MaMG"));
        modelBxhClb.setMaCLB(rs.getInt("MaCLB"));
        modelBxhClb.setHang(rs.getInt("Hang"));
        modelBxhClb.setSoTran(rs.getInt("SoTran"));
        modelBxhClb.setThang(rs.getInt("Thang"));
        modelBxhClb.setHoa(rs.getInt("Hoa"));
        modelBxhClb.setThua(rs.getInt("Thua"));
        modelBxhClb.setHieuSo(rs.getInt("HieuSo"));
        modelBxhClb.setDiem(rs.getInt("Diem"));
        return modelBxhClb;
    }

    @Override
    public ArrayList<MODEL_BXH_CLB> selectAllDB() throws SQLException {
        ArrayList<MODEL_BXH_CLB> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM BANGXEPHANG_CLB ORDER BY Hang";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                try {
                    list.add(getFromRs(rs));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    @Override
    public MODEL_BXH_CLB selectByID(int id) throws SQLException {
        return null;
    }

    @Override
    public ArrayList<MODEL_BXH_CLB> selectByCondition(String Condition) throws SQLException {
        return null;
    }
}
