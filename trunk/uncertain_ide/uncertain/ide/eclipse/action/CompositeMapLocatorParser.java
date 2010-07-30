/*
 * CompositeMapHandle.java
 *
 * Created on 2002��1��5��, ����2:12
 */

package uncertain.ide.eclipse.action;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uncertain.composite.CompositeMap;
import uncertain.composite.NameProcessor;

/**
 * 
 * @author jinxiao.lin
 * @version
 */
public class CompositeMapLocatorParser extends DefaultHandler {

	private final int lineToCompositeMap = 0;
	private final int compositeMapToLine = 1;
	private int function;
	private CompositeMap targetCompositeMap;
	private int line;
	private boolean serchfinished = false;

	/**
	 * @param composite_loader
	 */
	public CompositeMapLocatorParser() {
		super();
	}

	/**
	 * partly supports W3C XInclude specification <xi:include
	 * xmlns:xi="http://www.w3.org/2001/XInclude" href="new_document.xml" />
	 */
	public static final String INCLUDE_INSTRUCTION = "include";
	public static final String XINCLUDE_URI = "http://www.w3.org/2001/XInclude";
	public static final String KEY_HREF = "href";

	CompositeMap current_node = null;

	LinkedList node_stack = new LinkedList();

	// namespace url -> prefix mapping
	Map uri_mapping = new HashMap();

	// save all namespace url -> prefix mapping
	Map saved_uri_mapping;

	// prefix -> namespace mapping
	Map namespace_mapping = new HashMap();

	NameProcessor name_processor;

	Locator locator;

	// boolean support_xinclude = false;

	// the default SAXParserFactory instance

	private HashMap compositeMapPositions;
	static SAXParserFactory parser_factory = SAXParserFactory.newInstance();
	static {
		try {
			parser_factory.setNamespaceAware(true);
			parser_factory.setValidating(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void push(CompositeMap node) {
		node_stack.addFirst(node);
	}

	CompositeMap pop() {
		CompositeMap node = (CompositeMap) node_stack.getFirst();
		node_stack.removeFirst();
		return node;
	}

	void addAttribs(CompositeMap node, Attributes attribs) {
		// Class nodeCls = node.getClass();
		for (int i = 0; i < attribs.getLength(); i++) {
			String attrib_name = attribs.getQName(i);
			/** @todo Add attribute namespace support */
			// String uri = attribs.getURI(i);
			if (name_processor != null)
				attrib_name = name_processor.getAttributeName(attrib_name);
			node.put(attrib_name, attribs.getValue(i));
		}
	}

	/** handles for SAX */

	public void startDocument() {
		current_node = null;
		node_stack.clear();
		uri_mapping.clear();
	}

	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes atts) throws SAXException {

		int lineNumber = locator.getLineNumber() - 1;

		if (name_processor != null)
			localName = name_processor.getElementName(localName);
		CompositeMap node = null;
		node = new CompositeMap((String) uri_mapping.get(namespaceURI),
				namespaceURI, localName);
		addAttribs(node, atts);

		if (current_node == null) {
			current_node = node;
		} else {
			current_node.addChild(node);
			push(current_node);
			current_node = node;
		}
		// System.out.println("lineNumber:" + lineNumber + " current_node:"
		// + current_node.toXML());
		if (!serchfinished && function == lineToCompositeMap) {
			if (lineNumber >= line) {
				targetCompositeMap = current_node;
				serchfinished = true;
			}
		}
		/*
		 * if (!serchfinished && function == compositeMapToLine &&
		 * current_node.equals(targetCompositeMap)) { line = lineNumber;
		 * serchfinished = true; } current_node.setStartLineNumber(lineNumber);
		 */
		if (function == compositeMapToLine)
			compositeMapPositions.put(current_node, new Integer(lineNumber));
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (node_stack.size() > 0) {
			int lineNumber = locator.getLineNumber() - 1;
			if (!serchfinished && function == lineToCompositeMap) {
				if (lineNumber >= line) {
					targetCompositeMap = current_node;
					serchfinished = true;
				}
			}
			current_node = pop();
		}
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		uri_mapping.put(uri, prefix);
		namespace_mapping.put(prefix, uri);
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		uri_mapping.remove(prefix);
	}

	public void characters(char ch[], int start, int length)
			throws SAXException {
		if (ch == null)
			return;
		if (start == length)
			return;
		if (current_node != null) {
			String t = current_node.getText();
			if (t != null)
				t += new String(ch, start, length);
			else
				t = new String(ch, start, length);
			current_node.setText(t);
		}
	}

	/** end handles */
	/*
	 * public CompositeMapParser() {
	 * 
	 * }
	 */

	/*
	 * public CompositeMapParser(CompositeMapParser prototype) { this();
	 * copySettings(prototype); }
	 */

	/*
	 * public CompositeMapParser( CompositeLoader loader){ if( loader == null)
	 * return; setCompositeLoader(loader); }
	 */

	/** set a new INameProcessor */
	/*
	 * public void setNameProcessor(NameProcessor processor) {
	 * this.name_processor = processor; }
	 */

	/** get root CompositeMap parsed */
	public CompositeMap getRoot() {
		return current_node;
	}

	private void parseStream(InputStream stream) throws SAXException,
			IOException {

		// using SAX parser shipped with JDK
		SAXParser parser = null;
		try {
			parser = parser_factory.newSAXParser();
		} catch (ParserConfigurationException ex) {
			throw new SAXException("error when creating SAXParser", ex);
		}
		parser.parse(stream, this);
		// if( getCompositeLoader().getSaveNamespaceMapping())
		// root.setNamespaceMapping(saved_uri_mapping);
	}

	public int LocateCompositeMapLine(InputStream stream,
			CompositeMap targetCompositeMap) throws SAXException, IOException {
		function = compositeMapToLine;
		this.targetCompositeMap = targetCompositeMap;
		compositeMapPositions = new HashMap();
		parseStream(stream);
		Object lineOBject = compositeMapPositions.get(targetCompositeMap);
		if (lineOBject != null) {
			line = ((Integer) lineOBject).intValue();
		}
		return line;

	}

	public CompositeMap getCompositeMapFromLine(InputStream stream, int line)
			throws SAXException, IOException {
		function = lineToCompositeMap;
		this.line = line;
		parseStream(stream);
		return targetCompositeMap;
	}

	public void clear() {
		current_node = null;
		if (node_stack != null)
			node_stack.clear();
		if (uri_mapping != null)
			uri_mapping.clear();
		name_processor = null;
	}

	/*
	 * public static CompositeMapParser createInstance(CompositeLoader loader) {
	 * CompositeMapParser parser = new CompositeMapParser(loader); return
	 * parser; }
	 */

	/*
	 * public Map getNamespaceMapping(){ return namespace_mapping; }
	 */

	/*
	 * public void setDocumentLocator(Locator locator) { last_locator = locator;
	 * }
	 */

	/*
	 * 
	 * public static CompositeMap parse( InputStream stream, CompositeLoader
	 * loader, NameProcessor processor ) throws SAXException, IOException {
	 * CompositeMapParser parser = null; try{ parser = createInstance(loader,
	 * processor); return parser.parseStream(stream); }finally{ if(parser!=null)
	 * parser.clear(); } }
	 * 
	 * public static CompositeMap parse( InputStream stream, CompositeLoader
	 * loader) throws SAXException, IOException { return parse( stream, loader,
	 * null); }
	 * 
	 * public static CompositeMap parse( InputStream stream, NameProcessor
	 * processor ) throws SAXException, IOException { return parse(stream, null,
	 * processor); }
	 * 
	 * public static CompositeMap parse( InputStream stream) throws
	 * SAXException, IOException { return parse(stream, null, null); }
	 */

	/**
	 * @todo Add element location info
	 */

	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
		super.setDocumentLocator(locator);
	}

	private static HashMap positions = new HashMap();

	public void addPositions(Integer pos, CompositeMap em) {
		positions.put(pos, em);
	}

	public HashMap getPostions() {
		return positions;

	}

}