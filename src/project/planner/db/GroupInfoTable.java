package project.planner.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * 
 * Class GroupInfoTable, responsible to create GROUPINFO table. This table class contains groupid and groupname 
 * 
 * @author KHAN
 */
public class GroupInfoTable {
	public static final String TABLE_NAME = "GROUPINFO";
	
	public static final String GROUP_ID = "ID";
	public static final String GROUP_GROUPNAME = "groupname";

	private static final String CREATE_GROUP_INFO_TABLE_QUERY = "create table if not exists "
			+ TABLE_NAME
			+ "( "
			+ 		GROUP_ID
			+ 		" integer primary key autoincrement, "
			+ 		GROUP_GROUPNAME + " text not null" 
			+ ");";

	public static void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(GroupInfoTable.CREATE_GROUP_INFO_TABLE_QUERY);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + GroupInfoTable.TABLE_NAME);
		// Create tables again
		GroupInfoTable.onCreate(db);
	}

}
