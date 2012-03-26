package uncertain.composite;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMapParser;

public class CommentCompositeMapParser extends CompositeMapParser {

	public static final String SAX_NEWlINE = "&#xA;";

	String comment;

	public CommentCompositeMapParser(CompositeLoader composite_loader) {
		super(composite_loader);
	}

	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes atts) throws SAXException {

		// test if this is an xinclude instruction
		if (composite_loader.getSupportXInclude())
			if (localName.equals(INCLUDE_INSTRUCTION) && namespaceURI != null)
				if (namespaceURI.equals(XINCLUDE_URI)) {
					String href_target = atts.getValue(KEY_HREF);
					if (href_target == null)
						throw new SAXException(
								"No 'href' attribute set for an XInclude instruction");
					CompositeMap included;
					try {
						included = getCompositeLoader().load(href_target);
					} catch (IOException ex) {
						throw new SAXException(ex);
					}

					if (current_node == null)
						current_node = included;
					else {
						/*
						 * System.out.println(current_node.getClass());
						 * System.out.println(current_node.getName());
						 */
						current_node.addChild(included);
					}
					return;
				}
		if (name_processor != null)
			localName = name_processor.getElementName(localName);
		CompositeMap node = null;
		if (getCompositeLoader() != null)
			node = getCompositeLoader().createCompositeMap(
					(String) uri_mapping.get(namespaceURI), namespaceURI,
					localName);
		else
			node = new CompositeMap((String) uri_mapping.get(namespaceURI),
					namespaceURI, localName);
		addAttribs(node, atts);
		if (comment != null) {
			node.setComment(comment);
			comment = null;
		}

		/*
		 * if(last_locator!=null) node.setLocator(last_locator);
		 */
		if (current_node == null) {
			current_node = node;
		} else {
			current_node.addChild(node);
			push(current_node);
			current_node = node;
		}

	}
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// last_locator = null;
		if (comment != null) {
			current_node.setEndElementComment(comment);
			comment = null;
		}
		// test if this is an xinclude instruction
		if (getCompositeLoader().getSupportXInclude())
			if (localName.equals(INCLUDE_INSTRUCTION) && uri != null)
				if (uri.equals(XINCLUDE_URI)) {
					return;
				}

		if (node_stack.size() > 0)
			current_node = pop();
	}
	public void characters(char ch[], int start, int length) throws SAXException {
		if (ch == null)
			return;
		if (0 == length)
			return;
		if (current_node != null) {
			String t = current_node.getText();
			if (t != null)
				t += new String(ch, start, length);
			else
				t = new String(ch, start, length);
			t = handleNewLine(t);
			t = t.replaceAll(SAX_NEWlINE,"\n");
			current_node.setText(t);
		}
	}
	public CompositeMap parseStream(InputStream stream) throws SAXException, IOException {


		// using SAX parser shipped with JDK
		SAXParser parser = null;
		try {
			parser = parser_factory.newSAXParser();
		} catch (ParserConfigurationException ex) {
			throw new SAXException("error when creating SAXParser", ex);
		}
		parser.setProperty("http://xml.org/sax/properties/lexical-handler", this);

		stream = handleNewLineInAttribute(stream);
		
		parser.parse(stream, this);

		CompositeMap root = getRoot();
		if (getCompositeLoader().getSaveNamespaceMapping())
			root.setNamespaceMapping(saved_uri_mapping);
		return root;
	}
	private InputStream handleNewLineInAttribute(InputStream stream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
		String content = "";
		String line = br.readLine();
		while (line != null) {
			content += line + "\r\n";
			line = br.readLine();
		}
		stream = new ByteArrayInputStream(convertNewLine(content,0).getBytes("UTF-8"));
		return stream;
	}
	public static String convertNewLine(String fileContent,int index) {
	    // 匹配双引号间内容   
	    String pstr = "\"([^\"]*)\"";   
	    Pattern p = Pattern.compile(pstr);
	    String content = fileContent.substring(index);
	    Matcher m = p.matcher(content);
	    if(m.find()){
	    	String text = m.group();
	    	text = text.replaceAll("\n", SAX_NEWlINE);
	    	int count = 0;
	    	int fromIndex = 0;
	    	while((fromIndex = text.indexOf(SAX_NEWlINE,fromIndex))!= -1){
	    		count++;
	    		fromIndex = fromIndex+5;
	    	}
	    	fileContent = fileContent.substring(0, m.start()+index)+text+fileContent.substring(m.end()+index);
	    	index = m.end()+index+4*count;
	    }else{
	    	return fileContent;
	    }
	    return convertNewLine(fileContent,index);
	} 
	public void comment(char ch[], int start, int length) throws SAXException {
		if (ch == null)
			return;
		String separator = "-->";
		String now = new String(ch, start, length);
		now = now.replaceAll(SAX_NEWlINE,"\n");
		if (comment != null)
			comment += separator + now;
		else
			comment = now;
	}
	private String handleNewLine(String src) {
		if (src == null)
			return null;
		String result = src.replace("\r", "");
		result = result.replace("\n", "\r\n");
		return result;
	}
}
