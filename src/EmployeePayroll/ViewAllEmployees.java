package EmployeePayroll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ViewAllEmployees extends JFrame {

    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> departmentCombo;
    private JComboBox<String> gradeCombo;
    private JComboBox<String> typeCombo;
    private Map<String, Integer> departmentMap;
    private Map<String, Integer> gradeMap;
    private Map<String, Integer> typeMap;

    public ViewAllEmployees() {
        setTitle("View All Employees");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initMaps();
        initComponents();
        loadEmployeeData();
    }

    private void initMaps() {
        departmentMap = new HashMap<>();
        gradeMap = new HashMap<>();
        typeMap = new HashMap<>();

        try (Connection conn = ConnectionClass.getConnection()) {
            // Load departments
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT department_id, department_name FROM Departments")) {
                while (rs.next()) {
                    departmentMap.put(rs.getString("department_name"), rs.getInt("department_id"));
                }
            }

            // Load grades
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT grade_id, grade FROM Grades")) {
                while (rs.next()) {
                    gradeMap.put(rs.getString("grade"), rs.getInt("grade_id"));
                }
            }

            // Load employee types
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT employee_type_id, type_name FROM EmployeeType")) {
                while (rs.next()) {
                    typeMap.put(rs.getString("type_name"), rs.getInt("employee_type_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        String[] columns = {
                "Employee ID", "Username", "Password", "Name", "Phone", "Email", "Gender", "Address",
                "CNIC", "Type", "Department", "Grade", "Hire Date"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Department filter
        departmentCombo = new JComboBox<>(new String[] { "All Departments" });
        departmentCombo.addItem("All Departments");
        for (String dept : departmentMap.keySet()) {
            departmentCombo.addItem(dept);
        }

        // Grade filter
        gradeCombo = new JComboBox<>(new String[] { "All Grades" });
        gradeCombo.addItem("All Grades");
        for (String grade : gradeMap.keySet()) {
            gradeCombo.addItem(grade);
        }

        // Type filter
        typeCombo = new JComboBox<>(new String[] { "All Types" });
        typeCombo.addItem("All Types");
        for (String type : typeMap.keySet()) {
            typeCombo.addItem(type);
        }

        // Add filters to panel
        filterPanel.add(new JLabel("Department:"));
        filterPanel.add(departmentCombo);
        filterPanel.add(new JLabel("Grade:"));
        filterPanel.add(gradeCombo);
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeCombo);

        // Add action listeners
        departmentCombo.addActionListener(e -> applyFilters());
        gradeCombo.addActionListener(e -> applyFilters());
        typeCombo.addActionListener(e -> applyFilters());

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new AdminHome().setVisible(true);
        });

        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void applyFilters() {
        tableModel.setRowCount(0); // Clear existing data

        String selectedDepartment = (String) departmentCombo.getSelectedItem();
        String selectedGrade = (String) gradeCombo.getSelectedItem();
        String selectedType = (String) typeCombo.getSelectedItem();

        try (Connection conn = ConnectionClass.getConnection();
                CallableStatement stmt = conn.prepareCall("{call ViewFilteredEmployees(?, ?, ?)}")) {

            // Set department parameter
            if (selectedDepartment.equals("All Departments")) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, departmentMap.get(selectedDepartment));
            }

            // Set grade parameter
            if (selectedGrade.equals("All Grades")) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, gradeMap.get(selectedGrade));
            }

            // Set type parameter
            if (selectedType.equals("All Types")) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, typeMap.get(selectedType));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] rowData = {
                            rs.getInt("employee_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("gender"),
                            rs.getString("address"),
                            rs.getString("cnic_num"),
                            rs.getString("employee_type"),
                            rs.getString("department_name"),
                            rs.getString("grade"),
                            rs.getDate("hire_date")
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadEmployeeData() {
        applyFilters(); // Initial load with all filters set to "All"
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewAllEmployees().setVisible(true));
    }
}
