package project.planner.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * 
 * Class SmsRecordsTable, responsible to create SMSRECORDINFO table.
 * 
 * This table class contains following columns
 *  <p>
 *  SMSRECORD_ID            unique id
 *  <p>
 *	SMSRECORD_TITLE         sms title
 *  <p>
 *	SMSRECORD_MESSAGE       sms text
 *  <p>	
 *	SMSRECORD_CONTACTS      contacts number
 *  <p>
 *	SMSRECORD_MESSAGEID     message id
 *  <p>
 *	SMSRECORD_INTENTID      intent id
 *  <p>
 *	SMSRECORD_VALID         is message valid
 *  <p>
 *	SMSRECORD_TIMECOUNT     estmated time
 *  <p>
 *	SMSRECORD_DELIVERED     is message delivered
 *  <p>
 *	SMSRECORD_TIMESTAMP     time stamp of creation time
 * 
 * @author KHAN
 */

public class SmsRecordsTable {
	
	 
	
	public static final String SMSRECORD_ID 		= "ID"; 	        
	public static final String SMSRECORD_TITLE 		= "smstitle";		
	public static final String SMSRECORD_MESSAGE 	= "smsmessage";		
	public static final String SMSRECORD_CONTACTS 	= "contacts";		
	public static final String SMSRECORD_MESSAGEID 	= "messageid";		
	public static final String SMSRECORD_INTENTID 	= "intentid";		
	public static final String SMSRECORD_VALID 		= "isvalid";
	public static final String SMSRECORD_TIMECOUNT 	= "timecount";
	public static final String SMSRECORD_DELIVERED 	= "isdelivered";
	public static final String SMSRECORD_TIMESTAMP 	= "timestamp";
    
	public static final String TABLE_NAME = "smsrecordinfo";
	
	public static final String CREATE_SMSRECORD_TABLE_QUERY =   
			" create table if not exists smsrecordinfo(ID Integer primary key autoincrement," +
					  " smstitle VARCHAR not null, smsmessage VARCHAR not null," +
					  "	contacts VARCHAR not null, messageid Integer not null, intentid Integer not null," +
					  " isvalid Integer not null,timecount VARCHAR not null, isdelivered Integer not null, timestamp VARCHAR not null);";

	public static void onCreate(SQLiteDatabase db){
		try{
			db.execSQL(CREATE_SMSRECORD_TABLE_QUERY);		
		} catch (SQLException e) {
			e.printStackTrace();		
		}
	}
	
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        SmsRecordsTable.onCreate(db);
        
    }	

}
