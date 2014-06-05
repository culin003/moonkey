package com.leoly.fuckey.views;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.listeners.OnMoveListener;
import com.leoly.fuckey.utils.DensityUtil;

/**
 * @author <a href="mailto:kris@krislq.com">Kris.lee</a>
 * @website www.krislq.com
 * @date Nov 29, 2012
 * @version 1.0.0
 */
public class FloatView extends ImageView {
	private float mTouchX;

	private float mTouchY;

	private float x;

	private float y;

	private float mStartX;

	private float mStartY;

	private OnClickListener mClickListener;

	private OnMoveListener mMoveListener;

	private boolean isMove = false;

	private int statusBarHeight = 0;

	private double range = 0;

	private int clickRange = 0;

	private int moveRange = 0;

	private int screenWidth = 0;

	private WindowManager windowManager = (WindowManager) getContext()
			.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

	private WindowManager.LayoutParams windowManagerParams = ((FloatApplication) getContext()
			.getApplicationContext()).getWindowParams();

	private Handler handler = ((FloatApplication) getContext()
			.getApplicationContext()).getHandler();

	public FloatView(Context context) {
		super(context);
	}

	private int getStatubarHeight() {
		if (statusBarHeight == 0) {
			Rect frame = new Rect();
			getWindowVisibleDisplayFrame(frame);
			statusBarHeight = frame.top;
		}

		return statusBarHeight;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x = event.getRawX();
		y = event.getRawY() - getStatubarHeight();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Bundle bundle = new Bundle();
			bundle.putBoolean(Cs.FLOAT_TAG, false);
			Message msg = new Message();
			msg.setData(bundle);
			handler.sendMessage(msg);
			mTouchX = event.getX();
			mTouchY = event.getY();
			mStartX = x;
			mStartY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			updateViewPosition();
			isMove = true;
			break;

		case MotionEvent.ACTION_UP:
			mTouchX = mTouchY = 0;
			range = Math.sqrt(Math.pow(Math.abs(x - mStartX), 2)
					+ Math.pow(Math.abs(y - mStartY), 2));
			if (range <= clickRange) {
				if (mClickListener != null) {
					isMove = false;
					mClickListener.onClick(this);
				}
			}

			if (isMove && range <= moveRange) {
				this.mMoveListener.onMove();
			}

			screenWidth = Float.valueOf(DensityUtil.getWidthInPx()).intValue();
			if (x >= Float.valueOf(DensityUtil.getWidthInPx() / 2).intValue()) {
				updateViewPosition(screenWidth, (int) y);
			} else {
				updateViewPosition(0, (int) y);
			}
			break;
		}
		return true;
	}

	public void setOnMoveListener(OnMoveListener mMoveListener) {
		this.mMoveListener = mMoveListener;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.mClickListener = l;
	}

	private void updateViewPosition() {
		windowManagerParams.x = (int) (x - mTouchX);
		windowManagerParams.y = (int) (y - mTouchY);
		windowManager.updateViewLayout(this, windowManagerParams);
	}

	private void updateViewPosition(int nx, int ny) {
		windowManagerParams.x = nx;
		windowManagerParams.y = ny - statusBarHeight;
		windowManager.updateViewLayout(this, windowManagerParams);
	}

	public void setClickRange(int clickRange) {
		this.clickRange = clickRange;
	}

	public void setMoveRange(int moveRange) {
		this.moveRange = moveRange;
	}
}
