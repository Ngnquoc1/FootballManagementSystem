package Controller.DAO;

import Model.MODEL_KETQUATD;

public class DAO_KQUATRANDAU implements DAOInterface<MODEL_KETQUATD>{
    @Override
    public void insertDB(MODEL_KETQUATD model) {

    }

    @Override
    public void updateDB(MODEL_KETQUATD model) {

    }

    @Override
    public int deleteDB(MODEL_KETQUATD model) {
        return 0;
    }

    @Override
    public MODEL_KETQUATD getFromRs(java.sql.ResultSet rs) throws Exception {
        return null;
    }

    @Override
    public java.util.ArrayList<MODEL_KETQUATD> selectAllDB() {
        return null;
    }

    @Override
    public Model.Match selectByID(int id) {
        return null;
    }

    @Override
    public java.util.ArrayList<MODEL_KETQUATD> selectByCondition(String Condition) {
        return null;
    }
}
