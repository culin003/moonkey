/**
 * 
 */
package com.leoly.fuckey.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.leoly.fuckey.adaptors.KeyViewAdaptor;
import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.views.BackKeyView;
import com.leoly.fuckey.views.CallKeyView;
import com.leoly.fuckey.views.CameraKeyView;
import com.leoly.fuckey.views.HomeKeyView;
import com.leoly.fuckey.views.MenuKeyView;
import com.leoly.fuckey.views.PowerKeyView;
import com.leoly.fuckey.views.RecentKeyView;
import com.leoly.fuckey.views.SettingKeyView;
import com.leoly.fuckey.views.VolumnAddKeyView;
import com.leoly.fuckey.views.VolumnSubKeyView;

/**
 * @author leoly
 * 
 */
public class ViewUtils {
	public static class MyViewInfo {
		private String name;
		private String pkg;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPkg() {
			return pkg;
		}

		public void setPkg(String pkg) {
			this.pkg = pkg;
		}
	}

	public static ArrayList<MyViewInfo> getMyViews() {
		ArrayList<MyViewInfo> infos = new ArrayList<MyViewInfo>();
		MyViewInfo info = new MyViewInfo();
		info.setName("返回键");
		info.setPkg(BackKeyView.class.getName());
		infos.add(info);

		info = new MyViewInfo();
		info.setName("拨号键");
		info.setPkg(CallKeyView.class.getName());
		infos.add(info);

		info = new MyViewInfo();
		info.setName("相机键");
		info.setPkg(CameraKeyView.class.getName());
		infos.add(info);

		info = new MyViewInfo();
		info.setName("主页键");
		info.setPkg(HomeKeyView.class.getName());
		infos.add(info);

		info = new MyViewInfo();
		info.setName("菜单键");
		info.setPkg(MenuKeyView.class.getName());
		infos.add(info);

		info = new MyViewInfo();
		info.setName("电源键");
		info.setPkg(PowerKeyView.class.getName());
		infos.add(info);

		info = new MyViewInfo();
		info.setName("任务键");
		info.setPkg(RecentKeyView.class.getName());
		infos.add(info);

		info = new MyViewInfo();
		info.setName("设置键");
		info.setPkg(SettingKeyView.class.getName());
		infos.add(info);

		info = new MyViewInfo();
		info.setName("音量增");
		info.setPkg(VolumnAddKeyView.class.getName());
		infos.add(info);

		info = new MyViewInfo();
		info.setName("音量减");
		info.setPkg(VolumnSubKeyView.class.getName());
		infos.add(info);
		return infos;
	}

	private static ArrayList<MyViewInfo> getSysViews(Context context) {
		ArrayList<MyViewInfo> infos = new ArrayList<MyViewInfo>();
		PackageManager pkm = context.getPackageManager();
		List<ApplicationInfo> apps = pkm
				.getInstalledApplications(PackageManager.GET_ACTIVITIES);
		Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(pkm));

		MyViewInfo info = null;
		for (ApplicationInfo app : apps) {
			if (app.enabled && !app.packageName.startsWith("com.leoly.fuckey")) {
				info = new MyViewInfo();
				info.setName(app.loadLabel(pkm).toString());
				info.setPkg(app.packageName);
				infos.add(info);
			}
		}

		return infos;
	}

	public static String[][] getAllViews(Context context) {
		String[][] results = new String[2][];
		ArrayList<MyViewInfo> myViews = getMyViews();
		myViews.addAll(getSysViews(context));
		int index = 0;
		int sudex = 0;
		String[] names = new String[myViews.size()];
		String[] pkgs = new String[myViews.size()];
		for (MyViewInfo info : myViews) {
			names[index++] = info.getName();
			pkgs[sudex++] = info.getName() + Cs.KEY_SPLIT + info.getPkg();
		}

		results[0] = names;
		results[1] = pkgs;
		myViews.clear();
		return results;
	}

	public static ArrayList<MyViewInfo> getCustomInfos(String sortedKeys) {
		ArrayList<MyViewInfo> infos = new ArrayList<ViewUtils.MyViewInfo>();
		String[] keys = sortedKeys.split(Cs.KEYS_SPLIT);
		String[] keyInfo = null;
		MyViewInfo info = null;
		for (String key : keys) {
			info = new MyViewInfo();
			keyInfo = key.split(Cs.KEY_SPLIT);
			info.setName(keyInfo[0]);
			info.setPkg(keyInfo[1]);
			infos.add(info);
		}

		return infos;
	}

	public static ArrayList<MyViewInfo> getCustomInfos(Set<String> customKeys) {
		ArrayList<MyViewInfo> infos = new ArrayList<ViewUtils.MyViewInfo>();
		MyViewInfo info = null;
		String[] keyInfo = null;
		for (String key : customKeys) {
			info = new MyViewInfo();
			keyInfo = key.split(Cs.KEY_SPLIT);
			info.setName(keyInfo[0]);
			info.setPkg(keyInfo[1]);
			infos.add(info);
		}

		return infos;
	}

	public static ArrayList<KeyViewAdaptor> getActureViews(String sortedKeys,
			final Context context) {
		ArrayList<KeyViewAdaptor> views = new ArrayList<KeyViewAdaptor>();
		if (Stuls.isEmpty(sortedKeys)) {
			return getDefaultViews(views, context);
		}
		String[] keys = sortedKeys.split(Cs.KEYS_SPLIT);
		String[] keyInfo = null;
		String pkg = null;
		KeyViewAdaptor a = null;
		Class c = null;
		final PackageManager pkm = context.getPackageManager();
		for (String key : keys) {
			keyInfo = key.split(Cs.KEY_SPLIT);
			pkg = keyInfo[1];
			if (keyInfo[1].startsWith("com.leoly.fuckey")) {
				try {
					c = Class.forName(pkg);
					a = (KeyViewAdaptor) c.getConstructor(Context.class)
							.newInstance(context);
					views.add(a);
				} catch (Exception e) {
					Log.e(Cs.APP_NAME, e.getMessage(), e);
				}
			} else {
				final KeyViewAdaptor k = new KeyViewAdaptor(context);
				final String pkgName = pkg;
				try {
					k.setImageDrawable(pkm.getApplicationIcon(pkg));
					k.setOnClickListener(new OnClickListener() {
						public void onClick(View view) {
							k.startAnimation(AnimationUtils
									.getDefaultAnimation());
							Intent intent = pkm
									.getLaunchIntentForPackage(pkgName);
							try {
								context.startActivity(intent);
							} catch (Exception e) {
								Log.e(Cs.APP_NAME, e.getMessage(), e);
							}
						}
					});
					views.add(k);
				} catch (NameNotFoundException e) {
					Log.e(Cs.APP_NAME, e.getMessage(), e);
				}
			}
		}

		return views;
	}

	private static ArrayList<KeyViewAdaptor> getDefaultViews(
			ArrayList<KeyViewAdaptor> views, Context context) {
		BackKeyView back = new BackKeyView(context);
		views.add(back);
		HomeKeyView home = new HomeKeyView(context);
		views.add(home);
		RecentKeyView recent = new RecentKeyView(context);
		views.add(recent);
		MenuKeyView menu = new MenuKeyView(context);
		views.add(menu);
		PowerKeyView power = new PowerKeyView(context);
		views.add(power);
		return views;
	}
}
