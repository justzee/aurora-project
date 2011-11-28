package aurora.ide.builder.processor;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.IType;
import aurora.ide.bm.BMUtil;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.helpers.ApplicationException;

public class BmProcessor extends AbstractProcessor {
	private static final Pattern dynamicPattern = Pattern
			.compile("\\$\\{[^}]+\\}");

	@Override
	public void processMap(BuildContext bc) {
		if (BuildContext.LEVEL_UNDEFINED_BM == 0)
			return;
		processAttribute(bc);
	}

	@Override
	protected void visitAttribute(Attribute a, BuildContext bc) {
		IType type = a.getAttributeType();
		if (SxsdUtil.isBMReference(type)) {
			String name = a.getName();
			String bm = bc.map.getString(name);
			bm = bm.split("\\?")[0];
			if (dynamicPattern.matcher(bm).matches()) {
				// System.out.println(bm);
				return;
			}
			IResource resource = null;
			try {
				resource = BMUtil.getBMResourceFromClassPath(
						bc.file.getProject(), bm);
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
			if (!(resource instanceof IFile)) {
				IRegion region = bc.info.getAttrValueRegion2(name);
				String msg = null;
				if (bm.length() == 0)
					msg = name + " 不能为空";
				else
					msg = name + " : " + bm + " , BM不存在";
				AuroraBuilder.addMarker(bc.file, msg,
						bc.info.getStartLine() + 1, region,
						BuildContext.LEVEL_UNDEFINED_BM,
						AuroraBuilder.UNDEFINED_BM);
			}
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {
	}
}
