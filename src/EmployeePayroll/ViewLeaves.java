package EmployeePayroll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Calendar;

public class ViewLeaves extends JFrame {
    private JTable leavesTable;
    private DefaultTableModel tableModel;
    private JLabel totalLeavesLabel;
    private JLabel remainingLeavesLabel;
    private int employeeId;
    private int userId;

    public ViewLeaves(int employeeId, int userId) {
        this.employeeId = employeeId;
        this.userId = userId;
        setTitle("View Leaves");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create info panel for total and remaining leaves
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        totalLeavesLabel = new JLabel("Total Leaves: Loading...");
        remainingLeavesLabel = new JLabel("Remaining Leaves: Loading...");
        infoPanel.add(totalLeavesLabel);
        infoPanel.add(remainingLeavesLabel);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Create table
        String[] columns = { "Leave ID", "Start Date", "End Date", "Leave Type", "Reason", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        leavesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(leavesTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create back button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("← Back to Home");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.addActionListener(e -> {
            new UserHome(userId).setVisible(true);
            dispose();
        });
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadLeaveData();
    }

    private void loadLeaveData() {
        try (Connection conn = ConnectionClass.getConnection()) {
            // Get total allowed leaves
            try (CallableStatement cs = conn.prepareCall("{call GetEmployeeAllowedLeaves(?)}")) {
                cs.setInt(1, employeeId);
                ResultSet rs = cs.executeQuery();
                if (rs.next()) {
                    int totalLeaves = rs.getInt("allowed_leaves");
                    totalLeavesLabel.setText("Total Leaves: " + totalLeaves);

                    // Get used leaves using stored procedure
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    System.out.println("Debug - Current Year: " + currentYear);
                    System.out.println("Debug - Employee ID: " + employeeId);

                    try (CallableStatement usedLeavesStmt = conn.prepareCall("{call GetEmployeeUsedLeaves(?, ?)}")) {
                        usedLeavesStmt.setInt(1, employeeId);
                        usedLeavesStmt.setInt(2, currentYear);
                        ResultSet usedRs = usedLeavesStmt.executeQuery();
                        if (usedRs.next()) {
                            int usedLeaves = usedRs.getInt("used_leaves");
                            System.out.println("Debug - Used Leaves: " + usedLeaves);

                            // Debug query to check leave requests
                            String debugQuery = "SELECT lr.leave_start_date, lr.leave_end_date, ls.status " +
                                    "FROM LeaveRequest lr " +
                                    "JOIN LeaveStatus ls ON lr.status_id = ls.status_id " +
                                    "WHERE lr.employee_id = ? " +
                                    "AND YEAR(lr.leave_start_date) = ?";

                            try (PreparedStatement debugStmt = conn.prepareStatement(debugQuery)) {
                                debugStmt.setInt(1, employeeId);
                                debugStmt.setInt(2, currentYear);
                                ResultSet debugRs = debugStmt.executeQuery();
                                System.out.println("Debug - Leave Requests for " + currentYear + ":");
                                while (debugRs.next()) {
                                    System.out.println("Start Date: " + debugRs.getDate("leave_start_date") +
                                            ", End Date: " + debugRs.getDate("leave_end_date") +
                                            ", Status: " + debugRs.getString("status"));
                                }
                            }

                            int remainingLeaves = totalLeaves - usedLeaves;
                            remainingLeavesLabel.setText("Remaining Leaves: " + remainingLeaves);
                        }
                    }
                }
            }
            // Load leave history
            try (CallableStatement cs = conn.prepareCall("{call GetEmployeeLeaveHistory(?)}")) {
                cs.setInt(1, employeeId);
                ResultSet rs = cs.executeQuery();

                tableModel.setRowCount(0); // Clear existing rows
                while (rs.next()) {

                    Object[] row = {
                            rs.getInt("request_id"),
                            rs.getDate("leave_start_date"),
                            rs.getDate("leave_end_date"),
                            rs.getString("type_name"),
                            rs.getString("leave_reason"),
                            rs.getString("status")
                    };
                    tableModel.addRow(row);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading leave data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing purposes, you can pass an employee ID and user ID
            new ViewLeaves(1, 1).setVisible(true);
        });
    }
}