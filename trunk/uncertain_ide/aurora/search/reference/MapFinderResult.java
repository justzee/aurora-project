package aurora.search.reference;

import java.util.ArrayList;
import java.util.List;

import uncertain.composite.CompositeMap;

public class MapFinderResult {
	private CompositeMap map;

	private List attributes;

	public CompositeMap getMap() {
		return map;
	}

	public void setMap(CompositeMap map) {
		this.map = map;
	}

	public List getAttributes() {
		return attributes;
	}

	public void setAttributes(List attributes) {
		this.attributes = attributes;
	}

	public void addAttribute(Object o) {
		if (attributes == null)
			attributes = new ArrayList();
		this.attributes.add(o);
	}

	public MapFinderResult(CompositeMap map, List attributes) {
		super();
		this.map = map;
		this.attributes = attributes;
	}

	
}
