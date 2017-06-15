package w.song.orchid.exception;

import android.util.Log;

public class HttpException extends Exception {
	
	private static final long serialVersionUID = 7717707042521737467L;
	public String exceptionSort;
	public HttpException(String mess,StackTraceElement[] se) {
		exceptionSort =mess;
		String excepInfo="line:"+se[0].getLineNumber()+" "+se[0].getFileName()+"httpException";
		Log.w("system err", excepInfo);
	}
	public HttpException(String mess, StackTraceElement[] se,Exception e) {
		String excepInfo="line:"+se[0].getLineNumber()+" "+se[0].getFileName()+"httpException";
		Log.w("system err", excepInfo);
	}
}
