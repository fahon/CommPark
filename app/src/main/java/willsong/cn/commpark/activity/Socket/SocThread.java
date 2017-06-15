package willsong.cn.commpark.activity.Socket;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
import java.util.Arrays;

public class SocThread extends Thread {
    private String ip;
    private int port;
    private String TAG = "socket thread";
    private int timeout = 10000;

    public Socket client = null;
    PrintWriter out;
    OutputStream outputStream;
    static BufferedReader in;
    public boolean isRun = true;
    static Handler  inHandler;
    static Handler  outHandler;
    Context ctx;
    private String TAG1 = "===Send===";
    SharedPreferences sp;

    public SocThread(Handler handlerin, Handler handlerout, Context context) {
        inHandler = handlerin;
        outHandler = handlerout;
        ctx = context;
        Log.i(TAG, "创建线程socket");
        sp = ctx.getSharedPreferences("SP", ctx.MODE_PRIVATE);
        ip = sp.getString("luzheng_ip", "");
        port = Integer.parseInt(sp.getString("luzheng_port","0"));
    }

    /**
     * 连接socket服务器
     */
    public void conn() {

        try {
            if(!"".equals(ip) && 0 != port) {
                Log.i(TAG,ip + "====" + port);
                Log.i(TAG, "连接中……");
                client = new Socket(ip, port);
                client.setSoTimeout(timeout);// 设置阻塞时间
                Log.i(TAG, "连接成功");
                in = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        client.getOutputStream())), true);
                outputStream = client.getOutputStream();
                Log.i(TAG, "输入输出流获取成功");
            }else {
                Toast.makeText(ctx,"请设置Ip或端口号",Toast.LENGTH_SHORT).show();
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

    public void initdate() {
        sp = ctx.getSharedPreferences("SP", ctx.MODE_PRIVATE);
        ip = sp.getString("luzheng_ip", ip);
        port = Integer.parseInt(sp.getString("luzheng_port", String.valueOf(port)));
        Log.i(TAG, "获取到ip端口:" + ip + ";" + port);
    }

    /**
     * 实时接受数据
     */
    @Override
    public void run() {
        Log.i(TAG, "线程socket开始运行");
        conn();
        Log.i(TAG, "1.run开始");
        byte[] lin = new byte[1024];
        InputStream inputStream;
        DataInputStream input = null;
        int length = -1;
        String mmsg;
        try {
            inputStream = client.getInputStream();
            input = new DataInputStream(inputStream);
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
                            byte[]  new_bts= Arrays.copyOfRange(lin, 5, lin.length - 1);
                            String Msg = new String(lin,0,length,"UTF-8");
                            String subMsg = Msg.substring(5);
                            Message msg = inHandler.obtainMessage();
                            msg.obj = subMsg;
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
}