package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, TicketDAO ticketDAO) {

		if (ticket.getOutTime() == null) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
		if (ticket.getOutTime().before(ticket.getInTime())) {
			throw new IllegalArgumentException("Out time provided is before in time:" + ticket.getOutTime().toString());
		}

		long inMilliseconds = ticket.getInTime().getTime();
		long outMilliseconds = ticket.getOutTime().getTime();

		long duration = (outMilliseconds - inMilliseconds);

		if (duration > 1800000) {
			duration -= 1800000;
			if (ticketDAO.isUserEverEntered(ticket.getVehicleRegNumber())) {
				duration /= 1.05;
			}
		} else {
			duration = 0;
		}

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			ticket.setPrice(duration * (Fare.CAR_RATE_PER_HOUR) / 3600000);
			break;
		}
		case BIKE: {
			ticket.setPrice(duration * (Fare.BIKE_RATE_PER_HOUR) / 3600000);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}

}
