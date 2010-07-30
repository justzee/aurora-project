/*
 * Created on 2009-7-21
 */
package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import uncertain.ide.Activator;
import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.CategoryLabel;
import uncertain.schema.Attribute;
import uncertain.schema.editor.AttributeValue;

public class PropertyHashLabelProvider extends BaseLabelProvider implements
		ITableLabelProvider {

	public PropertyHashLabelProvider() {
		super();
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		String imagePath = LocaleMessage.getString("property.icon");
		if (columnIndex == 0) {
			if (element instanceof CategoryLabel) {
				return Activator.getImageDescriptor(LocaleMessage.getString("category.icon"))
						.createImage();
			}
			return Activator.getImageDescriptor(imagePath).createImage();
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {

		if (!(element instanceof AttributeValue)) {
			return element.toString();
		}

		AttributeValue av = (AttributeValue) element;

		if (element instanceof CategoryLabel) {
			if (columnIndex == 0)
				return av.getValueString();
			else if (columnIndex == 1) {
				return "";
			}
		}
		if (av.getAttribute() == null)
			return av.getValueString();


		if (columnIndex == 0) {
			Attribute attr = av.getAttribute();
			String text = attr.getLocalName();
			if (attr.getUse() != null && attr.getUse().equals("required"))
				text = " * " + text;
			return text;
		} else if (columnIndex == 2) {
			Attribute attr = av.getAttribute();
			String document = attr.getDocument();
			return document;
		} else if (columnIndex == 1) {
			return av.getValueString();
		} else
			return "";
	}
}
