package aurora.ide.meta.gef.editors.source.gen;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.QueryDataSet;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.VBox;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class AuroraComponent2CompositMap {
	public static final String SCREEN_PREFIX = "a";

	public CompositeMap createScreenCompositeMap() {
		CompositeMap screen = new CompositeMap("screen");
		screen.setNameSpace(SCREEN_PREFIX,
				"http://www.aurora-framework.org/application");
		return screen;
	}

	public CompositeMap createChild(String name) {
		CompositeMap node = new CompositeMap(name);
		node.setPrefix(SCREEN_PREFIX);
		return node;
	}

	public CompositeMap toCompositMap(AuroraComponent c) {
		if (c instanceof Input) {
			return toInputMap((Input) c);
		}
		if (c instanceof Button) {
			return toButtonMap((Button) c);
		}
		if (c instanceof Form) {
			return toFormMap((Form) c);
		}
		if (c instanceof HBox) {
			return toHBoxMap((HBox) c);
		}
		if (c instanceof VBox) {
			return toVBoxMap((VBox) c);
		}
		if (c instanceof FieldSet) {
			return toFieldSetMap((FieldSet) c);
		}
		if (c instanceof CheckBox) {
			return toCheckBoxMap((CheckBox) c);
		}
		if (c instanceof Grid) {
			return toGridMap((Grid) c);
		}
		if (c instanceof GridColumn) {
			return toGridColumnMap((GridColumn) c);
		}
		if (c instanceof QueryDataSet) {
			return toQueryDataSetMap((QueryDataSet) c);
		}
		if (c instanceof ResultDataSet) {
			return toResultDataSetMap((ResultDataSet) c);
		}
		if (c instanceof Toolbar) {
			return toToolbarMap((Toolbar) c);
		}
		if (c instanceof TabItem) {
			return toTabItemMap((TabItem) c);
		}
		if (c instanceof TabFolder) {
			return toTabFolderMap((TabFolder) c);
		}
		if (c instanceof ViewDiagram) {
			return toViewMap((ViewDiagram) c);
		}

		return null;
	}

	private CompositeMap toButtonMap(Button c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toFormMap(Form c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toHBoxMap(HBox c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toVBoxMap(VBox c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toFieldSetMap(FieldSet c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toCheckBoxMap(CheckBox c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toGridMap(Grid c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toGridColumnMap(GridColumn c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toQueryDataSetMap(QueryDataSet c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toResultDataSetMap(ResultDataSet c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toToolbarMap(Toolbar c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toTabItemMap(TabItem c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toTabFolderMap(TabFolder c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toViewMap(ViewDiagram c) {
		// TODO Auto-generated method stub
		return null;
	}

	private CompositeMap toInputMap(Input c) {
		// TODO Auto-generated method stub
		return null;
	}

}
