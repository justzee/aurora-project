/**
 * 
 */
package uncertain.ide.eclipse.action;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.schema.Element;
import uncertain.schema.Wizard;

/**
 * @author linjinxiao
 *
 */
public class ActionsFactory {
	
	private static ActionsFactory instance;
	
	public synchronized static ActionsFactory getInstance() {
		if (instance == null)
			instance = new ActionsFactory();
		return instance;
	}
	
	public ActionListener[] createActions(ActionProperties actionProperties) throws Exception{
		CompositeMap parent = actionProperties.getParent();
		Element element = LoadSchemaManager.getSchemaManager().getElement(parent);
		if (element == null) {
			CustomDialog.showWarningMessageBox(LocaleMessage.getString("undefined.self.element"));
			return null;
		}
		ActionListener[] wizardActionListeners = getWizardActions(actionProperties, element);
		
		List childElements = CompositeMapAction.getAvailableChildElements(parent);
		ActionListener[] childActionListeners = null;
		if (childElements != null) {
			childActionListeners = new ActionListener[childElements.size()];
			Iterator ite = childElements.iterator();
			for (int i=0;ite.hasNext();i++) {
				Object object = ite.next();
				if(! (object instanceof Element))
					continue;
				Element ele = (Element) object;
				final QualifiedName qName = ele.getQName();
				childActionListeners[i] =new AddElementAction(actionProperties.getViewer(), parent, qName);

			}
		}
		return collectActionListeners(wizardActionListeners,childActionListeners);
	}

	private ActionListener[] getWizardActions(
			ActionProperties actionProperties, Element element)
			throws InstantiationException, IllegalAccessException {
		QualifiedName wizardQualifiedName = element.getWizardQName();
		Wizard wizard = LoadSchemaManager.getSchemaManager().getWizard(wizardQualifiedName);
		if(wizard == null)
			return null;
		String cls_name = wizard.getInstanceClass();
		Class cls;
		try {
			cls = Class.forName(cls_name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(LocaleMessage.getString("wizard.class") + cls_name
					+ LocaleMessage.getString("not.valid"));
		}
		Object wizardObject = cls.newInstance();
		IWizardAction wizardAction = (IWizardAction)wizardObject;
		ActionListener[] wizardActionListeners = wizardAction.createActions(actionProperties);
		return wizardActionListeners;
	}

	private ActionListener[] collectActionListeners(
			ActionListener[] wizardActionListeners,
			ActionListener[] childActionListeners) {
		int count = 0;
		int wizardCount = 0;
		int childCount = 0;
		if(wizardActionListeners != null && wizardActionListeners.length >0){
			wizardCount = wizardActionListeners.length;
		}
		if(childActionListeners != null && childActionListeners.length >0){
			childCount = childActionListeners.length;
		}
		count = wizardCount+childCount;
		ActionListener[] totalActionListeners = new ActionListener[count];
		if(wizardCount >0){
			System.arraycopy(wizardActionListeners, 0, totalActionListeners, 0, wizardCount);
		}
		if(childCount >0){
			System.arraycopy(childActionListeners, 0, totalActionListeners, wizardCount, childCount);
		}
		return totalActionListeners;
	}
	public void addActionsToMenu(Menu menu,ActionProperties actionProperties) throws Exception{
		ActionListener[] actionListeners = createActions(actionProperties);
		for(int i=0;i<actionListeners.length;i++){
			ActionListener  action =  actionListeners[i];
			MenuItem itemPush = new MenuItem(menu, SWT.PUSH);
			itemPush.addListener(SWT.Selection, action);
			itemPush.setText(action.getText());
			itemPush.setImage(action.getHoverImageDescriptor().createImage());
		}
	}
	public void addActionsToMenuManager(MenuManager menuManager,ActionProperties actionProperties)throws Exception{
		ActionListener[] actionListeners = createActions(actionProperties);
		if(actionListeners == null)
			return;
		for(int i=0;i<actionListeners.length;i++){
			ActionListener  action =  actionListeners[i];
			menuManager.add(action);
		}
	}

}
