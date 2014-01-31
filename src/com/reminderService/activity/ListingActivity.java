package com.reminderService.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.reminderService.VO.LocationVO;
import com.reminderService.adapter.LocationListAdapter;
import com.reminderService.constant.Constant;
import com.reminderService.network.HttpConnection;
import com.reminderService.parser.LocationParser;
import com.reminderService.util.CommonUtilities;
import com.reminderService.util.CustomProgress;
import com.reminderService.util.GpsTracker;
import com.reminderService.util.RegisterActivities;
import com.reminderService.util.Util;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

/**
 * @author Sumit Kumar Maji
 */

public class ListingActivity extends Activity {

	private CustomProgress progress;
	private PullToRefreshListView locationList;
	private ArrayList<LocationVO> list1;
	private int length;
	private Util utils;
	private LocationListAdapter adapter;
	private LocationVO locationVO;
	private GpsTracker gpsTracker;
	private String lat;
	private String lon;
	private EditText etSearchList;
	private Handler findLocationHandler;
	private String regId;
	private Typeface tf;


	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listing);
		RegisterActivities.registerActivity(this);
		utils=new Util(this);
		gpsTracker=new GpsTracker(ListingActivity.this);
		initView();
		registerDevice();
		findLocationHandler = new Handler()
		{
			private LocationParser locationParser;

			public void handleMessage(Message message)
			{
				switch(message.what)                  
				{
				case HttpConnection.DID_START:
					progress.show();
					break;
				case HttpConnection.DID_SUCCEED:

					if(progress!=null && progress.isShowing())
						progress.dismiss();

					String xmlData = (String)message.obj;
					locationParser = new LocationParser(xmlData);
					Constant.locationArray.clear();
					list1.clear();

					Constant.locationArray=locationParser.parse();
					initView();
					break;

				case HttpConnection.DID_UNSUCCESS:
					progress.dismiss();
					utils.showDialog("Unable to connect to internet");
					break;

				case HttpConnection.DID_ERROR:
					progress.dismiss();
					utils.showDialog("Connection Not Possible");
					break;
				}
			}
		};
	}

	public void registerDevice() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) 
		{		
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		}
		regId = GCMRegistrar.getRegistrationId(this);
		Constant.REG_ID=regId;
	}


	private void initView() {
		locationList = (PullToRefreshListView)findViewById(R.id.lvListLocation);

		list1 = new ArrayList<LocationVO>();
		//list2 = new ArrayList<String>();
		loadData();
		tf = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-ThCn.otf");

		etSearchList=(EditText)findViewById(R.id.etSearchList);
		etSearchList.setTypeface(tf);
		etSearchList.setTextSize(25);
		etSearchList.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text
				adapter.getFilter().filter(cs);	
				adapter.notifyDataSetChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub	
			}
		});

		Constant.DEVICE_ID = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

		locationList.setTextPullToRefresh("Pull to Refresh");
		locationList.setTextReleaseToRefresh("Release to Refresh");
		locationList.setTextRefreshing("Refreshing....");
		locationList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				callLocationHandler();
				// Your code to refresh the list contents goes here

				// for example:
				// If this is a webservice call, it might be asynchronous so
				// you would have to call listView.onRefreshComplete(); when
				// the webservice returns the data
				//loadData();

				// Make sure you call listView.onRefreshComplete()
				// when the loading is done. This can be done from here or any
				// other place, like on a broadcast receive from your loading
				// service or the onPostExecute of your AsyncTask.

				// For the sake of this sample, the code will pause here to
				// force a delay when invoking the refresh
				locationList.postDelayed(new Runnable() {


					@Override
					public void run() {
						locationList.onRefreshComplete();
					}
				}, 2000);
			}
		});
		locationList.onRefreshComplete();
	}

	private void loadData()
	{
		if(Constant.locationArray!=null) {
			length = Constant.locationArray.size();
			for (int i = 0; i < length; i++)
			{
				locationVO = (LocationVO)Constant.locationArray.get(i);
				list1.add(locationVO);			
			}
			setAdapter();
		}
		else if (length==0) {
			Toast.makeText(ListingActivity.this, "No location found", Toast.LENGTH_SHORT).show();
		}

	}

	private void setAdapter() {
		adapter = new LocationListAdapter(this, R.layout.custom_list_item,locationList,list1);
		locationList.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public void callLocationHandler()
	{
		if(!utils.isConnectionPossible())
		{
			new AlertDialog.Builder(ListingActivity.this)
			.setMessage("Please check your Internet Connection...")			
			.setTitle("No Internet Connection")
			.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					callLocationHandler();

				}
			})

			.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}).show();
		} 
		else 
		{
			if(gpsTracker.canGetLocation())
			{	        	
				lat = gpsTracker.getLatitude();
				lon = gpsTracker.getLongitude();	        	
			}
			else
			{
				gpsTracker.showSettingsAlert();
			}
			List<NameValuePair> nameValuePair = getValidationData();
			HttpConnection httpConnection = new HttpConnection(findLocationHandler);
			httpConnection.createPost(1, Constant.CONNECTION_URL, nameValuePair);
			progress = new CustomProgress(ListingActivity.this,"Loading data.....");
		}
	}

	private List<NameValuePair> getValidationData()
	{

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", "searchLocation"));
		nameValuePairs.add(new BasicNameValuePair("latitude", lat));
		nameValuePairs.add(new BasicNameValuePair("longitude", lon));
		return nameValuePairs;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(keyCode == event.KEYCODE_BACK)
		{
			new AlertDialog.Builder(ListingActivity.this)
			.setMessage("Do you want to Exit?")			
			.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					RegisterActivities.removeAllActivities();

				}
			})
			.setNegativeButton("NO", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			}).show();
		}

		return super.onKeyDown(keyCode, event);
	}


	//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	//
	//		switch (item.getItemId()) {
	//		case R.id.iRefresh:
	//			callLocationHandler();
	//			break;
	//		}
	//		return super.onMenuItemSelected(featureId, item);
	//	}


	//	public boolean onCreateOptionsMenu(Menu menu) {
	//	    MenuInflater inflater = this.getMenuInflater();
	//	    inflater.inflate(R.menu.menu, menu);
	//	    return true;
	//	}


	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.iRefresh:
			callLocationHandler();
			break;

		}
		return true;
	}

}
