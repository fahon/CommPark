package com.example.fxpsam;

import android.util.Log;

import com.fxpsam.nativeJni.RfidNative;

import java.util.Arrays;
import java.util.HashMap;

import static com.android.rfid.Tools.Bytes2HexString;

/**
 * Created by Administrator on 2016/11/2 0002.
 */

public class CartTools {
    private static String Tag="CartTools";

    public static HashMap<String,String> getCart(){
        byte[] inbuf = new byte[256];
        byte[] outbuf = new byte[256];
        byte[] POSID = new byte[6];
        int i;
        HashMap<String,String> map = new HashMap<>();
        RfidNative rfidNative = new RfidNative();
        //Open Port
        int ret0 = rfidNative.open(14, 115200);
        if (ret0 < 0) {
            LogE(Tag, "Err:rfidNative.open\n");
        } else {
            LogE(Tag, "OK:rfidNative.open\n");
        }
        //RFidPowerOn  打开射频电源
        ret0 = rfidNative.rfidpoweron();
        if (ret0 != 0) {
            LogE(Tag, "Err:rfidNative.rfidpoweron\n");
        } else {
            LogE(Tag, "OK:rfidNative.rfidpoweron\n");
        }
        //get Version   获取动态库版本号
        byte[] Version = new byte[64];
        Arrays.fill(Version, (byte) 0);
        rfidNative.sptcreaderapigetver(Version);
        LogE(Tag, "Get Version: " + Version.toString() + "\n");

        /*
        PSAM卡初始化,slot为1或2, 返回PSAM卡号,6字节hex。返回: 0 成功, !0 失败
        int sptc_reader_api_psam_init()
        */
        byte[] PSamNo = new byte[8];
        Arrays.fill(PSamNo, (byte) 0);
        int ret = rfidNative.sptcreaderapipsaminit(2, PSamNo);
        if (ret == 0) {
            //memcpy(POSID,PSamNo,6);
            for (i = 0; i < 6; i++) POSID[i] = PSamNo[i];
            //map.put("PSAMCARD",String.valueOf(POSID));
        }
        for (int j = 0;j < 3;j++) {
            byte[] outPut = new byte[64];
            byte[] SurfaceCart = new byte[11];//卡表面号
            byte[] CityCode = new byte[2]; //城市ID
            double CardLastRemain;//交易前金额
            Arrays.fill(outPut, (byte) 0);
            ret = rfidNative.sptcreaderapigetcardinfo(outPut); //读取公交卡信息
            if (ret != 0) {
                LogE(Tag, "Err:sptc_reader_api_get_card_info: " + ret + "\n");
                ret = rfidNative.sptcreaderapigetdebugstep();
                LogE(Tag, "Ok:sptc_reader_api_get_debug_step :" + ret + "\n");
                continue;
            } else {
                LogE(Tag, "OK:sptc_reader_api_get_card_info\n");
                LogE(Tag, "CardInfo = 0x" + Bytes2HexString(outPut, 48) + "\n");
            }
            System.arraycopy(outPut,24,SurfaceCart,0,11);
            map.put("SurfaceCart",getSuf(SurfaceCart));

            CityCode[0] = outbuf[5];
            CityCode[1] = outbuf[6];

            long k = (outPut[12] & 0xFF)* 256 * 256;
            long f = (outPut[13] & 0xFF) * 256;
            long d = outPut[14] & 0xFF;
            CardLastRemain = (k + f + d); //交易前余额
            map.put("cardTradeMoney",String.valueOf(CardLastRemain/100));   //交易前卡余额
            break;
        }
        ret = rfidNative.rfidpoweroff();
        if (ret != 0) {
            LogE(Tag, "Err:rfidNative.rfidpoweroff\n");
        } else {
            LogE(Tag, "OK:rfidNative.rfidpoweroff\n");
        }
        //Close Port
        ret = rfidNative.close(14);
        if (ret < 0) {
            LogE(Tag, "Err:rfidNative.close\n");
        } else {
            LogE(Tag, "OK:rfidNative.close\n");
        }
        return map;
    }


    //Log信息打印
    private static void LogE(String tag, String info) {
        Log.e(tag, info);
    }

    /**
     * 单字节转成String ascii
     * @param suf
     * @return
     */
    public static String getSuf(byte[] suf){
        String nRcvString;
        StringBuffer  tStringBuf=new StringBuffer ();
        char[] tChars=new char[suf.length];

        for(int i=0;i<suf.length;i++) {
            tChars[i] = (char) suf[i];
        }
        tStringBuf.append(tChars);

        nRcvString=tStringBuf.toString();          //nRcvString从tBytes转成了String类型的"123"
        return nRcvString;
    }
}
