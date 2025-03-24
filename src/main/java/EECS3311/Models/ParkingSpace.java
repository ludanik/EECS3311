package EECS3311.Models;

public class ParkingSpace {
    private int lotId;
    private int spaceId;
    private ParkingStatus status;
    public int getLotId() {
        return lotId;
    }

    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public ParkingStatus getStatus() {
        return status;
    }

    public void setStatus(ParkingStatus status) {
        this.status = status;
    }
}
