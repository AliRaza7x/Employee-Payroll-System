package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminHome extends JFrame implements ActionListener {
    JButton addEmpBtn, addAdminBtn, searchEmpBtn, deleteEmpBtn, updateEmpBtn, generatePayrollBtn, exitBtn, viewAllbtn, leavebtn;
    JLabel background;


    public AdminHome() {
        setTitle("Super Admin Homepage");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);


        // Load and scale background image
        ImageIcon bgIcon = new ImageIcon(ClassLoader.getSystemResource("Images/background.jpg"));
        Image scaledBG = bgIcon.getImage().getScaledInstance(800, 500, Image.SCALE_SMOOTH);
        background = new JLabel(new ImageIcon(scaledBG));
        background.setBounds(0, 0, 800, 500);
        background.setLayout(null);
        add(background);

        // Exit button (top-left)
        ImageIcon exitIcon = new ImageIcon(ClassLoader.getSystemResource("Images/switch.png"));
        Image exitScaled = exitIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        exitBtn = new JButton(new ImageIcon(exitScaled));
        exitBtn.setBounds(10, 10, 30, 30);
        exitBtn.setBorderPainted(false);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setFocusPainted(false);
        exitBtn.addActionListener(e -> {
            this.setVisible(false);
            new Login().setVisible(true);
        });
        background.add(exitBtn);

        // Button font
        Font btnFont = new Font("Arial", Font.BOLD, 14);

        // Add all buttons
        addEmpBtn = createButton("Add Employee", "people.png", 500, 40, btnFont);
        addAdminBtn = createButton("Add Admin", "accepted.png", 500, 90, btnFont);
        searchEmpBtn = createButton("Search Employee", "recruitment.png", 500, 140, btnFont);
        deleteEmpBtn = createButton("Delete Employee", "delete.png", 500, 190, btnFont);
        updateEmpBtn = createButton("Update Employee", "employee.png", 500, 240, btnFont);
        generatePayrollBtn = createButton("Generate Payroll", "payslip.png", 500, 290, btnFont);
        viewAllbtn = createButton("View All Employees", "website.png", 500, 340, btnFont);
        leavebtn = createButton("Approve/Reject Leaves", "approved.png", 500, 390, btnFont);

        // Add to background
        background.add(addEmpBtn);
        background.add(addAdminBtn);
        background.add(searchEmpBtn);
        background.add(deleteEmpBtn);
        background.add(updateEmpBtn);
        background.add(generatePayrollBtn);
        background.add(viewAllbtn);
        background.add(leavebtn);

        // Listeners
        addEmpBtn.addActionListener(this);
        addAdminBtn.addActionListener(this);
        searchEmpBtn.addActionListener(this);
        deleteEmpBtn.addActionListener(this);
        updateEmpBtn.addActionListener(this);
        generatePayrollBtn.addActionListener(this);
        viewAllbtn.addActionListener(this);
        leavebtn.addActionListener(this);
    }

    // Helper method to create icon + text buttons
    public JButton createButton(String text, String iconName, int x, int y, Font font) {
        ImageIcon rawIcon = new ImageIcon(ClassLoader.getSystemResource("Images/" + iconName));
        Image scaledIcon = rawIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        JButton button = new JButton(text, new ImageIcon(scaledIcon));
        button.setBounds(x, y, 230, 40);
        button.setFont(font);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addEmpBtn) {
             new AddEmployee(this).setVisible(true);
        } else if (e.getSource() == addAdminBtn) {
             new AddAdmin(this).setVisible(true);
        } else if (e.getSource() == searchEmpBtn) {
             new SearchEmployee(this).setVisible(true);
        } else if (e.getSource() == deleteEmpBtn) {
             new DeleteEmployee(this).setVisible(true);
        } else if (e.getSource() == updateEmpBtn) {
            new UpdateEmployee(this).setVisible(true);
        } else if (e.getSource() == generatePayrollBtn) {
             new AdminPayroll(this).setVisible(true);
        } else if (e.getSource() == viewAllbtn) {
             new ViewAllEmployees(this).setVisible(true);
        } else if (e.getSource() == leavebtn) {
            new ApproveRejectLeaves(this).setVisible(true);
        }
        this.setVisible(false);
    }

//    public static void main(String[] args) {
//        new AdminHome().setVisible(true);
//    }
}
