

//打开RFID读写器，串口和供电
int openRfid(int serialPort, int buadrate) ;
//字节数组转成十六进制字符串
void ByteToHexStr(unsigned char* source, char* dest, int sourceLen);
//
void ByteToHexStrExt(unsigned char* source, char* dest, int sourceLen);
//psam 上电复位
int psam_reset(int samCard, unsigned char* recv, int* recvLen) ;
//psam  apdu指令
int psam_apdu(int samCard,unsigned char* apdu, int apduLen, unsigned char* recv,int* recvLen);
//PSAM下电
int psam_close(int samCard, unsigned char* recv, int* recvLen);
//打开射频（暂未开发）
int open_rf();
//关闭射频（暂未开发）
int close_rf() ;
//复位视频
int reset_rf(int reset_time_ms);
//写寄存器
int write_reg(unsigned char addr, unsigned char *value, int* recvLen, unsigned char* recv) ;
//读寄存器
int read_reg(unsigned char addr, int* rlen, unsigned char* receive);
//
///////////////////////////////  M1 卡    ////////////////////////////////////////////////////////////////////
//M1卡寻卡
int m1_find_card(unsigned char* uid, int* uLen);
//M1卡认证
int m1_authentication(int keyType, int sector, unsigned char *sNkey,int keyLen, int isSH) ;
//M1读卡
int m1_read(int block, unsigned char* readData , int* rLen);
//M1写卡
int m1_write(int block, unsigned char* writeData) ;
//M1 initVal
int m1_initVal(int block,  unsigned char* value, int vLen) ;
//M1增值
int m1_increment(int block,  unsigned char* value, int vLen) ;
//M1减值
int m1_decrement(int block, unsigned char* value, int vLen) ;
//M1读值
int m1_read_val(int block, unsigned char* value) ;
//M1 transfer
int m1_transfer(int block) ;
//M1 restore
int m1_restore(int block) ;
//M1 halt
int m1_halt() ;
//
int m1_open_card(int type,unsigned char* recv, int* recvLen);
//
int cpu_open_card(int type,unsigned char* recv, int* recvLen);
//
int cpu_card_apdu(unsigned char* send,int sendlen,unsigned char* recv, int* recvLen);
//
void write_reg_0x22_0xf();
//
void write_reg_0x22_0x7();

