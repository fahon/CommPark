package com.fxpsam.nativeJni;

/**
 * 本地方法调用,注意com.fxpsam.nativeJni这个报名不可更改
 * @author admin
 *
 */
public class RfidNative {

	static{
		System.loadLibrary("devapi") ;
//		System.loadLibrary("Hpsam") ;
		System.loadLibrary("fxjni") ;
	}
	
	/****Java 接口*****/
	/**
	 * 打开RFID读写器，包括电源打开，串口打开
	 * @param serialPort  串口号，通常为14
	 * @param baudrate 波特率115200
	 * @return  返回：fd,大于0成功，-1初始化失败
	 */
	public int open(int serialPort, int baudrate){
		return rfidOpen( serialPort,  baudrate)  ;
	}
	
	/***
	 * 关闭RFID读写器
	 * @param serialPort  串口号，通常为14
	 * @return 返回：大于等于0成功，-1失败
	 */
	public int close(int serialPort){
		return rfidClose(serialPort) ;
	}
	
	/***
	 * 打开射频电源
	 * @param None
	 * @return 返回：0成功
	 */
	public int rfidpoweron(){
		return rfidPowerOn();
	}

	/***
	 * 关闭射频电源
	 * @param None
	 * @return 返回：0成功
	 */
	public int rfidpoweroff(){
		return rfidPowerOff();
	}
	
	
	/**
	 * psam卡上电复位
	 * @param psamCard  psam卡座号，psam1 = 1, psam2 = 2
	 * @param result  上电复位成功后返回的数据
	 * @return 返回：大于0成功，-1失败
	 */
	public int psamreset(int psamCard,byte[] result){
		return psamReset( psamCard, result) ;
	}
	
	/**
	 * psam卡下电关闭
	 * @param psamCard psam卡座好，psam1 = 1, psam2 = 2
	 * @return  返回：大于0成功，-1失败
	 */
	public int psamclose(int psamCard){
		return psamClose(psamCard) ;
	}
	
	/***
	 * psam卡apdu指令
	 * @param psamCard  psamCard psam卡座好，psam1 = 1, psam2 = 2
	 * @param apduCmd   apdu指令
	 * @param result   apdu指令执行结果
	 * @return 返回：0成功，-1失败，其他参考错误码
	 */
	public int psamapdu(int psamCard, byte[] apduCmd, byte[] result){
		return psamApdu( psamCard, apduCmd, result);
	}
	
	/***
	 * 获取读写器寄存器值
	 * @param regAddress  寄存器地址
	 * @param result   获取结果
	 * @return
	 */
	public int readreg(int regAddress, byte[] result){
		return readReg( regAddress,  result) ;
	}
	
	/**
	 * 写寄存器值
	 * @param regAddress
	 * @param value
	 * @param result
	 * @return
	 */
	public int writereg(int regAddress, byte[] value, byte[]result){
		return writeReg( regAddress,  value, result);
	}
	
	
	/***JNI 本地调用函数****/
	private native int rfidOpen(int serialPort, int baudrate) ;  
	
	private native int rfidClose(int serialPort) ; //关闭RFID读写器
	
	private native int rfidPowerOn() ;  //打开射频电源
	
	private native int rfidPowerOff() ; //关闭射频电源
	
	private native int psamReset(int psamCard,byte[] result) ; //psam卡上电复位
	
	private native int psamClose(int psamCard) ;//psam卡下电关闭
	
	private native int psamApdu(int psamCard, byte[] apduCmd, byte[] result) ;//psam卡apdu指令
	
	private native int readReg(int regAddress, byte[] result) ;//读寄存器
	
	private native int writeReg(int regAddress, byte[] value, byte[]result) ;//写寄存器
	
	/* SPTC 相关函数接口 */
	/*
	获取动态库版本号,目前为:"sptc_reader_dll_20141027_V0.04"
	void sptc_reader_api_get_ver()
	*/
	private native void SptcReaderApiGetVer(byte[] version);
	public void sptcreaderapigetver(byte[] version)
	{
		SptcReaderApiGetVer(version);
	}

	/*
	PSAM卡初始化,slot为1或2, 返回PSAM卡号,6字节hex。返回: 0 成功, !0 失败
	int sptc_reader_api_psam_init()
	*/
	private native int SptcReaderApiPsamInit(int slot,byte[] psamno);
	public int sptcreaderapipsaminit(int slot,byte[] psamno)
	{
		return SptcReaderApiPsamInit(slot,psamno);
	}
	/*
	读取上海公交卡信息
	int sptc_reader_api_get_card_info()  返回: 0 成功, !0 读卡失败
	outbuf[00..00]:UBYTE	1 卡片类型,0-M1,1-CPU
	outbuf[01..04]:UBYTE	4	(CPU)应用序列号第7——10字节,(M1)卡唯一号
	outbuf[05..06]:UBYTE	2	城市ID 
	outbuf[07..07]:BYTE	  1	交通卡卡类型
	outbuf[08..11]:UBYTE	4	有效期, YYYYMMDD, BCD 码
	outbuf[12..14]:BYTE	  3	卡余额，分，HEX
	outbuf[15..15]:UBYTE	1	卡状态
	outbuf[16..17]:BYTE	  2	卡计数器，HEX
	outbuf[18..18]:BYTE	  1	卡版本号(地区号)
	outbuf[19..22]:UBYTE	4	(CPU)应用序列号第3——6字节
	outbuf[23..23]:BYTE	  1	(CPU)算法标示
	outbuf[24..34]:BYTE	  11  卡面号 ASC
	*/
	private native int SptcReaderApiGetCardInfo(byte[] outbuf);
	public int sptcreaderapigetcardinfo(byte[] outbuf)
	{
		return SptcReaderApiGetCardInfo(outbuf);
	}
	
	/*
	int sptc_reader_api_debit() 返回:0 扣款成功, !0 扣款失败
	inbuf[00..06]:UBYTE	7	交易的日期时间, 表示为：YYYYMMDDhhmmss, BCD 码
	inbuf[07..09]:UBYTE	3	交易流水号，由上位机维护，但要求连续。
	inbuf[10..12]:UBYTE	3	交易金额,分
	
	outbuf[00..01]:交易计数	UINT	2	卡交易前计数值
	outbuf[02..04]:余额	BYTE	3	交易后钱包余额，分
	outbuf[05..08]:TAC	UBYTE	4	用于交通卡发行机构认证交易
	*/
	private native int SptcReaderApiDebit(byte[] inbuf,byte[] outbuf);
	public int sptcreaderapidebit(byte[] inbuf,byte[] outbuf)
	{
		return SptcReaderApiDebit(inbuf,outbuf);
	}
	
	/*
	取CPU卡交易认证码
	int sptc_reader_api_cpu_get_transaction_prove()
	inbuf[00..02]:交易流水号 3	交易流水号
	inbuf[03..06]:卡号		4	交易记录中的的卡唯一号
	inbuf[07..08]:计数器值	2	交易记录中的的交易前卡计数器值
	
	outbuf[00..03]:TAC	UBYTE	4	匹配交易的交易认证码
	*/
	private native int SptcReaderApiCpuGetTransactionProve(byte[] inbuf,byte[] outbuf);
	public int sptcreaderapicpugettransactionprove(byte[] inbuf,byte[] outbuf)
	{
		return SptcReaderApiCpuGetTransactionProve(inbuf,outbuf);
	}
	
	/*
	计算M1卡交易认证码
	int sptc_reader_api_m1_calc_tac()
	inbuf[00..02]:交易流水号 3	交易流水号
	inbuf[03..04]:城市代码   2
	inbuf[05..08]:卡号		   4	交易记录中的的卡唯一号
	inbuf[09..09]:卡类型     1
	inbuf[10..13]:卡片余额   4
	inbuf[14..17]:交易金额   4
	inbuf[18..24]:交易日期时间 7  BCD CCYYMMDDHHMMSS
	inbuf[25..26]:卡计数器     2	交易记录中的的交易前卡计数器值
	inbuf[27..30]:POSID        4  PSAM卡号后4字节
	
	outbuf[00..03]:TAC	UBYTE	4	交易认证码
	*/
	private native int SptcReaderApiM1CalcTac(byte[] inbuf,byte[] outbuf);
	public int sptcreaderapim1calctac(byte[] inbuf,byte[] outbuf)
	{
		return SptcReaderApiM1CalcTac(inbuf,outbuf); 
	}
	
	/*
	取内部调试记录标志
	int sptc_reader_api_get_debug_step()
	有问题时返回此代码以便跟踪
	返回:udword调试记录标志
	*/
	private native int SptcReaderApiGetDebugStep();
	public int sptcreaderapigetdebugstep()
	{
		return SptcReaderApiGetDebugStep();
	}
	
	/*
	读取最后一笔交易记录
	int sptc_reader_api_get_last_record()
	返回:0 读取成功, !0 读取失败
	需先调用读卡接口再调用此接口
	outbuf[00..03]:交易设备编码
	outbuf[04..07]:交易金额，分，HeX，MSB first
	outbuf[08..11]:交易后钱包余额，分，HeX，MSB first
	outbuf[12..18]:交易日期时间，BCD
	*/
	private native int SptcReaderApiGetLastRecord(byte[] outbuf);
	public int sptcreaderapigetlastrecord(byte[] outbuf)
	{
		return SptcReaderApiGetLastRecord(outbuf);
	}
	
}
