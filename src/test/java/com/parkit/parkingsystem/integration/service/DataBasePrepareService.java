package com.parkit.parkingsystem.integration.service;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

public class DataBasePrepareService {

	DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

	public void clearDataBaseEntries() {

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dataBaseTestConfig.getConnection();

			// set parking entries to available
			ps = connection.prepareStatement("update parking set available = true");
			ps.execute();
			ps.close();

			// clear ticket entries;
			ps = connection.prepareStatement("truncate table ticket");
			ps.execute();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dataBaseTestConfig.closePreparedStatement(ps);
			dataBaseTestConfig.closeConnection(connection);
		}
	}

}
