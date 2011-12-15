package aurora.statistics.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferencesTag {

	private PreferencesTag() {

	}

	private static PreferencesTag tag;

	public static PreferencesTag INSTANCE() {
		if (tag == null) {
			tag = new PreferencesTag();
		}
		return tag;
	}

	private Map<String, List<String>> namespaceMap = new HashMap<String, List<String>>();

	private Map<String, List<String>> defaultMap = new HashMap<String, List<String>>() {
		private static final long serialVersionUID = -2136894063010579177L;
		{
			//TODO
			// this.put(key, value)
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
		List<String> tags = this.getTags(namespace);
		//TODO
//		return tags.contains(tagName);
		return true;
	}

	public Collection<String> getNamespaces() {
		return getNSMap().keySet();
	}

	private Map<String, List<String>> getNSMap() {
		return this.namespaceMap.size() == 0 ? this.defaultMap
				: this.namespaceMap;
	}

	public String getType(String namespace, String tagName) {
		return null;
	}

}
