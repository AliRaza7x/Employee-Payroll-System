package EmployeePayroll;

import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import javax.swing.*;

public class Login extends JFrame implements ActionListener, KeyListener {
    JLabel l1, l2, l3, l4;
    JTextField textField;
    JPasswordField passwordField;
    JPanel p1, p2, p3;
    JButton bt1, bt2;
    Font f1, f2;

    public Login() {
        super("Login - Employee Payroll System");
        setLocation(400, 300);
        setSize(530, 250);

        f1 = new Font("Arial", Font.BOLD, 25);
        f2 = new Font("Arial", Font.BOLD, 18);

        l1 = new JLabel("Welcome to Payroll System!");
        l2 = new JLabel("Username:");
        l3 = new JLabel("Password:");

        l1.setHorizontalAlignment(JLabel.CENTER);
        l1.setFont(f1);
        l2.setFont(f2);
        l3.setFont(f2);

        textField = new JTextField();
        passwordField = new JPasswordField();
        textField.setFont(f2);
        passwordField.setFont(f2);

        // Add key listeners to both fields
        textField.addKeyListener(this);
        passwordField.addKeyListener(this);

        bt1 = new JButton("Login");
        bt2 = new JButton("Cancel");
        bt1.setFont(f2);
        bt2.setFont(f2);
        bt1.addActionListener(this);
        bt2.addActionListener(this);

        ImageIcon img = new ImageIcon(ClassLoader.getSystemResource("Images/payment.png"));
        Image img2 = img.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT);
        ImageIcon img3 = new ImageIcon(img2);
        l4 = new JLabel(img3);

        p1 = new JPanel(new GridLayout(3, 2, 10, 10));
        p1.add(l2);
        p1.add(textField);
        p1.add(l3);
        p1.add(passwordField);
        p1.add(bt1);
        p1.add(bt2);

        p2 = new JPanel(new GridLayout(1, 1, 10, 10));
        p2.add(l1);

        p3 = new JPanel(new GridLayout(1, 1, 10, 10));
        p3.add(l4);

        setLayout(new BorderLayout(10, 20));
        add(p2, "North");
        add(p3, "East");
        add(p1, "Center");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bt1) {
            performLogin();
        }
        if (e.getSource() == bt2) {
            new WelcomePage().setVisible(true);
            this.setVisible(false);
        }
    }

    // New method to handle login
    private void performLogin() {
        String username = textField.getText();
        String password = passwordField.getText();

        try {
            ConnectionClass obj = new ConnectionClass();
            String query = "SELECT user_id, role FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = obj.con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String role = rs.getString("role");

                if ("admin".equalsIgnoreCase(role)) {
                    new NormalAdminHome().setVisible(true);
                } else if ("user".equalsIgnoreCase(role)) {
                    new UserHome(userId).setVisible(true);
                } else if ("superadmin".equalsIgnoreCase(role)) {
                    new AdminHome().setVisible(true);
                }
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Connection failed. Please try again.");
        }
    }

    // Implement KeyListener methods
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            performLogin();
        }
    }

    public void keyReleased(KeyEvent e) {
        // Not needed for this functionality
    }

    public void keyTyped(KeyEvent e) {
        // Not needed for this functionality
    }
}
