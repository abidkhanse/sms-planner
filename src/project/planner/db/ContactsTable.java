package project.planner.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

 
/**
 * Database class, responsible to create CONTACTINFO table. This class contains user contacts including 
 * contact name and phone number. Groupid field works as foreign key connected with GROUPINFO table.
 * 
 * @author KHAN
 */

public class ContactsTable {
	
	public static final String TABLE_NAME = "CONTACTINFO";
	
	public static final String CONTACT_ID 				= BaseColumns._ID;
	public static final String CONTACT_GROUPID 			= "groupid";
	public static final String CONTACT_CONTACTNAME 		= "contactname";
	public static final String CONTACT_CONTACTPHONE 	= "contactphone";
	
	
	
	private static final String CREATE_CONTACT_TABLE_QUERY 	= 
												"CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + "(" +
												"ID INTEGER PRIMARY KEY AUTOINCREMENT," 		+
												"GROUPID INTEGER NOT NULL," 					+
												"CONTACTNAME VARCHAR NOT NULL," 				+
												"CONTACTPHONE VARCHAR NOT NULL);" ;

	public static void onCreate(SQLiteDatabase db){
		try
		{
			db.execSQL(ContactsTable.CREATE_CONTACT_TABLE_QUERY);
		
		} catch (SQLException e) {
			e.printStackTrace();		
		}
	}
	
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ContactsTable.TABLE_NAME);
        ContactsTable.onCreate(db);
    }	
	

}
