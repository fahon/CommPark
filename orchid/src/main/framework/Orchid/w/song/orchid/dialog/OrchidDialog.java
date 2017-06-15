package w.song.orchid.dialog;

import w.song.orchid.impl.ODiaRunStandard;
import android.content.Context;
import android.os.Bundle;

public abstract class OrchidDialog extends BaseDialog implements ODiaRunStandard {

	public OrchidDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public OrchidDialog(Context context, int theme) {
		super(context, theme);
	}

	public OrchidDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getParams();
		init(savedInstanceState);
		getComponent();
		setView();
		setListener();
		last();
	}
}
