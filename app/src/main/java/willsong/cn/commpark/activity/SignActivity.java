package willsong.cn.commpark.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import w.song.orchid.activity.OBaseActivity;
import willsong.cn.commpark.R;

/**
 * Created by Administrator on 2016/10/24 0024.
 */

public class SignActivity extends OBaseActivity {
//    流水号：seqno（int）
//    业务编号：code（string）
//    通用请求字段：commRequest
//    停车点编号：parkingSpotId(string) 必须
//    平台编号：platformId(string)
//    工号：uid（string），不超过12字符
//    密码：pwd（string），不超过32字符
//    经度：longi（string），不超过16字符，如121.480237
//    纬度：lati（string），不超过16字符，如31.2363
//    批次号:batchCode（string），不超过32字符
//    停车点名称：name（string），不超过30字符
//    停车点地址：address（string），不超过100字符
//    服务时段：opentime（string），不超过100字符
//    收费          标准：price（string），不超过100字符

    EditText parkingSpotId,platId,opentime,price;
    Button commit;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_sign);
        init();
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("parkingSpotId",parkingSpotId.getText().toString());
                editor.putString("platId",platId.getText().toString());
                editor.putString("opentime",opentime.getText().toString());
                editor.putString("price",price.getText().toString());
                editor.commit();
                finish();
            }
        });
    }

    private void init() {
        setRightButtonVisible(false);
        setTitleText("路政签到设置");
        sp = this.getSharedPreferences("sign", this.MODE_PRIVATE);
        editor = sp.edit();
        parkingSpotId = (EditText) findViewById(R.id.parkingSpotId);
        platId = (EditText) findViewById(R.id.platId);
        opentime = (EditText) findViewById(R.id.opentime);
        price = (EditText) findViewById(R.id.price);
        commit = (Button) findViewById(R.id.commit);

        parkingSpotId.setText(sp.getString("parkingSpotId",""));
        platId.setText(sp.getString("platId",""));
        opentime.setText(sp.getString("opentime",""));
        price.setText(sp.getString("price",""));
    }
}
