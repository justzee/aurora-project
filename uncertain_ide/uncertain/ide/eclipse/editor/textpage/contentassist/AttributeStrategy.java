package uncertain.ide.eclipse.editor.textpage.contentassist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.textpage.IColorConstants;
import uncertain.ide.eclipse.editor.textpage.scanners.XMLTagScanner;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.LoadSchemaManager;
import uncertain.ide.help.LocaleMessage;
import uncertain.ide.help.SystemException;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.Namespace;
import uncertain.schema.Schema;

public class AttributeStrategy implements IContentAssistStrategy {

	private XMLTagScanner scanner;
	private TokenString tokenString;
	private ITextViewer viewer;
	private int offset;

	public AttributeStrategy(XMLTagScanner scanner, TokenString tokenString) {
		super();
		this.scanner = scanner;
		this.tokenString = tokenString;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		this.viewer = viewer;
		this.offset = offset;
		IDocument document = viewer.getDocument();
		QualifiedName qn = null;
		try {
			qn = getElementQualifiedName(offset, document);
			if (qn.getPrefix() != null && qn.getNameSpace() == null) {
				return getNoNameSpaceProposal(qn);
			}
		} catch (SystemException e) {
			CustomDialog.showErrorMessageBox(e);
			return null;
		}
		Element element = LoadSchemaManager.getSchemaManager().getElement(qn);
		if (element == null)
			return getDefaultCompletionProposal();
		List allAttributes = element.getAllAttributes();

		List avaliableList = new ArrayList();
		String preString = tokenString.getStrBeforeCursor();
		List existsList = null;
		try {
			existsList = getExistsAttrs(document);
		} catch (SystemException e) {
			CustomDialog.showErrorMessageBox(e);
			return null;
		}
		for (Iterator iter = allAttributes.iterator(); iter.hasNext();) {
			Attribute attr = (Attribute) iter.next();
			String name = attr.getName();
			if (existsList.contains(name)) {
				continue;
			}
			if (preString != null && !name.startsWith(preString)) {
				continue;
			}
			String attributeDocument = attr.getDocument();
			String description = name;
			String replaceString = name + "=\"\" ";
			if (attributeDocument != null)
				description = formateAttributeName(name) + " - "
						+ attributeDocument;
			avaliableList.add(new CompletionProposal(replaceString, tokenString
					.getDocumentOffset(), tokenString.getLength(),
					replaceString.length() - 2, getDefaultImage(), description,
					null, attributeDocument));

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

	private ICompletionProposal[] getNoNameSpaceProposal(
			QualifiedName qualifiedName) {
		ICompletionProposal[] result = new ICompletionProposal[1];
		String name = "xmlns:" + qualifiedName.getPrefix() + "=\"\"";
		result[0] = new CompletionProposal(name, tokenString
				.getDocumentOffset(), 0, name.length() - 1, null, name + "  - "
				+ LocaleMessage.getString("namespace.of.this.element"), null,
				LocaleMessage.getString("please.define.the.namespace.first"));
		return result;
	}

	private Namespace[] getNameSpaces(Map namespaceToPrefix) {
		if (namespaceToPrefix == null)
			return null;

		Namespace[] namespaces = new Namespace[namespaceToPrefix.keySet()
				.size()];
		Iterator elements = namespaceToPrefix.keySet().iterator();
		int i = 0;
		while (elements.hasNext()) {
			Object element = elements.next();
			Namespace namespace = new Namespace();
			namespace.setPrefix(namespaceToPrefix.get(element).toString());
			namespace.setUrl(element.toString());
			namespaces[i++] = namespace;
		}
		return namespaces;
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

	private QualifiedName getElementQualifiedName(int documentOffset,
			IDocument document) throws SystemException {
		String prefix = null;
		String name = null;
		String uri = null;
		ITypedRegion region;
		try {
			region = document.getPartition(documentOffset);
		} catch (BadLocationException e) {
			throw new SystemException(e);
		}
		int partitionOffset = region.getOffset();
		scanner.setRange(document, partitionOffset, region.getLength());
		IToken token = null;
		while ((token = scanner.nextToken()) != Token.EOF) {
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				if (text.getForeground().getRGB().equals(
						IColorConstants.TAG_NAME)) {
					String tagName;
					try {
						tagName = document.get(scanner.getTokenOffset(),
								scanner.getTokenLength());
					} catch (BadLocationException e) {
						throw new SystemException(e);
					}
					String[] splits = tagName.split(":");
					if (splits.length == 2) {
						QualifiedName qn = null;
						try {
							qn = getQualifiedName(document, tagName);
						} catch (Exception e) {
							qn = null;
						}
						if (qn != null)
							return qn;
						prefix = splits[0];
						name = splits[1];
						// getQualifiedName method will change the document,it
						// will change the scanner .
						scanner.setRange(document, partitionOffset, region
								.getLength());
						uri = getElementUrl(document, scanner, splits[0]);
					} else {
						name = tagName;
					}
					break;
				}

			}

		}
		return new QualifiedName(prefix, uri, name);
	}

	private QualifiedName getQualifiedName(IDocument document, String tagName)
			throws SystemException {
		CompositeLoader csloader = new CompositeLoader();
		csloader.setSaveNamespaceMapping(true);

		int length = offset - tokenString.getDocumentOffset();
		String old = null;
		InputStream is = null;
		try {
			old = document.get(tokenString.getDocumentOffset(), length);
			document.replace(tokenString.getDocumentOffset(), length, "");

			is = new ByteArrayInputStream(document.get().getBytes("UTF-8"));
		} catch (BadLocationException e) {
			throw new SystemException(e);
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		}
		CompositeMap root;
		try {
			root = csloader.loadFromStream(is);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				document.replace(tokenString.getDocumentOffset(), 0, old);
			} catch (BadLocationException e) {
				throw new SystemException(e);
			}
			viewer.setSelectedRange(tokenString.getDocumentOffset()
					+ old.length(), 0);
		}
		Map namespace_mapping = CompositeUtil.getPrefixMapping(root);
		Schema schema = new Schema();
		Namespace[] ns = getNameSpaces(namespace_mapping);
		schema.addNameSpaces(ns);
		QualifiedName qn = schema.getQualifiedName(tagName);
		return qn;
	}

	private String getElementUrl(IDocument document, XMLTagScanner scanner,
			String prefix) throws SystemException {
		IToken token = null;
		String attributeName = null;
		String namespace = "xmlns:";
		while ((token = scanner.nextToken()) != Token.EOF) {
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				if (text.getForeground().getRGB().equals(
						IColorConstants.ATTRIBUTE)) {
					try {
						attributeName = document.get(scanner.getTokenOffset(),
								scanner.getTokenLength());
					} catch (BadLocationException e) {
						throw new SystemException(e);
					}
					if (attributeName.startsWith(namespace)) {
						String[] xmlns = attributeName.split(":");
						if (xmlns[1].equals(prefix)) {
							return getNextAttributeContent(document, scanner);
						}
					}
				}
			}
		}
		return null;
	}

	private String getNextAttributeContent(IDocument document,
			XMLTagScanner scanner) throws SystemException {
		IToken token = null;
		while ((token = scanner.nextToken()) != Token.EOF) {
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				if (text.getForeground().getRGB()
						.equals(IColorConstants.STRING)) {
					try {
						return document.get(scanner.getTokenOffset() + 1,
								scanner.getTokenLength() - 2);
					} catch (BadLocationException e) {
						throw new SystemException(e);
					}
				} else if (IColorConstants.ATTRIBUTE.equals(text
						.getForeground().getRGB())) {
					return null;
				}
			}
		}
		return null;
	}

	private List getExistsAttrs(IDocument document) throws SystemException {
		List existsAttrs = new ArrayList();
		IToken token = null;
		String attributeName = null;
		try {
			ITypedRegion region = document.getPartition(offset);
			int partitionOffset = region.getOffset();
			scanner.setRange(document, partitionOffset, region.getLength());
			while ((token = scanner.nextToken()) != Token.EOF) {
				if (token.getData() instanceof TextAttribute) {
					TextAttribute text = (TextAttribute) token.getData();
					if (text.getForeground().getRGB().equals(
							IColorConstants.ATTRIBUTE)) {

						attributeName = document.get(scanner.getTokenOffset(),
								scanner.getTokenLength());
						if (attributeName != null) {
							existsAttrs.add(attributeName);
						}

					}
				}
			}
		} catch (BadLocationException e) {
			throw new SystemException(e);
		}
		return existsAttrs;
	}

	private ICompletionProposal[] getDefaultCompletionProposal() {
		String text = tokenString.getText();
		if (text == null || text.equals(""))
			return null;
		String replaceString = tokenString.getText() + "=\"\" ";
		return new ICompletionProposal[] { new CompletionProposal(
				replaceString, tokenString.getDocumentOffset(), tokenString
						.getLength(), text.length() + 1, getDefaultImage(),
				null, null, null) };
	}

	private static Image getDefaultImage() {
		Image contentImage = Activator.getImageDescriptor(
				LocaleMessage.getString("contentassit.icon")).createImage();
		return contentImage;
	}

	public static void log(String message) {
		File file = new File("aurora.log");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				CustomDialog.showErrorMessageBox("create log File failure");
				return;
			}
		}
		try {
			FileOutputStream os = new FileOutputStream(file, true);
			String time = Calendar.getInstance().getTime().toGMTString();
			os.write((time + "\t" + message + "\n").getBytes());
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}

	}

}