package com.stadium.strategy;

import com.stadium.model.SyncMechanism;
import java.util.List;

public interface BookingStrategy {
    boolean tryBook(List<String> seatIds);
    SyncMechanism getMechanism();
}
