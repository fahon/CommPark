package willsong.cn.commpark.activity.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class AbnormalCarDB {
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public AbnormalCarDB(Context context) {
        helper = new DatabaseHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     *
     * @param abnormalCarEntity
     */
    public void add(List<AbnormalCarEntity> abnormalCarEntity) {
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            for (AbnormalCarEntity abnormalCarEntitys : abnormalCarEntity) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_ABNORMAL_CAR
                                + " VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new Object[]{abnormalCarEntitys.plateNumber, abnormalCarEntitys.outTime,abnormalCarEntitys.busCardMoney,abnormalCarEntitys.fieldCode,
                                abnormalCarEntitys.seqNo,abnormalCarEntitys.posId,abnormalCarEntitys.cityCode,abnormalCarEntitys.cardPhysicalNumber,
                                abnormalCarEntitys.card,abnormalCarEntitys.cardCount,abnormalCarEntitys.cardMoney,abnormalCarEntitys.money,
                                abnormalCarEntitys.cpuCar,abnormalCarEntitys.transportCardType,abnormalCarEntitys.cardTradeTac,abnormalCarEntitys.icType,
                                abnormalCarEntitys.cardVer,abnormalCarEntitys.corpId,abnormalCarEntitys.couponStr,abnormalCarEntitys.payMoney,
                                abnormalCarEntitys.CardTradeTime,abnormalCarEntitys.terminalNo,abnormalCarEntitys.payType,abnormalCarEntitys.couponNameStr});
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
    public void delete(AbnormalCarEntity abnormalCarEntitys) {
        db.delete(DatabaseHelper.TABLE_ABNORMAL_CAR, "seqNo = ?",
                new String[]{String.valueOf(abnormalCarEntitys.seqNo)});
    }

    //删除表中的所以列
    public void deleteTable() {
        db.execSQL("DELETE FROM AbnormalCarTable");
    }
    public void onUpgrade(int oldVersion, int newVersion,String addParm) {
//        if (oldVersion==1 && newVersion==2) {//升级判断,如果再升级就要再加两个判断,从1到3,从2到3
        db.execSQL("ALTER TABLE AbnormalCarTable ADD "+ addParm +" TEXT;");
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
                    , new String[]{DatabaseHelper.TABLE_ABNORMAL_CAR , "%" + columnName + "%"} );
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
    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public List<AbnormalCarEntity> query() {
        ArrayList<AbnormalCarEntity> persons = new ArrayList<AbnormalCarEntity>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            AbnormalCarEntity person = new AbnormalCarEntity();
            person._id = c.getInt(c.getColumnIndex("_id"));
            person.plateNumber = c.getString(c.getColumnIndex("plateNumber"));
            person.outTime = c.getString(c.getColumnIndex("outTime"));
            person.busCardMoney = c.getString(c.getColumnIndex("busCardMoney"));
            person.fieldCode = c.getString(c.getColumnIndex("fieldCode"));
            person.seqNo = c.getString(c.getColumnIndex("seqNo"));
            person.posId = c.getString(c.getColumnIndex("posId"));
            person.cityCode = c.getString(c.getColumnIndex("cityCode"));
            person.cardPhysicalNumber = c.getString(c.getColumnIndex("cardPhysicalNumber"));
            person.card = c.getString(c.getColumnIndex("card"));
            person.cardCount = c.getString(c.getColumnIndex("cardCount"));
            person.cardMoney = c.getString(c.getColumnIndex("cardMoney"));
            person.money = c.getString(c.getColumnIndex("money"));
            person.cpuCar = c.getString(c.getColumnIndex("cpuCar"));
            person.transportCardType = c.getString(c.getColumnIndex("transportCardType"));
            person.cardTradeTac = c.getString(c.getColumnIndex("cardTradeTac"));
            person.icType = c.getString(c.getColumnIndex("icType"));
            person.cardVer = c.getString(c.getColumnIndex("cardVer"));
            person.corpId = c.getString(c.getColumnIndex("corpId"));
            person.couponStr = c.getString(c.getColumnIndex("couponStr"));
            person.payMoney = c.getString(c.getColumnIndex("payMoney"));
            person.CardTradeTime = c.getString(c.getColumnIndex("CardTradeTime"));
            person.terminalNo = c.getString(c.getColumnIndex("terminalNo"));
            person.payType = c.getString(c.getColumnIndex("payType"));
            person.couponNameStr = c.getString(c.getColumnIndex("couponNameStr"));
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
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ABNORMAL_CAR,
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
