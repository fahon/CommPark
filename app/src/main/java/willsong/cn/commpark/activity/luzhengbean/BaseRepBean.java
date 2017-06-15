package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/20 0020.
 */

public class BaseRepBean<T> {

    //流水号：seqno（int）
    public int seqno;
    //业务编号：code（string）
    public String code;
    //    通用请求字段：commRequest
    public CommResponse commResponse;

    public class CommResponse{
        public String msg;
        public String result;
        public String systime;
    }
}
