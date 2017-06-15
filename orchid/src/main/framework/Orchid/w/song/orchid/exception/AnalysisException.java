package w.song.orchid.exception;

import android.util.Log;

public class AnalysisException extends Exception{
	private static final long serialVersionUID = 1L;

	public AnalysisException(String mess,StackTraceElement[] se) {
		String excepInfo="line:"+se[0].getLineNumber()+" "+se[0].getFileName()+"analysisException";
		Log.w("system err__", excepInfo);
	}
}
