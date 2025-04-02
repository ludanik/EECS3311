package EECS3311.DAO;

import EECS3311.Models.*;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingDAOTest {

    private static Connection mockConnection;
    private static PreparedStatement mockPreparedStatement;
    private static ResultSet mockResultSet;
    private static ParkingSpaceDAO mockParkingSpaceDAO;

    private User testUser;
    private ParkingLot testLot;
    private ParkingSpace testSpace;
    private Booking testBooking;

    @BeforeAll
    static void setUpClass() throws SQLException {
        // Set up shared mock objects before any tests run
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Mock static DBUtil.getConnection() method
        MockedStatic<DBUtil> dbUtilMock = mockStatic(DBUtil.class);
        dbUtilMock.when(DBUtil::getConnection).thenReturn(mockConnection);

        // Mock PreparedStatement behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Initialize instance variables for each test
        testUser = new User("test@example.com", "password", UserType.STUDENT, true);
        testLot = new ParkingLot(1, "P1", true);
        testSpace = new ParkingSpace(1, 1, ParkingStatus.OCCUPIED, 1);
        testSpace.setStatus(ParkingStatus.AVAILABLE);

        LocalDateTime now = LocalDateTime.now();
        testBooking = new Booking(1, testUser, testSpace, now, now.plusHours(2),
                10, 20, "credit", "ABC123", BookingStatus.BOOKED);

        when(mockParkingSpaceDAO.getParkingSpace(1)).thenReturn(testSpace);
    }

    @Test
    void testAddBooking_UpdatesParkingStatus() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        BookingDAO.addBooking(testBooking);

        // Verify parking space status was updated to BOOKED
        assertEquals(ParkingStatus.BOOKED, testSpace.getStatus());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddBooking_FailsWhenSpaceNotAvailable() throws SQLException {
        testSpace.setStatus(ParkingStatus.OCCUPIED);

        BookingDAO.addBooking(testBooking);

        // Verify no database insert occurred
        verify(mockPreparedStatement, never()).executeUpdate();
    }

    @Test
    void testCancelBooking_ReleasesParkingSpace() throws SQLException {
        testSpace.setStatus(ParkingStatus.BOOKED); // Simulate booked space
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        BookingDAO.cancelBooking(1);

        // Verify parking space status was updated to AVAILABLE
        assertEquals(ParkingStatus.AVAILABLE, testSpace.getStatus());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testGetClientBookings_WithParkingStatus() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Mock result set
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("booking_id")).thenReturn(1);
        when(mockResultSet.getInt("client_id")).thenReturn(1);
        when(mockResultSet.getInt("space_id")).thenReturn(1);
        when(mockResultSet.getString("license_plate")).thenReturn("ABC123");
        when(mockResultSet.getTimestamp("start_time")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(mockResultSet.getTimestamp("end_time")).thenReturn(Timestamp.valueOf(LocalDateTime.now().plusHours(2)));
        when(mockResultSet.getInt("deposit")).thenReturn(10);
        when(mockResultSet.getInt("total_cost")).thenReturn(20);
        when(mockResultSet.getString("status")).thenReturn("active");
        when(mockResultSet.getString("payment_method")).thenReturn("credit");

        ArrayList<Booking> bookings = BookingDAO.getClientBookings(1);

        assertEquals(1, bookings.size());
        assertEquals(ParkingStatus.BOOKED, bookings.get(0).getParkingSpace().getStatus());
    }

    @Test
    void testExtendBooking_WhileSpaceIsOccupied() throws SQLException {
        testSpace.setStatus(ParkingStatus.OCCUPIED);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Should still be able to extend booking even if space is occupied
        BookingDAO.extendBooking(testBooking, 1);

        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddBooking_ToMaintenanceSpaceFails() throws SQLException {
        testSpace.setStatus(ParkingStatus.MAINTENANCE);

        BookingDAO.addBooking(testBooking);

        verify(mockPreparedStatement, never()).executeUpdate();
    }

    @Test
    void testCancelBooking_OnMaintenanceSpace() throws SQLException {
        testSpace.setStatus(ParkingStatus.MAINTENANCE);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Should still be able to cancel booking even if space is in maintenance
        BookingDAO.cancelBooking(1);

        verify(mockPreparedStatement).executeUpdate();
        assertEquals(ParkingStatus.MAINTENANCE, testSpace.getStatus()); // Status shouldn't change
    }

    @Test
    void testParkingStatus_AfterBookingCompletion() throws SQLException {
        // Simulate a completed booking (end time in past)
        LocalDateTime pastTime = LocalDateTime.now().minusHours(3);
        Booking completedBooking = new Booking(2, testUser, testSpace,
                pastTime, pastTime.plusHours(1), 10, 20, "credit", "XYZ789", BookingStatus.COMPLETED);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        BookingDAO.addBooking(completedBooking);

        // Space should be available after completed booking
        assertEquals(ParkingStatus.AVAILABLE, testSpace.getStatus());
    }

    @Test
    void testConcurrentStatusUpdates() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Thread 1: Book the space
        new Thread(() -> {
            testSpace.setStatus(ParkingStatus.AVAILABLE);
            BookingDAO.addBooking(testBooking);
        }).start();

        // Thread 2: Try to book same space
        new Thread(() -> {
            testSpace.setStatus(ParkingStatus.AVAILABLE);
            Booking duplicateBooking = new Booking(2, testUser, testSpace,
                    LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                    10, 20, "credit", "DEF456", BookingStatus.BOOKED);
            BookingDAO.addBooking(duplicateBooking);
        }).start();

        // Verify only one booking was successful
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @AfterEach
    void tearDown() {
        reset(mockConnection, mockPreparedStatement, mockResultSet, mockParkingSpaceDAO);
    }

    @AfterAll
    static void tearDownClass() {
        // Clean up shared resources after all tests have run
        mockConnection = null;
        mockPreparedStatement = null;
        mockResultSet = null;
    }
}
