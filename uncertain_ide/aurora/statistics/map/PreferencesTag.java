package aurora.statistics.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uncertain.composite.CompositeMap;

public class PreferencesTag {

	private PreferencesTag() {

	}

	private static PreferencesTag tag;

	public static Set<String> set_1 = new HashSet<String>();
	public static Set<String> set_2 = new HashSet<String>();

	public static PreferencesTag INSTANCE() {
		if (tag == null) {
			tag = new PreferencesTag();
		}
		return tag;
	}

	private Map<String, List<String>> namespaceMap = new HashMap<String, List<String>>();

	public void setNamespaceMap(Map<String, List<String>> namespaceMap) {
		this.namespaceMap = namespaceMap;
	}

	private Map<String, List<String>> defaultMap = new HashMap<String, List<String>>() {
		private static final long serialVersionUID = -2136894063010579177L;
		{
			// TODO
			// this.put("http://www.aurora-framework.org/schema/bm", "");
		}
	};

	public void addTag(String namespace, String tagName) {
		List<String> tags = namespaceMap.get(namespace);
		if (tags == null) {
			tags = new ArrayList<String>();
			namespaceMap.put(namespace, tags);
		}
		if (tags.contains(tagName))
			return;
		tags.add(tagName);
	}

	public List<String> getTags(String namespace) {
		return getNSMap().get(namespace);
	}

	public boolean hasTag(String namespace, String tagName) {
		if (null == namespace || "".equals(namespace.trim())) {
			// set_2.add(tagName);
			// return true;
			namespace = "No namespace";
		}
		List<String> tags = this.getTags(namespace);
		if (null == tags) {
			set_2.add("2," + namespace + ":\t\t\t\t" + tagName);
			return false;
		}
		boolean bool = tags.contains(tagName);
		if (!bool) {
			set_1.add("1," + namespace + ":\t\t\t\t" + tagName);
		}
		return bool;
	}

	public Collection<String> getNamespaces() {
		return getNSMap().keySet();
	}

	private Map<String, List<String>> getNSMap() {
		return this.namespaceMap.size() == 0 ? this.defaultMap : this.namespaceMap;
	}

	public String getType(String namespace, String tagName) {
		return null;
	}

}
