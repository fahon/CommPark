package com.example.fxpsam;

import android.util.Log;

import com.android.rfid.Tools;
import com.fxpsam.nativeJni.RfidNative;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/11/22 0022.
 */

public class PsamUtil {

    private static String Tag="PsamTools";

    public static String getPsam(){
        byte[] POSID = new byte[6];    //psam号

        RfidNative rfidNative = new RfidNative();
        //Open Port
        int ret0 = rfidNative.open(14, 115200);
        if (ret0 < 0) {
            LogE(Tag, "Err:rfidNative.open\n");
        } else {
            LogE(Tag, "OK:rfidNative.open\n");
        }

        byte[] Version = new byte[64];
        Arrays.fill(Version, (byte) 0);
        rfidNative.sptcreaderapigetver(Version); //获取动态库版本号
        LogE(Tag, "Get Version: " + Version.toString() + "\n");

        //Psam Init
        byte[] PSamNo = new byte[8];
        Arrays.fill(PSamNo, (byte) 0);
        int ret = rfidNative.sptcreaderapipsaminit(1, PSamNo); //PSAM卡初始化
        if (ret == 0) {
            //memcpy(POSID,PSamNo,6);
            for (int i = 0; i < 6; i++) POSID[i] = PSamNo[i];
        }
         String psam = Tools.bytesToHexString(POSID);

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

        return psam;
    }

    //Log信息打印
    private static void LogE(String tag, String info) {
        Log.e(tag, info);
    }
}
