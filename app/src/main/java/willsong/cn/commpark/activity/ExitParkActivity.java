package willsong.cn.commpark.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
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
import willsong.cn.commpark.activity.Bike.PaymentActivity;
import willsong.cn.commpark.activity.Print.BluetoothUtil;
import willsong.cn.commpark.activity.database.EXITCARDB;
import willsong.cn.commpark.activity.database.ExitCarEntity;
import willsong.cn.commpark.activity.util.ExChangeUtil;
import willsong.cn.commpark.activity.util.Util;
import willsong.cn.commpark.activity.widget.MediaPlayerTool;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;
import willsong.cn.commpark.activity.widget.SpeechUtils;
import willsong.cn.commpark.activity.zxing.android.CaptureActivity;

import static com.printer.sdk.PrinterInstance.mPrinter;
import static willsong.cn.commpark.activity.apps.MyApplication.eh;

public class ExitParkActivity extends OBaseActivity implements View.OnClickListener {
    private static final int REQUEST_COUPONCODE_SCAN = 0x0000;
    private static final String DECODED_COUPONCONTENT_KEY = "codedContent";
    private static final String DECODED_COUPONBITMAP_KEY = "codedBitmap";

    private Button sureButton;
    private EditText enterTimeEditText;
    private EditText exitTimeEditText;
    private EditText plateNamberEditText;
    private EditText sumEditText;//应收费
    private Button activity_exit_park_info_BusCard, activity_exit_park_info_coupon, activity_exit_park_info_break;
    private EditText et_park_info_discount;//优惠金额/时间
    private EditText et_park_info_real_cost;//实收费
    private LinearLayout ll_coupon;

    public static class IMPORT_FIELD {
        public static final String PLATE_CODE = "plate_code";
    }

    private String plateCode = "";
    private String carColor = "";

    private int typePlate = 0;//0：有牌车  1：无牌车

    private int pytype = -1;
    int carType;//0月租车 1临时车 2免费车
    String seqNo;

    byte[] cardInfo;
    byte[] info;
    byte[] car_buf;

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


    private LinearLayout ll_write_coupon;//手动输入优惠券
    private EditText et_couponnum;
    private Button btn_coupon_submit, btn_elect_payment;
    //    private Boolean fiestCountTime = true;
    private int freeTime = 0;
    private String ParkName = "";//停车场名称

    //----------------
    private EXITCARDB exitcardb;

    public static boolean isShowChenNiao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_exit_park);

        TwoDimensionalScan = mBusinessManager.getIntentTwoDimensionalScan();
        exitcardb = new EXITCARDB(this);
        init();

    }

    public void init() {
        plateCode = getIntent().getStringExtra(IMPORT_FIELD.PLATE_CODE);
        typePlate = getIntent().getIntExtra("type", 0);
        bu = new BluetoothUtil(this);
//        mBluetoothUtilGate = new BluetoothUtilGate(this);
//        mBluetoothUtilGate.open();
        //组件
        sureButton = (Button) findViewById(R.id.activity_exit_park_info_Button_sure);
        sureButton.setOnClickListener(this);
        enterTimeEditText = (EditText) findViewById(R.id.activity_exit_park_info_EditText_enterTime);
        exitTimeEditText = (EditText) findViewById(R.id.activity_exit_park_info_EditText_exitTime);
        plateNamberEditText = (EditText) findViewById(R.id.activity_exit_park_info_EditText_plateNamber);
        sumEditText = (EditText) findViewById(R.id.activity_exit_park_info_EditText_sum);
        activity_exit_park_info_BusCard = (Button) findViewById(R.id.activity_exit_park_info_BusCard);
        activity_exit_park_info_BusCard.setOnClickListener(this);
        activity_exit_park_info_break = (Button) findViewById(R.id.activity_exit_park_info_break);
        activity_exit_park_info_break.setOnClickListener(this);
        activity_exit_park_info_coupon = (Button) findViewById(R.id.activity_exit_park_info_coupon);
        activity_exit_park_info_coupon.setOnClickListener(this);
        et_park_info_discount = (EditText) findViewById(R.id.et_park_info_discount);
        et_park_info_real_cost = (EditText) findViewById(R.id.et_park_info_real_cost);
        ll_coupon = (LinearLayout) findViewById(R.id.ll_coupon);

        ll_write_coupon = (LinearLayout) findViewById(R.id.ll_write_coupon);
        et_couponnum = (EditText) findViewById(R.id.et_couponnum);
        btn_coupon_submit = (Button) findViewById(R.id.btn_coupon_submit);
        btn_coupon_submit.setOnClickListener(this);
        btn_elect_payment = (Button) findViewById(R.id.btn_elect_payment);
        btn_elect_payment.setOnClickListener(this);

        if(("3").equals(mBusinessManager.getIntentSystemModel())) {
            setTitleText("出场管理");
        }else {
            setTitleText("出车管理");
        }
        oBack.setVisibility(View.GONE);
        oMenu.setVisibility(View.GONE);
        if (1 == typePlate) {
            plateNamberEditText.setText("无牌车");
        } else {
            plateNamberEditText.setText(plateCode);
        }
        //last
        if (SharedPreferencesConfig.getString(mContext, "loginFlag").equals("1")) {
            carColor = getIntent().getStringExtra("color");
//            if(plateCode.length()<=8){
//                plateNamberEditText.setText(plateCode);
//            }
            mBusinessManager.netWebRequestCost(true, "" + plateCode, "" + carColor, getNowDate(), "");
        } else {
            if (1 == typePlate) {
                mBusinessManager.netGetBillInfo(true, "", plateCode);
            } else {
                mBusinessManager.netGetBillInfo(true, plateNamberEditText.getText() + "", "");
            }
        }

        setTitleRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (1 == typePlate) {
                    mBusinessManager.netGetBillInfo(true, "", plateCode);
                } else {
                    mBusinessManager.netGetBillInfo(true, plateNamberEditText.getText() + "", "");
                }
            }
        });
        plateNamberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = intentInstance(MainActivity.class);
                ExitParkActivity.this.startActivityForResult(it, 0);
            }
        });
    }

    @Override
    public void refreshView(String type, final Map<String, Object> map) {
        super.refreshView(type, map);
        if (BusinessManager.NETGETBILLINFO.equals(type)) {
            plateNamberEditText.setText("" + map.get("CarPlate"));
            enterTimeEditText.setText("" + map.get("EnterTime"));
            exitTimeEditText.setText("" + map.get("OutTime"));
            beginTime = "" + map.get("EnterTime");
            endTime = "" + map.get("OutTime");
            Double cartype = (Double) map.get("CarType");
            carType = cartype.intValue();
            plateCode = "" + map.get("FieldCode");
            //费率---------------------------------
            Map<String, Object> change = (java.util.Map<String, Object>) map.get("ChargeInfo");
            eh = new ExChange();
            Double bc = (Double) change.get("BillingCycle");
            eh.BillingCycle = bc.intValue();
            eh.Rate = (double) change.get("Rate");
            Double tne = (Double) change.get("TimeNotEnough");
            eh.TimeNotEnough = tne.intValue();
            Double ft = (Double) change.get("FreeTime");
            freeTime = ft.intValue();
            eh.FreeTime = freeTime;
            Double fbc = (Double) change.get("FirstBillingCycle");
            eh.FirstBillingCycle = fbc.intValue();
            eh.FirstCycleRate = (double) change.get("FirstCycleRate");
            eh.IncludeFreeTime = (boolean) change.get("IncludeFreeTime");
            eh.FreeWeekend = (boolean) change.get("FreeWeekend");
            eh.FeeCeilingPerDay = (double) change.get("FeeCeilingPerDay");
            eh.BillingOnceaDay = (boolean) change.get("BillingOnceaDay");
            eh.ChargeByNatureDay = (boolean) change.get("ChargeByNatureDay");
            eh.CountFreeTimeAfterPref = (boolean) change.get("PreferentialLengthOr");
            ParkName = "" + change.get("ParkName");
            ExChangeUtil eu = new ExChangeUtil();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            double money = 0.0;
            try {
                if (1 == carType) {
                    money = eu.getMoneyChange(eh, sf.parse("" + map.get("EnterTime")), sf.parse("" + map.get("OutTime")));
                    money = formatDouble(money);
                }
            } catch (Exception e) {
            }
            sumEditText.setText(money + "");
            totalPayMoney = money;
            realPayMoney = money;
            seqNo = "" + map.get("SeqNo");
            //开始合成（离线）
//            HomePageActivity.speechUtils.speakText("收费"+HomePageActivity.speechUtils.convertMoney(money));
            MediaPlayerTool.getInstance(mContext).startPlay(SpeechUtils.convertMoney(money));

            //是否可以使用晨鸟优惠券【进出场时间为同一天（当天），并且进出场时间在指定时间范围内】
            boolean isSameEnterDay = isSameDay(map.get("EnterTime").toString().trim().substring(0,10));//进场时间是否是当天
            boolean isSameOutDay = isSameDay(map.get("OutTime").toString().trim().substring(0,10));//出场时间是否是当天
            boolean isInEnterDate = isInDate(map.get("EnterTime").toString().trim(),"07:30:00","09:30:00");//进场时间是否在指定时间范围内
            boolean isInOutDate = isInDate(map.get("OutTime").toString().trim(),"16:30:00","19:30:00");//出场时间是否在指定时间范围内
            if(isSameEnterDay && isSameOutDay && isInEnterDate && isInOutDate){
                isShowChenNiao = true;
                if("1".equals(mBusinessManager.getOpenChenNiao())){
                    mBusinessManager.netCouponList(true, "");
                }
            }else{
                isShowChenNiao = false;
            }
            if (0 != money) {
                activity_exit_park_info_BusCard.setVisibility(View.VISIBLE);
                activity_exit_park_info_coupon.setVisibility(View.VISIBLE);
//                ll_write_coupon.setVisibility(View.VISIBLE);
                btn_elect_payment.setVisibility(View.VISIBLE);
            }
        } else if (BusinessManager.NETINSERTCAROUTREC.equals(type)) {
//            printCntent();
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //路政出场
                    if ("1".equals(mBusinessManager.getLuZheng())) {
                        Intent intent = new Intent(Util.MYCAST);
                        intent.putExtra("index", 3);
                        intent.putExtra("seqNo", seqNo);
                        Double actTime = (Double) map.get("ActType");
                        int act = actTime.intValue();
                        intent.putExtra("actType", act);
                        intent.putExtra("enterTime", "" + enterTimeEditText.getText());
                        Double totBerthNum = (Double) map.get("TotRemainNum");
                        int tot = totBerthNum.intValue();
                        Double monthlyBerthNum = (Double) map.get("MonthlyRemainNum");
                        int month = monthlyBerthNum.intValue();
                        Double guesBerthNum = (Double) map.get("GuestRemainNum");
                        int gues = guesBerthNum.intValue();
                        intent.putExtra("totRemainNum", tot);
                        intent.putExtra("monthlyRemainNum", month);
                        intent.putExtra("guestRemainNum", gues);
                        intent.putExtra("actTime", exitTimeEditText.getText().toString());//出场时间
                        intent.putExtra("plate", plateNamberEditText.getText().toString());
                        double money = Double.parseDouble(sumEditText.getText().toString());
                        intent.putExtra("payMoney", (int) money); //收费金额
                        intent.putExtra("parkingTimeLength", datetime("" + enterTimeEditText.getText(),
                                "" + exitTimeEditText.getText()));//停车时长
                        intent.putExtra("carType", carType);
                        intent.putExtra("pytype", 2);
                        sendBroadcast(intent);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //市中
                    if ("1".equals(mBusinessManager.getShiZhong())) {
                        Intent intent = new Intent(Util.SHIZHONGCART);
                        intent.putExtra("index", 3);
                        intent.putExtra("seqNo", seqNo);
                        Double actTime = (Double) map.get("ActType");
                        int act = actTime.intValue();
                        intent.putExtra("actType", act);
                        intent.putExtra("enterTime", "" + enterTimeEditText.getText());

                        Double totBerthNum = (Double) map.get("TotRemainNum");
                        int tot = totBerthNum.intValue();
                        Double monthlyBerthNum = (Double) map.get("MonthlyRemainNum");
                        int month = monthlyBerthNum.intValue();
                        Double guesBerthNum = (Double) map.get("GuestRemainNum");
                        int gues = guesBerthNum.intValue();
                        intent.putExtra("totRemainNum", tot);
                        intent.putExtra("monthlyRemainNum", month);
                        intent.putExtra("guestRemainNum", gues);
                        intent.putExtra("actTime", exitTimeEditText.getText().toString());//出场时间
                        intent.putExtra("plate", plateNamberEditText.getText().toString());
                        double money = Double.parseDouble(sumEditText.getText().toString());
                        intent.putExtra("payMoney", (int) money); //收费金额
                        intent.putExtra("parkingTimeLength", datetime("" + enterTimeEditText.getText(),
                                "" + exitTimeEditText.getText()));//停车时长
                        intent.putExtra("carType", carType);
                        intent.putExtra("pytype", 2);
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
                            if (1 == carType) {
                                money = eu.getMoneyChange(eh, sf.parse(beginTime), sf.parse(endTime));
                                money = formatDouble(money);
                            }
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
                et_park_info_discount.setText(formatDouble(totalPayMoney - realPayMoney) + "元  " + webtotalFreeSecs + "秒");
                sumEditText.setText(totalPayMoney + "");
                et_park_info_real_cost.setText("" + realPayMoney);
                //开始合成（离线）
//                HomePageActivity.speechUtils.speakText("收费"+HomePageActivity.speechUtils.convertMoney(realPayMoney));
                MediaPlayerTool.getInstance(mContext).startPlay(SpeechUtils.convertMoney(realPayMoney));
                if (0 != realPayMoney) {
                    activity_exit_park_info_coupon.setVisibility(View.VISIBLE);
//                    ll_write_coupon.setVisibility(View.VISIBLE);
                }
            } else {
                MyTools.showToastShort(true, "该车已出场或未查到进场信息...", mContext);
            }
        } else if (type.equals(BusinessManager.WEBNECOUPON)) {
//            MyTools.showToastShort(true,"优惠券信息：" +map, mContext);
            String FIsUsed = "" + map.get("FIsUsed");
            if (("0").equals(FIsUsed)) {
                couponList.add(couponNo);
                couponStr = "";
                for (int i = 0; i < couponList.size(); i++) {
                    couponStr = couponStr + couponList.get(i) + ",";
                }
                ll_coupon.setVisibility(View.VISIBLE);
                mBusinessManager.netWebRequestCost(true, "" + plateCode, "" + carColor, "" + webFPayDate, "" + couponStr);
            } else if (("1").equals(FIsUsed)) {
                MyTools.showToastShort(true, "该优惠券已使用", mContext);
            } else if (("-1").equals(FIsUsed)) {
                MyTools.showToastShort(true, "该优惠券暂不能使用", mContext);
            } else if (("-2").equals(FIsUsed)) {
                MyTools.showToastShort(true, "该优惠券已过期", mContext);
            }
        } else if (type.equals(BusinessManager.WEBNETOUT)) {
            Message msg = hander.obtainMessage();
            msg.arg1 = 1;
            hander.sendMessage(msg);
        }else if (type.equals(BusinessManager.NETCOUPONLISTINFO)) {
            ifSpecialCoupon = true;//此类所有优惠券不与其他优惠券叠加使用
            couponStr = "";
            couponNameStr = couponNo;
//            MyTools.showToastShort(true,map+"",mContext);
            ll_coupon.setVisibility(View.VISIBLE);
//            MyTools.showToastShort(true,"优惠券信息：" +map, mContext);
            couponType = map.get("CouponType").toString().trim();
            if (couponType.contains(".")) {
                couponType = couponType.substring(0, couponType.indexOf("."));
            }

            couponName = map.get("CouponName").toString().trim();
            faceValue = map.get("FaceValue").toString().trim();
            if (couponType.equals("1")) {//抵消所有停车费用，只需支付面值上的金额
                realPayMoney = Double.valueOf(faceValue);
                realPayMoney = formatDouble(realPayMoney);
                et_park_info_discount.setText(formatDouble(totalPayMoney - realPayMoney) + "元  ");
                et_park_info_real_cost.setText("" + realPayMoney);

            } else if (couponType.equals("2")) {//抵扣时间
                try {
                    if (faceValue.contains(".")) {
                        faceValue = faceValue.substring(0, faceValue.indexOf("."));
                    }
                    if (faceValue.equals("0")) {
                        realPayMoney = 0.0;
                    } else {
                        couponSecond = Integer.parseInt(faceValue);
                        endTime = addDateMinut(endTime, Integer.parseInt(faceValue));
                        //----------------------------------------------
                        //优惠时间后是否计算免费时间
                        if (eh.CountFreeTimeAfterPref) {//是(优惠时间后，还可以使用免费时间进行计费出场)
//                            endTime = addDateMinut(endTime, Integer.parseInt(faceValue));
                        } else {//否(优惠时间后，免费时间不能用)
                            eh.FreeTime = 0;
                        }

                        ExChangeUtil eu = new ExChangeUtil();
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        double money = 0.0;
                        try {
                            if (1 == carType) {
                                money = eu.getMoneyChange(eh, sf.parse(beginTime), sf.parse(endTime));
                                money = formatDouble(money);
                            }
                        } catch (Exception e) {
                        }
                        //money:本次优惠后还需支付的费用
                        //totalPayMoney - money:本次优惠的金额
                        eh.FreeTime = freeTime;//计算完后，还原原来的免费时间
                        if (totalPayMoney >= money) {
                            realPayMoney = money;
                            realPayMoney = formatDouble(realPayMoney);
                        } else {
                            realPayMoney = 0.0;
                        }
                    }

                    et_park_info_discount.setText(formatDouble(totalPayMoney - realPayMoney) + "元  (" + couponSecond + ")秒");
                    et_park_info_real_cost.setText("" + realPayMoney);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (couponType.equals("3")) {//抵扣金额
                try {
                    if (faceValue.equals("0")) {
                        realPayMoney = 0.0;
                    } else {
                        if (totalPayMoney >= Double.parseDouble(faceValue)) {
                            realPayMoney = totalPayMoney - Double.parseDouble(faceValue);
                            realPayMoney = formatDouble(realPayMoney);
                        } else {
                            realPayMoney = 0.0;
                        }
                    }
                    couponSecond = 0;

                    et_park_info_discount.setText(formatDouble(totalPayMoney - realPayMoney) + "元  (" + couponSecond + ")秒");
                    et_park_info_real_cost.setText("" + realPayMoney);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            MediaPlayerTool.getInstance(mContext).startPlay(SpeechUtils.convertMoney(realPayMoney));
        }else  if (BusinessManager.NETCOUPONLIST.equals(type)) {
//           MyTools.showToastShort(true,map+"",mContext);
            List<String> groups = (List<String>) map.get("CouponNameList");
            String couponNum = "";
            for(int i=0;i<groups.size();i++){
                String couponNames = groups.get(i);
                if(couponNames.contains("晨鸟")){
                    couponNum = groups.get(i);
                    break;
                }
            }
            if(!couponNum.equals("")){
                couponNo = couponNum;
                mBusinessManager.netCouponListInfo(true, couponNum);
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

                MyTools.showToastShort(true, "优惠券码:" + content, ExitParkActivity.this);
                Log.i("ccm", "结果" + content);
                for (int i = 0; i < couponList.size(); i++) {
                    if (couponList.get(i).equals(content)) {
                        MyTools.showToastShort(true, "您已用过该优惠券了", ExitParkActivity.this);
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
                    printCntent();
                    //将出场纪录存储到数据库
                    ArrayList<ExitCarEntity> entity = new ArrayList<ExitCarEntity>();
                    ExitCarEntity mcar = new ExitCarEntity("" + plateNamberEditText.getText(), "" + enterTimeEditText.getText(), "" + exitTimeEditText.getText(),
                            "" + totalPayMoney, "" + realPayMoney, "现金支付");
                    entity.add(mcar);
                    exitcardb.add(entity);

                    //开始合成（离线）
//                    HomePageActivity.speechUtils.speakText("车辆出场成功");
                    MediaPlayerTool.getInstance(mContext).startPlay("一路顺风");
                    MyTools.showToastLong(true, "车辆出场成功", mContext);
                    clearAllActivity();
                    Intent intent = new Intent();
                    intent.setClass(ExitParkActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    try {
                        couponNo = msg.obj.toString();
                    } catch (Exception e) {
                    }
                    if (realPayMoney == 0.0) {
                        MyTools.showToastShort(true, "无需使用优惠券", ExitParkActivity.this);
                        return;
                    }
                    if (ifSpecialCoupon) {
                        MyTools.showToastShort(true, "此优惠券不能与其他优惠券叠加使用", ExitParkActivity.this);
                        return;
                    }
                    for (int i = 0; i < couponList.size(); i++) {
                        if (couponList.get(i).equals(couponNo)) {
                            MyTools.showToastShort(true, "您已用过该优惠券了", ExitParkActivity.this);
                            return;
                        }
                    }
                    if (SharedPreferencesConfig.getString(mContext, "loginFlag").equals("1")) {
                        mBusinessManager.netWebRequestCoupon(true, couponNo);
                    } else {
                        mBusinessManager.netCouponInfo(true, couponNo);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        TwoDecodeScan();
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
                if (!enterTimeEditText.getText().toString().equals("")) {
                    if (SharedPreferencesConfig.getString(this, "loginFlag").equals("1")) {
                        int IsPayLeave = 0;
                        if (mBusinessManager.getRecognizeAgain().equals("")) {
                            IsPayLeave = 1;
                        } else {
                            IsPayLeave = Integer.valueOf(mBusinessManager.getRecognizeAgain());
                        }
                        mBusinessManager.netWebRequestOut(true, IsPayLeave, webBusID, "现金支付", webtotalAmount, "" + plateCode, SharedPreferencesConfig.getString(mContext, "telephone"),
                                SharedPreferencesConfig.getString(mContext, "userName"), webPayAmount, webtotalSecs, webtotalFreeSecs, webPaySecs, "" + webFPayDate,
                                deviceId(), couponStr);
                    } else {
                        mBusinessManager.netInsertCarOutRec(true, plateNamberEditText.getText() + "",
                                enterTimeEditText.getText() + "",
                                exitTimeEditText.getText() + "",
                                realPayMoney + "", "4",
                                null, plateCode, seqNo, "", "", "", "", "", "", "", "", "", "", "", "", "", couponStr, totalPayMoney + "", "", "", false,couponNameStr);
                    }
                } else {
                    MyTools.showToastLong(true, "该车已出场或未查到进场信息...", mContext);
                    finish();
                }
                break;
            case R.id.activity_exit_park_info_BusCard:
                if (realPayMoney > 0) {
                    Intent it = new Intent(getApplicationContext(), BusCardActivity.class);
                    it.putExtra("BusCardMoney", "" + realPayMoney);
                    it.putExtra("type", typePlate);
                    it.putExtra("plateNamber", plateNamberEditText.getText() + "");
                    it.putExtra("outtime", exitTimeEditText.getText() + "");
                    it.putExtra("plateCode", plateCode);
                    it.putExtra("seqNo", seqNo);
                    it.putExtra("Entertime", "" + enterTimeEditText.getText());
                    it.putExtra("carType", carType);
                    it.putExtra("coupon", couponStr);
                    it.putExtra("receivable", sumEditText.getText() + "");
                    it.putExtra("couponName", couponNameStr);
                    startActivity(it);
                } else {
                    MyTools.showToastShort(true, "您已无需支付费用", ExitParkActivity.this);
                }
                break;
            case R.id.activity_exit_park_info_break:
                finish();
                break;
            case R.id.activity_exit_park_info_coupon:
                if (realPayMoney == 0.0) {
                    MyTools.showToastShort(true, "无需使用优惠券", ExitParkActivity.this);
                    break;
                }
                if (ifSpecialCoupon) {
                    MyTools.showToastShort(true, "此优惠券不能与其他优惠券叠加使用", ExitParkActivity.this);
                    break;
                }
                Intent intent = new Intent(ExitParkActivity.this,
                        CaptureActivity.class);
                intent.putExtra("cflag", "1");
                startActivityForResult(intent, REQUEST_COUPONCODE_SCAN);
                break;
            case R.id.btn_coupon_submit:
                if (realPayMoney == 0.0) {
                    MyTools.showToastShort(true, "无需使用优惠券", ExitParkActivity.this);
                    break;
                }
                if (ifSpecialCoupon) {
                    MyTools.showToastShort(true, "此优惠券不能与其他优惠券叠加使用", ExitParkActivity.this);
                    break;
                }
                String content = et_couponnum.getText().toString().trim();
                if (content.equals("")) {
                    MyTools.showToastShort(true, "请输入优惠券码", ExitParkActivity.this);
                    break;
                }
                for (int i = 0; i < couponList.size(); i++) {
                    if (couponList.get(i).equals(content)) {
                        MyTools.showToastShort(true, "您已用过该优惠券了", ExitParkActivity.this);
                        return;
                    }
                }
                couponNo = content;
                if (SharedPreferencesConfig.getString(mContext, "loginFlag").equals("1")) {
                    mBusinessManager.netWebRequestCoupon(true, couponNo);
                } else {
                    mBusinessManager.netCouponInfo(true, couponNo);
                }
                break;
            case R.id.btn_elect_payment:
                if (realPayMoney > 0) {
                    Intent it = new Intent(getApplicationContext(), PaymentActivity.class);
                    it.putExtra("realPayMoney", "" + realPayMoney);
                    it.putExtra("type", typePlate);
                    it.putExtra("plateNamber", plateNamberEditText.getText() + "");
                    it.putExtra("outtime", exitTimeEditText.getText() + "");
                    it.putExtra("plateCode", plateCode);
                    it.putExtra("seqNo", seqNo);
                    it.putExtra("Entertime", "" + enterTimeEditText.getText());
                    it.putExtra("carType", carType);
                    it.putExtra("coupon", couponStr);
                    it.putExtra("receivable", sumEditText.getText() + "");
                    it.putExtra("parkName", ParkName);
                    it.putExtra("couponName", couponNameStr);
                    startActivity(it);
                } else {
                    MyTools.showToastShort(true, "您已无需支付费用", ExitParkActivity.this);
                }
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
                mPrinter.printText("   " + mBusinessManager.getExitTitle()+"\n\n");//标题
                mPrinter.printText("车牌号码:" + plateNamberEditText.getText() + "\n");
                mPrinter.printText("入场时间:" + enterTimeEditText.getText() + "\n");
                mPrinter.printText("出场时间:" + exitTimeEditText.getText() + "\n");
                mPrinter.printText("停车时长:" + differenceTime(enterTimeEditText.getText().toString(),
                        exitTimeEditText.getText().toString()) + "\n");
                mPrinter.printText("应收金额:" + totalPayMoney + "\n");
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!CouponWriteActivity.couponNum.equals("")) {
            if(CouponWriteActivity.typeFlag==1){
                couponNo = CouponWriteActivity.couponNum;
                mBusinessManager.netCouponListInfo(true, couponNo);
            }else{
                for (int i = 0; i < couponList.size(); i++) {
                    if (couponList.get(i).equals(CouponWriteActivity.couponNum)) {
                        MyTools.showToastShort(true, "您已用过该优惠券了", ExitParkActivity.this);
                        return;
                    }
                }
                couponNo = CouponWriteActivity.couponNum;
                if (SharedPreferencesConfig.getString(mContext, "loginFlag").equals("1")) {
                    mBusinessManager.netWebRequestCoupon(true, couponNo);
                } else {
                    mBusinessManager.netCouponInfo(true, couponNo);
                }
            }
            CouponWriteActivity.couponNum = "";
            CouponWriteActivity.typeFlag = 0;
        }
    }

}
