package uncertain.ide.eclipse.editor.textpage.contentassist;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.action.CompositeMapAction;
import uncertain.ide.eclipse.action.CompositeMapLocatorParser;
import uncertain.ide.eclipse.editor.textpage.scanners.XMLPartitionScanner;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.schema.Element;

/**
 * @author linjinxiao
 * 
 */
public class ChildStrategy implements IContentAssistStrategy {

	TokenString tokenString;
	ITextViewer viewer;
	int offset;

	public ChildStrategy(TokenString tokenString) {
		this.tokenString = tokenString;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) throws BadLocationException {
		this.viewer = viewer;
		this.offset = offset;
		IDocument document = viewer.getDocument();
		int length = offset - tokenString.getDocumentOffset() + 1;
		String old = document.get(tokenString.getDocumentOffset() - 1, length);

		document.replace(tokenString.getDocumentOffset() - 1, length, "");
		String content = document.get();

		CompositeLoader cl = new CompositeLoader();
		cl.setSaveNamespaceMapping(true);
		ITypedRegion region = document.getPartition(offset - 1);
		ITypedRegion endRegion = null;
		int startCount = 0;
		int endCount = 0;
		while (region != null) {
			if (XMLPartitionScanner.XML_START_TAG.equals(region.getType())) {
				if (region.getOffset() < offset
						&& !document.get(
								region.getOffset() + region.getLength() - 2, 1)
								.equals("/")) {
					startCount++;
					if (startCount > endCount) {
						break;
					}
				}
			}
			if (XMLPartitionScanner.XML_END_TAG.equals(region.getType())) {
				if (endRegion == null) {
					endRegion = region;
				}
				endCount++;
			}
			region = document.getPartition(region.getOffset() - 1);
		}
		CompositeMap data;
		try {
			data = locateCompositeMap(content, region.getOffset());
		} catch (Exception e) {
			return getDefaultCompletionProposal();
		} finally {
			document.replace(tokenString.getDocumentOffset() - 1, 0, old);
			viewer.setSelectedRange(tokenString.getDocumentOffset() - 1
					+ old.length(), 0);
		}
		List childs = CompositeMapAction.getAvailableChildElements(data);
		if (childs == null)
			childs = new ArrayList();
		Element ele = LoadSchemaManager.getSchemaManager().getElement(data);
		if (ele != null) {
			childs.addAll(ele.getAllArrays());
		} else {
			return getDefaultCompletionProposal();
		}
		List avaliableList = new ArrayList();
		String preString = tokenString.getStrBeforeCursor();
		for (Iterator iter = childs.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			String name = CompositeMapAction.getContextFullName(data, element
					.getQName());
			if (preString != null && !name.startsWith(preString)) {
				continue;
			}
			String attributeDocument = element.getDocument();
			String description = name;
			String replaceString = name + " ></" + name + ">";
			if (attributeDocument != null)
				description = formateAttributeName(name) + " - "
						+ attributeDocument;
			Image contentImage = element.isArray() ? getArrayImage()
					: getElementImage();
			avaliableList.add(new CompletionProposal(replaceString, tokenString
					.getDocumentOffset(), tokenString.getLength(), name
					.length() + 1, contentImage, description, null,
					attributeDocument));
		}
		int allLength = avaliableList.size();
		if (allLength == 0)
			return getDefaultCompletionProposal();
		ICompletionProposal[] result = new ICompletionProposal[allLength];
		int i = 0;
		for (Iterator iter = avaliableList.iterator(); iter.hasNext();) {
			result[i] = (CompletionProposal) iter.next();
			i++;
		}
		return result;
	}

	private CompositeMap locateCompositeMap(String content, int offset) {
		CompositeMapLocatorParser parser = new CompositeMapLocatorParser();

		try {
			InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			CompositeMap cm = parser.getCompositeMapFromLine(is,
					getCursorLine(offset));
			return cm;
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		return null;
	}

	private int getCursorLine(int offset) {
		return viewer.getTextWidget().getLineAtOffset(offset);
	}

	private String formateAttributeName(String attributeName) {
		int defaultLength = 20;
		StringBuffer newAttributeName = new StringBuffer(attributeName);
		int strLength = newAttributeName.length();
		if (strLength < defaultLength) {
			for (int i = 0; i < defaultLength - strLength; i++) {
				newAttributeName.append(" ");
			}
		}
		return newAttributeName.toString();
	}

	private static Image getArrayImage() {
		Image contentImage = Activator.getImageDescriptor(
				LocaleMessage.getString("array.icon")).createImage();
		return contentImage;
	}

	private static Image getElementImage() {
		Image contentImage = Activator.getImageDescriptor(
				LocaleMessage.getString("element.icon")).createImage();
		return contentImage;
	}

	private static Image getDefaultImage() {
		Image contentImage = Activator.getImageDescriptor(
				LocaleMessage.getString("contentassit.icon")).createImage();
		return contentImage;
	}

	private ICompletionProposal[] getDefaultCompletionProposal() {
		String text = tokenString.getText();
		if (text == null || text.equals(""))
			return null;
		String replaceString = tokenString.getText() + " ></" + text + ">";
		return new ICompletionProposal[] { new CompletionProposal(
				replaceString, tokenString.getDocumentOffset(), tokenString
						.getLength(), text.length() + 1, getDefaultImage(),
				null, null, null) };
	}
}
