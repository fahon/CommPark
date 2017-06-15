package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/20 0020.
 */

public class universal {

    //    客户编号（string clientId），
    public String clientId;
    //    终端序列号（终端硬件的唯一标识，string tsn），
    public String tsn;
    //    SIM卡号（string sim），
    public String sim;
    //    PSAM卡号（string psam），
    public String psam;
    //    系统版本号（终端操作系统版本号，string sysVer），
    public String sysVer;
    //    应用版本号（终端应用版本号，string appVer）
    public String appVer;

    public universal(){};

    public universal(String clientId, String tsn,
                     String sim, String psam,
                     String sysVer, String appVer) {
        this.clientId = clientId;
        this.tsn = tsn;
        this.sim = sim;
        this.psam = psam;
        this.sysVer = sysVer;
        this.appVer = appVer;
    }
}
