package project.planner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * DatabaseOperation class, responsible to perform all important transactions
 * between database and logic layer.
 * 
 * @author KHAN
 * 
 */
public class DataBaseOperations {

	static DBAdapter dbAdapter;
	private static DataBaseOperations operations = null;
	SQLiteDatabase DB;

	private DataBaseOperations() {
	}

	public static DataBaseOperations getInstance(Context context) {
		if (operations == null) {
			dbAdapter = new DBAdapter(context);
			operations = new DataBaseOperations();
		}
		return operations;
	}

	/**
	 * 
	 * Inserts all important message information in SmsRecordTable.
	 * 
	 * @param smstitle
	 *            title of sms
	 * @param message
	 *            sms text
	 * @param contacts
	 *            contact number
	 * @param messageid
	 *            message id
	 * @param intentid
	 *            intent id
	 * @param valid
	 *            is message valid to transfer
	 * @param timecount
	 *            message time
	 * @param isdelivered
	 *            is delivered ot not
	 * @param timestamp
	 *            current time stamp
	 * 
	 * @return row ID of newly inserted row, otherwise -1
	 */
	public long InsertMessageInfo(String smstitle, String message,
			String contacts, long messageid, long intentid, int valid,
			long timecount, long isdelivered, String timestamp) {
		DB = dbAdapter.getWritableDatabase();
		ContentValues contectvalues = new ContentValues();
		contectvalues.put(SmsRecordsTable.SMSRECORD_TITLE, smstitle);
		contectvalues.put(SmsRecordsTable.SMSRECORD_MESSAGE, message);
		contectvalues.put(SmsRecordsTable.SMSRECORD_CONTACTS, contacts);
		contectvalues.put(SmsRecordsTable.SMSRECORD_MESSAGEID, messageid);
		contectvalues.put(SmsRecordsTable.SMSRECORD_INTENTID, intentid);
		contectvalues.put(SmsRecordsTable.SMSRECORD_VALID, valid);
		contectvalues.put(SmsRecordsTable.SMSRECORD_TIMECOUNT, timecount);
		contectvalues.put(SmsRecordsTable.SMSRECORD_DELIVERED, isdelivered);
		contectvalues.put(SmsRecordsTable.SMSRECORD_TIMESTAMP, timestamp);

		return DB.insert(SmsRecordsTable.TABLE_NAME, null, contectvalues);
	}

	/**
	 * 
	 * Update existing sms record in table.
	 * 
	 * @param rowid
	 * @param messageid
	 * @param intentid
	 * @param isvalid
	 * @return
	 */
	public int UpdateMessageInfo(long rowid, long messageid, long intentid,
			long isvalid) {
		DB = dbAdapter.getWritableDatabase();
		ContentValues contentvalue = new ContentValues();
		contentvalue.put(SmsRecordsTable.SMSRECORD_MESSAGEID, messageid);
		contentvalue.put(SmsRecordsTable.SMSRECORD_INTENTID, intentid);
		contentvalue.put(SmsRecordsTable.SMSRECORD_VALID, isvalid);

		return DB.update(SmsRecordsTable.TABLE_NAME, contentvalue,
				SmsRecordsTable.SMSRECORD_ID + "=" + rowid, null);
	}

	/**
	 * Update table if message is is valid or not
	 * 
	 * @param requestcode
	 * @param isvalid
	 * @return number of rows effected
	 */
	public int UpdateIsValidStatus(long requestcode, long isvalid) {

		DB = dbAdapter.getWritableDatabase();
		ContentValues contentvalue = new ContentValues();
		contentvalue.put(SmsRecordsTable.SMSRECORD_VALID, isvalid);
		return DB.update(SmsRecordsTable.TABLE_NAME, contentvalue,
				SmsRecordsTable.SMSRECORD_INTENTID + "=" + requestcode, null);

	}

	/**
	 * 
	 * update table if sms delivered successfuly or not
	 * 
	 * @param rowid
	 * @param isdelivered
	 * @return the number of rows affected
	 */

	public int UpdateStatusInfo(String rowid, long isdelivered) {
		DB = dbAdapter.getWritableDatabase();
		ContentValues contentvalue = new ContentValues();
		contentvalue.put(SmsRecordsTable.SMSRECORD_DELIVERED, isdelivered);
		return DB.update(SmsRecordsTable.TABLE_NAME, contentvalue,
				SmsRecordsTable.SMSRECORD_ID + "=" + rowid, null);
	}

	/**
	 * 
	 * Update ContactsTable, inserts contact information in against specific
	 * group.
	 * 
	 * @param groupid
	 * @param contactname
	 * @param phone
	 * 
	 * @return row id of newly inserted row, otherwise -1
	 * 
	 */
	public long InsertContactInfo(long groupid, String contactname, String phone) {
		DB = dbAdapter.getWritableDatabase();
		ContentValues contectvalues = new ContentValues();
		contectvalues.put(ContactsTable.CONTACT_CONTACTNAME, contactname);
		contectvalues.put(ContactsTable.CONTACT_CONTACTPHONE, phone);
		contectvalues.put(ContactsTable.CONTACT_GROUPID, groupid);
		return DB.insert(ContactsTable.TABLE_NAME, null, contectvalues);
	}

	/**
	 * delete one row
	 * 
	 * @param groupid
	 * @return number of row deleted, 0 otherwise
	 */

	public boolean DeleteContactInfo(long groupid) {
		DB = dbAdapter.getWritableDatabase();
		return DB.delete(ContactsTable.TABLE_NAME,
				ContactsTable.CONTACT_GROUPID + " = " + groupid, null) > 0;
	}

	/**
	 * insert group name in groupinfotable
	 * 
	 * @param groupname
	 * @return the number of rows affected
	 */
	public long InsertGroupName(String groupname) {
		DB = dbAdapter.getWritableDatabase();
		ContentValues contectvalues = new ContentValues();
		contectvalues.put(GroupInfoTable.GROUP_GROUPNAME, groupname);
		return DB.insert(GroupInfoTable.TABLE_NAME, null, contectvalues);
	}

	/**
	 * 
	 * update group name
	 * 
	 * @param rowid
	 * @param groupname
	 * @return the number of rows affected
	 */
	public int UpdateGroupName(long rowid, String groupname) {
		DB = dbAdapter.getWritableDatabase();
		ContentValues contentvalue = new ContentValues();
		contentvalue.put(GroupInfoTable.GROUP_GROUPNAME, groupname);
		return DB.update(GroupInfoTable.TABLE_NAME, contentvalue,
				GroupInfoTable.GROUP_ID + "=" + rowid, null);
	}

	/**
	 * delete group name in groupinfotable
	 * 
	 * @param rowid
	 * @return number of row deleted, 0 otherwise
	 */
	public boolean DeleteGroupRecord(long rowid) {
		DB = dbAdapter.getWritableDatabase();
		return DB.delete(GroupInfoTable.TABLE_NAME, GroupInfoTable.GROUP_ID
				+ " = " + rowid, null) > 0;
	}

	/**
	 * pass table name and array of fields name and get first cursor to all
	 * records.
	 * 
	 * @param TABLE_NAME
	 * @param array
	 * @param orderby
	 * @return A Cursor object, which is positioned before the first entry
	 */
	public Cursor GetAllRecords(String TABLE_NAME, String[] array,
			String orderby) {
		DB = dbAdapter.getWritableDatabase();
		return DB.query(TABLE_NAME, array, null, null, null, null, orderby);
	}

	/**
	 * get number of records
	 * 
	 * @param TABLE_NAME
	 * @param array
	 * @return number of count
	 */
	public long GetRecordCount(String TABLE_NAME, String[] array) {
		DB = dbAdapter.getWritableDatabase();
		Cursor mCursor = DB.query(TABLE_NAME, array, null, null, null, null,
				null);
		return mCursor.getCount();
	}

	
	/**
	 *  
	 * get number of contacts
	 * 
	 * @param where
	 * @param tablename
	 * @param array
	 * @param orderby
	 * @return A Cursor object, which is positioned before the first entry
	 */
	public Cursor GetContact(String where, String tablename, String[] array,
			String orderby) {
		DB = dbAdapter.getWritableDatabase();
		Cursor mCursor = DB.query(true, tablename, array, where, null, null,
				null, orderby, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Pass table name , and Where clause. It will return number existing rows,
	 * otherwise -1 .
	 */

	/**
	 * 
	 * Check if record exists
	 * @param whereclause
	 * @param tablename
	 * @return number of count otherwise -1
	 */
	public int IsRecordExists(String whereclause, String tablename) {
		DB = dbAdapter.getWritableDatabase();
		Cursor cursor = DB.rawQuery("select * from " + tablename + " where "
				+ whereclause, null);
		if (cursor != null) {
			cursor.moveToFirst();
			return cursor.getCount();
		}
		return -1;
	}


	/**
	 * get number of deleted rows.
	 * 
	 * @param tablename
	 * @param wherclause
	 * @return  the number of rows affected, 0 otherwise
	 * 
	 */
	public long DeleteRecords(String tablename, String wherclause) {
		DB = dbAdapter.getWritableDatabase();
		return DB.delete(tablename, wherclause, null);
	}

	
	/**
	 * 
	 * return number of rows
	 * @param tablename
	 * @param where
	 * @param orderby
	 * 
	 * @return return cursor object otherwise null
	 * 
	 */
	public Cursor GetSelectedRows(String tablename, String where, String orderby) {
		DB = dbAdapter.getWritableDatabase();
		Cursor mCursor = DB.query(true, tablename, null, where, null, null,
				null, orderby, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

}
