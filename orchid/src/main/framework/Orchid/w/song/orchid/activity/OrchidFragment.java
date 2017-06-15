package w.song.orchid.activity;

import w.song.orchid.impl.OFragRunStandard;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class OrchidFragment extends OBaseFragment implements OFragRunStandard {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		getParams();
		View view = init(inflater, container, savedInstanceState);
		getComponent();
		setView();
		setListener();
		last();
		return view;
	}

}
