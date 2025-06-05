package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminPayroll extends JFrame {
    JTextField empIdInput;
    JButton fetchBtn, editBtn, confirmBtn, backBtn;

    JTextField nameField, deptField, gradeField, genderField, typeField,
            emailField, cnicField, addressField, phoneField, hireDateField,
            baseSalaryField, absencesField, overtimeField, bonusField,
            deductionsField, taxField, netSalaryField;

    public AdminPayroll() {
        setTitle("Employee Payroll Details");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        // Top: Employee ID + Fetch
        JLabel empIdLabel = new JLabel("Employee ID:");
        empIdLabel.setBounds(30, 20, 100, 25);
        add(empIdLabel);

        empIdInput = new JTextField();
        empIdInput.setBounds(130, 20, 150, 25);
        add(empIdInput);

        fetchBtn = new JButton("Fetch");
        fetchBtn.setBounds(300, 20, 80, 25);
        add(fetchBtn);

        // Fields layout
        int labelX = 30, fieldX = 150, y = 60, h = 25, spacing = 30;

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
        absencesField = createField("Absences:", labelX, fieldX, y); y += spacing;
        overtimeField = createField("Overtime:", labelX, fieldX, y); y += spacing;
        bonusField = createField("Bonus:", labelX, fieldX, y); y += spacing;
        deductionsField = createField("Deductions:", labelX, fieldX, y); y += spacing;
        taxField = createField("Tax (%):", labelX, fieldX, y); y += spacing;
        netSalaryField = createField("Net Salary:", labelX, fieldX, y); y += spacing;

        // Set all fields non-editable initially
        setEditableFields(false);

        // Bottom buttons
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

        // Action Listeners
        fetchBtn.addActionListener(e -> {
            // TODO: Fetch employee data by ID
            // Populate all fields using DB result
            // Keep fields non-editable except on Edit
            setEditableFields(false);
        });

        editBtn.addActionListener(e -> {
            // Allow editing only of financial fields
            overtimeField.setEditable(true);
            bonusField.setEditable(true);
            deductionsField.setEditable(true);
        });

        confirmBtn.addActionListener(e -> {
            // TODO: Confirm & calculate Net Salary
            // Update to DB if needed
        });

        backBtn.addActionListener(e -> {
            dispose(); // close window
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JTextField createField(String label, int lx, int fx, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(lx, y, 100, 25);
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
        bonusField.setEditable(editable);
        deductionsField.setEditable(editable);
        taxField.setEditable(editable);
        netSalaryField.setEditable(editable);
    }

    public static void main(String[] args) {
        new AdminPayroll();
    }
}
