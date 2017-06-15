package willsong.cn.commpark.activity.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

public class DatabaseHelper extends SQLiteOpenHelper { // 继承SQLiteOpenHelper类

	// 数据库版本号
    private static final int DATABASE_VERSION = 1;
    // 数据库名
    private static final String DATABASE_NAME = "mTestDB.db";

    // 数据表名，一个数据库中可以有多个表
    public static final String TABLE_NAME = "CheckTable";//巡场车辆表
    public static final String TABLE_NAME_EXITCAR = "ExitCarTable";//出场车辆表
    public static final String TABLE_ABNORMAL_CAR= "AbnormalCarTable";//异常出场车表（公交卡扣款成功，出场失败的车辆）
    public static final String TABLE_PARAM_SET= "ParamsSetTable";//参数设置表

    // 构造函数，调用父类SQLiteOpenHelper的构造函数
    public DatabaseHelper(Context context, String name, CursorFactory factory,
            int version, DatabaseErrorHandler errorHandler)
    {
        super(context, name, factory, version, errorHandler);

    }

    public DatabaseHelper(Context context, String name, CursorFactory factory,
            int version)
    {
        super(context, name, factory, version);
        // SQLiteOpenHelper的构造函数参数：
        // context：上下文环境
        // name：数据库名字
        // factory：游标工厂（可选）
        // version：数据库模型版本号
    }

    public DatabaseHelper(Context context)
    {
        super(context, getMyDatabaseName(context), null, DATABASE_VERSION);

        // 数据库实际被创建是在getWritableDatabase()或getReadableDatabase()方法调用时
        // CursorFactory设置为null,使用系统默认的工厂类
    }

    // 继承SQLiteOpenHelper类,必须要覆写的三个方法：onCreate(),onUpgrade(),onOpen()
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // 调用时间：数据库第一次创建时onCreate()方法会被调用

        // onCreate方法有一个 SQLiteDatabase对象作为参数，根据需要对这个对象填充表和初始化数据
        // 这个方法中主要完成创建数据库后对数据库的操作


        // 构建创建表的SQL语句（可以从SQLite Expert工具的DDL粘贴过来加进StringBuffer中）
        StringBuffer sBuffer = new StringBuffer();

        sBuffer.append("CREATE TABLE [" + TABLE_NAME + "] (");
        sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append("[plateCode] TEXT,");
        sBuffer.append("[timeLonger] TEXT,");
        sBuffer.append("[enterTime] TEXT)");

        // 执行创建表的SQL语句
        db.execSQL(sBuffer.toString());

//        StringBuffer sBuffer_exitCar = new StringBuffer();
//
//        sBuffer_exitCar.append("CREATE TABLE [" + TABLE_NAME_EXITCAR + "] (");
//        sBuffer_exitCar.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
//        sBuffer_exitCar.append("[plateCode] TEXT,");
//        sBuffer_exitCar.append("[enterTime] TEXT)");
//        sBuffer_exitCar.append("[outTime] TEXT,");
//        sBuffer_exitCar.append("[shouldPay] TEXT,");
//        sBuffer_exitCar.append("[realPay] TEXT,");
//        sBuffer_exitCar.append("[payType] TEXT,");
//        db.execSQL(sBuffer_exitCar.toString());
        // 即便程序修改重新运行，只要数据库已经创建过，就不会再进入这个onCreate方法
        db.execSQL("create table ExitCarTable(_id integer not null primary key autoincrement,plateCode text,enterTime text,outTime text,shouldPay text,realPay text,payType text)");
        db.execSQL("create table AbnormalCarTable(_id integer not null primary key autoincrement,plateNumber text,outTime text,busCardMoney text,fieldCode text,seqNo text,posId text," +
                "cityCode text,cardPhysicalNumber text,card text,cardCount text,cardMoney text,money text,cpuCar text,transportCardType text,cardTradeTac text,icType text,cardVer text,corpId text," +
                "couponStr text,payMoney text,CardTradeTime text,terminalNo text,payType text,couponNameStr text)");
        db.execSQL("create table ParamsSetTable(_id integer not null primary key autoincrement,userName text,userPwd text,systemPwd text,deviceCode text,parkId text," +
                "activateCode text,isSign text,setPwd text,exitPwd text,systemMode text,isBluOpen text,isEnterPrint text,isOutPrint text,isPicUpload text,isTwoCodeScan text," +
                "blueDeviceName text,blueDeviceAddress text,titlePrint text,companyPrint text," +
                "telPrint text,isRecognizePlate text,isWebService text,isRecognizeAgain text,webServiceIP text,corpId text,plateFirstWord text," +
                "showCheckTime text,showDataStatistic text,printSignOutDet text,linkCamera text,cameraIP text,versionUpdate text,electPayIp text,storeNo text,appUrl text,allowEnterAgain text," +
                "openChenNiao text,enterCarTitle text,exitCarTitle text,enterBikeTitle text,exitBikeTitle text,showCouponType text,savePicNum text,savePicDays text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // 调用时间：如果DATABASE_VERSION值被改为别的数,系统发现现有数据库版本不同,即会调用onUpgrade

        // onUpgrade方法的三个参数，一个 SQLiteDatabase对象，一个旧的版本号和一个新的版本号
        // 这样就可以把一个数据库从旧的模型转变到新的模型
        // 这个方法中主要完成更改数据库版本的操作


        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EXITCAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ABNORMAL_CAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARAM_SET);
        onCreate(db);
        // 上述做法简单来说就是，通过检查常量值来决定如何，升级时删除旧表，然后调用onCreate来创建新表
        // 一般在实际项目中是不能这么做的，正确的做法是在更新数据表结构时，还要考虑用户存放于数据库中的数据不丢失

    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        // 每次打开数据库之后首先被执行

    }
    //数据库保存在sd卡上，app删除或者更新数据库不会被删除
    private static String getMyDatabaseName(Context context){
        String databasename = DATABASE_NAME;
        boolean isSdcardEnable =false;
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){//SDCard是否插入
            isSdcardEnable = true;
        }
        String dbPath = null;
        if(isSdcardEnable){
            dbPath =Environment.getExternalStorageDirectory().getPath() +"/database/";
        }else{//未插入SDCard，建在内存中
            dbPath =context.getFilesDir().getPath() + "/database/";
        }
        File dbp = new File(dbPath);
        if(!dbp.exists()){
            dbp.mkdirs();
        }
        databasename = dbPath +DATABASE_NAME;
        return databasename;
    }
}
