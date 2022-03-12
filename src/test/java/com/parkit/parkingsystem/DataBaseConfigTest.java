package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;

class DataBaseConfigTest {

	private DataBaseConfig dataBaseConfig = new DataBaseConfig();
	private Connection con;
	private PreparedStatement ps;
	private String statement;
	private ResultSet rs;

	@BeforeEach
	void setUp() throws Exception {
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
		Boolean isResultSetClosed, isPrepStatementClosed, isConnectionClosed;
		statement = DBConstants.GET_PARKING_SPOT_QUANTITY;

		// WHEN
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
