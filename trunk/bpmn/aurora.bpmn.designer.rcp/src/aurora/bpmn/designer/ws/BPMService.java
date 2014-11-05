package aurora.bpmn.designer.ws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.util.Base64;
import org.apache.commons.httpclient.Header;

public class BPMService {
	private ServiceModel sm;
	private BPMNDefineModel bdm;

	public BPMService(ServiceModel sm) {
		super();
		this.sm = sm;
	}

	public ServiceModel getServiceModel() {
		return sm;
	}

	public BPMNDefineModel getBPMNDefineModel() {
		return bdm;
	}
	
	public void setBPMNDefineModel(BPMNDefineModel define){
		this.bdm = define;
	}

	

	public OMElement send(String url)
			throws AxisFault {
		ServiceClient client = new ServiceClient();
		Options options = new Options();
		options.setTo(new EndpointReference(url));// 修正为实际工程的URL
		// addAuthorization("linjinxiao", "ok", options);
		addAuthorization(sm.getUserName(), sm.getPassword(), options);

		client.setOptions(options);
		OMElement request = makeRequest(bdm);
		OMElement response = client.sendReceive(request);
		return response;
		// System.out.println("response:" + response.toString());
	}

	protected void addAuthorization(String userName, String password,
			Options options) {
		String encoded = new String(Base64.encode(new String(userName + ":"
				+ password).getBytes()));
		List list = new ArrayList();
		// Create an instance of org.apache.commons.httpclient.Header
		Header header = new Header();
		header.setName("Authorization");
		header.setValue("Basic " + encoded);
		list.add(header);
		options.setProperty(
				org.apache.axis2.transport.http.HTTPConstants.HTTP_HEADERS,
				list);
	}

	protected OMElement makeRequest(BPMNDefineModel dm) {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement request = factory.createOMElement(new QName("", "parameter"));
		if (dm == null)
			return request;
		addAttribute(request, "define_id", dm.getDefine_id(), null);
		addAttribute(request, "name", dm.getName(), null);
		addAttribute(request, "process_code", dm.getProcess_code(), null);
		addAttribute(request, "process_version", dm.getProcess_version(), null);
		addAttribute(request, "description", dm.getDescription(), null);
		addAttribute(request, "current_version_flag",
				dm.getCurrent_version_flag(), null);
		addAttribute(request, "defines", dm.getDefines(), null);
		return request;
	}

	protected void addAttribute(OMElement request, String att, String value,
			OMNamespace omns) {
		if (value != null)
			request.addAttribute(att, value, null);
	}

}
