package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EmployeeDetailsWindow extends JFrame {
    private int userId;
    private JTextPane textPane;
    private JButton backButton;

    public EmployeeDetailsWindow(int userId) {
        this.userId = userId;

        setTitle("Employee Details");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Center Panel with text
        textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setBackground(Color.WHITE);
        textPane.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel with Back button
        JPanel bottomPanel = new JPanel();
        backButton = new JButton("Back to Home");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Back button action
        backButton.addActionListener(e -> {
            new UserHome(userId).setVisible(true);
            dispose();
        });

        // Load data
        loadEmployeeDetails();
    }

    private void loadEmployeeDetails() {
        try {
            ConnectionClass obj = new ConnectionClass();
            CallableStatement cs = obj.con.prepareCall("{call GetEmployeeDetailsByUserId(?)}");
            cs.setInt(1, userId);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                String empId = rs.getString("employee_id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String gender = rs.getString("gender");
                String address = rs.getString("address");
                String cnic = rs.getString("cnic_num");
                String empType = rs.getString("employee_type");
                String dept = rs.getString("department_name");
                String grade = rs.getString("grade");
                String hireDate = rs.getString("hire_date");

                String html = "<html><body style='font-family: Arial; font-size: 14px; color: black;'>"
                        + "<h2>Employee Details</h2>"
                        + "<table cellpadding='8'>"
                        + "<tr><td><b>Employee ID:</b></td><td>" + empId + "</td></tr>"
                        + "<tr><td><b>Name:</b></td><td>" + name + "</td></tr>"
                        + "<tr><td><b>Phone:</b></td><td>" + phone + "</td></tr>"
                        + "<tr><td><b>Email:</b></td><td>" + email + "</td></tr>"
                        + "<tr><td><b>Gender:</b></td><td>" + gender + "</td></tr>"
                        + "<tr><td><b>Address:</b></td><td>" + address + "</td></tr>"
                        + "<tr><td><b>CNIC:</b></td><td>" + cnic + "</td></tr>"
                        + "<tr><td><b>Employee Type:</b></td><td>" + empType + "</td></tr>"
                        + "<tr><td><b>Department:</b></td><td>" + dept + "</td></tr>"
                        + "<tr><td><b>Grade:</b></td><td>" + grade + "</td></tr>"
                        + "<tr><td><b>Hire Date:</b></td><td>" + hireDate + "</td></tr>"
                        + "</table></body></html>";

                textPane.setText(html);
            } else {
                textPane.setText("No employee details found.");
            }

            rs.close();
            cs.close();
            obj.con.close();
        } catch (Exception ex) {
            textPane.setText("Error loading details.");
            ex.printStackTrace();
        }
    }
}
