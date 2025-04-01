package EECS3311.DAO;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/parkingdb";
    private static final String TEST_URL = "jdbc:postgresql://localhost:5432/parkingdb_test";
    private static final String USER = "app";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(URL); // Use the class-level constant
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);
        return dataSource.getConnection();
    }

}
