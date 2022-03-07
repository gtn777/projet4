package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	private static DiscountService discountService;

	public void calculateFare(Ticket ticket) {

		if (ticket.getOutTime().before(ticket.getInTime())) {
			throw new IllegalArgumentException("Out time provided is before in time:" + ticket.getOutTime().toString());
		}
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		long inMilliseconds = ticket.getInTime().getTime();
		long outMilliseconds = ticket.getOutTime().getTime();

		long duration = (outMilliseconds - inMilliseconds);
//		duration = discountService.applyDiscount(ticket, duration);
		
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
