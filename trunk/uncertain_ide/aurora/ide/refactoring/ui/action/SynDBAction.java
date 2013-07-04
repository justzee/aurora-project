package aurora.ide.refactoring.ui.action;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.builder.CompositeMapInfo;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.DocumentUtil;
import aurora.ide.refactor.bm.BMFieldRefactoring;
import aurora.ide.refactoring.ui.AuroraRefactoringWizard;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class SynDBAction extends Action {

	private TextPage textPage;

	public SynDBAction() {
		this.setText("同步数据库");
		this.setId("aurora.ide.refactoring.ui.action.SynDBAction");
	}

	public SynDBAction(TextPage textPage) {
		this.textPage = textPage;
	}

	// {
	// String hover = getMarkerInfo(sourceViewer, hoverRegion);
	// if (hover != null)
	// return html(hover);
	// doc = textViewer.getDocument();
	// String word = null;
	// try {
	// word = doc.get(hoverRegion.getOffset(), hoverRegion.getLength());
	// } catch (BadLocationException e1) {
	// }
	// if (word == null || word.trim().length() == 0)
	// return null;
	// try {
	// map = CompositeMapUtil.loaderFromString(doc.get());
	// } catch (Exception e) {
	// return null;
	// }
	// CompositeMap cursorMap = QuickAssistUtil.findMap(map, doc,
	// hoverRegion.getOffset());
	// CompositeMapInfo info = new CompositeMapInfo(cursorMap, doc);
	// if (hoverRegion.equals(info.getMapNameRegion())
	// || hoverRegion.equals(info.getMapEntTagNameRegion())) {
	// // if hover region is Tag name...
	// return html(SxsdUtil.getHtmlDocument(cursorMap));
	// } else {
	// // ////if hover region is attribute.....
	// @SuppressWarnings("unchecked")
	// Set<String> keySet = cursorMap.keySet();
	// for (String key : keySet) {
	// IRegion region = info.getAttrNameRegion(key);
	// if (region == null)
	// continue;
	// if (hoverRegion.equals(region)) {
	// return html(getAttrDocument(cursorMap, key));
	// }
	// region = info.getAttrValueRegion2(key);
	// if (region == null)
	// continue;
	// if (RegionUtil.isSubRegion(region, hoverRegion)) {
	// // return html(cursorMap.getString(key));
	// SchemaTypeManager stm = new SchemaTypeManager(cursorMap);
	// ITypeDelegate de = stm.getAttributeTypeDelegate(key);
	// if (de != null) {
	// return html( de.getValue(cursorMap.getString(key)) );
	// }
	// return html("<pre>" + cursorMap.getString(key) + "</pre>");
	// }
	// }
	// // ////if the hover region is namespace declare.....
	// @SuppressWarnings("unchecked")
	// Map<String, String> nsMap = cursorMap.getNamespaceMapping();
	// Map<String, String> reverseNsMap = new HashMap<String, String>();
	// if (nsMap != null)
	// for (String key : nsMap.keySet()) {
	// reverseNsMap.put(nsMap.get(key), key);
	// }
	// for (String key : reverseNsMap.keySet()) {
	// String realKey = "xmlns:" + key;
	// IRegion region = info.getAttrNameRegion(realKey);
	// if (region == null)
	// continue;
	// if (RegionUtil.isSubRegion(region, hoverRegion)) {
	// return html("XML Namespace : " + key + "   ");
	// }
	// region = info.getAttrValueRegion2(realKey);
	// if (region == null)
	// continue;
	// if (RegionUtil.isSubRegion(region, hoverRegion)) {
	// return html("<pre>" + info.getAttrRealValue(realKey)
	// + "</pre>");
	// }
	// }
	// }
	// return html(w
	// }

	@Override
	public boolean isEnabled() {
		return textPage.isDirty() == false;
//		IEditorReference[] editorReferences = AuroraPlugin.getActivePage()
//				.getEditorReferences();
//		for (IEditorReference iEditorReference : editorReferences) {
//			if (iEditorReference.isDirty()) {
//				return false;
//			}
//		}
//		ISelection selection = textPage.getSelectionProvider().getSelection();
//		if (selection instanceof TextSelection == false) {
//			return false;
//		}
//		TextSelection ts = (TextSelection) selection;
//		if (ts.getLength() == 0)
//			return false;
//		if (ts.getStartLine() != ts.getEndLine()) {
//			return false;
//		}
//		CompositeMap cursorMap = null;
//		try {
//			cursorMap = CompositeMapUtil
//					.loaderFromString(textPage.getContent());
//		} catch (ApplicationException e) {
//			return false;
//		}
//		if (cursorMap == null)
//			return false;
//		CompositeMapInfo info = new CompositeMapInfo(cursorMap,
//				textPage.getDocument());
//		@SuppressWarnings("unchecked")
//		Set<String> keySet = cursorMap.keySet();
//		for (String key : keySet) {
//			if (isBMFieldType(key) == false) {
//				continue;
//			}
//			IRegion region = info.getAttrNameRegion(key);
//			if (region == null)
//				continue;
//			region = info.getAttrValueRegion2(key);
//			if (region == null)
//				continue;
//			if (region.equals(new Region(ts.getOffset(), ts.getLength()))) {
//				return true;
//			}
//		}
//		return false;
	}

//	public boolean performFinish() {
//		try {
//			ScreenBody viewDiagram = prototpyePage.getViewDiagram();
//			CompositeMap generate = generate(viewDiagram);
//			String text = createInsertText(generate);
//			String insertDatasetText = "";
//			IRewriteTarget target = (IRewriteTarget) textPage
//					.getAdapter(IRewriteTarget.class);
//			if (target != null)
//				target.beginCompoundChange();
//			int dsOffset = 0;
//			int dslength = 0;
//			if (viewMap != null && !this.isInSameLine(viewMap)) {
//				insertDatasetText = createInsertDatasetText(generate);
//				if (this.datasetsMap == null) {
//					dsOffset = DocumentUtil.getMapLineOffset(document, viewMap,
//							0, true);
//				}
//				if (this.datasetsMap != null) {
//					boolean inSameLine = this.isInSameLine(datasetsMap);
//					if (inSameLine) {
//						int endLine = datasetsMap.getLocation().getEndLine();
//						dsOffset = DocumentUtil.getMapLineOffset(document,
//								datasetsMap, -1, false);
//						dslength = document.getLineLength(endLine - 1);
//					} else {
//						dsOffset = DocumentUtil.getMapLineOffset(document,
//								datasetsMap, -1, false);
//					}
//				}
//
//			}
//
//			CompositeMap afterMap = prototpyePage.getAfterMap();
//			if (afterMap == null) {
//				afterMap = insertAfters.get(0);
//			}
//			int viewOffset = 0;
//			int viewLength = 0;
//
//			boolean inSameLine = this.isInSameLine(viewMap);
//			if (inSameLine) {
//				int endLine = viewMap.getLocation().getEndLine();
//				viewOffset = document.getLineOffset(endLine - 1);
//				viewLength = document.getLineLength(endLine - 1);
//			} else if ("begin".equals(afterMap.getName())) {
//				CompositeMap aMap = this.bodyMap == null ? viewMap : bodyMap;
//				viewOffset = DocumentUtil.getMapLineOffset(document, aMap, 0,
//						true);
//
//			} else if ("end".equals(afterMap.getName())) {
//				CompositeMap aMap = this.bodyMap == null ? viewMap : bodyMap;
//				viewOffset = DocumentUtil.getMapLineOffset(document, aMap, -1,
//						false);
//			} else {
//				viewOffset = DocumentUtil.getMapLineOffset(document, afterMap,
//						0, false);
//			}
//
//			if (viewOffset - dsOffset >= 0) {
//				document.replace(viewOffset, viewLength, text);
//				if (dsOffset != 0) {
//					document.replace(dsOffset, dslength, insertDatasetText);
//				}
//				viewOffset += insertDatasetText.length() - dslength;
//			} else {
//				if (dsOffset != 0) {
//					document.replace(dsOffset, dslength, insertDatasetText);
//				}
//				document.replace(viewOffset, viewLength, text);
//			}
//
//			StyledText st = (StyledText) textPage.getAdapter(StyledText.class);
//			st.setSelectionRange(viewOffset, text.length());
//		} catch (BadLocationException e) {
//			componentListPage.setErrorMessage("操作无法完成请查看log获得更多信息");
//			DialogUtil.logErrorException(e);
//			return false;
//		}
//		return true;
//	}
	public void run() {
		Shell shell = textPage.getSite().getShell();
		BMFieldRefactoring refactor = new BMFieldRefactoring(null);
		AuroraRefactoringWizard wizard = new AuroraRefactoringWizard(refactor);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(
				wizard);
		try {
			op.run(shell, "Screen Custom");
		} catch (InterruptedException e) {
		}
	}

}
