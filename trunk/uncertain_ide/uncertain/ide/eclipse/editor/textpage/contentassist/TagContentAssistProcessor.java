package uncertain.ide.eclipse.editor.textpage.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import uncertain.ide.eclipse.editor.textpage.scanners.XMLPartitionScanner;
import uncertain.ide.eclipse.editor.textpage.scanners.XMLTagScanner;
import uncertain.ide.help.CustomDialog;

public class TagContentAssistProcessor implements IContentAssistProcessor {

	private XMLTagScanner scanner;

	public TagContentAssistProcessor(XMLTagScanner scanner) {
		super();
		this.scanner = scanner;

	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {

		IDocument document = viewer.getDocument();
		IContentAssistStrategy strategy = null;
		try {
			strategy = getAssistStrategy(document, offset);
			if (strategy == null)
				return null;
			return strategy.computeCompletionProposals(viewer, offset);
		} catch (BadLocationException e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		return null;
	}

	private IContentAssistStrategy getAssistStrategy(IDocument document,
			int documentOffset) throws BadLocationException {
		ITypedRegion region = document.getPartition(documentOffset);
		if (!XMLPartitionScanner.XML_START_TAG.equals(region.getType()))
			return null;
		int partitionOffset = region.getOffset();
		int partitionLength = region.getLength();
		char beginChar = ' ';
		int index = documentOffset - partitionOffset;
		String partitionText = document.get(partitionOffset, partitionLength);
		int start = index - 1;
		if(start < 0)
			return null;
		char c = partitionText.charAt(start);
		while (beginChar(c, start)) {
			start--;
			c = partitionText.charAt(start);
		}
		start++;
		beginChar = c;

		int end = index;
		c = partitionText.charAt(end);
		while (endChar(c, end, partitionLength)) {
			end++;
			c = partitionText.charAt(end);
		}
		String substring = partitionText.substring(start, end);
		if (beginChar == '"' || c == '"') {
			return null;
		}
		TokenString ts = new TokenString(substring, partitionOffset + start,
				documentOffset);
		if (beginChar == '<') {
			return new ChildStrategy(ts);
		}
		return new AttributeStrategy(scanner, ts);

		// }

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

	private boolean beginChar(char c, int start) {
		return !Character.isWhitespace(c) && c != '<' && start >= 0 && c != '"';
	}

	private boolean endChar(char c, int end, int partitionLength) {
		return !Character.isWhitespace(c) && c != '>' && c != '/' && c != '<'
				&& (end < partitionLength - 1) && c != '"';
	}

}