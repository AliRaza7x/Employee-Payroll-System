package EmployeePayroll;

import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=employeePayrollDB;encrypt=true;trustServerCertificate=true";
        String user = "sa";
        String password = "ksbl1234";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to SQL Server successfully!");
            con.close();
        } catch (Exception e) {
            System.out.println("❌ Connection failed.");
            e.printStackTrace();
        }
    }
}

