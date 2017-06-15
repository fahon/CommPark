package w.song.orchid.widget;

import w.song.orchid.impl.OViewRunStandard;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public abstract class OBaseLinearLayout extends LinearLayout implements OViewRunStandard {	
	public LayoutInflater mInflater;
	public View mContainer;
	public OBaseLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		getParams();
		mInflater=LayoutInflater.from(context);
		init(context, attrs);
		getComponent();
		setView();
		setListener();
		last();
	}

}
