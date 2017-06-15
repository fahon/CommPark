package willsong.cn.commpark.activity.Bike;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import me.kevingo.licensekeyboard.MainActivity;
import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.MemoryCameraActivity;
import willsong.cn.commpark.activity.MemoryResultActivity;
import willsong.cn.commpark.activity.database.DBManager;
import willsong.cn.commpark.activity.database.RecordSteps;
import willsong.cn.commpark.activity.util.ExChangeUtil;

import static willsong.cn.commpark.activity.apps.MyApplication.eh;

public class BikeCheckActivity extends OBaseActivity {
    public static class IMPORT_FIELD {
        public static final String PLATE_CODE = "plate_code";
    }
    private String plateCode="";
    private EditText _plateNumberEditText;
    private TextView _enterTimeTextView;
    private TextView _parkTextView;
    private TextView _resultTextView;
    private Button car_check_break;
    private TextView tv_time_long,tv_pay_money;
    private LinearLayout ll_time_show;
    private String timeLonger = "",enterTime = "";

    //----------------
    private DBManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_bike_check);
        plateCode = getIntent().getStringExtra(IMPORT_FIELD.PLATE_CODE);
        _plateNumberEditText=(EditText)findViewById(R.id.activity_car_check_EditText_plateNumber);
        _enterTimeTextView=(TextView)findViewById(R.id.activity_car_check_TextView_enterTime);
        _parkTextView=(TextView)findViewById(R.id.activity_car_check_TextView_park);
        _resultTextView=(TextView)findViewById(R.id.activity_car_check_TextView_result);
        car_check_break= (Button) findViewById(R.id.car_check_break);
        tv_time_long=(TextView)findViewById(R.id.activity_car_check_TextView_long);
        tv_pay_money=(TextView)findViewById(R.id.activity_car_check_TextView_money);
        ll_time_show=(LinearLayout) findViewById(R.id.ll_time_show);
//        if(mBusinessManager.getIntentCheckCar().equals("1")){//巡查显示时间，费用字段
//            ll_time_show.setVisibility(View.VISIBLE);
//        }else{
//            ll_time_show.setVisibility(View.GONE);
//        }

        _plateNumberEditText.setText(plateCode);

        setTitleText("车辆巡查");
        setRightButtonText("刷新");
        oBack.setVisibility(View.GONE);
        oMenu.setVisibility(View.GONE);
        setTitleRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBusinessManager.netGetCheckInfo(true,_plateNumberEditText.getText()+"");
            }
        });

        _plateNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=intentInstance(MainActivity.class);
                BikeCheckActivity.this.startActivityForResult(it, 0);
            }
        });

        car_check_break.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MemoryCameraActivity.ISCHECKCAR==1){//车辆盘点中,回退到识别界面
                    Intent intent = new Intent(BikeCheckActivity.this,MemoryCameraActivity.class);
                    intent.putExtra("camera", true);//自动识别
                    intent.putExtra(MemoryResultActivity.IMPORT_FIELD.TASK, MemoryResultActivity.TASK_VALUE.CHECK_CAR);
                    startActivity(intent);
                    finish();
                }else{
                    finish();
                }
            }
        });

        mBusinessManager.netGetCheckInfo(true,_plateNumberEditText.getText()+"");

        // 初始化DBManager
        dbManager = new DBManager(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult=",""+requestCode+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0&&resultCode== MainActivity.RESULTCODE){
            String plateNumber = data.getStringExtra(MainActivity.RESULT_FIELD.LICENSE);
            _plateNumberEditText.setText(plateNumber);
            mBusinessManager.netGetCheckInfo(true,_plateNumberEditText.getText()+"");
        }else if(requestCode==0&&resultCode==MainActivity.RESULTCODE_BACK){
            //什么都不做
        }
    }

    @Override
    public void refreshView(String type, Map<String, Object> map) {
        super.refreshView(type, map);
        if(BusinessManager.NETGETCHECKINFO.equals(type)){
            _enterTimeTextView.setText(""+map.get("EnterTime"));
            _parkTextView.setText(""+map.get("ParkName"));
//            _resultTextView.setText(""+map.get("CarStatus"));
            _resultTextView.setText(""+map.get("CarType"));
            tv_time_long.setText(""+map.get("TimeLong"));
            timeLonger = ""+map.get("TimeLong");
            enterTime = ""+map.get("EnterTime");
            ExChangeUtil eu = new ExChangeUtil();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            double money = 0.0;
            try {
                if ((""+map.get("CarType")).equals("临时车")) {
                    money = eu.getMoneyChange(eh, sf.parse("" + map.get("EnterTime")), sf.parse("" + map.get("OutTime")));
                    money = formatDouble(money);
                }
            } catch (Exception e) {
            }
            tv_pay_money.setText(money + "");
        }else{
            timeLonger = "无在场记录";
            enterTime = "无在场记录";
        }
        if(MemoryCameraActivity.ISCHECKCAR==1){
            ArrayList<RecordSteps> persons = new ArrayList<RecordSteps>();
			RecordSteps mperson = new RecordSteps(plateCode, timeLonger,enterTime);
            persons.add(mperson);
            dbManager.add(persons);
        }
    }
}
