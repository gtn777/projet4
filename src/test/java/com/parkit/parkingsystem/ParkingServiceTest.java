package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;

import org.junit.jupiter.api.BeforeAll;
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

	private ParkingService parkingService;
	private Ticket ticket;

	@Mock
	private InputReaderUtil inputReaderUtil;
	@Mock
	private ParkingSpotDAO parkingSpotDAO;
	@Mock
	private TicketDAO ticketDAO;

	@BeforeAll
	private static void setUp() throws Exception {
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		ticket = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
	}

	@Order(1)
	@Test
	public void processExitingVehicleTest() throws Exception {
		// GIVEN
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

		// WHEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
	}

	@Order(2)
	@Test
	public void processIncomingVehicleWithRecurrentUser() throws Exception {
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
		when(ticketDAO.isUserEverEntered(any(String.class))).thenReturn(true);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

		// WHEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		// THEN
		verify(inputReaderUtil, Mockito.times(1)).readSelection();
		verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
		verify(inputReaderUtil, Mockito.times(1)).readVehicleRegistrationNumber();
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
		verify(ticketDAO, Mockito.times(1)).isUserEverEntered("ABCDEF");
		assertDoesNotThrow(() -> {
			parkingService.processIncomingVehicle();
		});
	}

	@Order(3)
	@Test
	public void getNextParkingNumberIfAvailable_whenParkingIsFull() {
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

		// WHEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.getNextParkingNumberIfAvailable();

		// THEN
		verify(inputReaderUtil, Mockito.times(1)).readSelection();
		verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any());
		assertDoesNotThrow(() -> {
			parkingService.getNextParkingNumberIfAvailable();
		});
	}

	@Order(4)
	@Test
	public void getNextParkingNumberIfAvailable_withBadVehicleTypeUserEntry() throws Exception {
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(5);

		// WHEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.getNextParkingNumberIfAvailable();

		// THEN
		verify(inputReaderUtil, Mockito.times(1)).readSelection();
		verify(parkingSpotDAO, Mockito.times(0)).getNextAvailableSlot(any());
		assertDoesNotThrow(() -> {
			parkingService.getNextParkingNumberIfAvailable();
		});
	}

	@Order(5)
	@Test
	public void processExitingVehicleTest_withTicketUpdateFaulty() throws Exception {
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any())).thenReturn(false);

		// WHEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();

		// THEN
		verify(inputReaderUtil, Mockito.times(1)).readVehicleRegistrationNumber();
		verify(ticketDAO, Mockito.times(1)).getTicket(anyString());
		verify(ticketDAO, Mockito.times(1)).updateTicket(any());
		verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
		assertDoesNotThrow(() -> {
			parkingService.getNextParkingNumberIfAvailable();
		});
	}

}
