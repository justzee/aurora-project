package aurora.ide.builder.processor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.preferencepages.BuildLevelPage;
import aurora.ide.search.core.Util;

public class NamespaceProcessor extends AbstractProcessor {
	private int level;

	@Override
	public void processMap(IFile file, CompositeMap map, IDocument doc) {
		level = BuildLevelPage.getBuildLevel(AuroraBuilder.NONENAMESPACE);
		if (level == 0)
			return;
		if (map.getNamespaceURI() == null) {
			String name = map.getName();
			int line = map.getLocation().getStartLine();
			IRegion region = null;
			try {
				int offset = doc.getLineOffset(line - 1);
				int length = doc.getLineLength(line - 1);
				region = Util.getDocumentRegion(offset, length, name, doc,
						IColorConstants.TAG_NAME);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			AuroraBuilder.addMarker(file, "标签 : " + name + " 没有命名空间", line,
					region, level, AuroraBuilder.NONENAMESPACE);
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {
		// TODO Auto-generated method stub

	}

}
