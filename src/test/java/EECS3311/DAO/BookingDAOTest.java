package EECS3311.DAO;

import EECS3311.Models.*;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingDAOTest {

    private static Connection mockConnection;
    private static PreparedStatement mockPreparedStatement;
    private static ResultSet mockResultSet;
    private MockedStatic<DBUtil> dbUtilMock;
    private ParkingSpaceDAO mockParkingSpaceDAO;

    private User testUser;
    private ParkingLot testLot;
    private ParkingSpace testSpace;
    private Booking testBooking;

    @BeforeEach
    void setUp() throws SQLException {
        // Create mock objects
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockParkingSpaceDAO = mock(ParkingSpaceDAO.class);

        // Mock static DBUtil.getConnection() method
        dbUtilMock = mockStatic(DBUtil.class);
        dbUtilMock.when(DBUtil::getConnection).thenReturn(mockConnection);

        // Mock PreparedStatement behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Setup test entities
        testUser = new User("student@example.com", "password123", UserType.STUDENT, true);
        testLot = new ParkingLot(1, "Lot A", true);
        testSpace = new ParkingSpace(1, 101, ParkingStatus.AVAILABLE, 1);
        testBooking = new Booking(1, testUser, testSpace, LocalDateTime.now(), LocalDateTime.now().plusHours(2), 50, 200, "Credit Card", "ABC-1234", BookingStatus.COMPLETED);
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
    void testCancelBooking_NonExistentBooking() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        BookingDAO.cancelBooking(999); // Assume ID 999 does not exist

        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testCancelAlreadyCanceledBooking() throws SQLException {
        testBooking.setStatus(BookingStatus.CANCELLED);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // No rows should be affected

        BookingDAO.cancelBooking(testBooking.getId());

        assertEquals(BookingStatus.CANCELLED, testBooking.getStatus());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testCancelBooking_OnlyAffectsTargetBooking() throws SQLException {
        Booking anotherBooking = new Booking(2, testUser, testSpace,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), 10, 20, "credit", "XYZ789", BookingStatus.BOOKED);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        BookingDAO.cancelBooking(1); // Cancel testBooking

        assertEquals(BookingStatus.BOOKED, anotherBooking.getStatus()); // Ensure another booking remains unaffected
        verify(mockPreparedStatement).executeUpdate();
    }


    @AfterEach
    void tearDown() {
        reset(mockConnection, mockPreparedStatement, mockResultSet, mockParkingSpaceDAO);
        if (dbUtilMock != null) {
            dbUtilMock.close();
        }
    }

    @AfterAll
    static void tearDownClass() {
        mockConnection = null;
        mockPreparedStatement = null;
        mockResultSet = null;
    }

}
