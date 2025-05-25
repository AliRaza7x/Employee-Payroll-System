package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserHome extends JFrame implements ActionListener {
    JButton checkInBtn, checkOutBtn, viewLeavesBtn, requestLeaveBtn, generateSlipBtn, exitBtn, viewDetailsBtn;
    JLabel background, empDetailsLabel;
    private int userId;

    public UserHome(int userId) {
        this.userId = userId;

        setTitle("User Homepage");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        ImageIcon bgIcon = new ImageIcon(ClassLoader.getSystemResource("Images/background.jpg"));
        Image scaledBG = bgIcon.getImage().getScaledInstance(800, 500, Image.SCALE_SMOOTH);
        background = new JLabel(new ImageIcon(scaledBG));
        background.setBounds(0, 0, 800, 500);
        background.setLayout(null);
        add(background);

        ImageIcon rawExitIcon = new ImageIcon(ClassLoader.getSystemResource("Images/switch.png"));
        Image exitImg = rawExitIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        exitBtn = new JButton(new ImageIcon(exitImg));
        exitBtn.setBounds(10, 10, 30, 30);
        exitBtn.setBorderPainted(false);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setFocusPainted(false);
        exitBtn.addActionListener(e -> System.exit(0));
        background.add(exitBtn);

        Font btnFont = new Font("Arial", Font.BOLD, 14);
        checkInBtn = createButton("Check-In", "attendance.png", 500, 60, btnFont);
        checkOutBtn = createButton("Check-Out", "logout.png", 500, 110, btnFont);
        viewLeavesBtn = createButton("View Leaves/Absences", "eye.png", 500, 160, btnFont);
        requestLeaveBtn = createButton("Request Leave", "leave.png", 500, 210, btnFont);
        generateSlipBtn = createButton("Generate Payroll Slip", "payslip.png", 500, 260, btnFont);
        viewDetailsBtn = createButton("View Employee Details", "details.png", 500, 310, btnFont); // new button

        background.add(checkInBtn);
        background.add(checkOutBtn);
        background.add(viewLeavesBtn);
        background.add(requestLeaveBtn);
        background.add(generateSlipBtn);
        background.add(viewDetailsBtn);  // add new button

        checkInBtn.addActionListener(this);
        checkOutBtn.addActionListener(this);
        viewLeavesBtn.addActionListener(this);
        requestLeaveBtn.addActionListener(this);
        generateSlipBtn.addActionListener(this);
        viewDetailsBtn.addActionListener(this); // listener for new button

        // Add label to display employee details
        empDetailsLabel = new JLabel();
        empDetailsLabel.setBounds(50, 60, 400, 200);
        empDetailsLabel.setForeground(Color.WHITE);
        empDetailsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        background.add(empDetailsLabel);

        // Load and show employee data
        displayEmployeeDetails(userId);
    }

    private JButton createButton(String text, String iconFile, int x, int y, Font font) {
        ImageIcon rawIcon = new ImageIcon(ClassLoader.getSystemResource("Images/" + iconFile));
        Image scaledIcon = rawIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        JButton button = new JButton(text, new ImageIcon(scaledIcon));
        button.setBounds(x, y, 230, 40);
        button.setFont(font);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }

    private void displayEmployeeDetails(int userId) {
        try {
            ConnectionClass obj = new ConnectionClass();
            CallableStatement cs = obj.con.prepareCall("{call GetEmployeeDetailsByUserId(?)}");
            cs.setInt(1, userId);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                String empId = rs.getString("employee_id");
                String name = rs.getString("name");
                String dept = rs.getString("department_name");
                String grade = rs.getString("grade");

                String details = "<html><body style='color:white; font-family: Arial; font-size: 14px;'>"
                        + "<table cellpadding='5' cellspacing='0' border='0'>"
                        + "<tr><td><b>Employee ID:</b></td><td>" + empId + "</td></tr>"
                        + "<tr><td><b>Name:</b></td><td>" + name + "</td></tr>"
                        + "<tr><td><b>Department:</b></td><td>" + dept + "</td></tr>"
                        + "<tr><td><b>Grade:</b></td><td>" + grade + "</td></tr>"
                        + "</table></body></html>";

                empDetailsLabel.setText(details);
            } else {
                empDetailsLabel.setText("Employee details not found.");
            }
            rs.close();
            cs.close();
            obj.con.close();
        } catch (Exception ex) {
            empDetailsLabel.setText("Error loading details.");
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkInBtn) {
            new CheckIn(userId).setVisible(true);
        } else if (e.getSource() == checkOutBtn) {
            new CheckOut(userId).setVisible(true);
        } else if (e.getSource() == viewLeavesBtn) {
            new ViewAllAbsences().setVisible(true);
        } else if (e.getSource() == requestLeaveBtn) {
            new RequestLeave(userId).setVisible(true);
        } else if (e.getSource() == generateSlipBtn) {
            // new GenerateOwnSlip(userId).setVisible(true);
        } else if (e.getSource() == viewDetailsBtn) {
            new EmployeeDetailsWindow(userId).setVisible(true);
        }
        this.setVisible(false);
    }
}
