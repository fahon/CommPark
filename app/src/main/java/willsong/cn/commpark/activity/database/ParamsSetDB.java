package willsong.cn.commpark.activity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ParamsSetDB {
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public ParamsSetDB(Context context) {
        helper = new DatabaseHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     *
     * @param paramsSetEntity
     */
    public void add(List<ParamsSetEntity> paramsSetEntity) {
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            for (ParamsSetEntity paramsSetEntitys : paramsSetEntity) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_PARAM_SET
                                + " VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)",
                        new Object[]{paramsSetEntitys.userName, paramsSetEntitys.userPwd,paramsSetEntitys.systemPwd,paramsSetEntitys.deviceCode,
                                paramsSetEntitys.parkId,paramsSetEntitys.activateCode,paramsSetEntitys.isSign,paramsSetEntitys.setPwd,
                                paramsSetEntitys.exitPwd,paramsSetEntitys.systemMode,paramsSetEntitys.isBluOpen,paramsSetEntitys.isEnterPrint,
                                paramsSetEntitys.isOutPrint,paramsSetEntitys.isPicUpload,paramsSetEntitys.isTwoCodeScan,paramsSetEntitys.blueDeviceName,
                                paramsSetEntitys.blueDeviceAddress,paramsSetEntitys.titlePrint,paramsSetEntitys.companyPrint,paramsSetEntitys.telPrint,
                                paramsSetEntitys.isRecognizePlate, paramsSetEntitys.isWebService,paramsSetEntitys.isRecognizeAgain,paramsSetEntitys.webServiceIP,
                                paramsSetEntitys.corpId,paramsSetEntitys.plateFirstWord,paramsSetEntitys.showCheckTime,paramsSetEntitys.showDataStatistic,
                                paramsSetEntitys.printSignOutDet,paramsSetEntitys.linkCamera,paramsSetEntitys.cameraIP,paramsSetEntitys.versionUpdate,
                                paramsSetEntitys.electPayIp,paramsSetEntitys.storeNo,paramsSetEntitys.appUrl,paramsSetEntitys.allowEnterAgain,paramsSetEntitys.openChenNiao,
                                paramsSetEntitys.enterCarTitle,paramsSetEntitys.exitCarTitle,paramsSetEntitys.enterBikeTitle,paramsSetEntitys.exitBikeTitle,
                                paramsSetEntitys.showCouponType,paramsSetEntitys.savePicNum,paramsSetEntitys.savePicDays});
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        } finally {
            db.endTransaction(); // 结束事务
        }
    }

    //删除表中plateCode对应的列
    public void delete(ParamsSetEntity paramsSetEntitys) {
        db.delete(DatabaseHelper.TABLE_PARAM_SET, "userName = ?",
                new String[]{String.valueOf(paramsSetEntitys.userName)});
    }

    //删除表中的所以列
    public void deleteTable() {
//        db.execSQL("DELETE FROM ParamsSetTable");//删除audios表中的所有数据
        db.delete("ParamsSetTable", null, null); //删除audios表中的所有数据（官方推荐方法）

       //设置id从1开始（sqlite默认id从1开始），若没有这一句，id将会延续删除之前的id
        db.execSQL("update sqlite_sequence set seq=0 where name='ParamsSetTable'");
    }

    public void onUpgrade(int oldVersion, int newVersion,String addParm) {
//        if (oldVersion==1 && newVersion==2) {//升级判断,如果再升级就要再加两个判断,从1到3,从2到3
            db.execSQL("ALTER TABLE ParamsSetTable ADD "+ addParm +" TEXT;");
//        }
    }

    /**
     * 方法2：检查表中某列是否存在
//     * @param db
//     * @param tableName 表名
     * @param columnName 列名
     * @return
     */
    public boolean checkColumnExists2(String columnName) {
        boolean result = false ;
        Cursor cursor = null ;

        try{
            cursor = db.rawQuery( "select * from sqlite_master where name = ? and sql like ?"
                    , new String[]{DatabaseHelper.TABLE_PARAM_SET , "%" + columnName + "%"} );
            result = null != cursor && cursor.moveToFirst() ;
        }catch (Exception e){
//            LogUtil.logErrorMessage("checkColumnExists2..." + e.getMessage());
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }

        return result ;
    }

    //更新paramsSetEntitys
    public void updateParamsSet(ParamsSetEntity paramsSetEntitys){
        ContentValues values=new ContentValues();
        values.put("userName",paramsSetEntitys.userName);
        values.put("userPwd",paramsSetEntitys.userPwd);
        values.put("systemPwd",paramsSetEntitys.systemPwd);
        values.put("deviceCode",paramsSetEntitys.deviceCode);
        values.put("parkId",paramsSetEntitys.parkId);
        values.put("activateCode",paramsSetEntitys.activateCode);
        values.put("isSign",paramsSetEntitys.isSign);
        values.put("setPwd",paramsSetEntitys.setPwd);
        values.put("exitPwd",paramsSetEntitys.exitPwd);
        values.put("systemMode",paramsSetEntitys.systemMode);
        values.put("isBluOpen",paramsSetEntitys.isBluOpen);
        values.put("isEnterPrint",paramsSetEntitys.isEnterPrint);
        values.put("isOutPrint",paramsSetEntitys.isOutPrint);
        values.put("isPicUpload",paramsSetEntitys.isPicUpload);
        values.put("isTwoCodeScan",paramsSetEntitys.isTwoCodeScan);
        values.put("blueDeviceName",paramsSetEntitys.blueDeviceName);
        values.put("blueDeviceAddress",paramsSetEntitys.blueDeviceAddress);
        values.put("titlePrint",paramsSetEntitys.titlePrint);
        values.put("companyPrint",paramsSetEntitys.companyPrint);
        values.put("telPrint",paramsSetEntitys.telPrint);
        values.put("isRecognizePlate",paramsSetEntitys.isRecognizePlate);
        values.put("isWebService",paramsSetEntitys.isWebService);
        values.put("isRecognizeAgain",paramsSetEntitys.isRecognizeAgain);
        values.put("webServiceIP",paramsSetEntitys.webServiceIP);
        values.put("corpId",paramsSetEntitys.corpId);
        values.put("plateFirstWord",paramsSetEntitys.plateFirstWord);
        values.put("showCheckTime",paramsSetEntitys.showCheckTime);
        values.put("showDataStatistic",paramsSetEntitys.showDataStatistic);
        values.put("printSignOutDet",paramsSetEntitys.printSignOutDet);
        values.put("linkCamera",paramsSetEntitys.linkCamera);
        values.put("cameraIP",paramsSetEntitys.cameraIP);
        values.put("versionUpdate",paramsSetEntitys.versionUpdate);
        values.put("electPayIp",paramsSetEntitys.electPayIp);
        values.put("storeNo",paramsSetEntitys.storeNo);
        values.put("appUrl",paramsSetEntitys.appUrl);
        values.put("allowEnterAgain",paramsSetEntitys.allowEnterAgain);
        values.put("openChenNiao",paramsSetEntitys.openChenNiao);
        values.put("enterCarTitle",paramsSetEntitys.enterCarTitle);
        values.put("exitCarTitle",paramsSetEntitys.exitCarTitle);
        values.put("enterBikeTitle",paramsSetEntitys.enterBikeTitle);
        values.put("exitBikeTitle",paramsSetEntitys.exitBikeTitle);
        values.put("showCouponType",paramsSetEntitys.showCouponType);
        values.put("savePicNum",paramsSetEntitys.savePicNum);
        values.put("savePicDays",paramsSetEntitys.savePicDays);

        db.update(DatabaseHelper.TABLE_PARAM_SET,values,"_id = ?",new String[]{String.valueOf("1")});
        db.close();
    }
    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public List<ParamsSetEntity> query() {
        ArrayList<ParamsSetEntity> persons = new ArrayList<ParamsSetEntity>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            ParamsSetEntity person = new ParamsSetEntity();
            person._id = c.getInt(c.getColumnIndex("_id"));
            person.userName = c.getString(c.getColumnIndex("userName"));
            person.userPwd = c.getString(c.getColumnIndex("userPwd"));
            person.systemPwd = c.getString(c.getColumnIndex("systemPwd"));
            person.deviceCode = c.getString(c.getColumnIndex("deviceCode"));
            person.parkId = c.getString(c.getColumnIndex("parkId"));
            person.activateCode = c.getString(c.getColumnIndex("activateCode"));
            person.isSign = c.getString(c.getColumnIndex("isSign"));
            person.setPwd = c.getString(c.getColumnIndex("setPwd"));
            person.exitPwd = c.getString(c.getColumnIndex("exitPwd"));
            person.systemMode = c.getString(c.getColumnIndex("systemMode"));
            person.isBluOpen = c.getString(c.getColumnIndex("isBluOpen"));
            person.isEnterPrint = c.getString(c.getColumnIndex("isEnterPrint"));
            person.isOutPrint = c.getString(c.getColumnIndex("isOutPrint"));
            person.isPicUpload = c.getString(c.getColumnIndex("isPicUpload"));
            person.isTwoCodeScan = c.getString(c.getColumnIndex("isTwoCodeScan"));
            person.blueDeviceName = c.getString(c.getColumnIndex("blueDeviceName"));
            person.blueDeviceAddress = c.getString(c.getColumnIndex("blueDeviceAddress"));
            person.titlePrint = c.getString(c.getColumnIndex("titlePrint"));
            person.companyPrint = c.getString(c.getColumnIndex("companyPrint"));
            person.telPrint = c.getString(c.getColumnIndex("telPrint"));
            person.isRecognizePlate = c.getString(c.getColumnIndex("isRecognizePlate"));
            person.isWebService = c.getString(c.getColumnIndex("isWebService"));
            person.isRecognizeAgain = c.getString(c.getColumnIndex("isRecognizeAgain"));
            person.webServiceIP = c.getString(c.getColumnIndex("webServiceIP"));
            person.corpId = c.getString(c.getColumnIndex("corpId"));
            person.plateFirstWord = c.getString(c.getColumnIndex("plateFirstWord"));
            person.showCheckTime = c.getString(c.getColumnIndex("showCheckTime"));
            person.showDataStatistic = c.getString(c.getColumnIndex("showDataStatistic"));
            person.printSignOutDet = c.getString(c.getColumnIndex("printSignOutDet"));
            person.linkCamera = c.getString(c.getColumnIndex("linkCamera"));
            person.cameraIP = c.getString(c.getColumnIndex("cameraIP"));
            person.versionUpdate = c.getString(c.getColumnIndex("versionUpdate"));
            person.electPayIp = c.getString(c.getColumnIndex("electPayIp"));
            person.storeNo = c.getString(c.getColumnIndex("storeNo"));
            person.appUrl = c.getString(c.getColumnIndex("appUrl"));
            person.allowEnterAgain = c.getString(c.getColumnIndex("allowEnterAgain"));
            person.openChenNiao =  c.getString(c.getColumnIndex("openChenNiao"));
            person.enterCarTitle =  c.getString(c.getColumnIndex("enterCarTitle"));
            person.exitCarTitle =  c.getString(c.getColumnIndex("exitCarTitle"));
            person.enterBikeTitle =  c.getString(c.getColumnIndex("enterBikeTitle"));
            person.exitBikeTitle =  c.getString(c.getColumnIndex("exitBikeTitle"));
            person.showCouponType =  c.getString(c.getColumnIndex("showCouponType"));
            person.savePicNum =  c.getString(c.getColumnIndex("savePicNum"));
            person.savePicDays =  c.getString(c.getColumnIndex("savePicDays"));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PARAM_SET,
                null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        // 释放数据库资源
        db.close();
    }

}
