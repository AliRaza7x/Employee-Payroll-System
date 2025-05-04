package EmployeePayroll;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddEmployee extends JFrame {
    JTextField nameField, phoneField, addressField, hireDateField;
    JComboBox<String> employeeTypeIdCombo, departmentIdCombo;
    JRadioButton male, female, other;
    ButtonGroup genderGroup;
    int userId;

    public AddEmployee(int userId) {
        this.userId = userId;

        setTitle("Add Employee Details");
        setSize(400, 500);
        setLayout(new GridLayout(12, 2, 10, 10));

        nameField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        hireDateField = new JTextField();

        // Dropdowns for Employee Type ID (1, 2) and Department ID (1-5)
        employeeTypeIdCombo = new JComboBox<>(new String[] {"1", "2"});
        departmentIdCombo = new JComboBox<>(new String[] {"1", "2", "3", "4", "5"});

        male = new JRadioButton("Male");
        female = new JRadioButton("Female");
        other = new JRadioButton("Other");
        genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        genderGroup.add(other);

        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("Phone:")); add(phoneField);
        add(new JLabel("Address:")); add(addressField);
        add(new JLabel("Gender:"));
        JPanel genderPanel = new JPanel();
        genderPanel.add(male); genderPanel.add(female); genderPanel.add(other);
        add(genderPanel);
        add(new JLabel("Employee Type ID:")); add(employeeTypeIdCombo);
        add(new JLabel("Department ID:")); add(departmentIdCombo);
        add(new JLabel("Hire Date (YYYY-MM-DD):")); add(hireDateField);

        JButton addBtn = new JButton("Add Employee");
        JButton backBtn = new JButton("Go Back");
        add(addBtn); add(backBtn);

        addBtn.addActionListener(e -> {
            try {
                ConnectionClass connectionClass = new ConnectionClass();
                Connection con = connectionClass.con;

                CallableStatement stmt = con.prepareCall("{call InsertEmployee(?, ?, ?, ?, ?, ?, ?, ?)}");
                stmt.setInt(1, userId);
                stmt.setString(2, nameField.getText());
                stmt.setString(3, phoneField.getText());

                String gender = male.isSelected() ? "Male" : female.isSelected() ? "Female" : "Other";
                stmt.setString(4, gender);
                stmt.setString(5, addressField.getText());

                int empTypeId = Integer.parseInt((String) employeeTypeIdCombo.getSelectedItem());
                int deptId = Integer.parseInt((String) departmentIdCombo.getSelectedItem());

                stmt.setInt(6, empTypeId);
                stmt.setInt(7, deptId);
                stmt.setDate(8, Date.valueOf(hireDateField.getText()));
                stmt.execute();

                JOptionPane.showMessageDialog(this, "Employee Added Successfully");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> {
            new AddUserWindow();
            dispose();
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}