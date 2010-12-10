package uncertain.ide.eclipse.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.IViewer;
import uncertain.ide.eclipse.editor.widgets.ListElementsExchangeDialog;

public class AddFieldAction extends Action {
	private IViewer viewer;

	private CompositeMap allFields;
	private CompositeMap target;

	private final static String primary_key = "primary-key";
	private final static String primaryKeyChild = "pk-field";
	private final static String order_by = "order-by";
	private final static String orderField = "order-field";
	private final static String defaultChild = "field";
	private final static String mainAttribute = "name";
	public AddFieldAction(IViewer viewer,CompositeMap source,CompositeMap target) {
		this.viewer = viewer;
		this.allFields = source;
		this.target = target;
		setHoverImageDescriptor(getDefaultImageDescriptor());
	}
	public void run() {
		List source_childs_list = allFields.getChildsNotNull();
		if(source_childs_list.size()==0)
			return ;
		QualifiedName fieldQN = getFieldQN();
		if(fieldQN == null)
			return ;
		List existFields = getExistFields();
		List source_array = getNotExistFields(source_childs_list, existFields);

		String[] source_items = getArrayFromList(source_array);
		String[] target_items = getArrayFromList(existFields);
		
		ListElementsExchangeDialog dialog = new ListElementsExchangeDialog(LocaleMessage.getString("get.fields"),allFields.getName(),
				target.getName(),source_items,target_items);
		if(dialog.open()==ListElementsExchangeDialog.CANCEL)
			return;
		String[] result = dialog.getRightItems();
		target.getChildsNotNull().clear();
		for(int i=0;i<result.length;i++){
			CompositeMap newNode = CompositeMapAction.addElement(target, fieldQN.getPrefix(), fieldQN.getNameSpace(), fieldQN.getLocalName());
			newNode.put(mainAttribute, result[i]);
		}
		if (viewer != null) {
			viewer.refresh(true);
		}
	}
	private String[] getArrayFromList(List source_array) {
		String[] source_items = new String[source_array.size()];
		for(int i=0;i<source_array.size();i++){
			source_items[i] = (String)source_array.get(i);
		}
		return source_items;
	}
	private List getNotExistFields(List source_childs_list, List existFields) {
		if(source_childs_list == null)
			return null;
		if(existFields == null)
			return source_childs_list;
		List  source_array = new ArrayList();
		for(Iterator it = source_childs_list.iterator();it.hasNext();){
			CompositeMap child = (CompositeMap)it.next();
			String node = child.getString(mainAttribute);
			if(node == null)
				continue;
			if(!existFields.contains(node))
				source_array.add(node);
		}
		return source_array;
	}
	private List getExistFields() {
		List target_childs_list = target.getChildsNotNull();
		List  target_array = new ArrayList();
		for(Iterator it = target_childs_list.iterator();it.hasNext();){
			CompositeMap child = (CompositeMap)it.next();
			String targetNode = child.getString(mainAttribute);
			if(targetNode == null)
				continue;
			target_array.add(targetNode);
		}
		return target_array;
	}
	private QualifiedName getFieldQN() {
		List source_childs_list = allFields.getChildsNotNull();
		if(source_childs_list.size() ==0)
			return null;
		String localName = defaultChild;
		if(primary_key.equals(target.getName())){
			localName = primaryKeyChild;
		}else if(order_by.equals(target.getName())){
			localName = orderField;
		}
		return new QualifiedName(allFields.getPrefix(),allFields.getNamespaceURI(),localName);
		
	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage.getString("add.icon"));
	}
}
