package DAO;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DAOInterface<T> {
    public void insertDB(T t) throws SQLException;

    public void updateDB(T t) throws SQLException;

    public int deleteDB(T t) throws SQLException;

    public T getFromRs(ResultSet rs) throws Exception;

    public ArrayList<T> selectAllDB() throws SQLException;

    public T selectByID(int id) throws SQLException;

    public ArrayList<T> selectByCondition(String Condition) throws SQLException;
}
