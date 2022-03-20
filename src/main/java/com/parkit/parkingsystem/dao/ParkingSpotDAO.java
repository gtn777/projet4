package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAO {
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

	private DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public int getNextAvailableSlot(ParkingType parkingType) throws NullPointerException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = -1;

		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
			ps.setString(1, parkingType.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			return result;
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception", e.getMessage());
			return result;
		} catch (SQLException e) {
			logger.error("Error fetching next available slot", e.getMessage());
			return result;
		} finally {
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
	}

	public boolean updateParking(ParkingSpot parkingSpot) {
		// update the availability for that parking slot
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
			ps.setBoolean(1, parkingSpot.isAvailable());
			ps.setInt(2, parkingSpot.getId());
			int updateRowCount = ps.executeUpdate();
			return (updateRowCount == 1);
		} catch (ClassNotFoundException ex) {
			logger.error("Error updating parking info" + ex.getMessage());
			return false;
		} catch (SQLException ex) {
			logger.error("Error updating parking info" + ex.getMessage());
			return false;
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
	}

	public boolean setDataBaseConfig(DataBaseConfig dbc) {
		this.dataBaseConfig = dbc;
		return this.dataBaseConfig == dbc ? true : false;
	}

	public DataBaseConfig getDataBaseConfig() {
		return this.dataBaseConfig;
	}

}
