package willsong.cn.commpark.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.print.demo.BluetoothDeviceList;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.BarrierGate.BluetoothUtilGate;
import willsong.cn.commpark.activity.BarrierGate.SampleGattAttributes;
import willsong.cn.commpark.activity.Print.BluetoothUtil;
import willsong.cn.commpark.activity.Print.GlobalContants;
import willsong.cn.commpark.activity.Print.PrefUtils;
import willsong.cn.commpark.activity.Service.LuzhengService;
import willsong.cn.commpark.activity.Service.ShiZhongService;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;

import static willsong.cn.commpark.activity.Print.BluetoothUtil.devicesAddress;
import static willsong.cn.commpark.activity.Print.BluetoothUtil.devicesName;
import static willsong.cn.commpark.activity.Print.BluetoothUtil.myPrinter;

/**
 * Created by Administrator on 2016/10/23 0023.
 */

public class SesstingActivity extends OBaseActivity implements View.OnClickListener {

    private final static int SCANNIN_GREQUEST_CODE = 2;

    LinearLayout ip_port;
    LinearLayout platfromtest;
    LinearLayout sign;
    LinearLayout print;
    LinearLayout bluetooth;
    BluetoothUtil mBluetoothUtil;//打印机蓝牙设备
    BluetoothUtilGate mBluetoothUtilGate;//阀门蓝牙设备
    LinearLayout printtitle;
    LinearLayout localAreaNetwork, plateRecognize, electronic_pay_Id,storeNo,appUrl,allowEnterAgain,savePicNum,savePicDay;
    LinearLayout sessing, corpId, code_modify, localArea, commonlicence, statistic, ll_system_model,camera_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_sessting);
        init();
    }

    private void init() {
        setRightButtonVisible(false);
        setTitleText("设置");
        ip_port = (LinearLayout) findViewById(R.id.ip_port);
        platfromtest = (LinearLayout) findViewById(R.id.platfromtest);
        sign = (LinearLayout) findViewById(R.id.sign);
        print = (LinearLayout) findViewById(R.id.print);
        bluetooth = (LinearLayout) findViewById(R.id.bluetooth);
        printtitle = (LinearLayout) findViewById(R.id.printtitle);
        sessing = (LinearLayout) findViewById(R.id.sessing);
        corpId = (LinearLayout) findViewById(R.id.corpId);
        code_modify = (LinearLayout) findViewById(R.id.code_modify);
        localAreaNetwork = (LinearLayout) findViewById(R.id.localAreaNetwork);
        localArea = (LinearLayout) findViewById(R.id.localArea);
        plateRecognize = (LinearLayout) findViewById(R.id.plateRecognize);
        plateRecognize.setOnClickListener(this);
        commonlicence = (LinearLayout) findViewById(R.id.commonlicence);
        commonlicence.setOnClickListener(this);
        statistic = (LinearLayout) findViewById(R.id.statistic);
        statistic.setOnClickListener(this);
        ll_system_model = (LinearLayout) findViewById(R.id.ll_system_model);
        ll_system_model.setOnClickListener(this);
        camera_scan = (LinearLayout) findViewById(R.id.camera_scan);
        camera_scan.setOnClickListener(this);
        electronic_pay_Id = (LinearLayout) findViewById(R.id.electronic_pay_Id);
        electronic_pay_Id.setOnClickListener(this);
        storeNo = (LinearLayout) findViewById(R.id.storeNo);
        storeNo.setOnClickListener(this);
        appUrl = (LinearLayout) findViewById(R.id.appUrl);
        appUrl.setOnClickListener(this);
        allowEnterAgain = (LinearLayout) findViewById(R.id.allowEnterAgain);
        allowEnterAgain.setOnClickListener(this);
        savePicNum = (LinearLayout) findViewById(R.id.savePicNum);
        savePicNum.setOnClickListener(this);
        savePicDay = (LinearLayout) findViewById(R.id.savePicDay);
        savePicDay.setOnClickListener(this);

        ip_port.setOnClickListener(this);
        platfromtest.setOnClickListener(this);
        sign.setOnClickListener(this);
        print.setOnClickListener(this);
        bluetooth.setOnClickListener(this);
        printtitle.setOnClickListener(this);
        sessing.setOnClickListener(this);
        corpId.setOnClickListener(this);
        code_modify.setOnClickListener(this);
        localAreaNetwork.setOnClickListener(this);
        localArea.setOnClickListener(this);

        mBluetoothUtil = new BluetoothUtil(this);
//        mBluetoothUtilGate = new BluetoothUtilGate(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.ip_port:
                final Intent intent = new Intent(getApplicationContext(), PlatFormTestAcitvity.class);
                startActivity(intent);
                break;
            case R.id.platfromtest:
                AlertDialog.Builder builder = new AlertDialog.Builder(SesstingActivity.this);
                builder.setTitle("选择检测平台");
                final View view = LayoutInflater.from(SesstingActivity.this).inflate(R.layout.activity_popupwindow, null);
                final CheckBox luzheng = (CheckBox) view.findViewById(R.id.luzheng);
                final CheckBox shizhong = (CheckBox) view.findViewById(R.id.shizhong);
                String lz = mBusinessManager.getLuZheng();
                String sz = mBusinessManager.getShiZhong();
                if ("1".equals(lz)) {
                    luzheng.setChecked(true);
                }
                if ("1".equals(sz)) {
                    shizhong.setChecked(true);
                }
                builder.setView(view);
                builder.setCancelable(false);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (luzheng.isChecked()) {
                            mBusinessManager.saveLuzheng("1");
                        } else {
                            Intent intent = new Intent(getApplicationContext(), LuzhengService.class);
                            stopService(intent);
                            mBusinessManager.saveLuzheng("0");
                        }

                        if (shizhong.isChecked()) {
                            mBusinessManager.saveShizhong("1");
                        } else {
                            Intent it = new Intent(getApplicationContext(), ShiZhongService.class);
                            stopService(it);
                            mBusinessManager.saveShizhong("0");
                        }
                    }
                });
                builder.show();
                break;
            case R.id.sign:
                Intent it = new Intent();
                it.setClass(getApplicationContext(), SignActivity.class);
                startActivity(it);
                break;
            case R.id.print:
                getPringBitmap();
                break;
            case R.id.bluetooth:
                mBluetoothUtil.bluetoothList();
                break;
            case R.id.printtitle:
                Intent intent2 = new Intent();
                intent2.setClass(getApplicationContext(), PrintContentActivity.class);
                startActivity(intent2);
//                String title = mBusinessManager.getIntentContent();
//                final EditText et = new EditText(this);
//                if(null != title) {
//                    et.setText(title);
//                    et.setSelection(title.length());
//                }
//                AlertDialog.Builder bd = new AlertDialog.Builder(this);
//                bd.setTitle("请设置头纸票打印头部");
//                bd.setView(et);
//                bd.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String content = et.getText().toString().trim();
//                        if(content != null){
//                            mBusinessManager.saveIntentContent(content);
//                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(et.getWindowToken(),0);
//                        }
//                    }
//                });
//                bd.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(et.getWindowToken(),0);
//                    }
//                });
//                bd.show();
                break;
            case R.id.sessing:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
            case R.id.corpId:
                String Cpporid = mBusinessManager.getIntentcorpId();
                final EditText exit = new EditText(this);
                if (null != Cpporid) {
                    exit.setText(Cpporid);
                    exit.setSelection(Cpporid.length());
                }
                AlertDialog.Builder burd = new AlertDialog.Builder(this);
                burd.setTitle("请设置运营商代码");
                burd.setView(exit);
                burd.setCancelable(false);
                burd.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = exit.getText().toString().trim();
                        if (content != null) {
                            mBusinessManager.saveIntentcorpId(content);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(exit.getWindowToken(), 0);
                        }
                    }
                });
                burd.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(exit.getWindowToken(), 0);
                    }
                });
                burd.show();
                break;
            case R.id.code_modify:
                Intent its = new Intent();
                its.setClass(getApplicationContext(), SetPwdActivity.class);
                startActivity(its);
                break;
            case R.id.plateRecognize:
                getPlateRecognize();
                break;
            case R.id.localAreaNetwork:
                getLANBitmap();
                break;
            case R.id.localArea:
                String localAreaid = mBusinessManager.getLocalAreaId();
                final EditText exits = new EditText(this);
                if (null != localAreaid) {
                    exits.setText(localAreaid);
                    exits.setSelection(localAreaid.length());
                }
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setTitle("请设置局域网连接地址");
                build.setView(exits);
                build.setCancelable(false);
                build.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = exits.getText().toString().trim();
                        if (content != null) {
                            mBusinessManager.saveLocalAreaId(content);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
                        }
                    }
                });
                build.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
                    }
                });
                build.show();
                break;
            case R.id.commonlicence:
                String commonLicence = mBusinessManager.getIntentCommonLicence();
                final EditText mexit = new EditText(this);
                if (null != commonLicence) {
                    mexit.setText(commonLicence);
                    mexit.setSelection(commonLicence.length());
                }
                AlertDialog.Builder mburd = new AlertDialog.Builder(this);
                mburd.setTitle("请设置4个常用车牌号首字");
                mburd.setView(mexit);
                mburd.setCancelable(false);
                mburd.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = mexit.getText().toString().trim();
                        if (("").equals(content)) {
                            MyTools.showToastShort(true, "常用车牌号不能为空", SesstingActivity.this);
                            return;
                        }
                        if (content.length() != 4) {
                            MyTools.showToastShort(true, "常用车牌号首字设置必须为4个", SesstingActivity.this);
                            return;
                        }
                        if (checkNameChese(content) == false) {
                            MyTools.showToastShort(true, "车牌号首字必须为中文汉字", SesstingActivity.this);
                            return;
                        } else {
                            MyTools.showToastShort(true, "设置成功", SesstingActivity.this);
                            mBusinessManager.saveIntentCommonLicence(content);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(mexit.getWindowToken(), 0);
                        }
                    }
                });
                mburd.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mexit.getWindowToken(), 0);
                    }
                });
                mburd.show();
                break;
            case R.id.statistic:
                getStatisticSet();
                break;
            case R.id.ll_system_model:
                getModelSet();
                break;
            case R.id.camera_scan:
                setCameraIP();
                break;
            case R.id.electronic_pay_Id:
                setElectPayIP();
                break;
            case R.id.storeNo:
                setStoreNo();
                break;
            case R.id.appUrl:
                setAppUrl();
                break;
            case R.id.allowEnterAgain:
                getAllowEnterAgain();
                break;
            case R.id.savePicNum:
                getSavePicNum();
                break;
            case R.id.savePicDay:
                getSavePicDay();
                break;

        }
    }

    public void getPringBitmap() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SesstingActivity.this);
        builder.setTitle("打印和二维头设置");
        final View view = LayoutInflater.from(SesstingActivity.this).inflate(R.layout.activity_printbitmap, null);
        final CheckBox BlueToothOpen = (CheckBox) view.findViewById(R.id.BlueToothOpen);
        final CheckBox EnterPrint = (CheckBox) view.findViewById(R.id.EnterPrint);
        final CheckBox ExitParkPrint = (CheckBox) view.findViewById(R.id.ExitParkPrint);
        final CheckBox CardBitmap = (CheckBox) view.findViewById(R.id.CardBitmap);
        final CheckBox CheckScam = (CheckBox) view.findViewById(R.id.checkScam);
        String bluethOpen = mBusinessManager.getBlueToothOpen();
        String enter = mBusinessManager.getEnterPrint();
        String exit = mBusinessManager.getExitPrint();
        String map = mBusinessManager.getCardBitmap();
        String scam = mBusinessManager.getIntentTwoDimensionalScan();
        if ("1".equals(bluethOpen)) {
            BlueToothOpen.setChecked(true);
        }
        if ("1".equals(enter)) {
            EnterPrint.setChecked(true);
        }
        if ("1".equals(exit)) {
            ExitParkPrint.setChecked(true);
        }
        if ("1".equals(map)) {
            CardBitmap.setChecked(true);
        }
        if ("1".equals(scam)) {
            CheckScam.setChecked(true);
        }
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (BlueToothOpen.isChecked()) {
                    mBusinessManager.saveBlueToothOpen("1");
                } else {
                    mBusinessManager.saveBlueToothOpen("0");
                }

                if (EnterPrint.isChecked()) {
                    mBusinessManager.saveEnterPrint("1");
                } else {
                    mBusinessManager.saveEnterPrint("0");
                }

                if (ExitParkPrint.isChecked()) {
                    mBusinessManager.saveExitPrint("1");
                } else {
                    mBusinessManager.saveExitPrint("0");
                }

                if (CardBitmap.isChecked()) {
                    mBusinessManager.saveCardBitmap("1");
                } else {
                    mBusinessManager.saveCardBitmap("0");
                }

                if (CheckScam.isChecked()) {
                    mBusinessManager.saveIntentTwoDimensionalScan("1");
                } else {
                    mBusinessManager.saveIntentTwoDimensionalScan("0");
                }
            }
        });
        builder.show();
    }
    //局域网连接设置
    public void getLANBitmap() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SesstingActivity.this);
        builder.setTitle("局域网连接设置");
        final View view = LayoutInflater.from(SesstingActivity.this).inflate(R.layout.activity_lanbitmap, null);
        final CheckBox WebService = (CheckBox) view.findViewById(R.id.WebService);
        final CheckBox RecognizeAgain = (CheckBox) view.findViewById(R.id.RecognizeAgain);
        String webService = mBusinessManager.getWebService();
        String recognizeAgain = mBusinessManager.getRecognizeAgain();
        if ("1".equals(webService)) {
            WebService.setChecked(true);
        }
        if ("0".equals(recognizeAgain)) {
            RecognizeAgain.setChecked(true);
        }
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (WebService.isChecked()) {
                    mBusinessManager.saveWebService("1");
                } else {
                    mBusinessManager.saveWebService("0");
                }

                if (RecognizeAgain.isChecked()) {
                    mBusinessManager.saveRecognizeAgain("0");
                } else {
                    mBusinessManager.saveRecognizeAgain("1");
                }
            }
        });
        builder.show();
    }

    //进出场车牌识别设置
    public void getPlateRecognize() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SesstingActivity.this);
        builder.setTitle("进出场车牌识别设置");
        final View view = LayoutInflater.from(SesstingActivity.this).inflate(R.layout.activity_platerecognize, null);
        final CheckBox PlateRecognize = (CheckBox) view.findViewById(R.id.plateRecognize);
        String webService = mBusinessManager.getPlateRecognize();
        if ("1".equals(webService)) {
            PlateRecognize.setChecked(true);
        }

        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (PlateRecognize.isChecked()) {
                    mBusinessManager.savePlateRecognize("1");
                } else {
                    mBusinessManager.savePlateRecognize("0");
                }
            }
        });
        builder.show();
    }

    //巡查，统计及交班打印,版本自动提示更新设置
    public void getStatisticSet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SesstingActivity.this);
        builder.setTitle("巡查、统计及交班设置");
        final View view = LayoutInflater.from(SesstingActivity.this).inflate(R.layout.activity_statisticset, null);
        final CheckBox CheckCar = (CheckBox) view.findViewById(R.id.checkCar);
        final CheckBox StatisticShow = (CheckBox) view.findViewById(R.id.statisticShow);
        final CheckBox Exchange = (CheckBox) view.findViewById(R.id.exchange);//交班
        final CheckBox LinkCamera = (CheckBox) view.findViewById(R.id.linkCamera);//连接立式摄像头
        final CheckBox VerUpdate = (CheckBox) view.findViewById(R.id.verUpdate);//版本自动提示更新

        String checkCar = mBusinessManager.getIntentCheckCar();
        String statisticShow = mBusinessManager.getIntentStatisticShow();
        String exchange = mBusinessManager.getntentExchangePrintDet();
        String linkCamera = SharedPreferencesConfig.getString(SesstingActivity.this,"isLinkCamera");
        String verUpdate = mBusinessManager.getVersionUpdate();
        if ("1".equals(checkCar)) {
            CheckCar.setChecked(true);
        }
        if ("1".equals(statisticShow)) {
            StatisticShow.setChecked(true);
        }
        if ("1".equals(exchange)) {
            Exchange.setChecked(true);
        }
        if ("1".equals(linkCamera)) {
            LinkCamera.setChecked(true);
        }
        if ("1".equals(verUpdate)) {
            VerUpdate.setChecked(true);
        }
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CheckCar.isChecked()) {
                    mBusinessManager.saveIntentCheckCar("1");
                } else {
                    mBusinessManager.saveIntentCheckCar("0");
                }
                if (StatisticShow.isChecked()) {
                    mBusinessManager.saveIntentStatisticShow("1");
                } else {
                    mBusinessManager.saveIntentStatisticShow("0");
                }
                if (Exchange.isChecked()) {
                    mBusinessManager.saveIntentExchangePrintDet("1");
                } else {
                    mBusinessManager.saveIntentExchangePrintDet("0");
                }
                if (LinkCamera.isChecked()) {
                    SharedPreferencesConfig.saveStringConfig(SesstingActivity.this,"isLinkCamera","1");
                } else {
                    SharedPreferencesConfig.saveStringConfig(SesstingActivity.this,"isLinkCamera","0");
                }
                if (VerUpdate.isChecked()) {
                    mBusinessManager.saveVersionUpdate("1");
                } else {
                    mBusinessManager.saveVersionUpdate("0");
                }
            }
        });
        builder.show();
    }

    //系统模式设置
    public void getModelSet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SesstingActivity.this);
        final View view = LayoutInflater.from(SesstingActivity.this).inflate(R.layout.activity_modelset, null);
        //根据ID找到RadioGroup实例
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        final RadioButton radioFormal = (RadioButton) view.findViewById(R.id.radioFormal);
        final RadioButton radioSimple = (RadioButton) view.findViewById(R.id.radioSimple);
        final RadioButton radioBike = (RadioButton) view.findViewById(R.id.radioBike);
        final RadioButton radioQRCode = (RadioButton) view.findViewById(R.id.radioQRCode);
        //绑定一个匿名监听器
        String model = mBusinessManager.getIntentSystemModel();
        if(model.equals("2")){//自行车模式
            radioBike.setChecked(true);
        }else if(model.equals("1")){//简易模式
            radioSimple.setChecked(true);
        }else if(model.equals("3")){//二维码模式
            radioQRCode.setChecked(true);
        }else{//标准模式
            radioFormal.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                if(checkedId == radioFormal.getId()){
                    radioFormal.setChecked(true);
                    mBusinessManager.saveIntentSystemModel("0");
                }else if(checkedId == radioSimple.getId()){
                    radioSimple.setChecked(true);
                    mBusinessManager.saveIntentSystemModel("1");
                }else if(checkedId == radioQRCode.getId()){
                    radioQRCode.setChecked(true);
                    mBusinessManager.saveIntentSystemModel("3");
                }else{
                    radioBike.setChecked(true);
                    mBusinessManager.saveIntentSystemModel("2");
                }
            }
        });
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
    //设置立式摄像头ip地址
    private void setCameraIP(){
        String cameraIP = SharedPreferencesConfig.getString(SesstingActivity.this,"cameraIp");
        final EditText exits = new EditText(this);
        if (null != cameraIP) {
            exits.setText(cameraIP);
            exits.setSelection(cameraIP.length());
        }
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("请设置立式摄像头IP地址");
        build.setView(exits);
        build.setCancelable(false);
        build.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = exits.getText().toString().trim();
                if (content != null) {
                    SharedPreferencesConfig.saveStringConfig(SesstingActivity.this,"cameraIp",content);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
                }
            }
        });
        build.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
            }
        });
        build.show();
    }
    //设置电子支付ip地址
    private void setElectPayIP(){
        String electPayIP = mBusinessManager.getElectPayIP();
        final EditText exits = new EditText(this);
        if (null != electPayIP) {
            exits.setText(electPayIP);
            exits.setSelection(electPayIP.length());
        }
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("请设置电子支付IP地址");
        build.setView(exits);
        build.setCancelable(false);
        build.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = exits.getText().toString().trim();
                if (content != null) {
                    mBusinessManager.saveElectPayIP(content);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
                }
            }
        });
        build.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
            }
        });
        build.show();
    }
    //设置商户门店号
    private void setStoreNo(){
        String storeNo = mBusinessManager.getStoreNo();
        final EditText exits = new EditText(this);
        if (null != storeNo) {
            exits.setText(storeNo);
            exits.setSelection(storeNo.length());
        }
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("请设置商户门店号");
        build.setView(exits);
        build.setCancelable(false);
        build.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = exits.getText().toString().trim();
                if (content != null) {
                    mBusinessManager.saveStoreNo(content);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
                }
            }
        });
        build.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
            }
        });
        build.show();
    }
    //设置app平台接口地址
    private void setAppUrl(){
        String storeNo = mBusinessManager.getAppUrl();
        final EditText exits = new EditText(this);
        if (null != storeNo) {
            exits.setText(storeNo);
            exits.setSelection(storeNo.length());
        }
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("请设置app平台接口地址");
        build.setView(exits);
        build.setCancelable(false);
        build.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = exits.getText().toString().trim();
                if (content != null) {
                    mBusinessManager.saveAppUrl(content);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
                }
            }
        });
        build.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
            }
        });
        build.show();
    }
    //车辆重复进场设置，晨鸟功能开启设置,优惠券种类设置
    public void getAllowEnterAgain() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SesstingActivity.this);
        builder.setTitle("车辆重复进场及晨鸟设置");
        final View view = LayoutInflater.from(SesstingActivity.this).inflate(R.layout.activity_allow_enter_again, null);
        final CheckBox CheckEnterAgain = (CheckBox) view.findViewById(R.id.check_enterAgain);
        final CheckBox CheckOpenChenNiao = (CheckBox) view.findViewById(R.id.check_openChenNiao);
        final CheckBox CheckShowCouponType = (CheckBox) view.findViewById(R.id.check_showCouponType);

        String checkCar = mBusinessManager.getAllowEnterAgain();
        String checkChenNiao = mBusinessManager.getOpenChenNiao();
        String checkCouponType = mBusinessManager.getShowCouponType();

        if ("1".equals(checkCar)) {
            CheckEnterAgain.setChecked(true);
        }
        if ("1".equals(checkChenNiao)) {
            CheckOpenChenNiao.setChecked(true);
        }
        if ("1".equals(checkCouponType)) {
            CheckShowCouponType.setChecked(true);
        }
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CheckEnterAgain.isChecked()) {
                    mBusinessManager.saveAllowEnterAgain("1");
                } else {
                    mBusinessManager.saveAllowEnterAgain("0");
                }

                if (CheckOpenChenNiao.isChecked()) {
                    mBusinessManager.saveOpenChenNiao("1");
                } else {
                    mBusinessManager.saveOpenChenNiao("0");
                }

                if (CheckShowCouponType.isChecked()) {
                    mBusinessManager.saveShowCouponType("1");
                } else {
                    mBusinessManager.saveShowCouponType("0");
                }
            }
        });
        builder.show();
    }

    //设置图片保留的张数
    public void getSavePicNum(){
        String picNum = mBusinessManager.getPicNum();
        final EditText exits = new EditText(this);
        //输入类型为数字文本
        exits.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (null != picNum) {
            exits.setText(picNum);
            exits.setSelection(picNum.length());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(SesstingActivity.this);
        builder.setTitle("设置保留图片的数量");
        builder.setView(exits);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = exits.getText().toString().trim();
                if(content.startsWith("0")&&content.length()>1){
                    MyTools.showToastShort(true,"请输入正确的保留张数",SesstingActivity.this);
                    return;
                }
                if (content != null) {
                    mBusinessManager.savePicNum(content);
                    if(!content.equals("0")&&!content.equals("")){
                        mBusinessManager.savePicDay("0");
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
                }
            }
        });
        builder.show();
    }
    //设置图片保留的天数
    public void getSavePicDay(){
        String picDay = mBusinessManager.getPicDay();
        final EditText exits = new EditText(this);
        //输入类型为数字文本
        exits.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (null != picDay) {
            exits.setText(picDay);
            exits.setSelection(picDay.length());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(SesstingActivity.this);
        builder.setTitle("设置保留图片的天数");
        builder.setView(exits);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = exits.getText().toString().trim();
                if(content.startsWith("0")&&content.length()>1){
                    MyTools.showToastShort(true,"请输入正确的保留天数",SesstingActivity.this);
                    return;
                }
                if (content != null) {
                    mBusinessManager.savePicDay(content);
                    if(!content.equals("0")&&!content.equals("")){
                        mBusinessManager.savePicNum("0");
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(exits.getWindowToken(), 0);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode,
                                    final Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == mBluetoothUtil.CONNECT_DEVICE) {// 连接设备
            if (mBluetoothUtil.interfaceType == 0) {
                devicesAddress = data.getExtras().getString(
                        BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
                devicesName = data.getExtras().getString(
                        BluetoothDeviceList.EXTRA_DEVICE_NAME);
                Log.i("fdh", "设备名：" + devicesName + "设备地址:" + devicesAddress);

//                if(devicesAddress.startsWith("BB")){//蓝牙阀门设备
//                    PrefUtils.setString(this, SampleGattAttributes.GATEDEVICEADDRESS,
//                            devicesAddress);
//                    mBluetoothUtilGate.connect2BlueToothGatedevice(devicesAddress);
//                }else{
                PrefUtils.setString(this, GlobalContants.DEVICEADDRESS,
                        devicesAddress);
                PrefUtils.setString(this, "DEVICENAME", devicesName);
                mBluetoothUtil.connect2BlueToothdevice(devicesAddress);
//                }
            } else if (mBluetoothUtil.interfaceType == 3) {// 串口
                int baudrate = 9600;
                String path = data.getStringExtra("path");
                devicesName = "Serial device";
                devicesAddress = path;
                String com_baudrate = data.getExtras().getString("baudrate");
                if (com_baudrate == null || com_baudrate.length() == 0) {
                    baudrate = 9600;
                }
                baudrate = Integer.parseInt(com_baudrate);
                myPrinter = com.printer.sdk.PrinterInstance.getPrinterInstance(new File(path),
                        baudrate, 0, mBluetoothUtil.mHandler);
                myPrinter.openConnection();
                Log.i(mBluetoothUtil.TAG, "波特率:" + baudrate + "路径:" + path);
            }

        }
        if (requestCode == SCANNIN_GREQUEST_CODE) {
            devicesAddress = data.getExtras().getString(
                    BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
            Log.i(mBluetoothUtil.TAG, "devicesAddress:" + devicesAddress);
            if (BluetoothAdapter.checkBluetoothAddress(devicesAddress)) {
                mBluetoothUtil.connect2BlueToothdevice(devicesAddress);

            } else {
                Toast.makeText(mContext, "蓝牙mac:" + devicesAddress + "不合法", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
