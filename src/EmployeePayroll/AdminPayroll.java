package EmployeePayroll;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
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

        nameField = createField("Name:", labelX, fieldX, y);
        y += spacing;
        deptField = createField("Department:", labelX, fieldX, y);
        y += spacing;
        gradeField = createField("Grade:", labelX, fieldX, y);
        y += spacing;
        genderField = createField("Gender:", labelX, fieldX, y);
        y += spacing;
        typeField = createField("Type:", labelX, fieldX, y);
        y += spacing;
        emailField = createField("Email:", labelX, fieldX, y);
        y += spacing;
        cnicField = createField("CNIC:", labelX, fieldX, y);
        y += spacing;
        addressField = createField("Address:", labelX, fieldX, y);
        y += spacing;
        phoneField = createField("Phone:", labelX, fieldX, y);
        y += spacing;
        hireDateField = createField("Hire Date:", labelX, fieldX, y);
        y += spacing;
        baseSalaryField = createField("Base Salary:", labelX, fieldX, y);
        y += spacing;
        absencesField = createField("Absences:", labelX, fieldX, y);
        y += spacing;
        overtimeField = createField("Overtime:", labelX, fieldX, y);
        y += spacing;
        foodAllowanceField = createField("Food Allowance:", labelX, fieldX, y);
        y += spacing;
        absenceDeductionField = createField("Absence Deduction:", labelX, fieldX, y);
        y += spacing;
        bonusField = createField("Bonus:", labelX, fieldX, y);
        y += spacing;
        deductionsField = createField("Deductions:", labelX, fieldX, y);
        y += spacing;
        taxField = createField("Tax:", labelX, fieldX, y);
        y += spacing;
        netSalaryField = createField("Net Salary:", labelX, fieldX, y);
        y += spacing;

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
            overtimeField.setEditable(true);
            bonusField.setEditable(true);

            // Attach document listeners ONCE in the constructor
            overtimeField.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    calculateNetSalary();
                }

                public void removeUpdate(DocumentEvent e) {
                    calculateNetSalary();
                }

                public void insertUpdate(DocumentEvent e) {
                    calculateNetSalary();
                }
            });
            bonusField.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    calculateNetSalary();
                }

                public void removeUpdate(DocumentEvent e) {
                    calculateNetSalary();
                }

                public void insertUpdate(DocumentEvent e) {
                    calculateNetSalary();
                }
            });
        });

        confirmBtn.addActionListener(e -> {
            try {
                int empId = Integer.parseInt(empIdInput.getText().trim());
                int monthIndex = monthCombo.getSelectedIndex() + 1;
                String monthStr = String.valueOf(monthIndex); // match database format
                int year = (int) yearCombo.getSelectedItem();

                int overtime = Integer.parseInt(overtimeField.getText().trim());
                BigDecimal bonus = new BigDecimal(bonusField.getText().trim());
                BigDecimal baseSalary = new BigDecimal(baseSalaryField.getText().trim());
                BigDecimal absenceDeduction = new BigDecimal(absenceDeductionField.getText().trim());
                BigDecimal foodAllowance = new BigDecimal(foodAllowanceField.getText().trim());

                BigDecimal gross = baseSalary
                        .add(new BigDecimal(overtime * 200))
                        .add(foodAllowance)
                        .add(bonus);

                BigDecimal tax;
                if (gross.compareTo(new BigDecimal(50000)) <= 0) {
                    tax = BigDecimal.ZERO;
                } else if (gross.compareTo(new BigDecimal(100000)) <= 0) {
                    tax = gross.multiply(new BigDecimal("0.05"));
                } else if (gross.compareTo(new BigDecimal(200000)) <= 0) {
                    tax = gross.multiply(new BigDecimal("0.10"));
                } else {
                    tax = gross.multiply(new BigDecimal("0.15"));
                }

                BigDecimal totalDeductions = absenceDeduction.add(tax);
                BigDecimal net = gross.subtract(totalDeductions);

                try (Connection conn = ConnectionClass.getConnection()) {
                    if (conn == null) {
                        JOptionPane.showMessageDialog(this, "Database connection failed.");
                        return;
                    }

                    CallableStatement stmt = conn.prepareCall("{call UpdatePayroll(?, ?, ?, ?, ?, ?)}");
                    stmt.setInt(1, empId);
                    stmt.setString(2, monthStr);
                    stmt.setInt(3, year);
                    stmt.setBigDecimal(4, baseSalary);
                    stmt.setBigDecimal(5, bonus);
                    stmt.setBigDecimal(6, totalDeductions);

                    stmt.execute();

                    taxField.setText(tax.toPlainString());
                    deductionsField.setText(totalDeductions.toPlainString());
                    netSalaryField.setText(net.toPlainString());

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
        baseSalaryField.setEditable(editable);
        absencesField.setEditable(editable);
        overtimeField.setEditable(editable);
        foodAllowanceField.setEditable(editable);
        absenceDeductionField.setEditable(editable);
        bonusField.setEditable(editable);
        deductionsField.setEditable(editable);
        taxField.setEditable(editable);
        netSalaryField.setEditable(editable);
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

                    baseSalaryField.setText(rs.getBigDecimal("BaseSalary").toString());
                    absencesField.setText(String.valueOf(rs.getInt("UnexcusedAbsences")));
                    overtimeField.setText(String.valueOf(rs.getInt("OvertimeHours")));
                    foodAllowanceField.setText(rs.getBigDecimal("FoodAllowance").toString());
                    absenceDeductionField.setText(rs.getBigDecimal("AbsenceDeduction").toString());
                    bonusField.setText(rs.getBigDecimal("Bonus").toString());
                    deductionsField.setText(rs.getBigDecimal("AbsenceDeduction").toString()); // optional: include
                                                                                              // food/tax etc.
                    taxField.setText(rs.getBigDecimal("Tax").toString());
                    netSalaryField.setText(rs.getBigDecimal("NetSalary").toString());
                }
            } else {
                JOptionPane.showMessageDialog(this, "No payroll data found.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching payroll: " + ex.getMessage());
            ex.printStackTrace();
        }
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

                gradeMap.put(display, gradeId);
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
                String selectedGrade = (String) gradeComboBox.getSelectedItem();
                int gradeId = gradeMap.get(selectedGrade);

                double bonusAmount = Double.parseDouble(bonusField.getText().trim());
                int bonusMonth = Integer.parseInt(monthField.getText().trim());
                int bonusYear = Integer.parseInt(yearField.getText().trim());

                try (Connection conn = ConnectionClass.getConnection()) {
                    CallableStatement stmt = conn.prepareCall("{call SetGradeBonus(?, ?, ?, ?)}");
                    stmt.setInt(1, gradeId);
                    stmt.setInt(2, bonusMonth);
                    stmt.setInt(3, bonusYear);
                    stmt.setBigDecimal(4, new java.math.BigDecimal(bonusAmount));
                    stmt.execute();

                    JOptionPane.showMessageDialog(this, "Grade bonus set successfully.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to set grade bonus: " + ex.getMessage());
            }
        }
    }

    private void calculateNetSalary() {
        try {
            int overtime = parseIntOrZero(overtimeField.getText().trim());
            BigDecimal bonus = parseBigDecimalOrZero(bonusField.getText().trim());
            BigDecimal baseSalary = parseBigDecimalOrZero(baseSalaryField.getText().trim());
            BigDecimal absenceDeduction = parseBigDecimalOrZero(absenceDeductionField.getText().trim());
            BigDecimal foodAllowance = parseBigDecimalOrZero(foodAllowanceField.getText().trim());

            BigDecimal gross = baseSalary
                    .add(new BigDecimal(overtime * 200))
                    .add(foodAllowance)
                    .add(bonus);

            BigDecimal tax;
            if (gross.compareTo(new BigDecimal(50000)) <= 0) {
                tax = BigDecimal.ZERO;
            } else if (gross.compareTo(new BigDecimal(100000)) <= 0) {
                tax = gross.multiply(new BigDecimal("0.05"));
            } else if (gross.compareTo(new BigDecimal(200000)) <= 0) {
                tax = gross.multiply(new BigDecimal("0.10"));
            } else {
                tax = gross.multiply(new BigDecimal("0.15"));
            }

            BigDecimal totalDeductions = absenceDeduction.add(tax);
            BigDecimal net = gross.subtract(totalDeductions);

            taxField.setText(tax.toPlainString());
            deductionsField.setText(totalDeductions.toPlainString());
            netSalaryField.setText(net.toPlainString());
        } catch (Exception ex) {
            // Ignore invalid input while typing
        }
    }

    private int parseIntOrZero(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private BigDecimal parseBigDecimalOrZero(String s) {
        try {
            return new BigDecimal(s);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminPayroll::new);
    }
}
