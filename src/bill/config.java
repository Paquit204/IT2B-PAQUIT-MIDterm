package bill;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class BillConfig {

    // Connection Method to SQLite
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC"); // Load the SQLite JDBC driver
            con = DriverManager.getConnection("jdbc:sqlite:Schedule.db"); // Establish connection
            System.out.println("Connection Successful");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection Failed: " + e);
        }
        return con;
    }

    // Method to add a new bill record
    public void addBill(String sql, Object... values) {
        try (Connection conn = BillConfig.connectDB(); // Use the connectDB method
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setPreparedStatementValues(pstmt, values);
            pstmt.executeUpdate();
            System.out.println("Bill added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding bill: " + e.getMessage());
        }
    }

    // Method to view bill records
    public void viewBills(String sqlQuery, String[] columnHeaders, String[] columnNames) {
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (Connection conn = BillConfig.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {

            printColumnHeaders(columnHeaders);
            while (rs.next()) {
                StringBuilder row = new StringBuilder("| ");
                for (String colName : columnNames) {
                    String value = rs.getString(colName);
                    row.append(String.format("%-20s | ", value != null ? value : ""));
                }
                System.out.println(row.toString());
            }
            System.out.println("--------------------------------------------------------------------------------");
        } catch (SQLException e) {
            System.out.println("Error retrieving bills: " + e.getMessage());
        }
    }


    public void updateBill(String sql, Object... values) {
        try (Connection conn = BillConfig.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setPreparedStatementValues(pstmt, values);
            pstmt.executeUpdate();
            System.out.println("Bill updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating bill: " + e.getMessage());
        }
    }

   
    public void deleteBill(String sql, Object... values) {
        try (Connection conn = BillConfig.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setPreparedStatementValues(pstmt, values);
            pstmt.executeUpdate();
            System.out.println("Bill deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting bill: " + e.getMessage());
        }
    }

    /
    private void setPreparedStatementValues(PreparedStatement pstmt, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) values[i]);
            } else if (values[i] instanceof Double) {
                pstmt.setDouble(i + 1, (Double) values[i]);
            } else if (values[i] instanceof Float) {
                pstmt.setFloat(i + 1, (Float) values[i]);
            } else if (values[i] instanceof Long) {
                pstmt.setLong(i + 1, (Long) values[i]);
            } else if (values[i] instanceof Boolean) {
                pstmt.setBoolean(i + 1, (Boolean) values[i]);
            } else if (values[i] instanceof java.util.Date) {
                pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) values[i]).getTime()));
            } else if (values[i] instanceof java.sql.Date) {
                pstmt.setDate(i + 1, (java.sql.Date) values[i]);
            } else {
                pstmt.setString(i + 1, values[i].toString());
            }
        }
    }

    
    private void printColumnHeaders(String[] columnHeaders) {
        StringBuilder headerLine = new StringBuilder();
        headerLine.append("--------------------------------------------------------------------------------\n| ");
        for (String header : columnHeaders) {
            headerLine.append(String.format("%-20s | ", header));
        }
        headerLine.append("\n--------------------------------------------------------------------------------");
        System.out.println(headerLine.toString());
    }
    
    
    
    
    
}
