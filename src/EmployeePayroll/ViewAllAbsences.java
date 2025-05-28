// not used now with ViewAbsences.java in place








package EmployeePayroll;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;


public class ViewAllAbsences extends JFrame {

    private JTable table;

    public ViewAllAbsences() {
        setTitle("All Absences");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null); // center the window

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        fetchAndDisplayAbsences();
    }

    private void fetchAndDisplayAbsences() {
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        dtm.setRowCount(0); // Clear existing rows
        dtm.setColumnCount(0); // Clear existing columns

        String url = "jdbc:sqlserver://localhost:1433;databaseName=employeePayrollDB;encrypt=true;trustServerCertificate=true;";
        String username = "sa";
        String password = "dblab";

        // New: directly select from the view instead of calling stored procedure
        String query = "SELECT * FROM Absences";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Set column names
            for (int i = 1; i <= columnCount; i++) {
                dtm.addColumn(rsmd.getColumnName(i));
            }

            // Add rows to table
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                dtm.addRow(rowData);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching absence data:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewAllAbsences frame = new ViewAllAbsences();
            frame.setVisible(true);
        });
    }
}
