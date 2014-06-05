/**
 * 
 */
package com.leoly.fuckey.activities;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.leoly.fuckey.R;
import com.leoly.fuckey.adaptors.DragListAdapter;
import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.utils.Stuls;
import com.leoly.fuckey.utils.ViewUtils;
import com.leoly.fuckey.utils.ViewUtils.MyViewInfo;
import com.leoly.fuckey.views.DragListView;

/**
 * @author leoly
 * 
 */
public class KeySortedActivity extends Activity {
	private DragListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drag_list_main);
		ArrayList<MyViewInfo> data = new ArrayList<ViewUtils.MyViewInfo>(0);
		SharedPreferences prefer = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		boolean forceUpdate = getIntent().getExtras()
				.getBoolean("FORCE_UPDATE");
		// 强制更新时，说明已经修改了自定义按钮，否则，是单纯的排序操作
		if (forceUpdate) {
			Set<String> customKeys = prefer.getStringSet("custom_key", null);
			if (null == customKeys) {
				data = ViewUtils.getMyViews();
			} else {
				data = ViewUtils.getCustomInfos(customKeys);
			}
		} else {
			String sortedKeys = prefer.getString("custom_key_sorted", null);
			if (Stuls.isNotEmpty(sortedKeys)) {
				data = ViewUtils.getCustomInfos(sortedKeys);
			}
		}

		DragListView dragListView = (DragListView) findViewById(R.id.other_drag_list);
		adapter = new DragListAdapter(this, data);
		dragListView.setAdapter(adapter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(Cs.APP_NAME, "Save custom key setting!!");
		saveCustomKey();
	}

	private void saveCustomKey() {
		SharedPreferences prefer = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = prefer.edit();
		MyViewInfo info = null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < adapter.getCount(); i++) {
			info = (MyViewInfo) adapter.getItem(i);
			sb.append(info.getName()).append(Cs.KEY_SPLIT)
					.append(info.getPkg()).append(Cs.KEYS_SPLIT);
		}

		editor.putString("custom_key_sorted", sb.toString());
		editor.commit();
	}
}
