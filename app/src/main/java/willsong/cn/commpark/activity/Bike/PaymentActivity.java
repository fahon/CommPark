package willsong.cn.commpark.activity.Bike;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hsm.barcode.DecodeResult;
import com.hsm.barcode.Decoder;
import com.hsm.barcode.DecoderConfigValues;
import com.hsm.barcode.DecoderException;
import com.hsm.barcode.SymbologyConfig;
import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.BusCardActivity;
import willsong.cn.commpark.activity.EnterParkInfoActivity;
import willsong.cn.commpark.activity.HomePageActivity;
import willsong.cn.commpark.activity.Print.BluetoothUtil;
import willsong.cn.commpark.activity.Print.PrefUtils;
import willsong.cn.commpark.activity.database.AbnormalCarDB;
import willsong.cn.commpark.activity.database.AbnormalCarEntity;
import willsong.cn.commpark.activity.database.EXITCARDB;
import willsong.cn.commpark.activity.database.ExitCarEntity;
import willsong.cn.commpark.activity.widget.MediaPlayerTool;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;
import willsong.cn.commpark.activity.widget.SpeechUtils;
import willsong.cn.commpark.activity.zxing.android.CaptureActivity;

import static com.printer.sdk.PrinterInstance.mPrinter;

/*
 *电子支付
 */
public class PaymentActivity extends OBaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_WX_PAY = 26;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";

    private Decoder mDecoder;  //扫描解码
    private DecodeResult mDecodeResult; //扫描结果
    boolean running = true;
    long exitSytemTime = 0;
    private boolean threadRunning = false;
    private final int timeOut = 5000;
    boolean scanning = false;

    String TwoDimensionalScan = "";
    BluetoothUtil bu;

    private Button btn_wx_payment, btn_break;//微信收款,返回
    private TextView et_pay_money;
    private RadioButton radioButton_weixin, radioButton_zhifubao;

    String realPayMoney, plateNamber, outtime, plateCode, seqNo, EnterTime;
    int type, carType;
    String couponStr = "";//优惠券号集
    String receivable = "";
    String PayType = "微信";//交易渠道 0全部 1支付宝 2微信 3现金 4银行卡
    String outPayType = "1";//1微信 2支付宝 4现金 5公交卡
    String parkName = "";
    String codeContent = "";//二维码内容
    String serialId = "";//交易流水号
    String storeNo = "";//商户门店号
    private String couponNameStr = "";//优惠券名称集
    //----------------
    private EXITCARDB exitcardb;
    private AbnormalCarDB abnormalCarDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_payment);

        TwoDimensionalScan = mBusinessManager.getIntentTwoDimensionalScan();
        storeNo = mBusinessManager.getStoreNo();
        exitcardb = new EXITCARDB(this);
        abnormalCarDB = new AbnormalCarDB(this);
        if (!abnormalCarDB.checkColumnExists2("payType")) {
            abnormalCarDB.onUpgrade(1, 2, "payType");
        }
        if (!abnormalCarDB.checkColumnExists2("couponNameStr")) {
            abnormalCarDB.onUpgrade(1, 2, "couponNameStr");
        }
        init();
    }

    public void init() {
        bu = new BluetoothUtil(this);
        setTitleText("电子支付");
        oBack.setVisibility(View.GONE);
        oMenu.setVisibility(View.GONE);
        btn_wx_payment = (Button) findViewById(R.id.btn_wx_payment);
        btn_wx_payment.setOnClickListener(this);
        btn_break = (Button) findViewById(R.id.btn_break);
        btn_break.setOnClickListener(this);
        et_pay_money = (TextView) findViewById(R.id.et_pay_money);
        radioButton_weixin = (RadioButton) findViewById(R.id.radioButton_weixin);
        radioButton_zhifubao = (RadioButton) findViewById(R.id.radioButton_zhifubao);

        realPayMoney = getIntent().getStringExtra("realPayMoney");
        plateNamber = getIntent().getStringExtra("plateNamber");
        outtime = getIntent().getStringExtra("outtime");
        plateCode = getIntent().getStringExtra("plateCode");
        seqNo = getIntent().getStringExtra("seqNo");
        type = getIntent().getIntExtra("type", 0);
        EnterTime = getIntent().getStringExtra("Entertime");
        carType = getIntent().getIntExtra("carType", 0);
        couponStr = getIntent().getStringExtra("coupon");
        receivable = getIntent().getStringExtra("receivable");
        et_pay_money.setText("¥" + realPayMoney);
        parkName = getIntent().getStringExtra("parkName");
        couponNameStr = getIntent().getStringExtra("couponName");

        radioButton_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButton_weixin.isChecked()) {
                    radioButton_zhifubao.setChecked(false);
                    PayType = "微信";
                    outPayType = "1";
                }
            }
        });
        radioButton_zhifubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButton_zhifubao.isChecked()) {
                    radioButton_weixin.setChecked(false);
                    PayType = "支付宝";
                    outPayType = "2";
                }
            }
        });
    }

    @Override
    public void refreshView(String type, final Map<String, Object> map) {
        super.refreshView(type, map);
        if (BusinessManager.NETINSERTCAROUTREC.equals(type)) {
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = hander.obtainMessage();
                    msg.arg1 = 1;
                    hander.sendMessage(msg);
                }
            });
            thread.start();
        } else if (BusinessManager.NETINSERTBIKEOUTRECSURE.equals(type)) {
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = hander.obtainMessage();
                    msg.arg1 = 1;
                    hander.sendMessage(msg);
                }
            });
            thread.start();
        } else if (BusinessManager.NETGETPAYQUERY.equals(type)) {
            if (map.get("trade_status_CN").toString().equals("支付成功") || map.get("trade_status").toString().equals("SUCCESS")) {
                MyTools.showToastLong(true, "支付成功", mContext);
                if (("2").equals(mBusinessManager.getIntentSystemModel())) {//自行车版
                    mBusinessManager.netInsertBikeOutRecSure(true, seqNo, receivable + "", realPayMoney + "", outPayType, couponStr, couponNameStr);
                } else {
                    mBusinessManager.netInsertCarOutRec(true, plateNamber + "",
                            EnterTime + "", outtime + "",
                            realPayMoney + "", outPayType,
                            null, plateCode, seqNo, "", "", "", "", "", "", "", "", "", "", "", "", "", couponStr, receivable + "", "", "", false, couponNameStr);
                }
            } else {
                if (map.get("trade_status").toString().equals("REFUND")) {
                    MyTools.showToastLong(true, "已退款", mContext);
                } else if (map.get("trade_status").toString().equals("REVOKED")) {
                    MyTools.showToastLong(true, "已撤单", mContext);
                } else if (map.get("trade_status").toString().equals("NOTPAY")) {
                    MyTools.showToastLong(true, "未支付", mContext);
                } else if (map.get("trade_status").toString().equals("USERPAYING")) {
                    MyTools.showToastLong(true, "等待用户支付", mContext);
                    isQuerySure();
                }
            }
        } else if (BusinessManager.GET_SCAN_FAIL.equals(type)) {
            MyTools.showToastLong(true, "支付失败，请重新扫码再试", mContext);
        } else if (BusinessManager.GET_QUERY_FAIL.equals(type)) {
            isQuerySure();
        } else if (BusinessManager.GET_REVERSE_FAIL.equals(type)) {
            MyTools.dialogIntro(true, "撤单失败:" + "\n" + "请记录订单号" + serialId + ",稍候手动查询和退款", mContext);
        } else if (BusinessManager.NETGETPAYREVERSE.equals(type)) {
            MyTools.showToastLong(true, "订单撤单成功，请改用其他方式付款", mContext);
        } else {
            isRetry();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult=", "" + requestCode + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WX_PAY && resultCode == RESULT_OK) {
            if (data != null) {
                codeContent = data.getStringExtra(DECODED_CONTENT_KEY);
//                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                serialId = getNowDatess() + seqNo;
                mBusinessManager.netScanPay(true, storeNo, "" + PayType, codeContent, "" + deviceId(),
                        SharedPreferencesConfig.getString(mContext, "telephone"), realPayMoney, "", serialId, parkName + plateNamber + getDateChange2(EnterTime));
            }
        } else {
            //什么都不做
        }

    }


    Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    if (("2").equals(mBusinessManager.getIntentSystemModel())) {//自行车版
                        printBikeCntent();
                    } else {
                        printCntent();
                    }

                    //将出场纪录存储到数据库
                    ArrayList<ExitCarEntity> entity = new ArrayList<ExitCarEntity>();
                    ExitCarEntity mcar = new ExitCarEntity("" + plateNamber, "" + EnterTime, "" + outtime,
                            "" + receivable, "" + realPayMoney, PayType);
                    entity.add(mcar);
                    exitcardb.add(entity);

                    //开始合成（离线）
//                    HomePageActivity.speechUtils.speakText("车辆出场成功");
                    MediaPlayerTool.getInstance(mContext).startPlay("一路顺风");
                    if (("2").equals(mBusinessManager.getIntentSystemModel())) {//自行车版
                        MyTools.showToastLong(true, "还车成功", mContext);
                    }else{
                        MyTools.showToastLong(true, "车辆出场成功", mContext);
                    }
                    clearAllActivity();
                    Intent intent = new Intent();
                    intent.setClass(PaymentActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
//        TwoDecodeScan();
    }

    private void TwoDecodeScan() {
        if (TwoDimensionalScan.equals("1")) {
            mDecodeResult = new DecodeResult();
            willsong.cn.commpark.activity.scan.service.util.Util.initSoundPool(this);
            //添加监听
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.rfid.FUN_KEY");
            registerReceiver(keyReceivers, filter);
            running = true;
            mDecoder = new Decoder();
            try {
                mDecoder.connectDecoderLibrary();
                settingPara();
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(300);
//                        mDecoder.startScanning();
//                        Thread.sleep(100);
//                        mDecoder.stopScanning();
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();
//			mDecoder.stopScanning();
            } catch (DecoderException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //减少频繁触发
        if (System.currentTimeMillis() - exitSytemTime < 100) {
//			exitSytemTime = System.currentTimeMillis();
            return true;
        }
        exitSytemTime = System.currentTimeMillis();
        if (keyCode == 131 || keyCode == 132 || keyCode == 133 || keyCode == 134 || keyCode == 135) {
            if (!threadRunning) {
                if (!SharedPreferencesConfig.getString(this, "loginFlag").equals("1")) {
                    if (TwoDimensionalScan.equals("1")) {
                        scan(timeOut);
                    }
                }
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (System.currentTimeMillis() - exitSytemTime > 2000) {
//                Toast.makeText(getApplicationContext(), "xxx",
//                        Toast.LENGTH_SHORT).show();
//                exitSytemTime = System.currentTimeMillis();
//                return true;
//            } else {
//                finish();
//            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置参数
     */
    void settingPara() {
        try {
            //设置EAN13校验位
            SymbologyConfig config = new SymbologyConfig(DecoderConfigValues.SymbologyID.SYM_EAN13);
            config.Flags = 5;
            config.Mask = 1;
            mDecoder.setSymbologyConfig(config);
            mDecoder.disableSymbology(DecoderConfigValues.SymbologyID.SYM_ALL);
//			mDecoder.enableSymbology(SymbologyID.SYM_ALL);

            mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_QR);
            mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_PDF417);
            mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_EAN13);
            mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_CODE128);
////
            mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_DATAMATRIX);


//			mDecoder.setOCRMode(0);
//			mDecoder.setOCRTemplates(0);
//			mDecoder.setOCRUserTemplate("13777777770".getBytes());
//			mDecoder.setDecodeWindowMode(0);
//			DecodeOptions decOpt = new DecodeOptions();
//			decOpt.DecAttemptLimit = -1; // ignore
//			decOpt.VideoReverse = -1; // ignore
//			decOpt.MultiReadCount = 3;
//			mDecoder.setDecodeOptions(decOpt);
//			mDecoder.setLightsMode(3);

//			mDecoder.setLightsMode(LightsMode.ILLUM_AIM_OFF) ;
            //关闭激光
            //mDecoder.setLightsMode(LightsMode.ILLUM_ONLY) ;
        } catch (DecoderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //监听按键消息
    private BroadcastReceiver keyReceivers = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            int keyCode = intent.getIntExtra("keyCode", 0);
            if (keyCode == 0) {//兼容H941
                keyCode = intent.getIntExtra("keycode", 0);
            }
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            Log.e("", "KEYcODE = " + keyCode + ", Down = " + keyDown);
            if (keyDown) {
                //减少频繁触发
                if (System.currentTimeMillis() - exitSytemTime < 100) {
//					exitSytemTime = System.currentTimeMillis();
                    return;
                }
                exitSytemTime = System.currentTimeMillis();
                if (!threadRunning) {
                    scan(timeOut);
                }
            }
        }
    };

    /**
     * 扫描
     *
     * @param timeout
     * @return
     */
    private void scan(final int timeout) {

        if (!scanning) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    scanning = true;
                    try {
                        Thread.sleep(50);
                        mDecoder.waitForDecodeTwo(timeout, mDecodeResult);
                        //保存图像
//						GetLastImage()  ;
                        Thread.sleep(100);
                    } catch (DecoderException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (mDecodeResult.length > 0) {
//						scanning = false;

                        try {
                            byte[] tt = mDecoder.getBarcodeByteData();
                            displayScanResult(new String(tt, "GBK"));
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (DecoderException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

//						displayScanResult(mDecodeResult.barcodeData);
                        // return mDecodeResult.barcodeData;
                        // displayUI(mDecodeResult.barcodeData);

                    }
                    scanning = false;

                }
            }).start();
        }
    }

    //
    private void displayScanResult(final String barCode) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                willsong.cn.commpark.activity.scan.service.util.Util.play(1, 0);
                String barCodes = barCode;

            }
        });
    }

    @Override
    protected void onPause() {
        running = false;
        threadRunning = false;

        if (mDecoder != null) {
            try {
                mDecoder.disconnectDecoderLibrary();
            } catch (DecoderException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            if (null != keyReceivers) {
                unregisterReceiver(keyReceivers);
            }
        } catch (Exception e) {

        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            if (null != keyReceivers) {
                unregisterReceiver(keyReceivers);
            }
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_break:
                finish();
                break;
            case R.id.btn_wx_payment:
                if (storeNo.equals("")) {
                    MyTools.showToastShort(true, "请先设置商户门店号", mContext);
                    return;
                }
                Intent it = new Intent(PaymentActivity.this,
                        CaptureActivity.class);
                it.putExtra("cflag", "5");
                startActivityForResult(it, REQUEST_CODE_WX_PAY);
                break;
        }
    }

    public void printCntent() {
        if ("1".equals(mBusinessManager.getExitPrint())) {
            if (mPrinter == null) {
//                    Toast.makeText(this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                bu.open();
            } else {
                mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
                mPrinter.printText("   " + mBusinessManager.getExitTitle() + "\n\n");
                mPrinter.printText("车牌号码:" + plateNamber + "\n");
                mPrinter.printText("入场时间:" + EnterTime + "\n");
                mPrinter.printText("出场时间:" + outtime + "\n");
                mPrinter.printText("停车时长:" + differenceTime(EnterTime, outtime) + "\n");
                mPrinter.printText("应收金额:" + receivable + "\n");
                mPrinter.printText("实收金额:" + realPayMoney + "\n\n");
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
    }

    //打印自行车预出场
    public void printBikeCntent() {
        if ("1".equals(mBusinessManager.getExitPrint())) {
            if (PrefUtils.getString(mContext, "DEVICENAME", "").startsWith("printer001")) {//新设备
                if (HomePageActivity.isConnect == true) {
                    if (mPrinter == null) {
                        bu.open();
                    }
                    HomePageActivity.satrtPrintBikeInfo(PaymentActivity.this, "" + plateNamber,
                            "" + EnterTime, outtime + "", seqNo + " " + realPayMoney,
                            mBusinessManager.getIntentContent(), mBusinessManager.getIntentEndCo(), mBusinessManager.getIntentEndTel(), 8,
                            mBusinessManager.getExitBikeTitle());
                } else {
                    if (mPrinter == null) {
                        bu.open();
                    }
                    HomePageActivity.sendble(PaymentActivity.this, true);
                }

            } else {
                if (mPrinter == null) {
//                    Toast.makeText(this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                    bu.open();
                } else {
                    mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
                    mPrinter.printText("    " + mBusinessManager.getExitBikeTitle() + "\n");//标题
                    mPrinter.printText("编号:" + plateNamber + "\n");
                    mPrinter.printText("入场时间:" + EnterTime + "\n");
                    mPrinter.printText("结算时间:" + outtime + "\n");
                    Barcode barcode2 = new Barcode(PrinterConstants.BarcodeType.QRCODE, 3, 3, 6, seqNo + " " + realPayMoney);
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

    //是否需要重新查询订单数据
    private void isQuerySure() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        builder.setMessage("查询支付失败，是否重新查询订单状态？");
        builder.setCancelable(false);
        builder.setNegativeButton("撤销订单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBusinessManager.netPayReverse(true, storeNo, "" + PayType, codeContent, "" + deviceId(),
                        SharedPreferencesConfig.getString(mContext, "telephone"), realPayMoney, "", serialId, parkName + plateNamber + getDateChange2(EnterTime));
            }
        });
        builder.show();
        builder.setPositiveButton("再次查询", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBusinessManager.netPayQuery(true, storeNo, "" + PayType, codeContent, "" + deviceId(),
                        SharedPreferencesConfig.getString(mContext, "telephone"), realPayMoney, "", serialId, parkName + plateNamber + getDateChange2(EnterTime));
            }
        });
        builder.show();
    }

    //是否要重试出场：是:重新请求出场  否:保存数据到本地，有网络时再传
    private void isRetry() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
        builder.setMessage("该车出场失败，确定要重试吗？");
        builder.setCancelable(false);
        builder.setNegativeButton("保存数据", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //将异常未出场成功数据存储到数据库
                ArrayList<AbnormalCarEntity> entity = new ArrayList<AbnormalCarEntity>();
                AbnormalCarEntity mcar = new AbnormalCarEntity(plateNamber, outtime, realPayMoney, "" + plateCode, seqNo,
                        "", "", "", "", "", "", "", "", "",
                        "", "", "", "", couponStr, receivable + "", "", "", "" + outPayType, couponNameStr);
                entity.add(mcar);
                abnormalCarDB.add(entity);
            }
        });
        builder.setPositiveButton("重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (("2").equals(mBusinessManager.getIntentSystemModel())) {//自行车版
                    mBusinessManager.netInsertBikeOutRecSure(true, seqNo, receivable + "", realPayMoney + "", outPayType, couponStr, couponNameStr);
                } else {
                    mBusinessManager.netInsertCarOutRec(true, plateNamber + "",
                            EnterTime + "", outtime + "",
                            realPayMoney + "", outPayType,
                            null, plateCode, seqNo, "", "", "", "", "", "", "", "", "", "", "", "", "", couponStr, receivable + "", "", "", false, couponNameStr);
                }
            }
        });
        builder.show();
    }
}
