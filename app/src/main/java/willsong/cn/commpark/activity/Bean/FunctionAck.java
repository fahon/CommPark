package willsong.cn.commpark.activity.Bean;

/**
 * Created by Administrator on 2016/10/11 0011.
 */

public interface FunctionAck {
    /// <summary>
    /// 进场数据帧的回应
    /// </summary>
    int EntryAck = 0x01;
    /// <summary>
    /// 离场数据帧的回应
    /// </summary>
    int LeaveAck = 0x02;
    /// <summary>
    /// 车位数据帧的回应
    /// </summary>
    int ParkAck = 0x03;
    /// <summary>
    /// 运行状态数据帧的回应
    /// </summary>
    int StateAck = 0x04;
}
