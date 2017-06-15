package w.song.orchid.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import w.song.orchid.util.MyTools;
import willsong.cn.orchid.R;

public class OrchidWebActivity extends OBaseActivity {
	public static class NEED_FIELD {
		public static String TITLE = "title";
		public static String URL = "url";
		public static String DEF_URL = "def_url";
	}

	public static String TAG = "OrchidWebActivity";

	private String title;
	private String userUrl;
	private String defUrl;

	private ProgressBar progressBar;
	private WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title= getIntentValue(NEED_FIELD.TITLE, "");
		userUrl = getIntentValue(NEED_FIELD.URL, "");
		defUrl = getIntentValue(NEED_FIELD.DEF_URL, "http://baidu.com");
		setContentViewWithTitle(R.layout.activity_orchid_web);
		setTitleText(title);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		webView = (WebView) findViewById(R.id.webView);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 0) {
					progressBar.setVisibility(View.VISIBLE);
				}else if (newProgress == 100) {
					progressBar.setVisibility(View.INVISIBLE);
				} else {
					progressBar.setProgress(newProgress);
				}
			}
		});
		webView.loadUrl(MyTools.replaceNull(userUrl, defUrl));
		setTitleRightButtonOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				webView.reload();
			}
		});

	}

	// 改写物理按键——返回的逻辑
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView.canGoBack()) {
				webView.goBack();// 返回上一页面
				return true;
			} else {
				System.exit(0);// 退出程序
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
