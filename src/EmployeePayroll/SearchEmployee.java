package EmployeePayroll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SearchEmployee extends JFrame {

    private JTextField empIdField;
    private JTable employeeTable;
    private DefaultTableModel tableModel;

    public SearchEmployee() {
        setTitle("Search Employee By ID");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JLabel empIdLabel = new JLabel("Enter Employee ID:");
        empIdField = new JTextField(10);
        JButton searchButton = new JButton("Search");
        JButton backButton = new JButton("Back");


        String[] columns = {
                "Employee ID", "Username", "Name", "Phone", "Email", "Gender", "Address",
                "CNIC", "Type", "Department", "Grade", "Hire Date"
        };
        tableModel = new DefaultTableModel(columns, 0);
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        JPanel inputPanel = new JPanel();
        inputPanel.add(empIdLabel);
        inputPanel.add(empIdField);
        inputPanel.add(searchButton);
        inputPanel.add(backButton);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int empId;
                try {
                    empId = Integer.parseInt(empIdField.getText().trim());
                    searchEmployeeById(empId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid Employee ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AdminHome().setVisible(true);
            }
        });
    }

    private void searchEmployeeById(int empId) {
        String procedure = "{call SearchEmployeeByID(?)}";

        try (Connection conn = ConnectionClass.getConnection();
             CallableStatement stmt = conn.prepareCall(procedure)) {

            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);

            if (rs.next()) {
                Object[] rowData = {
                        rs.getInt("employee_id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("cnic_num"),
                        rs.getString("employee_type"),
                        rs.getString("department_name"),
                        rs.getString("grade"),
                        rs.getDate("hire_date")
                };
                tableModel.addRow(rowData);
            } else {
                JOptionPane.showMessageDialog(this, "No employee found with ID: " + empId, "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

/*    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SearchEmployee().setVisible(true);
        });
    }*/
}
