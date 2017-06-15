package willsong.cn.commpark.activity;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.rfid.DevSettings;
import com.ice.iceplate.ActivateService;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.Print.PrefUtils;
import willsong.cn.commpark.activity.Service.LuzhengService;
import willsong.cn.commpark.activity.Service.ShiZhongService;
import willsong.cn.commpark.activity.database.ParamsSetDB;
import willsong.cn.commpark.activity.database.ParamsSetEntity;
import willsong.cn.commpark.activity.util.Util;
import willsong.cn.commpark.activity.widget.NowAutoCompleteAdapter;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;

import com.ice.ice_plate.*;
//013 工号001 密码123
import static willsong.cn.commpark.activity.apps.MyApplication.eh;

public class LoginActivity extends OBaseActivity {
    private Button setButton;
    private Button loginButton;
    private Button authButton;
    private AutoCompleteTextView userAutoCompleteTextView;
    private ImageView selectPartner;
    private EditText pwEditText;

    String luzheng;
    String shizhong;

    SharedPreferences sp;

    DevSettings dev;

    private String userName = "", passWord = "";
    private ArrayList<String> mArrayList = new ArrayList<String>();

    private ParamsSetDB paramsSetDB;//参数设置表

    //车牌扫描授权码绑定服务
    public ActivateService.ActivateBinder acBinder;
    public ServiceConnection acConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            acConnection = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            acBinder = (ActivateService.ActivateBinder) service;
            int code = -1;
            try {
                code = acBinder.login(SharedPreferencesConfig.getString(mContext, "ActivateCode"));
            } catch (Exception e) {
                code = -1;
            }
            if (code == 0) {
                MyTools.showToastShort(true, "程序已激活", mContext);
                SharedPreferencesConfig.saveBoolConfig(mContext, "isFirstSet", false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        paramsSetDB = new ParamsSetDB(this);
        getParamsSetData();//设置之前的存储参数
//        //禁用系统按键
//        SharedPreferencesConfig.closeHome(mContext,true);
        dev = new DevSettings(this);
        //组件
        setButton = (Button) findViewById(R.id.activity_login_Button_set);
        loginButton = (Button) findViewById(R.id.activity_login_Button_login);
        authButton = (Button) findViewById(R.id.activity_login_Button_auth);
        userAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.activity_login_autoCompleteTextView_user);
        selectPartner = (ImageView) findViewById(R.id.img_select_partner);
        pwEditText = (EditText) findViewById(R.id.activity_login_editText_pw);

        //mBusinessManager.saveIntentPsam(PsamUtil.getPsam());
        //String psam = mBusinessManager.getIntentsaveIntentPsam();
        if ("1".equals(mBusinessManager.getWebService())) {
            mBusinessManager.netWebRequestTime(true);
        } else {
            mBusinessManager.netGetSysTime(true);
        }

        //UI设置
        setLeftButtonVisible(false);
        setRightButtonVisible(false);
        setTitleText(getResources().getString(R.string.app_name));
//        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBusinessManager.getPromptUserName());
//        userAutoCompleteTextView.setAdapter(arrayAdapter);
        // 创建保存用户信息的文件
        try {
            userName = SharedPreferencesConfig.getString(LoginActivity.this,
                    "telephone");
            passWord = SharedPreferencesConfig.getString(LoginActivity.this,
                    "password");
            if (userName.equals("")) {

            } else {
                userAutoCompleteTextView.setText(userName);
                pwEditText.setText(passWord);
            }
        } catch (Exception e) {
        }
        userAutoCompleteTextView.setDropDownBackgroundDrawable(ContextCompat.getDrawable(LoginActivity.this, R.color.gray_bac));// 设置下拉列表的背景
        selectPartner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                userAutoCompleteTextView.setText("");
            }
        });
        initAutoComplete("history", userAutoCompleteTextView);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("admin".equals(userAutoCompleteTextView.getText().toString()) && "".equals(pwEditText.getText().toString())) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), SesstingActivity.class);
                    startActivity(intent);
                } else {
                    if (validate()) {
                        init();
                        if ("90001".equals(userAutoCompleteTextView.getText().toString())) {
                            MyTools.showToastShort(true, "禁止使用该账号登录", mContext);
                        } else {
                            if ("1".equals(mBusinessManager.getWebService())) {
                                mBusinessManager.netWebRequestLogin(true, userAutoCompleteTextView.getText().toString().trim(), pwEditText.getText().toString().trim());
                            } else {
                                mBusinessManager.netLogin(true, userAutoCompleteTextView.getText().toString().trim(), pwEditText.getText().toString().trim());
                            }
                        }
                    }
                }
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(LoginActivity.this);
                et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("请输入密码")
                        .setView(et)
                        .setCancelable(false)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String pwd = SharedPreferencesConfig.getString(mContext, "systempwd");
                                if (!pwd.equals("") && pwd.equals(SharedPreferencesConfig.getEtValue(et))) {
                                    Intent it = intentInstance(EquipmentValueSetActivity.class);
                                    startActivity(it);
                                } else {
                                    if (pwd.equals("")) {
                                        if ("admin".equals(SharedPreferencesConfig.getEtValue(et))) {
                                            Intent it = intentInstance(EquipmentValueSetActivity.class);
                                            startActivity(it);
                                        } else {
                                            MyTools.showToastShort(true, "密码输入错误", mContext);
                                        }
                                    } else {
                                        MyTools.showToastShort(true, "密码输入错误", mContext);
                                    }
                                }
                                try {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                                } catch (Exception e) {
                                }
                            }
                        })
                        .show();

            }
        });
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = intentInstance(IcePlateMainActivity.class);
                startActivity(it);
            }
        });
    }

    /**
     * 初始化AutoCompleteTextView，最多显示5项提示，使 AutoCompleteTextView在一开始获得焦点时自动提示
     *
     * @param field 保存在sharedPreference中的字段名
     * @param auto  要操作的AutoCompleteTextView
     */
    private void initAutoComplete(String field, AutoCompleteTextView auto) {
        SharedPreferences sp = getSharedPreferences("network_url", 0);
        String longhistory = sp.getString("history", "nothing");
        String[] hisArrays = longhistory.split(",");

        for (int i = 0; i < hisArrays.length; i++) {
            mArrayList.add(hisArrays[i]);
        }
        mArrayList.remove("nothing");

        NowAutoCompleteAdapter mAdapter = new NowAutoCompleteAdapter(this,
                mArrayList, 10);

        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        // android.R.layout.simple_dropdown_item_1line, hisArrays);
        // 只保留最近的50条的记录
        if (hisArrays.length > 50) {
            String[] newArrays = new String[50];
            System.arraycopy(hisArrays, 0, newArrays, 0, 50);
            ArrayList<String> mList = new ArrayList<String>();
            for (int i = 0; i < newArrays.length; i++) {
                mList.add(newArrays[i]);
            }
            mList.remove("nothing");
            mAdapter = new NowAutoCompleteAdapter(this, mList, 10);
            // adapter = new ArrayAdapter<String>(this,
            // android.R.layout.simple_dropdown_item_1line, newArrays);
        }
        auto.setAdapter(mAdapter);
        // auto.setDropDownHeight(350);
        // auto.setThreshold(1);
        // auto.setCompletionHint("最近的5条记录");
        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();// 显示屏幕上的下拉
                }
            }
        });
    }

    /**
     * 把指定AutoCompleteTextView中内容保存到sharedPreference中指定的字符段
     *
     * @param field 保存在sharedPreference中的字段名
     * @param auto  要操作的AutoCompleteTextView
     */
    private void saveHistory(String field, AutoCompleteTextView auto) {
        String text = auto.getText().toString();
        SharedPreferences sp = getSharedPreferences("network_url", 0);
        String longhistory = sp.getString(field, "nothing");
        if (!longhistory.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(longhistory);
            sb.insert(0, text + ",");
            sp.edit().putString("history", sb.toString()).commit();
        }
    }

    @Override
    public void refreshView(String type, Map<String, Object> Map) {
        super.refreshView(type, Map);
        if (type.equals(BusinessManager.NETLOGIN)) {
            //是否为外网登录 0：外网  1：局域网
            SharedPreferencesConfig.saveStringConfig(
                    LoginActivity.this, "loginFlag", "0");

            saveHistory("history", userAutoCompleteTextView);
            SharedPreferencesConfig.saveStringConfig(
                    LoginActivity.this, "telephone", userAutoCompleteTextView.getText().toString());
            SharedPreferencesConfig.saveStringConfig(
                    LoginActivity.this, "password", pwEditText.getText().toString());

            //MyTools.showToastShort(true, "登陆成功", mContext);
            Map<String, Object> change = (java.util.Map<String, Object>) Map.get("ChargeInfo");
            eh = new ExChange();
            Double bc = (Double) change.get("BillingCycle");
            eh.BillingCycle = bc.intValue();
            eh.Rate = (double) change.get("Rate");
            Double tne = (Double) change.get("TimeNotEnough");
            eh.TimeNotEnough = tne.intValue();
            Double ft = (Double) change.get("FreeTime");
            eh.FreeTime = ft.intValue();
            Double fbc = (Double) change.get("FirstBillingCycle");
            eh.FirstBillingCycle = fbc.intValue();
            eh.FirstCycleRate = (double) change.get("FirstCycleRate");
            eh.IncludeFreeTime = (boolean) change.get("IncludeFreeTime");
            eh.FreeWeekend = (boolean) change.get("FreeWeekend");
            eh.FeeCeilingPerDay = (double) change.get("FeeCeilingPerDay");
            eh.BillingOnceaDay = (boolean) change.get("BillingOnceaDay");
            eh.ChargeByNatureDay = (boolean) change.get("ChargeByNatureDay");
            eh.CountFreeTimeAfterPref = (boolean) change.get("PreferentialLengthOr");
            String empType = ("" + Map.get("EmpType")).split("\\.")[0];//EmpType 1为管理门岗人员 2为押金门岗人员
            SharedPreferencesConfig.saveStringConfig(LoginActivity.this, "EmpType", empType);
            mBusinessManager.saveUserNameId((String) Map.get("EmpNo"));
            mBusinessManager.saveParkName((String) Map.get("ParkName"));
            initService(Map);
            Intent it = intentInstance(HomePageActivity.class);
            startActivity(it);
            finish();
        } else if (BusinessManager.NETGETTIME.equals(type)) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = "" + Map.get("SysTime");
            Date mdate = null;
            try {
                mdate = sf.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dev.setCurrentTime(mdate.getTime());
        } else if (type.equals(BusinessManager.WEBNETLOGIN)) {
            SharedPreferencesConfig.saveStringConfig(
                    LoginActivity.this, "loginFlag", "1");
            SharedPreferencesConfig.saveStringConfig(
                    LoginActivity.this, "userName", Map.get("FUserName").toString());

            saveHistory("history", userAutoCompleteTextView);
            SharedPreferencesConfig.saveStringConfig(
                    LoginActivity.this, "telephone", userAutoCompleteTextView.getText().toString());
            SharedPreferencesConfig.saveStringConfig(
                    LoginActivity.this, "password", pwEditText.getText().toString());
            MyTools.showToastShort(true, "登录成功", mContext);
            Intent it = intentInstance(HomePageActivity.class);
            startActivity(it);
            finish();
        } else if (type.equals(BusinessManager.WEBNETTIME)) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = "" + Map.get("dateTime").toString();
            Date mdate = null;
            try {
                mdate = sf.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dev.setCurrentTime(mdate.getTime());
        } else {//失败

        }
    }

    public void init() {
        sp = this.getSharedPreferences("SP", this.MODE_PRIVATE);
        String luzheng_ip = sp.getString("luzheng_ip", "");
        int luzheng_port = 0;
        try {
            luzheng_port = Integer.parseInt(sp.getString("luzheng_port", "0"));
        } catch (Exception e) {
            luzheng_port = 0;
        }
        luzheng = mBusinessManager.getLuZheng();
        shizhong = mBusinessManager.getShiZhong();
        String shizhong_ip = sp.getString("shizhong_ip", "");
        int shizhong_port = 0;
        try {
            shizhong_port = Integer.parseInt(sp.getString("shizhong_port", "0"));
        } catch (Exception e) {
            shizhong_port = 0;
        }
        if ("1".equals(luzheng) && !"".equals(luzheng_ip) && 0 != luzheng_port) {
            Intent intent = new Intent(getApplicationContext(), LuzhengService.class);
            startService(intent);
        }
        if ("1".equals(shizhong) && !"".equals(shizhong_ip) && 0 != shizhong_port) {
            Intent intent = new Intent(getApplicationContext(), ShiZhongService.class);
            startService(intent);
        }
    }

    public void initService(Map<String, Object> map) {
        if ("1".equals(luzheng)) {
            Intent intent = new Intent(Util.MYCAST);
            intent.putExtra("index", 1);
            intent.putExtra("user_name", userAutoCompleteTextView.getText().toString());
            intent.putExtra("pwd", pwEditText.getText().toString());
            intent.putExtra("longi", String.valueOf(map.get("Longitude")));
            intent.putExtra("lati", String.valueOf(map.get("Latitude")));
            intent.putExtra("name", (String) map.get("ParkName"));
            intent.putExtra("address", (String) map.get("Address"));
            mBusinessManager.saveUserName(userAutoCompleteTextView.getText().toString());
            mBusinessManager.saveUserNameId((String) map.get("EmpNo"));
            mBusinessManager.saveParkName((String) map.get("ParkName"));
            sendBroadcast(intent);
        }
        if ("1".equals(shizhong)) {
            Intent intent = new Intent(Util.SHIZHONGCART);
            intent.putExtra("index", 1);
            sendBroadcast(intent);
        }
    }

    private boolean validate() {
        String userName = "" + userAutoCompleteTextView.getText().toString().trim();
        String pw = "" + pwEditText.getText().toString().trim();
        if (MyTools.isNullOrAirForString(userName)) {
            MyTools.showToastLong(true, "用户名不能为空", mContext);
            return false;
        } else if (MyTools.isNullOrAirForString(pw)) {
            MyTools.showToastLong(true, "密码不能为空", mContext);
            return false;
        }
        return true;
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
//            System.out.println("32位: " + buf.toString());// 32位的加密
//            System.out.println("16位: " + buf.toString().substring(8, 24));// 16位的加密，其实就是32位加密后的截取
            return cart = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void getParamsSetData() {
        if (!paramsSetDB.checkColumnExists2("electPayIp")) {
            paramsSetDB.onUpgrade(1, 2, "electPayIp");
        }
        if (!paramsSetDB.checkColumnExists2("storeNo")) {
            paramsSetDB.onUpgrade(1, 2, "storeNo");
        }
        if (!paramsSetDB.checkColumnExists2("appUrl")) {
            paramsSetDB.onUpgrade(1, 2, "appUrl");
        }
        if (!paramsSetDB.checkColumnExists2("allowEnterAgain")) {
            paramsSetDB.onUpgrade(1, 2, "allowEnterAgain");
        }
        if (!paramsSetDB.checkColumnExists2("openChenNiao")) {
            paramsSetDB.onUpgrade(1, 2, "openChenNiao");
        }
        if (!paramsSetDB.checkColumnExists2("enterCarTitle")) {
            paramsSetDB.onUpgrade(1, 2, "enterCarTitle");
        }
        if (!paramsSetDB.checkColumnExists2("exitCarTitle")) {
            paramsSetDB.onUpgrade(1, 2, "exitCarTitle");
        }
        if (!paramsSetDB.checkColumnExists2("enterBikeTitle")) {
            paramsSetDB.onUpgrade(1, 2, "enterBikeTitle");
        }
        if (!paramsSetDB.checkColumnExists2("exitBikeTitle")) {
            paramsSetDB.onUpgrade(1, 2, "exitBikeTitle");
        }
        if (!paramsSetDB.checkColumnExists2("showCouponType")) {
            paramsSetDB.onUpgrade(1, 2, "showCouponType");
        }
        if (!paramsSetDB.checkColumnExists2("savePicNum")) {
            paramsSetDB.onUpgrade(1, 2, "savePicNum");
        }
        if (!paramsSetDB.checkColumnExists2("savePicDays")) {
            paramsSetDB.onUpgrade(1, 2, "savePicDays");
        }
        ArrayList<ParamsSetEntity> persons = (ArrayList<ParamsSetEntity>) paramsSetDB.query();
        if (persons.size() == 0) {
            return;
        }
        ParamsSetEntity mset = persons.get(0);
        SharedPreferencesConfig.saveStringConfig(mContext, "telephone", mset.userName);
        SharedPreferencesConfig.saveStringConfig(mContext, "password", mset.userPwd);
        SharedPreferencesConfig.saveStringConfig(mContext, "systempwd", mset.systemPwd);
        mBusinessManager.saveDevCode(mset.deviceCode);
        mBusinessManager.saveParkCode(mset.parkId);
        SharedPreferencesConfig.saveStringConfig(mContext, "ActivateCode", mset.activateCode);
        String sign = mset.isSign;
        if (sign.equals("true")) {
            mBusinessManager.saveIsSign(true);
        } else {
            mBusinessManager.saveIsSign(false);
        }
        SharedPreferencesConfig.saveStringConfig(mContext, "setpwd", mset.setPwd);
        SharedPreferencesConfig.saveStringConfig(mContext, "exitpwd", mset.exitPwd);
        mBusinessManager.saveIntentSystemModel(mset.systemMode);
        mBusinessManager.saveBlueToothOpen(mset.isBluOpen);
        mBusinessManager.saveEnterPrint(mset.isEnterPrint);
        mBusinessManager.saveExitPrint(mset.isOutPrint);
        mBusinessManager.saveCardBitmap(mset.isPicUpload);
        mBusinessManager.saveIntentTwoDimensionalScan(mset.isTwoCodeScan);
        PrefUtils.setString(this, "DEVICENAME", mset.blueDeviceName);
        PrefUtils.setString(this, "deviceAddress", mset.blueDeviceAddress);
        mBusinessManager.saveIntentContent(mset.titlePrint);
        mBusinessManager.saveIntentEndCo(mset.companyPrint);
        mBusinessManager.saveIntentEndTel(mset.telPrint);
        mBusinessManager.savePlateRecognize(mset.isRecognizePlate);
        mBusinessManager.saveWebService(mset.isWebService);
        mBusinessManager.saveRecognizeAgain(mset.isRecognizeAgain);
        mBusinessManager.saveLocalAreaId(mset.webServiceIP);
        mBusinessManager.saveIntentcorpId(mset.corpId);
        mBusinessManager.saveIntentCommonLicence(mset.plateFirstWord);
        mBusinessManager.saveIntentCheckCar(mset.showCheckTime);
        mBusinessManager.saveIntentStatisticShow(mset.showDataStatistic);
        mBusinessManager.saveIntentExchangePrintDet(mset.printSignOutDet);
        SharedPreferencesConfig.saveStringConfig(mContext, "isLinkCamera", mset.linkCamera);
        SharedPreferencesConfig.saveStringConfig(mContext, "cameraIp", mset.cameraIP);
        mBusinessManager.saveVersionUpdate(mset.versionUpdate);

        if (mset.activateCode != null && !mset.activateCode.equals("")) {
            if (SharedPreferencesConfig.getBoolConfig(mContext, "isFirstSet", true)) {//如果授权码有值，如果是首次登陆，开启授权服务，自动授权
                Intent actiIntent = new Intent(mContext, ActivateService.class);
                bindService(actiIntent, acConnection, Service.BIND_AUTO_CREATE);
            }
        }

        mBusinessManager.saveElectPayIP(mset.electPayIp);
        mBusinessManager.saveStoreNo(mset.storeNo);
        mBusinessManager.saveAppUrl(mset.appUrl);
        mBusinessManager.saveAllowEnterAgain(mset.allowEnterAgain);
        mBusinessManager.saveOpenChenNiao(mset.openChenNiao);
        mBusinessManager.saveEnterTitle(mset.enterCarTitle);
        mBusinessManager.saveExitTitle(mset.exitCarTitle);
        mBusinessManager.saveEnterBikeTitle(mset.enterBikeTitle);
        mBusinessManager.saveExitBikeTitle(mset.exitBikeTitle);
        mBusinessManager.saveShowCouponType(mset.showCouponType);
        mBusinessManager.savePicNum(mset.savePicNum);
        mBusinessManager.savePicDay(mset.savePicDays);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (acBinder != null) {
            unbindService(acConnection);
        }
    }

}
