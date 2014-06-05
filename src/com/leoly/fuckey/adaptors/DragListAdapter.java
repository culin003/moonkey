package com.leoly.fuckey.adaptors;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leoly.fuckey.R;
import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.utils.ViewUtils.MyViewInfo;

/***
 * 
 * @author Leoly
 * 
 */
public class DragListAdapter extends BaseAdapter {
	private ArrayList<MyViewInfo> arrayTitles;
	private Context context;
	public boolean isHidden;
	private int invisilePosition = -1;
	private boolean isChanged = true;
	private boolean ShowItem = false;
	private int lastFlag = -1;
	private int height;
	private ArrayList<MyViewInfo> mCopyList = new ArrayList<MyViewInfo>();

	public DragListAdapter(Context context, ArrayList<MyViewInfo> arrayTitles) {
		this.context = context;
		this.arrayTitles = arrayTitles;
	}

	public void showDropItem(boolean showItem) {
		this.ShowItem = showItem;
	}

	public void setInvisiblePosition(int position) {
		invisilePosition = position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(
				R.layout.drag_list_item, null);

		TextView textView = (TextView) convertView
				.findViewById(R.id.drag_list_item_text);
		TextView valueView = (TextView) convertView
				.findViewById(R.id.drag_list_item_value);
		textView.setText(arrayTitles.get(position).getName());
		valueView.setText(arrayTitles.get(position).getPkg());
		if (isChanged) {
			Log.i(Cs.APP_NAME, "position == " + position);
			Log.i(Cs.APP_NAME, "holdPosition == " + invisilePosition);
			if (position == invisilePosition) {
				if (!ShowItem) {
					convertView.findViewById(R.id.drag_list_item_text)
							.setVisibility(View.INVISIBLE);
					convertView.findViewById(R.id.drag_list_item_image)
							.setVisibility(View.INVISIBLE);
				}
			}
			if (lastFlag != -1) {
				if (lastFlag == 1) {
					if (position > invisilePosition) {
						Animation animation;
						animation = getFromSelfAnimation(0, -height);
						convertView.startAnimation(animation);
					}
				} else if (lastFlag == 0) {
					if (position < invisilePosition) {
						Animation animation;
						animation = getFromSelfAnimation(0, height);
						convertView.startAnimation(animation);
					}
				}
			}
		}
		return convertView;
	}

	public void exchange(int startPosition, int endPosition) {
		Object startObject = getItem(startPosition);
		Log.d("ON", "startPostion ==== " + startPosition);
		Log.d("ON", "endPosition ==== " + endPosition);
		if (startPosition < endPosition) {
			arrayTitles.add(endPosition + 1, (MyViewInfo) startObject);
			arrayTitles.remove(startPosition);
		} else {
			arrayTitles.add(endPosition, (MyViewInfo) startObject);
			arrayTitles.remove(startPosition + 1);
		}
		isChanged = true;
	}

	public void exchangeCopy(int startPosition, int endPosition) {
		Object startObject = getCopyItem(startPosition);
		Log.d("ON", "startPostion ==== " + startPosition);
		Log.d("ON", "endPosition ==== " + endPosition);
		if (startPosition < endPosition) {
			mCopyList.add(endPosition + 1, (MyViewInfo) startObject);
			mCopyList.remove(startPosition);
		} else {
			mCopyList.add(endPosition, (MyViewInfo) startObject);
			mCopyList.remove(startPosition + 1);
		}
		isChanged = true;
	}

	public Object getCopyItem(int position) {
		return mCopyList.get(position);
	}

	@Override
	public int getCount() {
		return arrayTitles.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayTitles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addDragItem(int start, Object obj) {
		Log.i(Cs.APP_NAME, "start" + start);
		arrayTitles.remove(start);
		arrayTitles.add(start, (MyViewInfo) obj);
	}

	public void copyList() {
		mCopyList.clear();
		for (MyViewInfo info : arrayTitles) {
			mCopyList.add(info);
		}
	}

	public void pastList() {
		arrayTitles.clear();
		for (MyViewInfo info : mCopyList) {
			arrayTitles.add(info);
		}
	}

	public void setLastFlag(int flag) {
		lastFlag = flag;
	}

	public void setHeight(int value) {
		height = value;
	}

	public Animation getFromSelfAnimation(int x, int y) {
		TranslateAnimation go = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, x,
				Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, y);
		go.setInterpolator(new AccelerateDecelerateInterpolator());
		go.setFillAfter(true);
		go.setDuration(100);
		go.setInterpolator(new AccelerateInterpolator());
		return go;
	}

	public Animation getToSelfAnimation(int x, int y) {
		TranslateAnimation go = new TranslateAnimation(Animation.ABSOLUTE, x,
				Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, y,
				Animation.RELATIVE_TO_SELF, 0);
		go.setInterpolator(new AccelerateDecelerateInterpolator());
		go.setFillAfter(true);
		go.setDuration(100);
		go.setInterpolator(new AccelerateInterpolator());
		return go;
	}
}