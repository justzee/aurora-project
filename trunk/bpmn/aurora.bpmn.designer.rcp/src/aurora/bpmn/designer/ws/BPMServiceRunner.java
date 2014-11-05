package aurora.bpmn.designer.ws;

import java.io.IOException;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

public class BPMServiceRunner {
	private BPMService service;

	public BPMServiceRunner(BPMService service){
		this.service = service;
	}
	
	public BPMServiceResponse save() {
		return run(service.getServiceModel().getSaveServiceUrl());
	}

	public BPMServiceResponse run(String endpoint) {
		BPMServiceResponse response = null;
		try {
			OMElement send = service.send(endpoint);
			response = this.parseResponse(send);
		} catch (AxisFault e) {
			return new BPMServiceResponse(BPMServiceResponse.fail,
					e.getMessage());
		}
		return response;
	}

	public BPMServiceResponse fetch() {
		return run(service.getServiceModel().getFetchServiceUrl());
	}

	public BPMServiceResponse list() {
		return run(service.getServiceModel().getListServiceUrl());
	}
	
	public BPMServiceResponse delete() {
		return run(service.getServiceModel().getDeleteServiceUrl());
	}

	// <soapenv:Envelope
	// xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
	// success="true">
	// <queryResult bpmSize="1.0" serviceType="fetch">
	// <bpm process_code="888" process_version="007" define_id="1"
	// description="bpm Define" name="Test BPM"
	// current_version_flag="Y">&lt;abc/></bpm>
	// </queryResult>
	// </soapenv:Envelope>
	public BPMServiceResponse parseResponse(OMElement response) {
		BPMServiceResponse resp = new BPMServiceResponse(
				BPMServiceResponse.sucess, "OK");
		if (response != null) {
			try {
				CompositeMap map = CompositeLoader.createInstanceForOCM()
						.loadFromString(response.toString(), "UTF-8");
				CompositeMap queryResult = map.getChild("queryResult");
				if (queryResult != null) {
					resp.setBpmSize(queryResult.getInt("bpmSize", 0));
					resp.setServiceType(queryResult.getString("serviceType",
							null));
					List childsNotNull = queryResult.getChildsNotNull();
					for (Object object : childsNotNull) {
						if (object instanceof CompositeMap) {
							CompositeMap bpm = (CompositeMap) object;
							String name = bpm.getName();
							if ("bpm".equals(name)) {
								BPMNDefineModel define = new BPMNDefineModel();
								define.setDefine_id(bpm.getString("define_id",
										null));
								define.setName(bpm.getString("name", ""));
								define.setCurrent_version_flag(bpm.getString(
										"current_version_flag", "Y"));
								define.setDefine(bpm.getText());
								define.setDescription(bpm.getString(
										"description", ""));
								define.setProcess_code(bpm.getString(
										"process_code", ""));
								define.setProcess_version(bpm.getString(
										"process_version", ""));
								resp.addDefine(define);
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return new BPMServiceResponse(BPMServiceResponse.fail,
						e.getMessage());
			} catch (SAXException e) {
				e.printStackTrace();
				return new BPMServiceResponse(BPMServiceResponse.fail,
						e.getMessage());
			}
		} else {
			return new BPMServiceResponse(BPMServiceResponse.fail,
					"Response is NULL");
		}

		return resp;
	}

}
