package aurora.ide.editor.textpage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import uncertain.composite.XMLOutputter;
import aurora.ide.AuroraPlugin;
import aurora.ide.builder.RegionUtil;
import aurora.ide.editor.BaseCompositeMapEditor;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;

public class ToggleCommentAction extends Action implements
		IEditorActionDelegate {

	IEditorPart activeEditor;
	ISelection selection;

	public ToggleCommentAction() {
		setActionDefinitionId("aurora.ide.togglecomment");
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
	}

	public void run(IAction action) {
		if (activeEditor == null || !(activeEditor instanceof TextPage)) {
			return;
		}
		TextPage tp = (TextPage) activeEditor;
		try {
			comment(tp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void comment(TextPage page) throws Exception {
		IDocument doc = page.getInputDocument();
		Point sRange = page.getSelectedRange();
		ITypedRegion partitionRegion = doc.getPartition(sRange.x);
		String pType = partitionRegion.getType();
		if (XMLPartitionScanner.XML_CDATA.equals(pType)) {
			ITypedRegion pRegion = doc
					.getPartition(partitionRegion.getOffset() - 1);
			String pNode = doc.get(pRegion.getOffset(), pRegion.getLength())
					.toLowerCase();
			if (pNode.matches("<script( .*){0,1}>")) {
				doLineComment("//", page);
			} else if (pNode.matches(".+-sql.+")) {
				doLineComment("--", page);
			}
		} else if (XMLPartitionScanner.XML_START_TAG.equals(pType)
				|| XMLPartitionScanner.XML_END_TAG.equals(pType)) {
			if (sRange.y == 0) {
				String tagText = doc.get(partitionRegion.getOffset(),
						partitionRegion.getLength());
				doc.replace(partitionRegion.getOffset(),
						partitionRegion.getLength(),
						String.format("<!-- %s -->", tagText));
				page.getSelectionProvider().setSelection(
						new TextSelection(sRange.x + 5, 0));
				return;
			}

			String text = doc.get(sRange.x, sRange.y);
			doc.replace(sRange.x, sRange.y, String.format("<!-- %s -->", text));
			page.getSelectionProvider().setSelection(
					new TextSelection(sRange.x, sRange.y + 9));
		} else if (XMLPartitionScanner.XML_COMMENT.equals(pType)) {
			if (sRange.y == 0) {
				int startLine = doc
						.getLineOfOffset(partitionRegion.getOffset());
				if (doc.getLineOfOffset(partitionRegion.getOffset()
						+ partitionRegion.getLength()) != startLine)
					return;
				int startOffsetInComment = sRange.x
						- partitionRegion.getOffset();
				int startOffset = partitionRegion.getOffset();
				int length = partitionRegion.getLength();
				String commentText = doc.get(startOffset, length);
				int caretDelt = uncommentXMLDelt(commentText,
						startOffsetInComment);
				String textNew = uncommentXML(commentText);
				doc.replace(startOffset, length, textNew);
				page.getSelectionProvider().setSelection(
						new TextSelection(sRange.x + caretDelt, 0));
				return;
			}
			if (!RegionUtil.isSubRegion(partitionRegion, new Region(sRange.x,
					sRange.y)))
				return;
			sRange.x = partitionRegion.getOffset();
			sRange.y = partitionRegion.getLength();
			String text = doc.get(sRange.x, sRange.y);
			String textNew = uncommentXML(text);
			doc.replace(sRange.x, sRange.y, textNew);
			page.getSelectionProvider().setSelection(
					new TextSelection(sRange.x, sRange.y
							- (text.length() - textNew.length())));
		} else if (IDocument.DEFAULT_CONTENT_TYPE.equals(pType)) {
			IRegion sRegion = new Region(sRange.x, sRange.y);
			if (RegionUtil.isSubRegion(partitionRegion, sRegion)) {
				String text = doc.get(sRange.x, sRange.y);
				String textNew = String.format("<!-- %s -->", text);
				doc.replace(sRange.x, sRange.y, textNew);
				page.getSelectionProvider().setSelection(
						new TextSelection(sRange.x, sRange.y + 9));
			} else {
				ITypedRegion commentRegion = null;
				for (int i = sRange.x; i < sRange.x + sRange.y; i++) {
					ITypedRegion region = doc.getPartition(i);
					String type = region.getType();
					i = region.getOffset() + region.getLength();
					if (IDocument.DEFAULT_CONTENT_TYPE.equals(type)) {
						continue;
					} else if (XMLPartitionScanner.XML_COMMENT.equals(type)) {
						if (commentRegion == null) {
							commentRegion = region;
							continue;
						}
						return;
					} else
						return;
				}
				String text = doc.get(sRange.x, sRange.y);
				String textNew = uncommentXML(text);
				doc.replace(sRange.x, sRange.y, textNew);
				page.getSelectionProvider().setSelection(
						new TextSelection(commentRegion.getOffset(),
								commentRegion.getLength()
										- (text.length() - textNew.length())));
			}
		}
	}

	public void run() {
		try {
			comment((TextPage) ((BaseCompositeMapEditor) AuroraPlugin
					.getActivePage().getActiveEditor()).getActiveEditor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	private String uncommentXML(String cmxml) {
		StringBuilder sb = new StringBuilder(cmxml);
		int idx = sb.indexOf("<!-- ");
		if (idx != -1)
			sb.delete(idx, idx + 5);
		else {
			idx = sb.indexOf("<!--");
			sb.delete(idx, idx + 4);
		}
		idx = sb.lastIndexOf(" -->");
		if (idx != -1)
			sb.delete(idx, idx + 4);
		else {
			idx = sb.lastIndexOf("-->");
			sb.delete(idx, idx + 3);
		}
		return sb.toString();
	}

	private int uncommentXMLDelt(String cmstr, int offsetIL) {
		int idx1 = cmstr.indexOf("<!-- ");
		int sl = 5;
		if (idx1 == -1) {
			idx1 = cmstr.indexOf("<!--");
			sl = 4;
		}
		int idx2 = cmstr.lastIndexOf(" -->");
		int el = 4;
		if (idx2 == -1) {
			idx2 = cmstr.lastIndexOf("-->");
			el = 3;
		}
		if (offsetIL <= idx1)
			return 0;
		else if (offsetIL <= idx1 + sl)
			return idx1 - offsetIL;
		else if (offsetIL <= idx2)
			return -sl;
		else if (offsetIL <= idx2 + el)
			return idx2 - offsetIL - sl;
		return -sl - el;
	}

	private String getTextOfLine(IDocument doc, int line) throws Exception {
		IRegion region = doc.getLineInformation(line);
		return doc.get(region.getOffset(), region.getLength());
	}

	private String commentSingleLine(String lineText, String prefix) {
		StringBuilder sb = new StringBuilder(lineText.length() + 10);
		sb.append(lineText);
		for (int i = 0; i < lineText.length(); i++) {
			char c = sb.charAt(i);
			if (!Character.isWhitespace(c)) {
				sb.insert(i, prefix + " ");
				break;
			}
		}
		return sb.toString();
	}

	private String uncommentSingleLine(String lineText, String prefix) {
		StringBuilder sb = new StringBuilder(lineText.length());
		sb.append(lineText);
		int idx = sb.indexOf(prefix);
		if (idx == -1)
			return lineText;
		for (int i = 0; i < prefix.length(); i++)
			sb.deleteCharAt(idx);
		if (sb.charAt(idx) == ' ')
			sb.deleteCharAt(idx);
		return sb.toString();
	}

	private void doLineComment(String prefix, TextPage page) throws Exception {
		IDocument doc = page.getInputDocument();
		Point sRange = page.getSelectedRange();
		int startLine = doc.getLineOfOffset(sRange.x);
		int startOffsetInLine = sRange.x - doc.getLineOffset(startLine);
		ITypedRegion partitionRegion = doc.getPartition(sRange.x);
		if (sRange.y == 0) {
			String text = getTextOfLine(doc, startLine);
			String textNew = null;
			boolean tc = isCommentOf(text, prefix);
			textNew = tc ? uncommentSingleLine(text, prefix)
					: commentSingleLine(text, prefix);
			int delt[] = computeDelt(text, textNew, prefix, startOffsetInLine);
			doc.replace(doc.getLineOffset(startLine), text.length(), textNew);
			page.getSelectionProvider().setSelection(
					new TextSelection(sRange.x + delt[0], 0));
			return;
		}
		if (partitionRegion.getOffset() + partitionRegion.getLength() < sRange.x
				+ sRange.y) {
			return;
		}
		int endLine = doc.getLineOfOffset(sRange.x + sRange.y);
		if (doc.getLineOffset(endLine) == sRange.x + sRange.y)
			endLine--;
		int startOffset = doc.getLineOffset(startLine);
		IRegion endLineRegion = doc.getLineInformation(endLine);
		int length = endLineRegion.getOffset() + endLineRegion.getLength()
				- startOffset;
		int endOffsetInLine = sRange.x + sRange.y - doc.getLineOffset(endLine);
		String text = doc.get(startOffset, length).replace("\r\n", "\n")
				.replace("\r", "\n");
		String[] ss = text.split("\n");
		StringBuilder sb = new StringBuilder(ss.length * 4 + text.length());
		boolean ct = isCommentOf(ss, prefix);
		String textNew = ct ? uncommentSingleLine(ss[0], prefix)
				: commentSingleLine(ss[0], prefix);

		int[] delt = computeDelt(ss[0], textNew, prefix, startOffsetInLine);
		int offsetDelt = delt[0];
		int lengthDelt = delt[1];
		sb.append(textNew);
		if (ss.length > 1) {
			sb.append(XMLOutputter.LINE_SEPARATOR);
			for (int i = 1; i < ss.length - 1; i++) {
				textNew = ct ? uncommentSingleLine(ss[i], prefix)
						: commentSingleLine(ss[i], prefix);
				sb.append(textNew);
				sb.append(XMLOutputter.LINE_SEPARATOR);
				lengthDelt += (textNew.length() - ss[i].length());
			}
			text = ss[ss.length - 1];
			textNew = ct ? uncommentSingleLine(text, prefix)
					: commentSingleLine(text, prefix);
			sb.append(textNew);
			delt = computeDelt(text, textNew, prefix, endOffsetInLine);
			lengthDelt += delt[0];
		}
		doc.replace(startOffset, length, sb.toString());
		page.getSelectionProvider()
				.setSelection(
						new TextSelection(sRange.x + offsetDelt, sRange.y
								+ lengthDelt));
	}

	private boolean isCommentOf(String lineText, String prefix) {
		int idx = lineText.indexOf(prefix);
		if (idx == -1)
			return false;
		for (int i = 0; i < idx; i++) {
			if (!Character.isWhitespace(lineText.charAt(i)))
				return false;
		}
		return true;
	}

	private boolean isCommentOf(String[] ss, String prefix) {
		for (String s : ss) {
			if (s.trim().length() == 0)
				continue;
			if (!isCommentOf(s, prefix))
				return false;
		}
		return true;
	}

	private int getWspLength(String lineText) {
		int i = 0;
		for (; i < lineText.length(); i++) {
			if (!Character.isWhitespace(lineText.charAt(i)))
				return i;
		}
		return i;
	}

	private int getWapLength(String text, String prefix) {
		int idx = text.indexOf(prefix);
		idx += prefix.length();
		if (Character.isWhitespace(text.charAt(idx)))
			return idx + 1;
		return idx;
	}

	/**
	 * 
	 * @param text
	 * @param textNew
	 * @param prefix
	 * @param offsetIL
	 * @return delt[0]:caretDelt<br/>
	 *         delt[1]:lengthDelt
	 */
	private int[] computeDelt(String text, String textNew, String prefix,
			int offsetIL) {
		int[] delt = new int[] { 0, 0 };
		int l1 = text.length();
		int l2 = textNew.length();
		int wsp = getWspLength(text);
		if (l1 < l2)/* add comment */{
			if (wsp <= offsetIL) {
				delt[0] = l2 - l1;
			} else {
				delt[1] = l2 - l1;
			}
		} else/* remove comment */{
			int wap = getWapLength(text, prefix);
			if (wsp >= offsetIL) {
				delt[1] = l2 - l1;
			} else if (wap >= offsetIL) {
				delt[0] = wsp - offsetIL;
				delt[1] = offsetIL - wap;
			} else {
				delt[0] = l2 - l1;
			}
		}
		return delt;
	}
}
