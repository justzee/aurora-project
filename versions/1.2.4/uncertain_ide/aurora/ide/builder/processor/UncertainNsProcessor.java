package aurora.ide.builder.processor;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Element;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.BuildMessages;
import aurora.ide.helpers.LoadSchemaManager;

public class UncertainNsProcessor extends AbstractProcessor {
	private static Collection<?> allTypes = LoadSchemaManager
			.getSchemaManager().getAllTypes();
	private static final ArrayList<String> allTypesNames = new ArrayList<String>(
			200);
	private static String[] ignore = { "table", "label", "button", "form" };

	static {
		for (Object type : allTypes) {
			if (type instanceof Element) {
				String name = ((Element) type).getLocalName().toLowerCase();
				if (contains(name))
					continue;
				allTypesNames.add(name);
			}
		}
	}

	@Override
	public void processMap(BuildContext bc) {
		if (BuildContext.LEVEL_NONOENAMESPACE == 0)
			return;
		if (bc.map.getNamespaceURI() == null) {
			String name = bc.map.getName().toLowerCase();
			if (allTypesNames.indexOf(name) == -1)
				return;
			IRegion region = bc.info.getMapNameRegion();
			int line = bc.info.getLineOfRegion(region);
			String msg = String.format(
					BuildMessages.get("build.neednamespace"), name);
			AuroraBuilder.addMarker(bc.file, msg, line + 1, region,
					BuildContext.LEVEL_NONOENAMESPACE,
					AuroraBuilder.NONENAMESPACE);
		}
	}

	private static boolean contains(String str) {
		for (String s : ignore) {
			if (s.equalsIgnoreCase(str))
				return true;
		}
		return false;
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {

	}

}
