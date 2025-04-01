package EECS3311.DAO;

import EECS3311.Models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BookingDAOTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Mock the DBUtil.getConnection() method
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
        }

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    void testAddBooking() throws SQLException {
        // Arrange
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1);

        ParkingSpace mockSpace = mock(ParkingSpace.class);
        when(mockSpace.getSpaceId()).thenReturn(101);

        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking(
                1, mockUser, mockSpace, now, now.plusHours(2),
                10, 20, "credit", "ABC123", BookingStatus.BOOKED
        );

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        BookingDAO.addBooking(booking);

        // Assert
        verify(mockPreparedStatement).setInt(1, 1); // client_id
        verify(mockPreparedStatement).setInt(2, 101); // space_id
        verify(mockPreparedStatement).setString(3, "ABC123"); // license_plate
        verify(mockPreparedStatement).setTimestamp(4, Timestamp.valueOf(now)); // start_time
        verify(mockPreparedStatement).setTimestamp(5, Timestamp.valueOf(now.plusHours(2))); // end_time
        verify(mockPreparedStatement).setInt(6, 10); // deposit
        verify(mockPreparedStatement).setInt(7, 20); // total_cost
        verify(mockPreparedStatement).setString(8, "BOOKED"); // status
        verify(mockPreparedStatement).setString(9, "credit"); // payment_method
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddBookingWithException() throws SQLException {
        // Arrange
        User mockUser = mock(User.class);
        ParkingSpace mockSpace = mock(ParkingSpace.class);
        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking(
                1, mockUser, mockSpace, now, now.plusHours(2),
                10, 20, "credit", "ABC123", BookingStatus.BOOKED
        );

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        // Act & Assert (should not throw exception)
        assertDoesNotThrow(() -> BookingDAO.addBooking(booking));
    }

    @Test
    void testGetClientBookings() throws SQLException {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        when(mockResultSet.next()).thenReturn(true, true, false); // two records
        when(mockResultSet.getInt("booking_id")).thenReturn(1, 2);
        when(mockResultSet.getInt("client_id")).thenReturn(1, 1);
        when(mockResultSet.getInt("parking_space_id")).thenReturn(101, 102);
        when(mockResultSet.getString("license_plate")).thenReturn("ABC123", "XYZ789");
        when(mockResultSet.getTimestamp("start_time")).thenReturn(Timestamp.valueOf(now), Timestamp.valueOf(now.minusHours(1)));
        when(mockResultSet.getTimestamp("end_time")).thenReturn(Timestamp.valueOf(now.plusHours(2)), Timestamp.valueOf(now.plusHours(1)));
        when(mockResultSet.getInt("deposit")).thenReturn(10, 5);
        when(mockResultSet.getInt("total_cost")).thenReturn(20, 15);
        when(mockResultSet.getString("status")).thenReturn("BOOKED", "completed");
        when(mockResultSet.getString("payment_method")).thenReturn("credit", "debit");

        // Mock ParkingSpaceDAO
        ParkingSpace mockSpace1 = mock(ParkingSpace.class);
        ParkingSpace mockSpace2 = mock(ParkingSpace.class);
        try (MockedStatic<ParkingSpaceDAO> mockedSpaceDAO = Mockito.mockStatic(ParkingSpaceDAO.class)) {
            mockedSpaceDAO.when(() -> ParkingSpaceDAO.getParkingSpace(101)).thenReturn(mockSpace1);
            mockedSpaceDAO.when(() -> ParkingSpaceDAO.getParkingSpace(102)).thenReturn(mockSpace2);

            // Act
            ArrayList<Booking> bookings = BookingDAO.getClientBookings(1);

            // Assert
            assertEquals(2, bookings.size());

            Booking first = bookings.get(0);
            assertEquals(1, first.getId());
            assertEquals(mockSpace1, first.getParkingSpace());
            assertEquals("ABC123", first.getLicensePlate());
            assertEquals(now, first.getStartTime());
            assertEquals(now.plusHours(2), first.getEndTime());
            assertEquals(10, first.getDeposit());
            assertEquals(20, first.getTotalCost());
            assertEquals(BookingStatus.BOOKED, first.getStatus());
            assertEquals("credit", first.getPaymentMethod());

            Booking second = bookings.get(1);
            assertEquals(2, second.getId());
            assertEquals(mockSpace2, second.getParkingSpace());
            assertEquals("XYZ789", second.getLicensePlate());
            assertEquals(now.minusHours(1), second.getStartTime());
            assertEquals(now.plusHours(1), second.getEndTime());
            assertEquals(5, second.getDeposit());
            assertEquals(15, second.getTotalCost());
            assertEquals(BookingStatus.COMPLETED, second.getStatus());
            assertEquals("debit", second.getPaymentMethod());
        }
    }

    @Test
    void testGetClientBookingsWithException() throws SQLException {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        // Act
        ArrayList<Booking> bookings = BookingDAO.getClientBookings(1);

        // Assert
        assertTrue(bookings.isEmpty());
    }

    @Test
    void testCancelBooking() throws SQLException {
        // Arrange
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        BookingDAO.cancelBooking(1);

        // Assert
        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testCancelBookingWithException() throws SQLException {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        // Act & Assert (should not throw exception)
        assertDoesNotThrow(() -> BookingDAO.cancelBooking(1));
    }

    @Test
    void testExtendBooking() throws SQLException {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Booking mockBooking = mock(Booking.class);
        when(mockBooking.getId()).thenReturn(1);
        when(mockBooking.getEndTime()).thenReturn(now);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        BookingDAO.extendBooking(mockBooking, 2);

        // Assert
        verify(mockPreparedStatement).setTimestamp(1, Timestamp.valueOf(now.plusHours(2)));
        verify(mockPreparedStatement).setInt(2, 1);
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testExtendBookingWithException() throws SQLException {
        // Arrange
        Booking mockBooking = mock(Booking.class);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        // Act & Assert (should not throw exception)
        assertDoesNotThrow(() -> BookingDAO.extendBooking(mockBooking, 2));
    }

    @Test
    void testGetBooking() {
        // Currently returns null, test that behavior
        assertNull(BookingDAO.getBooking(1));
    }
}