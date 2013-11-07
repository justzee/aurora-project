/**
 * Created on: 2002-11-15 16:10:36
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.lwap.application.event.BasicServiceListenerManager;
import org.lwap.application.event.IServiceListenerManager;
import org.lwap.controller.MainService;
import org.lwap.database.DatabaseAccess;
import org.lwap.database.IConnectionInitializer;
import org.lwap.database.PerformanceRecorder;
import org.lwap.database.TransactionFactory;
import org.lwap.database.datatype.DataTypeManager;
import org.lwap.feature.IFileSubstitute;
import org.lwap.feature.UploadFileHandle;
import org.lwap.mvc.DataBindingConvention;
import org.lwap.mvc.ViewFactoryStore;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.logging.DummyLogger;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.logging.ILoggingTopicRegistry;
import uncertain.ocm.IObjectRegistry;

public class WebApplication implements Application {

	public static final String LWAP_APPLICATION_LOGGING_TOPIC = "org.lwap.application";
	public static final String APPLICATION_CONFIG_PATH = "application.xml";
	public static final String DEFAULT_SERVICE_CLASS = "org.lwap.controller.MainService";

	public static final String KEY_APPLICATION_TITLE = "title";
	public static final String KEY_APPLICATION_PATH = "application-path";
	public static final String KEY_APP_PATH = "app-path";
	public static final String KEY_PATH = "path";
	public static final String KEY_RELATIVE_PATH = "relative-path";

	public static final String KEY_TEMPLATE_PATH = "template-path";

	public static final String KEY_APPLICATION_INITIALIZER = "application-initializer";
	public static final String KEY_PRE_SERVICE = "pre-service";
	public static final String KEY_CLASS = "class";

	public static final String KEY_SERVLET_CONTEXT = "servlet-context";
	public static final String KEY_DATA_SOURCE = "data-source";
	public static final String KEY_VIEW_BUILDER_STORE = "view-builder-store";
	public static final String KEY_DEBUG = "debug";

	public static final String DEFAULT_SERVICE_EXT = "service";
	public static final String KEY_EXCEPTION_HANDLE = "exception-handle";
	public static final String KEY_EXCEPTION = "exception";
	public static final String KEY_DEFAULT_HANDLE = "default-handle";

	public static final String KEY_DATE_FORMAT = "date-format";
	public static final String KEY_DATETIME_FORMAT = "datetime-format";
	public static final String KEY_TIME_FORMAT = "time-format";
	public static final String KEY_CACHE_ENABLE = "cache-enable";
	public static final String KEY_PERFORMANCE_RECORD = "performance-record";
	public static final String KEY_PERFORMANCE_DETAIL = "performance-detail";

	// path where application documents reside in
	String app_path;

	// configuration of application
	CompositeMap application_conf;

	// CompositeLoader to load configurations
	CompositeLoader composite_loader;
	// Collection extra_doc_list = null;

	// A list of ServiceParticipant
	// Kept for compatibility
	Collection pre_service_list = new LinkedList();

	// ServletContext object
	ServletContext servlet_context;

	// DataSource
	DataSource data_source;

	// TransactionFactory to execute prepared sql configuration
	TransactionFactory transaction_factory;

	// ViewBuilderStore
	ViewFactoryStore viewbuilder_store;

	// ResourceBundleFactory
	ResourceBundleFactory resource_bundle_factory;

	// list of ApplicationInitializer
	Collection app_initializer_list = new LinkedList();

	// object pool
	HashMap object_pool = new HashMap();

	// debug flag
	boolean debug = false;

	// exception handle
	HashMap exception_handle_map = new HashMap();
	// HashMap exception_handle_config = new HashMap();
	ExceptionHandle default_exception_handle = null;

	// logger
	ILogger logger;

	// sql performance recorder
	PerformanceRecorder performance_recorder = null;
	boolean record_performance = false;

	// The UncertainEngine instance
	UncertainEngine uncertainEngine;

	// service event listeners
	IServiceListenerManager serviceListenerManager;

	// cache config file
	boolean cacheEnable = false;

	// Connection initializer
	IConnectionInitializer connection_initializer;

	// IFileSubstitute instance;
	IFileSubstitute file_substitute;

	Service _get_service_instance(String service_name) throws Exception {
		if (service_name == null)
			return new MainService();
		else
			return (Service) Class.forName(service_name).newInstance();
	}

	Service _create_service(CompositeMap service_conf)
			throws ServiceInstantiationException {
		String service_class = service_conf.getString(KEY_CLASS);
		/*
		 * if( service_class == null){
		 * 
		 * System.out.print(service_conf.getName());
		 * System.out.println(service_conf.entrySet()); Iterator it =
		 * service_conf.getChildIterator(); if(it!=null) while(it.hasNext()) {
		 * CompositeMap item = (CompositeMap)it.next();
		 * System.out.println("  "+item.getName()+":"+item.entrySet());
		 * 
		 * } //System.out.println(service_conf.toXML()); throw new
		 * ServiceInstantiationException
		 * ("No class specified in service config"); }
		 */
		/*
		 * if(service_class==null) service_class = DEFAULT_SERVICE_CLASS;
		 */
		try {
			Service service_instance = _get_service_instance(service_class);
			service_instance.setApplication(this);
			service_instance.init(service_conf);
			return service_instance;
		} catch (Throwable thr) {
			throw new ServiceInstantiationException(thr);
		}

	}

	/*
	 * void _merge_base_file( CompositeMap service_conf){ String _base_file =
	 * service_conf.getString("_base_file"); if(_base_file!=null){ CompositeMap
	 * base_config = } }
	 */

	Service _create_service(String service_name)
			throws ServiceInstantiationException {
		// load service config, create new service instance specified
		// by 'class' attribute of service config
		CompositeMap service_conf = null;
		String realPath=null;
		if (file_substitute != null)
			service_name = file_substitute.getRealFile(service_name);
		try {

			if (service_name.indexOf(File.separatorChar) < 0) {
				service_conf = getCompositeLoader().load(service_name);
			} else {
				// logger.info("requesting "+service_name);
				realPath=servlet_context.getRealPath(service_name);
				service_conf = getCompositeLoader().loadByFullFilePath(realPath);
			}

			// service_conf = this.getCompositeLoader().load(service_name);
		} catch (IOException ex) {
			throw new ServiceInstantiationException(
					"IO Error when loading service config", ex);
		} catch (org.xml.sax.SAXException sex) {
			throw new ServiceInstantiationException(
					"XML Syntax error in service config", sex);
		}

		if (service_conf == null)
			throw new ServiceInstantiationException(
					"Can't load service config:" + service_name);
		Service service_instance = _create_service(service_conf);
		if(service_name.lastIndexOf('/')>-1){
			String directory = service_name.substring(0,
					service_name.lastIndexOf('/')); 		
	 		String directoryPath = new File(getCompositeLoader().getBaseDir(),
	 				directory).getPath();
			if (!"".equals(directoryPath)) {			
				Iterator iterator =this.getCompositeLoader().getExtraLoader().iterator();
				while (iterator.hasNext()) {
					CompositeLoader loader=(CompositeLoader)iterator.next();
					File relativePath=new File(loader.getBaseDir());
					if(directoryPath.equals(relativePath.getPath())){
						service_name = service_name.substring(service_name
								.lastIndexOf('/'));
						break;
					}
				}			
			} 
//			service_name = service_name.substring(service_name.indexOf('/') + 1);
		}
		service_instance.setServiceName(service_name);
		return service_instance;
	}

	void init_participant_list(CompositeMap svc_list, Collection list)
			throws Exception {
		if (svc_list == null)
			return;
		Iterator it = svc_list.getChildIterator();
		if (it == null)
			return;
		while (it.hasNext()) {
			CompositeMap svc_def = (CompositeMap) it.next();
			ServiceParticipant svc_instance = (ServiceParticipant) Class
					.forName(svc_def.getString(KEY_CLASS)).newInstance();
			// svc_instance.setApplicationConfig(this.getApplicationConfig());
			svc_instance.init(svc_def);
			list.add(svc_instance);
		}
	}

	CompositeMap CreateInitContext() {
		CompositeMap map = new CompositeMap(null, null, "context");
		return map;
	}

	/*
	 * ------------------ BEGIN get/set application scope objects
	 * -----------------
	 */

	public void setCompositeLoader(CompositeLoader loader) {
		composite_loader = loader;
	}

	public CompositeLoader getCompositeLoader() {
		return composite_loader;
	}

	public CompositeMap getApplicationConfig() {
		return application_conf;
	}

	public void setServletContext(ServletContext context) {
		this.application_conf.put(KEY_SERVLET_CONTEXT, context);
		this.servlet_context = context;
	}

	public ServletContext getServletContext() {
		return this.servlet_context;
	}

	public void setDataSource(DataSource data_source) {
		this.application_conf.put(KEY_DATA_SOURCE, data_source);
		this.data_source = data_source;
		transaction_factory = new TransactionFactory(data_source,
				composite_loader);
	}

	public DataSource getDataSource() {
		return data_source;
	}

	public TransactionFactory getTransactionFactory() {
		return transaction_factory;
	}

	public void setViewBuilderStore(ViewFactoryStore viewbuilder_store) {
		this.viewbuilder_store = viewbuilder_store;
	}

	public ViewFactoryStore getViewBuilderStore() {
		return viewbuilder_store;
	}

	public void setResourceBundleFactory(
			ResourceBundleFactory resource_bundle_factory) {
		if (resource_bundle_factory == null)
			throw new NullPointerException();
		logger.info("ResourceBundleFactory set to " + resource_bundle_factory);
		if (this.resource_bundle_factory != null)
			logger.info("Old factory:" + this.resource_bundle_factory);
		this.resource_bundle_factory = resource_bundle_factory;
	}

	public ResourceBundleFactory getResourceBundleFactory() {
		return resource_bundle_factory;
	}

	/*
	 * ------------------ END get/set application scope objects
	 * -----------------
	 */

	public String getLocalizedString(Locale locale, String input) {
		if (getResourceBundleFactory() == null || input == null)
			return input;
		ResourceBundle bundle = getResourceBundleFactory().getResourceBundle(
				locale);
		return bundle == null ? input : bundle.getString(input);
	}

	public Service getService(CompositeMap service_config)
			throws ServiceInstantiationException {
		Service service_instance = _create_service(service_config);
		return service_instance;
	}

	public Service getService(String service_name, CompositeMap context)
			throws ServiceInstantiationException {
		Service service_instance = _create_service(service_name);
		service_instance.setServiceContext(context);
		return service_instance;
	}

	public Service getService(String service_name)
			throws ServiceInstantiationException {

		Service service_instance = _create_service(service_name);

		// create initial context
		CompositeMap context = CreateInitContext();
		/*
		 * context.addChild(this.getApplicationConfig());
		 * context.addChild(service_instance.getServiceConfig());
		 */
		service_instance.setServiceContext(context);
		return service_instance;
	}

	/**
	 * return a pooled instance of specified class
	 * 
	 * @return object if instance is successfuly created else return null
	 */
	public Object getPooledObject(String cls_name) {
		Object obj = object_pool.get(cls_name);
		if (obj != null)
			return obj;
		try {
			obj = Class.forName(cls_name).newInstance();
			object_pool.put(cls_name, obj);
			return obj;
		} catch (Throwable thr) {
			logger.log(Level.SEVERE, "Can't get pooled object", thr);
			return null;
		}
	}

	public ILogger getLogger() {
		return this.logger;
	}

	/**
	 * called by FacadeService to perform per-service works such as session
	 * check
	 */
	public int initService(HttpServletRequest request,
			HttpServletResponse response, Service service) throws IOException,
			ServletException {
		int ret = 0;
		Iterator it = pre_service_list.iterator();
		while (it.hasNext()) {
			ServiceParticipant spt = (ServiceParticipant) it.next();
			ret = spt.service(request, response, service);
			if (ret == ServiceParticipant.BREAK_PRE_SERVICE_LIST)
				break;
			else if (ret == ServiceParticipant.BREAK_WHOLE_SERVICE)
				return ret;
		}
		return ret;
	}

	public String getServiceURL(String service_name) {
		if (service_name.indexOf('.') < 0) {
			service_name = service_name + '.' + DEFAULT_SERVICE_EXT;
		}
		return service_name;
	}

	public ServiceDispatch createUrlDispatch(BaseService origin_service,
			String url, int dispatch_style) {
		return new ServiceDispatch(origin_service,
				ServiceDispatch.TARGET_TYPE_URL, url, dispatch_style);
	}

	public ServiceDispatch createServiceDispatch(BaseService origin_service,
			String service_name, int dispatch_style) {
		return new ServiceDispatch(origin_service,
				ServiceDispatch.TARGET_TYPE_SERVICE, service_name,
				dispatch_style);
	}

	public ServiceDispatch createDispatch(BaseService service, int target_type,
			String target_name, int dispatch_style) {
		return new ServiceDispatch(service, target_type, target_name,
				dispatch_style);
	}

	public void loadAppPath(CompositeMap config) {
		CompositeMap cfg = config.getChild(KEY_APP_PATH);
		if (cfg != null) {
			Iterator it = cfg.getChildIterator();
			if (it != null) {
				// extra_doc_list = new LinkedList();
				while (it.hasNext()) {
					CompositeMap item = (CompositeMap) it.next();
					String path = item.getString(KEY_RELATIVE_PATH);
					File pathFile = null;
					if (path != null) {
						pathFile = new File(getCompositeLoader().getBaseDir(),
								path);
					} else {
						path = item.getString(KEY_PATH);
						pathFile = new File(path);
					}
					if (pathFile.exists()) {
						CompositeLoader loader = new CompositeLoader(
								pathFile.getPath(), DEFAULT_SERVICE_EXT);
						loader.setSupportXInclude(true);
						this.getCompositeLoader().addExtraLoader(loader);
					} else
						logger.warning("Can't find application path " + path);
				}
			}
		}
	}

	void loadFormat(CompositeMap config) {
		if (config.containsKey(KEY_DATE_FORMAT)) {
			DataTypeManager.dateFormat = new SimpleDateFormat(
					config.getString(KEY_DATE_FORMAT));
		}

		if (config.containsKey(KEY_DATETIME_FORMAT)) {
			DataTypeManager.datetimeFormat = new SimpleDateFormat(
					config.getString(KEY_DATETIME_FORMAT));
		}

		if (config.containsKey(KEY_TIME_FORMAT)) {
			DataTypeManager.time_format = new SimpleDateFormat(
					config.getString(KEY_TIME_FORMAT));
		}
	}

	/** init application */
	public void init(CompositeMap config) throws ApplicationInitializeException {
		loadAppPath(config);
		loadFormat(config);
		debug = config.getBoolean(KEY_DEBUG, false);
		cacheEnable = config.getBoolean(KEY_CACHE_ENABLE, false);
		serviceListenerManager = new BasicServiceListenerManager();

		DataBindingConvention.setPrintDebugInfo(debug);

		CompositeMap app_init = config.getChild(KEY_APPLICATION_INITIALIZER);
		if (app_init == null)
			return;
		Iterator it = app_init.getChildIterator();
		if (it == null)
			return;
		while (it.hasNext()) {
			CompositeMap init = (CompositeMap) it.next();
			String cls = init.getString(KEY_CLASS);
			if (cls != null)
				try {
					ApplicationInitializer a = (ApplicationInitializer) Class
							.forName(cls).newInstance();
					a.initApplication(this, config);
					this.app_initializer_list.add(a);
					logger.info("initialized " + cls);
				} catch (ApplicationInitializeException aex) {
					throw aex;
				} catch (Exception ex) {
					throw new ApplicationInitializeException(ex);
				}
		}

		getCompositeLoader().setCacheEnabled(cacheEnable);

		setRecordRerformance(config.getBoolean(KEY_PERFORMANCE_RECORD, false));
		if (isRecordRerformance()) {
			performance_recorder.setRecordDetail(config.getBoolean(
					KEY_PERFORMANCE_DETAIL, false));
		}

	}

	/** load exception handle */
	void load_exception_handle(CompositeMap config) {
		CompositeMap exp_config = config.getChild(KEY_EXCEPTION_HANDLE);
		if (exp_config == null)
			return;
		String dft_handle = exp_config.getString(KEY_DEFAULT_HANDLE);
		if (dft_handle != null)
			default_exception_handle = (ExceptionHandle) getPooledObject(dft_handle);

		Iterator it = exp_config.getChildIterator();
		if (it == null)
			return;
		while (it.hasNext()) {
			CompositeMap item = (CompositeMap) it.next();
			String exception = item.getString(KEY_EXCEPTION);
			String cls = item.getString(KEY_CLASS);
			if (exception != null && cls != null) {
				ExceptionHandle handle = (ExceptionHandle) this
						.getPooledObject(cls);
				if (handle != null) {
					item.put(KEY_CLASS, handle);
					exception_handle_map.put(exception, handle);
					// exception_handle_config.put(exception, item);
				} else
					System.err.println(this.getClass().getName()
							+ ": can't load exception handle " + cls);
			}
		}
	}

	public void shutdown() {
		Iterator it = this.app_initializer_list.iterator();
		while (it.hasNext()) {
			ApplicationInitializer a = (ApplicationInitializer) it.next();
			a.cleanUp(this);
		}
		uncertainEngine.shutdown();
	}

	public WebApplication(String doc_path, ServletContext context)
			throws ApplicationInitializeException {
		this(doc_path, APPLICATION_CONFIG_PATH, context);
	}

	public WebApplication(String doc_path, String config_file_name,
			ServletContext context) throws ApplicationInitializeException {

		this.app_path = doc_path;
		this.servlet_context = context;
		if (config_file_name == null)
			config_file_name = APPLICATION_CONFIG_PATH;

		try {
			// load application config
			composite_loader = new CompositeLoader(doc_path,
					DEFAULT_SERVICE_EXT);
			composite_loader.setSupportXInclude(true);
			composite_loader.setSupportFileMerge(true);
			this.application_conf = composite_loader.load(config_file_name);

		} catch (IOException ioex) {
			throw new ApplicationInitializeException(
					"IOException while loading config file:"
							+ ioex.getMessage());
		} catch (org.xml.sax.SAXException sex) {
			throw new ApplicationInitializeException(
					"Config file syntax error:" + sex.getMessage());
		}

		application_conf.put(KEY_APPLICATION_PATH, doc_path);

		String title = application_conf.getString(KEY_APPLICATION_TITLE,
				doc_path);
		// logger = Logger.getLogger(title);
		logger = DummyLogger.getInstance();

		// init application
		init(application_conf);

		// init participant list
		try {
			init_participant_list(
					this.application_conf.getChild(KEY_PRE_SERVICE),
					this.pre_service_list);
		} catch (Exception thr) {
			thr.printStackTrace();
			logger.severe(thr.getMessage());
			throw new ApplicationInitializeException(thr.getCause());
		}

		// load exception handle
		load_exception_handle(application_conf);

		// Runtime.getRuntime().addShutdownHook(
		// new Thread(){
		// public void run(){
		// shutdown();
		// }
		// }
		// );
	}

	public CompositeMap getExceptionHandleConfig(Throwable exception) {

		String cls_name = exception.getClass().getName();
		CompositeMap config = (CompositeMap) exception_handle_map.get(cls_name);
		return config;
	}

	public ExceptionHandle getExceptionHandle(CompositeMap handle_config) {
		if (handle_config == null)
			return null;
		ExceptionHandle handle = (ExceptionHandle) handle_config.get(KEY_CLASS);
		if (handle == null)
			handle = default_exception_handle;
		return handle;
	}

	public void handleException(Service svc, Throwable thr) {
		if (thr == null)
			throw new NullPointerException();
		Throwable cause = thr.getCause();
		if (cause != null)
			thr = cause;

		ExceptionHandle handle = null;
		CompositeMap config = getExceptionHandleConfig(thr);
		if (config == null)
			handle = default_exception_handle;
		else
			handle = getExceptionHandle(config);

		if (handle != null) {
			handle.handleException(thr, this, svc, config);
		} else {
			logger.log(Level.WARNING, "Exception when executing service", thr);
		}

	}

	/**
	 * @return Returns the uncertainEngine.
	 */
	public UncertainEngine getUncertainEngine() {
		return uncertainEngine;
	}

	/**
	 * @param uncertainEngine
	 *            The uncertainEngine to set.
	 */
	public void setUncertainEngine(UncertainEngine uncertainEngine) {
		this.uncertainEngine = uncertainEngine;
		IObjectRegistry space = uncertainEngine.getObjectRegistry();
		space.registerInstance(WebApplication.class, this);
		space.registerInstance(IServiceListenerManager.class,
				serviceListenerManager);
		transaction_factory.setUncertainEngine(uncertainEngine);
		if (performance_recorder != null)
			space.registerInstance(PerformanceRecorder.class,
					performance_recorder);
		ILoggingTopicRegistry topics = uncertainEngine
				.getLoggingTopicRegistry();
		topics.registerLoggingTopic(LWAP_APPLICATION_LOGGING_TOPIC);
		topics.registerLoggingTopic(DatabaseAccess.LOGGING_TOPIC);
		topics.registerLoggingTopic(UploadFileHandle.LOGGING_TOPIC);
		// init logger
		ILoggerProvider provider = (ILoggerProvider) space
				.getInstanceOfType(ILoggerProvider.class);
		if (provider != null) {
			logger = provider.getLogger(LWAP_APPLICATION_LOGGING_TOPIC);
			logger.info("Logging service started");
		}

		ResourceBundleFactory fact = (ResourceBundleFactory) space
				.getInstanceOfType(ResourceBundleFactory.class);
		if (fact != null)
			setResourceBundleFactory(fact);

		file_substitute = (IFileSubstitute) space
				.getInstanceOfType(IFileSubstitute.class);
	}

	public IServiceListenerManager getServiceListenerManager() {
		return this.serviceListenerManager;
	}

	public void setServiceListenerManager(IServiceListenerManager manager) {
		serviceListenerManager = manager;
	}

	/**
	 * @return Returns the cacheEnable.
	 */
	public boolean isCacheEnable() {
		return cacheEnable;
	}

	/**
	 * @param cacheEnable
	 *            The cacheEnable to set.
	 */
	public void setCacheEnable(boolean cacheEnable) {
		this.cacheEnable = cacheEnable;
		this.composite_loader.setCacheEnabled(cacheEnable);
	}

	/**
	 * @return the performance_recorder
	 */
	public PerformanceRecorder getPerformanceRecorder() {
		return performance_recorder;
	}

	/**
	 * @return the record_performance
	 */
	public boolean isRecordRerformance() {
		return record_performance;
	}

	/**
	 * @param record_performance
	 *            the record_performance to set
	 */
	public void setRecordRerformance(boolean record_performance) {
		this.record_performance = record_performance;
		if (!record_performance) {
			if (performance_recorder != null)
				performance_recorder.clear();
			performance_recorder = null;
		} else {
			performance_recorder = new PerformanceRecorder();
		}
	}

	public IConnectionInitializer getConnectionInitializer() {
		return connection_initializer;
	}

	public void setConnectionInitializer(
			IConnectionInitializer connection_initializer) {
		this.connection_initializer = connection_initializer;
	}

}
