package com.leoly.fuckey.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.leoly.fuckey.R;
import com.leoly.fuckey.adaptors.KeyViewAdaptor;
import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.utils.AnimationUtils;
import com.leoly.fuckey.utils.SharePool;

public class PowerKeyView extends KeyViewAdaptor {

	public PowerKeyView(Context context) {
		super(context);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageBitmap(SharePool.get(Cs.POWER_KEY, Bitmap.class));
		setName("电源键");
	}

	public PowerKeyView(Context context, AttributeSet atrs) {
		super(context, atrs);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageBitmap(SharePool.get(Cs.POWER_KEY, Bitmap.class));
		setName("电源键");
	}

	public PowerKeyView(Context context, AttributeSet atrs, int defStyle) {
		super(context, atrs, defStyle);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageBitmap(SharePool.get(Cs.POWER_KEY, Bitmap.class));
		setName("电源键");
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrate();
				startAnimation(AnimationUtils.getDefaultAnimation());
				SharePool.getExecuter().sendKeys(KeyEvent.KEYCODE_POWER);
			}
		});
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		super.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				vibrate();
				SharePool.getExecuter().sendDownTouchKeys(
						KeyEvent.KEYCODE_POWER);
				return true;
			}
		});
	}
}
