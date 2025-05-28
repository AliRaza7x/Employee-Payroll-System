package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NormalAdminHome extends JFrame implements ActionListener {
    JButton addEmpBtn, searchEmpBtn, deleteEmpBtn, updateEmpBtn, generatePayrollBtn, exitBtn, viewAllbtn, leavesbtn;
    JLabel background;


    public NormalAdminHome() {
        setTitle("Admin Homepage");
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
        searchEmpBtn = createButton("Search Employee", "recruitment.png", 500, 90, btnFont);
        deleteEmpBtn = createButton("Delete Employee", "delete.png", 500, 140, btnFont);
        updateEmpBtn = createButton("Update Employee", "employee.png", 500, 190, btnFont);
        generatePayrollBtn = createButton("Generate Payroll", "payslip.png", 500, 240, btnFont);
        viewAllbtn = createButton("View All Employees", "website.png", 500, 290, btnFont);
        leavesbtn = createButton("Approve/Reject Leaves", "approved.png", 500, 340, btnFont);

        // Add to background
        background.add(addEmpBtn);
        background.add(searchEmpBtn);
        background.add(deleteEmpBtn);
        background.add(updateEmpBtn);
        background.add(generatePayrollBtn);
        background.add(viewAllbtn);
        background.add(leavesbtn);

        // Listeners
        addEmpBtn.addActionListener(this);
        searchEmpBtn.addActionListener(this);
        deleteEmpBtn.addActionListener(this);
        updateEmpBtn.addActionListener(this);
        generatePayrollBtn.addActionListener(this);
        viewAllbtn.addActionListener(this);
        leavesbtn.addActionListener(this);
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
            new AddEmployee().setVisible(true);
        } else if (e.getSource() == searchEmpBtn) {
            new SearchEmployee().setVisible(true);
        } else if (e.getSource() == deleteEmpBtn) {
            new DeleteEmployee().setVisible(true);
        } else if (e.getSource() == updateEmpBtn) {
            new UpdateEmployee().setVisible(true);
        } else if (e.getSource() == generatePayrollBtn) {
            // new GeneratePayroll().setVisible(true);
        } else if (e.getSource() == viewAllbtn) {
            new ViewAllEmployees().setVisible(true);
        } else if (e.getSource() == leavesbtn) {
            new ApproveRejectLeaves(this).setVisible(true);
        }
        this.setVisible(false);
    }

    public static void main(String[] args) {
        new NormalAdminHome().setVisible(true);
    }
}
