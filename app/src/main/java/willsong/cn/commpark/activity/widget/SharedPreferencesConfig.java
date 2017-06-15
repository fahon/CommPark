package willsong.cn.commpark.activity.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.android.rfid.DevSettings;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 
 * @data 2016
 * author:rebecca
 *  
 */
public class SharedPreferencesConfig {
	public static ProgressDialog pd;
	// 键值对存储文件名称
	public final static String CONFIG_NAME = "CTY_CONFIG";
	public final static String CONFIG_NAME_USERINFO = "CTY_CONFIG_USERINFO";

	public synchronized Class<?> getDataConfig(Context context, Class<?> cls,
			String key) {
		// SharedPreferences sharedPreferences =
		// context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
		return cls;
	}

	/**
	 * 保存字符型数据
	 * 
	 * @param context
	 * @param keyName
	 * @param keyValue
	 */
	public synchronized static void saveStringConfig(Context context,
			String keyName, String keyValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				CONFIG_NAME, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(keyName, keyValue);
		editor.commit();
	}
	
	/**
	 * 保存用户数据到配置文件
	 * @param context
	 * @param data
	 */
	public synchronized static void saveUserInfoConfig(Context context,
			HashMap<String,Object> data) {
		try {
			if(data != null){
				SharedPreferences sharedPreferences = context.getSharedPreferences(
						CONFIG_NAME_USERINFO, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				
				Iterator<Entry<String, Object>> it = data.entrySet().iterator(); 
				while(it.hasNext()){
					Entry<String, Object> maps = it.next(); 
					editor.putString(maps.getKey(), maps.getValue()+"");
					
				}	
				editor.putString("savaTime", new Date().getTime()+"");
				editor.commit();
			}
		} catch (Exception e) {
			Log.i("savaData", e.toString());
		}
		
		
	}
	
	/**
	 * 得到用户信息配置文件数据
	 * @param context
	 * @param key
	 * @return
	 */
	public synchronized static String getUserInfoConfig(Context context,String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				CONFIG_NAME_USERINFO, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");
		
	}
	
	/**
	 * 读取用户名
	 */
	public synchronized static String readStringConfig(Context context) {
		String result = "";
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				CONFIG_NAME, Context.MODE_PRIVATE);
		result = sharedPreferences.getString("mobile", "");
		return result;
	}

	/**
	 * 保存整修数据
	 * 
	 * @param context
	 * @param keyName
	 * @param keyValue
	 */
	public synchronized static void saveInt(Context context,
			String keyName, Integer keyValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				CONFIG_NAME, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(keyName, keyValue);
		//editor.commit();
		editor.apply();
	}

	/**
	 * 保存bool型数据
	 * 
	 * @param context
	 * @param keyName
	 * @param keyValue
	 */
	public synchronized static void saveBoolConfig(Context context,
			String keyName, boolean keyValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				CONFIG_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(keyName, keyValue);
		editor.commit();
	}

	/**
	 * 获取字符型数据
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public synchronized static String getString(Context context,
			String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				CONFIG_NAME, Context.MODE_MULTI_PROCESS);
		return sharedPreferences.getString(key, "");
	}

	/**
	 * 获取整形数据
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public synchronized static int getIntConfig(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				CONFIG_NAME, Context.MODE_MULTI_PROCESS);
		return sharedPreferences.getInt(key, 0);
	}

	/**
	 * 获取bool数据
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public synchronized static boolean getBoolConfig(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				CONFIG_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, false);
	}

	/**
	 * 获取bool数据
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public synchronized static boolean getBoolConfig(Context context,
			String key, Boolean default_bool) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				CONFIG_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, default_bool);
	}

	public static String getEtValue(EditText editText){
		if(editText == null){
			return "";
		}
		else{
			return editText.getText().toString().trim();
		}

	}

	public static String getEtValue(TextView editText){
		if(editText == null){
			return "";
		}
		else{
			return editText.getText().toString().trim();
		}

	}

	//禁用系统按键
	public static void closeHome(Context context,boolean closeHomes){
		boolean closeHome = closeHomes;
		//禁用系统按键
		DevSettings dev = new DevSettings(context);
		if(closeHome ==true){
			dev.lockStatusBar(true);//禁用下拉
			dev.lockHome(true);//禁用HOME键
			dev.setMenuKey(true);//禁用菜单
		}else{
			dev.lockStatusBar(false);//开启下拉
			dev.lockHome(false);//启用HOME键
			dev.setMenuKey(false);//启用菜单
		}
	}
	//替换空格
	public static String replaceNull(String value){
		try {
			if(value.contains(" ")){
				value = value.replace(" ","");
			}
		}
		catch (Exception ex){

		}
		return value;
	}
	//替换空格及其他特殊字符(时间)
	public static String replaceAll(String value){
		try {
			if(value.contains(" ")){
				value = value.replace(" ","");
			}
			if(value.contains("-")){
				value = value.replace("-","");
			}
			if(value.contains(":")){
				value = value.replace(":","");
			}
		}
		catch (Exception ex){

		}
		return value;
	}
	//进度框
	public static void showProgressDialog(final String message,final Context context) {
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setTitle(message);
		pd.setMessage("请等待");
		pd.setIndeterminate(true);
		pd.setCancelable(true);
		pd.setCanceledOnTouchOutside(true);
		pd.show();
	}
	public static void closeProgressDialog() {
		if (pd.isShowing()&&pd!=null) {
			pd.dismiss();
		}
	}

	//判断网络连接
	public static boolean isOnline(Context context) {
		boolean isOnline = false;
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null) {
			if (networkInfo.isConnected()) {
				isOnline = true;
			}
		}
		return isOnline;
	}

}
