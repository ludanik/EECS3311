package EECS3311.UI;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
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

        // Set custom renderers and editors for the button columns:
        // Column indices: 6 = ARRIVE, 7 = EXTEND, 8 = CANCEL
        bookingTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        bookingTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), "ARRIVE"));

        bookingTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        bookingTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox(), "EXTEND"));

        bookingTable.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        bookingTable.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox(), "CANCEL"));

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh bookings when the panel is shown
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshBookings();
            }
        });
    }

    // Refresh the table with updated bookings from the database.
    public void refreshBookings() {
        ArrayList<Booking> bookings = BookingDAO.getClientBookings(mainFrame.getCurrentUser().getId());
        tableModel.updateData(bookings);
    }

    // --- Custom Button Renderer ---
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    // --- Custom Button Editor ---
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private String actionType;
        private int row;

        public ButtonEditor(JCheckBox checkBox, String actionType) {
            super(checkBox);
            this.actionType = actionType;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Retrieve the Booking for the current row. Ensure BookingTableModel implements getBookingAt(row)
                Booking booking = tableModel.getBookingAt(row);
                if (booking != null) {
                    switch (actionType) {
                        case "ARRIVE":
                            // Process ARRIVE action. Replace with your actual logic.
                            JOptionPane.showMessageDialog(button, "Arrived for booking starting at " + booking.getStartTime());
                            break;
                        case "EXTEND":
                            // Process EXTEND action. Replace with your actual extension logic.
                            JOptionPane.showMessageDialog(button, "Extend booking starting at " + booking.getStartTime());
                            break;
                        case "CANCEL":
                            int confirm = JOptionPane.showConfirmDialog(button,
                                    "Are you sure you want to cancel this booking?",
                                    "Cancel Booking", JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                // Cancel the booking via BookingDAO. Assume cancelBooking exists.
                                BookingDAO.cancelBooking(booking.getId());
                                JOptionPane.showMessageDialog(button, "Booking canceled.");
                            }
                            break;
                    }
                    // Refresh table data after action
                    refreshBookings();
                }
            }
            isPushed = false;
            return label;
        }
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
