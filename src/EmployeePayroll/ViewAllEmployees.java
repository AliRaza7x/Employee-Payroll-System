package EmployeePayroll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewAllEmployees extends JFrame {

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    public ViewAllEmployees() {
        setTitle("View All Employees");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadEmployeeData();
    }

    private void initComponents() {
        String[] columns = {
                "Employee ID", "Username", "Name", "Phone", "Gender", "Address",
                "Type", "Department", "Hire Date"
        };

        tableModel = new DefaultTableModel(columns, 0);
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new AdminHome().setVisible(true);
        });

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadEmployeeData() {
        String procedure = "{call ViewAllEmployees}";

        try (Connection conn = ConnectionClass.getConnection();
             CallableStatement stmt = conn.prepareCall(procedure);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] rowData = {
                        rs.getInt("employee_id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("type_name"),
                        rs.getString("department_name"),
                        rs.getDate("hire_date")
                };
                tableModel.addRow(rowData);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewAllEmployees().setVisible(true));
    }
}
