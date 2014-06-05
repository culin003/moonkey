package com.leoly.fuckey.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.leoly.fuckey.R;
import com.leoly.fuckey.adaptors.KeyViewAdaptor;
import com.leoly.fuckey.utils.AnimationUtils;
import com.leoly.fuckey.utils.SharePool;

public class RecentKeyView extends KeyViewAdaptor {
	public RecentKeyView(Context context) {
		super(context);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageResource(R.drawable.task);
		setName("任务键");
	}

	public RecentKeyView(Context context, AttributeSet atrs) {
		super(context, atrs);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageResource(R.drawable.task);
		setName("任务键");
	}

	public RecentKeyView(Context context, AttributeSet atrs, int defStyle) {
		super(context, atrs, defStyle);
		setOnLongClickListener(null);
		setOnClickListener(null);
		setImageResource(R.drawable.task);
		setName("任务键");
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vibrate();
				startAnimation(AnimationUtils.getDefaultAnimation());
				SharePool.getExecuter().showTask();
				SharePool.getHideHandler().sendEmptyMessage(0);
			}
		});
	}
}
