package willsong.cn.commpark.activity.luzhengbean;

/**
 * Created by Administrator on 2016/10/20 0020.
 * 签到
 */

public class Sign{
    //流水号：seqno（int）
    public int seqno;
    //业务编号：code（string）
    public String code;
    //    通用请求字段：commRequest
    public universal commRequest;
//    停车点编号：parkingSpotId(string) 必须
    public String parkingSpotId;
//    平台编号：platformId(string)
    public String platformId;
//    工号：uid（string），不超过12字符
    public String uid;
//    密码：pwd（string），不超过32字符
    public String pwd;
//    经度：longi（string），不超过16字符，如121.480237
    public String longi;
//    纬度：lati（string），不超过16字符，如31.2363
    public String lati;
//    批次号:batchCode（string），不超过32字符
    public  String batchCode;
//    停车点名称：name（string），不超过30字符
    public String name;
//    停车点地址：address（string），不超过100字符
    public String address;
//    服务时段：opentime（string），不超过100字符
    public String opentime;
//    收费标准：price（string），不超过100字符
    public String price;

    public Sign(String parkingSpotId, String platformId, String uid, String pwd, String longi, String lati, String batchCode, String name, String address, String opentime, String price) {
        this.parkingSpotId = parkingSpotId;
        this.platformId = platformId;
        this.uid = uid;
        this.pwd = pwd;
        this.longi = longi;
        this.lati = lati;
        this.batchCode = batchCode;
        this.name = name;
        this.address = address;
        this.opentime = opentime;
        this.price = price;
    }

    public Sign(int seqno, String code, universal commRequest,
                String parkingSpotId, String platformId, String uid,
                String pwd, String longi, String lati, String batchCode,
                String name, String address, String opentime, String price) {
        this.seqno = seqno;
        this.code = code;
        this.commRequest = commRequest;
        this.parkingSpotId = parkingSpotId;
        this.platformId = platformId;
        this.uid = uid;
        this.pwd = pwd;
        this.longi = longi;
        this.lati = lati;
        this.batchCode = batchCode;
        this.name = name;
        this.address = address;
        this.opentime = opentime;
        this.price = price;
    }

    public Sign() {

    }
}
