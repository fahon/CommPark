package willsong.cn.commpark.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.Print.PrefUtils;
import willsong.cn.commpark.activity.Service.LuzhengService;
import willsong.cn.commpark.activity.Service.ShiZhongService;
import willsong.cn.commpark.activity.Service.UpdateAppService;
import willsong.cn.commpark.activity.Service.UpdateExitCarService;
import willsong.cn.commpark.activity.Service.UpdateManager;
import willsong.cn.commpark.activity.database.AbnormalCarDB;
import willsong.cn.commpark.activity.database.DBManager;
import willsong.cn.commpark.activity.database.EXITCARDB;
import willsong.cn.commpark.activity.database.ParamsSetDB;
import willsong.cn.commpark.activity.database.ParamsSetEntity;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;

/**
 * Created by Administrator on 2016/11/25 0025.
 */

public class RestsActivity extends OBaseActivity implements View.OnClickListener{

    LinearLayout sessing,outHome,cardBlance,versionUpdate;
    private DBManager dbManager;//巡场车辆
    private EXITCARDB exitcardb;//出场车辆
    private ParamsSetDB paramsSetDB;//参数设置表
    private AbnormalCarDB abnormalCarDB;//异常出场数据【公交已扣款，出场失败车辆】
    // 当前版本号
    public String versionCode = "";
    // 当前版本名称
    public String versionName = "";
    private TextView tv_current_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithTitle(R.layout.activity_rests);
        getCurrentVersion();
        dbManager = new DBManager(this);
        exitcardb = new EXITCARDB(this);
        paramsSetDB = new ParamsSetDB(this);
        abnormalCarDB = new AbnormalCarDB(this);
        init();
    }

    private void init() {
        setRightButtonVisible(false);
        setTitleText("其他");
        sessing = (LinearLayout) findViewById(R.id.sessing);
        outHome = (LinearLayout) findViewById(R.id.outHome);
        cardBlance = (LinearLayout) findViewById(R.id.cardBlance);
        versionUpdate = (LinearLayout) findViewById(R.id.versionUpdate);
        tv_current_version = (TextView) findViewById(R.id.tv_current_version);
        tv_current_version.setText("(当前版本: v"+versionName+")");

        sessing.setOnClickListener(this);
        outHome.setOnClickListener(this);
        cardBlance.setOnClickListener(this);
        versionUpdate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.sessing:
                String sessing = SharedPreferencesConfig.getString(RestsActivity.this,"setpwd");
                if(sessing.length() > 0){
                    getBuilder(1,sessing);
                }else {
                    getBuilder(1,"111111");
                }
                break;
            case R.id.outHome:
                String bpwd = SharedPreferencesConfig.getString(RestsActivity.this,"exitpwd");
                if(bpwd.length() > 0){
                    getBuilder(2,bpwd);
                }else {
                    getBuilder(2,"222222");
                }
                break;
            case R.id.cardBlance:
                Intent it = intentInstance(BusCardBlanceActivity.class);
                startActivity(it);
                break;
            case R.id.versionUpdate:
                mBusinessManager.netVsesionUpdate(true,versionName);
                break;
        }
    }

    public void getBuilder(final int type, final String sessingPwd){
        final EditText et = new EditText(RestsActivity.this);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog.Builder builder = new AlertDialog.Builder(RestsActivity.this);
        builder.setTitle("请输入密码")
                .setView(et)
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et.getWindowToken(),0);
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pwd = et.getText().toString().trim();
                        if (pwd.equals(sessingPwd)) {
                            if(1 == type){
                                Intent it = intentInstance(SesstingActivity.class);
                                startActivity(it);
                            }else if(2 == type){
                                SharedPreferencesConfig.closeHome(mContext,false);//启用系统按键
                                Intent intent = new Intent(getApplicationContext(), LuzhengService.class);
                                stopService(intent);
                                Intent it = new Intent(getApplicationContext(), ShiZhongService.class);
                                stopService(it);
//                                Intent updateIntent =new Intent(RestsActivity.this, UpdateAppService.class);
//                                stopService(updateIntent);//停止更新服务
                                Intent uploadCarintent = new Intent(getApplicationContext(), UpdateExitCarService.class);
                                stopService(uploadCarintent);//停止上传异常出场车辆服务
                                //将保存的设置内容存储到数据库
                                paramsSetDB.deleteTable();
                                ArrayList<ParamsSetEntity> entity = new ArrayList<ParamsSetEntity>();
                                entity.add(getParamsEnity());
                                paramsSetDB.add(entity);
                                //关闭数据库表
                                dbManager.closeDB();
                                exitcardb.closeDB();
                                paramsSetDB.closeDB();
                                abnormalCarDB.closeDB();
                                clearAllActivity();
//                                mBusinessManager.saveIsSign(false);
//                                isSign = false;
                                System.exit(0);
                            }

                        }else{
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et.getWindowToken(),0);
                            MyTools.showToastShort(true, "密码输入错误", mContext);
                        }
                    }
                })
                .show();
    }

    @Override
    public void refreshView(String type, Map<String, Object> map) {
        super.refreshView(type, map);
        if(type.equals(BusinessManager.NETVERSIONUPDATE)){//版本更新
            //将保存的设置内容存储到数据库
            paramsSetDB.deleteTable();
            ArrayList<ParamsSetEntity> entity = new ArrayList<ParamsSetEntity>();
            entity.add(getParamsEnity());
            paramsSetDB.add(entity);

            String downUrl = map.get("DownloadLink")+"";
            String version =  map.get("version")+"";
            String content =  map.get("Description")+"";
            UpdateManager manager = new UpdateManager(RestsActivity.this);
            // 弹出软件更新对话框
            manager.showUpdateDialog(downUrl,version,content);
        }else{//失败
            MyTools.showToastShort(true,"当前已是最新版本,无需更新",mContext);
        }
    }

    //获取要保存到数据库的设置实体
    private ParamsSetEntity getParamsEnity(){
        ParamsSetEntity mset = new ParamsSetEntity(
                SharedPreferencesConfig.getString(mContext,"telephone"),
                SharedPreferencesConfig.getString(mContext,"password"),
                SharedPreferencesConfig.getString(mContext, "systempwd"),
                mBusinessManager.getDevCode(),
                mBusinessManager.getParkCode(),
                SharedPreferencesConfig.getString(mContext,"ActivateCode"),
                mBusinessManager.getIsSign()+"",
                SharedPreferencesConfig.getString(mContext,"setpwd"),
                SharedPreferencesConfig.getString(mContext,"exitpwd"),
                mBusinessManager.getIntentSystemModel(),
                mBusinessManager.getBlueToothOpen(),
                mBusinessManager.getEnterPrint(),
                mBusinessManager.getExitPrint(),
                mBusinessManager.getCardBitmap(),
                mBusinessManager.getIntentTwoDimensionalScan(),
                PrefUtils.getString(this, "DEVICENAME", ""),
                PrefUtils.getString(this, "deviceAddress", ""),
                mBusinessManager.getIntentContent(),
                mBusinessManager.getIntentEndCo(),
                mBusinessManager.getIntentEndTel(),
                mBusinessManager.getPlateRecognize(),
                mBusinessManager.getWebService(),
                mBusinessManager.getRecognizeAgain(),
                mBusinessManager.getLocalAreaId(),
                mBusinessManager.getIntentcorpId(),
                mBusinessManager.getIntentCommonLicence(),
                mBusinessManager.getIntentCheckCar(),
                mBusinessManager.getIntentStatisticShow(),
                mBusinessManager.getntentExchangePrintDet(),
                SharedPreferencesConfig.getString(mContext,"isLinkCamera"),
                SharedPreferencesConfig.getString(mContext,"cameraIp"),
                mBusinessManager.getVersionUpdate(),
                mBusinessManager.getElectPayIP(),
                mBusinessManager.getStoreNo(),
                mBusinessManager.getAppUrl(),
                mBusinessManager.getAllowEnterAgain(),
                mBusinessManager.getOpenChenNiao(),
                mBusinessManager.getEnterTitle(),
                mBusinessManager.getExitTitle(),
                mBusinessManager.getEnterBikeTitle(),
                mBusinessManager.getExitBikeTitle(),
                mBusinessManager.getShowCouponType(),
                mBusinessManager.getPicNum(),
                mBusinessManager.getPicDay());
        return mset;
    }
    /**
     * 获得当前版本信息
     */
    public void getCurrentVersion() {
        try {
            // 获取应用包信息
            PackageInfo info = RestsActivity.this.getPackageManager().getPackageInfo(
                    RestsActivity.this.getPackageName(), 0);
            this.versionCode = String.valueOf(info.versionCode);
            this.versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
