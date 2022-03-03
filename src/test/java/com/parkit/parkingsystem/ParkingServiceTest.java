package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	private void setUpPerTest() {
	}

	@Order(1)
	@Test
	public void processExitingVehicleTest() throws Throwable {
		// GIVEN
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processExitingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
	}

	@Order(2)
	@Test
	public void processIncomingVehicle() throws Throwable {
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
		when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

		// WHEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
		verify(inputReaderUtil, Mockito.times(1)).readSelection();
	}

	@Order(3)
	@Test
	public void processGetNextParkingNumberIfAvailable_whenParkingIsFull() {
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
//		ParkingSpot result = null;

		// WHEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// THEN
		assertDoesNotThrow(() -> {
			parkingService.getNextParkingNumberIfAvailable();
		});
		verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any());
		verify(inputReaderUtil, Mockito.times(1)).readSelection();

	}

	@Order(4)
	@Test
	public void processGetNextParkingNumberIfAvailable_withBadVehicleTypeUserEntry() {
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(5);

		// WHEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// THEN
		assertDoesNotThrow(() -> {
			parkingService.getNextParkingNumberIfAvailable();
		});
		verify(inputReaderUtil, Mockito.times(1)).readSelection();
		verify(parkingSpotDAO, Mockito.times(0)).getNextAvailableSlot(any());
	}

}
