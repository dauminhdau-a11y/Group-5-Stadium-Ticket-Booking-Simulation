package util;

import model.enums.Stand;
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
            Stand stand = Stand.values()[Math.min(3, ((i - 1) * 4) / count)];
            String seatId = stand.name() + "-" + i;
            String line = seatId + "," + matchId + ",AVAILABLE," + stand.name()
                    + ",500000,1";
            writer.write(line);
            writer.newLine();
        }
        writer.close();
        return count;
    }
}
