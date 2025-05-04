package EmployeePayroll;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddUserWindow extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JLabel userIdLabel;

    public AddUserWindow() {
        setTitle("Add New User");
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
        add(new JLabel("user")); // fixed
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
                stmt.setString(3, "user");
                stmt.registerOutParameter(4, Types.INTEGER);
                stmt.execute();

                int newUserId = stmt.getInt(4);
                userIdLabel.setText("User ID: " + newUserId);
                JOptionPane.showMessageDialog(this, "User Created. ID: " + newUserId);
                new AddEmployee(newUserId); // next screen
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
