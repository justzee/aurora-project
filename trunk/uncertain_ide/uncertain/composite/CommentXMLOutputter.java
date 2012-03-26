package uncertain.composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import uncertain.util.XMLWritter;

public class CommentXMLOutputter extends XMLOutputter {

	public CommentXMLOutputter() {
		super();
	}

	/** Creates new XMLOutputter */
	public CommentXMLOutputter(String _indent, boolean _new_line) {
		super(_indent, _new_line);
	}

	static void getAttributeXML(Map map, StringBuffer attribs) {
		Iterator it = map.entrySet().iterator();
		HashMap strings = new HashMap();
		List keyList = new ArrayList();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (value != null) {
				strings.put(key.toString(), value.toString());
				keyList.add(key.toString());
			}
		}
		Object[] keys = keyList.toArray();
		// Arrays.sort(keys);
		keys = sortKey(keys);
		for (int i = 0; i < keys.length; i++) {
			attribs.append(" ").append(
					XMLWritter.getAttrib(keys[i].toString(),
							strings.get(keys[i]).toString()));
		}
		// Iterator it = map.entrySet().iterator();
		// while(it.hasNext()){
		// Map.Entry entry = (Map.Entry)it.next();
		// Object key = entry.getKey();
		// Object value = entry.getValue();
		// if( value != null)
		// attribs.append(" ").append(XMLWritter.getAttrib(key.toString(),
		// value.toString() ) );
		// }
	}

	static private Object[] sortKey(Object[] keys) {
		if (keys == null)
			return null;
		List keyColumnList = new ArrayList();
		keyColumnList.add("id");
		keyColumnList.add("name");
		Arrays.sort(keys);
		List list = new LinkedList();
		for (int i = 0; i < keys.length; i++) {
			String keyStr = keys[i].toString();
			if (keyColumnList.contains(keyStr)) {
				list.add(0, keyStr);
			} else {
				list.add(keyStr);
			}
		}
		return list.toArray();

	}
	/**
	 * internal method
	 * 
	 * @param namespaces
	 *            a Map of existing namespace: namespace -> Integer of ref count
	 * @param prefix_mapping
	 *            a Map of namespace -> prefix mapping
	 * @return string of XML
	 */
	String toXMLWithPrefixMapping(int level, CompositeMap map, Map namespaces, Map prefix_mapping) {

		StringBuffer attribs = new StringBuffer();
		StringBuffer childs = new StringBuffer();
		StringBuffer xml = new StringBuffer();
		String indent_str = getIndentString(level);
		String namespace_uri = map.getNamespaceURI();
		StringBuffer xmlns_declare = null;

		boolean need_new_line_local = mUseNewLine;

		if (prefix_mapping == null) {
			if (namespace_uri != null) {
				boolean uri_exists = false;
				if (namespaces != null) {
					uri_exists = (namespaces.get(namespace_uri) != null);
				}
				if (!uri_exists) {
					String xmlns = "xmlns";
					if (map.getPrefix() != null)
						xmlns = "xmlns:" + map.getPrefix();
					attribs.append(" ").append(XMLWritter.getAttrib(xmlns, namespace_uri));
				}
				namespaces = addRef(namespaces, namespace_uri, map);
			}
		}
		String comment = getComment(level, map);
		if (comment != null) {
			xml.append(comment);
		}
		getAttributeXML(map, attribs);

		String endElementComment = getEndElementComment(level + 1, map);
		if (map.getChilds() == null) {
			if (endElementComment != null) {
				childs.append(LINE_SEPARATOR).append(endElementComment);
			}
			if (map.getText() != null) {
				need_new_line_local = false;
				if (mGenerateCdata)
					childs.append(CDATA_BEGIN).append(map.getText()).append(CDATA_END);
				else
					childs.append(XMLWritter.escape(map.getText()));
			}
		} else {
			getChildXML(level + 1, map.getChilds(), childs, namespaces, prefix_mapping);
			if (endElementComment != null) {
				childs.append(endElementComment).append(LINE_SEPARATOR);
			}
		}

		if (prefix_mapping == null) {
			subRef(namespaces, namespace_uri);
		}

		String elm = null;
		if (prefix_mapping == null) {
			elm = map.getRawName();
		} else {
			elm = map.getName();
			if (namespace_uri != null) {
				String prefix = (String) prefix_mapping.get(namespace_uri);
				elm = prefix + ":" + elm;
			}
			if (level == 0) {
				xmlns_declare = new StringBuffer();
				appendNamespace(xmlns_declare, prefix_mapping);
			}
		}
		xml.append(indent_str).append('<').append(elm);
		if (xmlns_declare != null)
			xml.append(xmlns_declare);
		xml.append(attribs);
		if (childs.length() > 0) {
			xml.append('>');
			if (need_new_line_local)
				xml.append(LINE_SEPARATOR);
			xml.append(childs);
			if (need_new_line_local)
				xml.append(indent_str);
			xml.append(XMLWritter.endTag(elm));
		} else
			xml.append("/>");
		if (mUseNewLine)
			xml.append(LINE_SEPARATOR);
		return xml.toString();
	}
	private String getComment(int level, CompositeMap map) {
		StringBuffer xml = new StringBuffer();
		String indent_str = getIndentString(level);
		if (map.getComment() != null) {
			String[] comms = map.getComment().split("-->");
			for (int i = 0; i < comms.length; i++) {
				String comm = comms[i];
				String fullComm = "<!--" + comm + "-->";
				xml.append(indent_str).append(fullComm).append(LINE_SEPARATOR);
			}
		} else {
			return null;
		}
		return xml.toString();
	}
	private String getEndElementComment(int level, CompositeMap map) {
		StringBuffer xml = new StringBuffer();
		String indent_str = getIndentString(level);
		if (map.getEndElementComment() != null) {
			String[] comms = map.getEndElementComment().split("-->");
			for (int i = 0; i < comms.length; i++) {
				String comm = comms[i];
				String fullComm = "<!--" + comm + "-->";
				if(i>0){
					xml.append(LINE_SEPARATOR);
				}
				xml.append(indent_str).append(fullComm);
				
			}
		} else {
			return null;
		}
		return xml.toString();
	}

}
