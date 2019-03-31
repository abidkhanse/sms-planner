package project.planner.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**  
 * Database class, responsible to create GROUPCONTACT table. This table class contains group information including   
 * name, contact and groupid 
 * 
 * @author KHAN
 */


public class GroupContactTable {

	public static final String TABLE_NAME = "groupcontact";

	public static final String GROUPCONTACT_ID = BaseColumns._ID;
	public static final String GROUPCONTACT_GROUPID = "groupid";
	public static final String GROUPCONTACT_NAME = "name";
	public static final String GROUPCONTACT_PHONE = "phone";

	private static final String CREATE_GROUP_TABLE_QUERY = "create table if not exists "
			+ TABLE_NAME
			+ "( "
			+ GROUPCONTACT_ID
			+ " integer primary key autoincrement, "
			+ GROUPCONTACT_GROUPID
			+ " integer not null, "
			+ GROUPCONTACT_NAME
			+ " text, "
			+ GROUPCONTACT_PHONE + " text not null);";

	public static void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(CREATE_GROUP_TABLE_QUERY);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		GroupContactTable.onCreate(db);
	}

}
