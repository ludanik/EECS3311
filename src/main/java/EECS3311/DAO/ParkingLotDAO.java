package EECS3311.DAO;

import EECS3311.Models.*;
import java.sql.*;
import java.util.ArrayList;

public class ParkingLotDAO {
    public static ArrayList<ParkingLot> getEnabledLots() {
        ArrayList<ParkingLot> list = new ArrayList<>();
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM parkinglots WHERE status='enabled';");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ParkingLot p = new ParkingLot(rs.getInt("parking_lot_id"), rs.getString("name"), true);
                list.add(p);
                System.out.println(p);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void addLot(ParkingLot p) {
        if (ParkingLotDAO.getLot(p.getId()) != null) return;
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement insertStmt = c.prepareStatement("INSERT INTO parkinglots(name, location, status) VALUES (?, ?, ?)");

            String enabled = (p.isEnabled()) ? "enabled" : "disabled";

            insertStmt.setString(1, p.getName());
            insertStmt.setString(2, p.getName());
            insertStmt.setString(3, enabled);

            int insertedRows = insertStmt.executeUpdate();
            System.out.printf("inserted %s lots(s)%n", insertedRows);

            ParkingSpaceDAO.generateSpaces(p);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ParkingLot getLot(int id) {
        ParkingLot p = null;
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM parkinglots WHERE parking_lot_id=?;");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                boolean enabled = rs.getString("status").equals("enabled");
                p = new ParkingLot(rs.getInt("parking_lot_id"), rs.getString("name"),  enabled);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    public static ArrayList<ParkingLot> getAllLots() {
        ArrayList<ParkingLot> list = new ArrayList<>();
        ParkingLot p;
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM parkinglots;");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                boolean enabled = rs.getString("status").equals("enabled");
                p = new ParkingLot(rs.getInt("parking_lot_id"), rs.getString("name"),  enabled);
                list.add(p);
                System.out.println(p);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void enableLot(int id) {
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement updateStmt = c.prepareStatement("UPDATE parkinglots SET status = ? WHERE parking_lot_id = ?;");
            updateStmt.setString(1, "enabled");
            updateStmt.setInt(2, id);
            int rows = updateStmt.executeUpdate();
            System.out.println(rows);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disableLot(int id) {
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement updateStmt = c.prepareStatement("UPDATE parkinglots SET status = ? WHERE parking_lot_id = ?;");
            updateStmt.setString(1, "disabled");
            updateStmt.setInt(2, id);
            int rows = updateStmt.executeUpdate();
            System.out.println(rows);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getNextId() {
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("Select nextval(pg_get_serial_sequence('parkinglots', 'parking_lot_id')) as new_id;");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt("new_id") + 1;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }
}
