/**
 * 
 */
package com.leoly.fuckey.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.key.KeyEventExecuter;

import android.util.Log;

/**
 * @author Administrator
 * 
 */
public class Stuls {
	public static boolean isEmpty(String source) {
		return null == source || source.isEmpty() || source.trim().isEmpty();
	}

	public static boolean isNotEmpty(String source) {
		return !isEmpty(source);
	}

	public static boolean modifyBuildFile(KeyEventExecuter executer) {
		BufferedReader br = null;
		boolean result = false;
		try {
			File file = new File("/system/build.prop");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-8"));
			String line = null;
			boolean isExist = false;
			while (null != (line = br.readLine())) {
				if (line.startsWith("qemu.hw.mainkeys")) {
					isExist = true;
					break;
				}
			}

			if (isExist) {
				executer.runRootCmd("sed 's/^qemu.hw.mainkeys=./qemu.hw.mainkeys=1/g' /system/build.prop > /system/temp_moonkey.prop");
				executer.runRootCmd("mv /system/build.prop /system/build.prop.moonkeybak");
				executer.runRootCmd("mv /system/temp_moonkey.prop /system/build.prop");
			} else {
				executer.runRootCmd("echo qemu.hw.mainkeys=1 >> /system/build.prop");
			}

			result = true;
		} catch (Exception e) {
			result = false;
			Log.e(Cs.APP_NAME, "Modify build.prop error!!", e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				Log.e(Cs.APP_NAME, "Modify build.prop error!!", e);
			}
		}

		return result;
	}
}
