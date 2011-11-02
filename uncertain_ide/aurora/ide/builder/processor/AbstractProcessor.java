package aurora.ide.builder.processor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.helpers.LoadSchemaManager;

public abstract class AbstractProcessor {
	public abstract void processMap(IFile file, CompositeMap map, IDocument doc);

	/**
	 * 调用此方法应重写visitAttribute方法,以完成相应操作
	 * 
	 * @param file
	 * @param map
	 * @param doc
	 */
	public final void processAttribute(IFile file, CompositeMap map,
			IDocument doc) {
		List<Attribute> list;
		try {
			list = getAttributesInSchemaNotNull(map);
		} catch (Exception e) {
			e.printStackTrace();
			AuroraBuilder.addMarker(file, e.getMessage(), map
					.getLocationNotNull().getStartLine() - 1,
					IMarker.SEVERITY_ERROR, AuroraBuilder.FATAL_ERROR);
			return;
		}
		for (Attribute a : list) {
			if (map.get(a.getName()) != null)
				visitAttribute(a, file, map, doc);
		}
	}

	public List<Attribute> getAttributesInSchemaNotNull(CompositeMap map)
			throws Exception {
		Element ele = LoadSchemaManager.getSchemaManager().getElement(map);
		if (ele == null)
			return new ArrayList<Attribute>();
		List<Attribute> list = ele.getAllAttributes();
		if (list == null)
			return new ArrayList<Attribute>();
		return list;
	}

	/**
	 * 当所有结点遍历完成后调用,以便统一处理保存的数据<br/>
	 * (不是当前结点遍历完成后调用)
	 * 
	 * @param file
	 * @param map
	 * @param doc
	 */
	public abstract void processComplete(IFile file, CompositeMap map,
			IDocument doc);

	/**
	 * 遍历当前map的所有在Schema中定义过且在当前map中出现过的属性
	 * 
	 * @param a
	 * @param file
	 * @param map
	 * @param doc
	 */
	protected void visitAttribute(Attribute a, IFile file, CompositeMap map,
			IDocument doc) {
	}

}
