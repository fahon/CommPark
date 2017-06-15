package w.song.orchid.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import w.song.orchid.data.OField;

/**
 * android 数据存储工具类
 * 
 */
public class OSharedPreferencesHelper {

	SharedPreferences sp;
	SharedPreferences.Editor editor;
	Context context;

	public OSharedPreferencesHelper(Context context) {
		this(context, OField.SHARE_FILED_NAME);
	}

	@SuppressLint({ "CommitPrefEdits", "WorldReadableFiles" })
	@SuppressWarnings("deprecation")
	private OSharedPreferencesHelper(Context c, String name) {
		context = c;
		sp = context.getSharedPreferences(name, Context.MODE_WORLD_READABLE);

		editor = sp.edit();
	}

	public SharedPreferences getSharedPreferences() {
		return sp;
	}

	public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
		sp.registerOnSharedPreferenceChangeListener(listener);
	}

	public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
		sp.unregisterOnSharedPreferenceChangeListener(listener);
	}

	/**
	 * 存入值
	 * 
	 * @param key
	 * @param value
	 */
	public void putValue(String key, String value) {
		editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void putString(String key, String value) {
		editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void putInt(String key, int value) {
		editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void putLong(String key, long value) {
		editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public void putBoolean(String key, boolean value) {
		editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void putFloat(String key, float value) {
		editor = sp.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	/**
	 * 取出值
	 * 
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		return sp.getString(key, null);
	}

	/**
	 * 取出值
	 * 
	 * @param key
	 * @return
	 */
	// public String getValue(String key, String defValue)
	// {
	// return sp.getString(key, defValue);
	// }

	public String getString(String key, String defValue) {
		return sp.getString(key, defValue);
	}

	public int getInt(String key, int defValue) {
		return sp.getInt(key, defValue);
	}

	public long getLong(String key, long defValue) {
		return sp.getLong(key, defValue);
	}

	public boolean getBoolean(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}

	public float getFloat(String key, float defValue) {
		return sp.getFloat(key, defValue);
	}

	public void remove(String key) {
		editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}

	public void clear() {
		editor = sp.edit();
		editor.clear();
		editor.commit();
	}
}
