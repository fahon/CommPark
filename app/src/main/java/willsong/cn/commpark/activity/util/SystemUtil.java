package willsong.cn.commpark.activity.util;

import android.content.Context;

import w.song.orchid.util.MyTools;
import willsong.cn.commpark.activity.luzhengbean.universal;

/**
 * Created by Administrator on 2016/10/24 0024.
 */

public class SystemUtil {
    //路政通用平台
    public static universal getUniversal(Context context) throws Exception {
        universal uv= new universal();
        uv.clientId = " ";
        uv.tsn = MyTools.getImei(context);
        uv.sim = MyTools.getSimSerialNumber(context) + " ";
        uv.psam = MyTools.getImsi(context) + " ";
        uv.sysVer = MyTools.getSysVersionCode();
        uv.appVer = String.valueOf(MyTools.getAppVersionCode(context));
        return uv;
    }
}
