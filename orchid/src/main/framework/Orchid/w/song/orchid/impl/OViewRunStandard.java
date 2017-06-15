package w.song.orchid.impl;

import android.content.Context;
import android.util.AttributeSet;

public interface OViewRunStandard {
	/**
	 * 获取参数
	 */
	void getParams();

	/**
	 * 程序初始化
	 */
	void init(Context context,AttributeSet attrs);

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
