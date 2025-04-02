package EECS3311.DAO;

import EECS3311.Models.User;
import EECS3311.Models.UserType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserDAOTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DBUtil> dbUtilMock;
    private User testUser;

    @BeforeEach
    void setUp() throws SQLException {
        // Create mock objects
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Mock static DBUtil.getConnection() method
        dbUtilMock = mockStatic(DBUtil.class);
        dbUtilMock.when(DBUtil::getConnection).thenReturn(mockConnection);

        // Mock PreparedStatement behavior
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Setup test user
        testUser = new User("student@example.com", "password123", UserType.STUDENT, true);
    }

    @AfterEach
    void tearDown() {
        dbUtilMock.close();
    }

    @Test
    void testGetUserByUserObjectWithPendingStatus() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("email")).thenReturn("student@example.com");
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("user_type")).thenReturn("STUDENT");
        when(mockResultSet.getString("status")).thenReturn("PENDING");
        when(mockResultSet.getInt("id")).thenReturn(1);

        User result = UserDAO.getUser(testUser);

        assertNotNull(result);
        assertEquals("student@example.com", result.getEmail());
        assertEquals(UserType.STUDENT, result.getUserType());
        assertTrue(result.isPendingValidation());
        assertEquals(5.0, result.getUserType().getHourlyRate());
        assertEquals("Student", result.getUserType().toString());
    }

    @Test
    void testGetUserWithInvalidEmail() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        User result = UserDAO.getUser("nonexistent@example.com");

        assertNull(result);
    }

    @Test
    void testAddUserWithNullValues() {
        User nullUser = new User(null, null, null, false);

        assertThrows(NullPointerException.class, () -> {
            UserDAO.addUser(nullUser);
        });
    }

    @Test
    void testGetPendingUsersWithEmptyResult() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        ArrayList<User> result = UserDAO.getPendingUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void testUserTypeHourlyRates() {
        assertEquals(5.0, UserType.STUDENT.getHourlyRate());
        assertEquals(8.0, UserType.FACULTY.getHourlyRate());
        assertEquals(10.0, UserType.STAFF.getHourlyRate());
        assertEquals(15.0, UserType.VISITOR.getHourlyRate());
        assertEquals(0.0, UserType.MANAGER.getHourlyRate());
        assertEquals(0.0, UserType.SUPERMANAGER.getHourlyRate());
    }


    @Test
    void testAddDuplicateUser() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Duplicate entry"));

        assertThrows(RuntimeException.class, () -> {
            UserDAO.addUser(testUser);
        });
    }

    @Test
    void testGetUserWithMalformedEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserDAO.getUser("not-an-email");
        });
    }

    @Test
    void testUserTypeToString() {
        assertEquals("Student", UserType.STUDENT.toString());
        assertEquals("Faculty", UserType.FACULTY.toString());
        assertEquals("Staff", UserType.STAFF.toString());
        assertEquals("Visitor", UserType.VISITOR.toString());
        assertEquals("Manager", UserType.MANAGER.toString());
        assertEquals("SuperManager", UserType.SUPERMANAGER.toString());
    }

    @Test
    void testGetUserByEmailWithApprovedStatus() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("email")).thenReturn("visitor@example.com");
        when(mockResultSet.getString("password")).thenReturn("visitorpass");
        when(mockResultSet.getString("user_type")).thenReturn("VISITOR");
        when(mockResultSet.getString("status")).thenReturn("APPROVED");
        when(mockResultSet.getInt("id")).thenReturn(2);

        User result = UserDAO.getUser("visitor@example.com");

        assertNotNull(result);
        assertEquals("visitor@example.com", result.getEmail());
        assertEquals(UserType.VISITOR, result.getUserType());
        assertFalse(result.isPendingValidation());
        assertEquals(15.0, result.getUserType().getHourlyRate());
    }

    @Test
    void testAddUserWithDifferentUserTypes() throws SQLException {
        User student = new User("student2@example.com", "pass", UserType.STUDENT, true);
        User visitor = new User("visitor2@example.com", "pass", UserType.VISITOR, false);
        User manager = new User("manager@example.com", "pass", UserType.MANAGER, false);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act: Add users
        UserDAO.addUser(student);
        UserDAO.addUser(visitor);
        UserDAO.addUser(manager);

        // Verify the parameters for the student user
        verify(mockPreparedStatement).setInt(eq(1), anyInt()); // Random ID
        verify(mockPreparedStatement).setString(2, "student2@example.com");
        verify(mockPreparedStatement).setString(3, "STUDENT");
        verify(mockPreparedStatement).setString(4, "PENDING");
        verify(mockPreparedStatement).setString(5, "pass");

        // Verify the parameters for the visitor user
        verify(mockPreparedStatement).setInt(eq(1), anyInt()); // Random ID
        verify(mockPreparedStatement).setString(2, "visitor2@example.com");
        verify(mockPreparedStatement).setString(3, "VISITOR");
        verify(mockPreparedStatement).setString(4, "APPROVED");
        verify(mockPreparedStatement).setString(5, "pass");

        // Verify the parameters for the manager user
        verify(mockPreparedStatement).setInt(eq(1), anyInt()); // Random ID
        verify(mockPreparedStatement).setString(2, "manager@example.com");
        verify(mockPreparedStatement).setString(3, "MANAGER");
        verify(mockPreparedStatement).setString(4, "APPROVED");
        verify(mockPreparedStatement).setString(5, "pass");

        // Verify the executeUpdate() is called 3 times (for 3 users)
        verify(mockPreparedStatement, times(3)).executeUpdate();
    }


    @Test
    void testApprovePendingUser() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("email")).thenReturn("student@example.com");
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("user_type")).thenReturn("STUDENT");
        when(mockResultSet.getString("status")).thenReturn("PENDING");
        when(mockResultSet.getInt("id")).thenReturn(1);

        User pendingUser = UserDAO.getUser("student@example.com");

        assertNotNull(pendingUser);
        assertTrue(pendingUser.isPendingValidation());

        UserDAO.approveUser(pendingUser);

        verify(mockPreparedStatement).setString(1, "APPROVED");
        verify(mockPreparedStatement).setString(2, "student@example.com");
    }

    @Test
    void testGetPendingUsersWithMixedStatus() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("email")).thenReturn(
                "student@example.com",
                "faculty@example.com",
                "visitor@example.com",
                "visitor2@example.com"
        );
        when(mockResultSet.getString("password")).thenReturn("pass1", "pass2", "pass3", "pass4");
        when(mockResultSet.getString("user_type")).thenReturn("STUDENT", "FACULTY", "VISITOR", "VISITOR");
        when(mockResultSet.getString("status")).thenReturn("PENDING", "PENDING", "APPROVED", "APPROVED");

        ArrayList<User> result = UserDAO.getPendingUsers();

        assertEquals(3, result.size());
        assertEquals("student@example.com", result.get(0).getEmail());
        assertEquals("faculty@example.com", result.get(1).getEmail());
        assertTrue(result.get(0).isPendingValidation());
        assertTrue(result.get(1).isPendingValidation());
        assertEquals(5.0, result.get(0).getUserType().getHourlyRate());
        assertEquals(8.0, result.get(1).getUserType().getHourlyRate());
    }

    @Test
    void testUserTypeStatusLogic() {
        assertEquals("PENDING", UserType.STUDENT.getStatus());
        assertEquals("PENDING", UserType.FACULTY.getStatus());
        assertEquals("PENDING", UserType.STAFF.getStatus());
        assertEquals("APPROVED", UserType.VISITOR.getStatus());
        assertEquals("APPROVED", UserType.MANAGER.getStatus());
        assertEquals("APPROVED", UserType.SUPERMANAGER.getStatus());
    }
}