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

import com.android.rfid.DevSettings;
import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.Http;
import willsong.cn.commpark.activity.Bean.AlarmState;
import willsong.cn.commpark.activity.Bean.FunctionCode;
import willsong.cn.commpark.activity.Bean.ParkType;
import willsong.cn.commpark.activity.Bean.PayType;
import willsong.cn.commpark.activity.Bean.WorkingState;
import willsong.cn.commpark.activity.Socket.ThreadSocket;
import willsong.cn.commpark.activity.util.CAckFrame;
import willsong.cn.commpark.activity.util.CBaseFrame;
import willsong.cn.commpark.activity.util.CEntryFrame;
import willsong.cn.commpark.activity.util.CLeaveFrame;
import willsong.cn.commpark.activity.util.CParkingFrame;
import willsong.cn.commpark.activity.util.CStatusFrame;
import willsong.cn.commpark.activity.util.CTimeFrame;
import willsong.cn.commpark.activity.util.Util;

import static w.song.orchid.util.BusinessManager.HEADCODE_OK;
import static w.song.orchid.util.NetDataManager.TIMEOUT_CONNECTION;
import static willsong.cn.commpark.activity.luzhengActivity.bytesToHexString;
import static willsong.cn.commpark.activity.util.shizhong.StringToAscii;

/**
 * Created by Administrator on 2016/10/24 0024.
 */

public class ShiZhongService extends Service {

    Handler inHandler;
    Handler ouHandler;

    private String TAG = "===Client===";
    private String TAG1 = "===Send===";

    ThreadSocket socThread;


    short companyCode;
    short addressCode;
    int receiverAddressCode;


    ShiZhongCart serviceReceiver;
    SharedPreferences sp;

    DevSettings dev;

    protected BusinessManager mBusinessManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initHandler();
        startSocket();
        sp = getSharedPreferences("SP", this.MODE_PRIVATE);
        dev = new DevSettings(this);
        mBusinessManager = new BusinessManager(this);
        companyCode = Short.parseShort(sp.getString("code", ""));
        addressCode = Short.parseShort(sp.getString("address", ""));
        receiverAddressCode = Integer.parseInt(sp.getString("raddress", ""));
        serviceReceiver = new ShiZhongCart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Util.SHIZHONGCART);
        registerReceiver(serviceReceiver, filter);

    }

    class ShiZhongCart extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int cont = intent.getIntExtra("index", -1);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            final byte[] tmp;
            switch (cont) {
                case 1://状态
                    break;
                case 2://进场
                    int actType = intent.getIntExtra("actType", 0);
                    String actTime = intent.getStringExtra("actTime");
                    String carNumber = intent.getStringExtra("carNumber");
                    int totRemainNum = intent.getIntExtra("totRemainNum", 0);
                    int monthlyRemainNum = intent.getIntExtra("monthlyRemainNum", 0);
                    int guestRemainNum = intent.getIntExtra("guestRemainNum", 0);
                    String date = getSubTime(actTime);
                    Log.i("date", date);
                    tmp = PostEntryFrame(
                            date
                            , getParkType(actType), (short) totRemainNum
                            , (short) monthlyRemainNum, (short) guestRemainNum
                            , carNumber).Frame.ToBytes();
                    socThread.Send(DisGetHexToChs("", tmp).getBytes());
                    break;
                case 3://出场
                    int outactType = intent.getIntExtra("actType", 0);
                    String outActTime = intent.getStringExtra("actTime");
                    int outTotRemainNum = intent.getIntExtra("totRemainNum", 0);
                    int outMonthlyRemainNum = intent.getIntExtra("monthlyRemainNum", 0);
                    int outGuestRemainNum = intent.getIntExtra("guestRemainNum", 0);
                    String outPlate = intent.getStringExtra("plate");
                    int outPayMoney = intent.getIntExtra("payMoney", 0);
                    int outParkingTimeLength = intent.getIntExtra("parkingTimeLength", 0);
                    int outCartype = intent.getIntExtra("carType", 1);//车辆类型
                    int outPytype = intent.getIntExtra("pytype", 0);
                    tmp = PostLeaveFrame(getSubTime(outActTime)
                            , getParkType(outactType), (short) outTotRemainNum
                            , (short) outMonthlyRemainNum, (short) outGuestRemainNum
                            , outPlate
                            , outParkingTimeLength
                            , outPayMoney
                            , getPayType(outPytype)).Frame.ToBytes();
                    socThread.Send(DisGetHexToChs("", tmp).getBytes());
                    break;
                case 4://心跳
                    //PostParkFrame((short)200,(short)50,(short)150,(short)100,(short)10,(short)90);
                    break;
                case 5://时间校准

                    break;
            }
        }
    }


    void initHandler() {
        inHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Log.i(TAG, "mhandler接收到msg=" + msg.what);
                    if (msg.obj != null) {
                        byte[] lin = (byte[]) msg.obj;
                        int req = Client_Received(lin);
                        switch (req) {
                            case 0://时间

                                break;
                            case 1://进场
//                                Toast.makeText(ShiZhongService.this,"市中进场",Toast.LENGTH_SHORT).show();
                                break;
                            case 2://出场
                                //                               Toast.makeText(ShiZhongService.this,"市中出场场",Toast.LENGTH_SHORT).show();
                                break;
                            case 3://车位
//                                Toast.makeText(ShiZhongService.this,"市中车位数据",Toast.LENGTH_SHORT).show();
                                break;
                            case 4://状态
//                                  Toast.makeText(ShiZhongService.this,"市中状态",Toast.LENGTH_SHORT).show();
                                break;
                            default://时间

                                break;
                        }

                    } else {
                        Log.i(TAG, "没有数据返回不更新");
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
                    Log.i("ccm", "s=====" + s);
                } catch (Exception ee) {
                    Log.i(TAG, "加载过程出现异常");
                    ee.printStackTrace();
                }
            }
        };
    }

    //连接socket
    public void startSocket() {
        socThread = new ThreadSocket(inHandler, ouHandler, getApplicationContext());
        socThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socThread.close();
        if (null != serviceReceiver)
            unregisterReceiver(serviceReceiver);
    }

    //状态
    public SendFrameInfo PostStatusFrame() {
        CStatusFrame frame = new CStatusFrame(WorkingState.Normal, AlarmState.None);
        frame.CreateSenderAddress(companyCode, addressCode);
        frame.CreateReceiverAddress(receiverAddressCode);
        frame.setM_Id(getSInvokeId());
        return getFrame(frame);
    }

    /**
     * 进场
     */
    SendFrameInfo PostEntryFrame(String et, byte pt, short remainderTotal, short remainderByMonth,
                                 short remainderVister, String licPlate) {
        CEntryFrame frame = new CEntryFrame(et, pt, remainderTotal, remainderByMonth, remainderVister, licPlate);
        frame.CreateSenderAddress(companyCode, addressCode);
        frame.CreateReceiverAddress(receiverAddressCode);
        frame.setM_Id(getSInvokeId());
        return getFrame(frame);
    }


    /**
     * 出场
     *
     * @param et               离场时间
     * @param pt               离场类别
     * @param remainderTotal   总剩余车位
     * @param remainderByMonth 月租剩余车位
     * @param remainderVister  时租房客剩余车位
     * @param licPlate         离场车牌
     * @param duration         停车时长
     * @param fee              收费金额
     * @param pay              支付类型
     */
    SendFrameInfo PostLeaveFrame(String et, byte pt, short remainderTotal,
                                 short remainderByMonth, short remainderVister, String licPlate,
                                 int duration, int fee, byte pay) {
        CLeaveFrame frame = new CLeaveFrame(et, pt, remainderTotal, remainderByMonth,
                remainderVister, licPlate, duration, fee, pay);
        frame.CreateSenderAddress(companyCode, addressCode);
        frame.CreateReceiverAddress(receiverAddressCode);
        frame.setM_Id(getSInvokeId());
        return getFrame(frame);
    }

    /**
     * 车位
     *
     * @param tCnt    总车位
     * @param tMonth  月租长包车位
     * @param tVister 时租访客车位
     * @param rCnt    总剩余车位
     * @param rMonth  月租剩余车位
     * @param rVister 时租剩余车位
     */
    SendFrameInfo PostParkFrame(short tCnt, short tMonth, short tVister, short rCnt,
                                short rMonth, short rVister) {
        CParkingFrame frame = new CParkingFrame(tCnt, tMonth, tVister, rCnt, rMonth, rVister);
        frame.CreateSenderAddress(companyCode, addressCode);
        frame.CreateReceiverAddress(receiverAddressCode);
        frame.setM_Id(getSInvokeId());
        return getFrame(frame);
    }

    /**
     * 时间校准
     *
     * @param et
     * @return
     */
    public SendFrameInfo PostTimeFrame(String et) {
        CTimeFrame frame = new CTimeFrame(et);
        frame.CreateSenderAddress(companyCode, addressCode);
        frame.CreateReceiverAddress(receiverAddressCode);
        frame.setM_Id(getSInvokeId());
        return getFrame(frame);
    }

    public SendFrameInfo getFrame(CBaseFrame frame) {
        SendFrameInfo item = new SendFrameInfo();
        item.Id = frame.getM_Id();
        item.Frame = frame;
        item.SendTime = new Date();
        item.IsSend = false;
        item.SendTimes = 0;
        return item;
    }

    private static volatile short SInvokeId = 0;

    public static short getSInvokeId() {
        if (SInvokeId >= 0xFFFF)
            SInvokeId = 0;
        short ret = SInvokeId;
        SInvokeId++;
        return ret;
    }

    static byte[] m_TotalBuffer = new byte[0];

    static String a10To16(int s) {
        String ss = Integer.toHexString(s & 0xFF).toUpperCase();
        if (ss.length() == 1) {
            return "0" + ss;
        } else {
            return ss;
        }
    }

    static byte[] GetHexToChs(String hex, byte[] bytes) {
        bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            try {
                // 每两个字符是一个 byte。
                bytes[i] = (byte) (0xff & Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16));
                ;
            } catch (Exception e) {
            }
        }
        ;
        return bytes;
    }

    static String DisGetHexToChs(String hex, byte[] bytes) {
        hex = "";
        for (int i = 0; i < bytes.length; i++) {
            try {
                // 每两个字符是一个 byte。
                hex += a10To16(bytes[i]);
            } catch (Exception e) {
            }
        }
        ;
        return hex;
    }

    /**
     * 将short转换成byte类型
     */
    public static byte[] short2Byte(short a) {
        byte[] b = new byte[2];

        b[0] = (byte) (a >> 8);
        b[1] = (byte) (a);

        return b;
    }

    static class SendFrameInfo {
        public short Id;
        public CBaseFrame Frame;
        public Date SendTime;
        public int SendTimes;
        public Boolean IsSend;
    }

    /**
     * byte类型转换成String
     */
    public static String bytes2Hex(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] res = new char[src.length * 2]; // 每个byte对应两个字符
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >> 4 & 0x0f]; // 先存byte的高4位
            res[j++] = hexDigits[src[i] & 0x0f]; // 再存byte的低4位
        }
        return new String(res);
    }

    //修改本地时间
    public void testDate(String datetime) {//测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    int count = 0;

    public int Client_Received(byte[] req) throws UnsupportedEncodingException {

        //      m_TotalBuffer = GetHexToChs(StringToAscii(),null);
        String rec = new String(req, 0, req.length - 2, "UTF-8");
        String hex = bytesToHexString(req); // 转换成16进制
        String ascii = StringToAscii(hex);  //装换成Ascii编码
        byte[] chs = GetHexToChs(rec, null);//装成byte【】
        int stxPos = 0;
        if (chs.length > 0) {
            CAckFrame ack = CAckFrame.CreateFromBytes(chs, stxPos);
            Log.i("xxxx", "---------" + ack);
            if (ack == null) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                int[] tt = new int[6];
                tt = CTimeFrame.getTime(chs);
                setSystemTime(tt);
                if (tt != null) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(15000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            count++;
                            byte[] tmpe = PostTimeFrame(getSystemTime()).Frame.ToBytes();
                            socThread.Send(DisGetHexToChs("", tmpe).getBytes());
                            if (count == 2) {
                                byte[] tmp = PostStatusFrame().Frame.ToBytes();
                                socThread.Send(DisGetHexToChs("", tmp).getBytes());
                                startHeartBeat();
                            }
                        }
                    });
                    thread.start();
                }
            }
            if (FunctionCode.Unuse == ack.getM_AckType()) {
                return 0;
            } else if (FunctionCode.Entry == ack.getM_AckType()) {
                return 1;
            } else if (FunctionCode.Leave == ack.getM_AckType()) {
                return 2;
            } else if (FunctionCode.Park == ack.getM_AckType()) {
                return 3;
            } else if (FunctionCode.State == ack.getM_AckType()) {
                return 4;
            } else {
//
            }
        }
        return -1;
    }


    public void startHeartBeat() {
        final Http http = new Http();
        final Map<String, Object> valueMap = new Hashtable<String, Object>();
        valueMap.put("DevCode", mBusinessManager.getDevCode());
        final Gson mGson = new Gson();
        Timer time = new Timer(true);
        time.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                            mBusinessManager
                String[] info = http.ctSendRequest(mBusinessManager.SCHEARTBEAT, mGson.toJson(valueMap), Http.CtSendRequestType.POST, TIMEOUT_CONNECTION);
                if (("" + info[0]).equals("" + Http.HTTP_OK)) {// 访问正常
                    Map<String, Object> map = mGson.fromJson(info[1], Map.class);
                    Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                    String code = ("" + headMap.get("Code")).split("\\.")[0];
                    if (HEADCODE_OK.equals(code)) {
                        Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                        Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                        Double totBerthNum = (Double) dataMap.get("TotBerthNum");
                        short totBerthNumShort = totBerthNum.shortValue();

                        Double monthlyBerthNum = (Double) dataMap.get("MonthlyBerthNum");
                        short monthlyBerthNumShort = monthlyBerthNum.shortValue();

                        Double guesBerthNum = (Double) dataMap.get("GuesBerthNum");
                        short guesBerthNumShort = guesBerthNum.shortValue();

                        Double totRemainNum = (Double) dataMap.get("TotRemainNum");
                        short totRemainNumShort = totRemainNum.shortValue();

                        Double monthlyRemainNum = (Double) dataMap.get("MonthlyRemainNum");
                        short monthlyRemainNumShort = monthlyRemainNum.shortValue();

                        Double guestRemainNum = (Double) dataMap.get("GuestRemainNum");
                        short guestRemainNumShort = guestRemainNum.shortValue();

                        byte[] tep = PostParkFrame(totBerthNumShort, monthlyBerthNumShort, guesBerthNumShort,
                                totRemainNumShort, monthlyRemainNumShort, guestRemainNumShort).Frame.ToBytes();

                        socThread.Send(DisGetHexToChs("", tep).getBytes());
                    }
                    // return info;
                } else {// 访问异常
                    // return new String[] { "" + Http.EXC, isCodeShow ? Dic.PROMPT0 + "<br>" + "编号" + info[0] : Dic.PROMPT0 };
                }
            }
        }, 3000, 45000);
    }

    public byte getParkType(int actType) {
        if (0 == actType) {
            return ParkType.ByMonth;
        } else {
            return ParkType.Vister;
        }
    }

    public byte getPayType(int actType) {
        if (2 == actType) {
            return PayType.Cash;
        } else {
            return PayType.TrafficCard;
        }
    }

    //截取日期为YYYYMMDDHHMMSS
    public String getSubTime(String actTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(actTime.substring(0, 4));
        sb.append("/");
        sb.append(actTime.substring(5, 7));
        sb.append("/");
        sb.append(actTime.substring(8, 10));
        sb.append(" ");
        sb.append(actTime.substring(11, 13));
        sb.append(":");
        sb.append(actTime.substring(14, 16));
        sb.append(":");
        sb.append(actTime.substring(17, 19));
        return sb.toString();
    }

    /**
     * 时差
     *
     * @param
     * @param
     * @return
     */
    public String datetime(long l) {
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        System.out.println(hour + ":" + min + ":" + s);
//        System.out.println((int) l/1000 + "");
        return hour + ":" + min + ":" + s;
//        return (int) l/1000;
    }

    public void setSystemTime(int[] tme) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, tme[0]);
        c.set(Calendar.MONTH, tme[1] - 1);
        Log.i("time", "" + tme[1]);
        c.set(Calendar.DAY_OF_MONTH, tme[2]);
        c.set(Calendar.HOUR_OF_DAY, tme[3]);
        c.set(Calendar.MINUTE, tme[4]);
        c.set(Calendar.SECOND, tme[5]);
        long when = c.getTimeInMillis();
        dev.setCurrentTime(when);
    }

    public String getSystemTime() {
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        System.out.println(year + "/" + month + "/" + date + " " + hour + ":" + minute + ":" + second);
        return year + "/" + (month + 1) + "/" + date + " " + hour + ":" + minute + ":" + second;
    }
}
