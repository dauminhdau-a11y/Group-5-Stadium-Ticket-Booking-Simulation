package repository;

import model.Fan;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FanRepository {
    private String filePath;

    public FanRepository() {
        this.filePath = resolveFilePath();
        initFile();
    }

    private String resolveFilePath() {
        String fileName = "fans.csv";
        String[] candidatePaths = {
            "Guest/data/" + fileName,
            "data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation/Guest/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Group-5-Stadium-Ticket-Booking-Simulation/Guest/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Group-5-Stadium-Ticket-Booking-Simulation/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Guest/data/" + fileName
        };

        for (String path : candidatePaths) {
            File f = new File(path);
            if (f.exists()) {
                return f.getAbsolutePath();
            }
        }

        String[] candidateDirs = {
            "Guest/data",
            "data",
            "Group-5-Stadium-Ticket-Booking-Simulation/Guest/data",
            "Group-5-Stadium-Ticket-Booking-Simulation/data",
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Group-5-Stadium-Ticket-Booking-Simulation/Guest/data",
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Guest/data"
        };
        for (String dir : candidateDirs) {
            File d = new File(dir);
            if (d.exists() && d.isDirectory()) {
                return new File(d, fileName).getAbsolutePath();
            }
        }

        File defaultDir = new File("Guest/data");
        if (defaultDir.exists() || defaultDir.mkdirs()) {
            return new File(defaultDir, fileName).getAbsolutePath();
        }

        return new File("data", fileName).getAbsolutePath();
    }

    private void initFile() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (file.getParentFile() != null) file.getParentFile().mkdirs();
                try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                    pw.println("fanId,username,fullName,email,phone,password");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khởi tạo fans.csv: " + e.getMessage());
        }
    }

    public synchronized List<Fan> findAll() {
        List<Fan> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            initFile();
            return list;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // Header
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
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        boolean writeHeader = !file.exists() || file.length() == 0;

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            if (writeHeader) {
                pw.println("fanId,username,fullName,email,phone,password");
            }
            pw.println(fan.toCsv());
            pw.flush();
            syncToOtherFiles(fan);
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi ghi fans.csv: " + e.getMessage());
            return false;
        }
    }

    private void syncToOtherFiles(Fan fan) {
        String fileName = "fans.csv";
        String[] candidatePaths = {
            "Guest/data/" + fileName,
            "data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation/Guest/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Group-5-Stadium-Ticket-Booking-Simulation/Guest/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Group-5-Stadium-Ticket-Booking-Simulation/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Guest/data/" + fileName
        };
        for (String path : candidatePaths) {
            File f = new File(path);
            if (f.exists() && !f.getAbsolutePath().equalsIgnoreCase(new File(filePath).getAbsolutePath())) {
                try {
                    boolean writeHeader = f.length() == 0;
                    try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f, true), StandardCharsets.UTF_8))) {
                        if (writeHeader) {
                            pw.println("fanId,username,fullName,email,phone,password");
                        }
                        pw.println(fan.toCsv());
                        pw.flush();
                    }
                } catch (Exception ignored) {
                }
            }
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

        boolean success = writeAllFans(new File(filePath), fans);
        if (success) {
            syncAllToOtherFiles(fans);
        }
        return success;
    }

    private boolean writeAllFans(File file, List<Fan> fans) {
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                pw.println("fanId,username,fullName,email,phone,password");
                for (Fan f : fans) {
                    pw.println(f.toCsv());
                }
                pw.flush();
                return true;
            }
        } catch (IOException e) {
            System.err.println("Lỗi cập nhật file: " + file.getPath() + " - " + e.getMessage());
            return false;
        }
    }

    private void syncAllToOtherFiles(List<Fan> fans) {
        String fileName = "fans.csv";
        String[] candidatePaths = {
            "Guest/data/" + fileName,
            "data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation/Guest/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Group-5-Stadium-Ticket-Booking-Simulation/Guest/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Group-5-Stadium-Ticket-Booking-Simulation/data/" + fileName,
            "Group-5-Stadium-Ticket-Booking-Simulation-main/Guest/data/" + fileName
        };
        for (String path : candidatePaths) {
            File f = new File(path);
            if (f.exists() && !f.getAbsolutePath().equalsIgnoreCase(new File(filePath).getAbsolutePath())) {
                writeAllFans(f, fans);
            }
        }
    }

    public String getFilePath() {
        return filePath;
    }
}
