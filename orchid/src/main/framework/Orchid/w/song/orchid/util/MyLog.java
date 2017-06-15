package w.song.orchid.util;

import android.util.Log;

public class MyLog {
	public static boolean PRINT_STATUS = true;

	public static void e(String tag, String msg) {
		if (PRINT_STATUS) {
			Log.e(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (PRINT_STATUS) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (PRINT_STATUS) {
			Log.i(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (PRINT_STATUS) {
			Log.v(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (PRINT_STATUS) {
			Log.w(tag, msg);
		}
	}

}
