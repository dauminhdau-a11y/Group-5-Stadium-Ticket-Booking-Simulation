package com.ticketbooking.repository;

import com.ticketbooking.model.Voucher;

public class VoucherRepository extends CsvRepository<Voucher> {
    public VoucherRepository() {
        this("data/vouchers.csv");
    }

    public VoucherRepository(String fileName) {
        super(fileName);
    }

    @Override
    public Voucher parseLine(String line) {
        return new Voucher().fromCsvLine(line);
    }
}