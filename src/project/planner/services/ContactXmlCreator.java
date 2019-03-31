package project.planner.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import project.planner.activities.NotificationViewActivity;
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
import android.provider.ContactsContract;
import android.util.Xml;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * This class create XML file of all contacts from mobile phone This feature is
 * very useful when user want to replace gadget with new one.
 * 
 * @author KHAN
 */

public class ContactXmlCreator {
	CheckBox check;
	int count;

	String phonecontact = "/Phone_Contacts.xml";
	String filename = "Phone_Contacts.xml";
	String filesavedin = "";
	File newxmlfile = null;
	FileOutputStream fileos = null;
	List<GroupInfo> grouplist = new ArrayList<GroupInfo>();
	List<ContactInfo> contactlist = new ArrayList<ContactInfo>();
	GroupInfo info;
	Context context;

	List<ContactInfo> list = new ArrayList<ContactInfo>();
	List<ContactInfo> myList = new ArrayList<ContactInfo>();

	public ContactXmlCreator(Context c) {
		context = c;
	}

	/**
	 * Check if internal storage is available for storage or storage is not write protected.
	 * @return true if available, false otherwise
	 */
	boolean isInternalStorageAvailable() {

		boolean isindependent = true;
		newxmlfile = new File(context.getFilesDir() + phonecontact);
		try {
			newxmlfile.createNewFile();

		} catch (IOException e) {
			Toast toast = Toast.makeText(context,
					"No Space in External Storage OR Read only memory",
					Toast.LENGTH_SHORT);
			toast.show();
			isindependent = false;
			return isindependent;
		}

		fileos = null;
		try {
			fileos = new FileOutputStream(newxmlfile);
		} catch (FileNotFoundException e) {
			Toast toast = Toast.makeText(context,
					"can't create on file. Your Storage is write protected",
					Toast.LENGTH_SHORT);
			toast.show();
			isindependent = false;
			return isindependent;
		}
		filesavedin = "Internal Storage";

		return isindependent;
	}


	/**
	 * Check if external storage is available for storage or storage is not write protected.
	 * @return true if available, false otherwise
	 */
	boolean isExternalStorageAvailable() {
		boolean isindependent = true;
		isindependent = android.os.Environment
				.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_DOWNLOADS).canWrite();
		if (!isindependent) {
			return isindependent;
		}

		newxmlfile = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
						+ phonecontact);
		try {
			newxmlfile.createNewFile();

		} catch (IOException e) {
			notificationErrorMessage("Can't create file, No space on Download folder");
			isindependent = false;
			return isindependent;
		}

		fileos = null;
		try {
			fileos = new FileOutputStream(newxmlfile);

		} catch (FileNotFoundException e) {
			notificationErrorMessage("Can't create file. Download is write protected");
			isindependent = false;
			return isindependent;
		}

		filesavedin = "Download Folder";
		return isindependent;
	}

	List<ContactInfo> LoadContactListFromPhone() {
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if (cursor.moveToFirst()) {
			while (cursor.moveToNext()) {
				String contactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				String hasPhone = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				int hasph = Integer.parseInt(hasPhone);

				if (hasph > -1) {
					Cursor phones = context.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					if (phones.getCount() > 0) {
						while (phones.moveToNext()) {
							String phoneNumber = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String phonename = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
							list.add(new ContactInfo(phonename, 0, phoneNumber,
									0));
						}
					}
					phones.close();
				}
			}
			myList = list;
		} else {
			notificationErrorMessage("No Contact Found");

		}
		cursor.close();
		return myList;
	}
	
	/**
	 * Check if external or internal storage is available for saving file.
	 * fetch all group data from database and create xml file and save in download folder.
	 * 
	 * @return true if successful, false otherwise
	 */

	public boolean CreateXMLFile() {

		LoadContactListFromPhone();
		if (!isExternalStorageAvailable())
			if (!isInternalStorageAvailable())
				return false;

		int j = 0;
		if (list.size() > 0) {
			XmlSerializer serializer = Xml.newSerializer();

			try {
				serializer.setOutput(fileos, "UTF-8");
				serializer.startTag(null, "contact");
				int i = 0;

				while (list.size() > i) {
					ContactInfo info = list.get(i);
					String contactname = info.GetFirstName();
					String phone = info.GetPhoneNumber();
					serializer.startTag(null, "group");

					serializer.startTag(null, "contactname");
					serializer.text(contactname);
					serializer.endTag(null, "contactname");

					serializer.startTag(null, "phone");
					serializer.text(phone);
					serializer.endTag(null, "phone");
					serializer.endTag(null, "group");

					i++;
					j++;

				}
				serializer.endTag(null, "contact");
				serializer.endDocument();
				serializer.flush();

			} catch (Exception e) {
				notificationErrorMessage("Cannot create Contact file");
				return false;
			}
			try {
				String ticket = filename + " Created in " + filesavedin;
				NotificationMessage(ticket);
				fileos.close();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * Notify user with error result
	 * @param message
	 */
	private void notificationErrorMessage(String message) {
		NotificationManager myNotificationManager;
		myNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String notificationticket = "File Error...";
		String notificationtitle = "Phone_Contacts.xml file";

		Notification notification = new Notification(R.drawable.ic_launcher,notificationticket, 0);
		notification.setLatestEventInfo(context, notificationtitle, message,
				null);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		myNotificationManager.notify(1, notification);

	}

	/**
	 * Notify user with result
	 * @param ticket
	 */
	private void NotificationMessage(String ticket) {
		NotificationManager myNotificationManager;
		myNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String NotificationTicket = "File Saved";
		String NotificationTitle = "File Saved in Download folder ";
		String NotificationContent = ticket;

		Notification notification = new Notification(R.drawable.ic_launcher,
				NotificationTicket, 0);

		Intent _intent = new Intent(context, NotificationViewActivity.class);
		_intent.putExtra("contacts", NotificationTitle);
		_intent.putExtra("message", ticket + messageNote());

		PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
				_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, NotificationTitle,
				NotificationContent, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		myNotificationManager.notify(1, notification);
	}

	private String messageNote() {
		return "\n\nNow you can keep your important contacts secure in your device, "
				+ "in future if you plan to change your mobile/tablet, Just copy this file"
				+ " to your new device and load it by using Load Contacts option "
				+ "Your address book will be updated with friends and family phone contacts";

	}

}