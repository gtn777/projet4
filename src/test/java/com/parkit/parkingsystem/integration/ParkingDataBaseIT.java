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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

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
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("pdbIT");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterEach
	private void cleanDatabase() {
//		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {
//		dataBasePrepareService.clearDataBaseEntries();
	}

	@Test
	public void testParkingACar() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		// TODO: check that a ticket is actually saved in DB and Parking table is
		// updated with availability
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

		assertEquals(1, savedUnavailableParkingNumber);
	}

	@Test
	public void testParkingLotExit() {
		testParkingACar();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		// TODO: check that the fare generated and out time are populated correctly in
		// the database

		String savedRegNumber = "";
		try {
			Connection con = dataBaseTestConfig.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM ticket WHERE PRICE > 0 AND OUT_TIME > 0	");
			ResultSet rs = ps.executeQuery();
			if (rs.next() && rs.isLast()) {
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

		assertEquals("pdbIT", savedRegNumber);

	}

}
