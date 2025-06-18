package EmployeePayroll;
import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.sql.*;

public class AddAdmin extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JLabel userIdLabel;
    private JFrame parentDashboard;
    String password;

    public AddAdmin(JFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        setTitle("Add New Admin");
        setSize(400, 250);
        setLayout(new GridLayout(5, 2, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        userIdLabel = new JLabel();

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel("Role:"));
        add(new JLabel("admin")); // fixed
        JButton okBtn = new JButton("OK");
        JButton backBtn = new JButton("Go Back");
        add(okBtn);
        add(backBtn);
        add(userIdLabel);

        okBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String hashedPassword = hashPassword(password);

            // === Input Validation ===
            if (username.isEmpty() || !username.matches("[a-zA-Z0-9_]+")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid username (letters, digits, and underscores only).");
                return;
            }

            if (password.isEmpty() || password.length() < 8) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.");
                return;
            }

            try {
                ConnectionClass connectionClass = new ConnectionClass();
                Connection con = connectionClass.con;

                CallableStatement stmt = con.prepareCall("{call InsertUser(?, ?, ?, ?)}");
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, "admin");
                stmt.registerOutParameter(4, Types.INTEGER);
                stmt.execute();

                int newUserId = stmt.getInt(4);
                userIdLabel.setText("User ID: " + newUserId);
                JOptionPane.showMessageDialog(this, "Admin Created. ID: " + newUserId);
                new AdminHome().setVisible(true);
                this.setVisible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });


        backBtn.addActionListener(e -> {
            parentDashboard.setVisible(true);
            this.setVisible(false);
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
