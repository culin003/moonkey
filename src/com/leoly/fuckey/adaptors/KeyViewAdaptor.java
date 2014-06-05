/**
 * 
 */
package com.leoly.fuckey.adaptors;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.leoly.fuckey.constants.Cs;
import com.leoly.fuckey.utils.SharePool;
import com.leoly.fuckey.views.IKeyView;

/**
 * @author Leoly
 * 
 */
public class KeyViewAdaptor extends ImageView implements IKeyView {

	private String pkg;

	private String name;

	public KeyViewAdaptor(Context context) {
		super(context);
	}

	public KeyViewAdaptor(Context context, AttributeSet atrs) {
		super(context, atrs);
	}

	public KeyViewAdaptor(Context context, AttributeSet atrs, int defStyle) {
		super(context, atrs, defStyle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.leoly.fuckey.views.IKeyView#getPkg()
	 */
	@Override
	public String getPkg() {
		return this.pkg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.leoly.fuckey.views.IKeyView#setPkg(java.lang.String)
	 */
	@Override
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	protected void vibrate() {
		SharePool.get(Cs.VIBRATOR, VibratorAdaptor.class).vibrator();
	}

	public void setName(String nameTT) {
		this.name = nameTT;
	}

	public String getKeyName() {
		return this.name;
	}

}
