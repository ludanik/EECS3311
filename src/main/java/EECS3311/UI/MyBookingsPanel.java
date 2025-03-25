package EECS3311.UI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import EECS3311.DAO.BookingDAO;
import EECS3311.Models.Booking;
import EECS3311.Models.User;

public class MyBookingsPanel extends JPanel {
    private JTable bookingTable;
    private BookingTableModel tableModel;
    private MainFrame mainFrame;

    public MyBookingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());

        // Header label
        JLabel header = new JLabel("Your Bookings", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        add(header, BorderLayout.NORTH);

        // Retrieve bookings using the BookingDAO for the current user
        ArrayList<Booking> bookings = BookingDAO.getClientBookings(0);
        tableModel = new BookingTableModel(bookings);
        bookingTable = new JTable(tableModel);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        add(scrollPane, BorderLayout.CENTER);

        // Optionally, add a listener to refresh bookings every time the panel is shown
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshBookings();
            }
        });
    }

    // Refresh the table with updated bookings from the database.
    public void refreshBookings() {
        ArrayList<Booking> bookings = BookingDAO.getClientBookings(mainFrame.getCurrentUser().getId());
        tableModel.updateData(bookings);
    }
}

