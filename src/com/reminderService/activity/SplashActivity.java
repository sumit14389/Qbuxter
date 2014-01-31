package com.reminderService.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;

import com.reminderService.constant.Constant;
import com.reminderService.network.HttpConnection;
import com.reminderService.parser.LocationParser;
import com.reminderService.util.CustomProgress;
import com.reminderService.util.GpsTracker;
import com.reminderService.util.RegisterActivities;
import com.reminderService.util.Util;

/**
 * @author Sumit Kumar Maji
 */

public class SplashActivity extends Activity implements LocationListener{

	Timer timer;
	private Handler findLocationHandler;
	private Util utils;

	private GpsTracker gpsTracker;
	private String latitude;
	private String longitude;
	private Intent intent;
	private LocationParser locationParser;
	private CustomProgress progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		RegisterActivities.registerActivity(this);
		utils=new Util(this);
		initview();

		findLocationHandler = new Handler()
		{

			public void handleMessage(Message message)
			{
				switch(message.what)                  
				{
				case HttpConnection.DID_START:
					//					progress.show();
					break;
				case HttpConnection.DID_SUCCEED:
					String xmlData = (String)message.obj;
					locationParser = new LocationParser(xmlData);
					Constant.locationArray=locationParser.parse();
					finish();
					intent = new Intent(SplashActivity.this, ListingActivity.class);
					startActivity(intent);

					break;

				case HttpConnection.DID_UNSUCCESS:
					//					progress.dismiss();
					//utils.showDialog(SplashActivity.this, R.string.Authentication);
					break;

				case HttpConnection.DID_ERROR:
					//					progress.dismiss();
					new AlertDialog.Builder(SplashActivity.this)
					.setMessage("Please check your internet connection...")			
					.setTitle("No Internet Connection")
					.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							initview();

						}
					})

					.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).show();
					//utils.showDialog("Connection Not Possible");
					break;
				}
			}
		};

	}
	private void initview()
	{
		gpsTracker=new GpsTracker(SplashActivity.this);
		Constant.DEVICE_ID = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

		if(gpsTracker.canGetLocation())
		{	        	
			latitude = gpsTracker.getLatitude();
			longitude = gpsTracker.getLongitude();	        	
		}
		else
		{
			gpsTracker.showSettingsAlert();
		}
		Runnable runnable = new Runnable() {
			public void run() {

				callLocationHandler();
			}
		};
		Thread mythread = new Thread(runnable);
		mythread.start();

	}


	public void callLocationHandler() 
	{
		//		if(!utils.isConnectionPossible())
		//		{
		//			new AlertDialog.Builder(SplashActivity.this)
		//			.setMessage("Please check your internet connection...EXIT??")			
		//			.setTitle("No Internet Connection")
		//			.setNegativeButton("OK", new DialogInterface.OnClickListener() {
		//
		//				@Override
		//				public void onClick(DialogInterface dialog, int which) {
		//
		//				}
		//			}).show();
		//		} 
		//		else 
		{
			
			List<NameValuePair> nameValuePair = getValidationData();
			HttpConnection httpConnection = new HttpConnection(findLocationHandler);
			httpConnection.createPost(1, Constant.CONNECTION_URL, nameValuePair);
			//progress = new CustomProgress(SplashActivity.this,"Loading data.....");
		}

	}
	private List<NameValuePair> getValidationData()
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", "searchLocation"));
		nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
		nameValuePairs.add(new BasicNameValuePair("longitude",longitude));
		return nameValuePairs;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
