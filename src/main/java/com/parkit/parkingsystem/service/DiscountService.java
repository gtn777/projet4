package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class DiscountService {

	private static TicketDAO ticketDAO;

	public long applyDiscount(Ticket ticket, long duration) {
		if (duration < 1800000) {
			duration = 0;
		} else if (isUserEverEntered(ticket)) {
			duration /= 1.05;
		}
		return duration;
	}

	private boolean isUserEverEntered(Ticket ticketToCheck) {
		return ticketDAO.isUserEverEnteredAndExit(ticketToCheck.getVehicleRegNumber());
	}

	public TicketDAO getTicketDao() {
		return ticketDAO;
	}

	public void setTicketDAO(TicketDAO tdao) {
		ticketDAO = tdao;
	}

}
