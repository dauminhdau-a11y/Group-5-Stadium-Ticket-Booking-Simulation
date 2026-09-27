package com.stadium.controller;

import com.stadium.model.enums.SyncMechanism;

public class SimulatorController {
    private final BookingController bookingCtrl;

    public SimulatorController(BookingController bookingCtrl) { this.bookingCtrl = bookingCtrl; }
    public SimulationResult runSimulation(int numThreads, SyncMechanism mechanism) {
        return new SimulationResult(mechanism, numThreads, 0, 0, 0.0);
    }
}
