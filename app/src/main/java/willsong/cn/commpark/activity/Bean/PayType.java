package willsong.cn.commpark.activity.Bean;

/**
 * Created by Administrator on 2016/10/19 0019.
 */

public interface PayType {

    /// <summary>
    /// 现金支付
    /// </summary>
    byte Cash = 0x00;
    /// <summary>
    /// 交通卡支付
    /// </summary>
    byte TrafficCard = 0x01;
    /// <summary>
    /// 银行卡支付
    /// </summary>
    byte BankCard = 0x02;
    /// <summary>
    /// 手机支付
    /// </summary>
    byte MobilePhone = 0x03;
}
