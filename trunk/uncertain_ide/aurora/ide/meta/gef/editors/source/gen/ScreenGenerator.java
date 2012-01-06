package aurora.ide.meta.gef.editors.source.gen;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class ScreenGenerator {

	public static void genFile(ViewDiagram root) {
		CompositeMap screen = AuroraComponent2CompositMap
				.createScreenCompositeMap();
		CompositeMap view = AuroraComponent2CompositMap.toCompositMap(root);
		CompositeMap script = createCompositeMap("script");
		CompositeMap datasets = createCompositeMap("datasets");
		CompositeMap screenBody = createCompositeMap("screenBody");
		screen.addChild(view);
		view.addChild(script);
		view.addChild(datasets);
		view.addChild(screenBody);

		fill(root, screenBody, datasets);

		System.out.println(screen.toXML());

	}

	static public CompositeMap createCompositeMap(String name) {
		return AuroraComponent2CompositMap.createChild(name);
	}

	protected static void fill(Container root, CompositeMap parent,
			CompositeMap datasets) {

		List<AuroraComponent> children = root.getChildren();
		for (AuroraComponent ac : children) {
			CompositeMap child = AuroraComponent2CompositMap.toCompositMap(ac);
			if (child == null) {
				System.out.println(ac.getType());
				continue;
			}

			if (ac instanceof GridColumn && root instanceof Grid) {
				CompositeMap columns = parent.getChild("columns");
				if (columns == null) {
					columns = createCompositeMap("columns");
					parent.addChild(columns);
				}
				columns.addChild(child);
			} else {
				parent.addChild(child);
			}
			if (ac instanceof Container) {
				fill((Container) ac, child, datasets);
				fillDatasets((Container) ac, datasets);
			}
			if (ac instanceof Input || ac instanceof Grid) {

				bindDataset(root, ac, child, datasets);
			}
		}
	}

	private static void fillDatasets(Container ac, CompositeMap datasets) {
		Dataset dataset = ac.getDataset();
		if (dataset == null || dataset.isUseParentBM())
			return;
		CompositeMap rds = AuroraComponent2CompositMap.toCompositMap(dataset);
		datasets.addChild(rds);
	}

	// columns
	private static void bindDataset(Container root, AuroraComponent ac,
			CompositeMap child, CompositeMap datasets) {
		if (ac instanceof Grid || ac instanceof Input) {
			Dataset dataset = null;
			if (ac instanceof Grid) {
				dataset = findDataset((Grid) ac);
			} else {
				dataset = findDataset(root);
			}
			if (dataset == null)
				return;
			else {
				// lov,combox 特殊处理
				// required,readonly
				child.put("bindTarget", dataset.getId());
			}
		}
	}

	static private Dataset findDataset(Container container) {
		Dataset dataset = container.getDataset();
		if (dataset == null)
			return null;
		boolean useParentBM = dataset.isUseParentBM();
		if (useParentBM) {
			return findDataset(container.getParent());
		}
		return dataset;
	}

	// IFile newFileHandle = AuroraPlugin.getWorkspace().getRoot()
	// .getFile(new Path("/hr_aurora/web/a0.screen"));
	// CompositeMap cm = new CompositeMap("xx");
	// cm.put("x", "bb");
	// InputStream is = new ByteArrayInputStream(cm.toXML().getBytes());
	// CreateFileOperation op = new CreateFileOperation(newFileHandle, null,
	// is, "Create New File");
	// try {
	// PlatformUI
	// .getWorkbench()
	// .getOperationSupport()
	// .getOperationHistory()
	// .execute(
	// op,
	// null,
	// WorkspaceUndoUtil.getUIInfoAdapter(this.getSite()
	// .getShell()));
	// } catch (final ExecutionException e) {
	// // handle exceptions
	// e.printStackTrace();
	// }
	// xmlns:a="http://www.aurora-framework.org/application"

}
