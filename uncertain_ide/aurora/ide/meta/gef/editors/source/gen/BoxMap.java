package aurora.ide.meta.gef.editors.source.gen;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.VBox;

public class BoxMap extends AbstractComponentMap {

	private AuroraComponent c;

	public BoxMap(AuroraComponent c) {
		this.c = c;
	}

	@Override
	public CompositeMap toCompositMap() {
//		if (c instanceof HBox) {
//			return toHBoxMap((HBox) c);
//		}
//		if (c instanceof VBox) {
//			return toVBoxMap((VBox) c);
//		}
//		if (c instanceof FieldSet) {
//			return toFieldSetMap((FieldSet) c);
//		}c instanceof Form
		return null;
	}

}
