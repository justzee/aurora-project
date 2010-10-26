package uncertain.ide.eclipse.editor.textpage;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import uncertain.ide.eclipse.editor.textpage.scanners.XMLPartitionScanner;
import uncertain.ide.eclipse.editor.textpage.scanners.XMLTagScanner;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;

public class BMFileHyperlinkDetector implements IHyperlinkDetector {
	
	XMLTagScanner scanner;
	Region columnValue ;
	public BMFileHyperlinkDetector() {
		ColorManager colorManager = new ColorManager();
		scanner = new XMLTagScanner(colorManager);
		scanner.setDefaultReturnToken(new Token(new TextAttribute(
				colorManager.getColor(IXMLColorConstants.TAG))));
	}
	
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		// get doc
		IDocument doc = textViewer.getDocument();
		
		try {
			String columnName =  getColumnName(region.getOffset(),doc);
			if(columnName != null && (columnName.toLowerCase().indexOf("model") != -1)||columnName.toLowerCase().indexOf("extend") != -1){
				return new IHyperlink[] {
						new BMFileHyperlink(columnValue,textViewer)};
				}
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		return null;
	}
	private String getColumnName(int documentOffset,
			IDocument document) throws Exception {
		String columnName = null;
		ITypedRegion region = document.getPartition(documentOffset);
		if(!XMLPartitionScanner.XML_START_TAG.equals(region.getType()))
			return null;
		int partitionOffset = region.getOffset();
		scanner.setRange(document, partitionOffset, region.getLength());
		IToken token = null;
		while ((token = scanner.nextToken()) != Token.EOF) {
			int offset = scanner.getTokenOffset();
			int length = scanner.getTokenLength();
			if(offset <=documentOffset && (offset+length)>=documentOffset){
				if (token.getData() instanceof TextAttribute){
					TextAttribute text = (TextAttribute) token.getData();
					if (text.getForeground().getRGB().equals(
							IXMLColorConstants.STRING)) {
						columnValue = new Region(scanner.getTokenOffset()+1, scanner
								.getTokenLength()-2);
					}
				}
				break;
			}
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				if (text.getForeground().getRGB().equals(
						IXMLColorConstants.ATTRIBUTE)) {
					columnName = document.get(scanner.getTokenOffset(), scanner
							.getTokenLength());
				}

			}

		}
		return columnName;
	}
}
