package w.song.orchid.exception;

import android.util.Log;

public class RangeException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	public String exceptionSort;
	public RangeException(String mess,StackTraceElement[] se) {
		exceptionSort =mess;
		String excepInfo=exceptionSort+"\nRangeException"+"line:"+se[0].getLineNumber()+" "+se[0].getFileName();
		Log.e("system err", excepInfo);
	}
	
	public RangeException(String mess) {
		exceptionSort =mess;
		String excepInfo=exceptionSort+"\nRangeException";
		Log.e("system err", excepInfo);
	}
}
