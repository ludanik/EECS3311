package EECS3311.DAO;

import EECS3311.Models.*;
import EECS3311.Models.ParkingLot;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BookingDAO {
    public static void addBooking(Booking b) {
        if (BookingDAO.getBooking(b.getId()) != null) return;
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement insertStmt = c.prepareStatement(
                    "INSERT INTO bookings(client_id, parking_space_id, license_plate, start_time, end_time, deposit, total_cost, status, payment_method) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            insertStmt.setInt(1, b.getClient().getId());
            insertStmt.setInt(2, b.getParkingSpace().getSpaceId());
            insertStmt.setString(3, b.getLicensePlate());
            insertStmt.setTimestamp(4, Timestamp.valueOf(b.getStartTime()));
            insertStmt.setTimestamp(5, Timestamp.valueOf(b.getEndTime()));
            insertStmt.setInt(6, b.getDeposit());
            insertStmt.setInt(7, b.getTotalCost());
            insertStmt.setString(8, b.getStatus().toString().toLowerCase());
            insertStmt.setString(9, b.getPaymentMethod());

            int insertedRows = insertStmt.executeUpdate();
            System.out.printf("inserted %s bookings(s)%n", insertedRows);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Booking> getClientBookings(int clientId) {
        ArrayList<Booking> bookings = new ArrayList<>();
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM bookings WHERE client_id = ?");
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("booking_id");
                User u = null;

                ParkingSpace parkingSpace = ParkingSpaceDAO.getParkingSpace(rs.getInt("parking_space_id"));
                String licensePlate = rs.getString("license_plate");

                Timestamp startTs = rs.getTimestamp("start_time");
                Timestamp endTs = rs.getTimestamp("end_time");

                LocalDateTime startTime = (startTs != null) ? startTs.toLocalDateTime() : null;
                LocalDateTime endTime = (endTs != null) ? endTs.toLocalDateTime() : null;

                int deposit = rs.getInt("deposit");
                int totalCost = rs.getInt("total_cost");
                BookingStatus status = BookingStatus.valueOf(rs.getString("status").toUpperCase());
                String paymentMethod = rs.getString("payment_method");

                Booking booking = new Booking(
                        id,
                        u,
                        parkingSpace,
                        startTime,
                        endTime,
                        deposit,
                        totalCost,
                        paymentMethod,
                        licensePlate,
                        status
                );
                bookings.add(booking);
            }

            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public static Booking getBooking(int id) {
        // Retrieve a booking by id if needed
        return null;
    }

    public static void cancelBooking(int id) {

    }

    public static void extendBooking(int id, int hours) {

    }
}
