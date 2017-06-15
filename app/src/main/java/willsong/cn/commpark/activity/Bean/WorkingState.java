package willsong.cn.commpark.activity.Bean;

/**
 * Created by Administrator on 2016/10/19 0019.
 */

public interface WorkingState {
    /// <summary>
    /// 正常
    /// </summary>
    byte Normal = 0x01;
    /// <summary>
    /// 异常
    /// </summary>
    byte Error = 0x02;
    /// <summary>
    /// 调试
    /// </summary>
    byte Debug = 0x03;
}
