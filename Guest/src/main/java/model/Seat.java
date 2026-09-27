/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author tanda
 */
public class Seat {
    private String seatId;
    private int rowNumber;
    private int seatNumber;
    private SeatStatus status;
    private String matchId;
    private String sectionId;
    
    public Seat (){}
    public Seat(String seatId, int rowNumber, int seatNumber, SeatStatus status, String matchId, String sectionId) {
        this.seatId = seatId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.status = status;
        this.matchId = matchId;
        this.sectionId = sectionId;
    }

    public static Seat fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) return null;
        return new Seat(parts[0].trim(), Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()),
                SeatStatus.valueOf(parts[3].trim().toUpperCase()), parts[4].trim(), parts[5].trim());
    }

    public String getSeatId() { return seatId; }
    public int getRowNumber() { return rowNumber; }
    public int getSeatNumber() { return seatNumber; }
    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }
    public String getMatchId() { return matchId; }
    public String getSectionId() { return sectionId; }

}
