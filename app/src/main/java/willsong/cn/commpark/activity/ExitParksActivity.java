package willsong.cn.commpark.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hsm.barcode.DecodeResult;
import com.hsm.barcode.Decoder;
import com.hsm.barcode.DecoderConfigValues;
import com.hsm.barcode.DecoderException;
import com.hsm.barcode.SymbologyConfig;

import java.io.EOFException;
import java.io.UnsupportedEncodingException;

import w.song.orchid.activity.OBaseActivity;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.scan.service.util.Util;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;
import willsong.cn.commpark.activity.zxing.android.CaptureActivity;

/**
 * 出车管理
 * Created by Administrator on 2016/11/15 0015.
 */

public class ExitParksActivity extends OBaseActivity implements View.OnClickListener{

    private static final int REQUEST_CODE_SCAN = 0x0000;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";


    Button ExitCar;
    Button unExitCar;
    Button home_break;
    Button qr_scan;

    private String TAG = "ExitParksActivity";

    private Decoder mDecoder ;  //扫描解码
    private DecodeResult mDecodeResult ; //扫描结果
    boolean running = true ;
    long exitSytemTime = 0;
    private boolean threadRunning = false ;
    private final int timeOut = 5000 ;
    boolean scanning = false ;

    String TwoDimensionalScan = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_exitparks);
        TwoDimensionalScan = mBusinessManager.getIntentTwoDimensionalScan();
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        TwoDecodeScan();
    }
    private void TwoDecodeScan(){
        if(TwoDimensionalScan.equals("1")){
            mDecodeResult = new DecodeResult();
            Util.initSoundPool(this);
            //添加监听
            IntentFilter filter = new IntentFilter() ;
            filter.addAction("android.rfid.FUN_KEY") ;
            registerReceiver(keyReceiver, filter) ;
            running = true ;
            mDecoder = new Decoder();
            try {
                mDecoder.connectDecoderLibrary();
                settingPara();
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(300);
//                        mDecoder.startScanning();
//                        Thread.sleep(100);
//                        mDecoder.stopScanning();
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();
//			mDecoder.stopScanning();
            } catch (DecoderException e) {
                e.printStackTrace();
            }
        }else{

        }
    }

    private void init() {
        ExitCar = (Button) findViewById(R.id.ExitCar);
        unExitCar = (Button) findViewById(R.id.unExitCar);
        home_break = (Button) findViewById(R.id.home_break);
        qr_scan = (Button) findViewById(R.id.scan);
//        if(("3").equals(mBusinessManager.getIntentSystemModel())){
//            ExitCar.setVisibility(View.GONE);
//        }

        setRightButtonVisible(false);
        setLeftButtonVisible(false);
        setTitleText("出场管理");

        ExitCar.setOnClickListener(this);
        unExitCar.setOnClickListener(this);
        home_break.setOnClickListener(this);
        qr_scan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ExitCar:
                Intent video_intent = new Intent();
                if(mBusinessManager.getPlateRecognize().toString().equals("1")){//是否需要进入车牌识别界面：1：进入
                    video_intent.putExtra("camera", true);//判断是拍照识别还是自动识别 true:自动识别 false:拍照识别
                    video_intent.putExtra(MemoryResultActivity.IMPORT_FIELD.TASK, MemoryResultActivity.TASK_VALUE.EXIT_PARK);
                    video_intent.setClass(getApplicationContext(), MemoryCameraActivity.class);
                    startActivity(video_intent);
                }else{
                    video_intent.putExtra("camera", true);
                    video_intent.putExtra(MemoryResultActivity.IMPORT_FIELD.TASK, MemoryResultActivity.TASK_VALUE.EXIT_PARK);
                    video_intent.setClass(getApplicationContext(), MemoryResultActivity.class);
                    startActivity(video_intent);
                }

                break;
            case R.id.unExitCar:
                Intent it = new Intent(ExitParksActivity.this,
                        CaptureActivity.class);
                it.putExtra("cflag","0");
                startActivityForResult(it, REQUEST_CODE_SCAN);
                break;
            case R.id.home_break:
                finish();
                break;
            case R.id.scan:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
//                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                Log.i("ccm","结果" + content);
                Intent intent = new Intent(getApplicationContext(),ExitParkActivity.class);
                if(content.length()>=14){
                    intent.putExtra("type",1);
                }
                intent.putExtra("plate_code",content);
                if(SharedPreferencesConfig.getString(mContext,"loginFlag").equals("1")){
                    intent.putExtra("color","");
                }
                startActivity(intent);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //减少频繁触发
        if (System.currentTimeMillis() - exitSytemTime < 100) {
//			exitSytemTime = System.currentTimeMillis();
            return true;
        }
        exitSytemTime = System.currentTimeMillis();
        if(keyCode == 131 || keyCode == 132 || keyCode == 133 || keyCode == 134|| keyCode == 135){
            if(!threadRunning){
                if(TwoDimensionalScan.equals("1")){
                    scan(timeOut);
                }
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (System.currentTimeMillis() - exitSytemTime > 2000) {
//                Toast.makeText(getApplicationContext(), "xxx",
//                        Toast.LENGTH_SHORT).show();
//                exitSytemTime = System.currentTimeMillis();
//                return true;
//            } else {
//                finish();
//            }
            return  false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置参数
     */
    void settingPara(){
        try {
            //设置EAN13校验位
            SymbologyConfig config = new SymbologyConfig(DecoderConfigValues.SymbologyID.SYM_EAN13);
            config.Flags = 5 ;
            config.Mask = 1 ;
            mDecoder.setSymbologyConfig(config);
            mDecoder.disableSymbology(DecoderConfigValues.SymbologyID.SYM_ALL);
//			mDecoder.enableSymbology(SymbologyID.SYM_ALL);

            mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_QR);
            mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_PDF417);
            mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_EAN13);
            mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_CODE128) ;
////
            mDecoder.enableSymbology(DecoderConfigValues.SymbologyID.SYM_DATAMATRIX);


//			mDecoder.setOCRMode(0);
//			mDecoder.setOCRTemplates(0);
//			mDecoder.setOCRUserTemplate("13777777770".getBytes());
//			mDecoder.setDecodeWindowMode(0);
//			DecodeOptions decOpt = new DecodeOptions();
//			decOpt.DecAttemptLimit = -1; // ignore
//			decOpt.VideoReverse = -1; // ignore
//			decOpt.MultiReadCount = 3;
//			mDecoder.setDecodeOptions(decOpt);
//			mDecoder.setLightsMode(3);

//			mDecoder.setLightsMode(LightsMode.ILLUM_AIM_OFF) ;
            //关闭激光
            //mDecoder.setLightsMode(LightsMode.ILLUM_ONLY) ;
        } catch (DecoderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //监听按键消息
    private BroadcastReceiver keyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            int keyCode = intent.getIntExtra("keyCode", 0) ;
            if(keyCode == 0){//兼容H941
                keyCode = intent.getIntExtra("keycode", 0) ;
            }
            boolean keyDown = intent.getBooleanExtra("keydown", false) ;
            Log.e("", "KEYcODE = " + keyCode + ", Down = " + keyDown);
            if(keyDown){
                //减少频繁触发
                if (System.currentTimeMillis() - exitSytemTime < 100) {
//					exitSytemTime = System.currentTimeMillis();
                    return ;
                }
                exitSytemTime = System.currentTimeMillis();
                if(!threadRunning){
                    scan(timeOut);
                }
            }
        }
    };

    /**
     * 扫描
     * @param timeout
     * @return
     */
    private void scan(final int timeout) {

        if (!scanning) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    scanning = true;
                    try {
                        Thread.sleep(50) ;
                        mDecoder.waitForDecodeTwo(timeout, mDecodeResult);
                        //保存图像
//						GetLastImage()  ;
                        Thread.sleep(100) ;
                    } catch (DecoderException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (mDecodeResult.length > 0) {
//						scanning = false;

                        try {
                            byte[] tt = mDecoder.getBarcodeByteData();
                            displayScanResult(new String(tt,"GBK"));
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (DecoderException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

//						displayScanResult(mDecodeResult.barcodeData);
                        // return mDecodeResult.barcodeData;
                        // displayUI(mDecodeResult.barcodeData);

                    }
                    scanning = false;

                }
            }).start();
        }
    }
    //
    private void displayScanResult(final String barCode){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Util.play(1, 0);
                Intent intent = new Intent(getApplicationContext(),ExitParkActivity.class);
                intent.putExtra("type",1);
//                String plate_content = "";
//                if(barCode.contains("-")&&barCode.contains(":")){
//                    plate_content = barCode;
//                }else{
//                    if(barCode.length()==14){
//                        plate_content = getDateChange(barCode.substring(barCode.length()-14, barCode.length()));
//                    }else if(barCode.length()>14){
//                        plate_content = barCode.substring(0, barCode.length()-14)+getDateChange(barCode.substring(barCode.length()-14, barCode.length()));
//                    }else{
//                        plate_content = barCode;
//                    }
//                }
                intent.putExtra("plate_code",barCode);
                if(SharedPreferencesConfig.getString(mContext,"loginFlag").equals("1")){
                    intent.putExtra("color","");
                }
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        running = false ;
		threadRunning = false ;

		if(mDecoder != null){
			try {
				mDecoder.disconnectDecoderLibrary();
			} catch (DecoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        try{
            if(null!=keyReceiver){
                unregisterReceiver(keyReceiver) ;
            }
        }catch (Exception e){

        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try{
            if(null!=keyReceiver){
                unregisterReceiver(keyReceiver) ;
            }
        }catch (Exception e){

        }
        super.onDestroy();
    }
}
