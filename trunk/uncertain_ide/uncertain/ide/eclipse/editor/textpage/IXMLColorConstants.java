package uncertain.ide.eclipse.editor.textpage;

import org.eclipse.swt.graphics.RGB;

public interface IXMLColorConstants
{

	RGB XML_COMMENT = new RGB(56, 94 ,15 );
	RGB PROC_INSTR = new RGB(65, 105, 225 );
	RGB DOCTYPE = new RGB(0, 150, 150);
	RGB STRING = new RGB(0 ,0 ,255  );
	RGB DEFAULT = new RGB(0, 0, 0);
	RGB TAG = new RGB(0, 0, 128);

	//enhancements
	RGB ESCAPED_CHAR = new RGB(128, 128, 0);
	RGB CDATA = new RGB(0, 128, 128);
	RGB CDATA_TEXT = new RGB(0,0,0 );
	RGB TAG_NAME = new RGB(0,128,128);
	RGB ATTRIBUTE = new RGB(135, 38, 87 );//(128,0,0 );
	
	//js
	RGB KEYWORD= new RGB(86, 0, 191);
	RGB TYPE= new RGB(0, 0, 128);
	RGB SINGLE_LINE_COMMENT= new RGB(56, 94 ,15);
}