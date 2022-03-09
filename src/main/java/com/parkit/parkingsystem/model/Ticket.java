package com.parkit.parkingsystem.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class Ticket {
	private int id;
	private ParkingSpot parkingSpot;
	private String vehicleRegNumber;
	private double price;
	private Date inTime;
	private Date outTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParkingSpot getParkingSpot() {
		return parkingSpot;
	}

	public void setParkingSpot(ParkingSpot parkingSpot) {
		this.parkingSpot = parkingSpot;
	}

	public String getVehicleRegNumber() {
		String returnValue = this.vehicleRegNumber;
		return returnValue;
	}

	public void setVehicleRegNumber(String vehicleRegNumber) {
		this.vehicleRegNumber = vehicleRegNumber;
	}

	public double getPrice() {
		double returnValue = this.price;
		return returnValue;
	}

	public void setPrice(double price) {
		this.price = BigDecimal.valueOf(price).setScale(3, RoundingMode.HALF_UP).doubleValue();
	}

	public Date getInTime() {
		Date returnValue = this.inTime;
		return returnValue;
	}

	public void setInTime(Date inTime) {
		this.inTime = new Date(inTime.getTime());
	}

	public Date getOutTime() {
		Date returnValue = this.outTime;
		return returnValue;
	}

	public void setOutTime(Date outTime) {
		if (outTime == null) {
			this.outTime = null;
		} else {
			this.outTime = new Date(outTime.getTime());
		}
		
	}
}
