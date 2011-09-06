package aurora.ide.search.core;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import uncertain.schema.Element;
import uncertain.schema.IType;
import uncertain.schema.SimpleType;
import aurora.ide.bm.BMUtil;
import aurora.ide.editor.textpage.ColorManager;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.editor.textpage.scanners.XMLTagScanner;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.LoadSchemaManager;

public class Util {

	static private XMLTagScanner tagScanner;

	static public XMLTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			ColorManager manager = new ColorManager();
			tagScanner = new XMLTagScanner(manager);
			tagScanner.setDefaultReturnToken(new Token(new TextAttribute(
					manager.getColor(IColorConstants.TAG))));
		}
		return tagScanner;
	}

	static public IRegion getAttributeRegion(int offset, int length,
			String name, IDocument document) throws BadLocationException {
		XMLTagScanner scanner = getXMLTagScanner();
		IToken token = null;
		scanner.setRange(document, offset, length);
		while ((token = scanner.nextToken()) != Token.EOF) {
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				int tokenOffset = scanner.getTokenOffset();
				int tokenLength = scanner.getTokenLength();
				if (text.getForeground().getRGB().equals(
						IColorConstants.ATTRIBUTE)
						&& name.equals(document.get(tokenOffset, tokenLength))) {
					return new Region(tokenOffset, tokenLength);
				}
			}
		}
		return null;
	}

	static public IRegion getFirstWhitespaceRegion(int offset, int length,
			IDocument document) throws BadLocationException {
		XMLTagScanner scanner = getXMLTagScanner();
		IToken token = null;
		IRegion c_region = null;
		scanner.setRange(document, offset, length);
		while ((token = scanner.nextToken()) != Token.EOF) {
			int tokenOffset = scanner.getTokenOffset();
			int tokenLength = scanner.getTokenLength();
			if (Token.WHITESPACE.equals(token)) {
				c_region = new Region(tokenOffset, tokenLength);
			}
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				if (text.getForeground().getRGB().equals(
						IColorConstants.ATTRIBUTE)) {
					return c_region;
				}
			}
		}
		return null;
	}

	public static Object getReferenceModelPKG(CompositeMap map) {
		if (map == null)
			return null;
		Element element = LoadSchemaManager.getSchemaManager().getElement(map);
		if (element != null) {
			List attrib_list = element.getAllAttributes();
			for (Iterator it = attrib_list.iterator(); it.hasNext();) {
				Attribute attrib = (Attribute) it.next();
				IType attributeType = attrib.getAttributeType();
				boolean referenceOf = isBMReference(attributeType);
				if (referenceOf) {
					Object data = map.get(attrib.getName());
					return data;
				}
			}
		}
		return null;
	}

	public static boolean isBMReference(IType attributeType) {
		if (attributeType instanceof SimpleType) {
			return AbstractSearchService.bmReference
					.equals(((SimpleType) attributeType)
							.getReferenceTypeQName());
		}
		return false;
	}

	private static class WebInfFinder implements IResourceVisitor {
		private IFolder folder = null;

		public boolean visit(IResource resource) throws CoreException {
			if (folder != null)
				return false;
			if (resource.getType() == IResource.FOLDER) {
				if ("WEB-INF".equals(resource.getName())) {
					folder = (IFolder) resource;
					return false;
				}
				return true;
			}
			if (resource.getType() == IResource.FILE) {
				return false;
			}

			return true;
		}

		public IFolder getFolder() {
			return folder;
		}

	}

	public static IContainer findWebInf(IResource resource) {

		if (null == resource) {
			return null;
		}
		IProject project = resource.getProject();
		try {
			WebInfFinder finder = new WebInfFinder();
			project.accept(finder);
			return finder.getFolder();
		} catch (CoreException e) {

		}
		return null;
	}

	public static IResource getScope(IFile sourceFile) {
		if (sourceFile == null)
			return null;
		IProject project = sourceFile.getProject();
		IContainer scope = Util.findWebInf(sourceFile);
		if (scope == null) {
			scope = project;
		} else {
			scope = scope.getParent();
		}
		return scope;
	}

	public static IFile findBMFile(CompositeMap map) {
		Object pkg = Util.getReferenceModelPKG(map);
		if (pkg == null) {
			pkg = Util.getReferenceModelPKG(map.getParent());
		}
		if (pkg == null) {
			pkg = Util.getReferenceModelPKG(map.getParent().getParent());
		}
		if (pkg instanceof String) {
			return findBMFileByPKG(pkg);
		}
		return null;
	}

	public static IFile findBMFileByPKG(Object pkg) {
		try {
			IResource file = BMUtil.getBMResourceFromClassPath((String) pkg);
			if (file instanceof IFile
					&& "bm".equalsIgnoreCase(file.getFileExtension()))
				return (IFile) file;
		} catch (ApplicationException e) {

		}
		return null;
	}

	public static IFile findScreenFile(IFile file, Object pkg) {
		if (pkg instanceof String) {
			IContainer webInf = findWebInf(file);
			if (webInf == null)
				return null;
			IResource webRoot = webInf.getParent();
			IContainer parent = file.getParent();
			IPath parentPath = parent.getFullPath();
			IPath rootPath = webRoot.getFullPath();
			Path path = new Path((String) pkg);
			IPath relativePath = parentPath.makeRelativeTo(rootPath);
			boolean prefixOf = relativePath.isPrefixOf(path);
			if (prefixOf) {
				// fullpath
				IPath sourceFilePath = rootPath.append(path);
				IFile sourceFile = file.getProject().getParent().getFile(
						sourceFilePath);
				if (sourceFile.exists())
					return sourceFile;
			} else {
				// relativepath
				IFile sourceFile = parent.getFile(path);
				if (sourceFile.exists())
					return sourceFile;
			}
		}
		return null;
	}
}
