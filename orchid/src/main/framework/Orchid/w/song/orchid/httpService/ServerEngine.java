package w.song.orchid.httpService;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author syy
 * 
 */
public class ServerEngine {
	public static final int OUTTIME = 15000;
	public static final String REQUEST_GET = "GET";
	public static final String REQUEST_POST = "POST";
	private static ServerEngine se;
	private static AsyncHttpClient client; 
	private final String URLHEAD = "http:";

	private ServerEngine(){
		
	}
	
	public static ServerEngine getSEInstance() {
        if (se == null) {
        	se = new ServerEngine();  
        }
        if (client == null) {
        	client = new AsyncHttpClient();
        }
       return se;
   } 

	public  Object serverCall(Context context,String name, Map<String, Object> params,
			ServerCallback callback) {
//		client.setTimeout(OUTTIME);
//		client.post(serverCallUrl(name), encodeCmd(context,params), getHttpResponseHandler(REQUEST_POST,context,name,params,callback));
		setClientParams(context,name,REQUEST_POST,encodeCmd(context,params),params,callback);
		return client;
	}
	
	
	public Object serverCall(Context context,String name, Map<String, Object> params,String fileName,File file,
			ServerCallback callback) {

//		client.setTimeout(OUTTIME);
//		client.post(serverCallUrl(name), encodeCmd(context,params, fileName, file), getHttpResponseHandler(REQUEST_POST,context,name,params,callback));
		setClientParams(context,name,REQUEST_POST,encodeCmd(context,params, fileName, file),params,callback);
		return client;
	}
	
	public Object serverCall(Context context,String name,ServerCallback callback) {

//		client.setTimeout(OUTTIME);
//		client.get(serverCallUrl(name), getHttpResponseHandler(REQUEST_GET,context,name,null,callback));
		setClientParams(context,name,REQUEST_GET,null,null,callback);
		return client;
	}

	private static void setClientParams(Context context,String name,String requestClass,RequestParams requestParams,Map<String, Object> params,ServerCallback callback){
		client.setTimeout(OUTTIME);
		if(requestClass.equals(REQUEST_POST)){
			client.post(serverCallUrl(name), requestParams, getHttpResponseHandler(requestClass,context,name,params,callback));
		}
		else{
			client.get(serverCallUrl(name), getHttpResponseHandler(requestClass,context,name,null,callback));
		}

	}

	private static HttpResponseHandler getHttpResponseHandler(String requestClass,Context context,String name,Map<String, Object> params,ServerCallback callback){
		HttpResponseHandler handler = new HttpResponseHandler();
		if (params == null)
			params = new HashMap<String, Object>();
		handler.setContext(context);
		handler.setCallName(name);
		Log.d("HttpResponseHandler",params.toString());
		handler.setCallParams(params);
		handler.setCallback(callback);
		client.setTimeout(OUTTIME);
		handler.setRequestWay(requestClass);
		return handler;
	}

	private static RequestParams encodeCmd(Context context,Map<String, Object> data) {
		RequestParams request = new RequestParams();
		
		if (data == null) {
			return request;
		}
		Iterator<String> iter = data.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Object value = data.get(key);
			if (value == null) {
				value = "";
			}
			request.put(key, value.toString());
		}	
		return request;
	}

	

	private static RequestParams encodeCmd(Context context,Map<String, Object> data,String fileName,File file) {
		try {
			RequestParams request = new RequestParams();		
			Iterator<String> iter = data.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				Object value = data.get(key); 
				if (value == null) {
					value = "";
				}
				request.put(key, value.toString());
			}
			if(null != file){
				request.put(fileName, file);
			}			
			return request;
		} catch (Exception e) {
			return null;
		}
	}

//	private static RequestParams
	

	private static String serverCallUrl(String cmd_id) {
		Log.d("urlLog", cmd_id);
		return cmd_id;
	}	

	@SuppressWarnings("unchecked")
	public static Map<String, Object> decodeCmd(String content) {	

		Map<String, Object> result = (Map<String, Object>) HTTP.fromJson(content,Map.class);
		return result;
	}
	

}