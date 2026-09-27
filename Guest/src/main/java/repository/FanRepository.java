
package repository;
import model.Fan;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
public class FanRepository {
   private final String filePath = "data/fans.csv";

    public FanRepository() {
        initFile();
    }

    private void initFile() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (file.getParentFile() != null) file.getParentFile().mkdirs();
                try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                    pw.println("fanId,username,fullName,email,phone,password");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khởi tạo fans.csv: " + e.getMessage());
        }
    }

    public synchronized List<Fan> findAll() {
        List<Fan> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Fan fan = Fan.fromCsv(line);
                    if (fan != null) list.add(fan);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc fans.csv: " + e.getMessage());
        }
        return list;
    }

    public synchronized boolean save(Fan fan) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {
            pw.println(fan.toCsv());
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi ghi fans.csv: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean update(Fan updatedFan) {
        List<Fan> fans = findAll();
        boolean found = false;
        for (int i = 0; i < fans.size(); i++) {
            if (fans.get(i).getFanId().equalsIgnoreCase(updatedFan.getFanId())) {
                fans.set(i, updatedFan);
                found = true;
                break;
            }
        }
        if (!found) return false;

        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, false))) {
            pw.println("fanId,username,fullName,email,phone,password");
            for (Fan f : fans) {
                pw.println(f.toCsv());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi cập nhật fans.csv: " + e.getMessage());
            return false;
        }
    } 
}
