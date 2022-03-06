package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
import org.mockito.Mockito;
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

	private static ParkingService parkingService;
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static Connection con;
	private static PreparedStatement ps;
	private static ResultSet rs;
	private static final DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
	private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static final String vehicleRegNumber = "tdaoT";

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
		ticketDAO = new TicketDAO();
		ticketDAO.setDataBaseConfig(dataBaseTestConfig);
		con = null;
		ps = null;
		rs = null;
		parkingService = null;
	}

	@AfterEach
	private void cleanConnection() {
		dataBaseTestConfig.closeResultSet(rs);
		dataBaseTestConfig.closePreparedStatement(ps);
		dataBaseTestConfig.closeConnection(con);
	}

	@AfterAll
	private static void tearDown() {
		dataBasePrepareService.clearDataBaseEntries();
	}

	@Order(1)
	@Test
	public void testParkingABike() throws Exception {
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(2);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		// WHEN
		int savedUnavailableParkingNumber = -1;
		con = dataBaseTestConfig.getConnection();
		ps = con.prepareStatement("SELECT * FROM parking WHERE AVAILABLE = 0");
		rs = ps.executeQuery();
		if (rs.next() && rs.isLast()) {
			savedUnavailableParkingNumber = rs.getInt(1);
		} else {
			System.out.println("not available parking spot multiple or no entry, ParkingDataBaseIT.testParkingACAR \n");
		}

		// THEN
		verify(inputReaderUtil, Mockito.times(1)).readSelection();
		verify(inputReaderUtil, Mockito.times(1)).readVehicleRegistrationNumber();
		assertEquals(4, savedUnavailableParkingNumber); // check if first bike spot is not available
	}

	@Order(2)
	@Test
	public void testParkingLotExit() throws Exception {
		// GIVEN
		testParkingABike();
		Thread.sleep(400);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();

		// WHEN
		int availableParkingSlotQuantity = -1;
		con = dataBaseTestConfig.getConnection();
		ps = con.prepareStatement("SELECT COUNT(*) FROM parking WHERE AVAILABLE = 1");
		rs = ps.executeQuery();
		if (rs.next()) {
			availableParkingSlotQuantity = rs.getInt(1);
		} else {
			System.out.println("table parking multiple or no entry, ParkingDataBaseIT.testParkingLotExit \n");
		}

		// THEN
		verify(inputReaderUtil, Mockito.times(1)).readSelection();
		verify(inputReaderUtil, Mockito.times(2)).readVehicleRegistrationNumber();
		assertEquals(5, availableParkingSlotQuantity);// Check if all parking slot are available after exiting vehicle;
	}

}
