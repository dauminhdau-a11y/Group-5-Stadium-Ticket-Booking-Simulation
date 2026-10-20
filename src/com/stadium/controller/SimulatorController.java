
package com.stadium.controller;

import com.stadium.model.SimulationResult;
import com.stadium.model.SyncMechanism;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.ReentrantLock;

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

        // Số ghế dùng trong mô phỏng
        final int seatCount = Math.max(1, numThreads / 3);

        // Dữ liệu cho NO_LOCK
        final Set<Integer> noLockSeats = new HashSet<>();

        // Dữ liệu cho SYNCHRONIZED
        final boolean[] synchronizedSeats =
                new boolean[seatCount];
        final Object lock = new Object();

        // Dữ liệu cho OPTIMISTIC: 0 = trống, 1 = đã đặt
        final AtomicIntegerArray optimisticSeats =
                new AtomicIntegerArray(seatCount);

        // Khóa trong JVM giúp tránh OverlappingFileLockException
        // khi nhiều luồng trong cùng JVM khóa cùng một file.
        final ReentrantLock[] localFileLocks =
                new ReentrantLock[seatCount];

        for (int i = 0; i < seatCount; i++) {
            localFileLocks[i] = new ReentrantLock();
        }

        final AtomicInteger successes = new AtomicInteger();
        final AtomicInteger failures = new AtomicInteger();
        final AtomicInteger duplicateAttempts = new AtomicInteger();

        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(numThreads);

        ExecutorService pool = Executors.newFixedThreadPool(
                Math.min(numThreads, 50));

        Path fileDirectory = null;
        long begin = System.nanoTime();

        try {
            // Tạo file trạng thái ghế cho FILE_LOCK
            if (mechanism == SyncMechanism.FILE_LOCK) {
                fileDirectory =
                        Files.createTempDirectory("stadium-simulation-");

                for (int i = 0; i < seatCount; i++) {
                    Path seatFile = fileDirectory.resolve(
                            "seat-" + i + ".dat");

                    Files.write(
                            seatFile,
                            new byte[] { 0 },
                            StandardOpenOption.CREATE_NEW,
                            StandardOpenOption.WRITE);
                }
            }

            final Path finalDirectory = fileDirectory;

            for (int i = 0; i < numThreads; i++) {
                pool.execute(() -> {
                    try {
                        int seat = ThreadLocalRandom.current()
                                .nextInt(seatCount);

                        start.await();

                        boolean booked = tryBook(
                                mechanism,
                                seat,
                                noLockSeats,
                                synchronizedSeats,
                                lock,
                                optimisticSeats,
                                localFileLocks,
                                finalDirectory);

                        if (booked) {
                            successes.incrementAndGet();
                        } else {
                            failures.incrementAndGet();
                            duplicateAttempts.incrementAndGet();
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        failures.incrementAndGet();

                    } catch (IOException e) {
                        failures.incrementAndGet();
                        System.err.println(
                                "File-lock error: " + e.getMessage());

                    } finally {
                        done.countDown();
                    }
                });
            }

            begin = System.nanoTime();
            start.countDown();

            done.await();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Cannot initialize simulation.", e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            pool.shutdownNow();

        } finally {
            pool.shutdown();

            try {
                if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                    pool.awaitTermination(5, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }

            // Xóa các file tạm sau khi các tác vụ kết thúc
            if (fileDirectory != null) {
                try {
                    try (java.util.stream.Stream<Path> paths =
                            Files.list(fileDirectory)) {
                        paths.forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                System.err.println(
                                        "Cannot delete temp file: "
                                        + path);
                            }
                        });
                    }

                    Files.deleteIfExists(fileDirectory);

                } catch (IOException e) {
                    System.err.println(
                            "Cannot clean simulation files: "
                            + e.getMessage());
                }
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

    private boolean tryBook(
            SyncMechanism mechanism,
            int seat,
            Set<Integer> noLockSeats,
            boolean[] synchronizedSeats,
            Object lock,
            AtomicIntegerArray optimisticSeats,
            ReentrantLock[] localFileLocks,
            Path fileDirectory) throws IOException {

        switch (mechanism) {

            case NO_LOCK:
                // Cố ý không khóa dữ liệu dùng chung.
                // HashSet không an toàn khi bị sửa đồng thời.
                if (noLockSeats.contains(seat)) {
                    return false;
                }

                noLockSeats.add(seat);
                return true;

            case SYNCHRONIZED:
                synchronized (lock) {
                    if (synchronizedSeats[seat]) {
                        return false;
                    }

                    synchronizedSeats[seat] = true;
                    return true;
                }

            case OPTIMISTIC:
                // Chỉ một luồng có thể đổi trạng thái 0 -> 1.
                return optimisticSeats.compareAndSet(
                        seat, 0, 1);

            case FILE_LOCK:
                return tryBookWithFileLock(
                        seat,
                        localFileLocks,
                        fileDirectory);

            default:
                throw new IllegalArgumentException(
                        "Unsupported mechanism: " + mechanism);
        }
    }

    private boolean tryBookWithFileLock(
            int seat,
            ReentrantLock[] localFileLocks,
            Path fileDirectory) throws IOException {

        Path seatFile = fileDirectory.resolve(
                "seat-" + seat + ".dat");

        // FileLock không chờ được một khóa chồng lấn
        // trong cùng JVM; dùng khóa cục bộ trước.
        ReentrantLock localLock = localFileLocks[seat];
        localLock.lock();

        try {
            try (FileChannel channel = FileChannel.open(
                    seatFile,
                    StandardOpenOption.READ,
                    StandardOpenOption.WRITE);
                 FileLock fileLock = channel.lock()) {

                ByteBuffer buffer = ByteBuffer.allocate(1);

                channel.position(0);
                int bytesRead = channel.read(buffer);

                if (bytesRead != 1) {
                    throw new IOException(
                            "Invalid seat state file: " + seatFile);
                }

                buffer.flip();
                byte state = buffer.get();

                if (state != 0) {
                    return false;
                }

                ByteBuffer newState =
                        ByteBuffer.wrap(new byte[] { 1 });

                channel.position(0);

                while (newState.hasRemaining()) {
                    channel.write(newState);
                }

                channel.force(true);
                return true;
            }
        } finally {
            localLock.unlock();
        }
    }
}
