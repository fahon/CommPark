package willsong.cn.commpark.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.DataForSendToPrinterPos80;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.Bike.BikeCheckActivity;
import willsong.cn.commpark.activity.Bike.BikeDataStatisticActivity;
import willsong.cn.commpark.activity.Bike.EnterBikeActivity;
import willsong.cn.commpark.activity.Bike.ExitBikeActivity;
import willsong.cn.commpark.activity.Bike.PreExitBikeActivity;
import willsong.cn.commpark.activity.Print.BluetoothUtil;
import willsong.cn.commpark.activity.Print.GlobalContants;
import willsong.cn.commpark.activity.Print.PrefUtils;
import willsong.cn.commpark.activity.Service.LuzhengService;
import willsong.cn.commpark.activity.Service.ShiZhongService;
import willsong.cn.commpark.activity.Service.UpdateExitCarService;
import willsong.cn.commpark.activity.Service.UpdateManager;
import willsong.cn.commpark.activity.database.AbnormalCarDB;
import willsong.cn.commpark.activity.database.DBManager;
import willsong.cn.commpark.activity.database.EXITCARDB;
import willsong.cn.commpark.activity.database.ParamsSetDB;
import willsong.cn.commpark.activity.database.ParamsSetEntity;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;
import willsong.cn.commpark.activity.zxing.android.CaptureActivity;

import static com.printer.sdk.PrinterInstance.mPrinter;

public class HomePageActivity extends OBaseActivity {
    private GridView moduleListView;
    private ModuleListViewAapter moduleListViewAapter;
    private List<Map<String, Object>> moduleList;

    private boolean isPrint = false;

    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private static final int REQUEST_CODE_SCAN = 0x0000;

    public BluetoothUtil mBluetoothUtil;
    private DBManager dbManager;//巡场车辆
    private EXITCARDB exitcardb;//出场车辆
    private ParamsSetDB paramsSetDB;//参数设置表
    private AbnormalCarDB abnormalCarDB;//异常出场数据【公交已扣款，出场失败车辆】
    private static final int REQUEST_CODE_BORROW_BIKE = 23;
    private static final int REQUEST_CODE_RETURN_BIKE = 24;
    private static final int REQUEST_CODE_CHECK_BIKE = 25;
    private static final int REQUEST_CODE_CHECK = 35;//巡查：二维码模式
    private static final int REQUEST_CODE_EXIT = 36;//出场管理：二维码模式

    // 当前版本号
    public String versionCode = "";
    // 当前版本名称
    public String versionName = "";
    String downUrl = "",updatecontent ="",curVerName = "";//app下载地址,更新的内容，更新的版本号
    public static String APP_NAME = "CommPark.apk";// Password_lc.apk
//    //系统语音播报
//    public static SpeechUtils speechUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        DevSettings dev = new DevSettings(mContext);
//        dev.lockStatusBar(false);//开启下拉
        //------------新打印机-------------------
        try{
            //绑定service，获取ImyBinder对象
            Intent intent = new Intent(this, PosprinterService.class);
            bindService(intent, conn, BIND_AUTO_CREATE);
        }catch (Exception e){
        }
        //------------公交刷卡出场异常数据自动上传----------
        try{
            Intent intent2 = new Intent(this, UpdateExitCarService.class);
            startService(intent2);
        }catch (Exception e){
        }
        //初始化
        setContentViewWithTitle(R.layout.activity_home_page);
//        speechUtils = new SpeechUtils(this);//系统语言tts
        moduleListViewAapter = new ModuleListViewAapter();
        moduleList = new ArrayList<Map<String, Object>>();
        if(SharedPreferencesConfig.getString(this,"loginFlag").equals("1")){
            mBusinessManager.saveIsSign(false);
//            isSign = false;
            setRightButtonVisible(false);
            setModuleListTwo();
            setTitleText(SharedPreferencesConfig.getString(this,"userName"));
        }else{
//            if(isSign){
            if(mBusinessManager.getIsSign()==true){
                setModuleListTwo();
                setRightButtonText("签退");
            }else {
                setModuleList();
                setRightButtonText("签到");
            }
            setTitleText(mBusinessManager.getEmployeeName());
        }


        setLeftButtonVisible(false);
        //组件
        moduleListView = (GridView) findViewById(R.id.activity_home_page_ListView_module);
        //ui设置
        mBluetoothUtil = new BluetoothUtil(this);
        moduleListView.setAdapter(moduleListViewAapter);
        oBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LuzhengService.class);
                Intent it = new Intent(getApplicationContext(), ShiZhongService.class);
                stopService(intent);
                stopService(it);
                finish();
            }
        });
        setTitleRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuilderSign(oMenu.getText().toString());
            }
        });
//        if ("1".equals(mBusinessManager.getBlueToothOpen())) {
//            openBluet();
//        }
        dbManager = new DBManager(this);
        exitcardb = new EXITCARDB(this);
        paramsSetDB = new ParamsSetDB(this);
        abnormalCarDB = new AbnormalCarDB(this);
        //=======================
        if(("1").equals(mBusinessManager.getVersionUpdate())){//版本自动提示更新
            getCurrentVersion();
            mBusinessManager.netVsesionUpdate(false,versionName);
        }
    }

    class ModuleListViewAapter extends BaseAdapter {

        public class Bean {
            public RelativeLayout clickLinearLayout;
            public TextView infoTextView;
            public ImageView imgImageView;
        }

        public int getCount() {
            return moduleList.size();
        }

        public Object getItem(int arg0) {
            return arg0;
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            Bean bean = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_white_face_black_edge, null);
                bean = new Bean();
                // 获取控件
                bean.clickLinearLayout = (RelativeLayout) convertView.findViewById(R.id.item_RelativeLayout_click);
                bean.infoTextView = (TextView) convertView.findViewById(R.id.item_TextView_info);
                bean.imgImageView = (ImageView) convertView.findViewById(R.id.item_ImageView_img);
                convertView.setTag(bean);
            } else {
                bean = (Bean) convertView.getTag();
            }
            final Map<String, Object> map = moduleList.get(position);
            final String info = "" + map.get("module_name");
            final int img = (int)map.get("module_img");
            // 设置内容
            bean.infoTextView.setText(info);
            bean.imgImageView.setImageResource(img);
            bean.clickLinearLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String name = (String) moduleList.get(position).get("module_name");
                    if ("进车管理".equals(name)) {
                        Intent video_intent = new Intent();
                        video_intent.setClass(getApplicationContext(), EntersActivity.class);
                        startActivity(video_intent);
                    } else if ("出车管理".equals(name)) {
                        Intent video_intent = new Intent();
                        video_intent.setClass(getApplicationContext(), ExitParksActivity.class);
                        startActivity(video_intent);
                    } else if ("车辆巡查".equals(name)) {
                       if(("2").equals(mBusinessManager.getIntentSystemModel())){//自行车版
                           Intent it = new Intent(HomePageActivity.this, CaptureActivity.class);
                           it.putExtra("cflag","8");
                           startActivityForResult(it, REQUEST_CODE_CHECK_BIKE);
                       }else{
                           if(!SharedPreferencesConfig.getString(HomePageActivity.this,"loginFlag").equals("1")) {
                               Intent video_intent = new Intent();
                               if(mBusinessManager.getPlateRecognize().toString().equals("1")){
                                   video_intent.putExtra("camera", true);
                                   video_intent.putExtra(MemoryResultActivity.IMPORT_FIELD.TASK, MemoryResultActivity.TASK_VALUE.CHECK_CAR);
                                   video_intent.setClass(getApplicationContext(), MemoryCameraActivity.class);
                                   startActivity(video_intent);
                               }else{
                                   video_intent.putExtra("camera", false);
                                   video_intent.putExtra(MemoryResultActivity.IMPORT_FIELD.TASK, MemoryResultActivity.TASK_VALUE.CHECK_CAR);
                                   video_intent.setClass(getApplicationContext(), MemoryResultActivity.class);
                                   startActivity(video_intent);
                               }

                           }
                       }
                    }else if("数据统计".equals(name)){
                        if(!SharedPreferencesConfig.getString(HomePageActivity.this,"loginFlag").equals("1")){
                            Intent intent = new Intent();
                            if(("2").equals(mBusinessManager.getIntentSystemModel())){//自行车版
                                intent.setClass(getApplicationContext(), BikeDataStatisticActivity.class);
                            }else{
                                intent.setClass(getApplicationContext(), DataStatisticActivity.class);
                            }
                            startActivity(intent);
                        }
                        //UnlicensedCars();
                    }else if ("其他".equals(name)) {
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), RestsActivity.class);
                        startActivity(intent);
                    }else if("退出".equals(name)) {
                        final EditText et = new EditText(HomePageActivity.this);
                        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);
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
                                        String pwd = SharedPreferencesConfig.getString(HomePageActivity.this,"exitpwd");
                                        if(!pwd.equals("")&&pwd.equals(SharedPreferencesConfig.getEtValue(et))){
                                            SharedPreferencesConfig.closeHome(mContext,false);//启用系统按键
                                            Intent intent = new Intent(getApplicationContext(), LuzhengService.class);
                                            stopService(intent);
                                            Intent it = new Intent(getApplicationContext(), ShiZhongService.class);
                                            stopService(it);
//                                            Intent updateIntent =new Intent(HomePageActivity.this, UpdateAppService.class);
//                                            stopService(updateIntent);//停止更新服务
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
//                                            mBusinessManager.saveIsSign(false);
//                                            isSign = false;
                                            System.exit(0);
                                        }else{
                                            if(pwd.equals("")){
                                                if ("222222".equals(SharedPreferencesConfig.getEtValue(et))) {
                                                    SharedPreferencesConfig.closeHome(mContext,false);//启用系统按键
                                                    Intent intent = new Intent(getApplicationContext(), LuzhengService.class);
                                                    stopService(intent);
                                                    Intent it = new Intent(getApplicationContext(), ShiZhongService.class);
                                                    stopService(it);
//                                                    Intent updateIntent =new Intent(HomePageActivity.this, UpdateAppService.class);
//                                                    stopService(updateIntent);//停止更新服务
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
//                                                    mBusinessManager.saveIsSign(false);
//                                                    isSign = false;
                                                    System.exit(0);
                                                }else{
                                                    MyTools.showToastShort(true, "密码输入错误", mContext);
                                                }
                                            }else{
                                                MyTools.showToastShort(true, "密码输入错误", mContext);
                                            }
                                        }
                                        try{
                                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(et.getWindowToken(),0);
                                        }catch (Exception e){
                                        }
                                    }
                                })
                                .show();
                    }else if ("租车".equals(name)) {
                        Intent it = new Intent(HomePageActivity.this,
                                CaptureActivity.class);
                        it.putExtra("cflag","3");
                        startActivityForResult(it, REQUEST_CODE_BORROW_BIKE);
                    }else if ("还车".equals(name)) {
                        Intent it = new Intent(HomePageActivity.this,
                                CaptureActivity.class);
                        it.putExtra("cflag","4");
                        startActivityForResult(it, REQUEST_CODE_RETURN_BIKE);
                    }else if ("进场管理".equals(name)) {
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), EntersActivity.class);
                        startActivity(intent);
                    }else if ("出场管理".equals(name)) {
                        Intent it = new Intent(HomePageActivity.this,
                                CaptureActivity.class);
                        it.putExtra("cflag","4");
                        startActivityForResult(it, REQUEST_CODE_EXIT);
                    }else if ("巡查".equals(name)) {
                        Intent it = new Intent(HomePageActivity.this,
                                CaptureActivity.class);
                        it.putExtra("cflag","4");
                        startActivityForResult(it, REQUEST_CODE_CHECK);
                    }
                }
            });
            return convertView;
        }
    }

    private void setModuleList() {
        moduleList.removeAll(moduleList);
        if(("1").equals(mBusinessManager.getIntentStatisticShow())){
            String[] modules = {"车辆巡查","数据统计","其他","退出"};
            int[] moduleImgs = {R.mipmap.patrol_icon,R.mipmap.car_icon,R.mipmap.car_icon,R.mipmap.car_icon};
            Map<String, Object> map;
            int  i=0;
            for (String module : modules) {
                map = new HashMap<>();
                map.put("module_name", module);
                map.put("module_img", moduleImgs[i]);
                moduleList.add(map);
                i++;
            }
        }else{
            String[] modules = {"车辆巡查","其他","退出"};
            int[] moduleImgs = {R.mipmap.patrol_icon,R.mipmap.car_icon,R.mipmap.car_icon};
            Map<String, Object> map;
            int  i=0;
            for (String module : modules) {
                map = new HashMap<>();
                map.put("module_name", module);
                map.put("module_img", moduleImgs[i]);
                moduleList.add(map);
                i++;
            }
        }
    }

    private void setModuleListTwo() {
        moduleList.removeAll(moduleList);
        String model = mBusinessManager.getIntentSystemModel();
        if(("1").equals(mBusinessManager.getIntentStatisticShow())) {
            String[] modules = {"进车管理", "出车管理", "车辆巡查", "数据统计", "其他", "退出"};
            if(model.equals("2")){
                modules[0] = "租车";
                modules[1] = "还车";
            }else if(model.equals("3")){
                modules[0] = "进场管理";
                modules[1] = "出场管理";
                modules[2] = "巡查";
            }
            int[] moduleImgs = {R.mipmap.car_icon, R.mipmap.car_icon, R.mipmap.patrol_icon, R.mipmap.car_icon, R.mipmap.car_icon, R.mipmap.car_icon};
            Map<String, Object> map;
            int i = 0;
            for (String module : modules) {
                map = new HashMap<>();
                map.put("module_name", module);
                map.put("module_img", moduleImgs[i]);
                moduleList.add(map);
                i++;
            }
        }else{
            String[] modules = {"进车管理", "出车管理", "车辆巡查", "其他", "退出"};
            if(model.equals("2")){
                modules[0] = "租车";
                modules[1] = "还车";
            }else if(model.equals("3")){
                modules[0] = "进场管理";
                modules[1] = "出场管理";
                modules[2] = "巡查";
            }
            int[] moduleImgs = {R.mipmap.car_icon, R.mipmap.car_icon, R.mipmap.patrol_icon, R.mipmap.car_icon, R.mipmap.car_icon};
            Map<String, Object> map;
            int i = 0;
            for (String module : modules) {
                map = new HashMap<>();
                map.put("module_name", module);
                map.put("module_img", moduleImgs[i]);
                moduleList.add(map);
                i++;
            }
        }
    }

    public void openBluet(){
        if(!mBluetoothUtil.isConnected){
            mBluetoothUtil.open();
        }
    }

    public void BuilderSign(final String sing){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);
        if("签到".equals(sing)){
            builder.setTitle("签到")
                    .setMessage("确认签到吗？")
                    .setCancelable(false)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBusinessManager.netSignIn(true,mBusinessManager.getUserName(),mBusinessManager.getPwd());
                        }
                    })
                    .show();
        }else if("签退".equals(sing)){
            builder.setTitle("签退")
                    .setMessage("签退并且打印？")
                    .setCancelable(false)
                    .setNegativeButton("不打印", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBusinessManager.netOutput(true,mBusinessManager.getUserName(),mBusinessManager.getPwd());
                            isPrint = false;
                        }
                    })
                    .setNeutralButton("取消",null)
                    .setPositiveButton("打印", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mPrinter == null) {
                                mBluetoothUtil.open();
                            }else{
                                if(mBluetoothUtil.isConnected==true){
                                    mBusinessManager.netOutput(true,mBusinessManager.getUserName(),mBusinessManager.getPwd());
                                    isPrint = true;
                                }else{
                                    Toast.makeText(HomePageActivity.this, "打印机未连接!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .show();
        }else{
            Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
            //将保存的设置内容存储到数据库
            paramsSetDB.deleteTable();
            ArrayList<ParamsSetEntity> entity = new ArrayList<ParamsSetEntity>();
            entity.add(getParamsEnity());
            paramsSetDB.add(entity);
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public void refreshView(String type, Map<String, Object> Map) {
        super.refreshView(type, Map);
        if (type.equals(BusinessManager.NETSIGNIN)) {
            setModuleListTwo();
            moduleListViewAapter.notifyDataSetChanged();
            setRightButtonText("签退");
            mBusinessManager.saveIsSign(true);
//            isSign = true;
        }else if(type.equals(BusinessManager.NETSIGNOUT)){
//            MyTools.showToastShort(true,""+Map,mContext);
            setModuleList();
            moduleListViewAapter.notifyDataSetChanged();
            setRightButtonText("登录");
            mBusinessManager.saveIsSign(false);
//            isSign = false;
            File dir = new File(Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/");
            emptyFolder(dir);
            dbManager.deleteTable();
            exitcardb.deleteTable();
            if(mPrinter != null){
                if(isPrint) {
                    getPrintDetail(Map);
                }
            }
        }else if(type.equals(BusinessManager.NETVERSIONUPDATE)){//版本更新
           //将保存的设置内容存储到数据库
            paramsSetDB.deleteTable();
            ArrayList<ParamsSetEntity> entity = new ArrayList<ParamsSetEntity>();
            entity.add(getParamsEnity());
            paramsSetDB.add(entity);

            downUrl = Map.get("DownloadLink")+"";
            String version =  Map.get("version")+"";
            String content =  Map.get("Description")+"";
            UpdateManager manager = new UpdateManager(HomePageActivity.this);
            // 弹出软件更新对话框
            manager.showUpdateDialog(downUrl,version,content);
        }else{//失败

        }
    }

    public void getPrintDetail(Map<String,Object> map){
        mPrinter.printText(mBusinessManager.getIntentContent() + "\n");
        mPrinter.printText("    交班明细报表" + "\n");
        mPrinter.printText("收费员账号：" + map.get("UserName") + "\n");
        mPrinter.printText("收费员姓名：" + map.get("RealName") + "\n");
        mPrinter.printText("     班次：" + map.get("Classes") + "\n");
        mPrinter.printText("  上班时间：" + map.get("SignInTime") + "\n");
        mPrinter.printText("  下班时间："+ map.get("SignOutTime") + "\n");
        if(("1").equals(mBusinessManager.getntentExchangePrintDet())){
            mPrinter.printText("-------------------------" + "\n");
            mPrinter.printText("现金交易次数：" + map.get("CashTimes") + "\n");
            mPrinter.printText("现金交易金额：" + map.get("CashPay") + "\n");
            mPrinter.printText("公交卡交易次数：" + map.get("BusCardTimes") + "\n");
            mPrinter.printText("公交卡交易金额：" + map.get("BusCardPay") + "\n");
            mPrinter.printText("     合计应收：" + ((double)map.get("CashPay") + (double)map.get("BusCardPay")) + "\n");
            mPrinter.printText("     合计实收：" +((double)map.get("CashPay") + (double)map.get("BusCardPay")) + "\n");
            mPrinter.printText("\n\n\n");
        }
    }

    //------------------------------
    public static IMyBinder binder;
    public static boolean isConnect;
    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            //绑定成功
                binder = (IMyBinder) service;
                if (PrefUtils.getString(mContext, "DEVICENAME", "").startsWith("printer001")) {
                    sendble(HomePageActivity.this,false);
                }
            if ("1".equals(mBusinessManager.getBlueToothOpen())) {
                openBluet();
            }
        }
    };

    //新版蓝牙打印机连接
    public static void sendble(final Context context,final boolean isShowPro) {
        if(isShowPro){
            SharedPreferencesConfig.showProgressDialog("连接打印机中",context);
        }
        binder.connectBtPort(PrefUtils.getString(context, GlobalContants.DEVICEADDRESS, ""), new UiExecute() {

            @Override
            public void onsucess() {

                // TODO Auto-generated method stub
                //连接成功后在UI线程中的执行
                isConnect = true;
                if(isShowPro) {
                    SharedPreferencesConfig.closeProgressDialog();
                    Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show();
                }
                //此处也可以开启读取打印机的数据
                //参数同样是一个实现的UiExecute接口对象
                //如果读的过程重出现异常，可以判断连接也发生异常，已经断开
                //这个读取的方法中，会一直在一条子线程中执行读取打印机发生的数据，
                //直到连接断开或异常才结束，并执行onfailed
                binder.acceptdatafromprinter(new UiExecute() {

                    @Override
                    public void onsucess() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onfailed() {
                        // TODO Auto-generated method stub
                        isConnect = false;
                        if(isShowPro) {
                            Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onfailed() {
                // TODO Auto-generated method stub
                //连接失败后在UI线程中的执行
                isConnect = false;
                if(isShowPro) {
                    try{
                        SharedPreferencesConfig.closeProgressDialog();
                    }catch (Exception e){}
                    Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //开始打印
    public static void satrtPrint(final Context context,final String time,final String plate,final String cart
            ,final String content,final String endCode,final String endTel,final int codeSize,final String enterCarTitle) {
        if (isConnect) {
            // TODO Auto-generated method stub
            binder.writeDataByYouself(new UiExecute() {

                @Override
                public void onsucess() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onfailed() {
                    // TODO Auto-generated method stub

                }
            }, new ProcessData() {

                @Override
                public List<byte[]> processDataBeforeSend() {
                    ArrayList<byte[]> list = new ArrayList<byte[]>();
                    //创建一段我们想打印的文本,转换为byte[]类型，并添加到要发送的数据的集合list中
                    byte[] data1 = strTobytes(content);
                    byte[] data2 = strTobytes("   " + enterCarTitle);
                    byte[] data3 = strTobytes("入场时间:" + time);
                    byte[] data4 = strTobytes("车牌号码:" + plate);
                    byte[] data5;
                    if (!endCode.equals("")) {
                        data5 = strTobytes(endCode);
                    } else {
                        data5 = strTobytes("上海软杰智能设备有限公司研制");
                    }
                    byte[] data6;
                    if (!endTel.equals("")) {
                        data6 = strTobytes("   TEL:" + endTel);
                    } else {
                        data6 = strTobytes("   TEL:021-51099719");
                    }
                    list.add(data1);
                    //追加一个打印换行指令，因为，pos打印机满一行才打印，不足一行，不打印
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data2);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data3);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data4);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    //打印二维码
                    list.add(DataForSendToPrinterPos80.printQRcode(codeSize, 10, cart));
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data5);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data6);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());

                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    return list;
                }
            });
        } else {
            Toast.makeText(context, "请先连接打印机！", Toast.LENGTH_SHORT).show();
        }
    }

    //开始打印自行车预出场内容
    public static void satrtPrintBikeInfo(final Context context,final String plate,final String enterTime,final String outTime,
                                          final String cart,final String content,final String endCode,final String endTel,final int codeSize,
                                          final String exitBikeTitle) {
        if (isConnect) {
            // TODO Auto-generated method stub
            binder.writeDataByYouself(new UiExecute() {

                @Override
                public void onsucess() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onfailed() {
                    // TODO Auto-generated method stub

                }
            }, new ProcessData() {

                @Override
                public List<byte[]> processDataBeforeSend() {
                    ArrayList<byte[]> list = new ArrayList<byte[]>();
                    //创建一段我们想打印的文本,转换为byte[]类型，并添加到要发送的数据的集合list中
                    byte[] data1 = strTobytes(content);
                    byte[] data2 = strTobytes("   " + exitBikeTitle);
                    byte[] data3 = strTobytes("编号:" + plate);
                    byte[] data4 = strTobytes("入场时间:" + enterTime);
                    byte[] data5 = strTobytes("结算时间:" + outTime);
                    byte[] data6;
                    if (!endCode.equals("")) {
                        data6 = strTobytes(endCode);
                    } else {
                        data6 = strTobytes("上海软杰智能设备有限公司研制");
                    }
                    byte[] data7;
                    if (!endTel.equals("")) {
                        data7 = strTobytes("   TEL:" + endTel);
                    } else {
                        data7 = strTobytes("   TEL:021-51099719");
                    }
                    list.add(data1);
                    //追加一个打印换行指令，因为，pos打印机满一行才打印，不足一行，不打印
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data2);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data3);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data4);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data5);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    //打印二维码
                    list.add(DataForSendToPrinterPos80.printQRcode(codeSize, 10, cart));
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data6);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data7);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());

                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    return list;
                }
            });
        } else {
            Toast.makeText(context, "请先连接打印机！", Toast.LENGTH_SHORT).show();
        }
    }
    //开始打印自行车进场内容
    public static void startPrintBikeEnter(final Context context,final String plate,final String enterTime,final String content,final String endCode,final String endTel,
                                           final String enterBikeTitle) {
        if (isConnect) {
            // TODO Auto-generated method stub
            binder.writeDataByYouself(new UiExecute() {

                @Override
                public void onsucess() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onfailed() {
                    // TODO Auto-generated method stub

                }
            }, new ProcessData() {

                @Override
                public List<byte[]> processDataBeforeSend() {
                    ArrayList<byte[]> list = new ArrayList<byte[]>();
                    //创建一段我们想打印的文本,转换为byte[]类型，并添加到要发送的数据的集合list中
                    byte[] data1 = strTobytes(content);
                    byte[] data2 = strTobytes("   " + enterBikeTitle);
                    byte[] data3 = strTobytes("编号:" + plate);
                    byte[] data4 = strTobytes("租车时间:" + enterTime);
                    byte[] data6;
                    if (!endCode.equals("")) {
                        data6 = strTobytes(endCode);
                    } else {
                        data6 = strTobytes("上海软杰智能设备有限公司研制");
                    }
                    byte[] data7;
                    if (!endTel.equals("")) {
                        data7 = strTobytes("   TEL:" + endTel);
                    } else {
                        data7 = strTobytes("   TEL:021-51099719");
                    }
                    list.add(data1);
                    //追加一个打印换行指令，因为，pos打印机满一行才打印，不足一行，不打印
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data2);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data3);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data4);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data6);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(data7);
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());

                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    list.add(DataForSendToPrinterPos80.printAndFeedLine());
                    return list;
                }
            });
        } else {
            Toast.makeText(context, "请先连接打印机！", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_BORROW_BIKE && resultCode == RESULT_OK) {//租车
            if (data != null) {
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
//                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                Log.i("ccm","结果" + content);
                Intent intent = new Intent(getApplicationContext(),EnterBikeActivity.class);
                intent.putExtra("plate_code",content);
                startActivity(intent);
            }
        }else if(requestCode == REQUEST_CODE_RETURN_BIKE && resultCode == RESULT_OK){//还车
            if (data != null) {
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
//                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                Log.i("ccm","结果" + content);
                String empType = SharedPreferencesConfig.getString(HomePageActivity.this,"EmpType");
                if("1".equals(empType)){
                    Intent intent = new Intent(getApplicationContext(),PreExitBikeActivity.class);
                    intent.putExtra("plate_code",content);
                    startActivity(intent);
                }
                else if("2".equals(empType)){
                    Intent intent = new Intent(getApplicationContext(),ExitBikeActivity.class);
                    intent.putExtra("plate_code",content);
                    startActivity(intent);
                }

//                if(content.contains("\\u")){
//                    content = unicode2String(content);//unicode转字符串
//                    intent.putExtra("plate_code",content);
//                }else{
//                }

            }
        }else if(requestCode == REQUEST_CODE_CHECK_BIKE && resultCode == RESULT_OK){//查车
            if (data != null) {
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
//                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                Log.i("ccm","结果" + content);
                Intent intent = new Intent(getApplicationContext(),BikeCheckActivity.class);
//                if(content.contains("\\u")){
//                    content = unicode2String(content);
//                    intent.putExtra("plate_code",content);
//                }else{
                    intent.putExtra("plate_code",content);
//                }
                startActivity(intent);
            }
        }else if(requestCode == REQUEST_CODE_CHECK && resultCode == RESULT_OK){//巡查：二维码版
            if (data != null) {
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
//                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                Intent intent = new Intent(getApplicationContext(),CarCheckActivity.class);
                intent.putExtra("plate_code",content);
                startActivity(intent);
            }
        }else if(requestCode == REQUEST_CODE_EXIT && resultCode == RESULT_OK){//出场管理：二维码版
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
    /**
     * 获得当前版本信息
     */
    public void getCurrentVersion() {
        try {
            // 获取应用包信息
            PackageInfo info = HomePageActivity.this.getPackageManager().getPackageInfo(
                    HomePageActivity.this.getPackageName(), 0);
            this.versionCode = String.valueOf(info.versionCode);
            this.versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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

}
