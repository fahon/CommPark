package w.song.orchid.activity;

import w.song.orchid.impl.OActRunStandard;
import android.os.Bundle;

public abstract class OrchidActivity extends OBaseActivity implements OActRunStandard {

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
