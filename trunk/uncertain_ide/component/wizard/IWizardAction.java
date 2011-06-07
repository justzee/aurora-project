/**
 * 
 */
package component.wizard;

import node.action.ActionInfo;
import node.action.ActionListener;


/**
 * @author linjinxiao
 *
 */
public interface IWizardAction {

	public ActionListener[] createActions(ActionInfo actionProperties);
}
