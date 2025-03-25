package EECS3311.Models;

public class ParkingSpace {
    private int lotId;
    private int spaceNumber;
    private int spaceId;
    private ParkingStatus status;

    public ParkingSpace(int lotId, int spaceNumber, ParkingStatus status) {
        this.lotId = lotId;
        this.spaceNumber = spaceNumber;
        this.status = status;
    }

    public ParkingSpace(int lotId, int spaceNumber, ParkingStatus status, int spaceId) {
        this.lotId = lotId;
        this.spaceNumber = spaceNumber;
        this.status = status;
        this.spaceId = spaceId;
    }

    public int getSpaceId() { return this.spaceId; }

    public int getLotId() {
        return lotId;
    }

    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public int getSpaceNumber() {
        return spaceNumber;
    }

    public void setSpaceNumber(int spaceNumber) {
        this.spaceNumber = spaceNumber;
    }

    public ParkingStatus getStatus() {
        return status;
    }

    public void setStatus(ParkingStatus status) {
        this.status = status;
    }
}
