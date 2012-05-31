/*
 * Created on 2009-6-9
 */
package uncertain.demo.ocm;

public class SmtpHost {
    
    String  address;
    int     port = 25;
    
    public SmtpHost( String address, int port ){
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
