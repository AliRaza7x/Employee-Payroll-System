package EmployeePayroll;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;

public class AddEmployee extends JFrame {
    private JTextField nameField, phoneField, addressField, cnicField, emailField, hireDateField;
    private JRadioButton maleButton, femaleButton, otherButton;
    private JComboBox<String> employeeTypeComboBox, departmentComboBox, gradeComboBox;
    private JButton addButton, cancelButton;

    private int userId;

    public AddEmployee() {
        setTitle("Add Employee");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(0, 2, 10, 10));

        // Initialize components
        nameField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        cnicField = new JTextField();
        emailField = new JTextField();
        emailField.setEditable(false);
        hireDateField = new JTextField();

        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        otherButton = new JRadioButton("Other");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        genderGroup.add(otherButton);

        employeeTypeComboBox = new JComboBox<>(new String[] { "1", "2" }); // 1: Full-time, 2: Contract
        departmentComboBox = new JComboBox<>(new String[] { "1", "2", "3", "4", "5"});
        gradeComboBox = new JComboBox<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8" });

        addButton = new JButton("Add Employee");
        cancelButton = new JButton("Go Back");

        // Add components to frame
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
        add(addButton);
        add(new JLabel(""));
        add(cancelButton);

        // Generate user credentials and insert into Users table
        generateAndInsertUser();

        // Add listeners
        nameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                generateEmail();
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AdminHome().setVisible(true);
            }
        });

        setVisible(true);
    }

    private void generateAndInsertUser() {
        String username = generateRandomString(8);
        String password = generateRandomString(10);
        String role = "user";

        try (Connection conn = ConnectionClass.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Users (username, password, role) OUTPUT INSERTED.user_id VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inserting user.");
        }
    }

    private void generateEmail() {
        String fullName = nameField.getText().replaceAll("\\s+", "");
        if (!fullName.isEmpty() && userId != 0) {
            emailField.setText(fullName + userId + "@outlook.com");
        }
    }

    private void addEmployee() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String cnic = cnicField.getText();
        String email = emailField.getText();
        String hireDate = hireDateField.getText();
        String gender = maleButton.isSelected() ? "Male" : femaleButton.isSelected() ? "Female" : otherButton.isSelected() ? "Other" : "";

        int employeeTypeId = Integer.parseInt((String) employeeTypeComboBox.getSelectedItem());
        int departmentId = Integer.parseInt((String) departmentComboBox.getSelectedItem());
        int gradeId = Integer.parseInt((String) gradeComboBox.getSelectedItem());

        if (gender.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a gender.");
            return;
        }

        try (Connection conn = ConnectionClass.getConnection()) {

            // Check for duplicate CNIC
            PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM Employees WHERE cnic_num = ?");
            checkStmt.setString(1, cnic);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "An employee with this CNIC already exists.");
                return;
            }

            // Insert employee using stored procedure
            CallableStatement cs = conn.prepareCall("{call InsertEmployee(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            cs.setInt(1, userId);
            cs.setString(2, name);
            cs.setString(3, phone);
            cs.setString(4, email);
            cs.setString(5, gender);
            cs.setString(6, address);
            cs.setString(7, cnic);
            cs.setInt(8, employeeTypeId);
            cs.setInt(9, departmentId);
            cs.setInt(10, gradeId);
            cs.setString(11, hireDate);
            cs.execute();

            JOptionPane.showMessageDialog(this, "Employee added successfully.");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding employee.");
        }
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        while (sb.length() < length) {
            int index = (int) (rnd.nextFloat() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        new AddEmployee();
    }
}