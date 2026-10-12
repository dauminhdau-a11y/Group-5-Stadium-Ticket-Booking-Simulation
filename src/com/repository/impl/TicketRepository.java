package com.repository.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.model.Ticket;

public class TicketRepository extends AbstractCsvRepository<Ticket> {
	public TicketRepository() {
		super(Ticket.class, "tickets.csv");
	}

	public TicketRepository(Path dataDirectory) {
		super(Ticket.class, dataDirectory.resolve("tickets.csv"));
	}

	public List<Ticket> findByTransaction(String transactionId) {
		List<Ticket> result = new ArrayList<Ticket>();
		for (Ticket ticket : findAll()) {
			if (transactionId != null && transactionId.equals(ticket.getTransactionId())) {
				result.add(ticket);
			}
		}
		return result;
	}
}
