package willsong.cn.commpark.activity.database;

import android.app.Activity;

//异常车辆  最近记录的列表实体类
public class ParamsSetEntity extends Activity {
    public int _id;
    public String userName;//登录账号
    public String userPwd;//登录密码
    public String systemPwd;//系统参数设置密码
    public String deviceCode;//设备编号
    public String parkId;//停车场编号
    public String activateCode;//设备授权码
    public String isSign;//是否签到状态
    public String setPwd;//设置密码
    public String exitPwd;//退出密码
    public String systemMode;//系统模式
    public String isBluOpen;//蓝牙开启
    public String isEnterPrint;//进场打印
    public String isOutPrint;//出场打印
    public String isPicUpload;//图片上传
    public String isTwoCodeScan;//二维头扫描
    public String blueDeviceName;//连接的蓝牙设备名
    public String blueDeviceAddress;//连接的蓝牙设备地址
    public String titlePrint;//打印头部设置
    public String companyPrint;//打印尾部公司名称设置
    public String telPrint;//打印尾部电话号码设置
    public String isRecognizePlate;//进出场是否需要进入识别车牌界面
    public String isWebService;//是否连接局域网络
    public String isRecognizeAgain;//是否需要再次识别车牌出场
    public String webServiceIP;//是否需要再次识别车牌出场
    public String corpId;//运营商业代码
    public String plateFirstWord;//4个常用车牌号首字
    public String showCheckTime;//巡查显示时间，费用字段
    public String showDataStatistic;//数据统计模块显示
    public String printSignOutDet;//交班详情打印
    public String linkCamera;//连接立式摄像头
    public String cameraIP;//立式摄像头IP地址
    public String versionUpdate;//更新版本
    public String electPayIp;//电子支付ip地址
    public String storeNo;//商户门店号
    public String appUrl;//平台接口地址
    public String allowEnterAgain;//是否允许车辆重复进场
    public String openChenNiao;//是否开启晨鸟功能
    public String enterCarTitle;//进场打印标题（标准版）
    public String exitCarTitle;//出场打印标题（标准版）
    public String enterBikeTitle;//进场打印标题（自行车版）
    public String exitBikeTitle;//出场打印标题（自行车版）
    public String showCouponType;//优惠券种类显示
    public String savePicNum;//设置保留图片数量
    public String savePicDays;//设置保留图片天数


    public ParamsSetEntity(String userName, String userPwd, String systemPwd, String deviceCode, String parkId,
                           String activateCode, String isSign, String setPwd, String exitPwd, String systemMode,
                           String isBluOpen, String isEnterPrint, String isOutPrint, String isPicUpload, String isTwoCodeScan,
                           String blueDeviceName,String blueDeviceAddress,String titlePrint, String companyPrint, String telPrint, String isRecognizePlate,
                           String isWebService, String isRecognizeAgain,String webServiceIP, String corpId, String plateFirstWord,
                           String showCheckTime, String showDataStatistic,String printSignOutDet, String linkCamera, String cameraIP,
                           String versionUpdate,String electPayIp,String storeNo,String appUrl,String allowEnterAgain,String openChenNiao,
                           String enterCarTitle,String exitCarTitle,String enterBikeTitle,String exitBikeTitle,String showCouponType,
                           String savePicNum,String savePicDays) {
        super();
        this.userName = userName;
        this.userPwd = userPwd;
        this.systemPwd = systemPwd;
        this.deviceCode = deviceCode;
        this.parkId = parkId;
        this.activateCode = activateCode;
        this.isSign = isSign;
        this.setPwd = setPwd;
        this.exitPwd = exitPwd;
        this.systemMode = systemMode;
        this.isBluOpen = isBluOpen;
        this.isEnterPrint = isEnterPrint;
        this.isOutPrint = isOutPrint;
        this.isPicUpload = isPicUpload;
        this.isTwoCodeScan = isTwoCodeScan;
        this.blueDeviceName = blueDeviceName;
        this.blueDeviceAddress = blueDeviceAddress;
        this.titlePrint = titlePrint;
        this.companyPrint = companyPrint;
        this.telPrint = telPrint;
        this.isRecognizePlate = isRecognizePlate;
        this.isWebService = isWebService;
        this.isRecognizeAgain = isRecognizeAgain;
        this.webServiceIP = webServiceIP;
        this.corpId = corpId;
        this.plateFirstWord = plateFirstWord;
        this.showCheckTime = showCheckTime;
        this.showDataStatistic = showDataStatistic;
        this.printSignOutDet = printSignOutDet;
        this.linkCamera = linkCamera;
        this.cameraIP = cameraIP;
        this.versionUpdate = versionUpdate;
        this.electPayIp = electPayIp;
        this.storeNo = storeNo;
        this.appUrl = appUrl;
        this.allowEnterAgain = allowEnterAgain;
        this.openChenNiao = openChenNiao;
        this.enterCarTitle = enterCarTitle;
        this.exitCarTitle = exitCarTitle;
        this.enterBikeTitle = enterBikeTitle;
        this.exitBikeTitle = exitBikeTitle;
        this.showCouponType = showCouponType;
        this.savePicNum = savePicNum;
        this.savePicDays = savePicDays;

    }

    public ParamsSetEntity(int _id, String userName, String userPwd, String systemPwd, String deviceCode, String parkId,
                           String activateCode, String isSign, String setPwd, String exitPwd, String systemMode,
                           String isBluOpen, String isEnterPrint, String isOutPrint, String isPicUpload, String isTwoCodeScan,
                           String blueDeviceName,String blueDeviceAddress, String titlePrint, String companyPrint, String telPrint, String isRecognizePlate,
                           String isWebService, String isRecognizeAgain,String webServiceIP, String corpId, String plateFirstWord,
                           String showCheckTime, String showDataStatistic,String printSignOutDet, String linkCamera, String cameraIP,
                           String versionUpdate,String electPayIp,String storeNo,String appUrl,String allowEnterAgain,String openChenNiao,
                           String enterCarTitle,String exitCarTitle,String enterBikeTitle,String exitBikeTitle,String showCouponType,
                           String savePicNum,String savePicDays) {
        super();
        this._id = _id;
        this.userName = userName;
        this.userPwd = userPwd;
        this.systemPwd = systemPwd;
        this.deviceCode = deviceCode;
        this.parkId = parkId;
        this.activateCode = activateCode;
        this.isSign = isSign;
        this.setPwd = setPwd;
        this.exitPwd = exitPwd;
        this.systemMode = systemMode;
        this.isBluOpen = isBluOpen;
        this.isEnterPrint = isEnterPrint;
        this.isOutPrint = isOutPrint;
        this.isPicUpload = isPicUpload;
        this.isTwoCodeScan = isTwoCodeScan;
        this.blueDeviceName = blueDeviceName;
        this.blueDeviceAddress = blueDeviceAddress;
        this.titlePrint = titlePrint;
        this.companyPrint = companyPrint;
        this.telPrint = telPrint;
        this.isRecognizePlate = isRecognizePlate;
        this.isWebService = isWebService;
        this.isRecognizeAgain = isRecognizeAgain;
        this.webServiceIP = webServiceIP;
        this.corpId = corpId;
        this.plateFirstWord = plateFirstWord;
        this.showCheckTime = showCheckTime;
        this.showDataStatistic = showDataStatistic;
        this.printSignOutDet = printSignOutDet;
        this.linkCamera = linkCamera;
        this.cameraIP = cameraIP;
        this.versionUpdate = versionUpdate;
        this.electPayIp = electPayIp;
        this.storeNo = storeNo;
        this.appUrl = appUrl;
        this.allowEnterAgain = allowEnterAgain;
        this.openChenNiao = openChenNiao;
        this.enterCarTitle = enterCarTitle;
        this.exitCarTitle = exitCarTitle;
        this.enterBikeTitle = enterBikeTitle;
        this.exitBikeTitle = exitBikeTitle;
        this.showCouponType = showCouponType;
        this.savePicNum = savePicNum;
        this.savePicDays = savePicDays;
    }
    public ParamsSetEntity() {
        super();
    }

}
