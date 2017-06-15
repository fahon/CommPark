package willsong.cn.commpark.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.printer.sdk.Barcode;
import com.printer.sdk.PrinterConstants;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.DataForSendToPrinterPos58;
import net.posprinter.utils.DataForSendToPrinterPos80;
import net.posprinter.utils.DataForSendToPrinterTSC;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.CalendarTool;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.Bike.EnterBikeActivity;
import willsong.cn.commpark.activity.Print.BluetoothUtil;
import willsong.cn.commpark.activity.Print.GlobalContants;
import willsong.cn.commpark.activity.Print.PrefUtils;
import willsong.cn.commpark.activity.util.Util;
import willsong.cn.commpark.activity.widget.MediaPlayerTool;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;
import willsong.cn.commpark.activity.widget.SpeechUtils;
import willsong.cn.commpark.activity.zxing.android.CaptureActivity;

import static com.printer.sdk.PrinterInstance.mPrinter;

/**
 * 进车管理
 * Created by Administrator on 2016/11/15 0015.
 */

public class EntersActivity extends OBaseActivity implements View.OnClickListener {
    Button car;
    Button uncar;
    Button home_break;

    String cart;
    String datatime;
    public BluetoothUtil bu;
    private int codeSize = 7;
    private static final int REQUEST_CODE_RETURN = 20;
    private static final String DECODED_CONTENT_KEY = "codedContent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_enters);

        init();
        bu = new BluetoothUtil(this);
        datatime = CalendarTool.getTodayStrDate(MyTools.FORMATDATE[0]);
        if (SharedPreferencesConfig.getString(this, "loginFlag").equals("1")) {
            cart = SharedPreferencesConfig.replaceAll(datatime);
            codeSize = 8;
        } else {
            cart = mBusinessManager.getDevCode() + datatime;
            codeSize = 8;
        }

        //    cart = toMD5(md5);

        setLeftButtonVisible2(false);
        setRightButtonVisible(false);
        setTitleText("进场管理");
    }

    private void init() {
        car = (Button) findViewById(R.id.car);
        uncar = (Button) findViewById(R.id.uncar);
        home_break = (Button) findViewById(R.id.home_break);
        if(("3").equals(mBusinessManager.getIntentSystemModel())){
            car.setText("扫码");
            uncar.setText("打码");
        }

        car.setOnClickListener(this);
        uncar.setOnClickListener(this);
        home_break.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.car:
                if(("3").equals(mBusinessManager.getIntentSystemModel())){
                    Intent it = new Intent(EntersActivity.this,CaptureActivity.class);
                    it.putExtra("cflag","4");
                    startActivityForResult(it, REQUEST_CODE_RETURN);
                }else{
                    Intent video_intent = new Intent();
                    if (mBusinessManager.getPlateRecognize().toString().equals("1")) {
                        video_intent.putExtra("camera", true);
                        video_intent.putExtra(MemoryResultActivity.IMPORT_FIELD.TASK, MemoryResultActivity.TASK_VALUE.ENTER_PARK);
                        video_intent.setClass(getApplicationContext(), MemoryCameraActivity.class);
                        startActivity(video_intent);
                    } else {
                        video_intent.putExtra("camera", true);
                        video_intent.putExtra(MemoryResultActivity.IMPORT_FIELD.TASK, MemoryResultActivity.TASK_VALUE.ENTER_PARK);
                        video_intent.setClass(getApplicationContext(), MemoryResultActivity.class);
                        startActivity(video_intent);
                    }
                }
                break;
            case R.id.uncar:
                if (PrefUtils.getString(mContext, "DEVICENAME", "").startsWith("printer001")) {//新设备
                    if (HomePageActivity.isConnect == true) {
                        if (mPrinter == null) {
                            bu.open();
                        }
                        if (SharedPreferencesConfig.getString(this, "loginFlag").equals("1")) {
                            String FParkingName = "";
                            if (!mBusinessManager.getIntentEndCo().equals("")) {
                                if (mBusinessManager.getIntentEndCo().toString().length() > 4) {
                                    FParkingName = "" + mBusinessManager.getIntentEndCo().substring(0, 4);
                                } else {
                                    FParkingName = "" + mBusinessManager.getIntentEndCo();
                                }
                            } else {
                                FParkingName = "上海软杰";
                            }
                            mBusinessManager.netWebRequestEnter(true, FParkingName, "临时车", "" + cart, "无牌车",
                                    "无牌车", SharedPreferencesConfig.getString(mContext, "telephone"),
                                    SharedPreferencesConfig.getString(mContext, "userName"), "", datatime.toString().trim(), deviceId());
                        } else {
                            mBusinessManager.netGetIsPresence(true, "", datatime, cart,false);
                        }
                    } else {
                        if (mPrinter == null) {
                            bu.open();
                        }
//                        Toast.makeText(this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                        HomePageActivity.sendble(EntersActivity.this,true);
                    }
                } else {
                    if (mPrinter == null) {
//                    Toast.makeText(this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                        bu.open();
                    } else {
                        if (bu.isConnected == true) {
                            if (SharedPreferencesConfig.getString(this, "loginFlag").equals("1")) {
                                String FParkingName = "";
                                if (!mBusinessManager.getIntentEndCo().equals("")) {
                                    if (mBusinessManager.getIntentEndCo().toString().length() > 4) {
                                        FParkingName = "" + mBusinessManager.getIntentEndCo().substring(0, 4);
                                    } else {
                                        FParkingName = "" + mBusinessManager.getIntentEndCo();
                                    }
                                } else {
                                    FParkingName = "上海软杰";
                                }
                                mBusinessManager.netWebRequestEnter(true, FParkingName, "临时车", "" + cart, "无牌车",
                                        "无牌车", SharedPreferencesConfig.getString(mContext, "telephone"),
                                        SharedPreferencesConfig.getString(mContext, "userName"), "", datatime.toString().trim(), deviceId());
                            } else {
                                mBusinessManager.netGetIsPresence(true, "", datatime, cart,false);
                            }
                        } else {
                            Toast.makeText(this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.home_break:
                finish();
                break;
        }
    }

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

    @Override
    protected void onStop() {
        super.onStop();
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


    @Override
    public void refreshView(String type, Map<String, Object> map) {
        super.refreshView(type, map);
        if (BusinessManager.NETINSERTCARENTERREC.equals(type)) {
            printCntent();
            //开始合成（离线）
//            HomePageActivity.speechUtils.speakText("车辆进场成功");
            MediaPlayerTool.getInstance(mContext).startPlay("欢迎光临");
            MyTools.showToastShort(true, "提交成功", mContext);
            if ("1".equals(mBusinessManager.getLuZheng())) {
                Intent intent = new Intent(Util.MYCAST);
                intent.putExtra("index", 2);
                intent.putExtra("seqNo", (String) map.get("SeqNo"));
                Double actTime = (Double) map.get("ActType");
                int act = actTime.intValue();
                intent.putExtra("actType", act);
                intent.putExtra("actTime", datatime);
                intent.putExtra("carNumber", "无牌车");
                Double totBerthNum = (Double) map.get("TotRemainNum");
                int tot = totBerthNum.intValue();
                Double monthlyBerthNum = (Double) map.get("MonthlyRemainNum");
                int month = monthlyBerthNum.intValue();
                Double guesBerthNum = (Double) map.get("GuestRemainNum");
                int gues = guesBerthNum.intValue();
                intent.putExtra("totRemainNum", tot);
                intent.putExtra("monthlyRemainNum", month);
                intent.putExtra("guestRemainNum", gues);
                sendBroadcast(intent);
            }
            if ("1".equals(mBusinessManager.getShiZhong())) {
                Intent intent = new Intent(Util.SHIZHONGCART);
                intent.putExtra("index", 2);
                Double actTime = (Double) map.get("ActType");
                int act = actTime.intValue();
                intent.putExtra("actType", act);
                intent.putExtra("actTime", datatime);
                intent.putExtra("carNumber", "无牌车");
                Double totBerthNum = (Double) map.get("TotRemainNum");
                int tot = totBerthNum.intValue();
                Double monthlyBerthNum = (Double) map.get("MonthlyRemainNum");
                int month = monthlyBerthNum.intValue();
                Double guesBerthNum = (Double) map.get("GuestRemainNum");
                int gues = guesBerthNum.intValue();
                intent.putExtra("totRemainNum", tot);
                intent.putExtra("monthlyRemainNum", month);
                intent.putExtra("guestRemainNum", gues);
                sendBroadcast(intent);
            }
            finish();
        } else if (type.equals(BusinessManager.WEBNETINSERT)) {
            printCntent();
            //开始合成（离线）
//            HomePageActivity.speechUtils.speakText("车辆进场成功");
            MediaPlayerTool.getInstance(mContext).startPlay("欢迎光临");
            MyTools.showToastShort(true, "提交成功", mContext);
            finish();
        } else {
            MyTools.showToastLong(true, "车辆进场失败，请重试", mContext);
        }
    }

    public void printCntent() {
        if (PrefUtils.getString(mContext, "DEVICENAME", "").startsWith("printer001")) {//新设备
            HomePageActivity.satrtPrint(EntersActivity.this,""+datatime,"无牌车",cart,mBusinessManager.getIntentContent(),
                    mBusinessManager.getIntentEndCo(),mBusinessManager.getIntentEndTel(),codeSize,mBusinessManager.getEnterTitle());
        } else {//老设备
            mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
            mPrinter.printText("   " + mBusinessManager.getEnterTitle() +"\n");//标题
            mPrinter.printText("入场时间:" + datatime + "\n");
            mPrinter.printText("车牌号码:" + "无牌车" + "\n");
            Barcode barcode2 = new Barcode(com.printer.sdk.PrinterConstants.BarcodeType.QRCODE, 3, 3, 6, cart);
            int sum = mPrinter.printBarCode(barcode2);
//                    mPrinter.printText("上海软杰智能设备有限公司研制\n");
            if (!mBusinessManager.getIntentEndCo().equals("")) {
                mPrinter.printText(mBusinessManager.getIntentEndCo() + "\n");
            } else {
                mPrinter.printText("上海软杰智能设备有限公司研制\n");
            }
            if (!mBusinessManager.getIntentEndTel().equals("")) {
                mPrinter.printText("   TEL:" + mBusinessManager.getIntentEndTel());
            } else {
                mPrinter.printText("   TEL:021-51099719");
            }
            mPrinter.printText("\n\n\n");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RETURN && resultCode == RESULT_OK) {//二维码版：进场管理
            if (data != null) {
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
//                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                Intent it = new Intent(EntersActivity.this, EnterParkInfoActivity.class);
                it.putExtra(EnterParkInfoActivity.IMPORT_FIELD.PLATE_CODE,""+content);
                if(SharedPreferencesConfig.getString(mContext,"loginFlag").equals("1")){
                    it.putExtra("color","");
                }
                startActivity(it);
            }
        }
    }
}
