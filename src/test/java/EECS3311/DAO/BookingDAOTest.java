package EECS3311.DAO;

import EECS3311.Models.*;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BookingDAOTest {
    private static Connection testConnection;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeAll
    static void setUpAll() throws SQLException {
        testConnection = DBUtil.getConnection();
        initializeTestDatabase();
    }

    private static void initializeTestDatabase() throws SQLException {
        try (Statement stmt = testConnection.createStatement()) {
            // Clear tables in proper order due to foreign key constraints
            stmt.execute("TRUNCATE TABLE Bookings, ParkingSpaces, ParkingLots, users RESTART IDENTITY CASCADE");

            // Insert test data
            stmt.execute("INSERT INTO ParkingLots (name, location) VALUES ('Test Lot', 'Test Location')");
            stmt.execute("INSERT INTO ParkingSpaces (parking_lot_id, space_number) VALUES (1, 101)");
            stmt.execute("INSERT INTO users (email, user_type, status, password) VALUES " +
                    "('test@example.com', 'customer', 'active', 'password123')");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up after each test
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("TRUNCATE TABLE Bookings RESTART IDENTITY CASCADE");
        }
    }

    @AfterAll
    static void tearDownAll() throws SQLException {
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
        }

    }

    @BeforeEach
    void setUp() throws SQLException {
        // Mock setup for unit tests
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
        }

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    }

    @Test
    void testAddBooking() throws SQLException {
        // Arrange
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1);

        ParkingSpace mockSpace = mock(ParkingSpace.class);
        when(mockSpace.getSpaceId()).thenReturn(1); // Matches our test data

        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking(
                0, // ID will be generated
                mockUser,
                mockSpace,
                now,
                now.plusHours(2),
                10,
                20,
                "credit",
                "TEST123",
                BookingStatus.BOOKED
        );

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1); // Return generated ID

        // Act
        BookingDAO.addBooking(booking);

        // Assert
        verify(mockPreparedStatement).setInt(1, 1); // client_id
        verify(mockPreparedStatement).setInt(2, 1); // parking_space_id
        verify(mockPreparedStatement).setString(3, "TEST123");
        verify(mockPreparedStatement).setTimestamp(4, Timestamp.valueOf(now));
        verify(mockPreparedStatement).setTimestamp(5, Timestamp.valueOf(now.plusHours(2)));
        verify(mockPreparedStatement).setInt(6, 10);
        verify(mockPreparedStatement).setInt(7, 20);
        verify(mockPreparedStatement).setString(8, "booked");
        verify(mockPreparedStatement).setString(9, "credit");
    }

    @Test
    void integrationTestGetClientBookings() throws SQLException {
        // Arrange - use real database
        LocalDateTime now = LocalDateTime.now();

        try (PreparedStatement pstmt = testConnection.prepareStatement(
                "INSERT INTO Bookings (client_id, parking_space_id, license_plate, " +
                        "start_time, end_time, deposit, total_cost, status, payment_method) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            pstmt.setInt(1, 1);
            pstmt.setInt(2, 1);
            pstmt.setString(3, "INTEG123");
            pstmt.setTimestamp(4, Timestamp.valueOf(now));
            pstmt.setTimestamp(5, Timestamp.valueOf(now.plusHours(2)));
            pstmt.setInt(6, 15);
            pstmt.setInt(7, 30);
            pstmt.setString(8, "booked");
            pstmt.setString(9, "debit");
            pstmt.executeUpdate();
        }

        // Act
        ArrayList<Booking> bookings = BookingDAO.getClientBookings(1);

        // Assert
        assertEquals(1, bookings.size(), "Should find one booking");
        Booking booking = bookings.get(0);
        assertEquals("INTEG123", booking.getLicensePlate());
        assertEquals(15, booking.getDeposit());
        assertEquals(30, booking.getTotalCost());
        assertEquals(BookingStatus.BOOKED, booking.getStatus());
    }
}