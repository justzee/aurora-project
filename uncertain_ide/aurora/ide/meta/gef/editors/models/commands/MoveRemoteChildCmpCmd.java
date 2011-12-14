package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;

public class MoveRemoteChildCmpCmd extends Command {
	private Container srcContainer;
	private Container destContainer;
	private EditPart epToMove;
	private AuroraComponent acToMove;
	private AuroraComponent acReference = null;
	private int oriIndex = -1;

	public MoveRemoteChildCmpCmd() {
	}

	public void setEditPartToMove(EditPart child) {
		epToMove = child;
		acToMove = (AuroraComponent) child.getModel();
		srcContainer = (Container) epToMove.getParent().getModel();
	}

	public void setReferenceEditPart(EditPart after) {
		if (after != null) {
			acReference = (AuroraComponent) after.getModel();
		}
	}

	public void setTargetContainer(EditPart targetEditPart) {
		destContainer = (Container) targetEditPart.getModel();
	}

	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public void execute() {
		List<AuroraComponent> srcList = srcContainer.getChildren();
		List<AuroraComponent> destList = destContainer.getChildren();
		oriIndex = srcList.indexOf(acToMove);
		srcContainer.removeChild(oriIndex);
		if (acReference == null) {
			destContainer.addChild(acToMove);
		} else {
			int idx = destList.indexOf(acReference);
			destContainer.addChild(acToMove, idx);
		}
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() {
		destContainer.removeChild(acToMove);
		srcContainer.addChild(acToMove, oriIndex);
	}

}