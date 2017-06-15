package w.song.orchid.util;

import java.io.ByteArrayInputStream;
/**
 * 工具类
 */
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.format.Time;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

import willsong.cn.orchid.R;

@SuppressLint("SimpleDateFormat")
public class MyTools {
	private static String TAG = "MyTools";

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return String[0]版本名 String[1]升级序列
	 */
	public static String[] getVersion(Context context) {
		PackageManager manager = context.getPackageManager();
		String[] version = new String[2];
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		version[0] = info.versionName; // 版本名
		version[1] = ("" + info.versionCode).trim();// 升级序列
		return version;

	}

	public static String getImei(Context context) {
		return MyTools.replaceNull(
				((TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE)).getDeviceId(), "");
	}

	public static String getImsi(Context context) {
		return MyTools.replaceNull(
				((TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE)).getSubscriberId(), "");
	}

	public  static String  getSysVersionCode(){
		return android.os.Build.VERSION.RELEASE;
	}



	/**
	 * dip转换成px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dipToPx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px转换成dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int pxToDip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 字符串空判断
	 * 
	 * @param arg
	 * @return true为空（包括“null”） false 反之
	 */
	public static boolean isNullOrAirForString(String arg) {
		return arg == null || "".equals(arg.trim()) || "null".equals(arg);
	}

	/**
	 * 等待框
	 * 
	 * @param context
	 * @param info_0
	 *            标题
	 * @param info_1
	 *            正文
	 * @param picture
	 *            R.drawable.xx
	 * @return ProgressDialog
	 */
	public static ProgressDialog getWaitDialog(Context context, String info_0, String info_1, int picture) {
		ProgressDialog m_pDialog = new ProgressDialog(context);
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		// 设置ProgressDialog 标题
		m_pDialog.setTitle(info_0);

		// 设置ProgressDialog 提示信息
		m_pDialog.setMessage(info_1);

		// 设置ProgressDialog 标题图标
		m_pDialog.setIcon(picture);

		// 设置ProgressDialog 的进度条是否不明确
		m_pDialog.setIndeterminate(false);

		// 设置ProgressDialog 是否可以按退回按键取消
		m_pDialog.setCancelable(false);
		m_pDialog.show();
		return m_pDialog;
	}

	/**
	 * 等待框 可以将重写的m_pDialog传进来
	 * 
	 * @param m_pDialog
	 *            等待框
	 * @param info_0
	 *            标题
	 * @param info_1
	 *            正文
	 * @param picture
	 *            R.drawable.xx
	 * @return ProgressDialog
	 */
	public static ProgressDialog getWaitDialog(ProgressDialog m_pDialog, String info_0, String info_1, int picture) {
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		// 设置ProgressDialog 标题
		m_pDialog.setTitle(info_0);

		// 设置ProgressDialog 提示信息
		m_pDialog.setMessage(info_1);

		// 设置ProgressDialog 标题图标
		m_pDialog.setIcon(picture);

		// 设置ProgressDialog 的进度条是否不明确
		m_pDialog.setIndeterminate(false);

		// 设置ProgressDialog 是否可以按退回按键取消
		m_pDialog.setCancelable(false);
		m_pDialog.show();
		return m_pDialog;
	}

	/**
	 * 等待框 没有标题 可以将重写的m_pDialog传进来
	 * 
	 * @param m_pDialog
	 *            等待框
	 * @param info_1
	 *            正文
	 * @return ProgressDialog
	 */
	public static ProgressDialog getWaitDialog(ProgressDialog m_pDialog, String info_1) {
		try {
			m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// 设置ProgressDialog 提示信息
			m_pDialog.setMessage(info_1);

			// 设置ProgressDialog 的进度条是否不明确
			m_pDialog.setIndeterminate(false);

			// 设置ProgressDialog 是否可以按退回按键取消
			m_pDialog.setCancelable(false);
			m_pDialog.show();
		} catch (Exception e) {
			MyLog.e(TAG, e.getMessage());
		}

		return m_pDialog;
	}

	/**
	 * 查询结果显示
	 *
	 * @param info
	 */
	public static void dialogIntro(String info, Context context) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(info);
			builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 查询结果显示
	 *
	 */
	public static void dialogIntro(Spanned spannedInfo, Context context) {
		try {
			// TextView tv=new TextView(context);
			// LinearLayout.LayoutParams lp=new
			// LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			// tv.setLayoutParams(lp);
			// tv.setBackgroundColor(Color.BLACK);
			// tv.setTextSize(MyTools.dipToPx(context, 14));
			// tv.setText(spannedInfo);
			// tv.setTextColor(Color.WHITE);
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			// builder.setView(tv);
			builder.setCancelable(false);
			builder.setMessage(spannedInfo);
			builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 查询结果显示 这里支持标签 异常已经捕获
	 * 
	 * @param isShow
	 *            ture 对话框显示 反之
	 * @param info
	 * 
	 */
	public static void dialogIntro(boolean isShow, String info, Context context) {
		if (isShow == true) {
			// info = "<font color='black'>" + info + "<\font>";
			dialogIntro(Html.fromHtml(info), context);
		}

	}

	/**
	 * 查询结果显示
	 * 
	 * @param title
	 *            标题
	 * @param info
	 *            正文
	 */
	public static void dialogIntro(Spanned title, Spanned info, Activity activity) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle(title);
			builder.setMessage(info);
			builder.setCancelable(false);
			builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showToastShort(boolean isShow, String info, Context context) {
		if (isShow) {
			try {
				Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void showToastLong(boolean isShow, String info, Context context) {
		if (isShow) {
			try {
				Toast.makeText(context, info, Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * air为空，null，“null”时替换成tag 反之不替换
	 * 
	 * @param air
	 * @param tag
	 * @return
	 */
	public static String replaceNull(String air, String tag) {
		if (MyTools.isNullOrAirForString(air)) {
			return tag;
		}
		return air;
	}

	/**
	 * air为null时替换成tag 反之不替换
	 * 
	 * @param air
	 * @param tag
	 * @return
	 */
	public static int replaceNull(Integer air, int tag) {
		if (null == air) {
			return tag;
		}
		return air;
	}

	/**
	 * 取小数点后num位
	 * 
	 * @param value
	 * @param num
	 * @return
	 */
	public static double decimalAdoptPosition(double value, int num) {
		long lValue = Math.round(value * Math.pow(10, num)); // 四舍五入
		double result = lValue / Math.pow(10, num);
		return result;
	}

	/**
	 * 年利率转化成月利率 String型
	 * 
	 * @param s
	 *            被转换字符串数字
	 * @param d
	 *            转换成百分数后带的小数位数
	 * @return 返回结果需要后面带有%
	 */
	public static String yearToMouthForRate(String s, int d) {
		if (s == null || "".equals(s)) {
			s = "";
		}
		double f = Double.valueOf(s);
		f = (f / 12) * 100;
		f = decimalAdoptPosition(f, 2);
		s = "" + f;
		return s;
	}

	/**
	 * 百分数转换 String型
	 * 
	 * @param s
	 *            被转换字符串数字
	 * @param d
	 *            转换成百分数后带的小数位数
	 * @return 返回结果需要后面带有%
	 */
	public static String toPercent(String s, int d) {
		if (s == null || "".equals(s)) {
			s = "";
		}
		double f = Double.valueOf(s);
		f = f * 100;
		f = decimalAdoptPosition(f, d);
		s = "" + f;
		return s;
	}

	/**
	 * 删除文件夹及其所有内容 当前文件夹不会被删除
	 * 
	 * @return FolderDoNotExist文件夹不存在 thisIsNotFolder这不是文件夹 success删除成功 fail删除失败
	 */
	public static String deleteFoderAndFile(String folderPath) {
		String flag = "";
		File file = new File(folderPath);
		try {
			if (!file.exists()) {
				return flag = "FolderDoNotExist";
			}
			if (!file.isDirectory()) {
				return flag = "thisIsNotFolder";
			}
			String[] tempList = file.list();
			File temp = null;
			for (int i = 0; i < tempList.length; i++) {
				temp = new File(folderPath + "/" + tempList[i]);
				if (temp.isFile()) {
					temp.delete();
				}
				if (temp.isDirectory()) {
					deleteFoderAndFile(folderPath + "/" + tempList[i]);// 先删除文件夹里面的文件
					deleteFolder(folderPath + "/" + tempList[i]);// 删除空文件夹
					flag = "success";
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			flag = "fail";
		}

		return flag;

	}

	/**
	 * 删除空文件夹
	 *
	 *            文件夹的地址
	 * @return 0 成功 1 失败 2 不存在该文件夹
	 * @throws Exception
	 */
	public static int deleteFolder(String folderPath) {
		File folder = new File(folderPath);
		if (folder.exists()) {
			try {
				folder.delete();
				return 1;
			} catch (Exception ex) {
				ex.printStackTrace();
				return 0;
			}
		} else {
			return 2;
		}

	}

	/**
	 * format字段
	 * 
	 * @param 0.yyyy-MM-dd
	 *            HH:mm:ss
	 * @param 1.yyyy-MM-dd
	 *            HH:mm
	 * @param 2.yyyy-MM-dd
	 * @param 3.yyyy-M-d
	 *            HH:mm
	 * @param 4.HH:mm
	 * @param 5.MM/dd
	 *            HH:mm
	 * @param 6.M-d
	 *            7.EEEE 星期
	 * @param 8.H:m
	 * @param 9.yyyy-M-d
	 *            H:m
	 * @param 10.H:mm
	 * @param 11.yyyy年MM月dd日
	 * @param 12.yyyy年M月d日
	 * @param 13.yyyy-M-d
	 * @param 14.dd/MM/yyyy
	 * @param 15.yyyy/MM/dd
	 *            HH:mm:ss
	 * @param 16.yyyy/MM/dd
	 *            HH:mm
	 * @param 17.yyyy年
	 * @param 18.yyyy年M月
	 * @param 19.yyyy-M-dd
	 */
	public static String[] FORMATDATE = { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "yyyy-M-d HH:mm",
			"HH:mm", "MM/dd HH:mm", "M-d", "EEEE", "H:m", "yyyy-M-d H:m", "H:mm", "yyyy年MM月dd日", "yyyy年M月d日",
			"yyyy-M-d", "dd/MM/yyyy", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy年", "yyyy年M月", "yyyy-M-dd" };

	/**
	 * 日期转换成"yyyy-MM-dd"
	 * 
	 * @param strDate
	 *            格式见FORMATDATE[0]
	 * 
	 * @return ""或者FORMATDATE[2]格式，不会为null
	 * @throws ParseException
	 */
	public static String formatStrDateToAtherStrDate(String strDate) {
		DateFormat df_0 = new SimpleDateFormat(FORMATDATE[0]);
		DateFormat df_1 = new SimpleDateFormat(FORMATDATE[2]);
		Date date_0 = null;
		try {
			date_0 = df_0.parse(strDate.trim());
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		String needDate = df_1.format(date_0);
		return needDate;

	}

	/**
	 * 字符串日期任意格式转换
	 * 
	 * @param strDate
	 *            格式见FORMATDATE[]
	 * @param strDateFormat
	 *            原格式 字段参考FORMATDATE[]
	 * @param atherStrDateFormat
	 *            转换后的格式 字段参考FORMATDATE[]
	 * @return ""或者FORMATDATE[]格式，不会为null
	 * @throws ParseException
	 */
	public static String formatStrDateToAtherStrDate(String strDate, String strDateFormat, String atherStrDateFormat) {
		DateFormat df_0 = new SimpleDateFormat(strDateFormat);
		DateFormat df_1 = new SimpleDateFormat(atherStrDateFormat);
		Date date_0 = null;
		try {
			date_0 = df_0.parse(strDate.trim());
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		String needDate = df_1.format(date_0);
		return needDate;

	}

	/**
	 * 时间字符串格式化
	 * 
	 * @param date
	 * @param format
	 *            格式参考FORMATDATE
	 * @return Date 可以为null
	 */
	public static Date formatDate(String date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		Date d = null;
		try {
			d = df.parse(date);
		} catch (ParseException e) {
			// e.printStackTrace();
		}
		return d;
	}

	/**
	 * 将Date转换成String
	 * 
	 * @param date
	 * @param format
	 *            格式见FORMATDATE
	 * @return String 格式见FORMATDATE
	 */
	public static String formatDateToString(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	/**
	 * 将long型日期转换成String
	 * 
	 * @param date
	 * @param format
	 *            格式见FORMATDATE
	 * @return String 格式见FORMATDATE
	 */
	public static String longDateToString(long date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date d = new Date(date);
		return dateFormat.format(d);
	}

	/**
	 * 时间格式化成星期
	 * 
	 * @param strDate
	 * @param strDateFormat
	 * @param weekFormat
	 * @return 星期
	 */
	public static String StrDateToStringWeek(String strDate, String strDateFormat, String weekFormat) {
		Date date = formatDate(strDate, strDateFormat);
		SimpleDateFormat dateFm = new SimpleDateFormat(weekFormat);
		return dateFm.format(date);
	}

	/**
	 * 数值字符串小数点位数取舍
	 * 
	 * @param value
	 *            数值字符串
	 * @param num
	 *            小数点位数
	 * @return 字符串
	 */
	public static String digitSave(String value, int num) {
		double dValue = Double.valueOf(value.trim());
		return "" + MyTools.decimalAdoptPosition(dValue, num);
	}

	/**
	 * 
	 * @param startTime
	 *            开始日期
	 * @param endTime
	 *            结束日期
	 * @param format
	 *            日期格式 如yyyy-MM-dd
	 * @return -0.1程序错误
	 */
	public static double getDateLong(String startTime, String endTime, String format) {
		SimpleDateFormat sd = new SimpleDateFormat(format);
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long diff;
		try {
			// 获得两个时间的毫秒时间差异
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			long day = diff / nd;// 计算差多少天
			return day;
		} catch (Exception e) {
			e.printStackTrace();
			return -0.1;
		}

	}

	/**
	 * 将Map的value转换成List结构
	 * 
	 * @param map
	 *            注意value值必须是Map<String, Object>类型
	 * @return list
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> mapValueToList(Map<String, Object> map) {
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		for (String key : map.keySet()) {
			Map<String, Object> mapA = (Map<String, Object>) map.get(key);
			list.add(mapA);
		}

		return list;
	}

	/**
	 * 将List转换成Map,key为list节点中的一个字段
	 * 
	 * @param list
	 *            必须是List<Map<String, Object>> 类型
	 * @return list
	 */
	public static Map<String, Object> listToMap(List<Map<String, Object>> list, String key) {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			resultMap.put(("" + map.get(key)).trim(), map);
		}
		return resultMap;
	}

	/**
	 * 文本或者输入框获取文字，并除去首尾空字符
	 * 
	 * @param v
	 *            文本框或者编辑框
	 * @return string文字，可以为空，但不为null
	 */
	public static String getText(TextView v) {
		return (v.getText() + "").trim();
	}

	/**
	 * 字符串数组A映射字符串数组B的方式找出A中字符对应的B中字符
	 * 
	 * @param key
	 *            A中字符
	 * @param StrA
	 *            字符串数组A
	 * @param StrB
	 *            字符串数组B
	 * @return 对应的B中字符
	 */
	public static String strAToMapStrB(String key, String[] StrA, String[] StrB) {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < StrA.length; i++) {
			map.put(StrA[i], StrB[i]);
		}
		return map.get(key);
	}

	/**
	 * 返回一个整型数组一个值的索引
	 * 
	 * @param array
	 * @param key
	 * @return 这个值不存在，则返回-1
	 */
	public static int getIntArrayIndex(int[] array, int key) {
		for (int i = 0; i < array.length; i++) {
			if (key == array[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 生成文件名 由固定名字和手机号码组成
	 * 
	 * @param firstName
	 *            固定名字
	 * @param secondName
	 *            手机号码
	 * @return
	 */
	public static String getFileName(String firstName, String secondName) {
		return firstName + "_" + secondName;
	}

	/** 舍位 */
	public static int ABANDON = 0;
	/** 进位 */
	public static int CARRY = 1;

	/**
	 * 取整，可进位或者舍位
	 * 
	 * @param num
	 * @param type
	 *            值ABANDON/CARRY
	 * @return int
	 */
	public static int getInt(double num, int type) {
		int i = 0;
		if (ABANDON == type)
			i = (int) num;
		if (CARRY == type) {
			int j = (int) num;
			double d = num - j;
			if (d > 0) {
				i = j + 1;
			}
		}
		return i;
	}

	/***
	 * 字符串省略
	 * 
	 * @param str
	 *            要进行省略的字符串
	 * @param length
	 *            截取长度
	 * @return 不足长度的返回原字符串 反之省略部分为...
	 */
	public static String ellipsisString(String str, int length) {
		if (str.length() <= length) {
			return str;
		} else {
			return str.substring(0, length) + "...";
		}
	}

	/***
	 * 字符串省略
	 * 
	 * @param str
	 *            要进行省略的字符串
	 * @param length
	 *            截取长度
	 * @return 不足长度的返回原字符串 反之省略部分为... (注意有 乱码现象)
	 */
	public static String ellipsisStringForByte(String str, int length) {
		try {
			if (str.getBytes("UTF-8").length <= length) {
				return str;
			} else {
				return new String(getNeedLenghtByteString(str, length), "UTF-8") + "...";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "...";
		}
	}

	@SuppressWarnings("finally")
	private static byte[] getNeedLenghtByteString(String str, int length) {
		byte[] bs = new byte[length];
		try {
			byte[] strBs = str.getBytes("UTF-8");
			for (int i = 0; i < length; i++) {
				bs[i] = strBs[i];
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			return bs;
		}
	}

	public static String createChatImgName(int user_id, int group_id, String ExtensionName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");
		return group_id + "_" + user_id + "_" + sdf.format(new Date()) + getExtention(ExtensionName);
	}

	public static String createUserPHotoName(String user_id, String ExtensionName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");
		return user_id + "_T" + sdf.format(new Date()) + getExtention(ExtensionName);
	}

	public static String getExtention(String fileName) {
		int pos = fileName.lastIndexOf(".");
		return fileName.substring(pos);
	}

	// ----短信使用end----
	public final static int MAX1 = 99;
	public final static String _2DOT = "··";
	public final static String _3DOT = "···";

	/**
	 * 大于max值时其他字符串取代之
	 * 
	 * @param num
	 *            比较值
	 * @param max
	 *            被比较的最大值
	 * @param replace
	 *            大于max 取代的符号
	 * @return
	 */
	public static String maxOneNumThenReplaceOther(int num, int max, String replace) {
		if (num > max) {
			return replace;
		} else {
			return "" + num;
		}
	}

	/**
	 * value在ArrayList < HashMap < String,String > > 是否存在
	 * 
	 * @param info
	 * @param list
	 * @return
	 */
	public static boolean valueIsExist(String info, ArrayList<HashMap<String, String>> list) {
		for (HashMap<String, String> map : list) {
			if (map.containsValue(info)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 字符串为纯数字正则表达式
	 */
	public static String ALL_NUM_E = "[0-9]+";

	/**
	 * 判断是否是单数的字符串，是的在前面加个字符0
	 * 
	 * @param num
	 */
	public static String singleToDoubleStrNum(String num) {
		if (num.trim().length() == 1) {
			return "0" + num;
		} else {
			return num;
		}
	}

	/**
	 * 产生0-range的随机整数(不含range)
	 * 
	 * @param range
	 * @return
	 */
	public static int randomInt(int range) {
		Random rand = new Random();
		int i = rand.nextInt(); // int范围类的随机数
		i = rand.nextInt(range); // 生成0-range以内的随机数
		return i;
	}

	/**
	 * 产生0-range的随机整数（不含range？ 未验证）
	 * 
	 * @param range
	 * @return
	 */
	public static int randomInt2(int range) {
		return (int) (Math.random() * range); // 0-range以内的随机数，用Matn.random()方式;
	}

	/**
	 * 验证邮箱地址是否正确
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		boolean flag = false;
		try {
			// String check =
			// "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			String check = "^[a-z0-9A-Z][a-z0-9A-Z\\.\\_\\-]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}

	/**
	 * 判断当前设备是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnect(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		NetworkInfo info = mConnectivity.getActiveNetworkInfo();

		// 能联网
// 不能联网
		return info != null && info.isAvailable();

		// if (info == null || !mConnectivity.getBackgroundDataSetting()) {
		// return false;
		// }
		//
		// int netType = info.getType();
		// int netSubtype = info.getSubtype();
		//
		// if (netType == ConnectivityManager.TYPE_WIFI) {
		// return info.isConnected();
		// } else if (netType == ConnectivityManager.TYPE_MOBILE
		// && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
		// && !mTelephony.isNetworkRoaming()) {
		// return info.isConnected();
		// } else {
		// return false;
		// }
	}

	/**
	 * 获取网络连接类型
	 * 
	 * @param context
	 * @return -1:没有网络; 0：TYPE_MOBILE; 1：TYPE_WIFI
	 */
	public static int getNetworkType(Context context) {

		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();

				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return info.getType();
					}
				}
			}
		} catch (Exception e) {
			Log.v("mcn", e.toString());
		}
		return -1;
	}

	/**
	 * Gps是否打开
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/**
	 * wifi是否打开
	 */
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null
				&& mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED)
				|| mgrTel.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * 判断当前网络是否是wifi网络
	 * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //判断3G网
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 判断当前网络是否是3G网络
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean is3G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE;
	}

	/**
	 * 判断手机是否是飞行模式
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAirplaneMode(Context context) {
		int isAirplaneMode = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
		return (isAirplaneMode == 1);
	}

	/**
	 * 判断sim卡是否正常
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isSimReady(Context context) {
		TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		int simState = mTelephonyManager.getSimState();
		return simState == TelephonyManager.SIM_STATE_READY;

	}

	/**
	 * 判断存储卡是否存在
	 * 
	 * @return
	 */
	public static boolean existSDcard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * 判断是否在存储卡上创建指定文件
	 * 
	 * @param folder
	 *            文件夹名
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static boolean checkFsWritable(String folder, String fileName) {
		String directoryName = Environment.getExternalStorageDirectory() + "/" + folder;
		File directory = new File(directoryName); // 在SD卡上创建指定的目录

		if (!directory.isDirectory()) { // 测试创建目录
			if (!directory.mkdirs()) {
				return false;
			}
		}

		File f = new File(directoryName, fileName);
		try {
			// Remove stale file if any
			if (f.exists()) {
				f.delete();
			}
			if (!f.createNewFile()) { // 测试创建文件
				return false;
			}
			f.delete();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	public static HashMap<String, String> getDeviceInfo(Context context) {
		HashMap<String, String> deviceInfoMap = new HashMap<String, String>();
		try {
			// 获取手机厂商
			deviceInfoMap.put("manufacturer", android.os.Build.MANUFACTURER);
			// 获取手机型号
			deviceInfoMap.put("model", android.os.Build.MODEL);
			// SDK版本号
			deviceInfoMap.put("sdk", android.os.Build.VERSION.SDK);
			// android系统版本号
			deviceInfoMap.put("release", android.os.Build.VERSION.RELEASE);

			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			// 唯一的设备ID
			deviceInfoMap.put("deviceId", tm.getDeviceId());

			// 设备的软件版本号：
			// 例如：the IMEI/SV(software version) for GSM phones.
			deviceInfoMap.put("deviceSoftwareVersion", tm.getDeviceSoftwareVersion());

			// 手机号 GSM手机的 MSISDN.
			deviceInfoMap.put("line1Number", tm.getLine1Number());

			/*
			 * 当前使用的网络类型： 例如： NETWORK_TYPE_UNKNOWN 网络类型未知 0 NETWORK_TYPE_GPRS
			 * GPRS网络 1 NETWORK_TYPE_EDGE EDGE网络 2 NETWORK_TYPE_UMTS UMTS网络 3
			 * NETWORK_TYPE_HSDPA HSDPA网络 8 NETWORK_TYPE_HSUPA HSUPA网络 9
			 * NETWORK_TYPE_HSPA HSPA网络 10 NETWORK_TYPE_CDMA CDMA网络,IS95A 或
			 * IS95B. 4 NETWORK_TYPE_EVDO_0 EVDO网络, revision 0. 5
			 * NETWORK_TYPE_EVDO_A EVDO网络, revision A. 6 NETWORK_TYPE_1xRTT
			 * 1xRTT网络 7
			 */
			deviceInfoMap.put("networkType", String.valueOf(tm.getNetworkType()));

			/*
			 * 服务商名称： 例如：中国移动、联通 SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
			 */
			deviceInfoMap.put("simOperatorName", tm.getSimOperatorName());
			/*
			 * SIM卡的序列号： 需要权限：READ_PHONE_STATE
			 */
			deviceInfoMap.put("simSerialNumber", tm.getSimSerialNumber());

		} catch (Exception ex) {

		}

		return deviceInfoMap;
	}

	public static String getSimSerialNumber(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return MyTools.replaceNull(tm.getSimSerialNumber(),"");
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versionName;
	}

	public static int getAppVersionCode(Context context) {
		int versioncode = 0;
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versioncode = pi.versionCode;
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versioncode;
	}

	public static float getDensity(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.density;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static String groupSendSeparator = "";

	// public static String getGroupSendSeparator(Context context)
	// {
	// // final String[] models = {"samsung_GT-I9108",
	// "samsung_SCH-i909","samsung_GT-S5830","samsung_GT-I9228"};
	//
	// if (groupSendSeparator.length() == 0)
	// {
	// // HashMap<String,String> deviceInfo = getDeviceInfo(context);
	// // String model = deviceInfo.get("manufacturer") + "_" +
	// deviceInfo.get("model");
	// // groupSendSeparator = ";";
	// // for (String s : models)
	// // {
	// // if (s.equalsIgnoreCase(model))
	// // {
	// // groupSendSeparator = ",";
	// // break;
	// // }
	// // }
	// SharedPreferencesHelper sp = new SharedPreferencesHelper(context,
	// Constant.P_FILE);
	// String separator = AppTools.trim(sp.getValue(Constant.P_SMS_SEPARATOR));
	// if (separator.length() > 0)
	// {
	// groupSendSeparator = separator;
	// }
	// else
	// {
	// return ";";
	// }
	// }
	// return groupSendSeparator;
	// }

	/**
	 * 判断app是否在前台运行
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isAppOnForeground(Activity activity) {
		String packageName = "com.trendsnet.quanquan";
		ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否锁屏
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isScreenLocked(Context c) {
		android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c.getSystemService(Context.KEYGUARD_SERVICE);
		return !mKeyguardManager.inKeyguardRestrictedInputMode();
	}

	/**
	 * 获得本机手机号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String TelePhoneNumber = mTelephonyMgr.getLine1Number();
		if (TelePhoneNumber == null || "".equals(TelePhoneNumber)) {
			return TelePhoneNumber;
		}
		// 判断获得的手机是否为11位，特例+086135********;
		String temPhone = "";
		if (TelePhoneNumber.length() > 11) {
			temPhone = TelePhoneNumber.substring(TelePhoneNumber.length() - 11, TelePhoneNumber.length());
		} else {
			temPhone = TelePhoneNumber;
		}

		return temPhone;
	}

	/**
	 * 邮箱校验
	 */
	public static boolean checkEmail(String mail) {
		String regex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mail);
		return m.find();
	}

	/**
	 * 是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		Pattern p = Pattern.compile("[0-9]");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	public static String formatTimeStampString(Context context, long when) {
		return formatTimeStampString(context, when, false);
	}

	public static String formatTimeStampString(long when) {
		String retDate = "";
		Date date = new Date();
		date.setTime(when);
		String strDate = MyTools.formatDateToString(date, MyTools.FORMATDATE[2]);
		String strDate2 = MyTools.formatDateToString(date, MyTools.FORMATDATE[0]);
		if (CalendarTool.isToday(strDate)) {
			retDate = MyTools.formatStrDateToAtherStrDate(strDate2, MyTools.FORMATDATE[0], MyTools.FORMATDATE[8]);
		} else {
			retDate = MyTools.formatStrDateToAtherStrDate(strDate2, MyTools.FORMATDATE[0], MyTools.FORMATDATE[6]);
		}
		return retDate;
	}

	public static String formatTimeStampString(Context context, long when, boolean fullFormat) {
		Time then = new Time();
		then.set(when);
		Time now = new Time();
		now.setToNow();

		// Basic settings for formatDateTime() we want for all cases.
		int format_flags = android.text.format.DateUtils.FORMAT_NO_NOON_MIDNIGHT
				| android.text.format.DateUtils.FORMAT_ABBREV_ALL | android.text.format.DateUtils.FORMAT_CAP_AMPM;

		// If the message is from a different year, show the date and year.
		if (then.year != now.year) {
			format_flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR
					| android.text.format.DateUtils.FORMAT_SHOW_DATE;
		} else if (then.yearDay != now.yearDay) {
			// If it is from a different day than today, show only the date.
			format_flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
		} else {
			// Otherwise, if the message is from today, show the time.
			format_flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
		}

		// If the caller has asked for full details, make sure to show the date
		// and time no matter what we've determined above (but still make
		// showing
		// the year only happen if it is a different year from today).
		if (fullFormat) {
			format_flags |= (android.text.format.DateUtils.FORMAT_SHOW_DATE
					| android.text.format.DateUtils.FORMAT_SHOW_TIME);
		}

		return android.text.format.DateUtils.formatDateTime(context, when, format_flags);
	}

	/**
	 * 保留小数点两位,四舍五入,但不补0
	 * 
	 * @param value
	 * @return
	 */
	public static double getDoubleDotPositon(double value) {
		long l1 = Math.round(value * 100); // 四舍五入
		double ret = l1 / 100.0; // 注意：使用 100.0 而不是 100
		return ret;
	}

	/**
	 * 保留小数点指定位,四舍五入,但不补0
	 * 
	 * @param value
	 * @param pointPos
	 *            保留小数点位数
	 * @return
	 */
	public static double getDoubleDotPositon(double value, int pointPos) {
		int v = (int) Math.pow(10, pointPos);
		long l1 = Math.round(value * v); // 四舍五入
		double dv = Double.valueOf(v + "");
		double ret = l1 / dv;
		return ret;
	}

	/**
	 * 保留小数点两位，直接截取不四舍五入，不足补0
	 * 
	 * @param value
	 * @return String
	 */
	public static String getDoubleDotPositon2(double value) {
		// DecimalFormat df = new DecimalFormat("#,##0.00");// 不足补0,这个为三位会有个逗号
		DecimalFormat df = new DecimalFormat("0.00");// 不足补0
		// DecimalFormat df=new DecimalFormat("#.##");//不足不补0
		return df.format(value);
	}

	/**
	 * 保留小数点两位，四舍五入，不足补0
	 * 
	 * @param value
	 * @return String
	 */
	public static String getDoubleDotPositon3(double value) {
		double num = getDoubleDotPositon(value);
		return getDoubleDotPositon2(num);
	}

	/**
	 * 保留小数点指定位，直接截取不四舍五入，不足补0
	 * 
	 * @param value
	 * @return String
	 */
	public static String getDoubleDotPositon5(double value, int pointPos) {
		String format = "0.";
		for (int i = 0; i < pointPos; i++) {
			format += "0";
		}
		DecimalFormat df = new DecimalFormat(format);// 不足补0
		return df.format(value);
	}

	/**
	 * 保留小数点指定位，四舍五入，不足补0
	 * 
	 * @param value
	 * @return String
	 */
	public static String getDoubleDotPositon6(double value, int pointPos) {
		double num = getDoubleDotPositon(value, pointPos);
		return getDoubleDotPositon5(num, pointPos);
	}

	/**
	 * 获取文件名和行数
	 * 
	 * @return
	 */
	public static String getLineInfo() {
		StackTraceElement ste = new Throwable().getStackTrace()[1];
		return ste.getFileName() + ": Line " + ste.getLineNumber();
	}

	/**
	 * 获取文件名和行数
	 * 
	 * @return
	 */
	public static int getByteLength(String str) {
		return str.getBytes().length;
	}

	/**
	 * 字节数组反转
	 * 
	 * @param pre
	 * @return
	 */
	public static byte[] invertBytes(byte[] pre) {
		int len = pre.length;
		byte[] now = new byte[len];
		for (int i = 0; i < len; i++) {
			now[len - 1 - i] = pre[i];
		}
		return now;
	}

	/**
	 * 字符数组反转
	 * 
	 * @param pre
	 * @return
	 */
	public static char[] invertChars(char[] pre) {
		int len = pre.length;
		char[] now = new char[len];
		for (int i = 0; i < len; i++) {
			now[len - 1 - i] = pre[i];
		}
		return now;
	}

	/**
	 * 获取显示器尺寸
	 * 
	 * @param context
	 * @return [0]宽 [1]高
	 */
	public static int[] getDisplaySize(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		((FragmentActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
		return new int[] { metric.widthPixels, metric.heightPixels };
	}

	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	/** Bitmap 压缩 */
	public static Bitmap scalBitmap(Bitmap image, int size) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 85, out);
		float zoom = (float) Math.sqrt(size * 1024 / (float) out.toByteArray().length);

		Matrix matrix = new Matrix();
		matrix.setScale(zoom, zoom);

		Bitmap result = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

		out.reset();
		result.compress(Bitmap.CompressFormat.JPEG, 85, out);
		while (out.toByteArray().length > size * 1024) {
			System.out.println("压缩中大小：" + out.toByteArray().length / 1024 + "M");
			matrix.setScale(0.9f, 0.9f);
			result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
			out.reset();
			result.compress(Bitmap.CompressFormat.JPEG, 85, out);
		}
		return result;
	}

	/** 加载Bitmap 设置大小（file） */
	public static Bitmap decodeFileBitmap(String path, int size) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap image = BitmapFactory.decodeFile(path, opts);
		int height = opts.outHeight * size / opts.outWidth;
		opts.outHeight = height;
		opts.outWidth = size;
		opts.inJustDecodeBounds = false;
		image = BitmapFactory.decodeFile(path, opts);
		return image;
	}
	
	/** 设置Bitmao大小 避免出现OOM */
	public static Bitmap createImageThumbnail(String filePath) {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);

		opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
		opts.inJustDecodeBounds = false;

		try {
			bitmap = BitmapFactory.decodeFile(filePath, opts);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bitmap;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128
				: (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	
	
	/**
	 * 调用浏览器
	 * 
	 * @param url
	 * @param context
	 */
	public static void callBrowser(String url, Context context) {
		MyLog.i("callBrowser url====", url);
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(url);
		intent.addCategory(Intent.CATEGORY_BROWSABLE);
		intent.setData(content_url);
		context.startActivity(intent);
	}

	/**
	 * 版本更新提示
	 * 
	 */
	public static void showUpDateDialog(final Context context, String VersionInfo, final String url) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("发现新版本").setIcon(R.mipmap.ic_launcher);
		builder.setMessage(Html.fromHtml(VersionInfo));
		builder.setPositiveButton("以后再说", null);
		builder.setNegativeButton("立即更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callBrowser(url, context);
			}
		});
		builder.create().show();
	}

	/**
	 * 强制版本更新
	 * 
	 */
	public static void showUpDateDialog2(final Context context, String VersionInfo, final String url) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("发现新版本").setIcon(R.mipmap.ic_launcher);
		builder.setMessage(Html.fromHtml(VersionInfo));
		builder.setPositiveButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showToastShort(true, "版本太旧，请更新版本", context);
			}
		});
		builder.setNegativeButton("立即更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callBrowser(url, context);
			}
		});
		builder.setCancelable(true);
		builder.create().show();
	}

	/**
	 * Gallery居左设置 单位px
	 * 
	 * @param gallery
	 * @param galleryW
	 *            gallery总宽
	 * @param galleryItemW
	 *            菜单项宽度
	 */
	public static void setGalleryAlignLeft(Gallery gallery, int galleryW, int galleryItemW, int gallerySpacing) {
		MarginLayoutParams mlp = (MarginLayoutParams) gallery.getLayoutParams();
		mlp.width = 2 * galleryW;
		int spacingNum = galleryW / (galleryItemW + gallerySpacing) + 1;
		int offset = mlp.width / 2 - galleryItemW / 2 - spacingNum * gallerySpacing;
		mlp.setMargins(-offset, 0, 0, 0);
		gallery.setLayoutParams(mlp);
	}

	/**
	 * 获取activity的宽和高
	 * 
	 * @param activity
	 *            0.宽 1.高
	 * @return
	 */
	public static int[] getActivitySize(Activity activity) {
		int[] size = new int[2];
		Rect outRect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
		size[0] = outRect.width();
		size[1] = outRect.height();
		return size;
	}

	/**
	 * 是否显示状态栏
	 * 
	 * @param activity
	 * @param enable
	 */
	public static void isShowSateTitle(Activity activity, boolean enable) {
		if (enable) {
			WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
			attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			activity.getWindow().setAttributes(attr);
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		} else {
			WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
			lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			activity.getWindow().setAttributes(lp);
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}

	/**
	 * 账号密码加密
	 */
	public static String base64_encode(String code) {
		String str1 = Base64.encodeToString(code.getBytes(), Base64.DEFAULT);
		// 把每4个字符前面加一个随机字符 作为干扰字符
		Random rand = new Random();
		int i = 0;
		String ss = "";
		if (str1.length() % 4 == 0) {
			i = str1.length() / 4;
		} else {
			i = str1.length() / 4 + 1;
		}
		String strs[] = new String[i];
		for (int j = 0; j < i; j++) {
			if (j == i - 1) {
				strs[j] = str1.substring(j * 4, str1.length());
			} else {
				strs[j] = str1.substring(j * 4, (j + 1) * 4);
			}
			ss += (rand.nextInt(9) + 1) + strs[j];
		}
		return Base64.encodeToString(ss.getBytes(), Base64.DEFAULT);
	}

	/**
	 * 判断字符串是否为网址
	 */
	public static boolean isUrl(String str) {
		String regEx = "http://(([a-zA-z0-9]|-){1,}\\.){1,}[a-zA-z0-9]{1,}-*";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/** 根据本地路径获取文件的大小(KB) */
	public static long getFileSize(String filePath) {
		File file = new File(filePath);
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				size = fis.available();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return size / 1024;
	}

	/**
	 * 字节数组转int,适合转高位在前低位在后的byte[]
	 *
	 * @param bytes
	 * @return
	 */
	public static long byteArrayToLong(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);
		long result = 0;
		try {
			int len = dis.available();
			if (len == 1) {
				result = dis.readByte();
			} else if (len == 2) {
				result = dis.readShort();
			} else if (len == 4) {
				result = dis.readInt();
			} else if (len == 8) {
				result = dis.readLong();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dis.close();
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * int转byte[]，高位在前低位在后
	 *
	 * @param value
	 * @return
	 */
	public static byte[] int2byte(int res) {
		byte[] targets = new byte[3];

		targets[0] = (byte) (res & 0xff);// 最低位
		targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
		targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
//		targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
		return targets;
	}

	//byte数组
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 单字节转成String ascii
	 * @param suf
	 * @return
	 */
	public static String getSuf(byte[] suf){
		String nRcvString;
		StringBuffer  tStringBuf=new StringBuffer ();
		char[] tChars=new char[suf.length];

		for(int i=0;i<suf.length;i++) {
			tChars[i] = (char) suf[i];
		}
		tStringBuf.append(tChars);

		nRcvString=tStringBuf.toString();          //nRcvString从tBytes转成了String类型的"123"
		return nRcvString;
	}
}
