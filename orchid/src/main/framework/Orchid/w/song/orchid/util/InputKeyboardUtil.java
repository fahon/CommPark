package w.song.orchid.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class InputKeyboardUtil {
	/**
	 * 收起软键盘
	 */
	public static void collapseSoftInputMethod(Context context,View view) {
		
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

	/**
	 * 显示软键盘
	 */
	public static void showSoftInputMethod(Context context,EditText inputText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(inputText, InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}
	
	public static boolean isActive(Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.isActive();
	}
}
