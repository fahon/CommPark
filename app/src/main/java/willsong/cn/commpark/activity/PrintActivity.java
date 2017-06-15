package willsong.cn.commpark.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.demo.BluetoothDeviceList;
import com.android.print.demo.BluetoothOperation;
import com.android.print.demo.IPrinterOpertion;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;

import w.song.orchid.activity.OBaseActivity;
import willsong.cn.commpark.R;

public class PrintActivity extends OBaseActivity {
    public static class IMPORT_FIELD {
        public final static String TASK="task";
        //enter exit
        public final static String PARK="park";
        //enter exit
        public final static String ENTER_TIME="enter_time";
        //exit
        public final static String EXIT_TIME="exit_time";
        //enter exit
        public final static String PLATE_NUM="plate_num";
        //exit
        public final static String SUM="sum";
        //enter exit
        public final static String COMPANY="company";
        //enter exit
        public final static String CODE="code";
    }

    public static class TASK_VALUE {
        public final static String ENTER = "enter";
        public final static String EXIT = "exit";
    }

    private TextView _parkTextView;
    private Button _printButton;
    private EditText _enterTimeEditText;
    private EditText _exitTimeEditText;
    private EditText _plateNumberEditText;
    private EditText _sumEditText;
    private LinearLayout _exitTimeLinearLayout;
    private LinearLayout _sumLinearLayout;
    private TextView _footInfoTextView;
    private View _exitTimeLine;
    private View _sumLine;

    private static boolean isConnected = false;
    private ProgressDialog dialog;
    protected static IPrinterOpertion myOpertion;
    private PrinterInstance mPrinter;
    public static final int CONNECT_DEVICE = 1;
    public static final int ENABLE_BT = 2;

    private String bt_mac;
    private String bt_name;

    private String task = "";
    private String park = "";
    private String enter_time = "";
    private String exit_time = "";
    private String plate_num = "";
    private String sum = "";
    private String company = "";
    private String code = "";

    private String print_info="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_print);

        task = getIntent().getStringExtra(IMPORT_FIELD.TASK);
        code = getIntent().getStringExtra(IMPORT_FIELD.CODE);
        company = getIntent().getStringExtra(IMPORT_FIELD.COMPANY);
        enter_time = getIntent().getStringExtra(IMPORT_FIELD.ENTER_TIME);
        exit_time = getIntent().getStringExtra(IMPORT_FIELD.EXIT_TIME);
        park = getIntent().getStringExtra(IMPORT_FIELD.PARK);
        plate_num = getIntent().getStringExtra(IMPORT_FIELD.PLATE_NUM);
        sum = getIntent().getStringExtra(IMPORT_FIELD.SUM);

        _parkTextView=(TextView) findViewById(R.id.activity_print_TextView_park);
        _printButton = (Button) findViewById(R.id.activity_print_Button_print);
        _enterTimeEditText=(EditText)findViewById(R.id.activity_print_EditText_enter_time);
        _exitTimeEditText=(EditText)findViewById(R.id.activity_print_EditText_exit_time);
        _plateNumberEditText=(EditText)findViewById(R.id.activity_print_EditText_plate_number);
        _sumEditText=(EditText)findViewById(R.id.activity_print_EditText_sum);
        _exitTimeLinearLayout=(LinearLayout)findViewById(R.id.activity_print_LinearLayout_exit_time);
        _sumLinearLayout=(LinearLayout)findViewById(R.id.activity_print_LinearLayout_sum);
        _footInfoTextView=(TextView)findViewById(R.id.activity_print_TextView_foot_info);
        _exitTimeLine=findViewById(R.id.line_exit_time);
        _sumLine=findViewById(R.id.line_sum);

        setTitleText("打印小票");
        setRightButtonVisible(false);
        setPrintInfoAndView();

        _printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getConnBlueTooth();
            }
        });

        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("连接中...");
        dialog.setMessage("请稍候...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

    }

    private void getConnBlueTooth() {
        if (!isConnected) {
//            new AlertDialog.Builder(this).setTitle(com.android.print.demo.R.string.str_message)
//                    .setMessage("还未连接打印机,请搜索附近蓝牙打印机设备")
//                    .setPositiveButton(com.android.print.demo.R.string.yesconn, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            myOpertion = new BluetoothOperation(PrintActivity.this, mHandler);
//                            Context context = mContext;
//                            myOpertion.btAutoConn(context, mHandler);
//
//                        }
//                    })
//                    .setNegativeButton("搜索", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            myOpertion = new BluetoothOperation(PrintActivity.this, mHandler);
//                            myOpertion.chooseDevice();
//                        }
//
//                    })
//                    .show();
            myOpertion = new BluetoothOperation(PrintActivity.this, mHandler);
            myOpertion.chooseDevice();
        } else {
            myOpertion.close();
            myOpertion = null;
            mPrinter = null;

            myOpertion = new BluetoothOperation(PrintActivity.this, mHandler);
            myOpertion.chooseDevice();
        }
    }

    //用于接受连接状态消息的 Handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    isConnected = true;
                    mPrinter = myOpertion.getPrinter();
                    printInfo();
                    break;
                case PrinterConstants.Connect.FAILED:
                    isConnected = false;
                    Toast.makeText(mContext, com.android.print.demo.R.string.conn_failed,
                            Toast.LENGTH_SHORT).show();
                    break;
                case PrinterConstants.Connect.CLOSED:
                    isConnected = false;
//                    Toast.makeText(mContext, com.android.print.demo.R.string.conn_closed, Toast.LENGTH_SHORT)
//                            .show();
                    break;
                case PrinterConstants.Connect.NODEVICE:
                    isConnected = false;
                    Toast.makeText(mContext, com.android.print.demo.R.string.conn_no, Toast.LENGTH_SHORT)
                            .show();
                    break;

                default:
                    break;
            }

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    };

    @Override
    protected void onActivityResult(final int requestCode, int resultCode,
                                    final Intent data) {
        switch (requestCode) {
            case CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    bt_mac = data.getExtras().getString(BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
                    bt_name = data.getExtras().getString(BluetoothDeviceList.EXTRA_DEVICE_NAME);
                    dialog.show();
                    new Thread(new Runnable() {
                        public void run() {
                            myOpertion.open(bt_mac);
                        }
                    }).start();
                }
                break;
            case ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    myOpertion.chooseDevice();
                } else {
                    Toast.makeText(this, com.android.print.demo.R.string.bt_not_enabled,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myOpertion != null) {
            myOpertion.close();
        }
    }

    private void printInfo() {
        mPrinter.init();
        mPrinter.printText(print_info);
    }

    private void setPrintInfoAndView(){
       if(TASK_VALUE.ENTER.equals(task)){
          print_info= park+"\n\n车牌:"+plate_num+"\n进车时间:"+enter_time+"\n\n"+company+"\n"+code+"\n"+" "+"\n"+" "+"\n";
           _parkTextView.setText(park);
           _plateNumberEditText.setText(plate_num);
           _enterTimeEditText.setText(enter_time);
           _footInfoTextView.setText(company+"\n"+code);
           _exitTimeLinearLayout.setVisibility(View.GONE);
           _exitTimeLine.setVisibility(View.GONE);
           _sumLinearLayout.setVisibility(View.GONE);
           _sumLine.setVisibility(View.GONE);
       }else if(TASK_VALUE.EXIT.equals(task)){
           print_info=park+"\n\n车牌:"+plate_num+"\n进车时间:"+enter_time+"\n" +
                   "出车时间:"+exit_time+"\n费用:"+sum+"元\n\n"+company+"\n"+code+"\n"+" "+"\n"+" "+"\n";
           _parkTextView.setText(park);
           _plateNumberEditText.setText(plate_num);
           _enterTimeEditText.setText(enter_time);
           _exitTimeEditText.setText(exit_time);
           _sumEditText.setText(sum);
           _footInfoTextView.setText(company+"\n"+code);
       }
    }
}
