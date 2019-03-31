package project.planner.models;


/**
 * SmsInfo class is Persistent object class bind with SMSRECORDINFO table. 
 * 
 * @author KHAN
 *
 */
public class SmsInfo {
	
	public String title;
	public int isvalid;
	public int intentid;
	public String isdelivered;
	public String timeleft;
		

		public SmsInfo(String title, int isvalid, int intentid, String isdelivered,String timeleft) {
			super();
			this.title = title;
			this.isvalid = isvalid;
			this.intentid = intentid;
			this.isdelivered = isdelivered;
			this.timeleft = timeleft;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
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

		public String getIsdelivered() {
			return isdelivered;
		}

		public void setIsdelivered(String isdelivered) {
			this.isdelivered = isdelivered;
		}

		public String getTimeleft() {
			return timeleft;
		}

		public void setTimeleft(String timeleft) {
			this.timeleft = timeleft;
		}

}
