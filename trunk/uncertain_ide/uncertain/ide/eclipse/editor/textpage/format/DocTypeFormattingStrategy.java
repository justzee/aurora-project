/*
 * Created on Oct 11, 2004
 */
package uncertain.ide.eclipse.editor.textpage.format;


public class DocTypeFormattingStrategy extends DefaultFormattingStrategy
{

	public String format(String content, boolean isLineStart, String indentation, int[] positions)
	{
		return lineSeparator + content;
	}

}