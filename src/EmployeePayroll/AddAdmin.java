package EmployeePayroll;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddAdmin extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JLabel userIdLabel;

    public AddAdmin() {
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
            try {
                ConnectionClass connectionClass = new ConnectionClass();
                Connection con = connectionClass.con;

                CallableStatement stmt = con.prepareCall("{call InsertUser(?, ?, ?, ?)}");
                stmt.setString(1, usernameField.getText());
                stmt.setString(2, new String(passwordField.getPassword()));
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
            new AdminHome().setVisible(true);
            this.setVisible(false);
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
