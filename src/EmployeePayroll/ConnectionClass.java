package EmployeePayroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {
    public Connection con;

    public ConnectionClass() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=employeePayrollDB;encrypt=true;trustServerCertificate=true";
        String user = "sa";
        String password = "dblab";

        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to SQL Server successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Connection failed.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=employeePayrollDB;encrypt=true;trustServerCertificate=true";
        String user = "sa";
        String password = "dblab";

        try {
            Connection con = DriverManager.getConnection(url, user, password);
            return con;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
