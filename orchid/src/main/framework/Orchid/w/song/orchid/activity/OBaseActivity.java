package w.song.orchid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import w.song.orchid.util.BusinessManager;
import w.song.orchid.util.MyTools;
import w.song.orchid.util.OSharedPreferencesHelper;
import willsong.cn.orchid.R;

public abstract class OBaseActivity extends AppCompatActivity {
	public final static List<OBaseActivity> activityList = new LinkedList<OBaseActivity>();
	protected Context mContext;
	protected LayoutInflater mInflater;
	protected Intent mIntent;
	protected Gson mGson;
	protected OSharedPreferencesHelper mOSharedPreferencesHelper;
	protected BusinessManager mBusinessManager;
	private LinearLayout oContainer;
	private RelativeLayout oTitleBg;
	public Button oBack;
	public Button oMenu;
	private TextView oTitleText;
	protected DisplayImageOptions displayImageOptions;
	protected View.OnClickListener mOnClickListener;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 记录每个开启的activity
		// activityList.add(this);
		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		mIntent = getIntent();
		mGson = new Gson();
		mOSharedPreferencesHelper = new OSharedPreferencesHelper(this);
		mBusinessManager = new BusinessManager(this);
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
		displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.ic_launcher)
				.showImageForEmptyUri(R.mipmap.ic_launcher).showImageOnFail(R.mipmap.ic_launcher)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).build();
		activityList.add(this);


	}

	public Intent intentInstance(Class<?> clazz) {
		return new Intent(mContext, clazz);
	}

	public void setContentViewWithTitle(@LayoutRes int layoutResID){
		setContentView(R.layout.activity_obase);
		//组件
		oContainer = (LinearLayout) findViewById(R.id.activity_obase);
		oTitleBg = (RelativeLayout) findViewById(R.id.title_bg);
		oBack = (Button) findViewById(R.id.title_back);
		oMenu = (Button) findViewById(R.id.title_menu);
		oTitleText = (TextView) findViewById(R.id.title_text);

		View body = mInflater.inflate(layoutResID, null);
		ViewGroup.LayoutParams LP=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		body.setLayoutParams(LP);
		oContainer.addView(body);
		oBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnClickListener != null) {
					mOnClickListener.onClick(v);
				} else {
					finish();
				}
			}
		});
	}

	public void setLeftButtonImage(int resId) {
		if (oBack != null)
			oBack.setBackgroundResource(resId);
	}

	public void setLeftButtonText(String text) {
		if (oBack != null)
			oBack.setText(text);
	}

	public void setRightButtonImage(int resId) {
		if (oMenu != null ){
			oMenu.setBackgroundResource(resId);
		}
	}

	public void setLeftButtonVisible(boolean v) {
		if (oBack != null)
			oBack.setVisibility(v ? View.VISIBLE : View.INVISIBLE);
	}

	public void setLeftButtonVisible2(boolean v) {
		oBack.setVisibility(v ? View.VISIBLE : View.GONE);
	}


	public void setRightButtonText(String s) {
		if (oMenu != null)
			oMenu.setText(s);
	}


	public void setRightButtonVisible(boolean v) {
		if (oMenu != null)
			oMenu.setVisibility(v ? View.VISIBLE : View.INVISIBLE);
	}

	public void setRightButtonVisible2(boolean v) {
		if (oMenu != null)
			oMenu.setVisibility(v ? View.VISIBLE : View.GONE);
	}
	public void setTitleText(String text) {
		if (oTitleText != null)
			oTitleText.setText(text);
	}

	public void setTitleTextColor(int resId) {
		if (oTitleText != null)
			oTitleText.setTextColor(getResources().getColor(resId));
	}

	public void setTitleVisible(int visible) {
		oTitleText.setVisibility(visible);
	}

	public void setTitleLeftButtonOnClickListener(final View.OnClickListener onClickListener) {
		mOnClickListener = onClickListener;
	}

	public void setTitleRightButtonOnClickListener(final View.OnClickListener onClickListener) {
		if (oMenu != null) {
			oMenu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onClickListener != null)
						onClickListener.onClick(v);
				}
			});
		}
	}

	/**
	 *
	 * @param type
	 */
	public void refreshView(String type) {

	}

	/**
	 *
	 * @param type
	 */
	public void refreshView(String type, String json) {

	}

	/**
	 */
	public void refreshView(String type, String code, String codeInfo, String json) {

	}

	/**
	 *
	 * @param type
	 */
	public void refreshView(String type, ArrayList<String[]> list) {

	}

	/**
	 *
	 * @param type
	 */
	public void refreshView(String type, List<Map<String, Object>> list) {

	}

	/**
	 *
	 * @param type
	 */
	public void refreshView(String type, Map<String, Object> map) {

	}

	/**
	 *
	 * @param type
	 */
	public void refreshView(String type, String[] strs) {

	}

	public void refreshView(String type, Map<String, Object> tagMap, String json) {

	}

	public void refreshView(String type, Map<String, Object> tagMap, Object value) {

	}

	public void refreshView(String type, Map<String, Object> tagMap, Map<String, Object> valueMap) {

	}

	public void refreshViewForFail(String type, Map<String, Object> tagMap) {

	}

	/**
	 * finish掉所有的Activity
	 */
	public void clearAllActivity() {
		for (Activity act : OBaseActivity.activityList) {
			if (act != null) {
				act.finish();
			}
		}
		activityList.clear();
	}

	/**
	 * finish掉制定的Activity
	 */
	public void clearOneActivity(String className) {
		List<Integer> list = new ArrayList<Integer>();
		int i = 0;
		for (Activity act : activityList) {
			if (act != null) {
				if (className.equals(act.getClass().getName())) {
					act.finish();
					list.add(i);
				}
			}
			i++;
		}
		for (int j : list) {
			activityList.remove(j);
		}
	}

	/**
	 * finish掉制定的Activity
	 */
	public void clearOneActivity(Class<?> clazz) {
		List<Integer> list = new ArrayList<Integer>();
		int i = 0;
		for (Object obj : activityList) {
			if (obj != null) {
				if (clazz.isInstance(obj)) {
					OBaseActivity act = (OBaseActivity) obj;
					act.finish();
					list.add(i);
				}
			}
			i++;
		}
		// for(int j:list){
		// activityList.remove(j);
		// }
	}

	@Override
	protected void onDestroy() {
		// int size = activityList.size();
		// if (size > 0) {
		// activityList.remove(size - 1);
		// }

		super.onDestroy();
	}

	protected static class AnimateFirstDisplayDefultListener extends SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		public AnimateFirstDisplayDefultListener() {

		}

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	public void displayImage(String url, ImageView imageView) {
		ImageLoader.getInstance().displayImage(url, imageView, displayImageOptions,
				new AnimateFirstDisplayDefultListener());
	}

	public String getIntentValue(String key, String defultValue) {
		return MyTools.replaceNull(mIntent.getStringExtra(key), defultValue);
	}

	//获取当天日期：年月日
	public String getTodayDate(){
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		return f.format(c.getTime());
	}
	//获取昨天日期：年月日
	public String getYesterdayDate(){
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		return f.format(c.getTime());
	}
	//获取当前时间：年月日时分秒
	public String getNowDate(){
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		return f.format(c.getTime());
	}
	//获取当前时间：年月日时分秒:yyyyMMddHHmmss
	public String getNowDatess(){
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar c = Calendar.getInstance();
		return f.format(c.getTime());
	}

	/**
	 * 获取当前时间到兑换截止的时间（秒数）
	 * endDate:格式：如：2015-12-26
	 * @return
	 */
	public int getExchangeSurplusDay(String endDate,String startDate){
		try {
			SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date=simpleDateFormat .parse(endDate);
			Date date2=simpleDateFormat .parse(startDate);
			long surplusTime = date.getTime() - date2.getTime();
			return (int) (surplusTime/1000);
		} catch (Exception e) {
			return -1;//计算失败
		}
	}
	/**
	 * java设定一个日期时间，加几秒钟（小时或者天）后得到新的日期
	 * @param day yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public String addDateMinut(String day, int x){
		// 24小时制  引号里面个格式也可以是 HH:mm:ss或者HH:mm等等，很随意的，不过在主函数调用时，要和输入的变量day格式一致
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(day);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (date == null)
			return "";
		System.out.println("front:" + format.format(date)); //显示输入的日期
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, -x);// 24小时制
		date = cal.getTime();
		System.out.println("after:" + format.format(date));  //显示更新后的日期
		cal = null;
		return format.format(date);
	}
	/**
	 * 保留小数点后两位
	 * DecimalFormat is a concrete subclass of NumberFormat that formats decimal numbers.
	 * @param d
	 * @return
	 */
	public double formatDouble(double d) {
		DecimalFormat df = new DecimalFormat("######0.00");
		return Double.parseDouble(df.format(d));
	}
	//获得终端设备号
	public int deviceId(){
		int deviceId = 0;
		TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		String devId = tm.getDeviceId();
		if(!devId.equals("")&&devId.length()>8){
			deviceId = Integer.valueOf(devId.substring(devId.length()-8));
		}else{
			deviceId = Integer.valueOf(devId);
		}
		return deviceId;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
			return  false;
		}
		return super.onKeyDown(keyCode, event);
	}
	//清除文件夹的文件
	public void emptyFolder(File file){
//        String[] childFilePaths = file.getParentFile().list();
		String[] childFilePaths = file.list();
		if(childFilePaths==null){
			return;
		}
		String picSaveNum = mBusinessManager.getPicNum();//保留数量
		String picSaveDay = mBusinessManager.getPicDay();//保留天数(保留当天+天数)
		int saveNum = 0;
		int saveDay = 0;
		try{
			saveNum = Integer.parseInt(picSaveNum);
			saveDay = Integer.parseInt(picSaveDay);
		}catch (NumberFormatException e){
		}
          if(!("").equals(picSaveNum)&&!("0").equals(picSaveNum)){//判断保留数量
			  int msize = childFilePaths.length;
			  if(msize > saveNum){
				  for (String childFilePath : childFilePaths) {
					  File childFile = new File(file.getPath() + File.separator + childFilePath);
					  Log.i("childFile",""+file.getPath() + File.separator + childFilePath);
					  if(msize>saveNum){
						  childFile.delete();
						  msize--;
					  }else{
						break;
					  }
				  }
			  }
		  }else if(!("").equals(picSaveDay)&&!("0").equals(picSaveDay)){//判断保留天数
			  for (String childFilePath : childFilePaths) {
				  File childFile = new File(file.getPath() + File.separator + childFilePath);
				  Log.i("childFile",""+file.getPath() + File.separator + childFilePath);
				  String[] mPath = childFilePath.split("_");
				  try{
					  int datePath = Integer.parseInt(mPath[1]);
					  if(datePath < getaddDate(saveDay)){
						  childFile.delete();
					  }
				  }catch (Exception e){
				  }
			  }
		  }else{//删除所有
			  for (String childFilePath : childFilePaths) {
				  File childFile = new File(file.getPath() + File.separator + childFilePath);
				  Log.i("childFile",""+file.getPath() + File.separator + childFilePath);
				  childFile.delete();
			  }
		  }
	}
	/**
	 * 字符串转byte数组
	 * */
	public static byte[] strTobytes(String str){
		byte[] b=null,data=null;
		try {
			b = str.getBytes("utf-8");
			data=new String(b,"utf-8").getBytes("gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}



	/**
	 * 判定输入汉字
	 *
	 * @param c
	 * @return
	 */
	public boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}
	/**
	 * 检测String是否全是中文
	 *
	 * @param name
	 * @return
	 */
	public boolean checkNameChese(String name) {
		boolean res = true;
		char[] cTemp = name.toCharArray();
		for (int i = 0; i < name.length(); i++) {
			if (!isChinese(cTemp[i])) {
				res = false;
				break;
			}
		}
		return res;
	}
	public int getScreenWidth(){
		WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}
	public int getScreenHeight(){
		WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}

	public int datetime(String enterTime, String exitTime) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = null;
		Date date = null;
		try {
			now = df.parse(exitTime);
			date = df.parse(enterTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long l = now.getTime() - date.getTime();
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
		return (int) l / 1000;
	}

	public String differenceTime(String enterTime, String exitTime) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = null;
		Date date = null;
		try {
			now = df.parse(exitTime);
			date = df.parse(enterTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long l = now.getTime() - date.getTime();
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
		return "" + day + "天" + hour + "小时" + min + "分" + s + "秒";
	}

	/**
	 * 字符串转换unicode
	 */
	public String string2Unicode(String string) {

		StringBuffer unicode = new StringBuffer();

		for (int i = 0; i < string.length(); i++) {

			// 取出每一个字符
			char c = string.charAt(i);

			// 转换为unicode
			unicode.append("\\u" + Integer.toHexString(c));
		}

		return unicode.toString();
	}

	/**
	 * unicode 转字符串
	 */
	public String unicode2String(String unicode) {

		StringBuffer string = new StringBuffer();

		String[] hex = unicode.split("\\\\u");

		for (int i = 1; i < hex.length; i++) {

			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);

			// 追加成string
			string.append((char) data);
		}

		return string.toString();
	}
	/**
	 * 时差（秒）
	 * @param enterTime
	 * @param exitTime
	 * @return
	 */
	public int datetimeToSeconds(Date enterTime,Date exitTime){
		long l=exitTime.getTime()-enterTime.getTime();
		System.out.println((int) l/1000 + "");
		return (int) l/1000;
	}
	//时间格式转换 yyyy-MM-dd HH:mm:ss转yyyyMMddHHmmss
    //HH返回的是24小时制的时间
    //hh返回的是12小时制的时间
	public static String getDateChange2(String time){
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = formatter.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf2.format(date);
	}
	/* 使用java正则表达式去掉多余的.与0
	 * @param s
	 * @return
	 */
	public String subZeroAndDot(String s){
		if(s.indexOf(".") > 0){
			s = s.replaceAll("0+?$", "");//去掉多余的0
			s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
		}
		return s;
	}
	//去处小数点后多余的0
	public String subZeroAndDots(double d){
		NumberFormat nf = NumberFormat.getInstance();
		String str = nf.format(d);
		return str;
	}
	/**
	 * 判断时间是否在时间段内
	 *
	 * @param strDate
	 *            当前时间 yyyy-MM-dd HH:mm:ss
	 * @param strDateBegin
	 *            开始时间 00:00:00
	 * @param strDateEnd
	 *            结束时间 00:05:00
	 * @return
	 */
	public boolean isInDate(String strDate, String strDateBegin,String strDateEnd) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//		String strDate = sdf.format(date);   //2016-12-16 11:53:54
        // 截取当前时间时分秒 转成整型
		int  tempDate=Integer.parseInt(strDate.substring(11, 13)+strDate.substring(14, 16)+strDate.substring(17, 19));
       // 截取开始时间时分秒  转成整型
		int  tempDateBegin=Integer.parseInt(strDateBegin.substring(0, 2)+strDateBegin.substring(3, 5)+strDateBegin.substring(6, 8));
       // 截取结束时间时分秒  转成整型
		int  tempDateEnd=Integer.parseInt(strDateEnd.substring(0, 2)+strDateEnd.substring(3, 5)+strDateEnd.substring(6, 8));

		if ((tempDate >= tempDateBegin && tempDate <= tempDateEnd)) {
			return true;
		} else {
			return false;
		}
	}
	//判断两个日期是否为同一天
	public boolean isSameDay(String data1){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d2 = new Date();
		String data2 = sdf.format(d2);
		if (data1.equals(data2)) {
			return true;
		} else {
			return false;
		}
	}
	//当前日期前后n天的算法
	public int getaddDate(int n){
		SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd");
		//当前日期的七天前的日期
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.set(Calendar.DATE, mCalendar.get(Calendar.DATE) - n);
		Date SevenAgoTime=mCalendar.getTime();
		String mdate = DateFormat.format(SevenAgoTime);
		return Integer.valueOf(mdate);
	}

}
