package aurora.ide.editor.textpage.format.sqlformat;

public class Token {
	private String original;

	private String upper;

	private String custom;

	private int type;

	private int subType;

	private int x;

	private int y;

	private int index;

	private int elementLengthInParen;

	private int elementIndexInParen;

	private boolean valueOnlyInParen;

	private Token parentTokenInParen;

	private int depthParen;
	private int indent;

	public Token(String original, int x, int y, int index) {
		this.original = original;
		this.upper = "";
		this.custom = "";
		this.x = x;
		this.y = y;
		this.index = index;
		this.elementLengthInParen = 0;
		this.elementIndexInParen = 0;
		this.valueOnlyInParen = false;
		this.parentTokenInParen = null;
		this.depthParen = 0;
		this.indent = 0;
	}

	public Token(Token token) {
		this.original = token.getOriginal();
		this.upper = token.getUpper();
		this.custom = token.getCustom();
		this.x = token.getX();
		this.y = token.getY();
		this.index = token.getIndex();
		this.elementLengthInParen = token.getElementLengthInParen();
		this.elementIndexInParen = token.getElementIndexInParen();
		this.valueOnlyInParen = token.isValueOnlyInParen();
		this.parentTokenInParen = token.getParentTokenInParen();
		this.depthParen = token.getDepthParen();
		this.indent = token.getIndent();
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public String getUpper() {
		return upper;
	}

	public void setUpper(String upper) {
		//if()
		this.upper = upper.toUpperCase();
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
		setUpper(custom);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getSubType() {
		return subType;
	}

	public void setSubType(int subType) {
		this.subType = subType;
	}

	public int getElementLengthInParen() {
		return elementLengthInParen;
	}

	public void setElementLengthInParen(int elementLengthInParen) {
		this.elementLengthInParen = elementLengthInParen;
	}

	public boolean isValueOnlyInParen() {
		return valueOnlyInParen;
	}

	public void setValueOnlyInParen(boolean valueOnlyInParen) {
		this.valueOnlyInParen = valueOnlyInParen;
	}

	public int getOriginalLength() {
		return original.length();
	}

	public int getCustomLength() {
		return custom.length();
	}

	public Token getParentTokenInParen() {
		return parentTokenInParen;
	}

	public void setParentTokenInParen(Token parentTokenInParen) {
		this.parentTokenInParen = parentTokenInParen;
	}

	public int getElementIndexInParen() {
		return elementIndexInParen;
	}

	public void setElementIndexInParen(int elementIndexInParen) {
		this.elementIndexInParen = elementIndexInParen;
	}

	public int getDepthParen() {
		return depthParen;
	}

	public void setDepthParen(int depthParen) {
		this.depthParen = depthParen;
	}

	public int getIndent() {
		return indent;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}
}
