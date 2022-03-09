package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;

@ExtendWith(MockitoExtension.class)
class ParkingSpotDAOTest {

	private ParkingSpotDAO parkingSpotDAO;
	private DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
	private ParkingSpot parkingSpot;
	private DataBaseConfig dataBaseConfig = new DataBaseTestConfig();

	@Mock
	private DataBaseConfig dataBaseConfigMock = new DataBaseTestConfig();

	@BeforeEach
	void setUp() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.setDataBaseConfig(dataBaseConfig);
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void processSetDataBaseConfig_andGetIt() {
		// GIVEN
		parkingSpotDAO.setDataBaseConfig(dataBaseConfigMock);

		// WHEN
		DataBaseConfig settedDBConfig = parkingSpotDAO.getDataBaseConfig();

		// THEN
		assertEquals(dataBaseConfigMock, settedDBConfig);
	}

	@Test
	void processGetNextAvailableSpot_Bike() throws Exception {
		// WHEN
		int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);

		// THEN
		assertEquals(4, result);
	}

	@Test
	void processGetNextAvailableSpot_connectionFailed() throws Exception {
		// GIVEN
		when(dataBaseConfigMock.getConnection()).thenReturn(null);
		parkingSpotDAO.setDataBaseConfig(dataBaseConfigMock);

		// WHEN
		int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// THEN
		assertDoesNotThrow(() -> parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
		assertEquals(-1, result);
	}

	@Test
	void processGetNextAvailableSpot_vehicleTypeUnknown() {
		// WHEN
		int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.TEST);

		// THEN
		assertEquals(0, result);
	}

	@Test
	void processupdateParking() throws Exception {
		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		// WHEN
		boolean result = parkingSpotDAO.updateParking(parkingSpot);

		// THEN
		assertEquals(true, result);
		assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
	}

	@Test
	void processupdateParking_connectionFailed() throws ClassNotFoundException, SQLException {
		// GIVEN
		when(dataBaseConfigMock.getConnection()).thenReturn(null);
		parkingSpotDAO.setDataBaseConfig(dataBaseConfigMock);
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		// WHEN
		boolean result = parkingSpotDAO.updateParking(parkingSpot);

		// THEN
		assertEquals(false, result);
		assertDoesNotThrow(() -> parkingSpotDAO.updateParking(parkingSpot));
	}

	@Test
	void processupdateParking_unknownParkingSpot() {
		// GIVEN
		parkingSpot = new ParkingSpot(6, ParkingType.CAR, false);

		// WHEN
		boolean result = parkingSpotDAO.updateParking(parkingSpot);

		// THEN
		assertEquals(false, result);
	}

}
