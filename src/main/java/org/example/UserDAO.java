package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserDAO {
    public static ArrayList<User> getPendingUsers() {
        ArrayList<User> list = new ArrayList<>();
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM users WHERE status='PENDING';");

            // Execute the query, and store the results in the ResultSet instance
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                boolean pendingStatus = ( rs.getString("status").equals("PENDING") ) ? true : false;
                UserType t = UserType.valueOf(rs.getString("user_type"));
                User u = new User(rs.getString("email"), rs.getString("username"), "", t, pendingStatus);
                list.add(u);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
