
package com.reminderService.activity;

/**
 * @author Sumit Kumar Maji
 */

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.reminderService.util.RegisterActivities;

// Please see strings.xml for list values

public class UserLoginActivity extends Activity implements OnClickListener{

	// SocialAuth Components
	private static SocialAuthAdapter adapter;
	Profile profileMap;	
	AlertDialog dialog;
	TextView title;
	ProgressDialog mDialog;
	String providerName;
	private Button bFacebook,bTwitter,bGooglePlus;
	private int FACEBOOK_LOGIN_TRUE=0;
	private int TWITTER_LOGIN_TRUE=0;
	private int GOOGLE_LOGIN_TRUE=0;
	private static final int SELECT_PHOTO = 100;
	public static Bitmap bitmap;
	private final Provider[] providers = new Provider[] {Provider.GOOGLEPLUS,Provider.FACEBOOK, Provider.TWITTER};//
	private final int[] images = new int[] { R.drawable.googleplus ,R.drawable.facebook, R.drawable.twitter};
	private Button bCancel;
	private String location_name;
	private String location_id;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login);
		RegisterActivities.registerActivity(this);
		initView();
		Intent iin= getIntent();
		Bundle bundle = iin.getExtras();
		if(bundle!=null && !bundle.isEmpty())
		{
			location_name = (String)bundle.get("location_name");
			location_id=(String)bundle.get("location_id");
		}

	}


	private void initView() {
		// Adapter initialization
		adapter = new SocialAuthAdapter(new ResponseListener());

		bFacebook=(Button)findViewById(R.id.bFacebook);
		bFacebook.setBackgroundResource(R.drawable.facebook);

		bTwitter=(Button)findViewById(R.id.bTwitter);
		bTwitter.setBackgroundResource(R.drawable.twitter);

		bGooglePlus=(Button)findViewById(R.id.bGooglePlus);
		bGooglePlus.setBackgroundResource(R.drawable.googleplus);

		bFacebook.setOnClickListener(this);
		bTwitter.setOnClickListener(this);
		bGooglePlus.setOnClickListener(this);

		bCancel=(Button)findViewById(R.id.bCancel);
		bCancel.setOnClickListener(this);

	}


	// To receive the response after authentication
	private final class ResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {

			// Get the provider
			providerName = values.getString(SocialAuthAdapter.PROVIDER);

			mDialog = new ProgressDialog(UserLoginActivity.this);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.setMessage("Loading...");
			Events(0, providerName);
		}

		@Override
		public void onBack() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(SocialAuthError arg0) {
			// TODO Auto-generated method stub

		}
	}

	// Method to handle events of providers
	public void Events(int position, final String provider) {

		switch (position) {
		case 0: // Code to print user profile details for all providers
		{
			mDialog.show();
			adapter.getUserProfileAsync(new ProfileDataListener());
			break;
		}
		}

	}

	// To receive the profile response after authentication
	private final class ProfileDataListener implements SocialAuthListener<Profile> {

		private SharedPreferences sp;

		@Override
		public void onExecute(String provider, Profile t) {

			Log.d("Custom-UI", "Receiving Data");
			mDialog.dismiss();			
			Profile profileMap = t;

			if(provider.equalsIgnoreCase("FACEBOOK"))
			{
				sp=getSharedPreferences("Login", 0);
				SharedPreferences.Editor Ed=sp.edit();
				Ed.putString("email",profileMap.getEmail() );
				Ed.putString("fname",profileMap.getFirstName() );
				Ed.putString("lname",profileMap.getLastName() );
				Ed.commit();
				FACEBOOK_LOGIN_TRUE=1;
				if(profileMap.getEmail().equalsIgnoreCase("null")) {
					if(!profileMap.getFullName().equalsIgnoreCase(""))
					{
						Intent intent = new Intent(UserLoginActivity.this,CancelTicketActivity.class);
						intent.putExtra("location_id", location_id);
						intent.putExtra("location_name", location_name);
						startActivity(intent);
					}
				}
				else {
					if(!profileMap.getEmail().equalsIgnoreCase(""))
					{
						Intent intent = new Intent(UserLoginActivity.this,CancelTicketActivity.class);
						intent.putExtra("location_id", location_id);
						intent.putExtra("location_name", location_name);
						startActivity(intent);
					}


				}
			}
			else if(provider.equalsIgnoreCase("TWITTER"))
			{
				sp=getSharedPreferences("Login", 0);
				SharedPreferences.Editor Ed=sp.edit();
				Ed.putString("email",profileMap.getDisplayName());
				Ed.putString("fname",profileMap.getFirstName() );
				Ed.putString("lname",profileMap.getLastName() );
				Ed.commit();
				TWITTER_LOGIN_TRUE=1;
				if(!profileMap.getDisplayName().equalsIgnoreCase(""))
				{
					Intent intent = new Intent(UserLoginActivity.this,CancelTicketActivity.class);
					intent.putExtra("location_id", location_id);
					intent.putExtra("location_name", location_name);
					startActivity(intent);
				}
			}
			else if(provider.equalsIgnoreCase("GOOGLEPLUS"))
			{
				sp=getSharedPreferences("Login", 0);
				SharedPreferences.Editor Ed=sp.edit();
				Ed.putString("email",profileMap.getEmail() );
				Ed.putString("fname",profileMap.getFirstName() );
				Ed.putString("lname",profileMap.getLastName() );
				Ed.commit();
				GOOGLE_LOGIN_TRUE=1;
				if(profileMap.getEmail().equalsIgnoreCase("null")) {
					if(!profileMap.getFullName().equalsIgnoreCase(""))
					{
						Intent intent = new Intent(UserLoginActivity.this,CancelTicketActivity.class);
						intent.putExtra("location_id", location_id);
						intent.putExtra("location_name", location_name);
						startActivity(intent);
					}
				}
				else {
					if(!profileMap.getEmail().equalsIgnoreCase(""))
					{
						Intent intent = new Intent(UserLoginActivity.this,CancelTicketActivity.class);
						intent.putExtra("location_id", location_id);
						intent.putExtra("location_name", location_name);
						startActivity(intent);
					}
				}
			}
			//Toast.makeText(UserLoginActivity.this, profileMap.getDisplayName()+profileMap.getEmail()+"\n"+profileMap.getProfileImageURL(),Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();
				InputStream imageStream;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
					bitmap = BitmapFactory.decodeStream(imageStream);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	public void onClick(View v) {

		Boolean b=false;
		switch (v.getId()) {

		case R.id.bGooglePlus:

			if(GOOGLE_LOGIN_TRUE==1)
			{
				//b=adapter.signOut(UserLoginActivity.this, providers[0].toString());
				GOOGLE_LOGIN_TRUE=0;
			}
			adapter.authorize(UserLoginActivity.this, providers[0]);
			//			finish();
			break;

		case R.id.bFacebook:

			if(FACEBOOK_LOGIN_TRUE==1)
			{
				//b=adapter.signOut(UserLoginActivity.this, providers[1].toString());
				FACEBOOK_LOGIN_TRUE=0;
			}
			adapter.authorize(UserLoginActivity.this, providers[1]);
			//			finish();
			break;
		case R.id.bTwitter:

			if(TWITTER_LOGIN_TRUE==1)
			{
				//b=adapter.signOut(UserLoginActivity.this, providers[2].toString());
				TWITTER_LOGIN_TRUE=0;
			}
			adapter.authorize(UserLoginActivity.this, providers[2]);
			//			finish();
			break;
		case R.id.bCancel:
			finish();
			break;
		}

	}

}