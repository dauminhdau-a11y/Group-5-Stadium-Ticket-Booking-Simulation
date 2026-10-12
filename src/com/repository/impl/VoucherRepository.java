package com.repository.impl;

import java.nio.file.Path;

import com.model.Voucher;

public class VoucherRepository extends AbstractCsvRepository<Voucher> {
	public VoucherRepository() {
		super(Voucher.class, "vouchers.csv");
	}

	public VoucherRepository(Path dataDirectory) {
		super(Voucher.class, dataDirectory.resolve("vouchers.csv"));
	}
}
