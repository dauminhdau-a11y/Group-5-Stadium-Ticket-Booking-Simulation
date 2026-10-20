package com.stadium.strategy;

import com.stadium.model.SyncMechanism;
import com.stadium.repository.impl.SeatRepository;
import java.util.List;

public class NoLockStrategy implements BookingStrategy {
    private final SeatRepository seatRepo;

    public NoLockStrategy(SeatRepository seatRepo) {
        this.seatRepo = seatRepo;
    }

    @Override
    public boolean tryBook(List<String> seatIds) {
        for (String id : seatIds) {
            if (!seatRepo.tryBookSeat(id)) {
                return false;
            }
        }

        return !seatIds.isEmpty();
    }

    @Override
    public SyncMechanism getMechanism() {
        return SyncMechanism.NO_LOCK;
    }
}