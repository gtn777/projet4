package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;

class DataBaseConfigTest {

	private static DataBaseConfig dataBaseConfig;
	private static final Logger logger = LogManager.getLogger("DataBaseConfig");
	private static Connection con;
	private static PreparedStatement ps;
	private static String statement;
	private static ResultSet rs;
	private static Boolean isConnectionClosed;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		dataBaseConfig = new DataBaseConfig();
		con = null;
		ps=null;
		rs=null;
		statement = null;
		isConnectionClosed = null;
	}


	@Test
	void userAccessToSqlDatabaseTest() {
		// GIVEN
		Boolean isConnected = null;

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

	@Test
	void sqlRequestStatementTest() throws SQLException, ClassNotFoundException {

		// GIVEN
		int result = -1;
		Boolean isResultSetClosed, isPrepStatementClosed = null;
		statement = DBConstants.GET_PARKING_SPOT_QUANTITY;

		// WHEN
		con = dataBaseConfig.getConnection();
		ps = con.prepareStatement(statement);
		rs = ps.executeQuery();
		if (rs.next())
			result = rs.getInt(1);
		dataBaseConfig.closeResultSet(rs);
		isResultSetClosed = rs.isClosed();
		dataBaseConfig.closePreparedStatement(ps);
		isPrepStatementClosed = ps.isClosed();
		dataBaseConfig.closeConnection(con);
		isConnectionClosed = con.isClosed();

		// THEN
		assertEquals(5, result);
		assertTrue(isResultSetClosed);
		assertTrue(isPrepStatementClosed);
		assertTrue(isConnectionClosed);
		
	}

}
