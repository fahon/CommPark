package willsong.cn.commpark.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;

/**
 * 修改密码
 * Created by guof on 2016/12/14.
 */

public class ModifyPwdActivity extends OBaseActivity {
    private EditText et_oldpwd,et_newpwd;
    private Button bt_commitpwd;
    private String flag = "",oldpwd = "",newpwd = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_modifypwd);
        init();
    }
    private void init(){
        setRightButtonVisible(false);
        setTitleText("修改密码");
        et_oldpwd = (EditText) findViewById(R.id.et_oldpwd);
        et_newpwd = (EditText) findViewById(R.id.et_newpwd);
        bt_commitpwd = (Button) findViewById(R.id.bt_commitpwd);
        try{
            flag = getIntent().getStringExtra("flag");
            if(flag.equals("1")){
                oldpwd = SharedPreferencesConfig.getString(ModifyPwdActivity.this,"setpwd");
                if(oldpwd.equals("")){
                    oldpwd = "111111";
                }
            }else if(flag.equals("2")){
                oldpwd = SharedPreferencesConfig.getString(ModifyPwdActivity.this,"systempwd");
                if(oldpwd.equals("")){
                    oldpwd = "admin";
                }
            }else if(flag.equals("3")){
                oldpwd = SharedPreferencesConfig.getString(ModifyPwdActivity.this,"exitpwd");
                if(oldpwd.equals("")){
                    oldpwd = "222222";
                }
            }else{
                oldpwd = "";
            }
        }catch (Exception e){
            flag = "";
        }

        bt_commitpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedPreferencesConfig.getEtValue(et_oldpwd).equals("")) {
                    MyTools.showToastShort(true, "请输入旧密码", mContext);
                    return;
                }
                if(SharedPreferencesConfig.getEtValue(et_newpwd).equals("")){
                    MyTools.showToastShort(true, "请输入新密码", mContext);
                    return;
                }
                if(!SharedPreferencesConfig.getEtValue(et_oldpwd).equals(oldpwd)){
                    MyTools.showToastShort(true, "旧密码输入不正确", mContext);
                    return;
                }
                newpwd = SharedPreferencesConfig.getEtValue(et_newpwd);
                if(flag.equals("1")){
                    SharedPreferencesConfig.saveStringConfig(ModifyPwdActivity.this,"setpwd",newpwd);
                }else if(flag.equals("2")){
                    SharedPreferencesConfig.saveStringConfig(ModifyPwdActivity.this,"systempwd",newpwd);
                }else if(flag.equals("3")){
                    SharedPreferencesConfig.saveStringConfig(ModifyPwdActivity.this,"exitpwd",newpwd);
                }
                MyTools.showToastShort(true, "密码修改成功", mContext);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                finish();

            }
        });
    }
}
