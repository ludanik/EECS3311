package EECS3311.UI;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import EECS3311.Models.Booking;

public class BookingTableModel extends AbstractTableModel {
    private ArrayList<Booking> bookings;
    private final String[] columnNames = {
            "ID", "User Email", "Parking Lot", "Space", "Start Time", "Duration", "Total Cost", "Status"
    };

    public BookingTableModel(ArrayList<Booking> bookings) {
        this.bookings = bookings;
    }

    public void updateData(ArrayList<Booking> bookings) {
        this.bookings = bookings;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return bookings.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Booking booking = bookings.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return booking.getId();
            case 1:
                return booking.getClient();
            case 2:
                // Assuming your ParkingSpace model has a method getLotName()
                return booking.getParkingSpace().getLotId();
            case 3:
                return booking.getParkingSpace().getSpaceNumber();
            case 4:
                return booking.getStartTime().toString();
            case 5:
                return booking.getEndTime().toString();
            case 6:
                return booking.getTotalCost();
            case 7:
                return booking.getStatus().toString();
            default:
                return null;
        }
    }
}
