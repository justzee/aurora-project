package uncertain.composite;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import uncertain.composite.decorate.ElementModifier;

public class CommentCompositeLoader extends CompositeLoader {
	CompositeMap parse(InputStream stream) throws IOException, SAXException {
		CompositeMapParser p = new CommentCompositeMapParser(this);
		CompositeMap m = p.parseStream(stream);
		if (mSupportFileMerge) {
			String base_file = m.getString(KEY_BASE_FILE);
			if (base_file != null && m.getChilds() != null) {
				CompositeMap base = load(base_file);
				CompositeMap merged = ElementModifier.process(m.getChilds(),
						base);
				return merged;
			}
		}
		return m;
	}
}
