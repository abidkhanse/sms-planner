package project.planner.models;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * ParcelData class which is implemented with parcelable interface. 
 * This class is used to send data in object form from one activity to other.
 * 
 * @author KHAN
 *
 */
public class ParcelData implements Parcelable{

	String firstname;
	String phonenumber;
	
	public ParcelData() {
		
		firstname =	"";
		phonenumber = "";
	}

	public ParcelData(Parcel in) {
		
		firstname 	=		in.readString();
		phonenumber =		in.readString();
	}

	public void SetFirstName(String name) {
		firstname = name;
	}

	public void SetPhoneNumber(String phone) {
		phonenumber = phone;
	}
	
	public String GetFirstName() {
		return firstname ;
	}
	public String GetPhoneNumber() {
		return phonenumber;
	}
//
	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel parce, int arg1) {
		parce.writeString(firstname);
		parce.writeString(phonenumber);
	}

	public static final Parcelable.Creator<ParcelData> CREATOR = new Parcelable.Creator<ParcelData>(){
		public ParcelData createFromParcel(Parcel in){
			return new ParcelData(in);
		}
		public ParcelData[] newArray(int size){
			return new ParcelData[size];
		}
	};
}