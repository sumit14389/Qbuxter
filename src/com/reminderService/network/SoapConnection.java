package com.reminderService.network;

/**
 * @author Sumit Kumar Maji
 */

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.reminderService.constant.Constant;

public class SoapConnection {
	
	public HttpURLConnection getConnection(int inputLengtn)
			throws Exception {
		URL url = new URL(Constant.CONNECTION_URL);
		HttpURLConnection httpsURLConnection = null;
		httpsURLConnection = (HttpURLConnection) url.openConnection();		
		httpsURLConnection.setRequestMethod("POST");
		httpsURLConnection.setRequestProperty("Content-Type", "text/xml");
		httpsURLConnection.setRequestProperty("Content-Length", ""
				+ inputLengtn);
		httpsURLConnection.setRequestProperty("SOAPAction", "YesBankService");
		httpsURLConnection.setUseCaches(false);
		httpsURLConnection.setDoInput(true);
		httpsURLConnection.setDoOutput(true);

		return httpsURLConnection;
	}
	
	
	public void writeData(ByteArrayOutputStream input,
			HttpURLConnection connection) throws Exception {
		// send request
		DataOutputStream out = new DataOutputStream(connection
				.getOutputStream());
		out.writeBytes(input.toString());
		out.flush();
		out.close();
	}

}
