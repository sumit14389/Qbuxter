package com.reminderService.parser;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.reminderService.VO.TakeTicketVO;

public class TakeTicketParser {

	String jSonData;
	private TakeTicketVO takeTicketVO;
	public TakeTicketParser(String data)
	{
		this.jSonData = data;
	}

	public ArrayList<TakeTicketVO> parse()
	{
		
		ArrayList<TakeTicketVO> ticketArray = new ArrayList<TakeTicketVO>();
		
		try {
			JSONObject jObject = new JSONObject(jSonData);

			JSONObject success = jObject.getJSONObject("response");

			
			

			String isSucessCheck=success.getString("message");

			if(isSucessCheck.equalsIgnoreCase("Search Successful"))
			{

				JSONArray processDataArray = success.getJSONArray("result");

				int len = processDataArray.length();


				for (int i = 0; i < len; i++) {
					
					takeTicketVO = new TakeTicketVO();

					JSONObject resultArray = processDataArray.getJSONObject(i);
					String ticket_id = resultArray.getString("ticket_id");
					takeTicketVO.setTicket_no(ticket_id);

					String date_time = resultArray.getString("date_time");
					takeTicketVO.setTime_of_ticket(date_time);

					String first_queue = resultArray.getString("first_queue");
					takeTicketVO.setQueue_no(first_queue);
					
					String status = resultArray.getString("status");
					takeTicketVO.setStatus(status);

					
					ticketArray.add(takeTicketVO);
				}
			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return ticketArray;
	}
}
