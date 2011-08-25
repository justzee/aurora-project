package aurora.search.action;

import helpers.ApplicationException;
import helpers.CompositeMapLocatorParser;
import helpers.LoadSchemaManager;
import helpers.SystemException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Element;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.search.core.AbstractSearchService;
import aurora.search.core.Util;
import aurora.search.reference.BMFieldReferenceQuery;
import bm.BMUtil;
import editor.textpage.ColorManager;
import editor.textpage.IColorConstants;
import editor.textpage.TextPage;
import editor.textpage.scanners.XMLTagScanner;

public class FieldReferenceAction implements IEditorActionDelegate {

	private IFile sourceFile;
	private TextPage textPage;
	private XMLTagScanner tagScanner;
	private TextSelection selection;

	private class Attribute {
		private String name;
		private String value;
	}

	public FieldReferenceAction() {
	}

	public void run(IAction action) {
		IProject project = sourceFile.getProject();

		IContainer scope = Util.findWebInf(sourceFile);
		if (scope == null) {
			scope = project;
		} else {
			scope = scope.getParent();
		}
		BMFieldReferenceQuery query = new BMFieldReferenceQuery(scope,
				sourceFile, selection.getText());
		NewSearchUI.runQueryInBackground(query);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		boolean isEnable = checkSelection(selection);
		action.setEnabled(isEnable);
	}

	private boolean checkSelection(ISelection sel) {
		if (textPage == null) {
			return false;
		}
		if (!(sel instanceof TextSelection)) {
			return false;
		}
		selection = (TextSelection) sel;
		if (selection.getStartLine() != selection.getEndLine()) {
			return false;
		}
		Attribute attribute = getAttribute(selection);
		if (attribute == null) {
			return false;
		}
		sourceFile = getSourceFile(attribute, selection);
		if (sourceFile == null) {
			return false;
		}

		return true;
	}

	private IFile getSourceFile(Attribute att, TextSelection selection) {
		String content = textPage.getContent();
		try {
			CompositeMap map = locateCompositeMap(content,
					selection.getStartLine());
			Object value = map.get(att.name);
			if (att.value.equals(value)) {
				Element element = LoadSchemaManager.getSchemaManager()
						.getElement(map);
				if (element != null) {
					List attrib_list = element.getAllAttributes();
					for (Iterator it = attrib_list.iterator(); it.hasNext();) {
						uncertain.schema.Attribute attrib = (uncertain.schema.Attribute) it
								.next();
						if (att.name.equals(attrib.getName())) {
							IType attributeType = attrib.getAttributeType();
							if (attributeType instanceof SimpleType) {
								QualifiedName referenceTypeQName = ((SimpleType) attributeType)
										.getReferenceTypeQName();
								if (AbstractSearchService.foreignFieldReference
										.equals(referenceTypeQName)) {

									return getFile(map.getParent());

								}
								if (AbstractSearchService.localFieldReference
										.equals(referenceTypeQName)) {
									return getFile();
								}
							}
						}
					}
				}

			}

		} catch (ApplicationException e) {
		}
		return null;
	}

	private IFile getFile(CompositeMap map) {
		Object pkg = Util.getReferenceModelPKG(map);
		if (pkg == null) {
			pkg = Util.getReferenceModelPKG(map.getParent());
		}
		if (pkg instanceof String) {
			try {
				IResource file = BMUtil
						.getBMResourceFromClassPath((String) pkg);
				if (file instanceof IFile
						&& "bm".equalsIgnoreCase(file.getFileExtension()))
					return (IFile) file;
			} catch (ApplicationException e) {

			}
		}
		return null;
	}

	private Attribute getAttribute(TextSelection selection) {
		IDocument document = textPage.getInputDocument();
		int offset = selection.getOffset();
		int length = selection.getLength();

		String name = null;
		try {
			XMLTagScanner scanner = getXMLTagScanner();
			IToken token = null;
			ITypedRegion region = document.getPartition(offset);
			scanner.setRange(document, region.getOffset(), region.getLength());
			while ((token = scanner.nextToken()) != Token.EOF) {
				if (token.getData() instanceof TextAttribute) {
					TextAttribute text = (TextAttribute) token.getData();
					if (text.getForeground().getRGB()
							.equals(IColorConstants.ATTRIBUTE)) {
						name = document.get(scanner.getTokenOffset(),
								scanner.getTokenLength());
					}
				}
				if (scanner.getTokenOffset() == offset - 1
						&& (scanner.getTokenLength()) == length + 2) {
					if (token.getData() instanceof TextAttribute) {
						TextAttribute text = (TextAttribute) token.getData();
						if (text.getForeground().getRGB()
								.equals(IColorConstants.STRING)) {
							if (name == null)
								return null;
							Attribute attribute = new Attribute();
							attribute.name = name;
							attribute.value = selection.getText();
							return attribute;
						}
					}
				}
			}
		} catch (BadLocationException e) {
		}

		return null;
	}

	private CompositeMap locateCompositeMap(String content, int line)
			throws ApplicationException {
		try {
			CompositeMapLocatorParser parser = new CompositeMapLocatorParser();
			InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			CompositeMap cm = parser.getCompositeMapFromLine(is, line);
			return cm;
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		} catch (SAXException e) {
			throw new ApplicationException("请检查此文件格式是否正确.", e);
		} catch (IOException e) {
			throw new ApplicationException("请检查此文件格式是否正确.", e);
		}
	}

	private XMLTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			ColorManager manager = new ColorManager();
			tagScanner = new XMLTagScanner(manager);
			tagScanner.setDefaultReturnToken(new Token(new TextAttribute(
					manager.getColor(IColorConstants.TAG))));
		}
		return tagScanner;
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof TextPage) {
			textPage = (TextPage) targetEditor;
		} else {
			textPage = null;
		}
	}

	public IFile getFile() {
		IFile file = textPage.getFile();
		if ("bm".equalsIgnoreCase(file.getFileExtension())) {
			return textPage == null ? null : file;
		} else {
			return null;
		}
	}
}
