package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Calendar;
import java.math.BigDecimal;

public class UserGeneratePayroll extends JFrame {
    private int userId;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JButton fetchBtn, backBtn;
    private JPanel detailsPanel;
    private JScrollPane scrollPane;

    public UserGeneratePayroll(int userId) {
        this.userId = userId;
        setTitle("View Payroll Slip");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        // Month Selection
        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setBounds(30, 20, 50, 25);
        add(monthLabel);

        String[] months = {
                "1 - January", "2 - February", "3 - March", "4 - April",
                "5 - May", "6 - June", "7 - July", "8 - August",
                "9 - September", "10 - October", "11 - November", "12 - December"
        };
        monthCombo = new JComboBox<>(months);
        monthCombo.setBounds(80, 20, 130, 25);
        add(monthCombo);

        // Year Selection
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setBounds(230, 20, 40, 25);
        add(yearLabel);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[11];
        for (int i = 0; i <= 10; i++) {
            years[i] = currentYear - 5 + i;
        }
        yearCombo = new JComboBox<>(years);
        yearCombo.setBounds(270, 20, 80, 25);
        yearCombo.setSelectedItem(currentYear);
        add(yearCombo);

        // Fetch Button
        fetchBtn = new JButton("Fetch Payroll");
        fetchBtn.setBounds(370, 20, 120, 25);
        add(fetchBtn);

        // Back Button
        backBtn = new JButton("Back");
        backBtn.setBounds(510, 20, 80, 25);
        add(backBtn);

        // Details Panel
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBounds(30, 60, 730, 480);
        add(scrollPane);

        // Add action listeners
        fetchBtn.addActionListener(e -> fetchPayrollData());
        backBtn.addActionListener(e -> {
            dispose();
            new UserHome(userId).setVisible(true);
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void fetchPayrollData() {
        try {
            int month = monthCombo.getSelectedIndex() + 1;
            int year = (int) yearCombo.getSelectedItem();

            try (Connection conn = ConnectionClass.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Failed to connect to database.");
                    return;
                }

                // First get employee_id from user_id
                int employeeId;
                try (PreparedStatement pstmt = conn
                        .prepareStatement("SELECT employee_id FROM Employees WHERE user_id = ?")) {
                    pstmt.setInt(1, userId);
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this, "Employee not found.");
                        return;
                    }
                    employeeId = rs.getInt("employee_id");
                }

                // Then fetch payroll data using GetPayrollSlipForEmployee
                CallableStatement stmt = conn.prepareCall("{call GetPayrollSlipForEmployee(?, ?, ?)}");
                stmt.setInt(1, employeeId);
                stmt.setInt(2, month);
                stmt.setInt(3, year);

                boolean hasResult = stmt.execute();
                if (hasResult) {
                    ResultSet rs = stmt.getResultSet();
                    if (rs.next()) {
                        displayPayrollData(rs);
                    } else {
                        JOptionPane.showMessageDialog(this, "No payroll data found for the selected month and year.");
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching payroll: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void displayPayrollData(ResultSet rs) throws SQLException {
        detailsPanel.removeAll();
        detailsPanel.setLayout(new GridLayout(0, 2, 10, 10));

        // Create a title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("PAYROLL SLIP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        detailsPanel.add(titlePanel);
        detailsPanel.add(new JPanel()); // Empty panel for grid alignment

        // Employee Information
        addDetailRow("Employee Name:", rs.getString("name"));
        addDetailRow("Department:", rs.getString("department_name"));
        addDetailRow("Grade:", rs.getString("grade"));
        addDetailRow("Month:", monthCombo.getSelectedItem().toString());
        addDetailRow("Year:", yearCombo.getSelectedItem().toString());

        // Salary Details
        addDetailRow("Base Salary:", formatCurrency(rs.getBigDecimal("base_salary")));
        addDetailRow("Food Allowance:", formatCurrency(rs.getBigDecimal("food_allowance")));
        addDetailRow("Overtime Hours:", String.valueOf(rs.getInt("overtime_hours")));
        addDetailRow("Overtime Pay:",
                formatCurrency(new BigDecimal(rs.getInt("overtime_hours")).multiply(new BigDecimal("200"))));
        addDetailRow("Bonus:", formatCurrency(rs.getBigDecimal("bonus")));

        // Deductions
        addDetailRow("Absence Deduction:", formatCurrency(rs.getBigDecimal("absence_deduction")));
        addDetailRow("Tax:", formatCurrency(rs.getBigDecimal("tax")));
        addDetailRow("Total Deductions:", formatCurrency(rs.getBigDecimal("deductions")));

        // Net Salary
        JLabel netSalaryLabel = new JLabel("Net Salary:");
        netSalaryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        detailsPanel.add(netSalaryLabel);

        JLabel netSalaryValue = new JLabel(formatCurrency(rs.getBigDecimal("net_salary")));
        netSalaryValue.setFont(new Font("Arial", Font.BOLD, 16));
        detailsPanel.add(netSalaryValue);

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private void addDetailRow(String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsPanel.add(labelComponent);

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsPanel.add(valueComponent);
    }

    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null)
            return "0.00";
        return String.format("%,.2f", amount);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserGeneratePayroll(1).setVisible(true));
    }
}
