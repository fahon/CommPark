package willsong.cn.commpark.activity.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DatabaseHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add persons
     *
     * @param recordStep
     */
    public void add(List<RecordSteps> recordStep) {
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            for (RecordSteps recordSteps : recordStep) {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME
                                + " VALUES(null, ?, ?, ?)",
                        new Object[]{recordSteps.plateCode, recordSteps.timeLonger,
                                recordSteps.enterTime});
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
    public void delete(RecordSteps recordSteps) {
        db.delete(DatabaseHelper.TABLE_NAME, "plateCode = ?",
                new String[]{String.valueOf(recordSteps.plateCode)});
    }

    //删除表中的所以列
    public void deleteTable() {
//        db.execSQL("DELETE FROM CheckTable");
        db.delete("CheckTable", null, null); //删除audios表中的所有数据（官方推荐方法）

        //设置id从1开始（sqlite默认id从1开始），若没有这一句，id将会延续删除之前的id
        db.execSQL("update sqlite_sequence set seq=0 where name='CheckTable'");
    }

    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public List<RecordSteps> query() {
        ArrayList<RecordSteps> persons = new ArrayList<RecordSteps>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            RecordSteps person = new RecordSteps();
            person._id = c.getInt(c.getColumnIndex("_id"));
            person.plateCode = c.getString(c.getColumnIndex("plateCode"));
            person.timeLonger = c.getString(c.getColumnIndex("timeLonger"));
            person.enterTime = c.getString(c.getColumnIndex("enterTime"));
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
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME,
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
