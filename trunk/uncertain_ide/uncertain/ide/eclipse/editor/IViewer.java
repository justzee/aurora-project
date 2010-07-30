/**
 * 
 */
package uncertain.ide.eclipse.editor;

public interface IViewer {
	/**
	 * The acitons of this viewer can use the
	 * <code>true<code> value.Its parent use <code>false<code> to refresh this viewer.
	 * @param isDirty
	 */
	public void refresh(boolean isDirty);
}
