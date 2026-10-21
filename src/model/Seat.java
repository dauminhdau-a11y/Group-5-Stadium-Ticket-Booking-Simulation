package model;

public class Seat extends BaseEntity {
    private String seatId;
    private SeatStatus status;
    private String matchId;
    private int version;

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

    @Override
    public String toCsvLine() {
        return seatId + "," + matchId + "," + status.name() + "," + version;
    }

    @Override
    public void fromCsvLine(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return;
        }
        String[] parts = csvLine.split(",");

        if (parts.length >= 3) {
            this.seatId = parts[0].trim();
            this.matchId = parts[1].trim();
            this.status = SeatStatus.valueOf(parts[2].trim());

            if (parts.length >= 4) {
                this.version = Integer.parseInt(parts[3].trim());
            } else {
                this.version = 1;
            }
        }
    }
}
