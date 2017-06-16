package willsong.cn.commpark.activity.Bike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hsm.barcode.DecodeResult;
import com.hsm.barcode.Decoder;
import com.hsm.barcode.DecoderConfigValues;
import com.hsm.barcode.DecoderException;
import com.hsm.barcode.SymbologyConfig;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.kevingo.licensekeyboard.MainActivity;
import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.BusCardActivity;
import willsong.cn.commpark.activity.CouponWriteActivity;
import willsong.cn.commpark.activity.ExChange;
import willsong.cn.commpark.activity.ExitParkActivity;
import willsong.cn.commpark.activity.HomePageActivity;
import willsong.cn.commpark.activity.Print.BluetoothUtil;
import willsong.cn.commpark.activity.database.EXITCARDB;
import willsong.cn.commpark.activity.database.ExitCarEntity;
import willsong.cn.commpark.activity.util.ExChangeUtil;
import willsong.cn.commpark.activity.widget.MediaPlayerTool;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;
import willsong.cn.commpark.activity.widget.SpeechUtils;
import willsong.cn.commpark.activity.zxing.android.CaptureActivity;

import static com.printer.sdk.PrinterInstance.mPrinter;
import static willsong.cn.commpark.activity.apps.MyApplication.eh;

/**
 * 自行车：正式出场
 */
public class ExitBikeActivity extends OBaseActivity implements View.OnClickListener {
    private static final int REQUEST_COUPONCODE_SCAN = 66;
    private static final String DECODED_COUPONCONTENT_KEY = "codedContent";

    private Button sureButton;
    private EditText enterTimeEditText;
    private EditText exitTimeEditText;
    private EditText plateNamberEditText, et_serial_number;//车牌编号，停车流水号
    private EditText sumEditText;//应收费
    private EditText et_timeLonger;//使用时长
    private Button btn_electronic_payment, activity_exit_park_info_break;

    public static class IMPORT_FIELD {
        public static final String PLATE_CODE = "plate_code";
    }

    private String plateCode = "";
    private String carColor = "";

    private int typePlate = 0;//1:无牌车  0:有牌车

    private int pytype = -1;
    int carType;//0月租车 1临时车 2免费车
    String seqNo;

    BluetoothUtil bu;

    private Decoder mDecoder;  //扫描解码
    private DecodeResult mDecodeResult; //扫描结果
    boolean running = true;
    long exitSytemTime = 0;
    private boolean threadRunning = false;
    private final int timeOut = 5000;
    boolean scanning = false;

    String TwoDimensionalScan = "";
    private String couponNo = "";//优惠券编号
    private String couponType = "";//优惠券类型 1:指定金额 2:抵扣时间 3:抵扣费用
    private String couponName = "";//优惠券名称
    private String faceValue = "";//优惠券面值 1,3时元 2时秒
    private String beginTime = "";//开始时间
    private String endTime = "";//结束时间
    private double totalPayMoney = 0.0;//应付的总金额
    private double realPayMoney = 0.0;//实际要付的金额
    private double couponSecond = 0.0;//总优惠的秒数
    private boolean ifSpecialCoupon = false;//是否是特殊优惠券
    ArrayList<String> couponList = new ArrayList<String>();
    private String couponStr = "";//优惠券号数组（以,号分隔）
    private String couponNameStr = "";//优惠券名称数组（以,号分隔）

    private String webInDateTime = "";//web入场时间
    private String webFPayDate = "";//web出场结算时间
    private double webPayAmount = 0;//web应支付金额
    private double webtotalAmount = 0;//web总支付金额
    private int webBusID = 0;//web公交id
    private int webtotalSecs = 0;//web总秒数
    private int webPaySecs = 0;//web应支付的秒数
    private int webtotalFreeSecs = 0;//web总免费秒数
    private String webFTypeName = "";//web支付方式


    private int freeTime = 0;
    //----------------
    private EXITCARDB exitcardb;
    private boolean isCorrectNo;
    private Button btn_bike_coupon;//优惠券
    private String ParkName = "",carPlate = "";//停车场名称,车牌号
    private EditText et_park_info_discount;//优惠金额/时间
    private EditText et_park_info_real_cost;//实收费
    private LinearLayout ll_coupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_exit_bike);

        exitcardb = new EXITCARDB(this);
        TwoDimensionalScan = mBusinessManager.getIntentTwoDimensionalScan();
        init();
    }

    public void init() {
        plateCode = getIntent().getStringExtra(IMPORT_FIELD.PLATE_CODE);
        try {
            String[] code = plateCode.split(" ");
            seqNo = code[0];
            realPayMoney = Double.valueOf(code[1]);
            totalPayMoney = Double.valueOf(code[1]);
            isCorrectNo = true;
            //开始合成（离线）
//            HomePageActivity.speechUtils.speakText("收费"+HomePageActivity.speechUtils.convertMoney(realPayMoney));
            MediaPlayerTool.getInstance(mContext).startPlay(SpeechUtils.convertMoney(realPayMoney));
        } catch (Exception e) {
            isCorrectNo = false;
//            MyTools.showToastShort(true,"非正确的流水号",ExitBikeActivity.this);
        }

//        typePlate = getIntent().getIntExtra("type", 0);
        bu = new BluetoothUtil(this);
        //组件
        sureButton = (Button) findViewById(R.id.activity_exit_park_info_Button_sure);
        sureButton.setOnClickListener(this);
        enterTimeEditText = (EditText) findViewById(R.id.activity_exit_park_info_EditText_enterTime);
        exitTimeEditText = (EditText) findViewById(R.id.activity_exit_park_info_EditText_exitTime);
        plateNamberEditText = (EditText) findViewById(R.id.activity_exit_park_info_EditText_plateNamber);
        sumEditText = (EditText) findViewById(R.id.activity_exit_park_info_EditText_sum);
        activity_exit_park_info_break = (Button) findViewById(R.id.activity_exit_park_info_break);
        activity_exit_park_info_break.setOnClickListener(this);
        btn_electronic_payment = (Button) findViewById(R.id.btn_electronic_payment);
        btn_electronic_payment.setOnClickListener(this);
        btn_bike_coupon = (Button) findViewById(R.id.btn_bike_coupon);
        btn_bike_coupon.setOnClickListener(this);
        et_park_info_discount = (EditText) findViewById(R.id.et_park_info_discount);
        et_park_info_real_cost = (EditText) findViewById(R.id.et_park_info_real_cost);
        ll_coupon = (LinearLayout) findViewById(R.id.ll_coupon);
        et_timeLonger = (EditText) findViewById(R.id.et_timeLonger);
        et_serial_number = (EditText) findViewById(R.id.et_serial_number);
        et_serial_number.setText(seqNo);
        sumEditText.setText("" + realPayMoney);

        setTitleText("还车管理");
        oBack.setVisibility(View.GONE);
        oMenu.setVisibility(View.GONE);
        if (1 == typePlate) {
            plateNamberEditText.setText("无牌车");
        } else {
            plateNamberEditText.setText(plateCode);
        }

        if (0 != realPayMoney) {
//            btn_electronic_payment.setVisibility(View.VISIBLE);
            btn_bike_coupon.setVisibility(View.VISIBLE);
        }

        mBusinessManager.netQueryParkingRec(true, seqNo);
    }

    @Override
    public void refreshView(String type, final Map<String, Object> map) {
        super.refreshView(type, map);
        if (BusinessManager.NETQUERYPARKINGREC.equals(type)) {
            enterTimeEditText.setText("" + map.get("EnterTime"));
            exitTimeEditText.setText("" + map.get("OutTime"));

            beginTime = "" + map.get("EnterTime");
            endTime = "" + map.get("OutTime");
            carPlate = "" + map.get("CarPlate");
            ParkName = "" + map.get("parkname");
            seqNo = "" + map.get("SeqNo");
            if (0 != realPayMoney) {
            btn_electronic_payment.setVisibility(View.VISIBLE);
//                btn_bike_coupon.setVisibility(View.VISIBLE);
            }
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
        } else if (type.equals(BusinessManager.WEBNETCOST)) {
            String status = "";
            status = map.get("Status") + "";
            webInDateTime = map.get("InDateTime") + "";
            webFPayDate = map.get("FPayDate") + "";
            webPayAmount = (Double) map.get("PayAmount");
            webtotalAmount = (Double) map.get("totalAmount");
            Double busid = (Double) map.get("BusID");
            webBusID = busid.intValue();
            Double totalSecs = (Double) map.get("totalSecs");
            webtotalSecs = totalSecs.intValue();
            Double PaySecs = (Double) map.get("PaySecs");
            webPaySecs = PaySecs.intValue();
            Double totalFreeSecs = (Double) map.get("totalFreeSecs");
            webtotalFreeSecs = totalFreeSecs.intValue();
            webFTypeName = map.get("FTypeName") + "";
            if (status.equals("未支付")) {
                plateNamberEditText.setText("" + map.get("PlateNumber"));
                enterTimeEditText.setText(webInDateTime);
                exitTimeEditText.setText(webFPayDate);
                totalPayMoney = webtotalAmount;
                realPayMoney = webPayAmount;
                sumEditText.setText(totalPayMoney + "");
                et_timeLonger.setText("" + differenceTime(enterTimeEditText.getText().toString(),
                        exitTimeEditText.getText().toString()));
                if (0 != realPayMoney) {
//                    btn_electronic_payment.setVisibility(View.VISIBLE);
                    btn_bike_coupon.setVisibility(View.VISIBLE);
                }
            } else {
                MyTools.showToastShort(true, "该车已出场或未查到进场信息...", mContext);
            }
        } else if (type.equals(BusinessManager.WEBNETOUT)) {
            Message msg = hander.obtainMessage();
            msg.arg1 = 1;
            hander.sendMessage(msg);
        } else if (type.equals(BusinessManager.NETGETSCANPAY)) {
            MyTools.showToastShort(true, map.toString() + "", mContext);
        } else if (BusinessManager.NETCOUPONINFO.equals(type)) {
            ll_coupon.setVisibility(View.VISIBLE);
//            MyTools.showToastShort(true,"优惠券信息：" +map, mContext);
            couponType = map.get("CouponType").toString().trim();
            if (couponType.contains(".")) {
                couponType = couponType.substring(0, couponType.indexOf("."));
            }
            //指定金额消费的优惠券不与其他优惠券叠加使用
            if (couponType.equals("1")) {
                couponList.clear();
                couponList.add(couponNo);
            } else {
                couponList.add(couponNo);
            }
            couponStr = "";
            for (int i = 0; i < couponList.size(); i++) {
                couponStr = couponStr + couponList.get(i) + ",";
            }

            couponName = map.get("CouponName").toString().trim();
            faceValue = map.get("FaceValue").toString().trim();
            if (couponType.equals("1")) {//抵消所有停车费用，只需支付面值上的金额
                ifSpecialCoupon = true;
                couponNameStr = couponName;
                realPayMoney = Double.valueOf(faceValue);
                realPayMoney = formatDouble(realPayMoney);
                et_park_info_discount.setText(formatDouble(totalPayMoney - realPayMoney) + "元  ");
                et_park_info_real_cost.setText("" + realPayMoney);
                //开始合成（离线）
//                HomePageActivity.speechUtils.speakText("收费"+HomePageActivity.speechUtils.convertMoney(realPayMoney));
                MediaPlayerTool.getInstance(mContext).startPlay(SpeechUtils.convertMoney(realPayMoney));

            } else if (couponType.equals("2")) {//抵扣时间
                couponNameStr = couponNameStr + couponName + ",";
                try {
                    if (faceValue.contains(".")) {
                        faceValue = faceValue.substring(0, faceValue.indexOf("."));
                    }
                    if (faceValue.equals("0")) {
                        realPayMoney = 0.0;
                    } else {
                        couponSecond = couponSecond + Integer.parseInt(faceValue);
                        endTime = addDateMinut(endTime, Integer.parseInt(faceValue));
                        //----------------------------------------------
                        //优惠时间后是否计算免费时间
                        if (eh.CountFreeTimeAfterPref) {//是(优惠时间后，还可以使用免费时间进行计费出场)
//                            endTime = addDateMinut(endTime, Integer.parseInt(faceValue));
                        } else {//否(优惠时间后，免费时间不能用)
                            eh.FreeTime = 0;
//                            if(fiestCountTime){
//                                endTime = addDateMinut(endTime, Integer.parseInt(faceValue)-eh.FreeTime);
//                                fiestCountTime = false;
//                            }else{
//                                endTime = addDateMinut(endTime, Integer.parseInt(faceValue));
//                            }
                        }

                        ExChangeUtil eu = new ExChangeUtil();
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        double money = 0.0;
                        try {
//                            if (1 == carType) {
                                money = eu.getMoneyChange(eh, sf.parse(beginTime), sf.parse(endTime));
                                money = formatDouble(money);
//                            }
                        } catch (Exception e) {
                        }
                        //money:本次优惠后还需支付的费用
                        //totalPayMoney - money:本次优惠的金额
                        eh.FreeTime = freeTime;//计算完后，还原原来的免费时间
                        if (realPayMoney >= totalPayMoney - money) {
                            realPayMoney = realPayMoney - (totalPayMoney - money);
                            realPayMoney = formatDouble(realPayMoney);
                        } else {
                            realPayMoney = 0.0;
                        }
                    }

                    et_park_info_discount.setText(formatDouble(totalPayMoney - realPayMoney) + "元  (" + couponSecond + ")秒");
                    et_park_info_real_cost.setText("" + realPayMoney);
                    //开始合成（离线）
//                    HomePageActivity.speechUtils.speakText("收费"+HomePageActivity.speechUtils.convertMoney(realPayMoney));
                    MediaPlayerTool.getInstance(mContext).startPlay(SpeechUtils.convertMoney(realPayMoney));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (couponType.equals("3")) {//抵扣金额
                couponNameStr = couponNameStr + couponName + ",";
                try {
                    if (faceValue.equals("0")) {
                        realPayMoney = 0.0;
                    } else {
                        if (realPayMoney >= Double.parseDouble(faceValue)) {
                            realPayMoney = realPayMoney - Double.parseDouble(faceValue);
                            realPayMoney = formatDouble(realPayMoney);
                        } else {
                            realPayMoney = 0.0;
                        }
                    }

                    et_park_info_discount.setText(formatDouble(totalPayMoney - realPayMoney) + "元  (" + couponSecond + ")秒");
                    et_park_info_real_cost.setText("" + realPayMoney);

                    //开始合成（离线）
//                    HomePageActivity.speechUtils.speakText("收费"+HomePageActivity.speechUtils.convertMoney(realPayMoney));
                    MediaPlayerTool.getInstance(mContext).startPlay(SpeechUtils.convertMoney(realPayMoney));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult=", "" + requestCode + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == MainActivity.RESULTCODE) {
            if (data != null) {
                String plateNumber = data.getStringExtra(MainActivity.RESULT_FIELD.LICENSE);
                plateNamberEditText.setText(plateNumber);
                mBusinessManager.netGetBillInfo(true, plateNamberEditText.getText() + "", "");
            }
        } else if (requestCode == REQUEST_COUPONCODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(DECODED_COUPONCONTENT_KEY);
//                Bitmap bitmap = data.getParcelableExtra(DECODED_COUPONBITMAP_KEY);

                MyTools.showToastShort(true, "优惠券码:" + content, ExitBikeActivity.this);
                Log.i("ccm", "结果" + content);
                for (int i = 0; i < couponList.size(); i++) {
                    if (couponList.get(i).equals(content)) {
                        MyTools.showToastShort(true, "您已用过该优惠券了", ExitBikeActivity.this);
                        return;
                    }
                }
                couponNo = content;
                if (SharedPreferencesConfig.getString(mContext, "loginFlag").equals("1")) {
                    mBusinessManager.netWebRequestCoupon(true, "" + couponNo);
                } else {
                    mBusinessManager.netCouponInfo(true, couponNo);
                }
            }
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
//                    printCntent();
                    //将出场纪录存储到数据库
                    ArrayList<ExitCarEntity> entity = new ArrayList<ExitCarEntity>();
                    ExitCarEntity mcar = new ExitCarEntity("" + carPlate, "" + enterTimeEditText.getText(), "" + exitTimeEditText.getText(),
                            "" + totalPayMoney, "" + realPayMoney, "现金支付(自行车版正式出场)");
                    entity.add(mcar);
                    exitcardb.add(entity);

                    MediaPlayerTool.getInstance(mContext).startPlay("一路顺风");
                    MyTools.showToastLong(true, "还车成功", mContext);
                    clearAllActivity();
                    Intent intent = new Intent();
                    intent.setClass(ExitBikeActivity.this, HomePageActivity.class);
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
                Message msg = hander.obtainMessage();
                msg.arg1 = 2;
                msg.obj = barCodes;
                hander.sendMessage(msg);
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
            case R.id.activity_exit_park_info_Button_sure:
                if (!isCorrectNo) {
                    MyTools.showToastLong(true, "流水号不正确，请重新扫描", mContext);
                } else {
                    //开始合成（离线）
//                    HomePageActivity.speechUtils.speakText("环车成功");
//                    MyTools.showToastLong(true, "还车成功", mContext);
//                    clearAllActivity();
//                    Intent intent = new Intent();
//                    intent.setClass(ExitBikeActivity.this, HomePageActivity.class);
//                    startActivity(intent);

                    if (SharedPreferencesConfig.getString(this, "loginFlag").equals("1")) {
                        int IsPayLeave = 0;
                        if (mBusinessManager.getRecognizeAgain().equals("")) {
                            IsPayLeave = 1;
                        } else {
                            IsPayLeave = Integer.valueOf(mBusinessManager.getRecognizeAgain());
                        }
                        mBusinessManager.netWebRequestOut(true, IsPayLeave, webBusID, "现金支付", webtotalAmount, "" + plateCode, SharedPreferencesConfig.getString(mContext, "telephone"),
                                SharedPreferencesConfig.getString(mContext, "userName"), webPayAmount, webtotalSecs, webtotalFreeSecs, webPaySecs, "" + webFPayDate,
                                deviceId(), "");
                    } else {
                        mBusinessManager.netInsertBikeOutRecSure(true, seqNo, totalPayMoney + "", realPayMoney + "", "4", couponStr, couponNameStr);
                    }
                }


                break;
            case R.id.btn_electronic_payment:
                if (!isCorrectNo) {
                    MyTools.showToastLong(true, "流水号不正确，请重新扫描", mContext);
                } else {
                    if (realPayMoney > 0) {
                        Intent it = new Intent(getApplicationContext(), PaymentActivity.class);
                        it.putExtra("realPayMoney", "" + realPayMoney);
                        it.putExtra("type", typePlate);
                        it.putExtra("plateNamber", carPlate);
                        it.putExtra("outtime", exitTimeEditText.getText() + "");
                        it.putExtra("plateCode", carPlate);
                        it.putExtra("seqNo", seqNo);
                        it.putExtra("Entertime", enterTimeEditText.getText() + "");
                        it.putExtra("carType", carType);
                        it.putExtra("coupon", couponStr);
                        it.putExtra("receivable", sumEditText.getText() + "");
                        it.putExtra("parkName", ParkName);
                        it.putExtra("couponName", couponNameStr);
                        startActivity(it);
                    } else {
                        MyTools.showToastShort(true, "您已无需支付费用", ExitBikeActivity.this);
                    }
                }
                break;
            case R.id.activity_exit_park_info_break:
                finish();
                break;
            case R.id.btn_bike_coupon:
                if (realPayMoney == 0.0) {
                    MyTools.showToastShort(true, "无需使用优惠券", ExitBikeActivity.this);
                    break;
                }
                if (ifSpecialCoupon) {
                    MyTools.showToastShort(true, "此优惠券不能与其他优惠券叠加使用", ExitBikeActivity.this);
                    break;
                }
                if (!isCorrectNo) {
                    MyTools.showToastLong(true, "流水号不正确，请重新扫描", mContext);
                    break;
                }
                Intent intent = new Intent(ExitBikeActivity.this, CaptureActivity.class);
                intent.putExtra("cflag", "1");
                startActivityForResult(intent, REQUEST_COUPONCODE_SCAN);
                break;
        }
    }

    public void printCntent() {
        if (mPrinter == null) {
            bu.open();
        } else {
            mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
            mPrinter.printText("   " + "结算凭证\n\n");
            mPrinter.printText("付款金额:" + realPayMoney + "\n");
            mPrinter.printText("商户名称:" + "" + "\n");
            mPrinter.printText("支付状态:" + "" + "\n");
            mPrinter.printText("支付时间:" + "" + "\n");
            mPrinter.printText("支付方式:" + "" + "\n");
            mPrinter.printText("租车时间:" + enterTimeEditText.getText() + "\n");
            mPrinter.printText("还车时间:" + exitTimeEditText.getText() + "\n");
            mPrinter.printText("使用时长:" + differenceTime(enterTimeEditText.getText().toString(),
                    exitTimeEditText.getText().toString()) + "\n");
            mPrinter.printText("交易单号:" + "" + "\n");
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!CouponWriteActivity.couponNum.equals("")) {

            for (int i = 0; i < couponList.size(); i++) {
                if (couponList.get(i).equals(CouponWriteActivity.couponNum)) {
                    MyTools.showToastShort(true, "您已用过该优惠券了", ExitBikeActivity.this);
                    return;
                }
            }
            couponNo = CouponWriteActivity.couponNum;
            if (SharedPreferencesConfig.getString(mContext, "loginFlag").equals("1")) {
                mBusinessManager.netWebRequestCoupon(true, couponNo);
            } else {
                mBusinessManager.netCouponInfo(true, couponNo);
            }
            CouponWriteActivity.couponNum = "";
            CouponWriteActivity.typeFlag = 0;
        }
    }
}
