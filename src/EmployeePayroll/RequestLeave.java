package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class RequestLeave extends JFrame {
    private JComboBox<String> leaveTypeCombo;
    private JTextField startDateField, endDateField;
    private JTextArea reasonField;
    private JButton submitBtn, backBtn;
    private int userId;

    public RequestLeave(int userId) {
        this.userId = userId;
        setTitle("Request Leave");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        LocalDate today = LocalDate.now();

        // Leave Type
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Leave Type:"), gbc);
        leaveTypeCombo = new JComboBox<>();
        populateLeaveTypes();
        gbc.gridx = 1;
        panel.add(leaveTypeCombo, gbc);

        // Reason
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Reason:"), gbc);
        reasonField = new JTextArea(4, 20);
        reasonField.setLineWrap(true);
        reasonField.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(reasonField);
        gbc.gridx = 1;
        panel.add(scrollPane, gbc);

        // Start Date
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Start Date:"), gbc);
        startDateField = new JTextField(today.toString());
        gbc.gridx = 1;
        panel.add(startDateField, gbc);

        // End Date
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("End Date:"), gbc);
        endDateField = new JTextField(today.toString());
        gbc.gridx = 1;
        panel.add(endDateField, gbc);

        // Buttons
        submitBtn = new JButton("Submit");
        backBtn = new JButton("Back");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.add(submitBtn);
        btnPanel.add(backBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        add(panel);
        setVisible(true);

        submitBtn.addActionListener(e -> submitLeave());
        backBtn.addActionListener(e -> {
            new UserHome(userId).setVisible(true);
            dispose();
        });
    }

    private void populateLeaveTypes() {
        try (Connection con = ConnectionClass.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT type_name FROM LeaveTypes")) {

            while (rs.next()) {
                leaveTypeCombo.addItem(rs.getString("type_name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading leave types: " + e.getMessage());
        }
    }

    private void submitLeave() {
        String reason = reasonField.getText().trim();
        String selectedType = (String) leaveTypeCombo.getSelectedItem();

        LocalDate startDate, endDate;

        try {
            startDate = LocalDate.parse(startDateField.getText().trim());
            endDate = LocalDate.parse(endDateField.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid start and end dates (yyyy-mm-dd).");
            return;
        }

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reason cannot be empty.");
            return;
        }

        try (Connection con = ConnectionClass.getConnection()) {
            // Get employee_id
            PreparedStatement empStmt = con.prepareStatement("SELECT employee_id FROM Employees WHERE user_id = ?");
            empStmt.setInt(1, userId);
            ResultSet empRs = empStmt.executeQuery();
            int employeeId = -1;
            if (empRs.next()) {
                employeeId = empRs.getInt("employee_id");
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found for the given user ID.");
                return;
            }

            // Get leave_type_id
            PreparedStatement typeStmt = con.prepareStatement("SELECT leave_type_id FROM LeaveTypes WHERE type_name = ?");
            typeStmt.setString(1, selectedType);
            ResultSet rs = typeStmt.executeQuery();
            int typeId = -1;
            if (rs.next()) {
                typeId = rs.getInt("leave_type_id");
            }

            // Call stored procedure
            CallableStatement cs = con.prepareCall("{call InsertLeave (?, ?, ?, ?, ?)}");
            cs.setInt(1, employeeId);
            cs.setDate(2, Date.valueOf(startDate));
            cs.setDate(3, Date.valueOf(endDate));
            cs.setString(4, reason);
            cs.setInt(5, typeId);

            cs.execute();
            JOptionPane.showMessageDialog(this, "Leave request submitted successfully!");
            reasonField.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
