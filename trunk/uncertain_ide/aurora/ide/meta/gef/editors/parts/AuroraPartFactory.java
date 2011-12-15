package aurora.ide.meta.gef.editors.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Navbar;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.VBox;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

/**
 */
public class AuroraPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		if (model instanceof ViewDiagram)
			part = new ViewDiagramPart();
		// else if (model instanceof Label)
		// part = new LabelPart();
		else if (model instanceof Form) {
			part = new BoxPart();
		}else if (model instanceof HBox) {
			part = new BoxPart();
		}else if (model instanceof VBox) {
			part = new BoxPart();
		} else if (model instanceof Input) {
			part = new InputPart();
		} else if (model instanceof Grid) {
			part = new GridPart();
		} else if (model instanceof Button) {
			part = new ButtonPart();
		} else if (model instanceof GridColumn) {
			part = new GridColumnPart();
		} else if (model instanceof Toolbar) {
			part = new ToolbarPart();
		} else if (model instanceof Navbar) {
			part = new NavbarPart();
		}
		if(part == null){
			System.out.println();
		}
		part.setModel(model);
		return part;
	}

}
