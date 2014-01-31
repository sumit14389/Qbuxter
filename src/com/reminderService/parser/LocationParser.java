package com.reminderService.parser;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.reminderService.VO.LocationVO;

public class LocationParser {

	String jSonData;
	private LocationVO locationVO;
	public LocationParser(String data)
	{
		this.jSonData = data;
	}

	public ArrayList<LocationVO> parse()
	{
		
		ArrayList<LocationVO> locationArray = new ArrayList<LocationVO>();
		
		try {
			JSONObject jObject = new JSONObject(jSonData);

			JSONObject response = jObject.getJSONObject("response");

			//String milesExtent=success.getString("miles");
			

			String isSucessCheck=response.getString("message");

			if(isSucessCheck.equalsIgnoreCase("Search Successful"))
			{

				JSONArray processDataArray = response.getJSONArray("result");

				int len = processDataArray.length();


				for (int i = 0; i < len; i++) {
					
					locationVO = new LocationVO();

					JSONObject resultArray = processDataArray.getJSONObject(i);
					String id = resultArray.getString("id");
					locationVO.setId(id);

					String user_id = resultArray.getString("user_id");
					locationVO.setUser_id(user_id);
					
					String location_name = resultArray.getString("location_name");
					locationVO.setLocation_name(location_name);

					String latitude = resultArray.getString("latitude");
					locationVO.setLatitude(latitude);

					String longitude = resultArray.getString("longitude");
					locationVO.setLongitude(longitude);

					String duty_hour_start = resultArray.getString("duty_hour_start");
					locationVO.setDuty_hour_start(duty_hour_start);

					String duty_hour_end = resultArray.getString("duty_hour_end");
					locationVO.setDuty_hour_end(duty_hour_end);
					
					String active = resultArray.getString("active");
					locationVO.setIsActive(active);
					
					String miles = resultArray.getString("miles");
					locationVO.setMiles(miles);
					
					locationArray.add(locationVO);
				}
			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return locationArray;
	}
}
