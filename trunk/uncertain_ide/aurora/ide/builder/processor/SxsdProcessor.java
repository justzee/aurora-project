package aurora.ide.builder.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.builder.validator.AbstractValidator;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.preferencepages.BuildLevelPage;
import aurora.ide.search.core.Util;

public class SxsdProcessor extends AbstractProcessor {
	private int attrLevel;
	private int tagLevel;

	/**
	 * 检查当前结点map是否可以出现其父结点下中(如果当前结点不是根节点)
	 * 
	 * @param file
	 * @param map
	 * @param doc
	 */
	private void checkTag(IFile file, CompositeMap map, IDocument doc) {
		tagLevel = BuildLevelPage.getBuildLevel(AuroraBuilder.UNDEFINED_TAG);
		if (tagLevel == 0)
			return;
		List<Element> childs = SxsdUtil.getAvailableChildElements(map);
		List<CompositeMap> childMap = map.getChildsNotNull();
		HashMap<String, Integer> countMap = new HashMap<String, Integer>(20);
		L: for (CompositeMap m : childMap) {
			// // ignore AnyElement
			Element elem = LoadSchemaManager.getSchemaManager().getElement(m);
			if (elem == null || SxsdUtil.isExtOfAnyElement(elem))
				continue;
			// ///
			String uri = m.getNamespaceURI();
			if (uri == null || !uri.startsWith("http:"))
				continue;
			String mapName = m.getName();
			if (countMap.get(mapName) == null)
				countMap.put(mapName, 1);
			else
				countMap.put(mapName, countMap.get(mapName) + 1);
			boolean reachMax = false;
			int mc = 0;
			for (int i = 0; i < childs.size(); i++) {
				Element e = childs.get(i);
				if (mapName.equalsIgnoreCase(e.getQName().getLocalName())) {
					String maxOccurs = e.getMaxOccurs();
					if (maxOccurs != null) {
						mc = Integer.parseInt(maxOccurs);
						if (mc < countMap.get(mapName)) {
							reachMax = true;
							break;
						}
					}
					continue L;
				}
			}
			int line = m.getLocationNotNull().getStartLine();
			if (line == 0)
				line = 1;
			IRegion region = null;
			try {
				region = Util.getDocumentRegion(doc.getLineOffset(line - 1),
						doc.getLineLength(line - 1), m.getRawName(), doc,
						IColorConstants.TAG_NAME);
			} catch (BadLocationException e) {
				region = new Region(0, 0);
				e.printStackTrace();
			}
			String msg = "Tag : "
					+ mapName
					+ (reachMax ? (" , 已超出最大重复数 : " + mc) : (" , 不应该出现在 "
							+ map.getName() + " 下"));
			AuroraBuilder.addMarker(file, msg, line, region, tagLevel,
					AuroraBuilder.UNDEFINED_TAG);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void processMap(IFile file, CompositeMap map, IDocument doc) {
		String uri = map.getNamespaceURI();
		if (uri == null || !uri.startsWith("http:"))
			return;
		checkTag(file, map, doc);

		attrLevel = BuildLevelPage
				.getBuildLevel(AuroraBuilder.UNDEFINED_ATTRIBUTE);
		if (attrLevel == 0)
			return;

		// 特别处理record标签
		if (map.getName().equals("record")
				&& map.getParent().getName().equals("datas"))
			return;
		Set<String> nameSet = new HashSet<String>();
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
			nameSet.add(a.getName().toLowerCase());
		}
		for (Map.Entry entry : (Set<Map.Entry>) map.entrySet()) {
			String k = (String) entry.getKey();
			if (nameSet.contains(k.toLowerCase()))
				continue;
			int line = map.getLocationNotNull().getStartLine();
			if (line == 0)
				line = 1;
			IRegion region = AbstractValidator.getAttributeRegion(doc, map
					.getLocationNotNull().getStartLine() - 1, k);
			IMarker marker = AuroraBuilder.addMarker(file, "属性 : " + k
					+ " , 未在 [ " + map.getName() + " ] 的Schema中定义过", line,
					region, attrLevel, AuroraBuilder.UNDEFINED_ATTRIBUTE);
			if (marker != null) {
				try {
					marker.setAttribute("ATTRIBUTE_NAME", k);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void processComplete(IFile file, CompositeMap map, IDocument doc) {

	}
}
