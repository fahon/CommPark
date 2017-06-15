package willsong.cn.commpark.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;

/**
 * Created by Administrator on 2016/10/23 0023.
 */

public class PlatFormTestAcitvity extends OBaseActivity implements View.OnClickListener{

    EditText luzheng_ip,shizhong_ip;
    EditText luzheng_port,shizhong_port,code,address,raddress;
    Button commit;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_platformtest);
        init();
        Date();
    }

    private void init(){
        setRightButtonVisible(false);
        setTitleText("IP端口号设置");
        luzheng_ip = (EditText) findViewById(R.id.luzheng_ip);
        shizhong_ip = (EditText) findViewById(R.id.shizhong_ip);
        luzheng_port = (EditText) findViewById(R.id.luzheng_port);
        shizhong_port = (EditText) findViewById(R.id.shizhong_port);

        code = (EditText) findViewById(R.id.code);
        address = (EditText) findViewById(R.id.addresscode);
        raddress = (EditText) findViewById(R.id.raddress);
        commit = (Button) findViewById(R.id.commin);
        sp = getSharedPreferences("SP", this.MODE_PRIVATE);
        editor = sp.edit();
        commit.setOnClickListener(this);
    }

    public void Date(){
        luzheng_ip.setText(sp.getString("luzheng_ip",""));
        luzheng_ip.setSelection(sp.getString("luzheng_ip","").length());
        shizhong_ip.setText(sp.getString("shizhong_ip",""));

        luzheng_port.setText(sp.getString("luzheng_port",""));
        shizhong_port.setText(sp.getString("shizhong_port",""));
        code.setText(sp.getString("code",""));
        address.setText(sp.getString("address",""));
        raddress.setText(sp.getString("raddress",""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.commin:
                    String lz_ip = luzheng_ip.getText().toString();
                    String lz_port = luzheng_port.getText().toString().trim();
                if(!MyTools.isNullOrAirForString(lz_ip) && !MyTools.isNullOrAirForString(lz_port)) {
                    editor.putString("luzheng", "1");
                    editor.putString("luzheng_ip", lz_ip);
                    editor.putString("luzheng_port", lz_port);
                    editor.commit();
                }
                String sz_ip = shizhong_ip.getText().toString();
                String sz_port = shizhong_port.getText().toString().trim();
                    editor.putString("shizhong", "1");
                    editor.putString("shizhong_ip", sz_ip);
                    editor.putString("shizhong_port", sz_port);
                    editor.putString("code",code.getText().toString());
                    editor.putString("address",address.getText().toString());
                    editor.putString("raddress",raddress.getText().toString());
                    editor.commit();
                finish();
                break;
        }
    }
}
