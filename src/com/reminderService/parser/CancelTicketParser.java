package com.reminderService.parser;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.reminderService.VO.CancelTicketVO;

public class CancelTicketParser {

	String jSonData;
	private CancelTicketVO cancelTicketVO;
	public CancelTicketParser(String data)
	{
		this.jSonData = data;
	}

	public ArrayList<CancelTicketVO> parse()
	{

		ArrayList<CancelTicketVO> cancelArray = new ArrayList<CancelTicketVO>();

		try {
			JSONObject jObject = new JSONObject(jSonData);

			JSONObject success = jObject.getJSONObject("response");

			String isSucessCheck=success.getString("message");

			if(isSucessCheck.equalsIgnoreCase("Cancel Successful"))
			{
				cancelTicketVO = new CancelTicketVO();
				cancelTicketVO.setCancelStatus(isSucessCheck);
				cancelArray.add(cancelTicketVO);
			}


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return cancelArray;
	}
}
