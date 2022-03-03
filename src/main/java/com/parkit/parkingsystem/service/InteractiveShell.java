package com.parkit.parkingsystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class InteractiveShell {

	private final Logger logger = LogManager.getLogger("InteractiveShell");
	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;
	private ParkingService parkingService;
	private boolean continueApp = true;

	// Constructor for App.java
	public InteractiveShell() {
		this.inputReaderUtil = new InputReaderUtil();
		this.parkingSpotDAO = new ParkingSpotDAO();
		this.ticketDAO = new TicketDAO();
		this.parkingService = new ParkingService(this.inputReaderUtil, this.parkingSpotDAO, this.ticketDAO);
	}

	// Constructor for InteractiveShellTest
	public InteractiveShell(ParkingService psv, InputReaderUtil iru, ParkingSpotDAO psdao, TicketDAO tdao) {
		this.inputReaderUtil = iru;
		this.parkingService = psv;
		this.parkingSpotDAO = psdao;
		this.ticketDAO = tdao;
	}

	public void loadInterface() {
		logger.info("App initialized!!!");
		System.out.println("Welcome to Parking System!");
		while (this.continueApp) {
			loadMenu();
			int option = this.inputReaderUtil.readSelection();
			switch (option) {
			case 1: {
				this.parkingService.processIncomingVehicle();
				break;
			}
			case 2: {
				this.parkingService.processExitingVehicle();
				break;
			}
			case 3: {
				System.out.println("Exiting from the system!");
				this.continueApp = false;
				break;
			}
			default:
				System.out.println("Unsupported option. Please enter a number corresponding to the provided menu");
			}
		}
	}

	private void loadMenu() {
		System.out.println("Please select an option. Simply enter the number to choose an action");
		System.out.println("1 New Vehicle Entering - Allocate Parking Space");
		System.out.println("2 Vehicle Exiting - Generate Ticket Price");
		System.out.println("3 Shutdown System");
	}

	public Boolean isAppRunning() {
		return this.continueApp;
	}

}
