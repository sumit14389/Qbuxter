package com.reminderService.parser;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.reminderService.VO.TicketWaitingVO;

public class TicketWaitingParser {

	String jSonData;
	private TicketWaitingVO ticketWaitingVO;
	public TicketWaitingParser(String data)
	{
		this.jSonData = data;
	}

	public ArrayList<TicketWaitingVO> parse()
	{
		
		ArrayList<TicketWaitingVO> ticketWaitingArray = new ArrayList<TicketWaitingVO>();
		
		try {
			JSONObject jObject = new JSONObject(jSonData);

			JSONObject success = jObject.getJSONObject("response");

			
			

			String isSucessCheck=success.getString("message");

			if(isSucessCheck.equalsIgnoreCase("Search Successful"))
			{

				String processData = success.getString("result");
				
					
				ticketWaitingVO = new TicketWaitingVO();
				
				ticketWaitingVO.setUpdatedQueue(processData);

					
					ticketWaitingArray.add(ticketWaitingVO);
			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return ticketWaitingArray;
	}
}
