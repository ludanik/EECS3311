package EECS3311.DAO;

import EECS3311.Models.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserDAO {

    public static User getUser(User u) {
        User user = null;
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM users WHERE email=?;");
            stmt.setString(1, u.getEmail());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserType t = UserType.valueOf(rs.getString("user_type").toUpperCase());
                boolean pendingValidation = rs.getString("status").equals("PENDING");
                user = new User(rs.getString("email"), rs.getString("password"),  t, pendingValidation);
                user.setId(rs.getInt("id"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static User getUser(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        User user = null;
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM users WHERE email=?;");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserType t = UserType.valueOf(rs.getString("user_type").toUpperCase());
                boolean pendingValidation = rs.getString("status").equals("PENDING");
                user = new User(rs.getString("email"), rs.getString("password"),  t, pendingValidation);
                user.setId(rs.getInt("id"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void addUser(User u) {

        if (u == null) {
            throw new NullPointerException("User cannot be null");
        }

        // Validate individual fields
        if (u.getEmail() == null) {
            throw new NullPointerException("Email cannot be null");
        }

        if (u.getUserType() == null) {
            throw new NullPointerException("UserType cannot be null");
        }

        if (u.getPassword() == null) {
            throw new NullPointerException("Password cannot be null");
        }

        User existingUser = UserDAO.getUser(u);
        if (existingUser != null) {
            if (!existingUser.isPendingValidation()) {
                throw new IllegalStateException("User with this email is already approved");
            }
            throw new RuntimeException("User with this email already exists");
        }

        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement insertStmt = c.prepareStatement("INSERT INTO users(id, email, user_type, status, password) VALUES (?, ?, ?, ?, ?)");

            insertStmt.setInt(1, (int)(Math.random() * 99999999));
            insertStmt.setString(2, u.getEmail());
            insertStmt.setString(3, u.getUserType().toString());
            insertStmt.setString(4, u.getUserType().getStatus());
            insertStmt.setString(5, u.getPassword());

            int insertedRows = insertStmt.executeUpdate();
            System.out.printf("inserted %s users(s)%n", insertedRows);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteUser(User u) {
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement deleteStmt = c.prepareStatement("DELETE FROM users WHERE email = ?");
            deleteStmt.setString(1, u.getEmail());
            int deletedRows = deleteStmt.executeUpdate();
            System.out.printf("deleted %s email(s)%n", deletedRows);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void approveUser(User u) {
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement updateStmt = c.prepareStatement("UPDATE users SET status = ? WHERE email = ?;");
            updateStmt.setString(1, "APPROVED");
            updateStmt.setString(2, u.getEmail());
            int rows = updateStmt.executeUpdate();
            System.out.println(rows);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<User> getPendingUsers() {
        ArrayList<User> list = new ArrayList<>();
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM users WHERE status='PENDING';");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserType t = UserType.valueOf(rs.getString("user_type").toUpperCase());
                User u = new User(rs.getString("email"), rs.getString("password"),  t, true);
                list.add(u);
                System.out.println(u);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
