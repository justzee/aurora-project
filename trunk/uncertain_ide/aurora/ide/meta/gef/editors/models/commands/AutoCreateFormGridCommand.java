package aurora.ide.meta.gef.editors.models.commands;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.HBox;

public class AutoCreateFormGridCommand extends DropBMCommand {
	
	private Container container;
	
	public void execute() {
		createDS();

		container.addChild(createForm());

		container.addChild(createButtons());

		container.addChild(createGrid());

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

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

}
