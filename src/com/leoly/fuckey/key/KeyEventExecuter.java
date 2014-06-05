/**
 * 
 */
package com.leoly.fuckey.key;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;

import com.leoly.fuckey.utils.Lg;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

/**
 * @author Administrator
 * 
 */
public class KeyEventExecuter {

	private InputShell mInputShell;

	private Context context;

	private static Shell rootShell;

	public KeyEventExecuter(Context context) {
		this.context = context;
		try {
			rootShell = RootTools.getShell(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runRootCmd(String cmd) {
		Command command = new CommandCapture(0, cmd);
		try {
			rootShell.add(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public InputShell getInputShell() {
		AssetManager localAssetManager;
		InputStream localInputStream = null;
		FileOutputStream localFileOutputStream = null;
		byte[] arrayOfByte;
		if (this.mInputShell == null) {
			String filePath = context.getFilesDir().getAbsolutePath();
			File localFile = new File(filePath + "/input2_jb.jar");
			localAssetManager = context.getResources().getAssets();
			arrayOfByte = new byte[4096];
			try {
				localInputStream = localAssetManager.open("input2_jb.jar");
				localFileOutputStream = new FileOutputStream(localFile);
				int i = localInputStream.read(arrayOfByte);
				localFileOutputStream.write(arrayOfByte, 0, i);
			} catch (Exception e) {
				Lg.error(null, e);
			} finally {
				try {
					localFileOutputStream.close();
					localInputStream.close();
					this.mInputShell = new InputShell("su", filePath);
				} catch (Exception e) {
					Lg.error(null, e);
				}
			}
		}

		return mInputShell;
	}

	public int sendDownTouchKeys(int paramInt) {
		try {
			getInputShell().runCommand("down " + paramInt);
			return 0;
		} catch (Exception localException) {
			Lg.error(null, localException);
			localException.printStackTrace();
		}
		return 1;
	}

	public int sendDownTouchKeys(int paramInt1, int paramInt2) {
		try {
			getInputShell().runCommand("downr " + paramInt1 + " " + paramInt2);
			return 0;
		} catch (Exception localException) {
			Lg.error(null, localException);
			localException.printStackTrace();
		}
		return 1;
	}

	public void showTask() {
		try {
			getInputShell().runCommand("task");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int sendKeys(final int paramInt) {
		try {
			new Thread(new Runnable() {
				public void run() {
					try {
						getInputShell().runCommand("down " + paramInt);
						Thread.sleep(100L);
						getInputShell().runCommand("up " + paramInt);
						return;
					} catch (Exception localException) {
						Lg.error(null, localException);
					}
				}
			}).start();
			return 0;
		} catch (Exception localException) {
			Lg.error(null, localException);
			localException.printStackTrace();
		}
		return 1;
	}

	public int sendUpTouchKeys(int paramInt) {
		try {
			getInputShell().runCommand("up " + paramInt);
			return 0;
		} catch (Exception localException) {
			Lg.error(null, localException);
			localException.printStackTrace();
		}
		return 1;
	}

	public class InputShell {
		private OutputStream o;
		private Process p;

		InputShell(String paramString1, String str) throws Exception {
			this.p = Runtime.getRuntime().exec(paramString1);
			this.o = this.p.getOutputStream();
			system("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
			system("export CLASSPATH=" + str + "/input2_jb.jar");
			if (Build.VERSION.SDK_INT >= 19) {
				system("/system/bin/setenforce 0");
			}

			system("exec app_process " + str + " com.smart.swkey.input");
		}

		private void system(String paramString) throws Exception {
			this.o.write((paramString + "\n").getBytes("ASCII"));
		}

		public void close() throws Exception {
			this.o.flush();
			this.o.close();
			this.p.destroy();
			if (null != rootShell) {
				RootTools.closeShell(true);
				RootTools.closeAllShells();
			}
		}

		public void runCommand(String paramString) throws Exception {
			system(paramString);
		}
	}
}
