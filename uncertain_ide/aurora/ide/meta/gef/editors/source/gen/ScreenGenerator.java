package aurora.ide.meta.gef.editors.source.gen;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class ScreenGenerator {

	public static void genFile(ViewDiagram root) {
		AuroraComponent2CompositMap c2m = new AuroraComponent2CompositMap();
		CompositeMap screen = c2m.createScreenCompositeMap();
		CompositeMap view = c2m.toCompositMap(root);
		CompositeMap script = c2m.createChild("script");
		CompositeMap datasets = c2m.createChild("datasets");
		CompositeMap screenBody = c2m.createChild("screenBody");
		screen.addChild(view);
		view.addChild(script);
		view.addChild(datasets);
		view.addChild(screenBody);

		fill(root, screenBody);

		System.out.println(screen.toXML());

	}

	protected static void fill(Container root, CompositeMap parent) {
		AuroraComponent2CompositMap c2m = new AuroraComponent2CompositMap();

		List<AuroraComponent> children = root.getChildren();
		for (AuroraComponent ac : children) {
			CompositeMap compositMap = c2m.toCompositMap(ac);
			if(compositMap == null){
				System.out.println();
				continue;
			}
			parent.addChild(compositMap);
			if (ac instanceof Container) {
				fill((Container) ac, compositMap);
			}
		}
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
