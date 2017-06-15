package willsong.cn.commpark.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import w.song.orchid.activity.OBaseActivity;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.Socket.SocThread;
import willsong.cn.commpark.activity.luzhengbean.BaseRepBean;

/**
 * Created by Administrator on 2016/10/24 0024.
 */

public class luzhengActivity extends OBaseActivity {

    Handler inHandler;
    Handler ouHandler;

    private String TAG = "===Client===";
    private String TAG1 = "===Send===";

    SocThread socThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_luzheng);
        initHandler();
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
                            Log.i("ccm",rep.seqno + "");
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
    public void startSocket(View view){
        socThread= new SocThread(inHandler,ouHandler,getApplicationContext());
        socThread.start();
    }


    @Override
    protected void onStop() {
        super.onStop();
        socThread.close();
    }

    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    //java 合并两个byte数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
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
}
