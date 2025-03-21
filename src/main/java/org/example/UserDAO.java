package org.example;

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
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static User getUser(String email) {
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
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void addUser(User u) {
        if (UserDAO.getUser(u) != null) return;
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
