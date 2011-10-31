package aurora.ide.editor.textpage.quickfix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.composite.XMLOutputter;
import uncertain.schema.Attribute;
import aurora.ide.AuroraPlugin;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.builder.validator.AbstractValidator;
import aurora.ide.search.core.CompositeMapInDocument;
import aurora.ide.search.core.CompositeMapInDocumentManager;

public class CompletionProposalCreator {
	private static Image img_new = null;
	private static Image img_remove = null;
	private static Image img_rename = null;
	static {
		try {
			img_new = AuroraPlugin.getImageDescriptor("/icons/add.gif")
					.createImage();
			img_remove = AuroraPlugin.getImageDescriptor("/icons/delete.gif")
					.createImage();
			img_rename = AuroraPlugin.getImageDescriptor("/icons/rename.gif")
					.createImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private CompositeMap rootMap;
	private IDocument doc;
	private IMarker marker;
	private IRegion markerRegion;
	private int line = 0;
	private String word;
	private String markerType;

	public CompletionProposalCreator(IDocument doc, CompositeMap rootMap,
			IMarker marker) {
		this.rootMap = rootMap;
		this.doc = doc;
		this.marker = marker;
		int offset = marker.getAttribute(IMarker.CHAR_START, 0);
		int length = marker.getAttribute(IMarker.CHAR_END, 0) - offset;
		markerRegion = new Region(offset, length);
		try {
			line = doc.getLineOfOffset(offset);
			word = doc.get(offset, length);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		try {
			markerType = marker.getType();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public boolean isFixable() {
		if (markerType == null || word == null || rootMap == null
				|| doc == null)
			return false;
		return true;
	}

	public ICompletionProposal[] getCompletionProposal() {
		if (!isFixable())
			return null;
		if (markerType.equals(AuroraBuilder.UNDEFINED_ATTRIBUTE)) {
			return getAttributeProposal();
		} else if (markerType.equals(AuroraBuilder.UNDEFINED_DATASET)) {
			return getDataSetProposal();
		}
		return null;
	}

	private ICompletionProposal[] getAttributeProposal() {
		CompositeMap map = QuickAssistUtil.findMap(rootMap, line);
		String value = map.getString(word);
		if (value == null)// 很可能是Annotation发生错位...
			return null;
		IRegion valueRegion = AbstractValidator.getValueRegion(doc, map
				.getLocationNotNull().getStartLine() - 1, word, value);
		int deleteLength = valueRegion.getOffset() + valueRegion.getLength()
				+ 2 - markerRegion.getOffset();
		ArrayList<SortElement> comp = new ArrayList<SortElement>();
		@SuppressWarnings("unchecked")
		Set<Map.Entry<String, String>> set = map.entrySet();
		Set<String> keySet = new HashSet<String>();// map中已经出现的属性名
		for (Map.Entry<String, String> entry : set) {
			keySet.add(entry.getKey().toLowerCase());
		}
		List<Attribute> definedAttribute = SxsdUtil.getAttributesNotNull(map);// map的合法属性
		for (Attribute attr : definedAttribute) {
			String aname = attr.getName();
			if (keySet.contains(aname.toLowerCase()))// 已经被使用的属性名掠过...
				continue;
			int ed = QuickAssistUtil.getApproiateEditDistance(word, aname);
			if (ed == -1)// 无效
				continue;
			comp.add(new SortElement(aname, ed));
		}
		// 排序
		Collections.sort(comp);
		ICompletionProposal[] cps = new ICompletionProposal[comp.size() + 1];
		for (int i = 0; i < comp.size(); i++) {
			String str = comp.get(i).name;
			cps[i] = new CompletionProposal(str, markerRegion.getOffset(),
					markerRegion.getLength(), str.length(), img_rename, "更改 "
							+ word + " 为 " + str, null, "建议修改为 : " + str);
		}
		cps[comp.size()] = new CompletionProposal("",
				markerRegion.getOffset() - 1, deleteLength, 0, img_remove,
				"删除此属性", null, "建议删除");
		return cps;
	}

	private ICompletionProposal[] getDataSetProposal() {
		// if (!validWord(word))
		// return null;
		CompositeMap map = QuickAssistUtil.findMap(rootMap, line);
		@SuppressWarnings("unchecked")
		Map<String, String> nsMapping = rootMap.getNamespaceMapping();
		String aPrefix = map
				.getString("http://www.aurora-framework.org/application");
		if (aPrefix == null)
			aPrefix = "a";
		List<SortElement> definedDS = new ArrayList<SortElement>();
		for (String ds : getDefinedDataSets(rootMap)) {
			int ed = QuickAssistUtil.getApproiateEditDistance(word, ds);
			if (ed == -1)
				continue;
			definedDS.add(new SortElement(ds, ed));
		}
		CompositeMap[] pathMap = getDataSetsPathMaps(rootMap);
		if (pathMap[0] == null)
			return null;
		int insertOffset = 0;
		String insertTag = "";
		CompositeMap dataSetMap = new CompositeMap();// 将要插入的DataSet结点
		dataSetMap.setNamespaceMapping(nsMapping);
		dataSetMap.setName("dataSet");
		dataSetMap.setPrefix(aPrefix);
		dataSetMap.put("id", word);
		int replaceLength = 0;
		if (pathMap[2] == null) {// 如果不存在dataSets结点
			CompositeMap dataSetsMap = new CompositeMap();
			dataSetsMap.setNamespaceMapping(nsMapping);
			dataSetsMap.setName("dataSets");
			dataSetsMap.setPrefix(aPrefix);
			dataSetsMap.addChild(dataSetMap);
			String prefix = "";

			CompositeMapInDocument relMap;
			if (pathMap[1] == null) {// script不存在,直接作为view的子接点,插在最前面
				relMap = CompositeMapInDocumentManager
						.getCompositeMapInDocument(pathMap[0], doc);
				prefix = getLeadingPrefix(doc, relMap)
						+ XMLOutputter.DEFAULT_INDENT;
				IRegion region = relMap.getStart();
				insertOffset = region.getOffset() + region.getLength();
				insertTag = XMLOutputter.LINE_SEPARATOR
						+ prefix
						+ dataSetsMap
								.toXML()
								.trim()
								.replace(XMLOutputter.LINE_SEPARATOR,
										XMLOutputter.LINE_SEPARATOR + prefix);

			} else {// 插在script段下面
				relMap = CompositeMapInDocumentManager
						.getCompositeMapInDocument(pathMap[1], doc);
				prefix = getLeadingPrefix(doc, relMap);
				IRegion endRegion = relMap.getEnd();
				insertOffset = endRegion.getOffset() + endRegion.getLength();
				insertTag = XMLOutputter.LINE_SEPARATOR
						+ prefix
						+ dataSetsMap
								.toXML()
								.trim()
								.replace(XMLOutputter.LINE_SEPARATOR,
										XMLOutputter.LINE_SEPARATOR + prefix);
			}
		} else {
			CompositeMap outerDsMap = getOuterDataSetMap(map);
			/*
			 * 如果当前结点本身就在dataSet中,那就插在当前dataSet结点前面,
			 */
			if (outerDsMap != null) {
				CompositeMapInDocument relMap = CompositeMapInDocumentManager
						.getCompositeMapInDocument(outerDsMap, doc);
				IRegion region = relMap.getStart();
				String prefix = getLeadingPrefix(doc, relMap);
				if (prefix == null)
					return null;
				insertOffset = region.getOffset();
				insertTag = dataSetMap.toXML().trim()
						+ XMLOutputter.LINE_SEPARATOR + prefix;
			}
			/*
			 * 插在dataSets段的尾部
			 */
			else {
				CompositeMapInDocument relMap = CompositeMapInDocumentManager
						.getCompositeMapInDocument(pathMap[2], doc);
				IRegion region = relMap.getStart();
				IRegion endRegion = relMap.getEnd();
				String prefix = getLeadingPrefix(doc, relMap);
				if (prefix == null)
					return null;
				clearNameSpaceURI(pathMap[2]);
				replaceLength = endRegion.getOffset() + endRegion.getLength()
						- region.getOffset();
				pathMap[2].addChild(dataSetMap);
				insertOffset = region.getOffset();
				insertTag = pathMap[2]
						.toXML()
						.trim()
						.replace(XMLOutputter.LINE_SEPARATOR,
								XMLOutputter.LINE_SEPARATOR + prefix);
			}
		}

		Collections.sort(definedDS);
		ICompletionProposal[] cps = new ICompletionProposal[definedDS.size() + 1];
		for (int i = 0; i < definedDS.size(); i++) {
			String str = definedDS.get(i).name;
			cps[i] = new CompletionProposal(str, markerRegion.getOffset(),
					markerRegion.getLength(), str.length(), img_rename, "更改为 "
							+ str, null, "建议修改为 : " + str);
		}
		cps[definedDS.size()] = new CompletionProposal(insertTag, insertOffset,
				replaceLength, insertTag.length(), img_new, "创建一个DataSet",
				null, "创建DataSet " + word);
		return cps;
	}

	/**
	 * 清除map及其所有子节点的nameSpaceURI,以便toXML时不会再出现
	 * 
	 * @param map
	 */
	private void clearNameSpaceURI(CompositeMap map) {
		map.iterate(new IterationHandle() {

			public int process(CompositeMap map) {
				map.setNameSpaceURI(null);
				return 0;
			}
		}, true);
	}

	private Set<String> getDefinedDataSets(CompositeMap map) {
		final Set<String> set = new HashSet<String>();
		map.iterate(new IterationHandle() {

			public int process(CompositeMap map) {
				if (!map.getName().equalsIgnoreCase("dataset"))
					return 0;
				String ds = map.getString("id");
				if (ds != null)
					set.add(ds);
				return 0;
			}
		}, true);
		return set;
	}

	private String getLeadingPrefix(IDocument doc, CompositeMapInDocument relMap) {
		IRegion startRegion = relMap.getStart();
		int offset = startRegion.getOffset();
		try {
			int line = doc.getLineOfOffset(offset);
			int lineOffset = doc.getLineOffset(line);
			return doc.get(lineOffset, offset - lineOffset);
		} catch (BadLocationException e) {
			e.printStackTrace();
			return null;
		}
	}

	private CompositeMap getOuterDataSetMap(CompositeMap map) {
		while (map != null && !map.getName().equalsIgnoreCase("dataSet"))
			map = map.getParent();
		return map;
	}

	/**
	 * 返回3个CompositeMap:view结点,view 下的script结点,DataSets结点
	 * 
	 * @param rootMap
	 * @return
	 */
	private CompositeMap[] getDataSetsPathMaps(CompositeMap rootMap) {
		final CompositeMap[] path = new CompositeMap[3];
		rootMap.iterate(new IterationHandle() {

			public int process(CompositeMap map) {
				if (map.getName().equals("view")) {
					path[0] = map;
				} else if (map.getName().equals("script")
						&& map.getParent().getName().equals("view")) {
					path[1] = map;
				} else if (map.getName().equalsIgnoreCase("dataSets")) {
					path[2] = map;
					return IterationHandle.IT_BREAK;
				}
				return 0;
			}
		}, true);
		return path;
	}
}
