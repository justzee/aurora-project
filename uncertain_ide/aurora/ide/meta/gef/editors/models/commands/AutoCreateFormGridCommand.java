package aurora.ide.meta.gef.editors.models.commands;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.HBox;

public class AutoCreateFormGridCommand extends DropBMCommand {
	public void execute() {
		createDS();

		this.getDiagram().addChild(createForm());

		this.getDiagram().addChild(createButtons());

		getDiagram().addChild(createGrid());

	}

	protected AuroraComponent createGrid() {
		Grid grid = new Grid();
		fillGrid(grid);

		return grid;
	}

	protected AuroraComponent createButtons() {
		// TODO js 查询
		HBox hbox = new HBox();
		Button search = new Button();
		search.setText("查询");
		Button reset = new Button();
		reset.setText("重置");
		hbox.addChild(search);
		hbox.addChild(reset);
		return hbox;
	}

	protected Form createForm() {
		Form form = new Form();
		fillForm(form);
		return form;

	}

}
