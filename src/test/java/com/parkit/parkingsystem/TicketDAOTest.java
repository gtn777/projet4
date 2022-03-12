package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

@TestMethodOrder(OrderAnnotation.class)
class TicketDAOTest {

	private TicketDAO ticketDAO;
	private final DataBaseConfig dataBaseTestConfig = new DataBaseTestConfig();
	private DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
	private final ParkingSpot parkingSpot = new ParkingSpot(5, ParkingType.BIKE, false);
	private static final String vehicleRegNumber = "tdaoT";
	private Ticket ticket;

	@BeforeEach
	void setUp() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
		ticketDAO = new TicketDAO();
		ticketDAO.setDataBaseConfig(dataBaseTestConfig);
		ticket = new Ticket();
		Date inTime = new Date();
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setPrice(5);
	}

	@Order(1)
	@Test
	void testToSaveAndGetTicket() {
		// WHEN
		ticketDAO.saveTicket(ticket);
		Ticket currentSavedTicket = ticketDAO.getTicket(vehicleRegNumber);

		// THEN
		assertEquals(ticket.getParkingSpot(), currentSavedTicket.getParkingSpot());
	}

	@Order(2)
	@Test
	void testToUpdateSavedTicketAndGetIt() {
		// GIVEN
		ticketDAO.saveTicket(ticket);
		ticket.setPrice(42);
		ticket.setOutTime(new Date(System.currentTimeMillis()));
		ticket.setId(1);

		// WHEN
		ticketDAO.updateTicket(ticket);

		// THEN
		assertEquals(42, ticketDAO.getTicket(vehicleRegNumber).getPrice());
	}

	@Order(3)
	@Test
	void testIsUserEverEntered_unknownUser() {
		// WHEN
		ticketDAO.saveTicket(ticket);

		// THEN
		assertFalse(ticketDAO.isUserEverEntered(vehicleRegNumber));
	}

	@Order(4)
	@Test
	void testIsUserEverEntered_knownUser() {
		// WHEN
		ticketDAO.saveTicket(ticket);
		ticketDAO.saveTicket(ticket);

		// THEN
		assertTrue(ticketDAO.isUserEverEntered(vehicleRegNumber));
	}

	@Order(5)
	@Test
	void testSetDataBaseConfig() {
		// GIVEN
		ticketDAO = new TicketDAO();
		String initialDataBaseConfig = "";
		String currentDataBaseConfig = "";

		// WHEN
		initialDataBaseConfig = ticketDAO.getDataBaseConfig().getClass().toString();
		ticketDAO.setDataBaseConfig(dataBaseTestConfig);
		currentDataBaseConfig = ticketDAO.getDataBaseConfig().getClass().toString();

		// THEN
		assertNotEquals(initialDataBaseConfig, currentDataBaseConfig);

	}

}
