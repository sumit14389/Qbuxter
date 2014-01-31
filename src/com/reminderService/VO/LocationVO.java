package com.reminderService.VO;

/**
 * @author Sumit Kumar Maji
 */

public class LocationVO {

	private String success = "";
	private String id = "";
	private String user_id = "";
	private String isActive = "";
	private String duty_hour_start = "";
	private String duty_hour_end = "";
	private String latitude = "";
	private String longitude = "";
	private String location_name="";
	
	private String miles = "";
	public LocationVO() {
	    super();
	    
	}
	public LocationVO(String location_name, String miles,
			String id,String user_id,String isActive,String duty_hour_start,String duty_hour_end,String latitude,String longitude) {
	    super();
	    this.location_name = location_name;
	    this.miles = miles;

	    this.id = id;
	    this.user_id = user_id;
	    this.isActive = isActive;
	    this.duty_hour_start = duty_hour_start;
	    this.duty_hour_end = duty_hour_end;
	    this.latitude = latitude;
	    this.longitude = longitude;

	}
	public String getMiles() {
		return miles;
	}
	public void setMiles(String miles) {
		this.miles = miles;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDuty_hour_start() {
		return duty_hour_start;
	}
	public void setDuty_hour_start(String duty_hour_start) {
		this.duty_hour_start = duty_hour_start;
	}
	public String getDuty_hour_end() {
		return duty_hour_end;
	}
	public void setDuty_hour_end(String duty_hour_end) {
		this.duty_hour_end = duty_hour_end;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLocation_name() {
		return location_name;
	}
	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}



}
