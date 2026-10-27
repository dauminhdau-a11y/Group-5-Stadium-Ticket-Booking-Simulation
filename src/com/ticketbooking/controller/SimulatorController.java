package com.ticketbooking.controller;

import com.ticketbooking.model.Seat;
import com.ticketbooking.model.SimulationResult;
import com.ticketbooking.model.Transaction;
import com.ticketbooking.model.enums.SeatStatus;
import com.ticketbooking.model.enums.SyncMechanism;
import com.ticketbooking.model.enums.TransactionStatus;
import com.ticketbooking.repository.SeatRepository;
import com.ticketbooking.repository.SimulationResultRepository;
import com.ticketbooking.repository.TicketRepository;
import com.ticketbooking.repository.TransactionRepository;
import com.ticketbooking.strategy.BookingStrategy;
import com.ticketbooking.strategy.FileLockStrategy;
import com.ticketbooking.strategy.NoLockStrategy;
import com.ticketbooking.strategy.OptimisticStrategy;
import com.ticketbooking.strategy.SynchronizedStrategy;
import com.ticketbooking.util.DataGenerator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulatorController {
    private int threadCount;
    private SyncMechanism selectedMechanism;
    private DataGenerator dataGenerator;
    private SimulationResultRepository resultRepo;
    private SeatRepository seatRepo;
    private TicketRepository ticketRepo;
    private TransactionRepository transactionRepo;
    private List<SimulationResult> memoryResults;

    public SimulatorController() {
        this(10, SyncMechanism.SYNCHRONIZED);
    }

    public SimulatorController(int threadCount, SyncMechanism selectedMechanism) {
        this.threadCount = threadCount;
        this.selectedMechanism = selectedMechanism;
        this.dataGenerator = new DataGenerator("data/seats.csv");
        this.resultRepo = new SimulationResultRepository("data/simulation_results.csv");
        this.seatRepo = new SeatRepository("data/seats.csv");
        this.ticketRepo = new TicketRepository("data/tickets.csv");
        this.transactionRepo = new TransactionRepository("data/transactions.csv");
        this.memoryResults = new ArrayList<>();
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        if (threadCount <= 0) {
            this.threadCount = 10;
        } else {
            this.threadCount = threadCount;
        }
    }

    public SyncMechanism getSelectedMechanism() {
        return selectedMechanism;
    }

    public void setSelectedMechanism(SyncMechanism selectedMechanism) {
        this.selectedMechanism = selectedMechanism;
    }

    public int generateData(int rowCount, String matchId) throws IOException {
        if (rowCount < 10000) {
            rowCount = 10000;
        }
        return dataGenerator.generateSeatsData(rowCount, matchId);
    }

    public BookingStrategy resolveStrategy(SyncMechanism mechanism) {
        if (mechanism == SyncMechanism.SYNCHRONIZED) {
            return new SynchronizedStrategy();
        } else if (mechanism == SyncMechanism.OPTIMISTIC) {
            return new OptimisticStrategy();
        } else if (mechanism == SyncMechanism.FILE_LOCK) {
            return new FileLockStrategy();
        } else {
            return new NoLockStrategy();
        }
    }

    public SimulationResult runSimulation(String matchId, int requestCount) throws Exception {
        if (requestCount <= 0) {
            requestCount = 1000;
        }
        List<Seat> seats = seatRepo.findAll();
        List<Seat> targetSeats = new ArrayList<>();
        for (Seat s : seats) {
            if (matchId == null || matchId.equalsIgnoreCase(s.getMatchId())) {
                targetSeats.add(s);
            }
        }
        if (targetSeats.isEmpty()) {
            throw new IllegalStateException("No seats available to simulate for match " + matchId);
        }

        BookingStrategy strategy = resolveStrategy(selectedMechanism);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            int requestIndex = i;
            executor.submit(() -> {
                try {
                    int seatIdx = requestIndex % Math.min(targetSeats.size(), 500);
                    Seat seatToBook = targetSeats.get(seatIdx);
                    String simFanId = "FAN_SIM_" + (requestIndex % 100);
                    boolean booked = strategy.bookSeat(seatToBook, simFanId, matchId,
                            seatToBook.getPrice(), ticketRepo, seatRepo);
                    if (booked) {
                        successCount.incrementAndGet();
                    } else {
                        failedCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failedCount.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        long totalTime = System.currentTimeMillis() - startTime;

        String resultId = "SIM-" + UUID.randomUUID().toString().substring(0, 8);
        SimulationResult result = new SimulationResult(
                resultId,
                selectedMechanism,
                threadCount,
                requestCount,
                successCount.get(),
                failedCount.get(),
                totalTime,
                LocalDateTime.now());

        memoryResults.add(result);
        return result;
    }

    public List<SimulationResult> getComparisonResults() throws IOException {
        List<SimulationResult> all = new ArrayList<>();
        all.addAll(resultRepo.findAll());
        all.addAll(memoryResults);
        return all;
    }

    public void saveResultsToCsv() throws IOException {
        for (SimulationResult res : memoryResults) {
            resultRepo.save(res);
        }
        memoryResults.clear();
    }

    public List<Transaction> getTransactions() throws IOException {
        return transactionRepo.findAll();
    }

    public double calculateRevenue() throws IOException {
        double total = 0.0;
        for (Transaction txn : transactionRepo.findAll()) {
            if (txn.getStatus() == TransactionStatus.CONFIRMED || txn.getStatus() == TransactionStatus.SUCCESS) {
                total += txn.getTotalAmount();
            }
        }
        return total;
    }
}
