/**
 * 
 */
package com.leoly.fuckey.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.leoly.fuckey.R;
import com.leoly.fuckey.utils.Lg;

/**
 * @author culin003
 * 
 */
public class ChangLogActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changlog);
		EditText text = (EditText) findViewById(R.id.changeLogText);
		text.setEnabled(false);
		text.setFocusable(false);
		text.setTextSize(19);
		text.setBackgroundColor(Color.WHITE);
		text.setTextColor(Color.BLACK);
		InputStream in = ChangLogActivity.class
				.getResourceAsStream("changlog.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String temp = null;
		try {
			while ((temp = reader.readLine()) != null) {
				text.append(temp);
				text.append("\n");
			}
		} catch (Exception e) {
			text.append("Empty Change Log!");
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				Lg.error(null, e);
			}
		}
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
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
}
