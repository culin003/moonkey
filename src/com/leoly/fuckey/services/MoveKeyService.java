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
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.leoly.fuckey.R;
import com.leoly.fuckey.activities.SettingActivity;
import com.leoly.fuckey.adaptors.GestureAdaptor;
import com.leoly.fuckey.adaptors.KeyViewAdaptor;
import com.leoly.fuckey.adaptors.VibratorAdaptor;
import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.key.KeyEventExecuter;
import com.leoly.fuckey.utils.DensityUtil;
import com.leoly.fuckey.utils.SharePool;
import com.leoly.fuckey.utils.ViewUtils;

/**
 * @author Administrator
 * 
 */
public class MoveKeyService extends Service {

	/**
	 * 窗口控制器
	 */
	private WindowManager windowManager;

	/**
	 * 触发区域
	 */
	private LinearLayout layout;

	/**
	 * 触发区域窗口参数
	 */
	private LayoutParams appParams;

	/**
	 * 参数获取器
	 */
	private SharedPreferences prefer;

	/**
	 * 触发区域手势监控者
	 */
	private GestureDetector detector = null;

	/**
	 * 按键区域
	 */
	private RelativeLayout scrollView;

	/**
	 * 透明度控制器，用于显示触发区域5秒后，将触发区域的透明度设置为0
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			layout.getBackground().setAlpha(0);
		}
	};

	/**
	 * 按键区域控制器，用于按钮点击后，需要隐藏按键
	 */
	private Handler hideHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			hideButtons();
		}
	};

	/**
	 * 透明度控制器的执行者
	 */
	private Runnable run = new Runnable() {
		public void run() {
			handler.sendEmptyMessage(0);
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

		layout = (LinearLayout) LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.fire_buttom, null);
		layout.setBackgroundColor(Color.BLUE);
		appParams = new LayoutParams();
		int location = Integer.valueOf(prefer.getString("fire_location", "1"));
		setAppParams(location);
		setDetector(location);
		windowManager.addView(layout, appParams);
		handler.postDelayed(run, 5 * 1000);
	}

	/**
	 * 设置手势监控器
	 * 
	 * @param location
	 *            按键触发区域的位置
	 */
	private void setDetector(final int location) {
		layout.setLongClickable(true);
		detector = new GestureDetector(MoveKeyService.this,
				new GestureAdaptor() {
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						switch (location) {
						// 下
						case 1:
							if (Math.abs(e2.getY()) > Math.abs(e1.getY())) {
								showKeys();
							}
							break;
						// 左
						case 2:
							// 右
						case 3:
							if (Math.abs(e2.getX()) > Math.abs(e1.getX())) {
								showKeys();
							}
							break;
						default:
							break;
						}

						return true;
					};
				});

		layout.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});
	}

	/**
	 * 显示按键菜单
	 */
	private void showKeys() {
		// 震动
		SharePool.get(Cs.VIBRATOR, VibratorAdaptor.class).vibrator();
		scrollView = (RelativeLayout) LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.key_layout, null);
		scrollView.getBackground().setAlpha(
				prefer.getInt("key_area_alpha", 127));
		LayoutParams layoutParams = new LayoutParams();
		setLayoutParams(layoutParams);
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
		layoutParams.height = DensityUtil.dip2px(size * (5 + 5 + keySize));
		windowManager.addView(scrollView, layoutParams);
	}

	/**
	 * 隐藏按键菜单
	 */
	private void hideButtons() {
		scrollView.setVisibility(View.INVISIBLE);
		scrollView.setClickable(false);
	}

	/**
	 * 设置按键菜单显示参数
	 * 
	 * @param layoutParams
	 *            显示参数
	 */
	private void setLayoutParams(LayoutParams layoutParams) {
		// 关键属性
		layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
		layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		layoutParams.format = PixelFormat.RGBA_8888;
		layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		layoutParams.flags = layoutParams.flags
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		layoutParams.width = LayoutParams.FILL_PARENT;
		String anaimType = prefer.getString("float_key_anima", "1");
		switch (Integer.valueOf(anaimType)) {
		case 1:
			layoutParams.windowAnimations = android.R.style.Animation_Dialog;
			break;
		case 2:
			layoutParams.windowAnimations = android.R.style.Animation_Translucent;
			break;
		case 3:
			layoutParams.windowAnimations = android.R.style.Animation_Toast;
			break;
		default:
			layoutParams.windowAnimations = android.R.style.Animation_Translucent;
			break;
		}
	}

	/**
	 * 设置按键菜单
	 * 
	 * @return 按钮行数，用于计算窗口显示高度
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
	 * 设置通知
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
	 * 设置按键触发区域窗口参数
	 * 
	 * @param location
	 *            触发区域位置
	 */
	private void setAppParams(int location) {
		// 关键属性
		setGravity(location);
		appParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		appParams.format = PixelFormat.RGBA_8888;
		appParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		appParams.flags = appParams.flags
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		int width = prefer.getInt("fire_wight_range", 125);
		appParams.width = width == 0 ? WindowManager.LayoutParams.FILL_PARENT
				: DensityUtil.dip2px(width);
		int height = prefer.getInt("fire_height_range", 5);
		appParams.height = height == 0 ? WindowManager.LayoutParams.FILL_PARENT
				: DensityUtil.dip2px(height);
		appParams.windowAnimations = android.R.style.Animation_InputMethod;
	}

	/**
	 * 设置触发区域位置
	 * 
	 * @param value
	 */
	private void setGravity(int value) {
		switch (value) {
		// 下
		case 1:
			appParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
			break;
		// 左
		case 2:
			appParams.gravity = Gravity.LEFT | Gravity.CENTER;
			break;
		// 右
		case 3:
			appParams.gravity = Gravity.RIGHT | Gravity.CENTER;
			break;
		default:
			appParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	public void onDestroy() {
		// 销毁窗口与缓存
		if (null != windowManager) {
			if (null != layout)
				windowManager.removeView(layout);
			if (null != scrollView) {
				windowManager.removeView(scrollView);
			}
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
