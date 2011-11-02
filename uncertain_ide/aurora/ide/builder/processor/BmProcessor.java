package aurora.ide.builder.processor;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.IType;
import aurora.ide.bm.BMUtil;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.builder.validator.AbstractValidator;
import aurora.ide.helpers.ApplicationException;

public class BmProcessor extends AbstractProcessor {
	private static final Pattern dynamicPattern = Pattern
			.compile("\\$\\{.*\\}");

	@Override
	public void processMap(IFile file, CompositeMap map, IDocument doc) {
		processAttribute(file, map, doc);
	}

	@Override
	protected void visitAttribute(Attribute a, IFile file, CompositeMap map,
			IDocument doc) {
		IType type = a.getAttributeType();
		if (SxsdUtil.isBMReference(type)) {
			String name = a.getName();
			String bm = map.getString(name);
			bm = bm.split("\\?")[0];
			if (dynamicPattern.matcher(bm).matches()) {
				// System.out.println(bm);
				return;
			}
			IResource resource = null;
			try {
				resource = BMUtil.getBMResourceFromClassPath(file.getProject(),
						bm);
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
			if (!(resource instanceof IFile)) {
				int line = map.getLocation().getStartLine();
				IRegion region = AbstractValidator.getValueRegion(doc,
						line - 1, name, bm);
				String msg = null;
				if (bm.length() == 0)
					msg = name + " 不能为空";
				else
					msg = name + " : " + bm + " , BM不存在";
				AuroraBuilder.addMarker(file, msg, line, region,
						IMarker.SEVERITY_ERROR, AuroraBuilder.UNDEFINED_BM);
			}
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {
	}
}
