
package com.stadium.controller;

import com.stadium.model.SimulationResult;
import com.stadium.model.SyncMechanism;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulatorController {

    private final BookingController bookingCtrl;

    public SimulatorController() {
        this(null);
    }

    public SimulatorController(BookingController bookingCtrl) {
        this.bookingCtrl = bookingCtrl;
    }

    public SimulationResult runSimulation(
            int numThreads,
            SyncMechanism mechanism) {

        if (numThreads <= 0) {
            throw new IllegalArgumentException(
                    "Number of users must be greater than zero.");
        }

        if (mechanism == null) {
            throw new IllegalArgumentException(
                    "Synchronization mechanism cannot be null.");
        }

        // Số ghế trong dữ liệu mô phỏng
        final int seatCount = Math.max(1, numThreads / 3);

        // Danh sách ghế đã được đặt
        final Set<Integer> bookedSeats = new HashSet<>();

        // Đối tượng khóa dùng cho chế độ synchronized
        final Object lock = new Object();

        // Bộ đếm an toàn giữa các luồng
        final AtomicInteger successes = new AtomicInteger();
        final AtomicInteger failures = new AtomicInteger();
        final AtomicInteger duplicateAttempts = new AtomicInteger();

        // Chờ các luồng sẵn sàng
        final CountDownLatch ready =
                new CountDownLatch(numThreads);

        // Phát tín hiệu bắt đầu
        final CountDownLatch start =
                new CountDownLatch(1);

        // Chờ các luồng hoàn thành
        final CountDownLatch done =
                new CountDownLatch(numThreads);

        ExecutorService pool =
                Executors.newFixedThreadPool(
                        Math.min(numThreads, 50));

        long begin = System.nanoTime();

        for (int i = 0; i < numThreads; i++) {
            pool.execute(() -> {

                int seat = ThreadLocalRandom.current()
                        .nextInt(seatCount);

                ready.countDown();

                try {
                    start.await();

                    boolean booked;

                    if (mechanism == SyncMechanism.NO_LOCK) {
                        // Chế độ không khóa
                        booked = bookedSeats.add(seat);
                    } else {
                        // Các chế độ còn lại dùng cùng một khóa
                        synchronized (lock) {
                            booked = bookedSeats.add(seat);
                        }
                    }

                    if (booked) {
                        successes.incrementAndGet();
                    } else {
                        failures.incrementAndGet();
                        duplicateAttempts.incrementAndGet();
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    failures.incrementAndGet();

                } finally {
                    done.countDown();
                }
            });
        }

        try {
            ready.await();

            begin = System.nanoTime();
            start.countDown();

            done.await();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        } finally {
            pool.shutdown();

            try {
                if (!pool.awaitTermination(
                        5, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                pool.shutdownNow();
            }
        }

        long durationMs = Math.max(
                1,
                TimeUnit.NANOSECONDS.toMillis(
                        System.nanoTime() - begin));

        return new SimulationResult(
                mechanism,
                numThreads,
                durationMs,
                successes.get(),
                failures.get(),
                duplicateAttempts.get());
    }
}
