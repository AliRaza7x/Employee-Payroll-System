package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EmployeeDetailsWindow extends JFrame {
    private int userId;
    private JPanel detailsPanel;
    private JButton backButton;

    public EmployeeDetailsWindow(int userId) {
        this.userId = userId;

        setTitle("Employee Details");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Center panel for employee details
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        detailsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for back button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(240, 240, 240));
        backButton = new JButton("← Back to Home");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.DARK_GRAY);
        backButton.setFocusPainted(false);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            new UserHome(userId).setVisible(true);
            dispose();
        });

        loadEmployeeDetails();
    }

    private void loadEmployeeDetails() {
        try {
            ConnectionClass obj = new ConnectionClass();
            CallableStatement cs = obj.con.prepareCall("{call GetEmployeeDetailsByUserId(?)}");
            cs.setInt(1, userId);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                addDetail("Employee ID", rs.getString("employee_id"));
                addDetail("Name", rs.getString("name"));
                addDetail("Phone", rs.getString("phone"));
                addDetail("Email", rs.getString("email"));
                addDetail("Gender", rs.getString("gender"));
                addDetail("Address", rs.getString("address"));
                addDetail("CNIC", rs.getString("cnic_num"));
                addDetail("Employee Type", rs.getString("employee_type"));
                addDetail("Department", rs.getString("department_name"));
                addDetail("Grade", rs.getString("grade"));
                addDetail("Hire Date", rs.getString("hire_date"));
            } else {
                JLabel notFoundLabel = new JLabel("No employee details found.");
                notFoundLabel.setFont(new Font("Arial", Font.BOLD, 14));
                notFoundLabel.setForeground(Color.RED);
                detailsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                detailsPanel.add(notFoundLabel);
            }

            rs.close();
            cs.close();
            obj.con.close();
        } catch (Exception ex) {
            JLabel errorLabel = new JLabel("Error loading employee details.");
            errorLabel.setForeground(Color.RED);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
            detailsPanel.add(errorLabel);
            ex.printStackTrace();
        }
    }

    private void addDetail(String label, String value) {
        JLabel keyLabel = new JLabel(label + ": ");
        keyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        keyLabel.setForeground(Color.DARK_GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        valueLabel.setForeground(Color.BLACK);

        detailsPanel.add(keyLabel);
        detailsPanel.add(valueLabel);
    }
}
