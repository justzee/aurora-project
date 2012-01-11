package aurora.ide.meta.gef.extension;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import aurora.ide.meta.gef.editors.wizard.INewTemplateWizard;

public class ExtensionManager {

	public static final String EXTENSION_ID = "aurora.ide.template.wizard";

	private List<ExtensionBean> beans = new ArrayList<ExtensionBean>();

	public List<ExtensionBean> getBeans() {
		return beans;
	}

	private static ExtensionManager instance;

	private ExtensionManager() {
		init();
	}

	public static ExtensionManager getInstance() {
		if (instance == null) {
			instance = new ExtensionManager();
		}
		return instance;
	}

	private void init() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(EXTENSION_ID);
		if (elements == null || elements.length == 0) {
			return;
		}
		for (IConfigurationElement element : elements) {
			ExtensionBean bean = new ExtensionBean();
			bean.setId(element.getAttribute("id"));
			bean.setName(element.getAttribute("name"));
			bean.setDescription(element.getAttribute("description"));
			bean.setThumbnail(element.getAttribute("thumbnail"));
			try {
				Object object = element.createExecutableExtension("class");
				if (!(object instanceof INewTemplateWizard)) {
					continue;
				}
				((INewTemplateWizard) object).addPages();
				bean.setWizard((INewTemplateWizard) object);
			} catch (CoreException e) {
				e.printStackTrace();
				continue;
			}
			if (!beans.contains(bean)) {
				beans.add(bean);
			}
		}
	}
}
