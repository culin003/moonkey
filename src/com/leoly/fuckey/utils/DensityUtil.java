/**
 * 
 */
package com.leoly.fuckey.utils;

import android.util.DisplayMetrics;

/**
 * @author Administrator
 * 
 */
public class DensityUtil {

	public static DisplayMetrics metrics;

	public static final void reset(DisplayMetrics tempMetrics) {
		metrics = tempMetrics;
	}

	public static final float getHeightInPx() {
		return metrics.heightPixels;
	}

	public static final float getWidthInPx() {
		return metrics.widthPixels;
	}

	private static final float getDensity() {
		return metrics.density;
	}

	public static final int getHeightInDp() {
		int heightInDp = px2dip(getHeightInPx());
		return heightInDp;
	}

	public static final int getWidthInDp() {
		int widthInDp = px2dip(getHeightInPx());
		return widthInDp;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		final float scale = getDensity();
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float pxValue) {
		final float scale = getDensity();
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(float pxValue) {
		final float scale = getDensity();
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(float spValue) {
		final float scale = getDensity();
		return (int) (spValue * scale + 0.5f);
	}
}
