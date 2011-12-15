package aurora.ide.meta.gef.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.jface.resource.ImageDescriptor;

import aurora.ide.AuroraPlugin;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Navbar;
import aurora.ide.meta.gef.editors.models.Toolbar;
import aurora.ide.meta.gef.editors.models.VBox;

public class VScreenEditorPaletteFactory {

	private static List<PaletteContainer> createCategories(PaletteRoot root) {
		List<PaletteContainer> categories = new ArrayList<PaletteContainer>();
		categories.add(createControlGroup(root));
		categories.add(createComponentsDrawer());
		return categories;
	}

	private static PaletteContainer createComponentsDrawer() {

		PaletteDrawer drawer = new PaletteDrawer("Components", null);

		List entries = new ArrayList();

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				"TextField", "Create a new TextField", Input.class,
				new SimpleFactory(Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setType(Input.TEXT);
						return newObject;
					}
				}, ImagesUtils.getImageDescriptor("palette/input_edit.png"),
				ImagesUtils.getImageDescriptor("palette/input_edit.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Combox",
				"Create a new Combox", Input.class, new SimpleFactory(
						Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setType(Input.Combo);
						return newObject;
					}
				}, ImagesUtils.getImageDescriptor("palette/itembar_01.png"),
				ImagesUtils.getImageDescriptor("palette/itembar_01.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Cal", "Create a new Cal",
				Input.class, new SimpleFactory(Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setType(Input.CAL);
						return newObject;
					}
				}, ImagesUtils.getImageDescriptor("palette/itembar_02.png"),
				ImagesUtils.getImageDescriptor("palette/itembar_02.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("LOV", "Create a new Lov",
				Input.class, new SimpleFactory(Input.class) {
					public Object getNewObject() {
						Input newObject = (Input) super.getNewObject();
						newObject.setType(Input.LOV);
						return newObject;
					}
				}, ImagesUtils.getImageDescriptor("palette/itembar_03.png"),
				ImagesUtils.getImageDescriptor("palette/itembar_03.png"));
		entries.add(combined);
		// /button
		combined = new CombinedTemplateCreationEntry("Button",
				"Create a Button", Button.class,
				new SimpleFactory(Button.class) {
					public Object getNewObject() {
						Button newObject = (Button) super.getNewObject();
						return newObject;
					}
				},
				ImagesUtils.getImageDescriptor("palette/toolbar_btn_01.png"),
				ImagesUtils.getImageDescriptor("palette/toolbar_btn_01.png"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Form", "Create a  Form",
				Form.class, new SimpleFactory(Form.class),
				ImageDescriptor.createFromFile(AuroraPlugin.class,
						"images/parallel16.gif"),
				ImageDescriptor.createFromFile(AuroraPlugin.class,
						"images/parallel16.gif"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("FieldSet",
				"Create a  FieldSet", FieldSet.class, new SimpleFactory(
						FieldSet.class), ImageDescriptor.createFromFile(
						AuroraPlugin.class, "images/parallel16.gif"),
				ImageDescriptor.createFromFile(AuroraPlugin.class,
						"images/parallel16.gif"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("HBox", "Create a  HBox",
				Grid.class, new SimpleFactory(HBox.class),
				ImageDescriptor.createFromFile(AuroraPlugin.class,
						"images/parallel16.gif"),
				ImageDescriptor.createFromFile(AuroraPlugin.class,
						"images/parallel16.gif"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("VBox", "Create a  VBox",
				Grid.class, new SimpleFactory(VBox.class),
				ImageDescriptor.createFromFile(AuroraPlugin.class,
						"images/parallel16.gif"),
				ImageDescriptor.createFromFile(AuroraPlugin.class,
						"images/parallel16.gif"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Grid", "Create a  Grid",
				Grid.class, new SimpleFactory(Grid.class),
				ImageDescriptor.createFromFile(AuroraPlugin.class,
						"images/parallel16.gif"),
				ImageDescriptor.createFromFile(AuroraPlugin.class,
						"images/parallel16.gif"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Grid Column",
				"Create a  Grid Column", GridColumn.class, new SimpleFactory(
						GridColumn.class), ImageDescriptor.createFromFile(
						AuroraPlugin.class, "images/parallel16.gif"),
				ImageDescriptor.createFromFile(AuroraPlugin.class,
						"images/parallel16.gif"));
		entries.add(combined);
		combined = new CombinedTemplateCreationEntry("Toolbar",
				"Create a  Toolbar ", Toolbar.class, new SimpleFactory(
						Toolbar.class), ImageDescriptor.createFromFile(
						AuroraPlugin.class, "images/parallel16.gif"),
				ImageDescriptor.createFromFile(AuroraPlugin.class,
						"images/parallel16.gif"));
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("Navbar",
				"Create a  Navbar", Navbar.class, new SimpleFactory(
						Navbar.class),
				ImagesUtils.getImageDescriptor("palette/navigation_04.png"),
				ImagesUtils.getImageDescriptor("palette/navigation_04.png"));
		entries.add(combined);

		drawer.addAll(entries);
		return drawer;
	}

	private static PaletteContainer createControlGroup(PaletteRoot root) {
		PaletteGroup controlGroup = new PaletteGroup("Control Group");

		List entries = new ArrayList();

		ToolEntry tool = new SelectionToolEntry();
		entries.add(tool);
		root.setDefaultEntry(tool);

		tool = new MarqueeToolEntry();
		entries.add(tool);

		PaletteSeparator sep = new PaletteSeparator(
				"org.eclipse.gef.examples.flow.flowplugin.sep2");
		sep.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		entries.add(sep);

		controlGroup.addAll(entries);
		return controlGroup;
	}

	/**
	 * Creates the PaletteRoot and adds all Palette elements.
	 * 
	 * @return the root
	 */
	public static PaletteRoot createPalette() {
		PaletteRoot flowPalette = new PaletteRoot();
		flowPalette.addAll(createCategories(flowPalette));
		return flowPalette;
	}

}
