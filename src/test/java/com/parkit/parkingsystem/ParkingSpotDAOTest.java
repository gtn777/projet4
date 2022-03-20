package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
	private DataBaseConfig dataBaseTestConfig;

	@BeforeEach
	void setUp() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
		parkingSpotDAO = new ParkingSpotDAO();
		dataBaseTestConfig = new DataBaseTestConfig();
		parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
	}

	@Test
	void processSetDataBaseConfig_andGetIt() {
		// GIVEN
		parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);

		// WHEN
		DataBaseConfig settedDBConfig = parkingSpotDAO.getDataBaseConfig();

		// THEN
		assertEquals(dataBaseTestConfig, settedDBConfig);
	}

	@Test
	void processGetNextAvailableSpot_parkingTypeBike() throws Exception {
		// WHEN
		int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);

		// THEN
		assertEquals(4, result);
	}

	@Test
	void processGetNextAvailableSpot_parkingTypeUnknown() {
		// GIVEN
		int result = -1;

		// WHEN
		result = parkingSpotDAO.getNextAvailableSlot(ParkingType.TEST);

		// THEN
		assertEquals(0, result);
	}

	@Test
	void processUpdateParking_firstSpotCarNotAvailable() throws Exception {
		// GIVEN
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		// WHEN
		boolean result = parkingSpotDAO.updateParking(parkingSpot);

		// THEN
		assertEquals(true, result);
		assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
	}

	@Test
	void processUpdateParking_unknownParkingSpot() {
		// GIVEN
		parkingSpot = new ParkingSpot(6, ParkingType.CAR, false);

		// WHEN
		boolean result = parkingSpotDAO.updateParking(parkingSpot);

		// THEN
		assertEquals(false, result);
		assertDoesNotThrow(() -> parkingSpotDAO.updateParking(parkingSpot));
	}

	@Test
	void processGetNextAvailableSlot_withSqlAccessFaulty() {
		// GIVEN
		DataBaseTestConfig dbConfigTest = new DataBaseTestConfig();

		// WHEN
		dbConfigTest.setAbsoluteLocationOfCredentials("resources/parkingsystemExample.properties");
		parkingSpotDAO.setDataBaseConfig(dbConfigTest);

		// THEN
		assertEquals(-1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
		assertThrows(SQLException.class, () -> dbConfigTest.getConnection());
		assertDoesNotThrow(() -> parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
	}

	@Test
	void processUpdateParking_withSqlAccessFaulty() {
		// GIVEN
		DataBaseTestConfig dbConfigTest = new DataBaseTestConfig();

		// WHEN
		dbConfigTest.setAbsoluteLocationOfCredentials("resources/parkingsystemExample.properties");
		parkingSpotDAO.setDataBaseConfig(dbConfigTest);

		// THEN
		assertFalse(parkingSpotDAO.updateParking(parkingSpot));
		assertThrows(SQLException.class, () -> dbConfigTest.getConnection());
		assertDoesNotThrow(() -> parkingSpotDAO.updateParking(parkingSpot));
	}
}
