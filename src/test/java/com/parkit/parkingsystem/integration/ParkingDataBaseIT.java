package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
		dataBasePrepareService.clearDataBaseEntries();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("pdbIT");
	}

	@AfterEach
	private void cleanDatabase() {
	}

	@AfterAll
	private static void tearDown() {
		dataBasePrepareService.clearDataBaseEntries();
	}

	@Order(1)
	@Test
	public void testParkingACar() {
		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		
		// WHEN
		int savedUnavailableParkingNumber = -1;
		try {
			Connection con = dataBaseTestConfig.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM parking WHERE AVAILABLE = 0");
			ResultSet rs = ps.executeQuery();
			if (rs.next() && rs.isLast()) {
				savedUnavailableParkingNumber = rs.getInt(1);
			} else {
				System.out.println("table parking multiple or no entry, ParkingDataBaseIT.testParkingACAR \n");
			}
			dataBaseTestConfig.closeResultSet(rs);
			dataBaseTestConfig.closePreparedStatement(ps);
			dataBaseTestConfig.closeConnection(con);
		} catch (Exception e) {
			System.out.println("Error fetching parking table, ParkingDataBaseIT.testParkingCar \n" + e);
		}
		// THEN
		assertEquals(1, savedUnavailableParkingNumber);
	}

	@Order(2)
	@Test
	public void testParkingLotExit() {
		// GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		
		// WHEN
		String savedRegNumber = "";
		try {
			Connection con = dataBaseTestConfig.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM ticket WHERE PRICE > 0 AND OUT_TIME > 0	");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				savedRegNumber = rs.getString("VEHICLE_REG_NUMBER");
			} else {
				System.out.println("table ticket multiple or no entry, ParkingDataBaseIT.testParkingLotExit \n");
			}
			dataBaseTestConfig.closeResultSet(rs);
			dataBaseTestConfig.closePreparedStatement(ps);
			dataBaseTestConfig.closeConnection(con);
		} catch (Exception e) {
			System.out.println("Error fetching parking ticket, ParkingDataBaseIT.testParkingLotExit \n" + e);
		}

		// THEN
		assertEquals("pdbIT", savedRegNumber);

	}

}
