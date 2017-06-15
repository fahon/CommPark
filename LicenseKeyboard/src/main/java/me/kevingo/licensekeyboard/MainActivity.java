package me.kevingo.licensekeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    public static final String INPUT_LICENSE_COMPLETE = "me.kevingo.licensekeyboard.input.comp";
    public static final String INPUT_LICENSE_KEY = "LICENSE";
    public static final int RESULTCODE=0;
    public static final int RESULTCODE_BACK=1;

    /**
     * 返回调用页面字段
     */
    public static class RESULT_FIELD {
        public final static String LICENSE = "LICENSE";
    }

    private EditText inputbox1, inputbox2,
            inputbox3, inputbox4,
            inputbox5, inputbox6, inputbox7,inputbox8;
    private LicenseKeyboardUtil keyboardUtil;
    private BroadcastReceiver receiver;

    //-----------------------
    private TextView et_common_plate1,et_common_plate2,et_common_plate3,et_common_plate4;
    public static LinearLayout ll_commom_paltes;
    String commonLicence = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lk_main);
        commonLicence = getIntent().getStringExtra("commonLicence");

        inputbox1 = (EditText) this.findViewById(R.id.et_car_license_inputbox1);
        inputbox2 = (EditText) this.findViewById(R.id.et_car_license_inputbox2);
        inputbox3 = (EditText) this.findViewById(R.id.et_car_license_inputbox3);
        inputbox4 = (EditText) this.findViewById(R.id.et_car_license_inputbox4);
        inputbox5 = (EditText) this.findViewById(R.id.et_car_license_inputbox5);
        inputbox6 = (EditText) this.findViewById(R.id.et_car_license_inputbox6);
        inputbox7 = (EditText) this.findViewById(R.id.et_car_license_inputbox7);
        inputbox8 = (EditText) this.findViewById(R.id.et_car_license_inputbox8);

        ll_commom_paltes = (LinearLayout) this.findViewById(R.id.ll_commom_paltes);
        et_common_plate1 = (TextView) this.findViewById(R.id.et_common_plate1);
        et_common_plate2 = (TextView) this.findViewById(R.id.et_common_plate2);
        et_common_plate3 = (TextView) this.findViewById(R.id.et_common_plate3);
        et_common_plate4 = (TextView) this.findViewById(R.id.et_common_plate4);
        if(commonLicence.toString().trim().length()==4){
            et_common_plate1.setText(commonLicence.substring(0,1));
            et_common_plate2.setText(commonLicence.substring(1,2));
            et_common_plate3.setText(commonLicence.substring(2,3));
            et_common_plate4.setText(commonLicence.substring(3));
        }else{
            ll_commom_paltes.setVisibility(View.GONE);
        }

        et_common_plate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(LicenseKeyboardUtil.INPUT_COMMON_PLATE, et_common_plate1.getText().toString().trim());
                intent.setAction(LicenseKeyboardUtil.INPUT_LICENSE_COMMON);
                sendBroadcast(intent);
            }
        });
        et_common_plate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(LicenseKeyboardUtil.INPUT_COMMON_PLATE, et_common_plate2.getText().toString().trim());
                intent.setAction(LicenseKeyboardUtil.INPUT_LICENSE_COMMON);
                sendBroadcast(intent);
            }
        });
        et_common_plate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(LicenseKeyboardUtil.INPUT_COMMON_PLATE, et_common_plate3.getText().toString().trim());
                intent.setAction(LicenseKeyboardUtil.INPUT_LICENSE_COMMON);
                sendBroadcast(intent);
            }
        });
        et_common_plate4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(LicenseKeyboardUtil.INPUT_COMMON_PLATE, et_common_plate4.getText().toString().trim());
                intent.setAction(LicenseKeyboardUtil.INPUT_LICENSE_COMMON);
                sendBroadcast(intent);
            }
        });
        //输入车牌完成后的intent过滤器
        IntentFilter finishFilter = new IntentFilter(INPUT_LICENSE_COMPLETE);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String license = intent.getStringExtra(INPUT_LICENSE_KEY);
                if (keyboardUtil != null) {
                    keyboardUtil.hideKeyboard();
                }
                MainActivity.this.unregisterReceiver(this);
                Intent it = MainActivity.this.getIntent();
                it.putExtra(RESULT_FIELD.LICENSE, license);
                MainActivity.this.setResult(RESULTCODE, it);
                MainActivity.this.finish();
            }
        };
        this.registerReceiver(receiver, finishFilter);
        keyboardUtil = new LicenseKeyboardUtil(this, new EditText[]{inputbox1, inputbox2, inputbox3,
                inputbox4, inputbox5, inputbox6, inputbox7,inputbox8});
        keyboardUtil.showKeyboard();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (keyboardUtil != null) {
            keyboardUtil.hideKeyboard();
        }
        if(receiver!=null){
            unregisterReceiver(receiver);
            Intent it = getIntent();
            MainActivity.this.setResult(RESULTCODE_BACK, it);
            MainActivity.this.finish();
        }
    }
}
