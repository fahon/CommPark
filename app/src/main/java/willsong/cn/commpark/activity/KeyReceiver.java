package willsong.cn.commpark.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KeyReceiver extends BroadcastReceiver {

	private String TAG = "KeyReceiver" ;
	@Override
	public void onReceive(Context context, Intent intent) {
		int keyCode = intent.getIntExtra("keyCode", 0) ;
		boolean keyDown = intent.getBooleanExtra("keydown", false) ;
//		Log.e(TAG, "KEYcODE = " + keyCode + ", Down = " + keyDown);

	}

}
