package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.DiscountService;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

	private static DiscountService discountService;
	private static Ticket ticket;
	private static final String VEHICLE_REG = "dsT";
	
	@Mock
	private static TicketDAO ticketDAO;
	
	@BeforeAll
	private static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	private static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	private void setUp() throws Exception {
		
//		when(ticketDAO.saveTicket(anyTicket)).thenReturn(false);
//		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
		discountService = new DiscountService();
		ticket = new Ticket();
		ticket.setPrice(-10);
		ticket.setVehicleRegNumber(VEHICLE_REG);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void unknownUser29MinutesDiscountTest() {
		// GIVEN
		long duration = 1740000;
		discountService.setTicketDAO(ticketDAO);

		// WHEN
		duration = discountService.applyDiscount(ticket, duration);

		// THEN
		verify(ticketDAO, Mockito.times(0)).isUserEverEnteredAndExit(any(String.class));
		assertEquals(0, duration);
	}

	@Test
	void unknownUser31MinutesDiscountTest() {
		// GIVEN
		when(ticketDAO.isUserEverEnteredAndExit(VEHICLE_REG)).thenReturn(false);
		discountService.setTicketDAO(ticketDAO);
		long duration = 1860000;

		// WHEN
		duration = discountService.applyDiscount(ticket, duration);

		// THEN
		verify(ticketDAO, Mockito.times(1)).isUserEverEnteredAndExit(any(String.class));
		assertEquals(1860000, duration);
	}

	@Test
	void unknownUser10hoursDiscountTest() {
		// GIVEN
		when(ticketDAO.isUserEverEnteredAndExit(VEHICLE_REG)).thenReturn(false);
		discountService.setTicketDAO(ticketDAO);
		long duration = 36000000;

		// WHEN
		duration = discountService.applyDiscount(ticket, duration);

		// THEN
		verify(ticketDAO, Mockito.times(1)).isUserEverEnteredAndExit(any(String.class));
		assertEquals(36000000, duration);
	}

	@Test
	void knownUser29minutesDiscountTest() {
		// GIVEN
		discountService.setTicketDAO(ticketDAO);
		long duration = 1740000;

		// WHEN
		duration = discountService.applyDiscount(ticket, duration);

		// THEN
		verify(ticketDAO, Mockito.times(0)).isUserEverEnteredAndExit(any(String.class));
		assertEquals(0, duration);
	}

	@Test
	void knownUser10HoursDiscountTest() {
		// GIVEN
		when(ticketDAO.isUserEverEnteredAndExit(VEHICLE_REG)).thenReturn(true);
		discountService.setTicketDAO(ticketDAO);
		long duration = 36000000;

		// WHEN
		duration = discountService.applyDiscount(ticket, duration);

		// THEN
		verify(ticketDAO, Mockito.times(1)).isUserEverEnteredAndExit(any(String.class));
		assertEquals(34285714, duration);
	}
	
}
