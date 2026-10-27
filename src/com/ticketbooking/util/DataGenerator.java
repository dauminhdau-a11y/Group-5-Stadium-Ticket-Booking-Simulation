package com.ticketbooking.util;

import com.ticketbooking.model.enums.SeatType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataGenerator {
    private String seatsFilePath;

    public DataGenerator() {
        this("data/seats.csv");
    }

    public DataGenerator(String seatsFilePath) {
        this.seatsFilePath = seatsFilePath;
    }

    public String getSeatsFilePath() {
        return seatsFilePath;
    }

    public void setSeatsFilePath(String seatsFilePath) {
        this.seatsFilePath = seatsFilePath;
    }

    public int generateSeatsData(int count, String matchId) throws IOException {
        if (count < 10000) {
            count = 10000;
        }
        File file = new File(seatsFilePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (int i = 1; i <= count; i++) {
            SeatType type;
            double price;
            if (i <= count * 0.05) {
                type = SeatType.VVIP;
                price = 1800000.0;
            } else if (i <= count * 0.25) {
                type = SeatType.VIP;
                price = 1000000.0;
            } else if (i <= count * 0.70) {
                type = SeatType.STANDARD;
                price = 500000.0;
            } else if (i <= count * 0.90) {
                type = SeatType.ECONOMY;
                price = 250000.0;
            } else {
                type = SeatType.STANDING;
                price = 80000.0;
            }

            String seatId = type.name() + "-" + i;
            String line = seatId + "," + matchId + ",AVAILABLE," + type.name() + ","
                    + ((long) price) + ",1";
            writer.write(line);
            writer.newLine();
        }
        writer.close();
        return count;
    }
}
