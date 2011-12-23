package aurora.ide.meta.gef.editors;

import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.meta.gef.editors.figures.ColorConstants;

public abstract class FlayoutBMGEFEditor extends
		GraphicalEditorWithFlyoutPalette {
	private FlyoutPaletteComposite splitter;

	public void createPartControl(Composite parent) {
	
		
//		super.createPartControl(parent);
		
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
//		sashForm.SASH_WIDTH = 3;
		
		Composite c = new Composite(sashForm, SWT.BORDER);
		c.setBackground(ColorConstants.WHITE);
		c.setLayout(new GridLayout());
		createBMViewer(c);
		createPropertyViewer(c);
		
		super.createPartControl(sashForm);
		sashForm.setWeights(new int[] { 1, 4 });
	}

	protected abstract void createPropertyViewer(Composite c);

	protected abstract void createBMViewer(Composite c);
}
