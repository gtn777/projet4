package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InteractiveShellTest {

	private InteractiveShell interactiveShell;

	@Mock
	private static ParkingService parkingService;
	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	void setUp() throws Exception {
		// Those mocks are send in parameters to InteractiveShell,
		// for secure the production database during tests
		when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(ticketDAO.getTicket(any(String.class))).thenReturn(null);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
	}

	@AfterEach
	void tearDown() throws Exception {
		interactiveShell = null;
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@Order(1)
	@Test
	public void startAndExitApplicationTest() {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(3);

		// WHEN
		interactiveShell = new InteractiveShell(parkingService, inputReaderUtil, parkingSpotDAO, ticketDAO);
		Boolean isAppstarted = interactiveShell.isAppRunning();
		interactiveShell.loadInterface();

		// THEN
		assertEquals(true, isAppstarted);
		verify(inputReaderUtil, Mockito.times(1)).readSelection();
		assertEquals(false, interactiveShell.isAppRunning());
	}

	@Order(2)
	@Test
	public void optionOneThenUnsupportedOption() {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1, 5, 3);

		// WHEN
		interactiveShell = new InteractiveShell(parkingService, inputReaderUtil, parkingSpotDAO, ticketDAO);
		interactiveShell.loadInterface();

		// THEN
		verify(parkingService, Mockito.times(1)).processIncomingVehicle();
		verify(inputReaderUtil, Mockito.times(3)).readSelection();
		assertEquals(false, interactiveShell.isAppRunning());
	}

	@Order(3)
	@Test
	public void optionTwoThenBadEntry() {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(2, 42, 3);

		// WHEN
		interactiveShell = new InteractiveShell(parkingService, inputReaderUtil, parkingSpotDAO, ticketDAO);
		interactiveShell.loadInterface();

		// THEN
		verify(parkingService, Mockito.times(1)).processExitingVehicle();
		assertEquals(false, interactiveShell.isAppRunning());
	}
	
	@Order(4)
	@Test
	public void productionConstructorTest() {
		// GIVEN
		
		// WHEN
		
		// THEN
	}

}
