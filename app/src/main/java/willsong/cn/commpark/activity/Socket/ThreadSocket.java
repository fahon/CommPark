package willsong.cn.commpark.activity.Socket;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import willsong.cn.commpark.activity.util.CAckFrame;

import static willsong.cn.commpark.activity.util.shizhong.StringToAscii;

/**
 * Created by Administrator on 2016/10/24 0024.
 */

public class ThreadSocket extends Thread{

    private String ip = "";
    private int port = 0;
    private String TAG = "socket thread";
    private int timeout = 10000;

    public Socket client = null;
    PrintWriter out;
    OutputStream outputStream;
    static BufferedReader in;
    public boolean isRun = false;
    static Handler inHandler;
    static Handler  outHandler;
    Context ctx;
    private String TAG1 = "===Send===";
    SharedPreferences sp;

    public ThreadSocket(Handler handlerin, Handler handlerout, Context context) {
        inHandler = handlerin;
        outHandler = handlerout;
        ctx = context;
        Log.i(TAG, "创建线程socket");
        sp = ctx.getSharedPreferences("SP", ctx.MODE_PRIVATE);
        ip = sp.getString("shizhong_ip", "");
        port = Integer.parseInt(sp.getString("shizhong_port","0"));
    }

    /**
     * 连接socket服务器
     */
    public void conn() {
        try {
            if(!"".equals(ip) && 0 != port) {
                Log.i(TAG, "连接中……");
                client = new Socket(ip, port);
                client.setSoTimeout(timeout);// 设置阻塞时间
                Log.i(TAG, "连接成功");
                isRun = true;
                in = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        client.getOutputStream())), true);
                outputStream = client.getOutputStream();
                Log.i(TAG, "输入输出流获取成功");
            }
        } catch (UnknownHostException e) {
            Log.i(TAG, "连接错误UnknownHostException 重新获取");
            e.printStackTrace();
            conn();
        } catch (IOException e) {
            Log.i(TAG, "连接服务器io错误");
            e.printStackTrace();
        } catch (Exception e) {
            Log.i(TAG, "连接服务器错误Exception" + e.getMessage());
            e.printStackTrace();
        }

    }
    /**
     * 实时接受数据
     */
    @Override
    public void run() {
        Log.i(TAG, "线程socket开始运行");
            Log.i(TAG, "1.run开始");
            byte[] lin = new byte[256];
            InputStream inputStream;
            DataInputStream input = null;
            int length = -1;
            String mmsg;
        if(null == client)
            conn();
            try {
                if(null != client) {
                    if (!"".equals(ip) && 0 != port) {
                        inputStream = client.getInputStream();
                        input = new DataInputStream(inputStream);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        while (isRun) {
            try {
                if (client != null) {
                    Log.i(TAG, "2.检测数据");
                    Log.i(TAG, "4.start set Message");
                    length = input.read(lin);
                    if(length != -1){
                        Message msg = inHandler.obtainMessage();
                        msg.obj = lin;
                        inHandler.sendMessage(msg);// 结果返回给UI处理
                        Log.i(TAG1, "5.send to handler");
                    }
                } else {
                    Log.i(TAG, "没有可用连接");
                    conn();
                }
            } catch (Exception e) {
                Log.i(TAG, "数据接收错误" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送数据
     *
     * @param mess
     */
    public void Send(byte[] mess) {
        try {
            if (client != null) {
                Log.i(TAG1, "发送" + mess + "至"
                        + client.getInetAddress().getHostAddress() + ":"
                        + String.valueOf(client.getPort()));
                // out.println(mess);
                // out.write(mess,0,mess.length);
                outputStream.write(mess,0,mess.length);
                out.flush();
                Log.i(TAG1, "发送成功");
                Message msg = outHandler.obtainMessage();
//                msg.obj = mess;
                msg.what = 1;
                outHandler.sendMessage(msg);// 结果返回给UI处理
            } else {
                Log.i(TAG, "client 不存在");
                Message msg = outHandler.obtainMessage();
                msg.obj = mess;
                msg.what = 0;
                outHandler.sendMessage(msg);// 结果返回给UI处理
                Log.i(TAG, "连接不存在重新连接");
                conn();
            }

        } catch (Exception e) {
            Log.i(TAG1, "send error");
            e.printStackTrace();
        } finally {
            Log.i(TAG1, "发送完毕");

        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            if (client != null) {
                Log.i(TAG, "close in");
                in.close();
                Log.i(TAG, "close out");
                out.close();
                Log.i(TAG, "close client");
                client.close();
            }
        } catch (Exception e) {
            Log.i(TAG, "close err");
            e.printStackTrace();
        }
    }

    public CAckFrame Client_Received(byte[] req){

 //       m_TotalBuffer = GetHexToChs(StringToAscii(),null);
        String hex = bytes2Hex(req);
        String ascii = StringToAscii(hex);
        byte[] chs = GetHexToChs(ascii,null);
        int stxPos = 0;
        if (chs.length > 0) {
            int nLen = CAckFrame.FrameLength();
            CAckFrame ack = CAckFrame.CreateFromBytes(chs,stxPos);
            return ack;
        }
        return null;
    }


    /**
     * byte类型转换成String
     */
    public static String bytes2Hex(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] res = new char[src.length * 2]; // 每个byte对应两个字符
        final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >> 4 & 0x0f]; // 先存byte的高4位
            res[j++] = hexDigits[src[i] & 0x0f]; // 再存byte的低4位
        }
        return new String(res);
    }

    static byte[] m_TotalBuffer = new byte[0];

    static byte[] GetHexToChs(String hex, byte[] bytes) {
        bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++)
        {
            try
            {
                // 每两个字符是一个 byte。
                bytes[i] = (byte)(0xff & Integer.parseInt(hex.substring(i*2, i*2+2),16));;
            }
            catch(Exception e) { }
        };
        return bytes;
    }
}
