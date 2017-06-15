package willsong.cn.commpark.activity.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.rfid.Tools;
import com.google.gson.Gson;

import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.Http;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.activity.Socket.SocThread;
import willsong.cn.commpark.activity.luzhengbean.BaseRepBean;
import willsong.cn.commpark.activity.luzhengbean.BusCard;
import willsong.cn.commpark.activity.luzhengbean.BusCardAccess;
import willsong.cn.commpark.activity.luzhengbean.Business;
import willsong.cn.commpark.activity.luzhengbean.BusinessLog;
import willsong.cn.commpark.activity.luzhengbean.CarPortCont;
import willsong.cn.commpark.activity.luzhengbean.CommTradeRecord;
import willsong.cn.commpark.activity.luzhengbean.Enter;
import willsong.cn.commpark.activity.luzhengbean.EzStop;
import willsong.cn.commpark.activity.luzhengbean.OutBus;
import willsong.cn.commpark.activity.luzhengbean.Sign;
import willsong.cn.commpark.activity.luzhengbean.commParkingRecord;
import willsong.cn.commpark.activity.luzhengbean.universal;
import willsong.cn.commpark.activity.util.SystemUtil;
import willsong.cn.commpark.activity.util.Util;

import static w.song.orchid.util.BusinessManager.HEADCODE_OK;
import static w.song.orchid.util.NetDataManager.TIMEOUT_CONNECTION;

/**
 * Created by Administrator on 2016/10/24 0024.
 */

public class LuzhengService extends Service {


    Handler inHandler;
    Handler ouHandler;

    private String TAG = "===Client===";
    private String TAG1 = "===Send===";

    SocThread socThread;

    MyCast serviceReceiver;
    protected BusinessManager mBusinessManager;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    universal universal = null;

    int index = 1;

    byte[] cardInfo;
    byte[] info;
    byte[] car_buf;

    int outSeqNotwo;//业务流水号
    int outPytype;//支付类型
    int outPayMoney;//交易金额
    String outActTime;//出场时间

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = this.getSharedPreferences("sign", this.MODE_PRIVATE);
        editor = sp.edit();
        initHandler();
        startSocket();
        serviceReceiver = new MyCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Util.MYCAST);
        registerReceiver(serviceReceiver, filter);
        mBusinessManager = new BusinessManager(this);
        try {
            universal = SystemUtil.getUniversal(LuzhengService.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startHeartBeat();
    }

    void initHandler(){
        inHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Log.i(TAG, "mhandler接收到msg=" + msg.what);
                    if (msg.obj != null) {
                        String s = msg.obj.toString();
                        if (s.trim().length() > 0) {
                            Log.i(TAG, "mhandler接收到obj=" + s);
                            Log.i(TAG, "开始更新UI");
                            Log.i(TAG, "更新UI完毕");
                            Gson gson = new Gson();
                            BaseRepBean rep = gson.fromJson(s, BaseRepBean.class);
                            BaseRepBean.CommResponse cr = rep.commResponse;
                            Log.i("ccm",rep.seqno + "");
                            if("011".equals(rep.code)){
                                //Toast.makeText(LuzhengService.this,rep.seqno + "路政成功签到",Toast.LENGTH_SHORT).show();
                            }else if("131".equals(rep.code)){
                                if("成功".equals(cr.msg))
                                    Toast.makeText(LuzhengService.this,rep.seqno + "路政成功进场",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(LuzhengService.this,cr.msg,Toast.LENGTH_SHORT).show();
                            }else if("132".equals(rep.code)){
                                if("成功".equals(cr.msg)) {
                                    if (1 == outPytype) {
                                        OutBusDataByBusCard();
                                    }
                                    Toast.makeText(LuzhengService.this,rep.seqno + "路政成功出场",Toast.LENGTH_SHORT).show();
                                }
                            }else if("161".equals(rep.code)){
                                //Toast.makeText(LuzhengService.this,rep.seqno + "路政心跳信息",Toast.LENGTH_SHORT).show();
                            }else if("151".equals(rep.code)){
                                Toast.makeText(LuzhengService.this,rep.seqno + "路政公交卡信息",Toast.LENGTH_SHORT).show();
                            }else if("141".equals(rep.code)){
                                //Toast.makeText(LuzhengService.this,rep.seqno + "路政完整记录",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.i(TAG, "没有数据返回不更新");
                        }
                    }
                } catch (Exception ee) {
                    Log.i(TAG, "加载过程出现异常");
                    ee.printStackTrace();
                }
            }
        };
        ouHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Log.i(TAG, "mhandlerSend接收到msg.what=" + msg.what);
                    String s = msg.obj.toString();
                    Gson gson = new Gson();
                    Log.i("ccm","s=====" + s);
                } catch (Exception ee) {
                    Log.i(TAG, "加载过程出现异常");
                    ee.printStackTrace();
                }
            }
        };
    }

    //连接socket
    public void startSocket(){
        socThread= new SocThread(inHandler,ouHandler,getApplicationContext());
        socThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socThread.close();
        if(null != serviceReceiver)
            unregisterReceiver(serviceReceiver);
    }

    class MyCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int cont = intent.getIntExtra("index",-1);
            if(index == Integer.MAX_VALUE){
                index = 1;
            }
            switch (cont){
                case 1://签到
                    String username = intent.getStringExtra("user_name");
                    String pwd = intent.getStringExtra("pwd");
                    String longi = intent.getStringExtra("longi");
                    String lati = intent.getStringExtra("lati");
                    String name = intent.getStringExtra("name");
                    String address = intent.getStringExtra("address");
                    sign(username,pwd,longi,lati," ",name,address);
                    break;
                case 2://进场
                    String bizSn = intent.getStringExtra("seqNo");
                    int bizSntwo = Integer.parseInt(bizSn);
                    int actType = intent.getIntExtra("actType",0);
                    String actTime = intent.getStringExtra("actTime");
                    String carNumber = intent.getStringExtra("carNumber");
                    int totRemainNum = intent.getIntExtra("totRemainNum",0);
                    int monthlyRemainNum = intent.getIntExtra("monthlyRemainNum",0);
                    int guestRemainNum = intent.getIntExtra("guestRemainNum",0);
                    enter(bizSntwo,actType,getSubTime(actTime),carNumber,totRemainNum,monthlyRemainNum,guestRemainNum);
                    break;
                case 3://出场
                    String outSeqNo = intent.getStringExtra("seqNo");
                    outSeqNotwo = Integer.parseInt(outSeqNo);
                    int outActType = intent.getIntExtra("actType",0);
                    outActTime = intent.getStringExtra("actTime");
                    int outTotRemainNum = intent.getIntExtra("totRemainNum",0);
                    int outMonthlyRemainNum = intent.getIntExtra("monthlyRemainNum",0);
                    int outGuestRemainNum = intent.getIntExtra("guestRemainNum",0);
                    String outPlate = intent.getStringExtra("plate");
                    outPayMoney = intent.getIntExtra("payMoney",0);
                    int outParkingTimeLength = intent.getIntExtra("parkingTimeLength",0);
                    int outCartype = intent.getIntExtra("carType",1);//车辆类型
                    outPytype = intent.getIntExtra("pytype",0);
                    String enterTime = intent.getStringExtra("enterTime");
                    cardInfo = intent.getByteArrayExtra("cardInfo");
                    info = intent.getByteArrayExtra("info");
                    car_buf = intent.getByteArrayExtra("car_buf");
                    OutBusData(outSeqNotwo,outActType,getSubTime(outActTime),outTotRemainNum,
                            outMonthlyRemainNum,outGuestRemainNum,outPlate,
                            outPayMoney,outParkingTimeLength,outCartype,outPytype);
                    EzStop(outPlate,outCartype,outActType,getSubTime(enterTime),getSubTime(outActTime),outParkingTimeLength,outSeqNotwo,outPayMoney);
                    break;
            }
        }
    }
    //签到(011)
    public void sign(String uid,
                     String pwd, String longi, String lati,
                     String batchCode, String name, String address){
        Object sign = new Sign(
                ++index, "011", universal,
                sp.getString("parkingSpotId",""), sp.getString("platId",""),
                uid, pwd, longi, lati, batchCode, name, address,
                sp.getString("opentime",""), sp.getString("price",""));
        socThread.Send(BusCardAccess.send(sign));
    }
    //心跳（161）
    public void HeartBeat(String actTime,int totBerthNum,int monthlyBerthNum,int guesBerthNum,int totRemainNum,int monthlyRemainNum,int guestRemainNum){
        if(index == Integer.MAX_VALUE){
            index = 1;
        }
        Object carPortCont = new CarPortCont(++index,"161",universal,mBusinessManager.getUserName(),
                " ",0,actTime,sp.getString("parkingSpotId",""),sp.getString("platId",""),totBerthNum,
                monthlyBerthNum,guesBerthNum,totRemainNum,monthlyRemainNum,guestRemainNum);
        socThread.Send(BusCardAccess.send(carPortCont));
    }

    //公交卡(151)
    public void OutBusDataByBusCard(){
        byte[] cardCityCode = new byte[2]; //城市id
        cardCityCode[0] = cardInfo[5];
        cardCityCode[1] = cardInfo[6];
        byte[] cardPhysicsNumber = new byte[4]; //卡号
        byte[] cardSurfaceNumber = new byte[11];  //卡表面号
        byte[] cardTradeCount = new byte[2];    //交易次数
        byte[] cpuCardNo = new byte[4];     //cpu卡号

        byte[] TAC = new byte[4];
        System.arraycopy(cardInfo,1,cardPhysicsNumber,0,4);
        System.arraycopy(cardInfo,24,cardSurfaceNumber,0,11);
        System.arraycopy(cardInfo,16,cardTradeCount,0,2);
        System.arraycopy(cardInfo,19,cpuCardNo,0,4);
        System.arraycopy(car_buf,5,TAC,0,4);
        long k = (cardInfo[12] & 0xFF)* 256 * 256;
        long f = (cardInfo[13] & 0xFF) * 256;
        long d = cardInfo[14] & 0xFF;
        String cardcount = cardTradeCount[0] + "" + cardTradeCount[1];
        int cardBeforeTradeMoney = (int) (k + f + d); //交易前余额
        CommTradeRecord commTradeRecord = new CommTradeRecord(sp.getString("parkingSpotId",""),sp.getString("platId",""),
                " "," ",outSeqNotwo,0,2,1,outPayMoney,outPayMoney,0,0,
                getSubTime(outActTime),0,
                Integer.parseInt(MyTools.bytesToHexString(cardCityCode)),
                0,Integer.valueOf(MyTools.bytesToHexString(cardPhysicsNumber),16),
                Integer.parseInt(MyTools.getSuf(cardSurfaceNumber)),
                Integer.parseInt(cardcount),cardBeforeTradeMoney,outPayMoney,
                Integer.parseInt(getSubTime(outActTime).substring(0,8)), Integer.parseInt(getSubTime(outActTime).substring(8,14)),
                Tools.Bytes2HexString(TAC,4),cardInfo[0],0, 0,
                Tools.Bytes2HexString(cpuCardNo,4),cardInfo[0],cardInfo[0] == 0?"00":"06",
                " "," ",88,cardInfo[18]);
        CommTradeRecord[] commTradeRecords = { commTradeRecord };
        Object busCard = new BusCard(++index,"151",universal,mBusinessManager.getUserName(),commTradeRecords);
        socThread.Send(BusCardAccess.send(busCard));
    }

    //出场
    public void OutBusData(int seq, int outActType, String outActTime,
                           int outTotRemainNum, int outMonthlyRemainNum, int outGuestRemainNum,String outPlate,
                           int outPayMoney, int outParkingTimeLength, int outCartype, int outPytype){
        Business business = new Business(" ",
                seq,sp.getString("parkingSpotId",""),sp.getString("platId",""),
                " "," ",2,outActType,
                outActTime,outPlate,
                " ",outCartype,outTotRemainNum,
                outMonthlyRemainNum,outGuestRemainNum,
                outParkingTimeLength,outPayMoney,
                outPytype,0," ");
        Business[] businesses = new Business[1];
        businesses[0] = business;
        Object outbus = new OutBus(++index,"132",universal,mBusinessManager.getUserName(),businesses);
        socThread.Send(BusCardAccess.send(outbus));
    }

    //进场(131)
    public void enter(int bizSn,int actType,String actTime,
                      String carNumber,int totRemainNum,int monthlyRemainNum,
                      int guestRemainNum){
        BusinessLog[] enterArrayList = new BusinessLog[1];
        BusinessLog businessLog = new BusinessLog(" ",
               bizSn,sp.getString("parkingSpotId",""),sp.getString("platId",""),"","",
                1,actType,actTime,carNumber," ",1,totRemainNum,monthlyRemainNum,
                guestRemainNum,101," ");
        enterArrayList[0] = businessLog;
        Object enter = new Enter(++index,"131",universal,mBusinessManager.getUserName(),enterArrayList);
        socThread.Send(BusCardAccess.send(enter));
    }

    public void EzStop(String carNumber,int outCartype,int actType,
                       String enterTime,String exitTime,int parkingTimeLength,
                       int enterSeqNo,int outPayMoney){
        commParkingRecord cpr = new commParkingRecord(1,sp.getString("parkingSpotId",""),sp.getString("platId",""),
                " "," ",carNumber,
                outCartype,actType,actType,
                enterTime,exitTime,parkingTimeLength,
                " ",enterSeqNo,
                " ",
                enterSeqNo,outPayMoney,0,outPayMoney,
                outPayMoney,0,0,0,0,0,0);
        commParkingRecord[] commParkingRecords = {cpr};
        Object object = new EzStop(++index,"141",universal,mBusinessManager.getUserName(),commParkingRecords);
        socThread.Send(BusCardAccess.send(object));
    }

    public void startHeartBeat(){
        final Http http = new Http();
        final Map<String, Object> valueMap = new Hashtable<String, Object>();
        valueMap.put("DevCode",mBusinessManager.getDevCode());
        final Gson mGson = new Gson();
        Timer time = new Timer(true);
        time.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                            mBusinessManager
                String[] info = http.ctSendRequest(mBusinessManager.SCHEARTBEAT,mGson.toJson(valueMap),Http.CtSendRequestType.POST, TIMEOUT_CONNECTION);
                if (("" + info[0]).equals("" + Http.HTTP_OK)) {// 访问正常
                    Map<String, Object> map = mGson.fromJson(info[1], Map.class);
                    Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                    String code = ("" + headMap.get("Code")).split("\\.")[0];
                    if (HEADCODE_OK.equals(code)) {
                        Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                        Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                        String actTime = (String) dataMap.get("ActTime");
                        Double totBerthNum = (Double) dataMap.get("TotBerthNum");
                        int tot = totBerthNum.intValue();
                        Double monthlyBerthNum = (Double)dataMap.get("MonthlyBerthNum");
                        int month = monthlyBerthNum.intValue();
                        Double guesBerthNum = (Double)dataMap.get("GuesBerthNum");
                        int gues = guesBerthNum.intValue();
                        Double totRemainNum = (Double)dataMap.get("TotRemainNum");
                        int totRemain = totRemainNum.intValue();
                        Double monthlyRemainNum = (Double)dataMap.get("MonthlyRemainNum");
                        int monthlyRemain = monthlyRemainNum.intValue();
                        Double guestRemainNum = (Double)dataMap.get("GuestRemainNum");
                        int guestRemain = guestRemainNum.intValue();
                        HeartBeat(getSubTime(actTime),tot,month,gues,totRemain,monthlyRemain,guestRemain);
                    }
                    // return info;
                } else {// 访问异常
                    // return new String[] { "" + Http.EXC, isCodeShow ? Dic.PROMPT0 + "<br>" + "编号" + info[0] : Dic.PROMPT0 };
                }
            }
        },0,60000);
    }

    //截取日期为YYYYMMDDHHMMSS
    public String getSubTime(String actTime){
        StringBuilder sb = new StringBuilder();
        sb.append(actTime.substring(0,4));
        sb.append(actTime.substring(5,7));
        sb.append(actTime.substring(8,10));
        sb.append(actTime.substring(11,13));
        sb.append(actTime.substring(14,16));
        sb.append(actTime.substring(17,19));
        return sb.toString();
    }
}
