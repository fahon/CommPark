package willsong.cn.commpark.activity.widget;

import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import willsong.cn.commpark.R;

public class MediaPlayerTool {

	static MediaPlayerTool mediaPlayerTool;
	SpeechUtils speechUtils;
	Map<String, MediaPlayer> mediaPlayers = new HashMap<String, MediaPlayer>();
	Map<String, String> names = new HashMap<String, String>();
	String[] particularValue = {"小时","分钟","欢迎光临","一路顺风"};

	private MediaPlayerTool(Context context){
		mediaPlayers.put("零", MediaPlayer.create(context.getApplicationContext(), R.raw.media0));
		mediaPlayers.put("一", MediaPlayer.create(context.getApplicationContext(), R.raw.media1));
		mediaPlayers.put("二", MediaPlayer.create(context.getApplicationContext(), R.raw.media2));
		mediaPlayers.put("三", MediaPlayer.create(context.getApplicationContext(), R.raw.media3));
		mediaPlayers.put("四", MediaPlayer.create(context.getApplicationContext(), R.raw.media4));
		mediaPlayers.put("五", MediaPlayer.create(context.getApplicationContext(), R.raw.media5));
		mediaPlayers.put("六", MediaPlayer.create(context.getApplicationContext(), R.raw.media6));
		mediaPlayers.put("七", MediaPlayer.create(context.getApplicationContext(), R.raw.media7));
		mediaPlayers.put("八", MediaPlayer.create(context.getApplicationContext(), R.raw.media8));
		mediaPlayers.put("九", MediaPlayer.create(context.getApplicationContext(), R.raw.media9));
		mediaPlayers.put("拾", MediaPlayer.create(context.getApplicationContext(), R.raw.media10));
		mediaPlayers.put("佰", MediaPlayer.create(context.getApplicationContext(), R.raw.media100));
		mediaPlayers.put("千", MediaPlayer.create(context.getApplicationContext(), R.raw.media1000));
		mediaPlayers.put("日", MediaPlayer.create(context.getApplicationContext(), R.raw.date));
		mediaPlayers.put("小时", MediaPlayer.create(context.getApplicationContext(), R.raw.hour));
		mediaPlayers.put("分钟", MediaPlayer.create(context.getApplicationContext(), R.raw.minute));
		mediaPlayers.put("月", MediaPlayer.create(context.getApplicationContext(), R.raw.month));
		mediaPlayers.put("秒", MediaPlayer.create(context.getApplicationContext(), R.raw.second));
		mediaPlayers.put("欢迎光临", MediaPlayer.create(context.getApplicationContext(), R.raw.thsin));
		mediaPlayers.put("一路顺风", MediaPlayer.create(context.getApplicationContext(), R.raw.thsouted));
		mediaPlayers.put("年", MediaPlayer.create(context.getApplicationContext(), R.raw.year));
		mediaPlayers.put("元", MediaPlayer.create(context.getApplicationContext(), R.raw.yuan));
		mediaPlayers.put("角", MediaPlayer.create(context, R.raw.jiao));
		mediaPlayers.put("分", MediaPlayer.create(context, R.raw.fen));
		mediaPlayers.put("亿", MediaPlayer.create(context, R.raw.yi));
	};
	public static MediaPlayerTool getInstance(Context context) {
		if(mediaPlayerTool == null){
			mediaPlayerTool = new MediaPlayerTool(context);
		}
		return mediaPlayerTool;
	}

	public void startPlay(String str){
		value = str;

		dealChar();
	}



	MediaPlayer mediaPlayer = null;
	String value = "";
	public void dealChar(){

		new Thread(){

			@Override
			public void run() {
				try {

					for(int i = 0; i < particularValue.length; i++){
						if(value.contains(particularValue[i])){
							value = value.replaceAll(particularValue[i], i+"");
						}
					}
					char[] chars = value.toCharArray();
					for(int i=0; i < chars.length; i++){
						if(ifSpecial(chars[i])){
							mediaPlayer = mediaPlayers.get(particularValue[Integer.valueOf(chars[i]+"")]);
						}
						else{
							mediaPlayer = mediaPlayers.get(chars[i]+"");
						}
						Message msg = Message.obtain();
						msg.obj = mediaPlayer;
						msg.what = 1;
						handler.sendMessage(msg);
						Thread.sleep(700);
					}
				} catch (Exception e) {
				}


			}

		}.start();


	}

	/**
	 * 针对一个两个文字以上的语音做处理
	 * @return
	 */
	public boolean ifSpecial(char c){
		boolean ifspecial = false;
		for(int i =0; i < particularValue.length; i++){
			Log.d("ifSpecial",(int)c+"");
			if((int)c == 48+i){
				ifspecial = true;
				break;
			}
		}
		return ifspecial;
	}

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				try {
					MediaPlayer mediaPlayer =  (MediaPlayer) msg.obj;
					mediaPlayer.start();
				} catch (Exception e) {
				}

			}
		}

	};

}

