package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
	private static final String vehicleRegNumber = "fcsT";
	private Date inTime;
	private Date outTime;
	private ParkingSpot parkingSpot = null;

	@Mock
	private TicketDAO ticketDAO;

	@BeforeEach
	void setUpPerTest() {
		ticket = new Ticket();
		inTime = new Date();
		outTime = new Date();
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		ticket.setOutTime(outTime);
		ticket.setVehicleRegNumber(vehicleRegNumber);
		when(ticketDAO.isUserEverEntered(anyString())).thenReturn(false);
	}

	@Test
	public void calculateFare_carOneHour() {
		// GIVEN
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		ticket.setInTime(inTime);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals(Fare.CAR_RATE_PER_HOUR / 2, ticket.getPrice());
	}

	@Test
	public void calculateFare_carTenDays_recurrentUser() {
		// GIVEN
		when(ticketDAO.isUserEverEntered(anyString())).thenReturn(true);
		inTime.setTime(System.currentTimeMillis() - (240 * 60 * 60 * 1000));
		ticket.setInTime(inTime);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals(342.143, ticket.getPrice());
	}

	@Test
	public void calculateFare_withBikeForOneHour() {
		// GIVEN
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals(Fare.BIKE_RATE_PER_HOUR / 2, ticket.getPrice());
	}

	@Test
	public void calculateFare_unkownVehicleType() {
		// GIVEN
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		parkingSpot = new ParkingSpot(1, ParkingType.TEST, false);
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorService.calculateFare(ticket, ticketDAO);
		} catch (Exception e) {
			System.out.println("Unknown vehicle type, test");
		}

		// THEN
//		verify(ticketDAO, Mockito.times(0)).isUserEverEntered(any());
		assertEquals(0, ticket.getPrice());
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDAO));
	}

	@Test
	public void calculateFare_withFutureInTime() {
		// GIVEN
		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
		parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorService.calculateFare(ticket, ticketDAO);
		} catch (Exception e) {
			System.out.println("outTime is before in time, test");
		}

		// THEN
		verify(ticketDAO, Mockito.times(0)).isUserEverEntered(any());
		assertEquals(0, ticket.getPrice());
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDAO));
	}

	@Test
	public void calculateFareBike_withLessThanOneHourParkingTime() {
		// GIVEN
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
		parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals((0.25 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCar_withLessThanOneHourParkingTime() {
		// GIVEN
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
		ticket.setInTime(inTime);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals((0.25 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCar_withMoreThanADayParkingTime() {
		// GIVE
		inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
		ticket.setInTime(inTime);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals((23.5 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCar_withMoreThanADayParkingTime_recurrentUser() {
		// GIVE
		when(ticketDAO.isUserEverEntered(anyString())).thenReturn(true);
		inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
		ticket.setInTime(inTime);

		// WHEN
		fareCalculatorService.calculateFare(ticket, ticketDAO);

		// THEN
		assertEquals(33.571, ticket.getPrice());
	}

	@Test
	public void calculateFare_withNullValueForOutTime() {
		// GIVE
		inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
		ticket.setInTime(inTime);
		ticket.setOutTime(null);

		// WHEN
		try {
			fareCalculatorService.calculateFare(ticket, ticketDAO);
		} catch (Exception e) {
			System.out.println("nullPointerException FarCalculatorService");
		}

		// THEN
		verify(ticketDAO, Mockito.times(0)).isUserEverEntered(any());
		assertEquals(0, ticket.getPrice());
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, ticketDAO));
	}

}
