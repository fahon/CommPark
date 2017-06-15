package w.song.orchid.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface OFragRunStandard {
	/**
	 * 获取参数
	 */
	void getParams();

	/**
	 * 程序初始化
	 */
	View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	/**
	 * 获取组件
	 */
	void getComponent();

	/**
	 * 设置视图
	 */
	void setView();

	/**
	 * 设置监听
	 */
	void setListener();

	/**
	 * 最后的处理
	 */
	void last();
}
