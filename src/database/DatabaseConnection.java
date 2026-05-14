package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private static final String URL = "jdbc:mariadb://localhost:3306/gestion_examenes";
    private static final String USER = "root";
    private static final String PASSWORD = "Mbappe";
    
    private static Connection connection = null;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}