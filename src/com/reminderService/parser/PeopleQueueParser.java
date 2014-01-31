package com.reminderService.parser;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.reminderService.VO.PeopleQueueVO;

public class PeopleQueueParser {

	String jSonData;
	private PeopleQueueVO peopleQueueVO;
	public PeopleQueueParser(String data)
	{
		this.jSonData = data;
	}

	public ArrayList<PeopleQueueVO> parse()
	{
		
		ArrayList<PeopleQueueVO> peopleArray = new ArrayList<PeopleQueueVO>();
		
		try {
			JSONObject jObject = new JSONObject(jSonData);

			JSONObject success = jObject.getJSONObject("response");

		
			

			String isSucessCheck=success.getString("message");

			if(isSucessCheck.equalsIgnoreCase("Search Successful"))
			{

				String processDataArray = success.getString("result");

				
					peopleQueueVO = new PeopleQueueVO();
				
					peopleQueueVO.setQueue(processDataArray);

					peopleArray.add(peopleQueueVO);
			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return peopleArray;
	}
}
