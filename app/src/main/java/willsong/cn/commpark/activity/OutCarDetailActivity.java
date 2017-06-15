package willsong.cn.commpark.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;

import w.song.orchid.activity.OBaseActivity;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.Bike.PreExitBikeActivity;
import willsong.cn.commpark.activity.Print.BluetoothUtil;
import willsong.cn.commpark.activity.Print.PrefUtils;

import static com.printer.sdk.PrinterInstance.mPrinter;

/**
 * 出场车辆详情
 * Created by guof on 2016/12/14.
 */

public class OutCarDetailActivity extends OBaseActivity implements View.OnClickListener {
    private EditText et_plate_no;
    private TextView tv_enter_time,tv_out_time,tv_time_long,tv_pay_type,tv_pay_money;
    private Button btn_print_receipt;
    private String carPlate="",enterTime="",outTime="",timeLong="",payType="",payAmount = "",Amount = "",seqNo = "";
    BluetoothUtil bu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_out_car_detail);
        bu = new BluetoothUtil(this);
        init();
    }

    private void init() {
        setRightButtonVisible(false);
        setTitleText("出场车辆详情");

        et_plate_no = (EditText) findViewById(R.id.et_plate_no);
        tv_enter_time = (TextView) findViewById(R.id.tv_enter_time);
        tv_out_time = (TextView) findViewById(R.id.tv_out_time);
        tv_time_long = (TextView) findViewById(R.id.tv_time_long);
        tv_pay_type = (TextView) findViewById(R.id.tv_pay_type);
        tv_pay_money = (TextView) findViewById(R.id.tv_pay_money);
        btn_print_receipt = (Button) findViewById(R.id.btn_print_receipt);
        btn_print_receipt.setOnClickListener(this);

        carPlate = getIntent().getStringExtra("CarPlate");
        enterTime = getIntent().getStringExtra("EnterTime");
        outTime = getIntent().getStringExtra("OutTime");
        timeLong = getIntent().getStringExtra("TimeLong");
        if(timeLong.contains("小时")){
            timeLong = timeLong.replace("小时","时");
        }
        if(timeLong.contains("分钟")){
            timeLong = timeLong.replace("分钟","分");
        }
        payType = getIntent().getStringExtra("PayType");
        payAmount = getIntent().getStringExtra("PayAmount");
        Amount = getIntent().getStringExtra("Amount");
        seqNo = getIntent().getStringExtra("SerialNo");
        et_plate_no.setText(carPlate);
        tv_enter_time.setText(enterTime);
        tv_out_time.setText(outTime);
        tv_time_long.setText(timeLong);
        tv_pay_type.setText(payType);
        tv_pay_money.setText(payAmount);
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
            if (mPrinter == null) {
//                    Toast.makeText(this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                bu.open();
            } else {
                mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
                mPrinter.printText("   " + mBusinessManager.getExitTitle()+"\n\n");
                mPrinter.printText("车牌号码:" + carPlate + "\n");
                mPrinter.printText("入场时间:" + enterTime + "\n");
                mPrinter.printText("出场时间:" + outTime + "\n");
                mPrinter.printText("停车时长:" + timeLong + "\n");
                mPrinter.printText("应收金额:" + Amount + "\n");
                mPrinter.printText("实收金额:" + payAmount + "\n\n");
                mPrinter.printText("-------------------------\n");
                //                   mPrinter.printText("上海软杰智能设备有限公司研制\n");
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

    //打印自行车版预出场内容
    private void printBikeCntent() {
        if (PrefUtils.getString(mContext, "DEVICENAME", "").startsWith("printer001")) {//新设备
            if (HomePageActivity.isConnect == true) {
                if (mPrinter == null) {
                    bu.open();
                }
                HomePageActivity.satrtPrintBikeInfo(OutCarDetailActivity.this, "" + carPlate,
                        "" + enterTime, outTime + "", seqNo + " " + payAmount,
                        mBusinessManager.getIntentContent(), mBusinessManager.getIntentEndCo(), mBusinessManager.getIntentEndTel(), 8,
                        mBusinessManager.getExitBikeTitle());
            } else {
                if (mPrinter == null) {
                    bu.open();
                }
                HomePageActivity.sendble(OutCarDetailActivity.this, true);
            }

        } else {
            if (mPrinter == null) {
//                    Toast.makeText(this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                bu.open();
            } else {
                mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
                mPrinter.printText("    " + mBusinessManager.getExitBikeTitle()+"\n");
                mPrinter.printText("编号:" + carPlate + "\n");
                mPrinter.printText("入场时间:" + enterTime + "\n");
                mPrinter.printText("结算时间:" + outTime + "\n");
                Barcode barcode2 = new Barcode(PrinterConstants.BarcodeType.QRCODE, 3, 3, 6, seqNo + " " + payAmount);
                int sum = mPrinter.printBarCode(barcode2);

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
