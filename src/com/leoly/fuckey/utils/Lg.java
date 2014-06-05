/**
 * 
 */
package com.leoly.fuckey.utils;

import android.util.Log;

import com.leoly.fuckey.constants.Cs;

/**
 * @author Administrator
 * 
 */
public class Lg {
	public static void info(String msg) {
		Log.i(Cs.APP_NAME, msg);
	}

	public static void warn(String msg, Throwable tr) {
		Log.w(Cs.APP_NAME, msg, tr);
	}

	public static void error(String msg, Throwable tr) {
		Log.e(Cs.APP_NAME, msg, tr);
	}
}
