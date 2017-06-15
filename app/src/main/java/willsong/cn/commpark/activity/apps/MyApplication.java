package willsong.cn.commpark.activity.apps;

import android.app.Application;
import willsong.cn.commpark.activity.ExChange;
import willsong.cn.commpark.activity.widget.SharedPreferencesConfig;

/**
 * Created by Administrator on 2016/11/21 0021.
 */

public class MyApplication extends Application {

//    public static boolean isSign = false; //签到

    public static ExChange eh;  //费率
    @Override
    public void onCreate() {
        super.onCreate();
        setDefaultUncaughtExceptionHandler();
        //禁用系统按键
        SharedPreferencesConfig.closeHome(this.getApplicationContext(),true);
    }
    //捕获异常数据
    private void setDefaultUncaughtExceptionHandler() {
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}
