package willsong.cn.commpark.activity.Bean;

/**
 * Created by Administrator on 2016/10/11 0011.
 * 贞方向位
 */

public interface StreamDirection {
    /// <summary>
    /// 电子收费系统发出的数据
    /// </summary>
    byte Upload = 0x01;
    /// <summary>
    /// 电子收费系统接收的数据
    /// </summary>
    byte Ack = 0x00;
}
