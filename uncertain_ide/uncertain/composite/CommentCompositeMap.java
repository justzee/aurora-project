package uncertain.composite;

import uncertain.composite.CompositeMap;

public class CommentCompositeMap extends CompositeMap {
	protected String globalComment;

	protected String comment;

	protected String endElementComment;

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
