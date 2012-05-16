package aurora.ide.editor.outline;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.jface.text.Region;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class OutlineParser extends DefaultHandler2 {

	private OutlineTree root;
	private OutlineTree tree;
	private int offset = 0;
	private String source = null;
	private Stack<OutlineTree> stack = new Stack<OutlineTree>();

	public OutlineParser(String source) {
		this.setSource(source);
		root = new OutlineTree();
		stack.add(root);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tree = new OutlineTree();
		stack.peek().add(tree);
		stack.push(tree);
		tree.setText(qName);
		tree.setOther(getValue(attributes));
		offset = source.indexOf(qName, offset);
		tree.setStartRegion(new Region(offset, qName.length()));
		if (source.indexOf("/>", offset) > source.indexOf("<", offset)) {
			offset += qName.length();
		} else {
			int start = source.lastIndexOf("<", offset);
			tree.setRegion(new Region(start, source.indexOf("/>", offset) + 2 - start));
		}
	}

	@Override
	public void comment(char[] ch, int start, int length) throws SAXException {
		offset = source.indexOf("-->", offset);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		offset = source.indexOf(qName, offset);
		stack.pop().setEndRegion(new Region(offset, qName.length()));
		offset += qName.length();
	}

	@Override
	public void endDocument() throws SAXException {
		source = null;
		stack.clear();
	}

	public OutlineTree getTree() {
		return root;
	}

	private String getValue(Attributes attributes) {
		String[] values = { "id", "name", "type" };
		for (int i = 0; i < attributes.getLength(); i++) {
			for (String s : values) {
				if (s.equalsIgnoreCase(attributes.getQName(i))) {
					return "(" + attributes.getValue(attributes.getQName(i)) + ")";
				}
			}
		}
		return "";
	}

	public void parser() throws ParserConfigurationException, SAXException, IOException {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.setProperty("http://xml.org/sax/properties/lexical-handler", this);
		InputStream is = new ByteArrayInputStream(source.getBytes());
		try {
			parser.parse(is, this);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
