package willsong.cn.commpark.activity.Bean;

/**
 * Created by Administrator on 2016/10/19 0019.
 */

public interface AlarmState {
    /// <summary>
    ///
    /// </summary>
    short None = 0x0000;
    /// <summary>
    /// 备用电源供电
    /// </summary>
    short BackupPower = 0x0001;
    /// <summary>
    /// 无法打印
    /// </summary>
    short CannotPrint = 0x0002;
    /// <summary>
    /// 人工控制
    /// </summary>
    short ManualControl = 0x0004;
}
