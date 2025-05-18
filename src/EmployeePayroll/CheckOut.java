package EmployeePayroll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CheckOut extends JFrame {
    int user_id;

    public CheckOut(int user_id) {
        this.user_id = user_id;
        setTitle("Employee Check-Out");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Check-Out", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 1));
        JLabel msgLabel = new JLabel("Press the button below to check out for today:", JLabel.CENTER);
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        centerPanel.add(msgLabel);
        add(centerPanel, BorderLayout.CENTER);

        // Buttons: Check-Out + Back
        JButton checkoutButton = new JButton("Check Out");
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        checkoutButton.setBackground(Color.RED);
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setPreferredSize(new Dimension(120, 40));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setPreferredSize(new Dimension(120, 40));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(checkoutButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Check-Out Button Action ---
        checkoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performCheckOut();
            }
        });

        // --- Back Button Action ---
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close CheckOut window
                new UserHome(user_id).setVisible(true); // Go back to UserHome
            }
        });

        setVisible(true);
    }

    private void performCheckOut() {
        try (Connection con = ConnectionClass.getConnection()) {
            CallableStatement cs = con.prepareCall("{call CheckOutEmployeeByUserId(?)}");
            cs.setInt(1, user_id);
            cs.execute();

            JOptionPane.showMessageDialog(this, "✅ Check-out successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error during check-out:\n" + e.getMessage(), "Check-Out Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

//    public static void main(String[] args) {
//        new CheckOut(3); // Example user ID
//    }
}
