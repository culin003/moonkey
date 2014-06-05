/**
 * 
 */
package com.leoly.fuckey.services;

import java.util.ArrayList;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.leoly.fuckey.R;
import com.leoly.fuckey.activities.SettingActivity;
import com.leoly.fuckey.adaptors.KeyViewAdaptor;
import com.leoly.fuckey.adaptors.VibratorAdaptor;
import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.key.KeyEventExecuter;
import com.leoly.fuckey.listeners.OnMoveListener;
import com.leoly.fuckey.utils.DensityUtil;
import com.leoly.fuckey.utils.SharePool;
import com.leoly.fuckey.utils.ViewUtils;
import com.leoly.fuckey.views.FloatApplication;
import com.leoly.fuckey.views.FloatView;

/**
 * @author Administrator
 * 
 */
public class FloatKeyService extends Service {

	/**
	 * 浮动按键
	 */
	private FloatView floatView;

	/**
	 * 按键菜单窗口参数
	 */
	private LayoutParams appParams;

	/**
	 * 按键菜单区域
	 */
	private RelativeLayout scrollView;

	/**
	 * 窗口控制器
	 */
	private WindowManager windowManager;

	/**
	 * 参数控制器
	 */
	private SharedPreferences prefer;

	/**
	 * 浮动按键透明度控制器
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			boolean wantShow = data.getBoolean(Cs.FLOAT_TAG);
			if (wantShow) {
				floatView.setAlpha(prefer.getInt(Cs.NO_ACTION_ALPHA, 127));
			} else {
				floatView.setAlpha(255);
				handler.removeCallbacks(run);
				handler.postDelayed(run,
						prefer.getInt(Cs.NO_ACTION_TIME, 10) * 1000);
			}
		}
	};

	private Runnable run = new Runnable() {
		public void run() {
			Bundle bundle = new Bundle();
			bundle.putBoolean(Cs.FLOAT_TAG, true);
			Message msg = new Message();
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	};

	private Handler hideHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			hideButtons();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// 配置
		prefer = getApplicationContext().getSharedPreferences(
				"com.leoly.fuckey_preferences", Context.MODE_MULTI_PROCESS);

		// 设置前台通知，让服务前台运行，防止服務进程被杀
		setNotification();

		// 初始化像素工具
		DensityUtil.reset(getApplicationContext().getResources()
				.getDisplayMetrics());
		// 初始化事件处理工具
		KeyEventExecuter executer = new KeyEventExecuter(
				getApplicationContext());
		// 初始化震动服务
		VibratorAdaptor vibratorAdaptor = new VibratorAdaptor(
				getApplicationContext(), prefer.getBoolean(
						Cs.START_KEY_VIBRATE, false));
		SharePool.push(Cs.KEYEVENTEXECUTER, executer);
		SharePool.push(Cs.HIDEHANDLER, hideHandler);
		SharePool.push(Cs.VIBRATOR, vibratorAdaptor);

		windowManager = (WindowManager) getApplicationContext()
				.getSystemService(WINDOW_SERVICE);
		FloatApplication floatApp = ((FloatApplication) getApplication());
		floatApp.setHandler(handler);
		floatView = new FloatView(floatApp);
		floatView.setImageResource(R.drawable.paopao);
		WindowManager.LayoutParams floatParams = floatApp.getWindowParams();
		// 设置参数
		setFloatParams(floatParams);
		// 设置事件
		setEvent();
		windowManager.addView(floatView, floatParams);
		handler.postDelayed(run, prefer.getInt(Cs.NO_ACTION_TIME, 10) * 1000);
	}

	/**
	 * 设置服务通知
	 */
	private void setNotification() {
		Notification notification = new Notification(R.drawable.ic_launcher,
				"Moonkey", System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, SettingActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(this, "Moonkey", "Moonkey服务正在运行中……",
				pendingIntent);
		startForeground(Cs.FUCKEY_SERVICE, notification);
	}

	/**
	 * 设置浮动按键点击事件
	 */
	private void setEvent() {
		floatView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharePool.get(Cs.VIBRATOR, VibratorAdaptor.class).vibrator();
				SharePool.getExecuter().sendKeys(KeyEvent.KEYCODE_BACK);
			}
		});

		floatView.setOnMoveListener(new OnMoveListener() {
			@Override
			public void onMove() {
				floatView.setVisibility(View.INVISIBLE);
				floatView.setClickable(false);
				SharePool.get(Cs.VIBRATOR, VibratorAdaptor.class).vibrator();
				createMenus();
			}
		});
	}

	/**
	 * 创建按键菜单窗口
	 */
	private void createMenus() {
		scrollView = (RelativeLayout) LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.key_layout, null);
		scrollView.getBackground().setAlpha(
				prefer.getInt("key_area_alpha", 127));
		appParams = new LayoutParams();
		setAppParams(appParams);
		scrollView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent motionevent) {
				if (motionevent.getAction() == MotionEvent.ACTION_OUTSIDE) {
					hideButtons();
				}
				return false;
			}
		});

		int keySize = prefer.getInt("key_size", 48);
		int size = setKeys(keySize);
		// 设置浮动窗口高度，为TableRow的个数乘以Table元素的高度加上上下两边的Padding值
		appParams.height = DensityUtil.dip2px(size * (5 + 5 + keySize));
		windowManager.addView(scrollView, appParams);
	}

	/**
	 * 设置按键菜单
	 * 
	 * @return 按钮行数
	 */
	private int setKeys(int keySize) {
		TableLayout table = (TableLayout) scrollView
				.findViewById(R.id.keyTable);
		String sortedKeys = prefer.getString("custom_key_sorted", null);
		ArrayList<KeyViewAdaptor> list = ViewUtils.getActureViews(sortedKeys,
				getApplicationContext());
		android.widget.TableRow.LayoutParams rowParam = new android.widget.TableRow.LayoutParams(
				android.widget.TableRow.LayoutParams.FILL_PARENT,
				android.widget.TableRow.LayoutParams.WRAP_CONTENT);
		android.widget.TableRow.LayoutParams viewParam = new android.widget.TableRow.LayoutParams(
				DensityUtil.dip2px(keySize), DensityUtil.dip2px(keySize));
		TableRow tr = null;
		int index = 0;
		int size = 0;
		int keyCount = prefer.getInt(Cs.ROW_KEY_COUNT, 5);
		for (KeyViewAdaptor app : list) {
			if (index % keyCount == 0) {
				if (null != tr) {
					table.addView(tr, rowParam);
				}
				tr = new TableRow(getApplicationContext());
				tr.setPadding(0, DensityUtil.dip2px(5), 0,
						DensityUtil.dip2px(5));
				size++;
			}

			tr.addView(app, viewParam);
			index++;
		}

		if (null != tr) {
			table.addView(tr, rowParam);
		}

		table.setStretchAllColumns(true);
		return size;
	}

	/**
	 * 隐藏按键菜单
	 */
	private void hideButtons() {
		scrollView.setVisibility(View.INVISIBLE);
		scrollView.setClickable(false);
		floatView.setVisibility(View.VISIBLE);
		floatView.setClickable(true);
	}

	/**
	 * 设置按键菜单区域窗口参数
	 * 
	 * @param appParams
	 *            窗口参数
	 */
	private void setAppParams(LayoutParams appParams) {
		// 关键属性
		appParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
		appParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		appParams.format = PixelFormat.RGBA_8888;
		appParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		appParams.flags = appParams.flags
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		appParams.width = LayoutParams.FILL_PARENT;
		String anaimType = prefer.getString("float_key_anima", "1");
		switch (Integer.valueOf(anaimType)) {
		case 1:
			appParams.windowAnimations = android.R.style.Animation_Dialog;
			break;
		case 2:
			appParams.windowAnimations = android.R.style.Animation_Translucent;
			break;
		case 3:
			appParams.windowAnimations = android.R.style.Animation_Toast;
			break;
		default:
			appParams.windowAnimations = android.R.style.Animation_Translucent;
			break;
		}
	}

	/**
	 * 设置浮动按钮参数
	 * 
	 * @param floatParams
	 */
	private void setFloatParams(WindowManager.LayoutParams floatParams) {
		floatParams.type = LayoutParams.TYPE_PHONE;
		floatParams.format = PixelFormat.RGBA_8888;
		floatParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		floatParams.gravity = Gravity.LEFT | Gravity.TOP;
		floatParams.x = 0;
		floatParams.y = 0;
		int keySize = prefer.getInt(Cs.FLOAT_KEY_SIZE, 48);
		floatParams.width = DensityUtil.dip2px(keySize);
		floatParams.height = DensityUtil.dip2px(keySize);
		floatView.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		floatView.setMoveRange(DensityUtil.dip2px(prefer.getInt(Cs.MOVE_RANGE,
				55)));
		floatView.setClickRange(DensityUtil.dip2px(prefer.getInt(
				Cs.CLICK_RANGE, 8)));
		floatParams.windowAnimations = android.R.style.Animation_Toast;
	}

	public void onDestroy() {
		// 销毁窗口与缓存
		if (null != windowManager) {
			if (null != floatView)
				windowManager.removeView(floatView);
			if (null != scrollView)
				windowManager.removeView(scrollView);
		}

		if (null != SharePool.getExecuter()) {
			try {
				SharePool.getExecuter().getInputShell().close();
				SharePool.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
