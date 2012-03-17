/**
 * Created on: 2004-6-15 17:18:08
 * Author:     zhoufan
 */
package uncertain.testcase.object;

/**
 * 
 */
public class Person {

	Name			name;
	ContactInfo		contact_info;

	public Person[]		Workmates;
	
	public String			BORNPLACE;
	public String			Homeaddress;
	public boolean		Assigned = false;
	
	public Person(){
	    super();
	}
	
	public Person(Name name){
	    this.name = name;
	}
	
	public Person(ContactInfo info, boolean assigned){
	    this.contact_info = info;
	    this.Assigned = assigned;
	}
	
	public void addName(Name name){
		this.name = name;
	}
	
	public Name getName(){
		return name;
	}
	
	public void addContactInfo(ContactInfo info){
		this.contact_info = info;
	}
	
	public ContactInfo getContactInfo(){
		return contact_info;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append("[name="+name+", contact_info="+contact_info+", Bornplace="+BORNPLACE+", Homeaddress="+Homeaddress);

		if( Workmates != null){
			buf.append("workmates:{");
			for(int i=0; i<Workmates.length; i++){
				buf.append(Workmates[i]).append(";");
			}
			buf.append("}");
		}
		
		buf.append(']');
		return buf.toString();		
	}

}
