package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.config.DataBaseConfig;

class DataBaseConfigTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	private static final Logger logger = LogManager.getLogger("DataBaseConfig");

	@Test
	void userAccessToSqlDatabaseTest(){
		// GIVEN
		DataBaseConfig dataBaseConfig = new DataBaseConfig();
		Connection con = null;
		Boolean isConnected = null;
		Boolean isConnectionClosed = null;
		// WHEN
		try {
			con = dataBaseConfig.getConnection();
			isConnected = con.isValid(500);
			dataBaseConfig.closeConnection(con);
			isConnectionClosed = !con.isValid(500);
		} catch (ClassNotFoundException | SQLException e) {
			logger.error(e.getMessage());
			logger.error("Access to database impossible, check user access or database existing");
		}
		
		// THEN
		assertInstanceOf(Connection.class, con);
		assertEquals(true, isConnected);	
		assertEquals(true, isConnectionClosed);
	}
	
	@Disabled
	@Test
	void sqlRequestStatementTest() {
		fail("Not yet implemented");
	}

	@Disabled
	@Test
	void test3() {
		fail("Not yet implemented");
	}

	@Disabled
	@Test
	void test4() {
		fail("Not yet implemented");
	}
}
