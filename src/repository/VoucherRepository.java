package repository;

import model.Voucher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VoucherRepository implements CsvRepository<Voucher> {
    private static final Path FILE_PATH = Paths.get("data", "vouchers.csv");

    @Override
    public List<Voucher> findAll() {
        List<Voucher> vouchers = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return vouchers;
        }

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Voucher voucher = new Voucher();
                voucher.fromCsvLine(line);
                vouchers.add(voucher);
            }
        } catch (IOException e) {
            System.out.println("Error reading vouchers file: " + e.getMessage());
        }
        return vouchers;
    }

    @Override
    public Voucher findById(String code) {
        if (code == null) return null;
        for (Voucher voucher : findAll()) {
            if (voucher.getCode().equalsIgnoreCase(code.trim())) {
                return voucher;
            }
        }
        return null;
    }

    @Override
    public void save(Voucher voucher) {
        List<Voucher> vouchers = findAll();
        int existingIndex = -1;
        for (int i = 0; i < vouchers.size(); i++) {
            if (vouchers.get(i).getCode().equalsIgnoreCase(voucher.getCode())) {
                existingIndex = i;
                break;
            }
        }
        if (existingIndex >= 0) {
            vouchers.set(existingIndex, voucher);
        } else {
            vouchers.add(voucher);
        }
        writeAll(vouchers);
    }

    @Override
    public void delete(String code) {
        List<Voucher> vouchers = findAll();
        vouchers.removeIf(v -> v.getCode().equalsIgnoreCase(code));
        writeAll(vouchers);
    }

    @Override
    public List<Voucher> findByCondition(Predicate<Voucher> predicate) {
        return findAll().stream().filter(predicate).collect(Collectors.toList());
    }

    private void writeAll(List<Voucher> vouchers) {
        try {
            if (!Files.exists(FILE_PATH.getParent())) {
                Files.createDirectories(FILE_PATH.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
                for (Voucher voucher : vouchers) {
                    writer.write(voucher.toCsvLine());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing vouchers file: " + e.getMessage());
        }
    }
}
