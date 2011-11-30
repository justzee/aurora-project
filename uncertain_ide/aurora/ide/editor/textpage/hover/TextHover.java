package aurora.ide.editor.textpage.hover;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.builder.SxsdUtil;
import aurora.ide.editor.textpage.IColorConstants;
import aurora.ide.editor.textpage.quickfix.QuickAssistUtil;
import aurora.ide.editor.textpage.scanners.XMLTagScanner;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.search.core.Util;

public class TextHover extends DefaultTextHover implements ITextHoverExtension {
	private ISourceViewer sourceViewer;
	private static String style = "<style>body,table{ font-family:sans-serif; font-size:9pt; background:#FFFFE1; } table,td,th {border:1px solid #888 ;border-collapse:collapse;}</style>";
	private MarkerAnnotation lastAnno = null;
	private IDocument doc;
	private CompositeMap map;

	public TextHover(ISourceViewer sourceViewer) {
		super(sourceViewer);
		this.sourceViewer = sourceViewer;
	}

	public IInformationControlCreator getHoverControlCreator() {
		return new HoverInformationControlCreator(lastAnno);
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		String hover = getMarkerInfo(sourceViewer, hoverRegion);
		if (hover != null)
			return html(hover);

		try {
			doc = textViewer.getDocument();
			final int line = doc.getLineOfOffset(hoverRegion.getOffset());
			String word = doc.get(hoverRegion.getOffset(),
					hoverRegion.getLength());
			if (word == null || word.trim().length() == 0)
				return null;
			if (isAttributeValue(doc, line, hoverRegion.getOffset(),
					hoverRegion.getLength()))
				return html(word);
			try {
				map = CompositeMapUtil.loaderFromString(doc.get());
			} catch (Exception e) {
			}
			if (map == null)
				return null;
			CompositeMap cursorMap = QuickAssistUtil.findMap(map, doc,
					hoverRegion.getOffset());
			if (word.equals(cursorMap.getName())) {
				word = SxsdUtil.getHtmlDocument(cursorMap);
			} else {
				List<Attribute> list = null;
				try {
					list = SxsdUtil.getAttributesNotNull(cursorMap);
				} catch (Exception e) {
					word = e.getMessage();
				}
				if (list != null) {
					for (Attribute a : list) {
						if (word.equalsIgnoreCase(a.getName())) {
							word = a.getName() + "<br/>"
									+ SxsdUtil.notNull(a.getDocument());
							if (SxsdUtil.getTypeNameNotNull(
									a.getAttributeType()).length() > 0)
								word += "<br/>Type : "
										+ SxsdUtil.getTypeNameNotNull(a
												.getAttributeType());
						}
					}
				}
			}
			return html(word);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		IDocument doc = textViewer.getDocument();
		try {
			final int line = doc.getLineOfOffset(offset);
			int ls = doc.getLineOffset(line);
			int len = doc.getLineLength(line);

			String text = doc.get(ls, len);
			if (text == null || text.length() == 0)
				return super.getHoverRegion(textViewer, offset);
			int s = offset - ls, e = offset - ls;
			char c = text.charAt(s);
			if (isWordPart(c)) {
				while (s >= 0 && isWordPart(text.charAt(s)))
					s--;
				s++;
				while (e < text.length() && isWordPart(text.charAt(e)))
					e++;
				return new Region(ls + s, e - s);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return super.getHoverRegion(textViewer, offset);
	}

	private String getMarkerInfo(ISourceViewer sourceViewer, IRegion hoverRegion) {
		IAnnotationModel model = null;
		if (sourceViewer instanceof ISourceViewerExtension2) {
			ISourceViewerExtension2 extension = (ISourceViewerExtension2) sourceViewer;
			model = extension.getVisualAnnotationModel();
		} else
			model = sourceViewer.getAnnotationModel();
		if (model == null)
			return null;

		@SuppressWarnings("unchecked")
		Iterator<Annotation> e = model.getAnnotationIterator();
		while (e.hasNext()) {
			Annotation a = e.next();
			if (!(a instanceof MarkerAnnotation))
				continue;
			MarkerAnnotation ma = (MarkerAnnotation) a;
			lastAnno = ma;
			Position p = model.getPosition(ma);
			if (p != null
					&& p.overlapsWith(hoverRegion.getOffset(),
							hoverRegion.getLength())) {
				String msg = ma.getText();
				if (msg != null && msg.trim().length() > 0)
					return msg;
			}
		}
		lastAnno = null;
		return null;
	}

	public static String html(String str) {
		StringBuilder sb = new StringBuilder(5000);
		sb.append("<html><head>");
		sb.append(style);
		sb.append("</head><body>");
		sb.append(str.replace("\\n", "<br/>"));
		sb.append("</body></html>");
		return sb.toString();
	}

	private boolean isAttributeValue(IDocument doc, int line, int offset,
			int length) {
		try {
			XMLTagScanner scanner = Util.getXMLTagScanner();
			int lineoffset = doc.getLineOffset(line);
			int linelength = doc.getLineLength(line);
			scanner.setRange(doc, lineoffset, linelength);
			IToken token = Token.EOF;
			while ((token = scanner.nextToken()) != Token.EOF) {
				if (token.getData() instanceof TextAttribute) {
					TextAttribute text = (TextAttribute) token.getData();
					if (new Position(scanner.getTokenOffset(),
							scanner.getTokenLength()).overlapsWith(offset,
							length)) {
						if (text.getForeground().getRGB()
								.equals(IColorConstants.STRING)) {
							return true;
						}
						break;
					}
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isWordPart(char c) {
		return c == '-' || Character.isJavaIdentifierPart(c);
	}

}
