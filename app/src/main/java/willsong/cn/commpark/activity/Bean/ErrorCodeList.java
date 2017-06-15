package willsong.cn.commpark.activity.Bean;

/**
 * Created by Administrator on 2016/10/14 0014.
 */

public interface ErrorCodeList {

    /// <summary>
    /// 没有错误
    /// </summary>
    byte None = 0x00;
    /// <summary>
    /// 数据校验错误
    /// </summary>
    byte CRC_Fail = 0x01;
}
