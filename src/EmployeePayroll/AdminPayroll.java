package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminPayroll extends JFrame {
    JTextField empIdInput;
    JComboBox<String> monthCombo;
    JComboBox<Integer> yearCombo;
    JButton fetchBtn, editBtn, confirmBtn, backBtn, setBonusBtn;

    JTextField nameField, deptField, gradeField, genderField, typeField,
            emailField, cnicField, addressField, phoneField, hireDateField,
            baseSalaryField, absencesField, overtimeField, foodAllowanceField, absenceDeductionField,
            bonusField, deductionsField, taxField, netSalaryField;

    public AdminPayroll() {
        setTitle("Employee Payroll Details");
        setSize(700, 850);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        JLabel empIdLabel = new JLabel("Employee ID:");
        empIdLabel.setBounds(30, 20, 100, 25);
        add(empIdLabel);

        empIdInput = new JTextField();
        empIdInput.setBounds(130, 20, 150, 25);
        add(empIdInput);

        JLabel monthLabel = new JLabel("Month:");
        monthLabel.setBounds(300, 20, 50, 25);
        add(monthLabel);

        String[] months = {
                "1 - January", "2 - February", "3 - March", "4 - April",
                "5 - May", "6 - June", "7 - July", "8 - August",
                "9 - September", "10 - October", "11 - November", "12 - December"
        };
        monthCombo = new JComboBox<>(months);
        monthCombo.setBounds(350, 20, 130, 25);
        add(monthCombo);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setBounds(500, 20, 40, 25);
        add(yearLabel);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[11];
        for (int i = 0; i <= 10; i++) {
            years[i] = currentYear - 5 + i;
        }
        yearCombo = new JComboBox<>(years);
        yearCombo.setBounds(540, 20, 80, 25);
        yearCombo.setSelectedItem(currentYear);
        add(yearCombo);

        fetchBtn = new JButton("Fetch");
        fetchBtn.setBounds(130, 55, 80, 25);
        add(fetchBtn);

        setBonusBtn = new JButton("Set Grade Bonus");
        setBonusBtn.setBounds(230, 55, 160, 25);
        add(setBonusBtn);

        int labelX = 30, fieldX = 150, y = 100, spacing = 30;

        nameField = createField("Name:", labelX, fieldX, y); y += spacing;
        deptField = createField("Department:", labelX, fieldX, y); y += spacing;
        gradeField = createField("Grade:", labelX, fieldX, y); y += spacing;
        genderField = createField("Gender:", labelX, fieldX, y); y += spacing;
        typeField = createField("Type:", labelX, fieldX, y); y += spacing;
        emailField = createField("Email:", labelX, fieldX, y); y += spacing;
        cnicField = createField("CNIC:", labelX, fieldX, y); y += spacing;
        addressField = createField("Address:", labelX, fieldX, y); y += spacing;
        phoneField = createField("Phone:", labelX, fieldX, y); y += spacing;
        hireDateField = createField("Hire Date:", labelX, fieldX, y); y += spacing;
        baseSalaryField = createField("Base Salary:", labelX, fieldX, y); y += spacing;
        absencesField = createField("Unexcused Absences:", labelX, fieldX, y); y += spacing;
        overtimeField = createField("Overtime Hours:", labelX, fieldX, y); y += spacing;
        foodAllowanceField = createField("Food Allowance:", labelX, fieldX, y); y += spacing;
        absenceDeductionField = createField("Absence Deduction:", labelX, fieldX, y); y += spacing;
        bonusField = createField("Bonus:", labelX, fieldX, y); y += spacing;
        deductionsField = createField("Total Deductions:", labelX, fieldX, y); y += spacing;
        taxField = createField("Tax:", labelX, fieldX, y); y += spacing;
        netSalaryField = createField("Net Salary:", labelX, fieldX, y); y += spacing;

        setEditableFields(false);

        int btnY = y + 10;
        editBtn = new JButton("Edit");
        editBtn.setBounds(100, btnY, 100, 30);
        add(editBtn);

        confirmBtn = new JButton("Confirm");
        confirmBtn.setBounds(230, btnY, 100, 30);
        add(confirmBtn);

        backBtn = new JButton("Back");
        backBtn.setBounds(360, btnY, 100, 30);
        add(backBtn);

        fetchBtn.addActionListener(e -> {
            try {
                int empId = Integer.parseInt(empIdInput.getText());
                int month = monthCombo.getSelectedIndex() + 1;
                int year = (int) yearCombo.getSelectedItem();

                fetchPayrollData(empId, month, year);
                setEditableFields(false);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric inputs.");
            }
        });

        editBtn.addActionListener(e -> {
            bonusField.setEditable(true);
            foodAllowanceField.setEditable(true);
            overtimeField.setEditable(true);
            absenceDeductionField.setEditable(true);
        });

        confirmBtn.addActionListener(e -> {
            try {
                int empId = Integer.parseInt(empIdInput.getText().trim());
                int year = (int) yearCombo.getSelectedItem();
                int month = monthCombo.getSelectedIndex() + 1; // 1-based month index
                String monthStr = String.valueOf(month);

                BigDecimal baseSalary = new BigDecimal(baseSalaryField.getText().trim());
                BigDecimal foodAllowance = new BigDecimal(foodAllowanceField.getText().trim());
                int overtimeHours = Integer.parseInt(overtimeField.getText().trim());
                BigDecimal absenceDeduction = new BigDecimal(absenceDeductionField.getText().trim());

                try (Connection conn = ConnectionClass.getConnection()) {
                    if (conn == null) {
                        JOptionPane.showMessageDialog(this, "Database connection failed.");
                        return;
                    }

                    // Step 1: Get base bonus from procedure
                    BigDecimal baseBonus = getBaseBonusFromProcedure(empId, month, year, conn);

                    // Step 2: Get admin adjustment from field
                    BigDecimal adminAdjustment;
                    try {
                        adminAdjustment = new BigDecimal(bonusField.getText().trim());
                    } catch (NumberFormatException ex) {
                        adminAdjustment = BigDecimal.ZERO;
                    }

                    // Step 3: Final bonus = base bonus + admin adjustment
                    BigDecimal totalBonus = baseBonus.add(adminAdjustment);

                    // Step 4: Calculate overtime pay
                    BigDecimal overtimePay = new BigDecimal(overtimeHours).multiply(new BigDecimal("200")); // fixed rate
                    BigDecimal totalDeductions = absenceDeduction; // extend as needed

                    // Step 5: Calculate tax and net salary
                    BigDecimal taxableAmount = baseSalary.add(totalBonus).add(overtimePay).add(foodAllowance).subtract(totalDeductions);
                    BigDecimal tax = calculateTax(taxableAmount);
                    BigDecimal netSalary = taxableAmount.subtract(tax);

                    // Step 6: Call procedure to update payroll
                    CallableStatement stmt = conn.prepareCall("{call UpdatePayroll(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
                    stmt.setInt(1, empId);
                    stmt.setString(2, monthStr);
                    stmt.setInt(3, year);
                    stmt.setBigDecimal(4, baseSalary);
                    stmt.setBigDecimal(5, totalBonus);  // final computed bonus
                    stmt.setBigDecimal(6, totalDeductions);
                    stmt.setBigDecimal(7, tax);
                    stmt.setBigDecimal(8, absenceDeduction);
                    stmt.setBigDecimal(9, foodAllowance);
                    stmt.setInt(10, overtimeHours);
                    stmt.execute();

                    // Step 7: Update UI
                    deductionsField.setText(totalDeductions.toPlainString());
                    taxField.setText(tax.toPlainString());
                    netSalaryField.setText(netSalary.toPlainString());

                    JOptionPane.showMessageDialog(this, "Payroll updated successfully.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });







        backBtn.addActionListener(e -> dispose());
        setBonusBtn.addActionListener(e -> showSetGradeBonusDialog());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JTextField createField(String label, int lx, int fx, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(lx, y, 130, 25);
        add(l);

        JTextField field = new JTextField();
        field.setBounds(fx, y, 200, 25);
        add(field);
        return field;
    }

    private void setEditableFields(boolean editable) {
        nameField.setEditable(editable);
        deptField.setEditable(editable);
        gradeField.setEditable(editable);
        genderField.setEditable(editable);
        typeField.setEditable(editable);
        emailField.setEditable(editable);
        cnicField.setEditable(editable);
        addressField.setEditable(editable);
        phoneField.setEditable(editable);
        hireDateField.setEditable(editable);
        baseSalaryField.setEditable(false);
        absencesField.setEditable(false);
        overtimeField.setEditable(editable);
        foodAllowanceField.setEditable(editable);
        absenceDeductionField.setEditable(editable);
        bonusField.setEditable(editable);
        deductionsField.setEditable(false);
        taxField.setEditable(false);
        netSalaryField.setEditable(false);
    }
    public BigDecimal getBaseBonusFromProcedure(int employeeId, int month, int year, Connection conn) {
        BigDecimal baseBonus = BigDecimal.ZERO;

        try (CallableStatement stmt = conn.prepareCall("{call GetBonusByGrade(?, ?, ?, ?)}")) {
            stmt.setInt(1, employeeId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            stmt.registerOutParameter(4, java.sql.Types.DECIMAL);

            stmt.execute();
            baseBonus = stmt.getBigDecimal(4);
            if (baseBonus == null) {
                baseBonus = BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle appropriately
        }

        return baseBonus;
    }


    private void fetchPayrollData(int empId, int month, int year) {
        try (Connection conn = ConnectionClass.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Failed to connect to database.");
                return;
            }

            CallableStatement stmt = conn.prepareCall("{call CalculatePayrollForEmployee(?, ?, ?)}");
            stmt.setInt(1, empId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);

            boolean hasResult = stmt.execute();
            if (hasResult) {
                ResultSet rs = stmt.getResultSet();

                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    deptField.setText(rs.getString("department_name"));
                    gradeField.setText(rs.getString("grade"));
                    genderField.setText(rs.getString("gender"));
                    typeField.setText(rs.getString("type_name"));
                    emailField.setText(rs.getString("email"));
                    cnicField.setText(rs.getString("cnic_num"));
                    addressField.setText(rs.getString("address"));
                    phoneField.setText(rs.getString("phone"));
                    hireDateField.setText(rs.getDate("hire_date").toString());

                    baseSalaryField.setText(getSafeDecimal(rs, "BaseSalary"));
                    absencesField.setText(String.valueOf(rs.getInt("UnexcusedAbsences")));
                    overtimeField.setText(String.valueOf(rs.getInt("OvertimeHours")));
                    foodAllowanceField.setText(getSafeDecimal(rs, "FoodAllowance"));
                    absenceDeductionField.setText(getSafeDecimal(rs, "AbsenceDeduction"));
                    taxField.setText(getSafeDecimal(rs, "Tax"));
                    netSalaryField.setText(getSafeDecimal(rs, "NetSalary"));

                    // 🔁 NEW: Call GetBonusByGrade procedure
                    BigDecimal baseBonus = getBaseBonusFromProcedure(empId, month, year, conn);
                    bonusField.setText(baseBonus.toPlainString()); // Sets bonusField using procedure

                    // If admin had made adjustment before, you can preserve/display that here too
                } else {
                    JOptionPane.showMessageDialog(this, "No payroll data found.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching payroll: " + ex.getMessage());
            ex.printStackTrace();
        }
    }



    private String getSafeDecimal(ResultSet rs, String column) throws SQLException {
        BigDecimal value = rs.getBigDecimal(column);
        return (value != null) ? value.toPlainString() : "0.00";
    }
    private BigDecimal calculateTax(BigDecimal taxableAmount) {
        BigDecimal tax = BigDecimal.ZERO;

        BigDecimal slab1Limit = new BigDecimal("10000");
        BigDecimal slab2Limit = new BigDecimal("30000");
        BigDecimal slab3Limit = new BigDecimal("50000");

        BigDecimal slab1Rate = new BigDecimal("0.05");  // 5%
        BigDecimal slab2Rate = new BigDecimal("0.10");  // 10%
        BigDecimal slab3Rate = new BigDecimal("0.15");  // 15%
        BigDecimal slab4Rate = new BigDecimal("0.20");  // 20%

        if (taxableAmount.compareTo(slab1Limit) <= 0) {
            tax = taxableAmount.multiply(slab1Rate);
        } else if (taxableAmount.compareTo(slab2Limit) <= 0) {
            tax = slab1Limit.multiply(slab1Rate)
                    .add(taxableAmount.subtract(slab1Limit).multiply(slab2Rate));
        } else if (taxableAmount.compareTo(slab3Limit) <= 0) {
            tax = slab1Limit.multiply(slab1Rate)
                    .add(slab2Limit.subtract(slab1Limit).multiply(slab2Rate))
                    .add(taxableAmount.subtract(slab2Limit).multiply(slab3Rate));
        } else {
            tax = slab1Limit.multiply(slab1Rate)
                    .add(slab2Limit.subtract(slab1Limit).multiply(slab2Rate))
                    .add(slab3Limit.subtract(slab2Limit).multiply(slab3Rate))
                    .add(taxableAmount.subtract(slab3Limit).multiply(slab4Rate));
        }

        return tax.setScale(2, RoundingMode.HALF_UP);
    }


    private void showSetGradeBonusDialog() {
        Map<String, Integer> gradeMap = new HashMap<>();
        JComboBox<String> gradeComboBox = new JComboBox<>();

        try (Connection conn = ConnectionClass.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT grade_id, grade FROM Grades")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int gradeId = rs.getInt("grade_id");
                int gradeValue = rs.getInt("grade");
                String display = "Grade " + gradeValue;

                // Store grade number (not grade_id) for passing to procedure
                gradeMap.put(display, gradeValue);
                gradeComboBox.addItem(display);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load grade data: " + e.getMessage());
            return;
        }

        JTextField bonusField = new JTextField();
        JTextField monthField = new JTextField();
        JTextField yearField = new JTextField();

        Object[] message = {
                "Select Grade:", gradeComboBox,
                "Bonus Amount:", bonusField,
                "Month (1-12):", monthField,
                "Year:", yearField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Set Grade Bonus", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                // This line must be **before** using selectedGrade anywhere
                String selectedGrade = (String) gradeComboBox.getSelectedItem();

                // Get the grade number (e.g., 10) from the map, NOT grade_id
                int grade = gradeMap.get(selectedGrade);

                double bonusAmount = Double.parseDouble(bonusField.getText().trim());
                int bonusMonth = Integer.parseInt(monthField.getText().trim());
                int bonusYear = Integer.parseInt(yearField.getText().trim());

                try (Connection conn = ConnectionClass.getConnection()) {
                    CallableStatement stmt = conn.prepareCall("{call SetGradeBonus(?, ?, ?, ?)}");
                    stmt.setInt(1, grade);  // Pass the actual grade number here
                    stmt.setInt(2, bonusMonth);
                    stmt.setInt(3, bonusYear);
                    stmt.setBigDecimal(4, new BigDecimal(bonusAmount));
                    stmt.execute();

                    JOptionPane.showMessageDialog(this, "Grade bonus set successfully.");
                }

            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminPayroll::new);
    }
}
