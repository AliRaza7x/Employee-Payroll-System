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
        setSize(1000, 600);
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
                "Employee ID", "Username", "Name", "Phone", "Email", "Gender", "Address",
                "CNIC", "Type", "Department", "Grade", "Hire Date"
        };

        tableModel = new DefaultTableModel(columns, 0);
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

        try (Connection conn = ConnectionClass.getConnection()) {
            String sql = "SELECT e.employee_id, u.username, e.name, e.phone, e.email, " +
                    "e.gender, e.address, e.cnic_num, et.type_name AS employee_type, " +
                    "d.department_name, g.grade, e.hire_date " +
                    "FROM Employees e " +
                    "INNER JOIN Users u ON e.user_id = u.user_id " +
                    "INNER JOIN EmployeeType et ON e.employee_type_id = et.employee_type_id " +
                    "INNER JOIN Departments d ON e.department_id = d.department_id " +
                    "INNER JOIN Grades g ON e.grade_id = g.grade_id " +
                    "WHERE 1=1 ";

            if (!selectedDepartment.equals("All Departments")) {
                sql += "AND e.department_id = " + departmentMap.get(selectedDepartment) + " ";
            }
            if (!selectedGrade.equals("All Grades")) {
                sql += "AND e.grade_id = " + gradeMap.get(selectedGrade) + " ";
            }
            if (!selectedType.equals("All Types")) {
                sql += "AND e.employee_type_id = " + typeMap.get(selectedType) + " ";
            }

            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Object[] rowData = {
                            rs.getInt("employee_id"),
                            rs.getString("username"),
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
