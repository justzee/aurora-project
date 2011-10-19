package aurora.ide.bm;

import java.io.IOException;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;
import aurora.bm.BusinessModel;
import aurora.bm.ModelFactory;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.search.core.Util;

public class ExtendModelFactory extends ModelFactory {

	public ExtendModelFactory(OCManager ocm) {
		super(ocm);
	}

	@Override
	protected BusinessModel getNewModelInstance(String name, String ext) throws IOException {
		if (name == null) {
			throw new IllegalArgumentException("model name is null");
		}
		try {
			IFile file = Util.findBMFileByPKG(name);
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
