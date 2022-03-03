package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InteractiveShellTest {

	private InteractiveShell interactiveShell;

	@Mock
	private ParkingService parkingService;
	@Mock
	private InputReaderUtil inputReaderUtil;
	@Mock
	private ParkingSpotDAO parkingSpotDAO;
	@Mock
	private TicketDAO ticketDAO;

	@BeforeEach
	void setUp() throws Exception {
		when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(ticketDAO.getTicket(any(String.class))).thenReturn(null);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
	}

	@AfterEach
	void tearDown() throws Exception {
		parkingService = null;
		interactiveShell = null;
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void startAndExitApplicationTest() {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(3);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		interactiveShell = new InteractiveShell(parkingService, inputReaderUtil);
		Boolean isAppstarted = interactiveShell.isAppRunning();
		interactiveShell.loadInterface();

		// THEN
		assertEquals(true, isAppstarted);
		verify(inputReaderUtil, Mockito.times(1)).readSelection();
		assertEquals(false, interactiveShell.isAppRunning());
	}

	@Test
	public void optionOneThenUnsupportedOption() {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1, 5, 3);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		interactiveShell = new InteractiveShell(parkingService, inputReaderUtil);
		interactiveShell.loadInterface();

		// THEN
		verify(parkingService, Mockito.times(1)).processIncomingVehicle();
		verify(inputReaderUtil, Mockito.times(3)).readSelection();
		assertEquals(false, interactiveShell.isAppRunning());
	}

	@Test
	public void optionTwoThenBadRegistrationEntry() {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(2, 123, 3);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		interactiveShell = new InteractiveShell(parkingService, inputReaderUtil);
		interactiveShell.loadInterface();

		// THEN
		verify(parkingService, Mockito.times(1)).processExitingVehicle();
		verify(ticketDAO, Mockito.times(1)).getTicket(any(String.class));
		assertEquals(false, interactiveShell.isAppRunning());
	}

}
