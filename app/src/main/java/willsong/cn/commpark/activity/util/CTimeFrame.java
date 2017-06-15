package willsong.cn.commpark.activity.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import willsong.cn.commpark.activity.Bean.FunctionCode;
import willsong.cn.commpark.activity.Bean.StreamDirection;

import static java.lang.System.arraycopy;

/**
 * Created by Administrator on 2016/10/11 0011.
 */

public class CTimeFrame extends CBaseFrame {
    public static int rst;

    public CTimeFrame() {

    }

    public CTimeFrame(String dt)
    {
        CreateControlCode(StreamDirection.Upload, FunctionCode.Time);
        m_CalibrationTime = dt;
    }

    public String getM_CalibrationTime() {
        return m_CalibrationTime;
    }

    public void setM_CalibrationTime(String m_CalibrationTime) {
        this.m_CalibrationTime = m_CalibrationTime;
    }

    private String m_CalibrationTime;


    public static int FrameLength()
    {
         return 2 + 1 + 2 + 4 + 4 + 1 + 2 + 1 + 6;
    }

    @Override
    protected void SetData()
    {
        try{
            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            setM_Data(ConvertTimeToBytes(sf.parse(m_CalibrationTime)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static CTimeFrame CreateFromBytes(byte[] src,int stxPos)
    {
        if (src == null || src.length < FrameLength()) { rst = -1; return null; }
            stxPos = -1;
        for (int i = 0; i < src.length - 1; i++)
        {
//            byte[] bzSTX = TrunDESCbyte(ToBytes((short) STX), null);
//            if (bzSTX[i] == src[i] && bzSTX[i+1] == src[i+1])
//            { stxPos = i; break; }

            byte[] bzSTX = TrunDESCbyte(ToBytes(getSTX()));
            if (bzSTX[i] == src[i] && bzSTX[i + 1] == src[i + 1])
            { stxPos = i; break; }
        }
        if (stxPos == -1 || src.length < stxPos + FrameLength()) { rst = -2; return null; }

//        if (src[stxPos + FrameLength() - 1] != ETX) { rst = -3; return null; }

        int crc = FromBytes(TrunDESCbyte(new byte[] { src[stxPos + FrameLength() - 3], src[stxPos + FrameLength() - 2] }, null));
        byte[] bzData = new byte[FrameLength() - 3];
        arraycopy(src, stxPos, bzData, 0, FrameLength() - 3);
        if (CRC16Check(bzData, CRC_SEED, CRC_POLY) != crc) { rst = -4; return null; }
            rst = 0;
        byte[] bzId = new byte[2]; arraycopy(src, stxPos + 2 + 1, bzId, 0, 2);
        byte[] bzSAddrComp = new byte[2]; arraycopy(src, stxPos + 2 + 1 + 2, bzSAddrComp, 0, 2);
        byte[] bzSAddrCode = new byte[2]; arraycopy(src, stxPos + 2 + 1 + 2 + 2, bzSAddrCode, 0, 2);
        byte[] bzRAddress = new byte[4]; arraycopy(src, stxPos + 2 + 1 + 2 + 4, bzRAddress, 0, 4);
        byte bzCtl = src[stxPos + 2 + 1 + 2 + 4 + 4];
        byte[] bzDatas = new byte[6]; arraycopy(src, stxPos + 2 + 1 + 2 + 4 + 4 + 1, bzDatas, 0, 6);
        byte[] bzCrc = new byte[2]; arraycopy(src, stxPos + 2 + 1 + 2 + 4 + 4 + 1 + 2, bzCrc, 0, 2);
        CTimeFrame ret = new CTimeFrame();
        short usId, usComp, usAddrCode, usCrc;
        int unRAddr;
        usId = FromBytes(TrunDESCbyte( bzId, null));
        ret.setM_Id(usId);
        usComp = FromBytes(TrunDESCbyte(bzSAddrComp, null));
        usAddrCode = FromBytes(TrunDESCbyte(bzSAddrCode, null));
        ret.CreateSenderAddress(usComp,usAddrCode);
        unRAddr = FromBytes(TrunDESCbyte(bzRAddress, null));
        ret.CreateReceiverAddress(unRAddr);
        byte dir = (byte)(bzCtl >> 7);
        byte fun = (byte)(bzCtl & 0x1F);
        ret.CreateControlCode(dir, fun);
        ret.setM_Data(bzDatas);
        usCrc = FromBytes(TrunDESCbyte(bzCrc, null));
        ret.setM_CRC(usCrc);
        ret.setM_CalibrationTime(ConvertBytesToDate(ret.getM_Data()));
        return ret;
    }

    public static int[] getTime(byte[] req){
        int[] time = new int[6];
        byte[] bzDatas = new byte[6];
        arraycopy(req, 2 + 1 + 2 + 4 + 4 + 1, bzDatas, 0, 6);
        int year = bzDatas[0] + 2000;
        int month = bzDatas[1];
        int day = bzDatas[2];
        int hour = bzDatas[3];
        int minute = bzDatas[4];
        int second = bzDatas[5];

        time[0] = bzDatas[0] + 2000;
        time[1] = bzDatas[1];
        time[2] = bzDatas[2];
        time[3] = bzDatas[3];
        time[4] = bzDatas[4];
        time[5] = bzDatas[5];
        return time;
        //return year + "" + month + "" + day + "." + hour + "" + minute + "" + second;
    }
    private static byte[] TrunDESCbyte(byte[] bytes) {
        byte[] DESCbytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            DESCbytes[bytes.length - 1 - i] = bytes[i];
        }
        return DESCbytes;
    }



    public static String ConvertBytesToDate(byte[] date)
    {
        String ret = "";
        try
        {
            int i = 0;
            int year = 2000 + (int)date[i]; i ++;
            int mon = (int)date[i]; i++;
            int day = (int)date[i]; i++;
            int hour = (int)date[i]; i++;
            int min = (int)date[i]; i++;
            int sec = (int)date[i]; i++;
//            SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            ret = year + "/" + mon +"/"+ day + hour +":"+ min +":"+ sec;
//            ret = new Date(year, mon, day, hour, min, sec);
        }
        catch(Exception e) {
//            ret = new Date(2000, 1, 1, 0, 0, 0);
        }
        return ret;
    }

    public static short FromBytes(byte[] pbtBuffer)
    {
        return (short) a16To10(DisGetHexToChs("", pbtBuffer));
    }

    public static byte[] TrunDESCbyte(byte[] bytes, byte[] DESCbytes)
    {
        DESCbytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++)
        {
            DESCbytes[bytes.length - 1 - i] = bytes[i];
        }
        return DESCbytes;
    }

    private static String DisGetHexToChs(String hex, byte[] bytes)
    {
        hex = "";
        for (int i = 0; i < bytes.length; i++)
        {
            try
            {
                // 每两个字符是一个 byte。
                hex += shizhong.a10To16(bytes[i]);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        };
        return hex;
    }
    private static int a16To10(String s)
    {
        return Integer.parseInt(s,16);
    }
}
