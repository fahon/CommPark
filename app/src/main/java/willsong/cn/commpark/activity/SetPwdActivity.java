package willsong.cn.commpark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import w.song.orchid.activity.OBaseActivity;
import willsong.cn.commpark.R;

/**
 * 设置密码
 * Created by guof on 2016/12/14.
 */

public class SetPwdActivity extends OBaseActivity implements View.OnClickListener{
    private LinearLayout modify_setpwd,modify_devicepwd,modify_exitpwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_setpwd);
        init();
    }
    private void init(){
        setRightButtonVisible(false);
        setTitleText("密码设置");
        modify_setpwd = (LinearLayout) findViewById(R.id.modify_setpwd);
        modify_devicepwd = (LinearLayout) findViewById(R.id.modify_devicepwd);
        modify_exitpwd = (LinearLayout) findViewById(R.id.modify_exitpwd);
        modify_setpwd.setOnClickListener(this);
        modify_devicepwd.setOnClickListener(this);
        modify_exitpwd.setOnClickListener(this);
    }
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.modify_setpwd:
                final Intent intent = new Intent(getApplicationContext(), ModifyPwdActivity.class);
                intent.putExtra("flag","1");
                startActivity(intent);
                break;
            case R.id.modify_devicepwd:
                final Intent intent2 = new Intent(getApplicationContext(), ModifyPwdActivity.class);
                intent2.putExtra("flag","2");
                startActivity(intent2);
                break;
            case R.id.modify_exitpwd:
                final Intent intent3 = new Intent(getApplicationContext(), ModifyPwdActivity.class);
                intent3.putExtra("flag","3");
                startActivity(intent3);
                break;
        }
    }
}
