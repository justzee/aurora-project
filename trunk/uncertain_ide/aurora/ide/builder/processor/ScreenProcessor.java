package aurora.ide.builder.processor;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.validator.AbstractValidator;
import aurora.ide.preferencepages.BuildLevelPage;
import aurora.ide.search.core.AbstractSearchService;
import aurora.ide.search.core.Util;

public class ScreenProcessor extends AbstractProcessor {
	private static final Pattern siPattern = Pattern
			.compile("/{0,1}([a-zA-Z_\\d]+/)*[a-zA-Z_\\d]+\\.screen(\\?.*){0,1}");
	private int level;

	@Override
	public void processMap(IFile file, CompositeMap map, IDocument doc) {
		level = BuildLevelPage.getBuildLevel(AuroraBuilder.UNDEFINED_SCREEN);
		if (level == 0)
			return;
		processAttribute(file, map, doc);
	}

	@Override
	public void visitAttribute(Attribute a, IFile file, CompositeMap map,
			IDocument doc) {
		if (isScreenReference(a.getAttributeType())) {
			String name = a.getName();
			String value = map.getString(name);
			int line = map.getLocationNotNull().getStartLine();
			IRegion vregion = AbstractValidator.getValueRegion(doc, line - 1,
					name, value);
			if (value.length() == 0) {
				String msg = name + " 不能为空";
				AuroraBuilder.addMarker(file, msg, line, vregion,
						IMarker.SEVERITY_ERROR, AuroraBuilder.UNDEFINED_SCREEN);
				return;
			}
			if (!siPattern.matcher(value).matches()) {
				String msg = value + " 可能不是一个有效的值";
				AuroraBuilder.addMarker(file, msg, line, vregion,
						IMarker.SEVERITY_WARNING,
						AuroraBuilder.UNDEFINED_SCREEN);
				return;
			}
			value = value.split("\\?")[0];
			IContainer webDir = Util.findWebInf(file).getParent();
			IPath path = new Path(value).makeRelativeTo(webDir.getFullPath())
					.makeAbsolute();
			// System.out.println(path);
			IFile findScreenFile = webDir.getFile(path);
			// System.out.println(findScreenFile.getLocation());
			if (findScreenFile != null && findScreenFile.exists())
				return;
			String msg = name + " : " + value + " 不存在";
			vregion = AbstractValidator.getValueRegion(doc, line - 1, name,
					value);
			AuroraBuilder.addMarker(file, msg, line, vregion, level,
					AuroraBuilder.UNDEFINED_SCREEN);
		}
	}

	private boolean isScreenReference(IType attributeType) {
		if (attributeType instanceof SimpleType) {
			return AbstractSearchService.screenReference
					.equals(((SimpleType) attributeType)
							.getReferenceTypeQName());
		}
		return false;
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {

	}

}
