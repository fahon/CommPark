#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include "devapi.h"
#include "jni.h"
#include "android/log.h"
#include "Hpsam.h"

static const char *TAG="Hpsam";
#define LOG_TAG "SPTC_Debug"

#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#define  STATE_OK 	1   //返回状态

#define   TEST_ERROR					0X01

#define TYPE_STD 0
#define TYPE_ALL 1

//*************************Command **************************
#define   CMD_DELAY                     0x55
//Device
#define   CMDH_DEVICE					0x00		//设备操作高位
#define   CMDL_VERSION					0x01		//获取硬件版本号
#define   CMDL_HALT						0x45		//rf_halt

#define		CMDL_CLOSERF				0x00		//关闭射频
#define		CMDL_OPENRF					0x01		//开启射频

//CPU Card
#define   CMDH_RFCPU					0xC1		//非接CPU卡
#define   CMDH_CPU						0xC0		//接触式CPU卡
#define   CMDH_SAM1						0xC2		//SAM1卡
#define   CMDH_SAM2						0xC3		//SAM2卡
#define   CMDL_OPENCARD					0x30		//非接CPU卡 打开卡片
#define   CMDL_RESET					0x30		//复位
#define   CMDL_APDU						0x31		//APDU
#define   CMDL_DOWN						0x32		//下电
#define   CMDL_DESELE					0x33		//rf_desele

//M1 Card
#define   CMDH_M1						0xC1		//M1卡
#define   MT_OKH						0x00		//函数调用成功高位
#define   MT_OKL						0x00		//函数调用成功低位

#define   CMDL_RFRESET					0x4E		//射频复位
#define   CMDL_CARD						0x40		//寻卡，M1卡打开卡片
#define   CMDL_AUTH						0x5F		//认证
#define   CMDL_READ						0x46		//读数据
#define   CMDL_WRITE					0x47		//写数据
#define   CMDL_INC						0x48		//增值
#define   CMDL_DEC						0x49		//减值
#define   CMDL_RESTORE					0x4A		//回传
#define   CMDL_TRANSFER					0x4B		//传送
#define   CMDL_RFHALT  					0x45		//设置卡片状态为halt

//15693
#define   CMDL_15693INV					0x60		//清点寻卡
#define   CMDL_15693SEL					0x61		//选卡
#define   CMDL_15693Read				0x62		//读卡
#define   CMDL_15693Write				0x63		//写卡
#define   CMDL_15693RTR					0x64		//复位到准备状态
#define   CMDL_15693STQ					0x65		//设置静默
#define   CMDL_15693GSI					0x66		//取卡片系统信息

//*************************Command**************************
//************************* Error code ***********************//
#define ERR_DATAFORMAT					-0x13		//数据值范围错误
#define ERR_UNDEFINE_CARD               -0x14       //未识别卡类型
#define ERR_NO_CARD						-0x12
#define ERR_RFREAD						-0x31		//
#define ERR_OVER						-0x32		//
#define ERR_LESS						-0x33		//

#define ERR_CLRDATA						-0x34		//擦除数据失败
#define ERR_OPENCARD					-0X35		//非接触CPU卡，未寻卡
#define ERR_WRITECOM					-0X36       //写串口错误

//***************************身份证ID****************************//
#define CMDH_IDCARD_UID					0xCF
#define CMDL_IDCARD_UID					0x55


#define FALSE							-1

//int bOpenCard=0;						//非接CPU卡，0--未寻卡，1--已寻卡
//unsigned char nCardResetLen=0;			//非接CPU卡复位信息长度
//unsigned char sCardResetData[100]={0};	//非接CPU卡复位信息

//************************************************************//

//connect to the device
typedef int HANDLE;
typedef unsigned int DWORD;
//************************ 通信协议的包头包尾****************//
#define OP_OK      					0
#define STX							0x02
#define ETX							0x03

//************************ 通信协议的Error code *************//
#define ERR_UNDEFINED_HANDLE		-0x21
#define ERR_STX						-0x22
#define ERR_ETX						-0x23
#define ERR_BCC						-0x24
#define ERR_GENERAL					-0x25

/*******读写寄存器**********/
#define  READ_REG_H    0xC1 ;
#define  READ_REG_L    0xA1 ;

#define  WRITE_REG_H    0xC1 ;
#define  WRITE_REG_L    0xA2 ;

/*******射频复位**********/
#define  CMD_RFRESET_H    0xC1 ;
#define  CMD_RFRESET_L    0x4E ;

int fd = 0;    //全局变量,串口操作句柄
/**
 *
 * openRfid(int serialPort, int baudrate)
 * 功能：打开电源，并设置好串口和波特率
 * 参数：int serialPort：串口号0,11,12,13,14(大于0的int值)
 * 		int baudrate :波特率,14,115200
 * 返回：fd,大于0成功，-1初始化失败
 */
int openRfid(int serialPort, int baudrate)
{

	int ret = 0 ;
	//打开串口
	ret = h900_uart_open(serialPort,baudrate);
	fd = ret ;
	LOGE("openRfid->h900_uart_open, fd = %d" ,fd);
	if(fd == -1){
		return -1;
	}
//	sleep(1) ;
	//打开电源
	//h900_psam_power_on() ;
	LOGE("openRfid->h900_uart_open,serialPort = %d; baudrate = %d ", serialPort ,baudrate );
	//sleep(1) ;
//	unsigned char addr = 0x10 ;
//	read_reg(addr, NULL, NULL) ;
//	int aa = 10 ;
//	test(&aa) ;
//	LOGE("TEST aa = %d",aa );
	//测试字节流转HEX字符串
//	unsigned char  bb[] = {0x11,0x66} ;
//	char dest[16] ;
//	int sourceLen = 2 ;
//	ByteToHexStr(bb, dest, sourceLen) ;
//	LOGE("hex to string = %s",dest );
	return OP_OK ;
}

void test(int *tt){
	*tt = 20 ;
	return ;
}

/**
 *
 * closeRfid(int serialPort)
 * 功能：关闭电源和串口
 * 参数：int serialPort：串口号0,11,12,13,14(大于0的int值)
 * 返回：大于等于0成功，-1失败
 */
int closeRfid(int serialPort)
{
	int close ;
	//关闭电源
	//h900_psam_power_off();
	//关闭串口
	close = h900_uart_close(serialPort, fd);
  //
	LOGE("closeRfid->h900_uart_close, fd = %d,close = %d", fd,close);
	//LOGE("closeRfid, close = %d", close );
  //psam_reset(2) ;
	return close ;
}

/**
 *
 * psam_reset(int samCard, unsigned char* recv, int* recvLen)
 * 功能：psam卡上电复位
 * 参数：int samCard：psam卡座号sam1 = 1, sam2 = 2
 * 返回：大于0成功，-1失败
 */
//#define _DEBUG_PSAM_RESET_
int psam_reset(int samCard, unsigned char* recv, int* recvLen)
{
	unsigned char content_buffer[10]={0,0,0,0,0,0,0,0,0,0};
	unsigned char cmd_package[64];
	int st = 0 ;
	memset(cmd_package,0,64);
	int nRLen=0;
	int sendBufferLen = 0;
	if((samCard<0)||(samCard>4))
		return ERR_DATAFORMAT;
	switch(samCard)
	{
		case 1:
				content_buffer[0]=CMDH_SAM2;
				break;
		case 2:
				content_buffer[0]=CMDH_SAM1;
				break;
		default:
			  content_buffer[0]=CMDH_SAM2;
				break;
	}
	content_buffer[1]=CMDL_RESET;
	content_buffer[2]=0x01;
	sendBufferLen = 3 ;
	//生成指令
	gen_cmd(sendBufferLen, content_buffer,&nRLen, cmd_package);
	//指令已经在receive_bufer中了
	#ifdef _DEBUG_PSAM_RESET_
	LOGE("PSAM_RESET -- %d", nRLen) ;
	char temp[1024];
	memset(temp,0,sizeof(temp));
	ByteToHexStr(cmd_package,temp, nRLen);
	#endif

	//发送数据
	int ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_PSAM_RESET_
	LOGE("uart_send result-- %d", ret) ;
	#endif
	if(ret < 0){
		return ret ;
	}
	unsigned char buffer[128] ;
	usleep(20000);
	//串口数据接收
	st = -1;
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		#ifdef _DEBUG_PSAM_RESET_
		char ppp[1024];
		memset(ppp,0,sizeof(ppp));
		ByteToHexStr(buffer,ppp, post) ;
		#endif
		//将得到的数组数据解析后回传给调用者,
		nRLen = 0;
		st = resolve_data_mt3(buffer, &nRLen, recv);
		*recvLen = nRLen;
		#ifdef _DEBUG_PSAM_RESET_
		LOGE("psam reset RECV_LEN-- %d ", *recvLen);
		#endif
	}
	return st ;
}

/**
 * psam卡apdu指令
 */
//#define _DEBUG_PSAM_APDU_ 
int psam_apdu(int samCard,unsigned char* apdu, int apduLen, unsigned char* recv,int* recvLen)
{
	unsigned char content_buffer[4096] ;
	memset(content_buffer,0,4096) ;
	unsigned char cmd_package[4128] ;
	int st = 0 ;
	int nRLen=0;
	int sendBufferLen = 0;
	if((samCard<0)||(samCard>4))
		return ERR_DATAFORMAT;
	switch(samCard)
	{
		case 1:
				content_buffer[0]=CMDH_SAM2;
				break;
		case 2:
				content_buffer[0]=CMDH_SAM1;
				break;
		default:
			  content_buffer[0]=CMDH_SAM2;
				break;
	}
	content_buffer[1]=CMDL_APDU; //APDU
	content_buffer[2]=0x00;
	sendBufferLen = 3 + apduLen;
	//将apdu指令组装进去
	memcpy(content_buffer + 3,apdu, apduLen) ;
	//生成指令
	gen_cmd(sendBufferLen, content_buffer,&nRLen, cmd_package);
	#ifdef _DEBUG_PSAM_APDU_
	LOGE("PSAM_apdu -- %d", nRLen) ;
	char temp[1024];
	memset(temp,0,sizeof(temp));
	ByteToHexStr(cmd_package,temp, nRLen);
	#endif
	
	//发送数据
	int ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_PSAM_APDU_
	LOGE("uart_send result-- %d", ret) ;
	#endif
	if(ret < 0){
		return ret ;
	}
	unsigned char buffer[128] ;
	usleep(20);
	//串口数据接收
	st = -1;
	*recvLen = 0;
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		#ifdef _DEBUG_PSAM_APDU_
		char ppp[1024];
		memset(ppp,0,sizeof(ppp));
		ByteToHexStr(buffer,ppp, post) ;
		#endif
		//将得到的数组数据解析后回传给调用者,
		nRLen = 0;
		st = resolve_data_mt3(buffer, &nRLen, recv);
		*recvLen = nRLen;
		#ifdef _DEBUG_PSAM_APDU_
		LOGE("psam apdu RECV_LEN-- %d", *recvLen);
		#endif
	}
	return st ;
}

/**
 * 关闭psam卡
 */
//#define _DEBUG_PSAM_CLOSE_ 
int psam_close(int samCard, unsigned char* recv, int* recvLen)
{
	unsigned char content_buffer[10]={0,0,0,0,0,0,0,0,0,0};
	unsigned char cmd_package[64];
	int st = 0 ;
	memset(cmd_package,0,64);
	int nRLen=0;
	int sendBufferLen = 0;
	if((samCard<0)||(samCard>4))
		return ERR_DATAFORMAT;
	switch(samCard)
	{
		case 1:
				content_buffer[0]=CMDH_SAM2;
				break;
		case 2:
				content_buffer[0]=CMDH_SAM1;
				break;
		default:
			  content_buffer[0]=CMDH_SAM2;
				break;
	}
	content_buffer[1]=CMDL_DOWN; //下电指令
	content_buffer[2]=0x01;
	sendBufferLen = 3 ;
	//生成指令
	gen_cmd(sendBufferLen, content_buffer,&nRLen, cmd_package);
	#ifdef _DEBUG_PSAM_CLOSE_
	LOGE("PSAM_CLOSE -- %d", nRLen) ;
	char temp[1024];
	memset(temp,0,sizeof(temp));
	ByteToHexStr(cmd_package,temp, nRLen);
	#endif
	
	//发送数据
	int ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_PSAM_CLOSE_
	LOGE("uart_send result-- %d", ret) ;
	#endif
	if(ret < 0){
		return ret ;
	}
	unsigned char buffer[128] ;
	usleep(20);
	//串口数据接收
	st = -1;
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		#ifdef _DEBUG_PSAM_CLOSE_
		char ppp[1024];
		memset(ppp,0,sizeof(ppp));
		ByteToHexStr(buffer,ppp, post) ;
		#endif
		//将得到的数组数据解析后回传给调用者,
		nRLen = 0;
		st = resolve_data_mt3(buffer, &nRLen, recv);
		*recvLen = nRLen;
		#ifdef _DEBUG_PSAM_CLOSE_
		LOGE("psam close RECV_LEN-- %d ", *recvLen);
		#endif
	}
	return st ;
}

/**
 * 打开射频
 */
int open_rf()
{
  h900_psam_power_on();
	LOGE("open_rf->h900_psam_power_on");
	usleep(5000);
	return 0;
}


/**
 * 关闭射频
 */
int close_rf()
{
  //关闭电源
	h900_psam_power_off();
	LOGE("close_rf->h900_psam_power_off");
	usleep(5000);
	return 0;
}

/**
 * 复位射频
 */
//#define _DEBUG_RF_RESET_ 
int reset_rf(int reset_time_ms)
{
	unsigned char send_buffer[20];
	unsigned char cmd_package[64];
	unsigned char recv[64];
	memset(cmd_package,0,64) ;
	memset(send_buffer,0,20) ;
	int nRLen = 0;
	int st = 0;
	int slen = 5;
	memset(send_buffer, 0, 20);
	send_buffer[0] = CMD_RFRESET_H;
	send_buffer[1] = CMD_RFRESET_L;
	send_buffer[2] = 0x00;
	send_buffer[3] = (unsigned char)(reset_time_ms/256);
	send_buffer[4] = (unsigned char)(reset_time_ms%256);

	//生成指令
	st = gen_cmd(slen,send_buffer,&nRLen,cmd_package);
	#ifdef _DEBUG_RF_RESET_
	LOGE("RF reset cmd LEN = %d", nRLen);
	char temp[1024];
	memset(temp,0,sizeof(temp));
	ByteToHexStr(cmd_package,temp, nRLen);
	#endif
	
	//发送数据
	int ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_RF_RESET_
	LOGE("uart_send result-- %d", ret) ;
	#endif
	if(ret < 0){
		return ret;
	}
	usleep(20);
	unsigned char buffer[128] ;
	//串口数据接收
	st = -1;
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		#ifdef _DEBUG_RF_RESET_
		char ppp[1024];
		memset(ppp,0,sizeof(ppp));
		ByteToHexStr(buffer,ppp, post);
		#endif
		//将得到的数组数据解析后回传给调用者,
		nRLen = 0;
		st = resolve_data_mt3(buffer, &nRLen, recv);
		#ifdef _DEBUG_RF_RESET_
		LOGE("RF reset RECV_LEN-- %d", nRLen) ;
		#endif
	}
	return st;	
} 


/***
 * 写寄存器
 */
//#define _DEBUG_WRITE_REG_
int write_reg(unsigned char addr, unsigned char *value, int* recvLen, unsigned char* recv)
{
	unsigned char send_buffer[20];
	unsigned char cmd_package[64];
	memset(cmd_package,0,64) ;
	memset(send_buffer,0,20) ;
	int nRLen = 0;
	int st = 0;
	int slen = 5 ;
	send_buffer[0] = WRITE_REG_H;
	send_buffer[1] = WRITE_REG_L;
	send_buffer[2] = 0x00;
	send_buffer[3] = addr ;
	send_buffer[4] = value[0];
	//生成指令
	st = gen_cmd( slen, send_buffer, &nRLen, cmd_package);
	#ifdef _DEBUG_WRITE_REG_
	LOGE("Write reg cmd LEN = %d", nRLen);
	char temp[1024];
	memset(temp,0,sizeof(temp));
	ByteToHexStr(cmd_package,temp, nRLen);
	#endif
	
	//发送数据
	int ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_WRITE_REG_
	LOGE("uart_send result-- %d", ret) ;
	#endif
	if(ret < 0){
		return ret;
	}
	usleep(20);
	unsigned char buffer[128] ;
	//串口数据接收
	st = -1;
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		#ifdef _DEBUG_WRITE_REG_
		char ppp[1024];
		memset(ppp,0,sizeof(ppp));
		ByteToHexStr(buffer,ppp, post) ;
		#endif
		//将得到的数组数据解析后回传给调用者,
		nRLen = 0;
		st = resolve_data_mt3(buffer, &nRLen, recv);
		*recvLen = nRLen;
		#ifdef _DEBUG_WRITE_REG_
		LOGE("write reg RECV_LEN-- %d [%02x]", *recvLen,(unsigned char)recv[0]) ;
		#endif
	}
	return st ;
}

/**
 * 读寄存器
 */
//#define _DEBUG_READ_REG_ 
int read_reg(unsigned char addr, int* recvLen, unsigned char* recv)
{
	unsigned char send_buffer[20];
	unsigned char cmd_package[64];
	memset(cmd_package,0,64) ;
	memset(send_buffer,0,20) ;
	int nRLen = 0;
	int st = 0;
	int slen = 4 ;
	memset(send_buffer, 0, 20);
	send_buffer[0] = READ_REG_H;
	send_buffer[1] = READ_REG_L;
	send_buffer[2] = 0x00;
	send_buffer[3] = addr;

	//生成指令
	st = gen_cmd(slen,send_buffer,&nRLen,cmd_package);
	#ifdef _DEBUG_READ_REG_
	LOGE("Read reg cmd LEN = %d", nRLen);
	char temp[1024];
	memset(temp,0,sizeof(temp));
	ByteToHexStr(cmd_package,temp, nRLen);
	#endif
	
	//发送数据
	int ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_READ_REG_
	LOGE("uart_send result-- %d", ret) ;
	#endif
	if(ret < 0){
		return ret;
	}
	usleep(20);
	unsigned char buffer[128] ;
	//串口数据接收
	st = -1;
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		#ifdef _DEBUG_READ_REG_
		char ppp[1024];
		memset(ppp,0,sizeof(ppp));
		ByteToHexStr(buffer,ppp, post);
		#endif
		//将得到的数组数据解析后回传给调用者,
		nRLen = 0;
		st = resolve_data_mt3(buffer, &nRLen, recv);
		*recvLen = nRLen;
		#ifdef _DEBUG_READ_REG_
		LOGE("read reg RECV_LEN-- %d [%02x]", *recvLen,(unsigned char)recv[0]) ;
		#endif
	}
	return st;
}


/**********************************************************
 * *******  M1 卡******************************************
 **********************************************************/

/**
 * M1卡寻卡
 */
//#define _DEBUG_M1_FIND_CARD_
int m1_find_card(unsigned char* uid, int* uLen)
{

	unsigned char send_buffer[10]={0,0,0,0,0,0,0,0,0,0};
	unsigned char cmd_package[100];
	memset(cmd_package,0,100);
	int nRLen=0;
	int st=0;
	int sLen = 4 ;
	send_buffer[0]=CMDH_M1;
	send_buffer[1]=CMDL_CARD;
	send_buffer[2]=0x00;
	send_buffer[3]=0x01;// mode 01 ALL
	//生成指令
	st=gen_cmd( sLen,send_buffer,&nRLen,cmd_package);

	char dest[128] ;
	memset(dest,'0', 128) ;
	ByteToHexStr(cmd_package, dest, nRLen);
	//发送数据
	int ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_M1_FIND_CARD_
	LOGE("M1 find card send-- %d", ret) ;
	#endif
	if(ret < 0){
		st = ret ;
		return st ;
	}
	usleep(20);
	unsigned char buffer[128] ;
	//串口数据接收
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		//char ppp[post] ;
		//ByteToHexStr(buffer,ppp, post) ;
		//将得到的数组数据解析后回传给调用者,
		st = resolve_data_mt3(buffer, uLen, uid) ;
		LOGE("m1 card st == %d", st) ;
		if(*uLen > 0){
			st = *uLen ;
//			memset(dest,'0', 128) ;
//			ByteToHexStr(uid, dest, *uLen);
		}

//		*recvLen = post ;
//		memcpy(recv, buffer, post);
	}
	return st ;
}


/**
 * M1卡认证
 */
//#define _DEBUG_M1_AUTHENTICATION_
int m1_authentication(int keyType, int sector, unsigned char *sNkey,int keyLen, int isSH)
{
	unsigned char send_buffer[16]={0,0,0,0,0,0,0,0,0,0};
	unsigned char cmd_package[100];
	memset(cmd_package,0,100);
	int nRLen=0;
	int st=0;
	int sLen = 11 ;
	int ret = 0 ;
	send_buffer[0] = CMDH_M1;
	send_buffer[1] = CMDL_AUTH;
	if(isSH == 1){
		send_buffer[2] = 0x01;
	}else{
		send_buffer[2] = 0x00;
	}

	send_buffer[3] = keyType;
	send_buffer[4] = sector*4;
	memcpy(&send_buffer[5],sNkey,keyLen);
	#ifdef _DEBUG_M1_AUTHENTICATION_
	memset(cmd_package,0,sizeof(cmd_package));
	ByteToHexStrExt(send_buffer,(char *)cmd_package,sLen);
	LOGD("m1 Auth: %s",cmd_package); 
	#endif
	//生成指令
	st = gen_cmd(sLen,send_buffer,&nRLen,cmd_package);
	//打印指令
	#ifdef _DEBUG_M1_AUTHENTICATION_
	char dest[128];
	memset(dest,0,sizeof(dest));
	ByteToHexStrExt(cmd_package,(char *)dest,nRLen);
	LOGD("m1 Auth cmd_package: %s",dest);
	#endif
	//发送指令
	ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_M1_AUTHENTICATION_
	LOGE("M1 Auth--- send length-- %d", ret) ;
	#endif
	if(ret < 0){
		st = ret ;
		return st ;
	}
	usleep(20);
	unsigned char buffer[128] ;
	unsigned char temp[128] ;
	int bLen = 0 ;
	//串口数据接收
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		//char ppp[post];
		//ByteToHexStr(buffer,ppp, post) ;
		#ifdef _DEBUG_M1_AUTHENTICATION_
		memset(temp,0,sizeof(temp));
		ByteToHexStrExt(buffer,(char *)temp,post);
		LOGD("m1 Auth: %s",temp); 
		#endif
		//将得到的数组数据解析后回传给调用者,
		st = resolve_data_mt3(buffer, &bLen, temp) ;
		#ifdef _DEBUG_M1_AUTHENTICATION_
		LOGE("m1 Auth--- st == %d", st) ;
		#endif
		if(bLen > 0){
//			st = *uLen ;
//			memset(dest,'0', 128) ;
//			ByteToHexStr(uid, dest, *uLen);
		}
	}
	return st;
}

/****
 *M1卡读
 */
//#define _DEBUG_M1_READ_
int m1_read(int block, unsigned char* readData , int* rLen)
{
	unsigned char send_buffer[10]={0,0,0,0,0,0,0,0,0,0};
	unsigned char receive_buffer[100];
	memset(receive_buffer,0,100);
	unsigned char cmd_package[100];
	memset(cmd_package,0,100);
	int nRLen=0;
	int st=0;
	int sLen = 4 ;
	int ret = 0 ;
	send_buffer[0] = CMDH_M1;
	send_buffer[1] = CMDL_READ;
	send_buffer[2] = 0x00;
	send_buffer[3] = block;
	//生成指令
	st = gen_cmd( sLen,send_buffer,&nRLen,cmd_package);
	//打印指令
	char dest[128] ;
	memset(dest,'0', 128) ;
	ByteToHexStr(cmd_package, dest, nRLen);
	//发送指令
	ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_M1_READ_
	LOGE("M1 m1_read--- cmd length-- %d", ret) ;
	#endif
	if(ret < 0){
		st = ret ;
		return st ;
	}
	usleep(20);
	unsigned char buffer[128] ;
	unsigned char temp[128] ;
	int bLen = 0 ;
	//串口数据接收
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		//char ppp[post] ;
		//ByteToHexStr(buffer,ppp, post) ;
		#ifdef _DEBUG_M1_READ_
		memset(temp,0,sizeof(temp));
		ByteToHexStrExt(buffer,(char *)temp,post);
		LOGD("m1 read: %s",temp); 
		#endif
		//将得到的数组数据解析后回传给调用者,
		st = resolve_data_mt3(buffer, rLen, readData) ;
		#ifdef _DEBUG_M1_READ_
		LOGE("m1 read--- st == %d", st) ;
		#endif
		if(*rLen > 0){
			st = *rLen ;
			memset(dest,'0', 128) ;
			ByteToHexStr(readData, dest, st);
		}
	}
	return st ;
}

/***
 * 写块数据
 */
//#define _DEBUG_M1_WRITE_
int m1_write(int block, unsigned char* writeData)
{
	unsigned char send_buffer[100];
	unsigned char receive_buffer[100];
	memset(send_buffer,0,100);
	memset(receive_buffer,0,100);
	unsigned char cmd_package[100];
	memset(cmd_package,0,100);
	int nRLen=0;
	int st=0;
	int sLen = 20 ;
	int ret = 0 ;
	send_buffer[0] = CMDH_M1;
	send_buffer[1] = CMDL_WRITE;
	send_buffer[2] = 0x00;
	send_buffer[3] = block ;
	//要写入的数据
	memcpy(&send_buffer[4],writeData,16);
	//生成指令
	st = gen_cmd( sLen,send_buffer,&nRLen,cmd_package);
	//打印指令
	char dest[128] ;
	memset(dest,'0', 128) ;
	ByteToHexStr(cmd_package, dest, nRLen);
	//发送指令
	ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_M1_WRITE_
	LOGE("M1 m1_write--- cmd length-- %d", ret) ;
	#endif
	if(ret < 0){
		st = ret ;
		return st ;
	}
	usleep(20);
	unsigned char buffer[128] ;
	unsigned char temp[128] ;
	int bLen = 0 ;
	//串口数据接收
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		//char ppp[post] ;
		//ByteToHexStr(buffer,ppp, post) ;
		#ifdef _DEBUG_M1_WRITE_
		memset(temp,0,sizeof(temp));
		ByteToHexStrExt(buffer,(char *)temp,post);
		LOGD("m1 write: %s",temp); 
		#endif
		//将得到的数组数据解析后回传给调用者,
		st = resolve_data_mt3(buffer, &bLen, temp) ;
		#ifdef _DEBUG_M1_WRITE_
		LOGE("m1 write--- st == %d", st) ;
		#endif
		if(bLen > 0){
			
		}		
	}
	return st;
}

/**
 * M1卡初始化
 */
int m1_initVal(int block,  unsigned char* value, int vLen)
{
	LOGE("+++++  m1_initVal +++++") ;
	unsigned char send_buffer[16];
	memset(send_buffer,0,16);
	int st = 0 ;
	send_buffer[0] = *(value+3) ;
	send_buffer[1] = *(value+2) ;
	send_buffer[2] = *(value+1) ;
	send_buffer[3] = *(value) ;
	send_buffer[4] = ~send_buffer[0] ;
	send_buffer[5] = ~send_buffer[1] ;
	send_buffer[6] = ~send_buffer[2] ;
	send_buffer[7] = ~send_buffer[3] ;
	send_buffer[8] = send_buffer[0] ;
	send_buffer[9] = send_buffer[1] ;
	send_buffer[10] = send_buffer[2] ;
	send_buffer[11] = send_buffer[3] ;
	send_buffer[12] = block;
	send_buffer[13] = ~send_buffer[12];
	send_buffer[14] = send_buffer[12];
    send_buffer[15] = send_buffer[13];

    st = m1_write(block,send_buffer) ;
    return st ;
}


/**
 * M1 增值
 *
 */
//#define _DEBUG_M1_INCREMENT_
int m1_increment(int block,  unsigned char* value, int vLen)
{
	unsigned char send_buffer[10]={0,0,0,0,0,0,0,0,0,0};
	unsigned char cmd_package[60];
	memset(cmd_package,0,60);
	int nRLen=0;
	int st = 0 ;
	int sLen = 8 ;
	int ret = 0 ;
	send_buffer[0] = CMDH_M1 ;
	send_buffer[1] = CMDL_INC ;
	send_buffer[2] = 0x00 ;
	send_buffer[3] = block ;
	send_buffer[4] = *(value+3) ;
	send_buffer[5] = *(value+2) ;
	send_buffer[6] = *(value+1) ;
	send_buffer[7] = *(value) ;
	//生成指令
	st = gen_cmd( sLen,send_buffer,&nRLen,cmd_package);
	//打印指令
	char dest[128] ;
	memset(dest,'0', 128) ;
	ByteToHexStr(cmd_package, dest, nRLen);
	//发送指令
	ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_M1_INCREMENT_
	LOGE("M1 m1_increment--- cmd length-- %d", ret) ;
	#endif
	if(ret < 0){
		st = ret ;
		return st ;
	}
	usleep(20);
	unsigned char buffer[128] ;
	unsigned char temp[128] ;
	int bLen = 0 ;
	//串口数据接收
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		//char ppp[post] ;
		//ByteToHexStr(buffer,ppp, post) ;
		#ifdef _DEBUG_M1_INCREMENT_
		memset(temp,0,sizeof(temp));
		ByteToHexStrExt(buffer,(char *)temp,post);
		LOGD("m1 increment: %s",temp); 
		#endif
		//将得到的数组数据解析后回传给调用者,
		st = resolve_data_mt3(buffer, &bLen, temp) ;
		#ifdef _DEBUG_M1_INCREMENT_
		LOGE("m1 increment--- st == %d", st) ;
		#endif
		if(bLen > 0){
			
		}
	}
	return st ;
}

//M1减值
//#define _DEBUG_M1_DECREMENT_
int m1_decrement(int block, unsigned char* value, int vLen)
{
	unsigned char send_buffer[10]={0,0,0,0,0,0,0,0,0,0};
	unsigned char cmd_package[60];
	memset(cmd_package,0,60);
	int nRLen=0;
	int st = 0 ;
	int sLen = 8 ;
	int ret = 0 ;
	send_buffer[0] = CMDH_M1 ;
	send_buffer[1] = CMDL_DEC ;
	send_buffer[2] = 0x00 ;
	send_buffer[3] = block ;
	send_buffer[4] = *(value + 3) ;
	send_buffer[5] = *(value+2) ;
	send_buffer[6] = *(value+1) ;
	send_buffer[7] = *(value+0) ;
	//生成指令
	st = gen_cmd( sLen,send_buffer,&nRLen,cmd_package);
	//打印指令
	char dest[128] ;
	memset(dest,'0', 128) ;
	ByteToHexStr(cmd_package, dest, nRLen);
	//发送指令
	ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_M1_DECREMENT_
	LOGE("M1 m1_decrement--- cmd length-- %d", ret) ;
	#endif
	if(ret < 0){
		st = ret ;
		return st ;
	}
	usleep(20);
	unsigned char buffer[128] ;
	unsigned char temp[128] ;
	int bLen = 0 ;
	//串口数据接收
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		//char ppp[post] ;
		//ByteToHexStr(buffer,ppp, post) ;
		#ifdef _DEBUG_M1_DECREMENT_
		memset(temp,0,sizeof(temp));
		ByteToHexStrExt(buffer,(char *)temp,post);
		LOGD("m1 decrement: %s",temp); 
		#endif
		//将得到的数组数据解析后回传给调用者,
		st = resolve_data_mt3(buffer, &bLen, temp) ;
		#ifdef _DEBUG_M1_DECREMENT_
		LOGE("m1 decrement--- st == %d", st) ;
		#endif
		if(bLen > 0){
			
		}
	}
	return st ;
}

//M1读值
int m1_read_val(int block, unsigned char* value)
{
	unsigned char receive_buffer[40];
	memset(receive_buffer,0,40);
	unsigned char i;
	int rLen = 0 ;
	int st=0;
	st = m1_read(block, receive_buffer , &rLen);
	if (st < 0)
		return ERR_RFREAD;

    for(i=0;i<4;i++)
	{
		if (receive_buffer[i]==receive_buffer[i+4])
			return ERR_DATAFORMAT;
	}

	for(i=0;i<4;i++)
	{
		if (receive_buffer[i]!=receive_buffer[i+8])
			return ERR_DATAFORMAT;
	}
	if (receive_buffer[12]!=receive_buffer[14])
		return ERR_DATAFORMAT;

	if (receive_buffer[12]==receive_buffer[13])
		return ERR_DATAFORMAT;

	if (receive_buffer[13]!=receive_buffer[15])
		return ERR_DATAFORMAT;
	memcpy(value,receive_buffer,4);
	//打印指令
	char dest[128] ;
	memset(dest,'0', 128) ;
	ByteToHexStr(value, dest, 4);
	return st ;
}

//M1 restore
//#define _DEBUG_M1_RESTORE_
int m1_restore(int block)
{
	unsigned char send_buffer[10]={0,0,0,0,0,0,0,0,0,0};
	unsigned char cmd_package[60];
	memset(cmd_package,0,60);
	int nRLen=0;
	int st = 0 ;
	int sLen = 4 ;
	int ret = 0 ;
	send_buffer[0] = CMDH_M1 ;
	send_buffer[1] = CMDL_RESTORE ;
	send_buffer[2] = 0x00 ;
	send_buffer[3] = block ;
	st = gen_cmd(sLen,send_buffer,&nRLen,cmd_package) ;
	//打印指令
	char dest[128] ;
	memset(dest,'0', 128) ;
	ByteToHexStr(cmd_package, dest, nRLen);
	//发送指令
	ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_M1_RESTORE_
	LOGE("M1 m1_restore--- cmd length-- %d", ret) ;
	#endif
	if(ret < 0){
		st = ret ;
		return st ;
	}
	usleep(20);
	unsigned char buffer[128] ;
	unsigned char temp[128] ;
	int bLen = 0 ;
	//串口数据接收
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		//char ppp[post] ;
		//ByteToHexStr(buffer,ppp, post) ;
		#ifdef _DEBUG_M1_RESTORE_
		memset(temp,0,sizeof(temp));
		ByteToHexStrExt(buffer,(char *)temp,post);
		LOGD("m1 restore: %s",temp); 
		#endif
		//将得到的数组数据解析后回传给调用者,
		st = resolve_data_mt3(buffer, &bLen, temp) ;
		#ifdef _DEBUG_M1_RESTORE_
		LOGE("m1 restore--- st == %d", st) ;
		#endif
		if(bLen > 0){
			
		}
	}
	return st ;
}

//M1 transfer
//#define _DEBUG_M1_TRANSFER_
int m1_transfer(int block)
{
	unsigned char send_buffer[10]={0,0,0,0,0,0,0,0,0,0};
	unsigned char cmd_package[60];
	memset(cmd_package,0,60);
	int nRLen=0;
	int st = 0 ;
	int sLen = 4 ;
	int ret = 0 ;
	send_buffer[0] = CMDH_M1 ;
	send_buffer[1] = CMDL_TRANSFER ;
	send_buffer[2] = 0x00 ;
	send_buffer[3] = block ;
	st = gen_cmd(sLen,send_buffer,&nRLen,cmd_package) ;
	//打印指令
	char dest[128] ;
	memset(dest,'0', 128);
	ByteToHexStr(cmd_package, dest, nRLen);
	//发送指令
	ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_M1_TRANSFER_
	LOGE("M1 m1_transfer--- cmd length-- %d", ret) ;
	#endif
	if(ret < 0){
		st = ret ;
		return st ;
	}
	usleep(20);
	unsigned char buffer[128] ;
	unsigned char temp[128] ;
	int bLen = 0 ;
	//串口数据接收
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		//char ppp[post] ;
		//ByteToHexStr(buffer,ppp, post) ;
		#ifdef _DEBUG_M1_TRANSFER_
		memset(temp,0,sizeof(temp));
		ByteToHexStrExt(buffer,(char *)temp,post);
		LOGD("m1 transfer: %s",temp); 
		#endif
		//将得到的数组数据解析后回传给调用者,
		st = resolve_data_mt3(buffer, &bLen, temp) ;
		#ifdef _DEBUG_M1_TRANSFER_
		LOGE("m1 transfer--- st == %d", st) ;
		#endif
		if(bLen > 0){
			
		}
	}
	return st ;
}

//M1 halt
//#define _DEBUG_M1_HALT_
int m1_halt()
{
	unsigned char send_buffer[10]={0,0,0,0,0,0,0,0,0,0};
	unsigned char cmd_package[60];
	memset(cmd_package,0,60);
	int nRLen=0;
	int st = 0 ;
	int sLen = 3;
	int ret = 0 ;
	send_buffer[0] = CMDH_M1 ;
	send_buffer[1] = CMDL_TRANSFER ;
	send_buffer[2] = 0x00 ;
	st = gen_cmd(sLen,send_buffer,&nRLen,cmd_package) ;
	//打印指令
	char dest[128] ;
	memset(dest,'0', 128) ;
	ByteToHexStr(cmd_package, dest, nRLen);
	//发送指令
	ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_M1_HALT_
	LOGE("M1 m1_halt--- cmd length-- %d", ret) ;
	#endif
	if(ret < 0){
		st = ret ;
		return st ;
	}
	usleep(20);
	unsigned char buffer[128] ;
	unsigned char temp[128] ;
	int bLen = 0 ;
	//串口数据接收
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		//char ppp[post] ;
		//ByteToHexStr(buffer,ppp, post) ;
		#ifdef _DEBUG_M1_HALT_
		memset(temp,0,sizeof(temp));
		ByteToHexStrExt(buffer,(char *)temp,post);
		LOGD("m1 halt: %s",temp); 
		#endif
		//将得到的数组数据解析后回传给调用者,
		st = resolve_data_mt3(buffer, &bLen, temp) ;
		#ifdef _DEBUG_M1_HALT_
		LOGE("m1 halt--- st == %d", st) ;
		#endif
		if(bLen > 0){
			
		}
	}
	return st ;
}

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * gen_cmd(int slen,unsigned char* sendcmd,int* rlen,unsigned char* receivedata)
 * 功能：生成完整指令
 * 参数：int slen：sendcmd长度
 * 		unsigned char* sendcmd :cmdH+cmdL+delay+nData 指令+延时（无用） + n字节的数据
 * 		int* rlen, 返回的完整指令长度
 * 		unsigned char* receivedata  返回的完整指令
 * 返回：fd,大于0成功，-1初始化失败
 */
int gen_cmd(int slen,unsigned char* sendcmd,int* rlen,unsigned char* receivedata)
{
	unsigned char send_buffer[512]={0};
	unsigned char receive_buffer[512]={0};
	unsigned char nLRC=0;	//LRC
	int nRecLen=0;	 		//接收数据长度
	int st=0;
	unsigned int  cRecLen=sizeof(receive_buffer)-1;
	memset(send_buffer,0,512);
	memset(receive_buffer,0,512);

	*rlen=slen+5;
	//包头和数据长度
	send_buffer[0]=STX;
	send_buffer[1]=slen/256;
	send_buffer[2]=slen%256;


	//cmddata
	memcpy(&send_buffer[3],sendcmd,slen);
	//BCC
	nLRC=cr_bcc2(slen, sendcmd);
	send_buffer[slen+3]=nLRC;

	//ETX
	send_buffer[slen+4]=ETX;	//到此打好一包
	memcpy(receivedata, send_buffer, slen+5);  //把封装好的数据包传给receivedata
	*receivedata=STX;
	return(OP_OK);
}


/*从设备返回的数据包中解析出数据
 * 参数len： 数据包长度
 * 参数resourceData：数据包
 * 参数rlen: 解析出来的数据长度
 * 参数receive_buffer：解析出来的数据
 * */
int resolve_data_mt3(unsigned char *resourceData, int *recvlen, unsigned char *receive_buffer)
{
	int st = 0;
	int len;
	if(*resourceData != STX) return -1;
	len = (*(resourceData+1))*256 + *(resourceData + 2);
	*recvlen = len - 3;
	if(*(resourceData+3) == 0x00 && *(resourceData+4) == 0x00)
	{
		memcpy(receive_buffer,(resourceData + 6),len - 3);
	}else{
		return -2;
	}

	return 1;
}

/**
 * 串口发送数据
 */
//#define _DEBUG_UART_SEND_ 
int uart_send(int fd, char *data, int datalen)
{
    #ifdef _DEBUG_UART_SEND_
    LOGE("uart send len is %d",datalen);
    LOGE("uart fd = %d", fd );
    #endif
	  //printasc(data,datalen,"send");
    int len = 0;
    len = write(fd, data, datalen);//实际写入的长度
    if(len == datalen) {
        return len;

    }else{
        tcflush(fd, TCOFLUSH);//TCOFLUSH刷新写入的数据但不传送
        #ifdef _DEBUG_UART_SEND_
        LOGE("uart_send error");
        #endif
        return -1;
    }

    return 0;
}

/**
 * 串口接收数据
 */
//#define _DEBUG_UART_RECV_ 
int uart_recv(int fd, char *data, int datalen)
{
    int len=0, ret =0,pos=0;
    fd_set fs_read;
    struct timeval tv_timeout;
    #ifdef _DEBUG_UART_RECV_
    LOGE("uart fd = %d", fd );
    #endif
    FD_ZERO(&fs_read);
    FD_SET(fd, &fs_read);
    tv_timeout.tv_sec  = 1;// (10*20/115200+2);
    tv_timeout.tv_usec = 0;
    ret = select(fd+1, &fs_read, NULL, NULL, &tv_timeout);
    //如果返回0，代表在描述符状态改变前已超过timeout时间,错误返回-1

    //char tmp[128];
    char tmp[1024];
    int time_cnt = 0;
		if(FD_ISSET(fd, &fs_read))
		{
			while(1)
			{
				//#ifdef _DEBUG_UART_RECV_
				LOGE("receiving...time_cnt[%d]",time_cnt);
				//#endif
				usleep(1000);
				time_cnt++;
				if(time_cnt>3000) //大于3秒退出
				{
				   //#ifdef _DEBUG_UART_RECV_
					 LOGE("uart_revc time out...time_cnt[%d]",time_cnt);
					 //#endif
				   return -1;
				}
				memset(tmp,0,sizeof(tmp));
				len = read(fd, tmp, sizeof(tmp));
				if(len<0){
					continue;
				}
				memcpy(data+pos,tmp,len);
				pos+=len;
				if(pos>3)
				{
					time_cnt++;
					int l = data[1]<<8|data[2] + 5;
					if(l==pos)
					{
						#ifdef _DEBUG_UART_RECV_
						LOGE("uart_recv len is %d",pos);
						#endif
	          //printasc(data,pos,"receiv");
						return pos;
					}
					if(l<pos) {
						#ifdef _DEBUG_UART_RECV_
						LOGE("uart_recv length error,[%d]vs[%d]",l,pos);
						#endif
						return -2;
					}
				}
			}
		}
		else{
			#ifdef _DEBUG_UART_RECV_
			LOGE("uart_recv FD_ISSET error");
			#endif
			return -3;
		}
    return 0;
}


/*
 * 字节流转换为十六进制字符串
 */
//#define _DEBUG_BYTETOHEXSTR_ 
void ByteToHexStr(unsigned char* source, char* dest, int sourceLen)
{
    short i;
    unsigned char highByte, lowByte;
    
    if(sourceLen > 256)
  	{
  		 #ifdef _DEBUG_BYTETOHEXSTR_
  		 LOGD("ByteToHexStr--Surround[%d]",sourceLen);
  		 #endif
  		 return;
  	}

    for (i = 0; i < sourceLen; i++)
    {
        highByte = source[i] >> 4;
        lowByte = source[i] & 0x0f ;

        highByte += 0x30;

        if (highByte > 0x39)
                dest[i * 3] = highByte + 0x07;
        else
                dest[i * 3] = highByte;

        lowByte += 0x30;
        if (lowByte > 0x39)
            dest[i * 3 + 1] = lowByte + 0x07;
        else
            dest[i * 3 + 1] = lowByte;
        //    
        dest[i * 3 + 2] = 0x20;
    }
    #ifdef _DEBUG_BYTETOHEXSTR_
    LOGE("hex to string = %s",dest );
    #endif
    return ;
}

/*
 * 字节流转换为十六进制字符串
 */
#define _DEBUG_BYTETOHEXSTREXT_ 
void ByteToHexStrExt(unsigned char* source, char* dest, int sourceLen)
{
    short i;
    unsigned char highByte, lowByte;
    
    if(sourceLen > 256)
  	{
  		 #ifdef _DEBUG_BYTETOHEXSTREXT_
  		 LOGD("ByteToHexStrExt--Surround[%d]",sourceLen);
  		 #endif
  		 return;
  	}

    for (i = 0; i < sourceLen; i++)
    {
        highByte = source[i] >> 4;
        lowByte = source[i] & 0x0f ;

        highByte += 0x30;
        if (highByte > 0x39)
                dest[i * 3] = highByte + 0x07;
        else
                dest[i * 3] = highByte;

        lowByte += 0x30;
        if (lowByte > 0x39)
            dest[i * 3 + 1] = lowByte + 0x07;
        else
            dest[i * 3 + 1] = lowByte;
        //    
        dest[i * 3 + 2] = 0x20;
    }
    #ifdef _DEBUG_BYTETOHEXSTREXT_
    //LOGE("hex to string = %s",dest );
    #endif
    return ;
}

//计算异或和，发送出去
int cr_bcc2(int len, unsigned char *bcc_buffer)
{	int temp=0, i;
	for(i=0;i<len;i++)
		temp=temp^bcc_buffer[i];
	return(temp);
}


//接收时，计算异或和，校验数据是否正确
int ck_bcc2(int len, unsigned char *bcc_buffer)
{
	int temp=0, i;
	for(i=0;i<len;i++)
		temp=temp^bcc_buffer[i];
	if(temp==0)
		return(OP_OK);
	else
		return(ERR_BCC);
}

//检验返回数据
int check_data2(unsigned char* data)
{
	if(*data != STX) return -1;
	if(*(data+1) != 0x00) return -1;
	if(*(data+2) != 0x03) return -1;
	if(*(data+3) != MT_OKH) return -1;
	if(*(data+3) != MT_OKL) return -1;
	return OP_OK;
}

/**
 * M1卡打开卡片
 */
//#define _DEBUG_M1_OPEN_CARD_ 
int m1_open_card(int type,unsigned char* recv, int* recvLen)
{
	unsigned char content_buffer[10]={0,0,0,0,0,0,0,0,0,0};
	unsigned char cmd_package[64];
	int st = 0 ;
	memset(cmd_package,0,64);
	int nRLen=0;
	int sendBufferLen = 0;
  //
  content_buffer[0]=CMDH_RFCPU;
	content_buffer[1]=CMDL_CARD;
	content_buffer[2]=0;
	if(type == 0)
	   content_buffer[3]=TYPE_STD;
	else
		 content_buffer[3]=TYPE_ALL;
	sendBufferLen = 4;
	//生成指令
	gen_cmd(sendBufferLen, content_buffer,&nRLen, cmd_package);
	#ifdef _DEBUG_M1_OPEN_CARD_
	LOGE("m1 open card -- %d", nRLen) ;
	char temp[1024];
	memset(temp,0,sizeof(temp));
	ByteToHexStr(cmd_package,temp, nRLen);
	#endif
	
	//发送数据
	int ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_M1_OPEN_CARD_
	LOGE("uart_send result-- %d", ret) ;
	#endif
	if(ret < 0){
		return ret ;
	}
	unsigned char buffer[128] ;
	usleep(20);
	//串口数据接收
	st = -1;
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		#ifdef _DEBUG_M1_OPEN_CARD_
		char ppp[1024];
		memset(ppp,0,sizeof(ppp));
		ByteToHexStr(buffer,ppp, post) ;
		#endif
		//将得到的数组数据解析后回传给调用者,
		nRLen = 0;
		st = resolve_data_mt3(buffer, &nRLen, recv);
		*recvLen = nRLen;
		#ifdef _DEBUG_M1_OPEN_CARD_
		LOGE("m1 open card RECV_LEN-- %d ", *recvLen);
		#endif
	}
	return st ;
}


/**
 * CPU卡打开卡片
 */
//#define _DEBUG_CPU_OPEN_CARD_ 
int cpu_open_card(int type,unsigned char* recv, int* recvLen)
{
	unsigned char content_buffer[10]={0,0,0,0,0,0,0,0,0,0};
	unsigned char cmd_package[64];
	int st = 0 ;
	memset(cmd_package,0,64);
	int nRLen=0;
	int sendBufferLen = 0;
	//
  content_buffer[0]=CMDH_RFCPU;
	content_buffer[1]=CMDL_OPENCARD;
	content_buffer[2]=0;
	if(type == 0)
	   content_buffer[3]=TYPE_STD;
	else
		 content_buffer[3]=TYPE_ALL;
	sendBufferLen = 4 ;
	//生成指令
	gen_cmd(sendBufferLen, content_buffer,&nRLen, cmd_package);
	#ifdef _DEBUG_CPU_OPEN_CARD_
	LOGE("cpu open card -- %d", nRLen) ;
	char temp[1024];
	memset(temp,0,sizeof(temp));
	ByteToHexStr(cmd_package,temp, nRLen);
	#endif
	
	//发送数据
	int ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_CPU_OPEN_CARD_
	LOGE("uart_send result-- %d", ret) ;
	#endif
	if(ret < 0){
		return ret ;
	}
	unsigned char buffer[128] ;
	usleep(20);
	//串口数据接收
	st = -1;
	int post = uart_recv(fd, buffer, 128) ;
	if(post > 0){
		#ifdef _DEBUG_CPU_OPEN_CARD_
		char ppp[1024];
		memset(ppp,0,sizeof(ppp));
		ByteToHexStr(buffer,ppp, post);
		#endif
		//将得到的数组数据解析后回传给调用者,
		nRLen = 0;
		st = resolve_data_mt3(buffer, &nRLen, recv);
		*recvLen = nRLen;
		#ifdef _DEBUG_CPU_OPEN_CARD_
		LOGE("cpu open card RECV_LEN-- %d ", *recvLen);
		#endif
	}
	return st ;
}

/**
 * CPU卡APDU
 */
//#define _DEBUG_CPU_CARD_APDU_ 
int cpu_card_apdu(unsigned char* send,int sendlen,unsigned char* recv, int* recvLen)
{
	unsigned char content_buffer[256];
	unsigned char cmd_package[64];
	int st = 0 ;
	memset(cmd_package,0,64);
	int nRLen=0;
	int sendBufferLen = 0;
	//
  content_buffer[0]=CMDH_RFCPU;
	content_buffer[1]=CMDL_APDU;
	content_buffer[2]=0;
	memcpy(&content_buffer[3],send,sendlen);
	sendBufferLen = 3 + sendlen;
	//生成指令
	gen_cmd(sendBufferLen, content_buffer,&nRLen, cmd_package);
	#ifdef _DEBUG_CPU_CARD_APDU_
	LOGE("cpu open card -- %d", nRLen) ;
	char temp[1024];
	memset(temp,0,sizeof(temp));
	ByteToHexStr(cmd_package,temp, nRLen);
	#endif
	
	//发送数据
	int ret = uart_send(fd,cmd_package,nRLen) ;
	#ifdef _DEBUG_CPU_OPEN_CARD_
	LOGE("uart_send result-- %d", ret) ;
	#endif
	if(ret < 0){
		return ret ;
	}
	unsigned char buffer[128] ;
	usleep(20);
	//串口数据接收
	st = -1;
	int post = uart_recv(fd, buffer, 128);
	if(post > 0){
		#ifdef _DEBUG_CPU_OPEN_CARD_
		char ppp[1024];
		memset(ppp,0,sizeof(ppp));
		ByteToHexStr(buffer,ppp, post) ;
		#endif
		//将得到的数组数据解析后回传给调用者,
		nRLen = 0;
		st = resolve_data_mt3(buffer, &nRLen, recv);
		*recvLen = nRLen;
		#ifdef _DEBUG_CPU_OPEN_CARD_
		LOGE("cpu open card RECV_LEN-- %d ", *recvLen);
		#endif
	}
	return st ;
}

void write_reg_0x22_0xf()
{
	unsigned char addr;
	unsigned char value[4];
	int recvLen;
	unsigned char recv[16];
	
	addr = 0x22;
	memset(value,0,sizeof(value));
	memset(recv,0,sizeof(recv));
	value[0] = 0x0f;
	recvLen = 0;
	
	write_reg(addr, value, &recvLen, recv);
	
	return;
}

void write_reg_0x22_0x7()
{
	unsigned char addr;
	unsigned char value[2];
	int recvLen;
	unsigned char recv[8];
	
	addr = 0x22;
	memset(value,0,sizeof(value));
	memset(recv,0,sizeof(recv));
	value[0] = 0x07;
	recvLen = 0;
	
	write_reg(addr, value, &recvLen, recv);
	
	return;	
}

