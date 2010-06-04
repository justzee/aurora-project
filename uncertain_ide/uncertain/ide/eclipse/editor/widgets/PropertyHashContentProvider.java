/*
 * Created on 2009-7-3
 */
package uncertain.ide.eclipse.editor.widgets;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import uncertain.composite.CompositeMap;
import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.CategoryLabel;
import uncertain.ide.eclipse.editor.ICategoryContainer;
import uncertain.schema.Attribute;
import uncertain.schema.Category;
import uncertain.schema.ISchemaManager;
import uncertain.schema.editor.AttributeValue;
import uncertain.schema.editor.CompositeMapEditor;

/**
 * For edit CompositeMap in property sheet view ElementContentProvider
 * 
 * @author Zhou Fan
 * 
 */
public class PropertyHashContentProvider implements IStructuredContentProvider {

	/**
	 * @todo add attributes cache // Map<QName, Attribute[]>
	 * 
	 *       /**
	 * @param schemaManager
	 */
	public static HashMap Categorys = new HashMap();
	ICategoryContainer mViewer;

	public PropertyHashContentProvider(ICategoryContainer mViewer) {
		super();
		this.mViewer = mViewer;
	}

	// ISchemaManager mSchemaManager;

	public Object[] getElements(Object inputElement) {
		Categorys.clear();
		CompositeMap map = (CompositeMap) inputElement;
		ISchemaManager mSchemaManager = Common.getSchemaManager();
		CompositeMapEditor editor = new CompositeMapEditor(mSchemaManager, map);
		AttributeValue[] avs = editor.getAttributeList();
		if (!mViewer.isCategory())
			return avs;
		for (int i = 0; i < avs.length; i++) {
			AttributeValue av = avs[i];
			Attribute attr = av.getAttribute();

			Category category = attr.getCategoryInstance();
			if (category != null) {
				if (!Categorys.containsKey(category.getLocalName())) {
					Integer index = new Integer((Categorys.size() + 1) * 10);
					Categorys.put(category.getLocalName(), index);
				}
			}
		}
		Categorys.put(Common.getString("noncategory"), new Integer((Categorys
				.size() + 1) * 10));

		AttributeValue[] newAttrv = new AttributeValue[avs.length
				+ Categorys.size()];
		System.arraycopy(avs, 0, newAttrv, 0, avs.length);
		Iterator itr = Categorys.keySet().iterator();
		int i = avs.length;
		while (itr.hasNext()) {
			String cgl = (String) itr.next();
			CategoryLabel attrv = new CategoryLabel(null, null, cgl);
			newAttrv[i] = attrv;
			i++;
		}

		return newAttrv;
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}
