package willsong.cn.commpark.activity.util;


import willsong.cn.commpark.activity.Bean.FunctionCode;
import willsong.cn.commpark.activity.Bean.StreamDirection;

/**
 * Created by Administrator on 2016/10/14 0014.
 */

public class CAckFrame extends CBaseFrame {

    public CAckFrame()
    {
        CreateControlCode(StreamDirection.Ack, FunctionCode.Ack);
    }

    public static int FrameLength()
    {
        return 2 + 1 + 2 + 4 + 4 + 1 + 2 + 1 + 1 + 1;
    }

    private byte m_AckType;
    /// <summary>
    /// 回应类型
    /// </summary>

    public static int rst;

    public byte getM_AckType() {
        return m_AckType;
    }

    public void setM_AckType(byte m_AckType) {
        this.m_AckType = m_AckType;
    }

    private byte m_ErrorCode;
    /// <summary>
    /// 错误代码
    /// </summary>


    public byte getM_ErrorCode() {
        return m_ErrorCode;
    }

    public void setM_ErrorCode(byte m_ErrorCode) {
        this.m_ErrorCode = m_ErrorCode;
    }

    protected void SetData()
    {
        //Data = new byte[] { (byte)AckType, (byte)ErrorCode };
        setM_Data(new byte[] { m_AckType, m_ErrorCode });
    }

    public static CAckFrame CreateFromBytes(byte[] src,int stxPos)
    {
        if (src == null || src.length < FrameLength()) { rst = -1; return null; }
        stxPos = -1;
        for (int i = 0; i < src.length - 1; i++)
        {
            byte[] bzSTX = TrunDESCbyte(ToBytes(getSTX()));
            if (bzSTX[i] == src[i] && bzSTX[i + 1] == src[i + 1])
            { stxPos = i; break; }
        }
        if (stxPos == -1 || src.length < stxPos + FrameLength()) { rst = -2; return null; }
        if (src[stxPos + FrameLength() - 1] != (byte) ETX) { rst = -3; return null; }
        short crc = FromBytes(TrunDESCbyte(new byte[] { src[stxPos + FrameLength() - 3], src[stxPos + FrameLength() - 2] }));
        byte[] bzData = new byte[FrameLength() - 3];
        System.arraycopy(src, stxPos, bzData, 0, FrameLength() - 3);
        if (CRC16Check(bzData, CRC_SEED, CRC_POLY) != crc) { rst = -4; return null; }
        rst = 0;
        byte[] bzId = new byte[2]; System.arraycopy(src, stxPos + 2 + 1, bzId, 0, 2);
        byte[] bzSAddrComp = new byte[2]; System.arraycopy(src, stxPos + 2 + 1 + 2, bzSAddrComp, 0, 2);
        byte[] bzSAddrCode = new byte[2]; System.arraycopy(src, stxPos + 2 + 1 + 2 + 2, bzSAddrCode, 0, 2);
        byte[] bzRAddress = new byte[4]; System.arraycopy(src, stxPos + 2 + 1 + 2 + 4, bzRAddress, 0, 4);
        byte bzCtl = src[stxPos + 2 + 1 + 2 + 4 + 4];
        byte[] bzDatas = new byte[2]; System.arraycopy(src, stxPos + 2 + 1 + 2 + 4 + 4 + 1, bzDatas, 0, 2);
        byte[] bzCrc = new byte[2]; System.arraycopy(src, stxPos + 2 + 1 + 2 + 4 + 4 + 1 + 2, bzCrc, 0, 2);
        CAckFrame ret = new CAckFrame();
        short usId,usComp,usAddrCode,usCrc;
        int unRAddr;
        usId = FromBytes(TrunDESCbyte(bzId, null));
        ret.setM_Id(usId);
        usComp = FromBytes(TrunDESCbyte(bzSAddrComp, null));
        usAddrCode = FromBytes(TrunDESCbyte(bzSAddrCode, null));
        ret.CreateSenderAddress(usComp, usAddrCode);
        unRAddr = FromBytes(TrunDESCbyte(bzRAddress, null));
        ret.CreateReceiverAddress(unRAddr);
//        StreamDirection dir = (StreamDirection)((byte)(bzCtl >> 7));
//        FunctionCode fun = (FunctionCode)((byte)(bzCtl & 0x1F));
        byte dir = (byte)(bzCtl >> 7);
        byte fun = (byte)(bzCtl & 0x1F);
        ret.CreateControlCode(dir, fun);
        ret.setM_Data(bzDatas);
        usCrc = FromBytes(TrunDESCbyte(bzCrc, null));
        ret.setM_CRC(usCrc);
        byte date = ret.getM_Data()[0];
        ret.setM_AckType(date);//AckType = (FunctionAck)ret.Data[0];
        ret.setM_ErrorCode(ret.getM_Data()[1]);// = (ErrorCodeList)ret.Data[1];
        return ret;
    }

    private static byte[] TrunDESCbyte(byte[] bytes) {
        byte[] DESCbytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            DESCbytes[bytes.length - 1 - i] = bytes[i];
        }
        return DESCbytes;
    }
    public static short FromBytes(byte[] pbtBuffer)
    {
        return (short)a16To10(DisGetHexToChs("", pbtBuffer));
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
                hex += a10To16(bytes[i]);
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        };
        return hex;
    }
    private static int a16To10(String s)
    {
        return Integer.parseInt(s, 16);
    }
}
