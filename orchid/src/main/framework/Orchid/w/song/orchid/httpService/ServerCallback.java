package w.song.orchid.httpService;

import java.util.Map;

/**
 * @author shiyouyuan
 * 
 */
public interface ServerCallback {
	public boolean serverCallback(String name, Object data,
								  int code, String desc, String json,
								  Map<String, Object> request_params);

}
