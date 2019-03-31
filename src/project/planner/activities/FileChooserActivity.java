package project.planner.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import project.planner.adapters.FileArrayAdapter;
import project.planner.db.DataBaseOperations;
import project.planner.models.Option;
import project.planner.models.globalVariables;
import project.planner.sms.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * An activity that help user to select valid xml file from local directory.
 * User can insert all mobile contacts from xml file to mobile directory. This
 * feature can be used when user replaces new gadget with old one.
 */
public class FileChooserActivity extends ListActivity {

	private File currentDir;
	private FileArrayAdapter adapter;
	boolean allowed = true;
	String selectedfile = "";
	Document dom;
	SAXParser sp;
	Stack<File> dirStack = new Stack<File>();
	ImageView btnSelect;
	EditText inputSearch;
    
    Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentDir = new File(getFilesDir().toString());
		fill(currentDir);
		setContentView(R.layout.smsplanneractivity);
		btnSelect = (ImageView) findViewById(R.id.SelectButton);

		inputSearch = (EditText) findViewById(R.id.inputSearch);
		inputSearch.setVisibility(View.GONE);
        context = getApplicationContext();
	
    }

	public void onClick(View v) {
		finish();
	}

	/**
	 * 
	 * Fill list view with supplied directory. Show folder name, file name and
	 * file size.
	 * 
	 * @param File
	 * 
	 */

	private void fill(File file) {
		File[] dirs = file.listFiles();

		this.setTitle("Current Dir: " + file.getName());
		List<Option> dir = new ArrayList<Option>();
		List<Option> fls = new ArrayList<Option>();
		try {
			for (File ff : dirs) {
				if (ff.isDirectory()) {
					dir.add(new Option(ff.getName(), "Folder", ff
							.getAbsolutePath()));
				} else {
					fls.add(new Option(ff.getName(), "File Size: "
							+ ff.length(), ff.getAbsolutePath()));
				}
			}
		} catch (Exception e) {
		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!file.getName().equalsIgnoreCase(""))
			dir.add(0, new Option("..", "Parent Directory", file.getParent()));
		adapter = new FileArrayAdapter(this, R.layout.file_view, dir);
		if (adapter != null)
			this.setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (allowed) {
			Option o = adapter.getItem(position);
			if (o != null && !o.getPath().isEmpty()) {
				if ((o.getData().equalsIgnoreCase("folder") || o.getData()
						.equalsIgnoreCase("parent directory"))) {
					dirStack.push(currentDir);
					currentDir = new File(o.getPath());
					if (currentDir.getName() == "") {
						allowed = false;
					}

					fill(currentDir);
				} else {
					onFileClick(o);
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (dirStack.size() == 0)
			return;

		currentDir = dirStack.pop();
		fill(currentDir);
	}

	private void createOKDialog(String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(message);
		alertDialog
				.setMessage(
                            context.getResources().getString(R.string.updaing_group_book) +
                            "..." +
                            context.getResources().getString(R.string.feel_free_use_device)
                            );
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}

	/**
	 * 
	 * Checks if selected file is xml and check if selected xml files is valid.
	 * 
	 * @param Option contains requested information.
	 * 
	 */
	private void onFileClick(Option o) {
		selectedfile = o.getName();
		String extenshion = selectedfile.substring(selectedfile.length() - 4,
				selectedfile.length());
		if (!globalVariables.loadgroupxmlinprogress) {

			if (extenshion.compareTo(".xml") == 0) {
				if (IsValidXMLFile(o.getPath().toString())) {
					AreYouSure(o.getPath().toString());
				} else {
					Toast.makeText(this, "No valid group_contact.xml file found", Toast.LENGTH_SHORT)
					.show();
				}
			} else {
				Toast.makeText(this, "select xml file", Toast.LENGTH_SHORT)
				.show();
			}
		} else {
			createOKDialog(context.getResources().getString(R.string.in_progress));
		}
	}

	private String RefineString(String str) {
		return str.replaceAll("-", "");
	}

	/**
	 * Ask user to proceed.
	 * 
	 * @param String xml file name.
	 * 
	 */

	private void AreYouSure(final String filename) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle(context.getResources().getString(R.string.confirmation));
        
		myAlertDialog.setMessage(context.getResources().getString(R.string.updaing_group_book) +
                                 context.getResources().getString(R.string.group_contacts_xml)
        );
		
        myAlertDialog.setPositiveButton(context.getResources().getString(R.string.continue_progress),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						startProgress(filename);
						createOKDialog(context.getResources().getString(R.string.in_progress));
					}
				});
		myAlertDialog.setNegativeButton(context.getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {

						// do something when the Cancel button is clicked
						//
					}
				});
		myAlertDialog.show();
	}

	/**
	 * Thread start for reading XML file and saving into gadget.
	 * 
	 * @param String xml file name.
	 * 
	 */
	public void startProgress(final String f) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				ReadContactsFromXML(f);
			}
		};
		new Thread(runnable).start();
	}

	/**
	 * Check if selected file is valid.
	 * 
	 * @param String xml file name.
	 */
	private boolean IsValidXMLFile(String file) {
		try {
			InputStream inputstream = null;
			Document doc = null;
			DocumentBuilderFactory documentbuilderfactory = null;
			DocumentBuilder documentbuilder = null;
			inputstream = new FileInputStream(file);
			documentbuilderfactory = DocumentBuilderFactory.newInstance();
			documentbuilder = documentbuilderfactory.newDocumentBuilder();
			doc = documentbuilder.parse(inputstream);
			if (null != doc) {

				NodeList items = doc.getElementsByTagName("root");
				if (items.item(0).getChildNodes().getLength() > 0) {
					return true;

				}
			}
		} catch (Exception e) {
			return false;

		}

		return false;
	}

	/**
	 * Notification if successful or not.
	 * 
	 * @param String message
	 * @param String error type
	 * 
	 */

	private void notificationErrorMessage(String message, String error) {
		NotificationManager myNotificationManager;
		myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String notificationticket = error;
		String notificationtitle =   context.getResources().getString(R.string.phone_contacts_xml);
       	
        Notification notification = new Notification(R.drawable.ic_launcher,
				notificationticket, 0);
		notification.setLatestEventInfo(this, notificationtitle, message, null);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		myNotificationManager.notify(1, notification);
	}

	/**
	 * Read contact information for xml file.
	 * @param String xml file name.
	 * 
	 */

	private void ReadContactsFromXML(String file) {
		try {
			InputStream inputstream = null;
			Document doc = null;
			DocumentBuilderFactory documentbuilderfactory = null;
			DocumentBuilder documentbuilder = null;
			inputstream = new FileInputStream(file);
			documentbuilderfactory = DocumentBuilderFactory.newInstance();
			documentbuilder = documentbuilderfactory.newDocumentBuilder();
			doc = documentbuilder.parse(inputstream);
			if (null != doc) {
				NodeList items = doc.getElementsByTagName("root");
				if (items.item(0).getChildNodes().getLength() > 0) {
					globalVariables.loadgroupxmlinprogress = true;
					DataBaseOperations operations = DataBaseOperations
							.getInstance(context);
					for (int i = 0; i < items.getLength(); i++) {
						Node item = items.item(i);
						NodeList properties = item.getChildNodes();
						String groupname = "";
						String phone = "";
						long groupid = -1;
						for (int j = 0; j < properties.getLength(); j++) {
							Node property = properties.item(j);
							NodeList childnodelist = property.getChildNodes();

							phone = RefineString(childnodelist.item(2)
									.getTextContent());
							if (groupname.isEmpty()) {
								groupname = childnodelist.item(0)
										.getTextContent().toString();
								groupid = operations.InsertGroupName(groupname);
								if (groupid > -1) {
									operations.InsertContactInfo(groupid,
											childnodelist.item(1)
													.getTextContent(), phone);
								}

							} else if (groupname
									.toLowerCase()
									.trim()
									.compareTo(
											childnodelist.item(0)
													.getTextContent()
													.toLowerCase().trim()
													.toString()) == 0) {
								operations.InsertContactInfo(groupid,
										childnodelist.item(1).getTextContent(),
										phone);
							} else {
								groupname = childnodelist.item(0)
										.getTextContent().toString();
								groupid = operations.InsertGroupName(groupname);
								if (groupid > -1) {
									operations.InsertContactInfo(groupid,
											childnodelist.item(1)
													.getTextContent(), phone);
								}
							}
						}
					}
					globalVariables.loadgroupxmlinprogress = false;
					finish();
				} else {
					globalVariables.loadgroupxmlinprogress = false;
                                                            
					notificationErrorMessage(context.getResources().getString(R.string.no_information_found),
                                             context.getResources().getString(R.string.file_error));
				}
			}
		} catch (Exception e) {

		}
	}
}
