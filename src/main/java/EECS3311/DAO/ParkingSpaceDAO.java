package EECS3311.DAO;

import EECS3311.Models.BookingStatus;
import EECS3311.Models.ParkingLot;
import EECS3311.Models.ParkingSpace;
import EECS3311.Models.ParkingStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;

public class ParkingSpaceDAO {

    public static ParkingSpace getParkingSpace(int parkingSpaceId) {
        ParkingSpace p = null;
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM parkingspaces WHERE parking_space_id=?;");

            stmt.setInt(1,parkingSpaceId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                p = new ParkingSpace(rs.getInt("parking_lot_id"), rs.getInt("space_number"), ParkingStatus.valueOf(rs.getString("status").toUpperCase()), rs.getInt("parking_space_id"));
                System.out.println(p);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    public static int getParkingSpaceId(int parkingLotId, int spaceNumber) {
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT parking_space_id FROM parkingspaces WHERE parking_lot_id = ? AND space_number = ?;");
            stmt.setInt(1, parkingLotId);
            stmt.setInt(2, spaceNumber);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt("parking_space_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static ArrayList<ParkingSpace> getAvailableSpaces(int parkingLotId) {
        ArrayList<ParkingSpace> list = new ArrayList<>();
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM parkingspaces WHERE status='available' AND parking_lot_id=?;");

            stmt.setInt(1,parkingLotId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ParkingSpace p = new ParkingSpace(parkingLotId, rs.getInt("space_number"), ParkingStatus.AVAILABLE, rs.getInt("parking_space_id"));
                list.add(p);
                System.out.println(p);
            }

            list.sort(Comparator.comparingInt(ParkingSpace::getSpaceNumber));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void generateSpaces(ParkingLot p) {
        // TODO: check if spaces are already generated
        Connection c = null;
        try {
            c = DBUtil.getConnection();
            c.setAutoCommit(false);
            for (int i = 1; i < 101; i++) {
                PreparedStatement insertStmt = c.prepareStatement("INSERT INTO parkingspaces(parking_lot_id, space_number, location_description, status) VALUES (?, ?, ?,?)");

                insertStmt.setInt(1, p.getId());
                insertStmt.setInt(2, i);
                insertStmt.setString(3, p.getName());
                insertStmt.setString(4, "available");

                insertStmt.executeUpdate();
            }
            c.commit();

            System.out.printf("inserted 100 spaces");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                c.rollback();
            } catch (Exception gg) {
                gg.printStackTrace();
            }
        }
    }

    public static void disableSpace(int parkingLotId, int spaceNumber) {
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement updateStmt = c.prepareStatement("UPDATE parkingspaces SET status = ? WHERE parking_lot_id = ? AND space_number = ?;");
            updateStmt.setString(1, "maintenance");
            updateStmt.setInt(2, parkingLotId);
            updateStmt.setInt(3, spaceNumber);
            int rows = updateStmt.executeUpdate();
            System.out.println(rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enableSpace(int parkingLotId, int spaceNumber) {
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement updateStmt = c.prepareStatement("UPDATE parkingspaces SET status = ? WHERE parking_lot_id = ? AND space_number = ?;");
            updateStmt.setString(1, "available");
            updateStmt.setInt(2, parkingLotId);
            updateStmt.setInt(3, spaceNumber);
            int rows = updateStmt.executeUpdate();
            System.out.println(rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bookSpace(int parkingLotId, int spaceNumber) {
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement updateStmt = c.prepareStatement("UPDATE parkingspaces SET status = ? WHERE parking_lot_id = ? AND space_number = ?;");
            updateStmt.setString(1, "booked");
            updateStmt.setInt(2, parkingLotId);
            updateStmt.setInt(3, spaceNumber);
            int rows = updateStmt.executeUpdate();
            System.out.println(rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void occupySpace(int parkingLotId, int spaceNumber) {
        try {
            Connection c = DBUtil.getConnection();
            PreparedStatement updateStmt = c.prepareStatement("UPDATE parkingspaces SET status = ? WHERE parking_lot_id = ? AND space_number = ?;");
            updateStmt.setString(1, "occupied");
            updateStmt.setInt(2, parkingLotId);
            updateStmt.setInt(3, spaceNumber);
            int rows = updateStmt.executeUpdate();
            System.out.println(rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
