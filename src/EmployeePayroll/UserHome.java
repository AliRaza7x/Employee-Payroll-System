package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UserHome extends JFrame implements ActionListener {
    JButton checkInBtn, checkOutBtn, viewLeavesBtn, requestLeaveBtn, generateSlipBtn, exitBtn;
    JLabel background;
    private int userId; // ✅ Store the userId

    // ✅ Constructor that accepts userId
    public UserHome(int userId) {
        this.userId = userId; // Store for later use (e.g., pass to CheckIn)

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
        ImageIcon exitIcon = new ImageIcon(exitImg);
        exitBtn = new JButton(exitIcon);
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

        background.add(checkInBtn);
        background.add(checkOutBtn);
        background.add(viewLeavesBtn);
        background.add(requestLeaveBtn);
        background.add(generateSlipBtn);

        checkInBtn.addActionListener(this);
        checkOutBtn.addActionListener(this);
        viewLeavesBtn.addActionListener(this);
        requestLeaveBtn.addActionListener(this);
        generateSlipBtn.addActionListener(this);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkInBtn) {
            new CheckIn(userId).setVisible(true);
        } else if (e.getSource() == checkOutBtn) {
             new CheckOut(userId).setVisible(true);
        } else if (e.getSource() == viewLeavesBtn) {
            // new ViewLeaves().setVisible(true);
        } else if (e.getSource() == requestLeaveBtn) {
             new RequestLeave(userId).setVisible(true);
        } else if (e.getSource() == generateSlipBtn) {
            // new GenerateOwnSlip().setVisible(true);
        }
        this.setVisible(false);
    }
}
