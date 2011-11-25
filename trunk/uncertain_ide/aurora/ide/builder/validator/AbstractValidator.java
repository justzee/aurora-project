package aurora.ide.builder.validator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.processor.AbstractProcessor;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.CompositeMapInDocument;
import aurora.ide.search.core.CompositeMapInDocumentManager;
import aurora.ide.search.core.Util;

public abstract class AbstractValidator implements IterationHandle {
	protected IFile file;
	protected CompositeMap map;
	protected IDocument doc;
	private AbstractProcessor[] aps = null;

	public AbstractValidator(IFile file) {
		super();
		this.file = file;
		try {
			map = CacheManager.getCompositeMapCacher().getCompositeMap(file);
			doc = CacheManager.getDocumentCacher().getDocument(file);
		} catch (Exception e) {
			AuroraBuilder.addMarker(file, e.getMessage(), 1,
					IMarker.SEVERITY_ERROR, AuroraBuilder.FATAL_ERROR);
			e.printStackTrace();
		}
	}

	public AbstractValidator() {
	}

	public final void validate() {
		if (map == null)
			return;
		aps = getMapProcessor();
		map.iterate(this, true);
		for (AbstractProcessor np : aps) {
			np.processComplete(file, map, doc);
		}
	}

	public int process(CompositeMap map) {
		CompositeMapInDocument mapdoc = CompositeMapInDocumentManager
				.getCompositeMapInDocument(map, doc);
		for (AbstractProcessor np : aps) {
			np.processMap(file, map, doc);
		}
		return 0;
	}

	/**
	 * 为了能使AbstractProcessor保存自己的数据,应确保此方法没次调用返回相同的值
	 * 
	 * @return
	 */
	public abstract AbstractProcessor[] getMapProcessor();

	public static IRegion getAttributeRegion(IDocument doc, int line,
			String attrName) {
		try {
			int offset = doc.getLineOffset(line);
			int length = doc.getLineLength(line);
			return Util.getAttributeRegion(offset, length, attrName, doc);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 在doc的lineno行中查找属性名为name,值为value的属性
	 * 
	 * @param doc
	 * @param lineno
	 *            行号,从0开始
	 * @param name
	 *            属性名
	 * @param value
	 *            属性值 (不包含引号)
	 * @return value的IRegion
	 */
	public static IRegion getValueRegion(IDocument doc, int lineno,
			String name, String value) {
		value = value.replace("<", "&lt;");
		value = value.replace(">", "&gt;");
		value = value.replace("&", "&amp;");
		value = value.replace("'", "&apos;");

		try {
			int offset = doc.getLineOffset(lineno);
			int length = doc.getLineLength(lineno);
			IRegion aregion = Util
					.getAttributeRegion(offset, length, name, doc);
			if (aregion == null)
				return null;
			IRegion vregion = Util.getValuePartRegion(aregion.getOffset(),
					length + offset - aregion.getOffset(), value, doc,
					IColorConstants.STRING);
			return vregion;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return null;
	}

}
