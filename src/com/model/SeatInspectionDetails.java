package com.model;

public class SeatInspectionDetails {
    private final MatchSeat seat;
    private final Match match;
    private final Ticket ticket;
    private final BookingTransaction transaction;
    private final long remainingLockSeconds;

    public SeatInspectionDetails(MatchSeat seat, Match match, Ticket ticket,
                                BookingTransaction transaction, long remainingLockSeconds) {
        this.seat = seat;
        this.match = match;
        this.ticket = ticket;
        this.transaction = transaction;
        this.remainingLockSeconds = remainingLockSeconds;
    }

    public MatchSeat getSeat() {
        return seat;
    }

    public Match getMatch() {
        return match;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public BookingTransaction getTransaction() {
        return transaction;
    }

    public long getRemainingLockSeconds() {
        return remainingLockSeconds;
    }
}
