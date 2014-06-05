package com.leoly.fuckey.views;

import android.app.Application;
import android.os.Handler;
import android.view.WindowManager;

/**
 * 
 * @author <a href="mailto:kris@krislq.com">Kris.lee</a>
 * @website www.krislq.com
 * @date Nov 29, 2012
 * @version 1.0.0
 * 
 */
public class FloatApplication extends Application {
	private WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
	private Handler handler;

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public WindowManager.LayoutParams getWindowParams() {
		return windowParams;
	}
}
