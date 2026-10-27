package com.ticketbooking.strategy;

import com.ticketbooking.exception.SeatAlreadyBookedException;
import com.ticketbooking.model.Seat;
import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.enums.SeatStatus;
import com.ticketbooking.model.enums.TicketStatus;
import com.ticketbooking.repository.SeatRepository;
import com.ticketbooking.repository.TicketRepository;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.time.LocalDateTime;
import java.util.UUID;

public class FileLockStrategy implements BookingStrategy {
    private String lockFilePath;

    public FileLockStrategy() {
        this("data/seats.lock");
    }

    public FileLockStrategy(String lockFilePath) {
        this.lockFilePath = lockFilePath;
    }

    public String getLockFilePath() {
        return lockFilePath;
    }

    public void setLockFilePath(String lockFilePath) {
        this.lockFilePath = lockFilePath;
    }

    @Override
    public boolean bookSeat(Seat seat, String fanId, String matchId, double price,
            TicketRepository ticketRepo, SeatRepository seatRepo) throws Exception {
        File file = new File(lockFilePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileChannel channel = raf.getChannel();
        FileLock lock = null;
        try {
            lock = channel.lock();
            Seat latestSeat = seatRepo.findById(seat.getSeatId());
            if (latestSeat != null && latestSeat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatAlreadyBookedException("Seat " + seat.getSeatId() + " is already booked.");
            }
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatAlreadyBookedException("Seat " + seat.getSeatId() + " is already booked.");
            }
            seat.setStatus(SeatStatus.BOOKED);
            seatRepo.save(seat);

            String ticketId = "TCK-" + UUID.randomUUID().toString().substring(0, 8);
            Ticket ticket = new Ticket(ticketId, matchId, seat.getSeatId(), fanId, price,
                    TicketStatus.SOLD, LocalDateTime.now().toString());
            ticketRepo.save(ticket);
            return true;
        } finally {
            if (lock != null && lock.isValid()) {
                lock.release();
            }
            channel.close();
            raf.close();
        }
    }
}
