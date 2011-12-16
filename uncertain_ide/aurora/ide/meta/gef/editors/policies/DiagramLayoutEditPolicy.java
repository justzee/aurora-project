package aurora.ide.meta.gef.editors.policies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.commands.CreateComponentCommand;
import aurora.ide.meta.gef.editors.models.commands.MoveChildCmpCmd;
import aurora.ide.meta.gef.editors.models.commands.MoveComponentCommand;
import aurora.ide.meta.gef.editors.models.commands.MoveRemoteChildCmpCmd;
import aurora.ide.meta.gef.editors.parts.BoxPart;

public class DiagramLayoutEditPolicy extends FlowLayoutEditPolicy {
	private EditPart targetEditPart = null;

	protected Command createAddCommand(EditPart child, Object constraint) {
		return null;
	}

	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {

		if (!(constraint instanceof Rectangle))
			return null;

		MoveComponentCommand cmd = new MoveComponentCommand();
		cmd.setNode((AuroraComponent) child.getModel());

		// cmd.setNode((Node) child.getModel());
		cmd.setLocation(((Rectangle) constraint).getLocation());
		return cmd;

	}

	@Override
	protected void decorateChild(EditPart child) {
		// System.out.println("decorateChild");
		super.decorateChild(child);
	}

	@Override
	protected void decorateChildren() {
		// System.out.println("decorateChildren");
		super.decorateChildren();
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		targetEditPart = super.getTargetEditPart(request);
		return targetEditPart;
	}

	@Override
	public void showTargetFeedback(Request request) {
		super.showTargetFeedback(request);
	}

	protected Command getCreateCommand(CreateRequest request) {
		if (request.getNewObject() instanceof AuroraComponent) {
			Container parentModel = (Container) getHost().getModel();
			AuroraComponent ac = (AuroraComponent) request.getNewObject();
			if (!parentModel.isResponsibleChild(ac))
				return null;
			EditPart reference = getInsertionReference(request);
			CreateComponentCommand cmd = new CreateComponentCommand();
			cmd.setDiagram(parentModel);
			cmd.setChild(ac);
			cmd.setReferenceEditPart(reference);
			return cmd;
		}
		return null;
	}

	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

	@Override
	protected Command createAddCommand(EditPart child, EditPart after) {
		if (targetEditPart == null)
			return null;
		MoveRemoteChildCmpCmd cmd = new MoveRemoteChildCmpCmd();
		cmd.setEditPartToMove(child);
		if (targetEditPart.getModel() instanceof Container)
			cmd.setTargetContainer(targetEditPart);
		cmd.setReferenceEditPart(after);
		return cmd;
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		MoveChildCmpCmd cmd = new MoveChildCmpCmd();
		cmd.setEditPartToMove(child);
		cmd.setReferenceEditPart(after);
		return cmd;
	}

	protected boolean isLayoutHorizontal() {
		if (getHost() instanceof BoxPart) {
			Class<? extends Object> modelClass = getHost().getModel()
					.getClass();
			if (modelClass.equals(HBox.class)
					|| modelClass.equals(FieldSet.class)
					|| modelClass.equals(Form.class))
				return true;
		}
		return false;
	}
}