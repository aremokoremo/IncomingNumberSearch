package com.aremokoremo.IncomingNumberSearch;

import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Conf conf=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//to save configuration
		conf = new Conf(this);

		//create UI
		createOnOffCheckbox();
	}
	
	private void createOnOffCheckbox()
	{
		CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);

		//default check status
		boolean is_service_running = isServiceRunning();
		checkBox.setChecked(is_service_running);
		
		//Listener setting 
		checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v;

				boolean checked = checkBox.isChecked();
				if(true==checked)
				{
					startBgService();
				}
				else
				{
					stopBgService();
				}
			}
		});
		
	}

	private boolean isServiceRunning() {
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
		final String mServiceName = BgService.class.getCanonicalName();

		for (RunningServiceInfo info : services) {
			if (mServiceName.equals(info.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	private void startBgService()
	{
		boolean is_survice_running = isServiceRunning();

		if(false==is_survice_running)
		{
			Intent intent = new Intent(MainActivity.this, BgService.class);
			startService(intent);

			//save status
			try {
				conf.saveOnOffStatus(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			Toast.makeText(MainActivity.this,
					"already running",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void stopBgService()
	{
		Intent intent = new Intent(MainActivity.this, BgService.class);
		MainActivity.this.stopService(intent);

		//save status
		try {
			conf.saveOnOffStatus(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}