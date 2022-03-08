package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class FareCalculatorServiceTest {

	private FareCalculatorService fareCalculatorService = new FareCalculatorService();
	private Ticket ticket;
	private final String vehicleRegNumber = "fcsT";

	@Mock
	private TicketDAO ticketDAO;

	@BeforeAll
	private static void setUp() {
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
		ticket.setVehicleRegNumber(vehicleRegNumber);
		when(ticketDAO.isUserEverEntered(anyString())).thenReturn(false);
	}

	@Test
	public void calculateFare_carOneHour() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals(Fare.CAR_RATE_PER_HOUR / 2, ticket.getPrice());
	}

	@Test
	public void calculateFare_withBikeForOneHour() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals(Fare.BIKE_RATE_PER_HOUR / 2, ticket.getPrice());
	}

	@Test
	public void calculateFare_unkownVehicleType() {
		// GIVEN
		Date inTime = new Date();
		Date outTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorService.calculateFare(ticket, ticketDAO);
		} catch (Exception e) {
			System.out.println("Unknown vehicle type, test");
		}

		// THEN
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDAO));
	}

	@Test
	public void calculateFare_withFutureInTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorService.calculateFare(ticket, ticketDAO);
		} catch (Exception e) {
			System.out.println("outTime is before in time, test");
		}

		// THEN
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDAO));
	}

	@Test
	public void calculateFareBike_withLessThanOneHourParkingTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals((0.25 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCar_withLessThanOneHourParkingTime() {
		// GIVEN
		Date inTime = new Date();
		Date outTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals((0.25 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCar_withMoreThanADayParkingTime() {
		// GIVEN
		Date inTime = new Date();
		Date outTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals((23.5 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

}
