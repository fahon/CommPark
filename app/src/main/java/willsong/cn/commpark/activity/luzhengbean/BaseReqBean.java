package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/20 0020.
 * 签到
 */

public class BaseReqBean{
    //流水号：seqno（int）
    public int seqno;
    //业务编号：code（string）
    public String code;
    //    通用请求字段：commRequest
    public universal commRequest;

    public BaseReqBean(int seqno, String code,universal universal) {
        this.seqno = seqno;
        this.code = code;
        this.commRequest = universal;
    }

    public BaseReqBean(){}

}
