package com.reminderService.VO;

/**
 * @author Sumit Kumar Maji
 */

public class TakeTicketVO {

	private String ticket_no="";
	private String status="";
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTicket_no() {
		return ticket_no;
	}
	public void setTicket_no(String ticket_no) {
		this.ticket_no = ticket_no;
	}
	public String getTime_of_ticket() {
		return time_of_ticket;
	}
	public void setTime_of_ticket(String time_of_ticket) {
		this.time_of_ticket = time_of_ticket;
	}
	public String getQueue_no() {
		return queue_no;
	}
	public void setQueue_no(String queue_no) {
		this.queue_no = queue_no;
	}
	private String time_of_ticket="";
	private String queue_no="";
	

	public void setErrorCode(String valueOf) {
		// TODO Auto-generated method stub

	}
	public void setErrorUrl(String url) {
		// TODO Auto-generated method stub

	}


}
