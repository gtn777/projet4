package com.parkit.parkingsystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.service.InteractiveShell;

public class App {
	private static final Logger logger = LogManager.getLogger("App");

	public static void main(String args[]) {
		System.setProperty("prop.dir", "/home/gpi/projet4/parkingsystem/");
		logger.info("Initializing Parking System");
		InteractiveShell interactiveShell = new InteractiveShell();
		interactiveShell.loadInterface();
	}
}
