package w.song.orchid.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;
import biz.source_code.base64Coder.Base64Coder;
import it.sauronsoftware.base64.Base64;
import w.song.orchid.exception.HttpException;

public class Http {
	private final static String TAG = "Http";

	/*
	 * 表示代号
	 */

	public final static String SEN = "serverError errorNum:";// 服务器错误可加错误码
	public final static String SRE = "serverReturnSuccess";// 服务器正常返回信息
	public final static String NE = "netException";// 访问网络异常
	public final static String NAE = "netAbortException";// 访问网络强制中断异常
	public final static String CE = "codeException";// 程序出错
	public final static String OS = "others";// 其他情况
	//
	public final static String NOBASIC = "nobasic";
	public final static String BASIC = "basic";

	public HttpPost httpRequest = null; // post方式
	public HttpGet httpGet = null;// get方式
	public HttpURLConnection conn = null;
	// 变量数组
	public static final String[] RESULT = { "Created", "Conflict", "Duplicate", "" };

	/* the new member */
	/* 常量 */
	public final static int EXC = -1;// 程序异常
	public final static int NETEXC = -2;// 网络请求异常

	/**
	 * 判断当前设备是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnect(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		// 能联网
// 不能联网
		return info != null && info.isAvailable();
	}

	/**
	 * post提交返回结果
	 * 
	 * @param urlpath
	 *            提交的参数Map类型
	 * @return 返回服务端返回字符串
	 */
	public String uploadPost(String urlpath, Map<String, String> paramsMap) {
		String result = null;
		try {
			URL url = new URL(urlpath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Chatset", "UTF-8");

			InputStream is = conn.getInputStream();
			InputStreamReader rd = new InputStreamReader(is);
			java.io.BufferedReader br = new BufferedReader(rd);

			StringBuffer sb = new StringBuffer();
			String line; // 临时存储器
			while ((line = br.readLine()) != null) {// 读取
				sb.append(line); // 写入
			}
			br.close();
			rd.close();
			is.close();
			result = sb.toString();
			return result;
			// int ch;
			// BufferedReader br = new BufferedReader(rd);
			// StringBuffer b = new StringBuffer();
			// while ((ch = is.read()) != -1) {
			// b.append((char) ch);
			// }
			// result = b.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result = "noresult";
	}

	public String uploadPostEx(String urlpath, String info) {
		String result = "";
		DataOutputStream ds = null;
		try {
			URL url = new URL(urlpath);
			// String info = "";
			// Iterator<Map.Entry<String, String>> it = paramsMap.entrySet()
			// .iterator();
			// while (it.hasNext()) {
			// Map.Entry<String, String> e = (Map.Entry<String, String>) it
			// .next();
			// info += e.getKey() + "=" + e.getValue() + "&";
			// }
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Chatset", "UTF-8");
			conn.setRequestProperty("Content-Type", "text/html");
			ds = new DataOutputStream(conn.getOutputStream());
			ds.write(info.getBytes());
			/** 取得Response内容 */
			InputStream is = conn.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			result = b.toString();
			// System.out.println(b.toString());
			ds.close();
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			return "noresult";
		}
	}

	public boolean uploadFileByProgressBar(String urlpath, File uploadFile, ProgressBar pb) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			String newName = uploadFile.getName();
			URL url = new URL(urlpath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Chatset", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" + newName + "\"" + end);
			ds.writeBytes(end);
			/** 取得文件的FileInputStream **/
			FileInputStream fStream = new FileInputStream(uploadFile);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			int size = fStream.available() / bufferSize;
			pb.setMax(size);
			while ((length = fStream.read(buffer)) != -1) {
				pb.incrementProgressBy(1);
				ds.write(buffer, 0, length);
				// Thread.sleep(100);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			fStream.close();
			ds.flush();
			/** 取得Response内容 */
			InputStream is = conn.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			// 上传成功
			ds.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean uploadFile(String urlpath, File uploadFile) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			String newName = uploadFile.getName();
			URL url = new URL(urlpath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Chatset", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" + newName + "\"" + end);
			ds.writeBytes(end);
			/** 取得文件的FileInputStream **/
			FileInputStream fStream = new FileInputStream(uploadFile);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			while ((length = fStream.read(buffer)) != -1) {
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			fStream.close();
			ds.flush();
			/** 取得Response内容 */
			InputStream is = conn.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			// 上传成功
			ds.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean uploadFileByProgressBarEx(String urlpath, File uploadFile, ProgressBar pb, String seq) {
		// String end = "\r\n";
		// String twoHyphens = "--";
		String boundary = "*****";
		try {
			String newName = uploadFile.getName();
			URL url = new URL(urlpath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Chatset", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
			// ds.writeBytes(twoHyphens + boundary + end);
			/** 取得文件的FileInputStream **/
			FileInputStream fStream = new FileInputStream(uploadFile);
			String info = "type=upload&Seq=" + seq + "&PhotoType=1&FileName=" + new String(newName.getBytes(), "ISO-8859-1") + "&FileLength="
					+ fStream.available() + "&FileContent=";

			// = URLDecoder.decode(newName)
			ds.writeBytes(info);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			int size = fStream.available() / bufferSize;
			pb.setMax(size);
			while ((length = fStream.read(buffer)) != -1) {
				pb.incrementProgressBy(1);
				ds.write(buffer, 0, length);
			}
			// ds.writeBytes(end);
			// ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			fStream.close();
			ds.flush();
			/** 取得Response内容 */
			InputStream is = conn.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			// 上传成功
			ds.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 新系统拉数据
	public String upload(String urlpath, Map<String, String> reqMap) throws Exception {
		HttpPost httpRequest = new HttpPost(urlpath);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			Iterator<Map.Entry<String, String>> it = reqMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> en = it.next();
				params.add(new BasicNameValuePair(en.getKey(), en.getValue()));
			}

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 取得HTTP response
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			return EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 新系统拉数据,可设连接和响应超时 post方式
	 * 
	 * @param url
	 * @param valueMap
	 * @param timeoutConnection
	 * @param timeoutSocket
	 * @return 返回结果（成功和serverError errorNum：xx）或者异常
	 * @throws Exception
	 */
	public String upload_timeout(String url, Map<String, String> valueMap, int timeoutConnection, int timeoutSocket) throws HttpException {
		httpRequest = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpParams httpParameters;
		try {
			Iterator<Map.Entry<String, String>> it = valueMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> en = it.next();
				params.add(new BasicNameValuePair(en.getKey(), en.getValue()));
			}

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			httpParameters = new BasicHttpParams();// Set the timeout in
													// milliseconds until a
													// connection is
													// established.
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set
																							// the
																							// default
																							// socket
																							// timeout
																							// (SO_TIMEOUT)
																							// //
																							// in
																							// milliseconds
																							// which
																							// is
																							// the
																							// timeout
																							// for
																							// waiting
																							// for
																							// data.
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			// 取得HTTP response
			HttpResponse httpResponse_0 = httpClient.execute(httpRequest);
			int state = httpResponse_0.getStatusLine().getStatusCode();
			if (state == HttpStatus.SC_OK) {// 返回成功
				return EntityUtils.toString(httpResponse_0.getEntity());
			} else {// 服务器错误
				return SEN + httpResponse_0.getStatusLine().getStatusCode() + "," + EntityUtils.toString(httpResponse_0.getEntity());
			}

		} catch (Exception e) {// 网络异常
			e.printStackTrace();
			StackTraceElement[] ste = e.getStackTrace();
			throw new HttpException("", ste);
		}
	}

	/**
	 * 新系统拉数据,可设连接和响应超时 get方式 headerMap报头信息 Authorization用于basic认证的用户:密码
	 * 
	 * @param assignment
	 *            任务 ，其值basic 为basic认证任务
	 * @param url
	 * @param valueMap
	 *            不能为null
	 * @param headerMap
	 *            assignment为 basic时 headerMap不能为null
	 * @param timeoutConnection
	 * @param timeoutSocket
	 * @return 返回结果、服务器错误码、网络异常（抛）
	 * @throws Exception
	 */
	public String upload_timeout_get(String assignment, String url, Map<String, String> headerMap, Map<String, String> valueMap, int timeoutConnection,
			int timeoutSocket) throws HttpException {
		String value = "?";
		Iterator<Map.Entry<String, String>> it = valueMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> en = it.next();
			value += "&" + en.getKey() + "=" + en.getValue();
		}

		httpGet = new HttpGet(url + value);
		if (BASIC.equals(assignment)) {// basic认证需要的报头信息
			String Authorization = "" + headerMap.get("Authorization");
			httpGet.addHeader("Authorization", "basic " + Base64.encode(Authorization));
		}
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpParams httpParameters;
		try {

			// httpRequest.setEntity(new UrlEncodedFormEntity(params,
			// HTTP.UTF_8));

			httpParameters = new BasicHttpParams();// Set the timeout in
													// milliseconds until a
													// connection is
													// established.
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set
																							// the
																							// default
																							// socket
																							// timeout
																							// (SO_TIMEOUT)
																							// //
																							// in
																							// milliseconds
																							// which
																							// is
																							// the
																							// timeout
																							// for
																							// waiting
																							// for
																							// data.
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			// 取得HTTP response
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int state = httpResponse.getStatusLine().getStatusCode();
			Log.v("---------", "state=" + state);
			if (state == HttpStatus.SC_OK) {// 返回成功
				return EntityUtils.toString(httpResponse.getEntity());
			} else if (state == HttpStatus.SC_UNAUTHORIZED) {// 未授权
				return EntityUtils.toString(httpResponse.getEntity());
			} else if (state == HttpStatus.SC_BAD_REQUEST) {// 错误请求
				return "" + state;
			} else if (state == HttpStatus.SC_NOT_FOUND) {// 未找到
				return "" + state;
			} else {// 服务器错误
				return SEN + httpResponse.getStatusLine().getStatusCode() + "," + EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
			StackTraceElement[] ste = e.getStackTrace();
			throw new HttpException("", ste);
		}
	}

	/**
	 * 此方法将逐步取代上面的upload_timeout_get 新系统拉数据,可设连接和响应超时 get方式 headerMap报头信息
	 * Authorization用于basic认证的用户:密码
	 * 
	 * @param assignment
	 *            任务 ，其值basic 为basic认证任务
	 * @param url
	 * @param valueMap
	 *            不能为null
	 * @param headerMap
	 *            assignment为 basic时 headerMap不能为null
	 * @param timeoutConnection
	 * @param timeoutSocket
	 * @return 返回结果、服务器错误码、网络异常（抛）
	 * @throws Exception
	 */
	public String upload_timeout_get_new(String assignment, String url, Map<String, String> headerMap, Map<String, String> valueMap, int timeoutConnection,
			int timeoutSocket) throws HttpException {
		String value = "?";
		Iterator<Map.Entry<String, String>> it = valueMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> en = it.next();
			value += "&" + en.getKey() + "=" + en.getValue();
		}

		httpGet = new HttpGet(url + value);
		if (BASIC.equals(assignment)) {// basic认证需要的报头信息
			String Authorization = "" + headerMap.get("Authorization");
			httpGet.addHeader("Authorization", "basic " + Base64.encode(Authorization));
		}
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpParams httpParameters;
		try {

			// httpRequest.setEntity(new UrlEncodedFormEntity(params,
			// HTTP.UTF_8));

			httpParameters = new BasicHttpParams();// Set the timeout in
													// milliseconds until a
													// connection is
													// established.
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			// 取得HTTP response
			HttpResponse httpResponse = httpClient.execute(httpGet);
			int state = httpResponse.getStatusLine().getStatusCode();
			if (state == HttpStatus.SC_OK) {// 返回成功
				return EntityUtils.toString(httpResponse.getEntity());
			} else if (state == HttpStatus.SC_UNAUTHORIZED) {// 未授权
				return "" + state;
			} else if (state == HttpStatus.SC_BAD_REQUEST) {// 错误请求
				return "" + state;
			} else if (state == HttpStatus.SC_NOT_FOUND) {// 未找到
				return "" + state;
			} else {// 服务器错误
				return SEN + httpResponse.getStatusLine().getStatusCode() + "," + EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
			StackTraceElement[] ste = e.getStackTrace();
			throw new HttpException("", ste);
		}
	}

	/**
	 * 新系统拉数据,可设连接和响应超时 get方式 headerMap报头信息 Authorization用于basic认证的用户:密码
	 * 
	 * @param assignment
	 *            任务 ，其值basic 为basic认证任务
	 * @param url
	 * @param valueMap
	 *            不能为null
	 * @param headerMap
	 *            assignment为 basic时 headerMap不能为null
	 * @param timeoutConnection
	 * @param timeoutSocket
	 * @return 返回结果、服务器错误码、网络异常（抛）
	 * @throws Exception
	 */
	public String upload_timeout_post(String assignment, String url, Map<String, String> headerMap, Map<String, String> valueMap, int timeoutConnection,
			int timeoutSocket) throws HttpException {

		httpRequest = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpParams httpParameters;
		try {
			Iterator<Map.Entry<String, String>> it = valueMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> en = it.next();
				params.add(new BasicNameValuePair(en.getKey(), en.getValue()));
			}

			if (BASIC.equals(assignment)) {// basic认证需要的报头信息
				String Authorization = "" + headerMap.get("Authorization");
				httpRequest.addHeader("Authorization", "basic " + Base64.encode(Authorization));
			}

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			httpParameters = new BasicHttpParams();// Set the timeout in
													// milliseconds until a
													// connection is
													// established.
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			// 取得HTTP response
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			int state = httpResponse.getStatusLine().getStatusCode();
			if (state == HttpStatus.SC_OK || state == HttpStatus.SC_CREATED || state == HttpStatus.SC_UNAUTHORIZED || state == HttpStatus.SC_CONFLICT) {// 返回成功
				return EntityUtils.toString(httpResponse.getEntity());
			} else {// 服务器错误
				return SEN + httpResponse.getStatusLine().getStatusCode() + "," + EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
			StackTraceElement[] ste = e.getStackTrace();
			throw new HttpException("", ste);
		}
	}

	/**
	 * 此方法将逐步取代upload_timeout_post 新系统拉数据,可设连接和响应超时 get方式 headerMap报头信息
	 * Authorization用于basic认证的用户:密码
	 * 
	 * @param assignment
	 *            任务 ，其值basic 为basic认证任务
	 * @param url
	 * @param valueMap
	 *            不能为null
	 * @param headerMap
	 *            assignment为 basic时 headerMap不能为null
	 * @param timeoutConnection
	 * @param timeoutSocket
	 * @return String[0]报头编码 String[1]正文
	 * @throws Exception
	 */
	@SuppressWarnings("finally")
	public String[] upload_timeout_post_new(String assignment, String url, Map<String, String> headerMap, Map<String, Object> valueMap, int timeoutConnection,
			int timeoutSocket) {
		MyLog.v("requestPost1=", "url=" + url);
		httpRequest = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		HttpParams httpParameters;
		String[] result = new String[2];
		try {
			Iterator<Map.Entry<String, Object>> it = valueMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> en = it.next();
				params.add(new BasicNameValuePair(en.getKey(), "" + en.getValue()));
			}

			if (BASIC.equals(assignment)) {// basic认证需要的报头信息
				String Authorization = "" + headerMap.get("Authorization");
				httpRequest.addHeader("Authorization", "basic " + Base64.encode(Authorization));
			}
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			// 取得HTTP response
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			int state = httpResponse.getStatusLine().getStatusCode();
			result[0] = "" + state;
			result[1] = EntityUtils.toString(httpResponse.getEntity());
			MyLog.v("requestPost=", "state=" + result[0]);
			MyLog.v("requestPost=", "msg=" + result[1]);
		} catch (Exception e) {
			result[0] = "" + Http.EXC;
			result[1] = EXC_PROMPT;
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	// 新系统断点上传图片和内容
	public String uploadFile(String urlpath, Map<String, String> reqMap, int updateLength) throws Exception {
		String result = "";
		String filePath = reqMap.get("file_path");
		String strImage = "";// 图片字符串
		String state = "";// 上传状态
		int index_int = 0;// 上传位置
		int length_int = 0;// 文件长度
		File file = null;
		Map<String, Object> updteImageInfo_map = null;// 标记map 存储 index_int
														// length_int
														// state信息
		File infoFile = null;
		try {
			file = new File(filePath);
			infoFile = new File(file.getAbsolutePath().replaceAll(".jpg", ".bak"));// 标记文件

			/************ 有无断点续传标记文件,并为标记赋值 **************************/
			if (!infoFile.exists()) {// 没有断点信息文件
				byte[] bytes = this.readFile(file);
				strImage = Base64Coder.encodeLines(bytes);
				state = "start";
				index_int = 0;
				length_int = strImage.length();
				saveStrImage(infoFile, state, "上传中", index_int, length_int);
			} else {// 有断点信息文件
				byte[] bytes = this.readFile(file);
				strImage = Base64Coder.encodeLines(bytes);
				updteImageInfo_map = (Map<String, Object>) readStrImage(infoFile);
				state = (String) updteImageInfo_map.get("state");
				index_int = (Integer) updteImageInfo_map.get("index_int");
				length_int = (Integer) updteImageInfo_map.get("length_int");
				saveStrImage(infoFile, state, "上传中", index_int, length_int);
			}

			/*********************** 分段上传 *******************************/

			for (int i = 0; true; i++) {// 分段上传
				updteImageInfo_map = (Map<String, Object>) readStrImage(infoFile);
				state = (String) updteImageInfo_map.get("state");
				index_int = (Integer) updteImageInfo_map.get("index_int");
				length_int = (Integer) updteImageInfo_map.get("length_int");
				/******************** 文件拆分 *****************************/
				if (index_int <= length_int - 1 - updateLength) {// 剩余字段大于一次分段
					reqMap.put("file_content", strImage.substring(index_int, index_int + updateLength));
					reqMap.put("state", "updating");
					reqMap.put("position", "" + index_int);
				} else {// 剩余字段大小不足一次分段
					reqMap.put("file_content", strImage.substring(index_int, length_int));
					reqMap.put("state", "end");
					reqMap.put("position", "" + index_int);
				}

				/***************** 文件上传，并改变标记状态 *****************/
				result = upload(urlpath, reqMap);
				if (result.contains("status=start") || result.contains("status=updating")) {// 阶段上传成功
					saveStrImage(infoFile, state, "上传中", index_int + updateLength, length_int);

				} else if (result.contains("status=yes")) {// 上传完成
					saveStrImage(infoFile, state, "已完成", index_int + updateLength, length_int);
					break;
				} else if (result.contains("status=error")) {// 服务器报错
					saveStrImage(infoFile, state, "请续传", index_int, length_int);
					break;
				} else {
					i--;
				}
			}
		} catch (Exception e) {
			saveStrImage(infoFile, state, "请续传", index_int, length_int);
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	/**
	 * 存储断点续传标记

	 *            经过Base64Coder编码的图片字符串
	 * @param state
	 *            传输状态 从未、传输中、结束
	 * @param showState
	 *            上传页面显示状态
	 * @param index
	 *            断点位置
	 * @param length
	 *            经过Base64Coder编码的图片字符串长度
	 */
	private void saveStrImage(File file, String state, String showState, int index, int length) {
		Map<String, Object> map = new HashMap<String, Object>();
		// map.put("file_content", strImage);
		map.put("showState", showState);
		map.put("state", state);
		map.put("index_int", index);
		map.put("length_int", length);
		IO os = new IO();
		os.saveObject(file, map);
	}

	/**
	 * 读取断点续传标记文件
	 * 
	 * @param file
	 * @return
	 */
	private Object readStrImage(File file) {
		IO os = new IO();
		Object obj = null;
		try {
			obj = os.readObject(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	private byte[] readFile(File file) throws Exception {
		IO os = new IO();
		return os.readFile(file);
	}

	// 新系统 返回串解析
	public Map<String, String> getResult(String result) {
		String[] str1 = result.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < str1.length; i++) {
			String[] str2 = str1[i].split("=");
			if (str2.length >= 2) {
				map.put(str2[0], str2[1]);
			} else {
				map.put(str2[0], "");
			}
		}
		return map;
	}

	public boolean uploadFileByProgressBarEx0124(String urlpath, File uploadFile, String seq) {
		HttpPost httpRequest = new HttpPost(urlpath);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			String newName = uploadFile.getName();
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(uploadFile));
			byte[] bytes = new byte[bis.available()];
			bis.read(bytes);
			params.add(new BasicNameValuePair("FileName", Base64Coder.encodeString(newName)));
			params.add(new BasicNameValuePair("FileContent", Base64Coder.encodeLines(bytes)));

			// String tmp = URLEncoder.encode(Base64.encodeBase64String(bytes));
			// String info = "type=upload&Seq=" + seq
			// + "&PhotoType=1&FileName="+Base64Coder.encodeString(newName)//
			// new String(newName.getBytes(), "ISO-8859-1")
			// + "&FileLength=" + bis.available() + "&FileContent="
			// + Base64Coder.encodeLines(bytes);

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 取得HTTP response
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			String strResult = EntityUtils.toString(httpResponse.getEntity());
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 从服务器取图片
	 * 
	 * @param url
	 * @return Bitmap 可以为null
	 */
	public Bitmap getHttpBitmap(String url) {
		if (url == null || "".equals(url.trim())) {
			return null;
		}
		URL myFileUrl = null;
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			myFileUrl = new URL(url);
			conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setConnectTimeout(10 * 1000);// 设置连接主机超时
			conn.setReadTimeout(15 * 1000);// 设置从主机读取数据超时（已连接主机）
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 下载并保存图片
	 * 
	 * @param url
	 * @param folderPath
	 * @param fileName
	 * @param x
	 *            设置图片的宽 如果为0 保存图片原始大小
	 * @param y
	 *            设置图片的高 如果为0 保存图片原始大小
	 */
	public boolean downloadAndSaveHttpImage(String url, String folderPath, String fileName, int x, int y) {
		/*
		 * 检测文件夹，不存在则创建
		 */
		File folderFile = new File(folderPath);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
		}
		//
		Log.v(TAG, "...........1");
		Bitmap bitmap = getHttpBitmap(url);
		Log.v(TAG, "...........3");
		File fileP = null;
		FileOutputStream bos = null;
		if (bitmap == null) {
			return false;
		} else {
			try {
				fileP = new File(folderPath, fileName);
				bos = new FileOutputStream(fileP);
				if (x > 0 && y > 0) {
					bitmap = ImageTool.imageZoom(bitmap, x, y);// xy都不为0 设置图片大小
				}
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} finally {
				try {
					if (bos != null) {
						bos.flush();
						bos.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/* the new method */

	/**
	 * @param Authorization
	 * @param basic
	 *            base验证
	 */
	public static String[] REQUESTFIELD = { "Authorization", "basic" };
	public final static int TIMEOUTCONNECTION = 5 * 1000;
	public final static int TIMEOUTSOCKET = 15 * 1000;
	private final static String EXC_PROMPT = "网络异常";

	/**
	 * 此方法将逐步取代upload_timeout_post 新系统拉数据,可设连接和响应超时 get方式 headerMap报头信息
	 * Authorization用于basic认证的用户:密码
	 * 
	 * @param assignment
	 *            任务 ，其值basic 为basic认证任务
	 * @param url
	 * @param valueMap
	 *            不能为null
	 * @param headerMap
	 *            assignment为 basic时 headerMap不能为null
	 * @param timeoutConnection
	 * @param timeoutSocket
	 * @return String[0]报头编码 String[1]正文
	 * @throws Exception
	 */
	@SuppressWarnings("finally")
	public String[] requestPost(String assignment, String url, Map<String, String> headerMap, Map<String, Object> valueMap, int timeoutConnection,
			int timeoutSocket) {
		MyLog.v("requestPost=", "url=" + url);
		String[] result = new String[2];
		try {
			HttpPost httpRequest = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (valueMap != null) {
				Iterator<Map.Entry<String, Object>> it = valueMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Object> en = it.next();
					params.add(new BasicNameValuePair(en.getKey(), "" + en.getValue()));
					MyLog.v("requestPost=", en.getKey() + "=" + en.getValue());
				}
			}
			if (REQUESTFIELD[1].equals(assignment)) {// basic认证需要的报头信息
				String Authorization = "" + headerMap.get("Authorization");
				httpRequest.addHeader(REQUESTFIELD[0], REQUESTFIELD[1] + Base64.encode(Authorization));
			}
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			// 取得HTTP response
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			int state = httpResponse.getStatusLine().getStatusCode();
			result[0] = "" + state;
			result[1] = EntityUtils.toString(httpResponse.getEntity());
			MyLog.v("requestPost=", "state=" + result[0]);
			MyLog.v("requestPost=", "msg=" + result[1]);
		} catch (Exception e) {
			result[0] = "" + Http.EXC;
			result[1] = EXC_PROMPT;
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	// webservice/wcf

	public final static int SOAPENV_VAR11 = SoapEnvelope.VER11;
	public final static int HTTP_OK = 200;

	/**
	 * 
	 * @param soapEnvVar
	 *            soap版本 SOAPENV_VAR11
	 * @param nameSpace
	 *            空间名
	 * @param methodName
	 *            方法名
	 * @param url
	 *            服务器地址
	 * @param valueMap
	 * @return 不为null
	 */
	public Object[] soapRequest(int soapEnvVar, String nameSpace, String methodName, String url, String soapAction_prefix,
			LinkedHashMap<String, Object> valueMap) {
		MyLog.d(TAG, "nameSpace=" + nameSpace);
		MyLog.d(TAG, "methodName=" + methodName);
		MyLog.d(TAG, "url=" + url);
		MyLog.d(TAG, "soapAction=" + soapAction_prefix + methodName);

		// 指定WebService的命名空间和调用方法
		SoapObject soapObject = new SoapObject(nameSpace, methodName);
		if (valueMap != null) {
			for (String key : valueMap.keySet()) {
				soapObject.addProperty(key, valueMap.get(key));
				MyLog.d(TAG, "" + key + "=" + valueMap.get(key));
			}
		}

		// 生成调用WebService方法调用的soap信息，并且指定Soap版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(soapEnvVar);
		envelope.bodyOut = soapObject;
		// 是否调用DotNet开发的WebService
		envelope.dotNet = true;
		envelope.setOutputSoapObject(soapObject);
		HttpTransportSE transport = new HttpTransportSE(url);
		SoapObject object = null;
		try {
			transport.call(soapAction_prefix + methodName, envelope);
			// 获取返回的结果
			object = (SoapObject) envelope.bodyIn;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		MyLog.d(TAG, "envelope.bodyIn=" + envelope.bodyIn);
		// 获取返回的结果
		if (object == null) {// 网络异常、服务端异常、服务器中间件异常
			return new Object[] { EXC, EXC_PROMPT };
		} else {// 正常情况
			return new Object[] { HTTP_OK, object };
		}
	}

	/*
	 * 判断网络连接是否已开
	 */
	public static boolean isConn(Context context) {
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}
		return bisConnFlag;
	}

	// 常通云风格
	public static class CtSendRequestType {
		public final static String POST = "POST";
		public final static String PUT = "PUT";
		public final static String GET = "GET";
	}

	public  String[] ctSendRequest(String url, String strJson, String type, int timeoutConnection) {
		DataOutputStream out = null;
		String returnLine = "";
		String[] result = new String[2];
		MyLog.i(TAG, "url="+url);
		MyLog.i(TAG,"strJson="+strJson);
		try {
			URL my_url = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) my_url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod(type);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(timeoutConnection);
			connection.connect();
			out = new DataOutputStream(connection.getOutputStream());
			byte[] content = strJson.getBytes("utf-8");
			out.write(content, 0, content.length);
			out.flush();
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			// StringBuilder builder = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null) {
				// line = new String(line.getBytes(), "utf-8");
				returnLine += line;
			}
			reader.close();
			int code=connection.getResponseCode();
			connection.disconnect();
			MyLog.i(TAG,"code="+code);
			MyLog.i(TAG,"result="+returnLine);
			result[0] = "" + code;
			result[1] = returnLine;
		} catch (Exception e) {
			e.printStackTrace();
			result[0] = "" + EXC;
			result[1] = NE;
		} finally {
			//
		}

		return result;
	}

}
