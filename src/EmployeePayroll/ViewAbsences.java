package EmployeePayroll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewAbsences extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private int userId;
    private JLabel yearLabel;
    private JLabel totalAbsencesLabel;
    private JComboBox<Integer> yearComboBox;
    private int currentYear;

    public ViewAbsences(int userId) {
        this.userId = userId;
        this.currentYear = Calendar.getInstance().get(Calendar.YEAR);

        setTitle("View Absences");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Main panel with border
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBounds(0, 0, 540, 40);
        headerPanel.setBackground(new Color(255, 69, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel titleLabel = new JLabel("❗ My Uninformed Absences - Year: ");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create year dropdown
        Integer[] years = new Integer[5];
        int startYear = currentYear - 2;
        for (int i = 0; i < 5; i++) {
            years[i] = startYear + i;
        }
        yearComboBox = new JComboBox<>(years);
        yearComboBox.setSelectedItem(currentYear);
        yearComboBox.setFont(new Font("Arial", Font.BOLD, 16));
        yearComboBox.setBackground(new Color(255, 69, 0));
        yearComboBox.setForeground(Color.WHITE);
        yearComboBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        yearComboBox.addActionListener(e -> {
            currentYear = (Integer) yearComboBox.getSelectedItem();
            model.setRowCount(0); // Clear existing data
            loadAbsences(); // Reload data for selected year
        });
        headerPanel.add(yearComboBox, BorderLayout.EAST);

        mainPanel.add(headerPanel);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBounds(0, 50, 540, 60);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel infoLabel1 = new JLabel("You were marked absent without prior notice");
        JLabel infoLabel2 = new JLabel("on the following days.");
        infoLabel1.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel2.setFont(new Font("Arial", Font.PLAIN, 14));

        totalAbsencesLabel = new JLabel("Total Uninformed Absences: 0 days");
        totalAbsencesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalAbsencesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(infoLabel1);
        infoPanel.add(infoLabel2);
        infoPanel.add(Box.createVerticalStrut(0));
        infoPanel.add(totalAbsencesLabel);

        mainPanel.add(infoPanel);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBounds(0, 150, 540, 250);
        tablePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Create table model with columns
        String[] columns = { "Date", "Day", "Department" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setGridColor(Color.GRAY);
        table.setShowGrid(true);

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel);

        // Back Button
        JButton backBtn = new JButton("⬅ Back to Dashboard");
        backBtn.setBounds(200, 410, 200, 40);
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.addActionListener(e -> {
            new UserHome(userId).setVisible(true);
            dispose();
        });
        mainPanel.add(backBtn);

        // Load data
        loadAbsences();

        // Make sure the frame is visible
        setVisible(true);
    }

    private void loadAbsences() {
        try {
            ConnectionClass obj = new ConnectionClass();
            String query = "SELECT a.date, d.department_name " +
                    "FROM Attendance a " +
                    "JOIN Employees e ON a.employee_id = e.employee_id " +
                    "JOIN Departments d ON e.department_id = d.department_id " +
                    "WHERE e.user_id = ? AND a.status = 'Absent' " +
                    "AND YEAR(a.date) = ? " +
                    "ORDER BY a.date DESC";

            PreparedStatement pst = obj.con.prepareStatement(query);
            pst.setInt(1, userId);
            pst.setInt(2, currentYear);
            ResultSet rs = pst.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

            int totalAbsences = 0;
            while (rs.next()) {
                Date date = rs.getDate("date");
                String day = dayFormat.format(date);
                Object[] row = {
                        dateFormat.format(date),
                        day,
                        rs.getString("department_name")
                };
                model.addRow(row);
                totalAbsences++;
            }

            totalAbsencesLabel.setText("Total Uninformed Absences: " + totalAbsences + " days");

            rs.close();
            pst.close();
            obj.con.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading absences: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // For testing purposes
        SwingUtilities.invokeLater(() -> {
            new ViewAbsences(1); // Replace 1 with a valid user ID for testing
        });
    }
}
