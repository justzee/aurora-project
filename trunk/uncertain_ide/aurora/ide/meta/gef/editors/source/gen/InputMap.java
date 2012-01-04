package aurora.ide.meta.gef.editors.source.gen;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.Input;

public class InputMap extends AbstractComponentMap {

	private Input input;

	public InputMap(Input c) {
		this.input = c;
	}

	@Override
	public CompositeMap toCompositMap() {
		AuroraComponent2CompositMap a2c = new AuroraComponent2CompositMap();
		String type = input.getType();
		CompositeMap map = a2c.createChild(type);
		IPropertyDescriptor[] propertyDescriptors = input.getPropertyDescriptors();
		for (IPropertyDescriptor iPropertyDescriptor : propertyDescriptors) {
			iPropertyDescriptor.getId();
			iPropertyDescriptor.getDisplayName();
			iPropertyDescriptor.getCategory();
//			input.
//			map.put(key, value)
//			if
		}
		
		input.getEmptyText();
		input.getName();
		input.getPrompt();
		input.getSize();
		input.isReadOnly();
		input.isRequired();
		
		//number
		input.isAllowDecimals();
		input.isAllowFormat();
		input.isAllowNegative();
		
		//cal
		input.getEnableBediseDays();
		input.getEnableMonthBtn();
		//lov,textField,combo
		input.getTypeCase();
		
		
		return null;
	}

}
