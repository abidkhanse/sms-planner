package project.planner.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;
import project.planner.activities.NotificationViewActivity;
import project.planner.db.ContactsTable;
import project.planner.db.DBAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.db.GroupInfoTable;
import project.planner.models.ContactInfo;
import project.planner.models.GroupInfo;
import project.planner.sms.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.util.Xml;

/**
 * This class create XML file of all groups and related contacts which user has
 * created in this allocation. This feature is very useful when user want to
 * replace gadget with new one.
 * 
 * @author KHAN
 */

public class GroupXmlCreator { 

	String groupcontacts = "/Group_Contacts.xml";
	String filename = "Group_Contacts.xml";
	String filesavedin = "";
	File newxmlfile = null;
	FileOutputStream fileos = null;
	List<GroupInfo> grouplist = new ArrayList<GroupInfo>();
	List<ContactInfo> contactlist = new ArrayList<ContactInfo>();
	GroupInfo info;
	Context context;

	public GroupXmlCreator(Context context) {

		this.context = context;
	}

	private String messageNote() {

		return context.getResources().getString(R.string.notification_message);
	}

	/**
	 * Check if internal storage is available for storage or storage is not
	 * write protected.
	 * 
	 * @return true if available, false otherwise
	 */
	boolean isInternalStorageAvailable() {
		boolean isavailable = true;
		newxmlfile = new File(context.getFilesDir() + groupcontacts);
		try {
			newxmlfile.createNewFile();
		} catch (IOException e) {
			notificationErrorMessage(context.getResources().getString(
					R.string.cant_create_file)
					+ context.getResources().getString(
							R.string.dl_folder_wrt_prt));
			return false;
		}
		fileos = null;

		try {
			fileos = new FileOutputStream(newxmlfile);
		} catch (FileNotFoundException e) {
			notificationErrorMessage(context.getResources().getString(
					R.string.cant_create_file)
					+ context.getResources().getString(
							R.string.dl_folder_wrt_prt));
			return false;
		}
		filesavedin = "Internal Storage";
		return isavailable;
	}

	/**
	 * Check if external storage is available for storage or storage is not
	 * write protected.
	 * 
	 * @return true if available, false otherwise
	 */
	boolean isExternalStorageAvailable() {
		boolean isavailable = true;
		isavailable = android.os.Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS).canWrite();
		if (!isavailable) {
			return isavailable;
		}

		newxmlfile = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
						+ groupcontacts);
		try {
			newxmlfile.createNewFile();

		} catch (IOException e) {
			notificationErrorMessage(context.getResources().getString(
					R.string.cant_create_file)
					+ context.getResources().getString(
							R.string.no_space_dl_foder));

			return false;
		}

		fileos = null;
		try {
			fileos = new FileOutputStream(newxmlfile);

		} catch (FileNotFoundException e) {
			notificationErrorMessage(context.getResources().getString(
					R.string.cant_create_file)
					+ context.getResources().getString(
							R.string.dl_folder_wrt_prt));

			isavailable = false;
			return isavailable;
		}

		filesavedin = "Download Folder";
		return isavailable;
	}

	private String RefineString(String str) {
		return str.replaceAll("-", "");
	}

	/**
	 * Check if external or internal storage is available for saving file. fetch
	 * all group data from database and create xml file and save in download
	 * folder.
	 * 
	 * @return true if successful, false otherwise
	 */
	public boolean LoadContactsList() {
		if (!isExternalStorageAvailable())
			if (!isInternalStorageAvailable())
				return false;

		if (grouplist.size() > 0) {
			DataBaseOperations operations = DataBaseOperations
					.getInstance(this.context);
			int i = 0;
			String groupname = "";
			String groupid = "";
			String[] array = { ContactsTable.CONTACT_CONTACTPHONE };
			XmlSerializer serializer = Xml.newSerializer();
			try {
				serializer.setOutput(fileos, "UTF-8");
				serializer.startTag(null, "root");
				while (grouplist.size() > i) {
					groupname = grouplist.get(i).getName();
					groupid = grouplist.get(i).getID();
					String where = ContactsTable.CONTACT_GROUPID + " = "
							+ groupid;

					Cursor c = operations.GetContact(where,
							ContactsTable.TABLE_NAME, array,
							ContactsTable.CONTACT_CONTACTNAME);
					if (c.moveToFirst()) {
						do {
							serializer.startTag(null, "group");
							serializer.startTag(null, "groupname");
							serializer.text(groupname);
							serializer.endTag(null, "groupname");
							serializer.startTag(null, "contactname");
							serializer.text(c.getString(0).toString());
							serializer.endTag(null, "contactname");
							serializer.startTag(null, "phone");
							serializer.text(RefineString(c.getString(1)
									.toString()));
							serializer.endTag(null, "phone");
							serializer.endTag(null, "group");
						} while (c.moveToNext());
					}
					i++;
				}
				serializer.endTag(null, "root");
				serializer.endDocument();
				serializer.flush();
			} catch (Exception e) {
				notificationErrorMessage("Can't create " + groupcontacts);
				return false;
			}
			try {
				String ticket = filename + " Created in " + filesavedin;
				NotificationMessage(ticket);
				fileos.close();
			} catch (IOException e) {
				return false;
			}
		} else {
			notificationErrorMessage((context.getResources()
					.getString(R.string.no_group_found)));

		}
		return false;
	}

	/**
	 * Notify user with result
	 * 
	 * @param ticket
	 */
	private void NotificationMessage(String ticket) {
		NotificationManager myNotificationManager;
		myNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String notificationticket = context.getResources().getString(
				R.string.file_saved);
		String notificationtitle = context.getResources().getString(
				R.string.file_save_dl_folder);
		String NotificationContent = ticket;

		Notification notification = new Notification(R.drawable.ic_launcher,
				notificationticket, 0);

		Intent _intent = new Intent(context, NotificationViewActivity.class);
		_intent.putExtra("contacts", notificationtitle);
		_intent.putExtra("message", ticket + messageNote());

		PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
				_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, notificationtitle,
				NotificationContent, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		myNotificationManager.notify(1, notification);

	}

	private void notificationErrorMessage(String message) {
		NotificationManager myNotificationManager;
		myNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String notificationticket = context.getResources().getString(
				R.string.error);
		String notificationtitle = context.getResources().getString(
				R.string.group_contacts_xml);

		Notification notification = new Notification(R.drawable.ic_launcher,
				notificationticket, 0);

		notification.setLatestEventInfo(context, notificationtitle, message,
				null);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		myNotificationManager.notify(1, notification);

	}

	public boolean LoadGroupList() {
		boolean valid = true;

		DataBaseOperations db = DataBaseOperations.getInstance(this.context);
		Cursor c = db.GetAllRecords(GroupInfoTable.TABLE_NAME, null, null);
		if (c.moveToFirst()) {
			do {
				grouplist.add(new GroupInfo(c.getString(0), c.getString(1)));
			} while (c.moveToNext());
		} else {
			notificationErrorMessage(context.getResources().getString(
					R.string.no_group_found));
			valid = false;
		}
		return valid;
	}

}