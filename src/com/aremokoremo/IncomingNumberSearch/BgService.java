package com.aremokoremo.IncomingNumberSearch;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import 	android.telephony.PhoneStateListener;

public class BgService extends Service {

	private boolean mRinging=false;
	private Timer timerForSearchingStatus = new Timer();
	private Handler handler = null;
	private Context context = null;
	private Conf conf=null;

	TelephonyManager mTelephonyManager;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if(null==handler)
		{
			handler = new Handler();
		}

		if(null==context)
		{
			context = this;
		}
		
		if(null==conf)
		{
			conf = new Conf(context);
		}

		Toast.makeText(this, "Service has been started.", Toast.LENGTH_SHORT).show();
		initTelephonyListener();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service has been terminated.", Toast.LENGTH_SHORT).show();
	}

	private void initTelephonyListener()
	{
		mTelephonyManager
		= (TelephonyManager)getSystemService(TELEPHONY_SERVICE);

		PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String number) {
				phoneCallEvent(state, number);
			}
		};

		mTelephonyManager.listen(mPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);		
	}

	private void phoneCallEvent(int state, String number){
		switch(state) {

		case TelephonyManager.CALL_STATE_RINGING:
			try {
				boolean ret = conf.readOnOffStatus();
				if(true == ret)
				{
					if(false==mRinging)
					{
						boolean isExisting = isPhoneNumInPhonebook(number);
						if(false == isExisting)
						{
							Toast.makeText(this, "Unknown Num:"+number, Toast.LENGTH_LONG).show();

							//wait phone app launching completed
							try{
								Thread.sleep(1800); 
							}catch(InterruptedException e){}

							//search phone number using internet
							searchPhoneNumberViaInternet(number);
						}
						else
						{
							Toast.makeText(this, "Existing in Phonebook", Toast.LENGTH_SHORT).show();
						}
						mRinging=true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
		case TelephonyManager.CALL_STATE_IDLE:
			if(true==mRinging)
			{
				hideSearchingStatus();

				Toast.makeText(this, "off hook", Toast.LENGTH_SHORT).show();
				mRinging=false;

			}
			break;
		}
	}

	private void searchPhoneNumberViaInternet(String number) {
		if(number.equals(""))
		{
			Toast.makeText(context, "phone number missing", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Uri uri = Uri.parse("https://www.google.co.jp/search?q="+number);

			Intent i = new Intent(Intent.ACTION_VIEW,uri);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);

			showSearchingStatus();

		}
	}

	private void showSearchingStatus() {
		timerForSearchingStatus.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "Search unknow number", Toast.LENGTH_SHORT).show();
					}
				});
			}
		}, 0, 5000);
	}

	private void hideSearchingStatus() {
		if (timerForSearchingStatus != null) {
			timerForSearchingStatus.cancel();
		}
	}

	private boolean isPhoneNumInPhonebook(String phoneNum)
	{
		boolean ret = false;

		ContentResolver cr=getContentResolver();
		Cursor c=cr.query(ContactsContract.Contacts.CONTENT_URI,
				null,null,null,null);

		while (c.moveToNext()) {
			//Group
			int group=c.getInt(c.getColumnIndex(
					ContactsContract.Contacts.IN_VISIBLE_GROUP));
			if (group!=1) continue;

			//ID
			String id=c.getString(
					c.getColumnIndex(ContactsContract.Contacts._ID));

			//PhoneNum
			String dial1="";
			if (Integer.parseInt(c.getString(
					c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0) {
				Cursor cp=cr.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
						new String[]{id}, null);
				while (cp.moveToNext()) {
					dial1=cp.getString(cp.getColumnIndex(
							ContactsContract.CommonDataKinds.Phone.DATA1));
					dial1=dial1.replace("-", "");//depending on phonebook spec??
					if(dial1.equals(phoneNum))
					{
						ret = true;
						break;
					}
				}
				cp.close();
			} 
			if(true==ret)
			{
				break;
			}
		}

		//(Debug) Show Found or Not
		//		if(ret==true){
		//			Toast.makeText(this, "found", Toast.LENGTH_SHORT).show();
		//		}
		//		else{
		//			Toast.makeText(this, "not found", Toast.LENGTH_SHORT).show();
		//		}

		return ret;
	}
}