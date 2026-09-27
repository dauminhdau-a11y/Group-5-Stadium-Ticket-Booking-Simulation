package model;

public class Seat {
    private String seatId;
    private SeatStatus status;
    private String matchId;

    public Seat(String seatId, String matchId) {
        this.seatId = seatId;
        this.matchId = matchId;
    }

    public String getSeatId() {
        return seatId;
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
}
