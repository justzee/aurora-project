package uncertain.ide.eclipse.celleditor;

import java.lang.reflect.Constructor;

import org.eclipse.swt.widgets.TableItem;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.eclipse.editor.ITableViewer;
import uncertain.schema.Attribute;
import uncertain.schema.Editor;
import uncertain.schema.Enumeration;
import uncertain.schema.IType;
import uncertain.schema.Restriction;
import uncertain.schema.SimpleType;

public class CellEditorFactory {
	private static CellEditorFactory editorFactory;
	public final static QualifiedName boolean_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema",
			"boolean");
	public final static QualifiedName string_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "string");
	public final static QualifiedName int_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "int");
	public final static QualifiedName integer_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema",
			"integer");
	public final static QualifiedName long_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "long");
	public final static QualifiedName float_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "float");
	public final static QualifiedName double_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "double");
	public final static String required = "required";

	private CellEditorFactory() {
	}

	public static CellEditorFactory getInstance() {
		if (editorFactory == null)
			editorFactory = new CellEditorFactory();
		return editorFactory;
	}

	public ICellEditor createCellEditor(ITableViewer viewer,
			Attribute attribute, CompositeMap record, TableItem item) {
		ICellEditor cellEditor = null;
		if (attribute == null)
			return cellEditor;

		QualifiedName attributeQName = attribute.getQName();

		cellEditor = createDefinedEditor(viewer, attribute, record, item,
				attributeQName);
		if (cellEditor == null)
			cellEditor = createDefaultEditor(viewer, attribute, record, item,
					attributeQName);

		if (cellEditor != null) {
			cellEditor.init();
		}
		return cellEditor;
	}

	private ICellEditor createDefaultEditor(ITableViewer viewer,
			Attribute attribute, CompositeMap record, TableItem item,
			QualifiedName attributeQName) {
		ICellEditor cellEditor = null;
		QualifiedName typeQname = attribute.getTypeQName();

		IType attributeType = LoadSchemaManager.getSchemaManager().getType(
				typeQname);
		if (attributeType != null && attributeType instanceof SimpleType) {
			SimpleType simpleType = (SimpleType) attributeType;
			Restriction rest = simpleType.getRestriction();
			if (rest != null) {
				Enumeration[] emus = rest.getEnumerations();
				if (emus != null) {
					cellEditor = new ComboxCellEditor(viewer, attribute,
							record, item);
					return cellEditor;
				}
			}
		}

		if (typeQname == null) {
			if (required.equals(attribute.getUse())) {
				cellEditor = new StringTextCellEditor(viewer, attribute,
						record, item);
				return cellEditor;
			}
			return cellEditor;
		}
		// String typeName = typeQname.getLocalName();
		if (typeQname.equals(boolean_qn)) {
			cellEditor = new BoolCellEditor(viewer, attribute, record, item);
			return cellEditor;
		}
		if (required.equals(attribute.getUse())) {
			if (typeQname.equals(string_qn)) {
				cellEditor = new StringTextCellEditor(viewer, attribute,
						record, item);
				return cellEditor;
			} else if (typeQname.equals(int_qn) || typeQname.equals(integer_qn)
					|| typeQname.equals(double_qn) || typeQname.equals(long_qn)
					|| typeQname.equals(float_qn)
					|| typeQname.equals(double_qn)) {
				cellEditor = new NumberTextCellEditor(viewer, attribute,
						record, item);
				return cellEditor;
			}
		}
		return cellEditor;

	}

	private ICellEditor createDefinedEditor(ITableViewer viewer,
			Attribute attribute, CompositeMap record, TableItem item,
			QualifiedName attributeQName) {
		Object editor;
		QualifiedName editorName = attribute.getEditorQName();
		if (editorName == null)
			return null;
		Editor ed = LoadSchemaManager.getSchemaManager().getEditor(editorName);
		if (ed == null)
			return null;
		String cls_name = ed.getInstanceClass();
		Class cls;
		try {
			cls = Class.forName(cls_name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("editor class " + cls_name
					+ " is not valid!");
		}
		Constructor constructor = null;
		try {
			Class[] constructorClasses = new Class[] { ITableViewer.class,
					Attribute.class, CompositeMap.class, TableItem.class };
			constructor = cls.getConstructor(constructorClasses);
			Object[] constructorObjects = new Object[] { viewer, attribute,
					record, item };
			editor = constructor.newInstance(constructorObjects);
			ICellEditor cellEditor = (ICellEditor) editor;
			return cellEditor;
		} catch (Exception e) {
			throw new RuntimeException("creat instance " + cls_name
					+ "(IContainer,CompositeMap,Attribute,TableItem) failure",
					e);
		}
	}
}
