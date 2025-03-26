package EECS3311.UI;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

import EECS3311.DAO.ParkingLotDAO;
import EECS3311.Models.Booking;

public class BookingTableModel extends AbstractTableModel {
    private ArrayList<Booking> bookings;
    private final String[] columnNames = {
            "Parking Lot", "Space", "Start Time", "End Time", "Total Cost", "Status", "ARRIVE", "EXTEND", "CANCEL"
    };

    public Booking getBookingAt(int row) {
        return bookings.get(row);
    }

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
                return ParkingLotDAO.getLot(booking.getParkingSpace().getLotId()).getName();
            case 1:
                return booking.getParkingSpace().getSpaceNumber();
            case 2:
                return "" + booking.getStartTime().getHour() + ":" + booking.getStartTime().getMinute();
            case 3:
                return "" + booking.getEndTime().getHour() + ":" + booking.getEndTime().getMinute();
            case 4:
                return "$" + booking.getTotalCost();
            case 5:
                return booking.getStatus().toString();
            case 6:
                return "ARRIVE";
            case 7:
                return "EXTEND";
            case 8:
                return "CANCEL";
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex >= 6;
    }
}
