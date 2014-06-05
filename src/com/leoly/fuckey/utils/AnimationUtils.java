/**
 * 
 */
package com.leoly.fuckey.utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * @author Administrator
 * 
 */
public class AnimationUtils {
	private static final Animation scaleAnimation = new ScaleAnimation(1f,
			0.7f, 1f, 0.7f, Animation.RELATIVE_TO_SELF, 0.5f,
			Animation.RELATIVE_TO_SELF, 0.5f);

	static {
		scaleAnimation.setDuration(100);
		scaleAnimation.setRepeatCount(1);
		scaleAnimation.setRepeatMode(AlphaAnimation.REVERSE);
	}

	public static Animation getDefaultAnimation() {
		return scaleAnimation;
	}
}
