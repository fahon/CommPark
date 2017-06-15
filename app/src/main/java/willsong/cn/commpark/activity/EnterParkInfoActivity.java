package willsong.cn.commpark.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import me.kevingo.licensekeyboard.MainActivity;
import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.CalendarTool;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.BarrierGate.BluetoothUtilGate;
import willsong.cn.commpark.activity.Print.BluetoothUtil;
import willsong.cn.commpark.activity.Print.GlobalContants;
import willsong.cn.commpark.activity.Print.PrefUtils;
import willsong.cn.commpark.activity.util.Util;
import willsong.cn.commpark.activity.widget.MediaPlayerTool;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;
import willsong.cn.commpark.activity.widget.SpeechUtils;
import willsong.cn.commpark.activity.widget.webService.WebServiceUtils;

import static com.printer.sdk.PrinterInstance.mPrinter;

public class EnterParkInfoActivity extends OBaseActivity implements View.OnClickListener{
    private EditText plateNumberEditText;
    private TextView timeTextView;
    private Button sureButton, btn_break_enter;

    public static class IMPORT_FIELD {
        public static final String PLATE_CODE = "plate_code";
    }

    private String plateCode = "";
    private String carColor = "";
    int tyep = 0;

    BluetoothUtil bu;

    private String car;
    private int codeSize = 7;
    BluetoothUtilGate mBluetoothUtilGate;//道闸蓝牙
    private Button open_enter_park2,close_enter_park2;//开闸，关闸
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_enter_park_info);

        plateCode = getIntent().getStringExtra(IMPORT_FIELD.PLATE_CODE);
        bu = new BluetoothUtil(this);
//        mBluetoothUtilGate = new BluetoothUtilGate(this);
//        mBluetoothUtilGate.open();
        //组件
        plateNumberEditText = (EditText) findViewById(R.id.activity_enter_park_info_EditText_plateumber);
        timeTextView = (TextView) findViewById(R.id.activity_enter_park_info_TextView_time);
        sureButton = (Button) findViewById(R.id.activity_enter_park_info_Button_sure);
        btn_break_enter = (Button) findViewById(R.id.btn_break_enter);

        open_enter_park2 = (Button) findViewById(R.id.open_enter_park2);
        open_enter_park2.setOnClickListener(this);
        close_enter_park2 = (Button) findViewById(R.id.close_enter_park2);
        close_enter_park2.setOnClickListener(this);
        //UI设置
        if(("3").equals(mBusinessManager.getIntentSystemModel())) {
            setTitleText("进场管理");
        }else{
            setTitleText("进车管理");
        }
        oBack.setVisibility(View.GONE);
        oMenu.setVisibility(View.GONE);

        plateNumberEditText.setText(plateCode);
        timeTextView.setText(CalendarTool.getTodayStrDate(MyTools.FORMATDATE[0]));
        if(SharedPreferencesConfig.getString(mContext,"loginFlag").equals("1")){
            car = SharedPreferencesConfig.replaceAll(timeTextView.getText().toString());
            carColor = getIntent().getStringExtra("color");
            codeSize = 8;
        }else{
            car = mBusinessManager.getDevCode() + timeTextView.getText().toString();
            codeSize = 8;
        }

        sureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(SharedPreferencesConfig.getString(mContext,"loginFlag").equals("1")) {
                    String FParkingName = "";
                    if (!mBusinessManager.getIntentEndCo().equals("")) {
                        if(mBusinessManager.getIntentEndCo().toString().length()>4){
                            FParkingName = ""+mBusinessManager.getIntentEndCo().substring(0,4);
                        }else{
                            FParkingName = ""+mBusinessManager.getIntentEndCo();
                        }
                    } else {
                        FParkingName = "上海软杰";
                    }
                    mBusinessManager.netWebRequestEnter(true,FParkingName,"临时车",""+car,plateNumberEditText.getText().toString().trim(),
                            plateNumberEditText.getText().toString().trim(),SharedPreferencesConfig.getString(mContext,"telephone"),
                            SharedPreferencesConfig.getString(mContext,"userName"),""+carColor,timeTextView.getText().toString().trim(),deviceId());
                   }else{
                    mBusinessManager.netGetIsPresence(true, plateNumberEditText.getText() + "", timeTextView.getText() + "", car,false);
                }
            }
        });

        plateNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = intentInstance(MainActivity.class);
                EnterParkInfoActivity.this.startActivityForResult(it, 0);
            }
        });

        btn_break_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void refreshView(String type, final Map<String, Object> map) {
        super.refreshView(type, map);
        if (BusinessManager.NETINSERTCARENTERREC.equals(type)) {
//            printCntent();
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if ("1".equals(mBusinessManager.getLuZheng())) {
                        Intent intent = new Intent(Util.MYCAST);
                        intent.putExtra("index", 2);
                        intent.putExtra("seqNo", (String) map.get("SeqNo"));
                        Double actTime = (Double) map.get("ActType");
                        int act = actTime.intValue();
                        intent.putExtra("actType", act);
                        intent.putExtra("actTime", timeTextView.getText() + "");
                        intent.putExtra("carNumber", plateNumberEditText.getText() + "");
                        Double totBerthNum = (Double) map.get("TotRemainNum");
                        int tot = totBerthNum.intValue();
                        Double monthlyBerthNum = (Double) map.get("MonthlyRemainNum");
                        int month = monthlyBerthNum.intValue();
                        Double guesBerthNum = (Double) map.get("GuestRemainNum");
                        int gues = guesBerthNum.intValue();
                        intent.putExtra("totRemainNum", tot);
                        intent.putExtra("monthlyRemainNum", month);
                        intent.putExtra("guestRemainNum", gues);
                        intent.putExtra("pasm", mBusinessManager.getIntentsaveIntentPsam());
                        sendBroadcast(intent);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if ("1".equals(mBusinessManager.getShiZhong())) {
                        Intent intent = new Intent(Util.SHIZHONGCART);
                        intent.putExtra("index", 2);
                        Double actTime = (Double) map.get("ActType");
                        int act = actTime.intValue();
                        intent.putExtra("actType", act);
                        intent.putExtra("actTime", timeTextView.getText() + "");
                        intent.putExtra("carNumber", plateNumberEditText.getText() + "");
                        Double totBerthNum = (Double) map.get("TotRemainNum");
                        int tot = totBerthNum.intValue();
                        Double monthlyBerthNum = (Double) map.get("MonthlyRemainNum");
                        int month = monthlyBerthNum.intValue();
                        Double guesBerthNum = (Double) map.get("GuestRemainNum");
                        int gues = guesBerthNum.intValue();
                        intent.putExtra("totRemainNum", tot);
                        intent.putExtra("monthlyRemainNum", month);
                        intent.putExtra("guestRemainNum", gues);
                        sendBroadcast(intent);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = hander.obtainMessage();
                    msg.arg1 = 1;
                    hander.sendMessage(msg);
                }
            });
            thread.start();
        } else if(type.equals(BusinessManager.WEBNETINSERT)){
            Message msg = hander.obtainMessage();
            msg.arg1 = 1;
            hander.sendMessage(msg);
        }else if(type.equals(BusinessManager.NETGETISPRESENCE)){
            if(mBusinessManager.getAllowEnterAgain().equals("1")){
                isCarPresence();
            }else{
                MyTools.dialogIntro(true,"该车辆已在场，不能重复进场",mContext);
            }
        }else {
            MyTools.showToastLong(true, "车辆进场失败，请重试", mContext);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult=", "" + requestCode + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == MainActivity.RESULTCODE) {
            String plateNumber = data.getStringExtra(MainActivity.RESULT_FIELD.LICENSE);
            plateNumberEditText.setText(plateNumber);
        } else if (requestCode == 0 && resultCode == MainActivity.RESULTCODE_BACK) {
            //什么都不做
        }
    }

    Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                        printCntent();
                    //开始合成（离线）
//                    HomePageActivity.speechUtils.speakText("车辆进场成功");
                    MediaPlayerTool.getInstance(mContext).startPlay("欢迎光临");
                        MyTools.showToastLong(true, "车辆进场成功", mContext);
                        clearAllActivity();
                        Intent intent = new Intent();
                        intent.setClass(EnterParkInfoActivity.this, HomePageActivity.class);
                        startActivity(intent);

//                        MyTools.showToastShort(true, "车辆进场成功", mContext);
//                        sureButton.setVisibility(View.GONE);
//                        open_enter_park2.setVisibility(View.VISIBLE);
//                        close_enter_park2.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    public void printCntent() {
        if ("1".equals(mBusinessManager.getEnterPrint())) {
            if (PrefUtils.getString(mContext, "DEVICENAME", "").startsWith("printer001")) {//新设备
                if (HomePageActivity.isConnect == true) {
                    if (mPrinter == null) {
                        bu.open();
                    }
                    HomePageActivity.satrtPrint(EnterParkInfoActivity.this,""+timeTextView.getText(),""+plateNumberEditText.getText(),car,
                            mBusinessManager.getIntentContent(),mBusinessManager.getIntentEndCo(),mBusinessManager.getIntentEndTel(),codeSize,
                            mBusinessManager.getEnterTitle());
                }else{
                    if (mPrinter == null) {
                        bu.open();
                    }
                    HomePageActivity.sendble(EnterParkInfoActivity.this,true);
                }
            }else{
            if (mPrinter == null) {
//                    Toast.makeText(this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                bu.open();
            } else {
                mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
                mPrinter.printText("    " + mBusinessManager.getEnterTitle() +"\n");//标题
                mPrinter.printText("入场时间:" + timeTextView.getText() + "\n");
                mPrinter.printText("车牌号码:" + plateNumberEditText.getText() + "\n");

                //--------------------------------------------------
//                if(BluetoothUtil.devicesAddress.startsWith("DC")) {
//                    Barcode barcode1 = new Barcode(PrinterConstants.BarcodeType.CODE128, 2, 150, 0,
//                            SharedPreferencesConfig.replaceAll(car));//第三个参数为内容显示位置：0不显示 1上方 2下方
//                    mPrinter.printBarCode(barcode1);
//                }else{
                    Barcode barcode2 = new Barcode(com.printer.sdk.PrinterConstants.BarcodeType.QRCODE, 3, 3, 6,car);
                    int sum = mPrinter.printBarCode(barcode2);
//                }
//                    mPrinter.printText("上海软杰智能设备有限公司研制\n");
                if (!mBusinessManager.getIntentEndCo().equals("")) {
                    mPrinter.printText(mBusinessManager.getIntentEndCo() + "\n");
                } else {
                    mPrinter.printText("上海软杰智能设备有限公司研制\n");
                }
                if (!mBusinessManager.getIntentEndTel().equals("")) {
                    mPrinter.printText("   TEL:" + mBusinessManager.getIntentEndTel());
                } else {
                    mPrinter.printText("   TEL:021-51099719");
                }
                mPrinter.printText("\n\n\n");
            }
          }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.open_enter_park2://开闸
                if(mBluetoothUtilGate.mConnected == true){
                    mBluetoothUtilGate.openPark();
                }else{
                    mBluetoothUtilGate.open();
                }
                break;
            case R.id.close_enter_park2://关闸
                if(mBluetoothUtilGate.mConnected == true){
                    mBluetoothUtilGate.closePark();
                    finish();
                }else{
                    mBluetoothUtilGate.open();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        try {
            mBluetoothUtilGate.unregistermReceiver();
        } catch (Exception e) {
        }
        super.onDestroy();
    }
    //是否需要重复进场：是:允许重复进场  否:不再进场
    private void isCarPresence(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EnterParkInfoActivity.this);
        builder.setMessage("该车辆已在场，确定要重复进场吗？");
        builder.setCancelable(false);
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBusinessManager.netInsertCarEnterRec(true, plateNumberEditText.getText() + "", timeTextView.getText() + "", car,false);
            }
        });
        builder.show();
    }
}
