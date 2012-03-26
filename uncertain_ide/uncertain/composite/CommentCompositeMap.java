package uncertain.composite;

import java.util.Map;

import uncertain.composite.CompositeMap;

public class CommentCompositeMap extends CompositeMap {
	protected String globalComment;

	protected String comment;

	protected String endElementComment;

	public CommentCompositeMap() {
		super();
	}

	public CommentCompositeMap(CompositeMap another) {
		super(another);
	}

	public CommentCompositeMap(int size, float load_factor) {
		super(size, load_factor);
	}

	public CommentCompositeMap(int size) {
		super(size);
	}

	public CommentCompositeMap(String name, Map map) {
		super(name, map);
	}

	public CommentCompositeMap(String _prefix, String _uri, String _name) {
		super(_prefix, _uri, _name);
	}

	public CommentCompositeMap(String _name) {
		super(_name);
	}

	/** gets comment in XML document */
	public String getComment() {
		return comment;
	}

	/** sets comment ( comment section in XML document ) */
	public void setComment(String t) {
		comment = t;
	}

	/** gets comment in XML document */
	public String getEndElementComment() {
		return endElementComment;
	}

	/** sets comment ( comment section in XML document ) */
	public void setEndElementComment(String t) {
		endElementComment = t;
	}

	public void setStartPoint(int line, int column) {
		getLocationNotNull().setStartPoint(line, column);
	}

	public void setEndPoint(int line, int column) {
		getLocationNotNull().setEndPoint(line, column);
	}

	public boolean equals(Object o) {
		if (o instanceof CompositeMap) {
			CompositeMap cm = (CompositeMap) o;
			if (this.toXML().equals(cm.toXML())) {
				return true;
			}
		}
		return false;
	}
}
