package com.leoly.fuckey.views;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.leoly.fuckey.R;
import com.leoly.fuckey.adaptors.KeyViewAdaptor;
import com.leoly.fuckey.utils.AnimationUtils;
import com.leoly.fuckey.utils.Lg;
import com.leoly.fuckey.utils.SharePool;

public class BackKeyView extends KeyViewAdaptor {
	
	public BackKeyView(Context context) {
		super(context);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageResource(R.drawable.back);
		setName("返回键");
	}

	public BackKeyView(Context context, AttributeSet atrs) {
		super(context, atrs);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageResource(R.drawable.back);
		setName("返回键");
	}

	public BackKeyView(Context context, AttributeSet atrs, int defStyle) {
		super(context, atrs, defStyle);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageResource(R.drawable.back);
		setName("返回键");
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrate();
				startAnimation(AnimationUtils.getDefaultAnimation());
				SharePool.getExecuter().sendKeys(KeyEvent.KEYCODE_BACK);
			}
		});
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		super.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				vibrate();
				startAnimation(AnimationUtils.getDefaultAnimation());
				final List<RunningTaskInfo> rti = ((ActivityManager) getContext()
						.getSystemService("activity")).getRunningTasks(1);
				final String cmpName = ((ActivityManager.RunningTaskInfo) rti
						.get(0)).baseActivity.getPackageName();
				String temp = new String(cmpName).toLowerCase();
				Lg.info(temp);
				// 不杀启动器
				if (temp.contains("launcher") || temp.contains("fuckey")
						|| temp.contains("googlequicksearchbox")) {
					return true;
				}
				SharePool.getExecuter().sendKeys(KeyEvent.KEYCODE_HOME);
				new Timer().schedule(new TimerTask() {
					public void run() {
						try {
							SharePool.getExecuter().runRootCmd(
									"am force-stop " + cmpName);
						} catch (Exception e) {
							Lg.error(null, e);
						}
					}
				}, 1000L);
				return true;
			}
		});
	}
}
