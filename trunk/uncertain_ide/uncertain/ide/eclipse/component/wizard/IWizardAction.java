/**
 * 
 */
package uncertain.ide.eclipse.component.wizard;

import uncertain.ide.eclipse.node.action.ActionListener;
import uncertain.ide.eclipse.node.action.ActionInfo;


/**
 * @author linjinxiao
 *
 */
public interface IWizardAction {

	public ActionListener[] createActions(ActionInfo actionProperties);
}
