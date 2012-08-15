package aurora.ide.fake.uncertain.engine;

import java.io.IOException;

import uncertain.composite.CompositeLoader;
import uncertain.core.DirectoryConfig;
import uncertain.core.IContainer;
import uncertain.core.UncertainEngine;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IClassLocator;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.pkg.IPackageManager;
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

	FakeUncertainEngine() {
		init();
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

		setDefaultClassRegistry(classRegistry);
		registerBuiltinInstances();
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

}
