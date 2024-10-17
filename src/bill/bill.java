package bill;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class bill {

    static Scanner sc = new Scanner(System.in);
    private static Connection conn;

    // Connect to the SQLite database
    public static Connection connectDB() {
        if (conn == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection("jdbc:sqlite:Schedule.db");
                System.out.println("Connection Successful");
                createScheduleTable(); // Ensure the schedule table exists
                createBillTable(); // Ensure the bill table exists
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("Connection Failed: " + e.getMessage());
            }
        }
        return conn;
    }

    // Create the schedule table if it doesn't exist
    private static void createScheduleTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Schedule (" +
                     "schedule_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "day_of_week TEXT NOT NULL, " +
                     "start_time TIME NOT NULL, " +
                     "end_time TIME NOT NULL, " +
                     "CHECK (start_time < end_time))";

        try (PreparedStatement pstmt = connectDB().prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating schedule table: " + e.getMessage());
        }
    }

    // Create the bill table if it doesn't exist
    private static void createBillTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Bill (" +
                     "bill_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "bill_date TEXT NOT NULL, " +
                     "total_amount REAL NOT NULL, " +
                     "payment_status TEXT NOT NULL)";

        try (PreparedStatement pstmt = connectDB().prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating bill table: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        while (true) {
            displayChoices();
        }
    }

    // Add a new schedule
    public static void addSchedule() {
        System.out.print("Day of Week: ");
        String day = sc.nextLine();
        System.out.print("Start Time (HH:MM:SS): ");
        String startTime = sc.nextLine();
        System.out.print("End Time (HH:MM:SS): ");
        String endTime = sc.nextLine();

        String sql = "INSERT INTO Schedule (day_of_week, start_time, end_time) VALUES (?, ?, ?)";
        executeUpdate(sql, day, startTime, endTime);
    }

    // Update a schedule
    public static void updateSchedule() {
        System.out.print("Enter Schedule ID to update: ");
        int id = sc.nextInt();
        sc.nextLine(); // Consume newline
        System.out.print("Updated Day of Week: ");
        String day = sc.nextLine();
        System.out.print("Updated Start Time (HH:MM:SS): ");
        String startTime = sc.nextLine();
        System.out.print("Updated End Time (HH:MM:SS): ");
        String endTime = sc.nextLine();

        String sql = "UPDATE Schedule SET day_of_week = ?, start_time = ?, end_time = ? WHERE schedule_id = ?";
        executeUpdate(sql, day, startTime, endTime, id);
    }

    // Delete a schedule
    public static void deleteSchedule() {
        System.out.print("Enter Schedule ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine(); // Consume newline

        String sql = "DELETE FROM Schedule WHERE schedule_id = ?";
        executeUpdate(sql, id);
    }

    // View all schedules
    public static void viewSchedule() {
        String sql = "SELECT * FROM Schedule";
        try (PreparedStatement pstmt = connectDB().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("schedule_id");
                String day = rs.getString("day_of_week");
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");
                System.out.printf("ID: %d, Day: %s, Start Time: %s, End Time: %s%n", id, day, startTime, endTime);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving schedule records: " + e.getMessage());
        }
    }

    // Add a new bill
    public static void addBill() {
        System.out.print("Bill Date (YYYY-MM-DD): ");
        String billDate = sc.nextLine();
        System.out.print("Total Amount: ");
        double totalAmount = sc.nextDouble();
        sc.nextLine(); // Consume newline
        System.out.print("Payment Status: ");
        String paymentStatus = sc.nextLine();

        String sql = "INSERT INTO Bill (bill_date, total_amount, payment_status) VALUES (?, ?, ?)";
        executeUpdate(sql, billDate, totalAmount, paymentStatus);
    }

    // Update a bill
    public static void updateBill() {
        System.out.print("Enter Bill ID to update: ");
        int id = sc.nextInt();
        sc.nextLine(); // Consume newline
        System.out.print("Updated Bill Date (YYYY-MM-DD): ");
        String billDate = sc.nextLine();
        System.out.print("Updated Total Amount: ");
        double totalAmount = sc.nextDouble();
        sc.nextLine(); // Consume newline
        System.out.print("Updated Payment Status: ");
        String paymentStatus = sc.nextLine();

        String sql = "UPDATE Bill SET bill_date = ?, total_amount = ?, payment_status = ? WHERE bill_id = ?";
        executeUpdate(sql, billDate, totalAmount, paymentStatus, id);
    }

    // Delete a bill
    public static void deleteBill() {
        System.out.print("Enter Bill ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine(); // Consume newline

        String sql = "DELETE FROM Bill WHERE bill_id = ?";
        executeUpdate(sql, id);
    }

    // View all bills
    public static void viewBills() {
        String sql = "SELECT * FROM Bill";
        try (PreparedStatement pstmt = connectDB().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("bill_id");
                String billDate = rs.getString("bill_date");
                double totalAmount = rs.getDouble("total_amount");
                String paymentStatus = rs.getString("payment_status");
                System.out.printf("ID: %d, Date: %s, Total Amount: %.2f, Payment Status: %s%n", id, billDate, totalAmount, paymentStatus);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving bill records: " + e.getMessage());
        }
    }

    // Execute an update operation
    private static void executeUpdate(String sql, Object... values) {
        try (PreparedStatement pstmt = connectDB().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
            System.out.println("Operation completed successfully!");
        } catch (SQLException e) {
            System.out.println("Error executing operation: " + e.getMessage());
        }
    }

    // Display options for user to choose from
    public static void displayChoices() {
        System.out.println("1. ADD SCHEDULE");
        System.out.println("2. UPDATE SCHEDULE");
        System.out.println("3. DELETE SCHEDULE");
        System.out.println("4. VIEW SCHEDULE");
        System.out.println("5. ADD BILL");
        System.out.println("6. UPDATE BILL");
        System.out.println("7. DELETE BILL");
        System.out.println("8. VIEW BILLS");
        System.out.println("9. EXIT");
        System.out.print("Choose an option: ");
        int choice = sc.nextInt();
        sc.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                addSchedule();
                break;
            case 2:
                updateSchedule();
                break;
            case 3:
                deleteSchedule();
                break;
            case 4:
                viewSchedule();
                break;
            case 5:
                addBill();
                break;
            case 6:
                updateBill();
                break;
            case 7:
                deleteBill();
                break;
            case 8:
                viewBills();
                break;
            case 9:
                System.out.println("Exiting...");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please choose a valid option.");
        }
    }
}
