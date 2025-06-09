package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UpdateEmployee extends JFrame {
    private JTextField idField, nameField, phoneField, addressField, cnicField, emailField, hireDateField;
    private JRadioButton maleButton, femaleButton, otherButton;
    private JComboBox<String> employeeTypeComboBox, departmentComboBox, gradeComboBox;
    private JButton updateButton, cancelButton, fetchButton;
    private JFrame parentDashboard;

    public UpdateEmployee(JFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        setTitle("Update Employee");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(0, 2, 10, 10));

        idField = new JTextField();
        nameField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        cnicField = new JTextField();
        emailField = new JTextField();
        hireDateField = new JTextField();

        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        otherButton = new JRadioButton("Other");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        genderGroup.add(otherButton);

        employeeTypeComboBox = new JComboBox<>(new String[]{"1", "2"});
        departmentComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
        gradeComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});

        updateButton = new JButton("Update");
        cancelButton = new JButton("Back");
        fetchButton = new JButton("Fetch");

        add(new JLabel("Employee ID:"));
        JPanel fetchPanel = new JPanel(new BorderLayout());
        fetchPanel.add(idField, BorderLayout.CENTER);
        fetchPanel.add(fetchButton, BorderLayout.EAST);
        add(fetchPanel);

        add(new JLabel("Full Name:"));
        add(nameField);
        add(new JLabel("Phone:"));
        add(phoneField);
        add(new JLabel("Address:"));
        add(addressField);
        add(new JLabel("CNIC:"));
        add(cnicField);
        add(new JLabel("Gender:"));
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        genderPanel.add(otherButton);
        add(genderPanel);
        add(new JLabel("Employee Type:"));
        add(employeeTypeComboBox);
        add(new JLabel("Department:"));
        add(departmentComboBox);
        add(new JLabel("Grade:"));
        add(gradeComboBox);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Hire Date (YYYY-MM-DD):"));
        add(hireDateField);
        add(new JLabel(""));
        add(updateButton);
        add(new JLabel(""));
        add(cancelButton);

        fetchButton.addActionListener(e -> fetchEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        cancelButton.addActionListener(e -> {
            dispose();
            parentDashboard.setVisible(true);
        });

        setVisible(true);
    }

    private void fetchEmployee() {
        try (Connection conn = ConnectionClass.getConnection()) {
            int empId = Integer.parseInt(idField.getText());
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Employees WHERE employee_id = ?");
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                phoneField.setText(rs.getString("phone"));
                addressField.setText(rs.getString("address"));
                cnicField.setText(rs.getString("cnic_num"));
                emailField.setText(rs.getString("email"));
                hireDateField.setText(rs.getString("hire_date"));

                String gender = rs.getString("gender");
                if (gender.equalsIgnoreCase("Male")) maleButton.setSelected(true);
                else if (gender.equalsIgnoreCase("Female")) femaleButton.setSelected(true);
                else otherButton.setSelected(true);

                employeeTypeComboBox.setSelectedItem(String.valueOf(rs.getInt("employee_type_id")));
                departmentComboBox.setSelectedItem(String.valueOf(rs.getInt("department_id")));
                gradeComboBox.setSelectedItem(String.valueOf(rs.getInt("grade_id")));
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching employee.");
        }
    }

    private void updateEmployee() {
        try (Connection conn = ConnectionClass.getConnection()) {
            CallableStatement cs = conn.prepareCall("{call UpdateEmployeeByID(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            cs.setInt(1, Integer.parseInt(idField.getText()));
            cs.setString(2, nameField.getText());
            cs.setString(3, phoneField.getText());
            cs.setString(4, emailField.getText());

            String gender = maleButton.isSelected() ? "Male" : femaleButton.isSelected() ? "Female" : "Other";
            cs.setString(5, gender);

            cs.setString(6, addressField.getText());
            cs.setString(7, cnicField.getText());
            cs.setInt(8, Integer.parseInt((String) employeeTypeComboBox.getSelectedItem()));
            cs.setInt(9, Integer.parseInt((String) departmentComboBox.getSelectedItem()));
            cs.setInt(10, Integer.parseInt((String) gradeComboBox.getSelectedItem()));
            cs.setDate(11, Date.valueOf(hireDateField.getText()));

            cs.execute();
            JOptionPane.showMessageDialog(this, "Employee updated successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating employee.");
        }
    }
}
