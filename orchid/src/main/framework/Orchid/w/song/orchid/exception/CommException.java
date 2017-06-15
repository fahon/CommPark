package w.song.orchid.exception;

import android.util.Log;

public class CommException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	public String exceptionSort;
	public CommException(String mess,StackTraceElement[] se) {
		exceptionSort =mess;
		String excepInfo=exceptionSort+"\nCommException"+"line:"+se[0].getLineNumber()+" "+se[0].getFileName();
		Log.e("system err", excepInfo);
	}
	
	public CommException(String mess) {
		exceptionSort =mess;
		String excepInfo=exceptionSort+"\nCommException";
		Log.e("system err", excepInfo);
	}
}
