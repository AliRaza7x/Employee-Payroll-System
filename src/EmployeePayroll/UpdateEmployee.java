package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;

public class UpdateEmployee extends JFrame {

    private JTextField empIdField, nameField, phoneField, addressField, hireDateField, genderField;
    private JComboBox<String> usernameBox, deptBox, typeBox;
    private HashMap<String, Integer> userMap = new HashMap<>();
    private HashMap<String, Integer> deptMap = new HashMap<>();
    private HashMap<String, Integer> typeMap = new HashMap<>();

    public UpdateEmployee() {
        setTitle("Update Employee");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(10, 2, 5, 5));

        // UI Components
        add(new JLabel("Employee ID:"));
        empIdField = new JTextField();
        add(empIdField);

        JButton searchBtn = new JButton("Search");
        add(searchBtn);
        add(new JLabel("")); // spacer

        add(new JLabel("Username:"));
        usernameBox = new JComboBox<>();
        add(usernameBox);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Phone:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Gender:"));
        genderField = new JTextField();
        genderField.setEditable(false);
        add(genderField);

        add(new JLabel("Address:"));
        addressField = new JTextField();
        add(addressField);

        add(new JLabel("Department:"));
        deptBox = new JComboBox<>();
        add(deptBox);

        add(new JLabel("Employee Type:"));
        typeBox = new JComboBox<>();
        add(typeBox);

        add(new JLabel("Hire Date (YYYY-MM-DD):"));
        hireDateField = new JTextField();
        add(hireDateField);

        JButton updateBtn = new JButton("Update");
        add(updateBtn);

        JButton backBtn = new JButton("Back");
        add(backBtn);

        // Load dropdown data
        loadDropdowns();

        // Actions
        searchBtn.addActionListener(e -> fetchEmployee());
        updateBtn.addActionListener(e -> updateEmployee());
        backBtn.addActionListener(e -> dispose());
    }

    private void loadDropdowns() {
        try (Connection con = ConnectionClass.getConnection()) {
            // Users
            ResultSet rs = con.createStatement().executeQuery("SELECT user_id, username FROM Users");
            while (rs.next()) {
                String name = rs.getString("username");
                int id = rs.getInt("user_id");
                userMap.put(name, id);
                usernameBox.addItem(name);
            }

            // Departments
            rs = con.createStatement().executeQuery("SELECT department_id, department_name FROM Departments");
            while (rs.next()) {
                String name = rs.getString("department_name");
                int id = rs.getInt("department_id");
                deptMap.put(name, id);
                deptBox.addItem(name);
            }

            // Employee Types
            rs = con.createStatement().executeQuery("SELECT employee_type_id, type_name FROM EmployeeType");
            while (rs.next()) {
                String name = rs.getString("type_name");
                int id = rs.getInt("employee_type_id");
                typeMap.put(name, id);
                typeBox.addItem(name);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Dropdown Load Error: " + e.getMessage());
        }
    }

    private void fetchEmployee() {
        try (Connection con = ConnectionClass.getConnection()) {
            int empId = Integer.parseInt(empIdField.getText().trim());
            String query = "SELECT e.*, u.username, d.department_name, et.type_name " +
                    "FROM Employees e " +
                    "JOIN Users u ON e.user_id = u.user_id " +
                    "JOIN Departments d ON e.department_id = d.department_id " +
                    "JOIN EmployeeType et ON e.employee_type_id = et.employee_type_id " +
                    "WHERE e.employee_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, empId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                usernameBox.setSelectedItem(rs.getString("username"));
                nameField.setText(rs.getString("name"));
                phoneField.setText(rs.getString("phone"));
                genderField.setText(rs.getString("gender"));
                addressField.setText(rs.getString("address"));
                deptBox.setSelectedItem(rs.getString("department_name"));
                typeBox.setSelectedItem(rs.getString("type_name"));
                hireDateField.setText(rs.getString("hire_date"));
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search Error: " + e.getMessage());
        }
    }

    private void updateEmployee() {
        try (Connection con = ConnectionClass.getConnection()) {
            CallableStatement stmt = con.prepareCall("{call UpdateEmployeeDetails(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setInt(1, Integer.parseInt(empIdField.getText().trim()));
            stmt.setInt(2, userMap.get(usernameBox.getSelectedItem().toString()));
            stmt.setString(3, nameField.getText().trim());
            stmt.setString(4, phoneField.getText().trim());
            stmt.setString(5, addressField.getText().trim());
            stmt.setInt(6, typeMap.get(typeBox.getSelectedItem().toString()));
            stmt.setInt(7, deptMap.get(deptBox.getSelectedItem().toString()));
            stmt.setDate(8, Date.valueOf(hireDateField.getText().trim()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Employee updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Update failed.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update Error: " + e.getMessage());
        }
    }

    // Main method to test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdateEmployee().setVisible(true));
    }
}
