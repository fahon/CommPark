package willsong.cn.commpark.activity.Bean;

/**
 * Created by Administrator on 2016/10/12 0012.
 */

public interface FunctionCode{
    /// <summary>
    /// 不使用
    /// </summary>
    byte Unuse = 0x00;
    /// <summary>
    /// 进场数据
    /// </summary>
    byte Entry = 0x01;
    /// <summary>
    /// 离场数据
    /// </summary>
    byte Leave = 0x02;
    /// <summary>
    /// 车位数据
    /// </summary>
    byte Park = 0x03;
    /// <summary>
    /// 状态数据
    /// </summary>
    byte State = 0x04;
    /// <summary>
    /// 时间校核数据
    /// </summary>
    byte Time = 0x05;
    /// <summary>
    /// 确认数据
    /// </summary>
    byte Ack = 0x17;
}
