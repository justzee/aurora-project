package aurora.ide.bm;

import java.io.IOException;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;
import aurora.bm.BusinessModel;
import aurora.bm.ModelFactory;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraResourceUtil;

public class ExtendModelFactory extends ModelFactory {

	private IFile c_file;

	public ExtendModelFactory(OCManager ocm, IFile file) {
		super(ocm);
		this.c_file = file;
	}

	@Override
	protected BusinessModel getNewModelInstance(String name, String ext)
			throws IOException {
		if (name == null) {
			throw new IllegalArgumentException("model name is null");
		}
		try {
			IFile file = (IFile) BMUtil.getBMResourceFromClassPath(
					this.c_file.getProject(), name);
			//
			CompositeMap config = AuroraResourceUtil.loadFromResource(file);
			if (config == null) {
				throw new IOException("Can't load resource " + name);
			}
			BusinessModel model = createBusinessModelInternal(config);
			model.setName(name);
			return model;
		} catch (ApplicationException e) {
			e.printStackTrace();
			throw new RuntimeException("Error when parsing " + name, e);
		}
	}
}
