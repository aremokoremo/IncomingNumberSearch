package com.aremokoremo.IncomingNumberSearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver{
	private Conf conf=null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(null==conf)
		{
			conf = new Conf(context);
		}
		
		//decide to launch or not bg service on bootup
		try {
			boolean ret = conf.readOnOffStatus();
			if(true == ret)
			{
				Intent serviceIntent = new Intent(context, BgService.class);
				context.startService(serviceIntent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}	
}