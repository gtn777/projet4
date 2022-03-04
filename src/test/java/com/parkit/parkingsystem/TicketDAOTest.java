package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

class TicketDAOTest {

	private static TicketDAO ticketDAO;
	private static final DataBaseConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static DataBasePrepareService dataBasePrepareService;
	private static final ParkingSpot parkingSpot = new ParkingSpot(5, ParkingType.BIKE, false);
	private static final String vehicleRegNumber = "tdaoT";
	private static Ticket ticket;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		dataBasePrepareService = new DataBasePrepareService();
		dataBasePrepareService.clearDataBaseEntries();
		ticket = new Ticket();
	}

	@BeforeEach
	void setUp() throws Exception {
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testToSaveAndGetTicket() {
		// GIVEN
		Date inTime = new Date();
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber(vehicleRegNumber);

		// WHEN
		ticketDAO.saveTicket(ticket);
		Ticket currentSavedTicket = ticketDAO.getTicket(vehicleRegNumber);

		// THEN
		assertEquals(ticket.getParkingSpot(), currentSavedTicket.getParkingSpot());
	}

	@Test
	void testToUpdateSavedTicketAndGetIt() {
		// GIVEN
		ticket.setPrice(42);
		ticket.setOutTime(new Date(System.currentTimeMillis()));
		ticket.setId(1);
		Ticket currentSavedTicket = null;

		// WHEN
		ticketDAO.updateTicket(ticket);
		currentSavedTicket = ticketDAO.getTicket(vehicleRegNumber);

		// THEN
		assertEquals(ticket.getPrice(), currentSavedTicket.getPrice());
	}
	
}
