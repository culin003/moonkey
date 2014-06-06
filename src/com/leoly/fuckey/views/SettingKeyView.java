package com.leoly.fuckey.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import com.leoly.fuckey.R;
import com.leoly.fuckey.adaptors.KeyViewAdaptor;
import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.utils.AnimationUtils;
import com.leoly.fuckey.utils.SharePool;

public class SettingKeyView extends KeyViewAdaptor {

	public SettingKeyView(Context context) {
		super(context);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageBitmap(SharePool.get(Cs.SETTING_KEY, Bitmap.class));
		setName("设置键");
	}

	public SettingKeyView(Context context, AttributeSet atrs) {
		super(context, atrs);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageBitmap(SharePool.get(Cs.SETTING_KEY, Bitmap.class));
		setName("设置键");
	}

	public SettingKeyView(Context context, AttributeSet atrs, int defStyle) {
		super(context, atrs, defStyle);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageBitmap(SharePool.get(Cs.SETTING_KEY, Bitmap.class));
		setName("设置键");
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrate();
				startAnimation(AnimationUtils.getDefaultAnimation());
				Intent intent = new Intent();
				intent.setClassName("com.android.settings",
						"com.android.settings.Settings");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getContext().startActivity(intent);
			}
		});
	}
}
