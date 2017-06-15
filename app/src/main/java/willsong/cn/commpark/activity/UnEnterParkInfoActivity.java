package willsong.cn.commpark.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.print.demo.BluetoothDeviceList;
import com.android.print.demo.BluetoothOperation;
import com.android.print.demo.IPrinterOpertion;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.CalendarTool;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;

/**
 * Created by Administrator on 2016/10/30 0030.
 * 无牌车进场
 */

public class UnEnterParkInfoActivity extends OBaseActivity {

    EditText UnEnterTime;
    ImageView QRcode;
    Button UnEnterPrint;

    protected static IPrinterOpertion myOpertion;
    private PrinterInstance mPrinter;
    private static boolean isConnected = false;
    private String print_info = "";
    private ProgressDialog dialog;
    public static final int CONNECT_DEVICE = 1;
    public static final int ENABLE_BT = 2;

    private String bt_mac;
    private String bt_name;
    String cart;

    private BluetoothAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_unenterpack);
        init();
        UnEnterPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getConnBlueTooth();
            }
        });
        String datatime = CalendarTool.getTodayStrDate(MyTools.FORMATDATE[0]);
        UnEnterTime.setText(datatime);
        String md5 = mBusinessManager.getDevCode() + datatime;
        cart = toMD5(md5);
        if (!"".equals(cart)) {
            QRcode.setImageBitmap(generateBitmap(cart, 400, 400));
        }
    }

    public void init() {
        UnEnterTime = (EditText) findViewById(R.id.UnEnterTime);
        QRcode = (ImageView) findViewById(R.id.QRcode);
        UnEnterPrint = (Button) findViewById(R.id.UnEnterPrint);
        setRightButtonText("扫描");
        setTitleRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected) {
                    myOpertion = new BluetoothOperation(UnEnterParkInfoActivity.this, mHandler);
                    myOpertion.chooseDevice();
                } else {
                    myOpertion.close();
                    myOpertion = null;
                    mPrinter = null;

                    myOpertion = new BluetoothOperation(UnEnterParkInfoActivity.this, mHandler);
                    myOpertion.chooseDevice();
                }
            }
        });

        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("连接中...");
        dialog.setMessage("请稍候...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
    }

    private void getConnBlueTooth() {
        final String mac = mBusinessManager.getIntentMac();
        if (!isConnected) {
            myOpertion = new BluetoothOperation(UnEnterParkInfoActivity.this, mHandler);
            if(null != mac && mac.length() > 0){
                dialog.show();
                new Thread(new Runnable() {
                    public void run() {
                        myOpertion.open(mac);
                    }
                }).start();
            }else {
                myOpertion.chooseDevice();
            }
        } else {
            myOpertion.close();
            myOpertion = null;
            mPrinter = null;

            myOpertion = new BluetoothOperation(UnEnterParkInfoActivity.this, mHandler);
            if(null != mac && mac.length() > 0){
                dialog.show();
                new Thread(new Runnable() {
                    public void run() {
                        myOpertion.open(mac);
                    }
                }).start();
            }else {
                myOpertion.chooseDevice();
            }
        }
    }

    //用于接受连接状态消息的 Handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    isConnected = true;
                    mPrinter = myOpertion.getPrinter();
                    printInfo();
                    //调用接口
                    Intent intent = new Intent(getApplicationContext(), EnterParkInfoActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("plate_code", cart);
                    startActivity(intent);
                    finish();
                    break;
                case PrinterConstants.Connect.FAILED:
                    isConnected = false;
                    Toast.makeText(mContext, com.android.print.demo.R.string.conn_failed,
                            Toast.LENGTH_SHORT).show();
                    break;
                case PrinterConstants.Connect.CLOSED:
                    isConnected = false;
                    break;
                case PrinterConstants.Connect.NODEVICE:
                    isConnected = false;
                    Toast.makeText(mContext, com.android.print.demo.R.string.conn_no, Toast.LENGTH_SHORT)
                            .show();
                    break;

                default:
                    break;
            }

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    };


    /**
     * 二维码
     *
     * @param content
     * @param width
     * @param height
     * @return
     */
    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void printInfo() {
        mPrinter.init();
        mPrinter.printText(UnEnterTime.getText().toString() + "\n");
        mPrinter.printImage(generateBitmap(cart, 200, 200));
        mPrinter.printText("\n\n");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myOpertion != null) {
            myOpertion.close();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode,
                                    final Intent data) {
        switch (requestCode) {
            case CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    bt_mac = data.getExtras().getString(BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
                    bt_name = data.getExtras().getString(BluetoothDeviceList.EXTRA_DEVICE_NAME);
                    dialog.show();
                    new Thread(new Runnable() {
                        public void run() {
                            myOpertion.open(bt_mac);
                            mBusinessManager.saveIntentMac(bt_mac);
                        }
                    }).start();
                }
                break;
            case ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    myOpertion.chooseDevice();
                } else {
                    Toast.makeText(this, com.android.print.demo.R.string.bt_not_enabled,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public String toMD5(String plainText) {
        String cart = "";
        try {
            //生成实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            //使用指定的字节数组更新摘要。
            md.update(plainText.getBytes());
            //通过执行诸如填充之类的最终操作完成哈希计算。
            byte b[] = md.digest();
            //生成具体的md5密码到buf数组
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            System.out.println("32位: " + buf.toString());// 32位的加密
            System.out.println("16位: " + buf.toString().substring(8, 24));// 16位的加密，其实就是32位加密后的截取
            return cart = buf.toString().substring(8, 24);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

