package com.ticketbooking.controller;

import com.ticketbooking.exception.VoucherInvalidException;
import com.ticketbooking.model.Voucher;
import com.ticketbooking.repository.VoucherRepository;
import java.io.IOException;
import java.util.List;

public class VoucherController {
    private VoucherRepository voucherRepository;

    public VoucherController() {
        this(new VoucherRepository("data/vouchers.csv"));
    }

    public VoucherController(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public VoucherRepository getVoucherRepository() {
        return voucherRepository;
    }

    public void setVoucherRepository(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public List<Voucher> getAllVouchers() throws IOException {
        return voucherRepository.findAll();
    }

    public Voucher getVoucherByCode(String code) throws IOException {
        if (code == null) {
            return null;
        }
        return voucherRepository.findById(code.trim());
    }

    public boolean createVoucher(Voucher voucher) throws IOException {
        if (voucher == null || voucher.getCode() == null || voucher.getCode().trim().isEmpty()) {
            return false;
        }
        if (voucherRepository.findById(voucher.getCode()) != null) {
            return false;
        }
        voucherRepository.save(voucher);
        return true;
    }

    public boolean updateVoucher(String code, double discountPercent, double discountAmount, boolean used) throws IOException {
        Voucher voucher = voucherRepository.findById(code);
        if (voucher == null) {
            return false;
        }
        voucher.setDiscountPercent(discountPercent);
        voucher.setDiscountAmount(discountAmount);
        voucher.setUsed(used);
        voucherRepository.save(voucher);
        return true;
    }

    public boolean removeVoucher(String code) throws IOException {
        return voucherRepository.deleteById(code);
    }

    public double applyVoucher(String code, double orderAmount) throws IOException, VoucherInvalidException {
        if (code == null || code.trim().isEmpty()) {
            return 0.0;
        }
        Voucher voucher = voucherRepository.findById(code.trim());
        if (voucher == null || voucher.isUsed()) {
            throw new VoucherInvalidException("Voucher is invalid or has already been used: " + code);
        }
        return voucher.calculateDiscount(orderAmount);
    }
}
