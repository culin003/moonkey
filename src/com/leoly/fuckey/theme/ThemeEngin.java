/**
 * 
 */
package com.leoly.fuckey.theme;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.leoly.fuckey.R;
import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.utils.Lg;
import com.leoly.fuckey.utils.SharePool;

/**
 * @author leoly
 * 
 */
public class ThemeEngin {
	private static final class ThemeEnginCreater {
		public static final ThemeEngin themeEngin = new ThemeEngin();
	}

	public static ThemeEngin getInstance() {
		return ThemeEnginCreater.themeEngin;
	}

	public Bitmap getImage(int key) {
		return SharePool.get(key, Bitmap.class);
	}

	public void initEngin(String zipFilePath, Resources res) {
		if (null == zipFilePath || "".equals(zipFilePath.trim())
				|| !zipFilePath.endsWith(".zip")) {
			setDefaultTheme(res);
		}

		ZipFile zip = null;
		try {
			zip = new ZipFile(zipFilePath);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			if (null == entries || !entries.hasMoreElements()) {
				throw new Exception();
			}

			ZipEntry entry = null;
			InputStream is = null;
			while (entries.hasMoreElements()) {
				entry = entries.nextElement();
				is = zip.getInputStream(entry);
				SharePool.push(Integer.valueOf(entry.getName()),
						BitmapFactory.decodeStream(is));
				is.close();
			}
		} catch (Exception e) {
			Lg.error("Initialize ThemeEngin failed!! Use default theme!!", e);
			setDefaultTheme(res);
		} finally {
			try {
				zip.close();
			} catch (IOException e) {
				Lg.error("Initialize ThemeEngin failed!! Use default theme!!",
						e);
			}
		}
	}

	private void setDefaultTheme(Resources res) {
		SharePool.push(Cs.BACK_KEY,
				BitmapFactory.decodeResource(res, R.drawable.back));
		SharePool.push(Cs.CALL_KEY,
				BitmapFactory.decodeResource(res, R.drawable.call));
		SharePool.push(Cs.CAMERA_KEY,
				BitmapFactory.decodeResource(res, R.drawable.camera));
		SharePool.push(Cs.HOME_KEY,
				BitmapFactory.decodeResource(res, R.drawable.home));
		SharePool.push(Cs.MENU_KEY,
				BitmapFactory.decodeResource(res, R.drawable.menu));
		SharePool.push(Cs.POWER_KEY,
				BitmapFactory.decodeResource(res, R.drawable.poweroff));
		SharePool.push(Cs.RECENT_KEY,
				BitmapFactory.decodeResource(res, R.drawable.task));
		SharePool.push(Cs.SETTING_KEY,
				BitmapFactory.decodeResource(res, R.drawable.ic_actions));
		SharePool.push(Cs.V_DOWN_KEY,
				BitmapFactory.decodeResource(res, R.drawable.volume_down));
		SharePool.push(Cs.V_UP_KEY,
				BitmapFactory.decodeResource(res, R.drawable.volume_up));
		SharePool.push(Cs.FLOAT_KEY,
				BitmapFactory.decodeResource(res, R.drawable.paopao));
		// SharePool.push(Cs.BACKGROUND_IMG,
		// BitmapFactory.decodeResource(res,R.drawable.volume_down));
		SharePool.push(Cs.PROFILE_IMG, R.drawable.default_profile);
	}

	public void destroyThemeCache() {
		for (int i = Cs.BACK_KEY; i <= Cs.PROFILE_IMG; i++) {
			SharePool.remove(i);
		}
	}
}
