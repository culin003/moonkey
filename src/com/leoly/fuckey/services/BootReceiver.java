package com.leoly.fuckey.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.leoly.fuckey.constants.Cs;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent i) {
		SharedPreferences prefer = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean autoStart = prefer.getBoolean(Cs.AUTO_START_KEY_SERVICE, false);
		if (autoStart) {
			Class<?> service = null;
			switch (Integer.valueOf(prefer.getString("key_fire_type", "1"))) {
			case 1:
				service = FloatKeyService.class;
				break;

			default:
				service = MoveKeyService.class;
				break;
			}

			Intent intent1 = new Intent(context, service);
			context.startService(intent1);
		}
	}
}
