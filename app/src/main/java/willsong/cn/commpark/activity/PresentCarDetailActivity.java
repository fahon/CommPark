package willsong.cn.commpark.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.printer.sdk.Barcode;

import java.util.ArrayList;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.Bike.EnterBikeActivity;
import willsong.cn.commpark.activity.Print.BluetoothUtil;
import willsong.cn.commpark.activity.Print.PrefUtils;
import willsong.cn.commpark.activity.adapter.PresentCarAdapter;
import willsong.cn.commpark.activity.widget.xlistview.XListView;

import static com.printer.sdk.PrinterInstance.mPrinter;

/**
 * 在场车辆详情
 * Created by guof on 2016/12/14.
 */

public class PresentCarDetailActivity extends OBaseActivity implements View.OnClickListener {
    private EditText et_plate_no;
    private TextView tv_enter_time,tv_car_type,tv_time_long;
    private Button btn_print_receipt;
    private String carPlate="",enterTime="",timeLong="",carType="";
    BluetoothUtil bu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_present_car_detail);
        bu = new BluetoothUtil(this);
        init();
    }

    private void init() {
        setRightButtonVisible(false);
        setTitleText("在场车辆详情");

        et_plate_no = (EditText) findViewById(R.id.et_plate_no);
        tv_enter_time = (TextView) findViewById(R.id.tv_enter_time);
        tv_car_type = (TextView) findViewById(R.id.tv_car_type);
        tv_time_long = (TextView) findViewById(R.id.tv_time_long);
        btn_print_receipt = (Button) findViewById(R.id.btn_print_receipt);
        btn_print_receipt.setOnClickListener(this);

        carPlate = getIntent().getStringExtra("CarPlate");
        enterTime = getIntent().getStringExtra("EnterTime");
        timeLong = getIntent().getStringExtra("TimeLong");
        if(timeLong.contains("小时")){
            timeLong = timeLong.replace("小时","时");
        }
        if(timeLong.contains("分钟")){
            timeLong = timeLong.replace("分钟","分");
        }
        carType = getIntent().getStringExtra("CarType");
        et_plate_no.setText(carPlate);
        tv_enter_time.setText(enterTime);
        tv_time_long.setText(timeLong);
        if("1".equals("carType")){
            tv_car_type.setText("月租车");
        }else{
            tv_car_type.setText("临时车");
        }

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_print_receipt:
                if(("2").equals(mBusinessManager.getIntentSystemModel())){//自行车版
                    printBikeCntent();
                }else{
                    printCntent();
                }
                break;
        }
    }
    private void printCntent() {
            if (PrefUtils.getString(mContext, "DEVICENAME", "").startsWith("printer001")) {//新设备
                if (HomePageActivity.isConnect == true) {
                    if (mPrinter == null) {
                        bu.open();
                    }
                    HomePageActivity.satrtPrint(PresentCarDetailActivity.this,""+enterTime,""+carPlate,mBusinessManager.getDevCode()+enterTime,
                            mBusinessManager.getIntentContent(),mBusinessManager.getIntentEndCo(),mBusinessManager.getIntentEndTel(),8,
                            mBusinessManager.getEnterTitle());
                }else{
                    if (mPrinter == null) {
                        bu.open();
                    }
                    HomePageActivity.sendble(PresentCarDetailActivity.this,true);
                }
            }else{
                if (mPrinter == null) {
//                    Toast.makeText(this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                    bu.open();
                } else {
                    mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
                    mPrinter.printText("    " + mBusinessManager.getEnterTitle()+"\n");
                    mPrinter.printText("入场时间:" + enterTime + "\n");
                    mPrinter.printText("车牌号码:" + carPlate + "\n");

                    //--------------------------------------------------
//                if(BluetoothUtil.devicesAddress.startsWith("DC")) {
//                    Barcode barcode1 = new Barcode(PrinterConstants.BarcodeType.CODE128, 2, 150, 0,
//                            SharedPreferencesConfig.replaceAll(car));//第三个参数为内容显示位置：0不显示 1上方 2下方
//                    mPrinter.printBarCode(barcode1);
//                }else{
                    Barcode barcode2 = new Barcode(com.printer.sdk.PrinterConstants.BarcodeType.QRCODE, 3, 3, 6,mBusinessManager.getDevCode()+enterTime);
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

    private void printBikeCntent() {
//        if (PrefUtils.getString(mContext, "DEVICENAME", "").startsWith("printer001")) {//新设备
//            if (HomePageActivity.isConnect == true) {
//                if (mPrinter == null) {
//                    bu.open();
//                }
//                HomePageActivity.startPrintBikeEnter(PresentCarDetailActivity.this, "" + carPlate, "" + enterTime,
//                        mBusinessManager.getIntentContent(), mBusinessManager.getIntentEndCo(), mBusinessManager.getIntentEndTel(),
//                        mBusinessManager.getEnterBikeTitle());
//            } else {
//                if (mPrinter == null) {
//                    bu.open();
//                }
//                HomePageActivity.sendble(PresentCarDetailActivity.this, true);
//            }
//
//        } else {
            if (mPrinter == null) {
//                    Toast.makeText(this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                bu.open();
            } else {
                mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
                mPrinter.printText("    " + mBusinessManager.getEnterBikeTitle()+"\n");
                mPrinter.printText("编号:" + carPlate + "\n");
                mPrinter.printText("入场时间:" + enterTime + "\n");

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
//        }
    }
}
