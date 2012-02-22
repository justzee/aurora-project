/**
 * Created on: 2004-6-15 17:14:17
 * Author:     zhoufan
 */
package uncertain.testcase.object;
import uncertain.ocm.IChildContainerAcceptable;
import uncertain.composite.CompositeMap;
import java.util.LinkedList;

public class ContactInfo implements IChildContainerAcceptable{

	String	email;
	int	phone = 0;
	String  msn;
	int	icq = 0;
	Integer	mobile;
	
	LinkedList	extra_info = new LinkedList();
	
	public ContactInfo(){
	    super();
	}
	
	public ContactInfo(String email){
	    this.email = email;
	}
	
	public ContactInfo(String email, int phone){
	    this.email = email;
	    this.phone = phone;
	}
	
	public String toString(){
		return "ContactInfo:email:"+email+",phone:"+phone+",msn:"+msn+",icq:"+icq+",mobile:"+mobile;
	}

	/**
	 * Returns the email.
	 * @return String
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns the icq.
	 * @return String
	 */
	public int getIcq() {
		return icq;
	}

	/**
	 * Returns the mobile.
	 * @return String
	 */
	public Integer getMobile() {
		return mobile;
	}

	/**
	 * Returns the msn.
	 * @return String
	 */
	public String getMsn() {
		return msn;
	}

	/**
	 * Returns the phone.
	 * @return String
	 */
	public int getPhone() {
		return phone;
	}

	/**
	 * Sets the email.
	 * @param email The email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the icq.
	 * @param icq The icq to set
	 */
	public void setIcq(int icq) {
		this.icq = icq;
	}

	/**
	 * Sets the mobile.
	 * @param mobile The mobile to set
	 */
	public void setMobile(Integer mobile) {
		this.mobile = mobile;
	}

	/**
	 * Sets the msn.
	 * @param msn The msn to set
	 */
	public void setMsn(String msn) {
		this.msn = msn;
	}

	/**
	 * Sets the phone.
	 * @param phone The phone to set
	 */
	public void setPhone(int phone) {
		this.phone = phone;
	}
	
	public void addChild(CompositeMap child){
	    extra_info.add(child);
	}
	
	public LinkedList getExtraInfo(){
	    return extra_info;
	}

}
