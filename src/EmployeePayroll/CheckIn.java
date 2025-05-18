package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CheckIn extends JFrame {
    int User_id;

    public CheckIn(int User_id) {
        this.User_id = User_id;
        setTitle("Employee Check-In");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Header Label ---
        JLabel titleLabel = new JLabel("Check-In", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Center Panel with Message ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));
        JLabel msgLabel = new JLabel("Press the button below to check in for today:", JLabel.CENTER);
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        centerPanel.add(msgLabel);
        add(centerPanel, BorderLayout.CENTER);

        // --- Buttons ---
        JButton checkinButton = new JButton("Check In");
        checkinButton.setFont(new Font("Arial", Font.BOLD, 16));
        checkinButton.setBackground(Color.GREEN);
        checkinButton.setForeground(Color.WHITE);
        checkinButton.setFocusPainted(false);
        checkinButton.setPreferredSize(new Dimension(120, 40));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(120, 40));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(checkinButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Check-In Button Action ---
        checkinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performCheckIn();
            }
        });

        // --- Back Button Action ---
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close CheckIn window
                new UserHome(User_id).setVisible(true); // Navigate back to UserHome
            }
        });

        setVisible(true);
    }

    private void performCheckIn() {
        try (Connection con = EmployeePayroll.ConnectionClass.getConnection()) {
            CallableStatement cs = con.prepareCall("{call CheckInEmployeeByUserId(?)}");
            cs.setInt(1, User_id);
            cs.execute();

            JOptionPane.showMessageDialog(this, "✅ Check-in successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error during check-in:\n" + e.getMessage(), "Check-In Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

//    public static void main(String[] args) {
//        int employee_id = 3;
//        new CheckIn(employee_id);
//    }
}
