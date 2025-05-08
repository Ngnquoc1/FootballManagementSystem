package Controller.DAO;

import Model.MODEL_VONGDAU;
import Model.Match;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Dao_VONGDAU implements DAOInterface<MODEL_VONGDAU> {


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
    public Match selectByID(int id) {
        return null;
    }

    @Override
    public ArrayList<MODEL_VONGDAU> selectByCondition(String Condition) {
        return null;
    }
}
