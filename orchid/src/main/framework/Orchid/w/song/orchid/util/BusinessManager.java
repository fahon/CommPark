package w.song.orchid.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import w.song.orchid.activity.OBaseActivity;
import w.song.orchid.activity.OBaseFragment;
import w.song.orchid.data.Dic;
import w.song.orchid.data.Field;

public class BusinessManager extends OBaseBusinessManager {
    private final static String TAG = "BusinessManager";
    public final static String HEADCODE_OK = "200";
    public final static String GET_FAIL = "get_fail";
    public final static String GET_BLACK_FAIL = "get_black_fail";
    //接口
    //   private final static String URL = "http://api.parkfull.com.cn/";
//    public final String URL = getAppUrl();//"http://139.196.175.54:8081/"
    private final String LOGIN = getAppUrl() + "api/Employee/Login";
    private final String GETSYSTIME = getAppUrl() + "api/Employee/GetSysTime";
    private final String GETSIGNIN = getAppUrl() + "api/Employee/SignIn";
    private final String SIGNOUT = getAppUrl() + "api/Employee/SignOut";


    private final String INSERTCARENTERREC = getAppUrl() + "api/Car/SCInsertCarEnterRecAngin";//车辆重复进场
    private final String GETBILLINFO = getAppUrl() + "api/Car/GetBillInfo";
    private final String INSERTCAROUTREC = getAppUrl() + "api/Car/SCInsertCarOutRec";
    private final String GETCHECKINFO = getAppUrl() + "api/Car/GetCheckInfo";

    //新接口
    private final String SCLOGIN = getAppUrl() + "api/Employee/SCLogin";

    public final String SCHEARTBEAT = getAppUrl() + "api/Employee/SCHeartBeat";

    private final String SCDATASTATISTIC = getAppUrl() + "api/Employee/GetTotalCarThroughData";//数据统计
    private final String SCBILLDETAIL = getAppUrl() + "api/Employee/GetCarThroughDetails";//出场明细
    private final String SCCOUPONINFO = getAppUrl() + "api/Coupon/GetCouponInfo";//查询优惠券详情
    private final String SCPRESENTCARL = getAppUrl() + "api/Employee/GetTotalCarThroughDataListL";//在场车辆明细（临时车）
    private final String SCPRESENTCARY = getAppUrl() + "api/Employee/GetTotalCarThroughDataListY";//在场车辆明细（月租车）
    private final String GETISPRESENCE = getAppUrl() + "api/Car/SCInsertCarEnterRec";//车辆是否在场
    private final String SCVERSIONUPDATE = getAppUrl() + "api/System/GetLastVersion";//版本更新
    private final String SCISBLACKCARD = getAppUrl() + "api/Car/BusCardIsBlack";//是否为黑名单卡

//    public final String PAYURL = getElectPayIP();//电子支付ip"http://122.112.82.212:8081/"
    private final String SCSCANPAY = getElectPayIP()+"api/ThirdPayment/BarcodePay";//电子扫码支付接口
    private final String SCPAYQUERY = getElectPayIP()+"api/ThirdPayment/PayQuery";//订单查询接口
    private final String SCPAYREVERSE = getElectPayIP()+"api/ThirdPayment/PayReverse";//撤销订单接口
    private final String SCCOUPONLIST = getAppUrl() + "api/Coupon/GetCouponList";//查询优惠券名称列表
    private final String SCCOUPONLISTINFO = getAppUrl() + "api/Coupon/GetCouponListInfo";//查询优惠券名称列表详情
    private final String INSERTBIKEOUTRECSURE = getAppUrl() + "api/Car/SCInsertCarOutRecSure";//自行车版正式出场接口
    private final String QUERYPARKINGREC = getAppUrl() + "api/Car/SCQueryParkingRec";//根据流水查询订单详情

    protected final NetDataManager mNetDataManager;

    public BusinessManager(Context context) {
        super(context);
        mNetDataManager = new NetDataManager(context);
    }

    public BusinessManager(OBaseActivity oBaseActivity) {
        super(oBaseActivity);
        mNetDataManager = new NetDataManager(oBaseActivity);
    }

    public BusinessManager(OBaseFragment oBaseFragment) {
        super(oBaseFragment);
        mNetDataManager = new NetDataManager(oBaseFragment.getActivity());
    }

    public final static String NETLOGIN = TAG + ".netLogin";
    public final static String NETSIGNIN = TAG + ".netSignIn";
    public final static String NETSIGNOUT = TAG + ".netSignInOut";
    public final static String NETGETTIME = TAG + ".netGettime";

    /**
     * 时间校准
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netGetSysTime(final boolean isDialogShow) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "时间校对中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                String[] status = mNetDataManager.post(GETSYSTIME, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETGETTIME, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    /**
     * 用户登陆
     *
     * @param isDialogShow
     * @param loginName
     * @param pw
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netLogin(final boolean isDialogShow, final String loginName, final String pw) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "登陆中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("UserName", loginName);
                valueMap.put("PassWord", pw);
                //               valueMap.put("CompnayCode", getCompnayCode());
                valueMap.put("DevCode", getDevCode());
                valueMap.put("TerminalType", Dic.TERMINALTYPE);
                valueMap.put("Tsn", MyTools.getImei(mContext));
                valueMap.put("Sim", MyTools.getSimSerialNumber(mContext));
                valueMap.put("Psam", MyTools.getImsi(mContext));
                valueMap.put("SysVer", MyTools.getSysVersionCode());
                valueMap.put("AppVer", MyTools.getAppVersionCode(mContext));
                String[] status = mNetDataManager.post(SCLOGIN, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            saveUserInfo(dataMap);
                            savePromptUserName(loginName);
                            saveUserName(loginName);
                            savePwd(pw);
                            mOBaseActivity.refreshView(NETLOGIN, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    /**
     * 签到
     *
     * @param isDialogShow
     * @param loginName
     * @param pw
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netSignIn(final boolean isDialogShow, final String loginName, final String pw) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "签到中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("UserName", loginName);
                valueMap.put("PassWord", pw);
                //               valueMap.put("CompnayCode", getCompnayCode());
                valueMap.put("DevCode", getDevCode());
                valueMap.put("TerminalType", Dic.TERMINALTYPE);
                valueMap.put("Tsn", MyTools.getImei(mContext));
                valueMap.put("Sim", MyTools.getSimSerialNumber(mContext));
                valueMap.put("Psam", MyTools.getImsi(mContext));
                valueMap.put("SysVer", MyTools.getSysVersionCode());
                valueMap.put("AppVer", MyTools.getAppVersionCode(mContext));
                String[] status = mNetDataManager.post(GETSIGNIN, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> dataMap = new HashMap<String, Object>();
                            mOBaseActivity.refreshView(NETSIGNIN, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }

                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }


    /**
     * 签退
     *
     * @param isDialogShow
     * @param loginName
     * @param pw
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netOutput(final boolean isDialogShow, final String loginName, final String pw) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "签退中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("DevCode", getDevCode());
                valueMap.put("UserName", loginName);
                valueMap.put("PassWord", pw);
                String[] status = mNetDataManager.post(SIGNOUT, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETSIGNOUT, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }

                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETINSERTCARENTERREC = TAG + ".netInsertCarEnterRec";

    /**
     * 插入进车记录(重复进场接口)
     *
     * @param isDialogShow
     * @param carPlate
     * @param time
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netInsertCarEnterRec(final boolean isDialogShow, final String carPlate,
                                     final String time, final String UnCart,final boolean IsBike) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "提交中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("DevCode", getDevCode());
                valueMap.put("EnterTime", time);
                valueMap.put("EmpName", getEmployeeName());
                valueMap.put("EmpNo", getUserNameId());
                if (null != carPlate && carPlate.length() > 0) {
                    valueMap.put("CarPlate", carPlate);
                    valueMap.put("FieldCode", UnCart);
                } else {
                    valueMap.put("CarPlate", "无牌车");
                    valueMap.put("FieldCode", UnCart);
                }
                valueMap.put("CardCode", "");
                valueMap.put("CardType", "");
                valueMap.put("CarType", "");
                valueMap.put("MemId", "");
                valueMap.put("IsBike", IsBike);
                String[] status = mNetDataManager.post(INSERTCARENTERREC, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETINSERTCARENTERREC, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }

                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETGETBILLINFO = TAG + ".netGetBillInfo";

    /**
     * 获取计费信息
     *
     * @param isDialogShow
     * @param carPlate
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netGetBillInfo(final boolean isDialogShow, final String carPlate, final String FieldCode) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "获取中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
//                valueMap.put("CompanyId", getCompanyId());
//                valueMap.put("ParkId", getParkId());
                valueMap.put("DevCode", getDevCode());
                valueMap.put("CarPlate", carPlate);
                valueMap.put("FieldCode", FieldCode);//场内码
                valueMap.put("OutTime", CalendarTool.getTodayStrDate(MyTools.FORMATDATE[0]));
                String[] status = mNetDataManager.post(GETBILLINFO, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETGETBILLINFO, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }

                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETINSERTCAROUTREC = TAG + ".netInsertCarOutRec";

    public final static String NETGETCHECKINFO = TAG + ".netGetCheckInfo";

    /**
     * 车辆巡查
     *
     * @param isDialogShow
     * @param carPlate
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netGetCheckInfo(final boolean isDialogShow, final String carPlate) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "获取中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("DevCode", getDevCode());
                valueMap.put("CarPlate", carPlate);
                String[] status = mNetDataManager.post(GETCHECKINFO, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETGETCHECKINFO, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }

                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETGETISPRESENCE = TAG + ".netGetIsPresence";//车辆是否在场

    /**
     * 车辆进场：车辆是否在场(是：这个接口直接进场  否：弹出对话框，用户选择是否再进场)
     *
     * @param isDialogShow
     * @param carPlate
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netGetIsPresence(final boolean isDialogShow, final String carPlate,
                                 final String time, final String UnCart,final boolean IsBike) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "提交中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("DevCode", getDevCode());
                valueMap.put("EnterTime", time);
                valueMap.put("EmpName", getEmployeeName());
                valueMap.put("EmpNo", getUserNameId());
                if (null != carPlate && carPlate.length() > 0) {
                    valueMap.put("CarPlate", carPlate);
                    valueMap.put("FieldCode", UnCart);
                } else {
                    valueMap.put("CarPlate", "无牌车");
                    valueMap.put("FieldCode", UnCart);
                }
                valueMap.put("CardCode", "");
                valueMap.put("CardType", "");
                valueMap.put("CarType", "");
                valueMap.put("MemId", "");
                valueMap.put("IsBike", IsBike);
                String[] status = mNetDataManager.post(GETISPRESENCE, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETINSERTCARENTERREC, dataMap);
                        } else {
                            if (code.equals("517")) {
                                mOBaseActivity.refreshView(NETGETISPRESENCE, new HashMap<String, Object>());
                            } else {
                                MyTools.dialogIntro(isDialogShow,
                                        NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                                mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                            }
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    /**
     * 公交卡支付金额  PayType=1
     * 插入出车记录    PayType=2
     *
     * @param isDialogShow
     * @param carPlate
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netInsertCarOutRec(final boolean isDialogShow,
                                   final String carPlate,
                                   final String enterTime,
                                   final String outTime,
                                   final String sum,
                                   final String PayType,
                                   final String SptCreaderApiGetCardInfoData,
                                   final String FieldCode,
                                   final String seqNo,
                                   final String pa,
                                   final String CityCode,
                                   final String cardNumber,
                                   final String card,
                                   final String cardcount,
                                   final String cardMoney,
                                   final String money,
                                   final String cpuCar,
                                   final String cardtype,
                                   final String cardTrade,
                                   final String icType,
                                   final String cardVer,
                                   final String corpId,
                                   final String coupon,
                                   final String amount,
                                   final String cardDateTime,
                                   final String TerminalTenSeq,
                                   final Boolean IsBike,
                                   final String CouponName) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "提交中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("DevCode", getDevCode());
                valueMap.put("OutTime", outTime);
                valueMap.put("SeqNo", seqNo);//PosSeq  POS机流水号 10进制   停车记录表的流水ID 【终端交易流水号为同一个】
                valueMap.put("CarPlate", carPlate);
                valueMap.put("FieldCode", FieldCode);
                valueMap.put("Fees", sum);//实收金额
                valueMap.put("EmpName", getEmployeeName());
                valueMap.put("EmpNo", getUserNameId());
                valueMap.put("PayType", PayType);//支付方式：4现金支付 5公交卡支付
                valueMap.put("Psam", pa);//PosId POS机号【PSAM卡号后4字节】
                valueMap.put("CardCityCode", CityCode);//CityCode 城市代码
//                valueMap.put("CardPhysicsNumber",cardNumber);//【生成 CPUCardId 卡号（CityCode+CpuCardNo+CardPhysicsNumber）需要用到】
                valueMap.put("CardSurfaceNumber", card);//CardFaceNum 卡面号
                valueMap.put("CardTradeCount", cardcount);//TxnCounter 交易计数器
                valueMap.put("CardBeroreTradeMoney", cardMoney);//BalBef  消费前卡余额
                valueMap.put("CardTradeMoney", money);//TxnAmt  交易金额
                valueMap.put("CardTac", cpuCar);//TAC 交易认证码
                valueMap.put("CardType", cardtype);//CardKind 交通卡卡类型
                valueMap.put("CpuCardNo", CityCode + cardTrade + cardNumber);//【生成 CPUCardId 卡号（CityCode+CpuCardNo+CardPhysicsNumber）需要用到】
                valueMap.put("ICType", icType);//卡片类型,0-M1,1-CPU
                valueMap.put("CardVer", cardVer);//CardVerNo 卡内版本号
                valueMap.put("CardBusinessCode", 52);
                valueMap.put("CorpId", corpId);//CorpId  营运单位代码
                valueMap.put("Coupon", coupon);//优惠券号
                valueMap.put("Amount", amount);//应收金额
                valueMap.put("CardDateTime", cardDateTime);//公交卡交易时间
                valueMap.put("TerminalTenSeq", TerminalTenSeq);//终端交易流水号
                valueMap.put("IsBike", IsBike);//是不是自行车版
                valueMap.put("CouponName", CouponName);//优惠券名称
                String[] status = mNetDataManager.post(INSERTCAROUTREC, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETINSERTCAROUTREC, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }

                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETDATASTATISTIC = TAG + ".netDataStatistic";

    /**
     * 数据统计
     *
     * @param isDialogShow
     * @param currTime
     * @param
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netDataStatistic(final boolean isDialogShow, final String currTime) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "数据加载中...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("UserName", getUserName());
                valueMap.put("PassWord", getPwd());
                valueMap.put("DevCode", getDevCode());
                valueMap.put("QueryDate", currTime);
                String[] status = mNetDataManager.post(SCDATASTATISTIC, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETDATASTATISTIC, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETBILLDETAIL = TAG + ".netBillDetail";

    /**
     * 出场明细
     *
     * @param isDialogShow
     * @param currTime
     * @param pageIndex
     * @param pageSize
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netBillDetail(final boolean isDialogShow, final String currTime, final int pageIndex, final int pageSize) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "数据加载中...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("UserName", getUserName());
                valueMap.put("PassWord", getPwd());
                valueMap.put("DevCode", getDevCode());
                valueMap.put("QueryDate", currTime);
                valueMap.put("PageIndex", pageIndex);
                valueMap.put("PageSize", pageSize);
                String[] status = mNetDataManager.post(SCBILLDETAIL, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETBILLDETAIL, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETCOUPONINFO = TAG + ".netCouponInfo";

    /**
     * 查询优惠券信息
     *
     * @param isDialogShow
     * @param couponCode   优惠券编码
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netCouponInfo(final boolean isDialogShow, final String couponCode) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "数据加载中...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("CouponCode", couponCode);
                valueMap.put("DevCode", getDevCode());
                String[] status = mNetDataManager.post(SCCOUPONINFO, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETCOUPONINFO, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETPRESENTCAR = TAG + ".netPresentCar";

    /**
     * 在场车辆明细
     *
     * @param isDialogShow
     * @param currTime
     * @param pageIndex
     * @param pageSize
     * @param type         0:临时车 1:月租车
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netPresentCar(final boolean isDialogShow,
                              final String currTime,
                              final int pageIndex,
                              final int pageSize,
                              final int type) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "数据加载中...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("UserName", getUserName());
                valueMap.put("PassWord", getPwd());
                valueMap.put("DevCode", getDevCode());
                valueMap.put("QueryDate", currTime);
                valueMap.put("PageIndex", pageIndex);
                valueMap.put("PageSize", pageSize);
                if (type == 0) {
                    String[] status = mNetDataManager.post(SCPRESENTCARL, valueMap);
                    Message msg = new Message();
                    Map<String, Object> msgMap = new HashMap<String, Object>();
                    msgMap.put("status", status);
                    msg.obj = msgMap;
                    handler.sendMessage(msg);
                } else {
                    String[] status = mNetDataManager.post(SCPRESENTCARY, valueMap);
                    Message msg = new Message();
                    Map<String, Object> msgMap = new HashMap<String, Object>();
                    msgMap.put("status", status);
                    msg.obj = msgMap;
                    handler.sendMessage(msg);
                }
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETPRESENTCAR, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETVERSIONUPDATE = TAG + ".netVsesionUpdate";

    /**
     * 版本更新
     *
     * @param isDialogShow
     * @param versionCode  版本号
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netVsesionUpdate(final boolean isDialogShow, final String versionCode) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "数据加载中...").setCancelable(true);
        } else {
            progressDialog = null;
        }
        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("VersionNumber", versionCode);
                String[] status = mNetDataManager.get(SCVERSIONUPDATE, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Double codes = (Double) map.get("err");
                        int code = codes.intValue();
                        if (code==200) {
                            mOBaseActivity.refreshView(NETVERSIONUPDATE, map);
                        } else {
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }
            };
        }.start();
    }

//    private final String WEBURL = getLocalAreaId();//http://122.112.82.212:8081/api/LANAndroid/
    private final String WEBREQUESTTIME = getLocalAreaId() + "GetNetTime";//局域网时间
    private final String WEBREQUESTENTER = getLocalAreaId() + "InsertParkInOutDetail";//局域网进场
    private final String WEBREQUESTLOGIN = getLocalAreaId() + "IsLogin";//局域网登录
    private final String WEBREQUESTCOST = getLocalAreaId() + "GetClassSettlementByPlateNumber";//局域网计算金额
    private final String WEBREQUESTOUT = getLocalAreaId() + "UpdateParkInOutDetail";//局域网出场
    private final String WEBREQUESTCOUPON = getLocalAreaId() + "QueryVouInfo";//局域网优惠券详情
    public final static String WEBNETLOGIN = TAG + ".netWebRequestLogin";
    public final static String WEBNETTIME = TAG + ".netWebRequestTime";
    public final static String WEBNETINSERT = TAG + ".netWebRequestInsert";
    public final static String WEBNETOUT = TAG + ".netWebRequestOut";
    public final static String WEBNETCOST = TAG + ".netWebRequestCost";
    public final static String WEBNECOUPON = TAG + ".netWebRequestCoupon";

    /**
     * 局域网  时间校准
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netWebRequestTime(final boolean isDialogShow) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "时间校对中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("dateTime", "1");
                String[] status = mNetDataManager.post(WEBREQUESTTIME, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(WEBNETTIME, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    /**
     * 局域网 登录
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netWebRequestLogin(final boolean isDialogShow,
                                   final String FUserNo,
                                   final String Password) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "登录中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("FUserNo", FUserNo);
                valueMap.put("Password", Password);
                String[] status = mNetDataManager.post(WEBREQUESTLOGIN, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(WEBNETLOGIN, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    /**
     * 局域网 进场
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netWebRequestEnter(final boolean isDialogShow,
                                   final String FParkingName,
                                   final String FTypeName,
                                   final String FAreaCardNo,
                                   final String FCarNo,
                                   final String FPlateNo,
                                   final String FUserNo,
                                   final String FUserName,
                                   final String CarColor,
                                   final String FDateBeg,
                                   final int FDevID) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "提交中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("FParkingName", FParkingName);
                valueMap.put("FTypeName", FTypeName);
                valueMap.put("FAreaCardNo", FAreaCardNo);
                valueMap.put("FCarNo", FCarNo);
                valueMap.put("FPlateNo", FPlateNo);
                valueMap.put("FUserNo", FUserNo);
                valueMap.put("FUserName", FUserName);
                valueMap.put("CarColor", CarColor);
                valueMap.put("FDateBeg", FDateBeg);
                valueMap.put("FDevID", FDevID);
                String[] status = mNetDataManager.post(WEBREQUESTENTER, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> dataMap = new HashMap<String, Object>();
                            mOBaseActivity.refreshView(WEBNETINSERT, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }

                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    /**
     * 局域网 计算金额
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netWebRequestCost(final boolean isDialogShow,
                                  final String FAreaCardNo,
                                  final String PalteNoColor,
                                  final String PayDate,
                                  final String VouNoGroup) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "数据加载中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("FAreaCardNo", FAreaCardNo);
                valueMap.put("PalteNoColor", PalteNoColor);
                valueMap.put("PayDate", PayDate);
                valueMap.put("VouNoGroup", VouNoGroup);
                String[] status = mNetDataManager.post(WEBREQUESTCOST, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(WEBNETCOST, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }

                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }
            };
        }.start();
    }

    /**
     * 局域网 优惠券详情
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netWebRequestCoupon(final boolean isDialogShow,
                                    final String FVouNo) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "数据加载中...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("FVouNo", FVouNo);
                String[] status = mNetDataManager.post(WEBREQUESTCOUPON, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(WEBNECOUPON, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }


    /**
     * 局域网 出场
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netWebRequestOut(final boolean isDialogShow,
                                 final int IsPayLeave,
                                 final int FBusID,
                                 final String FTypeName,
                                 final double FAmount,
                                 final String FOutCarNo,
                                 final String FUserNo,
                                 final String FUserName,
                                 final double FPayAmount,
                                 final int FTotalSecs,
                                 final int FFreeSecs,
                                 final int FPaySecs,
                                 final String FDateEnd,
                                 final int FDevID,
                                 final String VouNoGroup) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "提交中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("IsPayLeave", IsPayLeave);
                valueMap.put("FBusID", FBusID);
                valueMap.put("FTypeName", FTypeName);
                valueMap.put("FAmount", FAmount);
                valueMap.put("FOutCarNo", FOutCarNo);
                valueMap.put("FUserNo", FUserNo);
                valueMap.put("FUserName", FUserName);
                valueMap.put("FPayAmount", FPayAmount);
                valueMap.put("FTotalSecs", FTotalSecs);
                valueMap.put("FFreeSecs", FFreeSecs);
                valueMap.put("FPaySecs", FPaySecs);
                valueMap.put("FDateEnd", FDateEnd);
                valueMap.put("FDevID", FDevID);
                valueMap.put("VouNoGroup", VouNoGroup);

                String[] status = mNetDataManager.post(WEBREQUESTOUT, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> dataMap = new HashMap<String, Object>();
                            mOBaseActivity.refreshView(WEBNETOUT, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }

                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }
            };
        }.start();
    }

    public final static String NETGETSCANPAY = TAG + ".netScanPay";

    public final static String GET_SCAN_FAIL = "get_scan_fail";
    /**
     * 扫码支付
     * DoorNo 门店号
     * PayModeName 交易渠道0全部 1支付宝 2微信 3现金 4银行卡
     * auth_code 条码
     * dev_id 设备终端号
     * oper_id 操作员工号
     * amount 订单金额(单位元,保留2位小数)
     * raw_data 订单金额(单位元,保留2位小数)
     * down_trade_no 商户订单号
     * subject 订单标题
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netScanPay(final boolean isDialogShow,
                           final String DoorNo,
                           final String PayModeName,
                           final String auth_code,
                           final String dev_id,
                           final String oper_id,
                           final String amount,
                           final String raw_data,
                           final String down_trade_no,
                           final String subject) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "扣款中,请稍后...").setCancelable(false);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
              Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("DoorNo", DoorNo);
                valueMap.put("PayModeName", PayModeName);
                valueMap.put("auth_code", auth_code);
                valueMap.put("dev_id", dev_id);
                valueMap.put("oper_id", oper_id);
                valueMap.put("amount", amount);
                valueMap.put("raw_data", raw_data);
                valueMap.put("down_trade_no", down_trade_no);
                valueMap.put("subject", subject);

                String[] status = mNetDataManager.post(SCSCANPAY, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (("2000").equals(code)||("2001").equals(code)) {
//                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
//                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
//                            mOBaseActivity.refreshView(NETGETSCANPAY, dataMap);
                            netPayQuery(true,DoorNo,PayModeName,auth_code,dev_id,oper_id,amount,raw_data,down_trade_no,subject);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_SCAN_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_SCAN_FAIL, new HashMap<String, Object>());
                    }

                }

            };
        }.start();
    }

    public final static String NETGETPAYQUERY = TAG + ".netPayQuery";
    public final static String GET_QUERY_FAIL = "get_query_fail";
    private int count = 0;
    /**
     * 订单查询
     * DoorNo 门店号
     * PayModeName 交易渠道0全部 1支付宝 2微信 3现金 4银行卡
     * auth_code 条码
     * dev_id 设备终端号
     * oper_id 操作员工号
     * amount 订单金额(单位元,保留2位小数)
     * raw_data 订单金额(单位元,保留2位小数)
     * down_trade_no 商户订单号
     * subject 订单标题
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netPayQuery(final boolean isDialogShow,
                           final String DoorNo,
                           final String PayModeName,
                           final String auth_code,
                           final String dev_id,
                           final String oper_id,
                           final String amount,
                           final String raw_data,
                           final String down_trade_no,
                           final String subject) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "订单查询中,请稍后...").setCancelable(false);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("DoorNo", DoorNo);
                valueMap.put("PayModeName", PayModeName);
                valueMap.put("auth_code", auth_code);
                valueMap.put("dev_id", dev_id);
                valueMap.put("oper_id", oper_id);
                valueMap.put("amount", amount);
                valueMap.put("raw_data", raw_data);
                valueMap.put("down_trade_no", down_trade_no);
                valueMap.put("subject", subject);

                String[] status = mNetDataManager.post(SCPAYQUERY, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (("2000").equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETGETPAYQUERY, dataMap);
                        } else if(("2001").equals(code)){
//                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
//                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
//                            mOBaseActivity.refreshView("GET_REQUERY", dataMap);
                            if(count<10){
                                netPayQuery(true,DoorNo,PayModeName,auth_code,dev_id,oper_id,amount,raw_data,down_trade_no,subject);
                                count++;
                            }else{
                                MyTools.showToastShort(isDialogShow,
                                        NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                                mOBaseActivity.refreshView(GET_QUERY_FAIL, new HashMap<String, Object>());
                            }
                        }else {
                            MyTools.showToastShort(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_QUERY_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.showToastShort(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_QUERY_FAIL, new HashMap<String, Object>());
                    }

                }

            };
        }.start();
    }
    public final static String NETGETPAYREVERSE = TAG + ".netPayReverse";
    public final static String GET_REVERSE_FAIL = "get_reverse_fail";
    /**
     * 撤销订单
     * DoorNo 门店号
     * PayModeName 交易渠道0全部 1支付宝 2微信 3现金 4银行卡
     * auth_code 条码
     * dev_id 设备终端号
     * oper_id 操作员工号
     * amount 订单金额(单位元,保留2位小数)
     * raw_data 订单金额(单位元,保留2位小数)
     * down_trade_no 商户订单号
     * subject 订单标题
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netPayReverse(final boolean isDialogShow,
                            final String DoorNo,
                            final String PayModeName,
                            final String auth_code,
                            final String dev_id,
                            final String oper_id,
                            final String amount,
                            final String raw_data,
                            final String down_trade_no,
                            final String subject) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "订单撤销中,请稍后...").setCancelable(false);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("DoorNo", DoorNo);
                valueMap.put("PayModeName", PayModeName);
                valueMap.put("auth_code", auth_code);
                valueMap.put("dev_id", dev_id);
                valueMap.put("oper_id", oper_id);
                valueMap.put("amount", amount);
                valueMap.put("raw_data", raw_data);
                valueMap.put("down_trade_no", down_trade_no);
                valueMap.put("subject", subject);

                String[] status = mNetDataManager.post(SCPAYREVERSE, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (("2000").equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETGETPAYREVERSE, dataMap);
                        } else {
                            MyTools.showToastShort(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                           mOBaseActivity.refreshView(GET_REVERSE_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.showToastShort(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_REVERSE_FAIL, new HashMap<String, Object>());
                    }

                }

            };
        }.start();
    }
    public final static String NETISBLACKCARD = TAG + ".netIsBlackCard";

    /**
     * 是否为黑名单卡
     *
     * @param isDialogShow
     * @param cardNo  卡号
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netIsBlackCard(final boolean isDialogShow, final String cardNo) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "数据请求中...").setCancelable(true);
        } else {
            progressDialog = null;
        }
        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("BusCard", cardNo);
                String[] status = mNetDataManager.get(SCISBLACKCARD, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> data = (Map<String, Object>)map.get("Head");
                        Double codes = (Double) data.get("Code");
                        int code = codes.intValue();
                        if (code==200) {
                            mOBaseActivity.refreshView(NETISBLACKCARD, map);
                        } else {
                            MyTools.dialogIntro(true, ""+data.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_BLACK_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_BLACK_FAIL, new HashMap<String, Object>());
                    }
                }
            };
        }.start();
    }

    public final static String NETCOUPONLIST = TAG + ".netCouponList";

    /**
     * 查询优惠券列表
     *
     * @param isDialogShow
     * @param couponCode   优惠券
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netCouponList(final boolean isDialogShow, final String couponCode) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "数据加载中...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
//                valueMap.put("CouponCode", couponCode);
                valueMap.put("DevCode", getDevCode());
                String[] status = mNetDataManager.post(SCCOUPONLIST, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETCOUPONLIST, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETCOUPONLISTINFO = TAG + ".netCouponListInfo";

    /**
     * 查询优惠券列表详情
     *
     * @param isDialogShow
     * @param couponCode   优惠券名
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netCouponListInfo(final boolean isDialogShow, final String couponCode) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "数据加载中...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("CouponName", couponCode);
                valueMap.put("DevCode", getDevCode());
                String[] status = mNetDataManager.post(SCCOUPONLISTINFO, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETCOUPONLISTINFO, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }
                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETINSERTBIKEOUTRECSURE = TAG + ".netInsertBikeOutRecSure";
    /**
     * 自行车版正式出场
     *
     * @param isDialogShow
     * @param
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netInsertBikeOutRecSure(final boolean isDialogShow,
                                   final String SeqNo,
                                   final String Amount,//应收
                                   final String Fees,//实收
                                   final String PayType,
                                   final String Coupon,
                                   final String CouponName) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "提交中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("DevCode", getDevCode());
                valueMap.put("EmpName", getEmployeeName());
                valueMap.put("EmpNo", getUserNameId());
                valueMap.put("SeqNo", SeqNo);//流水号
                valueMap.put("Amount", Amount);//应收金额
                valueMap.put("Fees", Fees);//实收金额
                valueMap.put("PayType", PayType);//支付方式：4现金支付 5公交卡支付
                valueMap.put("Coupon", Coupon);
                valueMap.put("CouponName", CouponName);//优惠券名称
                String[] status = mNetDataManager.post(INSERTBIKEOUTRECSURE, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETINSERTBIKEOUTRECSURE, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }

                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }

    public final static String NETQUERYPARKINGREC = TAG + ".netQueryParkingRec";
    /**
     * 根据流水查询订单详情
     *
     * @param isDialogShow
     * @param
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("unchecked")
    public void netQueryParkingRec(final boolean isDialogShow,
                                        final String SeqNo) {
        final ProgressDialog progressDialog;
        if (isDialogShow) {
            progressDialog = new ProgressDialog(mOBaseActivity) {
                public boolean onKeyDown(int keyCode, KeyEvent event) {

                    return super.onKeyDown(keyCode, event);
                }
            };
            MyTools.getWaitDialog(progressDialog, "提交中，请稍候...").setCancelable(true);
        } else {
            progressDialog = null;
        }

        new Thread() {
            @Override
            public void run() {
                Map<String, Object> valueMap = new Hashtable<String, Object>();
                valueMap.put("DevCode", getDevCode());
                valueMap.put("SeqNo", SeqNo);//流水号
                String[] status = mNetDataManager.post(QUERYPARKINGREC, valueMap);
                Message msg = new Message();
                Map<String, Object> msgMap = new HashMap<String, Object>();
                msgMap.put("status", status);
                msg.obj = msgMap;
                handler.sendMessage(msg);
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    Map<String, Object> msgMap = (Map<String, Object>) msg.obj;
                    String[] status = (String[]) msgMap.get("status");
                    if (("" + Http.HTTP_OK).equals(status[0])) {
                        Map<String, Object> map = mGson.fromJson(status[1], Map.class);
                        Map<String, Object> headMap = (Map<String, Object>) map.get("Head");
                        String code = ("" + headMap.get("Code")).split("\\.")[0];
                        if (HEADCODE_OK.equals(code)) {
                            Map<String, Object> bodyMap = (Map<String, Object>) map.get("Body");
                            Map<String, Object> dataMap = (Map<String, Object>) bodyMap.get("Data");
                            mOBaseActivity.refreshView(NETQUERYPARKINGREC, dataMap);
                        } else {
                            MyTools.dialogIntro(isDialogShow,
                                    NetDataManager.isCodeShow ? "" + headMap.get("ErrMsg") + "\n编号：" + code : "" + headMap.get("ErrMsg"), mOBaseActivity);
                            mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                        }

                    } else {
                        MyTools.dialogIntro(isDialogShow, status[1], mOBaseActivity);
                        mOBaseActivity.refreshView(GET_FAIL, new HashMap<String, Object>());
                    }
                }

            };
        }.start();
    }


    //本地数据
    public void saveUserInfo(Map<String, Object> dataMap) {
        mOSharedPreferencesHelper.putString(Field.SAVE_USER_INFO, mGson.toJson(dataMap));
    }

    public Map<String, Object> getUserInfo() {
        return mGson.fromJson(mOSharedPreferencesHelper.getString(Field.SAVE_USER_INFO, "{}"), Map.class);
    }

    public Map<String, Object> getEmployeeInfo() {
        return getUserInfo();
    }

    public Map<String, Object> getCompanyInfo() {
        return (Map<String, Object>) getUserInfo().get("Company");
    }

    public Map<String, Object> getDeviceInfo() {
        return (Map<String, Object>) getUserInfo().get("Device");
    }

    public String getEmployeeName() {
        return "" + getEmployeeInfo().get("EmpName");
    }

    public String getEmployeeId() {
        return ("" + getEmployeeInfo().get("EmpId")).split("\\.")[0];
    }

    public String getCompanyId() {
        return ("" + getCompanyInfo().get("Id")).split("\\.")[0];
    }

    public String getDeviceId() {
        return ("" + getDeviceInfo().get("DeviceId")).split("\\.")[0];
    }

    public String getParkId() {
        return ("" + getDeviceInfo().get("ParkId")).split("\\.")[0];
    }

    public String getParkName() {
        return mOSharedPreferencesHelper.getString(Field.PARKNAME, "");
    }

    public void saveParkName(String parkName) {
        mOSharedPreferencesHelper.putString(Field.PARKNAME, parkName);
    }

    public String getCompanyName() {
        return "" + getCompanyInfo().get("Name");
    }


    //保存登录名提示
    public void savePromptUserName(String name) {
        List<String> list = getPromptUserName();
        boolean boo = false;
        for (String value : list) {
            if (name.equals(value)) {
                boo = true;
                break;
            }
        }
        if (!boo) {
            list.add(name);
        }
        mOSharedPreferencesHelper.putString(Field.PROMPT_USER_NAME, mGson.toJson(list));
    }

    public List<String> getPromptUserName() {
        return mGson.fromJson(mOSharedPreferencesHelper.getString(Field.PROMPT_USER_NAME, "[]"), List.class);
    }

    public void saveDevCode(String code) {
        mOSharedPreferencesHelper.putString(Field.DEV_CODE, code);
    }

    public String getDevCode() {
        return mOSharedPreferencesHelper.getString(Field.DEV_CODE, "");
    }

    public void saveCompnayCode(String code) {
        mOSharedPreferencesHelper.putString(Field.COMPNAY_CODE, code);
    }

    public String getCompnayCode() {
        return mOSharedPreferencesHelper.getString(Field.COMPNAY_CODE, "");
    }

    public void saveUserName(String username) {
        mOSharedPreferencesHelper.putString(Field.USER_NAME, username);
    }

    public String getUserName() {
        return mOSharedPreferencesHelper.getString(Field.USER_NAME, "");
    }

    public void saveUserNameId(String user_name_id) {
        mOSharedPreferencesHelper.putString(Field.USER_NAME_ID, user_name_id);
    }

    public String getUserNameId() {
        return mOSharedPreferencesHelper.getString(Field.USER_NAME_ID, "");
    }

    //密码
    public void savePwd(String pwd) {
        mOSharedPreferencesHelper.putString(Field.PWD, pwd);
    }

    public String getPwd() {
        return mOSharedPreferencesHelper.getString(Field.PWD, "");
    }

    //路政
    public void saveLuzheng(String code) {
        mOSharedPreferencesHelper.putString(Field.LUZHENG, code);
    }

    public String getLuZheng() {
        return mOSharedPreferencesHelper.getString(Field.LUZHENG, "");
    }

    //市中
    public void saveShizhong(String code) {
        mOSharedPreferencesHelper.putString(Field.SHIZHONG, code);
    }

    public String getShiZhong() {
        return mOSharedPreferencesHelper.getString(Field.SHIZHONG, "");
    }

    //进出场车牌识别
    public void savePlateRecognize(String code) {
        mOSharedPreferencesHelper.putString(Field.PLATERECOGNIZE, code);
    }

    public String getPlateRecognize() {
        return mOSharedPreferencesHelper.getString(Field.PLATERECOGNIZE, "1");
    }

    //局域网连接
    public void saveWebService(String code) {
        mOSharedPreferencesHelper.putString(Field.WEBSERVICE, code);
    }

    public String getWebService() {
        return mOSharedPreferencesHelper.getString(Field.WEBSERVICE, "");
    }

    //局域网地址
    public void saveLocalAreaId(String psam) {
        mOSharedPreferencesHelper.putString(Field.LOCALAREA, psam);
    }

    public String getLocalAreaId() {
        return mOSharedPreferencesHelper.getString(Field.LOCALAREA, "http://122.112.82.212:8081/api/LANAndroid/");
    }

    //车牌再次识别后离场
    public void saveRecognizeAgain(String code) {
        mOSharedPreferencesHelper.putString(Field.RECOGNIZEAGAIN, code);
    }

    public String getRecognizeAgain() {
        return mOSharedPreferencesHelper.getString(Field.RECOGNIZEAGAIN, "");
    }

    //蓝牙开启
    public void saveBlueToothOpen(String code) {
        mOSharedPreferencesHelper.putString(Field.BLUETOOTHOPEN, code);
    }

    public String getBlueToothOpen() {
        return mOSharedPreferencesHelper.getString(Field.BLUETOOTHOPEN, "");
    }

    //进场打印
    public void saveEnterPrint(String code) {
        mOSharedPreferencesHelper.putString(Field.ENTERPRINT, code);
    }

    public String getEnterPrint() {
        return mOSharedPreferencesHelper.getString(Field.ENTERPRINT, "");
    }

    //出场打印
    public void saveExitPrint(String code) {
        mOSharedPreferencesHelper.putString(Field.EXITPARKPRINT, code);
    }

    public String getExitPrint() {
        return mOSharedPreferencesHelper.getString(Field.EXITPARKPRINT, "");
    }

    //图片上传
    public void saveCardBitmap(String code) {
        mOSharedPreferencesHelper.putString(Field.CARDBITMAP, code);
    }

    public String getCardBitmap() {
        return mOSharedPreferencesHelper.getString(Field.CARDBITMAP, "");
    }

    //蓝牙mac地址
    public void saveIntentMac(String bt_mac) {
        mOSharedPreferencesHelper.putString(Field.BLUETOOTHMAC, bt_mac);
    }

    public String getIntentMac() {
        return mOSharedPreferencesHelper.getString(Field.BLUETOOTHMAC, "");
    }

    //打印内容抬头
    public void saveIntentContent(String content) {
        mOSharedPreferencesHelper.putString(Field.PRINTTITLE, content);
    }

    public String getIntentContent() {
        return mOSharedPreferencesHelper.getString(Field.PRINTTITLE, "");
    }

    //Psam
    public void saveIntentPsam(String psam) {
        mOSharedPreferencesHelper.putString(Field.PSAM, psam);
    }

    public String getIntentsaveIntentPsam() {
        return mOSharedPreferencesHelper.getString(Field.PSAM, "");
    }

    //运营商户id
    public void saveIntentcorpId(String psam) {
        mOSharedPreferencesHelper.putString(Field.CORPID, psam);
    }

    public String getIntentcorpId() {
        return mOSharedPreferencesHelper.getString(Field.CORPID, "");
    }

    //设置密码
    public void saveIntentSessingPwd(String sessingpwd) {
        mOSharedPreferencesHelper.putString(Field.SESSINGPWD, sessingpwd);
    }

    public String getIntentSessingPwd() {
        return mOSharedPreferencesHelper.getString(Field.SESSINGPWD, "");
    }

    //退出密码
    public void saveIntentBreakPwd(String sessingpwd) {
        mOSharedPreferencesHelper.putString(Field.BREAKPWD, sessingpwd);
    }

    public String getIntentBreakPwd() {
        return mOSharedPreferencesHelper.getString(Field.BREAKPWD, "");
    }

    //系统参数密码
    public void saveIntentSysParmPwd(String syspwd) {
        mOSharedPreferencesHelper.putString(Field.SYSTEMPWD, syspwd);
    }

    public String getIntentSysParmPwd() {
        return mOSharedPreferencesHelper.getString(Field.SYSTEMPWD, "");
    }

    //二维头开关
    public void saveIntentTwoDimensionalScan(String sessingpwd) {
        mOSharedPreferencesHelper.putString(Field.BREAKPWD, sessingpwd);
    }

    public String getIntentTwoDimensionalScan() {
        return mOSharedPreferencesHelper.getString(Field.BREAKPWD, "");
    }

    //打印尾部公司名称
    public void saveIntentEndCo(String coname) {
        mOSharedPreferencesHelper.putString(Field.PRINTENDCO, coname);
    }

    public String getIntentEndCo() {
        return mOSharedPreferencesHelper.getString(Field.PRINTENDCO, "");
    }

    //打印尾部公司电话
    public void saveIntentEndTel(String tel) {
        mOSharedPreferencesHelper.putString(Field.PRINTENDTel, tel);
    }

    public String getIntentEndTel() {
        return mOSharedPreferencesHelper.getString(Field.PRINTENDTel, "");
    }

    //常用车牌号首字设置
    public void saveIntentCommonLicence(String commonLicence) {
        mOSharedPreferencesHelper.putString(Field.COMMONLIENCE, commonLicence);
    }

    public String getIntentCommonLicence() {
        return mOSharedPreferencesHelper.getString(Field.COMMONLIENCE, "沪京粤苏");
    }

    //巡查显示时间，费用设置
    public void saveIntentCheckCar(String data) {
        mOSharedPreferencesHelper.putString(Field.CHECKCARSET, data);
    }

    public String getIntentCheckCar() {
        return mOSharedPreferencesHelper.getString(Field.CHECKCARSET, "0");
    }

    //数据统计模块显示
    public void saveIntentStatisticShow(String data) {
        mOSharedPreferencesHelper.putString(Field.STATISTICSHOW, data);
    }

    public String getIntentStatisticShow() {
        return mOSharedPreferencesHelper.getString(Field.STATISTICSHOW, "0");
    }

    //交班打印详细信息
    public void saveIntentExchangePrintDet(String data) {
        mOSharedPreferencesHelper.putString(Field.EXCHANGEPRINTDET, data);
    }

    public String getntentExchangePrintDet() {
        return mOSharedPreferencesHelper.getString(Field.EXCHANGEPRINTDET, "0");
    }

    //选择系统模式
    public void saveIntentSystemModel(String data) {
        mOSharedPreferencesHelper.putString(Field.SYSTEMMODEL, data);
    }

    public String getIntentSystemModel() {
        return mOSharedPreferencesHelper.getString(Field.SYSTEMMODEL, "0");
    }

    //是否是签到状态
    public void saveIsSign(Boolean data) {
        mOSharedPreferencesHelper.putBoolean(Field.ISSIGN, data);
    }

    public boolean getIsSign() {
        return mOSharedPreferencesHelper.getBoolean(Field.ISSIGN, false);
    }
    //设置停车场编号
    public void saveParkCode(String code) {
        mOSharedPreferencesHelper.putString(Field.PARK_CODE, code);
    }

    public String getParkCode() {
        return mOSharedPreferencesHelper.getString(Field.PARK_CODE, "");
    }
    //设置版本是否自动提示更新
    public void saveVersionUpdate(String code) {
        mOSharedPreferencesHelper.putString(Field.VERSION_UPDATE, code);
    }

    public String getVersionUpdate() {
        return mOSharedPreferencesHelper.getString(Field.VERSION_UPDATE, "0");
    }
    //设置电子支付接口
    public void saveElectPayIP(String code) {
        mOSharedPreferencesHelper.putString(Field.ELECT_PAY_IP, code);
    }

    public String getElectPayIP() {
        return mOSharedPreferencesHelper.getString(Field.ELECT_PAY_IP, "http://122.112.82.212:8081/");
    }
    //设置商户门店号
    public void saveStoreNo(String code) {
        mOSharedPreferencesHelper.putString(Field.STORENO, code);
    }

    public String getStoreNo() {
        return mOSharedPreferencesHelper.getString(Field.STORENO, "");
    }
    //设置商户门店号
    public void saveAppUrl(String code) {
        mOSharedPreferencesHelper.putString(Field.APPURL, code);
    }

    public String getAppUrl() {
        return mOSharedPreferencesHelper.getString(Field.APPURL, "http://139.196.175.54:8081/");
    }
    //车辆重复进场设置
    public void saveAllowEnterAgain(String data) {
        mOSharedPreferencesHelper.putString(Field.ALLOWENTERAGAIN, data);
    }

    public String getAllowEnterAgain() {
        return mOSharedPreferencesHelper.getString(Field.ALLOWENTERAGAIN, "0");
    }

    //是否开启晨鸟设置
    public void saveOpenChenNiao(String data) {
        mOSharedPreferencesHelper.putString(Field.OPENCHENNIAO, data);
    }

    public String getOpenChenNiao() {
        return mOSharedPreferencesHelper.getString(Field.OPENCHENNIAO, "0");
    }

    //优惠券种类显示设置
    public void saveShowCouponType(String data) {
        mOSharedPreferencesHelper.putString(Field.SHOWCOUPONTYPE, data);
    }

    public String getShowCouponType() {
        return mOSharedPreferencesHelper.getString(Field.SHOWCOUPONTYPE, "0");
    }

    //打印进场标题设置（标准版）
    public void saveEnterTitle(String date) {
        mOSharedPreferencesHelper.putString(Field.ENTERTITLE, date);
    }

    public String getEnterTitle() {
        return mOSharedPreferencesHelper.getString(Field.ENTERTITLE, "入场凭证");
    }
    //打印出场标题设置（标准版）
    public void saveExitTitle(String date) {
        mOSharedPreferencesHelper.putString(Field.EXITTITLE, date);
    }

    public String getExitTitle() {
        return mOSharedPreferencesHelper.getString(Field.EXITTITLE, "结算凭证");
    }

    //打印进场标题设置（自行车版）
    public void saveEnterBikeTitle(String date) {
        mOSharedPreferencesHelper.putString(Field.ENTERBIKETITLE, date);
    }

    public String getEnterBikeTitle() {
        return mOSharedPreferencesHelper.getString(Field.ENTERBIKETITLE, "租车凭证");
    }
    //打印出场标题设置（自行车版）
    public void saveExitBikeTitle(String date) {
        mOSharedPreferencesHelper.putString(Field.EXITBIKETITLE, date);
    }

    public String getExitBikeTitle() {
        return mOSharedPreferencesHelper.getString(Field.EXITBIKETITLE, "结算凭证");
    }

    //设置图片保留张数
    public void savePicNum(String code) {
        mOSharedPreferencesHelper.putString(Field.SAVEPICNUM, code);
    }

    public String getPicNum() {
        return mOSharedPreferencesHelper.getString(Field.SAVEPICNUM, "0");
    }
    //设置图片保留天数
    public void savePicDay(String code) {
        mOSharedPreferencesHelper.putString(Field.SAVEPICDAY, code);
    }

    public String getPicDay() {
        return mOSharedPreferencesHelper.getString(Field.SAVEPICDAY, "0");
    }
}
