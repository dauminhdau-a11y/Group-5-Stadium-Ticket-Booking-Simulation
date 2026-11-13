package model;

import model.enums.SeatStatus;
import model.enums.Stand;
import java.time.Duration;
import java.time.LocalDateTime;

public class Seat extends BaseEntity {
    private String seatId;
    private String matchId;
    private String sectionId;
    private String rowLabel;
    private int seatNumber;
    private SeatStatus status;
    private Stand stand;
    private double price;
    private long version;
    private boolean sectionBasedFormat;
    private LocalDateTime lockedAt;
    private String lockedBy;

    public Seat() {
        super();
        this.status = SeatStatus.AVAILABLE;
        this.price = 500000.0;
        this.version = 1;
    }

    public Seat(String seatId, String matchId, SeatStatus status, Stand stand,
            double price, long version) {
        super(seatId);
        this.seatId = seatId;
        this.matchId = matchId;
        this.status = status;
        this.stand = stand;
        this.price = price;
        this.version = version;
    }

    public Seat(String seatId, String matchId, String sectionId, String rowLabel,
            int seatNumber, SeatStatus status, long version) {
        super(seatId);
        this.seatId = seatId;
        this.matchId = matchId;
        this.sectionId = sectionId;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.status = status;
        this.version = version;
        this.sectionBasedFormat = true;
    }

    public String getSeatId() {
        return seatId != null ? seatId : getId();
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
        setId(seatId);
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
        this.version++;
    }

    public Stand getStand() {
        return stand;
    }

    public void setStand(Stand stand) {
        this.stand = stand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public boolean isHoldExpired(int timeoutMinutes) {
        if (this.status != SeatStatus.LOCKED || this.lockedAt == null) {
            return false;
        }
        return Duration.between(this.lockedAt, LocalDateTime.now()).toMinutes() >= timeoutMinutes;
    }

    public long getRemainingHoldSeconds(int timeoutMinutes) {
        if (this.status != SeatStatus.LOCKED || this.lockedAt == null) {
            return 0;
        }
        long elapsed = Duration.between(this.lockedAt, LocalDateTime.now()).getSeconds();
        return Math.max(0, (timeoutMinutes * 60L) - elapsed);
    }

    public void lockForHold(String fanId) {
        this.status = SeatStatus.LOCKED;
        this.lockedAt = LocalDateTime.now();
        this.lockedBy = fanId;
        this.version++;
    }

    public void unlock() {
        this.status = SeatStatus.AVAILABLE;
        this.lockedAt = null;
        this.lockedBy = null;
        this.version++;
    }

    public void confirmBooked() {
        this.status = SeatStatus.BOOKED;
        this.lockedAt = null;
        this.lockedBy = null;
        this.version++;
    }

    public void setSectionDetails(Stand stand, double price) {
        this.stand = stand;
        this.price = price;
    }

    @Override
    public String toCsvLine() {
        if (sectionBasedFormat) {
            return String.join(",", getSeatId(), matchId != null ? matchId : "",
                    sectionId != null ? sectionId : "",
                    rowLabel != null ? rowLabel : "",
                    Integer.toString(seatNumber), status != null ? status.name() : "AVAILABLE",
                    Long.toString(version));
        }
        return String.join(",", getSeatId(), matchId != null ? matchId : "",
                status != null ? status.name() : "AVAILABLE",
                stand != null ? stand.name() : "",
                Double.toString(price), Long.toString(version),
                lockedAt != null ? lockedAt.toString() : "",
                lockedBy != null ? lockedBy : "");
    }

    public Seat fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid seat CSV data.");
        }
        this.seatId = parts[0].trim();
        try {
            if (parts.length == 7 && isSeatStatus(parts[5].trim())) {
                this.matchId = parts[1].trim();
                this.sectionId = parts[2].trim();
                this.rowLabel = parts[3].trim();
                this.seatNumber = Integer.parseInt(parts[4].trim());
                this.status = SeatStatus.valueOf(parts[5].trim());
                this.version = Long.parseLong(parts[6].trim());
                this.sectionBasedFormat = true;
                setId(seatId);
                return this;
            }
            this.matchId = parts[1].trim();
            this.status = SeatStatus.valueOf(parts[2].trim());
            this.stand = parseStand(parts[3].trim());
            this.price = Double.parseDouble(parts[4].trim());
            this.version = Long.parseLong(parts[5].trim());
            this.sectionBasedFormat = false;

            if (parts.length >= 7 && !parts[6].trim().isEmpty()) {
                try {
                    this.lockedAt = LocalDateTime.parse(parts[6].trim());
                } catch (Exception ignored) {
                }
            }
            if (parts.length >= 8 && !parts[7].trim().isEmpty()) {
                this.lockedBy = parts[7].trim();
            }
        } catch (IllegalArgumentException legacyFormatFailure) {
            if (parts.length == 6 && isSeatStatus(parts[4].trim())) {
                this.sectionId = parts[1].trim();
                this.rowLabel = parts[2].trim();
                this.seatNumber = Integer.parseInt(parts[3].trim());
                this.status = SeatStatus.valueOf(parts[4].trim());
                this.version = Long.parseLong(parts[5].trim());
                this.matchId = "";
                this.sectionBasedFormat = true;
            } else {
                throw legacyFormatFailure;
            }
        }
        setId(seatId);
        return this;
    }

    private Stand parseStand(String value) {
        try {
            return Stand.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException legacyType) {
            switch (value.toUpperCase()) {
                case "VIP":
                case "VVIP":
                    return Stand.A;
                case "STANDARD":
                    return Stand.B;
                case "ECONOMY":
                    return Stand.C;
                case "STANDING":
                    return Stand.D;
                default:
                    throw legacyType;
            }
        }
    }

    private boolean isSeatStatus(String value) {
        try {
            SeatStatus.valueOf(value);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }
}