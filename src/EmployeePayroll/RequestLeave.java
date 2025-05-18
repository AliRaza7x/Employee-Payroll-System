package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class RequestLeave extends JFrame {
    private JComboBox<String> leaveTypeCombo;
    private JTextField dateField;
    private JTextArea reasonField;
    private JButton submitBtn, backBtn;
    private int userId;

    public RequestLeave(int userId) {
        this.userId = userId;
        setTitle("Request Leave");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use a panel with GridBagLayout
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
        panel.add(reasonField, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Date:"), gbc);
        dateField = new JTextField(today.toString());
        dateField.setEditable(false);
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        // Buttons
        submitBtn = new JButton("Submit");
        backBtn = new JButton("Back");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.add(submitBtn);
        btnPanel.add(backBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
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
        LocalDate today = LocalDate.now();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reason cannot be empty.");
            return;
        }

        try (Connection con = ConnectionClass.getConnection()) {
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

            PreparedStatement typeStmt = con.prepareStatement("SELECT leave_type_id FROM LeaveTypes WHERE type_name = ?");
            typeStmt.setString(1, selectedType);
            ResultSet rs = typeStmt.executeQuery();
            int typeId = -1;
            if (rs.next()) {
                typeId = rs.getInt("leave_type_id");
            }

            CallableStatement cs = con.prepareCall("{call InsertLeave (?, ?, ?, ?)}");
            cs.setInt(1, employeeId);
            cs.setDate(2, Date.valueOf(today));
            cs.setString(3, reason);
            cs.setInt(4, typeId);

            cs.execute();
            JOptionPane.showMessageDialog(this, "Leave request submitted successfully!");
            reasonField.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
