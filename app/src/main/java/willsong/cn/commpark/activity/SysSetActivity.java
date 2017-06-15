package willsong.cn.commpark.activity;

import android.app.Activity;
import android.os.Bundle;

import w.song.orchid.activity.OBaseActivity;
import willsong.cn.commpark.R;

public class SysSetActivity extends OBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_sys_set);
    }
}
