package uncertain.ide.eclipse.editor;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import uncertain.ide.eclipse.action.IPropertyCategory;
import uncertain.schema.Category;
import uncertain.schema.editor.AttributeValue;

public class PropertySorter extends ViewerSorter {
	private static final int ASCENDING = 0;
	private static final int DESCENDING = 1;
	private int order;
	private int column;
	IPropertyCategory mDirtyObject;
	public PropertySorter(IPropertyCategory dirtyObject){
		mDirtyObject = dirtyObject;
	}
	
	public void doSort(int column) {
		if (column == this.column) {
			order = 1 - order;
		} else {
			this.column = column;
			order = ASCENDING;
		}
	}

	public int category(Object element) {
		int result = 0;
		String cln = "";
		int side = (order == ASCENDING) ? 1 : -1;
		AttributeValue av = (AttributeValue) element;
		if (element instanceof CategoryLabel) {
			result = (Integer) PropertySheetContentProvider.Categorys.get(av
					.getValueString());
			result = result - side;
		} else {
			Category category = av.getAttribute().getCategoryInstance();
			if (category != null) {
				cln = category.getLocalName();

			} else {
				cln = "Î´·Ö×é";
			}
			result = (Integer) PropertySheetContentProvider.Categorys.get(cln);
			// int result=((File) element).isDirectory() ? 0 : 1;
		}
		if (order == DESCENDING)
			result = -result;
		return result;
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		int result;
		if (mDirtyObject.IsCategory()) {
			int cat1 = category(e1);
			int cat2 = category(e2);
			if (cat1 != cat2) {
				return cat1 - cat2;
			}
		}
		String name1;
		String name2;

		if (viewer == null || !(viewer instanceof ContentViewer)) {
			name1 = e1.toString();
			name2 = e2.toString();
		} else {
			IBaseLabelProvider prov = ((ContentViewer) viewer)
					.getLabelProvider();
			if (prov instanceof ILabelProvider) {
				ILabelProvider lprov = (ILabelProvider) prov;
				name1 = lprov.getText(e1);
				name2 = lprov.getText(e2);
			} else if (prov instanceof ITableLabelProvider) {
				ITableLabelProvider lprov = (ITableLabelProvider) prov;
				name1 = lprov.getColumnText(e1, column);
				name2 = lprov.getColumnText(e2, column);
			} else {
				name1 = e1.toString();
				name2 = e2.toString();
			}
		}
		if (name1 == null) {
			name1 = "";//$NON-NLS-1$
		}
		if (name2 == null) {
			name2 = "";//$NON-NLS-1$
		}

		// use the comparator to compare the strings
		// System.out.println(name1+":"+name2);
		result = getComparator().compare(name1, name2);
		if (order == DESCENDING)
			result = -result;
		return result;
	}
}
