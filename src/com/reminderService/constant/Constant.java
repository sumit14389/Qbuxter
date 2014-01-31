package com.reminderService.constant;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import com.reminderService.VO.CancelTicketVO;
import com.reminderService.VO.LocationVO;
import com.reminderService.VO.PeopleQueueVO;
import com.reminderService.VO.TakeTicketVO;
import com.reminderService.VO.TicketWaitingVO;


public class Constant {
	

	public static String CONNECTION_URL = "http://174.136.1.35/dev/Qbuxter/api.php";

	public static String DEVICE_ID="";
	
	public static ArrayList<LocationVO> locationArray;
	public static ArrayList<TakeTicketVO> ticketArray;
	public static ArrayList<PeopleQueueVO> peopleArray;
	public static ArrayList<TicketWaitingVO> peopleWaitingArray;
	public static ArrayList<CancelTicketVO> cancelArray;

	public static String REG_ID="";
	
	
	
	
	
	

}
