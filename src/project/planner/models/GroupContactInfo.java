package project.planner.models;


/**
 * GroupContactInfo class is Persistent object class bind with GROUPCONTACT table. 
 * 
 * @author KHAN
 *
 */

public class GroupContactInfo {
	public String firstname;
	public int selected;
	public String phonenumber;

		public GroupContactInfo(String name, int selected, String phonenumber) {
			this.firstname = name;
			this.selected = selected;
			this.phonenumber = phonenumber;
		}

		String GetFirstName() {

			return firstname;
			
		}

		public int isSelected() {
			return selected;
		}

		String GetPhoneNumber() {

			return phonenumber;
	
		}

}
