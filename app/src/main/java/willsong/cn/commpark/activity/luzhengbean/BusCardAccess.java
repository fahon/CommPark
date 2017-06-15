package willsong.cn.commpark.activity.luzhengbean;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/10/20 0020.
 */

public class BusCardAccess {
    public static byte[] send(Object obj){
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        Log.i("ccm",json.length() + "");
        Log.i("ccm",json.getBytes().length + "xxxxxx");
        try {
            byte[] top = ReqTop.intToByteArray(json.getBytes("UTF-8").length);
            Log.i("ccm",json);
            Log.i("ccm",json.getBytes("UTF-8").length + "xxxxxx");
            top = Arrays.copyOf(top,top.length+1);
            top[top.length - 1] = 0;
            byte[] mes = byteMerger(top,json.getBytes("UTF-8"));
            Log.i("ccm",json.getBytes().length + "-------");
            return mes;
        }catch (Exception e){}
        return null;
    }

    //java 合并两个byte数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}
