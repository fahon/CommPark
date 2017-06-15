package w.song.orchid.util;

import com.google.gson.Gson;

import android.content.Context;
import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.activity.OBaseFragment;

public abstract class OBaseBusinessManager {
	protected Context mContext;
	protected OBaseActivity mOBaseActivity;
	protected OBaseFragment mOBaseFragment;
	protected OSharedPreferencesHelper mOSharedPreferencesHelper;
	protected MySQLHelper mMySQLHelper;
	protected Gson mGson;
	public OBaseBusinessManager(Context context) {
		mContext = context;
		initData();
	}

	public OBaseBusinessManager(OBaseActivity oBaseActivity) {
		mContext = oBaseActivity;
		mOBaseActivity = oBaseActivity;
		initData();
	}
	
	public OBaseBusinessManager(OBaseFragment oBaseFragment) {
		mContext = oBaseFragment.mContext;
		mOBaseFragment = oBaseFragment;
		//mOBaseActivity=(OBaseActivity)oBaseFragment.getActivity();
		initData();
	}
	
	public void initData() {
		mGson=new Gson();
		mOSharedPreferencesHelper=new OSharedPreferencesHelper(mContext);	
		mMySQLHelper=new MySQLHelper(mContext);
	}
}
