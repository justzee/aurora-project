/**
 * 
 */
package uncertain.ide.eclipse.editor;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.core.IViewer;

/**
 * @author linjinxiao
 *
 */
public abstract class CompositeMapPage extends FormPage implements IViewer{
	public CompositeMapPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	public abstract void setContent(CompositeMap content);
	public abstract CompositeMap getContent();
	public abstract String getFullContent();
}
