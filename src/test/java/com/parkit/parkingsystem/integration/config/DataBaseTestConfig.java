package com.parkit.parkingsystem.integration.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;

public class DataBaseTestConfig extends DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");
	private Properties properties = new Properties();
	private String user = null, pass = null, url = null, driver = null;
	private String absoluteLocationOfCredentials = "resources/parkingsystem.properties";
	private FileInputStream fileInputStream = null;

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		logger.info("Trying to retrieve credentials from file located in : \n" + System.getProperty("prop.dir") + "/"
				+ absoluteLocationOfCredentials);
		try {
			fileInputStream = new FileInputStream(absoluteLocationOfCredentials);
		} catch (FileNotFoundException e) {
			System.out.println("file resources/parkingsystem.properties not found");
			e.printStackTrace();
		}
		try {
			properties.load(fileInputStream);
		} catch (IOException e) {
			System.out.println("failed to read properties in resources/parkingsystem.properties");
			e.printStackTrace();
		}
		user = properties.getProperty("username");
		pass = properties.getProperty("password");
		url = properties.getProperty("urlForTest");
		driver = properties.getProperty("driver");
		logger.info("Create DB connection");
		Class.forName(driver);
		return DriverManager.getConnection(url, user, pass);
	}

	public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
				logger.info("Closing DB connection");
			} catch (SQLException e) {
				logger.error("Error while closing connection", e);
			}
		}
	}

	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
				logger.info("Closing Prepared Statement");
			} catch (SQLException e) {
				logger.error("Error while closing prepared statement", e);
			}
		}
	}

	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				logger.info("Closing Result Set");
			} catch (SQLException e) {
				logger.error("Error while closing result set", e);
			}
		}
	}

	public void setAbsoluteLocationOfCredentials(String location) {
		this.absoluteLocationOfCredentials = location;
	}

	public String getAbsoluteLocationOfCredentials() {
		return this.absoluteLocationOfCredentials;
	}
}
