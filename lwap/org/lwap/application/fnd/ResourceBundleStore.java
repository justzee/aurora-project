/*
 * Created on 2009-6-1
 */
package org.lwap.application.fnd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.lwap.application.ResourceBundleFactory;
import org.lwap.application.WebApplication;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import aurora.database.FetchDescriptor;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.RawSqlService;
import aurora.database.service.SqlServiceContext;

/**
 * ResourceBundleStore.
 * 
 * @version $Id: ResourceBundleStore.java v 1.0 2009-6-1 下午01:32:39 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
public class ResourceBundleStore implements ResourceBundleFactory, IGlobalInstance {

	private IObjectRegistry mRegistry;

	private ILogger mLogger;

	private DatabaseServiceFactory mSvcFactory;

	private DataSource dataSource;

	private static final String FND_LANGUAGE_SERVICE = "sys.load_fnd_language";

	private static final String SYSTEM_PROMOT_SERVICE = "sys.load_system_prompts";

	private static final String COMPANY_SERVICE = "sys.load_comany_prompts";

	private static final String ROLE_SERVICE = "sys.load_role_prompts";

	private static final String USER_SERVICE = "sys.load_user_prompts";

	private Map resourceCache = new HashMap();

	private Map localeCache = new HashMap();

	/**
	 * @param registry
	 */
	public ResourceBundleStore(IObjectRegistry registry, DatabaseServiceFactory factory, DataSource ds) {
		mRegistry = registry;
		mSvcFactory = factory;
		dataSource = ds;
	}

	public void onInitialize() {
		mLogger = LoggingContext.getLogger(WebApplication.LWAP_APPLICATION_LOGGING_TOPIC, mRegistry);
		mLogger.info("Loading prompt");
		loadSysLanguage();
		loadResoure();
	}

	private void loadSysLanguage() {
		try {
			SqlServiceContext sqlServiceContext = SqlServiceContext.createSqlServiceContext(dataSource.getConnection());
			RawSqlService sqlService = mSvcFactory.getSqlService(FND_LANGUAGE_SERVICE);
			CompositeMap resultMap = sqlService.queryAsMap(sqlServiceContext, FetchDescriptor.getDefaultInstance());
			List list = resultMap.getChilds();
			Iterator it = list.iterator();
			while (it.hasNext()) {
				CompositeMap cm = (CompositeMap) it.next();
				String language_code = cm.getString("LANGUAGE_CODE").toUpperCase();
				String locale_code = cm.getString("LOCALE_CODE");
				if (localeCache.get(language_code) == null) {
					String language = locale_code.substring(0, locale_code.indexOf("_"));
					String country = locale_code.substring(locale_code.indexOf("_") + 1, locale_code.length());
					Locale locale = new Locale(language, country);
					localeCache.put(language_code, locale);
				}
			}
		} catch (Exception e) {
			mLogger.warning(e.getMessage());
		}
	}

	private void loadResoure() {
		try {
			SqlServiceContext sqlServiceContext = SqlServiceContext.createSqlServiceContext(dataSource.getConnection());
			RawSqlService sqlService = mSvcFactory.getSqlService(SYSTEM_PROMOT_SERVICE);
			CompositeMap resultMap = sqlService.queryAsMap(sqlServiceContext, FetchDescriptor.getDefaultInstance());
			List list = resultMap.getChilds();
			Iterator it = list.iterator();
			while (it.hasNext()) {
				CompositeMap cm = (CompositeMap) it.next();
				String locale = cm.getString("LOCALE_CODE").toUpperCase();
				String code = cm.getString("PROMPT_CODE");
				String description = cm.getString("DESCRIPTION");
				DefaultResourceBundle rb = (DefaultResourceBundle) resourceCache.get(locale);
				if (resourceCache.get(locale) == null) {
					rb = new DefaultResourceBundle();
					resourceCache.put(locale, rb);
				}
				rb.putString(code, description);
			}
		} catch (Exception e) {
			mLogger.warning(e.getMessage());
		}
	}
	
	public Locale getLocale(String language) {
		return (Locale)localeCache.get(language);
	}

	public ResourceBundle getResourceBundle(Locale locale) {
		String language = locale.getLanguage().toUpperCase();
		String country = locale.getCountry().toUpperCase();
		ResourceBundle bundle = (ResourceBundle) resourceCache.get(language.concat("_").concat(country));
		if (bundle == null)
			bundle = new DefaultResourceBundle();
		return bundle;
	}

}
