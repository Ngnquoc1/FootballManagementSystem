package Controller.DAO;


import Model.Match;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DAOInterface<T> {
    public void insertDB(T t) throws SQLException;

    public void updateDB(T t) throws SQLException;

    public int deleteDB(T t) throws SQLException;

    public T getFromRs(ResultSet rs) throws Exception;

    public ArrayList<T> selectAllDB();

    public Match selectByID(int id);

    public ArrayList<T> selectByCondition(String Condition);
}
