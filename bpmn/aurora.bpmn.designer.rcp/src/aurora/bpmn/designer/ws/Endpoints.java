package aurora.bpmn.designer.ws;

public class Endpoints {
	// public static final String HOST = "http://localhost:8888/HAP_DBI/";

	public static final String SAVE_SERVICE = "/modules/bpm/ws/save_bpm_define.svc";
	public static final String LIST_SERVICE = "/modules/bpm/ws/query_bpm_define_list.svc";
	public static final String FETCH_SERVICE = "/modules/bpm/ws/fetch_bpm_define.svc";
	public static final String DEL_SERVICE = "/modules/bpm/ws/del_bpm_define.svc";
	public static final String LIST_CATEGORY_SERVICE = "/modules/bpm/ws/query_bpm_category_list.svc";

	public static String getListService(String host) {
		return host + LIST_SERVICE;
	}

	public static String getSaveService(String host) {
		return host + SAVE_SERVICE;
	}

	public static String getFetchService(String host) {
		return host + FETCH_SERVICE;
	}

	public static String getDeleteService(String host) {
		return host + DEL_SERVICE;
	}

	public static String getlistBPMCategoryService(String host) {
		return host + LIST_CATEGORY_SERVICE;
	}

}
