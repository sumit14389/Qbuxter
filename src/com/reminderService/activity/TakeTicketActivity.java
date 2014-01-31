package com.reminderService.activity;

/**
 * @author Sumit Kumar Maji
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.brickred.socialauth.android.SocialAuthAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.reminderService.VO.LocationVO;
import com.reminderService.VO.PeopleQueueVO;
import com.reminderService.constant.Constant;
import com.reminderService.network.HttpConnection;
import com.reminderService.parser.PeopleQueueParser;
import com.reminderService.util.CustomProgress;
import com.reminderService.util.RegisterActivities;
import com.reminderService.util.Util;

public class TakeTicketActivity extends Activity {

	private Button takeTicketButton;
	private Typeface tf,tfb;
	private TextView tvLocation,tvPeopleInQueueText,tvNoOfPeople,tvServiceTime,tvServiceTimeText;
	TextView title;

	private Handler getQueueHandler;
	private LocationVO locationVO;
	private Util utils;
	private String queueNo;

	private LoadTimertoContinousSendData loadTimerTosendData;
	private String location_id;
	private String location_name;
	private CustomProgress progress;
	private String listNo;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_ticket);
		RegisterActivities.registerActivity(this);
		utils=new Util(this);
		setTextFont();
		Intent iin= getIntent();
		Bundle bundle = iin.getExtras();
		if(bundle!=null && !bundle.isEmpty())
		{
			location_name = (String)bundle.get("location_name");
			location_id=(String)bundle.get("location_id");
		}

		getQueueHandler = new Handler()
		{

			private Intent intent;
			private PeopleQueueVO peopleVo;
			private PeopleQueueParser peopleQueueParser;

			public void handleMessage(Message message)
			{
				switch(message.what)                  
				{
				case HttpConnection.DID_START:
					//					progress.show();
					break;
				case HttpConnection.DID_SUCCEED:
					//					if(progress!=null && progress.isShowing())
					//						progress.dismiss();
					String xmlData = (String)message.obj;
					peopleQueueParser = new PeopleQueueParser(xmlData);
					Constant.peopleArray=peopleQueueParser.parse();
					if(Constant.peopleArray!=null && Constant.peopleArray.size()>0)
					{
						peopleVo = (PeopleQueueVO)Constant.peopleArray.get(0);
						queueNo=peopleVo.getQueue();
						initview();
					}
					else if (Constant.peopleArray==null || Constant.peopleArray.size()==0) {
						Toast.makeText(TakeTicketActivity.this, "No data found", Toast.LENGTH_SHORT).show();
					}

					break;

				case HttpConnection.DID_UNSUCCESS:
					//					progress.dismiss();
					//utils.showDialog(SplashActivity.this, R.string.Authentication);
					break;

				case HttpConnection.DID_ERROR:
					//					progress.dismiss();
					utils.showDialog("Connection Not Possible");
					break;
				}
			}
		};

		callPeopleQueueHandler(location_id);


	}
	private void setTextFont() {

		tf = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-ThCn.otf");
		tfb = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-BlkCn.otf");
		tvLocation=(TextView)findViewById(R.id.tvLocation);
		tvNoOfPeople=(TextView)findViewById(R.id.tvNoOfPeople);
		tvPeopleInQueueText=(TextView)findViewById(R.id.tvPeopleInQueueText);
		tvServiceTime=(TextView)findViewById(R.id.tvServiceTime);
		tvServiceTimeText=(TextView)findViewById(R.id.tvServiceTimeText);
		tvLocation.setTypeface(tf);
		tvNoOfPeople.setTypeface(tfb);
		tvPeopleInQueueText.setTypeface(tf);
		tvServiceTime.setTypeface(tfb);
		tvServiceTimeText.setTypeface(tf);

	}
	public void callPeopleQueueHandler(String location_id) {
		final String location=location_id;
		if(!utils.isConnectionPossible())
		{
			new AlertDialog.Builder(TakeTicketActivity.this)
			.setMessage("Please check your internet connection...")			
			.setTitle("No Internet Connection")
			.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					callPeopleQueueHandler(location);

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

			List<NameValuePair> nameValuePair = getQueueData(location_id);
			HttpConnection httpConnection = new HttpConnection(getQueueHandler);
			httpConnection.createPost(1, Constant.CONNECTION_URL, nameValuePair);
			//progress = new CustomProgress(TakeTicketActivity.this,"Loading data.....");
		}

	}
	private List<NameValuePair> getQueueData(String location_id)
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", "peopleWaiting"));
		nameValuePairs.add(new BasicNameValuePair("location_id", location_id));

		return nameValuePairs;
	}


	private void initview() {

		loadDataSendContinous(location_id);

		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String time=String.valueOf(today.hour)+":"+String.valueOf(today.minute);
		SimpleDateFormat dfprevious = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dfold = new SimpleDateFormat("HH:mm aa");

		Date datenew = null;
		try {
			datenew = dfprevious.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String hr_min = dfold.format(datenew);

		tvLocation.setText(location_name);		
		tvNoOfPeople.setText(queueNo);
		tvServiceTime.setText(hr_min);

		takeTicketButton=(Button)findViewById(R.id.bTakeTicket);

		takeTicketButton.setOnClickListener(new OnClickListener() {

			private SocialAuthAdapter adapter;

			@Override
			public void onClick(View v) {
				SharedPreferences sp=getSharedPreferences("Login", 0);
				String email=sp.getString("email", "0");
				if(email.equalsIgnoreCase("0"))
				{
					if(loadTimerTosendData!=null) {
						loadTimerTosendData.cancel();
					}

					Intent intent = new Intent(TakeTicketActivity.this,UserLoginActivity.class);
					intent.putExtra("location_id", location_id);
					intent.putExtra("location_name", location_name);
					startActivity(intent);
				}
				else{

					if(loadTimerTosendData!=null) {
						loadTimerTosendData.cancel();
					}
					finish();
					Intent intent = new Intent(TakeTicketActivity.this,CancelTicketActivity.class);
					intent.putExtra("location_id", location_id);
					intent.putExtra("location_name", location_name);
					startActivity(intent);
				}
			}
		});
	}


	public void loadDataSendContinous(String location_id)
	{
		loadTimerTosendData = new LoadTimertoContinousSendData(15000,400,location_id);// 300000 5minutes senddatacontinouse
		loadTimerTosendData.start();
	}
	public class LoadTimertoContinousSendData extends CountDownTimer
	{
		String location_id;
		//		public LoadTimertoContinousSendData( String location_id){
		//
		//			
		//		}
		public LoadTimertoContinousSendData(long startTime, long interval,String location_id)
		{
			super(startTime, interval);
			System.out.println("-----LoadTimertoContinousSendData-----1-----"+startTime);
			this.location_id=location_id;
		}

		@Override
		public void onFinish()
		{  
			loadTimerTosendData.start();
			callPeopleQueueHandler(location_id);
		} 
		@Override
		public void onTick(long millisUntilFinished)
		{

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(loadTimerTosendData!=null) {
				loadTimerTosendData.cancel();
			}
			finish();
			Intent intent=new Intent(TakeTicketActivity.this,ListingActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case R.id.iRefresh:
			callPeopleQueueHandler(location_id);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
}