package com.stadium.controller;

import com.stadium.model.SimulationResult;
import com.stadium.model.SyncMechanism;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulatorController {

    private final SyncMechanism syncMechanism;

    public SimulatorController() {
        syncMechanism = new SyncMechanism();
    }

    public SimulationResult runSimulation(
            int numberOfUsers,
            int numberOfSeats) {

        if (numberOfUsers <= 0 || numberOfSeats <= 0) {
            throw new IllegalArgumentException(
                    "Number of users and seats must be greater than 0."
            );
        }

        syncMechanism.reset();

        AtomicInteger successfulBookings =
                new AtomicInteger(0);

        AtomicInteger failedBookings =
                new AtomicInteger(0);

        int threadPoolSize =
                Math.min(numberOfUsers, 20);

        ExecutorService executor =
                Executors.newFixedThreadPool(threadPoolSize);

        CountDownLatch start =
                new CountDownLatch(1);

        CountDownLatch finished =
                new CountDownLatch(numberOfUsers);

        long startTime =
                System.currentTimeMillis();

        for (int i = 0; i < numberOfUsers; i++) {

            executor.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        start.await();

                        int selectedSeat =
                                ThreadLocalRandom.current()
                                        .nextInt(1, numberOfSeats + 1);

                        boolean booked =
                                syncMechanism
                                        .tryBookSeat(selectedSeat);

                        if (booked) {

                            successfulBookings
                                    .incrementAndGet();

                        } else {

                            failedBookings
                                    .incrementAndGet();
                        }

                    } catch (InterruptedException e) {

                        Thread.currentThread().interrupt();

                    } finally {

                        finished.countDown();
                    }
                }
            });
        }

        try {

            start.countDown();

            finished.await();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

        } finally {

            executor.shutdown();

            try {

                executor.awaitTermination(
                        5,
                        TimeUnit.SECONDS
                );

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        }

        long endTime =
                System.currentTimeMillis();

        long executionTime =
                endTime - startTime;

        return new SimulationResult(
                numberOfUsers,
                numberOfUsers,
                successfulBookings.get(),
                failedBookings.get(),
                numberOfSeats,
                executionTime
        );
    }
}