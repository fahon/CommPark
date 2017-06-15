package willsong.cn.commpark.activity.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import willsong.cn.commpark.activity.Bean.AlarmState;
import willsong.cn.commpark.activity.Bean.ErrorCodeList;
import willsong.cn.commpark.activity.Bean.FunctionCode;
import willsong.cn.commpark.activity.Bean.ParkType;

/**
 * Created by Administrator on 2016/10/14 0014.
 */

public class shizhong {

    private String TAG = "===Client===";
    private String TAG1 = "===Send===";
    private Context ctx;

    Handler mhandler;
    Handler mhandlerSend;

    Socket socketThread;

    static Socket socket;
    static byte[] bs;
    static byte[] recvBytes = new byte[256];
    static Thread threadReceiveSocket = null;

    public static boolean InitSocket()
    {
        try
        {
            threadReceiveSocket = new Thread(new Runnable() {
                @Override
                public void run() {
                    ReceiveSocket();
                }
            });
            //threadReceiveSocket.IsBackground = true;
            threadReceiveSocket.setPriority(10);
            threadReceiveSocket.setName("ReceiveSocket");
            threadReceiveSocket.start();
//            threadReceiveSocket.Priority = ThreadPriority.Highest;
//            threadReceiveSocket.Name = "ReceiveSocket";
//            threadReceiveSocket.Start();
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    static int resultlength = -1;
    static void ReceiveSocket()
    {
    //    while (true)
        {
            try
            {
                resultlength = socket.getInputStream().read(recvBytes,0,recvBytes.length);
                if (resultlength > 0)
                    Client_Received(recvBytes);
            }
            catch(Exception e) { }
        }
    }
    static byte[] m_TotalBuffer = new byte[0];
    static String a10To16(int s)
    {
        String ss = Integer.toHexString(s & 0xFF).toUpperCase();
        if (ss.length() == 1){
            return "0" + ss;
        } else{
            return ss;
        }
    }
    static byte[] GetHexToChs(String hex, byte[] bytes)
    {
        bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++)
        {
            try
            {
                // 每两个字符是一个 byte。
                bytes[i] = (byte)(0xff & Integer.parseInt(hex.substring(i*2, i*2+2),16));;
            }
            catch(Exception e) { }
        };
        return bytes;
    }
    static String DisGetHexToChs(String hex, byte[] bytes)
    {
        hex = "";
        for (int i = 0; i < bytes.length; i++)
        {
            try
            {
                // 每两个字符是一个 byte。
                hex += a10To16(bytes[i]);
            }
            catch(Exception e) { }
        };
        return hex;
    }
    static Hashtable<Integer,SendFrameInfo> m_WaitForSend = new Hashtable();
    static void Client_Received(byte[] buffer)
    {
        try
        {
//            m_TotalBuffer = GetHexToChs(System.Text.Encoding.ASCII.GetString(buffer, 0, buffer.length), null);
            m_TotalBuffer = GetHexToChs(StringToAscii(bytes2Hex(buffer)),null);
            int stxPos = 0;
            if (m_TotalBuffer.length > 0)
            {
                int nLen = CAckFrame.FrameLength();
                CAckFrame ack = CAckFrame.CreateFromBytes(m_TotalBuffer,stxPos);
                if (ack != null)
                {
                    short id = ack.getM_Id();
                    if (m_WaitForSend.containsKey(id))
                    {
                        if (ack.getM_ErrorCode() == ErrorCodeList.None)
                        {
                            RemoveFrame(ack.getM_Id());
                        }
                        else
                        {
                            RePostFrame(ack.getM_Id());
                        }
                    }
                }
                else
                {
                    CTimeFrame tt = CTimeFrame.CreateFromBytes(m_TotalBuffer,stxPos);
                    if (tt != null)
                    {
                        nLen = CTimeFrame.FrameLength();
                        // 修改本机时间   tt.CalibrationTime;
                        //SystemTimeHelp.SetSysTime(tt.CalibrationTime);
                        Runtime.getRuntime().exec(tt.getM_CalibrationTime().toString());
                        //Helper.Sleep(10000);
                        Thread.sleep(10000);
                        TimeData(tt.getM_CalibrationTime());

                    }
                }
                switch (CAckFrame.rst)
                {
                    case -3:
                    case -4:
                    case 0:
                        //m_TotalBuffer = JYArray.Subtraction(m_TotalBuffer, stxPos + nLen /* CAckFrame.Framelength */, false);
                        break;
                    case -1:
                    case -2:
                    default:
                        //if (m_TotalBuffer.length > (10 * CAckFrame.Framelength))
                        //    m_TotalBuffer = JYArray.Subtraction(m_TotalBuffer, 10 * CAckFrame.Framelength, false);
                        break;
                }
            }
        }
        catch (Exception ex)
        {
            //FileLogger.WriteLog("", "client received:" + ex.Message);
        }
    }
    //发送socket
    static boolean SendSocket(byte[] BytesArr)//, out byte[] _recvBytes)
    {
        //_recvBytes = null;
        try
        {
            if (InitSocket())
            {
                if (socket == null){
                    socket = new Socket("222.73.176.27",9999);
                }
                bs = BytesArr;
                //socket.SendTimeout = 2000;
//                socket.Send(bs, bs.length, 0);

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(bs,0,bs.length);
                //socket.ReceiveTimeout = 2000;
                //socket.Receive(recvBytes, recvBytes.length, 0);//从服务器端接受返回信息
                //_recvBytes = recvBytes;
                //socket.Close();
                return true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
    public static void ThreadTask()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                m_Thread_Task();
            }
        });
        thread.setPriority(10);//最高的优先级别
        thread.setName("m_Thread_Task");
        thread.start();
    }
    //发送socket
    public static void m_Thread_Task()
    {
       // while (true)
        {
            if (m_WaitForSend.size() > 0)
            {
                try
                {
                    synchronized (m_WaitForSend)
                    {
                        for (SendFrameInfo sendFrame:m_WaitForSend.values())
                        {
                            if (!sendFrame.IsSend) {
                                byte[] tmp = sendFrame.Frame.ToBytes();
                                if (tmp != null) {
                                    //String strFrame = DisGetHexToChs("", tmp).Replace(" ", "");
                                    //if(sendFrame.Frame.Function==FunctionCode.Leave)
                                    //PDA.RJ_HandSetPDA.Model.CloudHelper.Write("send frame strFrame" + strFrame);
                                    //byte[] bzSend = System.Text.Encoding.ASCII.GetBytes(strFrame);
                                    SendSocket(DisGetHexToChs("", tmp).getBytes());
                                    SendFrame(sendFrame.Id);
                                }
                                else
                                    RemoveFrame(sendFrame.Id);
                                break;
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                    //FileLogger.WriteLog("", "send frame:" + ex.Message);
                }
                try
                {
                    synchronized (m_WaitForSend)
                    {
                        for (SendFrameInfo sendFrame:m_WaitForSend.values()) {
                            if(sendFrame.IsSend){
                                Date ts = getDateTime(new Date(),sendFrame.SendTime);
                                if (ts.getTime() > getM_ResendTimeout())
                                {
                                    RePostFrame(sendFrame.Id);
                                    break;
                                }
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                    //FileLogger.WriteLog("", "check timeout:" + ex.Message);
                }
            }
        }
    }
    private static int m_ResendTimeout = 20;
    /// <summary>
    /// 超时重发时长，单位：S
    /// </summary>

    public static int getM_ResendTimeout() {
        return m_ResendTimeout;
    }

    public static void setM_ResendTimeout(int m_ResendTimeout) {
        shizhong.m_ResendTimeout = m_ResendTimeout;
    }

    private static volatile short SInvokeId = 0;

    public static short getSInvokeId() {
        if (SInvokeId >= 0xFFFF)
            SInvokeId = 0;
        short ret = SInvokeId;
        SInvokeId++;
        return ret;
    }

    //转16进制
    public static class SendFrameInfo {
        public short Id;
        public CBaseFrame Frame;
        public Date  SendTime;
        public int SendTimes;
        public Boolean IsSend;
    }
    static void RemoveFrame(short id)
    {
        synchronized (m_WaitForSend){
            if(m_WaitForSend.containsKey(id))
                m_WaitForSend.remove(id);
        }
    }
    static void RePostFrame(short id)
    {
            synchronized (m_WaitForSend){
                if (m_WaitForSend.containsKey(id))
                {
                    SendFrameInfo frame = (SendFrameInfo)m_WaitForSend.get(id);
                    if (frame.Frame.getM_Fun() == FunctionCode.Park)
                        m_WaitForSend.remove(id);
                    else
                    {
                        frame.IsSend = false;
                        frame.SendTime = new Date();
                        frame.SendTimes++;
                        m_WaitForSend.put((int)id,frame);
                    }
                }
            }
        }

    static void SendFrame(short id)
    {
        synchronized (m_WaitForSend){
            if (m_WaitForSend.containsKey(id)) {
                SendFrameInfo frame = m_WaitForSend.get(id);
                frame.IsSend = true;
                m_WaitForSend.put((int)id,frame);
            }
        }
    }
    static void PostFrame(CBaseFrame frame) {
        SendFrameInfo item = new SendFrameInfo();
        item.Id = frame.getM_Id();
        item.Frame = frame;
        item.SendTime = new Date();
        item.IsSend = false;
        item.SendTimes = 0;
        synchronized (m_WaitForSend){
            if (m_WaitForSend.containsKey(item.Id))
                m_WaitForSend.put((int)item.Id,item);
            else
                m_WaitForSend.put((int)frame.getM_Id(), item);
        }
    }
        public static void PostTimeFrame(short companyCode, short addressCode, int receiverAddressCode, String et)
        {
            CTimeFrame frame = new CTimeFrame(et);
            frame.CreateSenderAddress(companyCode, addressCode);
            frame.CreateReceiverAddress(receiverAddressCode);
            frame.setM_Id(getSInvokeId());
            PostFrame(frame);
        }
        static void TimeData(String NowTime)
        {
            try
            {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                PostTimeFrame(Short.parseShort(Helper.CODE)
                        , Short.parseShort(Helper.SHIZHONGCODE)
                        , (int)Short.parseShort(Helper.RADDRESS), NowTime);
            }
            catch(Exception e)
            {
            }
        }
//      static void TimeData(String NowTime)
//        {
//            try
//            {
//                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                PostTimeFrame(Short.parseShort(Helper.CODE)
//                        , Short.parseShort(Helper.SHIZHONGCODE)
//                        , (int)Short.parseShort(Helper.RADDRESS), sf.parse(NowTime.trim()));
//            }
//            catch(Exception ex)
//            {
//            }
//    }

    /**
     * 计算时间差
     * @param d1
     * @param d2
     * @return
     */
    public static Date getDateTime(Date d1,Date d2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long diff = d1.getTime() - d2.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            return new Date(days);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * byte类型转换成String
     */
    public static String bytes2Hex(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] res = new char[src.length * 2]; // 每个byte对应两个字符
        final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >> 4 & 0x0f]; // 先存byte的高4位
            res[j++] = hexDigits[src[i] & 0x0f]; // 再存byte的低4位
        }

        return new String(res);
    }

    /**
     * 字符串装换成ASCLL
     * @param value
     * @return
     */
    public static String StringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }

    /**
     * 进场
     */
    static void PostEntryFrame(short companyCode, short addressCode,
                               int receiverAddressCode, String et, byte pt,
                               short remainderTotal, short remainderByMonth,
                               short remainderVister, String licPlate)
    {
        CEntryFrame frame = new CEntryFrame(et, pt, remainderTotal, remainderByMonth, remainderVister, licPlate);
        frame.CreateSenderAddress(companyCode, addressCode);
        frame.CreateReceiverAddress(receiverAddressCode);
        frame.setM_Id(getSInvokeId());
        PostFrame(frame);
    }

    public static void InData(String InDateTime, String CarNo, int InType)
    {
        try
        {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            PostEntryFrame(Short.parseShort(Helper.CODE), Short.parseShort(Helper.SHIZHONGCODE)
                    , (int)Short.parseShort(Helper.RADDRESS), sf.format(new Date())
                    , ParkType.Free, (short)100
                    , (short)10, (short)90
                    , "沪A 66666");
        }
        catch(Exception ex)
        {

        }
    }

    /**
     * 出场
     * @param companyCode
     * @param addressCode
     * @param receiverAddressCode
     * @param et 离场时间
     * @param pt 离场类别
     * @param remainderTotal  总剩余车位
     * @param remainderByMonth 月租剩余车位
     * @param remainderVister  时租房客剩余车位
     * @param licPlate   离场车牌
     * @param duration   停车时长
     * @param fee        收费金额
     * @param pay        支付类型
     */
    static void PostLeaveFrame(short companyCode, short addressCode,
                               int receiverAddressCode, String et, byte pt,
                               short remainderTotal, short remainderByMonth,
                               short remainderVister, String licPlate,
                               int duration, int fee, byte pay)
    {
        CLeaveFrame frame = new CLeaveFrame(et, pt, remainderTotal, remainderByMonth, remainderVister, licPlate, duration, fee, pay);
        frame.CreateSenderAddress(companyCode, addressCode);
        frame.CreateReceiverAddress(receiverAddressCode);
        frame.setM_Id(getSInvokeId());
        PostFrame(frame);
    }
    /// <summary>
    /// OutData
    /// </summary>
    /// <param name="OutDateTime"></param>
    /// <param name="OutType">0月,1时,2免费,3未知</param>
    /// <param name="CarNo"></param>
    /// <param name="StopTimeSecs"></param>
    /// <param name="feePayAmount">金额 分</param>
    /// <param name="PayType">0现金,1交通,2银行,3手机</param>
    public static void OutData(String OutDateTime, int OutType, String CarNo, String StopTimeSecs, String feePayAmount, byte PayType)
    {
        try
        {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            PostLeaveFrame(Short.parseShort(Helper.CODE)
                    , Short.parseShort(Helper.SHIZHONGCODE)
                    , (int)Short.parseShort(Helper.RADDRESS)
                    , sf.format(new Date())
                    , ParkType.Free, (short)100
                    , (short)10, (short)90
                    , "沪A 66666"
                    , Integer.parseInt("86400")
                    , Integer.parseInt("86400")
                    , PayType);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 车位
     * @param companyCode
     * @param addressCode
     * @param receiverAddressCode
     * @param tCnt   总车位
     * @param tMonth 月租长包车位
     * @param tVister 时租访客车位
     * @param rCnt   总剩余车位
     * @param rMonth 月租剩余车位
     * @param rVister 时租剩余车位
     */
    static void PostParkFrame(short companyCode, short addressCode, int receiverAddressCode, short tCnt, short tMonth, short tVister, short rCnt, short rMonth, short rVister)
    {
        CParkingFrame frame = new CParkingFrame(tCnt, tMonth, tVister, rCnt, rMonth, rVister);
        frame.CreateSenderAddress(companyCode, addressCode);
        frame.CreateReceiverAddress(receiverAddressCode);
        frame.setM_Id(getSInvokeId());
        PostFrame(frame);
    }
//    public static void ThreadSendCarPort()
//    {
//        Thread thread = new Thread(SendCarPort);
//        //thread.IsBackground = true;
//        thread.Priority = ThreadPriority.Highest;
//        thread.Name = "SendCarPort";
//        thread.Start();
//    }
    public static void SendCarPort(int TotalCount,
                            int TotalByMonth,
                            int TotalVister,
                            int RemainderCount,
                            int RemainderByMonth,
                            int RemainderVister
                            )
    {
        try
        {
            PostParkFrame(Short.parseShort(Helper.CODE)
                    , Short.parseShort(Helper.SHIZHONGCODE)
                    , Short.parseShort(Helper.RADDRESS)
                    , (short) TotalCount
                    , (short) TotalByMonth
                    , (short) TotalVister
                    , (short) RemainderCount
                    , (short) RemainderByMonth
                    , (short) RemainderVister);
        }
        catch(Exception ex) { }
    }

    /**
     *
     * @param companyCode
     * @param addressCode
     * @param receiverAddressCode
     * @param work 工作状态
     * @param alarm 报警状态
     */
    public static void PostStatusFrame(short companyCode, short addressCode, int receiverAddressCode, byte work, short alarm)
    {
        CStatusFrame frame = new CStatusFrame(work, alarm);
        frame.CreateSenderAddress(companyCode, addressCode);
        frame.CreateReceiverAddress(receiverAddressCode);
        frame.setM_Id(getSInvokeId());
        PostFrame(frame);
    }
    public static void StateData(byte textBox1, String textBox2)
    {
        String[] s = null;
        try
        {
            if (textBox2.trim() != "")
            {
                s = textBox2.split(",");
            }
        }
        catch(Exception e) { }
        try
        {
            PostStatusFrame(Short.parseShort(Helper.CODE), Short.parseShort(Helper.SHIZHONGCODE)
                    , (int)Short.parseShort(Helper.RADDRESS), textBox1, GetCheckedAlarmState(s));
        }
        catch(Exception e) { }
    }
    static short GetCheckedAlarmState(String[] AlarmStateCheckedItems)
    {
        short ret = AlarmState.None;
        short backupPower = AlarmState.BackupPower;
        if (AlarmStateCheckedItems != null)
        {
            for (String strItem:AlarmStateCheckedItems
                 ) {
                Log.i("ccm",strItem);
                if(strItem.getBytes() == short2Byte(AlarmState.BackupPower)){
                    return ret;
                }
//                if(Short.parseShort(strItem) == AlarmState.None){
//                    return ret = AlarmState.None;
//                }else {
//                    return ret = ret;
//                }
            }
        }
        return ret;
    }

    /**
     * 将short转换成byte类型
     */
    public static byte[] short2Byte(short a){
        byte[] b = new byte[2];

        b[0] = (byte) (a >> 8);
        b[1] = (byte) (a);

        return b;
    }
}
