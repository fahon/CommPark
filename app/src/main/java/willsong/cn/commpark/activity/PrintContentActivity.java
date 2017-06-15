package willsong.cn.commpark.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import w.song.orchid.activity.OBaseActivity;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;

/**
 * 设置打印头部，尾部
 * Created by guof on 2016/12/14.
 */

public class PrintContentActivity extends OBaseActivity {
    private EditText et_title,et_company,et_tel,et_enter_title,et_exit_title,et_enter_bike_title,et_exit_bike_title;//头部标题，尾部公司名，尾部电话
    private Button bt_ok;//确认
    String title = "",coName = "",tel = "";
    String enterTitle = "",exitTitle = "",enterBikeTitle="",exitBikeTitle="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_printcontent);
        init();
    }
    private void init() {
        setRightButtonVisible(false);
        setTitleText("打印内容设置");
        et_title = (EditText) findViewById(R.id.et_title);
        et_company = (EditText) findViewById(R.id.et_company);
        et_tel = (EditText) findViewById(R.id.et_tel);
        et_enter_title = (EditText) findViewById(R.id.et_enter_title);
        et_exit_title = (EditText) findViewById(R.id.et_exit_title);
        et_enter_bike_title = (EditText) findViewById(R.id.et_enter_bike_title);
        et_exit_bike_title = (EditText) findViewById(R.id.et_exit_bike_title);

        title = mBusinessManager.getIntentContent();
        coName = mBusinessManager.getIntentEndCo();
        tel = mBusinessManager.getIntentEndTel();
        enterTitle = mBusinessManager.getEnterTitle();
        exitTitle = mBusinessManager.getExitTitle();
        enterBikeTitle = mBusinessManager.getEnterBikeTitle();
        exitBikeTitle = mBusinessManager.getExitBikeTitle();
        if(!title.equals("")){
            et_title.setText(title);
        }
        if(!coName.equals("")){
            et_company.setText(coName);
        }else{
            et_company.setText("上海软杰智能设备有限公司");
        }
        if(!tel.equals("")){
            et_tel.setText(tel);
        }else{
            et_tel.setText("021-51099719");
        }
        if(!enterTitle.equals("")){
            et_enter_title.setText(enterTitle);
        }
        if(!exitTitle.equals("")){
            et_exit_title.setText(exitTitle);
        }
        if(!enterBikeTitle.equals("")){
            et_enter_bike_title.setText(enterBikeTitle);
        }
        if(!exitBikeTitle.equals("")){
            et_exit_bike_title.setText(exitBikeTitle);
        }
        bt_ok = (Button) findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = SharedPreferencesConfig.getEtValue(et_title);
                coName = SharedPreferencesConfig.getEtValue(et_company);
                tel = SharedPreferencesConfig.getEtValue(et_tel);
                enterTitle = SharedPreferencesConfig.getEtValue(et_enter_title);
                exitTitle = SharedPreferencesConfig.getEtValue(et_exit_title);
                enterBikeTitle = SharedPreferencesConfig.getEtValue(et_enter_bike_title);
                exitBikeTitle = SharedPreferencesConfig.getEtValue(et_exit_bike_title);
                mBusinessManager.saveIntentContent(title);
                mBusinessManager.saveIntentEndCo(coName);
                mBusinessManager.saveIntentEndTel(tel);
                mBusinessManager.saveEnterTitle(enterTitle);
                mBusinessManager.saveExitTitle(exitTitle);
                mBusinessManager.saveEnterBikeTitle(enterBikeTitle);
                mBusinessManager.saveExitBikeTitle(exitBikeTitle);
                PrintContentActivity.this.finish();
            }
        });
    }
}
