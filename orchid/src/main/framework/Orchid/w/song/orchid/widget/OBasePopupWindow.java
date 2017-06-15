package w.song.orchid.widget;

import android.view.View;
import android.widget.PopupWindow;

public abstract class OBasePopupWindow extends PopupWindow {
	
	public OBasePopupWindow(View contentView, int width, int height) {
		super(contentView, width, height);
	}

}
