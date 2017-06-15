package w.song.orchid.httpService;

import java.io.File;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.util.Log;

public class HTTP {
	private static ObjectMapper mapper = new ObjectMapper(); 

	public static Object serverCall(Context context,String name, Map<String, Object> params,
			ServerCallback callback) {
		Log.d("server call-->", "-1");
		return ServerEngine.getSEInstance().serverCall(context,name, params, callback);
	}
	
	public static Object serverCall(Context context,String url,
			ServerCallback callback) {
		return ServerEngine.getSEInstance().serverCall(context,url,callback);
	}
	
	public static Object serverCall(Context context,String url,Map<String, Object> params, String fileName, File file,
			ServerCallback callback) {
		return ServerEngine.getSEInstance().serverCall(context,url, params, fileName, file, callback);
	}	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object fromJson(String json_string, Class c) {
		Object object = null;
		try {
//			mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
			object = mapper.readValue(json_string, c);						
		} catch (Exception e) {
			object = null;
			Log.d("fromJson", e.toString());			
		}
		return object;
	}	

	
}
