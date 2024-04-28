// In the name of Almighty Allah
// This code is written by "Md Abu Omayer Babu"

// CodeAlpha Internship in "Java Programming" project named "Hotel Reservation System"

// Special Features: JDBC with MySQL Database Integration, Graphical User Interface (GUI)

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.*;

public class HotelReservationSystemGUI {
    private JFrame frame;
    private JPanel panel;
    private JButton searchRooomsButton;
    private JButton makeReservationButton;
    private JButton viewReservationButton;
    private JButton updateBookingButton;
    private JButton paymentProcessingButton;
    private JButton exitButton;
    private JTable bookingDetailsTable;

    private Connection connection;
    String url = "jdbc:mysql://localhost/hotel_db";
    String user = "replace_with_your_own_name";
    String password = "replace_with_your_own_password";

    public HotelReservationSystemGUI() {
        // connect to the database
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        frame = new JFrame("Hotel Reservation System");
        panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));

        searchRooomsButton = new JButton("Search for Available Rooms");
        makeReservationButton = new JButton("Make Reservation");
        viewReservationButton = new JButton("View Reservation Details");
        updateBookingButton = new JButton("Update Reservation Details");
        paymentProcessingButton = new JButton("Payment Processing");
        exitButton = new JButton("Exit");

        searchRooomsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchAvailableRooms();
            }
        });

        makeReservationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                makeReservation();
            }
        });

        viewReservationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showBookingDetails();
            }
        });

        updateBookingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateBookingDetails();
            }
        });

        paymentProcessingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UpdatePaymentStatus();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    exit();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Adding buttons to the panel
        panel.add(searchRooomsButton);
        panel.add(makeReservationButton);
        panel.add(viewReservationButton);
        panel.add(updateBookingButton);
        panel.add(paymentProcessingButton);
        panel.add(exitButton);

        frame.add(panel); // Adding the panel to the frame

        // set frame properties
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Centre the frame
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void searchAvailableRooms() {
        // Create JComboBox for category
        String[] categories = { "A", "B", "C" };
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);

        // Create JComboBox for room type
        String[] roomTypes = { "Single", "Double", "Suite" };
        JComboBox<String> roomTypeComboBox = new JComboBox<>(roomTypes);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Category: "));
        inputPanel.add(categoryComboBox);
        inputPanel.add(new JLabel("Room Type: "));
        inputPanel.add(roomTypeComboBox);

        int result = JOptionPane.showConfirmDialog(null, inputPanel, "Search for Available Rooms",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // Get user selected category and room type
            String category = (String) categoryComboBox.getSelectedItem();
            String roomType = (String) roomTypeComboBox.getSelectedItem();

            // Query the database to find available rooms based on user preferences
            try {
                String query = "SELECT * FROM rooms r " +
                        "LEFT JOIN reservations rs ON r.room_id = rs.room_id " +
                        "WHERE category = ? AND room_type = ? AND rs.room_id IS NULL";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, category);
                statement.setString(2, roomType);
                ResultSet resultSet = statement.executeQuery();

                // Create table model
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.addColumn("Floor Number");
                tableModel.addColumn("Room Number");

                // Populate table model with data from result set
                while (resultSet.next()) {
                    int floorNumber = resultSet.getInt("floor_number");
                    int roomNumber = resultSet.getInt("room_number");
                    tableModel.addRow(new Object[] { floorNumber, roomNumber });
                }

                // Create JTable with the table model
                bookingDetailsTable = new JTable(tableModel);

                // Show table in a scrollable dialog
                JOptionPane.showMessageDialog(frame, new JScrollPane(bookingDetailsTable), "Available Rooms",
                        JOptionPane.PLAIN_MESSAGE);

                resultSet.close();
                statement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error occurred while searching for available rooms.");
            }
        }
    }

    private void makeReservation() {
        JTextField nameField = new JTextField();
        JTextField contactNumberField = new JTextField();
        JTextField nidNumberField = new JTextField();
        JTextField permanentAddressField = new JTextField();
        JTextField roomNumberField = new JTextField();
        JTextField bookingDurationField = new JTextField();
    
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Contact Number:"));
        panel.add(contactNumberField);
        panel.add(new JLabel("NID Number:"));
        panel.add(nidNumberField);
        panel.add(new JLabel("Permanent Address:"));
        panel.add(permanentAddressField);
        panel.add(new JLabel("Room Number(s):"));
        panel.add(roomNumberField);
        panel.add(new JLabel("Booking Duration (Hour(s)):"));
        panel.add(bookingDurationField);
    
        int result = JOptionPane.showConfirmDialog(frame, panel, "Make Reservation",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String contactNumber = contactNumberField.getText();
            String nidNumber = nidNumberField.getText();
            String permanentAddress = permanentAddressField.getText();
            String roomNumbers = roomNumberField.getText();
    
            String[] roomNumbersArray = roomNumbers.split(",");
    
            try {
                int bookingDuration = Integer.parseInt(bookingDurationField.getText());
                // Check if booking duration is a positive integer
                if (bookingDuration <= 0) {
                    JOptionPane.showMessageDialog(frame, "Booking duration must be a positive integer.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Exit the method if booking duration is invalid
                }
    
                boolean reservationSuccessful = checkAvailabilityAndMakeReservation(name, contactNumber, nidNumber,
                        permanentAddress, roomNumbersArray, bookingDuration);
                if (reservationSuccessful) {
                    // double billToPay = calculateBillToPay(roomNumbersArray, bookingDuration);
                    // Get the reservation ID based on user details
                    int userId = getUserId(name, contactNumber, nidNumber, permanentAddress);
                    // Assuming you have a method to get the reservation ID
                    int reservationId = getReservationId(userId);
                    // Update payment status to "Paid" using the reservation ID
                    updatePaymentStatusToPaid(reservationId);
                    JOptionPane.showMessageDialog(frame, "Reservation made successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to make reservation!!! The " + roomNumbers + " number room is unavailable.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                // Handle case where booking duration is not a valid integer
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Booking duration must be a valid integer.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLIntegrityConstraintViolationException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Room number is invalid for our hotel.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error occurred while making reservation.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }    

    private int getReservationId(int userId) throws SQLException {
        int reservationId = -1; // Initialize to a default value indicating no reservation found
        String query = "SELECT reservation_id FROM reservations WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            reservationId = resultSet.getInt("reservation_id");
        }
        resultSet.close();
        statement.close();
        return reservationId;
    }

    private String getRoomType(int roomId) throws SQLException {
        String roomType = "";
        String query = "SELECT room_type FROM rooms WHERE room_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, roomId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            roomType = resultSet.getString("room_type");
        }
        resultSet.close();
        statement.close();
        return roomType;
    }

    private String getRoomCategory(int roomId) throws SQLException {
        String category = "";
        String query = "SELECT category FROM rooms WHERE room_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, roomId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            category = resultSet.getString("category");
        }
        resultSet.close();
        statement.close();
        return category;
    }

    // Method to check room availability and make reservation
    private boolean checkAvailabilityAndMakeReservation(String name, String contactNumber, String nidNumber,
            String permanentAddress, String[] roomNumbersArray, int bookingDuration) throws SQLException {
        // Query database to check room availability
        String query = "SELECT COUNT(*) FROM reservations WHERE room_id = ? AND DATE_ADD(booking_date, INTERVAL booking_duration DAY) > CURRENT_DATE";
        PreparedStatement availabilityStatement = connection.prepareStatement(query);

        // Check availability for each room
        for (String roomNumber : roomNumbersArray) {
            int roomId = getRoomId(Integer.parseInt(roomNumber));
            availabilityStatement.setInt(1, roomId);
            ResultSet resultSet = availabilityStatement.executeQuery();
            if (resultSet.next()) {
                int reservationsCount = resultSet.getInt(1);
                if (reservationsCount > 0) {
                    return false;
                }
            }
        }

        // All rooms are available, proceed with reservation
        // Insert reservation details into the database

        String reservationQuery = "INSERT INTO reservations (user_id, room_id, booking_date, booking_duration) VALUES (?, ?, CURRENT_DATE, ?)";
        PreparedStatement reservationStatement = connection.prepareStatement(reservationQuery);
        int userId = getUserId(name, contactNumber, nidNumber, permanentAddress);
        for (String roomNumber : roomNumbersArray) {
            int roomId = getRoomId(Integer.parseInt(roomNumber));
            reservationStatement.setInt(1, userId);
            reservationStatement.setInt(2, roomId);
            reservationStatement.setInt(3, bookingDuration);
            reservationStatement.executeUpdate();
        }

        return true;
    }

    // Method to get the user ID based on user details
    public int getUserId(String name, String contactNumber, String nidNumber, String permanentAddress)
            throws SQLException {
        // Query the database to get the user ID based on user details
        String query = "SELECT user_id FROM users WHERE name = ? AND contact_number = ? AND nid_number = ? AND permanent_address = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, name);
        statement.setString(2, contactNumber);
        statement.setString(3, nidNumber);
        statement.setString(4, permanentAddress);
        ResultSet resultSet = statement.executeQuery();
        int userId = 0;
        if (resultSet.next()) {
            userId = resultSet.getInt("user_id");
        } else {
            // If user not found, insert new user into the database
            String insertQuery = "INSERT INTO users (name, contact_number, nid_number, permanent_address) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery,
                    Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(1, name);
            insertStatement.setString(2, contactNumber);
            insertStatement.setString(3, nidNumber);
            insertStatement.setString(4, permanentAddress);
            insertStatement.executeUpdate();
            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                userId = generatedKeys.getInt(1);
            }
            generatedKeys.close();
            insertStatement.close();
        }
        resultSet.close();
        statement.close();
        return userId;
    }

    // Method to get the room ID based on the room number
    private int getRoomId(int roomNumber) throws SQLException {
        // Query the database to get the room ID based on the room number
        String query = "SELECT room_id FROM rooms WHERE room_number = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, roomNumber);
        ResultSet resultSet = statement.executeQuery();
        int roomId = 0;
        if (resultSet.next()) {
            roomId = resultSet.getInt("room_id");
        }
        resultSet.close();
        statement.close();
        return roomId;
    }

    // Method to display booking details in a table
    private void showBookingDetails() {
        // Query the database to get booking details
        try {
            String query = "SELECT r.reservation_id, r.booking_date, r.booking_duration, u.name, u.contact_number, u.nid_number, u.permanent_address, rt.room_number, COALESCE(r.payment_status, 'Unpaid') AS payment_status, p.bill_to_pay "
                    + "FROM reservations r " +
                    "INNER JOIN users u ON r.user_id = u.user_id " +
                    "INNER JOIN rooms rt ON r.room_id = rt.room_id " +
                    "LEFT JOIN payments p ON r.reservation_id = p.reservation_id";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Create table model
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Reservation ID");
            model.addColumn("Booking Date");
            model.addColumn("Booking Duration");
            model.addColumn("Name");
            model.addColumn("Contact Number");
            model.addColumn("NID Number");
            model.addColumn("Permanent Address");
            model.addColumn("Room Number");
            model.addColumn("Payment Status");
            model.addColumn("Bill to Pay");

            // Populate the table model with data from the result set
            while (resultSet.next()) {
                Object[] row = new Object[10];
                row[0] = resultSet.getInt("reservation_id");
                row[1] = resultSet.getDate("booking_date");
                row[2] = resultSet.getInt("booking_duration");
                row[3] = resultSet.getString("name");
                row[4] = resultSet.getString("contact_number");
                row[5] = resultSet.getString("nid_number");
                row[6] = resultSet.getString("permanent_address");
                row[7] = resultSet.getInt("room_number");
                row[8] = resultSet.getString("payment_status");
                // Calculate bill to pay based on room category and booking duration
                int roomId = resultSet.getInt("room_number");
                double billToPay = calculateBillToPay(roomId, resultSet.getInt("booking_duration"));
                row[9] = billToPay;
                model.addRow(row);
            }

            // Create JTable with the table model
            JTable bookingDetailsTable = new JTable(model);

            // Set default column width for all columns
            int defaultColumnWidth = 600;
            for (int i = 0; i < bookingDetailsTable.getColumnCount(); i++) {
                bookingDetailsTable.getColumnModel().getColumn(i).setPreferredWidth(defaultColumnWidth);
            }

            // Enable horizontal scrolling if needed
            JScrollPane scrollPane = new JScrollPane(bookingDetailsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            // Set preferred size for the scroll pane to create a larger table window
            scrollPane.setPreferredSize(new Dimension(800, 400));

            // Show table in a larger dialog
            JOptionPane.showMessageDialog(frame, scrollPane, "Booking Details", JOptionPane.PLAIN_MESSAGE);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            if (e.getSQLState().equals("42S02")) {
                // Handle the case where the payments table doesn't exist
                // This could involve displaying a message to the user or logging the error
                System.out.println("The payments table does not exist.");
            } else {
                // Handle other SQL exceptions
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error occurred while fetching booking details.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    // Method to calculate bill to pay based on room category and room type
    private double calculateBillToPay(int roomId, int bookingDuration) throws SQLException {
        double totalBill = 10;
        try {
            String category = getRoomCategory(roomId);
            String roomType = getRoomType(roomId);

            // Calculate rate based on room category and type
            double rate = 10;

            if (category.equals("A")) {
                if (roomType.equals("Single")) {
                    rate = 30;
                } else if (roomType.equals("Double")) {
                    rate = 60;
                } else if (roomType.equals("Suit")) {
                    rate = 90;
                }
            } else if (category.equals("B")) {
                if (roomType.equals("Single")) {
                    rate = 20;
                } else if (roomType.equals("Double")) {
                    rate = 40;
                } else if (roomType.equals("Suit")) {
                    rate = 60;
                }
            } else if (category.equals("C")) {
                if (roomType.equals("Single")) {
                    rate = 10;
                } else if (roomType.equals("Double")) {
                    rate = 20;
                } else if (roomType.equals("Suit")) {
                    rate = 30;
                }
            }

            // Calculate total bill based on rate and booking duration
            totalBill = rate * bookingDuration;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return totalBill;
    }

    // Method to update booking details
    private void updateBookingDetails() {
        // Display options for updating booking details
        String[] options = { "Update User Details", "Delete Reservation" };
        int choice = JOptionPane.showOptionDialog(frame, "Select an option", "Update Booking Details",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0:
                // Update user details
                updateReservationDetails();
                break;
            case 1:
                // Delete reservation
                deleteReservation();
                break;
            default:
                break;
        }
    }

    private void updateReservationDetails() {
        // Get reservation ID
        int reservationId = gettingReservationID();

        // Confirm deletion with the user
        int confirmation = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete reservation #" + reservationId + "?", "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(frame, "Old Reservation is deleted successfully!\n Now, make a new reservation to update details");
            makeReservation(); // If user cancels deletion, proceed with updating the reservation
        }
    }

    private int gettingReservationID() {
        // Create input fields for reservation ID
        JTextField reservationIdField = new JTextField();

        // Create panel with input field
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Reservation ID:"));
        panel.add(reservationIdField);

        // Show input dialog
        int result = JOptionPane.showConfirmDialog(frame, panel, "Enter Reservation ID",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Get user input
                return Integer.parseInt(reservationIdField.getText());
            } catch (NumberFormatException ex) {
                // Handle invalid input
                JOptionPane.showMessageDialog(frame, "Invalid Reservation ID. Please enter a valid number.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Return -1 if input is invalid or canceled
        return -1;
    }

    private void deleteReservation() {
        // Get reservation ID
        int reservationId = gettingReservationID();

        // Check if reservationId is -1, indicating cancellation or invalid input
        if (reservationId == -1) {
            return; // Exit the method
        }
        // Confirm deletion with the user
        int confirmation = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete reservation #" + reservationId + "?", "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            // Delete reservation from the database
            try {
                String deleteQuery = "DELETE FROM reservations WHERE reservation_id = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                deleteStatement.setInt(1, reservationId);

                int rowsDeleted = deleteStatement.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(frame, "Reservation deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to delete reservation. Reservation ID not found.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                deleteStatement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error occurred while deleting reservation.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void UpdatePaymentStatus() {
        // Get reservation ID from the user
        JTextField reservationIdField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Reservation ID:"));
        panel.add(reservationIdField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Enter Reservation ID",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // Get user input
            int reservationId = Integer.parseInt(reservationIdField.getText());

            // Update payment status to "Paid" for the given reservation ID
            try {
                String updateQuery = "UPDATE reservations SET payment_status = 'Paid' WHERE reservation_id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setInt(1, reservationId);
                int rowsUpdated = updateStatement.executeUpdate();
                updateStatement.close();

                if (rowsUpdated > 0) {
                    // Payment status updated successfully, prompt user to pay the bill
                    payBill(reservationId);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "No rows updated. Reservation ID " + reservationId + " may not exist.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error occurred while updating payment status.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to prompt user to pay the bill
    private void payBill(int reservationId) {
        try {
            // Create a PreparedStatement to execute the SQL query
            String query = "SELECT bill_to_pay FROM reservations WHERE reservation_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            // Set the reservation_id parameter
            statement.setInt(1, reservationId);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Check if the query returned any rows
            if (resultSet.next()) {
                // Prompt user to confirm payment
                int confirmOption = JOptionPane.showConfirmDialog(frame,
                        "Would you like to proceed with the payment?",
                        "Payment Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirmOption == JOptionPane.YES_OPTION) {
                    // Proceed with payment

                    // Update payment status to "Paid"
                    updatePaymentStatusToPaid(reservationId);
                    JOptionPane.showMessageDialog(frame,
                            "Payment processed successfully! Your reservation is now paid.", "Payment Status",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // User chose not to pay
                    JOptionPane.showMessageDialog(frame, "You chose not to pay.", "Payment Status",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // If no rows were returned, it means the reservation ID was not found
                JOptionPane.showMessageDialog(frame, "Bill details not found for reservation ID " + reservationId,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Close the ResultSet and PreparedStatement
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error occurred while processing payment.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePaymentStatusToPaid(int reservationId) {
        try {
            // Update the payment status to "Paid" for the specified reservation ID
            String updateQuery = "UPDATE payments SET payment_status = 'Paid' WHERE reservation_id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setInt(1, reservationId);
            // int rowsUpdated = updateStatement.executeUpdate();
            // if (rowsUpdated > 0) {
            // System.out.println("Payment status updated to 'Paid' for reservation ID: " +
            // reservationId);
            // } else {
            // System.out.println("Failed to update payment status for reservation ID: " +
            // reservationId);
            // }
            updateStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void exit() throws InterruptedException {
        JFrame exitFrame = new JFrame("Exiting System");
        exitFrame.setSize(300, 150);
        exitFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        exitFrame.setLayout(null);
        exitFrame.setLocationRelativeTo(null); // Center the frame

        JLabel messageLabel = new JLabel("𝑬𝒙𝒊𝒕𝒊𝒏𝒈 𝑺𝒚𝒔𝒕𝒆𝒎...");
        messageLabel.setBounds(100, 20, 200, 30);
        exitFrame.add(messageLabel);

        JLabel dotLabel = new JLabel("");
        dotLabel.setBounds(100, 50, 200, 30);
        exitFrame.add(dotLabel);

        exitFrame.setVisible(true);

        Thread thread = new Thread(() -> {
            try {
                for (int i = 5; i > 0; i--) {
                    dotLabel.setText(dotLabel.getText() + "● ");
                    Thread.sleep(500);
                }
                exitFrame.dispose();
                showThankYouMessage();
                System.out.println("Thank You For Using This Hotel Reservation System!!!");
                // System.exit(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private static void showThankYouMessage() {
        JFrame thankYouFrame = new JFrame("Thank You");
        thankYouFrame.setSize(400, 150);
        thankYouFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        thankYouFrame.setLocationRelativeTo(null); // Center the frame

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centered alignment with 10px horizontal
                                                                              // and vertical gap
        thankYouFrame.add(panel);

        JLabel thankYouLabel = new JLabel(
                "𝐓𝐡𝐚𝐧𝐤 𝐘𝐨𝐮 𝐅𝐨𝐫 𝐔𝐬𝐢𝐧𝐠 𝐓𝐡𝐢𝐬 𝐇𝐨𝐭𝐞𝐥 𝐑𝐞𝐬𝐞𝐫𝐯𝐚𝐭𝐢𝐨𝐧 𝐒𝐲𝐬𝐭𝐞𝐦!!!");
        panel.add(thankYouLabel);

        thankYouFrame.setVisible(true);

        Timer timer = new Timer(3000, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                thankYouFrame.dispose();
                System.exit(0);
            }
        });

        timer.setRepeats(false);
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HotelReservationSystemGUI();
            }
        });
    }

}