package aurora.ide.search.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Text;

public class ReplaceAttributeAction extends Action implements ISearchResultPageAction{
	public static final int NAME = 0;
	public static final int VALUE = 1;

	private int type;

	public ReplaceAttributeAction(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setControl(Text replace, Text with) {
		// TODO Auto-generated method stub
		
	}

	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public boolean isRefactorSelectionEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRefactorAllEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void runAll() {
		// TODO Auto-generated method stub
		
	}

	public void runSelection() {
		// TODO Auto-generated method stub
		
	}

	public void addActionChangedListener(IActionChangedListener listener) {
		// TODO Auto-generated method stub
		
	}

}
