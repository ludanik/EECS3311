package EECS3311.DAO;

import EECS3311.Models.ParkingLot;
import EECS3311.Models.ParkingSpace;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ParkingSpaceDAO {
    public static ParkingSpace getSpace() {
        ParkingSpace p = null;
        return p;
    }

    public static void reserveSpace(ParkingSpace p) {}
    public static void occupySpace(ParkingSpace p) {}
    public static void vacateSpace(ParkingSpace p) {}
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
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                c.rollback();
            }
            catch (Exception gg) {
                gg.printStackTrace();
            }
        }
    }
    public static void disableSpace(ParkingSpace p) {}
    public static void enableSpace(ParkingSpace p) {

    }
}
