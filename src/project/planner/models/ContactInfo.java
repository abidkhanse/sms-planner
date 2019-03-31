package project.planner.models;

/**
 * ContactInfo class is Persistent object class bind with CONTACTINFO table. 
 * 
 * @author KHAN
 *
 */

public class ContactInfo   {

	public String firstname;
	public int selected;
	public String phonenumber;

	public int isvalid;
	public int intentid;
	public int isdelivered;
	public String smstitle;
	public String smsmessage;
	public String timecount;
	public String contacts;

	
	public ContactInfo(String smstitle, String displaytime, String timecount,int isvalid, int intentid, int isdelivered, String smsmessage, String contacts )
	{
		this.firstname			= smstitle;
		this.phonenumber 		= displaytime;
		
		this.smstitle			= smstitle;
		this.timecount 			= timecount;
		
		
		this.isvalid 			= isvalid;
		this.intentid 			= intentid;
		this.isdelivered 		= isdelivered;
		this.smsmessage 		= smsmessage;
		this.contacts			= contacts;
		
		this.selected 			= -1;
	}

	public ContactInfo(String firstname, int selected, String phonenumber,int isvalid)
	{
		this.firstname 		= firstname;
		this.selected 		= selected;
		this.phonenumber 	= phonenumber;
		this.isvalid 		= isvalid;
		intentid 		= -1;
		isdelivered 	= -1;
		smsmessage 		= "";
		timecount 		= "";
		smstitle 		= "";
	}
	
	public String GetContas(){
		return contacts;
	 }
	
	public void setContacts(String contacts){
		this.contacts = contacts;
	}
	
	public String getSmsTitle() {
		return smstitle;
	}

	public void setSmsTitle(String smstitle) {
		this.smstitle = smstitle;
	}

	public String getTimeinstring() {
		return timecount;
	}

	public void setTimeinstring(String timecount) {
		this.timecount = timecount;
	}

	public String GetMessage(){
		return smsmessage;
	}

	public void SetMessage(String smsmessage) {
		this.smsmessage = smsmessage;
	}

	public String GetFirstName(){
		return firstname;
	}

	public int isSelected() {
		return selected;
	}

	public String GetPhoneNumber() {

		return phonenumber;
	}

	public void SetPhoneNumber(String phonenumber){

		this.phonenumber = phonenumber;
	}

	public int getIsvalid() {
		return isvalid;
	}

	public void setIsvalid(int isvalid) {
		this.isvalid = isvalid;
	}

	public int getIntentid() {
		return intentid;
	}

	public void setIntentid(int intentid) {
		this.intentid = intentid;
	}

	public int getIsdelivered() {
		return isdelivered;
	}

	public void setIsdelivered(int isdelivered) {
		this.isdelivered = isdelivered;
	}


}