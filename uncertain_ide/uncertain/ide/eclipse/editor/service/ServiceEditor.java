package uncertain.ide.eclipse.editor.service;

import uncertain.ide.eclipse.editor.AuroraEditor;




public class ServiceEditor extends AuroraEditor{
	/**
	 * @param auroraPage
	 */
	public ServiceEditor() {
		super();
		auroraPage = new ServicePage(this);
	}
}