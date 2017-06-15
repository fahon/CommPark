package w.song.orchid.httpService;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.Map;

public class HttpResponseHandler extends AsyncHttpResponseHandler {
	private static final String TAG = "HttpResponseHandler";

	private static final String STATUS = "status";
	private static final String MSG = "msg";
	private static final String DATA = "data";
	private static final int CODE1 = -1;// callback is null
	private static final int CODE2 = -2;// data format is error

	private ServerCallback callback;

	private String callName;
	
	private Context context;

	private Map<String, Object> callParams;

	private String requestWay;


	public void setRequestWay(String requestWay) {
		this.requestWay = requestWay;
	}

	public void setCallback(ServerCallback callback) {
		this.callback = callback;
	}

	public void setCallName(String name) {
		this.callName = name;
	}

	public void setCallParams(Map<String, Object> params) {
		this.callParams = params;
	}
	
	

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void onSuccess(int arg0, Header[] arg1, byte[] response_byte) {
		Log.d(TAG, new String(response_byte) + "-");
//		final String response = StringEscapeUtils.unescapeJava(new String(response_byte));
		final String response = new String(response_byte); 
//		LogUtil.d("getCode", "|"+response.substring(0, 2)+"|");
		Log.d(TAG, response + "-");
		if (response.length() == 0) {
			callbackIsNull();
		}

		asynDealData(response);
	}

	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		Log.d(TAG, "response error:" + arg3.toString() + "..." + callback);
		if (null != callback) {
			callback.serverCallback(callName, null,
					HttpCodeState.SERVER_CONNECTION_FAILED,
					"server connection failed","", callParams);
		}

	}

	/**
	 * Asynchronous processing network the data returned
	 * 
	 * @param response
	 */
	private void asynDealData(final String response) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, Object> responses = ServerEngine.decodeCmd(response);
				Log.d(TAG, "response-->" + responses);

//				if(StringUtil.getKeyValue(responses.get("resultStatus")).equals("1103")){
//					context.startService(new Intent(context, LoginService.class));
//				}

				if(null == callback){
					callbackIsNull();
					return;
				}
				if (responses == null && response.length() > 0) {
					callback.serverCallback(callName, responses,
							-1, "",response, callParams);
					return;
				}
				callback.serverCallback(callName, responses,
						0, "",response, callParams);
				
			}

		}).start();
	}

	private void callbackIsNull() {
		if (null != callback) {
			callback.serverCallback(callName, null, CODE1, null, "",callParams);
			return;
		} else {
			return;
		}
	}

}
