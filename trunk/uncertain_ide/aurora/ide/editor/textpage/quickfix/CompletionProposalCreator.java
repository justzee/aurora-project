package aurora.ide.editor.textpage.quickfix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.bm.wizard.sql.BMFromSQLWizard;
import aurora.ide.bm.wizard.sql.BMFromSQLWizardPage;
import aurora.ide.builder.AuroraBuilder;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.project.propertypage.ProjectPropertyPage;

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
	private int markerOffset;

	private String bm;

	public CompletionProposalCreator(IDocument doc, CompositeMap rootMap,
			IMarker marker) {
		this.rootMap = rootMap;
		this.doc = doc;
		this.marker = marker;
		markerOffset = marker.getAttribute(IMarker.CHAR_START, 0);
		int length = marker.getAttribute(IMarker.CHAR_END, 0) - markerOffset;
		markerRegion = new Region(markerOffset, length);
		try {
			line = doc.getLineOfOffset(markerOffset);
			word = doc.get(markerOffset, length);
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
		} else if (markerType.equals(AuroraBuilder.UNDEFINED_BM)) {
			return getBmProposal();
		} else if (markerType.equals(AuroraBuilder.UNDEFINED_TAG)) {
			return getTagProposal();
		}
		return null;
	}

	private ICompletionProposal[] getAttributeProposal() {
		CompositeMap map = QuickAssistUtil.findMap(rootMap, doc, markerOffset);
		String value = map.getString(word);
		if (value == null)// 很可能是Annotation发生错位...
			return null;
		CompositeMapInfo info = new CompositeMapInfo(map, doc);
		IRegion attrRegion = info.getAttrRegion(word);
		// FIXME 属性值中含有特殊字符,被转义,查找失败,返回null
		if (attrRegion == null)
			return null;
		ArrayList<SortElement> comp = new ArrayList<SortElement>();
		@SuppressWarnings("unchecked")
		Set<Map.Entry<String, String>> set = map.entrySet();
		Set<String> keySet = new HashSet<String>();// map中已经出现的属性名
		for (Map.Entry<String, String> entry : set) {
			keySet.add(entry.getKey().toLowerCase());
		}
		List<Attribute> definedAttribute;
		try {
			definedAttribute = SxsdUtil.getAttributesNotNull(map);
		} catch (Exception e) {
			return null;
		}// map的合法属性
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
			cps[i] = new CompletionProposal(str, attrRegion.getOffset(),
					word.length(), str.length(), img_rename, "更改 " + word
							+ " 为 " + str, null, "建议修改为 : " + str);
		}
		cps[comp.size()] = new CompletionProposal("",
				attrRegion.getOffset() - 1, attrRegion.getLength() + 1, 0,
				img_remove, "删除此属性", null, "建议删除");
		return cps;
	}

	private ICompletionProposal[] getDataSetProposal() {
		if (!isValidWord(word))
			return null;
		CompositeMap map = QuickAssistUtil.findMap(rootMap, doc, markerOffset);
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
		CompositeMap dataSetMap = new CommentCompositeMap();// 将要插入的DataSet结点
		dataSetMap.setNamespaceMapping(nsMapping);
		dataSetMap.setName("dataSet");
		dataSetMap.setPrefix(aPrefix);
		dataSetMap.put("id", word);
		int replaceLength = 0;
		if (pathMap[2] == null) {// 如果不存在dataSets结点
			CompositeMap dataSetsMap = new CommentCompositeMap();
			dataSetsMap.setNamespaceMapping(nsMapping);
			dataSetsMap.setName("dataSets");
			dataSetsMap.setPrefix(aPrefix);
			dataSetsMap.addChild(dataSetMap);
			String prefix = "";

			CompositeMapInfo info;
			if (pathMap[1] == null) {// script不存在,直接作为view的子接点,插在最前面
				info = new CompositeMapInfo(pathMap[0], doc);
				prefix = info.getLeadPrefix();
				prefix += CommentXMLOutputter.DEFAULT_INDENT;
				IRegion region = info.getStartTagRegion();
				insertOffset = region.getOffset() + region.getLength();
				insertTag = CommentXMLOutputter.LINE_SEPARATOR
						+ prefix
						+ dataSetsMap
								.toXML()
								.trim()
								.replace(CommentXMLOutputter.LINE_SEPARATOR,
										CommentXMLOutputter.LINE_SEPARATOR + prefix);

			} else {// 插在script段下面
				info = new CompositeMapInfo(pathMap[1], doc);
				prefix = info.getLeadPrefix();
				IRegion endRegion = info.getEndTagRegion();
				insertOffset = endRegion.getOffset() + endRegion.getLength();
				insertTag = CommentXMLOutputter.LINE_SEPARATOR
						+ prefix
						+ dataSetsMap
								.toXML()
								.trim()
								.replace(CommentXMLOutputter.LINE_SEPARATOR,
										CommentXMLOutputter.LINE_SEPARATOR + prefix);
			}
		} else {
			CompositeMap outerDsMap = getOuterDataSetMap(map);
			/*
			 * 如果当前结点本身就在dataSet中,那就插在当前dataSet结点前面,
			 */
			if (outerDsMap != null) {
				CompositeMapInfo info = new CompositeMapInfo(outerDsMap, doc);
				IRegion region = info.getStartTagRegion();
				String prefix = info.getLeadPrefix();
				insertOffset = region.getOffset();
				insertTag = dataSetMap.toXML().trim()
						+ CommentXMLOutputter.LINE_SEPARATOR + prefix;
			}
			/*
			 * 插在dataSets段的尾部
			 */
			else {
				CompositeMapInfo info = new CompositeMapInfo(pathMap[2], doc);
				IRegion region = info.getMapRegion();
				String prefix = info.getLeadPrefix();
				clearnsURI(pathMap[2]);
				replaceLength = region.getLength();
				pathMap[2].addChild(dataSetMap);
				insertOffset = region.getOffset();
				insertTag = pathMap[2]
						.toXML()
						.trim()
						.replace(CommentXMLOutputter.LINE_SEPARATOR,
								CommentXMLOutputter.LINE_SEPARATOR + prefix);
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

	private ICompletionProposal[] getBmProposal() {
		if (word.equals("\"\"") || word.equals("''"))
			return null;
		if (!isValidWord(word))
			return null;
		String bmHomeStr = null;
		IProject project = AuroraPlugin.getActiveIFile().getProject();
		try {
			bmHomeStr = project.getPersistentProperties().get(
					ProjectPropertyPage.BMQN);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (bmHomeStr == null)
			return null;
		int idx = word.lastIndexOf('.');
		bm = word;
		String prefix = "";
		if (idx != -1) {
			prefix = word.substring(0, idx).replace(".", "/");
			bm = word.substring(idx + 1);
		}
		final IFolder folder = project.getParent().getFolder(
				new Path(bmHomeStr + "/" + prefix));
		final ArrayList<SortElement> list = new ArrayList<SortElement>();
		try {
			if (folder.exists())
				folder.accept(new IResourceVisitor() {

					public boolean visit(IResource resource)
							throws CoreException {
						if (resource instanceof IFile) {
							IFile file = (IFile) resource;
							if ("bm".equalsIgnoreCase(file.getFileExtension())) {
								String fn = file.getName();
								fn = fn.substring(0, fn.length() - 3);
								int ed = QuickAssistUtil
										.getApproiateEditDistance(bm, fn);
								if (ed != -1) {
									list.add(new SortElement(fn, ed));
								}
							}
						}
						return true;
					}
				}, IResource.DEPTH_ONE, false);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (prefix.length() > 0)
			prefix = prefix.replace("/", ".") + ".";
		Collections.sort(list);
		ICompletionProposal cps[] = new ICompletionProposal[list.size() + 1];
		for (int i = 0; i < list.size(); i++) {
			SortElement se = list.get(i);
			String str = prefix + se.name;
			cps[i] = new CompletionProposal(str, markerRegion.getOffset(),
					markerRegion.getLength(), str.length(), img_rename, "更改为 "
							+ str, null, "建议修改为 : " + str);
		}
		CompletionProposalAction cpa = new CompletionProposalAction("",
				markerRegion.getOffset(), 0, 0, img_new, "创建bm : " + word,
				null, "新建一个BM");
		cpa.setAction(new EmptyAction() {

			@Override
			public void run() {
				BMFromSQLWizard sqlWizard = new BMFromSQLWizard();
				WizardDialog wd = new WizardDialog(new Shell(Display
						.getCurrent()), sqlWizard);
				wd.setBlockOnOpen(false);
				wd.open();
				BMFromSQLWizardPage wp = (BMFromSQLWizardPage) wd
						.getCurrentPage();
				wp.setFolder(folder.getFullPath().toString());
				wp.setFileName(bm);
				wp.getSQLTextField().forceFocus();
			}
		});
		cpa.setIgnoreReplace(true);
		cps[list.size()] = cpa;
		return cps;
	}

	private ICompletionProposal[] getTagProposal() {
		CompositeMap map = QuickAssistUtil.findMap(rootMap, doc, markerOffset);
		final String tagName = map.getName();
		CompositeMap parent = map.getParent();
		List<Element> aChilds = SxsdUtil.getAvailableChildElements(parent);
		final ArrayList<SortElement> list = new ArrayList<SortElement>();
		for (Element e : aChilds) {
			String name = e.getQName().getLocalName();
			if (name.equals(tagName))
				continue;
			int ed = QuickAssistUtil.getApproiateEditDistance(tagName, name);
			if (ed != -1) {
				list.add(new SortElement(name, ed));
			}
		}
		Collections.sort(list);
		CompositeMapInfo info = new CompositeMapInfo(map, doc);
		IRegion mapRegion = info.getMapRegion();
		IRegion mapNameRegion = info.getMapNameRegion();
		IRegion mapEndNameRegion = info.getMapEntTagNameRegion();
		boolean isSelfClose = mapNameRegion.equals(mapEndNameRegion);
		StringBuilder mapStr = null;
		int start1 = 0, start2 = 0;
		if (!isSelfClose) {
			start2 = mapEndNameRegion.getOffset() - mapRegion.getOffset();
			start1 = mapNameRegion.getOffset() - mapRegion.getOffset();
			mapStr = new StringBuilder(getString(doc, mapRegion));
		}
		ICompletionProposal[] cps = new ICompletionProposal[list.size() + 1];
		for (int i = 0; i < cps.length - 1; i++) {
			String name = list.get(i).name;
			if (!isSelfClose) {
				mapStr.replace(start2, start2 + tagName.length(), name);
				mapStr.replace(start1, start1 + tagName.length(), name);
				cps[i] = new CompletionProposal(mapStr.toString(),
						mapRegion.getOffset(), mapRegion.getLength(),
						mapNameRegion.getOffset() - mapRegion.getOffset()
								+ name.length(), img_rename, "更改为 " + name,
						null, "建议修改为 : " + name);
				continue;
			}
			cps[i] = new CompletionProposal(name, mapNameRegion.getOffset(),
					mapNameRegion.getLength(), name.length(), img_rename,
					"更改为 " + name, null, "建议修改为 : " + name);
		}
		cps[cps.length - 1] = new CompletionProposal("", mapRegion.getOffset(),
				mapRegion.getLength(), 0, img_remove, "删除Tag : " + tagName,
				null, "建议删除Tag : " + tagName);
		return cps;
	}

	/**
	 * 清除map及其所有子节点的nameSpaceURI,以便toXML时不会再出现
	 * 
	 * @param map
	 */
	private void clearnsURI(CompositeMap map) {
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
				if (!map.getName().equalsIgnoreCase("dataSet"))
					return 0;
				String ds = map.getString("id");
				if (ds != null)
					set.add(ds);
				return 0;
			}
		}, true);
		return set;
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
				if (map.getName().equalsIgnoreCase("view")) {
					path[0] = map;
				} else if (map.getName().equalsIgnoreCase("script")
						&& map.getParent().getName().equalsIgnoreCase("view")) {
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

	private boolean isValidWord(String w) {
		char[] ics = { '\'', '"', ' ', '\t', '\r', '\n' };
		for (char c : ics) {
			if (w.indexOf(c) != -1)
				return false;
		}
		return true;
	}

	private String getString(IDocument doc, IRegion region) {
		try {
			return doc.get(region.getOffset(), region.getLength());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
