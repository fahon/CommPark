// IScan.aidl
package com.example.myscandemo;

interface IScan {
	//初始化扫描头
	void initEngine();
	//扫描
	String scan();
	//关闭扫描
	void close();
}
