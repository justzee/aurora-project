package aurora.ide.builder.processor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;

public class NamespaceProcessor extends AbstractProcessor {

	@Override
	public void processMap(BuildContext bc) {
		if (BuildContext.LEVEL_NONOENAMESPACE == 0)
			return;
		if (bc.map.getNamespaceURI() == null) {
			String name = bc.map.getName();
			IRegion region = bc.info.getMapNameRegion();
			AuroraBuilder.addMarker(bc.file, "标签 : " + name + " 没有命名空间",
					bc.info.getStartLine() + 1, region,
					BuildContext.LEVEL_NONOENAMESPACE,
					AuroraBuilder.NONENAMESPACE);
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {
		// TODO Auto-generated method stub

	}

}
