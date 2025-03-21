package EECS3311.DAO;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/parkingdb";;
    private static final String USER = "app";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        // The url specifies the address of our database along with username and password credentials
        // you should replace these with your own username and password
        final String url =
                "jdbc:postgresql://localhost:5432/parkingdb?user=app&password=password";
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        return dataSource.getConnection();
    }
}
