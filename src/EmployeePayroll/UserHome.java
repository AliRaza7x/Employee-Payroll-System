package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserHome extends JFrame implements ActionListener {
    JButton checkInBtn, checkOutBtn, viewLeavesBtn, requestLeaveBtn, generateSlipBtn, exitBtn, viewDetailsBtn,
            viewAbsencesBtn;
    private int userId;
    private JPanel infoPanel;

    public UserHome(int userId) {
        this.userId = userId;

        setTitle("User Homepage");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Background image
        ImageIcon bgIcon = new ImageIcon(ClassLoader.getSystemResource("Images/background.jpg"));
        Image scaledBG = bgIcon.getImage().getScaledInstance(800, 500, Image.SCALE_SMOOTH);
        JLabel background = new JLabel(new ImageIcon(scaledBG));
        background.setBounds(0, 0, 800, 500);
        background.setLayout(null);
        add(background);

        // Exit Button
        ImageIcon rawExitIcon = new ImageIcon(ClassLoader.getSystemResource("Images/switch.png"));
        Image exitImg = rawExitIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        exitBtn = new JButton(new ImageIcon(exitImg));
        exitBtn.setBounds(10, 10, 30, 30);
        exitBtn.setBorderPainted(false);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setFocusPainted(false);
        exitBtn.addActionListener(e -> System.exit(0));
        background.add(exitBtn);

        // Font for buttons
        Font btnFont = new Font("Arial", Font.BOLD, 14);

        // Create and add buttons
        checkInBtn = createButton("Check-In", "attendance.png", 500, 60, btnFont);
        checkOutBtn = createButton("Check-Out", "logout.png", 500, 110, btnFont);
        viewAbsencesBtn = createButton("View Absences", "eye.png", 500, 160, btnFont);
        viewLeavesBtn = createButton("View Leaves", "eye.png", 500, 210, btnFont);
        requestLeaveBtn = createButton("Request Leave", "leave.png", 500, 260, btnFont);
        generateSlipBtn = createButton("Generate Payroll Slip", "payslip.png", 500, 310, btnFont);
        viewDetailsBtn = createButton("View Employee Details", "details.png", 500, 360, btnFont);

        background.add(checkInBtn);
        background.add(checkOutBtn);
        background.add(viewAbsencesBtn);
        background.add(viewLeavesBtn);
        background.add(requestLeaveBtn);
        background.add(generateSlipBtn);
        background.add(viewDetailsBtn);

        // Button actions
        checkInBtn.addActionListener(this);
        checkOutBtn.addActionListener(this);
        viewAbsencesBtn.addActionListener(this);
        viewLeavesBtn.addActionListener(this);
        requestLeaveBtn.addActionListener(this);
        generateSlipBtn.addActionListener(this);
        viewDetailsBtn.addActionListener(this);

        // Info Panel
        infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        infoPanel.setOpaque(false);
        infoPanel.setBounds(50, 60, 400, 140);
        background.add(infoPanel);

        displayEmployeeDetails(userId);
    }

    public JButton createButton(String text, String iconName, int x, int y, Font font) {
        ImageIcon rawIcon = new ImageIcon(ClassLoader.getSystemResource("Images/" + iconName));
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
                infoPanel.add(createLabel("Employee ID:"));
                infoPanel.add(createValue(rs.getString("employee_id")));

                infoPanel.add(createLabel("Name:"));
                infoPanel.add(createValue(rs.getString("name")));

                infoPanel.add(createLabel("Department:"));
                infoPanel.add(createValue(rs.getString("department_name")));

                infoPanel.add(createLabel("Grade:"));
                infoPanel.add(createValue(rs.getString("grade")));
            } else {
                infoPanel.setLayout(new FlowLayout());
                infoPanel.add(new JLabel("Employee details not found."));
            }

            rs.close();
            cs.close();
            obj.con.close();
        } catch (Exception ex) {
            infoPanel.setLayout(new FlowLayout());
            infoPanel.add(new JLabel("Error loading details."));
            ex.printStackTrace();
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JLabel createValue(String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkInBtn) {
            new CheckIn(userId).setVisible(true);
        } else if (e.getSource() == checkOutBtn) {
            new CheckOut(userId).setVisible(true);
        } else if (e.getSource() == viewAbsencesBtn) {
            new ViewAbsences(userId).setVisible(true);
        } else if (e.getSource() == viewLeavesBtn) {
            try (Connection conn = ConnectionClass.getConnection()) {
                String query = "SELECT employee_id FROM Employees WHERE user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, userId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        int employeeId = rs.getInt("employee_id");
                        new ViewLeaves(employeeId, userId).setVisible(true);
                        dispose();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
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
