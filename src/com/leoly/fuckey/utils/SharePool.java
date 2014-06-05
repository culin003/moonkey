/**
 * 
 */
package com.leoly.fuckey.utils;

import android.os.Handler;
import android.util.SparseArray;

import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.key.KeyEventExecuter;

/**
 * @author Leoly
 * 
 */
public class SharePool {

	/**
	 * 共享池
	 */
	public static SparseArray<Object> shareObj = new SparseArray<Object>(3);

	public static void push(int key, Object value) {
		shareObj.put(key, value);
	}

	public static <T> T get(int key, Class<T> claz) {
		return claz.cast(shareObj.get(key));
	}

	public static KeyEventExecuter getExecuter() {
		return get(Cs.KEYEVENTEXECUTER, KeyEventExecuter.class);
	}

	public static Handler getHideHandler() {
		return get(Cs.HIDEHANDLER, Handler.class);
	}

	public static void remove(int key) {
		shareObj.remove(key);
	}

	public static void destroy() {
		shareObj.clear();
	}
}
