package w.song.orchid.widget;

import w.song.orchid.impl.OViewRunStandard;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public abstract class OBaseView extends View implements
		OViewRunStandard {

	public OBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getParams();
		init(context,attrs);
		getComponent();
		setView();
		setListener();
		last();
	}

}
