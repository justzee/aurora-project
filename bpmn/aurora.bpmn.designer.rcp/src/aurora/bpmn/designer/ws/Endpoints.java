package aurora.bpmn.designer.ws;

public class Endpoints {
	public static final String HOST = "http://localhost:8888/HAP_DBI/";

	public static final String SAVE_SERVICE = HOST
			+ "modules/bpm/ws/save_bpm_define.svc";
	public static final String LIST_SERVICE = HOST
			+ "modules/bpm/ws/query_bpm_define_list.svc";
	public static final String FETCH_SERVICE = HOST
			+ "modules/bpm/ws/fetch_bpm_define.svc";
	public static final String DEL_SERVICE = HOST
			+ "modules/bpm/ws/del_bpm_define.svc";

	public static String getListService() {
		return LIST_SERVICE;
	}

	public static String getSaveService() {
		return SAVE_SERVICE;
	}

	public static String getFetchService() {
		return FETCH_SERVICE;
	}

	public static String getDeleteService() {
		return DEL_SERVICE;
	}

}
