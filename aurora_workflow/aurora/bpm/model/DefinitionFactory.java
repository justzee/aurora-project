package aurora.bpm.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.di.BpmnDiPackage;
import org.eclipse.bpmn2.util.Bpmn2Resource;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.dd.dc.DcPackage;
import org.eclipse.dd.di.DiPackage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.PackageMapping;
import aurora.bpm.command.sqlje.load_definition;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class DefinitionFactory {
	public static final String BPMN_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";

	private HashMap<String, Definitions> definitionCache = new HashMap<String, Definitions>();

	private OCManager ocManager;
	private CompositeLoader loader;

	private String defaultLoadType = "db";// fs
	private String baseDir = "";

	// static {
	// try {
	// Bpmn2Package bpmn2Package = Bpmn2Package.eINSTANCE;
	// Bpmn2Package.eINSTANCE.eClass();
	//
	// Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
	// "bpmn", new Bpmn2ResourceFactoryImpl());
	// EPackage.Registry.INSTANCE.put(Bpmn2Package.eNS_URI,
	// Bpmn2Package.eINSTANCE);
	// EPackage.Registry.INSTANCE.put(BpmnDiPackage.eNS_URI,
	// BpmnDiPackage.eINSTANCE);
	// EPackage.Registry.INSTANCE.put(DiPackage.eNS_URI,
	// DiPackage.eINSTANCE);
	// EPackage.Registry.INSTANCE.put(DcPackage.eNS_URI,
	// DcPackage.eINSTANCE);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public DefinitionFactory(OCManager ocm) {
		super();
		this.ocManager = ocm;
		loader = new CompositeLoader(".", "bpmn");
		loader.setCaseInsensitive(true);
		ClassRegistry reg = ocManager.getClassRegistry();
		PackageMapping pkm = new PackageMapping(BPMN_NAMESPACE, this.getClass()
				.getPackage().getName());
		reg.addPackageMapping(pkm);
	}

	public void setDefaultLoadType(String loadType) {
		this.defaultLoadType = loadType;
	}

	public String getDefaultLoadType() {
		return this.defaultLoadType;
	}

	public void setBaseDir(String dir) {
		this.baseDir = dir;
	}

	public String getBaseDir() {
		return this.baseDir;
	}

	public Definitions loadDefinition(String code, String version,
			ISqlCallStack callStack) throws Exception {
		String cacheKey = code + "-" + version;
		Definitions def = definitionCache.get(cacheKey);
		if (def != null)
			return def;
		if ("db".equals(defaultLoadType))
			def = loadDefinitionFromDb(code, version, callStack);
		else
			def = loadDefinitionFromFs(code, version);
		definitionCache.put(cacheKey, def);
		return def;
	}

	public static Definitions loadFromFile(String filepath) throws IOException {
		System.out.println(filepath);
		
		//Bpmn2Package bpmn2Package = Bpmn2Package.eINSTANCE;
		//Bpmn2Package.eINSTANCE.eClass();

		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				"bpmn", new Bpmn2ResourceFactoryImpl());
		EPackage.Registry.INSTANCE.put(Bpmn2Package.eNS_URI,
				Bpmn2Package.eINSTANCE);
		EPackage.Registry.INSTANCE.put(BpmnDiPackage.eNS_URI,
				BpmnDiPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(DiPackage.eNS_URI, DiPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(DcPackage.eNS_URI, DcPackage.eINSTANCE);

		ResourceSet resourceSet = new ResourceSetImpl();
		org.eclipse.emf.common.util.URI fileURI = URI.createFileURI(filepath);
		// create resource
		Bpmn2Resource resource = (Bpmn2Resource) resourceSet
				.createResource(fileURI);
		// load from file
		resource.load(null);
		// add resource to global ResourceSet instance
		resourceSet.getResources().add(resource);
		// get your model
		DocumentRoot eObject = (DocumentRoot) resource.getContents().get(0);
		Definitions definitions = eObject.getDefinitions();
		return definitions;
	}

	public void clearCache() {
		definitionCache.clear();
	}

	/**
	 * load from database
	 * 
	 * @param code
	 * @param version
	 * @return
	 * @throws Exception
	 */
	public Definitions loadDefinitionFromDb(String code, String version,
			ISqlCallStack callStack) throws Exception {
		load_definition xml_loader = new load_definition();
		xml_loader._$setSqlCallStack(callStack);
		String xml = xml_loader.loadFromDb(code, version);
		if (xml == null)
			throw new Exception("Definition content is null.code:" + code
					+ ",version:" + version);
		File f = File.createTempFile("_aurora_bpmn_model_tmp_", ".bpmn");
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(xml.getBytes());
		fos.close();
		return loadFromFile(f.getAbsolutePath());

		// CompositeMap map = loader.loadFromString(xml);
		// return createFromMap(map);
	}

	/**
	 * load from file system
	 * 
	 * @param code
	 * @param version
	 * @return
	 * @throws Exception
	 */
	public Definitions loadDefinitionFromFs(String code, String version)
			throws Exception {
		return null;
	}

	private Definitions createFromMap(CompositeMap map) throws Exception {
		Definitions df = (Definitions) ocManager.createObject(map);
		// df.validate();
		return df;
	}

}
