package model;

public class Seat extends BaseEntity {
    private String seatId;
    private String matchId;
    private SeatStatus status = SeatStatus.AVAILABLE;
    private SeatType seatType = SeatType.STANDARD;
    private double price = 500000.0;
    private int version = 1;

    public Seat() {
    }

    public Seat(String seatId, String matchId) {
        this.seatId = seatId;
        this.matchId = matchId;
        this.seatType = inferSeatType(seatId);
        this.price = this.seatType.getBasePrice();
        this.status = SeatStatus.AVAILABLE;
        this.version = 1;
    }

    public Seat(String seatId, String matchId, SeatStatus status, SeatType seatType, double price, int version) {
        this.seatId = seatId;
        this.matchId = matchId;
        this.status = status;
        this.seatType = seatType != null ? seatType : inferSeatType(seatId);
        this.price = price > 0 ? price : this.seatType.getBasePrice();
        this.version = version;
    }

    private SeatType inferSeatType(String id) {
        if (id == null) return SeatType.STANDARD;
        String upper = id.toUpperCase();
        if (upper.startsWith("VVIP")) return SeatType.VVIP;
        if (upper.startsWith("VIP")) return SeatType.VIP;
        if (upper.startsWith("ECO") || upper.startsWith("C") || upper.startsWith("D")) return SeatType.ECONOMY;
        if (upper.startsWith("STD") || upper.startsWith("B")) return SeatType.STANDARD;
        if (upper.equals("S1") || upper.equals("S2")) return SeatType.VIP;
        return SeatType.STANDARD;
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

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
        if (seatType != null) {
            this.price = seatType.getBasePrice();
        }
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toCsvLine() {
        return seatId + "," + matchId + "," + status.name() + "," + seatType.name() + "," + price + "," + version;
    }

    @Override
    public void fromCsvLine(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty() || csvLine.startsWith("seatId")) {
            return;
        }
        String[] parts = csvLine.split(",");
        if (parts.length >= 6 && (parts[4].trim().equalsIgnoreCase("AVAILABLE") 
                || parts[4].trim().equalsIgnoreCase("BOOKED") 
                || parts[4].trim().equalsIgnoreCase("LOCKED"))) {
            this.seatId = parts[0].trim();
            this.matchId = "M001";
            try {
                this.status = SeatStatus.valueOf(parts[4].trim().toUpperCase());
            } catch (Exception e) {
                this.status = SeatStatus.AVAILABLE;
            }
            this.seatType = inferSeatType(this.seatId);
            this.price = this.seatType.getBasePrice();
            try {
                this.version = Integer.parseInt(parts[5].trim());
            } catch (Exception ignored) {
                this.version = 1;
            }
        } else if (parts.length >= 3) {
            this.seatId = parts[0].trim();
            this.matchId = parts[1].trim();
            try {
                this.status = SeatStatus.valueOf(parts[2].trim().toUpperCase());
            } catch (Exception e) {
                this.status = SeatStatus.AVAILABLE;
            }
            this.seatType = inferSeatType(this.seatId);
            this.price = this.seatType.getBasePrice();
            this.version = 1;

            if (parts.length >= 5) {
                try {
                    this.seatType = SeatType.valueOf(parts[3].trim());
                } catch (Exception ignored) {
                }
                try {
                    this.price = Double.parseDouble(parts[4].trim());
                } catch (Exception ignored) {
                }
            }
            if (parts.length >= 6) {
                try {
                    this.version = Integer.parseInt(parts[5].trim());
                } catch (Exception ignored) {
                }
            }
        }
    }
}
