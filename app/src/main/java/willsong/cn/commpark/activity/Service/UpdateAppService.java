package willsong.cn.commpark.activity.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import willsong.cn.commpark.R;
import willsong.cn.commpark.activity.LoginActivity;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class UpdateAppService extends Service{

    private static final String TAG="UpdateService";
    private String apkName = "CommPark.apk";
    private String downUrl = "";

    //文件存储
    private File updateDir = null;
    private File updateFile = null;

    //通知栏
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;
    //通知栏跳转Intent
    private Intent updateIntent = null;
    private PendingIntent updatePendingIntent = null;
    Notification.Builder builder;
    RemoteViews contentView;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onStart.....");
        super.onStart(intent, startId);
    }
    @SuppressLint("NewApi") @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        //获取传值
        Log.d(TAG, "onStartCommand.....");
        apkName = intent.getStringExtra("titleId");
        downUrl = intent.getStringExtra("downUrl");
        //创建文件
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            updateDir = new File(Environment.getExternalStorageDirectory(),"/cty/");
            updateFile = new File(updateDir.getPath(),apkName);
        }

        this.updateNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        this.updateNotification = new Notification();
        contentView = new RemoteViews(getPackageName(), R.layout.appupdate_notify_progress_layout);
       builder= new Notification.Builder(this);
        builder.setAutoCancel(true)
                .setContentTitle("停车系统APP")
                .setContentText("正在下载")
                .setSmallIcon(R.drawable.part_icon)
                .setContent(contentView);
        Intent intents = new Intent(this, LoginActivity.class);
        updatePendingIntent = PendingIntent.getActivity(this, 0, intents, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(updatePendingIntent);
        updateNotification = builder.getNotification();
        updateNotificationManager.notify(0,updateNotification);


        //开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
        new Thread(new updateRunnable()).start();//这个是下载的重点，是下载的过程

        return super.onStartCommand(intent, flags, startId);
    }


    class updateRunnable implements Runnable {
        Message message = updateHandler.obtainMessage();
        public void run() {
            message.what = DOWNLOAD_COMPLETE;
            try{
                //增加权限;
                if(!updateDir.exists()){
                    updateDir.mkdirs();
                }
                if(!updateFile.exists()){
                    updateFile.createNewFile();
                }

                //增加权限;
                long downloadSize = downloadUpdateFile(downUrl,updateFile);
                if(downloadSize>0){
                    //下载成功
                    updateHandler.sendMessage(message);
                }
            }catch(Exception ex){
                ex.printStackTrace();
                message.what = DOWNLOAD_FAIL;
                //下载失败
                updateHandler.sendMessage(message);
            }
        }
    }


    public long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
        //这样的下载代码很多，我就不做过多的说明
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;

        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
            if(currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
            }
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte buffer[] = new byte[4096];
            int readsize = 0;
            while((readsize = is.read(buffer)) > 0){
                fos.write(buffer, 0, readsize);
                totalSize += readsize;
                //为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                if((downloadCount == 0)||(int) (totalSize*100/updateTotalSize)-10>downloadCount){
                    downloadCount += 10;

                    BigDecimal bd = new BigDecimal(totalSize*100d/updateTotalSize);
                    BigDecimal bd1 = bd.setScale(0, BigDecimal.ROUND_HALF_UP);

                    int currentProgressValue = (int) bd1.doubleValue();
                    Log.d("downloadCount", totalSize*100/updateTotalSize+"-->"+totalSize+"#"+updateTotalSize);
                    Message msg = updateHandler.obtainMessage();
                    msg.what = DOWNLOADING;
                    msg.obj = currentProgressValue;
                    updateHandler.sendMessage(msg);
                }

            }
        } finally {
            if(httpConnection != null) {
                httpConnection.disconnect();
            }
            if(is != null) {
                is.close();
            }
            if(fos != null) {
                fos.close();
            }
        }
        return totalSize;
    }


    private final static int DOWNLOAD_COMPLETE = 0;
    private final static int DOWNLOAD_FAIL = 1;
    private final static int DOWNLOADING=2;

    private Handler updateHandler = new  Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case DOWNLOAD_COMPLETE:
                    //点击安装PendingIntent
                    Uri uri = Uri.fromFile(updateFile);
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                    updatePendingIntent = PendingIntent.getActivity(UpdateAppService.this, 0, installIntent, 0);
                    builder= new Notification.Builder(UpdateAppService.this);
                    builder.setContentTitle("停车系统APP")
                            .setContentText("下载完成,点击安装")
                            .setSmallIcon(R.drawable.part_icon)
                            .setDefaults(Notification.DEFAULT_ALL);

                    builder.setContentIntent(updatePendingIntent);
                    updateNotification = builder.getNotification();
                    updateNotificationManager.notify(0, updateNotification);

                    //下载完成后，自动安装
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(updateFile), "application/vnd.android.package-archive");
                    startActivity(intent);


                    //停止服务
                    stopSelf();
                    break;
                case DOWNLOAD_FAIL:
                    //下载失败
                    builder= new Notification.Builder(UpdateAppService.this);
                    builder.setContentTitle("停车系统APP")
                            .setContentText("下载失败")
                            .setSmallIcon(R.drawable.part_icon)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentIntent(updatePendingIntent);
                    updateNotification = builder.getNotification();
                    updateNotificationManager.notify(0, updateNotification);
                    break;
                case DOWNLOADING:
                    //下载中

                    int progressValue = (Integer) msg.obj;
                    updateNotification.contentView.setTextViewText(R.id.notificationTitle, "停车系统APP 正在下载");
                    updateNotification.contentView.setTextViewText(R.id.notificationPercent, progressValue+"%");
                    updateNotification.contentView.setProgressBar(R.id.notificationProgress, 100, progressValue, false);
                    updateNotificationManager.notify(0, updateNotification);




                    break;
                default:
                    stopSelf();
                    break;
            }
        }
    };

}
