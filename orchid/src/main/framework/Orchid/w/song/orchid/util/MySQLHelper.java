package w.song.orchid.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import w.song.orchid.data.OField;

public class MySQLHelper extends SQLiteOpenHelper {

	private static final String TAG = "MySQLHelper";

	public static final int VERSION = 3;

	public static Object DBLookObj = new Object();

	private Context context;

	public MySQLHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	public MySQLHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public MySQLHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	public MySQLHelper(Context context) {
		this(context, OField.DB_NAME);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		MyLog.i(TAG, "onCreate：创建数据库表。");
		createTable(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		MyLog.i("MySQLHelper", "onUpgrade：更新数据库表。");
		clearAllTable(db);

	}

	public void clearAllTable(SQLiteDatabase db) {
		synchronized (MySQLHelper.DBLookObj) {

			String sql = "drop table if exists tb_sys_bd";
			db.execSQL(sql);
			sql = "drop table if exists tb_sys_dic_list";
			db.execSQL(sql);
			sql = "drop table if exists tb_sys_gateway";
			db.execSQL(sql);
			sql = "drop table if exists tb_sys_wlc";
			db.execSQL(sql);
			sql = "drop table if exists tb_sys_wlp";
			db.execSQL(sql);
			sql = "drop table if exists tb_sys_eq";
			db.execSQL(sql);
			sql = "drop table if exists tb_sys_timeseg";
			db.execSQL(sql);
			sql = "drop table if exists tb_stat_eq_timeseg";
			db.execSQL(sql);
			sql = "drop table if exists tb_data_eq_his";
			db.execSQL(sql);
			sql = "drop table if exists tb_sys_user";
			db.execSQL(sql);
			sql = "drop table if exists tb_sys_role";
			db.execSQL(sql);
			sql = "drop table if exists tb_sys_menu";
			db.execSQL(sql);
			sql = "drop table if exists notice_list";
			db.execSQL(sql);
		}
		createTable(db);
	}

	private void createTable(SQLiteDatabase db) {
		synchronized (MySQLHelper.DBLookObj) {
			// 建筑基本信息表
			String sql = "CREATE TABLE IF NOT EXISTS tb_sys_bd (" + "bd_guid                  text primary key ," + "org_id                   text,"
					+ "parent_bd_guid           text," + "bd_id                    text," + "bd_full_name             text," + "bd_name                  text,"
					+ "bd_alias	                text," + "index_no	                text," + "order_no                 int," + "bd_area	                int,"
					+ "bd_use	                text," + "bd_fun	                text," + "bd_use_id	            text," + "bd_fun_id	            text,"
					+ "remark	                text," + "is_delete	            int," + "oper_user	            text," + "oper_time	            text,"
					+ "level	                text,"// 注意接口给的字段是DB_LEVEL 从1开始
					+ "bd_type_id	                text,"
					+ "bd_type_class	                text,"
					+ "bd_area_id	                text,"
					+ "bd_area_class	                text,"
					+ "person_count	                text,"
					+ "has_child	            text)";// 后来加上的，以后改用这个字段
			db.execSQL(sql);

			// 数据字典表
			sql = "CREATE TABLE IF NOT EXISTS tb_sys_dic_list (" + "dic_guid                  text primary key ," + "type_tag                   text,"
					+ "dic_value           text," + "dic_desc                    text," + "relation_item                    text,"
					+ "org_id                    text," + "order_no                    int," + "is_enable             int," + "is_delete             int)";
			db.execSQL(sql);

			// 网关
			sql = "CREATE TABLE IF NOT EXISTS tb_sys_gateway (" + "gateway_guid                  text primary key ," + "org_id                   text,"
					+ "bd_guid           text," + "gateway_name                    text," + "gprs_code             int," + "gateway_id                   text,"
					+ "gateway_type           text," + "factory_date                    text," + "version             text,"
					+ "freeze_count                   int," + "freeze_cycle           int," + "freeze_data_len                    int,"
					+ "freeze_max_count             int," + "max_count                   int," + "load_count           int,"
					+ "pick_port                    text," + "order_no             int," + "install_address             text,"
					+ "install_time                   text," + "install_pic_guid           text," + "person_id                    text,"
					+ "is_delete             int," + "oper_user             text," + "oper_time             text)";
			db.execSQL(sql);

			// 集中器
			sql = "CREATE TABLE IF NOT EXISTS tb_sys_wlc (" + "wlc_guid                  text primary key ," + "org_id                   text,"
					+ "gateway_guid           text," + "bd_guid                    text," + "wlc_name             text," + "address                  text,"
					+ "channel                   int," + "sp_baud           int," + "sky_baud                    int," + "sub_address             text,"
					+ "bandwidth                  int," + "other_param                   text," + "overtime_fail_reply           int,"
					+ "max_level                    int," + "signal             text," + "install_address                  text,"
					+ "install_time                   text," + "install_pic_guid           text," + "is_delete           int," + "oper_user           text,"
					+ "oper_time           text)";
			db.execSQL(sql);

			// 采集器
			sql = "CREATE TABLE IF NOT EXISTS tb_sys_wlp (" + "wlp_guid                  text primary key ," + "org_id                   text,"
					+ "wlc_guid           text," + "bd_guid                    text," + "wlp_name             text," + "address                  text,"
					+ "channel                   int," + "sp_baud           int," + "sky_baud                    int," + "sub_address             text,"
					+ "bandwidth                  int," + "other_param                   text," + "install_address           text,"
					+ "install_time                    text," + "install_pic_guid             text," + "person_id                  text,"
					+ "is_delete                   int," + "oper_user           text," + "oper_time             text)";
			db.execSQL(sql);

			// // 表计
			// sql = "CREATE TABLE IF NOT EXISTS tb_sys_eq ("
			// + "eq_guid                  text primary key ,"
			// + "org_id                   text,"
			// + "p_sche_guid           text,"
			// + "e_sche_guid                    text,"
			// + "bd_guid             text,"
			// + "gateway_guid                   text,"
			// + "wlp_guid           text,"
			// + "price_sche_guid                    text,"
			// + "is_local                    int,"
			// + "eq_address             text,"
			// + "eq_name                   text,"
			// + "eq_model           text,"
			// + "factory_id                    text,"
			// + "rate             int,"
			// + "eq_state                   text,"
			// + "freeze_no           int,"
			// + "gateway_freeze                    int,"
			// + "energy_type_id               text,"
			// + "subitem_type_id                   text,"
			// + "cfg_guid                   text,"
			// + "lately_pick_time           text,"
			// + "pick_state                    int,"
			// + "un_pick_count             int,"
			// + "install_address                   text,"
			// + "install_pic_guid           text,"
			// + "person_id                    text,"
			// + "install_time             text,"
			// + "is_online                   int,"
			// + "is_stat                   int,"
			// + "online_time           text,"
			// + "stat_cycle                    text,"
			// + "order_no             int,"
			// + "is_delete                    int,"
			// + "oper_user             text,"
			// + "oper_time             text)";
			// db.execSQL(sql);

			// 表计
			sql = "CREATE TABLE IF NOT EXISTS tb_sys_eq (" + "eq_guid                  text primary key ," + "parent_eq_guid                   text,"
					+ "org_id                   text," + "is_virtual                   int," + "com_formula                   text,"
					+ "eq_address                   text," + "eq_name           text," + "eq_model                    text," + "bd_guid             text,"
					+ "gateway_guid                   text," + "wlp_guid           text," + "eq_type                    text,"
					+ "use_type                    text," + "sub_type1                    text," + "sub_type2             text,"
					+ "custom_type                   text," + "is_belong           int," + "factory_id                    text," + "rate             int,"
					+ "eq_circuit                   int," + "freeze_no           int," + "is_free_write                    int,"
					+ "down_interface               text," + "header_type                   text," + "protocol_type                   int,"
					+ "validation           int," + "baud                    int," + "overtime_time             int," + "freeze_num                   int,"
					+ "target_ip           text," + "target_port                    text," + "is_encrypt             int,"
					+ "is_time_price                   int," + "price_sche_guid                   text," + "is_local           int,"
					+ "is_enable_local                    int," + "write_count             int," + "is_ladder_price                    int,"
					+ "ladder_price_guid             text," + "init_value             int," + "install_address             text,"
					+ "install_pic_guid             text," + "install_time             text," + "switch_state             int,"
					+ "switch_time             text," + "is_online             int," + "is_manual             int," + "is_com             int,"
					+ "online_time             text," + "stat_cycle             int," + "is_stop             int," + "stop_time             text,"
					+ "is_time_control             int," + "is_open             int," + "order_no             int," + "is_delete             int,"
					+ "oper_user             text," + "oper_time             text)";
			db.execSQL(sql);

			// 时段定义表
			sql = "CREATE TABLE IF NOT EXISTS tb_sys_timeseg (" + "timeseg_id                  text primary key ," + "org_id                   text,"
					+ "timeseg_name           text," + "begin_time                    text," + "end_time             text," + "is_cross                   int)";
			db.execSQL(sql);

			// 设备时段统计
			sql = "CREATE TABLE IF NOT EXISTS tb_stat_eq_timeseg (" + "org_id                  text primary key ," + "eq_guid                   text,"
					+ "stat_time           text," + "timeseg_id                    text," + "is_work_day             int," + "eq_type                   text,"
					+ "use_type           text," + "begin_time                    text," + "begin_value                    int,"
					+ "pick_time1             text," + "end_time                   text," + "end_value           int," + "pick_time2                    text,"
					+ "diff_value             int," + "amount                   int," + "com_exp           text," + "com_exp_desc                    text,"
					+ "lately_num1               int," + "lately_num2                   int," + "is_valid                   int," + "com_time           text,"
					+ "unpick_count                    int," + "pick_count             int," + "repairpick_count                   int,"
					+ "deadpick1_count           int," + "deadpick2_count                    int," + "failpick_count             int,"
					+ "unit_area_num                   int," + "unit_area_sce                   int," + "sce_num           int)";
			db.execSQL(sql);

			// 设备整点数据
			sql = "CREATE TABLE IF NOT EXISTS tb_data_eq_his (" + "data_guid                  text primary key ," + "eq_guid                   text,"
					+ "org_id           text," + "eq_address                    text," + "eq_type             text," + "use_type                   text,"
					+ "pick_time           text," + "break_state                    int," + "current_value                    real,"
					+ "cycle_no             int," + "cycle_time                   text," + "lately_value           real,"
					+ "lately_time                    text," + "diff_value             real," + "amount                   int," + "price_tag           text,"
					+ "hour_num                    int," + "day_num               int," + "month_num                   int,"
					+ "year_num                   int," + "status           int," + "is_halve                    int," + "write_time             text)";
			db.execSQL(sql);

			// 用户基础数据表
			sql = "CREATE TABLE IF NOT EXISTS tb_sys_user (" + "user_guid         text primary key ," + "user_id      text," + "parent_user_id         text,"
					+ "user_name         text," + "user_password         text," + "user_sex         int," + "user_address	    text," + "user_m_tel	    text,"
					+ "user_tel     int," + "user_email	 int," + "user_role_list	 text," + "is_enable	int," + "is_delete	int," + "last_login	 text,"
					+ "oper_user	text," + "oper_time	text)";
			db.execSQL(sql);

			// 角色定义表
			sql = "CREATE TABLE IF NOT EXISTS tb_sys_role (" + "role_guid         text primary key ," + "org_id      text," + "role_tag         text,"
					+ "parent_role_tag         text," + "role_desc         text," + "is_enable         int," + "oper_user	    text," + "oper_time	    text)";
			db.execSQL(sql);

			// 系统菜单表
			sql = "CREATE TABLE IF NOT EXISTS tb_sys_menu (" + "org_id         text primary key ," + "memu_id      text," + "menu_name         text,"
					+ "menu_name_mobile         text," + "parent_memu_id         text," + "menu_url         text," + "menu_ico	    text,"
					+ "menu_ico_mobile	    text," + "is_mobile	    int," + "oper_user	    text," + "oper_time	    text)";
			db.execSQL(sql);

			// 消息通知列表
			sql = "CREATE TABLE IF NOT EXISTS notice_list (" 
			        + "id         text primary key ," 
					+ "title      text," 
					+ "info         text,"
					+ "release_time         text," 
					+ "release_person         text," 
					+ "is_read         text,"
					+ "type         text," 
					+ "type_param	    text)";
			db.execSQL(sql);

			// sql = "CREATE TABLE IF NOT EXISTS tb_new_troubles " +
			// troublesField;
			// db.execSQL(sql);
			//
			// sql = "CREATE TABLE IF NOT EXISTS tb_my_troubles " +
			// troublesField;
			// db.execSQL(sql);
			//
			// sql = "CREATE TABLE IF NOT EXISTS tb_my_journey " +
			// troublesField;
			// db.execSQL(sql);

		}
	}

	public Map<String, Object> getOneRecord(String sql, String[] args) {
		synchronized (MySQLHelper.DBLookObj) {
			Map<String, Object> map = new HashMap<String, Object>();
			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, args);
			String[] columnNames = cursor.getColumnNames();
			if (cursor.moveToNext()) {
				for (String name : columnNames) {
					String value = cursor.getString(cursor.getColumnIndex(name));
					map.put(name, value);
				}
			}
			cursor.close();
			db.close();
			return map;
		}
	}

	public List<Map<String, Object>> getRecords(String sql) {
		return getRecords(sql, new String[] {});
	}

	public List<Map<String, Object>> getRecords(String sql, String[] args) {
		synchronized (MySQLHelper.DBLookObj) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			SQLiteDatabase db = getReadableDatabase();
			Log.v(TAG, "getRecords sql=" + sql);
			Cursor cursor = db.rawQuery(sql, args);
			String[] columnNames = cursor.getColumnNames();
			while (cursor.moveToNext()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (String name : columnNames) {
					String value = cursor.getString(cursor.getColumnIndex(name));
					map.put(name, value);
				}
				list.add(map);
			}

			db.close();
			cursor.close();
			return list;
		}
	}

	public String getOneString(String sql, String[] args) {
		synchronized (MySQLHelper.DBLookObj) {
			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, args);
			String ret = null;
			if (cursor.moveToNext()) {
				ret = cursor.getString(0);
			}
			cursor.close();
			db.close();
			return ret;
		}
	}

	public void executeSQL(String sql) {
		synchronized (MySQLHelper.DBLookObj) {
			SQLiteDatabase db = getWritableDatabase();
			db.execSQL(sql);
			db.close();
		}
	}

	public void executeSQL(String sql, Object[] args) {
		synchronized (MySQLHelper.DBLookObj) {
			SQLiteDatabase db = getWritableDatabase();
			db.execSQL(sql, args);
			db.close();
		}
	}

	public void executeSQL(String sql, LinkedHashMap<String, Object> linkedMap) {
		synchronized (MySQLHelper.DBLookObj) {
			Object[] objs = new Object[linkedMap.size()];
			int i = 0;
			for (Object value : linkedMap.values()) {
				objs[i] = value;
				i++;
			}
			SQLiteDatabase db = getWritableDatabase();
			db.execSQL(sql, objs);
			db.close();
		}
	}

	public void executeSQL(String sql, LinkedTreeMap<String, Object> linkedMap) {
		synchronized (MySQLHelper.DBLookObj) {
			Object[] objs = new Object[linkedMap.size()];
			int i = 0;
			for (Object value : linkedMap.values()) {
				objs[i] = value;
				i++;
			}
			SQLiteDatabase db = getWritableDatabase();
			db.execSQL(sql, objs);
			db.close();
		}
	}

}
