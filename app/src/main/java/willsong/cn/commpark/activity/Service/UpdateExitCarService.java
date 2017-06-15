package willsong.cn.commpark.activity.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import w.song.orchid.httpService.HTTP;
import w.song.orchid.httpService.ServerCallback;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.LoginActivity;
import willsong.cn.commpark.activity.database.AbnormalCarDB;
import willsong.cn.commpark.activity.database.AbnormalCarEntity;
import willsong.cn.commpark.activity.database.RecordSteps;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class UpdateExitCarService extends Service {

    private static final String TAG = "UpdateExitCarService";
    private AbnormalCarDB abnormalCarDB;
    BusinessManager mBusinessManager;
    String url = "",bikeUrl = "";

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onStart.....");
        super.onStart(intent, startId);
    }

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        //获取传值
        Log.d(TAG, "onStartCommand.....");
        abnormalCarDB = new AbnormalCarDB(this);
        if (!abnormalCarDB.checkColumnExists2("payType")) {
            abnormalCarDB.onUpgrade(1, 2, "payType");
        }
        if (!abnormalCarDB.checkColumnExists2("couponNameStr")) {
            abnormalCarDB.onUpgrade(1, 2, "couponNameStr");
        }
        mBusinessManager = new BusinessManager(this);
        url = mBusinessManager.getAppUrl()+ "api/Car/SCInsertCarOutRec";
        bikeUrl = mBusinessManager.getAppUrl()+ "api/Car/SCInsertCarOutRecSure";

        if (("2").equals(mBusinessManager.getIntentSystemModel())) {//自行车版
            updateBikeData();
        }else{
            updateData();
        }

        //开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
//        new Thread(new updateExitCarRunnable()).start();//这个是下载的重点，是下载的过程
//        MyTools.showToastShort(true,"lalal",this);
//        ArrayList<AbnormalCarEntity> mlist = (ArrayList<AbnormalCarEntity>) abnormalCarDB.query();
//            AbnormalCarEntity entity = new AbnormalCarEntity();
//            entity.seqNo = "1300096";
//            abnormalCarDB.delete(entity);
        return super.onStartCommand(intent, flags, startId);
    }


    public void updateData() {
        ArrayList<AbnormalCarEntity> persons = (ArrayList<AbnormalCarEntity>) abnormalCarDB.query();
//        Collections.reverse(persons);//将数据倒序排列
        if(persons.size()==0){
            return;
        }
        if(!SharedPreferencesConfig.isOnline(getApplicationContext())){
            return;
        }
        for (int j = 0; j < persons.size(); j++) {//读取数据库里的数据
            final AbnormalCarEntity person2 = persons.get(j);
            Map<String, Object> valueMap = new Hashtable<String, Object>();
            valueMap.put("DevCode", mBusinessManager.getDevCode());
            valueMap.put("OutTime", person2.outTime);
            valueMap.put("SeqNo", person2.seqNo);//PosSeq  POS机流水号 10进制   停车记录表的流水ID 【终端交易流水号为同一个】
            valueMap.put("CarPlate", person2.plateNumber);
            valueMap.put("FieldCode", person2.fieldCode);
            valueMap.put("Fees", person2.busCardMoney);//实收金额
            valueMap.put("EmpName", mBusinessManager.getEmployeeName());
            valueMap.put("EmpNo", mBusinessManager.getUserNameId());
            if(person2.payType.equals("")){
                valueMap.put("PayType", "5");//支付类型
            }else{
                valueMap.put("PayType", person2.payType);//支付类型
            }
            valueMap.put("Psam", person2.posId);//PosId POS机号【PSAM卡号后4字节】
            valueMap.put("CardCityCode", person2.cityCode);//CityCode 城市代码
//                valueMap.put("CardPhysicsNumber",cardNumber);//【生成 CPUCardId 卡号（CityCode+CpuCardNo+CardPhysicsNumber）需要用到】
            valueMap.put("CardSurfaceNumber", person2.card);//CardFaceNum 卡面号
            valueMap.put("CardTradeCount", person2.cardCount);//TxnCounter 交易计数器
            valueMap.put("CardBeroreTradeMoney", person2.cardMoney);//BalBef  消费前卡余额
            valueMap.put("CardTradeMoney", person2.money);//TxnAmt  交易金额
            valueMap.put("CardTac", person2.cardTradeTac);//TAC 交易认证码
            valueMap.put("CardType", person2.transportCardType);//CardKind 交通卡卡类型
            valueMap.put("CpuCardNo", person2.cpuCar);//【生成 CPUCardId 卡号（CityCode+CpuCardNo+CardPhysicsNumber）需要用到】
            valueMap.put("ICType", person2.icType);//卡片类型,0-M1,1-CPU
            valueMap.put("CardVer", person2.cardVer);//CardVerNo 卡内版本号
            valueMap.put("CardBusinessCode", 52);
            valueMap.put("CorpId", person2.corpId);//CorpId  营运单位代码
            valueMap.put("Coupon", person2.couponStr);//优惠券号集
            valueMap.put("Amount", person2.payMoney);//应收金额
            valueMap.put("CardDateTime", person2.CardTradeTime);//公交卡交易时间
            valueMap.put("TerminalTenSeq", person2.terminalNo);//终端交易流水号
            valueMap.put("CouponName", person2.couponNameStr);//优惠券名称集
            HTTP.serverCall(getApplicationContext(), url, valueMap, new ServerCallback() {
                @Override
                public boolean serverCallback(String name, Object data, int code, String desc, String json, Map<String, Object> request_params) {
                    if (null != data) {
                        Map<String, Object> map = (Map<String, Object>) data;
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String mcode = ("" + headMap.get("Code")).split("\\.")[0];
                        if ("200".equals(mcode)) {
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.obj = person2.seqNo;
                            handler.sendMessage(msg);
                        } else {
                            Message msg = Message.obtain();
                            msg.what = 1;
                            msg.obj = person2.seqNo;
                            handler.sendMessage(msg);
                        }
                    }
                    return false;
                }
            });
        }
    }

    //自行车版
    public void updateBikeData() {
        ArrayList<AbnormalCarEntity> persons = (ArrayList<AbnormalCarEntity>) abnormalCarDB.query();
//        Collections.reverse(persons);//将数据倒序排列
        if(persons.size()==0){
            return;
        }
        if(!SharedPreferencesConfig.isOnline(getApplicationContext())){
            return;
        }
        for (int j = 0; j < persons.size(); j++) {//读取数据库里的数据
            final AbnormalCarEntity person2 = persons.get(j);
            Map<String, Object> valueMap = new Hashtable<String, Object>();
            valueMap.put("DevCode", mBusinessManager.getDevCode());
            valueMap.put("EmpName", mBusinessManager.getEmployeeName());
            valueMap.put("EmpNo", mBusinessManager.getUserNameId());
            valueMap.put("SeqNo", person2.seqNo);//流水号
            valueMap.put("Amount", person2.payMoney);//应收金额
            valueMap.put("Fees", person2.busCardMoney);//实收金额
            if(person2.payType.equals("")){
                valueMap.put("PayType", "5");//支付类型
            }else{
                valueMap.put("PayType", person2.payType);//支付类型
            }
            valueMap.put("Coupon", person2.couponStr);//优惠券号集
            valueMap.put("CouponName", person2.couponNameStr);//优惠券名称集
            HTTP.serverCall(getApplicationContext(), bikeUrl, valueMap, new ServerCallback() {
                @Override
                public boolean serverCallback(String name, Object data, int code, String desc, String json, Map<String, Object> request_params) {
                    if (null != data) {
                        Map<String, Object> map = (Map<String, Object>) data;
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String mcode = ("" + headMap.get("Code")).split("\\.")[0];
                        if ("200".equals(mcode)) {
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.obj = person2.seqNo;
                            handler.sendMessage(msg);
                        } else {
                            Message msg = Message.obtain();
                            msg.what = 1;
                            msg.obj = person2.seqNo;
                            handler.sendMessage(msg);
                        }
                    }
                    return false;
                }
            });
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {//成功
                //删除表里的该条数据
                AbnormalCarEntity entity = new AbnormalCarEntity();
                entity.seqNo = ""+msg.obj;
                abnormalCarDB.delete(entity);
            } else if (msg.what == 1) {//失败

            }
        }
    };

}
