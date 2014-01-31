package com.reminderService.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.reminderService.VO.CancelTicketVO;
import com.reminderService.VO.LocationVO;
import com.reminderService.VO.TakeTicketVO;
import com.reminderService.VO.TicketWaitingVO;
import com.reminderService.constant.Constant;
import com.reminderService.network.HttpConnection;
import com.reminderService.parser.CancelTicketParser;
import com.reminderService.parser.TakeTicketParser;
import com.reminderService.parser.TicketWaitingParser;
import com.reminderService.util.CommonUtilities;
import com.reminderService.util.CustomProgress;
import com.reminderService.util.MyService;
import com.reminderService.util.RegisterActivities;
import com.reminderService.util.Util;
/**
 * @author Sumit Kumar Maji
 */
public class CancelTicketActivity extends Activity {


	private Button cancelTicketButton;
	private Typeface tf,tfb;
	private TextView tvLocation,tvTicketNo,tvNoOfPeopleInQueue,tvServiceTime;
	private String location_id;
	private TakeTicketParser takeTicketParser;
	private Handler getTicketNoHandler;
	private Util utils;
	private String email;
	private LoadTimertoContinousSendData loadTimerTosendData; 
	private TakeTicketVO takeTicketVO;
	private String queueNo,dateTime,ticketNo;
	private LocationVO locationVO;
	private String location_name;
	private Handler getTicketWaitingHandler;
	private Intent intent;
	private TicketWaitingParser ticketWaitingParser;
	private TicketWaitingVO ticketWaitingVO;
	private Handler getTicketCancelHandler;
	private CustomProgress progress;
	private String fname,lname;
	private String name;
	private String regId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_cancel_ticket);
		registerDevice();
		RegisterActivities.registerActivity(this);
		utils=new Util(this);

		Intent iin= getIntent();
		Bundle bundle = iin.getExtras();
		if(bundle!=null && !bundle.isEmpty())
		{
			location_name = (String)bundle.get("location_name");
			location_id=(String)bundle.get("location_id");
		}
		else {

			Intent intent = new Intent(CancelTicketActivity.this,ListingActivity.class);
			startActivity(intent);
		}



		SharedPreferences sp=getSharedPreferences("Login", 0);
		email=sp.getString("email", "0");
		fname=sp.getString("fname", "0");
		lname=sp.getString("lname", "0");
		name=fname+" "+lname;

		

		getTicketNoHandler = new Handler()
		{

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
					takeTicketParser = new TakeTicketParser(xmlData);
					Constant.ticketArray=takeTicketParser.parse();
					if(Constant.ticketArray!=null && Constant.ticketArray.size()>0)
					{
						takeTicketVO = (TakeTicketVO)Constant.ticketArray.get(0);
						queueNo=takeTicketVO.getQueue_no();
						dateTime=takeTicketVO.getTime_of_ticket();
						ticketNo=takeTicketVO.getTicket_no();
						initview();
					}
					else {
						Toast.makeText(CancelTicketActivity.this, "No data found", Toast.LENGTH_SHORT).show();
					}
					break;

				case HttpConnection.DID_UNSUCCESS:
					progress.dismiss();
					//utils.showDialog(SplashActivity.this, R.string.Authentication);
					break;

				case HttpConnection.DID_ERROR:
					progress.dismiss();
					utils.showDialog("Connection Not Possible");
					break;
				}
			}
		};

		getTicketWaitingHandler = new Handler()
		{

			public void handleMessage(Message message)
			{
				switch(message.what)                  
				{
				case HttpConnection.DID_START:

					break;
				case HttpConnection.DID_SUCCEED:
					String xmlData = (String)message.obj;
					ticketWaitingParser = new TicketWaitingParser(xmlData);
					Constant.peopleWaitingArray=ticketWaitingParser.parse(); 
					if (Constant.peopleWaitingArray!=null && Constant.peopleWaitingArray.size()>0) {
						ticketWaitingVO = (TicketWaitingVO)Constant.peopleWaitingArray.get(0);
						queueNo=ticketWaitingVO.getUpdatedQueue();
						setPoepleInQueue();
					} else {
						Toast.makeText(CancelTicketActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
					}


					break;

				case HttpConnection.DID_UNSUCCESS:
					//utils.showDialog(SplashActivity.this, R.string.Authentication);
					break;

				case HttpConnection.DID_ERROR:       
					utils.showDialog("Connection Not Possible");
					break;
				}
			}
		};

		getTicketCancelHandler = new Handler()
		{

			private CancelTicketParser cancelTicketParser;
			private CancelTicketVO cancelTicketVO;
			private String cancelStatus;

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
					cancelTicketParser = new CancelTicketParser(xmlData);
					Constant.cancelArray=cancelTicketParser.parse();
					if (Constant.cancelArray!=null && Constant.cancelArray.size()>0) {
						cancelTicketVO = (CancelTicketVO)Constant.cancelArray.get(0);
						cancelStatus=cancelTicketVO.getCancelStatus();
						if(cancelStatus.equalsIgnoreCase("Cancel Successful"))
						{
							calListActivity();
						}
					} 
					else 
					{
						Toast.makeText(CancelTicketActivity.this, "Try again to cancel your ticket", Toast.LENGTH_SHORT).show();
					}

					break;

				case HttpConnection.DID_UNSUCCESS:
					progress.dismiss();
					//utils.showDialog(SplashActivity.this, R.string.Authentication);
					break;

				case HttpConnection.DID_ERROR:
					progress.dismiss();
					utils.showDialog("Connection Not Possible");
					break;
				}
			}
		};

		if(bundle!=null && !bundle.isEmpty())
		{
			callTicketNoHandler(location_id);
		}


	}
	public void registerDevice() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
		regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) 
		{		
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		}
		regId = GCMRegistrar.getRegistrationId(this);
		Constant.REG_ID=regId;
	}


	public void calListActivity() 
	{
		if(loadTimerTosendData!=null) 
		{
			loadTimerTosendData.cancel();
		}
		Intent intent = new Intent(CancelTicketActivity.this,ListingActivity.class);
		startActivity(intent);
	}

	private void callTicketNoHandler(String location_id) {

		final String location=location_id;
		if(!utils.isConnectionPossible())
		{
			new AlertDialog.Builder(CancelTicketActivity.this)
			.setMessage("Please check your internet connection...")			
			.setTitle("No Internet Connection")
			.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					callTicketNoHandler(location);

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
			List<NameValuePair> nameValuePair = getTicketNoData(location_id);
			HttpConnection httpConnection = new HttpConnection(getTicketNoHandler);
			httpConnection.createPost(1, Constant.CONNECTION_URL, nameValuePair);
			progress = new CustomProgress(CancelTicketActivity.this,"Loading data.....");
		}
	}

	private List<NameValuePair> getTicketNoData(String location_id) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", "takeTicket"));
		nameValuePairs.add(new BasicNameValuePair("location_id", location_id));
		nameValuePairs.add(new BasicNameValuePair("user_email", email));
		nameValuePairs.add(new BasicNameValuePair("user_name", name));
		nameValuePairs.add(new BasicNameValuePair("device_token", Constant.REG_ID));
		nameValuePairs.add(new BasicNameValuePair("device_type","android"));
		return nameValuePairs;
	}

	private void callTicketWaitingHandler(String location_id) {
		final String location=location_id;
		if(!utils.isConnectionPossible())
		{
			new AlertDialog.Builder(CancelTicketActivity.this)
			.setMessage("Please check your internet connection...")			
			.setTitle("No Internet Connection")
			.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					callTicketWaitingHandler(location);

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
			List<NameValuePair> nameValuePair = getTicketWaitingData(location_id);
			HttpConnection httpConnection = new HttpConnection(getTicketWaitingHandler);
			httpConnection.createPost(1, Constant.CONNECTION_URL, nameValuePair);
			progress = new CustomProgress(CancelTicketActivity.this,"Loading data.....");
		}
	}

	private List<NameValuePair> getTicketWaitingData(String location_id) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", "ticketWaiting"));
		nameValuePairs.add(new BasicNameValuePair("location_id", location_id));
		nameValuePairs.add(new BasicNameValuePair("ticket_number", ticketNo));
		nameValuePairs.add(new BasicNameValuePair("ticket_time",dateTime ));
		return nameValuePairs;
	}

	private void initview() {

		String date=dateTime;
		//		Character[] array = new Character[date.length()];
		//		for (int i = 0; i < date.length(); i++) {
		//			array[i] = new Character(date.charAt(i));
		//		}

		SimpleDateFormat dfprevious = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		SimpleDateFormat dfold = new SimpleDateFormat("HH:mm aa");

		Date datenew = null;
		try {
			datenew = dfprevious.parse(dateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String hr_min = dfold.format(datenew);

		loadDataSendContinous(location_id);

		tf = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-ThCn.otf");
		tfb = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-BlkCn.otf");

		tvLocation=(TextView)findViewById(R.id.tvLocation);
		tvLocation.setText(location_name);

		setPoepleInQueue();

		tvTicketNo=(TextView)findViewById(R.id.tvTicketNo);
		tvTicketNo.setText(ticketNo);

		tvServiceTime=(TextView)findViewById(R.id.tvServiceTime);
		tvServiceTime.setText(hr_min);
		tvLocation.setTypeface(tf);
		tvTicketNo.setTypeface(tfb);
		tvNoOfPeopleInQueue.setTypeface(tfb);
		tvServiceTime.setTypeface(tfb);


		cancelTicketButton=(Button)findViewById(R.id.bCancelTicket);			
		cancelTicketButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(loadTimerTosendData!=null) {
					loadTimerTosendData.cancel();
				}
				new AlertDialog.Builder(CancelTicketActivity.this)
				.setMessage("Do you want to cancel your ticket?")			
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						callTicketCancelHandler(location_id);

					}
				})
				.setNegativeButton("NO", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();


			}
		});
	}

	private void callTicketCancelHandler(String location_id) {

		List<NameValuePair> nameValuePair = getTicketCancelData(location_id);
		HttpConnection httpConnection = new HttpConnection(getTicketCancelHandler);
		httpConnection.createPost(1, Constant.CONNECTION_URL, nameValuePair);

	}

	private List<NameValuePair> getTicketCancelData(String location_id) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", "cancelTicket"));		
		nameValuePairs.add(new BasicNameValuePair("ticket_number", ticketNo));
		nameValuePairs.add(new BasicNameValuePair("location_id", location_id));

		return nameValuePairs;
	}

	private void setPoepleInQueue()
	{
		tvNoOfPeopleInQueue=(TextView)findViewById(R.id.tvNoOfPeopleInQueue);
		tvNoOfPeopleInQueue.setText(queueNo);
	}


	public void loadDataSendContinous(String location_id)
	{
		loadTimerTosendData = new LoadTimertoContinousSendData(15000,400,location_id);// 300000 5minutes senddatacontinouse
		loadTimerTosendData.start();
	}
	public class LoadTimertoContinousSendData extends CountDownTimer
	{
		String location_id;

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

			//loadTimerTosendData.cancel();
			callTicketWaitingHandler(location_id);
			System.out.println("--continous-send data---1--");

		} 

		@Override
		public void onTick(long millisUntilFinished)
		{
			System.out.println("--continous-send data--2---"+millisUntilFinished);

		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		Intent service = new Intent(this, MyService.class);
		stopService(service);
		super.onResume();
	}
	@Override
	protected void onPause() {
		Intent service = new Intent(this, MyService.class);
		startService(service);
		super.onPause();
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		private String pushMessage;
		private MediaPlayer mediaPlayer;
		private Vibrator vibrator;

		@Override
		public void onReceive(Context context, Intent intent) {

			pushMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
			pushMessage.trim();
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */
			if(pushMessage.equalsIgnoreCase("call successful"))
			{
				pushMessage="Its Your turn now..!!!";
			}else if(pushMessage.equalsIgnoreCase("ticket cancel"))
			{
				pushMessage="Ticket cancelled..Contact Admin";
			}

			final AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(CancelTicketActivity.this);

			mediaPlayer = MediaPlayer.create(CancelTicketActivity.this, R.raw.call);
			mediaPlayer.setLooping(true);
			mediaPlayer.start();
		
			alertDialog3.setTitle("Qbuxter");
			alertDialog3.setMessage(pushMessage);	
			alertDialog3.setCancelable(false);

			// Setting Positive Button
			alertDialog3.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,int which) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
						mediaPlayer.release();
						
					}
					mediaPlayer = null;
					if(loadTimerTosendData!=null) {
						loadTimerTosendData.cancel();
					}

					calListActivity();
					//		            if (vibrator.hasVibrator()){
					//		                vibrator.cancel();
					//		                vibrator = null;
					//		            }

				}
			});

			alertDialog3.show();


			//						new AlertDialog.Builder(CancelTicketActivity.this)
			//						.setMessage(pushMessage)			
			//						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			//			MediaPlayer myMp=new MediaPlayer();
			//			
			//						
			//							
			//							@Override
			//							public void onClick(DialogInterface dialog, int which) {
			//								if(loadTimerTosendData!=null) {
			//									loadTimerTosendData.cancel();
			//								}
			//								
			//								calListActivity();
			//							}
			//						}).show();

			// Showing received message
			//SaveValueClass.messageShow.append(newMessage + "\n");		
			/*dialog_push = new Dialog(LoginSetup.this);
			dialog_push.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog_push.setContentView(R.layout.push_show);
			Button cancel = (Button)dialog_push.findViewById(R.id.button_cancel_push);
			TextView message = (TextView)dialog_push.findViewById(R.id.textView_pushShow);
			message.setText(newMessage+"\n");
			cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog_push.dismiss();
				}
			});
			dialog_push.show();*/
			//Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
			//utils.callSound();
			// Releasing wake lock
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {

		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) 
		{
		case R.id.iRefresh:
			callTicketWaitingHandler(location_id);
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
