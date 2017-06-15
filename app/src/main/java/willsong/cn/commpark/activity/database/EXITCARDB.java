package willsong.cn.commpark.activity.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class EXITCARDB {
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public EXITCARDB(Context context) {
        helper = new DatabaseHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     *
     * @param exitCarEntity
     */
    public void add(List<ExitCarEntity> exitCarEntity) {
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            for (ExitCarEntity exitCarEntitys : exitCarEntity) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME_EXITCAR
                                + " VALUES(null, ?, ?, ?, ?, ?, ?)",
                        new Object[]{exitCarEntitys.plateCode, exitCarEntitys.enterTime,
                                exitCarEntitys.outTime,exitCarEntitys.shouldPay,
                                exitCarEntitys.realPay,exitCarEntitys.payType});
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
    public void delete(ExitCarEntity exitCarEntitys) {
        db.delete(DatabaseHelper.TABLE_NAME_EXITCAR, "plateCode = ?",
                new String[]{String.valueOf(exitCarEntitys.plateCode)});
    }

    //删除表中的所以列
    public void deleteTable() {
//        db.execSQL("DELETE FROM ExitCarTable");
        db.delete("ExitCarTable", null, null); //删除audios表中的所有数据（官方推荐方法）

        //设置id从1开始（sqlite默认id从1开始），若没有这一句，id将会延续删除之前的id
        db.execSQL("update sqlite_sequence set seq=0 where name='ExitCarTable'");
    }

    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public List<ExitCarEntity> query() {
        ArrayList<ExitCarEntity> persons = new ArrayList<ExitCarEntity>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            ExitCarEntity person = new ExitCarEntity();
            person._id = c.getInt(c.getColumnIndex("_id"));
            person.plateCode = c.getString(c.getColumnIndex("plateCode"));
            person.enterTime = c.getString(c.getColumnIndex("enterTime"));
            person.outTime = c.getString(c.getColumnIndex("outTime"));
            person.shouldPay = c.getString(c.getColumnIndex("shouldPay"));
            person.realPay = c.getString(c.getColumnIndex("realPay"));
            person.payType = c.getString(c.getColumnIndex("payType"));
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
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME_EXITCAR,
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
