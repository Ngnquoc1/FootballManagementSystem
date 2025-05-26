package DAO;

import Controller.Connection.DatabaseConnection;
import Model.MODEL_VONGDAU;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO_VONGDAU implements DAOInterface<MODEL_VONGDAU> {
    private Connection conn;

    public DAO_VONGDAU() {
        try {
            conn = DatabaseConnection.getInstance().getConnectionn();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void insertDB(MODEL_VONGDAU modelVongdau) throws SQLException {

    }

    @Override
    public void updateDB(MODEL_VONGDAU modelVongdau) throws SQLException {

    }

    @Override
    public int deleteDB(MODEL_VONGDAU modelVongdau) throws SQLException {
        return 0;
    }

    @Override
    public MODEL_VONGDAU getFromRs(ResultSet rs) throws Exception {
        return null;
    }

    @Override
    public ArrayList<MODEL_VONGDAU> selectAllDB() {
        return null;
    }

    @Override
    public MODEL_VONGDAU selectByID(int id) {
        return null;
    }

    @Override
    public ArrayList<MODEL_VONGDAU> selectByCondition(String Condition) {
        return null;
    }

}
