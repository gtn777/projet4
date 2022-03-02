package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	public void setProdDatabaseProtection() {
		when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		when(ticketDAO.getTicket(any(String.class))).thenReturn(null);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
	}

	@Test
	public void startAndExitApplicationTest() {
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(3);

		// WHEN
		interactiveShell = new InteractiveShell(parkingService, inputReaderUtil);
		Boolean isAppstarted = interactiveShell.isAppRunning();
		interactiveShell.loadInterface();

		// THEN
		assertEquals(true, isAppstarted);
		assertEquals(false, interactiveShell.isAppRunning());
		verify(inputReaderUtil, Mockito.times(1)).readSelection();
	}

}
