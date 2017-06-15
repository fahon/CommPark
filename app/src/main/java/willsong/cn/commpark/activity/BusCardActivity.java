package willsong.cn.commpark.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.rfid.Tools;
import com.example.fxpsam.PsamTools;
import com.fxpsam.nativeJni.RfidNative;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.database.AbnormalCarDB;
import willsong.cn.commpark.activity.database.AbnormalCarEntity;
import willsong.cn.commpark.activity.database.EXITCARDB;
import willsong.cn.commpark.activity.database.ExitCarEntity;
import willsong.cn.commpark.activity.luzhengbean.BusCard;
import willsong.cn.commpark.activity.util.Util;
import willsong.cn.commpark.activity.widget.MediaPlayerTool;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;
import willsong.cn.commpark.activity.widget.SpeechUtils;

import static com.android.rfid.Tools.Bytes2HexString;
import static com.example.fxpsam.CartTools.getCart;
import static com.printer.sdk.PrinterInstance.mPrinter;

public class BusCardActivity extends OBaseActivity {
    private Button chargeButton;
    private EditText sumEditText;
    private Map<String, byte[]> maps;

    String BusCardMoney;

    LinearLayout bus_money;
    TextView sumMoney, amount_money, tx;
    Button bus_break;

    String plateNamber, outtime, plateCode, seqNo, EnterTime;
    int type, carType;
    byte[] cardInfo, inbuf, car_buf;
    String couponStr = "";//优惠券码
    String receivable = "";//应收金额
    int TerminalTenSeq = 0;//终端交易流水号
    private String couponNameStr = "";//优惠券名称集
    //----------------
    private EXITCARDB exitcardb;
    //营运单位代码,公交卡交易时间,城市id，卡物理值,卡表面号,卡计数器,cpu卡号,交易认证码,POSID,终端交易流水号
    String corpId="",tradeTime="",cityCode ="",cardNumber ="",card ="",cardcount="",cardTrade="",cpuCar="",pa="",terminalNo="";
    int cardMoney,money,transportCardType,icType,cardVer;//交易前余额,交易金额,交通卡卡类型，芯片类型,卡版本号
    private AbnormalCarDB abnormalCarDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_bus_card);
        setRightButtonVisible(false);
        setTitleText("公交卡支付");
        abnormalCarDB = new AbnormalCarDB(this);
        if (!abnormalCarDB.checkColumnExists2("payType")) {
            abnormalCarDB.onUpgrade(1, 2, "payType");
        }
        if (!abnormalCarDB.checkColumnExists2("couponNameStr")) {
            abnormalCarDB.onUpgrade(1, 2, "couponNameStr");
        }
        exitcardb = new EXITCARDB(this);
        chargeButton = (Button) findViewById(R.id.activity_bus_card_Button_charge);
        sumEditText = (EditText) findViewById(R.id.activity_bus_card_EditText_sum);
        oBack.setVisibility(View.GONE);
        oMenu.setVisibility(View.GONE);
        BusCardMoney = getIntent().getStringExtra("BusCardMoney");
        plateNamber = getIntent().getStringExtra("plateNamber");//场内码
        outtime = getIntent().getStringExtra("outtime");
        plateCode = getIntent().getStringExtra("plateCode");
        seqNo = getIntent().getStringExtra("seqNo");
        try {
            TerminalTenSeq = Integer.valueOf(seqNo);
        }catch (Exception e){
        }
        type = getIntent().getIntExtra("type", 0);
        EnterTime = getIntent().getStringExtra("Entertime");
        carType = getIntent().getIntExtra("carType", 0);
        couponStr = getIntent().getStringExtra("coupon");
        receivable = getIntent().getStringExtra("receivable");
        couponNameStr = getIntent().getStringExtra("couponName");

        bus_money = (LinearLayout) findViewById(R.id.bus_money);
        sumMoney = (TextView) findViewById(R.id.sum_money);
        amount_money = (TextView) findViewById(R.id.amount_money);
        bus_break = (Button) findViewById(R.id.bus_break);
        tx = (TextView) findViewById(R.id.tx);
        if (null != BusCardMoney && !("".equals(BusCardMoney))) {
            sumEditText.setText(BusCardMoney);
            sumEditText.setSelection(BusCardMoney.length());
        }
        bus_break.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        chargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyTools.isNullOrAirForString("" + sumEditText.getText())) {
                    MyTools.showToastLong(true, "请输入金额", mContext);
                    return;
                }
                corpId = mBusinessManager.getIntentcorpId();//营运单位代码
                if (corpId.length() > 0) {
                    if(Double.parseDouble(sumEditText.getText().toString())<0.1){
                        MyTools.showToastLong(true, "消费金额必须大于等于1角", mContext);
                        return;
                    }
                    Map<String,String> map =  PsamTools.getCart( BusCardActivity.this);
                    if(map.size()<1){
                        MyTools.showToastLong(true, "扣款失败", mContext);
                    }else{
                        mBusinessManager.netIsBlackCard(false,map.get("cardPhysicalNo").toUpperCase());//卡号（卡物理值）
                    }
                } else {
                    MyTools.showToastLong(true, "请输入运营商业代码", mContext);
                    return;
                }
            }
        });
    }


    @Override
    public void refreshView(String types, final Map<String, Object> map) {
        super.refreshView(types, map);
        if (BusinessManager.NETINSERTCAROUTREC.equals(types)) {
            if ("1".equals(mBusinessManager.getExitPrint())) {
                if (mPrinter == null) {
                    //bu.open();
                } else {
                    mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
                    mPrinter.printText("   " + mBusinessManager.getExitTitle()+"\n\n");
                    mPrinter.printText("车牌号码:" + plateNamber + "\n");
                    mPrinter.printText("入场时间:" + EnterTime + "\n");
                    mPrinter.printText("出场时间:" + outtime + "\n");
                    mPrinter.printText("停车时长:" + differenceTime(EnterTime, outtime) + "\n");
                    mPrinter.printText("应收金额:" + receivable + "\n");
                    mPrinter.printText("实收金额:" + sumEditText.getText() + "\n\n");
                    mPrinter.printText("-------------------------\n");
                    //                   mPrinter.printText("上海软杰智能设备有限公司研制\n");
//                    mPrinter.printText("上海普天网络技术有限公司研制\n");
//                    mPrinter.printText("   TEL:021-51099719");
                    if(!mBusinessManager.getIntentEndCo().equals("")){
                        mPrinter.printText(mBusinessManager.getIntentEndCo()+"\n");
                    }else{
                        mPrinter.printText("上海软杰智能设备有限公司研制\n");
                    }
                    if(!mBusinessManager.getIntentEndTel().equals("")){
                        mPrinter.printText("   TEL:"+mBusinessManager.getIntentEndTel());
                    }else{
                        mPrinter.printText("   TEL:021-51099719");
                    }
                    mPrinter.printText("\n\n\n");
                }
            }
            //路政出场
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if ("1".equals(mBusinessManager.getLuZheng())) {
                        Intent intent = new Intent(Util.MYCAST);
                        intent.putExtra("index", 3);
                        intent.putExtra("seqNo", seqNo);
                        Double actTime = (Double) map.get("ActType");
                        int act = actTime.intValue();
                        intent.putExtra("actType", act);
                        intent.putExtra("enterTime", EnterTime);

                        Double totBerthNum = (Double) map.get("TotRemainNum");
                        int tot = totBerthNum.intValue();
                        Double monthlyBerthNum = (Double) map.get("MonthlyRemainNum");
                        int month = monthlyBerthNum.intValue();
                        Double guesBerthNum = (Double) map.get("GuestRemainNum");
                        int gues = guesBerthNum.intValue();
                        intent.putExtra("totRemainNum", tot);
                        intent.putExtra("monthlyRemainNum", month);
                        intent.putExtra("guestRemainNum", gues);
                        intent.putExtra("actTime", outtime);//出场时间
                        intent.putExtra("plate", plateNamber);
                        double money = Double.parseDouble(sumEditText.getText().toString());
                        intent.putExtra("payMoney", (int) money); //收费金额
                        intent.putExtra("parkingTimeLength", datetime(EnterTime, outtime));//停车时长
                        intent.putExtra("carType", carType);
                        intent.putExtra("pytype", 1);
                        intent.putExtra("cardInfo", cardInfo);//公交卡信息
                        intent.putExtra("info", inbuf);//发起交易信息
                        intent.putExtra("car_buf", car_buf); //交易后信息
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
                        intent.putExtra("enterTime", EnterTime);

                        Double totBerthNum = (Double) map.get("TotRemainNum");
                        int tot = totBerthNum.intValue();
                        Double monthlyBerthNum = (Double) map.get("MonthlyRemainNum");
                        int month = monthlyBerthNum.intValue();
                        Double guesBerthNum = (Double) map.get("GuestRemainNum");
                        int gues = guesBerthNum.intValue();
                        intent.putExtra("totRemainNum", tot);
                        intent.putExtra("monthlyRemainNum", month);
                        intent.putExtra("guestRemainNum", gues);
                        intent.putExtra("actTime", outtime);//出场时间
                        intent.putExtra("plate", plateNamber);
                        double money = Double.parseDouble(sumEditText.getText().toString());
                        intent.putExtra("payMoney", (int) money); //收费金额
                        intent.putExtra("parkingTimeLength", datetime(EnterTime,
                                outtime));//停车时长
                        intent.putExtra("carType", carType);
                        intent.putExtra("pytype", 1);
                        intent.putExtra("cardInfo", cardInfo);//公交卡信息
                        intent.putExtra("info", inbuf);//发起交易信息
                        intent.putExtra("car_buf", car_buf); //交易后信息
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
        }else if(BusinessManager.NETISBLACKCARD.equals(types)){
            maps = PsamTools.charge((int) (Float.valueOf("" + sumEditText.getText()) * 100), BusCardActivity.this, TerminalTenSeq);
            Log.i("maps",""+maps);
            if (maps.size() < 5) {//支付失败
                MyTools.showToastLong(true, "扣款失败", mContext);
            } else {//支付成功
                tx.setVisibility(View.GONE);
                bus_money.setVisibility(View.VISIBLE);
                cardInfo = maps.get("card_info");//读取公交卡输出参数
//                    byte[] debit = maps.get("debit");
                inbuf = maps.get("inbuf");//扣款时的输入参数

                //===交易不成功，但已扣款时需获取的参数===
                byte[] TACInfo = maps.get("TAC");//匹配交易的交易认证码【已扣款但返回失败时取值】
                byte[] CardRemainMoney = maps.get("CardRemainMoney");//交易后钱包余额，分
                //=========================================
                byte[] TradeTime = maps.get("TransTime");//交易时间
                byte[] TransTime = new byte[7];
                TransTime[0] = TradeTime[0];
                TransTime[1] = TradeTime[1];
                TransTime[2] = TradeTime[2];
                TransTime[3] = TradeTime[3];
                TransTime[4] = TradeTime[4];
                TransTime[5] = TradeTime[5];
                TransTime[6] = TradeTime[6];
                tradeTime = bytesToHexString(TransTime);//交易时间
                car_buf = maps.get("car_buf");//扣款后输出参数【取交易认证码+交易后卡余额】

                byte[] psam = maps.get("pasm");//PSAM输出参数
                byte[] cardCityCode = new byte[2]; //城市id
                cardCityCode[0] = cardInfo[5];
                cardCityCode[1] = cardInfo[6];

                byte[] cardPhysicsNumber = new byte[4]; //卡号
                byte[] cardSurfaceNumber = new byte[11];  //卡表面号
                byte[] cardTradeCount = new byte[2];    //交易次数
                byte[] cpuCardNo = new byte[4];     //cpu卡号  【生成CPUCardId 卡号 16进制（CityCode+CpuCardNo+CardPhysicsNumber）需用到】
                long a = (cardInfo[12] & 0xFF) * 256 * 256;
                long b = (cardInfo[13] & 0xFF) * 256;
                long c = cardInfo[14] & 0xFF;


                byte[] TAC = new byte[4];
                System.arraycopy(cardInfo, 1, cardPhysicsNumber, 0, 4);
                System.arraycopy(cardInfo, 24, cardSurfaceNumber, 0, 11);
                System.arraycopy(cardInfo, 16, cardTradeCount, 0, 2);//卡计数器（读取公交卡的返回）
//                    System.arraycopy(car_buf, 0, cardTradeCount, 0, 2);//卡计数器（扣款输出参数）
                System.arraycopy(cardInfo, 19, cpuCardNo, 0, 4);
                if(car_buf==null){
                    System.arraycopy(TACInfo, 0, TAC, 0, 4);//交易认证码（计算M1卡交易认证码输出参数）
                }else{
                    System.arraycopy(car_buf, 5, TAC, 0, 4);//交易认证码（扣款输出参数）
                }
                cityCode = bytesToHexString(cardCityCode);//城市id


                cardNumber = bytesToHexString(cardPhysicsNumber);//卡物理值
                card = MyTools.getSuf(cardSurfaceNumber);//卡面号
                //String cardcount = bytesToHexString(cardTradeCount);
//                    int cardcount = cardTradeCount[0] * 256 + cardTradeCount[1];//卡计数器（转换成十进制返回）
                cardcount = bytesToHexString(cardTradeCount);//卡计数器(返回原数据)
                cardTrade = bytesToHexString(cpuCardNo);//cpu卡号
                cpuCar = bytesToHexString(TAC);//交易认证码
                MyTools.showToastShort(true,"交易认证码:"+cpuCar,mContext);
                pa = bytesToHexString(psam).substring(4);//POSID
                cardMoney = (int) (a + b + c); //交易前余额
                money = (int) (Float.valueOf("" + sumEditText.getText()) * 100);//交易金额
                transportCardType = cardInfo[7];//交通卡卡类型
                int cardtype = cardInfo[0];//卡片类型
                if (cardtype == 0) {
                    icType = 0;
                } else {
                    icType = 1;
                }
                cardVer = cardInfo[18];//卡版本号
                terminalNo = ""+Integer.toHexString(TerminalTenSeq).toUpperCase();//终端交易流水号(十进制转化为十六进制,字母大写)
                if (1 == type) {
                    mBusinessManager.netInsertCarOutRec(true, plateNamber,
                            "", outtime,
                            BusCardMoney, "5", "", plateCode, seqNo,
                            pa, cityCode, cardNumber, card, cardcount+"",
                            cardMoney + "", money + "", cpuCar + "", transportCardType + "",
                            cardTrade + "", icType + "", cardVer + "", corpId,couponStr,receivable+"",""+tradeTime, terminalNo,false,couponNameStr);

                } else {
                    mBusinessManager.netInsertCarOutRec(true, plateNamber,
                            "", outtime,
                            BusCardMoney, "5", "", "", seqNo,
                            pa, cityCode, cardNumber, card, cardcount+"",
                            cardMoney + "", money + "", cpuCar + "",
                            transportCardType + "", cardTrade + "", icType + "", cardVer + "", corpId,couponStr,receivable+"",""+tradeTime,
                            terminalNo,false,couponNameStr);

                }
                long k,f,d;
                float cardBeforeTradeMoney;
                if(car_buf==null){
                    k = (CardRemainMoney[0] & 0xFF) * 256 * 256;
                    f = (CardRemainMoney[1] & 0xFF) * 256;
                    d = CardRemainMoney[2] & 0xFF;
                    cardBeforeTradeMoney = (float) (k + f + d) / 100;
                }else{
                    k = (car_buf[2] & 0xFF) * 256 * 256;
                    f = (car_buf[3] & 0xFF) * 256;
                    d = car_buf[4] & 0xFF;
                    cardBeforeTradeMoney = (float) (k + f + d) / 100;
                }
                sumMoney.setText(sumEditText.getText() + "元");
                amount_money.setText(cardBeforeTradeMoney + "元");

            }
        }else if(BusinessManager.GET_BLACK_FAIL.equals(types)){
            MyTools.showToastShort(true,"获取黑名单数据异常，请重试",mContext);
        }else{
            isRetry();
        }
    }

    //byte数组
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public int datetime(String enterTime, String exitTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = null;
        Date date = null;
        try {
            now = df.parse(exitTime);
            date = df.parse(enterTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
        return (int) l;
    }

    Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    //将出场纪录存储到数据库
                    ArrayList<ExitCarEntity> entity = new ArrayList<ExitCarEntity>();
                    ExitCarEntity mcar = new ExitCarEntity(""+plateNamber, ""+EnterTime,""+outtime,
                            ""+receivable,""+sumEditText.getText(),"公交卡支付");
                    entity.add(mcar);
                    exitcardb.add(entity);

                    //开始合成（离线）
//                    HomePageActivity.speechUtils.speakText("车辆出场成功");
                    MediaPlayerTool.getInstance(mContext).startPlay("一路顺风");
                    MyTools.showToastLong(true, "车辆出场成功", mContext);
                    clearAllActivity();
                    Intent intent = new Intent();
                    intent.setClass(BusCardActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    //是否要重试出场：是:重新请求出场  否:保存数据到本地，有网络时再传
    private void isRetry(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BusCardActivity.this);
        builder.setMessage("该车出场失败，确定要重试吗？");
        builder.setCancelable(false);
        builder.setNegativeButton("保存数据", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //将异常未出场成功数据存储到数据库
                ArrayList<AbnormalCarEntity> entity = new ArrayList<AbnormalCarEntity>();
                if (1 == type) {//无牌车
                    AbnormalCarEntity mcar = new AbnormalCarEntity(plateNamber, outtime, BusCardMoney, plateCode, seqNo,
                            pa, cityCode, cardNumber, card, cardcount + "",
                            cardMoney + "", money + "", cpuCar + "", transportCardType + "",
                            cardTrade + "", icType + "", cardVer + "", corpId, couponStr, receivable + "", "" + tradeTime, terminalNo,"5",couponNameStr);
                    entity.add(mcar);
                }else{
                    AbnormalCarEntity mcar = new AbnormalCarEntity(plateNamber, outtime, BusCardMoney, "", seqNo,
                            pa, cityCode, cardNumber, card, cardcount + "",
                            cardMoney + "", money + "", cpuCar + "", transportCardType + "",
                            cardTrade + "", icType + "", cardVer + "", corpId, couponStr, receivable + "", "" + tradeTime, terminalNo,"5",couponNameStr);
                    entity.add(mcar);
                }
                abnormalCarDB.add(entity);
            }
        });
        builder.setPositiveButton("重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (1 == type) {//无牌车
                    mBusinessManager.netInsertCarOutRec(true, plateNamber,
                            "", outtime,
                            BusCardMoney, "5", "", plateCode, seqNo,
                            pa, cityCode, cardNumber, card, cardcount+"",
                            cardMoney + "", money + "", cpuCar + "", transportCardType + "",
                            cardTrade + "", icType + "", cardVer + "", corpId,couponStr,receivable+"",""+tradeTime,
                            terminalNo,false,couponNameStr);

                } else {
                    mBusinessManager.netInsertCarOutRec(true, plateNamber,
                            "", outtime,
                            BusCardMoney, "5", "", "", seqNo,
                            pa, cityCode, cardNumber, card, cardcount+"",
                            cardMoney + "", money + "", cpuCar + "",
                            transportCardType + "", cardTrade + "", icType + "", cardVer + "", corpId,couponStr,receivable+"",""+tradeTime,
                            terminalNo,false,couponNameStr);

                }
            }
        });
        builder.show();
    }
}
