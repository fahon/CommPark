#include "string.h"

#include <stdio.h>
#include <time.h>
#include<signal.h>
#include "devapi.h"
#include "jni.h"
#include "Hpsam.h"
#include "android/log.h"
static const char *TAG="fxjni";
#define LOG_TAG "System.out.c"
//#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
//#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

/**
 *打开rfid读写器
 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_rfidOpen
  (JNIEnv *env, jobject thiz, jint port, jint baudrate)
{
	int st = openRfid(port, baudrate);
	return  st;
}

/**
 *关闭RFID读写器
 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_rfidClose
  (JNIEnv *env, jobject thiz, jint port)
{
	int st = closeRfid(port) ;
	return st ;
}

/**
 *打开rfid电源
 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_rfidPowerOn
  (JNIEnv *env, jobject thiz)
{
	int st = open_rf();
	return  st;
}

/**
 *关闭RFID电源
 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_rfidPowerOff
  (JNIEnv *env, jobject thiz)
{
	int st = close_rf();
	return st;
}

/**
 * psam上电复位
 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_psamReset
  (JNIEnv *env, jobject thiz, jint psamCard, jbyteArray jarray)
{
	int st = 0 ;
	int bufferLen = 0 ;
	//获取指针
	unsigned char* buffer = (*env)->GetByteArrayElements(env, jarray, 0) ;
	//调用上电复位
	st =psam_reset(psamCard, buffer, &bufferLen) ;
	//释放指针
	(*env)->ReleaseByteArrayElements(env, jarray, buffer, 0);
	return st ;
}

/**
 *psam下电
 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_psamClose
  (JNIEnv *env, jobject thiz, jint psamCard)
{
	int st = 0 ;
	int bufferLen = 128 ;
	unsigned char buffer[bufferLen] ;
	memset(buffer,0,bufferLen) ;
	st = psam_close(psamCard,   buffer,  &bufferLen);
//	free(buffer) ;
	return st ;
}

/**
 * PSAM apdu指令
 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_psamApdu
  (JNIEnv *env, jobject thiz, jint psamCard, jbyteArray apdu, jbyteArray result)
{
	int st = 0 ;
	int apduLen ;
	int resultLen = 0 ;
	//获取指针
	unsigned char* resultBuffer = (*env)->GetByteArrayElements(env, result, 0) ;
	unsigned char* apduBuffer = (*env)->GetByteArrayElements(env, apdu, 0) ;
	apduLen = (*env)->GetArrayLength(env, apdu) ;
	LOGE("PSAM APDU LEN = %d",  apduLen);
	//调用apdu
	st = psam_apdu(psamCard,apduBuffer,apduLen, resultBuffer,&resultLen);
	//释放指针
	(*env)->ReleaseByteArrayElements(env, apdu, apduBuffer, 0) ;
	(*env)->ReleaseByteArrayElements(env, result, resultBuffer, 0) ;
	return st ;
}


/**
 * 读寄存器值
 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_readReg
  (JNIEnv *env, jobject thiz, jint regaddress, jbyteArray result)
{
	int st = 0 ;
	int resultLen = 0 ;
	//获取指针
	unsigned char* resultBuffer = (*env)->GetByteArrayElements(env, result, 0) ;
	unsigned char addr = regaddress ;
	//调用读取函数
	st = read_reg(addr, &resultLen, resultBuffer);
	//释放指针
	(*env)->ReleaseByteArrayElements(env, result, resultBuffer, 0) ;
	return st ;
}

/***
 * 写寄存器
 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_writeReg
  (JNIEnv *env, jobject thiz, jint regaddress, jbyteArray value, jbyteArray result)
{
	int st = 0 ;
	int resultLen = 0 ;
	unsigned char addr = regaddress ;
	//获取指针
	unsigned char* resultBuffer = (*env)->GetByteArrayElements(env, result, 0) ;
	unsigned char* valueBuffer = (*env)->GetByteArrayElements(env, value, 0) ;
	//调用
	st = write_reg(addr, valueBuffer, &resultLen ,resultBuffer) ;
	//释放指针
	(*env)->ReleaseByteArrayElements(env, result, resultBuffer, 0) ;
	(*env)->ReleaseByteArrayElements(env, value, valueBuffer, 0) ;
	return st ;
}

/* 获取版本号 */
JNIEXPORT void JNICALL Java_com_fxpsam_nativeJni_RfidNative_SptcReaderApiGetVer
  (JNIEnv *env, jobject thiz, jbyteArray result)
{
	//int st = 0 ;
	//获取指针
	unsigned char* resultBuffer = (*env)->GetByteArrayElements(env, result, 0) ;
	//调用
	sptc_reader_api_get_ver(resultBuffer) ;
	//释放指针
	(*env)->ReleaseByteArrayElements(env, result, resultBuffer, 0) ;
	return;
}

/* PSAM卡初始化 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_SptcReaderApiPsamInit
  (JNIEnv *env, jobject thiz, jint psam_slot, jbyteArray result)
{
	int st = 0 ;
	unsigned char slot = psam_slot ;
	//获取指针
	unsigned char* resultBuffer = (*env)->GetByteArrayElements(env, result, 0) ;
	//调用
	st = sptc_reader_api_psam_init(slot, resultBuffer) ;
	//释放指针
	(*env)->ReleaseByteArrayElements(env, result, resultBuffer, 0) ;
	return st ;
}

/* 读卡片信息 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_SptcReaderApiGetCardInfo
  (JNIEnv *env, jobject thiz, jbyteArray result)
{
	int st = 0 ;
	//获取指针
	unsigned char* resultBuffer = (*env)->GetByteArrayElements(env, result, 0) ;
	//调用
	st = sptc_reader_api_get_card_info(resultBuffer) ;
	//释放指针
	(*env)->ReleaseByteArrayElements(env, result, resultBuffer, 0) ;
	return st ;
}

/* 卡片扣款 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_SptcReaderApiDebit
  (JNIEnv *env, jobject thiz, jbyteArray value, jbyteArray result)
{
	int st = 0 ;
	//获取指针
	unsigned char* resultBuffer = (*env)->GetByteArrayElements(env, result, 0) ;
	unsigned char* valueBuffer = (*env)->GetByteArrayElements(env, value, 0) ;
	//调用
	st = sptc_reader_api_debit(valueBuffer, resultBuffer) ;
	//释放指针
	(*env)->ReleaseByteArrayElements(env, result, resultBuffer, 0) ;
	(*env)->ReleaseByteArrayElements(env, value, valueBuffer, 0) ;
	return st ;
}

/* 获取CPU卡交易认证码 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_SptcReaderApiCpuGetTransactionProve
  (JNIEnv *env, jobject thiz, jbyteArray value, jbyteArray result)
{
	int st = 0 ;
	//获取指针
	unsigned char* resultBuffer = (*env)->GetByteArrayElements(env, result, 0) ;
	unsigned char* valueBuffer = (*env)->GetByteArrayElements(env, value, 0) ;
	//调用
	st = sptc_reader_api_cpu_get_transaction_prove(valueBuffer, resultBuffer) ;
	//释放指针
	(*env)->ReleaseByteArrayElements(env, result, resultBuffer, 0) ;
	(*env)->ReleaseByteArrayElements(env, value, valueBuffer, 0) ;
	return st ;
}

/* M1卡计算交易认证码 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_SptcReaderApiM1CalcTac
  (JNIEnv *env, jobject thiz, jbyteArray value, jbyteArray result)
{
	int st = 0 ;
	//获取指针
	unsigned char* resultBuffer = (*env)->GetByteArrayElements(env, result, 0) ;
	unsigned char* valueBuffer = (*env)->GetByteArrayElements(env, value, 0) ;
	//调用
	st = sptc_reader_api_m1_calc_tac(valueBuffer, resultBuffer) ;
	//释放指针
	(*env)->ReleaseByteArrayElements(env, result, resultBuffer, 0) ;
	(*env)->ReleaseByteArrayElements(env, value, valueBuffer, 0) ;
	return st ;
}

/* 获取调试指针 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_SptcReaderApiGetDebugStep
  (JNIEnv *env, jobject thiz)
{
	int st = 0 ;

	//获取指针
	
	//调用
	st = sptc_reader_api_get_debug_step() ;
	//释放指针
	
	return st ;
}

/* 获取最近一次交易记录 */
JNIEXPORT jint JNICALL Java_com_fxpsam_nativeJni_RfidNative_SptcReaderApiGetLastRecord
  (JNIEnv *env, jobject thiz, jbyteArray result)
{
	int st = 0 ;

	//获取指针
	unsigned char* resultBuffer = (*env)->GetByteArrayElements(env, result, 0) ;
	//调用
	st = sptc_reader_api_get_last_record(resultBuffer);
	//释放指针
	(*env)->ReleaseByteArrayElements(env, result, resultBuffer, 0) ;
	return st ;
}

















