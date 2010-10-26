/*
 * Created on Oct 11, 2004
 */
package uncertain.ide.eclipse.editor.textpage.contentassist;

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
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Image;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.textpage.IXMLColorConstants;
import uncertain.ide.eclipse.editor.textpage.scanners.XMLTagScanner;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.Namespace;
import uncertain.schema.Schema;

public class TagContentAssistProcessor implements IContentAssistProcessor {

	private XMLTagScanner scanner;

	public TagContentAssistProcessor(XMLTagScanner scanner) {
		super();
		this.scanner = scanner;

	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {

		IDocument document = viewer.getDocument();
		QualifiedName qn = null;
		TextInfo currentText = currentText(document, offset);
		try {
			qn = getElementQualifiedName(offset, document);
			if(qn.getPrefix()!=null&&qn.getNameSpace()==null){
				return getNoNameSpaceProposal(qn,currentText);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Element element = LoadSchemaManager.getSchemaManager().getElement(qn);
		if (element == null)
			return null;
		List allAttributes = element.getAllAttributes();

		ICompletionProposal[] result = new ICompletionProposal[allAttributes
				.size()];
		int i = 0;
		for (Iterator iter = allAttributes.iterator(); iter.hasNext();) {
			Attribute attr = (Attribute) iter.next();
			String name = attr.getName();
			String attributeDocument = attr.getDocument();
			String description = name;
			if (attributeDocument != null)
				description = formateAttributeName(name) + " - "
						+attributeDocument;
			Image contentImage = Activator.getImageDescriptor(LocaleMessage.getString("contentassit.icon")).createImage();
			result[i++] = new CompletionProposal(name,
					currentText.documentOffset, currentText.text.length(),
					description.length(), contentImage, description, null, attributeDocument);

		}
		return result;
	}
	private ICompletionProposal[] getNoNameSpaceProposal(QualifiedName qualifiedName,TextInfo currentText){
		ICompletionProposal[] result = new ICompletionProposal[1];
		String name = "xmlns:"+qualifiedName.getPrefix()+"=\"\"";
		result[0] = new CompletionProposal(name,
				currentText.documentOffset, 0,
				name.length()-1, null, name+"  - "+LocaleMessage.getString("namespace.of.this.element"), null, LocaleMessage.getString("please.define.the.namespace.first"));
		return result;
	}
	public Namespace[] getNameSpaces(Map namespaceToPrefix) {
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
			namespaces[i] = namespace;
		}
		return namespaces;
	}

	public String formateAttributeName(String attributeName) {
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

	private TextInfo currentText(IDocument document, int documentOffset) {

		try {

			ITypedRegion region = document.getPartition(documentOffset);

			int partitionOffset = region.getOffset();
			int partitionLength = region.getLength();

			int index = documentOffset - partitionOffset;

			String partitionText = document.get(partitionOffset,
					partitionLength);


			char c = partitionText.charAt(index);

			if (Character.isWhitespace(c)
					|| Character.isWhitespace(partitionText.charAt(index - 1))) {
				return new TextInfo("", documentOffset, true);
			} else if (c == '<') {
				return new TextInfo("", documentOffset, true);
			} else {
				int start = index;
				c = partitionText.charAt(start);

				while (!Character.isWhitespace(c) && c != '<' && start >= 0) {
					start--;
					c = partitionText.charAt(start);
				}
				start++;

				int end = index;
				c = partitionText.charAt(end);

				while (!Character.isWhitespace(c) && c != '>' && c != '/'
						&& end < partitionLength - 1) {
					end++;
					c = partitionText.charAt(end);
				}

				String substring = partitionText.substring(start, end);
				return new TextInfo(substring, partitionOffset + start, false);

			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return null;
	}

private QualifiedName getElementQualifiedName(int documentOffset,
			IDocument document) throws Exception {
		String prefix = null;
		String name = null;
		String uri = null;
		ITypedRegion region = document.getPartition(documentOffset);
		int partitionOffset = region.getOffset();
		scanner.setRange(document, partitionOffset, region.getLength());
		IToken token = null;
		while ((token = scanner.nextToken()) != Token.EOF) {
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				if (text.getForeground().getRGB().equals(
						IXMLColorConstants.TAG_NAME)) {
					String tagName = document.get(scanner.getTokenOffset(), scanner
							.getTokenLength());
					String[] splits = tagName.split(":");
					if (splits.length==2) {
						QualifiedName qn = null;
						try {
							qn = getQualifiedName(document,tagName);
						} catch (Exception e) {
							qn = null;
						}
						if(qn != null)
							return qn;
						prefix = splits[0];
						name = splits[1];
						uri =  getElementUrl(document,scanner,splits[0]);
					}else{
						name = tagName;
					}
					break;
				}

			}

		}
		return new QualifiedName(prefix,uri,name);
	}
	private QualifiedName getQualifiedName(IDocument document,String tagName) throws Exception{
		 CompositeLoader csloader = new CompositeLoader();
		 CompositeMap root = csloader.loadFromString(document.get());
		 Map namespace_mapping = CompositeUtil.getPrefixMapping(root);
		 Schema schema = new Schema();
		 Namespace[] ns = getNameSpaces(namespace_mapping);
		 schema.addNameSpaces(ns);
		 QualifiedName qn = schema.getQualifiedName(tagName);
		 return qn;
	}
	private String getElementUrl(IDocument document, XMLTagScanner scanner,
			String prefix) {
		IToken token = null;
		String attributeName = null;
		String namespace = "xmlns:";
		while ((token = scanner.nextToken()) != Token.EOF) {
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				if (text.getForeground().getRGB().equals(
						IXMLColorConstants.ATTRIBUTE)) {
					try {
						attributeName = document.get(scanner.getTokenOffset(),
								scanner.getTokenLength());
					} catch (BadLocationException e) {
						throw new RuntimeException(e);
					}
					if (attributeName.startsWith(namespace)) {
						String[] xmlns = attributeName.split(":");
						if (xmlns[1].equals(prefix)) {
							return getNextAttributeContent(document,scanner);
						}
					}
				}
			}
		}
		return null;
	}

	private String getNextAttributeContent(IDocument document,
			XMLTagScanner scanner) {
		IToken token = null;
		while ((token = scanner.nextToken()) != Token.EOF) {
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				if (text.getForeground().getRGB().equals(
						IXMLColorConstants.STRING)) {
					try {
						return document.get(scanner.getTokenOffset()+1,
								scanner.getTokenLength()-2);
					} catch (BadLocationException e) {
						throw new RuntimeException(e);
					}
				}else if(IXMLColorConstants.ATTRIBUTE.equals(text.getForeground().getRGB())){
						return null;
					}
			}
		}
	  return null;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	static class TextInfo {
		TextInfo(String text, int documentOffset, boolean isWhiteSpace) {
			this.text = text;
			this.isWhiteSpace = isWhiteSpace;
			this.documentOffset = documentOffset;
		}

		String text;

		boolean isWhiteSpace;

		int documentOffset;
	}

}