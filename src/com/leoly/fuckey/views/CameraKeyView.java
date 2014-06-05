package com.leoly.fuckey.views;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.leoly.fuckey.R;
import com.leoly.fuckey.adaptors.KeyViewAdaptor;
import com.leoly.fuckey.utils.AnimationUtils;
import com.leoly.fuckey.utils.Lg;
import com.leoly.fuckey.utils.SharePool;

public class CameraKeyView extends KeyViewAdaptor {

	private Handler showMsgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(getContext(),
					"截图完成：" + msg.getData().getString("PIC_PATH"),
					Toast.LENGTH_SHORT).show();
		}
	};

	public CameraKeyView(Context context) {
		super(context);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageResource(R.drawable.camera);
		setName("相机键");
	}

	public CameraKeyView(Context context, AttributeSet atrs) {
		super(context, atrs);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageResource(R.drawable.camera);
		setName("相机键");
	}

	public CameraKeyView(Context context, AttributeSet atrs, int defStyle) {
		super(context, atrs, defStyle);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageResource(R.drawable.camera);
		setName("相机键");
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrate();
				startAnimation(AnimationUtils.getDefaultAnimation());
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(intent);
			}
		});
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		super.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				vibrate();
				startAnimation(AnimationUtils.getDefaultAnimation());
				Toast.makeText(getContext(), "正在截图，请稍等……", Toast.LENGTH_SHORT)
						.show();
				SharePool.getHideHandler().sendEmptyMessage(0);
				Thread t = new Thread() {
					@Override
					public void run() {
						SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat
								.getDateTimeInstance();
						sdf.applyPattern("yyyyMMddHHmmssS");
						File file = new File(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/Screenshot");
						if (!file.exists()) {
							SharePool
									.getExecuter()
									.runRootCmd(
											"mkdir "
													+ Environment
															.getExternalStorageDirectory()
															.getAbsolutePath()
													+ "/Screenshot");
						}
						String mSavedPath = Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/Screenshot/screenshot_"
								+ sdf.format(Calendar.getInstance().getTime())
								+ ".png";
						try {
							Thread.sleep(300);
							SharePool.getExecuter().runRootCmd(
									"screencap -p " + mSavedPath);
							Thread.sleep(1500);
							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putString("PIC_PATH", mSavedPath);
							msg.setData(bundle);
							showMsgHandler.sendMessage(msg);
						} catch (Exception e) {
							e.printStackTrace();
							Lg.error(null, e);
						}
					}
				};

				t.start();
				return true;
			}
		});
	}
}
