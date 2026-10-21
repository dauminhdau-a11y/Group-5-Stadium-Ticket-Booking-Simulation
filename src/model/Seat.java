package model;

public class Seat {
    private String seatId;
    private SeatStatus status;
    private String matchId;
    private long version;

    public Seat() {
        this.status = SeatStatus.AVAILABLE;
        this.version = 0L;
    }

    public Seat(String seatId, String matchId) {
        this.seatId = seatId;
        this.matchId = matchId;
        this.status = SeatStatus.AVAILABLE;
        this.version = 0L;
    }

    public Seat(String seatId, String matchId, SeatStatus status, long version) {
        this.seatId = seatId;
        this.matchId = matchId;
        this.status = status;
        this.version = version;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
