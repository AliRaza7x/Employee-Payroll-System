package EmployeePayroll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ApproveRejectLeaves extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JButton approveBtn, rejectBtn, backBtn;
    private JFrame parentDashboard;

    public ApproveRejectLeaves(JFrame parentDashboard) {
        this.parentDashboard = parentDashboard;

        setTitle("Approve or Reject Leaves");
        setSize(1000, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table and model
        model = new DefaultTableModel(new String[]{
                "Request ID", "Employee ID", "Start Date", "End Date", "Reason", "Leave Type", "Status"
        }, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        approveBtn = new JButton("Approve");
        rejectBtn = new JButton("Reject");
        backBtn = new JButton("Back");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data
        loadLeaveRequests();

        // Button actions
        approveBtn.addActionListener(e -> handleAction("approve"));
        rejectBtn.addActionListener(e -> handleAction("reject"));
        backBtn.addActionListener(e -> {
            parentDashboard.setVisible(true);
            dispose();
        });
    }

    private void loadLeaveRequests() {
        model.setRowCount(0); // Clear old data
        try (Connection conn = ConnectionClass.getConnection();
             CallableStatement stmt = conn.prepareCall("{call GetAllLeaveRequests}")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("request_id"),
                        rs.getInt("employee_id"),
                        rs.getDate("leave_start_date"),
                        rs.getDate("leave_end_date"),
                        rs.getString("leave_reason"),
                        rs.getString("leave_type"),
                        rs.getString("leave_status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading leave requests:\n" + e.getMessage());
        }
    }

    private void handleAction(String action) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a leave request first.");
            return;
        }

        int requestId = (int) model.getValueAt(selectedRow, 0);

        try (Connection conn = ConnectionClass.getConnection()) {
            CallableStatement stmt;

            if (action.equals("approve")) {
                stmt = conn.prepareCall("{call ApproveLeaveRequest(?)}");
            } else {
                stmt = conn.prepareCall("{call RejectLeaveRequest(?)}");
            }

            stmt.setInt(1, requestId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Leave request " + action + "d successfully.");
            loadLeaveRequests(); // Refresh the table

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating leave request:\n" + e.getMessage());
        }
    }
}
