package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;

public class MoveChildCmpCmd extends Command {
	private EditPart epParent;
	private AuroraComponent acToMove;
	private AuroraComponent acRel = null;
	private int oriIndex = -1;

	public MoveChildCmpCmd() {
	}

	public void setChild(EditPart child) {
		epParent = child.getParent();
		acToMove = (AuroraComponent) child.getModel();
	}

	public void setAfterEditPart(EditPart after) {
		if (after != null)
			acRel = (AuroraComponent) after.getModel();
	}

	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public void execute() {
		List<AuroraComponent> children = ((Container) epParent.getModel())
				.getChildren();
		oriIndex = strictIndexOf(children, acToMove);
		children.remove(oriIndex);
		if (acRel == null) {
			children.add(acToMove);
		} else {
			int idx = strictIndexOf(children, acRel);
			children.add(idx, acToMove);
		}
		epParent.refresh();
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() {
		List<AuroraComponent> children = ((Container) epParent.getModel())
				.getChildren();
		children.remove(acToMove);
		children.add(oriIndex, acToMove);
		epParent.refresh();
	}

	private int strictIndexOf(List list, Object ele) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i) == ele)
				return i;
		return -1;
	}

}