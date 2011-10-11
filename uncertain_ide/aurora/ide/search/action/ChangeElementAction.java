package aurora.ide.search.action;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ChangeElementAction  extends AbstractSearchResultPageAction {

	public ChangeElementAction(Shell shell) {
		super(shell);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int getInfoSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected String getSubTaskName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Refactoring createRefactoring(List lines, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setControl(Text namespace, Text name) {
		// TODO Auto-generated method stub
		
	}}
