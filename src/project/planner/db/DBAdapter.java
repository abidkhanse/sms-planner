package project.planner.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database adapter class, instantiate all table classes of this application
 * 
 * @author KHAN
 */
public class DBAdapter extends SQLiteOpenHelper {


	public static final String DATABASE_NAME = "smsplanner";
	public static final int DATABASE_VERSION = 1;

	public DBAdapter(Context context) {
		super(context, DBAdapter.DATABASE_NAME, null,
				DBAdapter.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		GroupContactTable.onCreate(db);
		GroupInfoTable.onCreate(db);
		ContactsTable.onCreate(db);
		SmsRecordsTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ContactsTable.onUpgrade(db, oldVersion, newVersion);
		GroupInfoTable.onUpgrade(db, oldVersion, newVersion);
		GroupContactTable.onUpgrade(db, oldVersion, newVersion);
		SmsRecordsTable.onUpgrade(db, oldVersion, newVersion);
	}

	public DBAdapter open() throws SQLException {
		getWritableDatabase();
		return this;
	}

}
