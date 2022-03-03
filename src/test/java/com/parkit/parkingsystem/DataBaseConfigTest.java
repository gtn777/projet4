package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;

class DataBaseConfigTest {

	private static DataBaseConfig dataBaseConfig;
	private static Connection con;
	private static PreparedStatement ps;
	private static String statement;
	private static ResultSet rs;

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
		ps = null;
		rs = null;
		statement = null;
	}

	@Test
	void userAccessToSqlDatabaseTest() {
		// GIVEN
		Boolean isConnected = false;
		Boolean isConnectionClosed = false;
		// WHEN
		try {
			con = dataBaseConfig.getConnection();
			isConnected = con.isValid(10);
			dataBaseConfig.closeConnection(con);
			isConnectionClosed = !con.isValid(10);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Access to database impossible, check user access or database existing");
		}

		// THEN
		assertInstanceOf(Connection.class, con);
		assertEquals(true, isConnected);
		assertEquals(true, isConnectionClosed);
	}

	@Test
	void sqlRequestStatementTest() throws SQLException, ClassNotFoundException {

		// GIVEN
		int requestResult = -1;
		Boolean isResultSetClosed, isPrepStatementClosed, isConnectionClosed = false;
		statement = DBConstants.GET_PARKING_SPOT_QUANTITY;

		// WHEN
		dataBaseConfig.closeResultSet(rs);
		dataBaseConfig.closePreparedStatement(ps);
		dataBaseConfig.closeConnection((con));
		con = dataBaseConfig.getConnection();
		ps = con.prepareStatement(statement);
		rs = ps.executeQuery();
		if (rs.next())
			requestResult = rs.getInt(1);
		dataBaseConfig.closeResultSet(rs);
		isResultSetClosed = rs.isClosed();
		dataBaseConfig.closePreparedStatement(ps);
		isPrepStatementClosed = ps.isClosed();
		dataBaseConfig.closeConnection(con);
		isConnectionClosed = con.isClosed();

		// THEN
		assertEquals(5, requestResult);
		assertTrue(isResultSetClosed);
		assertTrue(isPrepStatementClosed);
		assertTrue(isConnectionClosed);
	}

}
