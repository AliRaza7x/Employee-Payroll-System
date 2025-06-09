package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class AddEmployee extends JFrame {
    private JTextField nameField, phoneField, addressField, cnicField, emailField, hireDateField;
    private JRadioButton maleButton, femaleButton, otherButton;
    private JComboBox<String> employeeTypeComboBox, departmentComboBox, gradeComboBox;
    private JButton addButton, cancelButton;
    String username, password;
    private JFrame parentDashboard;
    private int userId;

    // Basic ID lists for selected values
    private ArrayList<Integer> employeeTypeIds = new ArrayList<>();
    private ArrayList<Integer> departmentIds = new ArrayList<>();
    private ArrayList<Integer> gradeIds = new ArrayList<>();

    public AddEmployee(JFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
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

        employeeTypeComboBox = new JComboBox<>();
        departmentComboBox = new JComboBox<>();
        gradeComboBox = new JComboBox<>();

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

        // Load combo box data
        loadComboBoxData();

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
                parentDashboard.setVisible(true);
            }
        });

        setVisible(true);
    }

    private void generateAndInsertUser() {
        username = generateRandomString(8);
        password = generateRandomString(10);
        String role = "user";
        String hashedPassword = hashPassword(password);

        try (Connection conn = ConnectionClass.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Users (username, password, role) OUTPUT INSERTED.user_id VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
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

    private void loadComboBoxData() {
        try (Connection conn = ConnectionClass.getConnection()) {

            PreparedStatement stmt1 = conn.prepareStatement("SELECT employee_type_id, type_name FROM EmployeeType ORDER BY employee_type_id");
            ResultSet rs1 = stmt1.executeQuery();
            while (rs1.next()) {
                employeeTypeIds.add(rs1.getInt("employee_type_id"));
                employeeTypeComboBox.addItem(rs1.getString("type_name"));
            }

            // Load Departments
            PreparedStatement stmt2 = conn.prepareStatement("SELECT department_id, department_name FROM Departments ORDER BY department_id");
            ResultSet rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                departmentIds.add(rs2.getInt("department_id"));
                departmentComboBox.addItem(rs2.getString("department_name"));
            }

            // Load Grades
            PreparedStatement stmt3 = conn.prepareStatement("SELECT grade_id, grade FROM Grades ORDER BY grade_id");
            ResultSet rs3 = stmt3.executeQuery();
            while (rs3.next()) {
                gradeIds.add(rs3.getInt("grade_id"));
                gradeComboBox.addItem(rs3.getString("grade"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading combo box data from database.");
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

        int employeeTypeId = employeeTypeIds.get(employeeTypeComboBox.getSelectedIndex());
        int departmentId = departmentIds.get(departmentComboBox.getSelectedIndex());
        int gradeId = gradeIds.get(gradeComboBox.getSelectedIndex());

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

//            JOptionPane.showMessageDialog(this, "Employee added successfully with username " + username + " and password " + password);
            String message = "Employee added successfully!\n\nUsername: " + username + "\nPassword: " + password;

            Object[] options = {"Copy", "OK"};

            int result = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Employee Added",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[1]
            );

            if (result == JOptionPane.YES_OPTION) {
                StringSelection selection = new StringSelection("Username: " + username + "\nPassword: " + password);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection,null);
            }

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
            int index = rnd.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
