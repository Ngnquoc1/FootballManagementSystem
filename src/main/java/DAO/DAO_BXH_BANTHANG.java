package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_BXH_BANTHANG;
import Model.MODEL_BXH_CLB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAO_BXH_BANTHANG implements DAOInterface<MODEL_BXH_BANTHANG> {

    @Override
    public void insertDB(MODEL_BXH_BANTHANG modelBxhBanthang) throws SQLException {

    }

    @Override
    public void updateDB(MODEL_BXH_BANTHANG modelBxhBanthang) throws SQLException {

    }

    @Override
    public int deleteDB(MODEL_BXH_BANTHANG modelBxhBanthang) throws SQLException {
        return 0;
    }

    @Override
    public MODEL_BXH_BANTHANG getFromRs(ResultSet rs) throws Exception {
        MODEL_BXH_BANTHANG modelBxhCT = new MODEL_BXH_BANTHANG();
        modelBxhCT.setMaMG(rs.getInt("MaMG"));
        modelBxhCT.setMaCT(rs.getInt("MaCT"));
        modelBxhCT.setHang(rs.getInt("XepHang"));
        modelBxhCT.setSoBanThang(rs.getInt("SoBanThang"));
        modelBxhCT.setPenalty(rs.getInt("Penalty"));
        return modelBxhCT;
    }

    @Override
    public ArrayList<MODEL_BXH_BANTHANG> selectAllDB() throws SQLException {
        return null;
    }

    @Override
    public MODEL_BXH_BANTHANG selectByID(int id) throws SQLException {
        return null;
    }

    @Override
    public ArrayList<MODEL_BXH_BANTHANG> selectByCondition(String Condition) throws SQLException {
        ArrayList<MODEL_BXH_BANTHANG> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnectionn();
        String sql = "SELECT * FROM BANGXEPHANG_BANTHANG WHERE " + Condition+" ORDER BY XepHang";
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
}
