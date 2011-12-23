package aurora.ide.meta.gef.editors.models.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.commands.Command;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Navbar;
import aurora.ide.meta.gef.editors.models.QueryDataSet;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.search.cache.CacheManager;

public class DropBMCommand extends Command {
	private ViewDiagram diagram;
	private IFile bm;
	private Dataset rds;
	private QueryDataSet qds;

	public ViewDiagram getDiagram() {
		return diagram;
	}

	public void setDiagram(ViewDiagram diagram) {
		this.diagram = diagram;
	}

	public IFile getBm() {
		return bm;
	}

	public void setBm(IFile bm) {
		this.bm = bm;
	}

	protected void fillGrid(Grid grid) {
		Toolbar tb = new Toolbar();
		Button b = new Button();
		b.setButtonType(Button.ADD);
		tb.addButton(b);
		b = new Button();
		b.setButtonType(Button.SAVE);
		tb.addButton(b);
		b = new Button();
		b.setButtonType(Button.DELETE);
		tb.addButton(b);
		grid.setBindTarget(this.rds);
		grid.addChild(tb);
		grid.addChild(new Navbar());
		try {
			List<CompositeMap> fields = this.getFields();
			for (CompositeMap f : fields) {
				String string = this.getPrompt(f);
				GridColumn gc = new GridColumn();
				gc.setPrompt(string);
				grid.addChild(gc);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected List<CompositeMap> getFields() throws CoreException,
			ApplicationException {
		CompositeMap model = CacheManager.getCompositeMap(bm);
		List<CompositeMap> fs = new ArrayList<CompositeMap>();
		CompositeMap fields = model.getChild("fields");
		if (fields != null) {
			Iterator childIterator = fields.getChildIterator();
			while (childIterator != null && childIterator.hasNext()) {
				CompositeMap qf = (CompositeMap) childIterator.next();
				if ("field".equals(qf.getName())) {
					fs.add(qf);
				}
			}
		}
		return fs;
	}

	protected void fillForm(BOX form) {
		form.setBindTarget(qds);
		try {
			CompositeMap model = CacheManager.getCompositeMap(bm);
			List<CompositeMap> qfs = new ArrayList<CompositeMap>();
			List<CompositeMap> fs = getFields();
			CompositeMap child = model.getChild("query-fields");
			if (child != null) {
				Iterator childIterator = child.getChildIterator();
				while (childIterator != null && childIterator.hasNext()) {
					CompositeMap qf = (CompositeMap) childIterator.next();
					if ("query-field".equals(qf.getName())) {
						qfs.add(qf);
					}
				}
			}
			for (CompositeMap qf : qfs) {
				String name = (String) qf.get("field");
				name = name == null ? qf.getString("name") : name;
				name = name == null ? "" : name;
				Input input = new Input();
				input.setName(name);
				CompositeMap field = this.getField(name, fs);
				if (field == null) {
					// TODO model extend???
					// 或者根本不存在。需要页面定义。属性是name
					System.out.println();
					field = this.getField(name, fs);
				}
				input.setPrompt(getPrompt(field));
				input.setType(getType(field));
				input.setBindTarget(qds);
				form.addChild(input);
				// input.setReadOnly(readOnly)
				// input.setRequired(required)
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}

	protected String getPrompt(CompositeMap field) {
		return field != null ? field.getString("prompt") : "prompt:";
	}

	protected String getType(CompositeMap field) {
		if (field == null) {
			return Input.TEXT;
		}
		if ("java.lang.Long".equals(field.getString("datatype"))) {
			return Input.NUMBER;
		}
		if ("java.lang.String".equals(field.getString("datatype"))) {
			return Input.TEXT;
		}
		if ("java.util.Date".equals(field.getString("datatype"))) {
			return Input.CAL;
		}
		return Input.TEXT;
	}

	protected CompositeMap getField(String name, List<CompositeMap> fs) {
		for (CompositeMap f : fs) {
			if (name.equals(f.getString("name"))) {
				return f;
			}
		}
		return null;
	}

	protected void createDS() {
		this.createQueryDataset();
		this.createResultDataset();
	}

	protected void createQueryDataset() {
		qds = new QueryDataSet();
		qds.setResultDataset(rds);
		qds.setBmPath(bm.getProjectRelativePath().toString());
		if (rds != null)
			rds.setQueryDataSet(qds);
		diagram.addDataset(qds);
	}

	protected void createResultDataset() {
		rds = new Dataset();
		rds.setAutoQuery(false);
		rds.setBmPath(bm.getProjectRelativePath().toString());
		rds.setPageSize(10);
		rds.setSelectable(true);
		rds.setQueryDataSet(qds);
		if (qds != null)
			qds.setResultDataset(rds);
		diagram.addDataset(rds);

	}

	public void redo() {
		this.execute();
	}

	public void undo() {
		// TODO
		removeDS();
	}

	protected void removeDS() {
		if (rds != null)
			diagram.removeDataset(rds);
		if (qds != null)
			diagram.removeDataset(qds);
	}

}
