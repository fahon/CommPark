package willsong.cn.commpark.activity.Bike;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;

import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.CalendarTool;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.EnterParkInfoActivity;
import willsong.cn.commpark.activity.HomePageActivity;
import willsong.cn.commpark.activity.Print.BluetoothUtil;
import willsong.cn.commpark.activity.Print.PrefUtils;
import willsong.cn.commpark.activity.widget.MediaPlayerTool;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;
import willsong.cn.commpark.activity.widget.SpeechUtils;

import static com.printer.sdk.PrinterInstance.mPrinter;

public class EnterBikeActivity extends OBaseActivity {
    private EditText plateNumberEditText;

    private TextView timeTextView;
    private Button sureButton, btn_break_enter, btn_print;

    private String plateCode = "";

    BluetoothUtil bu;

    private String car;
    private int codeSize = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_enter_bike);
        plateCode = getIntent().getStringExtra("plate_code");
        bu = new BluetoothUtil(this);
        //组件
        plateNumberEditText = (EditText) findViewById(R.id.activity_enter_park_info_EditText_plateumber);
        timeTextView = (TextView) findViewById(R.id.activity_enter_park_info_TextView_time);
        sureButton = (Button) findViewById(R.id.activity_enter_park_info_Button_sure);
        btn_break_enter = (Button) findViewById(R.id.btn_break_enter);
        btn_print = (Button) findViewById(R.id.btn_print);

        //UI设置
        setTitleText("进车管理");
        oBack.setVisibility(View.GONE);
        oMenu.setVisibility(View.GONE);

        plateNumberEditText.setText(plateCode);
        timeTextView.setText(CalendarTool.getTodayStrDate(MyTools.FORMATDATE[0]));
        if (SharedPreferencesConfig.getString(mContext, "loginFlag").equals("1")) {
            car = SharedPreferencesConfig.replaceAll(timeTextView.getText().toString());
            codeSize = 7;
        } else {
            car = mBusinessManager.getDevCode() + timeTextView.getText().toString();
            codeSize = 7;
        }

        sureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (SharedPreferencesConfig.getString(mContext, "loginFlag").equals("1")) {
                    String FParkingName = "";
                    if (!mBusinessManager.getIntentEndCo().equals("")) {
                        if (mBusinessManager.getIntentEndCo().toString().length() > 4) {
                            FParkingName = "" + mBusinessManager.getIntentEndCo().substring(0, 4);
                        } else {
                            FParkingName = "" + mBusinessManager.getIntentEndCo();
                        }
                    } else {
                        FParkingName = "上海软杰";
                    }
                    mBusinessManager.netWebRequestEnter(true, FParkingName, "临时车", "" + car, plateNumberEditText.getText().toString().trim(),
                            plateNumberEditText.getText().toString().trim(), SharedPreferencesConfig.getString(mContext, "telephone"),
                            SharedPreferencesConfig.getString(mContext, "userName"), "", timeTextView.getText().toString().trim(), deviceId());
                } else {
                    mBusinessManager.netGetIsPresence(true, plateNumberEditText.getText() + "", timeTextView.getText() + "", car, true);
                }
            }
        });

        btn_break_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printCntent(true);
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
                    Message msg = hander.obtainMessage();
                    msg.arg1 = 1;
                    hander.sendMessage(msg);
                }
            });
            thread.start();
        } else if (type.equals(BusinessManager.WEBNETINSERT)) {
            Message msg = hander.obtainMessage();
            msg.arg1 = 1;
            hander.sendMessage(msg);
        } else if (type.equals(BusinessManager.NETGETISPRESENCE)) {
            if(mBusinessManager.getAllowEnterAgain().equals("1")){
                isCarPresence();
            }else{
                MyTools.dialogIntro(true,"该车辆已在场，不能重复进场",mContext);
            }
        } else {
            MyTools.showToastLong(true, "租车失败，请重试", mContext);
        }
    }

    Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    printCntent(false);
//                    sureButton.setVisibility(View.GONE);
//                    btn_print.setVisibility(View.VISIBLE);
//                    btn_break_enter.setText("确认租车");
                    //开始合成（离线）
//                    HomePageActivity.speechUtils.speakText("租车成功");
                    MediaPlayerTool.getInstance(mContext).startPlay("欢迎光临");
                    MyTools.showToastLong(true, "租车成功", mContext);
                        clearAllActivity();
                        Intent intent = new Intent();
                        intent.setClass(EnterBikeActivity.this, HomePageActivity.class);
                        startActivity(intent);
                    break;
            }
        }
    };

    public void printCntent(boolean isPrint) {
        if ("1".equals(mBusinessManager.getEnterPrint())||isPrint) {
//            if (PrefUtils.getString(mContext, "DEVICENAME", "").startsWith("printer001")) {//新设备
//                if (HomePageActivity.isConnect == true) {
//                    if (mPrinter == null) {
//                        bu.open();
//                    }
//                    HomePageActivity.startPrintBikeEnter(EnterBikeActivity.this, "" + plateNumberEditText.getText().toString().trim(), "" + timeTextView.getText(),
//                            mBusinessManager.getIntentContent(), mBusinessManager.getIntentEndCo(), mBusinessManager.getIntentEndTel(),mBusinessManager.getEnterBikeTitle());
//                } else {
//                    if (mPrinter == null) {
//                        bu.open();
//                    }
//                    HomePageActivity.sendble(EnterBikeActivity.this, true);
//                }
//
//            } else {
                if (mPrinter == null) {
//                    Toast.makeText(this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                    bu.open();
                } else {
                    mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
                    mPrinter.printText("    " + mBusinessManager.getEnterBikeTitle()+"\n");//标题
                    mPrinter.printText("编号:" + plateNumberEditText.getText().toString().trim() + "\n");
                    mPrinter.printText("入场时间:" + timeTextView.getText() + "\n");

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
//            }
        }
    }

    //是否需要重复进场：是:允许重复进场  否:不再进场
    private void isCarPresence() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EnterBikeActivity.this);
        builder.setMessage("该车辆已在场，确定要重复进场吗？");
        builder.setCancelable(false);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBusinessManager.netInsertCarEnterRec(true, plateNumberEditText.getText() + "", timeTextView.getText() + "", car, true);
            }
        });
        builder.show();
    }
}
