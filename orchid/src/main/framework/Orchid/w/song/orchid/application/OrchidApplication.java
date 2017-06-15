package w.song.orchid.application;

import android.app.Application;
import w.song.orchid.util.CrashHandler;

public class OrchidApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
	}
}
