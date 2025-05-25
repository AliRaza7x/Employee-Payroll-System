package EmployeePayroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCTest {
    public static void main(String[] args) {
        // JDBC connection parameters
        String url = "jdbc:sqlserver://localhost:1433;databaseName=employeePayrollDB;encrypt=true;trustServerCertificate=true";
        String username = "sa";
        String password = "dblab";

        try {
            // Register the JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Attempt to establish connection
            System.out.println("Connecting to database...");
            Connection connection = DriverManager.getConnection(url, username, password);

            if (connection != null) {
                System.out.println("Database connection successful!");
                connection.close();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }
}