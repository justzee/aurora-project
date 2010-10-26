package uncertain.ide.eclipse.editor.textpage;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class XMLDoubleClickStrategy implements ITextDoubleClickStrategy {
	protected ITextViewer fText;
	StyleRange[] oldStyles;
	boolean register = false; 
	String oldContent;
	Color highLight= Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
	public void doubleClicked(final ITextViewer part) {
		if(!register){
			part.getTextWidget().addCaretListener(new CaretListener() {
				public void caretMoved(CaretEvent event) {
					if(oldStyles == null)
						return ;
					String newContent = part.getTextWidget().getText();
					if(oldContent != null&& !oldContent.equals(newContent)){
						StyleRange[] nowStyleRanges = part.getTextWidget().getStyleRanges();
						for(int i=0;i<nowStyleRanges.length;i++){
							StyleRange sr = nowStyleRanges[i];
							if(highLight.equals(sr.background)){
								sr.background = null;
								fText.getTextWidget().replaceStyleRanges(sr.start, sr.length,new StyleRange[]{sr});
							}
						}
					}else{
						if(oldStyles != null){
							part.getTextWidget().setStyleRanges(oldStyles);
						}
					}
					oldStyles = null;
				}

			});

		}
		oldContent = part.getTextWidget().getText();
		
		int pos = part.getSelectedRange().x;

		if (pos < 0)
			return;

		fText = part;

//		if (!selectComment(pos)) {
			selectWord(pos);
//		}
//		part.getDocument().ge
	}
	protected boolean selectComment(int caretPos) {
		IDocument doc = fText.getDocument();
		int startPos, endPos;

		try {
			int pos = caretPos;
			char c = ' ';

			while (pos >= 0) {
				c = doc.getChar(pos);
				if (c == '\\') {
					pos -= 2;
					continue;
				}
				if (c == Character.LINE_SEPARATOR || c == '\"')
					break;
				--pos;
			}

			if (c != '\"')
				return false;

			startPos = pos;

			pos = caretPos;
			int length = doc.getLength();
			c = ' ';

			while (pos < length) {
				c = doc.getChar(pos);
				if (c == Character.LINE_SEPARATOR || c == '\"')
					break;
				++pos;
			}
			if (c != '\"')
				return false;

			endPos = pos;

			int offset = startPos + 1;
			int len = endPos - offset;
			fText.setSelectedRange(offset, len);
			return true;
		} catch (BadLocationException x) {
		}

		return false;
	}
	protected boolean selectWord(int caretPos) {

		IDocument doc = fText.getDocument();
		int startPos, endPos;

		try {

			int pos = caretPos;
			char c;

			while (pos >= 0) {
				c = doc.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
					break;
				--pos;
			}

			startPos = pos;

			pos = caretPos;
			int length = doc.getLength();

			while (pos < length) {
				c = doc.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
					break;
				++pos;
			}

			endPos = pos;
			selectRange(startPos, endPos);
			int offset = startPos + 1;
			int wordLength = endPos - offset;
			String keyword = fText.getDocument().get(offset, wordLength);
			setHighLight(keyword);
			return true;

		} catch (BadLocationException x) {
		}

		return false;
	}
	private void setHighLight(String keyword){
		if(oldStyles != null){
			fText.getTextWidget().setStyleRanges(oldStyles);

		}else{
			oldStyles = fText.getTextWidget().getStyleRanges();
		}
		String line = fText.getTextWidget().getText();
		int cursor = -1;
		while ((cursor = line.indexOf(keyword, cursor + 1)) >= 0) {
			StyleRange[] srs = fText.getTextWidget().getStyleRanges(cursor, keyword.length());
			for(int i=0;i<srs.length;i++){
				srs[i].background = highLight;
			}
			fText.getTextWidget().replaceStyleRanges(cursor, keyword.length(),srs);
		}
	}
	protected void selectRange(int startPos, int stopPos) {
		int offset = startPos + 1;
		int length = stopPos - offset;
		fText.setSelectedRange(offset, length);
	}
	public static void main(String[] args){
		System.out.println(Character.isJavaIdentifierPart('.'));
	}
}