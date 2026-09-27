package com.stadium.repository.impl;

import com.stadium.model.Ticket;
import java.util.List;

public class TicketRepository extends AbstractCsvRepository<Ticket> {
    public List<Ticket> findByMatchId(String matchId) {
        return findByCondition(ticket -> matchId != null && matchId.equals(ticket.getMatchId()));
    }
}
