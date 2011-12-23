package aurora.ide.meta.gef.editors.policies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.commands.AutoCreateFormGridCommand;
import aurora.ide.meta.gef.editors.request.DropBMRequest;

public class AutoCreateFormGridEditPolicy extends AbstractEditPolicy {

	@Override
	public Command getCommand(Request request) {
		if (request instanceof DropBMRequest)
			return getBindCommand((DropBMRequest) request);
		return super.getCommand(request);
	}

	protected Command getBindCommand(DropBMRequest request) {
		AutoCreateFormGridCommand cmd = new AutoCreateFormGridCommand();
		// cmd.setDiagram(parentModel);
		// cmd.setChild(new Input());
		cmd.setBm(request.getBm());
		Object model = this.getHost().getModel();
		cmd.setDiagram((ViewDiagram) model);
		return cmd;
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		if (request instanceof DropBMRequest)
			return this.getHost();
		return super.getTargetEditPart(request);
	}

	@Override
	public boolean understandsRequest(Request req) {
		return super.understandsRequest(req);
	}

}
