package aurora.ide.meta.gef.editors.policies;

import aurora.ide.meta.gef.editors.figures.GridColumnFigure;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.commands.CreateComponentCommand;
import aurora.ide.meta.gef.editors.models.commands.MoveChildCmpCmd;
import aurora.ide.meta.gef.editors.models.commands.MoveComponentCommand;
import aurora.ide.meta.gef.editors.models.commands.MoveRemoteChildCmpCmd;
import aurora.ide.meta.gef.editors.parts.GridColumnPart;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.DropRequest;

public class FormLayoutEditPolicy extends FlowLayoutEditPolicy {

	private EditPart targetEditPart;

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

	protected Command getCreateCommand(CreateRequest request) {
		if (request.getNewObject() instanceof AuroraComponent) {
			Container parentModel = (Container) getHost().getModel();
			AuroraComponent ac = (AuroraComponent) request.getNewObject();
			if (!parentModel.isResponsibleChild(ac)) {
				return null;
			}
			CreateComponentCommand cmd = new CreateComponentCommand();
			cmd.setDiagram(parentModel);
			cmd.setChild(ac);

			// Rectangle constraint = (Rectangle) getConstraintFor(request);
			// cmd.setLocation( request.getLocation());
			return cmd;
		}
		return null;
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		targetEditPart = super.getTargetEditPart(request);
		if ((targetEditPart instanceof GridColumnPart)
				&& (request instanceof DropRequest)) {
			GridColumnPart gcp = (GridColumnPart) targetEditPart;
			GridColumnFigure figure = (GridColumnFigure) gcp.getFigure();
			GridColumn model = (GridColumn) gcp.getModel();
			if (((DropRequest) request).getLocation().y > figure.getBounds().y
					+ model.getHeadHight())
				targetEditPart = targetEditPart.getParent();
		}
		return targetEditPart;
	}

	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		return null;
	}

	@Override
	protected Command createAddCommand(EditPart child, EditPart after) {
		if (targetEditPart == null)
			return null;
		MoveRemoteChildCmpCmd cmd = new MoveRemoteChildCmpCmd();
		cmd.setEditPartToMove(child);
		if (targetEditPart.getModel() instanceof Container) {
			Container dest = (Container) targetEditPart.getModel();
			AuroraComponent ac = (AuroraComponent) child.getModel();
			if (!dest.isResponsibleChild(ac))
				return null;
			cmd.setTargetContainer(targetEditPart);
		}
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
		return getHost().getModel() instanceof GridColumn;
	}

	@Override
	public void showTargetFeedback(Request request) {
		// if (targetEditPart != null
		// && targetEditPart.getClass().equals(GridPart.class)) {
		// if (request instanceof DropRequest
		// && (!REQ_RESIZE.equals(request.getType()))) {
		// ComponentPart ref = (ComponentPart) getInsertionReference(request);
		// if ((ref instanceof NavbarPart)) {
		// // GridColumnPart firstColumn = null;
		// // GridColumnPart lastColumn = null;
		// // boolean findFirst = false;
		// // for (Object ep : targetEditPart.getChildren()) {
		// // if (ep instanceof GridColumnPart) {
		// // if (!findFirst) {
		// // firstColumn = (GridColumnPart) ep;
		// // findFirst = true;
		// // }
		// // lastColumn = (GridColumnPart) ep;
		// // }
		// // }
		// // if (lastColumn != null) {
		// // Rectangle rect = lastColumn.getFigure().getBounds()
		// // .getShrinked(-4, -2);
		// // Polyline linefb = getLineFeedback();
		// // linefb.setStart(rect.getTopRight());
		// // linefb.setEnd(rect.getBottomRight());
		// // // getFeedbackLayer().add(linefb);
		// // return;
		// // }
		// }
		// }
		// }
		super.showTargetFeedback(request);
	}

	// protected EditPart getInsertionReference(Request request) {
	// EditPart ep = super.getInsertionReference(request);
	// List children = getHost().getChildren();
	// if (ep.getClass().equals(ToolbarPart.class)) {
	// int idx = children.indexOf(ep);
	// // if(idx==)
	// }
	// return null;
	// }
}