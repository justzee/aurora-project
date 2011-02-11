package uncertain.ide.eclipse.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.bm.BMUtil;
import uncertain.ide.eclipse.editor.bm.GridDialog;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.editor.widgets.GridViewer;
import uncertain.ide.eclipse.editor.widgets.core.IGridViewer;
import uncertain.ide.util.LocaleMessage;
import aurora.ide.AuroraConstant;


public class AddRefFieldAction extends ActionListener {
	
	private GridViewer viewer;
	private CompositeMap model;
	private static final String relations = "relations";
	private static final String refModel = "refModel";
	private static final String ref_fields = "ref-fields";
	private static final String refFieldElement = "ref-field";
	private static final String specialSeparator = "\"";
	private static final String[] gridProperties = new String[]{"name","relationName","ref_model"};
	private static final String[] refFieldProperties = new String[]{"sourceField","name","relationName"};
	private CompositeMap gridInput;
	
	public AddRefFieldAction(GridViewer viewer,CompositeMap model,int actionStyle) {
		this.viewer = viewer;
		this.model = model;
		setActionStyle(actionStyle);
	}
	public void run() {
		if(model == null){
			CustomDialog.showErrorMessageBox("This model is null !");
			return;
		}
		gridInput = new CompositeMap("gridInput");
		QualifiedName modelQN = new QualifiedName(model.getNamespaceURI(),model.getName());
		Assert.isTrue(AuroraConstant.ModelQN.equals(modelQN), "This CompositeMap is not a model element!");
		CompositeMap relationsCM = model.getChild(relations);
		if(relationsCM == null){
			CustomDialog.showErrorMessageBox("relations is null !");
			return;
		}
		List childList = relationsCM.getChildsNotNull();
		if(childList.size() == 0){
			CustomDialog.showErrorMessageBox("relations have no child !");
			return;
		}
		CompositeMap refFields_array = viewer.getInput();
		List existRefFields = getExistRefFields(refFields_array);
		if(existRefFields == null)
			existRefFields = new ArrayList();
		for(Iterator it = childList.iterator();it.hasNext();){
			CompositeMap relation = (CompositeMap)it.next();
			CompositeMap fields = makeInput(relation,existRefFields);
			if(fields == null)
				continue;
			else{
				gridInput.addChilds(fields.getChildsNotNull());
			}
		}
		CompositeMap selectFileds = selectFileds();
		CompositeMap refFields = createRefFields(selectFileds);
		if(refFields == null){
			return ;
		}
		List fields = refFields.getChildsNotNull();
		if(fields.size()==0)
			return ;
		if(model.getChild(ref_fields) == null){
			model.addChild(refFields_array);
		}
		refFields_array.addChilds(fields);
		viewer.refresh(true);
	}
	public ImageDescriptor getDefaultImageDescriptor(){
		return Activator.getImageDescriptor(LocaleMessage.getString("add.icon"));
	}
	private CompositeMap makeInput(CompositeMap relation,List existRefFields){
		final String fieldName = "name";
		CompositeMap input = new CompositeMap("input"); 
		String ref_model = relation.getString(refModel);
		
		if(ref_model == null)
			return null;
		CompositeMap fields = null;
		try {
			fields = BMUtil.getFields(ref_model);
		} catch (Exception e) {
			CustomDialog.showErrorMessageBox(e);
			return null;
		}
		if(fields == null)
			return null;
		List fieldList = fields.getChildsNotNull();
		if(fieldList.size() == 0)
			return null;
		for(Iterator it = fieldList.iterator();it.hasNext();){
			CompositeMap field = (CompositeMap)it.next();
			String field_name = field.getString(fieldName);
			if(field_name == null){
				CustomDialog.showErrorMessageBox(ref_model+"'s "+field.toXML()+" has no 'name' property");
				continue;
			}
			String relationName = relation.getString("name");
			String fieldKey = field_name+specialSeparator+relationName;
			if(existRefFields.contains(fieldKey))
				continue;
			CompositeMap record = new CompositeMap(model.getPrefix(),model.getNamespaceURI(),refFieldElement);
			record.put(gridProperties[0], field_name);
			record.put(gridProperties[1], relationName);
			record.put(gridProperties[2], ref_model);
			input.addChild(record);
		}
		return input;
	}
	private CompositeMap selectFileds(){
		CompositeMap selectResult = null;
		GridViewer grid = new GridViewer(IGridViewer.isMulti);
		grid.setData(gridInput);
		grid.setGridProperties(gridProperties);
		GridDialog dialog = new GridDialog(new Shell(),grid);
		if (dialog.open() == Window.OK) {
			selectResult = dialog.getSelected();
		}
		return selectResult;
	}
	private CompositeMap createRefFields(CompositeMap selectResult){
		CompositeMap refFields = new CompositeMap("refFileds");
		if(selectResult == null){
			return null;
		}
		for(Iterator it = selectResult.getChildsNotNull().iterator();it.hasNext();){
			CompositeMap record = (CompositeMap)it.next();
			record.put(refFieldProperties[0], record.getString("name"));
			record.remove(gridProperties[2]);
			refFields.addChild(record);
		}
		return refFields;
		
	}
	private List getExistRefFields(CompositeMap refFields){
		if(refFields == null){
			return null;
		}
		List existRefFields = new ArrayList();
		List childs = refFields.getChildsNotNull();
		if(childs.size()==0){
			return null;
		}
		for(Iterator it = childs.iterator();it.hasNext();){
			CompositeMap child = (CompositeMap)it.next();
			String fieldKey = getFieldKey(child);
			if(fieldKey != null)
				existRefFields.add(fieldKey);
		}
		return existRefFields;
			
	}
	private String getFieldKey(CompositeMap field){
		if(field == null){
			return null;
		}
		String sourceField = field.getString(refFieldProperties[0]);
		String relationName = field.getString(refFieldProperties[2]);
		if(sourceField==null || relationName == null)
			return null;
		String fieldKey =  sourceField+specialSeparator+relationName;
		return fieldKey;
	}
}
