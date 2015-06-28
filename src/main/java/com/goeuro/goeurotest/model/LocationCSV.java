package com.goeuro.goeurotest.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "type", "latitude", "longitude"})
public class LocationCSV {

	private String id;
	private String name;
	private String type;
	
	private double latitude;
	private double longitude;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public LocationCSV(Location location) {
		this.id = location.getId();
		this.name = location.getName();
		this.type = location.getType();
		this.latitude = location.getGeoPosition().getLatitude();
		this.longitude = location.getGeoPosition().getLongitude();
	}
	
	
}
