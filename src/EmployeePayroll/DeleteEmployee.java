package EmployeePayroll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DeleteEmployee extends JFrame {

    private JTextField empIdField;
    private JTable employeeTable;
    private DefaultTableModel tableModel;

    public DeleteEmployee() {
        setTitle("Delete Employee By ID");
        setSize(750, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JLabel empIdLabel = new JLabel("Enter Employee ID to Delete:");
        empIdField = new JTextField(10);
        JButton fetchButton = new JButton("Fetch Details");
        JButton deleteButton = new JButton("Delete");
        JButton backButton = new JButton("Back");

        String[] columns = {
                "Employee ID", "Username", "Name", "Phone", "Gender", "Address",
                "Type", "Department", "Hire Date"
        };
        tableModel = new DefaultTableModel(columns, 0);
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);

        JPanel inputPanel = new JPanel();
        inputPanel.add(empIdLabel);
        inputPanel.add(empIdField);
        inputPanel.add(fetchButton);
        inputPanel.add(deleteButton);
        inputPanel.add(backButton);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchEmployeeDetails();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new AdminHome().setVisible(true);
        });
    }

    private void fetchEmployeeDetails() {
        String procedure = "{call SearchEmployeeByID(?)}";

        try (Connection conn = ConnectionClass.getConnection();
             CallableStatement stmt = conn.prepareCall(procedure)) {

            int empId = Integer.parseInt(empIdField.getText().trim());
            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);

            if (rs.next()) {
                Object[] row = {
                        rs.getInt("employee_id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("gender"),
                        rs.getString("address"),
                        rs.getString("type_name"),
                        rs.getString("department_name"),
                        rs.getDate("hire_date")
                };
                tableModel.addRow(row);
            } else {
                JOptionPane.showMessageDialog(this, "No employee found with this ID.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        int rowCount = tableModel.getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "Please fetch the employee details before deleting.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String procedure = "{call DeleteEmployeeByID(?)}";

        try (Connection conn = ConnectionClass.getConnection();
             CallableStatement stmt = conn.prepareCall(procedure)) {

            int empId = Integer.parseInt(empIdField.getText().trim());
            stmt.setInt(1, empId);
            stmt.execute();

            JOptionPane.showMessageDialog(this, "✅ Employee deleted successfully.");
            tableModel.setRowCount(0);
            empIdField.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Employee ID.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error deleting employee: " + e.getMessage());
        }
    }

/*    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DeleteEmployee().setVisible(true));
    }*/
}
