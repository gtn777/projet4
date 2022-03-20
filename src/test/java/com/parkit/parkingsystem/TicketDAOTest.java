package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
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
	private final String vehicleRegNumber = "tdaoT";
	private Ticket ticket;

	@BeforeEach
	void setUp() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
		ticketDAO = new TicketDAO();
		ticketDAO.setDataBaseConfig(dataBaseTestConfig);
		ticket = new Ticket();
		ticket.setInTime(new Date(System.currentTimeMillis() - (1000 * 3600 * 24)));
		ticket.setOutTime(new Date(System.currentTimeMillis()));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setPrice(5);
		ticket.setId(1);
	}

	@Order(1)
	@Test
	void processSave_thenGetTicket() {
		// GIVEN
		ticketDAO.saveTicket(ticket);

		// WHEN
		Ticket currentSavedTicket = ticketDAO.getTicket(vehicleRegNumber);

		// THEN
		assertEquals(ticket.getParkingSpot(), currentSavedTicket.getParkingSpot());
	}

	@Order(2)
	@Test
	void processUpdateTicket_thenGetTicket() {
		// GIVEN
		ticketDAO.saveTicket(ticket);
		ticket.setPrice(42);

		// WHEN
		boolean result = ticketDAO.updateTicket(ticket);
		Ticket currentSavedTicket = ticketDAO.getTicket(vehicleRegNumber);

		// THEN
		assertTrue(result);
		assertEquals(42, currentSavedTicket.getPrice());
	}

	@Order(3)
	@Test
	void processIsUserRecurrent_notRecurrentUser() {
		// WHEN
		ticketDAO.saveTicket(ticket);

		// THEN
		assertFalse(ticketDAO.isUserRecurrent(vehicleRegNumber));
	}

	@Order(4)
	@Test
	void processIsUserRecurrent_recurrentUser() {
		// WHEN
		ticketDAO.saveTicket(ticket);
		ticketDAO.saveTicket(ticket);

		// THEN
		assertTrue(ticketDAO.isUserRecurrent(vehicleRegNumber));
	}

	@Order(5)
	@Test
	void processSetDataBaseConfig_thenGetDataBaseConfig() {
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

	@Order(6)
	@Test
	void processGetTicket_withSqlAccessFaulty() {
		// GIVEN
		DataBaseTestConfig faultyDataBaseConfigTest = new DataBaseTestConfig();

		// WHEN
		faultyDataBaseConfigTest.setAbsoluteLocationOfCredentials("resources/parkingsystemExample.properties");
		ticketDAO.setDataBaseConfig(faultyDataBaseConfigTest);

		// THEN
		assertEquals(null, ticketDAO.getTicket(vehicleRegNumber));
		assertThrows(SQLException.class, () -> faultyDataBaseConfigTest.getConnection());
		assertDoesNotThrow(() -> ticketDAO.getTicket(vehicleRegNumber));
	}

	@Order(7)
	@Test
	void processSaveTicket_withSqlAccessFaulty() {
		// GIVEN
		DataBaseTestConfig dbConfigTest = new DataBaseTestConfig();

		// WHEN
		dbConfigTest.setAbsoluteLocationOfCredentials("resources/parkingsystemExample.properties");
		ticketDAO.setDataBaseConfig(dbConfigTest);

		// THEN
		assertFalse(ticketDAO.saveTicket(ticket));
		assertThrows(SQLException.class, () -> dbConfigTest.getConnection());
		assertDoesNotThrow(() -> ticketDAO.saveTicket(ticket));
	}

	@Order(8)
	@Test
	void processUpdateTicket_withSqlAccessFaulty() {
		// GIVEN
		DataBaseTestConfig dbConfigTest = new DataBaseTestConfig();

		// WHEN
		dbConfigTest.setAbsoluteLocationOfCredentials("resources/parkingsystemExample.properties");
		ticketDAO.setDataBaseConfig(dbConfigTest);

		// THEN
		assertFalse(ticketDAO.updateTicket(ticket));
		assertThrows(SQLException.class, () -> dbConfigTest.getConnection());
		assertDoesNotThrow(() -> ticketDAO.updateTicket(ticket));
	}

	@Order(9)
	@Test
	void isUserRecurrent_withSqlAccessFaulty() {
		// GIVEN
		DataBaseTestConfig dbConfigTest = new DataBaseTestConfig();

		// WHEN
		dbConfigTest.setAbsoluteLocationOfCredentials("resources/parkingsystemExample.properties");
		ticketDAO.setDataBaseConfig(dbConfigTest);

		// THEN
		assertFalse(ticketDAO.isUserRecurrent(vehicleRegNumber));
		assertThrows(SQLException.class, () -> dbConfigTest.getConnection());
		assertDoesNotThrow(() -> ticketDAO.isUserRecurrent(vehicleRegNumber));
	}

	@Order(10)
	@Test
	void testIsUserRecurrent_noTicketWithThatRegNumber() {
		// WHEN
		dataBasePrepareService.clearDataBaseEntries();

		// THEN
		assertFalse(ticketDAO.isUserRecurrent(vehicleRegNumber));
	}

}
