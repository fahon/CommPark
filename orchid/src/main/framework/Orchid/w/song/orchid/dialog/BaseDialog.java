package w.song.orchid.dialog;

import android.app.Dialog;
import android.content.Context;

public abstract class BaseDialog extends Dialog {
    public Context mContext;
	public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		mContext=context;
	}

	public BaseDialog(Context context, int theme) {
		super(context, theme);
		mContext=context;
	}
	
	public BaseDialog(Context context) {		
		super(context);
		mContext=context;
	}
	
}
