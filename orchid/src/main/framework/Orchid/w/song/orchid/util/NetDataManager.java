package w.song.orchid.util;

import android.content.Context;
import android.util.Log;

import java.util.Map;

import w.song.orchid.data.Dic;

public class NetDataManager extends OBaseBusinessManager {
	public NetDataManager(Context context) {
		super(context);
	}

	public final static String TAG = "NetDataManager";
	public final static int TIMEOUT_CONNECTION = 3 * 1000;

	public final static boolean isCodeShow = true;

	public static class SAVE_STATE {
		public final static String SAVE = "save";// 存本地
		public final static String UNSAVE = "unsave";// 未存本地
	}

	public String[] post(String url ,Map<String,Object> map) {
		if (!Http.isConnect(mContext)) {
			return new String[] { "" + Http.EXC, isCodeShow ? Dic.PROMPT1 + "<br>" + "编号" + Http.EXC : Dic.PROMPT1 };
		}
		Http hp = new Http();
		String[] info = hp.ctSendRequest(url, mGson.toJson(map), Http.CtSendRequestType.POST, TIMEOUT_CONNECTION);
		Log.d("ccm",mGson.toJson(map));
		if (("" + info[0]).equals("" + Http.HTTP_OK)) {// 访问正常
			return info;
		} else {// 访问异常
			return new String[] { "" + Http.EXC, isCodeShow ? Dic.PROMPT0 + "<br>" + "编号" + info[0] : Dic.PROMPT0 };
		}
	}

	public String[] get(String url ,Map<String,Object> map) {
		if (!Http.isConnect(mContext)) {
			return new String[] { "" + Http.EXC, isCodeShow ? Dic.PROMPT1 + "<br>" + "编号" + Http.EXC : Dic.PROMPT1 };
		}
		Http hp = new Http();
		String[] info = hp.ctSendRequest(url, mGson.toJson(map), Http.CtSendRequestType.GET, TIMEOUT_CONNECTION);
		if (("" + info[0]).equals("" + Http.HTTP_OK)) {// 访问正常
			return info;
		} else {// 访问异常
			return new String[] { "" + Http.EXC, isCodeShow ? Dic.PROMPT0 + "<br>" + "编号" + info[0] : Dic.PROMPT0 };
		}
	}

	public String[] put(String url ,Map<String,Object> map) {
		if (!Http.isConnect(mContext)) {
			return new String[] { "" + Http.EXC, isCodeShow ? Dic.PROMPT1 + "<br>" + "编号" + Http.EXC : Dic.PROMPT1 };
		}
		Http hp = new Http();
		String[] info = hp.ctSendRequest(url, mGson.toJson(map), Http.CtSendRequestType.PUT, TIMEOUT_CONNECTION);
		if (("" + info[0]).equals("" + Http.HTTP_OK)) {// 访问正常
			return info;
		} else {// 访问异常
			return new String[] { "" + Http.EXC, isCodeShow ? Dic.PROMPT0 + "<br>" + "编号" + info[0] : Dic.PROMPT0 };
		}
	}
}
