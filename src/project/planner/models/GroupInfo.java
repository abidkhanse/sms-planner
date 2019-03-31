package project.planner.models;


/**
 * GroupInfo class is Persistent object class bind with GROUPINFO table. 
 * 
 * @author KHAN
 *
 */

public class GroupInfo {
	
	String id;
	String name;
	String timestamp;
	
	public GroupInfo(String _id, String _name) {
		id= _id;
		name = _name;
		timestamp = "";
	}
	
	public GroupInfo(String _id, String _name, String _timestamp) {
		id= _id;
		name = _name;
		timestamp = _timestamp;
	}
	
	public String getID(){
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getTimeStamp(){
		 return timestamp;
	}
}
