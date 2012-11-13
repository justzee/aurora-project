package aurora.ide.fake.uncertain.engine;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.DirectoryConfig;
import uncertain.core.ILifeCycle;
import uncertain.event.Configuration;
import uncertain.event.IContextListener;
import uncertain.event.IEventDispatcher;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IClassLocator;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.pkg.IInstanceCreationListener;
import uncertain.pkg.IPackageManager;
import uncertain.pkg.PackagePath;
import uncertain.proc.ParticipantRegistry;
import uncertain.schema.ISchemaManager;
import uncertain.schema.SchemaManager;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DialogUtil;

public class FakeUncertainEngine {
	private SchemaManager mSchemaManager;
	private CompositeLoader compositeLoader;
	private OCManager oc_manager;
	private ClassRegistry classRegistry;
	private InternalPackageManager internalPackageManager;
	private ObjectRegistryImpl objectRegistry;
	private DirectoryConfig directoryConfig;
	private ParticipantRegistry mParticipantRegistry;
//	private PackageManager mPackageManager;
	private boolean mIsRunning;
	private Set<String> mLoadedFiles = new HashSet<String>();
	private List<ILifeCycle> mLoadedLifeCycleList = new LinkedList<ILifeCycle>();
	@SuppressWarnings("rawtypes")
	private Set mContextListenerSet = new HashSet();
	private Configuration mConfig;
	private File mConfigDir;

	public FakeUncertainEngine() {
		init();
		loadPackageManager();
	}

	
	public FakeUncertainEngine(String base_dir,String config_dir) {
		init();
		this.directoryConfig.setBaseDirectory(base_dir);
		this.directoryConfig.setConfigDirectory(config_dir);
		loadPackageManager();
	}
	private InternalPackageManager loadPackageManager() {

		internalPackageManager = new InternalPackageManager(compositeLoader,
				oc_manager, mSchemaManager);
		try {
			internalPackageManager.loadPackgeDirectory(AuroraResourceUtil
					.getClassPathFile("uncertain_builtin_package")
					.getCanonicalPath());
			internalPackageManager.loadPackgeDirectory(AuroraResourceUtil
					.getClassPathFile("aurora_builtin_package")
					.getCanonicalPath());

		} catch (IOException e) {
			e.printStackTrace();
			DialogUtil.logErrorException(e);
		}
		return internalPackageManager;
	}

	private void init() {
		mSchemaManager = new SchemaManager();
		mSchemaManager.addSchema(SchemaManager.getSchemaForSchema());

		compositeLoader = CompositeLoader.createInstanceForOCM();
		objectRegistry = new ObjectRegistryImpl();
		oc_manager = new OCManager(objectRegistry);
		classRegistry = oc_manager.getClassRegistry();
		directoryConfig = DirectoryConfig.createDirectoryConfig();

		mParticipantRegistry = new ParticipantRegistry();

		setDefaultClassRegistry(classRegistry);
		registerBuiltinInstances();
	}

	public Configuration createConfig(CompositeMap cfg) {
		Configuration conf = new Configuration(mParticipantRegistry, oc_manager);
		conf.loadConfig(cfg);
		return conf;
	}

	private void loadInstanceFromPackage() {
		internalPackageManager.createInstances(objectRegistry,
				new IInstanceCreationListener() {

					public void onInstanceCreate(Object instance,
							File config_file) {
						if (!loadInstance(instance)) {
							throw BuiltinExceptionFactory
									.createInstanceStartError(instance,
											config_file.getAbsolutePath(), null);
						}
						mLoadedFiles.add(config_file.getAbsolutePath());

					}
				}, true);

	}

	private boolean loadInstance(Object inst) {
		mConfig.addParticipant(inst);
		if (inst instanceof IContextListener)
			addContextListener((IContextListener) inst);
		if (inst instanceof ILifeCycle) {
			ILifeCycle c = (ILifeCycle) inst;
			if (c.startup()) {
				mLoadedLifeCycleList.add(c);
				return true;
			} else
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public void addContextListener(IContextListener listener) {
		mContextListenerSet.add(listener);
	}

	public void addPackages(PackagePath[] paths) throws IOException {
		for (int i = 0; i < paths.length; i++)
			internalPackageManager.loadPackage(paths[i]);
	}

	private void registerBuiltinInstances() {
		// objectRegistry.registerInstanceOnce(IContainer.class, this);
		// objectRegistry.registerInstanceOnce(UncertainEngine.class, this);
		objectRegistry.registerInstance(CompositeLoader.class, compositeLoader);
		objectRegistry.registerInstance(IClassLocator.class, classRegistry);
		objectRegistry.registerInstance(ClassRegistry.class, classRegistry);
		objectRegistry.registerInstance(OCManager.class, oc_manager);
		objectRegistry.registerInstance(DirectoryConfig.class, directoryConfig);

		objectRegistry.registerInstanceOnce(IObjectRegistry.class,
				objectRegistry);
		objectRegistry.registerInstanceOnce(IObjectCreator.class,
				objectRegistry);
		objectRegistry.registerInstance(IPackageManager.class,
				internalPackageManager);

		objectRegistry.registerInstanceOnce(ISchemaManager.class,
				mSchemaManager);

		objectRegistry.registerInstance(ParticipantRegistry.class,
				mParticipantRegistry);
//		objectRegistry.registerInstance(IPackageManager.class, mPackageManager);
	}

	private void setDefaultClassRegistry(ClassRegistry mClassRegistry) {

		mClassRegistry.registerPackage("uncertain.proc");
		mClassRegistry.registerPackage("uncertain.ocm");
		mClassRegistry.registerPackage("uncertain.logging");
		mClassRegistry.registerPackage("uncertain.core");
		mClassRegistry.registerPackage("uncertain.core.admin");
		mClassRegistry.registerPackage("uncertain.event");
		mClassRegistry.registerPackage("uncertain.pkg");
		mClassRegistry.registerPackage("uncertain.cache");
		mClassRegistry.registerPackage("uncertain.cache.action");
		mClassRegistry.registerClass("class-registry", "uncertain.ocm",
				"ClassRegistry");
		mClassRegistry.registerClass("package-mapping", "uncertain.ocm",
				"PackageMapping");
		mClassRegistry.registerClass("class-mapping", "uncertain.ocm",
				"ClassMapping");
		mClassRegistry.registerClass("feature-attach", "uncertain.ocm",
				"FeatureAttach");
		mClassRegistry.registerClass("package-path", "uncertain.pkg",
				"PackagePath");

		// loadInternalRegistry(LoggingConfig.LOGGING_REGISTRY_PATH);
	}

	public SchemaManager getmSchemaManager() {
		return mSchemaManager;
	}

	public CompositeLoader getCompositeLoader() {
		return compositeLoader;
	}

	public OCManager getOc_manager() {
		return oc_manager;
	}

	public ClassRegistry getClassRegistry() {
		return classRegistry;
	}

	public InternalPackageManager getInternalPackageManager() {
		return internalPackageManager;
	}

	public ObjectRegistryImpl getObjectRegistry() {
		return objectRegistry;
	}

	public void startup() {
		long tick = System.currentTimeMillis();

		mIsRunning = false;
		
		mConfig = createConfig();

		// mConfig.setLogger(mLogger);

		File local_config_file = new File(getConfigDirectory(),
				"uncertain.local.xml");
		CompositeMap local_config_map = null;
		if (local_config_file.exists()) {
			try {
				local_config_map = compositeLoader.loadByFile(local_config_file
						.getAbsolutePath());
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			initialize(local_config_map);
		}

		// new part, load all instances
		loadInstanceFromPackage();
		// old part
		// scanConfigFiles(DEFAULT_CONFIG_FILE_PATTERN);
		mIsRunning = true;
		tick = System.currentTimeMillis() - tick;
	}
    public void initialize(CompositeMap config) {
        // populate self from config
        if (config != null) {
            oc_manager.populateObject(config, this);
            CompositeMap child = config
                    .getChild(DirectoryConfig.KEY_PATH_CONFIG);
            if (child != null) {
                if(directoryConfig==null)
                	directoryConfig = DirectoryConfig.createDirectoryConfig(child);
                else
                	directoryConfig.getObjectContext().putAll(child);
            }
            directoryConfig.checkValidation();
        }
    }

	public File getConfigDirectory() {
		if (mConfigDir == null) {
			String dir = directoryConfig.getConfigDirectory();
			if (dir != null)
				mConfigDir = new File(dir);
		}
		return mConfigDir;
	}

	public void shutdown() {
		mIsRunning = false;
	}

	public boolean isRunning() {
		return mIsRunning;
	}

	public Configuration createConfig() {
		Configuration conf = new Configuration(mParticipantRegistry, oc_manager);
		return conf;
	}

	public IEventDispatcher getEventDispatcher() {
		return mConfig;
	}

}
