package willsong.cn.commpark.activity.Bean;

/**
 * Created by Administrator on 2016/10/19 0019.
 */

public interface ParkType {
    /// <summary>
    /// 月租长包车辆
    /// </summary>
    byte ByMonth = 0x00;
    /// <summary>
    /// 时租访客车辆
    /// </summary>
    byte Vister = 0x01;
    /// <summary>
    /// 免费车辆
    /// </summary>
    byte Free = 0x02;
    /// <summary>
    /// 异常未知车辆
    /// </summary>
    byte Unkown = 0x03;
}
