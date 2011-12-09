package aurora.ide.meta.gef.editors.policies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.commands.CreateComponentCommand;
import aurora.ide.meta.gef.editors.models.commands.MoveComponentCommand;
import aurora.ide.meta.gef.editors.parts.LabelPart;

public class DiagramLayoutEditPolicy extends FlowLayoutEditPolicy {

	protected Command createAddCommand(EditPart child, Object constraint) {
		return null;
	}

	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		if (!(child instanceof LabelPart))
			return null;
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
		System.out.println("decorateChild");
		super.decorateChild(child);
	}

	@Override
	protected void decorateChildren() {
		System.out.println("decorateChildren");
		super.decorateChildren();
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
	
		System.out.println(request.getType());
		return super.getTargetEditPart(request);
	}

	@Override
	public void showTargetFeedback(Request request) {
		// TODO Auto-generated method stub
		super.showTargetFeedback(request);
	}

	protected Command getCreateCommand(CreateRequest request) {
		if (request.getNewObject() instanceof AuroraComponent) {
			CreateComponentCommand cmd = new CreateComponentCommand();
			cmd.setDiagram((Container) getHost().getModel());
			cmd.setChild((AuroraComponent) request.getNewObject());
			cmd.setLocation(request.getLocation());
//			Rectangle constraint = (Rectangle) getConstraintFor(request);
//			cmd.setLocation(	request.getLocation());
			return cmd;
		}
		return null;
	}

	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

	@Override
	protected Command createAddCommand(EditPart child, EditPart after) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		// TODO Auto-generated method stub
		return null;
	}
}