package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static ParkingService parkingService = null;
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static Connection con;
	private static PreparedStatement ps;
	private static ResultSet rs;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		dataBasePrepareService = new DataBasePrepareService();
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("pdbIT");
		con = null;
		ps = null;
		rs = null;
	}

	@AfterEach
	private void cleanDatabase() {
		dataBaseTestConfig.closeResultSet(rs);
		dataBaseTestConfig.closePreparedStatement(ps);
		dataBaseTestConfig.closeConnection(con);
	}

	@AfterAll
	private static void tearDown() {
	}

	@Order(1)
	@Test
	public void testParkingACar() throws ClassNotFoundException, SQLException {
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		// WHEN
		int savedUnavailableParkingNumber = -1;
		con = dataBaseTestConfig.getConnection();
		ps = con.prepareStatement("SELECT * FROM parking WHERE AVAILABLE = 0");
		rs = ps.executeQuery();
		// Checks if result contains only 1 row
		if (rs.next() && rs.isLast()) {
			savedUnavailableParkingNumber = rs.getInt(1);
		} else {
			System.out.println("table parking multiple or no entry, ParkingDataBaseIT.testParkingACAR \n");
		}
		// THEN
		// Checks if there is only one occupied parking slot
		// and that it has the identification number 1.
		assertEquals(1, savedUnavailableParkingNumber);
	}

	@Order(2)
	@Test
	public void testParkingLotExit() throws ClassNotFoundException, SQLException, InterruptedException {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		// WHEN
		int availableParkingSlotQuantity = -1;
		con = dataBaseTestConfig.getConnection();
		ps = con.prepareStatement("SELECT COUNT(*) FROM parking WHERE AVAILABLE = 1");
		rs = ps.executeQuery();
		System.out.println(rs);
		if (rs.next()) {
			availableParkingSlotQuantity = rs.getInt(1);
		} else {
			System.out.println("table parking multiple or no entry, ParkingDataBaseIT.testParkingLotExit \n");
		}

		// THEN
		// Check if all parking slot are available
		assertEquals(5, availableParkingSlotQuantity);
	}

}
