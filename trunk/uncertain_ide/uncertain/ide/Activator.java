package uncertain.ide;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Element;
import uncertain.schema.Schema;
import uncertain.schema.SchemaConstant;
import uncertain.schema.SchemaManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "uncertain_ide";

	// The shared instance
	private static Activator plugin;
	
	private static  SchemaManager schemaManager;
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	public static void openFileInEditor(IFile file, String id){
		IWorkbenchWindow iwb = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(iwb == null) return;
		IWorkbenchPage wp = iwb.getActivePage();
		if(wp == null) return;
		try {
			wp.openEditor(new FileEditorInput(file),id);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	public static SchemaManager getSchemaManager(){
		if(schemaManager != null)
			return schemaManager;
//		schemaManager = new SchemaManager();
		schemaManager = SchemaManager.getDefaultInstance();
		return schemaManager;
	}
	public static void main(String[] args){
//		SchemaManager sm = Activator.getSchemaManager();
//		Path path = new Path("xml/components.sxsd");
//		String filePath;
//		try {
//			filePath = FileLocator.resolve(
//					FileLocator.find(Activator.getDefault().getBundle(), path,
//							null)).getPath();
//
//		sm.loadSchemaByFile(filePath);
//		String prefix = "sc";
//		String namespace ="http://www.uncertain-framework.org/schema/simple-schema";
//		String local_name="grid";
//		QualifiedName qf = new QualifiedName(prefix,namespace,local_name);
//		Element el = sm.getElement(qf);
//		System.out.println("el.isArray():"+el.isArray());
////		System.out.println("el.isArray():"+el.());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Schema  schema = SchemaManager.getSchemaForSchema();
		Element[] eles= schema.getElements();
		for(int i=0;i<eles.length;i++){
			Element ele= eles[i];
			System.out.println(ele.getLocalName());
		}
		
		
		System.out.println(schema.getElements().length);
		QualifiedName qn = new QualifiedName(SchemaConstant.SCHEMA_NAMESPACE, SchemaConstant.NAME_ELEMENT);
		Element element = schema.getElement(qn);
		if(element != null){
			System.out.println("element..");
			List arrays = element.getAllElements();
			System.out.println("arrays.size():"+arrays.size());
			if(arrays != null){
				System.out.println("arrays..");
		        Iterator ite = arrays.iterator();
		        while(ite.hasNext()){
		        	System.out.println("hasNext..");
		        	final Element ele = (Element)ite.next();
		        	System.out.println(ele.getName());
				}
			}
		}
		
	}
}
