package EECS3311.DAO;

import EECS3311.Models.User;
import EECS3311.Models.UserType;
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
    private User testUser;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Mock DBUtil.getConnection()
        try (MockedStatic<DBUtil> mockedDBUtil = Mockito.mockStatic(DBUtil.class)) {
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
        }

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Create a test user (STUDENT has PENDING status by default)
        testUser = new User("student@example.com", "password123", UserType.STUDENT, true);
        testUser.setId(1);
    }

    @Test
    void testGetUserByUserObjectWithPendingStatus() throws SQLException {
        // Arrange
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("email")).thenReturn("student@example.com");
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("user_type")).thenReturn("STUDENT");
        when(mockResultSet.getString("status")).thenReturn("PENDING");
        when(mockResultSet.getInt("id")).thenReturn(1);

        // Act
        User result = UserDAO.getUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals("student@example.com", result.getEmail());
        assertEquals(UserType.STUDENT, result.getUserType());
        assertTrue(result.isPendingValidation());
        assertEquals(5.0, result.getUserType().getHourlyRate());
        assertEquals("Student", result.getUserType().toString());
    }

    @Test
    void testGetUserByEmailWithApprovedStatus() throws SQLException {
        // Test with a VISITOR which should be APPROVED by default
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
        assertFalse(result.isPendingValidation()); // VISITOR should be auto-approved
        assertEquals(15.0, result.getUserType().getHourlyRate());
    }

    @Test
    void testAddUserWithDifferentUserTypes() throws SQLException {
        // Test adding different user types and their default statuses
        User student = new User("student2@example.com", "pass", UserType.STUDENT, true);
        User visitor = new User("visitor2@example.com", "pass", UserType.VISITOR, false);
        User manager = new User("manager@example.com", "pass", UserType.MANAGER, false);

        // Mock that no users exist yet
        when(UserDAO.getUser(any(User.class))).thenReturn(null);

        // Add student (should be PENDING)
        UserDAO.addUser(student);
        verify(mockPreparedStatement).setString(3, "STUDENT");
        verify(mockPreparedStatement).setString(4, "PENDING");

        // Add visitor (should be APPROVED)
        UserDAO.addUser(visitor);
        verify(mockPreparedStatement).setString(3, "VISITOR");
        verify(mockPreparedStatement).setString(4, "APPROVED");

        // Add manager (should be APPROVED)
        UserDAO.addUser(manager);
        verify(mockPreparedStatement).setString(3, "MANAGER");
        verify(mockPreparedStatement).setString(4, "APPROVED");
    }

    @Test
    void testApprovePendingUser() throws SQLException {
        // Arrange - student is PENDING by default
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("email")).thenReturn("student@example.com");
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getString("user_type")).thenReturn("STUDENT");
        when(mockResultSet.getString("status")).thenReturn("PENDING");
        when(mockResultSet.getInt("id")).thenReturn(1);

        User pendingUser = UserDAO.getUser("student@example.com");
        assertTrue(pendingUser.isPendingValidation());

        // Act
        UserDAO.approveUser(pendingUser);

        // Assert
        verify(mockPreparedStatement).setString(1, "APPROVED");
        verify(mockPreparedStatement).setString(2, "student@example.com");
    }

    @Test
    void testGetPendingUsersWithMixedStatus() throws SQLException {
        // Arrange
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getString("email")).thenReturn(
                "student@example.com",
                "faculty@example.com",
                "visitor@example.com" // This one shouldn't appear in results
        );
        when(mockResultSet.getString("password")).thenReturn("pass1", "pass2", "pass3");
        when(mockResultSet.getString("user_type")).thenReturn("STUDENT", "FACULTY", "VISITOR");
        when(mockResultSet.getString("status")).thenReturn("PENDING", "PENDING", "APPROVED");

        // Act
        ArrayList<User> result = UserDAO.getPendingUsers();

        // Assert - should only get PENDING users (student and faculty)
        assertEquals(2, result.size());
        assertEquals("student@example.com", result.get(0).getEmail());
        assertEquals("faculty@example.com", result.get(1).getEmail());
        assertTrue(result.get(0).isPendingValidation());
        assertTrue(result.get(1).isPendingValidation());
        assertEquals(5.0, result.get(0).getUserType().getHourlyRate()); // STUDENT rate
        assertEquals(8.0, result.get(1).getUserType().getHourlyRate()); // FACULTY rate
    }

    @Test
    void testUserTypeStatusLogic() {
        // Directly test the UserType enum's status logic
        assertEquals("PENDING", UserType.STUDENT.getStatus());
        assertEquals("PENDING", UserType.FACULTY.getStatus());
        assertEquals("PENDING", UserType.STAFF.getStatus());
        assertEquals("APPROVED", UserType.VISITOR.getStatus());
        assertEquals("APPROVED", UserType.MANAGER.getStatus());
        assertEquals("APPROVED", UserType.SUPERMANAGER.getStatus());
    }
}